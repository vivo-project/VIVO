/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;
import freemarker.template.Configuration;

/**
 * Services a visualization request. This will return a simple error message and a 501 if
 * there is no jena Model.
 *
 * @author cdtank
 */
@SuppressWarnings("serial")
public class VisualizationController extends FreemarkerHttpServlet {

	private Map<String, VisualizationRequestHandler> visualizationIDsToClass;

	public static final String URL_ENCODING_SCHEME = "UTF-8";

	private static final Log log = LogFactory.getLog(VisualizationController.class.getName());
	
    protected static final Syntax SYNTAX = Syntax.syntaxARQ;
   
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    
		VitroRequest vreq = new VitroRequest(request);
	    	
	    String renderMode = vreq.getParameter(VisualizationFrameworkConstants
													.RENDER_MODE_KEY);
    
	    if (StringUtils.equalsIgnoreCase(renderMode, VisualizationFrameworkConstants.DYNAMIC_RENDER_MODE)) {
	    	
	    	Configuration config = getConfig(vreq);
	    	TemplateResponseValues trv = (TemplateResponseValues) processRequest(vreq);
	    	writeTemplate(trv.getTemplateName(), trv.getMap(), config, request, response);
	    	
	    } else {
	    	super.doGet(request, response);
	    }
    
    
	}


    
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
							.RELATIVE_LOCATION_OF_FM_VISUALIZATIONS_BEAN);
			
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
    protected ResponseValues processRequest(VitroRequest vreq) {
        
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
            return renderVisualization(vreq, visRequestHandler);
            
    	} else {
    		
    		return UtilityFunctions.handleMalformedParameters("Visualization Query Error", 
    														  "Inappropriate query parameters were submitted.", 
    														  vreq);
    		
    	}
    	
        
    }


	private ResponseValues renderVisualization(VitroRequest vitroRequest,
									 VisualizationRequestHandler visRequestHandler) {
		
		Model model = vitroRequest.getJenaOntModel(); // getModel()
        if (model == null) {
            
            String errorMessage = "This service is not supporeted by the current " 
            			+ "webapp configuration. A jena model is required in the " 
            			+ "servlet context.";

            log.error(errorMessage);
            
            return UtilityFunctions.handleMalformedParameters("Visualization Query Error", 
            												  errorMessage, 
            												  vitroRequest);
			
        }
		
		DataSource dataSource = setupJENADataSource(model, vitroRequest);
        
		if (dataSource != null && visRequestHandler != null) {
        	
        	return visRequestHandler.generateVisualization(vitroRequest, 
        											log, 
        											dataSource);
        	
        } else {
        	
    		String errorMessage = "Data Model Empty &/or Inappropriate " 
    									+ "query parameters were submitted. ";
    		
    		log.error(errorMessage);
    		
    		return UtilityFunctions.handleMalformedParameters("Visualization Query Error", 
    														  errorMessage, 
    														  vitroRequest);
    		
			
        }
	}

	private VisualizationRequestHandler getVisualizationRequestHandler(
				VitroRequest vitroRequest) {
		
		String visType = vitroRequest.getParameter(VisualizationFrameworkConstants
																	.VIS_TYPE_KEY);
    	VisualizationRequestHandler visRequestHandler = null;
    	
    	System.out.println(visType + " -->  " + visualizationIDsToClass);
    	
    	try {
    		visRequestHandler = visualizationIDsToClass.get(visType);
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

