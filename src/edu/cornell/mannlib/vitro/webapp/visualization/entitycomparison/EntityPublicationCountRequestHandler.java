/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.entitycomparison;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class EntityPublicationCountRequestHandler implements
		VisualizationRequestHandler {

	/*
	 * Vis container holds the "id" of the div on the final response html page
	 * that the visualization actually appears on.
	 */
	public static String ENTITY_VIS_MODE;
	public static String SUB_ENTITY_VIS_MODE;


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
		
		QueryRunner<Entity> queryManager = new EntityPublicationCountQueryRunner(
				entityURI, dataSource, log, ENTITY_VIS_MODE);		

		try {
			Entity entity = queryManager.getQueryResult();

			if (ENTITY_VIS_MODE.equalsIgnoreCase("DEPARTMENT")) {
				
				SUB_ENTITY_VIS_MODE = "PERSON";

			}else if (ENTITY_VIS_MODE.equalsIgnoreCase("SCHOOL")) {
				
				SUB_ENTITY_VIS_MODE = "DEPARTMENT";

			}else {
				SUB_ENTITY_VIS_MODE = "SCHOOL";

			}
			
			QueryRunner<Map<String, Set<String>>> queryManagerForsubOrganisationTypes = new EntitySubOrganizationTypesQueryRunner(
					entityURI, dataSource, log, ENTITY_VIS_MODE);
			
			Map<String, Set<String>> subOrganizationTypesResult = queryManagerForsubOrganisationTypes.getQueryResult();
						
			RequestDispatcher requestDispatcher = null;
			
			if (VisualizationFrameworkConstants.DATA_RENDER_MODE
					.equalsIgnoreCase(renderMode)) {

				prepareDataResponse(entity, entity.getSubEntities(),subOrganizationTypesResult, response, log);

			} else if (VisualizationFrameworkConstants.STANDALONE_RENDER_MODE
					.equalsIgnoreCase(renderMode)) {

				prepareStandaloneResponse(request, response, vitroRequest,
						entity,entityURI, subOrganizationTypesResult, log);
				requestDispatcher = request
						.getRequestDispatcher(Controllers.BASIC_JSP);
			}

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
	 * @param subOrganizationTypesResult 
	 * @param yearToPublicationCount
	 * @param response
	 * @param log 
	 */
	private void prepareDataResponse(Entity entity, Set<SubEntity> subentities,
			Map<String, Set<String>> subOrganizationTypesResult, HttpServletResponse response, Log log) {

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
			responseWriter.append(writePublicationsOverTimeJSON(subentities, subOrganizationTypesResult, log));

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
	 * @param entityURI 
	 * @param subOrganizationTypesResult 
	 * @param log 
	 */
	private void prepareStandaloneResponse(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq, Entity entity, String entityURI, Map<String, Set<String>> subOrganizationTypesResult, Log log) {

		Portal portal = vreq.getPortal();
		String jsonContent = "";
		/*
		 * We are side-effecting responseWriter since we are directly
		 * manipulating the response object of the servlet.
		 */
		jsonContent = writePublicationsOverTimeJSON(entity.getSubEntities(), subOrganizationTypesResult, log);
		
		request.setAttribute("OrganizationURI", entityURI);
		request.setAttribute("OrganizationLabel", entity.getEntityLabel());
		request.setAttribute("JsonContent", jsonContent);

		request.setAttribute("bodyJsp",
				"/templates/visualization/entity_comparison.jsp");
		request.setAttribute("portalBean", portal);
		request.setAttribute("title", "Entity Comparison visualization");
		request.setAttribute("scripts",
				"/templates/visualization/entity_comparison_inject_head.jsp");

	}

	/**
	 * function to generate a json file for year <-> publication count mapping
	 * @param subOrganizationTypesResult 
	 * @param log 
	 * 
	 * @param yearToPublicationCount
	 * @param responseWriter
	 * @param visMode
	 */
	private String writePublicationsOverTimeJSON(Set<SubEntity> subentities, Map<String, Set<String>> subOrganizationTypesResult, Log log) {
//		System.out.println("\nsub entity vis mode ------>"
//				+ SUB_ENTITY_VIS_MODE + "\n");
		log.debug("Creating JSONObject \n-----------------------");
		Gson json = new Gson();
		Set<JsonObject> subEntitiesJson = new HashSet<JsonObject>();

		for (SubEntity subentity : subentities) {
			JsonObject entityJson = new JsonObject(
					subentity.getIndividualLabel());

			List<List<Integer>> yearPubCount = new ArrayList<List<Integer>>();

			for (Map.Entry<String, Integer> pubEntry : UtilityFunctions
					.getYearToPublicationCount(subentity.getDocuments())
					.entrySet()) {

				List<Integer> currentPubYear = new ArrayList<Integer>();
				if (pubEntry.getKey().equals(
						VOConstants.DEFAULT_PUBLICATION_YEAR))
					currentPubYear.add(-1);
				else
					currentPubYear.add(Integer.parseInt(pubEntry.getKey()));
				currentPubYear.add(pubEntry.getValue());
				yearPubCount.add(currentPubYear);
			}

			entityJson.setYearToPublicationCount(yearPubCount);
			entityJson.getOrganizationType().addAll(subOrganizationTypesResult.get(entityJson.getLabel()));

			entityJson.setEntityURI(subentity.getIndividualURI());
			setEntityVisMode(entityJson);
			//entityJson.setVisMode(SUB_ENTITY_VIS_MODE);
			log.debug("Adding object with uri: "
					+ entityJson.getEntityURI() + " vismode: "
					+ entityJson.getVisMode() + " label: "
					+ entityJson.getLabel() + " type: "
					+ entityJson.getOrganizationType().toString());
			subEntitiesJson.add(entityJson);
		}
		
	//	System.out.println("\nStopWords are "+ EntitySubOrganizationTypesQueryRunner.stopWords.toString() + "\n");
		return json.toJson(subEntitiesJson);

	}

	private void setEntityVisMode(JsonObject entityJson) {
		if(entityJson.getOrganizationType().contains("Department")){
			entityJson.setVisMode("DEPARTMENT");
		}else if(entityJson.getOrganizationType().contains("School")){
			entityJson.setVisMode("SCHOOL");
		}else{
			entityJson.setVisMode(SUB_ENTITY_VIS_MODE);
		}
		
	}
}
