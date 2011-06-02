/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.entitycomparison;

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

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class EntityPublicationCountRequestHandler implements
		VisualizationRequestHandler {

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		return generateStandardVisualizationForPublicationTemporalVis(
				vitroRequest, log, dataset, entityURI);
	}

	private ResponseValues generateStandardVisualizationForPublicationTemporalVis(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String entityURI) throws MalformedQueryParametersException {

		if (StringUtils.isBlank(entityURI)) {

			entityURI = OrganizationUtilityFunctions
					.getStaffProvidedOrComputedHighestLevelOrganization(log,
							dataset, vitroRequest);

		}

		return prepareStandaloneMarkupResponse(vitroRequest, entityURI);

	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {

		return generateStandardVisualizationForPublicationTemporalVis(
				vitroRequest,
				log,
				dataSource,
				parameters
						.get(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY));

	}

	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI) throws MalformedQueryParametersException {

		ModelConstructor constructQueryRunner = new EntityPublicationCountConstructQueryRunner(
				subjectEntityURI, dataset, log);

		Model constructedModel = constructQueryRunner.getConstructedModel();

		QueryRunner<Entity> queryManager = new EntityPublicationCountQueryRunner(
				subjectEntityURI, constructedModel, log);

		Entity entity = queryManager.getQueryResult();

		if (entity.getEntityLabel().equals("no-label")) {

			return prepareStandaloneDataErrorResponse(vitroRequest,
					subjectEntityURI);

		} else {

			return getSubEntityTypesAndComputeDataResponse(vitroRequest, log,
					dataset, subjectEntityURI, entity);
		}
	}

	private Map<String, String> prepareStandaloneDataErrorResponse(
			VitroRequest vitroRequest, String subjectEntityURI) {

		Map<String, String> fileData = new HashMap<String, String>();

		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
				"application/octet-stream");
		fileData
				.put(DataVisualizationController.FILE_CONTENT_KEY,
						"{\"error\" : \"No Publications for this Organization found in VIVO.\"}");
		return fileData;
	}

	private Map<String, String> getSubEntityTypesAndComputeDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI, Entity entity)
			throws MalformedQueryParametersException {

		Map<String, Set<String>> subOrganizationTypesResult = OrganizationUtilityFunctions
				.getSubEntityTypes(log, dataset, subjectEntityURI);

		return prepareStandaloneDataResponse(vitroRequest, entity, entity
				.getSubEntities(), subOrganizationTypesResult);
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		/*
		 * This will provide the data in json format mainly used for standalone
		 * tmeporal vis.
		 */
		if (VisualizationFrameworkConstants.JSON_OUTPUT_FORMAT
				.equalsIgnoreCase(vitroRequest
						.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY))) {

			if (StringUtils.isNotBlank(entityURI)) {

				return getSubjectEntityAndGenerateDataResponse(vitroRequest,
						log, dataset, entityURI);
			} else {

				return getSubjectEntityAndGenerateDataResponse(
						vitroRequest,
						log,
						dataset,
						OrganizationUtilityFunctions
								.getStaffProvidedOrComputedHighestLevelOrganization(
										log, dataset, vitroRequest));
			}

		} else {
			/*
			 * This provides csv download files for the content in the tables.
			 */

			ModelConstructor constructQueryRunner = new EntityPublicationCountConstructQueryRunner(
					entityURI, dataset, log);

			Model constructedModel = constructQueryRunner.getConstructedModel();

			QueryRunner<Entity> queryManager = new EntityPublicationCountQueryRunner(
					entityURI, constructedModel, log);

			Entity entity = queryManager.getQueryResult();

			Map<String, Set<String>> subOrganizationTypesResult = OrganizationUtilityFunctions
					.getSubEntityTypes(log, dataset, entityURI);

			return prepareDataResponse(entity, entity.getSubEntities(),
					subOrganizationTypesResult);

		}

	}

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException(
				"Entity Pub Count does not provide Ajax Response.");
	}

	/**
	 * Provides response when json file containing the publication count over
	 * the years is requested.
	 * 
	 * @param entity
	 * @param subentities
	 * @param subOrganizationTypesResult
	 */
	private Map<String, String> prepareDataResponse(Entity entity,
			Set<SubEntity> subentities,
			Map<String, Set<String>> subOrganizationTypesResult) {

		String entityLabel = entity.getEntityLabel();

		/*
		 * To make sure that null/empty records for entity names do not cause
		 * any mischief.
		 */
		if (StringUtils.isBlank(entityLabel)) {
			entityLabel = "no-organization";
		}

		String outputFileName = UtilityFunctions.slugify(entityLabel)
				+ "_publications-per-year" + ".csv";

		Map<String, String> fileData = new HashMap<String, String>();

		fileData.put(DataVisualizationController.FILE_NAME_KEY, outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
				"application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
				getEntityPublicationsPerYearCSVContent(subentities,
						subOrganizationTypesResult));
		return fileData;
	}

	private Map<String, String> prepareStandaloneDataResponse(
			VitroRequest vitroRequest, Entity entity,
			Set<SubEntity> subentities,
			Map<String, Set<String>> subOrganizationTypesResult)
			throws MalformedQueryParametersException {

		Map<String, String> fileData = new HashMap<String, String>();

		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
				"application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
				writePublicationsOverTimeJSON(vitroRequest, entity
						.getSubEntities(), subOrganizationTypesResult));
		return fileData;
	}

	private TemplateResponseValues prepareStandaloneMarkupResponse(
			VitroRequest vreq, String entityURI) {

		String standaloneTemplate = "entityComparisonOnPublicationsStandalone.ftl";

		String organizationLabel = OrganizationUtilityFunctions
				.getEntityLabelFromDAO(vreq, entityURI);

		Map<String, Object> body = new HashMap<String, Object>();
		body
				.put("title", organizationLabel
						+ " - Temporal Graph Visualization");
		body.put("organizationURI", entityURI);
		body.put("organizationLocalName", UtilityFunctions
				.getIndividualLocalName(entityURI, vreq));
		body.put("vivoDefaultNamespace", vreq.getWebappDaoFactory()
				.getDefaultNamespace());
		body.put("organizationLabel", organizationLabel);

		return new TemplateResponseValues(standaloneTemplate, body);
	}

	/**
	 * Function to generate a json file for year <-> publication count mapping.
	 * 
	 * @param vreq
	 * @param subentities
	 * @param subOrganizationTypesResult
	 * @throws MalformedQueryParametersException
	 */
	private String writePublicationsOverTimeJSON(VitroRequest vreq,
			Set<SubEntity> subentities,
			Map<String, Set<String>> subOrganizationTypesResult)
			throws MalformedQueryParametersException {

		Gson json = new Gson();
		Set<JsonObject> subEntitiesJson = new HashSet<JsonObject>();

		for (SubEntity subentity : subentities) {
			JsonObject entityJson = new JsonObject(subentity
					.getIndividualLabel());

			List<List<Integer>> yearPubCount = new ArrayList<List<Integer>>();

			for (Map.Entry<String, Integer> pubEntry : UtilityFunctions
					.getYearToActivityCount(subentity.getActivities())
					.entrySet()) {

				List<Integer> currentPubYear = new ArrayList<Integer>();
				if (pubEntry.getKey().equals(
						VOConstants.DEFAULT_PUBLICATION_YEAR)) {
					currentPubYear.add(-1);
				} else {
					currentPubYear.add(Integer.parseInt(pubEntry.getKey()));
				}

				currentPubYear.add(pubEntry.getValue());
				yearPubCount.add(currentPubYear);
			}

			entityJson.setYearToActivityCount(yearPubCount);
			entityJson.getOrganizationTypes().addAll(
					subOrganizationTypesResult.get(entityJson.getLabel()));

			entityJson.setEntityURI(subentity.getIndividualURI());

			boolean isPerson = UtilityFunctions.isEntityAPerson(vreq, subentity
					.getIndividualURI());

			if (isPerson) {
				entityJson.setVisMode("PERSON");
			} else {
				entityJson.setVisMode("ORGANIZATION");
			}
			subEntitiesJson.add(entityJson);
		}
		return json.toJson(subEntitiesJson);
	}

	private String getEntityPublicationsPerYearCSVContent(
			Set<SubEntity> subentities,
			Map<String, Set<String>> subOrganizationTypesResult) {

		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Entity Name, Publication Count, Entity Type\n");

		for (SubEntity subEntity : subentities) {

			csvFileContent.append(StringEscapeUtils.escapeCsv(subEntity
					.getIndividualLabel()));
			csvFileContent.append(", ");
			csvFileContent.append(subEntity.getActivities().size());
			csvFileContent.append(", ");

			StringBuilder joinedTypes = new StringBuilder();

			for (String subOrganizationType : subOrganizationTypesResult
					.get(subEntity.getIndividualLabel())) {
				joinedTypes.append(subOrganizationType + "; ");
			}

			csvFileContent.append(StringEscapeUtils.escapeCsv(joinedTypes
					.toString()));
			csvFileContent.append("\n");
		}
		return csvFileContent.toString();
	}

	@Override
	public Actions getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}

}