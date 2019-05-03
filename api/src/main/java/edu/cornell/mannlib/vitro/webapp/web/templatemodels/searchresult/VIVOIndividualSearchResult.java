/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.searchresult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

public class VIVOIndividualSearchResult extends IndividualSearchResult {
    private static final Log log = LogFactory.getLog(VIVOIndividualSearchResult.class);

    private static String VCARD_DATA_QUERY = ""
            + "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
            + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>  \n"
            + "SELECT DISTINCT ?email ?title  \n"
            + "WHERE {  \n"
            + "    ?subject obo:ARG_2000028 ?vIndividual .  \n"
            + "    OPTIONAL { ?subject obo:ARG_2000028 ?vIndividual . \n"
            + "               ?vIndividual vcard:hasEmail ?vEmail . \n"
            + "               ?vEmail vcard:email ?email . \n"
            + "    } \n"
            + "    OPTIONAL { ?subject obo:ARG_2000028 ?vIndividual .\n"
            + "               ?vIndividual vcard:hasTitle ?vTitle . \n"
            + "               ?vTitle vcard:title ?title . \n"
            + "    } \n"
            + "} "  ;

    private String email = "";
    private String title = "";

    public VIVOIndividualSearchResult(Individual individual, VitroRequest vreq) {
    	super(individual, vreq);
    	log.debug("Called Individual Search Result");
    	findVcardInfo();
    }

    private void findVcardInfo() {
        String queryStr = QueryUtils.subUriForQueryVar(VCARD_DATA_QUERY, "subject", individual.getURI());
        log.debug("queryStr = " + queryStr);
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String t = QueryUtils.nodeToString( soln.get("title"));
                if (StringUtils.isNotBlank(t)) {
                	title = t;
                }
                String em = QueryUtils.nodeToString( soln.get("email"));
                if (StringUtils.isNotBlank(em)) {
                	email = em;
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }


    /* Access methods for templates */

    public String getPreferredTitle() {
    	return title;
    }

    public String getEmail() {
    	return email;
    }

    public static void register() {
        IndividualSearchResult.registerResultClass(VIVOIndividualSearchResult.class);
    }
}
