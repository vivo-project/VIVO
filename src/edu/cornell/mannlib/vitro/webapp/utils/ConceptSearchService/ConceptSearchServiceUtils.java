/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.PageDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.VClassGroupTemplateModel;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.EditConfigurationGenerator;

/**
 * This will pass these variables to the template:
 * classGroupUri: uri of the classgroup associated with this page.
 * vClassGroup: a data structure that is the classgroup associated with this page.     
 */
public class ConceptSearchServiceUtils {
    private static final Log log = LogFactory.getLog(ConceptSearchServiceUtils.class);
    //Get the appropriate search service class
    
    //Get the class that corresponds to the appropriate search
	public static String getConceptSearchServiceClassName(String searchServiceName) {
		HashMap<String, String> map = getMapping();
		if(map.containsKey(searchServiceName)) {
			return map.get(searchServiceName);
		}
		return null;
	}
	
	
    
    //Get the hashmap mapping service name to Service class
    private static HashMap<String, String> getMapping() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put("UMLS", "edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService.UMLSConceptSearch");
    	return map;
    }
    
    public static String getSearchResults(ServletContext context, VitroRequest vreq) {
    	String searchServiceName = getSearchServiceName(vreq);
    
    	String searchServiceClassName = getConceptSearchServiceClassName(searchServiceName);
    
    	ConceptSearchInterface conceptSearchClass = null;
	
	    Object object = null;
	    try {
	        Class classDefinition = Class.forName(searchServiceClassName);
	        object = classDefinition.newInstance();
	        conceptSearchClass = (ConceptSearchInterface) object;
	    } catch (InstantiationException e) {
	        System.out.println(e);
	    } catch (IllegalAccessException e) {
	        System.out.println(e);
	    } catch (ClassNotFoundException e) {
	        System.out.println(e);
	    }    	
    
	    if(conceptSearchClass == null){
	    	log.error("could not find Concept Search Class for " + searchServiceName);
	    	return null;
	    } 
	    return conceptSearchClass.doSearch(context, vreq);
    }



	private static String getSearchServiceName(VitroRequest vreq) {
		return vreq.getParameter("searchServiceName");
	}
}