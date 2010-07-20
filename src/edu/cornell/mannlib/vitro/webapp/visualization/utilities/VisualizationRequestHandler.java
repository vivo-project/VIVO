/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.filestorage.FileServingHelper;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.AllPropertiesQueryHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.GenericQueryHandler;

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

        String individualURIParam = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_URL_HANDLE);

        String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_URL_HANDLE);
        
        String profileInfoMode = "PROFILE_INFO";
        String profileVisMode = "PROFILE_URL";
        String coAuthorVisMode = "COAUTHORSHIP_URL";
        String personLevelVisMode = "PERSON_LEVEL_URL";
        String imageVisMode = "IMAGE_URL";
        
		String resultFormatParam = "RS_TEXT";
        String rdfResultFormatParam = "RDF/XML-ABBREV";

        String preparedURL = "";

        try {
        
            /*
    		 * If the info being requested is about a profile which includes the name, moniker
    		 * & image url.
    		 * */
    		if (profileInfoMode.equalsIgnoreCase(visMode)) {
    			
    			
    			String filterRule = "?predicate = j.2:mainImage || ?predicate = vitro:moniker  || ?predicate = rdfs:label";
    			AllPropertiesQueryHandler profileQueryHandler = new AllPropertiesQueryHandler(individualURIParam, 
    																			filterRule, 
    																			resultFormatParam, 
    																			rdfResultFormatParam, 
    																			dataSource, 
    																			log);
    			
    			try {
    				
    				GenericQueryMap profilePropertiesToValues = profileQueryHandler.getJavaValueObjects();
    				
    				profilePropertiesToValues.addEntry("imageContextPath", request.getContextPath());
    				
    				Gson profileInformation = new Gson();
    				
    				prepareVisualizationQueryResponse(profileInformation.toJson(profilePropertiesToValues));
    				
    				return;
    				
    				
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
    			
    			
    		} else if (imageVisMode.equalsIgnoreCase(visMode)) {
    			/*
        		 * If the url being requested is about a standalone image, which is used when we want
        		 * to render an image & other info for a co-author OR ego for that matter.
        		 * */
    			
    			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
    			fieldLabelToOutputFieldLabel.put("downloadLocation", QueryFieldLabels.THUMBNAIL_LOCATION_URL);
    			fieldLabelToOutputFieldLabel.put("fileName", QueryFieldLabels.THUMBNAIL_FILENAME);
    			
    			String whereClause = "<" + individualURIParam + "> j.2:thumbnailImage ?thumbnailImage .  " 
    									+ "?thumbnailImage j.2:downloadLocation ?downloadLocation ; j.2:filename ?fileName .";
    			
    			
    			
    			GenericQueryHandler imageQueryHandler = new GenericQueryHandler(individualURIParam,
    																			fieldLabelToOutputFieldLabel,
    																			whereClause,
    																			resultFormatParam, 
    																			rdfResultFormatParam, 
    																			dataSource, 
    																			log);
    			
    			try {
    				
    				String thumbnailAccessURL = getThumnailInformation(
    																imageQueryHandler.getResultSet(),
    																fieldLabelToOutputFieldLabel);
    				
    				System.out.println("thumnail access URL " + thumbnailAccessURL);
    				prepareVisualizationQueryResponse(thumbnailAccessURL);
    				return;
    				
    				
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
    			
    			
    		} else if (coAuthorVisMode.equalsIgnoreCase(visMode)) {
    	    	/*
    	    	 * By default we will be generating profile url else some specific url like coAuthorShip vis 
    	    	 * url for that individual.
    	    	 * */
				
				preparedURL += request.getContextPath()
								+ "/admin/visQuery"
								+ "?" 
								+ VisualizationFrameworkConstants.INDIVIDUAL_URI_URL_HANDLE 
								+ "=" + URLEncoder.encode(individualURIParam, 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
						 	    + "&"
			 				    + VisualizationFrameworkConstants.VIS_TYPE_URL_HANDLE 
								+ "=" + URLEncoder.encode("coauthorship", 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
			 				    + "&"
			 				    + VisualizationFrameworkConstants.RENDER_MODE_URL_HANDLE
								+ "=" + URLEncoder.encode(VisualizationFrameworkConstants.STANDALONE_RENDER_MODE_URL_VALUE, 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString();
				

				prepareVisualizationQueryResponse(preparedURL);
				return;

			} else if (personLevelVisMode.equalsIgnoreCase(visMode)) {
    	    	/*
    	    	 * By default we will be generating profile url else some specific url like coAuthorShip vis 
    	    	 * url for that individual.
    	    	 * */
				
				preparedURL += request.getContextPath()
								+ "/admin/visQuery"
								+ "?" 
								+ VisualizationFrameworkConstants.INDIVIDUAL_URI_URL_HANDLE 
								+ "=" + URLEncoder.encode(individualURIParam, 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
						 	    + "&"
			 				    + VisualizationFrameworkConstants.VIS_TYPE_URL_HANDLE 
								+ "=" + URLEncoder.encode("person_level", 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
			 				    + "&"
			 				    + VisualizationFrameworkConstants.RENDER_MODE_URL_HANDLE
								+ "=" + URLEncoder.encode(VisualizationFrameworkConstants.STANDALONE_RENDER_MODE_URL_VALUE, 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString();
				
				prepareVisualizationQueryResponse(preparedURL);
				return;

			} else {
				
				preparedURL += request.getContextPath()
								+ "/individual"
								+ "?" 
								+ VisualizationFrameworkConstants.INDIVIDUAL_URI_URL_HANDLE 
								+ "=" + URLEncoder.encode(individualURIParam, 
										 VisualizationController.URL_ENCODING_SCHEME).toString();
				
				prepareVisualizationQueryResponse(preparedURL);
				return;
	
			}
			
        } catch (UnsupportedEncodingException e) {
			log.error(e.getLocalizedMessage());
		}

	}

	private String getThumnailInformation(ResultSet resultSet, 
												   Map<String, String> fieldLabelToOutputFieldLabel) {
		
		String finalThumbNailLocation = "";
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			
			
			RDFNode downloadLocationNode = solution.get(fieldLabelToOutputFieldLabel.get("downloadLocation"));
			RDFNode fileNameNode = solution.get(fieldLabelToOutputFieldLabel.get("fileName"));
			
			if (downloadLocationNode != null && fileNameNode != null) {
				finalThumbNailLocation = FileServingHelper
												.getBytestreamAliasUrl(downloadLocationNode.toString(),
																	   fileNameNode.toString());
			}
			
		}
		
		return finalThumbNailLocation;
	}
	
	private void prepareVisualizationQueryResponse(String preparedURL) {

		response.setContentType("text/plain");
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		responseWriter.append(preparedURL);
		
		responseWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
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
