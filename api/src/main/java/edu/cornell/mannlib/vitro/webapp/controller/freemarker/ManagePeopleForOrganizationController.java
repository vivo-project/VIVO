/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

import javax.servlet.annotation.WebServlet;


@WebServlet(name = "ManagePeopleForOrganizationController", urlPatterns = {"/managePeople"} )
public class ManagePeopleForOrganizationController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ManagePeopleForOrganizationController.class.getName());
    private static final String TEMPLATE_NAME = "managePeopleForOrganization.ftl";

    @Override
	protected AuthorizationRequest requiredActions(VitroRequest vreq) {
		return SimplePermission.DO_FRONT_END_EDITING.ACTION;
	}

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();

        String subjectUri = vreq.getParameter("subjectUri");
        body.put("subjectUri", subjectUri);

        HashMap<String, List<Map<String,String>>>  people = getPeople(subjectUri, vreq);
		if ( log.isDebugEnabled() ) {
        	log.debug("people = " + people);
		}
        body.put("people", people);

        List<String> allSubclasses = getAllSubclasses(people);
        body.put("allSubclasses", allSubclasses);

        Individual subject = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);
        if( subject != null && subject.getName() != null ){
             body.put("subjectName", subject.getName());
        }else{
             body.put("subjectName", null);
        }

        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }

	private static String PEOPLE_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n"
        + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
        + "SELECT DISTINCT ?subclass ?position ?positionLabel (str(?label) as ?name) ?person ?hideThis WHERE { \n"
        + "    ?subject core:relatedBy ?position . \n"
        + "    ?position a core:Position . \n"
        + "    ?position rdfs:label ?positionLabel . \n"
        + "    OPTIONAL { \n"
        + "        ?subject core:relatedBy ?position . \n"
        + "        ?position a core:Position . \n"
        + "        ?position core:relates  ?person . "
        + "        ?person a foaf:Person . \n"
        + "        ?person rdfs:label ?label } \n"
        + "    OPTIONAL { \n"
        + "        ?subject core:relatedBy ?position . \n"
        + "        ?position a core:Position . \n"
        + "        ?position vitro:mostSpecificType ?subclass . \n"
        + "        OPTIONAL { ?subclass vitro:displayRankAnnot ?displayRank } \n"
		+ "    } \n "
        + "    OPTIONAL { ?position core:hideFromDisplay ?hideThis } \n "
        + "    FILTER ( !BOUND(?displayRank) || ?displayRank < 500 )"
        + "} ORDER BY ?subclass ?name";

    HashMap<String, List<Map<String,String>>>  getPeople(String subjectUri, VitroRequest vreq) {
        VClassDao vcDao = vreq.getUnfilteredAssertionsWebappDaoFactory().getVClassDao();

        String queryStr = QueryUtils.subUriForQueryVar(PEOPLE_QUERY, "subject", subjectUri);
        log.debug("queryStr = " + queryStr);
        HashMap<String, List<Map<String,String>>>  subclassToPeople = new HashMap<String, List<Map<String,String>>>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                RDFNode subclassUri= soln.get("subclass");
                if ( subclassUri != null ) {
                    String subclassUriStr = soln.get("subclass").toString();
                    VClass vClass = vcDao.getVClassByURI(subclassUriStr);
                    String subclass = ((vClass.getName() == null) ? subclassUriStr : vClass.getName());
                    if(!subclassToPeople.containsKey(subclass)) {
                        subclassToPeople.put(subclass, new ArrayList<Map<String,String>>());
                    }
                    List<Map<String,String>> peopleList = subclassToPeople.get(subclass);
                    peopleList.add(QueryUtils.querySolutionToStringValueMap(soln));
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return subclassToPeople;
    }

	private List<String> getAllSubclasses(
			HashMap<String, List<Map<String, String>>> people) {
        List<String> allSubclasses = new ArrayList<String>(people.keySet());
        Collections.sort(allSubclasses);
        return allSubclasses;
	}

}


