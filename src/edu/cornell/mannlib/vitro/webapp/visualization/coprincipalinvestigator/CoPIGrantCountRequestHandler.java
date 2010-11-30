/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoPIData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoPINode;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;
/**
 * @author bkoniden
 * Deepak Konidena
 */
public class CoPIGrantCountRequestHandler implements VisualizationRequestHandler{
	
	public void generateVisualization(VitroRequest vitroRequest, HttpServletRequest request, HttpServletResponse response, Log log, DataSource dataSource){
		
		String egoURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		String renderMode = vitroRequest.getParameter(VisualizationFrameworkConstants.RENDER_MODE_KEY);
		String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);
		
		QueryRunner<CoPIData> queryManager = new CoPIGrantCountQueryRunner(egoURI, dataSource, log);
		
		try{
			CoPIData PINodesAndEdges = queryManager.getQueryResult();
			
//			PINodesAndEdges.print();
			
			if (VisualizationFrameworkConstants.DATA_RENDER_MODE
					.equalsIgnoreCase(renderMode)) {
				
				if (VisualizationFrameworkConstants.COPI_VIS_MODE
						.equalsIgnoreCase(visMode)) { 

						prepareCoPIDataResponse(PINodesAndEdges, response);
						return;
		    		
				} else {
		    			/*
		    			 * When the graphML file is required - based on which copi network 
		    			 * visualization will be rendered.
		    			 * */
		    			prepareNetworkDataResponse(PINodesAndEdges, response);
						return;
				}
			}else {
				
				RequestDispatcher requestDispatcher = null;

				prepareStandaloneResponse(
						egoURI, 
						PINodesAndEdges,
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
			
		}catch(MalformedQueryParametersException e){
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
	
	/**
	 * When the page for person level visualization is requested.
	 * @param egoURI
	 * @param coPIVO
	 * @param vitroRequest
	 * @param request
	 */
	private void prepareStandaloneResponse(String egoURI,
			CoPIData pINodesAndEdges, VitroRequest vitroRequest,
			HttpServletRequest request) {
		
		Portal portal = vitroRequest.getPortal();

		request.setAttribute("egoURIParam", egoURI);

		String title = "";
		if (pINodesAndEdges.getNodes() != null
				&& pINodesAndEdges.getNodes().size() > 0) {
			request.setAttribute("numOfAuthors", pINodesAndEdges.getNodes()
					.size());
			title = pINodesAndEdges.getEgoNode().getNodeName() + " - ";
		}

		if (pINodesAndEdges.getEdges() != null
				&& pINodesAndEdges.getEdges().size() > 0) {
			request.setAttribute("numOfCoPIs", pINodesAndEdges
					.getEdges().size());
		}

		request.setAttribute("title", title + "Co-PI Visualization");
		request.setAttribute("portalBean", portal);
		request.setAttribute("scripts",
				"/templates/visualization/person_level_inject_head.jsp");
		request.setAttribute("bodyJsp",
				"/templates/visualization/co_authorship.jsp");
	}

	/**
	 * Provides a response when graphml formatted co-pi network is requested, typically by 
	 * the flash vis.
	 * @param pINodesAndEdges
	 * @param response
	 */
	private void prepareNetworkDataResponse(CoPIData pINodesAndEdges,
			HttpServletResponse response) {
		
		response.setContentType("text/xml");
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		CoPIGraphMLWriter coPIGraphMLWriter = 
				new CoPIGraphMLWriter(pINodesAndEdges);
		
		responseWriter.append(coPIGraphMLWriter.getCoPIGraphMLContent());
		
		responseWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Provides response when a csv file containing number & names of unique co-pis per 
	 * year is requested. 
	 * @param pINodesAndEdges
	 * @param response
	 */
	private void prepareCoPIDataResponse(CoPIData pINodesAndEdges,
			HttpServletResponse response) {
		
		String outputFileName;
		Map<String, Set<CoPINode>> yearToCoPI = new TreeMap<String, Set<CoPINode>>();
		
		if (pINodesAndEdges.getNodes() != null && pINodesAndEdges.getNodes().size() > 0) {
			
			outputFileName = UtilityFunctions.slugify(pINodesAndEdges
									.getEgoNode().getNodeName())
			+ "_copis-per-year" + ".csv";
			
			yearToCoPI = UtilityFunctions.getGrantYearToCoPI(pINodesAndEdges);
			
		} else {
			
			outputFileName = "no_copis-per-year" + ".csv";			
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
			writeCoPIsPerYearCSV(yearToCoPI, responseWriter);
		
			responseWriter.close();
			
			} catch (IOException e) {
			e.printStackTrace();
			}
		
	}

	private void writeCoPIsPerYearCSV(Map<String, Set<CoPINode>> yearToCoPI,
			PrintWriter responseWriter) {
		responseWriter.append("Year, Count, Co-PI(s)\n");
		for (Map.Entry<String, Set<CoPINode>> currentEntry : yearToCoPI.entrySet()) {
			
			responseWriter.append("\"" + currentEntry.getKey() + "\"," 
							   + "\"" + currentEntry.getValue().size() + "\","
							   + "\"" + getCoPINamesAsString(currentEntry.getValue()) 
							   + "\"\n");
		}
		
	}

	private String getCoPINamesAsString(Set<CoPINode> CoPIs) {
		
		StringBuilder coPIsMerged = new StringBuilder();
		
		String coPISeparator = ";";
		for(CoPINode currentCoPI : CoPIs){
			coPIsMerged.append(currentCoPI.getNodeName() + coPISeparator);
		}
		
		return StringUtils.removeEnd(coPIsMerged.toString(), coPISeparator);
	}
}
