/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personlevel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
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
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipGraphMLWriter;
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisVOContainer;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.VisualizationCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineVOContainer;
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
        
        String coAuthorsListMode = "coauthors";

        String egoPubSparklineVisContainerID = "ego_pub_sparkline";
        String uniqueCoauthorsSparklineVisContainerID = "unique_coauthors_sparkline";
        
        edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.QueryHandler coAuthorshipQueryManager =
        	new edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.QueryHandler(egoURIParam,
						     resultFormatParam,
						     rdfResultFormatParam,
						     dataSource,
						     log);
        
        edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.QueryHandler publicationQueryManager =
        	new edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.QueryHandler(egoURIParam,
						   	 resultFormatParam,
						     rdfResultFormatParam,
						     dataSource,
						     log);
        
		try {
			
			edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisVOContainer coAuthorshipVO = 
					coAuthorshipQueryManager.getVisualizationJavaValueObjects();
			
	    	/*
	    	 * In order to avoid unneeded computations we have pushed this "if" condition up.
	    	 * This case arises when the render mode is data. In that case we dont want to generate 
	    	 * HTML code to render sparkline, tables etc. Ideally I would want to avoid this flow.
	    	 * It is ugly! 
	    	 * */
	    	if (VisualizationFrameworkConstants.DATA_RENDER_MODE_URL_VALUE.equalsIgnoreCase(renderMode)) { 
			
					/* 
			    	 * We will be using the same visualization package for providing data for both 
			    	 * list of unique coauthors & network of coauthors (used in the flash vis). We will 
			    	 * use "VIS_MODE_URL_HANDLE" as a modifier to differentiate between these two.
			    	 * The defualt will be to provide data used to render the coauthorship network vis.
			    	 * */ 
					
					if (coAuthorsListMode.equalsIgnoreCase(visMode)) { 
		    			/*
		    			 * When the csv file is required - containing the unique co-authors vs how many times
		    			 * they have co-authored with the ego.
		    			 * */
							prepareVisualizationQueryListCoauthorsDataResponse(coAuthorshipVO);
							return;
			    		
					} else {
			    			/*
			    			 * When the graphML file is required - based on which coauthorship network visualization 
			    			 * will be rendered.
			    			 * */
			    			prepareVisualizationQueryNetworkDataResponse(coAuthorshipVO);
							return;
					}
	    		
	    		
			}
					
			List<BiboDocument> authorDocuments = publicationQueryManager.getVisualizationJavaValueObjects();
			
	    	/*
	    	 * Create a map from the year to number of publications. Use the BiboDocument's
	    	 * parsedPublicationYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToPublicationCount = publicationQueryManager
	    														.getYearToPublicationCount(authorDocuments);
	    														
//	    	Map<String, Integer> yearToUniqueCoauthorCount = getUniqueCoauthorsCountPerYear(coAuthorshipVO);
	    		
	    														
	    	/*
	    	 * Computations required to generate HTML for the sparklines & related context.
	    	 * */
	    	
	    	SparklineVOContainer publicationSparklineVO = new SparklineVOContainer();
	    	SparklineVOContainer uniqueCoauthorsSparklineVO = new SparklineVOContainer();

	    	edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.VisualizationCodeGenerator personPubCountVisCodeGenerator = 
	    		new edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.VisualizationCodeGenerator(
	    			vitroRequest.getRequestURI(),
	    			egoURIParam,
	    			VisualizationCodeGenerator.FULL_SPARKLINE_MODE_URL_HANDLE,
	    			egoPubSparklineVisContainerID,
	    			authorDocuments,
	    			yearToPublicationCount,
	    			publicationSparklineVO,
	    			log);	  
	    	
	    	edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisualizationCodeGenerator uniqueCoauthorsVisCodeGenerator = 
	    		new edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisualizationCodeGenerator(
	    			vitroRequest.getRequestURI(),
	    			egoURIParam,
	    			VisualizationCodeGenerator.FULL_SPARKLINE_MODE_URL_HANDLE,
	    			uniqueCoauthorsSparklineVisContainerID,
	    			getUniqueCoAuthorsPerYear(coAuthorshipVO),
	    			uniqueCoauthorsSparklineVO,
	    			log);
			
			
			RequestDispatcher requestDispatcher = null;

	    	prepareVisualizationQueryStandaloneResponse(egoURIParam, 
	    												publicationSparklineVO,
	    												uniqueCoauthorsSparklineVO,
	    												coAuthorshipVO,
	    												egoPubSparklineVisContainerID,
	    												uniqueCoauthorsSparklineVisContainerID,
	    												request, 
	    												response, 
	    												vitroRequest);

//	    	requestDispatcher = request.getRequestDispatcher("/templates/page/blankPage.jsp");
			requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);

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
/*
	private Map<String, Integer> getUniqueCoauthorsCountPerYear(
			edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisVOContainer coAuthorshipVO) {
		Map<String, Integer> yearToUniqueCoauthorCount = new TreeMap<String, Integer>();
		
		Map<String, Set<Node>> yearToUniqueCoauthors = getUniqueCoAuthorsPerYear(coAuthorshipVO);
			
		for (Entry<String, Set<Node>> currentEntry : yearToUniqueCoauthors.entrySet()) {
			
			yearToUniqueCoauthorCount.put(currentEntry.getKey(), currentEntry.getValue().size());
			
		}
		return yearToUniqueCoauthorCount;
	}
	*/
	
	private Map<String, Set<Node>> getUniqueCoAuthorsPerYear(edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisVOContainer authorNodesAndEdges) {

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

	private void prepareVisualizationQueryNetworkDataResponse(VisVOContainer coAuthorsipVO) {

		String outputFileName = "";
		
		if (coAuthorsipVO.getNodes() == null || coAuthorsipVO.getNodes().size() < 1) {
			
			outputFileName = "no-coauthorship-net" + ".csv";
			
		} else {
			
			outputFileName = UtilityFunctions.slugify(coAuthorsipVO.getEgoNode().getNodeName()) 
			+ "-coauthor-net" + ".xml";
			
		}
		
		
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		CoAuthorshipGraphMLWriter coAuthorShipGraphMLWriter = new CoAuthorshipGraphMLWriter(coAuthorsipVO);
		
		responseWriter.append(coAuthorShipGraphMLWriter.getCoAuthorshipGraphMLContent());
		
		responseWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareVisualizationQueryListCoauthorsDataResponse(VisVOContainer coAuthorsipVO) {

		String outputFileName = "";
		Map<String, Integer> coAuthorsToCount = new TreeMap<String, Integer>();
		
		if (coAuthorsipVO.getNodes() == null || coAuthorsipVO.getNodes().size() < 1 ) {
			
			outputFileName = "no-coauthors-list" + ".csv";
			
		} else {
			
			outputFileName = UtilityFunctions.slugify(coAuthorsipVO.getEgoNode().getNodeName()) 
										+ "-coauthors-list" + ".csv";
			
			coAuthorsToCount = getCoAuthorsList(coAuthorsipVO);
			
		}
			
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		generateCsvFileBuffer(coAuthorsToCount, 
							  responseWriter);

		responseWriter.close();
		
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	
	
	private Map<String, Integer> getCoAuthorsList(VisVOContainer coAuthorsipVO) {
		
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

	private void generateCsvFileBuffer(Map<String, Integer> coAuthorsToCount, PrintWriter printWriter) {
		
	    	printWriter.append("\"Co-Author\", \"Count\"\n");
			
			for (Entry<String, Integer> currentEntry : coAuthorsToCount.entrySet()) {
				
				printWriter.append("\"" + currentEntry.getKey() + "\"," 
								   + "\"" + currentEntry.getValue() + "\"\n"
											  );
			}
			
		printWriter.flush();
	}
	
	
	private void prepareVisualizationQueryStandaloneResponse(
		String egoURIParam, 
		SparklineVOContainer egoPubSparklineVO, 
		SparklineVOContainer uniqueCoauthorsSparklineVO, 
		VisVOContainer coAuthorshipVO, 
		String egoPubSparklineVisContainer, 
		String uniqueCoauthorsSparklineVisContainer, 
		HttpServletRequest request,
		HttpServletResponse response, 
		VitroRequest vreq) {

        Portal portal = vreq.getPortal();
        
        request.setAttribute("egoURIParam", egoURIParam);
        
        if (coAuthorshipVO.getNodes() != null) {
        	request.setAttribute("numOfAuthors", coAuthorshipVO.getNodes().size());
		}
		
		if (coAuthorshipVO.getEdges() != null) {
			request.setAttribute("numOfCoAuthorShips", coAuthorshipVO.getEdges().size());
		}
		
        
        request.setAttribute("egoPubSparklineVO", egoPubSparklineVO);
        request.setAttribute("uniqueCoauthorsSparklineVO", uniqueCoauthorsSparklineVO);
        
        request.setAttribute("egoPubSparklineContainerID", egoPubSparklineVisContainer);
        request.setAttribute("uniqueCoauthorsSparklineVisContainerID", uniqueCoauthorsSparklineVisContainer);
        
        request.setAttribute("title", "Person Level Visualization");
        request.setAttribute("portalBean", portal);
        request.setAttribute("scripts", "/templates/visualization/person_level_inject_head.jsp");
        
        request.setAttribute("bodyJsp", "/templates/visualization/person_level.jsp");
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
