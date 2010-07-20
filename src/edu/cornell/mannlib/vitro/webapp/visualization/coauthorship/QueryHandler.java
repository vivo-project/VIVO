/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Edge;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.VivoCollegeOrSchool;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UniqueIDGenerator;



/**
 * @author cdtank
 */
public class QueryHandler {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String egoURLParam, resultFormatParam, rdfResultFormatParam;
	private Map<String, VivoCollegeOrSchool> collegeURLToVO = new HashMap<String, VivoCollegeOrSchool>();
	private DataSource dataSource;

	private Log log;

	private UniqueIDGenerator nodeIDGenerator;

	private UniqueIDGenerator edgeIDGenerator;

	public QueryHandler(String egoURLParam,
			String resultFormatParam, String rdfResultFormatParam,
			DataSource dataSource, Log log) {

		this.egoURLParam = egoURLParam;
		this.resultFormatParam = resultFormatParam;
		this.rdfResultFormatParam = rdfResultFormatParam;
		this.dataSource = dataSource;
		this.log = log;
		
		this.nodeIDGenerator = new UniqueIDGenerator();
		this.edgeIDGenerator = new UniqueIDGenerator();

	}

	private VisVOContainer createJavaValueObjects(ResultSet resultSet) {
		
		Set<Node> nodes = new HashSet<Node>();
		
		Map<String, BiboDocument> biboDocumentURLToVO = new HashMap<String, BiboDocument>();
		Map<String, Set<Node>> biboDocumentURLToCoAuthors = new HashMap<String, Set<Node>>();
		Map<String, Node> nodeURLToVO = new HashMap<String, Node>();
		
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
				coAuthorsForCurrentBiboDocument = biboDocumentURLToCoAuthors.get(biboDocument.getDocumentURL());
			} else {
				coAuthorsForCurrentBiboDocument = new HashSet<Node>();
				biboDocumentURLToCoAuthors.put(biboDocument.getDocumentURL(), coAuthorsForCurrentBiboDocument);
			}
			
			coAuthorsForCurrentBiboDocument.add(coAuthorNode);
			
			Edge egoCoAuthorEdge = getExistingEdge(egoNode, coAuthorNode, edges);
			
			/*
			 * If "egoCoAuthorEdge" is null it means that no edge exists in between the egoNode & current 
			 * coAuthorNode. Else create a new edge, add it to the edges set & add the collaborator document 
			 * to it.
			 * */
			if (egoCoAuthorEdge != null) {
				egoCoAuthorEdge.addCollaboratorDocument(biboDocument);
			} else {
				egoCoAuthorEdge = new Edge(egoNode, coAuthorNode, biboDocument, edgeIDGenerator);
				edges.add(egoCoAuthorEdge);
			}
			
			
		}
		
		/*
		 * We need to create edges between 2 co-authors. E.g. On a paper there were 3 authors
		 * ego, A & B then we have already created edges like,
		 * 		ego - A
		 * 		ego - B
		 * The below sub-routine will take care of,
		 * 		A - B 
		 * 
		 * We are side-effecting "edges" here. The only reason to do this is because we are adding 
		 * edges en masse for all the co-authors on all the publications considered so far. The other
		 * reason being we dont want to compare against 2 sets of edges (edges created before & co-
		 * author edges created during the course of this method) when we are creating a new edge.
		 * */
		createCoAuthorEdges(biboDocumentURLToVO, 
							biboDocumentURLToCoAuthors,
							edges);
		
		
/*		System.out.println(collegeURLToVO);
		System.out.println("------------------------------------------------------------");
		System.out.println(departmentURLToVO);
		System.out.println("------------------------------------------------------------");
		System.out.println(employeeURLToVO);
		System.out.println("------------------------------------------------------------");
*/		
		return new VisVOContainer(egoNode, nodes, edges);
	}

	private void createCoAuthorEdges(
			Map<String, BiboDocument> biboDocumentURLToVO,
			Map<String, Set<Node>> biboDocumentURLToCoAuthors, Set<Edge> edges) {
		for (Map.Entry<String, Set<Node>> currentBiboDocumentEntry : biboDocumentURLToCoAuthors.entrySet()) {
			/*
			 * If there was only one co-author (other than ego) then we dont have to create any edges. so 
			 * the below condition will take care of that.
			 * */
			if (currentBiboDocumentEntry.getValue().size() > 1) {
			
				/*
				 * In order to leverage the nested "for loop" for making edges between all the co=authors
				 * we need to create a list out of the set first. 
				 * */
				List<Node> coAuthorNodes = new ArrayList<Node>(currentBiboDocumentEntry.getValue());
				
				int numOfCoAuthors = coAuthorNodes.size();
				
				for (int ii = 0; ii < numOfCoAuthors - 1; ii++) {
					for (int jj = ii + 1; jj < numOfCoAuthors; jj++) {
						
						Node coAuthor1 = coAuthorNodes.get(ii);
						Node coAuthor2 = coAuthorNodes.get(jj);
						
						Edge coAuthor1_2Edge = getExistingEdge(coAuthor1, coAuthor2, edges);
						
						BiboDocument currentBiboDocument = biboDocumentURLToVO
																.get(currentBiboDocumentEntry.getKey());
			
						if (coAuthor1_2Edge != null) {
							coAuthor1_2Edge.addCollaboratorDocument(currentBiboDocument);
						} else {
							coAuthor1_2Edge = new Edge(coAuthor1, coAuthor2, currentBiboDocument, edgeIDGenerator);
							edges.add(coAuthor1_2Edge);
						}
					}
				}
			
			}
			
		}
	}

	private Edge getExistingEdge(
			Node collaboratingNode1, 
			Node collaboratingNode2, 
			Set<Edge> edges) {
		
		Edge duplicateEdge = null;
		
		for (Edge currentEdge : edges) {
			
			/*
			 * We first check if either the source or target node of the current edge is
			 * the collaboratingNode1. If yes then we go on to check if the collaboratingNode2
			 * matches either the source or the target node. We dont care about the directionality
			 * of the edge. 
			 * */
			if (currentEdge.getSourceNode().getNodeID() == collaboratingNode1.getNodeID() 
					|| currentEdge.getTargetNode().getNodeID() == collaboratingNode1.getNodeID()) {
				
				if (currentEdge.getSourceNode().getNodeID() == collaboratingNode2.getNodeID() 
						|| currentEdge.getTargetNode().getNodeID() == collaboratingNode2.getNodeID()) {
					
					duplicateEdge = currentEdge;
					break;
				} 				
			} 
		}
		
		return duplicateEdge;
	}

	public Map<String, VivoCollegeOrSchool> getCollegeURLToVO() {
		return collegeURLToVO;
	}

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
			
			RDFNode publicationYearMonthNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_MONTH);
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
								   String resultFormatParam, 
								   String rdfResultFormatParam, 
								   DataSource dataSource) {

        QueryExecution queryExecution = null;
        try{
            Query query = QueryFactory.create(queryText, SYNTAX);

//            QuerySolutionMap qs = new QuerySolutionMap();
//            qs.add("authPerson", queryParam); // bind resource to s
            
            queryExecution = QueryExecutionFactory.create(query, dataSource);
            

            //remocve this if loop after knowing what is describe & construct sparql stuff.
            if (query.isSelectType()){
                return queryExecution.execSelect();
            }
        } finally {
            if(queryExecution != null) {
            	queryExecution.close();
            }

        }
		return null;
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
							+ "		(str(?publicationYearMonth) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_MONTH + ") " 
							+ "		(str(?publicationDate) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ") " 
							+ "WHERE { "
							+ "<" + queryURI + "> rdf:type foaf:Person ; rdfs:label ?authorLabel ; core:authorInAuthorship ?authorshipNode . "
							+ "?authorshipNode rdf:type core:Authorship ; core:linkedInformationResource ?document . "
							+ "?document rdf:type bibo:Document . " 
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
//							+ "FILTER (<" + queryURI + "> != ?coAuthorPerson ) . "
							+ "}";

//		System.out.println(sparqlQuery);
		
		return sparqlQuery;
	}

	
	public VisVOContainer getVisualizationJavaValueObjects()
		throws MalformedQueryParametersException {

        if (this.egoURLParam == null || "".equals(egoURLParam)) {
        	throw new MalformedQueryParametersException("URI parameter is either null or empty.");
        } else {

        	/*
        	 * To test for the validity of the URI submitted.
        	 * */
        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
    		IRI iri = iRIFactory.create(this.egoURLParam);
            if (iri.hasViolation(false)) {
                String errorMsg = ((Violation)iri.violations(false).next()).getShortMessage()+" ";
                log.error("Ego Co Authorship Vis Query " + errorMsg);
                throw new MalformedQueryParametersException("URI provided for an individual is malformed.");
            }
        }

		ResultSet resultSet	= executeQuery(generateEgoCoAuthorshipSparqlQuery(this.egoURLParam),
										   this.resultFormatParam,
										   this.rdfResultFormatParam,
										   this.dataSource);

		return createJavaValueObjects(resultSet);
	}

	public Map<String, Integer> getYearToPublicationCount(
			Set<BiboDocument> authorDocuments) {

    	/*
    	 * Create a map from the year to number of publications. Use the BiboDocument's
    	 * parsedPublicationYear to populate the data.
    	 * 
    	 * I am pushing the logic to check for validity of year in "getPublicationYear" itself
		 * because,
		 * 	1. We will be using getPub... multiple times & this will save us duplication of code
		 * 	2. If we change the logic of validity of a pub year we would not have to make changes
		 * all throughout the codebase.
		 * 	3. We are asking for a publication year & we should get a proper one or NOT at all.
		 * */
    	Map<String, Integer> yearToPublicationCount = new TreeMap<String, Integer>();

    	for (BiboDocument curr : authorDocuments) {

    		/*
    		 * Increment the count because there is an entry already available for
    		 * that particular year.
    		 * */
    		String publicationYear;
    		if (curr.getPublicationYear() != null) {
    			publicationYear = curr.getPublicationYear();
    		} else {
    			publicationYear = curr.getParsedPublicationYear();
    		}
    		
			if (yearToPublicationCount.containsKey(publicationYear)) {
    			yearToPublicationCount.put(publicationYear,
    									   yearToPublicationCount
    									   		.get(publicationYear) + 1);

    		} else {
    			yearToPublicationCount.put(publicationYear, 1);
    		}

    	}

		return yearToPublicationCount;
	}





}
