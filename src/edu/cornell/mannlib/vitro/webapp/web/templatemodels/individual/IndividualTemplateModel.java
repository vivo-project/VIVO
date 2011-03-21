/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;

public class IndividualTemplateModel extends BaseIndividualTemplateModel {

    private static final Log log = LogFactory.getLog(IndividualTemplateModel.class);
    
    public IndividualTemplateModel(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
    }
    
    private String getBaseVisUrl() {
        return getUrl(Route.VISUALIZATION.path(), "uri", getUri());
    }
    
    private String getVisUrl(ParamMap params) {
        String baseVisUrl = getBaseVisUrl();
        return UrlBuilder.addParams(baseVisUrl, params);
    }
    
    private String getVisUrl(String...params) {
        return getVisUrl(new ParamMap(params));
    }
    
    private String getPersonVisUrl(ParamMap params) {
        if (!isPerson()) {
            return null;
        }
        ParamMap paramMap = new ParamMap("vis", "person_level");
        paramMap.put(params);
        return getVisUrl(paramMap);
    }
    
      
    /* Access methods for templates */

    public boolean isPerson() {
        return isVClass("http://xmlns.com/foaf/0.1/Person");
    }
    
    public boolean isOrganization() {
        return isVClass("http://xmlns.com/foaf/0.1/Organization");        
    }
    
    public String getCoAuthorVisUrl() {
        return getPersonVisUrl(new ParamMap("vis_mode", "coauthor"));
    }
    
    public String getCoInvestigatorVisUrl() {
        return getPersonVisUrl(new ParamMap("vis_mode", "copi"));
    }
    
    public String getTemporalGraphUrl() {
        if (!isOrganization()) {
            return null;
        }
        return getVisUrl("vis", "entity_comparison");
    }
}
