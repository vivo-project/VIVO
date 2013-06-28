/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.searchresult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class IndividualSearchResult extends BaseIndividualSearchResult {

    private static final Log log = LogFactory.getLog(IndividualSearchResult.class);

    private static final String CORE = "http://vivoweb.org/ontology/core#";
       
    public IndividualSearchResult(Individual individual, VitroRequest vreq) {
    	super(individual, vreq);
    	log.info("Called Individual Search Result");
    }
    
    /* Access methods for templates */
    
    public String getPreferredTitle() {
    	log.info("Called get Title");
        return individual.getDataValue(CORE + "preferredTitle");
    }
    
    public String getEmail() {
    	log.info("Called get Email");
    	return individual.getDataValue(CORE + "email");
    }

}