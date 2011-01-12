/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.personpubcount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.PDFDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;

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
			DataSource dataSource) throws MalformedQueryParametersException {

		String personURI = vitroRequest
								.getParameter(
										VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		String visMode = vitroRequest
								.getParameter(
										VisualizationFrameworkConstants.VIS_MODE_KEY);

		String visContainer = vitroRequest
								.getParameter(
										VisualizationFrameworkConstants.VIS_CONTAINER_KEY);

		QueryRunner<Set<BiboDocument>> queryManager = new PersonPublicationCountQueryRunner(
															personURI, 
															dataSource, 
															log);

		Set<BiboDocument> authorDocuments = queryManager.getQueryResult();

		/*
		 * Create a map from the year to number of publications. Use the
		 * BiboDocument's parsedPublicationYear to populate the data.
		 */
		Map<String, Integer> yearToPublicationCount = 
				UtilityFunctions.getYearToPublicationCount(authorDocuments);

		boolean shouldVIVOrenderVis = 
				yearToPublicationCount.size() > 0 ? true : false;

		/*
		 * Computations required to generate HTML for the sparkline & related
		 * context.
		 */
		PersonPublicationCountVisCodeGenerator visualizationCodeGenerator = 
				new PersonPublicationCountVisCodeGenerator(
						personURI, 
						visMode, 
						visContainer, 
						authorDocuments,
						yearToPublicationCount, 
						log);

		SparklineData sparklineData = visualizationCodeGenerator
		.getValueObjectContainer();

		return prepareDynamicResponse(vitroRequest, sparklineData,
				shouldVIVOrenderVis);

	}

	@Override
	public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log,
			DataSource dataSource) throws MalformedQueryParametersException {

		String personURI = vitroRequest
		.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		QueryRunner<Set<BiboDocument>> queryManager = new PersonPublicationCountQueryRunner(
																personURI, 
																dataSource, 
																log);

		Set<BiboDocument> authorDocuments = queryManager.getQueryResult();

		/*
		 * Create a map from the year to number of publications. Use the
		 * BiboDocument's parsedPublicationYear to populate the data.
		 */
		Map<String, Integer> yearToPublicationCount = 
				UtilityFunctions.getYearToPublicationCount(authorDocuments);

		Individual author = ((PersonPublicationCountQueryRunner) queryManager).getAuthor();

		return prepareDataResponse(author, 
								   authorDocuments,
								   yearToPublicationCount);

	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
		throws MalformedQueryParametersException {

		String personURI = vitroRequest.getParameter(
									VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		String visMode = vitroRequest.getParameter(
									VisualizationFrameworkConstants.VIS_MODE_KEY);

		String visContainer = vitroRequest.getParameter(
									VisualizationFrameworkConstants.VIS_CONTAINER_KEY);

		QueryRunner<Set<BiboDocument>> queryManager = new PersonPublicationCountQueryRunner(
																personURI, 
																dataSource, 
																log);

		Set<BiboDocument> authorDocuments = queryManager.getQueryResult();

		/*
		 * Create a map from the year to number of publications. Use the
		 * BiboDocument's parsedPublicationYear to populate the data.
		 */
		Map<String, Integer> yearToPublicationCount = 
				UtilityFunctions.getYearToPublicationCount(authorDocuments);

		/*
		 * Computations required to generate HTML for the sparkline & related
		 * context.
		 */
		PersonPublicationCountVisCodeGenerator visualizationCodeGenerator = 
				new PersonPublicationCountVisCodeGenerator(
						personURI, 
						visMode, 
						visContainer, 
						authorDocuments,
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
	 * @param author
	 * @param authorDocuments
	 * @param yearToPublicationCount
	 * @return
	 */
	private Map<String, String> prepareDataResponse(Individual author,
			Set<BiboDocument> authorDocuments,
			Map<String, Integer> yearToPublicationCount) {

		String authorName = null;

		/*
		 * To protect against cases where there are no author documents
		 * associated with the individual.
		 */
		if (authorDocuments.size() > 0) {
			authorName = author.getIndividualLabel();
		}

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
	 * @param vreq
	 * @param valueObjectContainer
	 * @return
	 */
	private TemplateResponseValues prepareStandaloneResponse(VitroRequest vreq,
			SparklineData valueObjectContainer) {

		Portal portal = vreq.getPortal();

		String standaloneTemplate = "personPublicationCountStandaloneActivator.ftl";

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("portalBean", portal);
		body.put("title", "Individual Publication Count visualization");
		body.put("sparklineVO", valueObjectContainer);

		return new TemplateResponseValues(standaloneTemplate, body);

	}

	/**
	 * Provides response when the publication sparkline has to be rendered in
	 * already existing page, e.g. profile page.
	 * 
	 * @param vreq
	 * @param valueObjectContainer
	 * @param yearToPublicationCount
	 * @return
	 */
	private TemplateResponseValues prepareDynamicResponse(VitroRequest vreq,
			SparklineData valueObjectContainer, boolean shouldVIVOrenderVis) {

		Portal portal = vreq.getPortal();

//		String dynamicTemplate = "/visualization/publication/personPublicationCountDynamicActivator.ftl";
		String dynamicTemplate = "personPublicationCountDynamicActivator.ftl";

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("portalBean", portal);
		body.put("sparklineVO", valueObjectContainer);
		body.put("shouldVIVOrenderVis", shouldVIVOrenderVis);

		return new TemplateResponseValues(dynamicTemplate, body);

	}

	private void preparePDFResponse(Individual author,
			Set<BiboDocument> authorDocuments,
			Map<String, Integer> yearToPublicationCount,
			HttpServletResponse response) {

		String authorName = null;

		// To protect against cases where there are no author documents
		// associated with the
		// / individual.
		if (authorDocuments.size() > 0) {
			authorName = author.getIndividualLabel();
		}

		// To make sure that null/empty records for author names do not cause
		// any mischief.
		if (StringUtils.isBlank(authorName)) {
			authorName = "no-author";
		}

		String outputFileName = UtilityFunctions.slugify(authorName)
		+ "_report" + ".pdf";

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ outputFileName);

		ServletOutputStream responseOutputStream;
		try {
			responseOutputStream = response.getOutputStream();

			Document document = new Document();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
			document.open();

			PDFDocument pdfDocument = new PDFDocument(authorName,
					yearToPublicationCount, document, pdfWriter);

			document.close();

			// setting some response headers & content type
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control",
			"must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentLength(baos.size());
			// write ByteArrayOutputStream to the ServletOutputStream
			baos.writeTo(responseOutputStream);
			responseOutputStream.flush();
			responseOutputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
