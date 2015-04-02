/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* We are no longer using the SKOS API since Vitro has moved to V 4.0 of OWL API which does not appear to be compatible.
 This file will contain methods used for reading SKOS as XML and parsing it for the properties
 we want to extract*/

package edu.cornell.mannlib.semservices.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cornell.mannlib.semservices.bo.Concept;

public class SKOSUtils {
	protected final static Log log = LogFactory.getLog(SKOSUtils.class);

	public static String getConceptXML(String conceptUriString) {
		URL conceptURL = null;
		try {
			conceptURL = new URL(conceptUriString);
		} catch (Exception e) {
			log.error("Exception occurred in instantiating URL for "
					+ conceptUriString, e);
			// If the url is having trouble, just return null for the concept
			return null;
		}
		log.debug("loading concept uri " + conceptUriString);

		String results = null;
		try {

			StringWriter sw = new StringWriter();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conceptURL.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sw.write(inputLine);
			}
			in.close();

			results = sw.toString();
			log.debug(results);
		} catch (Exception ex) {
			log.error("Error occurred in getting concept from the URL "
					+ conceptUriString, ex);
			return null;
		}
		return results;
	}

	// Downloading the XML from the URI itself
	//No language tag support here but can be specified if need be at this level as well
	public static Concept createConceptUsingXMLFromURI(Concept concept,
			String conceptUriString, String relationshipScheme) {
		String results = getConceptXML(conceptUriString);
		if (StringUtils.isEmpty(results)) {
			return null;
		}
		return createConceptUsingXML(concept, results, relationshipScheme, null);
	}



	// Create concept given the actual XML (results_
	// Lang tag value, if populated, will return pref label and alt label which
	// match that language tag value
	public static Concept createConceptUsingXML(Concept concept,
			String results, String relationshipScheme, String langTagValue) {

		HashMap<String, String> relationshipHash = getRelationshipHash(relationshipScheme);
		try {
			Document doc = XMLUtils.parse(results);
			// Preferred label
			List<String> labelLiterals = new ArrayList<String>();
			if (StringUtils.isNotEmpty(langTagValue)) {
				labelLiterals = getValuesFromXMLNodes(doc,
						getPrefLabelTag(relationshipHash), "xml:lang", langTagValue);
			} else {
				labelLiterals = getValuesFromXMLNodes(doc,
						getPrefLabelTag(relationshipHash), null);
			}

			if (labelLiterals.size() > 0) {
				concept.setLabel(labelLiterals.get(0));
			} else {
				// This is an error because there should be at least one label
				// returned
				log.debug("The number of preferred labels is not greater than zero");
			}

			// Alternate label

			List<String> altLabelList = new ArrayList<String>();
			//if language tag is specified, get node values matching that language tag
			if (StringUtils.isNotEmpty(langTagValue)) {
				altLabelList = getValuesFromXMLNodes(doc,
						//TODO: Check if xml:lang or a different version should be used
						getAltLabelTag(relationshipHash), "xml:lang", langTagValue);
			} else {
				altLabelList = getValuesFromXMLNodes(doc,
						getAltLabelTag(relationshipHash), null);
			}
			concept.setAltLabelList(altLabelList);

			
			//Broder, narrower, exact match, and close match properties
			List<String> broaderURIList = getValuesFromXMLNodes(doc,
					getBroaderTag(relationshipHash), "rdf:resource");
			concept.setBroaderURIList(broaderURIList);
			List<String> narrowerURIList = getValuesFromXMLNodes(doc,
					getNarrowerTag(relationshipHash), "rdf:resource");
			concept.setNarrowerURIList(narrowerURIList);

			List<String> exactMatchURIList = getValuesFromXMLNodes(doc,
					getExactMatchTag(relationshipHash), "rdf:resource");
			concept.setExactMatchURIList(exactMatchURIList);
			List<String> closeMatchURIList = getValuesFromXMLNodes(doc,
					getCloseMatchTag(relationshipHash), "rdf:resource");
			concept.setCloseMatchURIList(closeMatchURIList);

		} catch (IOException e) {
			log.error("error occurred in parsing " + results, e);
		} catch (SAXException e) {
			log.error("error occurred in parsing " + results, e);
		} catch (ParserConfigurationException e) {
			log.error("error occurred in parsing " + results, e);
		}

		return concept;

	}

	// Default to English for search results but this should be made
	// configurable
	public static List<String> getValuesFromXMLNodes(Document doc,
			String tagName, String attributeName) {
		return getValuesFromXMLNodes(doc, tagName, attributeName, null);
	}

	public static List<String> getValuesFromXMLNodes(Document doc,
			String tagName, String attributeName, String matchAttributeValue) {
		NodeList nodes = doc.getElementsByTagName(tagName);

		return getValuesFromXML(nodes, attributeName, matchAttributeValue);
	}

	// Returns list of values based on nodes and whether or not a specific
	// attribute name should be used or just the text content
	// Attribute name returns the value for the attribute on the node
	// MatchAttributeValue: returns NODE values that MATCH this value for
	// attributeName
	public static List<String> getValuesFromXML(NodeList nodes,
			String attributeName, String matchAttributeValue) {
		int len = nodes.getLength();
		int i;

		List<String> values = new ArrayList<String>();
		for (i = 0; i < len; i++) {
			Node node = nodes.item(i);
			// If no attribute name specified, then get the node content
			if (StringUtils.isEmpty(attributeName)) {
				values.add(node.getTextContent());
			} else {
				// Attribute name is specified
				// Get the value for the attribute itself
				String attributeValue = getAttributeValue(attributeName, node);
				// If no matching value for attribute specified, return the
				// value of the attribute itself
				// e.g. value of "lang" attribute which is "en"
				if (StringUtils.isEmpty(matchAttributeValue)) {
					values.add(attributeValue);
				} else {
					// match attribute and match value are both specified, so
					// return NODE value that matches attribute value for given
					// attribute name
					// e.g. preferred label node value where lang = "en"
					if (attributeValue.equals(matchAttributeValue)) {
						values.add(node.getTextContent());
					}
				}
			}
		}
		return values;
	}

	public static String getAttributeValue(String attributeName, Node node) {
		NamedNodeMap attrs = node.getAttributes();
		Attr a = (Attr) attrs.getNamedItem(attributeName);
		if (a != null) {
			return a.getTextContent();
		}
		return null;
	}

	// The Hash will depend on the particular RDF results
	// TODO: Refactor this in a better method
	public static HashMap<String, String> getRelationshipHash(String tagset) {
		HashMap<String, String> relationshipHash = new HashMap<String, String>();
		String[] tagsArray = { "prefLabel", "altLabel", "broader", "narrower",
				"exactMatch", "closeMatch" };
		List<String> tags = Arrays.asList(tagsArray);

		switch (tagset) {
		case "xmlns":
			for (String tag : tags) {
				relationshipHash.put(tag, tag);
			}
			break;
		case "abbreviated":
			for (String tag : tags) {
				relationshipHash.put(tag, "skos:" + tag);
			}
			break;
		default:
			break;
		}
		return relationshipHash;

	}

	public static String getPrefLabelTag(
			HashMap<String, String> relationshipHash) {
		return relationshipHash.get("prefLabel");
	}

	public static String getAltLabelTag(HashMap<String, String> relationshipHash) {
		return relationshipHash.get("altLabel");
	}

	public static String getCloseMatchTag(
			HashMap<String, String> relationshipHash) {
		return relationshipHash.get("closeMatch");
	}

	public static String getExactMatchTag(
			HashMap<String, String> relationshipHash) {
		return relationshipHash.get("exactMatch");
	}

	public static String getBroaderTag(HashMap<String, String> relationshipHash) {
		return relationshipHash.get("broader");
	}

	public static String getNarrowerTag(HashMap<String, String> relationshipHash) {
		return relationshipHash.get("narrower");
	}

}