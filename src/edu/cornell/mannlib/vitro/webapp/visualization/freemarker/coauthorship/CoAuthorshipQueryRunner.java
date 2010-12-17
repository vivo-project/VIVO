/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coauthorship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Edge;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Node;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UniqueIDGenerator;

/**
 * This query runner is used to execute a sparql query to get all the publications
 * for a particular individual. It will also fetch all the authors that worked
 * on that particular publication. 
 * 
 * @author cdtank
 */
public class CoAuthorshipQueryRunner implements QueryRunner<CoAuthorshipData> {

	private static final int MAX_AUTHORS_PER_PAPER_ALLOWED = 100;

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String egoURI;
	
	private DataSource dataSource;

	private Log log;

	private UniqueIDGenerator nodeIDGenerator;

	private UniqueIDGenerator edgeIDGenerator;

	public CoAuthorshipQueryRunner(String egoURI,
			DataSource dataSource, Log log) {

		this.egoURI = egoURI;
		this.dataSource = dataSource;
		this.log = log;
		
		this.nodeIDGenerator = new UniqueIDGenerator();
		this.edgeIDGenerator = new UniqueIDGenerator();

	}

	private CoAuthorshipData createQueryResult(ResultSet resultSet) {
		
		Set<Node> nodes = new HashSet<Node>();
		
		Map<String, BiboDocument> biboDocumentURLToVO = new HashMap<String, BiboDocument>();
		Map<String, Set<Node>> biboDocumentURLToCoAuthors = new HashMap<String, Set<Node>>();
		Map<String, Node> nodeURLToVO = new HashMap<String, Node>();
		Map<String, Edge> edgeUniqueIdentifierToVO = new HashMap<String, Edge>();
		
		Node egoNode = null;

		Set<Edge> edges = new HashSet<Edge>();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			
			/*
			 * We only want to create only ONE ego node.
			 * */
			RDFNode egoAuthorURLNode = solution.get(QueryFieldLabels.AUTHOR_URL);
			if (nodeURLToVO.containsKey(egoAuthorURLNode.toString())) {

				egoNode = nodeURLToVO.get(egoAuthorURLNode.toString());
				
			} else {
				
				egoNode = new Node(egoAuthorURLNode.toString(), nodeIDGenerator);
				nodes.add(egoNode);
				nodeURLToVO.put(egoAuthorURLNode.toString(), egoNode);
				
				RDFNode authorLabelNode = solution.get(QueryFieldLabels.AUTHOR_LABEL);
				if (authorLabelNode != null) {
					egoNode.setNodeName(authorLabelNode.toString());
				}
			}
			
			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			BiboDocument biboDocument;
			
			if (biboDocumentURLToVO.containsKey(documentNode.toString())) {
				biboDocument = biboDocumentURLToVO.get(documentNode.toString());
			} else {
				biboDocument = createDocumentVO(solution, documentNode.toString());
				biboDocumentURLToVO.put(documentNode.toString(), biboDocument);	
			}
			
			egoNode.addAuthorDocument(biboDocument);
			
			/*
			 * After some discussion we concluded that for the purpose of this visualization
			 * we do not want a co-author node or edge if the publication has only one
			 * author and that happens to be the ego.
			 * */
			if (solution.get(QueryFieldLabels.AUTHOR_URL).toString().equalsIgnoreCase(
					solution.get(QueryFieldLabels.CO_AUTHOR_URL).toString())) {
				continue;
			}
			
			Node coAuthorNode;
			
			RDFNode coAuthorURLNode = solution.get(QueryFieldLabels.CO_AUTHOR_URL);
			if (nodeURLToVO.containsKey(coAuthorURLNode.toString())) {

				coAuthorNode = nodeURLToVO.get(coAuthorURLNode.toString());
				
			} else {
				
				coAuthorNode = new Node(coAuthorURLNode.toString(), nodeIDGenerator);
				nodes.add(coAuthorNode);
				nodeURLToVO.put(coAuthorURLNode.toString(), coAuthorNode);
				
				RDFNode coAuthorLabelNode = solution.get(QueryFieldLabels.CO_AUTHOR_LABEL);
				if (coAuthorLabelNode != null) {
					coAuthorNode.setNodeName(coAuthorLabelNode.toString());
				}
			}
			
			coAuthorNode.addAuthorDocument(biboDocument);
			
			Set<Node> coAuthorsForCurrentBiboDocument;
			
			if (biboDocumentURLToCoAuthors.containsKey(biboDocument.getDocumentURL())) {
				coAuthorsForCurrentBiboDocument = biboDocumentURLToCoAuthors
														.get(biboDocument.getDocumentURL());
			} else {
				coAuthorsForCurrentBiboDocument = new HashSet<Node>();
				biboDocumentURLToCoAuthors.put(biboDocument.getDocumentURL(), 
											   coAuthorsForCurrentBiboDocument);
			}
			
			coAuthorsForCurrentBiboDocument.add(coAuthorNode);
			
			Edge egoCoAuthorEdge = getExistingEdge(egoNode, coAuthorNode, edgeUniqueIdentifierToVO);
			
			/*
			 * If "egoCoAuthorEdge" is null it means that no edge exists in between the egoNode 
			 * & current coAuthorNode. Else create a new edge, add it to the edges set & add 
			 * the collaborator document to it.
			 * */
			if (egoCoAuthorEdge != null) {
				egoCoAuthorEdge.addCollaboratorDocument(biboDocument);
			} else {
				egoCoAuthorEdge = new Edge(egoNode, coAuthorNode, biboDocument, edgeIDGenerator);
				edges.add(egoCoAuthorEdge);
				edgeUniqueIdentifierToVO.put(
						getEdgeUniqueIdentifier(egoNode.getNodeID(),
												coAuthorNode.getNodeID()), 
						egoCoAuthorEdge);
			}
			
			
		}
		
		
		
		/*
		 * This method takes out all the authors & edges between authors that belong to documents 
		 * that have more than 100 authors. We conjecture that these papers do not provide much 
		 * insight. However, we have left the documents be.
		 * 
		 * This method side-effects "nodes" & "edges".  
		 * */
		removeLowQualityNodesAndEdges(nodes, 
									  biboDocumentURLToVO, 
									  biboDocumentURLToCoAuthors, 
									  edges);
		
		/*
		 * We need to create edges between 2 co-authors. E.g. On a paper there were 3 authors
		 * ego, A & B then we have already created edges like,
		 * 		ego - A
		 * 		ego - B
		 * The below sub-routine will take care of,
		 * 		A - B 
		 * 
		 * We are side-effecting "edges" here. The only reason to do this is because we are adding 
		 * edges en masse for all the co-authors on all the publications considered so far. The 
		 * other reason being we dont want to compare against 2 sets of edges (edges created before 
		 * & co-author edges created during the course of this method) when we are creating a new 
		 * edge.
		 * */
		createCoAuthorEdges(biboDocumentURLToVO, 
							biboDocumentURLToCoAuthors,
							edges,
							edgeUniqueIdentifierToVO);
		
		
		return new CoAuthorshipData(egoNode, nodes, edges);
	}

	private void removeLowQualityNodesAndEdges(Set<Node> nodes,
											   Map<String, BiboDocument> biboDocumentURLToVO,
											   Map<String, Set<Node>> biboDocumentURLToCoAuthors, 
											   Set<Edge> edges) {
		
		Set<Node> nodesToBeRemoved = new HashSet<Node>();
		for (Map.Entry<String, Set<Node>> currentBiboDocumentEntry 
					: biboDocumentURLToCoAuthors.entrySet()) {
				
				if (currentBiboDocumentEntry.getValue().size() > MAX_AUTHORS_PER_PAPER_ALLOWED) {
					
					BiboDocument currentBiboDocument = biboDocumentURLToVO
															.get(currentBiboDocumentEntry.getKey());
					
					Set<Edge> edgesToBeRemoved = new HashSet<Edge>();
					
					for (Edge currentEdge : edges) {
						Set<BiboDocument> currentCollaboratorDocuments = 
									currentEdge.getCollaboratorDocuments();
						
						if (currentCollaboratorDocuments.contains(currentBiboDocument)) {
							currentCollaboratorDocuments.remove(currentBiboDocument);
							if (currentCollaboratorDocuments.isEmpty()) {
								edgesToBeRemoved.add(currentEdge);
							}
						}
					}
						
					edges.removeAll(edgesToBeRemoved);

					for (Node currentCoAuthor : currentBiboDocumentEntry.getValue()) {
						currentCoAuthor.getAuthorDocuments().remove(currentBiboDocument);
						if (currentCoAuthor.getAuthorDocuments().isEmpty()) {
							nodesToBeRemoved.add(currentCoAuthor);
						}
					}
				}
		}
		nodes.removeAll(nodesToBeRemoved);
	}

	private void createCoAuthorEdges(
			Map<String, BiboDocument> biboDocumentURLToVO,
			Map<String, Set<Node>> biboDocumentURLToCoAuthors, Set<Edge> edges, 
			Map<String, Edge> edgeUniqueIdentifierToVO) {
		
		for (Map.Entry<String, Set<Node>> currentBiboDocumentEntry 
					: biboDocumentURLToCoAuthors.entrySet()) {
			
			/*
			 * If there was only one co-author (other than ego) then we dont have to create any 
			 * edges. so the below condition will take care of that.
			 * 
			 * We are restricting edges between co-author if a particular document has more than
			 * 100 co-authors. Our conjecture is that such edges do not provide any good insight
			 * & causes unnecessary computations causing the server to time-out.
			 * */
			if (currentBiboDocumentEntry.getValue().size() > 1 
					&& currentBiboDocumentEntry.getValue().size() 
							<= MAX_AUTHORS_PER_PAPER_ALLOWED) {
				
				
				Set<Edge> newlyAddedEdges = new HashSet<Edge>();
			
				/*
				 * In order to leverage the nested "for loop" for making edges between all the 
				 * co-authors we need to create a list out of the set first. 
				 * */
				List<Node> coAuthorNodes = new ArrayList<Node>(currentBiboDocumentEntry.getValue());
				Collections.sort(coAuthorNodes, new NodeComparator());
				
				int numOfCoAuthors = coAuthorNodes.size();
				
				for (int ii = 0; ii < numOfCoAuthors - 1; ii++) {
					for (int jj = ii + 1; jj < numOfCoAuthors; jj++) {
						
						Node coAuthor1 = coAuthorNodes.get(ii);
						Node coAuthor2 = coAuthorNodes.get(jj);
						
						Edge coAuthor1_2Edge = getExistingEdge(coAuthor1, 
															   coAuthor2, 
															   edgeUniqueIdentifierToVO);
						
						BiboDocument currentBiboDocument = biboDocumentURLToVO
																.get(currentBiboDocumentEntry
																			.getKey());
			
						if (coAuthor1_2Edge != null) {
							coAuthor1_2Edge.addCollaboratorDocument(currentBiboDocument);
						} else {
							coAuthor1_2Edge = new Edge(coAuthor1, 
													   coAuthor2, 
													   currentBiboDocument, 
													   edgeIDGenerator);
							newlyAddedEdges.add(coAuthor1_2Edge);
							edgeUniqueIdentifierToVO.put(
									getEdgeUniqueIdentifier(coAuthor1.getNodeID(),
															coAuthor2.getNodeID()), 
									coAuthor1_2Edge);
						}
					}
				}
				edges.addAll(newlyAddedEdges);
			}
			
		}
	}

	private Edge getExistingEdge(
					Node collaboratingNode1, 
					Node collaboratingNode2, 
					Map<String, Edge> edgeUniqueIdentifierToVO) {
		
		String edgeUniqueIdentifier = getEdgeUniqueIdentifier(collaboratingNode1.getNodeID(), 
															  collaboratingNode2.getNodeID());
		
		return edgeUniqueIdentifierToVO.get(edgeUniqueIdentifier);
		
	}

	private String getEdgeUniqueIdentifier(int nodeID1, int nodeID2) {

		String separator = "*"; 
		
		if (nodeID1 < nodeID2) {
			return nodeID1 + separator + nodeID2;
		} else {
			return nodeID2 + separator + nodeID1;
		}
			
	}

//	public Map<String, VivoCollegeOrSchool> getCollegeURLToVO() {
//		return collegeURLToVO;
//	}

	private BiboDocument createDocumentVO(QuerySolution solution, String documentURL) {

			BiboDocument biboDocument = new BiboDocument(documentURL);

			RDFNode documentLabelNode = solution.get(QueryFieldLabels.DOCUMENT_LABEL);
			if (documentLabelNode != null) {
				biboDocument.setDocumentLabel(documentLabelNode.toString());
			}

			RDFNode documentBlurbNode = solution.get(QueryFieldLabels.DOCUMENT_BLURB);
			if (documentBlurbNode != null) {
				biboDocument.setDocumentBlurb(documentBlurbNode.toString());
			}

			RDFNode documentMonikerNode = solution.get(QueryFieldLabels.DOCUMENT_MONIKER);
			if (documentMonikerNode != null) {
				biboDocument.setDocumentMoniker(documentMonikerNode.toString());
			}

			RDFNode publicationYearNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR);
			if (publicationYearNode != null) {
				biboDocument.setPublicationYear(publicationYearNode.toString());
			}
			
			RDFNode publicationYearMonthNode = solution.get(QueryFieldLabels
																.DOCUMENT_PUBLICATION_YEAR_MONTH);
			if (publicationYearMonthNode != null) {
				biboDocument.setPublicationYearMonth(publicationYearMonthNode.toString());
			}
			
			RDFNode publicationDateNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			if (publicationDateNode != null) {
				biboDocument.setPublicationDate(publicationDateNode.toString());
			}
			
			return biboDocument;
	}
	
	private ResultSet executeQuery(String queryText,
								   DataSource dataSource) {

        QueryExecution queryExecution = null;
        Query query = QueryFactory.create(queryText, SYNTAX);

        queryExecution = QueryExecutionFactory.create(query, dataSource);
        return queryExecution.execSelect();
    }

	private String generateEgoCoAuthorshipSparqlQuery(String queryURI) {
//		Resource uri1 = ResourceFactory.createResource(queryURI);

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
			+ "SELECT "
			+ "		(str(<" + queryURI + ">) as ?" + QueryFieldLabels.AUTHOR_URL + ") " 
			+ "		(str(?authorLabel) as ?" + QueryFieldLabels.AUTHOR_LABEL + ") " 
			+ "		(str(?coAuthorPerson) as ?" + QueryFieldLabels.CO_AUTHOR_URL + ") " 
			+ "		(str(?coAuthorPersonLabel) as ?" + QueryFieldLabels.CO_AUTHOR_LABEL + ") "
			+ "		(str(?document) as ?" + QueryFieldLabels.DOCUMENT_URL + ") "
			+ "		(str(?documentLabel) as ?" + QueryFieldLabels.DOCUMENT_LABEL + ") "
			+ "		(str(?documentMoniker) as ?" + QueryFieldLabels.DOCUMENT_MONIKER + ") "
			+ "		(str(?documentBlurb) as ?" + QueryFieldLabels.DOCUMENT_BLURB + ") "
			+ "		(str(?publicationYear) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR + ") "
			+ "		(str(?publicationYearMonth) as ?" 
						+ QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_MONTH + ") " 
			+ "		(str(?publicationDate) as ?" 
						+ QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ") " 
			+ "WHERE { "
			+ "<" + queryURI + "> rdf:type foaf:Person ;" 
								+ " rdfs:label ?authorLabel ;" 
								+ " core:authorInAuthorship ?authorshipNode . "
			+ "?authorshipNode rdf:type core:Authorship ;" 
								+ " core:linkedInformationResource ?document . "
			+ "?document rdfs:label ?documentLabel . " 
			+ "?document core:informationResourceInAuthorship ?coAuthorshipNode . " 
			+ "?coAuthorshipNode core:linkedAuthor ?coAuthorPerson . " 
			+ "?coAuthorPerson rdfs:label ?coAuthorPersonLabel . "
			+ "OPTIONAL {  ?document core:year ?publicationYear } . " 
			+ "OPTIONAL {  ?document core:yearMonth ?publicationYearMonth } . " 
			+ "OPTIONAL {  ?document core:date ?publicationDate } . "  
			+ "OPTIONAL {  ?document vitro:moniker ?documentMoniker } . " 
			+ "OPTIONAL {  ?document vitro:blurb ?documentBlurb } . " 
			+ "OPTIONAL {  ?document vitro:description ?documentDescription } " 
			+ "} " 
			+ "ORDER BY ?document ?coAuthorPerson";

//		System.out.println("COAUTHORSHIP QUERY - " + sparqlQuery);
		
		return sparqlQuery;
	}

	
	public CoAuthorshipData getQueryResult()
		throws MalformedQueryParametersException {

		if (StringUtils.isNotBlank(this.egoURI)) {
			/*
        	 * To test for the validity of the URI submitted.
        	 * */
        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
    		IRI iri = iRIFactory.create(this.egoURI);
            if (iri.hasViolation(false)) {
                String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
                log.error("Ego Co-Authorship Vis Query " + errorMsg);
                throw new MalformedQueryParametersException(
                		"URI provided for an individual is malformed.");
            }
        } else {
            throw new MalformedQueryParametersException("URI parameter is either null or empty.");
        }

		ResultSet resultSet	= executeQuery(generateEgoCoAuthorshipSparqlQuery(this.egoURI),
										   this.dataSource);
		return createQueryResult(resultSet);
	}

}
