/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.semservices.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.semservices.util.SKOSUtils;
import edu.cornell.mannlib.semservices.util.XMLUtils;
import edu.cornell.mannlib.vitro.webapp.utils.json.JacksonUtils;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;

public class AgrovocService implements ExternalConceptService {

	protected final Log logger = LogFactory.getLog(getClass());
	private final String schemeUri = "http://aims.fao.org/aos/agrovoc/agrovocScheme";
	private final String ontologyName = "agrovoc";
	private final String format = "SKOS";
	private final String lang = "en";
	private final String searchMode = "starts with";//Used to be Exact Match, or exact word or starts with
	protected final String dbpedia_endpoint = " http://dbpedia.org/sparql";
	// URL to get all the information for a concept

	protected final String conceptSkosMosBase = "http://agrovoc.uniroma2.it/agrovoc/rest/v1/";
	protected final String conceptsSkosMosSearch = conceptSkosMosBase + "search?";
	protected final String conceptSkosMosURL = conceptSkosMosBase + "data?";
	@Override
	public List<Concept> getConcepts(String term) throws Exception {
		List<Concept> conceptList = new ArrayList<>();

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

		HashSet<String> encounteredURI = new HashSet<>();

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

	public List<Concept> getConceptsByURIWithSparql(String uri)
			throws Exception {
		// deprecating this method...just return an empty list
		List<Concept> conceptList = new ArrayList<>();
		return conceptList;
	}

	protected String getAgrovocTermCode(String rdf) throws Exception {
		String termcode = "";
		try {
			Document doc = XMLUtils.parse(rdf);
			NodeList nodes = doc.getElementsByTagName("hasCodeAgrovoc");
			if (nodes.item(0) != null) {
				Node node = nodes.item(0);
				termcode = node.getTextContent();
			}

		} catch (SAXException | IOException | ParserConfigurationException e) {
			// e.printStackTrace();
			throw e;
		}
		return termcode;
	}

	protected String getConceptURIFromRDF(String rdf) {
		String conceptUri = "";
		try {
			Document doc = XMLUtils.parse(rdf);
			NodeList nodes = doc.getElementsByTagName("skos:Concept");
			Node node = nodes.item(0);

			NamedNodeMap attrs = node.getAttributes();
			Attr idAttr = (Attr) attrs.getNamedItem("rdf:about");
			conceptUri = idAttr.getTextContent();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			System.err.println("rdf: " + rdf);
		}
		return conceptUri;

	}

	// When utilizing the getTermExpansion method, will get a list of URIs back
	// and not just one URI
	protected List<String> getConceptURIsListFromRDF(String rdf) {
		List<String> conceptUris = new ArrayList<>();
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


		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			System.err.println("rdf: " + rdf);
		}
		return conceptUris;

	}

	protected String getDbpediaDescription(String uri) throws Exception {
		String descriptionSource = " (Source: DBpedia)";
		String description = "";
		String qs = ""
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>\n"
				+ "SELECT DISTINCT ?description WHERE { \n" + "<" + uri
				+ "> rdfs:comment ?description . \n"
				+ "FILTER (LANG(?description)='en' ) \n" + "}";
		// System.out.println(qs);
		List<HashMap> resultList = new ArrayList<>();
		QueryExecution qexec = null;
		try {

			Query query = QueryFactory.create(qs);
			qexec = QueryExecutionFactory.sparqlService(this.dbpedia_endpoint, query);
			qexec.setTimeout(5000, TimeUnit.MILLISECONDS);
			resultList = new ArrayList<>();
			ResultSet resultSet = qexec.execSelect();
			int resultSetSize = 0;
			while (resultSet.hasNext()) {
				resultSetSize++;
				QuerySolution solution = resultSet.nextSolution();
				Iterator varnames = solution.varNames();
				HashMap<String, String> hm = new HashMap<>();
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
	 * @param uri The URI
	 */
	protected String stripConceptId(String uri) {
		String conceptId = "";
		int lastslash = uri.lastIndexOf('/');
		conceptId = uri.substring(lastslash + 1, uri.length());
		return conceptId;
	}

	/**
	 * @param str The String
	 */
	protected String extractConceptId(String str) {
		try {
			return str.substring(1, str.length() - 1);
		} catch (Exception ex) {
			return "";
		}
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
			List<String> conceptURIs = new ArrayList<>();
			ObjectNode json = (ObjectNode) JacksonUtils.parseJson(results);
			//Format should be: { ..."results":["uri":uri...]
			if (json.has("results")) {
				ArrayNode jsonArray = (ArrayNode) json.get("results");
				int numberResults = jsonArray.size();
				int i;
				for(i = 0; i < numberResults; i++) {
					ObjectNode jsonObject = (ObjectNode) jsonArray.get(i);
					if(jsonObject.has("uri")) {
						conceptURIs.add(jsonObject.get("uri").asText());
					}
				}
			}
			return conceptURIs;
		}




}
