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


@WebServlet(name = "ManageGrantsForIndividualController", urlPatterns = {"/manageGrants"} )
public class ManageGrantsForIndividualController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ManageGrantsForIndividualController.class.getName());
    private static final String TEMPLATE_NAME = "manageGrantsForIndividual.ftl";

    @Override
	protected AuthorizationRequest requiredActions(VitroRequest vreq) {
		return SimplePermission.DO_FRONT_END_EDITING.ACTION;
	}

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();

        String subjectUri = vreq.getParameter("subjectUri");
        body.put("subjectUri", subjectUri);

        HashMap<String, List<Map<String,String>>>  grants = getGrants(subjectUri, vreq);
        if ( log.isDebugEnabled() ) {
			log.debug("grants = " + grants);
		}
        body.put("grants", grants);

        List<String> allSubclasses = getAllSubclasses(grants);
        body.put("allSubclasses", allSubclasses);

        Individual subject = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);
        if( subject != null && subject.getName() != null ){
             body.put("subjectName", subject.getName());
        }else{
             body.put("subjectName", null);
        }

        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }

    private static String GRANT_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n"
        + "SELECT DISTINCT ?subclass ?role (str(?label2) as ?label) ?activity ?hideThis WHERE { \n"
        + "    ?subject <http://purl.obolibrary.org/obo/RO_0000053> ?role . \n"
        + "    ?role a core:ResearcherRole . \n"
        + "    ?role vitro:mostSpecificType ?subclass \n"
        + "    OPTIONAL { \n"
        + "        ?subject <http://purl.obolibrary.org/obo/RO_0000053> ?role . \n"
        + "        ?role a core:ResearcherRole . \n"
        + "        ?role core:relatedBy ?activity . \n"
		+ "        ?activity a core:Grant . \n"
        + "        ?activity rdfs:label ?label2 . \n"
        + "    } \n"
        + "    OPTIONAL { \n"
        + "        ?subject <http://purl.obolibrary.org/obo/RO_0000053> ?role . \n"
        + "        ?role a core:ResearcherRole . \n"
        + "        ?role <http://purl.obolibrary.org/obo/BFO_0000054> ?activity . \n"
		+ "        ?activity a core:Project . \n"
        + "        ?activity rdfs:label ?label2 . \n"
        + "    } \n"
        + "    OPTIONAL { ?role core:hideFromDisplay ?hideThis } \n"
        + "} ORDER BY ?subclass ?label2";

    HashMap<String, List<Map<String,String>>>  getGrants(String subjectUri, VitroRequest vreq) {
        VClassDao vcDao = vreq.getUnfilteredAssertionsWebappDaoFactory().getVClassDao();

        String queryStr = QueryUtils.subUriForQueryVar(GRANT_QUERY, "subject", subjectUri);
        log.debug("queryStr = " + queryStr);
        HashMap<String, List<Map<String,String>>>  subclassToGrants = new HashMap<String, List<Map<String,String>>>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                RDFNode subclassUri= soln.get("subclass");
                if ( subclassUri != null ) {
                    String subclassUriStr = soln.get("subclass").toString();
                    VClass vClass = vcDao.getVClassByURI(subclassUriStr);
                    String subclass = ((vClass.getName() == null) ? subclassUriStr : vClass.getName());
                    if(!subclassToGrants.containsKey(subclass)) {
                        subclassToGrants.put(subclass, new ArrayList<Map<String,String>>()); //list of grant information
                    }
                    List<Map<String,String>> grantsList = subclassToGrants.get(subclass);
                    grantsList.add(QueryUtils.querySolutionToStringValueMap(soln));
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return subclassToGrants;
    }

    private List<String> getAllSubclasses(HashMap<String, List<Map<String, String>>> grants) {
        List<String> allSubclasses = new ArrayList<String>(grants.keySet());
        Collections.sort(allSubclasses);
        return allSubclasses;
    }
}


