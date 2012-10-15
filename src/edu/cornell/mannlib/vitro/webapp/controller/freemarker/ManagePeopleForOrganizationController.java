/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;


public class ManagePeopleForOrganizationController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ManagePeopleForOrganizationController.class.getName());
    private VClassDao vcDao = null;
    private static final String TEMPLATE_NAME = "managePeopleForOrganization.ftl";
    private List<String> allSubclasses;
    
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.DO_FRONT_END_EDITING.ACTIONS;
	}

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();

        String subjectUri = vreq.getParameter("subjectUri");
        
        body.put("subjectUri", subjectUri);

        if (vreq.getAssertionsWebappDaoFactory() != null) {
        	vcDao = vreq.getAssertionsWebappDaoFactory().getVClassDao();
        } else {
        	vcDao = vreq.getFullWebappDaoFactory().getVClassDao();
        }

        HashMap<String, List<Map<String,String>>>  people = getPeople(subjectUri, vreq);
        log.debug("people = " + people);
        body.put("people", people);
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
        + "PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#> \n"
        + "SELECT DISTINCT ?subclass ?position (str(?label) as ?name) ?person ?hideThis WHERE { \n"
        + "    ?subject core:organizationForPosition ?position . \n"
        + "    OPTIONAL { ?position core:positionForPerson  ?person . " 
        + "               ?person rdfs:label ?label } \n"
        + "    OPTIONAL { ?position vitro:mostSpecificType ?subclass . \n"
        + "              ?subclass rdfs:subClassOf core:Position } \n"
        + "    OPTIONAL { ?position core:hideFromDisplay ?hideThis } \n "
        + "} ORDER BY ?subclass ?name";    
       
    HashMap<String, List<Map<String,String>>>  getPeople(String subjectUri, VitroRequest vreq) {
          
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
                    VClass vClass = (VClass) vcDao.getVClassByURI(subclassUriStr);
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
       
        allSubclasses = new ArrayList<String>(subclassToPeople.keySet());
        Collections.sort(allSubclasses);
        return subclassToPeople;
    }
}


