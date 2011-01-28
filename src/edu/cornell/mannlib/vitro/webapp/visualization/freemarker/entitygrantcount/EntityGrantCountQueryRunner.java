/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitygrantcount;

import java.util.HashMap;
import java.util.Map;

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
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Grant;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;

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
	private DataSource dataSource;
	private Log log;

	
	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = "SELECT "
		+ "		(str(?organizationLabel) as ?organizationLabelLit) "
		+ "		(str(?subOrganization) as ?subOrganizationLit) "
		+ "		(str(?subOrganizationLabel) as ?subOrganizationLabelLit) "
		+ "		(str(?Person) as ?personLit) "
		+ "		(str(?PersonLabel) as ?personLabelLit) "
		+ "		(str(?SecondaryPositionLabel) as ?SecondaryPositionLabelLit)"
		+ "		(str(?Grant) as ?grantLit) "
		+ "		(str(?GrantLabel) as ?grantLabelLit) "
		+ " 	(str(?startDateTimeValue) as ?grantStartDateLit) "
		+ "		(str(?endDateTimeValue) as ?grantEndDateLit)  ";
	
	private static final String SPARQL_QUERY_COMMON_WHERE_CLAUSE =  " "
		+ "		?SecondaryPosition rdfs:label ?SecondaryPositionLabel . "
		+ "		?Role core:roleIn ?Grant . "
		+ "		?Grant rdfs:label ?GrantLabel . "
		+ 		"OPTIONAL {"	
		+			"?Role core:dateTimeInterval ?dateTimeIntervalValue . "		
		+			"?dateTimeIntervalValue core:start ?startDate . "		
		+			"?startDate core:dateTime ?startDateTimeValue . " 	
		+			"OPTIONAL {"	
		+				"?dateTimeIntervalValue core:end ?endDate . "	
		+				"?endDate core:dateTime ?endDateTimeValue . " 			
		+			"}"
		+ 		"}"	;		
	
	
	private static String ENTITY_LABEL = QueryFieldLabels.ORGANIZATION_LABEL;
	private static String ENTITY_URL = QueryFieldLabels.ORGANIZATION_URL;
	private static String SUBENTITY_LABEL = QueryFieldLabels.SUBORGANIZATION_LABEL ;
	private static String SUBENTITY_URL = QueryFieldLabels.SUBORGANIZATION_URL;

	
	public EntityGrantCountQueryRunner(String entityURI,
			DataSource dataSource, Log log) {

		this.entityURI = entityURI;
		this.dataSource = dataSource;
		this.log = log;

	}	
	
	private Entity createJavaValueObjects(ResultSet resultSet) {

		Entity entity = null;
		Map<String, Grant> grantURIToVO = new HashMap<String, Grant>();
		Map<String, SubEntity> subentityURLToVO = new HashMap<String, SubEntity>();
		Map<String, SubEntity> personURLToVO = new HashMap<String, SubEntity>();

		while (resultSet.hasNext()) {

			QuerySolution solution = resultSet.nextSolution();

			if (entity == null) {
				entity = new Entity(solution.get(ENTITY_URL).toString(),
						solution.get(ENTITY_LABEL).toString());
			}

			RDFNode grantNode = solution.get(QueryFieldLabels.GRANT_URL);
			Grant grant;

			if (grantURIToVO.containsKey(grantNode.toString())) {
				grant = grantURIToVO.get(grantNode.toString());

			} else {

				grant = new Grant(grantNode.toString());
				grantURIToVO.put(grantNode.toString(), grant);

				RDFNode grantLabelNode = solution
						.get(QueryFieldLabels.GRANT_LABEL);
				if (grantLabelNode != null) {
					grant.setGrantLabel(grantLabelNode.toString());
				}

				RDFNode grantStartDateNode = solution.get(QueryFieldLabels.GRANT_START_DATE);
				if(grantStartDateNode != null){
					grant.setGrantStartDate(grantStartDateNode.toString());
				}
				
				RDFNode grantEndDateNode = solution.get(QueryFieldLabels.GRANT_END_DATE);
				if(grantEndDateNode != null){
					grant.setGrantEndDate(grantEndDateNode.toString());
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
				subEntity.addGrants(grant);
			}
			
			
			RDFNode personURLNode = solution.get(QueryFieldLabels.PERSON_URL);
			
			if(personURLNode != null){
				SubEntity person ;
				if(personURLToVO.containsKey(personURLNode.toString())) {
					person = personURLToVO.get(personURLNode.toString());
				} else {
					person = new SubEntity(personURLNode.toString());
					personURLToVO.put(personURLNode.toString(), person);
				}
				
				RDFNode personLabelNode = solution.get(QueryFieldLabels.PERSON_LABEL);
				if (personLabelNode != null) {
					person.setIndividualLabel(personLabelNode.toString());
				}
				
//				entity.addSubEntity(person);
				person.addGrants(grant);				

			}
			
			entity.addGrants(grant);
		}
		
		if(subentityURLToVO.size() == 0 && personURLToVO.size() != 0){
			for(SubEntity person : personURLToVO.values()){
				entity.addSubEntity(person);
			}
		} else if (subentityURLToVO.size() == 0 && personURLToVO.size() == 0){
			entity = new Entity(this.entityURI, "no-label");
		}

		return entity;
	}

	private ResultSet executeQuery(String queryURI, DataSource dataSource) {

		QueryExecution queryExecution = null;
		Query query = QueryFactory.create(
				getSparqlQuery(queryURI), SYNTAX);
		queryExecution = QueryExecutionFactory.create(query, dataSource);
		return queryExecution.execSelect();
	}	
	
	private String getSparqlQuery(String queryURI){
		
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
		+ SPARQL_QUERY_COMMON_SELECT_CLAUSE + "		(str(<" + queryURI
		+ ">) as ?" + ENTITY_URL + ") "
		+ "WHERE { " + "<" + queryURI + "> rdf:type foaf:Organization ;"
		+ " rdfs:label ?organizationLabel ."
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization ."
		+ " ?subOrganization rdfs:label ?subOrganizationLabel ; core:organizationForPosition ?Position . "
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ " ?Person  core:hasCo-PrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization . "
		+ " ?subOrganization rdfs:label ?subOrganizationLabel ; core:organizationForPosition ?Position . "
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ " ?Person  core:hasPrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization . "
		+ " ?subOrganization rdfs:label ?subOrganizationLabel ; core:organizationForPosition ?Position . "
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ " ?Person  core:hasInvestigatorRole ?Role ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + ">  core:organizationForPosition ?Position . "
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ " ?Person  core:hasCo-PrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + ">  core:organizationForPosition ?Position . "
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ " ?Person  core:hasPrincipalInvestigatorRole ?Role ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + ">  core:organizationForPosition ?Position . "
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ " ?Person  core:hasInvestigatorRole ?Role ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"		
		+ " } ";
		
		//System.out.println("\n\nEntity Grant Count query is: "+ sparqlQuery);
		
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
				log.error("Entity Grant Count Query " + errorMsg);
				throw new MalformedQueryParametersException(
						"URI provided for an entity is malformed.");
			}

		} else {
			throw new MalformedQueryParametersException(
					"URL parameter is either null or empty.");
		}

		ResultSet resultSet = executeQuery(this.entityURI, this.dataSource);

		return createJavaValueObjects(resultSet);
	}	
}