/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.JsonServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListController.PageRecord;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListController;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;

/*
 * This class includes methods that help in selecting a data getter based on 
 * parameters, and VIVO will have its own version or extend this
 */
public class MenuManagementDataUtils {
    private static final Log log = LogFactory.getLog(MenuManagementDataUtils.class);

    //Data that is to be returned to template that does not involve data getters
    //e.g. what are the current class groups, etc.
    public static void includeRequiredSystemData(ServletContext context, Map<String, Object> templateData) {
    	
    	checkInstitutionalInternalClass(context, templateData);
    }
    
	//Check whether any classes exist with internal class restrictions
	private static void checkInstitutionalInternalClass(ServletContext context, Map<String, Object> templateData) {
		//TODO: replace with more generic ModelContext retrieval method
		String internalClass = retrieveInternalClass(context);
		if(internalClass != null) {			
			templateData.put("internalClass", internalClass);
			templateData.put("internalClassUri", internalClass);
		} else {
			//need to initialize to empty string anyway
			templateData.put("internalClassUri", "");
		}
		
	}
	
	private static String retrieveInternalClass(ServletContext context) {
		OntModel mainModel = ModelContext.getBaseOntModelSelector(context).getTBoxModel();
		StmtIterator internalIt = mainModel.listStatements(null, ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT), (RDFNode) null);
		if(internalIt.hasNext()) {			
			String internalClass = internalIt.nextStatement().getSubject().getURI();
			return internalClass;
		}
		return null;
	}

    
}