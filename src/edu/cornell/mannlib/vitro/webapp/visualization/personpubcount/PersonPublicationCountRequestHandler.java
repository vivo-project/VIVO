/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.PDFDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * 
 * This request handler is used to serve the content related to an individual's
 * publications over the years like,
 * 		1. Sprakline representing this
 * 		2. An entire page dedicated to the sparkline vis which will also have links to
 * download the data using which the sparkline was rendered & its tabular representation etc.
 * 		3. Downloadable CSV file containing number of publications over the years.
 * 		4. Downloadable PDf file containing the publications content, among other things. 
 * Currently this is disabled because the feature is half-baked. We plan to activate this in 
 * the next major release.  
 * 
 * @author cdtank
 */
public class PersonPublicationCountRequestHandler implements VisualizationRequestHandler {
	
	public void generateVisualization(VitroRequest vitroRequest,
									  HttpServletRequest request, 
									  HttpServletResponse response, 
									  Log log, 
									  DataSource dataSource) {
		
        String peronURI = vitroRequest.getParameter(
        									VisualizationFrameworkConstants
        											.INDIVIDUAL_URI_KEY);

        String renderMode = vitroRequest.getParameter(
        									VisualizationFrameworkConstants
        											.RENDER_MODE_KEY);
        
        String visMode = vitroRequest.getParameter(
        									VisualizationFrameworkConstants
        											.VIS_MODE_KEY);

        String visContainer = vitroRequest.getParameter(
        									VisualizationFrameworkConstants
        											.VIS_CONTAINER_KEY);

        QueryRunner<Set<BiboDocument>> queryManager =
        	new PersonPublicationCountQueryRunner(peronURI, dataSource, log);

		try {
			Set<BiboDocument> authorDocuments = queryManager.getQueryResult();

	    	/*
	    	 * Create a map from the year to number of publications. Use the BiboDocument's
	    	 * parsedPublicationYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToPublicationCount = 
	    			UtilityFunctions.getYearToPublicationCount(authorDocuments);
	    	
	    	Individual author = ((PersonPublicationCountQueryRunner) queryManager).getAuthor();

	    	if (VisualizationFrameworkConstants.DATA_RENDER_MODE
	    				.equalsIgnoreCase(renderMode)) {
	    		
				prepareDataResponse(author,
													  authorDocuments,
													  yearToPublicationCount,
													  response);
				return;
			}
	    	
	    	
	    	/*
	    	 * For now we are disabling the capability to render pdf file.
	    	 * */
	    	/*
	    	if (VisualizationFrameworkConstants.PDF_RENDER_MODE
	    				.equalsIgnoreCase(renderMode)) {
	    		
				preparePDFResponse(author,
													 authorDocuments,
													 yearToPublicationCount,
													 response);
				return;
			}
	    	*/
	    	
	    	/*
	    	 * Computations required to generate HTML for the sparkline & related context.
	    	 * */
	    	PersonPublicationCountVisCodeGenerator visualizationCodeGenerator = 
	    		new PersonPublicationCountVisCodeGenerator(vitroRequest.getContextPath(),
	    									   peronURI,
	    									   visMode,
	    									   visContainer,
	    									   authorDocuments,
	    									   yearToPublicationCount, 
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

				prepareDynamicResponse(request, 
									   response, 
									   vitroRequest, 
									   sparklineData, 
									   yearToPublicationCount);
		    	requestDispatcher = request.getRequestDispatcher("/templates/page/blankPage.jsp");

			} else {
		    	prepareStandaloneResponse(request, 
		    							  response, 
		    							  vitroRequest,
		    							  sparklineData);
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
				UtilityFunctions.handleMalformedParameters(
						e.getMessage(), 
						"Visualization Query Error - Individual Publication Count", 
						vitroRequest, 
						request, 
						response, 
						log);
			} catch (ServletException e1) {
				log.error(e1.getStackTrace());
			} catch (IOException e1) {
				log.error(e1.getStackTrace());
			}
			return;
		}
	}

	private void writePublicationsOverTimeCSV(Map<String, Integer> yearToPublicationCount,
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

	/**
	 * Provides response when csv file containing the publication count over the years
	 * is requested.
	 * @param author
	 * @param authorDocuments
	 * @param yearToPublicationCount
	 * @param response
	 */
	private void prepareDataResponse(
						Individual author,
						Set<BiboDocument> authorDocuments,
						Map<String, Integer> yearToPublicationCount, 
						HttpServletResponse response) {

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
		if (StringUtils.isBlank(authorName)) {
		authorName = "no-author";
		}
		
		String outputFileName = UtilityFunctions.slugify(authorName) 
										+ "_publications-per-year" + ".csv";
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
		
		try {
			
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		writePublicationsOverTimeCSV(yearToPublicationCount, responseWriter);

		responseWriter.close();		
		
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	
	/**
	 * Provides response when an entire page dedicated to publication sparkline is requested.
	 * @param request
	 * @param response
	 * @param vreq
	 * @param valueObjectContainer
	 */
	private void prepareStandaloneResponse(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq,
			SparklineData valueObjectContainer) {

        Portal portal = vreq.getPortal();

        request.setAttribute("sparklineVO", valueObjectContainer);

        request.setAttribute("bodyJsp", "/templates/visualization/publication_count.jsp");
        request.setAttribute("portalBean", portal);
        request.setAttribute("title", "Individual Publication Count visualization");
        request.setAttribute("scripts", "/templates/visualization/visualization_scripts.jsp");

	}

	/**
	 * Provides response when the publication sparkline has to be rendered in already existing 
	 * page, e.g. profile page.
	 * @param request
	 * @param response
	 * @param vreq
	 * @param valueObjectContainer
	 * @param yearToPublicationCount
	 */
	private void prepareDynamicResponse(
			HttpServletRequest request,
			HttpServletResponse response, 
			VitroRequest vreq, 
			SparklineData valueObjectContainer, 
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

	private void preparePDFResponse(
					Individual author,
					Set<BiboDocument> authorDocuments,
					Map<String, Integer> yearToPublicationCount, 
					HttpServletResponse response) {
		
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
		if (StringUtils.isBlank(authorName)) {
			authorName = "no-author";
		}
		
		String outputFileName = UtilityFunctions.slugify(authorName) + "_report" + ".pdf";
		
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
	
			ServletOutputStream responseOutputStream;
			try {
				responseOutputStream = response.getOutputStream();
				
				
				Document document = new Document();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
				document.open();
				
				PDFDocument pdfDocument = new PDFDocument(authorName, 
														  yearToPublicationCount, 
														  document, 
														  pdfWriter);
				
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
}
