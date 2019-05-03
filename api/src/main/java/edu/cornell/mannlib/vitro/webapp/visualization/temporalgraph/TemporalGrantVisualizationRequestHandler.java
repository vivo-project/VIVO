/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.model.OrganizationPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.CounterUtils;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.OrgUtils;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationCaches;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import org.apache.jena.query.Dataset;

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


public class TemporalGrantVisualizationRequestHandler implements
		VisualizationRequestHandler {

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		return generateStandardVisualizationForGrantTemporalVis(vitroRequest, log, dataset, entityURI);
	}


	private ResponseValues generateStandardVisualizationForGrantTemporalVis(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String entityURI) throws MalformedQueryParametersException {

		if (StringUtils.isBlank(entityURI)) {
			entityURI = OrganizationUtilityFunctions.getStaffProvidedOrComputedHighestLevelOrganization(
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
	public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException, JsonProcessingException {

		String entityURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

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

		try {
			return getSubjectEntityAndGenerateDataResponse(
					vitroRequest,
					log,
					dataset,
					entityURI,
					currentDataMode);
		} finally {
			VisualizationCaches.buildMissing();
		}
	}

	private Map<String, String> prepareDataErrorResponse() {
		String outputFileName = "no-organization_grants-per-year.csv";

		Map<String, String> fileData = new HashMap<String, String>();

		fileData.put(DataVisualizationController.FILE_NAME_KEY, outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, "");
		return fileData;
	}

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		throw new UnsupportedOperationException("Entity Grant Count does not provide Ajax response.");
	}

	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI, VisConstants.DataVisMode visMode)
			throws MalformedQueryParametersException, JsonProcessingException {

		RDFService rdfService = vitroRequest.getRDFService();

		Map<String, String> orgLabelMap = VisualizationCaches.organizationLabels.get(rdfService);
		Map<String, String> personLabelMap = VisualizationCaches.personLabels.get(rdfService);

		if (orgLabelMap.get(subjectEntityURI) == null) {
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		}

		Map<String, Set<String>> subOrgMap               = VisualizationCaches.organizationSubOrgs.get(rdfService);
		OrganizationPeopleMap organisationToPeopleMap = VisualizationCaches.organisationToPeopleMap.get(rdfService);
		Map<String, String> orgMostSpecificLabelMap      = VisualizationCaches.organizationToMostSpecificLabel.get(rdfService);
		Map<String, String> personMostSpecificLabelMap   = VisualizationCaches.personToMostSpecificLabel.get(rdfService);
		Map<String, Set<String>> personToGrantMap        = VisualizationCaches.personToGrant.get(rdfService);
		Map<String, String>      grantToYearMap          = VisualizationCaches.grantToYear.get(rdfService);

		Set<String> orgGrants       = new HashSet<String>();
		Set<String> orgGrantsPeople = new HashSet<String>();

		Map<String, Set<String>> subOrgGrantsMap = new HashMap<String, Set<String>>();

		OrgUtils.getObjectMappingsForOrgAndSubOrgs(
				subjectEntityURI,
				orgGrants,
				orgGrantsPeople,
				subOrgGrantsMap,
				subOrgMap,
				organisationToPeopleMap.organizationToPeople,
				personToGrantMap
		);

		if (orgGrants.isEmpty()) {
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		} else {

			Map<String, String> fileData = new HashMap<String, String>();
			if (VisConstants.DataVisMode.JSON.equals(visMode)) {
				Set subEntitiesJson = new HashSet();

				// For each suborganisation
				for (String subOrg : subOrgGrantsMap.keySet()) {
					JsonObject entityJson = new JsonObject(orgLabelMap.get(subOrg));

					if (subOrgGrantsMap.containsKey(subOrg)) {
						List<List<Integer>> yearPubCounts = CounterUtils.getObjectCountByYear(subOrgGrantsMap.get(subOrg), grantToYearMap);
						entityJson.setYearToActivityCount(yearPubCounts);
					} else {
						entityJson.setYearToActivityCount(new ArrayList<List<Integer>>());
					}

					String type = orgMostSpecificLabelMap.get(subOrg);
					entityJson.setOrganizationTypes(Arrays.asList(type == null ? "Organization" : type));

					entityJson.setEntityURI(subOrg);
					entityJson.setVisMode("ORGANIZATION");

					subEntitiesJson.add(entityJson);
				}

				// For each person
				for (String person : orgGrantsPeople) {
					JsonObject entityJson = new JsonObject(personLabelMap.get(person));

					if (personToGrantMap.containsKey(person)) {
						List<List<Integer>> yearPubCounts = CounterUtils.getObjectCountByYear(personToGrantMap.get(person), grantToYearMap);
						entityJson.setYearToActivityCount(yearPubCounts);
					} else {
						entityJson.setYearToActivityCount(new ArrayList<List<Integer>>());
					}

					String type = personMostSpecificLabelMap.get(person);
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

				ObjectMapper mapper = new ObjectMapper();

				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, mapper.writeValueAsString(subEntitiesJson));

			} else {
				String entityLabel = orgLabelMap.get(subjectEntityURI);
				if (StringUtils.isBlank(entityLabel)) {
					entityLabel = "no-organization";
				}

				StringBuilder csvFileContent = new StringBuilder();

				csvFileContent.append("Entity Name, Grant Count, Entity Type\n");

				for (String subOrg : subOrgGrantsMap.keySet()) {
					csvFileContent.append(StringEscapeUtils.escapeCsv(orgLabelMap.get(subOrg)));
					csvFileContent.append(", ");

					csvFileContent.append(subOrgGrantsMap.get(subOrg).size());
					csvFileContent.append(", ");

					csvFileContent.append("Organization");
					csvFileContent.append("\n");

				}

				String outputFileName = UtilityFunctions.slugify(entityLabel) + "_grants-per-year" + ".csv";
				fileData.put(DataVisualizationController.FILE_NAME_KEY, outputFileName);
				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, csvFileContent.toString());
			}
			return fileData;
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

		if (VisualizationCaches.personToGrant.isCached()) {
			body.put("builtFromCacheTime", VisualizationCaches.personToGrant.cachedWhen());
		}

		return new TemplateResponseValues(standaloneTemplate, body);
	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		return null;
	}
}
