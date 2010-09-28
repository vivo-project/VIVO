/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.entitycomparison;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import com.google.gson.Gson;

import com.hp.hpl.jena.query.DataSource;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.PDFDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

//TODO: Class description.
/**
 * To be described.
 * 
 * @author bkoniden
 */
public class EntityPublicationCountRequestHandler implements
		VisualizationRequestHandler {

	/*
	 * Vis container holds the "id" of the div on the final response html page
	 * that the visualization actually appears on.
	 */
	public static String ENTITY_VIS_MODE;
	public static String SUB_ENTITY_VIS_MODE;
	
	@SuppressWarnings("null")
	public void generateVisualization(VitroRequest vitroRequest,
			HttpServletRequest request, HttpServletResponse response, Log log,
			DataSource dataSource) {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

		String renderMode = vitroRequest
				.getParameter(VisualizationFrameworkConstants.RENDER_MODE_KEY);

		ENTITY_VIS_MODE = vitroRequest
				.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);

		String visContainer = vitroRequest
				.getParameter(VisualizationFrameworkConstants.VIS_CONTAINER_KEY);

		System.out
				.println("\nInside EntityPublicationCountRequestHandler! \n----------------------------------------- ");
		System.out.println("\nEntity URI: " + entityURI + "\nRender Mode: "
				+ renderMode + "\nVis Mode: " + ENTITY_VIS_MODE + "\nVis Containter: "
				+ visContainer);

		QueryRunner<Entity> queryManager = new EntityPublicationCountQueryRunner(
				entityURI, dataSource, log, ENTITY_VIS_MODE);

		try {
			Entity entity = queryManager.getQueryResult();

			if (ENTITY_VIS_MODE.equals("DEPARTMENT")) {
				SUB_ENTITY_VIS_MODE = "PERSON";
				System.out
						.println("\n\nDocuments within the Entity\n---------------------------------------------");
				for (BiboDocument document : entity.getPublications()) {
					System.out.println(document.getDocumentLabel() + " > "
							+ document.getDocumentURL());
				}

				System.out
						.println("\n\nSubEntities within the Entity\n---------------------------------------------");

				for (SubEntity person : entity.getSubEntities()) {
					System.out.println(person.getIndividualLabel());
				}
			}

			else if (ENTITY_VIS_MODE.equals("SCHOOL")) {
				SUB_ENTITY_VIS_MODE = "DEPARTMENT";
				System.out
						.println("\nDocuments within the Entity\n---------------------------------------------");
				for (BiboDocument document : entity.getPublications()) {
					System.out.println(document.getDocumentLabel() + " > "
							+ document.getDocumentURL());
				}
				
				System.out
						.println("\n\nSubEntities within the Entity\n---------------------------------------------");
				for (SubEntity department : entity.getSubEntities()) {	
					System.out.println(department.getIndividualLabel());
				}
			}

			else {
				//default is UNIVERSITY
				SUB_ENTITY_VIS_MODE = "SCHOOL";
				System.out
						.println("\nDocuments within the Entity\n---------------------------------------------");
				for (BiboDocument document : entity.getPublications()) {
					System.out.println(document.getDocumentLabel() + " > "
							+ document.getDocumentURL());
				}

				System.out
						.println("\n\nSubEntities within the Entity\n---------------------------------------------");

				for (SubEntity school : entity.getSubEntities()) {
					System.out.println(school.getIndividualLabel());

				}
			}
			
			RequestDispatcher requestDispatcher = null;
			 if (VisualizationFrameworkConstants.DATA_RENDER_MODE
			 .equalsIgnoreCase(renderMode)) {
			
				 prepareDataResponse(entity,entity.getSubEntities(), response);
				 
			 }
			 else if (VisualizationFrameworkConstants.STANDALONE_RENDER_MODE.equalsIgnoreCase(renderMode)){
				 
				 prepareStandaloneResponse(request, response, vitroRequest, entity);
				 requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
			 }
			//
			// /*
			// * Computations required to generate HTML for the sparkline &
			// * related context.
			// */
			// EntityPublicationCountVisCodeGenerator visualizationCodeGenerator
			// = new EntityPublicationCountVisCodeGenerator(
			// vitroRequest.getContextPath(), entityURI, visMode,
			// visContainer, departmentDocuments,
			// publicationCountForIndividualYears, log);
			//
			// SparklineData sparklineData = visualizationCodeGenerator
			// .getValueObjectContainer();
			//
			// /*
			// * This is side-effecting because the response of this method is
			// * just to redirect to a page with visualization on it.
			// */
			// RequestDispatcher requestDispatcher = null;
			//
			// if (VisualizationFrameworkConstants.DYNAMIC_RENDER_MODE
			// .equalsIgnoreCase(renderMode)) {
			//
			// prepareDynamicResponse(request, response, vitroRequest,
			// sparklineData, publicationCountForIndividualYears);
			// requestDispatcher = request
			// .getRequestDispatcher("/templates/page/blankPage.jsp");
			//
			// } else {
			// prepareStandaloneResponse(request, response, vitroRequest,
			// sparklineData);
			// requestDispatcher = request
			// .getRequestDispatcher(Controllers.BASIC_JSP);
			// }
			//
			try {
				requestDispatcher.forward(request, response);
			} catch (Exception e) {
				log.error("EntityEditController could not forward to view.");
				log.error(e.getMessage());
				log.error(e.getStackTrace());
			}
			//
		} catch (MalformedQueryParametersException e) {
			try {
				UtilityFunctions
						.handleMalformedParameters(
								e.getMessage(),
								"Visualization Query Error - Individual Publication Count",
								vitroRequest, request, response, log);
			} catch (ServletException e1) {
				log.error(e1.getStackTrace());
			} catch (IOException e1) {
				log.error(e1.getStackTrace());
			}
			return;
		}
	}

	/**
	 * Provides response when csv file containing the publication count over the
	 * years is requested.
	 * 
	 * @param author
	 * @param subentities
	 * @param yearToPublicationCount
	 * @param response
	 */
	private void prepareDataResponse(Entity entity,
			Set<SubEntity> subentities,
			HttpServletResponse response) {

		String entityLabel = entity.getEntityLabel();
		
		String outputFileName = UtilityFunctions.slugify(entityLabel)
				+ "_publications-per-year" + ".json";

		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ outputFileName);

		try {

			PrintWriter responseWriter = response.getWriter();

			/*
			 * We are side-effecting responseWriter since we are directly
			 * manipulating the response object of the servlet.
			 */
			responseWriter.append(
			writePublicationsOverTimeJSON(subentities
					));

			responseWriter.flush();
			responseWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Provides response when an entire page dedicated to publication sparkline
	 * is requested.
	 * 
	 * @param request
	 * @param response
	 * @param vreq
	 * @param entity
	 */
	private void prepareStandaloneResponse(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq,
			Entity entity) {

		Portal portal = vreq.getPortal();
		String jsonContent = "";
//		try{
//			PrintWriter responseWriter = response.getWriter();

			/*
			 * We are side-effecting responseWriter since we are directly
			 * manipulating the response object of the servlet.
			 */
			jsonContent = writePublicationsOverTimeJSON(entity.getSubEntities()
					);			
//		} catch(IOException e){
//			e.printStackTrace();
//		}
		
		request.setAttribute("JsonContent", jsonContent);

		request.setAttribute("bodyJsp",
				"/templates/visualization/entity_comparison.jsp");
		request.setAttribute("portalBean", portal);
		request.setAttribute("title",
				"Entity Comparison visualization");
		request.setAttribute("scripts",
				"/templates/visualization/entity_comparison_inject_head.jsp");

	}

	/**
	 * Provides response when the publication sparkline has to be rendered in
	 * already existing page, e.g. profile page.
	 * 
	 * @param request
	 * @param response
	 * @param vreq
	 * @param valueObjectContainer
	 * @param yearToPublicationCount
	 */
	private void prepareDynamicResponse(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq,
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
		request.setAttribute("bodyJsp",
				"/templates/visualization/ajax_vis_content.jsp");
	}

	private void preparePDFResponse(Individual author,
			Set<BiboDocument> authorDocuments,
			Map<String, Integer> yearToPublicationCount,
			HttpServletResponse response) {

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

	/**
	 * function to generate a json file for year <-> publication count mapping
	 * 
	 * @param yearToPublicationCount
	 * @param responseWriter
	 * @param visMode 
	 */
	private String writePublicationsOverTimeJSON(
			Set<SubEntity> subentities) {
		System.out.println("\nsub entity vis mode ------>" + SUB_ENTITY_VIS_MODE+"\n");
		Gson json = new Gson();
		Set<JsonObject> subEntitiesJson = new HashSet<JsonObject>();
		
		for(SubEntity subentity : subentities){
			JsonObject entityJson = new JsonObject(subentity.getIndividualLabel());
			//entityJson.setYearToPublicationCount(UtilityFunctions
			//		.getYearToPublicationCount(subentity.getDocuments()));
			
			List<List<Integer>> yearPubCount = new ArrayList<List<Integer>>(); 
			
			for (Map.Entry<String, Integer> pubEntry : UtilityFunctions
					.getYearToPublicationCount(subentity.getDocuments()).entrySet()) {
				
				List<Integer> currentPubYear = new ArrayList<Integer>();
				if(pubEntry.getKey().equals(VOConstants.DEFAULT_PUBLICATION_YEAR))
					currentPubYear.add(-1);
				else
					currentPubYear.add(Integer.parseInt(pubEntry.getKey()));
				currentPubYear.add(pubEntry.getValue());
				yearPubCount.add(currentPubYear);
			}
			
			entityJson.setYearToPublicationCount(yearPubCount);
			
			entityJson.setEntityURI(subentity.getIndividualURI());
			entityJson.setVisMode(SUB_ENTITY_VIS_MODE);
			subEntitiesJson.add(entityJson);
		}
		
//		responseWriter.append(json.toJson(subEntitiesJson));
//		
//		responseWriter.flush();
//		responseWriter.close();
		
		return json.toJson(subEntitiesJson);


	}
}
