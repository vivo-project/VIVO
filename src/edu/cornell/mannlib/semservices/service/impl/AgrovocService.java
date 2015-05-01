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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fao.www.webservices.AgrovocWS.ACSWWebService;
import org.fao.www.webservices.AgrovocWS.ACSWWebServiceServiceLocator;
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
import edu.cornell.mannlib.semservices.util.SKOSUtils;
import edu.cornell.mannlib.semservices.util.XMLUtils;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;

public class AgrovocService implements ExternalConceptService {

	protected final Log logger = LogFactory.getLog(getClass());
	private java.lang.String AgrovocWS_address = "http://agrovoc.fao.org/axis/services/SKOSWS";
	private final String schemeUri = "http://aims.fao.org/aos/agrovoc/agrovocScheme";
	private final String ontologyName = "agrovoc";
	private final String format = "SKOS";
	private final String lang = "en";
	private final String searchMode = "starts with";//Used to be Exact Match, or exact word or starts with
	protected final String dbpedia_endpoint = " http://dbpedia.org/sparql";
	// URL to get all the information for a concept
	
	protected final String conceptSkosMosBase = "http://aims.fao.org/skosmos/rest/v1/";
	protected final String conceptsSkosMosSearch = conceptSkosMosBase + "search?";
	protected final String conceptSkosMosURL = conceptSkosMosBase + "/agrovoc/data?";
	@Override
	public List<Concept> getConcepts(String term) throws Exception {
		List<Concept> conceptList = new ArrayList<Concept>();
		
		//For the RDF webservices mechanism, utilize the following
		/*
		String result = getTermExpansion(this.ontologyName, term,
				this.searchMode, this.format, this.lang);
		
		// return empty conceptList if conceptUri is empty
		if (StringUtils.isEmpty(result)) {
			return conceptList;
		}

		// Get the list of the concept URIs in the RDF
		List<String> conceptUris = getConceptURIsListFromRDF(result);
		*/
		
		//For the SKOSMos search mechanism, utilize this instead
		String result = getSKOSMosSearchResults(term, this.lang);
		List<String> conceptUris = getConceptURIsListFromSkosMosResult(result);
		if (conceptUris.size() == 0)
			return conceptList;
		int conceptCounter = 0;
		
		HashSet<String> encounteredURI = new HashSet<String>();
		
		// Loop through each of these URIs and load using the SKOSManager
		for (String conceptUri : conceptUris) {
			conceptCounter++;
			if (StringUtils.isEmpty(conceptUri)) {
				// If the conceptURI is empty, keep going
				continue;
			}
			if(encounteredURI.contains(conceptUri)) {
				//If we have already encountered this concept URI, do not redisplay or reprocess
				continue;
			}
			encounteredURI.add(conceptUri);

			// Test and see if the URI is valid
			URI uri = null;
			try {
				uri = new URI(conceptUri);
			} catch (URISyntaxException e) {
				logger.error("Error occurred with creating the URI ", e);
				continue;
			}
			// Returns concept information in the format specified, which is
			// currently XML
			// Utilizing Agrovoc's getConceptInfo returns alternate and
			// preferred labels but
			// none of the exact match or close match descriptions
			String bestMatch = "false";
			//Assume the first result is considered the 'best match'
			//Although that is not something we are actually retrieving from the service itself explicitly
			if(conceptCounter == 1) {
				bestMatch = "true";
			}
			Concept c = this.createConcept(bestMatch, conceptUri);
			if (c != null) {
				// Get definition from dbpedia references stored in the close
				// Match list
				List<String> closeMatches = c.getCloseMatchURIList();
				for (String closeMatch : closeMatches) {

					if (closeMatch.startsWith("http://dbpedia.org")) {
						try {
						String description = getDbpediaDescription(closeMatch);
						// System.out.println("description: "+ description);
						c.setDefinition(description);
						} catch (Exception ex) {
							logger.error("An error occurred in the process of retrieving dbpedia description", ex);
						}
					}
				}
				conceptList.add(c);
			}
		}

		return conceptList;
	}



	


	public List<Concept> processResults(String term) throws Exception {
		return getConcepts(term);
	}

	public Concept createConcept(String bestMatch, String skosConceptURI) {

		Concept concept = new Concept();
		concept.setUri(skosConceptURI);
		concept.setConceptId(stripConceptId(skosConceptURI));
		concept.setBestMatch(bestMatch);
		concept.setDefinedBy(schemeUri);
		concept.setSchemeURI(this.schemeUri);
		concept.setType("");

		String encodedURI = URLEncoder.encode(skosConceptURI);
		String encodedFormat = URLEncoder.encode("application/rdf+xml");
		String url = conceptSkosMosURL + "uri=" + encodedURI + "&format="
				+ encodedFormat;

		// Utilize the XML directly instead of the SKOS API
		try {

			concept = SKOSUtils
					.createConceptUsingXMLFromURL(concept, url, "en", false);

		} catch (Exception ex) {
			logger.debug("Error occurred for creating concept "
					+ skosConceptURI, ex);
			return null;
		}

		return concept;
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
		} catch (RemoteException e) {
			logger.error("remote exception", e);
			throw e;
		} catch (MalformedURLException e) {
			logger.error("malformed URL exception", e);
			throw e;
		}
		return result;
	}

	protected String getTermCodeByTermAndLangXML(String ontologyName,
			String searchString, String lang, String codeName, String format) {
		String result = new String();
		ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
		try {
			URL url = new URL(AgrovocWS_address);
			ACSWWebService agrovoc_service = locator.getACSWWebService(url);
			result = agrovoc_service.getTermCodeByTermAndLangXML(ontologyName,
					searchString, lang, codeName, format);
		} catch (ServiceException e) {
			logger.error("service exception", e);
			e.printStackTrace();
		} catch (RemoteException e) {
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

	// Creating method for term expansion
	protected String getTermExpansion(String ontologyName, String term,
			String searchMode, String format, String lang) {
		String result = new String();
		ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
		try {
			URL url = new URL(AgrovocWS_address);
			ACSWWebService agrovoc_service = locator.getACSWWebService(url);
			result = agrovoc_service.getTermExpansion(ontologyName, term,
					format, searchMode, lang); // the actual call has this order
												// for parameters
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
			result = agrovoc_service.getConceptInfoByTermcodeXML(termcode,
					format);
		} catch (ServiceException e) {
			logger.error("service exception", e);
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return result;
	}

	protected String getConceptByKeyword(String ontologyName,
			String searchString, String format, String searchMode, String lang) {
		String result = new String();
		ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
		try {
			URL url = new URL(AgrovocWS_address);
			ACSWWebService agrovoc_service = locator.getACSWWebService(url);
			result = agrovoc_service.getConceptByKeyword(ontologyName,
					searchString, format, searchMode, lang);
		} catch (ServiceException e) {
			logger.error("service exception", e);
			e.printStackTrace();
		} catch (RemoteException e) {
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

			BufferedReader in = new BufferedReader(new InputStreamReader(
					rss.openStream()));
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

	public List<Concept> getConceptsByURIWithSparql(String uri)
			throws Exception {
		// deprecating this method...just return an empty list
		List<Concept> conceptList = new ArrayList<Concept>();
		return conceptList;
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
			// e.printStackTrace();
			throw e;
		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// e.printStackTrace();
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
			System.err.println("rdf: " + rdf);
		} catch (SAXException e) {
			e.printStackTrace();
			System.err.println("rdf: " + rdf);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.err.println("rdf: " + rdf);
		}
		return conceptUri;

	}

	// When utilizing the getTermExpansion method, will get a list of URIs back
	// and not just one URI
	protected List<String> getConceptURIsListFromRDF(String rdf) {
		List<String> conceptUris = new ArrayList<String>();
		try {
			Document doc = XMLUtils.parse(rdf);
				NodeList nodes = doc.getElementsByTagName("skos:Concept");
				int numberNodes = nodes.getLength();
				int n;
				for (n = 0; n < numberNodes; n++) {
					Node node = nodes.item(n);
					NamedNodeMap attrs = node.getAttributes();
					Attr idAttr = (Attr) attrs.getNamedItem("rdf:about");
					String conceptUri = idAttr.getTextContent();
					conceptUris.add(conceptUri);
				}

			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("rdf: " + rdf);
		} catch (SAXException e) {
			e.printStackTrace();
			System.err.println("rdf: " + rdf);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.err.println("rdf: " + rdf);
		}
		return conceptUris;

	}

	protected String getDbpediaDescription(String uri) throws Exception {
		String descriptionSource = " (Source: DBpedia)";
		String description = new String();
		String qs = ""
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>\n"
				+ "SELECT DISTINCT ?description WHERE { \n" + "<" + uri
				+ "> rdfs:comment ?description . \n"
				+ "FILTER (LANG(?description)='en' ) \n" + "}";
		// System.out.println(qs);
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
		// Adding source so it is clear that this description comes from DBPedia
		return description + descriptionSource;
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

	// Get concept using agrovoc service
	protected String getConceptInfoByURI(String ontologyName,
			String conceptURI, String format) {
		String result = new String();
		ACSWWebServiceServiceLocator locator = new ACSWWebServiceServiceLocator();
		try {
			URL url = new URL(AgrovocWS_address);
			ACSWWebService agrovoc_service = locator.getACSWWebService(url);
			result = agrovoc_service.getConceptByURI(ontologyName, conceptURI,
					format);
		} catch (ServiceException e) {
			logger.error("service exception", e);
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * The code here utilizes the SKOSMOS REST API for Agrovoc
	 * This returns JSON LD so we would parse JSON instead of RDF
	 * The code above can still be utilized if we need to employ the web services directly
	 */
	//Get search results for a particular term and language code
		private String getSKOSMosSearchResults(String term, String lang) {
			String urlEncodedTerm = URLEncoder.encode(term); 
			//Utilize 'starts with' using the * operator at the end
			String searchUrlString = this.conceptsSkosMosSearch + "query=" + urlEncodedTerm + "*" + "&lang=" + lang;
			URL searchURL = null;
			try {
				searchURL = new URL(searchUrlString);
			} catch (Exception e) {
				logger.error("Exception occurred in instantiating URL for "
						+ searchUrlString, e);
				// If the url is having trouble, just return null for the concept
				return null;
			}
			
			String results = null;
			try {

				StringWriter sw = new StringWriter();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						searchURL.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					sw.write(inputLine);
				}
				in.close();

				results = sw.toString();
				logger.debug(results);
			} catch (Exception ex) {
				logger.error("Error occurred in getting concept from the URL "
						+ searchUrlString, ex);
				return null;
			}
			return results;
			
		}
		
		//JSON-LD array
		private List<String> getConceptURIsListFromSkosMosResult(String results) {
			List<String> conceptURIs = new ArrayList<String>();
			JSONObject json = (JSONObject) JSONSerializer.toJSON(results);
			//Format should be: { ..."results":["uri":uri...]
			if (json.containsKey("results")) {
				JSONArray jsonArray = json.getJSONArray("results");
				int numberResults = jsonArray.size();
				int i;
				for(i = 0; i < numberResults; i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if(jsonObject.containsKey("uri")) {
						conceptURIs.add(jsonObject.getString("uri"));
					}
				}
			}
			return conceptURIs;
		}

		
		

}
