/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.mapofscience;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
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
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph.OrganizationUtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.MapOfScienceActivity;
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


	private Map<String, String> getSubjectPersonEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, String subjectEntityURI, String entityLabel, VisConstants.DataVisMode dataOuputFormat)
					throws MalformedQueryParametersException {

		RDFService rdfService = vitroRequest.getRDFService();

		Map<String, Set<String>> personToPublicationMap = cachedPersonToPublication.get(rdfService);
		Map<String, String> publicationToJournalMap = cachedPublicationToJournal.get(rdfService);

		if (!personToPublicationMap.containsKey(subjectEntityURI)) {
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		} else {
			JournalPublicationCounts journalCounts = new JournalPublicationCounts();

			for (String publication : personToPublicationMap.get(subjectEntityURI)) {
				journalCounts.increment(publicationToJournalMap.get(publication));
			}

			ScienceMappingResult result = getScienceMappingResult(journalCounts.map);

			Map<String, String> fileData = new HashMap<String, String>();
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				Gson json = new Gson();
				Set jsonContent = new HashSet();

				MapOfScience entityJson = new MapOfScience(subjectEntityURI);
				entityJson.setLabel("");
				entityJson.setType("PERSON");

				entityJson.setPubsWithNoJournals(journalCounts.noJournalCount);
				updateEntityMapOfScienceInformation(entityJson, result);

				jsonContent.add(entityJson);

				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, json.toJson(jsonContent));
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
	}

	private Set<String> addOrgAndAllSubOrgs(Set<String> allSubOrgs, String org, Map<String, Set<String>> subOrgMap) {
		if (allSubOrgs.add(org)) {
			if (subOrgMap.containsKey(org)) {
				for (String subOrg : subOrgMap.get(org)) {
					addOrgAndAllSubOrgs(allSubOrgs, subOrg, subOrgMap);
				}
			}
		}

		return allSubOrgs;
	}

	private Map<String, String> getSubjectEntityAndGenerateDataResponse(
			VitroRequest vitroRequest, String subjectEntityURI, String entityLabel, VisConstants.DataVisMode dataOuputFormat)
			throws MalformedQueryParametersException {

		RDFService rdfService = vitroRequest.getRDFService();

		Map<String, String> orgLabelMap = cachedOrganizationLabels.get(rdfService);

		if (orgLabelMap.get(subjectEntityURI) == null) {
			if (VisConstants.DataVisMode.JSON.equals(dataOuputFormat)) {
				return prepareStandaloneDataErrorResponse();
			} else {
				return prepareDataErrorResponse();
			}
		}

		Map<String, Set<String>> subOrgMap = cachedOrganizationSubOrgs.get(rdfService);
		Map<String, Set<String>> organisationToPeopleMap = cachedOrganisationToPeopleMap.get(rdfService);
		Map<String, Set<String>> personToPublicationMap = cachedPersonToPublication.get(rdfService);
		Map<String, String> publicationToJournalMap = cachedPublicationToJournal.get(rdfService);

		Set<String> orgPublications       = new HashSet<String>();
		Set<String> orgPublicationsPeople = new HashSet<String>();

		Map<String, Set<String>> subOrgPublicationsMap = new HashMap<String, Set<String>>();

		if (subOrgMap.containsKey(subjectEntityURI)) {
			for (String topSubOrg : subOrgMap.get(subjectEntityURI)) {
				Set<String> subOrgPublications       = new HashSet<String>();
				Set<String> subOrgPublicationsPeople = new HashSet<String>();

				Set<String> fullSubOrgs  = addOrgAndAllSubOrgs(new HashSet<String>(), topSubOrg, subOrgMap);

				for (String subOrg : fullSubOrgs) {
					Set<String> peopleInSubOrg = organisationToPeopleMap.get(subOrg);
					if (peopleInSubOrg != null) {
						for (String person : peopleInSubOrg) {
							if (personToPublicationMap.containsKey(person)) {
								if (subOrgPublicationsPeople.add(person)) {
									subOrgPublications.addAll(personToPublicationMap.get(person));

									if (orgPublicationsPeople.add(person)) {
										orgPublications.addAll(personToPublicationMap.get(person));
									}
								}
							}
						}
					}
				}

				subOrgPublicationsMap.put(topSubOrg, subOrgPublications);
			}
		}

		Set<String> people = organisationToPeopleMap.get(subjectEntityURI);
		if (people != null) {
			for (String person : people) {
				if (personToPublicationMap.containsKey(person)) {
					if (orgPublicationsPeople.add(person)) {
						orgPublications.addAll(personToPublicationMap.get(person));
					}
				}
			}
		}

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
				Gson json = new Gson();
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

				fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, "application/octet-stream");
				fileData.put(DataVisualizationController.FILE_CONTENT_KEY, json.toJson(jsonContent));
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
	public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

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
        	
        } else {
        	body.put("entityType", "ORGANIZATION");
        }
        
        body.put("vivoDefaultNamespace", vreq.getWebappDaoFactory().getDefaultNamespace());
        
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

	private static CachingRDFServiceExecutor<Map<String, String>> cachedOrganizationLabels =
			new CachingRDFServiceExecutor<>(
					new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
						@Override
						Map<String, String> callWithService(RDFService rdfService) throws Exception {
							String query = QueryConstants.getSparqlPrefixQuery() +
									"SELECT ?org ?orgLabel\n" +
									"WHERE\n" +
									"{\n" +
									"  ?org a foaf:Organization .\n" +
									"  ?org rdfs:label ?orgLabel .\n" +
									"}\n";

							Map<String, String> map = new HashMap<>();

							InputStream is = null;
							ResultSet rs = null;
							try {
								is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
								rs = ResultSetFactory.fromJSON(is);

								while (rs.hasNext()) {
									QuerySolution qs = rs.next();
									String org      = qs.getResource("org").getURI();
									String orgLabel = qs.getLiteral("orgLabel").getString();

									map.put(org, orgLabel);
								}
							} finally {
								silentlyClose(is);
							}

							return map;
						}
					}
			);

	private static CachingRDFServiceExecutor<Map<String, Set<String>>> cachedOrganizationSubOrgs =
			new CachingRDFServiceExecutor<>(
					new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>() {
						@Override
						Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
							String query = QueryConstants.getSparqlPrefixQuery() +
									"SELECT ?org ?subOrg\n" +
									"WHERE\n" +
									"{\n" +
									"  ?org a foaf:Organization .\n" +
									"  ?org <http://purl.obolibrary.org/obo/BFO_0000051> ?subOrg .\n" +
									"}\n";

							Map<String, Set<String>> map = new HashMap<>();

							InputStream is = null;
							ResultSet rs = null;
							try {
								is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
								rs = ResultSetFactory.fromJSON(is);

								while (rs.hasNext()) {
									QuerySolution qs = rs.next();
									String org    = qs.getResource("org").getURI();
									String subOrg = qs.getResource("subOrg").getURI();

									Set<String> subOrgs = map.get(org);
									if (subOrgs == null) {
										subOrgs = new HashSet<String>();
										subOrgs.add(subOrg);
										map.put(org, subOrgs);
									} else {
										subOrgs.add(subOrg);
									}
								}
							} finally {
								silentlyClose(is);
							}

							return map;
						}
					}
			);

	private static CachingRDFServiceExecutor<Map<String, Set<String>>> cachedOrganisationToPeopleMap =
			new CachingRDFServiceExecutor<Map<String, Set<String>>>(
					new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>() {
						@Override
						public Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
							String query = QueryConstants.getSparqlPrefixQuery() +
									"SELECT ?organisation ?person\n" +
									"WHERE\n" +
									"{\n" +
									"  ?organisation a foaf:Organization .\n" +
									"  ?organisation core:relatedBy ?position .\n" +
									"  ?position core:relates ?person .\n" +
									"  ?person a foaf:Person .\n" +
									"}\n";

							// TODO Critical section?

							Map<String, Set<String>> orgToPeopleMap = new HashMap<String, Set<String>>();

							InputStream is = null;
							ResultSet rs = null;
							try {
								is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
								rs = ResultSetFactory.fromJSON(is);

								while (rs.hasNext()) {
									QuerySolution qs = rs.next();
									String org    = qs.getResource("organisation").getURI();
									String person = qs.getResource("person").getURI();

									Set<String> people = orgToPeopleMap.get(org);
									if (people == null) {
										people = new HashSet<String>();
										people.add(person);
										orgToPeopleMap.put(org, people);
									} else {
										people.add(person);
									}
								}
							} finally {
								silentlyClose(is);
							}

							return orgToPeopleMap;
						}
					}
			);

	private static CachingRDFServiceExecutor<Map<String, Set<String>>> cachedPersonToPublication =
			new CachingRDFServiceExecutor<Map<String, Set<String>>>(
					new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>() {
						@Override
						public Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
							String query = QueryConstants.getSparqlPrefixQuery() +
									"SELECT ?person ?document\n" +
									"WHERE\n" +
									"{\n" +
									"  ?person a foaf:Person .\n" +
									"  ?person core:relatedBy ?authorship .\n" +
									"  ?authorship core:relates ?document .\n" +
									"  ?document a bibo:Document .\n" +
									"}\n";

							Map<String, Set<String>> map = new HashMap<String, Set<String>>();

							InputStream is = null;
							ResultSet rs = null;
							try {
								is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
								rs = ResultSetFactory.fromJSON(is);

								while (rs.hasNext()) {
									QuerySolution qs = rs.next();

									Resource person   = qs.getResource("person");
									Resource document = qs.getResource("document");

									if (person != null && document != null) {
										String personURI = person.getURI();

										Set<String> documents = map.get(personURI);
										if (documents == null) {
											documents = new HashSet<String>();
											documents.add(document.getURI());
											map.put(personURI, documents);
										} else {
											documents.add(document.getURI());
										}
									}
								}
							} finally {
								silentlyClose(is);
							}

							return map;
						}
					}
			);

	private static CachingRDFServiceExecutor<Map<String, String>> cachedPublicationToJournal =
			new CachingRDFServiceExecutor<>(
					new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
						@Override
						Map<String, String> callWithService(RDFService rdfService) throws Exception {
							String query = QueryConstants.getSparqlPrefixQuery() +
									"SELECT ?document ?journalLabel\n" +
									"WHERE\n" +
									"{\n" +
									"  ?document a bibo:Document .\n" +
									"  ?document core:hasPublicationVenue ?journal . \n" +
									"  ?journal rdfs:label ?journalLabel . \n" +
									"}\n";

							Map<String, String> map = new HashMap<>();

							InputStream is = null;
							ResultSet rs = null;
							try {
								is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
								rs = ResultSetFactory.fromJSON(is);

								while (rs.hasNext()) {
									QuerySolution qs = rs.next();
									String document      = qs.getResource("document").getURI();
									String journalLabel = qs.getLiteral("journalLabel").getString();

									map.put(document, journalLabel);
								}
							} finally {
								silentlyClose(is);
							}

							return map;
						}
					}
			);

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

	private static class CachingRDFServiceExecutor<T> {
		private T cachedResults;
		private long lastCacheTime;

		private RDFServiceCallable<T> resultBuilder;
		private FutureTask<T> backgroundTask = null;

		CachingRDFServiceExecutor(RDFServiceCallable<T> resultBuilder) {
			this.resultBuilder = resultBuilder;
		}

		synchronized T get(RDFService rdfService) {
			if (cachedResults != null) {
				if (!resultBuilder.invalidateCache(System.currentTimeMillis() - lastCacheTime)) {
					return cachedResults;
				}
			}

			try {
				if (backgroundTask == null) {
					resultBuilder.setRDFService(rdfService);
					backgroundTask = new FutureTask<T>(resultBuilder);

					Thread thread = new Thread(backgroundTask);
					thread.setDaemon(true);
					thread.start();

					if (cachedResults == null || resultBuilder.executionTime < 2000) {
						completeBackgroundTask();
					}
				} else if (backgroundTask.isDone()) {
					completeBackgroundTask();
				}
			} catch (InterruptedException e) {
				abortBackgroundTask();
			} catch (ExecutionException e) {
				abortBackgroundTask();
				throw new RuntimeException("Background RDF thread through an exception", e.getCause());
			}

			return cachedResults;
		}

		private void abortBackgroundTask() {
			if (backgroundTask != null) {
				backgroundTask.cancel(true);
				backgroundTask = null;
			}
		}

		private void completeBackgroundTask() throws InterruptedException, ExecutionException {
			if (backgroundTask != null) {
				cachedResults = backgroundTask.get();
				lastCacheTime = System.currentTimeMillis();
				backgroundTask = null;
			}
		}
		static abstract class RDFServiceCallable<T> implements Callable<T> {
			private RDFService rdfService;
			private long executionTime = -1;

			final void setRDFService(RDFService rdfService) {
				this.rdfService = rdfService;
			}

			@Override
			final public T call() throws Exception {
				long start = System.currentTimeMillis();
				T val = callWithService(rdfService);
				executionTime = System.currentTimeMillis() - start;
				return val;
			}

			abstract T callWithService(RDFService rdfService) throws Exception;

			boolean invalidateCache(long timeCached) {
				if (executionTime > -1) {
					/*
						Determine validity as a function of the time it takes to execute the query.

						Query exec time  | Keep cache for
						-----------------+-----------------
						10 seconds       | 20 minutes
						30 seconds       | 1 hour
						1 minute         | 2 hours
						5 minutes        | 10 hours


						Multiplier of the last execution time is 120.

						At most, keep a cache for one day (24 * 60 * 60 * 1000 = 86400000)
					 */

					return timeCached > Math.min(executionTime * 120, 86400000);
				}
				return false;
			}
		}
	}

	private static void silentlyClose(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Throwable t) {

		}
	}
}
