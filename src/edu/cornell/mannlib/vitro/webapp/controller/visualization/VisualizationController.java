/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelMaker;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * Services a visualization request. This will return a simple error message and a 501 if
 * there is no jena Model.
 *
 * @author cdtank
 */
@SuppressWarnings("serial")
public class VisualizationController extends BaseEditController {

	private Map<String, VisualizationRequestHandler> visualizationIDsToClass;

	public static final String URL_ENCODING_SCHEME = "UTF-8";

	private static final Log log = LogFactory.getLog(VisualizationController.class.getName());

    protected static final Syntax SYNTAX = Syntax.syntaxARQ;

    /* This method is overridden to inject vis dependencies i.e. the vis algorithms that are 
     * being implemented into the vis controller. Modified Dependency Injection pattern is 
     * used here. XML file containing the location of all the vis is saved in accessible folder. 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
    	super.init();
    	try {
			
			String resourcePath = 
				getServletContext()
					.getRealPath(VisualizationFrameworkConstants
							.RELATIVE_LOCATION_OF_VISUALIZATIONS_BEAN);
			
			ApplicationContext context = new ClassPathXmlApplicationContext(
												"file:" + resourcePath);

			BeanFactory factory = context;
			
			VisualizationInjector visualizationInjector = 
					(VisualizationInjector) factory.getBean("visualizationInjector");
			
			visualizationIDsToClass = visualizationInjector.getVisualizationIDToClass();

		} catch (Exception e) {
			log.error(e);
		}
    }
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        this.doGet(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	super.doGet(request, response);

    	VitroRequest vitroRequest = new VitroRequest(request);
    	
    	/*
    	 * Based on the query parameters passed via URI get the appropriate visualization 
    	 * request handler.
    	 * */
    	VisualizationRequestHandler visRequestHandler = 
    			getVisualizationRequestHandler(request, response, vitroRequest);
    	
    	/*
    	 * Pass the query to the selected visualization request handler & render the visualization.
    	 * Since the visualization content is directly added to the response object we are side-
    	 * effecting this method.
    	 * */
        renderVisualization(request, response, vitroRequest, visRequestHandler);

        return;
    }


	private void renderVisualization(HttpServletRequest request,
									 HttpServletResponse response, 
									 VitroRequest vitroRequest,
									 VisualizationRequestHandler visRequestHandler)
			throws ServletException, IOException {
		
		DataSource dataSource = setupJENADataSource(request,
        											response,
        											vitroRequest);
        if (dataSource != null && visRequestHandler != null) {
        	
        	visRequestHandler.generateVisualization(vitroRequest, 
        											request, 
        											response, 
        											log, 
        											dataSource);
        	
        } else {
        	
    		String errorMessage = "Data Model Empty &/or Inappropriate " 
    									+ "query parameters were submitted. ";
    		
			handleMalformedParameters(errorMessage, 
					  request, 
					  response);
    		
			log.error(errorMessage);
        }
	}

	private VisualizationRequestHandler getVisualizationRequestHandler(
				HttpServletRequest request, 
				HttpServletResponse response,
				VitroRequest vitroRequest) 
			throws ServletException, IOException {
		
		String visType = vitroRequest.getParameter(VisualizationFrameworkConstants
																	.VIS_TYPE_KEY);
    	VisualizationRequestHandler visRequestHandler = null;
    	try {
    		visRequestHandler = visualizationIDsToClass.get(visType);
    	} catch (NullPointerException nullKeyException) {

    		/*
    		 * This is side-effecting because the error content is directly 
    		 * added to the request object. From where it is redirected to
    		 * the error page.
    		 * */
    		handleMalformedParameters("Inappropriate query parameters were submitted. ", 
					  request, 
					  response);
		}
		return visRequestHandler;
	}

	private DataSource setupJENADataSource(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq) {

		Model model = vreq.getJenaOntModel(); // getModel()
        if (model == null) {
            doNoModelInContext(request, response);
            return null;
        }

        log.debug("rdfResultFormat was: " + VisConstants.RDF_RESULT_FORMAT_PARAM);

        DataSource dataSource = DatasetFactory.create();
        ModelMaker maker = (ModelMaker) getServletContext().getAttribute("vitroJenaModelMaker");

    	dataSource.setDefaultModel(model);

        return dataSource;
	}

    private void doNoModelInContext(HttpServletRequest request, HttpServletResponse res) {
        try {
            res.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            ServletOutputStream sos = res.getOutputStream();
            sos.println("<html><body>this service is not supporeted by the current " 
            			+ "webapp configuration. A jena model is required in the " 
            			+ "servlet context.</body></html>");
            
        } catch (IOException e) {
            log.error("Could not write to ServletOutputStream");
        }
    }

    private void handleMalformedParameters(String errorMessage, HttpServletRequest request,
    					HttpServletResponse response)
    	throws ServletException, IOException {

        VitroRequest vreq = new VitroRequest(request);
        Portal portal = vreq.getPortal();

        request.setAttribute("error", errorMessage);

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp", "/templates/visualization/visualization_error.jsp");
        request.setAttribute("portalBean", portal);
        request.setAttribute("title", "Visualization Query Error");

        try {
            requestDispatcher.forward(request, response);
        } catch (Exception e) {
            log.error("EntityEditController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }
    }

}

