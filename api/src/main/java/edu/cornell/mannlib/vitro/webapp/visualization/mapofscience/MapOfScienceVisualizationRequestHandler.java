/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.mapofscience;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jena.query.QuerySolution;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.model.OrganizationPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.OrgUtils;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationCaches;
import mapping.ScienceMapping;
import mapping.ScienceMappingResult;

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
import edu.cornell.mannlib.vitro.webapp.visualization.constants.MapOfScienceConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph.OrganizationUtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json.MapOfScience;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class MapOfScienceVisualizationRequestHandler implements VisualizationRequestHandler {

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		return generateStandardVisualizationForScienceMapVis(vitroRequest, log, dataset, entityURI);
	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {

		if (vitroRequest.getRequestURI().endsWith("/about")) {
			return generateAboutScienceMapVisPage();
		} else {
			return generateStandardVisualizationForScienceMapVis(
					vitroRequest, log, dataset, parameters.get(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY));
		}
	}

	private ResponseValues generateAboutScienceMapVisPage() {
		return new TemplateResponseValues("aboutMapOfScience.ftl");
	}

	private ResponseValues generateStandardVisualizationForScienceMapVis(
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

	private Set<String> getPublicationsForPerson(RDFService rdfService, String personUri) {
		if (preferCachesForPersonMap() && VisualizationCaches.personToPublication.isCached()) {
			Map<String, Set<String>> personToPublicationMap = VisualizationCaches.personToPublication.get(rdfService).personToPublication;
			return personToPublicationMap.get(personUri);
		} else {
			final Set<String> queryResults = new HashSet<String>();
			String query = QueryConstants.getSparqlPrefixQuery() +
					"SELECT ?document\n" +
					"WHERE\n" +
					"{\n" +
					"  <" + personUri + "> core:relatedBy ?authorship .\n" +
					"  ?authorship a core:Authorship .\n" +
					"  ?authorship core:relates ?document .\n" +
					"  ?document a bibo:Document .\n" +
					"}\n";

			try {
				rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
					@Override
					protected void processQuerySolution(QuerySolution qs) {
						queryResults.add(qs.getResource("document").getURI());
					}
				});

			} catch (RDFServiceException e) {
			}

			return queryResults;
		}
	}

	private Map<String, String> getJournalsForPerson(RDFService rdfService, String personUri) {
		if (preferCachesForPersonMap() && VisualizationCaches.publicationToJournal.isCached()) {
			return VisualizationCaches.publicationToJournal.get(rdfService);
		} else {
			final Map<String, String> queryResults = new HashMap<String, String>();
			String query = QueryConstants.getSparqlPrefixQuery() +
					"SELECT ?document ?journalLabel\n" +
					"WHERE\n" +
					"{\n" +
					"  <" + personUri + "> core:relatedBy ?authorship .\n" +
					"  ?authorship a core:Authorship .\n" +
					"  ?authorship core:relates ?document .\n" +
					"  ?document a bibo:Document .\n" +
					"  ?document core:hasPublicationVenue ?journal . \n" +
					"  ?journal rdfs:label ?journalLabel . \n" +
					"}\n";

			try {
				rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
					@Override
					protected void processQuerySolution(QuerySolution qs) {
						queryResults.put(qs.getResource("document").getURI(), qs.getLiteral("journalLabel").getString());
					}
				});

			} catch (RDFServiceException e) {
			}

			return queryResults;
		}
	}

	private static boolean preferCachesForPersonMap() {
		return timeToGeneratePersonMap > 2000;
	}

	private static long timeToGeneratePersonMap = -1;
	private synchronized static void recordExecutionTimeForPersonMap(long time) {
		timeToGeneratePersonMap = Math.max(timeToGeneratePersonMap, time);
	}

	private Map<String, String> getSubjectPersonEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, String subjectEntityURI, String entityLabel, VisConstants.DataVisMode dataOuputFormat)
			throws MalformedQueryParametersException, JsonProcessingException {

		long startTime = System.currentTimeMillis();
		try {
			RDFService rdfService = vitroRequest.getRDFService();

			Set<String> publicationsForPerson = getPublicationsForPerson(rdfService, subjectEntityURI);

			if (publicationsForPerson == null || publicationsForPerson.size() == 0) {
				if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
					return prepareStandaloneDataErrorResponse();
				} else {
					return prepareDataErrorResponse();
				}
			} else {
				Map<String, String> publicationToJournalMap = getJournalsForPerson(rdfService, subjectEntityURI);

				JournalPublicationCounts journalCounts = new JournalPublicationCounts();

				for (String publication : publicationsForPerson) {
					journalCounts.increment(publicationToJournalMap.get(publication));
				}

				ScienceMappingResult result = getScienceMappingResult(journalCounts.map);

				Map<String, String> fileData = new HashMap<String, String>();
				if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
					Set jsonContent = new HashSet();

					MapOfScience entityJson = new MapOfScience(subjectEntityURI);
					entityJson.setLabel("");
					entityJson.setType("PERSON");

					entityJson.setPubsWithNoJournals(journalCounts.noJournalCount);
					updateEntityMapOfScienceInformation(entityJson, result);

					jsonContent.add(entityJson);

					ObjectMapper mapper = new ObjectMapper();

					fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
					fileData.put(DataVisualizationController.FILE_CONTENT_KEY, mapper.writeValueAsString(jsonContent));
				} else {
					if (StringUtils.isBlank(entityLabel)) {
						entityLabel = "no-name";
					}

					String outputFileName = UtilityFunctions.slugify(entityLabel);
					String fileContent = null;

					String visModeKey = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);
					if (VisualizationFrameworkConstants.SUBDISCIPLINE_TO_ACTIVTY_VIS_MODE.equalsIgnoreCase(visModeKey)) {
						outputFileName += "_subdiscipline-to-publications" + ".csv";
						fileContent = getSubDisciplineToPublicationsCSVContent(result);
					} else if (VisualizationFrameworkConstants.SCIENCE_UNLOCATED_JOURNALS_VIS_MODE.equalsIgnoreCase(visModeKey)) {
						outputFileName += "_unmapped-journals" + ".csv";
						fileContent = getUnlocatedJournalsCSVContent(result, journalCounts.noJournalCount);
					} else {
						outputFileName += "_discipline-to-publications" + ".csv";
						fileContent = getDisciplineToPublicationsCSVContent(result);
					}

					fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
					fileData.put(DataVisualizationController.FILE_NAME_KEY, outputFileName);
					fileData.put(DataVisualizationController.FILE_CONTENT_KEY, fileContent);
				}

				return fileData;
			}
		} finally {
			recordExecutionTimeForPersonMap(System.currentTimeMillis() - startTime);
		}
	}

	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, String subjectEntityURI, String entityLabel, VisConstants.DataVisMode dataOuputFormat)
			throws MalformedQueryParametersException, JsonProcessingException {

		RDFService rdfService = vitroRequest.getRDFService();

		Map<String, String> orgLabelMap = VisualizationCaches.organizationLabels.get(rdfService);

		if (orgLabelMap.get(subjectEntityURI) == null) {
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		}

		Map<String, Set<String>> subOrgMap = VisualizationCaches.organizationSubOrgs.get(rdfService);
		OrganizationPeopleMap organisationToPeopleMap = VisualizationCaches.organisationToPeopleMap.get(rdfService);
		Map<String, Set<String>> personToPublicationMap = VisualizationCaches.personToPublication.get(rdfService).personToPublication;
		Map<String, String> publicationToJournalMap = VisualizationCaches.publicationToJournal.get(rdfService);

		Set<String> orgPublications       = new HashSet<String>();
		Set<String> orgPublicationsPeople = new HashSet<String>();

		Map<String, Set<String>> subOrgPublicationsMap = new HashMap<String, Set<String>>();

		OrgUtils.getObjectMappingsForOrgAndSubOrgs(
				subjectEntityURI,
				orgPublications,
				orgPublicationsPeople,
				subOrgPublicationsMap,
				subOrgMap,
				organisationToPeopleMap.organizationToPeople,
				personToPublicationMap
		);

		if (orgPublications.isEmpty()) {
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		} else {
			JournalPublicationCounts journalCounts = new JournalPublicationCounts();

			for (String publication : orgPublications) {
				journalCounts.increment(publicationToJournalMap.get(publication));
			}

			ScienceMappingResult result = getScienceMappingResult(journalCounts.map);

			Map<String, String> fileData = new HashMap<String, String>();
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				Set jsonContent = new HashSet();

				MapOfScience entityJson = new MapOfScience(subjectEntityURI);
				entityJson.setLabel(entityLabel);
				entityJson.setType("ORGANIZATION");

				if (subOrgMap.containsKey(subjectEntityURI)) {
					for (String subOrg : subOrgMap.get(subjectEntityURI)) {
						Set<String> publications = subOrgPublicationsMap.get(subOrg);
						entityJson.addSubEntity(subOrg,
								orgLabelMap.get(subOrg),
								"ORGANIZATION",
								publications == null ? 0 : publications.size());
					}
				}

				entityJson.setPubsWithNoJournals(journalCounts.noJournalCount);
				updateEntityMapOfScienceInformation(entityJson, result);

				jsonContent.add(entityJson);

				ObjectMapper mapper = new ObjectMapper();

				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, mapper.writeValueAsString(jsonContent));
			} else {
				if (StringUtils.isBlank(entityLabel)) {
					entityLabel = "no-organization";
				}

				String outputFileName = UtilityFunctions.slugify(entityLabel);
				String fileContent = null;

				String visModeKey = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);
				if (VisualizationFrameworkConstants.SUBDISCIPLINE_TO_ACTIVTY_VIS_MODE.equalsIgnoreCase(visModeKey)) {
					outputFileName += "_subdiscipline-to-publications" + ".csv";
					fileContent = getSubDisciplineToPublicationsCSVContent(result);
				} else if (VisualizationFrameworkConstants.SCIENCE_UNLOCATED_JOURNALS_VIS_MODE.equalsIgnoreCase(visModeKey)) {
					outputFileName += "_unmapped-journals" + ".csv";
					fileContent = getUnlocatedJournalsCSVContent(result, journalCounts.noJournalCount);
				} else {
					outputFileName += "_discipline-to-publications" + ".csv";
					fileContent = getDisciplineToPublicationsCSVContent(result);
				}

				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_NAME_KEY, outputFileName);
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, fileContent);
			}
			return fileData;
		}
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

	private Map<String, String> prepareStandaloneDataErrorResponse() throws JsonProcessingException {

		GenericQueryMap errorDataResponse = new GenericQueryMap();
		errorDataResponse.addEntry("error", "No Publications for this Entity found in VIVO.");

		Map<String, String> fileData = new HashMap<String, String>();

		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "application/octet-stream");

		ObjectMapper mapper = new ObjectMapper();
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, mapper.writeValueAsString(errorDataResponse));

		return fileData;
	}

	@Override
	public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException, JsonProcessingException {

		String entityURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		if (StringUtils.isBlank(entityURI)) {
			entityURI = OrganizationUtilityFunctions
							.getStaffProvidedOrComputedHighestLevelOrganization(
									log,
									dataset,
									vitroRequest);
		}

		VisConstants.DataVisMode currentDataVisMode = VisConstants.DataVisMode.CSV;

		if (VisualizationFrameworkConstants.JSON_OUTPUT_FORMAT
				.equalsIgnoreCase(vitroRequest.getParameter(
						VisualizationFrameworkConstants.OUTPUT_FORMAT_KEY))) {
			currentDataVisMode = VisConstants.DataVisMode.JSON;
		}

		Individual individual = vitroRequest.getWebappDaoFactory()
				.getIndividualDao()
				.getIndividualByURI(entityURI);

		try {
			if (individual != null && individual.isVClass("http://xmlns.com/foaf/0.1/Person")) {
				return getSubjectPersonEntityAndGenerateDataResponse(
						vitroRequest,
						entityURI,
						individual != null ? individual.getDataValue("http://www.w3.org/2000/01/rdf-schema#label") : "",
						currentDataVisMode);
			} else {
				return getSubjectEntityAndGenerateDataResponse(
						vitroRequest,
						entityURI,
						individual != null ? individual.getDataValue("http://www.w3.org/2000/01/rdf-schema#label") : "",
						currentDataVisMode);
			}
		} finally {
			VisualizationCaches.buildMissing();
		}
	}


	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log, Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Map of Science Vis does not provide Ajax Response.");
	}

	private TemplateResponseValues prepareStandaloneMarkupResponse(VitroRequest vreq, String entityURI)
				throws MalformedQueryParametersException {

        String standaloneTemplate = "mapOfScienceStandalone.ftl";

        String entityLabel = UtilityFunctions.getIndividualLabelFromDAO(vreq, entityURI);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title", entityLabel + " - Map of Science Visualization");
        body.put("entityURI", entityURI);
        body.put("entityLocalName", UtilityFunctions.getIndividualLocalName(entityURI, vreq));
        body.put("entityLabel", entityLabel);

        if (UtilityFunctions.isEntityAPerson(vreq, entityURI)) {
        	body.put("entityType", "PERSON");
			if (preferCachesForPersonMap() && VisualizationCaches.personToPublication.isCached()) {
				body.put("builtFromCacheTime", VisualizationCaches.personToPublication.cachedWhen());
			}
        } else {
        	body.put("entityType", "ORGANIZATION");
			if (VisualizationCaches.personToPublication.isCached()) {
				body.put("builtFromCacheTime", VisualizationCaches.personToPublication.cachedWhen());
			}
        }


        body.put("vivoDefaultNamespace", vreq.getWebappDaoFactory().getDefaultNamespace());

		ConfigurationProperties properties = ConfigurationProperties.getBean(vreq);
		if (properties != null) {
			String key = properties.getProperty("google.maps.key");
			if (!StringUtils.isEmpty(key)) {
				body.put("googleMapsKey", key);
			}
		}

        return new TemplateResponseValues(standaloneTemplate, body);
	}

	private void updateEntityMapOfScienceInformation(MapOfScience entityJson, ScienceMappingResult result) {
		int mappedPublicationCount = 0;
		int publicationsWithInvalidJournalCount = 0;
		Map<Integer, Float> subdisciplineToActivity = new HashMap<Integer, Float>();

		if (result != null) {
			subdisciplineToActivity = result.getMappedResult();
			publicationsWithInvalidJournalCount = Math.round(result.getUnMappedPublications());
			mappedPublicationCount = Math.round(result.getMappedPublications());
		}

		entityJson.setPubsMapped(mappedPublicationCount);
		entityJson.setPubsWithInvalidJournals(publicationsWithInvalidJournalCount);
		entityJson.setSubdisciplineActivity(subdisciplineToActivity);
	}

	private ScienceMappingResult getScienceMappingResult(
			Map<String, Integer> journalToPublicationCount) {
		ScienceMappingResult result = null;
		try {
			result = (new ScienceMapping()).generateScienceMappingResult(journalToPublicationCount);
		} catch (NumberFormatException e) {
			System.err.println("NumberFormatException coming from Map Of Science Vis");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException coming from Map Of Science Vis");
			e.printStackTrace();
		}
		return result;
	}

	private String getDisciplineToPublicationsCSVContent(ScienceMappingResult result) {
		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Discipline, Publication Count, % Activity\n");

		Map<Integer, Float> disciplineToPublicationCount = new HashMap<Integer, Float>();

		Float totalMappedPublications = new Float(0);

		if (result != null) {
			for (Map.Entry<Integer, Float> currentMappedSubdiscipline : result.getMappedResult().entrySet()) {
				float updatedPublicationCount = currentMappedSubdiscipline.getValue();

				Integer lookedUpDisciplineID = MapOfScienceConstants.SUB_DISCIPLINE_ID_TO_DISCIPLINE_ID
													.get(currentMappedSubdiscipline.getKey());

				if (disciplineToPublicationCount.containsKey(lookedUpDisciplineID)) {
					updatedPublicationCount += disciplineToPublicationCount.get(lookedUpDisciplineID);
				}

				disciplineToPublicationCount.put(lookedUpDisciplineID, updatedPublicationCount);
			}

			totalMappedPublications = result.getMappedPublications();
		}

		DecimalFormat percentageActivityFormat = new DecimalFormat("#.#");

		for (Map.Entry<Integer, Float> currentMappedDiscipline : disciplineToPublicationCount.entrySet()) {
			csvFileContent.append(StringEscapeUtils.escapeCsv(MapOfScienceConstants.DISCIPLINE_ID_TO_LABEL.get(currentMappedDiscipline.getKey())));
			csvFileContent.append(", ");
			csvFileContent.append(percentageActivityFormat.format(currentMappedDiscipline.getValue()));
			csvFileContent.append(", ");

			if (totalMappedPublications > 0) {
				csvFileContent.append(percentageActivityFormat.format(100 * currentMappedDiscipline.getValue() / totalMappedPublications));
			} else {
				csvFileContent.append("Not Available");
			}

			csvFileContent.append("\n");
		}

		for (Map.Entry<Integer, String> currentDiscipline : MapOfScienceConstants.DISCIPLINE_ID_TO_LABEL.entrySet()) {

			Float currentDisciplineValue = disciplineToPublicationCount.get(currentDiscipline.getKey());
			if (currentDisciplineValue == null) {
				csvFileContent.append(StringEscapeUtils.escapeCsv(currentDiscipline.getValue()));
				csvFileContent.append(", ");
				csvFileContent.append(0);
				csvFileContent.append(", ");
				csvFileContent.append(0);
				csvFileContent.append("\n");
			}
		}

		return csvFileContent.toString();
	}

	private String getUnlocatedJournalsCSVContent(ScienceMappingResult result, int noJournalCount) {
		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Publication Venue, Publication Count\n");

		DecimalFormat percentageActivityFormat = new DecimalFormat("#.#");

		if (noJournalCount > 0) {
			csvFileContent.append(StringEscapeUtils.escapeCsv("No Publication Venue Given"));
			csvFileContent.append(", ");
			csvFileContent.append(percentageActivityFormat.format(noJournalCount));
			csvFileContent.append("\n");
		}

		if (result != null) {
			Map<String, Float> mappedResult = result.getUnmappedResult();

			for (Map.Entry<String, Float> currentUnMappedJournal : mappedResult.entrySet()) {
				csvFileContent.append(StringEscapeUtils.escapeCsv(currentUnMappedJournal.getKey()));
				csvFileContent.append(", ");
				csvFileContent.append(percentageActivityFormat.format(currentUnMappedJournal.getValue()));
				csvFileContent.append("\n");
			}
		}

		return csvFileContent.toString();
	}

	private String getSubDisciplineToPublicationsCSVContent(ScienceMappingResult result) {
		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Sub-Discipline, Publication Count, % Activity\n");

		Float totalMappedPublications = new Float(0);

		if (result != null) {
			DecimalFormat percentageActivityFormat = new DecimalFormat("#.#");

			totalMappedPublications = result.getMappedPublications();
			Map<Integer, Float> mappedResult = result.getMappedResult();

			for (Map.Entry<Integer, Float> currentMappedSubdiscipline : mappedResult.entrySet()) {
				csvFileContent.append(StringEscapeUtils.escapeCsv(MapOfScienceConstants.SUB_DISCIPLINE_ID_TO_LABEL
																	.get(currentMappedSubdiscipline.getKey())));
				csvFileContent.append(", ");
				csvFileContent.append(percentageActivityFormat.format(currentMappedSubdiscipline.getValue()));
				csvFileContent.append(", ");

				if (totalMappedPublications > 0) {
					csvFileContent.append(percentageActivityFormat.format(100 * currentMappedSubdiscipline.getValue() / totalMappedPublications));
				} else {
					csvFileContent.append("Not Available");
				}
				csvFileContent.append("\n");
			}

			for (Map.Entry<Integer, String> currentSubdiscipline : MapOfScienceConstants.SUB_DISCIPLINE_ID_TO_LABEL.entrySet()) {
				Float currentMappedSubdisciplineValue = mappedResult.get(currentSubdiscipline.getKey());
				if (currentMappedSubdisciplineValue == null) {
					csvFileContent.append(StringEscapeUtils.escapeCsv(currentSubdiscipline.getValue()));
					csvFileContent.append(", ");
					csvFileContent.append(0);
					csvFileContent.append(", ");
					csvFileContent.append(0);
					csvFileContent.append("\n");
				}
			}
		}

		return csvFileContent.toString();
	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}

	private static class JournalPublicationCounts {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int noJournalCount = 0;
		int total = 0;

		void increment(String journalName) {
			if (StringUtils.isEmpty(journalName)) {
				noJournalCount++;
			} else {
				Integer count = map.get(journalName);
				if (count == null) {
					map.put(journalName, 1);
				} else {
					map.put(journalName, 1 + count.intValue());
				}
			}

			total++;
		}

		boolean isEmpty() {
			return total == 0;
		}

		long getTotal() {
			return total;
		}
	}
}
