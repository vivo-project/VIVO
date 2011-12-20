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
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;
import edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.*;


/*
 * This class includes methods that help in selecting a data getter based on 
 * parameters, and VIVO will have its own version or extend this
 */
public class SelectDataGetterUtils {
    private static final Log log = LogFactory.getLog(SelectDataGetterUtils.class);

    /**Get data for an existing page and set variables for the template accordingly**/
   
    
	public static void processAndRetrieveData(VitroRequest vreq, ServletContext context, Map<String, Object> pageData, String dataGetterClass, Map<String, Object> templateData) {
		//The type of the data getter will show how to process the data from the data getter
		ProcessDataGetter processor = selectProcessor(dataGetterClass);
		processor.populateTemplate(context, pageData, templateData);
	}
	
	//This will be different in VIVO than in VITRO
	private static ProcessDataGetter selectProcessor(String dataGetterClass) {
		if(dataGetterClass.equals(ClassGroupPageData.class.getName())) {
			return new ProcessClassGroup();
		} else if(dataGetterClass.equals(InternalClassesDataGetter.class.getName())) {
			return new ProcessInternalClasses();
			//below should be for vitro specific version
			//return new ProcessIndividualsForClasses();
		} 
		return null;
	}
    

    /**Process parameters from form and select appropriate data getter on this basis **/   
    public static Model createDataGetterModel(VitroRequest vreq, Resource dataGetterResource) {
		Model dataGetterModel = null;
    	if(dataGetterResource != null) {
    		//If "All selected" then use class group else use individuals for classes
    		dataGetterModel = ModelFactory.createDefaultModel();
    		
    		ProcessInternalClasses individualsProcess = new ProcessInternalClasses();

    		ProcessClassGroup classGroupProcess = new ProcessClassGroup();
    		if(individualsProcess.useProcessor(vreq)) {
    			dataGetterModel = individualsProcess.processSubmission(vreq, dataGetterResource);
    		} else {
    			dataGetterModel = classGroupProcess.processSubmission(vreq, dataGetterResource);
    		}
    		
    		
    	} else {
    		log.error("Data getter is null ");
    	}
    	return dataGetterModel;
		
	}
    
}