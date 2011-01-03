/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coauthorship;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Node;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;

/**
 * This request handler is used when information related to co-authorship network
 * for an individual is requested. It currently provides 2 outputs,
 * 		1. Graphml content representing the individual's co-authorship network 
 * 		1. CSV file containing the list(& count) of unique co-authors with which 
 * the individual has worked over the years. This data powers the related sparkline.
 *  
 * @author cdtank
 */
public class CoAuthorshipRequestHandler implements VisualizationRequestHandler {

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			DataSource dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("CoAuthorship does not provide Ajax Response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {

	
		String egoURI = vitroRequest.getParameter(
				VisualizationFrameworkConstants
						.INDIVIDUAL_URI_KEY);

		String visMode = vitroRequest.getParameter(
						VisualizationFrameworkConstants
								.VIS_MODE_KEY);
		
		QueryRunner<CoAuthorshipData> queryManager =
		new CoAuthorshipQueryRunner(egoURI, dataSource, log);
		
		CoAuthorshipData authorNodesAndEdges = 
		queryManager.getQueryResult();
	
    	/* 
    	 * We will be using the same visualization package for both sparkline & coauthorship
    	 * flash vis. We will use "VIS_MODE_KEY" as a modifier to differentiate 
    	 * between these two. The default will be to render the coauthorship network vis.
    	 * */ 
		if (VisualizationFrameworkConstants.COAUTHORS_COUNT_PER_YEAR_VIS_MODE
				.equalsIgnoreCase(visMode)) { 
			/*
			 * When the csv file is required - based on which sparkline visualization will 
			 * be rendered.
			 * */
				return prepareCoauthorsCountPerYearDataResponse(authorNodesAndEdges);
				
		} else if (VisualizationFrameworkConstants.COAUTHORS_LIST_VIS_MODE
				.equalsIgnoreCase(visMode)) { 
			/*
			 * When the csv file is required - based on which sparkline visualization will 
			 * be rendered.
			 * */
				return prepareCoauthorsListDataResponse(authorNodesAndEdges);
				
		} else if (VisualizationFrameworkConstants.COAUTHOR_NETWORK_DOWNLOAD_VIS_MODE
				.equalsIgnoreCase(visMode)) { 
			/*
			 * When the csv file is required - based on which sparkline visualization will 
			 * be rendered.
			 * */
				return prepareNetworkDownloadDataResponse(authorNodesAndEdges);
				
		} else {
    			/*
    			 * When the graphML file is required - based on which coauthorship network 
    			 * visualization will be rendered.
    			 * */
    			return prepareNetworkStreamDataResponse(authorNodesAndEdges);
		}
	
	}

	public ResponseValues generateStandardVisualization(VitroRequest vitroRequest,
											  	Log log, 
											    DataSource dataSource) 
		throws MalformedQueryParametersException {

		/*
		 * Support for this has ceased to exist. Standalone mode was created only for demo 
		 * purposes for VIVO Conf.
		 * */
/*		String egoURI = vitroRequest.getParameter(
        										VisualizationFrameworkConstants
        												.INDIVIDUAL_URI_KEY);

		QueryRunner<CoAuthorshipData> queryManager =
        	new CoAuthorshipQueryRunner(egoURI, dataSource, log);
	
			CoAuthorshipData authorNodesAndEdges = 
					queryManager.getQueryResult();
			
	    	
		return prepareStandaloneResponse(egoURI,
										 authorNodesAndEdges,
										 vitroRequest);*/
		
		throw new UnsupportedOperationException("CoAuthorship does not provide Standalone Response.");
		
	}
	
	

	private String getCoauthorsListCSVContent(Map<String, Integer> coAuthorsToCount) {
		
		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Year, Count\n");
		
		for (Entry<String, Integer> currentEntry : coAuthorsToCount.entrySet()) {
			csvFileContent.append(StringEscapeUtils.escapeCsv(currentEntry.getKey()));
			csvFileContent.append(",");
			csvFileContent.append(currentEntry.getValue());
			csvFileContent.append("\n");
		}
		
		return csvFileContent.toString();
			
	}

	private String getCoauthorsPerYearCSVContent(Map<String, Set<Node>> yearToCoauthors) {
		
		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Year, Count, Co-Author(s)\n");
		
		for (Entry<String, Set<Node>> currentEntry : yearToCoauthors.entrySet()) {
			csvFileContent.append(StringEscapeUtils.escapeCsv(currentEntry.getKey()));
			csvFileContent.append(",");
			csvFileContent.append(currentEntry.getValue().size());
			csvFileContent.append(",");
			csvFileContent.append(StringEscapeUtils.escapeCsv(getCoauthorNamesAsString(currentEntry.getValue())));
			csvFileContent.append("\n");
		}
		
		return csvFileContent.toString();
			
	}
	
	private String getCoauthorNamesAsString(Set<Node> coAuthors) {
		
		StringBuilder coAuthorsMerged = new StringBuilder();
		
		String coAuthorSeparator = "; ";
		for (Node currCoAuthor : coAuthors) {
			coAuthorsMerged.append(currCoAuthor.getNodeName() + coAuthorSeparator);
		}
		
		return StringUtils.removeEnd(coAuthorsMerged.toString(), coAuthorSeparator);
	}

	/**
	 * Provides response when a csv file containing number & names of unique co-authors per 
	 * year is requested. 
	 * @param authorNodesAndEdges
	 * @param response
	 */
	private Map<String, String> prepareCoauthorsCountPerYearDataResponse(CoAuthorshipData authorNodesAndEdges) {
		
		String outputFileName;
		Map<String, Set<Node>> yearToCoauthors = new TreeMap<String, Set<Node>>();
		
		if (authorNodesAndEdges.getNodes() != null && authorNodesAndEdges.getNodes().size() > 0) {
			
			outputFileName = UtilityFunctions.slugify(authorNodesAndEdges
									.getEgoNode().getNodeName())
			+ "_coauthors-per-year" + ".csv";
			
			yearToCoauthors = UtilityFunctions.getPublicationYearToCoAuthors(authorNodesAndEdges);
			
		} else {
			
			outputFileName = "no_coauthors-per-year" + ".csv";			
		}
		
        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 getCoauthorsPerYearCSVContent(yearToCoauthors));

		return fileData;
	}

	/**
	 * Provides response when a csv file containing number & names of unique co-authors per 
	 * year is requested. 
	 * @param authorNodesAndEdges
	 * @param response
	 */
	private Map<String, String> prepareCoauthorsListDataResponse(CoAuthorshipData coAuthorshipData) {
		
		String outputFileName = "";
		Map<String, Integer> coAuthorsToCount = new TreeMap<String, Integer>();
		
		if (coAuthorshipData.getNodes() != null && coAuthorshipData.getNodes().size() > 0) {
			
			outputFileName = UtilityFunctions.slugify(coAuthorshipData.getEgoNode().getNodeName()) 
									+ "_coauthors" + ".csv";
	
			coAuthorsToCount = getCoAuthorsList(coAuthorshipData);
			
		} else {
			outputFileName = "no_coauthors" + ".csv";
		}
		
        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 getCoauthorsListCSVContent(coAuthorsToCount));

		return fileData;
	}
	
	private Map<String, Integer> getCoAuthorsList(CoAuthorshipData coAuthorsipVO) {
		
		Map<String, Integer> coAuthorsToCount = new TreeMap<String, Integer>();
		
		for (Node currNode : coAuthorsipVO.getNodes()) {
			
			/*
			 * We have already printed the Ego Node info.
			 * */
			if (currNode != coAuthorsipVO.getEgoNode()) {
				
				coAuthorsToCount.put(currNode.getNodeName(), currNode.getNumOfAuthoredWorks());
				
			}
		}
		return coAuthorsToCount;
	}
	
	/**
	 * Provides a response when graphml formatted co-authorship network is requested, typically by 
	 * the flash vis.
	 * @param authorNodesAndEdges
	 * @param response
	 */
	private Map<String, String> prepareNetworkStreamDataResponse(CoAuthorshipData authorNodesAndEdges) {
	
		CoAuthorshipGraphMLWriter coAuthorshipGraphMLWriter = 
				new CoAuthorshipGraphMLWriter(authorNodesAndEdges);
		
        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "text/xml");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 coAuthorshipGraphMLWriter.getCoAuthorshipGraphMLContent().toString());

		return fileData;
	
	}
	
	private Map<String, String> prepareNetworkDownloadDataResponse(CoAuthorshipData authorNodesAndEdges) {
		
		String outputFileName = "";
		
		if (authorNodesAndEdges.getNodes() != null && authorNodesAndEdges.getNodes().size() > 0) {
			
			outputFileName = UtilityFunctions.slugify(authorNodesAndEdges.getEgoNode().getNodeName()) 
									+ "_coauthor-network.graphml" + ".xml";
			
		} else {
			outputFileName = "no_coauthor-network.graphml" + ".xml";			
		}
		
		CoAuthorshipGraphMLWriter coAuthorshipGraphMLWriter = 
				new CoAuthorshipGraphMLWriter(authorNodesAndEdges);
		
        Map<String, String> fileData = new HashMap<String, String>();
        fileData.put(DataVisualizationController.FILE_NAME_KEY, 
				 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "text/xml");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 coAuthorshipGraphMLWriter.getCoAuthorshipGraphMLContent().toString());

		return fileData;
	
	}
	
	/**
	 * When the page for person level visualization is requested.
	 * @param egoURI
	 * @param coAuthorshipVO
	 * @param vitroRequest
	 * @param request
	 * @return 
	 */
	private TemplateResponseValues prepareStandaloneResponse(
					String egoURI, 
					CoAuthorshipData coAuthorshipVO, 
					VitroRequest vitroRequest) {

        Portal portal = vitroRequest.getPortal();
        
        String title = "";
        Map<String, Object> body = new HashMap<String, Object>();
        
        if (coAuthorshipVO.getNodes() != null && coAuthorshipVO.getNodes().size() > 0) {
        	title = coAuthorshipVO.getEgoNode().getNodeName() + " - ";
        	body.put("numOfAuthors", coAuthorshipVO.getNodes().size());
		}
		
		if (coAuthorshipVO.getEdges() != null && coAuthorshipVO.getEdges().size() > 0) {
			body.put("numOfCoAuthorShips", coAuthorshipVO.getEdges().size());
		}
		
        
        //request.setAttribute("scripts", "/templates/visualization/person_level_inject_head.jsp");
        
        String standaloneTemplate = "coAuthorship.ftl";

        
        body.put("portalBean", portal);
        body.put("egoURIParam", egoURI);
        body.put("title", title + "Co-Authorship Visualization");
        
        return new TemplateResponseValues(standaloneTemplate, body);
	}
	
}
