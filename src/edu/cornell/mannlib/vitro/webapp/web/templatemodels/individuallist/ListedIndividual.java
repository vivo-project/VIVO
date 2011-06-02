/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.BaseListedIndividual;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.ListedIndividual;

public class ListedIndividual extends BaseListedIndividual {

    private static final Log log = LogFactory.getLog(ListedIndividual.class);

    private static final String CORE = "http://vivoweb.org/ontology/core#";
    
    public ListedIndividual(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
    }
    
    /* Access methods for templates */
    
    public String getPreferredTitle() {
        return cleanTextForDisplay( individual.getDataValue(CORE + "preferredTitle") );
    }
    
    // Add method to get core:webpages
    
}
