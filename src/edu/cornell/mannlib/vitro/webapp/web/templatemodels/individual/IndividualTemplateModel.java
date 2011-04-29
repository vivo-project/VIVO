/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class IndividualTemplateModel extends BaseIndividualTemplateModel {

    private static final Log log = LogFactory.getLog(IndividualTemplateModel.class);
    private Map<String, String> qrData = null;
    
    public IndividualTemplateModel(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
    }
    
    private String getBaseVisUrl() {
        return getUrl(Route.VISUALIZATION_SHORT.path());
    }
    
    /* Access methods for templates */

    public boolean isPerson() {
        return isVClass("http://xmlns.com/foaf/0.1/Person");
    }
    
    public boolean isOrganization() {
        return isVClass("http://xmlns.com/foaf/0.1/Organization");        
    }
    
    public String getCoAuthorVisUrl() {
    	
        String coauthorVisURL = getBaseVisUrl() + "/" + VisualizationFrameworkConstants.COAUTHORSHIP_VIS_SHORT_URL + "/";
    	
    	return getVisUrl(coauthorVisURL);
    }

    public String getCoInvestigatorVisUrl() {
    	
    	String coinvestigatorVisURL = getBaseVisUrl() + "/" + VisualizationFrameworkConstants.COINVESTIGATOR_VIS_SHORT_URL + "/";
    	
    	return getVisUrl(coinvestigatorVisURL);
    }

	private String getVisUrl(String coinvestigatorVisURL) {
		boolean isUsingDefaultNameSpace = UrlBuilder.isUriInDefaultNamespace(
												getUri(),
												vreq);
        
        if (isUsingDefaultNameSpace) {
        	
        	return coinvestigatorVisURL + getLocalName();
        	
        } else {
        	
        	return UrlBuilder.addParams(
        			coinvestigatorVisURL, 
        			new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY, getUri())); 
        }
	}

    public String getTemporalGraphUrl() {
        if (!isOrganization()) {
            return null;
        }
        
        String temporalVisURL = getBaseVisUrl() + "/" + VisualizationFrameworkConstants.PUBLICATION_TEMPORAL_VIS_SHORT_URL + "/";
    	
    	return getVisUrl(temporalVisURL);
    }

    public String getSelfEditingId() {
        String id = null;
        String idMatchingProperty = ConfigurationProperties.getBean(getServletContext()).getProperty("selfEditing.idMatchingProperty");
        if (! StringUtils.isBlank(idMatchingProperty)) {
            // Use assertions model to side-step filtering. We need to get the value regardless of whether the property
            // is visible to the current user.
            WebappDaoFactory wdf = vreq.getAssertionsWebappDaoFactory();
            Collection<DataPropertyStatement> ids = 
                wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, idMatchingProperty);
            if (ids.size() > 0) {
                id = ids.iterator().next().getData();
            }
        }
        return id;
    }
    
    public Map<String, String> getQrData() {
        if(qrData == null)
            qrData = generateQrData();
        return qrData;
    }

    private Map<String, String> generateQrData() {
        String core = "http://vivoweb.org/ontology/core#";
        String foaf = "http://xmlns.com/foaf/0.1/";

        Map<String,String> qrData = new HashMap<String,String>();
        WebappDaoFactory wdf = vreq.getAssertionsWebappDaoFactory();
        Collection<DataPropertyStatement> firstNames = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, foaf + "firstName");
        Collection<DataPropertyStatement> lastNames = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, foaf + "lastName");
        Collection<DataPropertyStatement> preferredTitles = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, core + "preferredTitle");
        Collection<DataPropertyStatement> phoneNumbers = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, core + "phoneNumber");
        Collection<DataPropertyStatement> emails = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, core + "email");

        if(firstNames.size() > 0)
            qrData.put("firstName", firstNames.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(lastNames.size() > 0)
            qrData.put("lastName", lastNames.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(preferredTitles.size() > 0)
            qrData.put("preferredTitle", preferredTitles.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(phoneNumbers.size() > 0)
            qrData.put("phoneNumber", phoneNumbers.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(emails.size() > 0)
            qrData.put("email", emails.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());

        String tempUrl = vreq.getRequestURL().toString();
        String prefix = "http://";
        tempUrl = tempUrl.substring(0, tempUrl.replace(prefix, "").indexOf("/") + prefix.length());
        String profileUrl = getProfileUrl();
        String externalUrl = tempUrl + profileUrl;
        qrData.put("externalUrl", externalUrl);

        String individualUri = individual.getURI();
        String contextPath = vreq.getContextPath();
        qrData.put("exportQrCodeUrl", contextPath + "/qrcode?uri=" + UrlBuilder.urlEncode(individualUri));
        
        qrData.put("aboutQrCodesUrl", contextPath + "/qrcode/about");
        
        return qrData;
    }
}
