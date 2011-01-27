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
 * Deepak Konidena.
 * @author bkoniden
 */
public class EntityPublicationCountQueryRunner implements QueryRunner<Entity> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String entityURI;
	private DataSource dataSource;
	private Log log;

	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = ""
			+ "		(str(?Person) as ?personLit) "
			+ "		(str(?PersonLabel) as ?personLabelLit) "
			+ "		(str(?SecondaryPositionLabel) as ?SecondaryPositionLabelLit)"
			+ "		(str(?Document) as ?documentLit) "
			+ "		(str(?DocumentLabel) as ?documentLabelLit) "
			+ "		(str(?publicationDate) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ") "
			+ "		(str(?publicationYearUsing_1_1_property) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_USING_1_1_PROPERTY + ") "
			+ "		(str(?StartYear) as ?StartYearLit)";


	private static final String SPARQL_QUERY_COMMON_WHERE_CLAUSE = ""
			+ "?Document rdf:type bibo:Document ;"
			+ " rdfs:label ?DocumentLabel ."
			+ "OPTIONAL {  ?Document core:dateTimeValue ?dateTimeValue . " 
			+ "				?dateTimeValue core:dateTime ?publicationDate } ." 
			+ "OPTIONAL {  ?Document core:year ?publicationYearUsing_1_1_property } ." 
			+ "OPTIONAL {  ?SecondaryPosition core:startYear ?StartYear } .";

	private static String ENTITY_LABEL;
	private static String ENTITY_URL;
	private static String SUBENTITY_LABEL;
	private static String SUBENTITY_URL;

	public EntityPublicationCountQueryRunner(String entityURI,
			DataSource dataSource, Log log) {

		this.entityURI = entityURI;
		this.dataSource = dataSource;
		this.log = log;

	}

	private Entity createJavaValueObjects(ResultSet resultSet) {

		Entity entity = null;
		Map<String, BiboDocument> biboDocumentURLToVO = new HashMap<String, BiboDocument>();
		Map<String, SubEntity> subentityURLToVO = new HashMap<String, SubEntity>();
		Map<String, SubEntity> personURLToVO = new HashMap<String, SubEntity>();


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

				RDFNode publicationDateNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
				if (publicationDateNode != null) {
					biboDocument.setPublicationDate(publicationDateNode.toString());
				}

				/*
				 * This is being used so that date in the data from pre-1.2 ontology can be captured. 
				 * */
				RDFNode publicationYearUsing_1_1_PropertyNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_USING_1_1_PROPERTY);
				if (publicationYearUsing_1_1_PropertyNode != null) {
					biboDocument.setPublicationYear(publicationYearUsing_1_1_PropertyNode.toString());
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
				person.addPublications(biboDocument);				

			}			

			entity.addPublications(biboDocument);
		}
		
		if(subentityURLToVO.size() == 0 && personURLToVO.size() != 0){
			for(SubEntity person : personURLToVO.values()){
				entity.addSubEntity(person);
			}
		} else if (subentityURLToVO.size() == 0 && personURLToVO.size() == 0){
			entity = new Entity(this.entityURI, "no-label");
		}
		
		//TODO: return non-null value
		return entity;
	}
		
	private ResultSet executeQuery(String queryURI, DataSource dataSource) {

		QueryExecution queryExecution = null;
		Query query = QueryFactory.create(
				getSparqlQuery(queryURI), SYNTAX);
		queryExecution = QueryExecutionFactory.create(query, dataSource);
		return queryExecution.execSelect();
	}

	private String getSparqlQuery(String queryURI) {
		
		String result = "";
			
		ENTITY_URL = QueryFieldLabels.ORGANIZATION_URL;
		ENTITY_LABEL = QueryFieldLabels.ORGANIZATION_LABEL;
		SUBENTITY_URL = QueryFieldLabels.SUBORGANIZATION_URL;
		SUBENTITY_LABEL = QueryFieldLabels.SUBORGANIZATION_LABEL;

		result = getSparqlQueryForOrganization(queryURI);

		return result;
	}

	
	private String getSparqlQueryForOrganization(String queryURI){
		
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
		+ "SELECT 	(str(?organizationLabel) as ?organizationLabelLit) "
		+ "	 		(str(?subOrganization) as ?subOrganizationLit) "
		+ "			(str(?subOrganizationLabel) as ?subOrganizationLabelLit) "
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
		+ "<" + queryURI + "> core:organizationForPosition ?Position ."
		+ " ?Position rdf:type core:Position ; core:positionForPerson ?Person ."
		+ "	?Person  core:authorInAuthorship ?Resource ;   rdfs:label ?PersonLabel ; core:personInPosition ?SecondaryPosition . "
		+ " ?Resource core:linkedInformationResource ?Document ."
		+ " ?SecondaryPosition rdfs:label ?SecondaryPositionLabel ."
		+ SPARQL_QUERY_COMMON_WHERE_CLAUSE + "}"
		+ "}";
		
		//System.out.println("\n\nEntity Pub Count query is: "+ sparqlQuery);
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

		ResultSet resultSet = executeQuery(this.entityURI, this.dataSource);

		return createJavaValueObjects(resultSet);
	}

}



