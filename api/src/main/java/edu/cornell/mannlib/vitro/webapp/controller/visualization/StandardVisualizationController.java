/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * Services a standard visualization request, which involves templates. This will return a simple
 * error message and a 501 if there is no jena Model.
 *
 * @author cdtank
 */
@SuppressWarnings("serial")
@WebServlet(name = "StandardVisualizationController", urlPatterns = {"/visualizationfm","/visualization"})
public class StandardVisualizationController extends FreemarkerHttpServlet {

	public static final String URL_ENCODING_SCHEME = "UTF-8";

	private static final Log log = LogFactory.getLog(StandardVisualizationController.class.getName());

    protected static final Syntax SYNTAX = Syntax.syntaxARQ;

    public static ServletContext servletContext;

    @Override
    protected AuthorizationRequest requiredActions(VitroRequest vreq) {
    	/*
    	 * Based on the query parameters passed via URI get the appropriate visualization
    	 * request handler.
    	 * */
    	VisualizationRequestHandler visRequestHandler =
    			getVisualizationRequestHandler(vreq);

    	if (visRequestHandler != null) {

    		AuthorizationRequest requiredPrivileges = visRequestHandler.getRequiredPrivileges();
			if (requiredPrivileges != null) {
    			return requiredPrivileges;
    		}
    	}
    	return super.requiredActions(vreq);
    }

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
    	/*
    	 * Based on the query parameters passed via URI get the appropriate visualization
    	 * request handler.
    	 * */
    	VisualizationRequestHandler visRequestHandler =
    			getVisualizationRequestHandler(vreq);

    	servletContext = getServletContext();

    	if (visRequestHandler != null) {

    		/*
        	 * Pass the query to the selected visualization request handler & render the vis.
        	 * Since the visualization content is directly added to the response object we are side-
        	 * effecting this method.
        	 * */
            return renderVisualization(vreq, visRequestHandler);

    	} else {
    		return UtilityFunctions.handleMalformedParameters(
    									"Visualization Query Error",
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

		Dataset dataset = setupJENADataSource(vitroRequest);

		if (dataset != null && visRequestHandler != null) {

        	try {
				return visRequestHandler.generateStandardVisualization(vitroRequest,
														log,
														dataset);
			} catch (MalformedQueryParametersException e) {
				return UtilityFunctions.handleMalformedParameters(
						"Standard Visualization Query Error - Individual Publication Count",
						e.getMessage(),
						vitroRequest);
			}

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


    	try {
    		visRequestHandler = VisualizationsDependencyInjector
    									.getVisualizationIDsToClassMap(getServletContext())
    											.get(visType);
    	} catch (NullPointerException nullKeyException) {

    		return null;
		}

		return visRequestHandler;
	}

	private Dataset setupJENADataSource(VitroRequest vreq) {

        log.debug("rdfResultFormat was: " + VisConstants.RDF_RESULT_FORMAT_PARAM);

        return vreq.getDataset();
	}

}

