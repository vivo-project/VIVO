/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.controller.ajax;

import java.io.IOException;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.VitroAjaxController;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

public class GeoFocusResearcherCount extends AbstractAjaxResponder {

    private static final Log log = LogFactory.getLog(GeoFocusResearcherCount.class.getName());
    private List<Map<String,String>>  geoFocusCount;
    private static String GEO_FOCUS_COUNT_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#>  \n"
        + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>  \n"
        + "SELECT DISTINCT (COUNT(DISTINCT ?person) AS ?count)  \n"
        + "WHERE {  \n"
        + "    ?person a foaf:Person .  \n"
        + "    ?person core:geographicFocus ?focus   \n"
        + "}" ;
    
	public GeoFocusResearcherCount(HttpServlet parent, VitroRequest vreq,
			HttpServletResponse resp) {
		super(parent, vreq, resp);
    }

	@Override
	public String prepareResponse() throws IOException, JSONException {
		try {
            geoFocusCount = getGeoFocusCount(vreq);
            
            String response = "{ ";
            
            for (Map<String, String> map: geoFocusCount) {
                String theCount  = map.get("count");
                response += "\"count\": \"" + theCount + "\"";
            }
			response += " }";
			log.debug(response);
			return response;
		} catch (Exception e) {
			log.error("Failed geographic focus count", e);
			return EMPTY_RESPONSE;
		}
	}
           
    private List<Map<String,String>>  getGeoFocusCount(VitroRequest vreq) {
          
        String queryStr = GEO_FOCUS_COUNT_QUERY;
        log.debug("queryStr = " + queryStr);
        List<Map<String,String>>  count = new ArrayList<Map<String,String>>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                count.add(QueryUtils.querySolutionToStringValueMap(soln));
            }
        } catch (Exception e) {
            log.error(e, e);
        }    
       
        return count;
    }
}
