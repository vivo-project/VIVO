package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public abstract class VisualizationRequestHandler {

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
	
	public abstract void generateVisualization(DataSource dataSource);
	
	public VitroRequest getVitroRequest() {
		return vitroRequest;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Log getLog() {
		return log;
	}

}
