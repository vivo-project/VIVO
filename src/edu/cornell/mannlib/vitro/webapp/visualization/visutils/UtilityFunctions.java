/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoPIData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Grant;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoPINode;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;


public class UtilityFunctions {
	
	public static Map<String, Integer> getYearToPublicationCount(
			Set<BiboDocument> authorDocuments) {

    	/*
    	 * Create a map from the year to number of publications. Use the BiboDocument's
    	 * parsedPublicationYear to populate the data.
    	 * */
    	Map<String, Integer> yearToPublicationCount = new TreeMap<String, Integer>();

    	for (BiboDocument curr : authorDocuments) {

    		/*
    		 * Increment the count because there is an entry already available for
    		 * that particular year.
    		 * 
    		 * I am pushing the logic to check for validity of year in "getPublicationYear" itself
    		 * because,
    		 * 	1. We will be using getPub... multiple times & this will save us duplication of code
    		 * 	2. If we change the logic of validity of a pub year we would not have to make 
    		 * changes all throughout the codebase.
    		 * 	3. We are asking for a publication year & we should get a proper one or NOT at all.
    		 * */
    		String publicationYear;
    		if (curr.getPublicationYear() != null) { 
    			publicationYear = curr.getPublicationYear();
    		} else {
    			publicationYear = curr.getParsedPublicationYear();
    		}
    		
			if (yearToPublicationCount.containsKey(publicationYear)) {
    			yearToPublicationCount.put(publicationYear,
    									   yearToPublicationCount
    									   		.get(publicationYear) + 1);

    		} else {
    			yearToPublicationCount.put(publicationYear, 1);
    		}

    	}

		return yearToPublicationCount;
	}
	
	/**
	 * This method is used to return a mapping between publication year & all the co-authors
	 * that published with ego in that year. 
	 * @param authorNodesAndEdges
	 * @return
	 */
	public static Map<String, Set<Node>> getPublicationYearToCoAuthors(
										CoAuthorshipData authorNodesAndEdges) {

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
	
	/**
	 * Currently the approach for slugifying filenames is naive. In future if there is need, 
	 * we can write more sophisticated method.
	 * @param textToBeSlugified
	 * @return
	 */
	public static String slugify(String textToBeSlugified) {
		String textBlockSeparator = "-";
		return StringUtils.removeEnd(StringUtils.substring(textToBeSlugified.toLowerCase().trim()
											.replaceAll("[^a-zA-Z0-9-]+", textBlockSeparator), 
											0, 
											VisConstants.MAX_NAME_TEXT_LENGTH),
									 textBlockSeparator);
	}
	
	
	public static void handleMalformedParameters(String errorMessage,
												 String errorPageTitle,
												 VitroRequest vitroRequest,
												 HttpServletRequest request,
												 HttpServletResponse response,
												 Log log)
			throws ServletException, IOException {

		Portal portal = vitroRequest.getPortal();

		request.setAttribute("error", errorMessage);

		RequestDispatcher requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
		request.setAttribute("bodyJsp", "/templates/visualization/visualization_error.jsp");
		request.setAttribute("portalBean", portal);
		request.setAttribute("title", errorPageTitle);

		try {
			requestDispatcher.forward(request, response);
		} catch (Exception e) {
			log.error("EntityEditController could not forward to view.");
			log.error(e.getMessage());
			log.error(e.getStackTrace());
		}
	}

	public static Map<String, Set<CoPINode>> getGrantYearToCoPI(
			CoPIData pINodesAndEdges) {
		

		Map<String, Set<CoPINode>> yearToCoPIs = new TreeMap<String, Set<CoPINode>>();
		
		CoPINode egoNode = pINodesAndEdges.getEgoNode();
		
		for (CoPINode currNode : pINodesAndEdges.getNodes()) {
					
				/*
				 * We have already printed the Ego Node info.
				 * */
				if (currNode != egoNode) {
					
					for (String year : currNode.getYearToGrantCount().keySet()) {
						
						Set<CoPINode> coPINodes;
						
						if (yearToCoPIs.containsKey(year)) {
							
							coPINodes = yearToCoPIs.get(year);
							coPINodes.add(currNode);
							
						} else {
							
							coPINodes = new HashSet<CoPINode>();
							coPINodes.add(currNode);
							yearToCoPIs.put(year, coPINodes);
						}
						
					}
					
				}
		}
		return yearToCoPIs;

	}

	public static Map<String, Integer> getYearToGrantCount(Set<Grant> pIGrants) {
		
    	/*
    	 * Create a map from the year to number of grants. Use the Grant's
    	 * parsedGrantStartYear to populate the data.
    	 * */
    	Map<String, Integer> yearToGrantCount = new TreeMap<String, Integer>();

    	for (Grant curr : pIGrants) {

    		/*
    		 * Increment the count because there is an entry already available for
    		 * that particular year.
    		 * 
    		 * I am pushing the logic to check for validity of year in "getGrantYear" itself
    		 * because,
    		 * 	1. We will be using getGra... multiple times & this will save us duplication of code
    		 * 	2. If we change the logic of validity of a grant year we would not have to make 
    		 * changes all throughout the codebase.
    		 * 	3. We are asking for a grant year & we should get a proper one or NOT at all.
    		 * */
    		String grantYear;
    		if (curr.getGrantStartYear() != null) { 
    			grantYear = curr.getGrantStartYear();
    		} else {
    			grantYear = curr.getParsedGrantStartYear();
    		}
    		
			if (yearToGrantCount.containsKey(grantYear)) {
    			yearToGrantCount.put(grantYear,
    									   yearToGrantCount
    									   		.get(grantYear) + 1);

    		} else {
    			yearToGrantCount.put(grantYear, 1);
    		}

    	}

		return yearToGrantCount;
		
	}

}
