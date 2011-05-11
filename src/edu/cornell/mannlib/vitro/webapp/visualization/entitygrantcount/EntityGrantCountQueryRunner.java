/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.entitygrantcount;

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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;

/**
 * This query runner is used to execute a sparql query that will fetch all the
 * grants defined by core:Grant property for a particular
 * department/school/university.
 * 
 * Deepak Konidena
 * 
 * @author bkoniden
 */
public class EntityGrantCountQueryRunner implements QueryRunner<Entity>  {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String entityURI;
	private Model dataSource;
	private Log log = LogFactory.getLog(EntityGrantCountQueryRunner.class.getName());
	private long before, after;

	
	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = "SELECT "
		+ "		(str(?organizationLabel) as ?organizationLabelLit) "
		+ "		(str(?subOrganization) as ?subOrganizationLit) "
		+ "		(str(?subOrganizationLabel) as ?subOrganizationLabelLit) "
		+ "		(str(?Person) as ?personLit) "
		+ "		(str(?PersonLabel) as ?personLabelLit) "
		+ "		(str(?Grant) as ?grantLit) "
		+ "		(str(?GrantLabel) as ?grantLabelLit) "
		+ " 	(str(?startDateTimeValue) as ?grantStartDateLit) "
		+ "		(str(?endDateTimeValue) as ?grantEndDateLit)  "
		+ " 	(str(?startDateTimeValueForGrant) as ?grantStartDateForGrantLit) "
		+ "		(str(?endDateTimeValueForGrant) as ?grantEndDateForGrantLit)";
	
	private static final String SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME =  " "
		+ "		?Role core:roleIn ?Grant . "
		+ "		?Grant rdfs:label ?GrantLabel . "
		+ 		"OPTIONAL {"	
		+ "			?Role core:dateTimeInterval ?dateTimeIntervalValue . "
		+			"?dateTimeIntervalValue core:start ?startDate . "		
		+			"?startDate core:dateTime ?startDateTimeValue . " 	
		+			"OPTIONAL {"	
		+				"?dateTimeIntervalValue core:end ?endDate . "	
		+				"?endDate core:dateTime ?endDateTimeValue . " 			
		+			"}"
		+ 		"}";

	private static final String SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME =  " "
		+ "		?Role core:roleIn ?Grant . "
		+ "		?Grant rdfs:label ?GrantLabel . "
		+ 		"OPTIONAL {"	
		+ "			?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
		+			"?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "		
		+			"?startDateForGrant core:dateTime ?startDateTimeValueForGrant . " 	
		+			"OPTIONAL {"	
		+				"?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "	
		+				"?endDateForGrant core:dateTime ?endDateTimeValueForGrant . " 			
		+			"}"
		+ 		"}";	
	
	
	private static final String ENTITY_LABEL = QueryFieldLabels.ORGANIZATION_LABEL;
	private static final String ENTITY_URL = QueryFieldLabels.ORGANIZATION_URL;
	private static final String SUBENTITY_LABEL = QueryFieldLabels.SUBORGANIZATION_LABEL;
	private static final String SUBENTITY_URL = QueryFieldLabels.SUBORGANIZATION_URL;

	
	public EntityGrantCountQueryRunner(String entityURI,
			Model constructedModel, Log log) {

		this.entityURI = entityURI;
		this.dataSource = constructedModel;

	}	
	
	private Entity createJavaValueObjects(ResultSet resultSet) {
		
		Entity entity = null;
		Map<String, Activity> grantURIToVO = new HashMap<String, Activity>();
		Map<String, SubEntity> subentityURLToVO = new HashMap<String, SubEntity>();
		Map<String, SubEntity> personURLToVO = new HashMap<String, SubEntity>();
		
		before = System.currentTimeMillis();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();

			if (entity == null) {
				entity = new Entity(solution.get(ENTITY_URL).toString(),
						solution.get(ENTITY_LABEL).toString());
			}

			RDFNode grantNode = solution.get(QueryFieldLabels.GRANT_URL);
			Activity grant;

			if (grantURIToVO.containsKey(grantNode.toString())) {
				grant = grantURIToVO.get(grantNode.toString());

			} else {

				grant = new Activity(grantNode.toString());
				grantURIToVO.put(grantNode.toString(), grant);

				RDFNode grantLabelNode = solution
						.get(QueryFieldLabels.GRANT_LABEL);
				if (grantLabelNode != null) {
					grant.setActivityLabel(grantLabelNode.toString());
				}

				RDFNode grantStartDateNode = solution
						.get(QueryFieldLabels.ROLE_START_DATE);
				if (grantStartDateNode != null) {
					grant.setActivityDate(grantStartDateNode.toString());
				} else {
					grantStartDateNode = solution
							.get(QueryFieldLabels.GRANT_START_DATE);
					if (grantStartDateNode != null) {
						grant.setActivityDate(grantStartDateNode.toString());
					}
				}

				//TODO: Verify grant end date not needed.
				/*
				RDFNode grantEndDateNode = solution
						.get(QueryFieldLabels.ROLE_END_DATE);
				if (grantEndDateNode != null) {
					grant.setGrantEndDate(grantEndDateNode.toString());
				} else {
					grantEndDateNode = solution
							.get(QueryFieldLabels.GRANT_END_DATE);
					if (grantEndDateNode != null) {
						grant.setGrantEndDate(grantEndDateNode.toString());
					}
				}
				*/

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
				subEntity.addActivity(grant);
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
				
				person.addActivity(grant);

			}

//			entity.addActivity(grant);
		}

		if (subentityURLToVO.size() == 0 && personURLToVO.size() == 0) {
			entity = new Entity(this.entityURI, "no-label");
		}
		
		after = System.currentTimeMillis();
		log.debug("Time taken to iterate through the ResultSet of SELECT queries is in ms: " 
						+ (after - before));
		return entity;
	}

	private ResultSet executeQuery(String queryURI, Model dataSource2) {

		QueryExecution queryExecution = null;
		Query query = QueryFactory.create(
				getSparqlQuery(queryURI), SYNTAX);
		queryExecution = QueryExecutionFactory.create(query, dataSource2);
		return queryExecution.execSelect();
	}	
	
	private String getSparqlQuery(String queryURI) {
		
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
		+ SPARQL_QUERY_COMMON_SELECT_CLAUSE + "		(str(<" + queryURI
		+ ">) as ?" + ENTITY_URL + ") "
		+ "WHERE { " + "<" + queryURI + "> rdfs:label ?organizationLabel ."
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization ."
		+ " ?subOrganization rdfs:label ?subOrganizationLabel ;" 
			+ " core:organizationForPosition ?Position . "
		+ " ?Position core:positionForPerson ?Person ."
		+ " ?Person  core:hasCo-PrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel ."
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME  
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization . "
		+ " ?subOrganization rdfs:label ?subOrganizationLabel ;" 
			+ " core:organizationForPosition ?Position . "
		+ " ?Position core:positionForPerson ?Person ."
		+ " ?Person  core:hasPrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel . "
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME  
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization . "
		+ " ?subOrganization rdfs:label ?subOrganizationLabel ;" 
			+ " core:organizationForPosition ?Position . "
		+ " ?Position  core:positionForPerson ?Person ."
		+ " ?Person  core:hasInvestigatorRole ?Role ;   rdfs:label ?PersonLabel . "
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME  
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + ">  core:organizationForPosition ?Position . "
		+ " ?Position  core:positionForPerson ?Person ."
		+ " ?Person  core:hasCo-PrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel . "
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME  
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + ">  core:organizationForPosition ?Position . "
		+ " ?Position core:positionForPerson ?Person ."
		+ " ?Person  core:hasPrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel . "
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME  
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + ">  core:organizationForPosition ?Position . "
		+ " ?Position core:positionForPerson ?Person ."
		+ " ?Person  core:hasInvestigatorRole ?Role ;   rdfs:label ?PersonLabel . "
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_ROLE_DATE_TIME  
		+ SPARQL_QUERY_COMMON_OPTIONAL_BLOCK_FOR_GRANT_DATE_TIME + "}"		
		+ " } ";
		
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
				log.error("Entity Grant Count Query " + errorMsg);
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