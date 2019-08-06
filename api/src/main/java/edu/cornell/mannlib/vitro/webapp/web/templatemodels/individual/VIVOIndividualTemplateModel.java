/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;

public class VIVOIndividualTemplateModel extends IndividualTemplateModel {

    private static final Log log = LogFactory.getLog(VIVOIndividualTemplateModel.class);

    private static final String FOAF = "http://xmlns.com/foaf/0.1/";
    private static final String PERSON_CLASS = FOAF + "Person";
    private static final String AWARD_CLASS = "http://vivoweb.org/ontology/core#Award";
    private static final String DEGREE_CLASS = "http://vivoweb.org/ontology/core#AcademicDegree";
    private static final String CONTACT_CLASS = "http://purl.obolibrary.org/obo/ARG_2000376";
    private static final String CREDENTIAL_CLASS = "http://vivoweb.org/ontology/core#Credential";
    private static final String DTP_CLASS = "http://vivoweb.org/ontology/core#DateTimeValuePrecision";
    private static final String ORGANIZATION_CLASS = FOAF + "Organization";
    private static final String EVENT_CLASS = "http://purl.org/NET/c4dm/event.owl#Event";
    private static final String INFO_CONTENT_ENTITY_CLASS = "http://purl.obolibrary.org/obo/IAO_0000030";
    private static final String BASE_VISUALIZATION_URL =
        UrlBuilder.getUrl(Route.VISUALIZATION_SHORT.path());

    VIVOIndividualTemplateModel(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
    }

    private String getVisUrl(String visPath) {
        String visUrl;
        boolean isUsingDefaultNameSpace = UrlBuilder.isUriInDefaultNamespace(
                                                getUri(),
                                                vreq);

        if (isUsingDefaultNameSpace) {
            visUrl = visPath + getLocalName();
        } else {
            visUrl = UrlBuilder.addParams(
                    visPath,
                    new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY, getUri()));
        }

        return visUrl;
    }

    /* Template methods (for efficiency, not pre-computed) */
    public boolean conceptSubclass() {
        return isVClass(AWARD_CLASS) || isVClass(DEGREE_CLASS) || isVClass(CONTACT_CLASS) || isVClass(CREDENTIAL_CLASS) || isVClass(DTP_CLASS);
    }

    public boolean person() {
        return isVClass(PERSON_CLASS);
    }

    public boolean organization() {
        return isVClass(ORGANIZATION_CLASS);
    }

    public boolean event() {
        return isVClass(EVENT_CLASS);
    }

    public boolean infoContentEntity() {
        return isVClass(INFO_CONTENT_ENTITY_CLASS);
    }

    public String coAuthorVisUrl() {
        String url = BASE_VISUALIZATION_URL + "/" + VisualizationFrameworkConstants.COAUTHORSHIP_VIS_SHORT_URL + "/";
    	return getVisUrl(url);
    }

    public String coInvestigatorVisUrl() {
    	String url =
    	    BASE_VISUALIZATION_URL + "/" + VisualizationFrameworkConstants.COINVESTIGATOR_VIS_SHORT_URL + "/";
    	return getVisUrl(url);
    }

    public String temporalGraphUrl() {
        String url =
            BASE_VISUALIZATION_URL + "/" + VisualizationFrameworkConstants.PUBLICATION_TEMPORAL_VIS_SHORT_URL + "/";
    	return getVisUrl(url);
    }

    public String mapOfScienceUrl() {
    	String url =
    	    BASE_VISUALIZATION_URL + "/" + VisualizationFrameworkConstants.MAP_OF_SCIENCE_VIS_SHORT_URL + "/";
    	return getVisUrl(url);
    }

}
