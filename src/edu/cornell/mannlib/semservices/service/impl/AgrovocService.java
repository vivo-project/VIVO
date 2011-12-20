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

public class AgrovocService implements ExternalConceptService  {
   protected final Log logger = LogFactory.getLog(getClass());
   private java.lang.String AgrovocWS_address = "http://www.fao.org/webservices/AgrovocWS";

   public List<Concept> processResults(String term) throws Exception {
      List<Concept> conceptList = new ArrayList<Concept>();

      String termcode;
      try {
         termcode = getTermcodeByTerm(term);
      } catch (Exception e1) {
         logger.error("Could not get termcode from service", e1);
         throw e1;
      }

      String format = "SKOS";
      // if the termcode is null it means that either the service is not responding
      // or there was not a match for the string
      //System.out.println("Got termcode: "+termcode);

      String results = getConceptInfoByTermcodeXML(termcode, format);

      //XMLUtils.prettyPrint(results);

      try {
         Document doc = XMLUtils.parse(results);
         String prefLabelQuery = "child::*[@xml:lang='EN']";
         NodeList nodes = doc.getElementsByTagName("skos:Concept");
         //System.out.println("Found this many nodes: "+ nodes.getLength());
         for (int i=0; i < nodes.getLength(); i++) {

            Node node = nodes.item(i);
            //XMLUtils.serializeNode(node); System.out.println();

            Concept concept = new Concept();
            concept.setDefinedBy("http://aims.fao.org/aos/agrovoc/agrovocScheme");
            concept.setConceptId(termcode);

            NamedNodeMap attrs = node.getAttributes();
            Attr idAttr = (Attr) attrs.getNamedItem("rdf:about");
            String conceptUri = idAttr.getTextContent();
            concept.setUri(conceptUri);

            Node prefLabelNode = XMLUtils.getNodeWithXpath(node, prefLabelQuery);
            if (prefLabelNode != null) {
               String prefLabel = prefLabelNode.getTextContent();
               concept.setLabel(prefLabel);
            }
            conceptList.add(concept);
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      }
      return conceptList;
   }


   protected String getAgrovocLanguages() {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getAgrovocLanguages();
      } catch (ServiceException e) {
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getTermcodeByTerm(String term) throws Exception {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getTermcodeByTerm(term);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         throw e;
      }  catch (RemoteException e) {
         logger.error("remote exception", e);
         throw e;
      } catch (MalformedURLException e) {
         logger.error("malformed URL exception", e);
         throw e;
      }
      return result;
   }

   protected String getTermcodeByTermXML(String term, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getTermcodeByTermXML(term, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
      return result;
   }

   protected String getTermByLanguage(int termcode, String language) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getTermByLanguage(termcode, language);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
      return result;
   }

   protected String getTermByLanguageXML(int termcode, String language, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getTermByLanguageXML(termcode, language, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getTermsListByLanguage2(String termcodes, String language, String sep) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getTermsListByLanguage2(termcodes, language, sep);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
      return result;
   }

   protected String getTermsListByLanguageXML(String termcodes, String language, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getTermsListByLanguageXML(termcodes, language, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getAllLabelsByTermcode2(int termcode, String sep) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getAllLabelsByTermcode2(termcode, sep);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getAllLabelsByTermcodeXML(int termcode, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getAllLabelsByTermcodeXML(termcode, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String simpleSearchByMode2(String term, String mode, String sep ) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.simpleSearchByMode2(term, mode, sep);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String simpleSearchByModeXML(String term, String mode, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.simpleSearchByModeXML(term, mode, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String searchByTerm2(String term, String sep) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.searchByTerm2(term, sep);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String searchByTermXML(String term, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.searchByTermXML(term, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String searchCategoryByMode(String term, String lang, String schemeid, String mode, String sep) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.searchCategoryByMode(term, lang, schemeid, mode, sep);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String searchCategoryByModeXML(String term, String mode, String schemeid, String lang, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.searchCategoryByModeXML(term, mode, schemeid, lang, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String[] getConceptInfoByTermcode(String termcode) {
      String result[] = null;
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getConceptInfoByTermcode(termcode);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getConceptInfoByTermcodeXML(String termcode, String format) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getConceptInfoByTermcodeXML(termcode, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String[] getDefinitions(int termcode, String lang) {
      String[] result = null;
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getDefinitions(termcode, lang);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getDefinitionsXML(int termcode, String lang, String format) {
      String result = null;
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getDefinitionsXML(termcode, lang, format);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getTermExpansion(String aQuery, String langugage) {
      String result = new String();
      AgrovocWSServiceLocator locator = new AgrovocWSServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         AgrovocWS agrovoc_service = locator.getAgrovocWS(url);
         result = agrovoc_service.getTermExpansion(aQuery, langugage);
      } catch (ServiceException e) {
         logger.error("service exception", e);
         e.printStackTrace();
      }  catch (RemoteException e) {
         e.printStackTrace();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      return result;
   }

   protected String getWsdl() {
      String result = new String();
      try {

         StringWriter sw = new StringWriter();
         URL rss = new URL(AgrovocWS_address + "?wsdl");

         BufferedReader in = new BufferedReader(new InputStreamReader(rss.openStream()));
         String inputLine;
         while ((inputLine = in.readLine()) != null) {
            sw.write(inputLine);
         }
         in.close();

         result = sw.toString();

      } catch (Exception ex) {
         logger.error("error occurred in servlet", ex);
      }
      return result;
   }
}
