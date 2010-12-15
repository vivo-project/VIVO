/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personlevel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
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
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.persongrantcount.PersonGrantCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.persongrantcount.PersonGrantCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIGrantCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.PersonPublicationCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.PersonPublicationCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoPIData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Grant;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * This request handler is used to serve content rendered on the person level vis page
 * like,
 * 		1. Front end of the vis including the co-author & publication sparkline.
 * 		2. Downloadable file having the co-author network in graphml format.
 * 		3. Downloadable file having the list of co-authors that the individual has
 * worked with & count of such co-authorships.
 * 
 * @author cdtank
 */
public class PersonLevelRequestHandler implements VisualizationRequestHandler {

    private static final String EGO_PUB_SPARKLINE_VIS_CONTAINER_ID = "ego_pub_sparkline";
    private static final String UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID = 
    									"unique_coauthors_sparkline";
    private static final String EGO_GRANT_SPARKLINE_VIS_CONTAINER_ID = "ego_grant_sparkline";
    private static final String UNIQUE_COPIS_SPARKLINE_VIS_CONTAINER_ID = 
    									"unique_copis_sparkline";
    
    
	public void generateVisualization(VitroRequest vitroRequest,
			   HttpServletRequest request, 
			   HttpServletResponse response, 
			   Log log, 
			   DataSource dataSource) {

        String egoURI = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

        String renderMode = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.RENDER_MODE_KEY);
        
        String visMode = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.VIS_MODE_KEY);
        
		QueryRunner<CoAuthorshipData> coAuthorshipQueryManager =
	        	new CoAuthorshipQueryRunner(egoURI, dataSource, log);
        
        QueryRunner<Set<BiboDocument>> publicationQueryManager =
	        	new PersonPublicationCountQueryRunner(egoURI, dataSource, log);
        
		QueryRunner<CoPIData> coPIQueryManager = new CoPIGrantCountQueryRunner(egoURI, dataSource, log);
        
        
        QueryRunner<Set<Grant>> grantQueryManager =
        	new PersonGrantCountQueryRunner(egoURI, dataSource, log);
        
		try {
			
			CoAuthorshipData coAuthorshipData = coAuthorshipQueryManager.getQueryResult();
			
			CoPIData coPIData = coPIQueryManager.getQueryResult();
			
			if (VisualizationFrameworkConstants.DATA_RENDER_MODE
	    				.equalsIgnoreCase(renderMode)) { 
			
					/* 
			    	 * We will be using the same visualization package for providing data for both 
			    	 * list of unique coauthors & network of coauthors (used in the flash vis). 
			    	 * We will use "VIS_MODE_KEY" as a modifier to differentiate between 
			    	 * these two. The default will be to provide data used to render the co-
			    	 * authorship network vis.
			    	 * */ 
					if (VisualizationFrameworkConstants.COAUTHORSLIST_VIS_MODE
								.equalsIgnoreCase(visMode)) { 
		    			/*
		    			 * When the csv file is required - containing the co-authors & how 
		    			 * many times they have co-authored with the ego.
		    			 * */
							prepareListCoauthorsDataResponse(coAuthorshipData, 
																			   response);
							return;
			    		
					} else {
			    			/*
			    			 * When the graphML file is required - based on which co-authorship 
			    			 * network visualization will be rendered.
			    			 * */
			    			prepareNetworkDataResponse(coAuthorshipData, 
			    													     response);
							return;
					}
			}
					
			
			/*
			 * When the front-end for the person level vis has to be displayed we render couple of 
			 * sparklines. This will prepare all the data for the sparklines & other requested 
			 * files.
			 * */
			
			Set<BiboDocument> authorDocuments = publicationQueryManager.getQueryResult();
			
	    	/*
	    	 * Create a map from the year to number of publications. Use the BiboDocument's
	    	 * parsedPublicationYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToPublicationCount = 
	    			UtilityFunctions.getYearToPublicationCount(authorDocuments);
	    														
	    	/*
	    	 * Computations required to generate HTML for the sparklines & related context.
	    	 * */
	    	PersonPublicationCountVisCodeGenerator personPubCountVisCodeGenerator = 
	    		new PersonPublicationCountVisCodeGenerator(
	    			vitroRequest.getRequestURI(),
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_PUB_SPARKLINE_VIS_CONTAINER_ID,
	    			authorDocuments,
	    			yearToPublicationCount,
	    			log);	  
	    	
	    	SparklineData publicationSparklineVO = personPubCountVisCodeGenerator
	    														.getValueObjectContainer();
	    	
	    	CoAuthorshipVisCodeGenerator uniqueCoauthorsVisCodeGenerator = 
	    		new CoAuthorshipVisCodeGenerator(
	    			vitroRequest.getRequestURI(),
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getPublicationYearToCoAuthors(coAuthorshipData),
	    			log);
	    	
	    	SparklineData uniqueCoauthorsSparklineVO = uniqueCoauthorsVisCodeGenerator
	    															.getValueObjectContainer();
			
	    	/*
	    	 * grants over time sparkline
	    	 */
			
			Set<Grant> piGrants = grantQueryManager.getQueryResult();
			
	    	/*
	    	 * Create a map from the year to number of grants. Use the Grant's
	    	 * parsedGrantYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToGrantCount = 
	    			UtilityFunctions.getYearToGrantCount(piGrants);	    	
	    	
	    	PersonGrantCountVisCodeGenerator personGrantCountVisCodeGenerator = 
	    		new PersonGrantCountVisCodeGenerator(
	    			vitroRequest.getRequestURI(),
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_PUB_SPARKLINE_VIS_CONTAINER_ID,
	    			piGrants,
	    			yearToGrantCount,
	    			log);
	    	
	    	SparklineData grantSparklineVO = personGrantCountVisCodeGenerator
			.getValueObjectContainer();
	    	
	    	
	    	/*
	    	 * Co-PI's over time sparkline
	    	 */
	    	CoPIVisCodeGenerator uniqueCopisVisCodeGenerator = 
	    		new CoPIVisCodeGenerator(
	    			vitroRequest.getRequestURI(),
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getGrantYearToCoPI(coPIData),
	    			log);
	    	
	    	SparklineData uniqueCopisSparklineVO = uniqueCopisVisCodeGenerator
			.getValueObjectContainer();	    	
	    	
	    	
			RequestDispatcher requestDispatcher = null;

			prepareStandaloneResponse(
					egoURI, 
	    			publicationSparklineVO,
	    			uniqueCoauthorsSparklineVO,
	    			grantSparklineVO,
	    			uniqueCopisSparklineVO,
	    			coAuthorshipData,
	    			coPIData,
	    			EGO_PUB_SPARKLINE_VIS_CONTAINER_ID,
	    			UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID,
	    			vitroRequest,
	    			request, visMode);

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
				UtilityFunctions.handleMalformedParameters(
							e.getMessage(),
							"Visualization Query Error - Person Level Visualization",
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

	private void writeCoAuthorsToWorksCSV(Map<String, Integer> coAuthorsToCount, 
									   	  PrintWriter printWriter) {
		
//	    	printWriter.append("\"Co-Author\", \"Count\"\n");
	    	printWriter.append("Co-Author, Count\n");
			
			for (Entry<String, Integer> currentEntry : coAuthorsToCount.entrySet()) {
				
				printWriter.append("\"" + currentEntry.getKey() + "\"," 
								   + "\"" + currentEntry.getValue() + "\"\n"
											  );
			}
			
		printWriter.flush();
	}
	
	/**
	 * Provide response when graphml file for the co-authorship network is requested.
	 * @param coAuthorsipData
	 * @param response
	 */
	private void prepareNetworkDataResponse(
			CoAuthorshipData coAuthorsipData, HttpServletResponse response) {
	
		String outputFileName = "";
		
		if (coAuthorsipData.getNodes() != null && coAuthorsipData.getNodes().size() > 0) {
			
			outputFileName = UtilityFunctions.slugify(coAuthorsipData.getEgoNode().getNodeName()) 
									+ "_coauthor-network.graphml" + ".xml";
			
		} else {
			outputFileName = "no_coauthor-network.graphml" + ".xml";			
		}
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		CoAuthorshipGraphMLWriter coAuthorShipGraphMLWriter = 
				new CoAuthorshipGraphMLWriter(coAuthorsipData);
		
		responseWriter.append(coAuthorShipGraphMLWriter.getCoAuthorshipGraphMLContent());
		responseWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Provides response when a csv file containing co-author names & number of co-authored works
	 * is requested. 
	 * @param coAuthorshipData
	 * @param response
	 */
	private void prepareListCoauthorsDataResponse(
			CoAuthorshipData coAuthorshipData, HttpServletResponse response) {
	
		String outputFileName = "";
		Map<String, Integer> coAuthorsToCount = new TreeMap<String, Integer>();
		
		if (coAuthorshipData.getNodes() != null && coAuthorshipData.getNodes().size() > 0) {
			
			outputFileName = UtilityFunctions.slugify(coAuthorshipData.getEgoNode().getNodeName()) 
									+ "_coauthors" + ".csv";
	
			coAuthorsToCount = getCoAuthorsList(coAuthorshipData);
			
		} else {
			outputFileName = "no_coauthors" + ".csv";
		}
			
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + outputFileName);
		
		try {
		
		PrintWriter responseWriter = response.getWriter();
		
		/*
		 * We are side-effecting responseWriter since we are directly manipulating the response 
		 * object of the servlet.
		 * */
		writeCoAuthorsToWorksCSV(coAuthorsToCount, responseWriter);
	
		responseWriter.close();
		
		} catch (IOException e) {
		e.printStackTrace();
		}
	}

	/**
	 * When the page for person level visualization is requested.
	 * @param egoURI
	 * @param egoPubSparklineVO
	 * @param uniqueCoauthorsSparklineVO
	 * @param uniqueCopisSparklineVO 
	 * @param grantSparklineVO 
	 * @param coAuthorshipVO
	 * @param coPIVO 
	 * @param egoPubSparklineVisContainer
	 * @param uniqueCoauthorsSparklineVisContainer
	 * @param vitroRequest
	 * @param request
	 * @param visMode 
	 */
	private void prepareStandaloneResponse (
					String egoURI, 
					SparklineData egoPubSparklineVO, 
					SparklineData uniqueCoauthorsSparklineVO, 
					SparklineData egoGrantSparklineVO, SparklineData uniqueCopisSparklineVO, CoAuthorshipData coAuthorshipVO, 
					CoPIData coPIVO, String egoPubSparklineVisContainer, 
					String uniqueCoauthorsSparklineVisContainer, 
					VitroRequest vitroRequest, 
					HttpServletRequest request, String visMode) {
		
		String completeURL = "";
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
		
        if (coPIVO.getNodes() != null && coPIVO.getNodes().size() > 0) {
        	request.setAttribute("numOfInvestigators", coPIVO.getNodes().size());
        	//title = coPIVO.getEgoNode().getNodeName() + " - ";
		}
		
		if (coPIVO.getEdges() != null && coPIVO.getEdges().size() > 0) {
			request.setAttribute("numOfCoPIs", coPIVO.getEdges().size());
		}
		
		
		try {
			completeURL = getCompleteURL(request);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("visMode", visMode);
        request.setAttribute("completeURL", completeURL);
		request.setAttribute("egoPubSparklineVO", egoPubSparklineVO);
		request.setAttribute("egoGrantSparklineVO", egoGrantSparklineVO);
        request.setAttribute("uniqueCoauthorsSparklineVO", uniqueCoauthorsSparklineVO);
        request.setAttribute("uniqueCopisSparklineVO", uniqueCopisSparklineVO);
        
        request.setAttribute("egoPubSparklineContainerID", egoPubSparklineVisContainer);
        request.setAttribute("uniqueCoauthorsSparklineVisContainerID", 
        					 uniqueCoauthorsSparklineVisContainer);
        
        request.setAttribute("title",  title + "Person Level Visualization");
        request.setAttribute("portalBean", portal);
        request.setAttribute("scripts", "/templates/visualization/person_level_inject_head.jsp");
        request.setAttribute("bodyJsp", "/templates/visualization/person_level.jsp");
	}

	private String getCompleteURL(HttpServletRequest request) throws MalformedURLException {
		
		String file = request.getRequestURI();
//		System.out.println("\ngetRequestURI() -->  "+ file + "\ngetQueryString() -->  "+request.getQueryString()+ "\ngetScheme() -->  "+ request.getScheme());
//		System.out.println("\ngetServerName() -->  "+ request.getServerName() + "\ngetServerPort() -->  "+request.getServerPort());

		URL reconstructedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), file);
		
//		System.out.println("\nReconstructed URL is -->  " + reconstructedURL);
		
		return reconstructedURL.toString();
	}
	
}
