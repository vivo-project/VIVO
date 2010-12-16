/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.persongrantcount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
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
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Grant;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.PDFDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;


/**
 * 
 * This request handler is used to serve the content related to an individual's
 * grants over the years like,
 * 		1. Sparkline representing this
 * 		2. An entire page dedicated to the sparkline vis which will also have links to
 * download the data using which the sparkline was rendered & its tabular representation etc.
 * 		3. Downloadable CSV file containing number of grants over the years.
 * 		4. Downloadable PDf file containing the grant content, among other things. 
 * Currently this is disabled because the feature is half-baked. We plan to activate this in 
 * the next major release.  
 * 
 * @author bkoniden
 * Deepak Konidena
 */
public class PersonGrantCountRequestHandler implements VisualizationRequestHandler {
	
	public ResponseValues generateVisualization(VitroRequest vitroRequest,
			Log log, DataSource dataSource) {
		
		String personURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		String renderMode = vitroRequest
				.getParameter(VisualizationFrameworkConstants.RENDER_MODE_KEY);

		String visMode = vitroRequest
				.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);

		String visContainer = vitroRequest
				.getParameter(VisualizationFrameworkConstants.VIS_CONTAINER_KEY);

	QueryRunner<Set<Grant>> queryManager = new PersonGrantCountQueryRunner(personURI, dataSource, log );
	
	try{
		Set<Grant> piGrants = queryManager.getQueryResult();
		
    	/*
    	 * Create a map from the year to number of grants. Use the Grant's
    	 * parsedGrantYear to populate the data.
    	 * */
    	Map<String, Integer> yearToGrantCount = 
			UtilityFunctions.getYearToGrantCount(piGrants);
	
    	Individual investigator = ((PersonGrantCountQueryRunner) queryManager).getPrincipalInvestigator();

    	if (VisualizationFrameworkConstants.DATA_RENDER_MODE
    				.equalsIgnoreCase(renderMode)) {
    		
    		
    		/*
			return prepareDataResponse(investigator,
					piGrants,
					yearToGrantCount);
					*/
		}
    	
    	/*
    	 * For now we are disabling the capability to render pdf file.
    	 * */
    	/*
    	if (VisualizationFrameworkConstants.PDF_RENDER_MODE
    				.equalsIgnoreCase(renderMode)) {
    		
			preparePDFResponse(investigator,
												 piGrants,
												 yearToGrantCount,
												 response);
			return;
		}
    	*/
    	
    	/*
    	 * Computations required to generate HTML for the sparkline & related context.
    	 * */
    	PersonGrantCountVisCodeGenerator visualizationCodeGenerator = 
    		new PersonGrantCountVisCodeGenerator(vitroRequest.getContextPath(),
    									   personURI,
    									   visMode,
    									   visContainer,
    									   piGrants,
    									   yearToGrantCount, 
    									   log);
    	
    	SparklineData sparklineData = visualizationCodeGenerator
											.getValueObjectContainer();
    	
    	/*
    	 * This is side-effecting because the response of this method is just to redirect to
    	 * a page with visualization on it.
    	 * */
    	RequestDispatcher requestDispatcher = null;
    	
		if (VisualizationFrameworkConstants.DYNAMIC_RENDER_MODE
				.equalsIgnoreCase(renderMode)) {

		return prepareDynamicResponse(vitroRequest, 
							   		  sparklineData, 
							   		  yearToGrantCount);
		
		} else {
			return prepareStandaloneResponse(vitroRequest, 
    							  sparklineData);
		}
	} catch (MalformedQueryParametersException e) {
		return UtilityFunctions.handleMalformedParameters(
				"Visualization Query Error - Individual Grant Count",
				e.getMessage(), 
				vitroRequest);
	}
  }
	
	private String getGrantsOverTimeCSVContent(Map<String, Integer> yearToGrantCount) {
		
		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Year, Grants\n");

		for (Entry<String, Integer> currentEntry : yearToGrantCount.entrySet()) {
			csvFileContent.append(StringEscapeUtils.escapeCsv(currentEntry.getKey()));
			csvFileContent.append(",");
			csvFileContent.append(currentEntry.getValue());
			csvFileContent.append("\n");
		}

		return csvFileContent.toString();
	}
	
	/**
	 * Provides response when csv file containing the grant count over the years
	 * is requested.
	 * @param investigator
	 * @param piGrants
	 * @param yearToGrantCount
	 * @return 
	 */
	private Map<String, String> prepareDataResponse(
						Individual investigator,
						Set<Grant> piGrants,
						Map<String, Integer> yearToGrantCount) {
		
		
		String piName = null; 
		
		/*
		* To protect against cases where there are no PI grants associated with the
		* individual. 
		* */
		if (piGrants.size() > 0) {
		piName = investigator.getIndividualLabel();
		}
		
		/*
		* To make sure that null/empty records for PI names do not cause any mischief.
		* */
		if (StringUtils.isBlank(piName)) {
		piName = "no-principal-investigator";
		}
		
		String outputFileName = UtilityFunctions.slugify(piName) 
										+ "_grants-per-year" + ".csv";

		
        Map<String, Object> fileContents = new HashMap<String, Object>();
        fileContents.put("fileContent", getGrantsOverTimeCSVContent(yearToGrantCount));
		
//		return new FileResponseValues(new ContentType(), outputFileName, fileContents);
		
		return new HashMap<String, String>();
	}
	
	/**
	 * Provides response when an entire page dedicated to grant sparkline is requested.
	 * @param vreq
	 * @param valueObjectContainer
	 * @return 
	 */
	private TemplateResponseValues prepareStandaloneResponse(VitroRequest vreq,
			SparklineData valueObjectContainer) {

        Portal portal = vreq.getPortal();
        
        String standaloneTemplate = "/visualization/grantCount.ftl";

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("portalBean", portal);
        body.put("title", "Individual Grant Count visualization");
        body.put("sparklineVO", valueObjectContainer);

        /*
         * DO NOT DO THIS HERE. Set stylesheets/scripts in the *.ftl instead using $(scripts.add)
         * */
//        body.put("scripts", "/templates/visualization/visualization_scripts.jsp");
        
        return new TemplateResponseValues(standaloneTemplate, body);
        
	}
	
	/**
	 * Provides response when the grant sparkline has to be rendered in already existing 
	 * page, e.g. profile page.
	 * @param vreq
	 * @param valueObjectContainer
	 * @param yearToGrantCount
	 * @return 
	 */
	private TemplateResponseValues prepareDynamicResponse(
			VitroRequest vreq,
			SparklineData valueObjectContainer, 
			Map<String, Integer> yearToGrantCount) {

        Portal portal = vreq.getPortal();


        String dynamicTemplate = "/visualization/sparklineAjaxVisContent.ftl";

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("portalBean", portal);
        body.put("sparklineVO", valueObjectContainer);

        /*
         * DO NOT DO THIS HERE. Set stylesheets/scripts in the *.ftl instead using $(scripts.add)
         * */
//        body.put("scripts", "/templates/visualization/visualization_scripts.jsp");
        
        if (yearToGrantCount.size() > 0) {
        	body.put("shouldVIVOrenderVis", true);
        } else {
        	body.put("shouldVIVOrenderVis", false);
        }
        
        return new TemplateResponseValues(dynamicTemplate, body);
        
	}
	
	
	private void preparePDFResponse(Individual investigator,
			Set<Grant> piGrants,
			Map<String, Integer> yearToGrantCount,
			HttpServletResponse response) {

		String piName = null;

		// To protect against cases where there are no PI grants
		// associated with the
		// / individual.
		if (piGrants.size() > 0) {
			piName = investigator.getIndividualLabel();
		}

		// To make sure that null/empty records for PI names do not cause
		// any mischief.
		if (StringUtils.isBlank(piName)) {
			piName = "no-principal-investigator";
		}

		String outputFileName = UtilityFunctions.slugify(piName)
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

			PDFDocument pdfDocument = new PDFDocument(piName,
					yearToGrantCount, document, pdfWriter);

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

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			DataSource dataSource) throws MalformedQueryParametersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {
		// TODO Auto-generated method stub
		return null;
	}
}