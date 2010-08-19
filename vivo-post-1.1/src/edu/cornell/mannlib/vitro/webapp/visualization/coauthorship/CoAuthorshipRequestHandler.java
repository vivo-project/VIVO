/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

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

	public void generateVisualization(VitroRequest vitroRequest,
									  HttpServletRequest request, 
									  HttpServletResponse response, 
									  Log log, 
									  DataSource dataSource) {

		String egoURI = vitroRequest.getParameter(
        										VisualizationFrameworkConstants
        												.INDIVIDUAL_URI_KEY);

        String renderMode = vitroRequest.getParameter(
        										VisualizationFrameworkConstants
        												.RENDER_MODE_KEY);
        
        String visMode = vitroRequest.getParameter(
        										VisualizationFrameworkConstants
        												.VIS_MODE_KEY);

		QueryRunner<CoAuthorshipData> queryManager =
        	new CoAuthorshipQueryRunner(egoURI, dataSource, log);
	
		try {
			CoAuthorshipData authorNodesAndEdges = 
					queryManager.getQueryResult();
			
	    	
			if (VisualizationFrameworkConstants.DATA_RENDER_MODE
					.equalsIgnoreCase(renderMode)) {
				
		    	/* 
		    	 * We will be using the same visualization package for both sparkline & coauthorship
		    	 * flash vis. We will use "VIS_MODE_KEY" as a modifier to differentiate 
		    	 * between these two. The default will be to render the coauthorship network vis.
		    	 * */ 
				if (VisualizationFrameworkConstants.SPARKLINE_VIS_MODE
						.equalsIgnoreCase(visMode)) { 
	    			/*
	    			 * When the csv file is required - based on which sparkline visualization will 
	    			 * be rendered.
	    			 * */
						prepareSparklineDataResponse(authorNodesAndEdges, 
																	   response);
						return;
		    		
				} else {
		    			/*
		    			 * When the graphML file is required - based on which coauthorship network 
		    			 * visualization will be rendered.
		    			 * */
		    			prepareNetworkDataResponse(authorNodesAndEdges, response);
						return;
				}
			} else {
				
				RequestDispatcher requestDispatcher = null;

				prepareStandaloneResponse(
						egoURI, 
						authorNodesAndEdges,
		    			vitroRequest,
		    			request);

				requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);

		    	try {
		            requestDispatcher.forward(request, response);
		        } catch (Exception e) {
		            log.error("EntityEditController could not forward to view.");
		            log.error(e.getMessage());
		            log.error(e.getStackTrace());
		        }
				
				
			}
			
		} catch (MalformedQueryParametersException e) {
			try {
				UtilityFunctions.handleMalformedParameters(
						e.getMessage(), 
						"Visualization Query Error - Co-authorship Network",
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

	private void writeCoauthorsPerYearCSV(Map<String, Set<Node>> yearToCoauthors, 
									   PrintWriter printWriter) {
		
//        	printWriter.append("\"Year\", \"Count\", \"Co-Author(s)\"\n");
        	printWriter.append("Year, Count, Co-Author(s)\n");
			
			for (Entry<String, Set<Node>> currentEntry : yearToCoauthors.entrySet()) {
				
				printWriter.append("\"" + currentEntry.getKey() + "\"," 
								   + "\"" + currentEntry.getValue().size() + "\","
								   + "\"" + getCoauthorNamesAsString(currentEntry.getValue()) 
								   + "\"\n");
			}
			
		printWriter.flush();
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
	private void prepareSparklineDataResponse(CoAuthorshipData authorNodesAndEdges, 
											  HttpServletResponse response) {
		
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
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", 
									  "attachment;filename=" + outputFileName);
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		writeCoauthorsPerYearCSV(yearToCoauthors, responseWriter);
	
		responseWriter.close();
		
		} catch (IOException e) {
		e.printStackTrace();
		}
	}

	/**
	 * Provides a response when graphml formatted co-authorship network is requested, typically by 
	 * the flash vis.
	 * @param authorNodesAndEdges
	 * @param response
	 */
	private void prepareNetworkDataResponse(
			CoAuthorshipData authorNodesAndEdges, HttpServletResponse response) {
	
		response.setContentType("text/xml");
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		CoAuthorshipGraphMLWriter coAuthorshipGraphMLWriter = 
				new CoAuthorshipGraphMLWriter(authorNodesAndEdges);
		
		responseWriter.append(coAuthorshipGraphMLWriter.getCoAuthorshipGraphMLContent());
		
		responseWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * When the page for person level visualization is requested.
	 * @param egoURI
	 * @param coAuthorshipVO
	 * @param vitroRequest
	 * @param request
	 */
	private void prepareStandaloneResponse(
					String egoURI, 
					CoAuthorshipData coAuthorshipVO, 
					VitroRequest vitroRequest, 
					HttpServletRequest request) {

        Portal portal = vitroRequest.getPortal();
        
        request.setAttribute("egoURIParam", egoURI);
        
        String title = "";
        if (coAuthorshipVO.getNodes() != null && coAuthorshipVO.getNodes().size() > 0) {
        	request.setAttribute("numOfAuthors", coAuthorshipVO.getNodes().size());
        	title = coAuthorshipVO.getEgoNode().getNodeName() + " - ";
		}
		
		if (coAuthorshipVO.getEdges() != null && coAuthorshipVO.getEdges().size() > 0) {
			request.setAttribute("numOfCoAuthorShips", coAuthorshipVO.getEdges().size());
		}
		
        
        request.setAttribute("title",  title + "Co-Authorship Visualization");
        request.setAttribute("portalBean", portal);
        request.setAttribute("scripts", "/templates/visualization/person_level_inject_head.jsp");
        request.setAttribute("bodyJsp", "/templates/visualization/co_authorship.jsp");
	}
	
}
