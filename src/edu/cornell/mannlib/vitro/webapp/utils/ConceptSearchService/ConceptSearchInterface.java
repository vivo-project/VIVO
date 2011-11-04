/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

import java.util.Map;

import javax.servlet.ServletContext;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;

import org.json.JSONObject;

public interface ConceptSearchInterface{
    String doSearch(ServletContext context, VitroRequest vreq );
    
    String processResults(String searchEntry);
}
