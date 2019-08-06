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


@WebServlet(name = "ManagePublicationsForIndividualController", urlPatterns = {"/managePublications"} )
public class ManagePublicationsForIndividualController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ManagePublicationsForIndividualController.class.getName());
    private static final String TEMPLATE_NAME = "managePublicationsForIndividual.ftl";

    @Override
	protected AuthorizationRequest requiredActions(VitroRequest vreq) {
		return SimplePermission.DO_FRONT_END_EDITING.ACTION;
	}

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();

        String subjectUri = vreq.getParameter("subjectUri");
        body.put("subjectUri", subjectUri);

        HashMap<String, List<Map<String,String>>>  publications = getPublications(subjectUri, vreq);
        if ( log.isDebugEnabled() ) {
			log.debug("publications = " + publications);
		}
        body.put("publications", publications);

        List<String> allSubclasses = getAllSubclasses(publications);
        body.put("allSubclasses", allSubclasses);

        Individual subject = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);
        if( subject != null && subject.getName() != null ){
             body.put("subjectName", subject.getName());
        }else{
             body.put("subjectName", null);
        }

        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }

    private static String PUBLICATION_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n"
        + "SELECT DISTINCT ?subclass ?authorship (str(?label) as ?title) ?pub ?hideThis WHERE { \n"
        + "    ?subject core:relatedBy ?authorship . \n"
        + "    ?authorship a core:Authorship  . \n"
        + "    OPTIONAL { \n "
        + "        ?subject core:relatedBy ?authorship . \n"
        + "        ?authorship a core:Authorship  . \n"
        + "        ?authorship core:relates ?pub . "
        + "        ?pub a <http://purl.org/ontology/bibo/Document> . \n"
        + "        ?pub rdfs:label ?label . \n"
        + "        OPTIONAL { \n"
        + "            ?subject core:relatedBy ?authorship . \n"
        + "            ?authorship a core:Authorship  . \n"
        + "            ?authorship core:relates ?pub . "
        + "            ?pub a <http://purl.org/ontology/bibo/Document> . \n"
        + "            ?pub vitro:mostSpecificType ?subclass . \n"
        + "        } \n"
        + "    } \n"
        + "    OPTIONAL { ?authorship core:hideFromDisplay ?hideThis } \n"
        + "} ORDER BY ?subclass ?title";

    HashMap<String, List<Map<String,String>>>  getPublications(String subjectUri, VitroRequest vreq) {

        VClassDao vcDao = vreq.getUnfilteredAssertionsWebappDaoFactory().getVClassDao();

        String queryStr = QueryUtils.subUriForQueryVar(PUBLICATION_QUERY, "subject", subjectUri);
        String subclass = "";
        log.debug("queryStr = " + queryStr);
        HashMap<String, List<Map<String,String>>>  subclassToPublications = new HashMap<String, List<Map<String,String>>>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                RDFNode subclassUri= soln.get("subclass");
                if ( subclassUri != null ) {
                    String subclassUriStr = soln.get("subclass").toString();
                    VClass vClass = vcDao.getVClassByURI(subclassUriStr);
                    subclass = ((vClass.getName() == null) ? subclassUriStr : vClass.getName());
                }
                else {
                    subclass = "Unclassified Publication";
                }
                if(!subclassToPublications.containsKey(subclass)) {
                    subclassToPublications.put(subclass, new ArrayList<Map<String,String>>()); //list of publication information
                }
                List<Map<String,String>> publicationsList = subclassToPublications.get(subclass);
                publicationsList.add(QueryUtils.querySolutionToStringValueMap(soln));
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return subclassToPublications;
    }

    private List<String> getAllSubclasses(HashMap<String, List<Map<String, String>>> publications) {
        List<String> allSubclasses = new ArrayList<String>(publications.keySet());
        Collections.sort(allSubclasses);
        return allSubclasses;
    }
}
