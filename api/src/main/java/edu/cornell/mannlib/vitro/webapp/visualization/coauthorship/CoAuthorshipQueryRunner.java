/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationCaches;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CoAuthorshipData;
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
public class CoAuthorshipQueryRunner implements QueryRunner<CoAuthorshipData> {

	private static final int MAX_AUTHORS_PER_PAPER_ALLOWED = 100;

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private static boolean preferCaches = false;

	private String egoURI;

	private RDFService rdfService;

	private VitroRequest vitroRequest;

	private Log log;

	public CoAuthorshipQueryRunner(String egoURI, VitroRequest vreq, Log log) {

		this.egoURI = egoURI;
		this.rdfService = vreq.getRDFService();
		this.vitroRequest = vreq;
		this.log = log;

	}

	private static class QueryResultConsumer extends ResultSetConsumer {
		Set<Collaborator> nodes = new HashSet<Collaborator>();

		Map<String, Activity> biboDocumentURLToVO = new HashMap<String, Activity>();
		Map<String, Set<Collaborator>> biboDocumentURLToCoAuthors = new HashMap<String, Set<Collaborator>>();
		Map<String, Collaborator> nodeURLToVO = new HashMap<String, Collaborator>();
		Map<String, Collaboration> edgeUniqueIdentifierToVO = new HashMap<String, Collaboration>();

		Collaborator egoNode = null;

		Set<Collaboration> edges = new HashSet<Collaboration>();

		private UniqueIDGenerator nodeIDGenerator = new UniqueIDGenerator();
		private UniqueIDGenerator edgeIDGenerator = new UniqueIDGenerator();

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			RDFNode egoAuthorURLNode = qs.get(QueryFieldLabels.AUTHOR_URL);
			RDFNode authorLabelNode = qs.get(QueryFieldLabels.AUTHOR_LABEL);
			RDFNode documentNode = qs.get(QueryFieldLabels.DOCUMENT_URL);
			RDFNode coAuthorURLNode = qs.get(QueryFieldLabels.CO_AUTHOR_URL);
			RDFNode coAuthorLabelNode = qs.get(QueryFieldLabels.CO_AUTHOR_LABEL);
			RDFNode publicationDateNode = qs.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);

			String authorURI    = egoAuthorURLNode == null ? null : egoAuthorURLNode.asLiteral().getString();
			String authorName   = authorLabelNode == null ? null : authorLabelNode.asLiteral().getString();
			String documentURI  = documentNode == null ? null : documentNode.asLiteral().getString();
			String documentDate = publicationDateNode == null ? null : publicationDateNode.asLiteral().getString();
			String coAuthorURI  = coAuthorURLNode == null ? null : coAuthorURLNode.asLiteral().getString();
			String coAuthorName = coAuthorLabelNode == null ? null : coAuthorLabelNode.asLiteral().getString();

			processEntry(authorURI, authorName, documentURI, documentDate, coAuthorURI, coAuthorName);
		}

		public void processEntry(String authorURI, String authorName, String documentURI, String documentDate, String coAuthorURI, String coAuthorName) {
			/*
			 * We only want to create only ONE ego node.
			 * */
			if (nodeURLToVO.containsKey(authorURI)) {
				egoNode = nodeURLToVO.get(authorURI);
			} else {
				egoNode = new Collaborator(authorURI, nodeIDGenerator);
				nodes.add(egoNode);
				nodeURLToVO.put(authorURI, egoNode);

				if (authorName != null) {
					egoNode.setCollaboratorName(authorName);
				}
			}

			Activity biboDocument;

			if (biboDocumentURLToVO.containsKey(documentURI)) {
				biboDocument = biboDocumentURLToVO.get(documentURI);
			} else {
				biboDocument = createDocumentVO(documentURI, documentDate);
				biboDocumentURLToVO.put(documentURI, biboDocument);
			}

			egoNode.addActivity(biboDocument);

			/*
			 * After some discussion we concluded that for the purpose of this visualization
			 * we do not want a co-author node or Collaboration if the publication has only one
			 * author and that happens to be the ego.
			 * */
			if (authorURI.equalsIgnoreCase(coAuthorURI)) {
				return;
			}

			Collaborator coAuthorNode;

			if (nodeURLToVO.containsKey(coAuthorURI)) {
				coAuthorNode = nodeURLToVO.get(coAuthorURI);
			} else {
				coAuthorNode = new Collaborator(coAuthorURI, nodeIDGenerator);
				nodes.add(coAuthorNode);
				nodeURLToVO.put(coAuthorURI, coAuthorNode);

				if (coAuthorName != null) {
					coAuthorNode.setCollaboratorName(coAuthorName);
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

		@Override
		protected void endProcessing() {
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
		}

		public CoAuthorshipData getCollaborationData() {
			return new CoAuthorshipData(egoNode, nodes, edges, biboDocumentURLToVO);
		}

		private Activity createDocumentVO(String documentURL, String documentDate) {

			Activity biboDocument = new Activity(documentURL);

			if (documentDate != null) {
				biboDocument.setActivityDate(documentDate);
			}

			return biboDocument;
		}

		private String getEdgeUniqueIdentifier(int nodeID1, int nodeID2) {
			String separator = "*";

			if (nodeID1 < nodeID2) {
				return nodeID1 + separator + nodeID2;
			} else {
				return nodeID2 + separator + nodeID1;
			}

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

					coAuthorNodes.sort(new CollaboratorComparator());

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

		/* END QUERY RUNNER */
	}

	private String generateEgoCoAuthorshipSparqlQuery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
				+ "PREFIX local: <http://localhost/>\n"
				+ "SELECT \n"
				+ "		(str(<" + queryURI + ">) as ?" + QueryFieldLabels.AUTHOR_URL + ") \n"
				+ "		(str(?authorLabel) as ?" + QueryFieldLabels.AUTHOR_LABEL + ") \n"
				+ "		(str(?coAuthorPerson) as ?" + QueryFieldLabels.CO_AUTHOR_URL + ") \n"
				+ "		(str(?coAuthorPersonLabel) as ?" + QueryFieldLabels.CO_AUTHOR_LABEL + ") \n"
				+ "		(str(?document) as ?" + QueryFieldLabels.DOCUMENT_URL + ") \n"
				+ "		(str(?publicationDate) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ") \n"
				+ "WHERE { \n"
				+ "    <" + queryURI + "> local:authorLabel ?authorLabel ;"
								  + " local:authorOf ?document . \n"
	            + "    ?document local:coAuthor ?coAuthorPerson . \n"
				+ "    ?coAuthorPerson rdfs:label ?coAuthorPersonLabel . \n"
				+ "    OPTIONAL { ?document local:publicationDate ?publicationDate . } \n"
				+ "} \n"
				+ "ORDER BY ?document ?coAuthorPerson\n";

		log.debug("COAUTHORSHIP QUERY - " + sparqlQuery);

		return sparqlQuery;
	}

	private String generateEgoCoAuthorshipSparqlConstruct(String queryURI) {
		String sparqlConstruct = QueryConstants.getSparqlPrefixQuery()
				+ "PREFIX local: <http://localhost/>\n"
				+ "CONSTRUCT\n"
				+ "{\n"
				+ "    <" + queryURI + "> local:authorLabel ?authorLabel .\n"
				+ "    <" + queryURI + "> local:authorOf ?document .\n"
                + "    ?document local:publicationDate ?publicationDate .\n"
                + "    ?document local:coAuthor ?coAuthorPerson .\n"
                + "    ?coAuthorPerson rdfs:label ?coAuthorPersonLabel .\n"
				+ "}\n"
				+ "WHERE\n"
				+ "{\n"
                + "    {\n"
				+ "        <" + queryURI + "> rdf:type foaf:Person ;"
				+ "                rdfs:label ?authorLabel ;"
				+ "                core:relatedBy ?authorshipNode . \n"
				+ "        ?authorshipNode rdf:type core:Authorship ;"
				+ "                core:relates ?document . \n"
				+ "        ?document rdf:type <http://purl.obolibrary.org/obo/IAO_0000030> ; \n"
				+ "                core:relatedBy ?coAuthorshipNode . \n"
				+ "        ?coAuthorshipNode rdf:type core:Authorship ; \n"
				+ "                core:relates ?coAuthorPerson . \n"
				+ "        ?coAuthorPerson rdf:type foaf:Person ; \n"
				+ "                rdfs:label ?coAuthorPersonLabel . \n"
                + "    }\n"
                + "    UNION\n"
                + "    {\n"
                + "        <" + queryURI + "> rdf:type foaf:Person ;"
                + "                rdfs:label ?authorLabel ;"
                + "                core:relatedBy ?authorshipNode . \n"
                + "        ?authorshipNode rdf:type core:Authorship ;"
                + "                core:relates ?document . \n"
                + "        ?document core:dateTimeValue ?dateTimeValue . \n"
                + "		   ?dateTimeValue core:dateTime ?publicationDate . \n"
                + "    }\n"
				+ "}\n"
		;

		return sparqlConstruct;
	}

	public CoAuthorshipData getQueryResult()
		throws MalformedQueryParametersException {

		CoAuthorshipData data = getCachedData(this.egoURI);
		if (data != null) {
			return data;
		}

		return getQueryResultAndCache();
	}

	private CoAuthorshipData getCachedData(String egoURI) {
		CollaborationDataCacheEntry entry = collaborationDataCache.get(egoURI);
		if (entry != null && !entry.hasExpired()) {
			entry.accessTime = new Date().getTime();
			expireCache();
			return entry.data;
		}

		return null;
	}

	private synchronized CoAuthorshipData getQueryResultAndCache()
			throws MalformedQueryParametersException {

		CoAuthorshipData data = getCachedData(this.egoURI);
		if (data != null) {
			return data;
		}

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

		Date cacheTime = null;
		QueryResultConsumer consumer = new QueryResultConsumer();

		// If we've had long running queries (preferCaches), and the caches are available, use the cache
		if (preferCaches && VisualizationCaches.personToPublication.isCached()) {
			cacheTime = VisualizationCaches.personToPublication.cachedWhen();

			Map<String, String>      personLabelsMap         = VisualizationCaches.personLabels.get(rdfService);
			Map<String, Set<String>> personToPublicationMap  = VisualizationCaches.personToPublication.get(rdfService).personToPublication;
			Map<String, Set<String>> publicationToPersonMap  = VisualizationCaches.personToPublication.get(rdfService).publicationToPerson;
			Map<String, String>      publicationToYearMap    = VisualizationCaches.publicationToYear.get(rdfService);

			String authorURI = this.egoURI;
			String authorName = personLabelsMap.get(authorURI);
			for (String documentURI : personToPublicationMap.get(authorURI)) {
				String documentDate = publicationToYearMap.get(documentURI);
				for (String coAuthorURI : publicationToPersonMap.get(documentURI)) {
					String coAuthorName = personLabelsMap.get(coAuthorURI);
					consumer.processEntry(authorURI, authorName, documentURI, documentDate, coAuthorURI, coAuthorName);
				}
			}
			consumer.endProcessing();
		} else {
			// Not use the caches, so query the triple store - recording the time it took
			long queryStart = System.currentTimeMillis();
			try {
				Model constructedModel = ModelFactory.createDefaultModel();
				rdfService.sparqlConstructQuery(generateEgoCoAuthorshipSparqlConstruct(this.egoURI), constructedModel);
				QueryExecution qe = QueryExecutionFactory.create(generateEgoCoAuthorshipSparqlQuery(this.egoURI), constructedModel);
				try {
					consumer.processResultSet(qe.execSelect());
				} finally {
					qe.close();
				}
			} catch (RDFServiceException e) {
				log.error("Unable to execute query", e);
				throw new RuntimeException(e);
			} finally {
				// If the query took more than 5 seconds, start using the caches
				if (System.currentTimeMillis() - queryStart > 5000) {
					// If the caches haven't already been built, request a rebuild
					if (!preferCaches && !VisualizationCaches.personToPublication.isCached()) {
						VisualizationCaches.rebuildAll();
					}

					// Attempt to use caches next time
					preferCaches = true;
				}
			}
		}

		if (consumer.egoNode == null) {
			consumer.egoNode = makeEgoNode();
		}

		data = consumer.getCollaborationData();
		if (cacheTime != null) {
			data.setBuiltFromCacheTime(cacheTime);
		}

		CollaborationDataCacheEntry newEntry = new CollaborationDataCacheEntry();

		newEntry.uri = this.egoURI;
		newEntry.data = data;
		newEntry.creationTime = newEntry.accessTime = new Date().getTime();

		// Remove dead entries
		expireCache();

		// Cache the new entry
		collaborationDataCache.put(this.egoURI, newEntry);

		return data;
	}

	private Collaborator makeEgoNode() {
		Collaborator collab = new Collaborator(egoURI, new UniqueIDGenerator());
		collab.setCollaboratorName(UtilityFunctions.getIndividualLabelFromDAO(vitroRequest, egoURI));

		return collab;
	}

	private synchronized void expireCache() {
		for (String key : collaborationDataCache.keySet()) {
			CollaborationDataCacheEntry entry = collaborationDataCache.get(key);
			if (entry != null && entry.hasExpired()) {
				collaborationDataCache.remove(key);
			}
		}
	}

	private static final Map<String, CollaborationDataCacheEntry> collaborationDataCache = new ConcurrentHashMap<String, CollaborationDataCacheEntry>();

	private static class CollaborationDataCacheEntry {
		String uri;
		CoAuthorshipData data;
		long creationTime;
		long accessTime;

		boolean hasExpired() {
			long now = new Date().getTime();

			// If it's older than five minutes, and it hasn't *just* been accessed, it's expired
			if (creationTime < (now - 300000L) && accessTime < (now - 3000L)) {
				return true;
			}

			// Otherwise, if it is more than 30 seconds old, expire it
			return accessTime < (now - 30000L);
		}
	}
}
