/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.individuallist;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

/**
 * Wrap an Individual in a JSON object for display by the script.
 * 
 * This overrides the Vitro version so we can have more info in the display.
 */
public class IndividualJsonWrapper {
	private static final Log log = LogFactory
			.getLog(IndividualJsonWrapper.class);

	private static String VCARD_DATA_QUERY = ""
			+ "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
			+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>  \n"
			+ "SELECT DISTINCT ?title  \n" + "WHERE {  \n"
			+ "    ?subject obo:ARG_2000028 ?vIndividual .  \n"
			+ "    ?vIndividual vcard:hasTitle ?vTitle . \n"
			+ "    ?vTitle vcard:title ?title . \n" + "} ";

	static JSONObject packageIndividualAsJson(VitroRequest vreq, Individual ind)
			throws JSONException {
		// need an unfiltered dao to get firstnames and lastnames
		WebappDaoFactory fullWdf = vreq.getUnfilteredWebappDaoFactory();

		JSONObject jo = new JSONObject();
		jo.put("URI", ind.getURI());
		jo.put("label", ind.getRdfsLabel());
		jo.put("name", ind.getName());
		jo.put("thumbUrl", ind.getThumbUrl());
		jo.put("imageUrl", ind.getImageUrl());
		jo.put("profileUrl", UrlBuilder.getIndividualProfileUrl(ind, vreq));
		jo.put("mostSpecificTypes", getMostSpecificTypes(ind, fullWdf));
		jo.put("preferredTitle", findPreferredTitle(vreq, ind));
		return jo;
	}

	private static String findPreferredTitle(VitroRequest vreq, Individual ind) {
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

	public static Collection<String> getMostSpecificTypes(
			Individual individual, WebappDaoFactory wdf) {
		ObjectPropertyStatementDao opsDao = wdf.getObjectPropertyStatementDao();
		Map<String, String> mostSpecificTypes = opsDao
				.getMostSpecificTypesInClassgroupsForIndividual(individual
						.getURI());
		return mostSpecificTypes.values();
	}

}
