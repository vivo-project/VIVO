/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph;

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

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json.SubjectEntityJSON;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.SelectOnModelUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;


public class TemporalGrantVisualizationRequestHandler implements
		VisualizationRequestHandler {
	
	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		
		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		return generateStandardVisualizationForGrantTemporalVis(vitroRequest,
				log, dataset, entityURI);
	}


	private ResponseValues generateStandardVisualizationForGrantTemporalVis(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String entityURI) throws MalformedQueryParametersException {
		if (StringUtils.isBlank(entityURI)) {
			
			entityURI = OrganizationUtilityFunctions
								.getStaffProvidedOrComputedHighestLevelOrganization(
										log,
										dataset, 
										vitroRequest);
			
		}
		
		return prepareStandaloneMarkupResponse(vitroRequest, entityURI);
	}
	
	
	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {

		
		return generateStandardVisualizationForGrantTemporalVis(
				vitroRequest, log, dataset, parameters.get(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY));
		
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		VisConstants.DataVisMode currentDataMode = VisConstants.DataVisMode.CSV;
		
		/*
		 * This will provide the data in json format mainly used for standalone temporal vis. 
		 * */
		if (VisualizationFrameworkConstants.JSON_OUTPUT_FORMAT
					.equalsIgnoreCase(vitroRequest
							.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY))) {
			
			currentDataMode = VisConstants.DataVisMode.JSON;
			
			if (StringUtils.isBlank(entityURI)) {
				
				entityURI = OrganizationUtilityFunctions
								.getStaffProvidedOrComputedHighestLevelOrganization(
										log,
										dataset,
										vitroRequest);								
								
			} 
			
		} 		
		
		return getSubjectEntityAndGenerateDataResponse(
				vitroRequest, 
				log,
				dataset,
				entityURI,
				currentDataMode);
		
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
			String subjectEntityURI, VisConstants.DataVisMode visMode)
			throws MalformedQueryParametersException {
		
		Entity organizationEntity = SelectOnModelUtilities
		.getSubjectOrganizationHierarchy(dataset, subjectEntityURI);

		if (organizationEntity.getSubEntities() ==  null) {
			
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		}	
		
		Map<String, Activity> grantURIForAssociatedPeopleToVO = new HashMap<String, Activity>();
		Map<String, Activity> allGrantURIToVO = new HashMap<String, Activity>();
		
		allGrantURIToVO = SelectOnModelUtilities.getGrantsForAllSubOrganizations(dataset, organizationEntity);
		
		Entity organizationWithAssociatedPeople = SelectOnModelUtilities
					.getSubjectOrganizationAssociatedPeople(dataset, subjectEntityURI);
		
		if (organizationWithAssociatedPeople.getSubEntities() !=  null) {
			
			grantURIForAssociatedPeopleToVO = SelectOnModelUtilities
						.getGrantsForAssociatedPeople(dataset, organizationWithAssociatedPeople.getSubEntities());
			
			organizationEntity = OrganizationUtilityFunctions.mergeEntityIfShareSameURI(
										organizationEntity,
										organizationWithAssociatedPeople);
		}
		
		if (allGrantURIToVO.isEmpty() && grantURIForAssociatedPeopleToVO.isEmpty()) {
			
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
			
		} else {	
			
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
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
							 				 entity));
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

		String standaloneTemplate = "entityComparisonOnGrantsStandalone.ftl";
		
		String organizationLabel = OrganizationUtilityFunctions.getEntityLabelFromDAO(vreq,
											  entityURI);
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("title", organizationLabel + " - Temporal Graph Visualization");
		body.put("organizationURI", entityURI);
		body.put("organizationLocalName", UtilityFunctions.getIndividualLocalName(entityURI, vreq));
		body.put("vivoDefaultNamespace", vreq.getWebappDaoFactory().getDefaultNamespace());
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
										   Entity subjectEntity) {

		Gson json = new Gson();
		Set jsonifiedResponse = new HashSet();

		for (SubEntity subentity : subjectEntity.getSubEntities()) {
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
			
			jsonifiedResponse.add(entityJson);
		}
		
		
		SubjectEntityJSON subjectEntityJSON = new SubjectEntityJSON(subjectEntity.getEntityLabel(),
																	subjectEntity.getEntityURI(),
																	subjectEntity.getParents());
		
		jsonifiedResponse.add(subjectEntityJSON);
		
		return json.toJson(jsonifiedResponse);
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


	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		return null;
	}	
}
