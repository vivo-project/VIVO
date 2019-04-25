/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.semservices.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.semservices.util.SKOSUtils;
import edu.cornell.mannlib.semservices.util.XMLUtils;
import edu.cornell.mannlib.vitro.webapp.utils.json.JacksonUtils;

public class LCSHService implements ExternalConceptService {

	protected final Log log = LogFactory.getLog(getClass());
	private final String skosSuffix = ".skos.rdf";
	private final String hostUri = "http://id.loc.gov";
	private final String schemeUri = hostUri + "/authorities/subjects";
	private final String baseUri = hostUri + "/search/";



	@Override
	public List<Concept> getConcepts(String term) throws Exception {
		List<Concept> conceptList = new ArrayList<Concept>();
		String results = null;
		String dataUrl = baseUri + "?q=" + URLEncoder.encode(term, "UTF-8")
				+ "&q=cs%3Ahttp%3A%2F%2Fid.loc.gov%2Fauthorities%2Fsubjects"
				+ "&format=atom";
		log.debug("dataURL " + dataUrl);

		try {

			StringWriter sw = new StringWriter();
			URL rss = new URL(dataUrl);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					rss.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sw.write(inputLine);
			}
			in.close();

			results = sw.toString();
			log.debug(results);
		} catch (Exception ex) {
			log.error("error occurred in servlet", ex);
			return null;
		}

		if (StringUtils.isEmpty(results)) {
			return conceptList;
		}

		conceptList = processOutput(results);

		return conceptList;
	}

	// Results are in XML format (atom) - atom entries need to be extracted
	// retrieve the URIs and get the SKOS version of the entry, getting broader
	// and narrower terms as applicable as well as any description (skos:note)
	// that might exist
	private List<Concept> processOutput(String results) throws Exception {
		List<Concept> conceptList = new ArrayList<Concept>();
		// Get uris from the results
		List<String> uris = getConceptURIFromXML(results);
		String bestMatch = "true";
		int i = 0;
		for (String uri : uris) {
			if(i > 0) {
				bestMatch = "false";
			}
			log.debug("-" + uri + "-");
			//This is the URL for retrieving the concept - the pattern is http://id.loc.gov/authorities/subjects/sh85014203.skos.rdf
			//This is not the URI itself which would be http://id.loc.gov/authorities/subjects/sh85014203
			String conceptURLString = getSKOSURL(uri);
			String baseConceptURI = uri;
			URL conceptURL = null;
			try {
				conceptURL = new URL(conceptURLString);
			} catch (Exception e) {
				log.error("Error in trying to retrieve concept " + conceptURLString, e);
				return conceptList;
			}
			log.debug("loading concept uri " + conceptURLString);
			Concept c = this.createConcept(bestMatch, conceptURLString, baseConceptURI);
			if(c != null) {
				conceptList.add(c);
			}
			i++;

		}
		return conceptList;
	}


	//Load individual concept using a request
	//private

	public Concept createConcept(String bestMatch, String conceptURLString, String skosConceptURI) {

		Concept concept = new Concept();

		log.debug("SKOSConceptURI is " + skosConceptURI);
		// get skos version of uri

		concept.setUri(skosConceptURI);
		concept.setConceptId(stripConceptId(skosConceptURI));
		concept.setBestMatch(bestMatch);
		concept.setDefinedBy(schemeUri);
		concept.setSchemeURI(schemeUri);
		concept.setType("");

		//Utilize the XML directly instead of the SKOS API
		try {
			//LCSH doesn't need a language tag right now as results in english
			//Also want to add skos notes as definition
			concept = SKOSUtils.createConceptUsingXMLFromURL(concept, conceptURLString, null, true);

		}  catch(Exception ex) {
			log.debug("Error occurred for annotation retrieval for skos concept " + skosConceptURI, ex);
			return null;
		}


		return concept;
	}



	private String getSKOSURL(String uri) {
		String skosURI = uri + skosSuffix;

		return skosURI;
	}



	public List<String> getConceptURISFromJSON(String results) {
		List<String> uris = new ArrayList<String>();
		try {
			ObjectNode json = (ObjectNode) JacksonUtils.parseJson(results);
			log.debug(json.toString());
			// Get atom entry elements

		} catch (Exception ex) {
			log.error("Could not get concepts", ex);
			throw ex;
		}
		return uris;

	}

	protected List<String> getConceptURIFromXML(String rdf) {
		List<String> uris = new ArrayList<String>();
		String conceptUri = "";
		try {
			Document doc = XMLUtils.parse(rdf);
			NodeList nodes = doc.getElementsByTagName("entry");
			int len = nodes.getLength();
			int i, j;
			for (i = 0; i < len; i++) {
				Node node = nodes.item(i);
				NodeList childNodes = node.getChildNodes();
				for(j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if(childNode.getNodeName().equals("link")) {
						NamedNodeMap attrs = childNode.getAttributes();
						Attr hrefAttr = (Attr) attrs.getNamedItem("href");
						//if type doesn't exist, this is the direct URL without extension
						if((hrefAttr != null) && ((Attr)attrs.getNamedItem("type") == null)) {
							conceptUri = hrefAttr.getTextContent();
						}

					}
				}

				log.debug("concept uri is " + conceptUri);
				uris.add(conceptUri);
			}

		} catch (IOException | ParserConfigurationException | SAXException e) {
			log.error("error occurred in parsing " +rdf, e);
		}
        return uris;

	}

	public List<Concept> processResults(String term) throws Exception {
		return getConcepts(term);
	}

	/**
	 * @param uri URI
	 */
	protected String stripConceptId(String uri) {
		String conceptId = "";
		int lastslash = uri.lastIndexOf('/');
		conceptId = uri.substring(lastslash + 1, uri.length());
		return conceptId;
	}

	/**
	 * @param str String with concept id
	 */
	protected String extractConceptId(String str) {
		try {
			return str.substring(1, str.length() - 1);
		} catch (Exception ex) {
			log.error("Exception occurred in extracting concept id for " + str, ex);
			return "";
		}
	}

	@Override
	public List<Concept> getConceptsByURIWithSparql(String uri)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
