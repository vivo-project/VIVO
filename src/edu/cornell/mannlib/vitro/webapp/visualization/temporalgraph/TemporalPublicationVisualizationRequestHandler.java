/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.CounterUtils;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.OrgUtils;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationCaches;
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
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json.SubjectEntityJSON;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class TemporalPublicationVisualizationRequestHandler implements
		VisualizationRequestHandler {
	
	@Override
	public ResponseValues generateStandardVisualization(VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		
		String entityURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		return generateStandardVisualizationForPublicationTemporalVis(vitroRequest, log, dataset, entityURI);
	}
	
	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {

		return generateStandardVisualizationForPublicationTemporalVis(
				vitroRequest, log, dataset, parameters.get(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY));
	}

	private ResponseValues generateStandardVisualizationForPublicationTemporalVis(
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

	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI, VisConstants.DataVisMode visMode)
			throws MalformedQueryParametersException {

		RDFService rdfService = vitroRequest.getRDFService();

		Map<String, String> orgLabelMap = VisualizationCaches.cachedOrganizationLabels.get(rdfService);
		Map<String, String> personLabelMap = VisualizationCaches.cachedPersonLabels.get(rdfService);

		if (orgLabelMap.get(subjectEntityURI) == null) {
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		}

		Map<String, Set<String>> subOrgMap               = VisualizationCaches.cachedOrganizationSubOrgs.get(rdfService);
		Map<String, String> orgMostSpecificLabelMap      = VisualizationCaches.cachedOrganizationToMostSpecificLabel.get(rdfService);
		Map<String, String> personMostSpecificLabelMap   = VisualizationCaches.cachedPersonToMostSpecificLabel.get(rdfService);
		Map<String, Set<String>> organisationToPeopleMap = VisualizationCaches.cachedOrganisationToPeopleMap.get(rdfService);
		Map<String, Set<String>> personToPublicationMap  = VisualizationCaches.cachedPersonToPublication.get(rdfService);
		Map<String, String>      publicationToYearMap    = VisualizationCaches.cachedPublicationToYear.get(rdfService);

		Set<String> orgPublications       = new HashSet<String>();
		Set<String> orgPublicationsPeople = new HashSet<String>();

		Map<String, Set<String>> subOrgPublicationsMap = new HashMap<String, Set<String>>();

		OrgUtils.getObjectMappingsForOrgAnSubOrgs(
				subjectEntityURI,
				orgPublications,
				orgPublicationsPeople,
				subOrgPublicationsMap,
				subOrgMap,
				organisationToPeopleMap,
				personToPublicationMap
		);

		if (orgPublications.isEmpty()) {
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		} else {

			Map<String, String> fileData = new HashMap<String, String>();
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				Gson json = new Gson();
				Set subEntitiesJson = new HashSet();

				// For each suborganisation
				for (String subOrg : subOrgPublicationsMap.keySet()) {
					JsonObject entityJson = new JsonObject(orgLabelMap.get(subOrg));

					List<List<Integer>> yearPubCounts = CounterUtils.getObjectCountByYear(subOrgPublicationsMap.get(subOrg), publicationToYearMap);

					String type = orgMostSpecificLabelMap.get(subOrg);

					entityJson.setYearToActivityCount(yearPubCounts);
					entityJson.setOrganizationTypes(Arrays.asList(type == null ? "Organization" : type));
					entityJson.setEntityURI(subOrg);
					entityJson.setVisMode("ORGANIZATION");

					subEntitiesJson.add(entityJson);
				}

				// For each person
				for (String person : orgPublicationsPeople) {
					JsonObject entityJson = new JsonObject(personLabelMap.get(person));

					List<List<Integer>> yearPubCounts = CounterUtils.getObjectCountByYear(personToPublicationMap.get(person), publicationToYearMap);

					String type = personMostSpecificLabelMap.get(person);

					entityJson.setYearToActivityCount(yearPubCounts);
					entityJson.setOrganizationTypes(Arrays.asList(type == null ? "Person" : type));
					entityJson.setEntityURI(person);
					entityJson.setVisMode("PERSON");

					subEntitiesJson.add(entityJson);
				}

				SubjectEntityJSON subjectEntityJSON = new SubjectEntityJSON(
						orgLabelMap.get(subjectEntityURI),
						subjectEntityURI,
						OrgUtils.getParentURIsToLabel(subjectEntityURI, subOrgMap, orgLabelMap));

				subEntitiesJson.add(subjectEntityJSON);

				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, json.toJson(subEntitiesJson));

			} else {
				String entityLabel = orgLabelMap.get(subjectEntityURI);
				if (StringUtils.isBlank(entityLabel)) {
					entityLabel = "no-organization";
				}

				StringBuilder csvFileContent = new StringBuilder();

				csvFileContent.append("Entity Name, Publication Count, Entity Type\n");

				for (String subOrg : subOrgPublicationsMap.keySet()) {
					csvFileContent.append(StringEscapeUtils.escapeCsv(orgLabelMap.get(subOrg)));
					csvFileContent.append(", ");

					csvFileContent.append(subOrgPublicationsMap.get(subOrg).size());
					csvFileContent.append(", ");

					csvFileContent.append("Organization");
					csvFileContent.append("\n");

				}

				String outputFileName = UtilityFunctions.slugify(entityLabel) + "_publications-per-year" + ".csv";
				fileData.put(DataVisualizationController.FILE_NAME_KEY, outputFileName);
				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, csvFileContent.toString());
			}
			return fileData;
		}
	}

	private Map<String, String> prepareDataErrorResponse() {
		String outputFileName = "no-organization_publications-per-year.csv";
		
		Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY, outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,      "");
		return fileData;
	}

	private Map<String, String> prepareStandaloneDataErrorResponse() {
		Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,      "{\"error\" : \"No Publications for this Organization found in VIVO.\"}");
		return fileData;
	}

	@Override
	public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		VisConstants.DataVisMode currentDataMode = VisConstants.DataVisMode.CSV;
		
		/*
		 * This will provide the data in json format mainly used for standalone tmeporal vis. 
		 * */
		if (VisualizationFrameworkConstants.JSON_OUTPUT_FORMAT
					.equalsIgnoreCase(vitroRequest.getParameter(
							VisualizationFrameworkConstants.VIS_MODE_KEY))) {
			
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
	
	
	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log, Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Entity Pub Count does not provide Ajax Response.");
	}

	private TemplateResponseValues prepareStandaloneMarkupResponse(VitroRequest vreq, String entityURI) {
        String standaloneTemplate = "entityComparisonOnPublicationsStandalone.ftl";
        String organizationLabel = OrganizationUtilityFunctions.getEntityLabelFromDAO(vreq, entityURI);
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title", organizationLabel + " - Temporal Graph Visualization");
        body.put("organizationURI", entityURI);
        body.put("organizationLocalName", UtilityFunctions.getIndividualLocalName(entityURI, vreq));
        body.put("vivoDefaultNamespace", vreq.getWebappDaoFactory().getDefaultNamespace());
        body.put("organizationLabel", organizationLabel);
        
        return new TemplateResponseValues(standaloneTemplate, body);
	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}
}