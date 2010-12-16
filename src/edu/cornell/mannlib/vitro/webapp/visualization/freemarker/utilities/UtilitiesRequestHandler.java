/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.utilities;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.filestorage.FileServingHelper;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.AllPropertiesQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.GenericQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;

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
	
	public Object generateAjaxVisualization(VitroRequest vitroRequest,
											Log log, 
											DataSource dataSource) 
			throws MalformedQueryParametersException {

        String individualURI = vitroRequest.getParameter(
        									VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

        String visMode = vitroRequest.getParameter(
        									VisualizationFrameworkConstants.VIS_MODE_KEY);
        
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
			
			GenericQueryMap profilePropertiesToValues = 
						profileQueryHandler.getQueryResult();
			
			Gson profileInformation = new Gson();
			
			return profileInformation.toJson(profilePropertiesToValues);
				
				
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
			
			return getThumbnailInformation(imageQueryHandler.getQueryResult(),
											   fieldLabelToOutputFieldLabel);

		} else if (VisualizationFrameworkConstants.COAUTHOR_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {
			
	    	/*
	    	 * By default we will be generating profile url else some specific url like 
	    	 * coAuthorShip vis url for that individual.
	    	 * */
			ParamMap coAuthorProfileURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
															 individualURI,
															 VisualizationFrameworkConstants.VIS_TYPE_KEY,
															 VisualizationFrameworkConstants.COAUTHORSHIP_VIS,
															 VisualizationFrameworkConstants.RENDER_MODE_KEY,
															 VisualizationFrameworkConstants.STANDALONE_RENDER_MODE);
			
			return UrlBuilder.getUrl(VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
							  coAuthorProfileURLParams);
			
		} else if (VisualizationFrameworkConstants.PERSON_LEVEL_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {
	    	/*
	    	 * By default we will be generating profile url else some specific url like 
	    	 * coAuthorShip vis url for that individual.
	    	 * */
			ParamMap personLevelURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
														 individualURI,
														 VisualizationFrameworkConstants.VIS_TYPE_KEY,
														 VisualizationFrameworkConstants.PERSON_LEVEL_VIS,
														 VisualizationFrameworkConstants.RENDER_MODE_KEY,
														 VisualizationFrameworkConstants.STANDALONE_RENDER_MODE);
			
			return UrlBuilder.getUrl(VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
									 personLevelURLParams);
			
		} else {
			
			ParamMap individualProfileURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
														 individualURI);
			
			return UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX,
											individualProfileURLParams);
		}

	}

	private String getThumbnailInformation(ResultSet resultSet,
										   Map<String, String> fieldLabelToOutputFieldLabel) {
		
		String finalThumbNailLocation = "";
		
		while (resultSet.hasNext())  {
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
	/*
	
	private TemplateResponseValues prepareUtilitiesResponse(String preparedContent, VitroRequest vreq) {
		
//		response.setContentType("text/plain");
		
		String utilitiesTemplate = "/visualization/utilities.ftl";
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("utilityResopnse", preparedContent);

		return new TemplateResponseValues(utilitiesTemplate, body);
		
	}*/

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Utilities does not provide Data Response.");
	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Utilities does not provide Standard Response.");
	}
}

