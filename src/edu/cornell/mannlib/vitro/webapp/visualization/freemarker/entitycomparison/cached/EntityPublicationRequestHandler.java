/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitycomparison.cached;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitycomparison.EntityComparisonUtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.ModelConstructorUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.OrganizationAssociatedPeopleModelWithTypesConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.OrganizationModelWithTypesConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.OrganizationToPublicationsForSubOrganizationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.PersonToPublicationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.GenericQueryRunnerOnModel;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;

public class EntityPublicationRequestHandler implements
		VisualizationRequestHandler {
	
	public enum DataVisMode {
		CSV, JSON
	};
	
	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		if (StringUtils.isBlank(entityURI)) {
			
			entityURI = EntityComparisonUtilityFunctions
								.getStaffProvidedOrComputedHighestLevelOrganization(
											log, 
											dataset, 
											vitroRequest);
			
		}
		
		
		System.out.println("current models in the system are");
		for (Map.Entry<String, Model> entry : ConstructedModelTracker.getAllModels().entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue().size());
		}
		

		
		
		return prepareStandaloneMarkupResponse(vitroRequest, entityURI);
	}

	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI, DataVisMode visMode)
			throws MalformedQueryParametersException {
		

		Entity organizationEntity = getSubjectOrganizationHierarchy(dataset,
				subjectEntityURI);
		
		if (organizationEntity.getSubEntities() ==  null) {
			
			if (DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
			
		}
		
		Map<String, Activity> documentURIForAssociatedPeopleTOVO = new HashMap<String, Activity>();
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		allDocumentURIToVOs = getPublicationsForAllSubOrganizations(dataset, organizationEntity);
		
		Collection<SubEntity> associatedPeople = getSubjectOrganizationAssociatedPeople(dataset, subjectEntityURI);
		
		if (!associatedPeople.isEmpty()) {
			
			documentURIForAssociatedPeopleTOVO = getPublicationsForAssociatedPeople(dataset, associatedPeople);
			organizationEntity.addSubEntitities(associatedPeople);
		}
		
		if (allDocumentURIToVOs.isEmpty() && documentURIForAssociatedPeopleTOVO.isEmpty()) {
			
			if (DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
			
		} else {	
			
			if (DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataResponse(vitroRequest, organizationEntity);
			} else {
				return prepareDataResponse(organizationEntity);
			}
		}
	}
	
	/**
	 * Provides response when json file containing the publication count over the
	 * years is requested.
	 * 
	 * @param entity
	 * @param subentities
	 * @param subOrganizationTypesResult
	 */
	private Map<String, String> prepareDataResponse(Entity entity) {

		String entityLabel = entity.getEntityLabel();

		/*
		* To make sure that null/empty records for entity names do not cause any mischief.
		* */
		if (StringUtils.isBlank(entityLabel)) {
			entityLabel = "no-organization";
		}
		
		String outputFileName = UtilityFunctions.slugify(entityLabel)
				+ "_publications-per-year" + ".csv";
		
		
		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
				getEntityPublicationsPerYearCSVContent(entity));
		return fileData;
	}
	
	private Map<String, String> prepareDataErrorResponse() {
		
		String outputFileName = "no-organization_publications-per-year.csv";
		
		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, "");
		return fileData;
	}


	private Map<String, Activity> getPublicationsForAllSubOrganizations(
			Dataset dataset, Entity organizationEntity)
			throws MalformedQueryParametersException {
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		for (SubEntity subOrganization : organizationEntity.getSubEntities()) {
			
			Model subOrganizationPublicationsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	subOrganization.getIndividualURI(),
																	OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE,
																	dataset);
			
			System.out.println("getting publications for " + subOrganization.getIndividualLabel());
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("document", QueryFieldLabels.DOCUMENT_URL);
			fieldLabelToOutputFieldLabel.put("documentLabel", QueryFieldLabels.DOCUMENT_LABEL);
			fieldLabelToOutputFieldLabel.put("documentPublicationDate", QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			
			String whereClause = ""
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:hasPersonWithPublication ?document . "
				+ " ?document rdfs:label ?documentLabel . "
				+ " OPTIONAL { "
				+ " 	?document core:dateTimeValue ?dateTimeValue . "
				+ "     ?dateTimeValue core:dateTime ?documentPublicationDate } . ";
			
			QueryRunner<ResultSet> subOrganizationPublicationsQuery = 
				new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
										"",
										whereClause,
										"",
										subOrganizationPublicationsModel);
			
			subOrganization.addActivities(getPublicationForEntity(
												subOrganizationPublicationsQuery.getQueryResult(),
												allDocumentURIToVOs));
			
		}
		return allDocumentURIToVOs;
	}


	private Map<String, Activity> getPublicationsForAssociatedPeople(
			Dataset dataset, Collection<SubEntity> people)
			throws MalformedQueryParametersException {
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		for (SubEntity person : people) {
			
			Model personPublicationsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	person.getIndividualURI(),
																	PersonToPublicationsModelConstructor.MODEL_TYPE,
																	dataset);
			
			System.out.println("getting publications for " + person.getIndividualLabel());
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("document", QueryFieldLabels.DOCUMENT_URL);
			fieldLabelToOutputFieldLabel.put("documentLabel", QueryFieldLabels.DOCUMENT_LABEL);
			fieldLabelToOutputFieldLabel.put("documentPublicationDate", QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			
			String whereClause = ""
				+ " <" + person.getIndividualURI() + "> vivosocnet:hasPublication ?document . "
				+ " ?document rdfs:label ?documentLabel . "
				+ " OPTIONAL { "
				+ " 	?document core:dateTimeValue ?dateTimeValue . "
				+ "     ?dateTimeValue core:dateTime ?documentPublicationDate } . ";
			
			QueryRunner<ResultSet> personPublicationsQuery = 
				new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
										"",
										whereClause,
										"",
										personPublicationsModel);
			
			person.addActivities(getPublicationForEntity(
												personPublicationsQuery.getQueryResult(),
												allDocumentURIToVOs));
			
		}
		return allDocumentURIToVOs;
	}
	
	private Entity getSubjectOrganizationHierarchy(Dataset dataset,
			String subjectEntityURI) throws MalformedQueryParametersException {
		Model organizationModel = ModelConstructorUtilities
										.getOrConstructModel(
												null, 
												OrganizationModelWithTypesConstructor.MODEL_TYPE, 
												dataset);
		
		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("organizationLabel", QueryFieldLabels.ORGANIZATION_LABEL);
		fieldLabelToOutputFieldLabel.put("subOrganization", QueryFieldLabels.SUBORGANIZATION_URL);
		fieldLabelToOutputFieldLabel.put("subOrganizationLabel", QueryFieldLabels.SUBORGANIZATION_LABEL);
		fieldLabelToOutputFieldLabel.put("subOrganizationTypeLabel", QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL);
		
		String whereClause = ""
			+ " <" + subjectEntityURI + "> rdfs:label ?organizationLabel . "
			+ " <" + subjectEntityURI + "> core:hasSubOrganization ?subOrganization . "
			+ " ?subOrganization rdfs:label ?subOrganizationLabel . "
			+ " ?subOrganization rdf:type ?subOrgType . "
			+ " ?subOrgType rdfs:label ?subOrganizationTypeLabel . ";
		
		QueryRunner<ResultSet> subOrganizationsWithTypesQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									organizationModel);
		
		Entity organizationEntity = generateEntity(subjectEntityURI, 
												   subOrganizationsWithTypesQuery.getQueryResult());
		return organizationEntity;
	}

	private Collection<SubEntity> getSubjectOrganizationAssociatedPeople(Dataset dataset,
			String subjectEntityURI) throws MalformedQueryParametersException {
		Model associatedPeopleModel = ModelConstructorUtilities
										.getOrConstructModel(
												subjectEntityURI, 
												OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE, 
												dataset);
		
		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("organizationLabel", QueryFieldLabels.ORGANIZATION_LABEL);
		fieldLabelToOutputFieldLabel.put("person", QueryFieldLabels.PERSON_URL);
		fieldLabelToOutputFieldLabel.put("personLabel", QueryFieldLabels.PERSON_LABEL);
		fieldLabelToOutputFieldLabel.put("personTypeLabel", QueryFieldLabels.PERSON_TYPE_LABEL);
		
		String whereClause = ""
			+ " <" + subjectEntityURI + "> rdfs:label ?organizationLabel . "
			+ " <" + subjectEntityURI + "> vivosocnet:hasPersonWithActivity ?person . "
			+ " ?person rdfs:label ?personLabel . "
			+ " ?person rdf:type ?personType . "
			+ " ?personType rdfs:label ?personTypeLabel . ";
		
		QueryRunner<ResultSet> associatedPeopleWithTypesQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									associatedPeopleModel);
		
		return getAssociatedPeopleSubEntitities(associatedPeopleWithTypesQuery.getQueryResult());
	}
	
	private Collection<SubEntity> getAssociatedPeopleSubEntitities(
			ResultSet queryResult) {

		Map<String, SubEntity> associatedPeopleURIToVO = new HashMap<String, SubEntity>();
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			RDFNode personNode = solution.get(QueryFieldLabels.PERSON_URL);
			
			SubEntity subEntity;
			
			if (associatedPeopleURIToVO.containsKey(personNode.toString())) {
				
				subEntity = associatedPeopleURIToVO.get(personNode.toString());
				
			} else {
				
				subEntity = new SubEntity(personNode.toString());
				associatedPeopleURIToVO.put(personNode.toString(), subEntity);
				
				RDFNode personLabelNode = solution.get(QueryFieldLabels.PERSON_LABEL);
				if (personLabelNode != null) {
					subEntity.setIndividualLabel(personLabelNode.toString());
				}
			}

			RDFNode personTypeLabelNode = solution.get(QueryFieldLabels.PERSON_TYPE_LABEL);
			if (personTypeLabelNode != null) {
				subEntity.addEntityTypeLabel(personTypeLabelNode.toString());
			}
		}
		
		return associatedPeopleURIToVO.values();
	}

	private Collection<Activity> getPublicationForEntity(
			ResultSet queryResult,
			Map<String, Activity> allDocumentURIToVOs) {
		
		Set<Activity> currentEntityPublications = new HashSet<Activity>();

		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			Activity biboDocument;
			
			if (allDocumentURIToVOs.containsKey(documentNode.toString())) {
				biboDocument = allDocumentURIToVOs.get(documentNode.toString());

			} else {

				biboDocument = new Activity(documentNode.toString());
				allDocumentURIToVOs.put(documentNode.toString(), biboDocument);

				RDFNode publicationDateNode = solution.get(QueryFieldLabels
																.DOCUMENT_PUBLICATION_DATE);
				if (publicationDateNode != null) {
					biboDocument.setActivityDate(publicationDateNode.toString());
				}
			}
			
			currentEntityPublications.add(biboDocument);
			
		}
		
		return currentEntityPublications;
	}

	private Entity generateEntity(String subjectEntityURI, ResultSet queryResult) {

		Entity entity = new Entity(subjectEntityURI);
		Map<String, SubEntity> subOrganizationURIToVO = new HashMap<String, SubEntity>();
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(entity.getEntityLabel())) {
				
				RDFNode organizationLabelNode = solution.get(QueryFieldLabels.ORGANIZATION_LABEL);
				if (organizationLabelNode != null) {
					entity.setIndividualLabel(organizationLabelNode.toString());
				}
			}
			
			RDFNode subOrganizationNode = solution.get(QueryFieldLabels.SUBORGANIZATION_URL);
			
			SubEntity subEntity;
			
			if (subOrganizationURIToVO.containsKey(subOrganizationNode.toString())) {
				
				subEntity = subOrganizationURIToVO.get(subOrganizationNode.toString());
				
			} else {
				
				subEntity = new SubEntity(subOrganizationNode.toString());
				subOrganizationURIToVO.put(subOrganizationNode.toString(), subEntity);
				
				RDFNode subOrganizationLabelNode = solution.get(QueryFieldLabels.SUBORGANIZATION_LABEL);
				if (subOrganizationLabelNode != null) {
					subEntity.setIndividualLabel(subOrganizationLabelNode.toString());
				}
			}

			RDFNode subOrganizationTypeLabelNode = solution.get(QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL);
			if (subOrganizationTypeLabelNode != null) {
				subEntity.addEntityTypeLabel(subOrganizationTypeLabelNode.toString());
			}
		}
		
		entity.addSubEntitities(subOrganizationURIToVO.values());
		
		return entity;
	}

	private Map<String, String> prepareStandaloneDataErrorResponse() {

		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 "{\"error\" : \"No Publications for this Organization found in VIVO.\"}");
		return fileData;
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		/*
		 * This will provide the data in json format mainly used for standalone tmeporal vis. 
		 * */
		if (VisualizationFrameworkConstants.TEMPORAL_GRAPH_JSON_DATA_VIS_MODE
					.equalsIgnoreCase(vitroRequest.getParameter(
							VisualizationFrameworkConstants.VIS_MODE_KEY))) {
			
			if (StringUtils.isNotBlank(entityURI)) {
				
				return getSubjectEntityAndGenerateDataResponse(
								vitroRequest, 
								log,
								dataset, 
								entityURI,
								DataVisMode.JSON);
			} else {
				
				return getSubjectEntityAndGenerateDataResponse(
								vitroRequest, 
								log,
								dataset,
								EntityComparisonUtilityFunctions
										.getStaffProvidedOrComputedHighestLevelOrganization(
												log,
												dataset, 
												vitroRequest),
								DataVisMode.JSON);
			}
			
		} else {
			/*
			 * This provides csv download files for the content in the tables.
			 * */
			
				return getSubjectEntityAndGenerateDataResponse(
						vitroRequest, 
						log,
						dataset,
						entityURI,
						DataVisMode.CSV);
			
		}
		
	}
	
	
	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Entity Pub Count does not provide Ajax Response.");
	}

	private Map<String, String> prepareStandaloneDataResponse(
										VitroRequest vitroRequest, 
										Entity entity) {

		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 writePublicationsOverTimeJSON(vitroRequest, 
							 					   entity.getSubEntities()));
		return fileData;
	}
	
	private TemplateResponseValues prepareStandaloneMarkupResponse(VitroRequest vreq,
																   String entityURI) {

        Portal portal = vreq.getPortal();
        String standaloneTemplate = "entityComparisonOnPublicationsStandalone.ftl";
		
        String organizationLabel = EntityComparisonUtilityFunctions
        									.getEntityLabelFromDAO(vreq,
        														   entityURI);
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("portalBean", portal);
        body.put("title", organizationLabel + " - Temporal Graph Visualization");
        body.put("organizationURI", entityURI);
        body.put("organizationLabel", organizationLabel);
        
        return new TemplateResponseValues(standaloneTemplate, body);
	}

	/**
	 * Function to generate a json file for year <-> publication count mapping.
	 * @param vreq 
	 * @param subentities
	 * @param subOrganizationTypesResult  
	 */
	private String writePublicationsOverTimeJSON(VitroRequest vreq, 
												 Set<SubEntity> subentities) {

		Gson json = new Gson();
		Set<JsonObject> subEntitiesJson = new HashSet<JsonObject>();

		for (SubEntity subentity : subentities) {
			
			System.out.println("in write json current sub entity " + subentity.getIndividualLabel());
			
			JsonObject entityJson = new JsonObject(
					subentity.getIndividualLabel());

			List<List<Integer>> yearPubCount = new ArrayList<List<Integer>>();

			for (Map.Entry<String, Integer> pubEntry : UtilityFunctions
					.getYearToActivityCount(subentity.getActivities())
					.entrySet()) {

				List<Integer> currentPubYear = new ArrayList<Integer>();
				if (pubEntry.getKey().equals(VOConstants.DEFAULT_PUBLICATION_YEAR)) {
					currentPubYear.add(-1);
				} else {
					currentPubYear.add(Integer.parseInt(pubEntry.getKey()));
				}
					
				currentPubYear.add(pubEntry.getValue());
				yearPubCount.add(currentPubYear);
			}
			
			entityJson.setYearToActivityCount(yearPubCount);
			
			entityJson.setOrganizationTypes(subentity.getEntityTypeLabels());
			
			
			entityJson.setEntityURI(subentity.getIndividualURI());
			
			boolean isPerson = UtilityFunctions.isEntityAPerson(vreq, subentity);
			
			if (isPerson) {
				entityJson.setVisMode("PERSON");
			} else {
				entityJson.setVisMode("ORGANIZATION");
			}
			subEntitiesJson.add(entityJson);
		}
		return json.toJson(subEntitiesJson);
	}

	private String getEntityPublicationsPerYearCSVContent(Entity entity) {

		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Entity Name, Publication Count, Entity Type\n");
		
		for (SubEntity subEntity : entity.getSubEntities()) {
			
			csvFileContent.append(StringEscapeUtils.escapeCsv(subEntity.getIndividualLabel()));
			csvFileContent.append(", ");
			csvFileContent.append(subEntity.getActivities().size());
			csvFileContent.append(", ");
			
			String allTypes = StringUtils.join(subEntity.getEntityTypeLabels(), "; ");
			
			csvFileContent.append(StringEscapeUtils.escapeCsv(allTypes));
			csvFileContent.append("\n");
		}
		return csvFileContent.toString();
	}
}	