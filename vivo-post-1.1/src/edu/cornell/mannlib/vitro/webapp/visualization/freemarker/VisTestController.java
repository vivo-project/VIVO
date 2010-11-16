package edu.cornell.mannlib.vitro.webapp.visualization.freemarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;


public class VisTestController extends FreemarkerHttpServlet implements VisualizationRequestHandler {
	
	private static final String TEMPLATE_DEFAULT = "vistest.ftl";
	
	public void generateVisualization(VitroRequest vitroRequest, HttpServletRequest request, HttpServletResponse response, Log log, DataSource dataSource){
			
			return;
		}

	@Override
	protected ResponseValues processRequest(VitroRequest vitroRequest){
		
		Portal portal = vitroRequest.getPortal();
		Map<String, Object> body = new HashMap<String, Object>();
		
		String egoURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		String renderMode = vitroRequest.getParameter(VisualizationFrameworkConstants.RENDER_MODE_KEY);
		String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);
		
		List<String> parameters = new ArrayList<String>();
		
		parameters.add(egoURI);
		parameters.add(renderMode);
		parameters.add(visMode);
		
		body.put("parameters", parameters);
		body.put("title", getTitle("VIVO Vis"));
		
		return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
	}
	
}
