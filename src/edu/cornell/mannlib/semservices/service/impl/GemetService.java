/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.service.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fao.gilw.aims.webservices.AgrovocWS;
import org.fao.gilw.aims.webservices.AgrovocWSServiceLocator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.semservices.util.XMLUtils;

public class GemetService implements ExternalConceptService  {
   protected final Log logger = LogFactory.getLog(getClass());
   private final String GemetWS_address = "http://www.eionet.europa.eu/gemet/";
   private final String narrowerUri = "http://www.w3.org/2004/02/skos/core%23narrower";
   private final String broaderUri = "http://www.w3.org/2004/02/skos/core%23broader";
   private final String relatedUri = "http://www.w3.org/2004/02/skos/core%23related";
   private final String definitionUri = "http://www.w3.org/2004/02/skos/core%23definition";
   private final String prefLabelUri = "http://www.w3.org/2004/02/skos/core%23prefLabel";
   private final String scopeNoteUri = "http://www.w3.org/2004/02/skos/core%23scopeNote";
   private final String altLabelUri = "http://www.w3.org/2004/02/skos/core%23altLabel";
   private final String exampleUri = "http://www.w3.org/2004/02/skos/core%23example";
   private final String acronymLabelUri = "http://www.w3.org/2004/02/skos/core%23acronymLabel";

   public List<Concept> processResults(String term) throws Exception {
      List<Concept> conceptList = new ArrayList<Concept>();
      String results = getConceptsMatchingKeyword(term);
      conceptList = processOutput(results);
      return conceptList;

   }

   /**
    * @param results
    * @return
    */
   private List<Concept> processOutput(String results) {

      List<Concept> conceptList = new ArrayList<Concept>();

      try {
         JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON( results );
	   
			for (int i = 0; i < jsonArray.size(); i++) {
				Concept concept = new Concept();
				concept
						.setDefinedBy("http://www.eionet.europa.eu/gemet/gemetThesaurus");
				concept.setBestMatch("true");
				JSONObject json = jsonArray.getJSONObject(i);
				String uri = getJsonValue(json, "uri");

				concept.setUri(uri);
				concept.setConceptId(uri);
				if (json.has("preferredLabel")) {
					JSONObject preferredLabelObj = json
							.getJSONObject("preferredLabel");
					if (preferredLabelObj.has("string")) {
						concept.setLabel(getJsonValue(preferredLabelObj,
								"string"));
					}
				}
				if (json.has("definition")) {
					JSONObject definitionObj = json.getJSONObject("definition");
					if (definitionObj.has("string")) {
						concept.setDefinition(getJsonValue(definitionObj,
								"string"));
					}
				}
				conceptList.add(concept);

			}
         

      } catch (Exception ex ) {
         ex.printStackTrace();
         logger.error("Could not get concepts", ex);
      }
      return conceptList;

   }

   /**
    * Get a string from a json object or an empty string if there is no value for the given key
   * @param obj
   * @param key
   * @return
   */
  protected String getJsonValue(JSONObject obj, String key) {
      if (obj.has(key)) {
         return obj.getString(key);
      } else {
         return new String("");
      }
   }


   protected String getAvailableLangs(String concept_uri) throws Exception {
      String result = new String();
      String serviceUrl = GemetWS_address + "getAvailableLanguages" +
      "?concept_uri=" + concept_uri;
      result = getGemetResults(serviceUrl);
      return result;
   }

   protected String getConcept(String concept_uri) throws Exception {
      String result = new String();
      String serviceUrl = GemetWS_address + "getConcept" +
      "?concept_uri=" + concept_uri +
      "&language=en";
      result = getGemetResults(serviceUrl);
      return result;
   }
   protected String getAllTranslationsForConcept(String concept_uri, String property) throws Exception  {
      String result = new String();
      String property_uri = new String();
      if (property.equals("definition")) {
         property_uri = definitionUri;
      } else if (property.equals("preferredLabel")) {
         property_uri = prefLabelUri;
      } else if (property.equals("scopeNote")) {
         property_uri = scopeNoteUri;
      } else if (property.equals("nonPreferredLabels")) {
         property_uri = altLabelUri;
      } else if (property.equals("example")) {
         property_uri = exampleUri;
      } else if (property.equals("acronymLabel")) {
         property_uri = acronymLabelUri;
      }

      String serviceUrl = GemetWS_address + "getAllTranslationsForConcept" +
      "?concept_uri=" + concept_uri +
      "&property_uri=" + property_uri +
      "&language=en";
      result = getGemetResults(serviceUrl);
      return result;
   }


   protected String getRelatedConcepts(String concept_uri, String relation) throws Exception {
      String result = new String();
      String relation_uri = new String();
      if (relation.equals("broader")) {
         relation_uri = broaderUri;
      } else if (relation.equals("narrower")) {
         relation_uri = narrowerUri;
      } else if (relation.equals("related")) {
         relation_uri = relatedUri;
      }
      String serviceUrl = GemetWS_address + "getRelatedConcepts" +
      "?concept_uri=" + concept_uri +
      "&relation_uri=" + relation_uri +
      "&language=en";
      result = getGemetResults(serviceUrl);
      return result;
   }



   protected String getConceptsMatchingKeyword(String keyword) throws Exception {
      String result = new String();
      String serviceUrl = GemetWS_address + "getConceptsMatchingKeyword" +
      "?keyword="  + keyword +
      "&search_mode=0" +
      "&thesaurus_uri=http://www.eionet.europa.eu/gemet/concept/" +
      "&language=en";

      result = getGemetResults(serviceUrl);
      return result;

   }

   protected String getGemetResults(String url) throws Exception {
      String results = new String();
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
         throw ex;
      }
      return results;
   }

}
