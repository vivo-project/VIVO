/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.mapofscience;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mapping.ScienceMapping;
import mapping.ScienceMappingResult;

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
import edu.cornell.mannlib.vitro.webapp.visualization.constants.MapOfScienceConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph.OrganizationUtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.MapOfScienceActivity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json.MapOfScience;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.SelectOnModelUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class MapOfScienceVisualizationRequestHandler implements
		VisualizationRequestHandler {
	
	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		return generateStandardVisualizationForScienceMapVis(
				vitroRequest, log, dataset, entityURI);
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
	
	
	private Map<String, String> getSubjectPersonEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI, VisConstants.DataVisMode dataOuputFormat)
					throws MalformedQueryParametersException {
		
		Map<String, Activity> documentURIForAssociatedPeopleTOVO = new HashMap<String, Activity>();
		
		
		Entity personEntity = SelectOnModelUtilities
				.getSubjectPersonEntity(dataset, subjectEntityURI);
		
		if (personEntity.getSubEntities() !=  null) {
			
			documentURIForAssociatedPeopleTOVO = SelectOnModelUtilities
						.getPublicationsWithJournalForAssociatedPeople(dataset, personEntity.getSubEntities());
			
		}
		
		if (documentURIForAssociatedPeopleTOVO.isEmpty()) {
			
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
			
		} else {	
			
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataResponse(vitroRequest, personEntity);
			} else {
				return prepareDataResponse(vitroRequest, personEntity);
			}
		}
	}

	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, Log log, Dataset dataset,
			String subjectEntityURI, VisConstants.DataVisMode dataOuputFormat)
					throws MalformedQueryParametersException {
		
		Entity organizationEntity = SelectOnModelUtilities
				.getSubjectOrganizationHierarchy(dataset, subjectEntityURI);
		
		if (organizationEntity.getSubEntities() ==  null) {
			
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		}
		
		Map<String, Activity> documentURIForAssociatedPeopleTOVO = new HashMap<String, Activity>();
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		allDocumentURIToVOs = SelectOnModelUtilities.getPublicationsWithJournalForAllSubOrganizations(dataset, organizationEntity);
		
		Entity organizationWithAssociatedPeople = SelectOnModelUtilities
				.getSubjectOrganizationAssociatedPeople(dataset, subjectEntityURI);
		
		if (organizationWithAssociatedPeople.getSubEntities() !=  null) {
			
			documentURIForAssociatedPeopleTOVO = SelectOnModelUtilities
						.getPublicationsWithJournalForAssociatedPeople(dataset, organizationWithAssociatedPeople.getSubEntities());
			
			organizationEntity = OrganizationUtilityFunctions.mergeEntityIfShareSameURI(
										organizationEntity,
										organizationWithAssociatedPeople);
		} 
		
		if (allDocumentURIToVOs.isEmpty() && documentURIForAssociatedPeopleTOVO.isEmpty()) {
			
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
			
		} else {	
			
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataResponse(vitroRequest, organizationEntity);
			} else {
				return prepareDataResponse(vitroRequest, organizationEntity);
			}
		}
	}
	
	/**
	 * @param vitroRequest 
	 * 
	 * @param entity
	 * @param subentities
	 * @param subOrganizationTypesResult
	 */
	private Map<String, String> prepareDataResponse(VitroRequest vitroRequest, Entity entity) {

		String entityLabel = entity.getEntityLabel();

		/*
		* To make sure that null/empty records for entity names do not cause any mischief.
		* */
		if (StringUtils.isBlank(entityLabel)) {
			entityLabel = "no-organization";
		}
		
		String outputFileName = UtilityFunctions.slugify(entityLabel);
		
		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		
		if (VisualizationFrameworkConstants.SUBDISCIPLINE_TO_ACTIVTY_VIS_MODE
				.equalsIgnoreCase(vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY))) {
			
			fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
						 getSubDisciplineToPublicationsCSVContent(entity));
			
			outputFileName += "_subdiscipline-to-publications" + ".csv";
			
		} else if (VisualizationFrameworkConstants.SCIENCE_UNLOCATED_JOURNALS_VIS_MODE
				.equalsIgnoreCase(vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY))) {
			
			fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
						 getUnlocatedJournalsCSVContent(entity));
			
			outputFileName += "_unmapped-journals" + ".csv";
			
		} else {
			
			fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
						 getDisciplineToPublicationsCSVContent(entity));
			
			outputFileName += "_discipline-to-publications" + ".csv";
			
		}
		
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
				 outputFileName);

		
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

	private Map<String, String> prepareStandaloneDataErrorResponse() {
		
		GenericQueryMap errorDataResponse = new GenericQueryMap();
		errorDataResponse.addEntry("error", "No Publications for this Entity found in VIVO.");
		
		Gson jsonErrorResponse = new Gson();
		
		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, jsonErrorResponse.toJson(errorDataResponse));
		
		return fileData;
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
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
		
		if (UtilityFunctions.isEntityAPerson(vitroRequest, entityURI)) {
			
			return getSubjectPersonEntityAndGenerateDataResponse(
					vitroRequest, 
					log,
					dataset,
					entityURI,
					currentDataVisMode);
			
		} else {

			return getSubjectEntityAndGenerateDataResponse(
					vitroRequest, 
					log,
					dataset,
					entityURI,
					currentDataVisMode);
		
		}

	}
	
	
	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Map of Science Vis does not provide Ajax Response.");
	}

	private Map<String, String> prepareStandaloneDataResponse(
										VitroRequest vitroRequest, 
										Entity entity) 
			throws MalformedQueryParametersException {

		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 writeMapOfScienceDataJSON(vitroRequest, 
							 					   entity));
		return fileData;
	}
	
	private TemplateResponseValues prepareStandaloneMarkupResponse(VitroRequest vreq,
																   String entityURI) 
				throws MalformedQueryParametersException {

        String standaloneTemplate = "mapOfScienceStandalone.ftl";
		
        String entityLabel = UtilityFunctions.getIndividualLabelFromDAO(
        									vreq,
        									entityURI);
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title", entityLabel + " - Map of Science Visualization");
        body.put("entityURI", entityURI);
        body.put("entityLocalName", UtilityFunctions.getIndividualLocalName(entityURI, vreq));
        body.put("entityLabel", entityLabel);
        
        if (UtilityFunctions.isEntityAPerson(vreq, entityURI)) {
        	body.put("entityType", "PERSON");
        	
        } else {
        	body.put("entityType", "ORGANIZATION");
        }
        
        body.put("vivoDefaultNamespace", vreq.getWebappDaoFactory().getDefaultNamespace());
        
        return new TemplateResponseValues(standaloneTemplate, body);
	}

	/**
	 * Function to generate a json file for year <-> publication count mapping.
	 * @param vreq 
	 * @param subentities
	 * @param subOrganizationTypesResult  
	 * @throws MalformedQueryParametersException 
	 */
	private String writeMapOfScienceDataJSON(VitroRequest vreq, 
										     Entity subjectEntity) throws MalformedQueryParametersException {

		Gson json = new Gson();
		Set jsonContent = new HashSet();

		MapOfScience entityJson = new MapOfScience(subjectEntity.getIndividualURI());
		entityJson.setLabel(subjectEntity.getIndividualLabel());
		
		if (UtilityFunctions.isEntityAPerson(vreq, subjectEntity.getEntityURI())) {
			entityJson.setType("PERSON");
		} else {
			entityJson.setType("ORGANIZATION");
		}
		
		Set<Activity> publicationsForEntity = new HashSet<Activity>();
		
		for (SubEntity subentity : subjectEntity.getSubEntities()) {
			
			Set<Activity> subEntityActivities = subentity.getActivities();
			publicationsForEntity.addAll(subEntityActivities);
			
			
			String subEntityType = "ORGANIZATION";
			
			if (subentity.getEntityClass().equals(VOConstants.EntityClassType.PERSON)) {
				subEntityType = "PERSON";
			} 
			
			entityJson.addSubEntity(subentity.getIndividualURI(), 
									subentity.getIndividualLabel(), 
									subEntityType, 
									subEntityActivities.size());
			
		}
		
		PublicationJournalStats publicationStats = getPublicationJournalStats(publicationsForEntity);
		
		entityJson.setPubsWithNoJournals(publicationStats.noJournalCount);
		
		/*
		 * This method side-effects entityJson by updating its counts for mapped publications, 
		 * publications with no journal names & publications with invalid journal names & 
		 * map of subdiscipline to activity.
		 * */
		updateEntityMapOfScienceInformation(entityJson,
											publicationStats.journalToPublicationCount);
		
		jsonContent.add(entityJson);
		
		return json.toJson(jsonContent);
	}

	private PublicationJournalStats getPublicationJournalStats(
			Set<Activity> subEntityActivities) {
		
		Map<String, Integer> journalToPublicationCount = new HashMap<String, Integer>();
		int publicationsWithNoJournalCount = 0;
		
		for (Activity activity : subEntityActivities) {
			
			if (StringUtils.isNotBlank(((MapOfScienceActivity) activity).getPublishedInJournal())) {
				
				String journalName = ((MapOfScienceActivity) activity).getPublishedInJournal();
				if (journalToPublicationCount.containsKey(journalName)) {
					
					journalToPublicationCount.put(journalName, 
												  journalToPublicationCount.get(journalName) + 1);
				} else {
					
					journalToPublicationCount.put(journalName, 1);
				}
				
			} else {
				
				publicationsWithNoJournalCount++;
			}
			
		} 
		
		return new PublicationJournalStats(publicationsWithNoJournalCount, journalToPublicationCount, null);
	}

	private void updateEntityMapOfScienceInformation(MapOfScience entityJson,
			Map<String, Integer> journalToPublicationCount) {
//		System.out.println("journalToPublicationCount " + journalToPublicationCount);
		
		int mappedPublicationCount = 0;
		int publicationsWithInvalidJournalCount = 0;
		Map<Integer, Float> subdisciplineToActivity = new HashMap<Integer, Float>();
		
		ScienceMappingResult result = getScienceMappingResult(journalToPublicationCount); 
			
		if (result != null) {
			subdisciplineToActivity = result.getMappedResult();
			publicationsWithInvalidJournalCount = Math.round(result.getUnMappedPublications());
			mappedPublicationCount = Math.round(result.getMappedPublications());
		}
		
//		System.out.println("subdisciplineToActivity " + subdisciplineToActivity);
		
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

	private String getDisciplineToPublicationsCSVContent(Entity subjectEntity) {

		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Discipline, Publication Count, % Activity\n");
		
		PublicationJournalStats stats = extractScienceMappingResultFromActivities(subjectEntity);
		ScienceMappingResult result = stats.scienceMapping;
		
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
	
	private String getUnlocatedJournalsCSVContent(Entity subjectEntity) {

		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Publication Venue, Publication Count\n");
		
		PublicationJournalStats stats = extractScienceMappingResultFromActivities(subjectEntity);
		ScienceMappingResult result = stats.scienceMapping;

		DecimalFormat percentageActivityFormat = new DecimalFormat("#.#");
		
		if (stats.noJournalCount > 0) {
			csvFileContent.append(StringEscapeUtils.escapeCsv("No Publication Venue Given"));
			csvFileContent.append(", ");
			csvFileContent.append(percentageActivityFormat.format(stats.noJournalCount));
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
	
	private String getSubDisciplineToPublicationsCSVContent(Entity subjectEntity) {

		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Sub-Discipline, Publication Count, % Activity\n");
		
		PublicationJournalStats stats = extractScienceMappingResultFromActivities(subjectEntity);
		ScienceMappingResult result = stats.scienceMapping;
		
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

	private PublicationJournalStats extractScienceMappingResultFromActivities(
			Entity subjectEntity) {
		Set<Activity> publicationsForEntity = new HashSet<Activity>();
		
		for (SubEntity subEntity : subjectEntity.getSubEntities()) {
			
			publicationsForEntity.addAll(subEntity.getActivities());
		}
		
		
		PublicationJournalStats publicationStats = getPublicationJournalStats(publicationsForEntity);
		
		publicationStats.scienceMapping = getScienceMappingResult(publicationStats.journalToPublicationCount);
		
		return publicationStats;
	}
	
	private class PublicationJournalStats {
		
		int noJournalCount;
		Map<String, Integer> journalToPublicationCount;
		ScienceMappingResult scienceMapping;
		
		public PublicationJournalStats(int noJournalCount,
									   Map<String, Integer> journalToPublicationCount,
									   ScienceMappingResult scienceMapping) {

			this.noJournalCount = noJournalCount;
			this.journalToPublicationCount = journalToPublicationCount;
			this.scienceMapping = scienceMapping;
		}
		
	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	} 

}	