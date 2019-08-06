/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.VitroAjaxController;

/**
 * Handle the AJAX functions that are specific to the "new" home page sections, at
 * this point just the mapping of geographic locations.
 */
@WebServlet(name = "HomePageAjax", urlPatterns = {"/homePageAjax"} )
public class HomePageAjaxController extends VitroAjaxController {
	private static final Log log = LogFactory
			.getLog(HomePageAjaxController.class);

	private static final String PARAMETER_ACTION = "action";

	@Override
	protected void doRequest(VitroRequest vreq, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String function = vreq.getParameter(PARAMETER_ACTION);
			if ("getGeoFocusLocations".equals(function)) {
				new GeoFocusMapLocations(this, vreq, resp).processRequest();
			}
			else if ("getGeoFocusResearcherCount".equals(function)) {
			    new GeoFocusResearcherCount(this, vreq, resp).processRequest();
			}
			else {
				resp.getWriter().write("[]");
			}
		} catch (Exception e) {
			log.error(e, e);
			resp.getWriter().write("[]");
		}
	}

}
