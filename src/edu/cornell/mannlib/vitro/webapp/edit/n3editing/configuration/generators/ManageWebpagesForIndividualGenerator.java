/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

/**
 * This is an odd controller that is just drawing a page with links on it.
 * It is not an example of the normal use of the RDF editing system and
 * was just migrated over from an odd use of the JSP RDF editing system
 * during the 1.4 release. 
 * 
 * This mainly sets up pageData for the template to use.
 */
public class ManageWebpagesForIndividualGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {
    public static Log log = LogFactory.getLog(ManageWebpagesForIndividualGenerator.class);
    
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {

        EditConfigurationVTwo config = new EditConfigurationVTwo();
        config.setTemplate("manageWebpagesForIndividual.ftl");

        initBasics(config, vreq);
        initPropertyParameters(vreq, session, config);
        initObjectPropForm(config, vreq);

        config.setSubjectUri(EditConfigurationUtils.getSubjectUri(vreq));
        config.setEntityToReturnTo( EditConfigurationUtils.getSubjectUri(vreq));

        List<Map<String,String>> webpages = getWebpages(config.getSubjectUri(), vreq);
        config.addFormSpecificData("webpages",webpages);

        config.addFormSpecificData("rankPredicate", "http://vivoweb.org/ontology/core#rank" );
        config.addFormSpecificData("reorderUrl", "/edit/reorder" );       
        config.addFormSpecificData("deleteWebpageUrl", "/edit/primitiveDelete");              

        ParamMap paramMap = new ParamMap();
        paramMap.put("subjectUri", config.getSubjectUri());
        paramMap.put("editForm", AddEditWebpageFormGenerator.class.getName());
        paramMap.put("view", "form");
        String path = UrlBuilder.getUrl( UrlBuilder.Route.EDIT_REQUEST_DISPATCH ,paramMap);

        config.addFormSpecificData("baseEditWebpageUrl", path);                 

        paramMap = new ParamMap();
        paramMap.put("subjectUri", config.getSubjectUri());
        paramMap.put("predicateUri", config.getPredicateUri());
        paramMap.put("editForm" , AddEditWebpageFormGenerator.class.getName() );
        paramMap.put("cancelTo", "manage");
        path = UrlBuilder.getUrl( UrlBuilder.Route.EDIT_REQUEST_DISPATCH ,paramMap);

        config.addFormSpecificData("showAddFormUrl", path);          

        Individual subject = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(config.getSubjectUri());
        if( subject != null && subject.getName() != null ){
            config.addFormSpecificData("subjectName", subject.getName());
        }else{
            config.addFormSpecificData("subjectName", null);
        }
        prepare(vreq, config);
        return config;
    }
  
    
    private static String WEBPAGE_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "SELECT DISTINCT ?link ?url ?anchor ?rank WHERE { \n"
        + "    ?subject core:webpage ?link . \n"
        + "    OPTIONAL { ?link core:linkURI ?url } \n"
        + "    OPTIONAL { ?link core:linkAnchorText ?anchor } \n"
        + "    OPTIONAL { ?link core:rank ?rank } \n"
        + "} ORDER BY ?rank";
    
       
    private List<Map<String, String>> getWebpages(String subjectUri, VitroRequest vreq) {
          
        String queryStr = QueryUtils.subUriForQueryVar(WEBPAGE_QUERY, "subject", subjectUri);
        log.debug("Query string is: " + queryStr);
        List<Map<String, String>> webpages = new ArrayList<Map<String, String>>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                RDFNode node = soln.get("link");
                if (node.isURIResource()) {
                    webpages.add(QueryUtils.querySolutionToStringValueMap(soln));        
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }    
        
        return webpages;
    }
}
