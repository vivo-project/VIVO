/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.entitycomparison;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Model;


import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;


/**
 * This query runner is used to execute a sparql query that will fetch all the
 * publications defined by bibo:Document property for a particular
 * department/school/university.
 * 
 * Deepak Konidena.
 * @author bkoniden
 */
public class EntityPublicationCountQueryRunner implements QueryRunner<Entity> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String entityURI;
	private Model dataSource;
	private Log log = LogFactory.getLog(EntityPublicationCountQueryRunner.class.getName());
	private long before, after;

	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = ""
			+ "		(str(?Person) as ?personLit) "
			+ "		(str(?PersonLabel) as ?personLabelLit) "
			+ "		(str(?Document) as ?documentLit) "
			+ "		(str(?DocumentLabel) as ?documentLabelLit) "
			+ "		(str(?publicationDate) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ")";


	private static final String SPARQL_QUERY_COMMON_WHERE_CLAUSE = ""
			+ "?Document rdf:type bibo:Document ;"
			+ " rdfs:label ?DocumentLabel ."
			+ "OPTIONAL {  ?Document core:dateTimeValue ?dateTimeValue . " 
			+ "				?dateTimeValue core:dateTime ?publicationDate } ." 
			+ "OPTIONAL {  ?Document core:year ?publicationYearUsing_1_1_property } .";

	private static final String ENTITY_LABEL = QueryFieldLabels.ORGANIZATION_LABEL;
	private static final String ENTITY_URL = QueryFieldLabels.ORGANIZATION_URL;
	private static final String SUBENTITY_LABEL = QueryFieldLabels.SUBORGANIZATION_LABEL;
	private static final String SUBENTITY_URL = QueryFieldLabels.SUBORGANIZATION_URL;
	
	public EntityPublicationCountQueryRunner(String entityURI,
			Model dataSource, Log log) {

		this.entityURI = entityURI;
		this.dataSource = dataSource;
//		this.log = log;

	}

	private Entity createJavaValueObjects(ResultSet resultSet) {

		Entity entity = null;
		Map<String, Activity> biboDocumentURLToVO = new HashMap<String, Activity>();
		Map<String, SubEntity> subentityURLToVO = new HashMap<String, SubEntity>();
		Map<String, SubEntity> personURLToVO = new HashMap<String, SubEntity>();

		before = System.currentTimeMillis();

		while (resultSet.hasNext()) {

			QuerySolution solution = resultSet.nextSolution();

			if (entity == null) {
				entity = new Entity(solution.get(ENTITY_URL).toString(),
						solution.get(ENTITY_LABEL).toString());
			}

			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			Activity biboDocument;

			if (biboDocumentURLToVO.containsKey(documentNode.toString())) {
				biboDocument = biboDocumentURLToVO.get(documentNode.toString());

			} else {

				biboDocument = new Activity(documentNode.toString());
				biboDocumentURLToVO.put(documentNode.toString(), biboDocument);

				RDFNode publicationDateNode = solution.get(QueryFieldLabels
																.DOCUMENT_PUBLICATION_DATE);
				if (publicationDateNode != null) {
					biboDocument.setActivityDate(publicationDateNode.toString());
				}

			}

			RDFNode subEntityURLNode = solution.get(SUBENTITY_URL);
			
			if (subEntityURLNode != null) {
				SubEntity subEntity;
				if (subentityURLToVO.containsKey(subEntityURLNode.toString())) {
					subEntity = subentityURLToVO.get(subEntityURLNode
							.toString());
				} else {
					subEntity = new SubEntity(subEntityURLNode.toString());
					subentityURLToVO
							.put(subEntityURLNode.toString(), subEntity);
				}

				RDFNode subEntityLabelNode = solution.get(SUBENTITY_LABEL);
				if (subEntityLabelNode != null) {
					subEntity.setIndividualLabel(subEntityLabelNode.toString());
				}
				
				entity.addSubEntity(subEntity);
				
				subEntity.addActivity(biboDocument);
			}
			
			RDFNode personURLNode = solution.get(QueryFieldLabels.PERSON_URL);
			
			if (personURLNode != null) {
				SubEntity person;
				
				if (personURLToVO.containsKey(personURLNode.toString())) {
					person = personURLToVO.get(personURLNode.toString());
				} else {
					person = new SubEntity(personURLNode.toString());
					personURLToVO.put(personURLNode.toString(), person);
				}
				
				RDFNode personLabelNode = solution.get(QueryFieldLabels.PERSON_LABEL);
				if (personLabelNode != null) {
					person.setIndividualLabel(personLabelNode.toString());
				}
				
				/*
				 * This makes sure that either,
				 * 		1. the parent organization is a department-like organization with no 
				 * organizations beneath it, or 
				 * 		2. the parent organizations has both sub-organizations and people directly 
				 * attached to that organizations e.g. president of a university.
				 * */
				if (subEntityURLNode == null) {

					entity.addSubEntity(person);
					
				}
				
				person.addActivity(biboDocument);				

			}			

//			entity.addActivity(biboDocument);
		}
		
		if (subentityURLToVO.size() == 0 && personURLToVO.size() == 0) {
			
			entity = new Entity(this.entityURI, "no-label");
			
		}
		
		after = System.currentTimeMillis();
		log.debug("Time taken to iterate through the ResultSet of SELECT queries is in ms: " 
				+ (after - before));

		return entity;
	}
		
	private ResultSet executeQuery(String queryURI, Model dataSource) {

		QueryExecution queryExecution = null;
		Query query = QueryFactory.create(
				getSparqlQueryForOrganization(queryURI), SYNTAX);
		queryExecution = QueryExecutionFactory.create(query, dataSource);
		return queryExecution.execSelect();
	}

	private String getSparqlQueryForOrganization(String queryURI) {
		
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
		+ "SELECT 	(str(?organizationLabel) as ?organizationLabelLit) "
		+ "	 		(str(?subOrganization) as ?subOrganizationLit) "
		+ "			(str(?subOrganizationLabel) as ?subOrganizationLabelLit) "
		+ SPARQL_QUERY_COMMON_SELECT_CLAUSE + "		(str(<" + queryURI
		+ ">) as ?" + ENTITY_URL + ") "
		+ "WHERE { " + "<" + queryURI + "> rdfs:label ?organizationLabel ."
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization ."
		+ "?subOrganization rdfs:label ?subOrganizationLabel ;" 
			+ " core:organizationForPosition ?Position . "
		+ " ?Position core:positionForPerson ?Person ."
		+ " ?Person  core:authorInAuthorship ?Resource ;   rdfs:label ?PersonLabel . "
		+ " ?Resource core:linkedInformationResource ?Document .  "
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + "> core:organizationForPosition ?Position ."
		+ " ?Position core:positionForPerson ?Person ."
		+ "	?Person  core:authorInAuthorship ?Resource ;   rdfs:label ?PersonLabel . "
		+ " ?Resource core:linkedInformationResource ?Document ."
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "}";

		log.debug("\nThe sparql query is :\n" + sparqlQuery);
		return sparqlQuery;

	}
	
	public Entity getQueryResult() throws MalformedQueryParametersException {

		if (StringUtils.isNotBlank(this.entityURI)) {
			/*
			 * To test for the validity of the URI submitted.
			 */
			IRIFactory iRIFactory = IRIFactory.jenaImplementation();
			IRI iri = iRIFactory.create(this.entityURI);
			if (iri.hasViolation(false)) {
				String errorMsg = ((Violation) iri.violations(false).next())
						.getShortMessage();
				log.error("Entity Pub Count Query " + errorMsg);
				throw new MalformedQueryParametersException(
						"URI provided for an entity is malformed.");
			}

		} else {
			throw new MalformedQueryParametersException(
					"URL parameter is either null or empty.");
		}

		before = System.currentTimeMillis();
		
		ResultSet resultSet = executeQuery(this.entityURI, this.dataSource);
		
		after = System.currentTimeMillis();
		
		log.debug("Time taken to execute the SELECT queries is in milliseconds: " 
						+ (after - before));
		
		return createJavaValueObjects(resultSet);
	}
}