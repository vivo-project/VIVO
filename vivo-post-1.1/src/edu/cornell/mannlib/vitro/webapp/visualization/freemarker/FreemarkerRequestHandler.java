package edu.cornell.mannlib.vitro.webapp.visualization.freemarker;

import java.io.IOException;

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
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class FreemarkerRequestHandler implements VisualizationRequestHandler {
	
	public void generateVisualization(VitroRequest vitroRequest, HttpServletRequest request, HttpServletResponse response, Log log, DataSource dataSource){
		
		String egoURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		String renderMode = vitroRequest.getParameter(VisualizationFrameworkConstants.RENDER_MODE_KEY);
		String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);
		
		try{
			if (VisualizationFrameworkConstants.DATA_RENDER_MODE
					.equalsIgnoreCase(renderMode)) {
						return;
			}else {
				
				RequestDispatcher requestDispatcher = null;
				prepareStandaloneResponse(
						egoURI, 
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
			
		}catch(Exception e){
			try {
				UtilityFunctions.handleMalformedParameters(
						e.getMessage(), 
						"Visualization Query Error - Freemarker Request Handler",
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

	private void prepareStandaloneResponse(String egoURI,
			VitroRequest vitroRequest, HttpServletRequest request) {
		
		Portal portal = vitroRequest.getPortal();

		request.setAttribute("egoURIParam", egoURI);

		String title = "";
		
		request.setAttribute("title", title + "Freemarker Test");
		request.setAttribute("portalBean", portal);
		request.setAttribute("scripts",
				"/templates/visualization/person_level_inject_head.jsp");
		request.setAttribute("bodyJsp",
				"/templates/visualization/co_authorship.jsp");
		
	}
}
