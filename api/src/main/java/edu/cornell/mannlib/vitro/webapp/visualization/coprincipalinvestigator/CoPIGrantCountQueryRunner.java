/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CoInvestigationData;
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
 * @author bkoniden
 * Deepak Konidena
 */
public class CoPIGrantCountQueryRunner implements QueryRunner<CollaborationData> {

	private static final int MAX_PI_PER_GRANT_ALLOWED = 100;

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String egoURI;

	private RDFService rdfService;
	private VitroRequest vitroRequest;

	private Log log = LogFactory.getLog(CoPIGrantCountQueryRunner.class.getName());

	private long before, after;

	private static final String SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME = ""
		+ 		"OPTIONAL {"
		+ "			?Role core:dateTimeInterval ?dateTimeIntervalValue . "
		+			"?dateTimeIntervalValue core:start ?startDate . "
		+			"?startDate core:dateTime ?startDateTimeValue . "
//		+			"OPTIONAL {"
//		+				"?dateTimeIntervalValue core:end ?endDate . "
//		+				"?endDate core:dateTime ?endDateTimeValue . "
//		+			"}"
		+ 		"} . ";

	private static final String SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME = ""
		+ 		"OPTIONAL {"
		+ "			?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
		+			"?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
		+			"?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
//		+			"OPTIONAL {"
//		+				"?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
//		+				"?endDateForGrant core:dateTime ?endDateTimeValueForGrant . "
//		+			"}"
		+ 		"}";


	public CoPIGrantCountQueryRunner(String egoURI, VitroRequest vreq, Log log) {

		this.egoURI = egoURI;
		this.rdfService = vreq.getRDFService();
		this.vitroRequest = vreq;
	//	this.log = log;
	}

	private String generateEgoCoPIquery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
			+ "SELECT "
			+ "		(str(<" + queryURI + ">) as ?" + QueryFieldLabels.PI_URL + ") "
			+ "		(str(?PILabel) as ?" + QueryFieldLabels.PI_LABEL + ") "
			+ "		(str(?Grant) as ?"	+ QueryFieldLabels.GRANT_URL + ") "
//			+ "		(str(?GrantLabel) as ?" + QueryFieldLabels.GRANT_LABEL + ") "
			+ " 	(str(?startDateTimeValue) as ?grantStartDateLit) "
//			+ "		(str(?endDateTimeValue) as ?grantEndDateLit)  "
			+ " 	(str(?startDateTimeValueForGrant) as ?grantStartDateForGrantLit) "
//			+ "		(str(?endDateTimeValueForGrant) as ?grantEndDateForGrantLit)  "
			+ "		(str(?CoPI) as ?" + QueryFieldLabels.CO_PI_URL + ") "
			+ "		(str(?CoPILabel) as ?" + QueryFieldLabels.CO_PI_LABEL + ") "
			+ "WHERE "
			+ "{ "
			+ 		"<" + queryURI + "> rdfs:label ?PILabel . "
			+  		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
		    +			"?Role rdf:type core:CoPrincipalInvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:PrincipalInvestigatorRole . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
	        +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+  		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:CoPrincipalInvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:InvestigatorRole . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
	        +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+  		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:CoPrincipalInvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:CoPrincipalInvestigatorRole  . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
	        +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:PrincipalInvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:PrincipalInvestigatorRole  . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
			+			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:PrincipalInvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:InvestigatorRole  . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
	        +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:PrincipalInvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:CoPrincipalInvestigatorRole . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
	        +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:InvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:InvestigatorRole  . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
	        +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:InvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:CoPrincipalInvestigatorRole  . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
	        +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+		"UNION "
			+		"{ "
			+			"<" + queryURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
	        +			"?Role rdf:type core:InvestigatorRole . "
			+			"?Role core:relatedBy ?Grant . "
			+			"?Grant rdf:type core:Grant . "
			+			"?Grant core:relates ?RelatedRole . "
			+			"?RelatedRole rdf:type core:PrincipalInvestigatorRole . "
			+			"?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?CoPI . "
		    +			"?CoPI rdf:type foaf:Person .	"
			+			"?CoPI rdfs:label ?CoPILabel .	"
			+ 			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME
			+			SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME
			+ 		"} "
			+ "} ";
		return sparqlQuery;
	}

	public CoInvestigationData getQueryResult()
		throws MalformedQueryParametersException {

		CoInvestigationData data = getCachedData(this.egoURI);
		if (data != null) {
			return data;
		}

		return getQueryResultAndCache();
	}

	private CoInvestigationData getCachedData(String egoURI) {
		CollaborationDataCacheEntry entry = collaborationDataCache.get(egoURI);
		if (entry != null && !entry.hasExpired()) {
			entry.accessTime = new Date().getTime();
			expireCache();
			return entry.data;
		}

		return null;
	}

	private synchronized CoInvestigationData getQueryResultAndCache()
			throws MalformedQueryParametersException {

		CoInvestigationData data = getCachedData(this.egoURI);
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
				log.error("Ego Co-PI Vis Query " + errorMsg);
				throw new MalformedQueryParametersException(
						"URI provided for an individual is malformed.");
			}
		} else {
			throw new MalformedQueryParametersException("URI parameter is either null or empty.");
		}

		before = System.currentTimeMillis();

		try {
			QueryResultConsumer consumer = new QueryResultConsumer();
			rdfService.sparqlSelectQuery(generateEgoCoPIquery(this.egoURI), consumer);
			if (consumer.egoNode == null) {
				consumer.egoNode = makeEgoNode();
			}
			data = consumer.getData();
		} catch (RDFServiceException e) {
			log.error("Unable to execute query", e);
			throw new RuntimeException(e);
		}
		after = System.currentTimeMillis();

		log.debug("Time taken to execute the SELECT queries is in milliseconds: " + (after - before));

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
		CoInvestigationData data;
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

	private static class QueryResultConsumer extends ResultSetConsumer {
		Set<Collaborator> nodes = new HashSet<Collaborator>();

		Map<String, Activity> grantURLToVO = new HashMap<String, Activity>();
		Map<String, Set<Collaborator>> grantURLToCoPIs = new HashMap<String, Set<Collaborator>>();
		Map<String, Collaborator> nodeURLToVO = new HashMap<String, Collaborator>();
		Map<String, Collaboration> edgeUniqueIdentifierToVO = new HashMap<String, Collaboration>();

		Collaborator egoNode = null;

		Set<Collaboration> edges = new HashSet<Collaboration>();

		private UniqueIDGenerator nodeIDGenerator = new UniqueIDGenerator();
		private UniqueIDGenerator edgeIDGenerator = new UniqueIDGenerator();

		private Log log = LogFactory.getLog(CoPIGrantCountQueryRunner.class.getName());

		@Override
		protected void processQuerySolution(QuerySolution qs) {
				/*
				 * We only want to create only ONE ego node.
				 * */
			RDFNode egoPIURLNode = qs.get(QueryFieldLabels.PI_URL);
			if (nodeURLToVO.containsKey(egoPIURLNode.toString())) {

				egoNode = nodeURLToVO.get(egoPIURLNode.toString());

			} else {

				egoNode = new Collaborator(egoPIURLNode.toString(), nodeIDGenerator);
				nodes.add(egoNode);
				nodeURLToVO.put(egoPIURLNode.toString(), egoNode);


				RDFNode authorLabelNode = qs.get(QueryFieldLabels.PI_LABEL);
				if (authorLabelNode != null) {
					egoNode.setCollaboratorName(authorLabelNode.toString());
				}
			}
			log.debug("PI: " + egoNode.getIndividualLabel());

			RDFNode grantNode = qs.get(QueryFieldLabels.GRANT_URL);
			Activity grant;

			if (grantURLToVO.containsKey(grantNode.toString())) {
				grant = grantURLToVO.get(grantNode.toString());
			} else {
				grant = createGrantVO(qs, grantNode.toString());
				grantURLToVO.put(grantNode.toString(), grant);
			}

			egoNode.addActivity(grant);
			log.debug("Adding grant: " + grant.getIndividualLabel());

				/*
				 * After some discussion we concluded that for the purpose of this visualization
				 * we do not want a co-pi node or edge if the grant has only one
				 * pi and that happens to be the ego.
				 * */
			if (qs.get(QueryFieldLabels.PI_URL).toString().equalsIgnoreCase(
					qs.get(QueryFieldLabels.CO_PI_URL).toString())) {
				return;
			}

			Collaborator coPINode;

			RDFNode coPIURLNode = qs.get(QueryFieldLabels.CO_PI_URL);
			if (nodeURLToVO.containsKey(coPIURLNode.toString())) {

				coPINode = nodeURLToVO.get(coPIURLNode.toString());

			} else {

				coPINode = new Collaborator(coPIURLNode.toString(), nodeIDGenerator);
				nodes.add(coPINode);
				nodeURLToVO.put(coPIURLNode.toString(), coPINode);

				RDFNode coPILabelNode = qs.get(QueryFieldLabels.CO_PI_LABEL);
				if (coPILabelNode != null) {
					coPINode.setCollaboratorName(coPILabelNode.toString());
				}
			}

			log.debug("Adding CO-PI: "+ coPINode.getIndividualLabel());
			coPINode.addActivity(grant);

			Set<Collaborator> coPIsForCurrentGrant;

			if (grantURLToCoPIs.containsKey(grant.getActivityURI())) {
				coPIsForCurrentGrant = grantURLToCoPIs.get(grant.getActivityURI());
			} else {
				coPIsForCurrentGrant = new HashSet<Collaborator>();
				grantURLToCoPIs.put(grant.getActivityURI(),
						coPIsForCurrentGrant);
			}

			coPIsForCurrentGrant.add(coPINode);
			log.debug("Co-PI for current grant : " + coPINode.getIndividualLabel());

			Collaboration egoCoPIEdge =
					getExistingEdge(egoNode, coPINode, edgeUniqueIdentifierToVO);
				/*
				 * If "egoCoPIEdge" is null it means that no edge exists in between the egoNode
				 * & current coPINode. Else create a new edge, add it to the edges set & add
				 * the collaborator grant to it.
				 * */
			if (egoCoPIEdge != null) {
				egoCoPIEdge.addActivity(grant);
			} else {
				egoCoPIEdge = new Collaboration(egoNode, coPINode, grant, edgeIDGenerator);
				edges.add(egoCoPIEdge);
				edgeUniqueIdentifierToVO.put(
						getEdgeUniqueIdentifier(egoNode.getCollaboratorID(),
								coPINode.getCollaboratorID()),
						egoCoPIEdge);
			}
		}


		@Override
		protected void endProcessing() {
			super.endProcessing();
			/*
			 * This method takes out all the PIs & edges between PIs that belong to grants
			 * that have more than 100 PIs. We conjecture that these grants do not provide much
			 * insight. However, we have left the grants be.
			 * This method side-effects "nodes" & "edges".
			 * */
			removeLowQualityNodesAndEdges(nodes,
					grantURLToVO,
					grantURLToCoPIs,
					edges);
			/*
			 * We need to create edges between 2 co-PIs. E.g. On a grant there were 3 PI
			 * ego, A & B then we have already created edges like,
			 * 		ego - A
			 * 		ego - B
			 * The below sub-routine will take care of,
			 * 		A - B
			 *
			 * We are side-effecting "edges" here. The only reason to do this is because we are
			 * adding edges en masse for all the co-PIs on all the grants considered so far. The
			 * other reason being we dont want to compare against 2 sets of edges (edges created
			 * before & co-PI edges created during the course of this method) when we are creating
			 * a new edge.
			 * */
			createCoPIEdges(grantURLToVO,
					grantURLToCoPIs,
					edges,
					edgeUniqueIdentifierToVO);
		}


		public CoInvestigationData getData() {
			return new CoInvestigationData(egoNode, nodes, edges, grantURLToVO);
		}

		private void createCoPIEdges(Map<String, Activity> grantURLToVO,
									 Map<String, Set<Collaborator>> grantURLToCoPIs, Set<Collaboration> edges,
									 Map<String, Collaboration> edgeUniqueIdentifierToVO) {

			for (Map.Entry<String, Set<Collaborator>> currentGrantEntry
					: grantURLToCoPIs.entrySet()) {

		/*
		 * If there was only one co-PI (other than ego) then we dont have to create any
		 * edges. so the below condition will take care of that.
		 *
		 * We are restricting edges between co-PI if a particular grant has more than
		 * 100 co-PIs. Our conjecture is that such edges do not provide any good insight
		 * & causes unnecessary computations causing the server to time-out.
		 * */
				if (currentGrantEntry.getValue().size() > 1
						&& currentGrantEntry.getValue().size()
						<= MAX_PI_PER_GRANT_ALLOWED) {

					Set<Collaboration> newlyAddedEdges = new HashSet<Collaboration>();

			/*
			 * In order to leverage the nested "for loop" for making edges between all the
			 * co-PIs we need to create a list out of the set first.
			 * */
					List<Collaborator> coPINodes =
							new ArrayList<Collaborator>(currentGrantEntry.getValue());
					coPINodes.sort(new CollaboratorComparator());

					int numOfCoPIs = coPINodes.size();

					for (int ii = 0; ii < numOfCoPIs - 1; ii++) {
						for (int jj = ii + 1; jj < numOfCoPIs; jj++) {

							Collaborator coPI1 = coPINodes.get(ii);
							Collaborator coPI2 = coPINodes.get(jj);

							Collaboration coPI1_2Edge = getExistingEdge(coPI1,
									coPI2,
									edgeUniqueIdentifierToVO);

							Activity currentGrant = grantURLToVO.get(currentGrantEntry.getKey());

							if (coPI1_2Edge != null) {
								coPI1_2Edge.addActivity(currentGrant);
							} else {
								coPI1_2Edge = new Collaboration(coPI1,
										coPI2,
										currentGrant,
										edgeIDGenerator);
								newlyAddedEdges.add(coPI1_2Edge);
								edgeUniqueIdentifierToVO.put(
										getEdgeUniqueIdentifier(coPI1.getCollaboratorID(),
												coPI2.getCollaboratorID()),
										coPI1_2Edge);
							}
						}
					}
					edges.addAll(newlyAddedEdges);
				}
			}
		}

		private void removeLowQualityNodesAndEdges(Set<Collaborator> nodes,
												   Map<String, Activity> grantURLToVO,
												   Map<String, Set<Collaborator>> grantURLToCoPIs, Set<Collaboration> edges) {

			Set<Collaborator> nodesToBeRemoved = new HashSet<Collaborator>();
			for (Map.Entry<String, Set<Collaborator>> currentGrantEntry
					: grantURLToCoPIs.entrySet()) {

				if (currentGrantEntry.getValue().size() > MAX_PI_PER_GRANT_ALLOWED) {

					Activity currentGrant = grantURLToVO.get(currentGrantEntry.getKey());

					Set<Collaboration> edgesToBeRemoved = new HashSet<Collaboration>();

					for (Collaboration currentEdge : edges) {
						Set<Activity> currentCollaboratorGrants =
								currentEdge.getCollaborationActivities();

						if (currentCollaboratorGrants.contains(currentGrant)) {
							currentCollaboratorGrants.remove(currentGrant);
							if (currentCollaboratorGrants.isEmpty()) {
								edgesToBeRemoved.add(currentEdge);
							}
						}
					}

					edges.removeAll(edgesToBeRemoved);

					for (Collaborator currentCoPI : currentGrantEntry.getValue()) {
						currentCoPI.getCollaboratorActivities().remove(currentGrant);
						if (currentCoPI.getCollaboratorActivities().isEmpty()) {
							nodesToBeRemoved.add(currentCoPI);
						}
					}
				}
			}
			nodes.removeAll(nodesToBeRemoved);

		}

		private Activity createGrantVO(QuerySolution solution, String grantURL) {

			Activity grant = new Activity(grantURL);

//		RDFNode grantLabelNode = solution.get(QueryFieldLabels.GRANT_LABEL);
//		if (grantLabelNode != null) {
//			grant.setIndividualLabel(grantLabelNode.toString());
//		}


			RDFNode grantStartYear = solution.get(QueryFieldLabels.ROLE_START_DATE);
			if (grantStartYear != null) {
				grant.setActivityDate(grantStartYear.toString());
			} else {
				grantStartYear = solution.get(QueryFieldLabels.GRANT_START_DATE);
				if (grantStartYear != null) {
					grant.setActivityDate(grantStartYear.toString());
				}
			}

			//TODO: Verify that grant end date is not required.
		/*
		RDFNode grantEndDate = solution.get(QueryFieldLabels.ROLE_END_DATE);
		if (grantEndDate != null) {
			grant.setGrantEndDate(grantEndDate.toString());
		}else{
			grantEndDate = solution.get(QueryFieldLabels.GRANT_END_DATE);
			if(grantEndDate != null){
				grant.setGrantEndDate(grantEndDate.toString());
			}
		}
		*/

			return grant;
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

		/** END QUERY RESULT CONSUMER **/
	}
}
