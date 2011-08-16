/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.DefaultObjectWrapper;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.Field;
import javax.servlet.RequestDispatcher;
public class ProcessTerminologyController extends VitroHttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ProcessTerminologyController.class);
    private static final String submissionUrl = "/edit/processRdfForm2.jsp";
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        VitroRequest vreq = new VitroRequest(req);

        try{
        
        	
        	EditConfiguration editConfig = EditConfiguration.getConfigFromSession(req.getSession(), req);
        	//For query parameters, check whether CUI 
        	String[] termsArray = new String[1];
        	String referencedTerms = vreq.getParameter("referencedTerm");
    		if(referencedTerms.indexOf(",") != -1) {
    			termsArray = referencedTerms.split(",");
    		}
        	
    		int numberTerms = termsArray.length;
    		log.debug("Number of terms included is " + numberTerms);
    		//if multiple values then need to process this differently by adding additional
    		//fields, assertions, etc. as well as changing the URL slightly
    		String url = "/edit/processRdfForm2.jsp?" + vreq.getQueryString();
    		if(numberTerms > 1) {
    			//Add new resources, i.e. the additional terminology context nodes
    			addNewResources(editConfig, vreq, numberTerms);
    			//Add N3Required
    			addN3Required(editConfig,vreq, numberTerms);
    			//Add URIs on Form and Add Literals On Form
    			addLiteralsAndUrisOnForm(editConfig, vreq, numberTerms);
    			//Add fields
    			addFields(editConfig, vreq, numberTerms);
    			//Generate new url which will map input fields to value
    			url = generateUrl(editConfig, vreq);
    		}
    		log.debug("Submitting to url using the following parameters " + url);
    		RequestDispatcher dispatcher = req.getRequestDispatcher(url);
    		dispatcher.forward(req, resp);
        	
        	
	    } catch(Exception ex) {
	    		log.error("error occurred in servlet", ex);
	    }
        	   
    }

	private String generateUrl(EditConfiguration editConfig, VitroRequest vreq) {
		
		//Get original url and query string
		String newUrl = "/edit/processRdfForm2.jsp?" + vreq.getQueryString();
		//Iterate through the terms and include appropriate values
		Map <String,String[]> queryParameters = null;        
    	queryParameters = vreq.getParameterMap();      
    	//Use same query parameters to forward
    	String termParam = vreq.getParameter("referencedTerm");
		String labelParam = vreq.getParameter("termLabel");
		String typeParam = vreq.getParameter("termType");
		if(!labelParam.contains(",")){
			log.error("Multiple labels are not being sent back, this is an error");
		}
		if(!typeParam.contains(",")) {
			log.error("Multiple types are not being sent back, this is an error");
		}
		
    	String[] termsArray = termParam.split(",");
		String[] termLabels = labelParam.split(",");
		String[] termTypes = typeParam.split(",");
		String entryTerm = vreq.getParameter("entryTerm");
		int numberParameters = termsArray.length;
		int index;
		String referencedBase = "referencedTerm";
		String labelBase = "termLabel";
		String termBase = "termType";
		//Should already include entry term so no need to include here
		//newUrl += "&entryTerm=" + URLEncoder.encode(entryTerm);
		//Add entry 
		for(index = 1; index <= numberParameters; index++) {
			String referencedInputName = referencedBase + index;
			String termLabelInputName = labelBase + index;
			String termTypeInputName =termBase + index;
			//array is set to start at 0, not 1
			String referencedTerm = termsArray[index - 1];
			String termLabel = termLabels[index - 1];
			String termType = termTypes[index - 1];
			newUrl += "&" + referencedInputName + "=" + URLEncoder.encode(referencedTerm);
			newUrl += "&" + termLabelInputName + "=" + URLEncoder.encode(termLabel);
			newUrl += "&" + termTypeInputName + "=" + URLEncoder.encode(termType);
		}
		return newUrl;
	}

	private void addNewResources(EditConfiguration editConfig, VitroRequest vreq, int numberTerms) {
		Map<String, String> newResources = new HashMap<String, String>();
		//Based on number of terms, add new
		int n;
		String defaultNamespace = "";
		
		String base = "terminologyContextNode";
		for(n = 1; n <= numberTerms; n++) {
			String newNode = base + n;
			newResources.put(newNode, defaultNamespace);
		}
		//TODO: Check if below is required as this is a reference
		editConfig.setNewResources(newResources);
	}
	
	/*
	 *       "referencedTerm" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED", //UNSURE WHAT TO KEEP HERE
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForTerminology}" ]
      },   
      "entryTerm" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForTerminology}" ]
      },   
      "termLabel" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForTerminology}" ]
      }
	 */

	private void addFields(EditConfiguration editConfig, VitroRequest vreq, int numberTerms) {
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		int index;
		String referencedBase = "referencedTerm";
		String entryBase = "entryTerm";
		String labelBase = "termLabel";
		
		//Entry only needs to be added once
		fieldMap.put(entryBase, generateField(editConfig));
		
		//First one already included so add new ones here
		for(index = 1; index <= numberTerms; index++) {
			int suffix = index;
			String referencedTerm = referencedBase + suffix;
			String label = labelBase + suffix;
			//need to add termType
			//Generate Field for each
			fieldMap.put(referencedTerm, generateField(editConfig));
			fieldMap.put(label, generateField(editConfig));
			//termType would also go in here
		}
		editConfig.setFields(fieldMap);
	}

	/*
	"newResource"      : "false",
    "validators"       : [ "nonempty" ],
    "optionsType"      : "UNDEFINED",
    "literalOptions"   : [ ],
    "predicateUri"     : "",
    "objectClassUri"   : "",
    "rangeDatatypeUri" : "",
    "rangeLang"        : "",
    "assertions"       : [ "${n3ForTerminology}" ]*/
	private Field generateField(EditConfiguration editConfig) {
		List<String> n3Required = editConfig.getN3Required();
		Field field = new Field();
		field.setNewResource(false);
		List<String> validators = new ArrayList<String>();
		validators.add("nonempty");
		field.setValidators(validators);
		field.setOptionsType("UNDEFINED");
		field.setLiteralOptions(new ArrayList<List<String>>());
		field.setPredicateUri("");
		field.setObjectClassUri("");
		field.setRangeDatatypeUri("");
		field.setRangeLang("");
		field.setAssertions(n3Required);
		return field;
	}

	//Original uris and literals included:
	/*
	 * "urisOnForm"     : [ "referencedTerm" ],
    "literalsOnForm" : [ "entryTerm", "termLabel" ],
	 */
	private void addLiteralsAndUrisOnForm(EditConfiguration editConfig,
			VitroRequest vreq, int numberTerms) {
		List<String> urisOnForm = new ArrayList<String>();
		List<String> literalsOnForm = new ArrayList<String>();
		
		int index;
		String referencedBase = "referencedTerm";
		String entryBase = "entryTerm";
		String labelBase = "termLabel";
		//entry term needs to be added only once
		literalsOnForm.add(entryBase);

		//First one already included so add new ones here
		for(index = 1; index <= numberTerms; index++) {
			int suffix = index;
			String referencedTerm = referencedBase + suffix;
			String label = labelBase + suffix;
			urisOnForm.add(referencedTerm);
			literalsOnForm.add(label);
		}
		editConfig.setUrisOnform(urisOnForm);
		editConfig.setLiteralsOnForm(literalsOnForm);
	}

	//original n3 required is of this format
			/*@prefix core: <${vivoCore}> .   
    		?subject ?predicate ?terminologyContextNode .
    	    ?terminologyContextNode core:referencedTerm ?referencedTerm .
    	    ?terminologyContextNode core:entryTerm ?entryTerm .
    	    ?terminologyContextNode core:termLabel ?termLabel .
    	    */
	private void addN3Required(EditConfiguration editConfig, VitroRequest vreq, int numberTerms) {
		//List<String> n3Required = editConfig.getN3Required();
		List<String> n3Required = new ArrayList<String>();
		int index;
		String nodeBase = "?terminologyContextNode";
		String referencedBase = "?referencedTerm";
		//there's only one entry term in this case
		String entryBase = "?entryTerm";
		String labelBase = "?termLabel";
		String prefixStr = "@prefix core: <http://vivoweb.org/ontology/core#> .";
		//First one already included so add new ones here
		for(index = 1; index <= numberTerms; index++) {
			int suffix = index;
			String node = nodeBase + suffix;
			String referencedTerm = referencedBase + suffix;
			String label = labelBase + suffix;
			String n3String = prefixStr;
			n3String += "?subject ?predicate " + node + " . " + 
			node + " core:entryTerm " + entryBase + " . " + 
			node + " core:termLabel " + label + " . " + 
			node + " core:referencedTerm " + referencedTerm + " . ";
			n3Required.add(n3String);
		}
		editConfig.setN3Required(n3Required);
	}

}
