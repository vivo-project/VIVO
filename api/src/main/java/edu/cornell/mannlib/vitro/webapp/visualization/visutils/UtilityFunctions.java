/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;

public class UtilityFunctions {

	public static Map<String, Integer> getYearToActivityCount(
			Set<Activity> activities) {

    	/*
    	 * Create a map from the year to number of publications. Use the BiboDocument's
    	 * or Grant's parsedPublicationYear or parsedGrantYear to populate the data passed
    	 * via Activity's getParsedActivityYear.
    	 * */
    	Map<String, Integer> yearToActivityCount = new TreeMap<String, Integer>();

    	for (Activity currentActivity : activities) {

    		/*
    		 * Increment the count because there is an entry already available for
    		 * that particular year.
    		 * */
    		String activityYear = currentActivity.getParsedActivityYear();

			if (yearToActivityCount.containsKey(activityYear)) {
    			yearToActivityCount.put(activityYear,
    									   yearToActivityCount
    									   		.get(activityYear) + 1);

    		} else {
    			yearToActivityCount.put(activityYear, 1);
    		}

    	}

		return yearToActivityCount;
	}

	public static Map<String, Integer> getYearToActivityCount(
			Collection<Activity> activities) {
		return getYearToActivityCount(new HashSet<Activity>(activities));
	}

	/**
	 * This method is used to return a mapping between activity year & all the collaborators
	 * that published with ego in that year.
	 * @param collaborationData Collaboration data
	 */
	public static Map<String, Set<Collaborator>> getActivityYearToCollaborators(
										CollaborationData collaborationData) {

		Map<String, Set<Collaborator>> yearToCollaborators = new TreeMap<String,
																		 Set<Collaborator>>();

		Collaborator egoCollaborator = collaborationData.getEgoCollaborator();

		for (Collaborator currNode : collaborationData.getCollaborators()) {

				/*
				 * We have already printed the Ego Node info.
				 * */
				if (currNode != egoCollaborator) {

					for (String year : currNode.getYearToActivityCount().keySet()) {

						Set<Collaborator> collaboratorNodes;

						if (yearToCollaborators.containsKey(year)) {

							collaboratorNodes = yearToCollaborators.get(year);
							collaboratorNodes.add(currNode);

						} else {

							collaboratorNodes = new HashSet<Collaborator>();
							collaboratorNodes.add(currNode);
							yearToCollaborators.put(year, collaboratorNodes);
						}

					}

				}
		}
		return yearToCollaborators;
	}

	/**
	 * Currently the approach for slugifying filenames is naive. In future if there is need,
	 * we can write more sophisticated method.
	 * @param textToBeSlugified Text to process
	 */
	public static String slugify(String textToBeSlugified) {
		String textBlockSeparator = "-";
		return StringUtils.removeEnd(StringUtils.substring(textToBeSlugified.toLowerCase().trim()
											.replaceAll("[^a-zA-Z0-9-]+", textBlockSeparator),
											0,
											VisConstants.MAX_NAME_TEXT_LENGTH),
									 textBlockSeparator);
	}


    public static ResponseValues handleMalformedParameters(String errorPageTitle,
    													   String errorMessage,
    													   VitroRequest vitroRequest) {

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("error", errorMessage);
        body.put("title", errorPageTitle);

        return new TemplateResponseValues(VisualizationFrameworkConstants.ERROR_TEMPLATE, body);
    }

    public static void handleMalformedParameters(String errorMessage,
    											 HttpServletResponse response,
												 Log log)
		throws IOException {

		GenericQueryMap errorDataResponse = new GenericQueryMap();
		errorDataResponse.addEntry("error", errorMessage);

		ObjectMapper mapper = new ObjectMapper();

    	response.setContentType("application/octet-stream");
    	mapper.writeValue(response.getWriter(), errorDataResponse);
	}

	public static DateTime getValidParsedDateTimeObject(String unparsedDateTime) {

		for (DateTimeFormatter currentFormatter : VOConstants.POSSIBLE_DATE_TIME_FORMATTERS) {

			try {

				DateTime dateTime = currentFormatter.parseDateTime(unparsedDateTime);
				return dateTime;

			} catch (Exception e2) {
				/*
				 * The current date-time formatter did not pass the muster.
				 * */
			}
		}

		/*
		 * This means that none of the date time formatters worked.
		 * */
		return null;
	}

	/**
	 * This method will be called to get the inferred end year for the entity.
	 * The 2 choices, in order, are,
	 * 		1. parsed year from core:DateTime object saved in core:dateTimeValue
	 * 		2. Default Entity Year
	 */
	public static String getValidYearFromCoreDateTimeString(String inputDate,
															String defaultYearInCaseOfError) {
		/*
		 * Always return default year identifier in case of an illegal parsed year.
		 * */
		String parsedInputYear = defaultYearInCaseOfError;

		if (inputDate != null) {

			DateTime validParsedDateTimeObject = UtilityFunctions
					.getValidParsedDateTimeObject(inputDate);

			if (validParsedDateTimeObject != null) {
				return String.valueOf(validParsedDateTimeObject.getYear());
			}
		}

		return parsedInputYear;
	}

	public static String getCSVDownloadURL(String individualURI, String visType, String visMode) {

		ParamMap csvDownloadURLParams = null;

		if (StringUtils.isBlank(visMode)) {

			csvDownloadURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
					 individualURI,
					 VisualizationFrameworkConstants.VIS_TYPE_KEY,
					 visType);

		} else {

			csvDownloadURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
					 individualURI,
					 VisualizationFrameworkConstants.VIS_TYPE_KEY,
					 visType,
					 VisualizationFrameworkConstants.VIS_MODE_KEY,
					 visMode);

		}

		String csvDownloadLink = UrlBuilder.getUrl(
										VisualizationFrameworkConstants
												.DATA_VISUALIZATION_SERVICE_URL_PREFIX,
										csvDownloadURLParams);

		return csvDownloadLink != null ? csvDownloadLink : "" ;

	}

	public static String getCollaboratorshipNetworkLink(String individualURI,
														String visType,
														String visMode) {

		ParamMap collaboratorshipNetworkURLParams = new ParamMap(
					VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
					individualURI,
					VisualizationFrameworkConstants.VIS_TYPE_KEY,
					visType,
					VisualizationFrameworkConstants.VIS_MODE_KEY,
					visMode);

		String collaboratorshipNetworkURL = UrlBuilder.getUrl(
					VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
					collaboratorshipNetworkURLParams);

		return collaboratorshipNetworkURL != null ? collaboratorshipNetworkURL : "" ;
	}

	public static boolean isEntityAPerson(VitroRequest vreq, String individualURI)
		throws MalformedQueryParametersException {
		Individual individualByURI = vreq.getWebappDaoFactory()
						.getIndividualDao()
						.getIndividualByURI(individualURI);

		if (individualByURI != null) {

			return individualByURI
						.isVClass("http://xmlns.com/foaf/0.1/Person");
		} else {
			throw new MalformedQueryParametersException("Individual with " + individualURI + " not found in the system.");
		}

	}


	/**
	 *
	 * This method will test whether the current uri is based off of default namespace. If so,
	 * go ahead & provide local name.
	 * @param givenURI URI
	 * @param vitroRequest Vitro Request
	 */
	public static String getIndividualLocalName(String givenURI, VitroRequest vitroRequest) {

		if (UrlBuilder.isUriInDefaultNamespace(givenURI, vitroRequest)) {

			try {

				Individual individual = vitroRequest.getWebappDaoFactory().getIndividualDao()
												.getIndividualByURI(givenURI);

				return individual.getLocalName();

			} catch (Exception e) {

			}
		}

		return "";
	}

	public static String getIndividualLabelFromDAO(VitroRequest vitroRequest,
												   String entityURI) {

		IndividualDao iDao = vitroRequest.getWebappDaoFactory().getIndividualDao();
        Individual ind = iDao.getIndividualByURI(entityURI);

        String individualLabel = "Unknown Individual";

        if (ind != null) {
        	individualLabel = ind.getName();
        }
		return individualLabel;
	}
}
