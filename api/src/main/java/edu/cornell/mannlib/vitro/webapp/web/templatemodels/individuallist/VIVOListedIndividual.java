/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

public class VIVOListedIndividual extends ListedIndividual {
    private static final Log log = LogFactory.getLog(VIVOListedIndividual.class);

    private static String VCARD_DATA_QUERY = ""
            + "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
            + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>  \n"
            + "SELECT DISTINCT ?title  \n"
            + "WHERE {  \n"
            + "    ?subject obo:ARG_2000028 ?vIndividual .  \n"
            + "    ?vIndividual vcard:hasTitle ?vTitle . \n"
            + "    ?vTitle vcard:title ?title . \n"
            + "} "  ;

    private final String title;

    VIVOListedIndividual(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
        title = findPreferredTitle();
    }

    private String findPreferredTitle() {
        String queryStr = QueryUtils.subUriForQueryVar(VCARD_DATA_QUERY, "subject", individual.getURI());
        log.debug("queryStr = " + queryStr);
        String value = "";
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String t = QueryUtils.nodeToString( soln.get("title"));
                if (StringUtils.isNotBlank(t)) {
                	value = t;
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return value;
    }

    /* Template properties */

    public String getPreferredTitle() {
        return title;
    }

}
