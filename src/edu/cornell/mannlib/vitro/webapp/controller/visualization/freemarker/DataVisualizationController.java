/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelMaker;

import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;

/**
 * Services a visualization request. This will return a simple error message and a 501 if
 * there is no jena Model.
 *
 * @author cdtank
 */
@SuppressWarnings("serial")
public class DataVisualizationController extends VitroHttpServlet {

	public static final String URL_ENCODING_SCHEME = "UTF-8";

	private static final Log log = LogFactory.getLog(DataVisualizationController.class.getName());
	
    protected static final Syntax SYNTAX = Syntax.syntaxARQ;
    
    public static final String FILE_CONTENT_TYPE_KEY = "fileContentType";
    public static final String FILE_CONTENT_KEY = "fileContent";
    public static final String FILE_NAME_KEY = "fileName";
   
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    
		VitroRequest vreq = new VitroRequest(request);
	    	
    	/*
    	 * Based on the query parameters passed via URI get the appropriate visualization 
    	 * request handler.
    	 * */
    	VisualizationRequestHandler visRequestHandler = 
    			getVisualizationRequestHandler(vreq);
    	
    	if (visRequestHandler != null) {
    	
    		/*
        	 * Pass the query to the selected visualization request handler & render the visualization.
        	 * Since the visualization content is directly added to the response object we are side-
        	 * effecting this method.
        	 * */
            try {
            	
            	Map<String, String> dataResponse = renderVisualization(vreq, visRequestHandler);
            	
                response.setContentType(dataResponse.get(FILE_CONTENT_TYPE_KEY));
                
                if (dataResponse.containsKey(FILE_NAME_KEY)) {
                	response.setHeader("Content-Disposition", 
                					   "attachment;filename=" + dataResponse.get(FILE_NAME_KEY));
                }
                
        		response.getWriter().write(dataResponse.get(FILE_CONTENT_KEY));
        		
				return;
				
			} catch (MalformedQueryParametersException e) {

	    		UtilityFunctions.handleMalformedParameters("Visualization Query Error",
						   e.getMessage(),
						   vreq,
						   request,
						   response,
						   log);
				
			}
			
            return;
            
    	} else {
    		
    		UtilityFunctions.handleMalformedParameters("Visualization Query Error",
    												   "Inappropriate query parameters were submitted.",
    												   vreq,
    												   request,
    												   response,
    												   log);
    		
    	}
    	
        
    }


	private Map<String, String> renderVisualization(
			VitroRequest vitroRequest,
			VisualizationRequestHandler visRequestHandler) 
			throws MalformedQueryParametersException {
		
		Model model = vitroRequest.getJenaOntModel(); // getModel()
        if (model == null) {
            
            String errorMessage = "This service is not supporeted by the current " 
            			+ "webapp configuration. A jena model is required in the " 
            			+ "servlet context.";

            log.error(errorMessage);
            
            throw new MalformedQueryParametersException(errorMessage);
        }
		
		DataSource dataSource = setupJENADataSource(model, vitroRequest);
        
		if (dataSource != null && visRequestHandler != null) {
				return visRequestHandler.generateDataVisualization(vitroRequest, 
														log, 
														dataSource);
        	
        } else {
        	
    		String errorMessage = "Data Model Empty &/or Inappropriate " 
    									+ "query parameters were submitted. ";
    		
    		throw new MalformedQueryParametersException(errorMessage);
			
        }
	}

	private VisualizationRequestHandler getVisualizationRequestHandler(
				VitroRequest vitroRequest) {
		
		String visType = vitroRequest.getParameter(VisualizationFrameworkConstants
																	.VIS_TYPE_KEY);
    	VisualizationRequestHandler visRequestHandler = null;
    	
    	
    	try {
    		visRequestHandler = VisualizationsDependencyInjector
									.getVisualizationIDsToClassMap(getServletContext())
											.get(visType);
    	} catch (NullPointerException nullKeyException) {

    		return null;
		}
    	
		return visRequestHandler;
	}

	private DataSource setupJENADataSource(Model model, VitroRequest vreq) {

        log.debug("rdfResultFormat was: " + VisConstants.RDF_RESULT_FORMAT_PARAM);

        DataSource dataSource = DatasetFactory.create();
        ModelMaker maker = (ModelMaker) getServletContext().getAttribute("vitroJenaModelMaker");

    	dataSource.setDefaultModel(model);

        return dataSource;
	}

}

