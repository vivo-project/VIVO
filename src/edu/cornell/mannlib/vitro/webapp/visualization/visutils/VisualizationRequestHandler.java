/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public interface VisualizationRequestHandler {

	void generateVisualization(VitroRequest vitroRequest,
							   HttpServletRequest request, 
							   HttpServletResponse response, 
							   Log log, 
							   DataSource dataSource);
	
}
