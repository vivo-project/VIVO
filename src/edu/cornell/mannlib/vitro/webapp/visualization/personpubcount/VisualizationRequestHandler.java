/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.skife.csv.CSVWriter;
import org.skife.csv.SimpleWriter;

import com.hp.hpl.jena.query.DataSource;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.PDFDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineVOContainer;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;

public class VisualizationRequestHandler {
	
	private VitroRequest vitroRequest;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Log log;


	
	public VisualizationRequestHandler(VitroRequest vitroRequest,
			HttpServletRequest request, HttpServletResponse response, Log log) {

		this.vitroRequest = vitroRequest;
		this.request = request;
		this.response = response;
		this.log = log;

	}

	public void generateVisualization(DataSource dataSource) {

		String resultFormatParam = "RS_TEXT";
        String rdfResultFormatParam = "RDF/XML-ABBREV";

        String individualURIParam = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_URL_HANDLE);

        String renderMode = vitroRequest.getParameter(VisualizationFrameworkConstants.RENDER_MODE_URL_HANDLE);
        
        String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_URL_HANDLE);

        String visContainer = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_CONTAINER_URL_HANDLE);

        QueryHandler queryManager =
        	new QueryHandler(individualURIParam,
        										   resultFormatParam,
        										   rdfResultFormatParam,
        										   dataSource,
        										   log);

		try {
			List<BiboDocument> authorDocuments = queryManager.getVisualizationJavaValueObjects();

	    	/*
	    	 * Create a map from the year to number of publications. Use the BiboDocument's
	    	 * parsedPublicationYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToPublicationCount =
	    		queryManager.getYearToPublicationCount(authorDocuments);
	    	
	    	/*
	    	 * In order to avoid unneeded computations we have pushed this "if" condition up.
	    	 * This case arises when the render mode is data. In that case we dont want to generate 
	    	 * HTML code to render sparkline, tables etc. Ideally I would want to avoid this flow.
	    	 * It is ugly! 
	    	 * */
	    	if (VisualizationFrameworkConstants.DATA_RENDER_MODE_URL_VALUE.equalsIgnoreCase(renderMode)) { 
				prepareVisualizationQueryDataResponse(queryManager.getAuthor(),
													  authorDocuments,
													  yearToPublicationCount);
				return;
			}
	    	
	    	
	    	if (VisualizationFrameworkConstants.PDF_RENDER_MODE_URL_VALUE.equalsIgnoreCase(renderMode)) { 
				prepareVisualizationQueryPDFResponse(queryManager.getAuthor(),
													 authorDocuments,
													 yearToPublicationCount);
				return;
			}
	    	
	    	/*
	    	 * Computations required to generate HTML for the sparklines & related context.
	    	 * */
	    	
	    	SparklineVOContainer valueObjectContainer = new SparklineVOContainer();

	    	VisualizationCodeGenerator visualizationCodeGenerator = 
	    		new VisualizationCodeGenerator(vitroRequest.getContextPath(),
	    									   individualURIParam,
	    									   visMode,
	    									   visContainer,
	    									   authorDocuments,
	    									   yearToPublicationCount, 
	    									   valueObjectContainer, 
	    									   log);
	    	
	    	
	    	/*
	    	 * This is side-effecting because the response of this method is just to redirect to
	    	 * a page with visualization on it.
	    	 * */
			RequestDispatcher requestDispatcher = null;

			if (VisualizationFrameworkConstants.DYNAMIC_RENDER_MODE_URL_VALUE.equalsIgnoreCase(renderMode)) {

				prepareVisualizationQueryDynamicResponse(request, response, vitroRequest,
						valueObjectContainer, yearToPublicationCount);
		    	requestDispatcher = request.getRequestDispatcher("/templates/page/blankPage.jsp");

			} else {
		    	prepareVisualizationQueryStandaloneResponse(request, response, vitroRequest,
		    			valueObjectContainer);

		    	requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
			}

	    	try {
	            requestDispatcher.forward(request, response);
	        } catch (Exception e) {
	            log.error("EntityEditController could not forward to view.");
	            log.error(e.getMessage());
	            log.error(e.getStackTrace());
	        }

		} catch (MalformedQueryParametersException e) {
			try {
				handleMalformedParameters(e.getMessage());
			} catch (ServletException e1) {
				log.error(e1.getStackTrace());
			} catch (IOException e1) {
				log.error(e1.getStackTrace());
			}
			return;
		}

	}

	private void prepareVisualizationQueryPDFResponse(Individual author, List<BiboDocument> authorDocuments,
													   Map<String, Integer> yearToPublicationCount) {
		
		String authorName = null; 
		
		/*
		 * To protect against cases where there are no author documents associated with the
		 * individual. 
		 * */
		if (authorDocuments.size() > 0) {
			authorName = author.getIndividualLabel();
		}
		
		/*
		 * To make sure that null/empty records for author names do not cause any mischief.
		 * */
		if (authorName == null) {
			authorName = "";
		}
		
		String outputFileName = UtilityFunctions.slugify(authorName) + "report" + ".pdf";
		
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
 
			ServletOutputStream responseOutputStream;
			try {
				responseOutputStream = response.getOutputStream();
				
				
				Document document = new Document();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
				document.open();
				
				PDFDocument pdfDocument = new PDFDocument(authorName, yearToPublicationCount, document, pdfWriter);
				
				document.close();

				// setting some response headers & content type
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
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

	private void prepareVisualizationQueryDataResponse(Individual author, List<BiboDocument> authorDocuments,
			   Map<String, Integer> yearToPublicationCount) {

		String authorName = null; 
		
		/*
		* To protect against cases where there are no author documents associated with the
		* individual. 
		* */
		if (authorDocuments.size() > 0) {
		authorName = author.getIndividualLabel();
		}
		
		/*
		* To make sure that null/empty records for author names do not cause any mischief.
		* */
		if (authorName == null) {
		authorName = "";
		}
		
		String outputFileName = UtilityFunctions.slugify(authorName) + "pub-count-sparkline" + ".csv";
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition","attachment;filename=" + outputFileName);
		
		try {
			
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		generateCsvFileBuffer(yearToPublicationCount, 
							  responseWriter);

		responseWriter.close();		
		
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	
	private void generateCsvFileBuffer(Map<String, Integer> yearToPublicationCount, 
											   PrintWriter responseWriter) {
		
        CSVWriter csvWriter = new SimpleWriter(responseWriter);
        
        try {
			csvWriter.append(new String[]{"Year", "Publications"});
			for (Entry<String, Integer> currentEntry : yearToPublicationCount.entrySet()) {
				csvWriter.append(new Object[]{currentEntry.getKey(), currentEntry.getValue()});
			}
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		responseWriter.flush();

	}

	private void prepareVisualizationQueryStandaloneResponse(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq,
			SparklineVOContainer valueObjectContainer) {

        Portal portal = vreq.getPortal();

        request.setAttribute("sparklineVO", valueObjectContainer);

        request.setAttribute("bodyJsp", "/templates/visualization/publication_count.jsp");
        request.setAttribute("portalBean", portal);
        request.setAttribute("title", "Individual Publication Count visualization");
        request.setAttribute("scripts", "/templates/visualization/visualization_scripts.jsp");

	}

	private void prepareVisualizationQueryDynamicResponse(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq, SparklineVOContainer valueObjectContainer, 
			Map<String, Integer> yearToPublicationCount) {

        Portal portal = vreq.getPortal();

        request.setAttribute("sparklineVO", valueObjectContainer);

        if (yearToPublicationCount.size() > 0) {
        	request.setAttribute("shouldVIVOrenderVis", true);
        } else {
        	request.setAttribute("shouldVIVOrenderVis", false);
        }
        
        request.setAttribute("portalBean", portal);
        request.setAttribute("bodyJsp", "/templates/visualization/ajax_vis_content.jsp");

	}

	private void handleMalformedParameters(String errorMessage)
			throws ServletException, IOException {

		Portal portal = vitroRequest.getPortal();

		request.setAttribute("error", errorMessage);

		RequestDispatcher requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
		request.setAttribute("bodyJsp", "/templates/visualization/visualization_error.jsp");
		request.setAttribute("portalBean", portal);
		request.setAttribute("title", "Visualization Query Error - Individual Publication Count");

		try {
			requestDispatcher.forward(request, response);
		} catch (Exception e) {
			log.error("EntityEditController could not forward to view.");
			log.error(e.getMessage());
			log.error(e.getStackTrace());
		}
	}

}
