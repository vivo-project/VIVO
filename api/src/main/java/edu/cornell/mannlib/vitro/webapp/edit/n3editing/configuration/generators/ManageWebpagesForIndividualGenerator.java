/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

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
        config.setTemplate(this.getTemplate());

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
        paramMap.put("editForm", this.getEditForm());
        paramMap.put("view", "form");
        String path = UrlBuilder.getUrl( UrlBuilder.Route.EDIT_REQUEST_DISPATCH ,paramMap);

        config.addFormSpecificData("baseEditWebpageUrl", path);

        //Also add domainUri and rangeUri if they exist, adding here instead of template
        String domainUri = (String) vreq.getParameter("domainUri");
        String rangeUri = (String) vreq.getParameter("rangeUri");
        paramMap = new ParamMap();
        paramMap.put("subjectUri", config.getSubjectUri());
        paramMap.put("predicateUri", config.getPredicateUri());
        paramMap.put("editForm" , this.getEditForm() );
        paramMap.put("cancelTo", "manage");
        if(domainUri != null && !domainUri.isEmpty()) {
        	paramMap.put("domainUri", domainUri);
        }
        if(rangeUri != null && !rangeUri.isEmpty()) {
        	paramMap.put("rangeUri", rangeUri);
        }
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

    private static String WEBPAGE_MODEL = ""
            + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
            + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
            + "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#>\n"
            + "CONSTRUCT\n"
            + "{\n"
            + "    ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard .\n"
            + "    ?vcard vcard:hasURL ?link .\n"
            + "    ?link a vcard:URL .\n"
            + "    ?link vcard:url ?url .\n"
            + "    ?link rdfs:label ?linkLabel .\n"
            + "    ?link core:rank ?rank .\n"
            + "    ?link vitro:mostSpecificType ?type .\n"
            + "    ?type rdfs:label ?typeLabel .\n"
            + "}\n"
            + "WHERE\n"
            + "{\n"
            + "    {\n"
            + "        ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard .\n"
            + "        ?vcard vcard:hasURL ?link .\n"
            + "        ?link a vcard:URL .\n"
            + "    }\n"
            + "    UNION\n"
            + "    {\n"
            + "        ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard .\n"
            + "        ?vcard vcard:hasURL ?link .\n"
            + "        ?link a vcard:URL .\n"
            + "        ?link vcard:url ?url .\n"
            + "    }\n"
            + "    UNION\n"
            + "    {\n"
            + "        ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard .\n"
            + "        ?vcard vcard:hasURL ?link .\n"
            + "        ?link a vcard:URL .\n"
            + "        ?link rdfs:label ?linkLabel .\n"
            + "    }\n"
            + "    UNION\n"
            + "    {\n"
            + "        ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard . \n"
            + "        ?vcard vcard:hasURL ?link .\n"
            + "        ?link a vcard:URL .\n"
            + "        ?link core:rank ?rank .\n"
            + "    }\n"
            + "    UNION\n"
            + "    {\n"
            + "        ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard .\n"
            + "        ?vcard vcard:hasURL ?link .\n"
            + "        ?link a vcard:URL .\n"
            + "        ?link vitro:mostSpecificType ?type .\n"
            + "        ?type rdfs:label ?typeLabel .\n"
            + "    }\n"
            + "}\n";

    private static String WEBPAGE_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n"
        + "SELECT DISTINCT ?vcard ?link ?url ?rank ?typeLabel (group_concat(distinct ?linkLabel;separator=\"/\") as ?label) WHERE { \n"
        + "    ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard . \n"
        + "    ?vcard vcard:hasURL ?link . \n"
        + "    ?link a vcard:URL \n"
        + "    OPTIONAL { ?link vcard:url ?url } \n"
        + "    OPTIONAL { ?link rdfs:label ?linkLabel } \n"
        + "    OPTIONAL { ?link core:rank ?rank } \n"
        + "    OPTIONAL { ?link vitro:mostSpecificType ?type } \n"
        + "    OPTIONAL { ?type rdfs:label ?typeLabel } \n"
        + "} GROUP BY ?rank ?vcard ?link ?url ?typeLabel \n"
    	+ "  ORDER BY ?rank";


    private List<Map<String, String>> getWebpages(String subjectUri, VitroRequest vreq) {
        RDFService rdfService = vreq.getRDFService();

        List<Map<String, String>> webpages = new ArrayList<Map<String, String>>();
        try {
            String constructStr = QueryUtils.subUriForQueryVar(WEBPAGE_MODEL, "subject", subjectUri);

            Model constructedModel = ModelFactory.createDefaultModel();
            rdfService.sparqlConstructQuery(constructStr, constructedModel);

            String queryStr = QueryUtils.subUriForQueryVar(this.getQuery(), "subject", subjectUri);
            log.debug("Query string is: " + queryStr);

            QueryExecution qe = QueryExecutionFactory.create(queryStr, constructedModel);
            try {
                ResultSet results = qe.execSelect();

                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    RDFNode node = soln.get("link");
                    if (node != null && node.isURIResource()) {
                        webpages.add(QueryUtils.querySolutionToStringValueMap(soln));
                    }
                }
            } finally {
                qe.close();
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        log.debug("webpages = " + webpages);
        return webpages;
    }

    //Putting this into a method allows overriding it in subclasses
    protected String getEditForm() {
    	return AddEditWebpageFormGenerator.class.getName();
    }

    protected String getQuery() {
    	return WEBPAGE_QUERY;
    }

    protected String getTemplate() {
    	return "manageWebpagesForIndividual.ftl";
    }
}
