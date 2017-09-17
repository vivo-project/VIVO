/* $This file is distributed under the terms of the license in LICENSE$ */

package org.vivoweb.webapp.startup;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.individuallist.IndividualJsonWrapper;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class JSONWrapperSetup implements ServletContextListener {
    private static final Log log = LogFactory.getLog(JSONWrapperSetup.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        IndividualJsonWrapper.setAddJSONFields(new IndividualJsonWrapper.AddJSONFields() {
            @Override
            public void add(ObjectNode jo, VitroRequest vreq, Individual ind) {
                jo.put("preferredTitle", findPreferredTitle(vreq, ind));
            }

            private String VCARD_DATA_QUERY = ""
                    + "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
                    + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>  \n"
                    + "SELECT DISTINCT ?title  \n" + "WHERE {  \n"
                    + "    ?subject obo:ARG_2000028 ?vIndividual .  \n"
                    + "    ?vIndividual vcard:hasTitle ?vTitle . \n"
                    + "    ?vTitle vcard:title ?title . \n" + "} ";

            private String findPreferredTitle(VitroRequest vreq, Individual ind) {
                String queryStr = QueryUtils.subUriForQueryVar(VCARD_DATA_QUERY,
                        "subject", ind.getURI());
                log.debug("queryStr = " + queryStr);
                String value = "";
                try {
                    ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
                    while (results.hasNext()) {
                        QuerySolution soln = results.nextSolution();
                        String t = QueryUtils.nodeToString(soln.get("title"));
                        if (StringUtils.isNotBlank(t)) {
                            value = t;
                        }
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
                return value;
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
