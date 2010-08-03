/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.filestorage.FileServingHelper;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.AllPropertiesQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.GenericQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * This request handler is used when you need helpful information to add more context
 * to the visualization. It does not have any code for generating the visualization, 
 * just fires sparql queries to get info for specific cases like,
 * 		1. thumbnail/image location for a particular individual
 * 		2. profile information for a particular individual like label, moniker etc
 * 		3. person level vis url for a particular individual
 * 		etc.  
 * @author cdtank
 */
public class UtilitiesRequestHandler implements VisualizationRequestHandler {
	
	public void generateVisualization(VitroRequest vitroRequest,
									  HttpServletRequest request, 
									  HttpServletResponse response, 
									  Log log, 
									  DataSource dataSource) {

        String individualURI = vitroRequest.getParameter(
        									VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

        String visMode = vitroRequest.getParameter(
        									VisualizationFrameworkConstants.VIS_MODE_KEY);
        
        String preparedURL = "";

        try {
        
            /*
    		 * If the info being requested is about a profile which includes the name, moniker
    		 * & image url.
    		 * */
    		if (VisualizationFrameworkConstants.PROFILE_INFO_UTILS_VIS_MODE
    					.equalsIgnoreCase(visMode)) {
    			
    			
    			String filterRule = "?predicate = j.2:mainImage " 
    									+ "|| ?predicate = vitro:moniker  " 
    									+ "|| ?predicate = rdfs:label";
    			
    			QueryRunner<GenericQueryMap> profileQueryHandler = 
    					new AllPropertiesQueryRunner(individualURI, 
    												  filterRule,
    												  dataSource,
    												  log);
    			
    			try {
    				
    				GenericQueryMap profilePropertiesToValues = 
    							profileQueryHandler.getQueryResult();
    				
    				profilePropertiesToValues.addEntry("imageContextPath", 
    												   request.getContextPath());
    				
    				Gson profileInformation = new Gson();
    				
    				prepareUtilitiesResponse(
    						profileInformation.toJson(profilePropertiesToValues),
    						response);
    				
    				return;
    				
    				
    			} catch (MalformedQueryParametersException e) {
    				try {
    					UtilityFunctions.handleMalformedParameters(
    							e.getMessage(),
    							"Visualization Query Error - Utilities Profile Info",
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
    		} else if (VisualizationFrameworkConstants.IMAGE_UTILS_VIS_MODE
    						.equalsIgnoreCase(visMode)) {
    			/*
        		 * If the url being requested is about a standalone image, which is used when we 
        		 * want to render an image & other info for a co-author OR ego for that matter.
        		 * */
    			
    			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
    			fieldLabelToOutputFieldLabel.put("downloadLocation", 
    											  QueryFieldLabels.THUMBNAIL_LOCATION_URL);
    			fieldLabelToOutputFieldLabel.put("fileName", QueryFieldLabels.THUMBNAIL_FILENAME);
    			
    			String whereClause = "<" + individualURI 
    									+ "> j.2:thumbnailImage ?thumbnailImage .  " 
    									+ "?thumbnailImage j.2:downloadLocation " 
    									+ "?downloadLocation ; j.2:filename ?fileName .";
    			
    			
    			
    			QueryRunner<ResultSet> imageQueryHandler = 
    					new GenericQueryRunner(individualURI,
    											fieldLabelToOutputFieldLabel,
    											whereClause,
    											dataSource,
    											log);
    			
    			try {
    				
    				String thumbnailAccessURL = 
    						getThumbnailInformation(
    								imageQueryHandler.getQueryResult(),
    								fieldLabelToOutputFieldLabel);
    				
    				prepareUtilitiesResponse(thumbnailAccessURL, response);
    				return;
    				
    				
    			} catch (MalformedQueryParametersException e) {
    				try {
    					UtilityFunctions.handleMalformedParameters(
    							e.getMessage(),
    							"Visualization Query Error - Utilities Image Info",
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
    		} else if (VisualizationFrameworkConstants.COAUTHOR_UTILS_VIS_MODE
    						.equalsIgnoreCase(visMode)) {
    			
    	    	/*
    	    	 * By default we will be generating profile url else some specific url like 
    	    	 * coAuthorShip vis url for that individual.
    	    	 * */
				preparedURL += request.getContextPath()
								+ VisualizationFrameworkConstants.VISUALIZATION_URL_PREFIX
								+ "?" 
								+ VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY 
								+ "=" + URLEncoder.encode(individualURI, 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
						 	    + "&"
			 				    + VisualizationFrameworkConstants.VIS_TYPE_KEY 
								+ "=" + URLEncoder.encode("coauthorship", 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
			 				    + "&"
			 				    + VisualizationFrameworkConstants.RENDER_MODE_KEY
								+ "=" + URLEncoder.encode(VisualizationFrameworkConstants
																.STANDALONE_RENDER_MODE, 
														VisualizationController.URL_ENCODING_SCHEME)
												  .toString();
				

				prepareUtilitiesResponse(preparedURL, response);
				return;
			} else if (VisualizationFrameworkConstants.PERSON_LEVEL_UTILS_VIS_MODE
							.equalsIgnoreCase(visMode)) {
    	    	/*
    	    	 * By default we will be generating profile url else some specific url like 
    	    	 * coAuthorShip vis url for that individual.
    	    	 * */
				preparedURL += request.getContextPath()
								+ VisualizationFrameworkConstants.VISUALIZATION_URL_PREFIX
								+ "?" 
								+ VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY 
								+ "=" + URLEncoder.encode(individualURI, 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
						 	    + "&"
			 				    + VisualizationFrameworkConstants.VIS_TYPE_KEY 
								+ "=" + URLEncoder.encode("person_level", 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString()
			 				    + "&"
			 				    + VisualizationFrameworkConstants.RENDER_MODE_KEY
								+ "=" + URLEncoder.encode(VisualizationFrameworkConstants
																.STANDALONE_RENDER_MODE, 
						 				 VisualizationController.URL_ENCODING_SCHEME).toString();
				
				prepareUtilitiesResponse(preparedURL, response);
				return;
			} else {
				
				preparedURL += request.getContextPath()
								+ VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX
								+ "?" 
								+ VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY 
								+ "=" + URLEncoder.encode(individualURI, 
										 VisualizationController.URL_ENCODING_SCHEME).toString();
				
				prepareUtilitiesResponse(preparedURL, response);
				return;
			}
        } catch (UnsupportedEncodingException e) {
			log.error(e.getLocalizedMessage());
		}

	}

	private String getThumbnailInformation(ResultSet resultSet,
										   Map<String, String> fieldLabelToOutputFieldLabel) {
		
		String finalThumbNailLocation = "";
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			
			
			RDFNode downloadLocationNode = solution.get(
													fieldLabelToOutputFieldLabel
															.get("downloadLocation"));
			RDFNode fileNameNode = solution.get(fieldLabelToOutputFieldLabel.get("fileName"));
			
			if (downloadLocationNode != null && fileNameNode != null) {
				finalThumbNailLocation = 
						FileServingHelper
								.getBytestreamAliasUrl(downloadLocationNode.toString(),
										fileNameNode.toString());
			}
		}
		return finalThumbNailLocation;
	}
	
	private void prepareUtilitiesResponse(String preparedURL, 
												   HttpServletResponse response) {

		response.setContentType("text/plain");
		
		try {
			PrintWriter responseWriter = response.getWriter();
			responseWriter.append(preparedURL);
			responseWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

