/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 *
 * This request handler is used to serve the content related to an individual's
 * publications over the years like, 1. Sprakline representing this 2. An entire
 * page dedicated to the sparkline vis which will also have links to download
 * the data using which the sparkline was rendered & its tabular representation
 * etc. 3. Downloadable CSV file containing number of publications over the
 * years. 4. Downloadable PDf file containing the publications content, among
 * other things. Currently this is disabled because the feature is half-baked.
 * We plan to activate this in the next major release.
 *
 * @author cdtank
 */
public class PersonPublicationCountRequestHandler implements
VisualizationRequestHandler {

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {

		String personURI = vitroRequest
								.getParameter(
										VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		String visMode = vitroRequest
								.getParameter(
										VisualizationFrameworkConstants.VIS_MODE_KEY);

		String visContainer = vitroRequest
								.getParameter(
										VisualizationFrameworkConstants.VIS_CONTAINER_KEY);

        /* tlw72 -- Added in 1.6 for multi-view support. There are now two different "sparkline" templates     */
        /* and the one that gets loaded depends on which foaf person template is being used by the app. The    */
        /* personPublicationCountDynamicActivator.ftl template needs to know which is the requesting template. */
        String requestingTemplate = vitroRequest
            					.getParameter(
										VisualizationFrameworkConstants.REQUESTING_TEMPLATE_KEY);

		QueryRunner<Set<Activity>> queryManager = new PersonPublicationCountQueryRunner(
															personURI,
															vitroRequest.getRDFService(),
															log);

		Set<Activity> authorDocuments = queryManager.getQueryResult();

		/*
		 * Create a map from the year to number of publications. Use the
		 * BiboDocument's parsedPublicationYear to populate the data.
		 */
		Map<String, Integer> yearToPublicationCount =
				UtilityFunctions.getYearToActivityCount(authorDocuments);

		boolean shouldVIVOrenderVis = false;

		if (yearToPublicationCount.containsKey("Unknown")) {
			if (yearToPublicationCount.size() > 1) {
				shouldVIVOrenderVis = true;
			}
		} else {
			if (yearToPublicationCount.size() > 0) {
				shouldVIVOrenderVis = true;
			}
		}

		/*
		 * Computations required to generate HTML for the sparkline & related
		 * context.
		 */
		PersonPublicationCountVisCodeGenerator visualizationCodeGenerator =
				new PersonPublicationCountVisCodeGenerator(
						personURI,
						visMode,
						visContainer,
						yearToPublicationCount,
						log);

		SparklineData sparklineData = visualizationCodeGenerator.getValueObjectContainer();

		return prepareDynamicResponse(vitroRequest, sparklineData,
				shouldVIVOrenderVis, requestingTemplate);

	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Person Publication Count Visualization does not provide "
					+ "Short URL Response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {

		String personURI = vitroRequest
		.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		QueryRunner<Set<Activity>> queryManager = new PersonPublicationCountQueryRunner(
																personURI,
																vitroRequest.getRDFService(),
																log);

		Set<Activity> authorDocuments = queryManager.getQueryResult();

		/*
		 * Create a map from the year to number of publications. Use the
		 * BiboDocument's parsedPublicationYear to populate the data.
		 */
		Map<String, Integer> yearToPublicationCount =
				UtilityFunctions.getYearToActivityCount(authorDocuments);

		String authorName = ((PersonPublicationCountQueryRunner) queryManager).getAuthorName();


		return prepareDataResponse(authorName,
								   yearToPublicationCount);

	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
		throws MalformedQueryParametersException {

		String personURI = vitroRequest.getParameter(
									VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		String visMode = vitroRequest.getParameter(
									VisualizationFrameworkConstants.VIS_MODE_KEY);

		String visContainer = vitroRequest.getParameter(
									VisualizationFrameworkConstants.VIS_CONTAINER_KEY);

		QueryRunner<Set<Activity>> queryManager = new PersonPublicationCountQueryRunner(
																personURI,
																vitroRequest.getRDFService(),
																log);

		Set<Activity> authorDocuments = queryManager.getQueryResult();

		/*
		 * Create a map from the year to number of publications. Use the
		 * BiboDocument's parsedPublicationYear to populate the data.
		 */
		Map<String, Integer> yearToPublicationCount =
				UtilityFunctions.getYearToActivityCount(authorDocuments);

		/*
		 * Computations required to generate HTML for the sparkline & related
		 * context.
		 */
		PersonPublicationCountVisCodeGenerator visualizationCodeGenerator =
				new PersonPublicationCountVisCodeGenerator(
						personURI,
						visMode,
						visContainer,
						yearToPublicationCount,
						log);

		SparklineData sparklineData =
				visualizationCodeGenerator.getValueObjectContainer();

		return prepareStandaloneResponse(vitroRequest, sparklineData);
	}

	private String getPublicationsOverTimeCSVContent(
			Map<String, Integer> yearToPublicationCount) {

		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Year, Publications\n");

		for (Entry<String, Integer> currentEntry : yearToPublicationCount
				.entrySet()) {
			csvFileContent.append(StringEscapeUtils.escapeCsv(currentEntry
					.getKey()));
			csvFileContent.append(",");
			csvFileContent.append(currentEntry.getValue());
			csvFileContent.append("\n");
		}

		return csvFileContent.toString();
	}

	/**
	 * Provides response when csv file containing the publication count over the
	 * years is requested.
	 *
	 * @param authorName Name of author
	 * @param yearToPublicationCount Year / publication counts
	 */
	private Map<String, String> prepareDataResponse(String authorName,
			Map<String, Integer> yearToPublicationCount) {

		/*
		 * To make sure that null/empty records for author names do not cause
		 * any mischief.
		 */
		if (StringUtils.isBlank(authorName)) {
			authorName = "no-author";
		}

		String outputFileName = UtilityFunctions.slugify(authorName)
									+ "_publications-per-year" + ".csv";

		Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY,
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 getPublicationsOverTimeCSVContent(yearToPublicationCount));

		return fileData;
	}

	/**
	 * Provides response when an entire page dedicated to publication sparkline
	 * is requested.
	 *
	 * @param vreq Vitro Request
	 * @param valueObjectContainer Sparkline Data
	 */
	private TemplateResponseValues prepareStandaloneResponse(VitroRequest vreq,
			SparklineData valueObjectContainer) {

		String standaloneTemplate = "personPublicationCountStandaloneActivator.ftl";

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("title", "Individual Publication Count visualization");
		body.put("sparklineVO", valueObjectContainer);

		return new TemplateResponseValues(standaloneTemplate, body);

	}

	/**
	 * Provides response when the publication sparkline has to be rendered in
	 * already existing page, e.g. profile page.
	 *
	 * @param vreq Vitro Request
	 * @param valueObjectContainer Sparkline data
	 * @param shouldVIVOrenderVis Flag to render visualization
	 * @param requestingTemplate Requesting template name
	 */
	private TemplateResponseValues prepareDynamicResponse(VitroRequest vreq,
			SparklineData valueObjectContainer, boolean shouldVIVOrenderVis, String requestingTemplate) {

		String dynamicTemplate = "personPublicationCountDynamicActivator.ftl";

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("sparklineVO", valueObjectContainer);
		body.put("shouldVIVOrenderVis", shouldVIVOrenderVis);
		body.put("requestingTemplate", requestingTemplate); /* tlw72 -- Added in 1.6 for multi-view support.*/

		return new TemplateResponseValues(dynamicTemplate, body);

	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}
}
