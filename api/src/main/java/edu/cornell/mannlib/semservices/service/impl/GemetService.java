/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.semservices.service.impl;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.exceptions.ConceptsNotFoundException;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.vitro.webapp.utils.json.JacksonUtils;

public class GemetService implements ExternalConceptService  {
   protected final Log logger = LogFactory.getLog(getClass());
   private final String GemetWS_address = "https://www.eionet.europa.eu/gemet/";
   private final String narrowerUri = "http://www.w3.org/2004/02/skos/core%23narrower";
   private final String broaderUri = "http://www.w3.org/2004/02/skos/core%23broader";
   private final String relatedUri = "http://www.w3.org/2004/02/skos/core%23related";
   private final String definitionUri = "http://www.w3.org/2004/02/skos/core%23definition";
   private final String prefLabelUri = "http://www.w3.org/2004/02/skos/core%23prefLabel";
   private final String scopeNoteUri = "http://www.w3.org/2004/02/skos/core%23scopeNote";
   private final String altLabelUri = "http://www.w3.org/2004/02/skos/core%23altLabel";
   private final String exampleUri = "http://www.w3.org/2004/02/skos/core%23example";
   private final String acronymLabelUri = "http://www.w3.org/2004/02/skos/core%23acronymLabel";
   private final String endpoint = "http://cr.eionet.europa.eu/sparql";
   private final String schemeURI = "http://www.eionet.europa.eu/gemet/gemetThesaurus";


	@Override
	public List<Concept> getConcepts(String term) throws Exception {
		List<Concept> conceptList = new ArrayList<Concept>();
	      try {
	         String results = getConceptsMatchingKeyword(term);
	         //System.out.println(results);
	         conceptList = processOutput(results);

	      } catch (Exception ex) {
	         return new ArrayList<Concept>();
	         //ex.printStackTrace();
	         //throw ex;
	      }
	      return conceptList;
	}

   public List<Concept> processResults(String term) throws Exception {
      List<Concept> conceptList = new ArrayList<Concept>();
      try {
         String results = getConceptsMatchingKeyword(term);
         conceptList = processOutput(results);
      } catch (Exception ex) {
         //ex.printStackTrace();
         throw ex;
      }
      return conceptList;

   }

	public List<Concept> getConceptsByURIWithSparql(String uri)
			throws Exception {
		// deprecating this method...just return an empty list
		List<Concept> conceptList = new ArrayList<Concept>();
		return conceptList;
	}

   /**
    * @param results Results to process
    */
   private List<Concept> processOutput(String results) throws Exception  {

      List<Concept> conceptList = new ArrayList<Concept>();

      try {
         ArrayNode jsonArray = (ArrayNode) JacksonUtils.parseJson(results);
         if (jsonArray.size() == 0) {
            throw new ConceptsNotFoundException();
         }

         for (int i = 0; i < jsonArray.size(); i++) {
            Concept concept = new Concept();
            concept.setDefinedBy(schemeURI);
            concept.setBestMatch("true");
            ObjectNode json = (ObjectNode) jsonArray.get(i);
            String uri = getJsonValue(json, "uri");

            concept.setUri(uri);
            concept.setConceptId(stripConceptId(uri));
            concept.setSchemeURI(schemeURI);
            concept.setType("");
            if (json.has("preferredLabel")) {
               ObjectNode preferredLabelObj = (ObjectNode) json.get("preferredLabel");
               if (preferredLabelObj.has("string")) {
                  concept.setLabel(getJsonValue(preferredLabelObj,
                        "string"));
               }
            }
            if (json.has("definition")) {
               ObjectNode definitionObj = (ObjectNode) json.get("definition");
               if (definitionObj.has("string")) {
                  concept.setDefinition(getJsonValue(definitionObj,
                        "string"));
               }
            }

            String narrower = getRelatedConcepts(uri, "narrower");
            List<String> narrowerURIList = getRelatedUris(narrower);
            concept.setNarrowerURIList(narrowerURIList);

            String broader = getRelatedConcepts(uri, "broader");
            List<String> broaderURIList = getRelatedUris(broader);
            concept.setBroaderURIList(broaderURIList);

            /*String related = getRelatedConcepts(uri, "related");
            List<String> relatedURIList = getRelatedUris(related);
            for (String s: relatedURIList) {
            	System.out.println("related uri: "+s);
            }*/
            //String altLabels = getAllTranslationsForConcept(uri, "nonPreferredLabels");

            conceptList.add(concept);

         }

      } catch (Exception ex ) {
         //ex.printStackTrace();
         logger.error("Could not get concepts", ex);
         throw ex;
      }
      return conceptList;

   }

   /**
    * Get a string from a json object or an empty string if there is no value for the given key
   * @param obj JSON Object
   * @param key Key to retrieve
   */
  protected String getJsonValue(ObjectNode obj, String key) {
      if (obj.has(key)) {
         return obj.get(key).asText();
      } else {
         return "";
      }
   }


   /**
    * @param concept_uri Concept URI
    * @throws Exception
    */
   protected String getAvailableLangs(String concept_uri) throws Exception {
      String result = "";
      String serviceUrl = GemetWS_address + "getAvailableLanguages" +
      "?concept_uri=" + concept_uri;
      try {
         result = getGemetResults(serviceUrl);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
      return result;
   }

   /**
    * @param concept_uri concept URI
    * @throws Exception
    */
   protected String getConcept(String concept_uri) throws Exception {
      String result = "";
      String serviceUrl = GemetWS_address + "getConcept" +
      "?concept_uri=" + concept_uri +
      "&language=en";
      try {
         result = getGemetResults(serviceUrl);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
      return result;
   }

   /**
    * @param concept_uri Concept URI
    * @param property Property
    * @throws Exception
    */
   protected String getAllTranslationsForConcept(String concept_uri, String property) throws Exception {
      String result = "";
      String property_uri = "";
      switch (property) {
         case "definition":
            property_uri = definitionUri;
            break;
         case "preferredLabel":
            property_uri = prefLabelUri;
            break;
         case "scopeNote":
            property_uri = scopeNoteUri;
            break;
         case "nonPreferredLabels":
            property_uri = altLabelUri;
            break;
         case "example":
            property_uri = exampleUri;
            break;
         case "acronymLabel":
            property_uri = acronymLabelUri;
            break;
      }

      String serviceUrl = GemetWS_address + "getAllTranslationsForConcept" +
      "?concept_uri=" + concept_uri +
      "&property_uri=" + property_uri +
      "&language=en";
      try {
         result = getGemetResults(serviceUrl);
         List<String> props = getPropertyFromJson(result);

      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
      return result;
   }


   /**
    * @param concept_uri Concept URI
    * @param relation Relations
    * @throws Exception
    */
   protected String getRelatedConcepts(String concept_uri, String relation) throws Exception {
      String result = "";
      String relation_uri = "";
      switch (relation) {
         case "broader":
            relation_uri = broaderUri;
            break;
         case "narrower":
            relation_uri = narrowerUri;
            break;
         case "related":
            relation_uri = relatedUri;
            break;
      }
      String serviceUrl = GemetWS_address + "getRelatedConcepts" +
      "?concept_uri=" + concept_uri +
      "&relation_uri=" + relation_uri +
      "&language=en";
      try {
         result = getGemetResults(serviceUrl);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
      return result;
   }



   /**
    * @param keyword Keyword
    * @throws Exception
    */
   protected String getConceptsMatchingKeyword(String keyword) throws Exception {
      String result = "";
      String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
      String serviceUrl = GemetWS_address + "getConceptsMatchingKeyword" +
      "?keyword="  + encodedKeyword +
      "&search_mode=0" +
      "&thesaurus_uri=http://www.eionet.europa.eu/gemet/concept/" +
      "&language=en";
      try {
         result = getGemetResults(serviceUrl);
      } catch (Exception ex) {
         throw ex;
      }
      return result;

   }

   /**
    * @param url URI
    */
   protected String getGemetResults(String url) throws Exception  {
      String results = "";
      //System.out.println("url: "+url);
      try {

         StringWriter sw = new StringWriter();
         URL serviceUrl = new URL(url);

         BufferedReader in = new BufferedReader(new InputStreamReader(serviceUrl.openStream()));
         String inputLine;
         while ((inputLine = in.readLine()) != null) {
            sw.write(inputLine);
         }
         in.close();

         results = sw.toString();

      } catch (Exception ex) {
         logger.error("error occurred in servlet", ex);
         ex.printStackTrace();
         throw ex;
      }
      return results;
   }


   protected List<String> getRelatedUris(String json) {
	   List<String> uriList = new ArrayList<String>();
	   String uri = "";
	   ArrayNode jsonArray = (ArrayNode) JacksonUtils.parseJson(json);
	    if (jsonArray.size() == 0) {
           return new ArrayList<String>();
        }
	    for (int i = 0; i < jsonArray.size(); i++) {
	    	ObjectNode jsonObj = (ObjectNode) jsonArray.get(i);
            uri = getJsonValue(jsonObj, "uri");
            uriList.add(uri);
	    }

	   return uriList;

   }

	protected List<String> getPropertyFromJson(String json) {
		List<String> props = new ArrayList<String>();
		ArrayNode jsonArray = (ArrayNode) JacksonUtils.parseJson(json);
		if (jsonArray.size() == 0) {
			return new ArrayList<String>();
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			System.out.println((jsonArray.get(i)).toString());
		}
		return props;
	}

   protected String stripConceptId(String uri) {
	     String conceptId = "";
	     int lastslash = uri.lastIndexOf('/');
	     conceptId = uri.substring(lastslash + 1, uri.length());
	     return conceptId;
	  }



}
