/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.service.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fao.www.webservices.AgrovocWS.ACSWWebService;
import org.fao.www.webservices.AgrovocWS.ACSWWebServiceServiceLocator;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSEntity;
import org.semanticweb.skos.SKOSLiteral;
import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.semservices.util.XMLUtils;

public class AgrovocService implements ExternalConceptService  {
	
   protected final Log logger = LogFactory.getLog(getClass());
   private java.lang.String AgrovocWS_address = "http://agrovoc.fao.org/axis/services/SKOSWS";
   private final String schemeUri = "http://aims.fao.org/aos/agrovoc/agrovocScheme";
   private final String baseUri = "http://aims.fao.org/aos/agrovoc/";
   private final String ontologyName = "agrovoc";
   private final String format = "SKOS";
   private final String lang = "en";
   private final String codeName = "hasCodeAgrovoc";
   private final String searchMode = "Exact Match";
   protected final String dbpedia_endpoint = " http://dbpedia.org/sparql";

  
   
	@Override
	public List<Concept> getConcepts(String term) throws Exception {
		List<Concept> conceptList = new ArrayList<Concept>();
		//System.out.println("Searching for term: "+ term);
		String result = getURIByTermAndLangXML(this.ontologyName, term, this.searchMode, this.format, this.lang);
		// return empty conceptList if conceptUri is empty
        if (StringUtils.isEmpty(result)) {
        	return conceptList;
        }
		
		// First create a new SKOSManager
		String conceptUri = getConceptURIFromRDF(result);
        SKOSManager manager = new SKOSManager();
        
        // return empty conceptList if conceptUri is empty
        if (StringUtils.isEmpty(conceptUri)) {
        	return conceptList;
        }
        URI uri = null;
		try {
			uri = new URI(conceptUri);
		} catch (URISyntaxException e) { 
			e.printStackTrace();
			return conceptList;
		}
		
		System.out.println("uri: "+uri); 
        SKOSDataset dataset = manager.loadDataset(uri);
        
        for (SKOSConcept skosConcept : dataset.getSKOSConcepts()) {
        	Concept concept = new Concept();
            System.out.println("Concept: " + skosConcept.getURI());
            concept.setUri(skosConcept.getURI().toString());
            concept.setConceptId(stripConceptId(skosConcept.getURI().toString()));
            concept.setBestMatch("true");
            concept.setDefinedBy(this.schemeUri);
            concept.setSchemeURI(this.schemeUri);
            concept.setType("");
            String lang = "";
            
            for (SKOSLiteral literal : skosConcept.getSKOSRelatedConstantByProperty(dataset, manager.getSKOSDataFactory().getSKOSPrefLabelProperty())) {
              
              if (!literal.isTyped()) {
                  // if it has  language
                  SKOSUntypedLiteral untypedLiteral = literal.getAsSKOSUntypedLiteral();
                  if (untypedLiteral.hasLang()) {
                      lang = untypedLiteral.getLang();
                  } else {
                	  lang = "";
                  }
              }
              if (lang.equals("en")) {
                 //System.out.println("prefLabel: " + literal.getLiteral());
                 
                 concept.setLabel(literal.getLiteral());
              }
            }
            
            // get the broader property URI
            List<String> broaderURIList = new ArrayList<String>();
            for (SKOSAnnotation annotation: skosConcept.getSKOSAnnotationsByURI(dataset, manager.getSKOSDataFactory().getSKOSBroaderProperty().getURI())) {
            	String value = new String();
            	if (annotation.isAnnotationByConstant()) {
                   SKOSLiteral literal = annotation.getAnnotationValueAsConstant();
                   value = literal.getLiteral(); 
            	   //System.out.println("broder uri: "+ value);
            	} else {
                    // annotation is some resource
                    SKOSEntity entity = annotation.getAnnotationValue();
                    value = entity.getURI().toString();
                }
            	//System.out.println("broader uri: "+value);
            	broaderURIList.add(value);
            }
            concept.setBroaderURIList(broaderURIList);
            
            // get the narrower property URI
            List<String> narrowerURIList = new ArrayList<String>();
            for (SKOSAnnotation annotation: skosConcept.getSKOSAnnotationsByURI(dataset, manager.getSKOSDataFactory().getSKOSNarrowerProperty().getURI())) {
            	String value = new String();
            	if (annotation.isAnnotationByConstant()) {
                   SKOSLiteral literal = annotation.getAnnotationValueAsConstant();
                   value = literal.getLiteral(); 
            	   //System.out.println("narrower uri: "+ value);
            	} else {
                    // annotation is some resource
                    SKOSEntity entity = annotation.getAnnotationValue();
                    value = entity.getURI().toString();
                }
            	//System.out.println("narrower uri: "+value);
            	narrowerURIList.add(value);
            }
            concept.setNarrowerURIList(narrowerURIList);
            
            // exact match
            List<String> exactMatchURIList = new ArrayList<String>();
            for (SKOSAnnotation annotation: skosConcept.getSKOSAnnotationsByURI(dataset, manager.getSKOSDataFactory().getSKOSExactMatchProperty().getURI())) {
            	String value = new String();
            	if (annotation.isAnnotationByConstant()) {
                   SKOSLiteral literal = annotation.getAnnotationValueAsConstant();
                   value = literal.getLiteral(); 
            	   //System.out.println("exact match: "+ value);
            	} else {
                    // annotation is some resource
                    SKOSEntity entity = annotation.getAnnotationValue();
                    value = entity.getURI().toString();
                }
            	//System.out.println("exact match: "+value);
            	exactMatchURIList.add(value);
            }
            concept.setExactMatchURIList(exactMatchURIList);
            
            // close match
            List<String> closeMatchURIList = new ArrayList<String>();
            for (SKOSAnnotation annotation: skosConcept.getSKOSAnnotationsByURI(dataset, manager.getSKOSDataFactory().getSKOSCloseMatchProperty().getURI())) {
            	String value = new String();
            	if (annotation.isAnnotationByConstant()) {
                   SKOSLiteral literal = annotation.getAnnotationValueAsConstant();
                   value = literal.getLiteral(); 
            	   //System.out.println("close match: "+ value);
            	} else {
                    // annotation is some resource
                    SKOSEntity entity = annotation.getAnnotationValue();
                    value = entity.getURI().toString();
                }
            	//System.out.println("close match: "+value);
            	closeMatchURIList.add(value);
            	if (value.startsWith("http://dbpedia.org")) {
            		String description = getDbpediaDescription(value);
            		//System.out.println("description: "+ description);
            		concept.setDefinition(description);
            	}
            }
            concept.setCloseMatchURIList(closeMatchURIList);
            
            conceptList.add(concept);
             

        }
		return conceptList;
	}

   public List<Concept> processResults(String term) throws Exception {
       return getConcepts(term);
   }
   
   
   
   @Deprecated
   protected String getTermcodeByTerm(String term) throws Exception {
      String result = new String();
      ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         ACSWWebService agrovoc_service = locator.getACSWWebService(url);
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

    
   
   protected String getTermCodeByTermAndLangXML(String ontologyName, String searchString, String lang, String codeName, String format) {
	      String result = new String();
	      ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
	      try {
	         URL url = new URL(AgrovocWS_address);
	         ACSWWebService agrovoc_service = locator.getACSWWebService(url);
	         result = agrovoc_service.getTermCodeByTermAndLangXML(ontologyName, searchString, lang, codeName, format);
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

    

	protected String getURIByTermAndLangXML(String ontologyName, String term,
			String searchMode, String format, String lang) {
		String result = new String();
		ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
		try {
			URL url = new URL(AgrovocWS_address);
			ACSWWebService agrovoc_service = locator.getACSWWebService(url);
			result = agrovoc_service.getURIByTermAndLangXML(ontologyName, term,
					searchMode, format, lang);
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return result;
	}

   
 
    

   protected String getConceptInfoByTermcodeXML(String termcode, String format) {
      String result = new String();
      ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
      try {
         URL url = new URL(AgrovocWS_address);
         ACSWWebService agrovoc_service = locator.getACSWWebService(url);
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
   
   protected String getConceptByKeyword(String ontologyName, String searchString, String format, String searchMode, String lang) {
	      String result = new String();
	      ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
	      try {
	         URL url = new URL(AgrovocWS_address);
	         ACSWWebService agrovoc_service = locator.getACSWWebService(url);
	         result = agrovoc_service.getConceptByKeyword(ontologyName, searchString, format, searchMode, lang);
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
         URL rss = new URL(this.AgrovocWS_address + "?wsdl");

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


   public List<Concept> getConceptsByURIWithSparql(String uri) throws Exception {
	 //John Ferreira's original code has implementation
	      List<Concept> newConceptList = new ArrayList<Concept>();
	      return newConceptList;
   }
   
   protected String getAgrovocTermCode(String rdf) throws Exception {
	   String termcode = new String();
	   try {
	      Document doc = XMLUtils.parse(rdf);
	      NodeList nodes = doc.getElementsByTagName("hasCodeAgrovoc");
	      if (nodes.item(0) != null) {
	         Node node = nodes.item(0);
	         termcode = node.getTextContent();
	      }
	         
	   } catch (SAXException e) {
	      //e.printStackTrace();
	      throw e;
	   } catch (ParserConfigurationException e) {
	      //e.printStackTrace();
	      throw e;
	   } catch (IOException e) { 
		  //e.printStackTrace();
		  throw e;
	   }
	   return termcode;
   }
   
	protected String getConceptURIFromRDF(String rdf) {
		String conceptUri = new String();
		try {
			Document doc = XMLUtils.parse(rdf);
			NodeList nodes = doc.getElementsByTagName("skos:Concept");
			Node node = nodes.item(0);

			NamedNodeMap attrs = node.getAttributes();
			Attr idAttr = (Attr) attrs.getNamedItem("rdf:about");
			conceptUri = idAttr.getTextContent();
		} catch (IOException e) { 
			e.printStackTrace();
			System.err.println("rdf: "+rdf);
		} catch (SAXException e) { 
			e.printStackTrace();
			System.err.println("rdf: "+rdf);
		} catch (ParserConfigurationException e) { 
			e.printStackTrace();
			System.err.println("rdf: "+rdf);
		}
		return conceptUri;

	}
	
	protected String getDbpediaDescription(String uri) throws Exception{
		String description = new String();
		String qs = ""
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>\n"
				+ "SELECT DISTINCT ?description WHERE { \n" + "<" + uri
				+ "> rdfs:comment ?description . \n"
				+ "FILTER (LANG(?description)='en' ) \n" + "}";
		//System.out.println(qs);
		List<HashMap> resultList = new ArrayList<HashMap>();
		QueryExecution qexec = null;
		try {

			Query query = QueryFactory.create(qs);
			qexec = QueryExecutionFactory.sparqlService(this.dbpedia_endpoint,
					query);
			resultList = new ArrayList<HashMap>();
			ResultSet resultSet = qexec.execSelect();
			int resultSetSize = 0;
			while (resultSet.hasNext()) {
				resultSetSize++;
				QuerySolution solution = resultSet.nextSolution();
				Iterator varnames = solution.varNames();
				HashMap<String, String> hm = new HashMap<String, String>();
				while (varnames.hasNext()) {
					String name = (String) varnames.next();
					RDFNode rdfnode = solution.get(name);
					// logger.info("rdf node name, type: "+ name
					// +", "+getRDFNodeType(rdfnode));
					if (rdfnode.isLiteral()) {
						Literal literal = rdfnode.asLiteral();
						String nodeval = literal.getString();
						hm.put(name, nodeval);
					} else if (rdfnode.isResource()) {
						Resource resource = rdfnode.asResource();
						String nodeval = resource.toString();
						hm.put(name, nodeval);
					}
				}
				resultList.add(hm);
			}
			description = "";
			for (HashMap map : resultList) {
				if (map.containsKey("description")) {
					description = (String) map.get("description");
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return description;
	}
   
   /**
    * @param uri
    * @return
    */
   protected String stripConceptId(String uri) {
      String conceptId = new String();
      int lastslash = uri.lastIndexOf('/');
      conceptId = uri.substring(lastslash + 1, uri.length());
      return conceptId;
   }

   /**
    * @param str
    * @return
    */
   protected String extractConceptId(String str) {
      try {
         return str.substring(1, str.length() - 1);
      } catch (Exception ex) {
         return "";
      }
   }
   
   

}
