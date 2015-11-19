/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

public class ListedIndividual extends BaseListedIndividual {
    private static final Log log = LogFactory.getLog(ListedIndividual.class);

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
    
    public ListedIndividual(Individual individual, VitroRequest vreq) {
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
