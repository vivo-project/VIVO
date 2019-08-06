/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
/**
 * Utilities for search
 */
public class ConceptSearchServiceUtils {
    private static final Log log = LogFactory.getLog(ConceptSearchServiceUtils.class);
    //Get the appropriate search service class
    //TODO: Change this so retrieved from the system instead using a query
    private static final String UMLSVocabSource = "http://link.informatics.stonybrook.edu/umls";
    private static final String AgrovocVocabSource = "http://aims.fao.org/aos/agrovoc/agrovocScheme";
    private static final String GemetVocabSource = "http://www.eionet.europa.eu/gemet/gemetThesaurus";
    private static final String LCSHVocabSource = "http://id.loc.gov/authorities/subjects";

    //Get the class that corresponds to the appropriate search
	public static String getConceptSearchServiceClassName(String searchServiceName) {
		HashMap<String, String> map = getMapping();
		if(map.containsKey(searchServiceName)) {
			return map.get(searchServiceName);
		}
		return null;
	}

	//Get the URLS for the different services
	//URL to label
	public static HashMap<String, VocabSourceDescription> getVocabSources() {
		HashMap<String, VocabSourceDescription> map = new HashMap<String, VocabSourceDescription>();
    	map.put(UMLSVocabSource, new VocabSourceDescription("UMLS", UMLSVocabSource, "http://www.nlm.nih.gov/research/umls/", "Unified Medical Language System"));
    	//Commenting out agrovoc for now until implementation is updated
    	map.put(AgrovocVocabSource, new VocabSourceDescription("AGROVOC", AgrovocVocabSource, "http://www.fao.org/agrovoc/", "Agricultural Vocabulary"));
    	map.put(GemetVocabSource, new VocabSourceDescription("GEMET", GemetVocabSource, "http://www.eionet.europa.eu/gemet", "GEneral Multilingual Environmental Thesaurus"));
    	map.put(LCSHVocabSource, new VocabSourceDescription("LCSH", LCSHVocabSource, "http://id.loc.gov/authorities/subjects/", "Library of Congress Subject Headings"));

    	return map;
	}

	//Get additional vocab source info


    //Get the hashmap mapping service name to Service class
    private static HashMap<String, String> getMapping() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put(UMLSVocabSource, "edu.cornell.mannlib.semservices.service.impl.UMLSService");
    	map.put(AgrovocVocabSource, "edu.cornell.mannlib.semservices.service.impl.AgrovocService");
    	map.put(GemetVocabSource, "edu.cornell.mannlib.semservices.service.impl.GemetService");
    	map.put(LCSHVocabSource, "edu.cornell.mannlib.semservices.service.impl.LCSHService");

    	return map;
    }

    public static List<Concept> getSearchResults(ServletContext context, VitroRequest vreq) throws Exception {
    	String searchServiceName = getSearchServiceUri(vreq);
    	String searchServiceClassName = getConceptSearchServiceClassName(searchServiceName);

    	ExternalConceptService conceptServiceClass = null;

	    Object object = null;
	    try {
	        Class classDefinition = Class.forName(searchServiceClassName);
	        object = classDefinition.newInstance();
	        conceptServiceClass = (ExternalConceptService) object;
	    } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
	        System.out.println(e);
	    }

        if(conceptServiceClass == null){
	    	log.error("could not find Concept Search Class for " + searchServiceName);
	    	return null;
	    }

	    //Get search
	    String searchTerm = getSearchTerm(vreq);
	    List<Concept> conceptResults =  conceptServiceClass.getConcepts(searchTerm);
	    return conceptResults;
    }


	private static String getSearchServiceUri(VitroRequest vreq) {
		return vreq.getParameter("source");
	}

	private static String getSearchTerm(VitroRequest vreq) {
		return vreq.getParameter("searchTerm");
	}


}

