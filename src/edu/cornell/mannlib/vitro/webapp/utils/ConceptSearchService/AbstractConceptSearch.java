/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddRoleToPersonTwoStageGenerator;

abstract public class AbstractConceptSearch implements ConceptSearchInterface{
	private Log log = LogFactory.getLog(AbstractConceptSearch.class);

    public String doSearch(ServletContext context, VitroRequest vreq ) {
    	String searchEntry = vreq.getParameter("searchTerm");
    	String results = processResults(searchEntry);
    	
    	return results;
    }
    
    abstract public String processResults(String searchEntry);
	
}