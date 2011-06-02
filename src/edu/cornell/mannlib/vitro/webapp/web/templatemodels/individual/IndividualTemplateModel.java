/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.SimpleOntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactorySDB;

public class IndividualTemplateModel extends BaseIndividualTemplateModel {

    private static final Log log = LogFactory.getLog(IndividualTemplateModel.class);
    
    private static final String FOAF = "http://xmlns.com/foaf/0.1/";
    private static final String CORE = "http://vivoweb.org/ontology/core#";
    private static final String PERSON_CLASS = FOAF + "Person";
    private static final String ORGANIZATION_CLASS = FOAF + "Organization";
    
    private Map<String, String> qrData = null;
    
    public IndividualTemplateModel(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
    }
    
    private String getBaseVisUrl() {
        return getUrl(Route.VISUALIZATION_SHORT.path());
    }
 
    private Map<String, String> generateQrData() {

        Map<String,String> qrData = new HashMap<String,String>();
        
        WebappDaoFactory wdf = vreq.getUnfilteredWebappDaoFactory();
        
        Collection<DataPropertyStatement> firstNames = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, FOAF + "firstName");
        Collection<DataPropertyStatement> lastNames = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, FOAF + "lastName");
        Collection<DataPropertyStatement> preferredTitles = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, CORE + "preferredTitle");
        Collection<DataPropertyStatement> phoneNumbers = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, CORE + "phoneNumber");
        Collection<DataPropertyStatement> emails = wdf.getDataPropertyStatementDao().getDataPropertyStatementsForIndividualByDataPropertyURI(individual, CORE + "email");

        if(firstNames != null && ! firstNames.isEmpty())
            qrData.put("firstName", firstNames.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(lastNames != null && ! lastNames.isEmpty())
            qrData.put("lastName", lastNames.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(preferredTitles != null && ! preferredTitles.isEmpty())
            qrData.put("preferredTitle", preferredTitles.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(phoneNumbers != null && ! phoneNumbers.isEmpty())
            qrData.put("phoneNumber", phoneNumbers.toArray(new DataPropertyStatement[firstNames.size()])[0].getData());
        if(emails != null && ! emails.isEmpty())
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
    
    
    /* Access methods for templates */

    public boolean isPerson() {
        return isVClass(PERSON_CLASS);
    }
    
    public boolean isOrganization() {
        return isVClass(ORGANIZATION_CLASS);        
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

    public String getMapOfScienceUrl() {
    	String mapOfScienceVisURL = getBaseVisUrl() + "/" + VisualizationFrameworkConstants.MAP_OF_SCIENCE_VIS_SHORT_URL + "/";    	
    	return getVisUrl(mapOfScienceVisURL);
    }
    
    public Map<String, String> doQrData() {
        if(qrData == null)
            qrData = generateQrData();
        return qrData;
    }


}
