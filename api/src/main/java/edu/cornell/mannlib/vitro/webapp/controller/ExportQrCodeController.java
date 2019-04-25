/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalysisContextImpl;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalyzer;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestInfo;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.DefaultObjectWrapper;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "ExportQrCodeController", urlPatterns = {"/qrcode"})
public class ExportQrCodeController extends FreemarkerHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ExportQrCodeController.class);
    private static final String TEMPLATE_DEFAULT = "foaf-person--exportQrCode.ftl";
	private static String VCARD_DATA_QUERY = ""
	+ "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
	+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"
	+ "SELECT DISTINCT ?firstName ?lastName ?email ?phone ?title \n"
	+ "WHERE { \n"
	+ " ?subject obo:ARG_2000028 ?vIndividual . \n"
	+ " ?vIndividual vcard:hasName ?vName . \n"
	+ " ?vName vcard:givenName ?firstName . \n"
	+ " ?vName vcard:familyName ?lastName . \n"
	+ " OPTIONAL { ?vIndividual vcard:hasEmail ?vEmail . \n"
	+ " ?vEmail vcard:email ?email . \n"
	+ " } \n"
	+ " OPTIONAL { ?vIndividual vcard:hasTelephone ?vPhone . \n"
	+ " ?vPhone vcard:telephone ?phone . \n"
	+ " } \n"
	+ " OPTIONAL { ?vIndividual vcard:hasTitle ?vTitle . \n"
	+ " ?vTitle vcard:title ?title . \n"
	+ " } \n"
	+ "} " ;
	private List<Map<String,String>> vcardData;
	private Map<String, String> qrData = null;

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
        	Individual individual = getIndividualFromRequest(vreq);

			qrData = generateQrData(individual, vreq);

            DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
            wrapper.setExposureLevel(BeansWrapper.EXPOSE_SAFE);

            Map<String, Object> body = new HashMap<String, Object>();
            body.put("individual", wrapper.wrap(IndividualTemplateModelBuilder.build(individual, vreq)));
            body.put("qrData", qrData);
            return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
        } catch (Throwable e) {
            log.error(e, e);
            return new ExceptionResponseValues(e);
        }
    }

	private Individual getIndividualFromRequest(VitroRequest vreq) {
		IndividualRequestInfo requestInfo = new IndividualRequestAnalyzer(vreq,
				new IndividualRequestAnalysisContextImpl(vreq)).analyze();
		return requestInfo.getIndividual();
	}

	@Override
    protected String getTitle(String siteName, VitroRequest vreq) {
        try {
            return "Export QR Code for " + getIndividualFromRequest(vreq).getRdfsLabel();
        } catch (Throwable e) {
            log.error(e, e);
            return "There was an error in the system. The individual could not be found";
        }
    }

    private Map<String, String> generateQrData(Individual individual, VitroRequest vreq) {

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
            String externalUrl = tempUrl ;
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

}
