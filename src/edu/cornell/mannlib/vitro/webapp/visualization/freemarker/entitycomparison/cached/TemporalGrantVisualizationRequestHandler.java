/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitycomparison.cached;

import java.util.ArrayList;
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
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitycomparison.EntityComparisonUtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.SelectOnModelUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;


public class TemporalGrantVisualizationRequestHandler implements
		VisualizationRequestHandler {
	
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

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		/*
		 * This will provide the data in json format mainly used for standalone temporal vis. 
		 * */
		if (VisualizationFrameworkConstants.TEMPORAL_GRAPH_JSON_DATA_VIS_MODE
					.equalsIgnoreCase(vitroRequest
							.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY))) {
			
			if (StringUtils.isNotBlank(entityURI)) {
				
				return getSubjectEntityAndGenerateDataResponse(
								vitroRequest, 
								log,
								dataset, 
								entityURI,
								EntityComparisonConstants.DataVisMode.JSON);
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
								EntityComparisonConstants.DataVisMode.JSON);
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
					EntityComparisonConstants.DataVisMode.CSV);
		}
	}
	
	private Map<String, String> prepareDataErrorResponse() {
		
		String outputFileName = "no-organization_grants-per-year.csv";
		
		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, "");
		return fileData;
	}
	
	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		
		throw new UnsupportedOperationException("Entity Grant Count " 
				+ "does not provide Ajax response.");
	}
	
	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI, EntityComparisonConstants.DataVisMode visMode)
			throws MalformedQueryParametersException {
		
		Entity organizationEntity = SelectOnModelUtilities
		.getSubjectOrganizationHierarchy(dataset, subjectEntityURI);

		if (organizationEntity.getSubEntities() ==  null) {
			
			if (EntityComparisonConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		}	
		
		Map<String, Activity> grantURIForAssociatedPeopleToVO = new HashMap<String, Activity>();
		Map<String, Activity> allGrantURIToVO = new HashMap<String, Activity>();
		
/**
 * TODO: Change this to use DataSet when an optimum solution is reached. Currently grant constructs are causing
 * endless wait times on a large dataset like UFl. When I tried to add all the datasets manually to the Datasource
 * it responded in an order of magnitude higher than with just the defaultOntModel.
 * Brian Lowe is looking into this weird behavior see http://issues.library.cornell.edu/browse/NIHVIVO-2275 
 */ 
//		DataSource dataSource = DatasetFactory.create();
//		dataSource.setDefaultModel(vitroRequest.getJenaOntModel());
		
		allGrantURIToVO = SelectOnModelUtilities.getGrantsForAllSubOrganizations(dataset, organizationEntity);
		
		Entity organizationWithAssociatedPeople = SelectOnModelUtilities
					.getSubjectOrganizationAssociatedPeople(dataset, subjectEntityURI);
		
		if (organizationWithAssociatedPeople.getSubEntities() !=  null) {
			
			grantURIForAssociatedPeopleToVO = SelectOnModelUtilities
						.getGrantsForAssociatedPeople(dataset, organizationWithAssociatedPeople.getSubEntities());
			
			organizationEntity = EntityComparisonUtilityFunctions.mergeEntityIfShareSameURI(
										organizationEntity,
										organizationWithAssociatedPeople);
		}
		
		if (allGrantURIToVO.isEmpty() && grantURIForAssociatedPeopleToVO.isEmpty()) {
			
			if (EntityComparisonConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
			
		} else {	
			
			if (EntityComparisonConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataResponse(vitroRequest, organizationEntity);
			} else {
				return prepareDataResponse(organizationEntity);
			}
		}
	}

	private Map<String, String> prepareStandaloneDataErrorResponse() {

		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 "{\"error\" : \"No Grants for this Organization found in VIVO.\"}");
		return fileData;
	}
	
	
	private Map<String, String> prepareStandaloneDataResponse(
										VitroRequest vitroRequest, 
										Entity entity) {

		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 writeGrantsOverTimeJSON(vitroRequest, 
							 				 entity.getSubEntities()));
		return fileData;
	}

	/**
	 * Provides response when json file containing the grant count over the
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
				+ "_grants-per-year" + ".csv";
		
		
		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
				getEntityGrantsPerYearCSVContent(entity));
		return fileData;
	}
	

	private TemplateResponseValues prepareStandaloneMarkupResponse(VitroRequest vreq,
			   String entityURI) {

		Portal portal = vreq.getPortal();
		String standaloneTemplate = "entityComparisonOnGrantsStandalone.ftl";
		
		String organizationLabel = EntityComparisonUtilityFunctions.getEntityLabelFromDAO(vreq,
											  entityURI);
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("portalBean", portal);
		body.put("title", organizationLabel + " - Temporal Graph Visualization");
		body.put("organizationURI", entityURI);
		body.put("organizationLabel", organizationLabel);
		
		return new TemplateResponseValues(standaloneTemplate, body);
	}
	
	/**
	 * Function to generate a json file for year <-> grant count mapping.
	 * @param vreq 
	 * @param subentities
	 * @param subOrganizationTypesResult  
	 */
	private String writeGrantsOverTimeJSON(VitroRequest vreq, 
										   Set<SubEntity> subentities) {

		Gson json = new Gson();
		Set<JsonObject> subEntitiesJson = new HashSet<JsonObject>();

		for (SubEntity subentity : subentities) {
			JsonObject entityJson = new JsonObject(
					subentity.getIndividualLabel());

			List<List<Integer>> yearGrantCount = new ArrayList<List<Integer>>();

			for (Map.Entry<String, Integer> grantEntry : UtilityFunctions
					.getYearToActivityCount(subentity.getActivities())
					.entrySet()) {

				List<Integer> currentGrantYear = new ArrayList<Integer>();
				if (grantEntry.getKey().equals(
						VOConstants.DEFAULT_GRANT_YEAR)) {
					currentGrantYear.add(-1);
				} else {
					currentGrantYear.add(Integer.parseInt(grantEntry.getKey()));
				}
					
				currentGrantYear.add(grantEntry.getValue());
				yearGrantCount.add(currentGrantYear);
			}

			entityJson.setYearToActivityCount(yearGrantCount);
			
			entityJson.setOrganizationTypes(subentity.getEntityTypeLabels());

			entityJson.setEntityURI(subentity.getIndividualURI());
			
			entityJson.setLastCachedAtDateTime(subentity.getLastCachedAtDateTime());
			
			if (subentity.getEntityClass().equals(VOConstants.EntityClassType.PERSON)) {
				entityJson.setVisMode("PERSON");
			} else if (subentity.getEntityClass().equals(VOConstants.EntityClassType.ORGANIZATION)) {
				entityJson.setVisMode("ORGANIZATION");
			}		
			
			subEntitiesJson.add(entityJson);
		}
		
		return json.toJson(subEntitiesJson);
	}

	private String getEntityGrantsPerYearCSVContent(Entity entity) {

		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Entity Name, Grant Count, Entity Type\n");
		
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
