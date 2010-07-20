/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;
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

        String egoURIParam = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_URL_HANDLE);

        String renderMode = vitroRequest.getParameter(VisualizationFrameworkConstants.RENDER_MODE_URL_HANDLE);
        
        String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_URL_HANDLE);

        String visContainer = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_CONTAINER_URL_HANDLE);
        
        String sparklineVisMode = "sparkline";
        
        QueryHandler queryManager =
        	new QueryHandler(egoURIParam,
						     resultFormatParam,
						     rdfResultFormatParam,
						     dataSource,
						     
						     log);

		try {
			
			VisVOContainer authorNodesAndEdges = queryManager.getVisualizationJavaValueObjects();
			
	    	/*
	    	 * In order to avoid unneeded computations we have pushed this "if" condition up.
	    	 * This case arises when the render mode is data. In that case we dont want to generate 
	    	 * HTML code to render sparkline, tables etc. Ideally I would want to avoid this flow.
	    	 * It is ugly!
	    	 * */
	    	
			if (VisualizationFrameworkConstants.DATA_RENDER_MODE_URL_VALUE.equalsIgnoreCase(renderMode)) {
				
		    	/* 
		    	 * We will be using the same visualization package for both sparkline & coauthorship flash 
		    	 * vis. We will use "VIS_MODE_URL_HANDLE" as a modifier to differentiate between these two.
		    	 * The defualt will be to render the coauthorship network vis.
		    	 * */ 
				
				if (sparklineVisMode.equalsIgnoreCase(visMode)) { 
	    			/*
	    			 * When the csv file is required - based on which sparkline visualization will 
	    			 * be rendered.
	    			 * */
						prepareVisualizationQuerySparklineDataResponse(authorNodesAndEdges);
						return;
		    		
				} else {
		    			/*
		    			 * When the graphML file is required - based on which coauthorship network visualization 
		    			 * will be rendered.
		    			 * */
		    			prepareVisualizationQueryNetworkDataResponse(authorNodesAndEdges);
						return;
				}
			}
	    			
			
	    	
	    	/*
	    	 * Computations required to generate HTML for the sparklines & related context.
	    	 * */
	    	
	    	/*
	    	 * This is required because when deciding the range of years over which the vis
	    	 * was rendered we dont want to be influenced by the "DEFAULT_PUBLICATION_YEAR".
	    	 * */
//	    	publishedYearsForCollege.remove(VOConstants.DEFAULT_PUBLICATION_YEAR);

	    	/*
	    	VisualizationCodeGenerator visualizationCodeGenerator = 
	    		new VisualizationCodeGenerator(yearToPublicationCount, log);
	    	
			String visContentCode = visualizationCodeGenerator
										.getMainVisualizationCode(authorDocuments,
															  	  publishedYears,
															  	  visMode,
															  	  visContainer);

			String visContextCode = visualizationCodeGenerator
										.getVisualizationContextCode(vitroRequest.getRequestURI(), 
																	 collegeURIParam,
																	 visMode);
																	 */

	    	/*
	    	 * This is side-effecting because the response of this method is just to redirect to
	    	 * a page with visualization on it.
	    	 * */
			
			RequestDispatcher requestDispatcher = null;

	    	prepareVisualizationQueryStandaloneResponse(egoURIParam, request, response, vitroRequest);

//		    	requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
		    	requestDispatcher = request.getRequestDispatcher("/templates/page/blankPage.jsp");

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

	private void prepareVisualizationQueryNetworkDataResponse(VisVOContainer authorNodesAndEdges) {

		response.setContentType("text/xml");
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		
		CoAuthorshipGraphMLWriter coAuthorShipGraphMLWriter = new CoAuthorshipGraphMLWriter(authorNodesAndEdges);
		
		responseWriter.append(coAuthorShipGraphMLWriter.getCoAuthorshipGraphMLContent());
		
		responseWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareVisualizationQuerySparklineDataResponse(VisVOContainer authorNodesAndEdges) {
		
		String outputFileName = "";
		Map<String, Set<Node>> yearToCoauthors = new TreeMap<String, Set<Node>>();
		
		if (authorNodesAndEdges.getNodes() == null || authorNodesAndEdges.getNodes().size() < 1 ) {
			
			outputFileName = "no-coauthors" + ".csv";
			
		} else {
			
			outputFileName = UtilityFunctions.slugify(authorNodesAndEdges.getEgoNode().getNodeName()) 
			+ "-coauthors" + ".csv";
			
			yearToCoauthors = getCoAuthorsStats(authorNodesAndEdges);
			
		}
		
		 
			
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		generateCsvFileBuffer(yearToCoauthors, 
							  responseWriter);

		responseWriter.close();
		
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	
	private void generateCsvFileBuffer(Map<String, Set<Node>> yearToCoauthors, PrintWriter printWriter) {
		
        	printWriter.append("\"Year\", \"Number of Co-Authors\", \"Co-Author(s)\"\n");
			
			for (Entry<String, Set<Node>> currentEntry : yearToCoauthors.entrySet()) {
				
				printWriter.append("\"" + currentEntry.getKey() + "\"," 
								   + "\"" + currentEntry.getValue().size() + "\","
								   + "\"" + getCoauthorsString(currentEntry.getValue()) + "\"\n"
											  );
				
				
			}
			
		printWriter.flush();
		
	}
	
	private String getCoauthorsString(Set<Node> coAuthors) {
		
		StringBuilder coAuthorsMerged = new StringBuilder();
		
		for (Node currCoAuthor : coAuthors) {
			coAuthorsMerged.append(currCoAuthor.getNodeName() + "; ");
		}
		
		return coAuthorsMerged.toString();
	}
	
	private Map<String, Set<Node>> getCoAuthorsStats(VisVOContainer authorNodesAndEdges) {

		Map<String, Set<Node>> yearToCoAuthors = new TreeMap<String, Set<Node>>();
		
		Node egoNode = authorNodesAndEdges.getEgoNode();
		
		for (Node currNode : authorNodesAndEdges.getNodes()) {
					
				/*
				 * We have already printed the Ego Node info.
				 * */
				if (currNode != egoNode) {
					
					for (String year : currNode.getYearToPublicationCount().keySet()) {
						
						Set<Node> coAuthorNodes;
						
						if (yearToCoAuthors.containsKey(year)) {
							
							coAuthorNodes = yearToCoAuthors.get(year);
							coAuthorNodes.add(currNode);
							
						} else {
							
							coAuthorNodes = new HashSet<Node>();
							coAuthorNodes.add(currNode);
							yearToCoAuthors.put(year, coAuthorNodes);
						}
						
					}
					
				}
		}
		
		
		return yearToCoAuthors;
	}

	private void prepareVisualizationQueryStandaloneResponse(String egoURIParam, 
															 HttpServletRequest request,
															 HttpServletResponse response, 
															 VitroRequest vreq) {

        Portal portal = vreq.getPortal();

//        request.setAttribute("visContentCode", visContentCode);
//        request.setAttribute("visContextCode", visContextCode);

        request.setAttribute("egoURIParam", egoURIParam);
        
        request.setAttribute("bodyJsp", "/templates/visualization/co_authorship.jsp");
        request.setAttribute("portalBean", portal);
//        request.setAttribute("title", "Individual Publication Count Visualization");
//        request.setAttribute("scripts", "/templates/visualization/visualization_scripts.jsp");

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
