/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class IndividualTemplateModel extends BaseIndividualTemplateModel {

    private static final Log log = LogFactory.getLog(IndividualTemplateModel.class);
    
    private static final String FOAF = "http://xmlns.com/foaf/0.1/";
    private static final String PERSON_CLASS = FOAF + "Person";
    private static final String ORGANIZATION_CLASS = FOAF + "Organization";
    private static final String BASE_VISUALIZATION_URL = 
        UrlBuilder.getUrl(Route.VISUALIZATION_SHORT.path());
    private static String VCARD_DATA_QUERY = ""
        + "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
        + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>  \n"
        + "SELECT DISTINCT ?firstName ?lastName ?email ?phone ?title  \n"
        + "WHERE {  \n"
        + "    ?subject obo:ARG_2000028 ?vIndividual .  \n"
        + "    ?vIndividual vcard:hasName ?vName .   \n"
        + "    ?vName vcard:givenName ?firstName .  \n"
        + "    ?vName vcard:familyName ?lastName .  \n"
        + "    OPTIONAL { ?vIndividual vcard:hasEmail ?vEmail . \n"
        + "               ?vEmail vcard:email ?email . \n"
        + "    } \n"
        + "    OPTIONAL { ?vIndividual vcard:hasTelephone ?vPhone .   \n"
        + "               ?vPhone vcard:telephone ?phone .  \n"
        + "    } \n"
        + "    OPTIONAL { ?vIndividual vcard:hasTitle ?vTitle . \n"
        + "               ?vTitle vcard:title ?title . \n"
        + "    } \n"
        + "} "  ;
    
    private List<Map<String,String>>  vcardData;
    private Map<String, String> qrData = null;
    
    public IndividualTemplateModel(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
    }

    private Map<String, String> generateQrData() {
        
        try {
            String firstName = "";
            String lastName = "";
            String preferredTitle = "";
            String phoneNumber = "";
            String email = "";

            vcardData = getVcardData(individual, vreq);

            Map<String,String> qrData = new HashMap<String,String>();

            for (Map<String, String> map: vcardData) {        
                firstName = map.get("firstName");
                lastName = map.get("lastName");
                preferredTitle = map.get("title");
                phoneNumber = map.get("phone");
                email = map.get("email");
            }

            if(firstName != null &&  firstName.length() > 0)
                qrData.put("firstName", firstName);
            if(lastName != null &&  lastName.length() > 0)
                qrData.put("lastName", lastName);
            if(preferredTitle != null &&  preferredTitle.length() > 0)
                qrData.put("preferredTitle", preferredTitle);
            if(phoneNumber != null &&  phoneNumber.length() > 0)
                qrData.put("phoneNumber", phoneNumber);
            if(email != null &&  email.length() > 0)
                qrData.put("email", email);

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
		} catch (Exception e) {
			log.error("Failed getting QR code data", e);
			return null;
		}
    }
    
    private List<Map<String,String>>  getVcardData(Individual individual, VitroRequest vreq) {
        String queryStr = QueryUtils.subUriForQueryVar(VCARD_DATA_QUERY, "subject", individual.getURI());
        log.debug("queryStr = " + queryStr);
        List<Map<String,String>>  vcardData = new ArrayList<Map<String,String>>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                vcardData.add(QueryUtils.querySolutionToStringValueMap(soln));
            }
        } catch (Exception e) {
            log.error(e, e);
        }    
       
        return vcardData;
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

    public boolean person() {
        return isVClass(PERSON_CLASS);
    }
    
    public boolean organization() {
        return isVClass(ORGANIZATION_CLASS);        
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
    
    public Map<String, String> qrData() {
        if(qrData == null)
            qrData = generateQrData();
        return qrData;
    }

}
