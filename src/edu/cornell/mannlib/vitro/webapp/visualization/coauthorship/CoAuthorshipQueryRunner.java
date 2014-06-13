/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaboratorComparator;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UniqueIDGenerator;

/**
 * This query runner is used to execute a sparql query to get all the publications
 * for a particular individual. It will also fetch all the authors that worked
 * on that particular publication. 
 * 
 * @author cdtank
 */
public class CoAuthorshipQueryRunner implements QueryRunner<CollaborationData> {

	private static final int MAX_AUTHORS_PER_PAPER_ALLOWED = 100;

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String egoURI;
	
	private Dataset dataset;

	private Log log;

	private UniqueIDGenerator nodeIDGenerator;

	private UniqueIDGenerator edgeIDGenerator;

	public CoAuthorshipQueryRunner(String egoURI,
			Dataset dataset, Log log) {

		this.egoURI = egoURI;
		this.dataset = dataset;
		this.log = log;
		
		this.nodeIDGenerator = new UniqueIDGenerator();
		this.edgeIDGenerator = new UniqueIDGenerator();

	}

	private CollaborationData createQueryResult(ResultSet resultSet) {
		
		Set<Collaborator> nodes = new HashSet<Collaborator>();
		
		Map<String, Activity> biboDocumentURLToVO = new HashMap<String, Activity>();
		Map<String, Set<Collaborator>> biboDocumentURLToCoAuthors = 
					new HashMap<String, Set<Collaborator>>();
		Map<String, Collaborator> nodeURLToVO = new HashMap<String, Collaborator>();
		Map<String, Collaboration> edgeUniqueIdentifierToVO = new HashMap<String, Collaboration>();
		
		Collaborator egoNode = null;

		Set<Collaboration> edges = new HashSet<Collaboration>();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			
			/*
			 * We only want to create only ONE ego node.
			 * */
			RDFNode egoAuthorURLNode = solution.get(QueryFieldLabels.AUTHOR_URL);
			if (nodeURLToVO.containsKey(egoAuthorURLNode.toString())) {

				egoNode = nodeURLToVO.get(egoAuthorURLNode.toString());
				
			} else {
				
				egoNode = new Collaborator(egoAuthorURLNode.toString(), nodeIDGenerator);
				nodes.add(egoNode);
				nodeURLToVO.put(egoAuthorURLNode.toString(), egoNode);
				
				RDFNode authorLabelNode = solution.get(QueryFieldLabels.AUTHOR_LABEL);
				if (authorLabelNode != null) {
					egoNode.setCollaboratorName(authorLabelNode.toString());
				}
			}
			
			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			Activity biboDocument;
			
			if (biboDocumentURLToVO.containsKey(documentNode.toString())) {
				biboDocument = biboDocumentURLToVO.get(documentNode.toString());
			} else {
				biboDocument = createDocumentVO(solution, documentNode.toString());
				biboDocumentURLToVO.put(documentNode.toString(), biboDocument);	
			}
			
			egoNode.addActivity(biboDocument);
			
			/*
			 * After some discussion we concluded that for the purpose of this visualization
			 * we do not want a co-author node or Collaboration if the publication has only one
			 * author and that happens to be the ego.
			 * */
			if (solution.get(QueryFieldLabels.AUTHOR_URL).toString().equalsIgnoreCase(
					solution.get(QueryFieldLabels.CO_AUTHOR_URL).toString())) {
				continue;
			}
			
			Collaborator coAuthorNode;
			
			RDFNode coAuthorURLNode = solution.get(QueryFieldLabels.CO_AUTHOR_URL);
			if (nodeURLToVO.containsKey(coAuthorURLNode.toString())) {

				coAuthorNode = nodeURLToVO.get(coAuthorURLNode.toString());
				
			} else {
				
				coAuthorNode = new Collaborator(coAuthorURLNode.toString(), nodeIDGenerator);
				nodes.add(coAuthorNode);
				nodeURLToVO.put(coAuthorURLNode.toString(), coAuthorNode);
				
				RDFNode coAuthorLabelNode = solution.get(QueryFieldLabels.CO_AUTHOR_LABEL);
				if (coAuthorLabelNode != null) {
					coAuthorNode.setCollaboratorName(coAuthorLabelNode.toString());
				}
			}
			
			coAuthorNode.addActivity(biboDocument);
			
			Set<Collaborator> coAuthorsForCurrentBiboDocument;
			
			if (biboDocumentURLToCoAuthors.containsKey(biboDocument.getActivityURI())) {
				coAuthorsForCurrentBiboDocument = biboDocumentURLToCoAuthors
														.get(biboDocument.getActivityURI());
			} else {
				coAuthorsForCurrentBiboDocument = new HashSet<Collaborator>();
				biboDocumentURLToCoAuthors.put(biboDocument.getActivityURI(), 
											   coAuthorsForCurrentBiboDocument);
			}
			
			coAuthorsForCurrentBiboDocument.add(coAuthorNode);
			
			Collaboration egoCoAuthorEdge = 
					getExistingEdge(egoNode, coAuthorNode, edgeUniqueIdentifierToVO);
			
			/*
			 * If "egoCoAuthorEdge" is null it means that no Collaboration exists in between the 
			 * egoNode & current coAuthorNode. Else create a new Collaboration, add it to the edges 
			 * set & add the collaborator document to it.
			 * */
			if (egoCoAuthorEdge != null) {
				egoCoAuthorEdge.addActivity(biboDocument);
			} else {
				egoCoAuthorEdge = 
						new Collaboration(egoNode, coAuthorNode, biboDocument, edgeIDGenerator);
				edges.add(egoCoAuthorEdge);
				edgeUniqueIdentifierToVO.put(
						getEdgeUniqueIdentifier(egoNode.getCollaboratorID(),
												coAuthorNode.getCollaboratorID()), 
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
		 * Collaboration.
		 * */
		createCoAuthorEdges(biboDocumentURLToVO, 
							biboDocumentURLToCoAuthors,
							edges,
							edgeUniqueIdentifierToVO);
		
		
		return new CoAuthorshipData(egoNode, nodes, edges);
	}

	private void removeLowQualityNodesAndEdges(
							Set<Collaborator> nodes,
							Map<String, Activity> biboDocumentURLToVO,
							Map<String, Set<Collaborator>> biboDocumentURLToCoAuthors, 
							Set<Collaboration> edges) {
		
		Set<Collaborator> nodesToBeRemoved = new HashSet<Collaborator>();
		for (Map.Entry<String, Set<Collaborator>> currentBiboDocumentEntry 
					: biboDocumentURLToCoAuthors.entrySet()) {
				
				if (currentBiboDocumentEntry.getValue().size() > MAX_AUTHORS_PER_PAPER_ALLOWED) {
					
					Activity currentBiboDocument = biboDocumentURLToVO
															.get(currentBiboDocumentEntry.getKey());
					
					Set<Collaboration> edgesToBeRemoved = new HashSet<Collaboration>();
					
					for (Collaboration currentEdge : edges) {
						Set<Activity> currentCollaboratorDocuments = 
									currentEdge.getCollaborationActivities();
						
						if (currentCollaboratorDocuments.contains(currentBiboDocument)) {
							currentCollaboratorDocuments.remove(currentBiboDocument);
							if (currentCollaboratorDocuments.isEmpty()) {
								edgesToBeRemoved.add(currentEdge);
							}
						}
					}
						
					edges.removeAll(edgesToBeRemoved);

					for (Collaborator currentCoAuthor : currentBiboDocumentEntry.getValue()) {
						currentCoAuthor.getCollaboratorActivities().remove(currentBiboDocument);
						if (currentCoAuthor.getCollaboratorActivities().isEmpty()) {
							nodesToBeRemoved.add(currentCoAuthor);
						}
					}
				}
		}
		nodes.removeAll(nodesToBeRemoved);
	}

	private void createCoAuthorEdges(
			Map<String, Activity> biboDocumentURLToVO,
			Map<String, Set<Collaborator>> biboDocumentURLToCoAuthors, Set<Collaboration> edges, 
			Map<String, Collaboration> edgeUniqueIdentifierToVO) {
		
		for (Map.Entry<String, Set<Collaborator>> currentBiboDocumentEntry 
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
				
				
				Set<Collaboration> newlyAddedEdges = new HashSet<Collaboration>();
			
				/*
				 * In order to leverage the nested "for loop" for making edges between all the 
				 * co-authors we need to create a list out of the set first. 
				 * */
				List<Collaborator> coAuthorNodes = 
							new ArrayList<Collaborator>(currentBiboDocumentEntry.getValue());
				
				Collections.sort(coAuthorNodes, new CollaboratorComparator());
				
				int numOfCoAuthors = coAuthorNodes.size();
				
				for (int ii = 0; ii < numOfCoAuthors - 1; ii++) {
					for (int jj = ii + 1; jj < numOfCoAuthors; jj++) {
						
						Collaborator coAuthor1 = coAuthorNodes.get(ii);
						Collaborator coAuthor2 = coAuthorNodes.get(jj);
						
						Collaboration coAuthor1_2Edge = getExistingEdge(coAuthor1, 
															   coAuthor2, 
															   edgeUniqueIdentifierToVO);
						
						Activity currentBiboDocument = biboDocumentURLToVO
																.get(currentBiboDocumentEntry
																			.getKey());
			
						if (coAuthor1_2Edge != null) {
							coAuthor1_2Edge.addActivity(currentBiboDocument);
						} else {
							coAuthor1_2Edge = new Collaboration(coAuthor1, 
													   coAuthor2, 
													   currentBiboDocument, 
													   edgeIDGenerator);
							newlyAddedEdges.add(coAuthor1_2Edge);
							edgeUniqueIdentifierToVO.put(
									getEdgeUniqueIdentifier(coAuthor1.getCollaboratorID(),
															coAuthor2.getCollaboratorID()), 
									coAuthor1_2Edge);
						}
					}
				}
				edges.addAll(newlyAddedEdges);
			}
			
		}
	}

	private Collaboration getExistingEdge(
					Collaborator collaboratingNode1, 
					Collaborator collaboratingNode2, 
					Map<String, Collaboration> edgeUniqueIdentifierToVO) {
		
		String edgeUniqueIdentifier = getEdgeUniqueIdentifier(
												collaboratingNode1.getCollaboratorID(),
												collaboratingNode2.getCollaboratorID());
		
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

	private Activity createDocumentVO(QuerySolution solution, String documentURL) {

			Activity biboDocument = new Activity(documentURL);

			RDFNode publicationDateNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			if (publicationDateNode != null) {
				biboDocument.setActivityDate(publicationDateNode.toString());
			}

			return biboDocument;
	}
	
	private ResultSet executeQuery(String queryText,
								   Dataset dataset) {

        QueryExecution queryExecution = null;
        Query query = QueryFactory.create(queryText, SYNTAX);

        queryExecution = QueryExecutionFactory.create(query, dataset);
        return queryExecution.execSelect();
    }

	private String generateEgoCoAuthorshipSparqlQuery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
			+ "SELECT \n"
			+ "		(str(<" + queryURI + ">) as ?" + QueryFieldLabels.AUTHOR_URL + ") \n" 
			+ "		(str(?authorLabel) as ?" + QueryFieldLabels.AUTHOR_LABEL + ") \n" 
			+ "		(str(?coAuthorPerson) as ?" + QueryFieldLabels.CO_AUTHOR_URL + ") \n" 
			+ "		(str(?coAuthorPersonLabel) as ?" + QueryFieldLabels.CO_AUTHOR_LABEL + ") \n"
			+ "		(str(?document) as ?" + QueryFieldLabels.DOCUMENT_URL + ") \n"
			+ "		(str(?publicationDate) as ?" 
							+ QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ") \n"
			+ "WHERE { \n"
			+ "<" + queryURI + "> rdf:type foaf:Person ;" 
								+ " rdfs:label ?authorLabel ;" 
								+ " core:relatedBy ?authorshipNode . \n"
			+ "?authorshipNode rdf:type core:Authorship ;" 
								+ " core:relates ?document . \n"
            + "?document rdf:type bibo:Document . \n" 
			+ "?document core:relatedBy ?coAuthorshipNode . \n" 
			+ "?coAuthorshipNode rdf:type core:Authorship . \n" 
			+ "?coAuthorshipNode core:relates ?coAuthorPerson . \n" 
			+ "?coAuthorPerson rdf:type foaf:Person . \n"
			+ "?coAuthorPerson rdfs:label ?coAuthorPersonLabel . \n"
			+ "OPTIONAL {  ?document core:dateTimeValue ?dateTimeValue . \n" 
			+ "				?dateTimeValue core:dateTime ?publicationDate } .\n" 
			+ "} \n" 
			+ "ORDER BY ?document ?coAuthorPerson\n";

		log.debug("COAUTHORSHIP QUERY - " + sparqlQuery);
		
		return sparqlQuery;
	}

	
	public CollaborationData getQueryResult()
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
										   this.dataset);
		return createQueryResult(resultSet);
	}

}
