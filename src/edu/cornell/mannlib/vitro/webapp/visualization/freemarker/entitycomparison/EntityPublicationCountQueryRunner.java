/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitycomparison;

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
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;


/**
 * This query runner is used to execute a sparql query that will fetch all the
 * publications defined by bibo:Document property for a particular
 * department/school/university.
 * 
 * @author bkoniden
 */
public class EntityPublicationCountQueryRunner implements QueryRunner<Entity> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String entityURI;
	private DataSource dataSource;
	private Log log;
	private String visMode;

	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = ""
			+ "		(str(?Person) as ?personLit) "
			+ "		(str(?PersonLabel) as ?personLabelLit) "
			+ "		(str(?SecondaryPositionLabel) as ?SecondaryPositionLabelLit)"
			+ "		(str(?Document) as ?documentLit) "
			+ "		(str(?DocumentLabel) as ?documentLabelLit) "
			+ "		(str(?publicationYear) as ?publicationYearLit) "
			+ "		(str(?publicationYearMonth) as ?publicationYearMonthLit) "
			+ "		(str(?publicationDate) as ?publicationDateLit) "
			+ "		(str(?StartYear) as ?StartYearLit)";


	private static final String SPARQL_QUERY_COMMON_WHERE_CLAUSE = ""
			+ "?Document rdf:type bibo:Document ;"
			+ " rdfs:label ?DocumentLabel ."
			+ "OPTIONAL {  ?Document core:year ?publicationYear } ."
			+ "OPTIONAL {  ?Document core:yearMonth ?publicationYearMonth } ."
			+ "OPTIONAL {  ?Document core:date ?publicationDate } ."
			+ "OPTIONAL {  ?SecondaryPosition core:startYear ?StartYear } .";

	private static String ENTITY_LABEL;
	private static String ENTITY_URL;
	private static String SUBENTITY_LABEL;
	private static String SUBENTITY_URL;

	public EntityPublicationCountQueryRunner(String entityURI,
			DataSource dataSource, Log log, String visMode) {

		this.entityURI = entityURI;
		this.dataSource = dataSource;
		this.log = log;
		this.visMode = visMode;

	}

	private Entity createJavaValueObjects(ResultSet resultSet) {

		Entity entity = null;
		Map<String, BiboDocument> biboDocumentURLToVO = new HashMap<String, BiboDocument>();
		Map<String, SubEntity> subentityURLToVO = new HashMap<String, SubEntity>();

		while (resultSet.hasNext()) {

			QuerySolution solution = resultSet.nextSolution();

			if (entity == null) {
				entity = new Entity(solution.get(ENTITY_URL).toString(),
						solution.get(ENTITY_LABEL).toString());
			}

			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			BiboDocument biboDocument;

			if (biboDocumentURLToVO.containsKey(documentNode.toString())) {
				biboDocument = biboDocumentURLToVO.get(documentNode.toString());

			} else {

				biboDocument = new BiboDocument(documentNode.toString());
				biboDocumentURLToVO.put(documentNode.toString(), biboDocument);

				RDFNode documentLabelNode = solution
						.get(QueryFieldLabels.DOCUMENT_LABEL);
				if (documentLabelNode != null) {
					biboDocument.setDocumentLabel(documentLabelNode.toString());
				}

				RDFNode publicationYearNode = solution
						.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR);
				if (publicationYearNode != null) {
					biboDocument.setPublicationYear(publicationYearNode
							.toString());
				}

				RDFNode publicationYearMonthNode = solution
						.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_MONTH);
				if (publicationYearMonthNode != null) {
					biboDocument
							.setPublicationYearMonth(publicationYearMonthNode
									.toString());
				}

				RDFNode publicationDateNode = solution
						.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
				if (publicationDateNode != null) {
					biboDocument.setPublicationDate(publicationDateNode
							.toString());
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
				subEntity.addPublications(biboDocument);
			}

			entity.addPublications(biboDocument);
		}

		return entity;
	}

	private ResultSet executeQuery(String queryURI, DataSource dataSource) {

		QueryExecution queryExecution = null;
		Query query = QueryFactory.create(
				getSparqlQuery(queryURI, this.visMode), SYNTAX);
		queryExecution = QueryExecutionFactory.create(query, dataSource);
		return queryExecution.execSelect();
}

	private String getSparqlQuery(String queryURI, String visMode) {
		String result = "";

		if (visMode.equals("DEPARTMENT")) {
		//	result = getSparqlQueryForDepartment(queryURI);
			ENTITY_URL = QueryFieldLabels.DEPARTMENT_URL;
			ENTITY_LABEL = QueryFieldLabels.DEPARTMENT_LABEL;
			SUBENTITY_URL = QueryFieldLabels.PERSON_URL;
			SUBENTITY_LABEL = QueryFieldLabels.PERSON_LABEL;
		} else {
		//	result = getSparqlQueryForOrganization(queryURI);
			ENTITY_URL = QueryFieldLabels.ORGANIZATION_URL;
			ENTITY_LABEL = QueryFieldLabels.ORGANIZATION_LABEL;
			SUBENTITY_URL = QueryFieldLabels.SUBORGANIZATION_URL;
			SUBENTITY_LABEL = QueryFieldLabels.SUBORGANIZATION_LABEL;
		} 
		result = getSparqlQueryForOrganization(queryURI);

		return result;
	}

//	private String getSparqlQueryForDepartment(String queryURI) {
//
//		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
//				+ "SELECT (str(?DepartmentLabel) as ?departmentLabelLit) "
//				+ SPARQL_QUERY_COMMON_SELECT_CLAUSE + "		(str(<" + queryURI
//				+ ">) as ?" + QueryFieldLabels.DEPARTMENT_URL + ") "
//				+ "WHERE { " + "<" + queryURI + "> rdf:type core:Department ;"
//				+ " rdfs:label ?DepartmentLabel ;"
//				+ " core:organizationForPosition ?Position .  "
//				+ "	?Position rdf:type core:Position ;"
//				+ " core:positionForPerson ?Person .  "
//				+ "	?Person  core:authorInAuthorship ?Resource ;  "
//				+ " rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition .  "
//				+ " ?Resource core:linkedInformationResource ?Document ."
//				+ "	?SecondaryPosition rdfs:label ?SecondaryPositionLabel ."				
//				+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
//				+ " ORDER BY ?DocumentLabel";
//		System.out.println("\nThe sparql query is :\n" + sparqlQuery);
//		return sparqlQuery;
//
//	}

//	private String getSparqlQueryForOrganization(String queryURI) {
//
//		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
//				+ "SELECT (str(?organizationLabel) as ?organizationLabelLit) "
//				+ "	 	(str(?subOrganization) as ?subOrganizationLit) "
//				+ "		(str(?subOrganizationLabel) as ?subOrganizationLabelLit) "
//				+ SPARQL_QUERY_COMMON_SELECT_CLAUSE + "		(str(<" + queryURI
//				+ ">) as ?" + QueryFieldLabels.ORGANIZATION_URL + ") "
//				+ "WHERE { " + "<" + queryURI + "> rdf:type foaf:Organization ;"
//				+ " rdfs:label ?organizationLabel ;"
//				+ " core:hasSubOrganization ?subOrganization ."
//				+ " ?subOrganization rdfs:label ?subOrganizationLabel ;"
//				+ " core:organizationForPosition ?Position .  "
//				+ "	?Position rdf:type core:Position ;"
//				+ " core:positionForPerson ?Person .  "
//				+ "	?Person  core:authorInAuthorship ?Resource ;  "
//				+ " rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition .  "
//				+ " ?Resource core:linkedInformationResource ?Document ."
//				+ "	?SecondaryPosition rdfs:label ?SecondaryPositionLabel ."
//				+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
//				+ " ORDER BY ?DocumentLabel";
//		System.out.println("\nThe sparql query is :\n" + sparqlQuery);
//		return sparqlQuery;
//
//	}
	
	private String getSparqlQueryForOrganization(String queryURI){
		
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
		+ "SELECT 	(str(?organizationLabel) as ?organizationLabelLit) "
		+ "	 		(str(?subOrganization) as ?subOrganizationLit) "
		+ "			(str(?subOrganizationLabel) as ?subOrganizationLabelLit) "
		+ "			(str(?DepartmentLabel) as ?departmentLabelLit) "
		+ SPARQL_QUERY_COMMON_SELECT_CLAUSE + "		(str(<" + queryURI
		+ ">) as ?" + ENTITY_URL + ") "
		+ "WHERE { " + "<" + queryURI + "> rdf:type foaf:Organization ;"
		+ " rdfs:label ?organizationLabel ."
		+ "{ "
		+ "<" + queryURI + "> core:hasSubOrganization ?subOrganization ."
		+ "?subOrganization rdfs:label ?subOrganizationLabel ; core:organizationForPosition ?Position . "
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ " ?Person  core:authorInAuthorship ?Resource ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ " ?Resource core:linkedInformationResource ?Document .  "
		+ " ?SecondaryPosition rdfs:label ?SecondaryPositionLabel ."
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "UNION "
		+ "{ "
		+ "<" + queryURI + "> rdf:type core:Department ; rdfs:label ?DepartmentLabel ; core:organizationForPosition ?Position ."
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ "	?Person  core:authorInAuthorship ?Resource ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ " ?Resource core:linkedInformationResource ?Document ."
		+ " ?SecondaryPosition rdfs:label ?SecondaryPositionLabel ."
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
				log.error("Entity Comparison vis Query " + errorMsg);
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



