/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.controller.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalysisContextImpl;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalyzer;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestInfo;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;


public class QrCodeDetails extends AbstractAjaxResponder {

    private static final Log log = LogFactory.getLog(QrCodeDetails.class.getName());
    private List<Map<String,String>>  vcardData;
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

	public QrCodeDetails(HttpServlet parent, VitroRequest vreq,
			HttpServletResponse resp) {
		super(parent, vreq, resp);
    }

	@Override
	public String prepareResponse() throws IOException {
		try {
            Individual individual = getIndividualFromRequest(vreq);
            String firstName = "";
            String lastName = "";
            String preferredTitle = "";
            String phoneNumber = "";
            String email = "";
			String response = "[";

			vcardData = getVcardData(individual, vreq);

            for (Map<String, String> map: vcardData) {
                firstName = map.get("firstName");
                lastName = map.get("lastName");
                preferredTitle = map.get("title");
                phoneNumber = map.get("phone");
                email = map.get("email");
            }

/*
            String tempUrl = vreq.getRequestURL().toString();
            String prefix = "http://";
            tempUrl = tempUrl.substring(0, tempUrl.replace(prefix, "").indexOf("/") + prefix.length());
            String profileUrl = UrlBuilder.getIndividualProfileUrl(individual, vreq);
            String externalUrl = tempUrl + profileUrl;
*/
            if (firstName != null &&  firstName.length() > 0) {
                response += "{\"firstName\": \"" + firstName + "\"},";
			}
			else {
				response += "{\"firstName\": \"\"},";
			}
            if (lastName != null &&  lastName.length() > 0) {
                response += "{\"lastName\": \"" + lastName + "\"},";
			}
			else {
				response += "{\"lastName\": \"\"},";
			}
            if (preferredTitle != null &&  preferredTitle.length() > 0) {
                response += "{\"preferredTitle\": \"" + preferredTitle + "\"},";
			}
			else {
			    response += "{\"preferredTitle\": \"\"},";
			}
            if (phoneNumber != null &&  phoneNumber.length() > 0) {
                response += "{\"phoneNumber\": \"\"},";
			}
			else {
			    response += "{\"phoneNumber\": \"\"},";
			}
            if (email != null &&  email.length() > 0) {
                response += "{\"email\": \"" + email + "\"},";
			}
			else {
			    response += "{\"email\": \"\"},";
			}

			response += " ]";
			response = response.replace(", ]"," ]");

			log.debug(response);
			return response;
		} catch (Exception e) {
			log.error("Could not retrieve vCard information", e);
			return EMPTY_RESPONSE;
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

	private Individual getIndividualFromRequest(VitroRequest vreq) {
		IndividualRequestInfo requestInfo = new IndividualRequestAnalyzer(vreq,
				new IndividualRequestAnalysisContextImpl(vreq)).analyze();
		return requestInfo.getIndividual();
	}

}
