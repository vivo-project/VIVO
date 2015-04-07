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
			String conceptUriString, String langTagValue) {
		String results = getConceptXML(conceptUriString);
		if (StringUtils.isEmpty(results)) {
			return null;
		}
		return createConceptUsingXML(concept, results,  langTagValue);
	}



	// Create concept given the actual XML (results_
	// Lang tag value, if populated, will return pref label and alt label which
	// match that language tag value
	public static Concept createConceptUsingXML(Concept concept,
			String results, String langTagValue) {

		HashMap<String, String> relationshipHash = getRelationshipHash();
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
			String conceptURI = concept.getUri();
			List<String> broaderURIList = getBroaderOrNarrowerURIs(doc, getBroaderTag(relationshipHash));
			broaderURIList = removeConceptURIFromList(broaderURIList, conceptURI);
			concept.setBroaderURIList(broaderURIList);
			List<String> narrowerURIList = getBroaderOrNarrowerURIs(doc, getNarrowerTag(relationshipHash));
			narrowerURIList = removeConceptURIFromList(narrowerURIList, conceptURI);
			concept.setNarrowerURIList(narrowerURIList);

			List<String> exactMatchURIList = getCloseOrExactMatchURIs(doc, getExactMatchTag(relationshipHash));
			exactMatchURIList = removeConceptURIFromList(exactMatchURIList, conceptURI);
			concept.setExactMatchURIList(exactMatchURIList);
			List<String> closeMatchURIList =  getCloseOrExactMatchURIs(doc, getCloseMatchTag(relationshipHash));
			closeMatchURIList = removeConceptURIFromList(closeMatchURIList, conceptURI);
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
	
	//Because of the fact the xml returns matches by tag name, and the XML may look like <skos:narrower><skos:Concept ..><skos:broader rdf:resource:"conceptURI">
	//where conceptURI is the concept that is the subject of skos:narrower, we need to ensure we are not returning the same uri as that of the main concept
	public static List<String> removeConceptURIFromList(List<String> uris, String conceptURI) {
		//remove will return a boolean if the value exists in the list and is removed
		//if/when it returns false, the URI is not in the list
		while(uris.remove(conceptURI)) {};
		return uris;
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
	//Extending this based on specific SKOSMos search for Agrovoc, sometimes
	//results in format <skos:closeMatch rdf:resource "x">, other times in format <skos:closeMatch> <rfd:Description rdf:about="matchURI">..
	//closeMatch and exactMatch use these patterns
	//broader and narrower may be either rdf:resource pattern above or <skos:broader><skos:Concept rdf:about="...">...etc.
	public static List<String> getValuesFromXML(NodeList nodes,
			String attributeName, String matchAttributeValue) {
		int len = nodes.getLength();
		int i;

		List<String> values = new ArrayList<String>();
		for (i = 0; i < len; i++) {
			Node node = nodes.item(i);
			String nodeValue = getNodeValue(node, attributeName, matchAttributeValue);
			if(StringUtils.isNotEmpty(nodeValue)) {
				values.add(nodeValue);
			}
		}
		return values;
	}

	public static String getNodeValue(Node node, String attributeName, String matchAttributeValue) {
		String value = null;
		if (StringUtils.isEmpty(attributeName)) {
			value = node.getTextContent();
		} else {
			// Attribute name is specified
			// Get the value for the attribute itself
			String attributeValue = getAttributeValue(attributeName, node);
			// If no matching value for attribute specified, return the
			// value of the attribute itself
			// e.g. value of "lang" attribute which is "en"
			if (StringUtils.isEmpty(matchAttributeValue)) {
				value = attributeValue;
			} else {
				// match attribute and match value are both specified, so
				// return NODE value that matches attribute value for given
				// attribute name
				// e.g. preferred label node value where lang = "en"
				if (attributeValue.equals(matchAttributeValue)) {
					value = node.getTextContent();
				}
			}
		}
		return value;
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
	public static HashMap<String, String> getRelationshipHash() {
		HashMap<String, String> relationshipHash = new HashMap<String, String>();
		String[] tagsArray = { "prefLabel", "altLabel", "broader", "narrower",
				"exactMatch", "closeMatch" };
		List<String> tags = Arrays.asList(tagsArray);

		
		
			for (String tag : tags) {
				relationshipHash.put(tag, "skos:" + tag);
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

	
	/**
	 * 
	 * Broader, narrower, close match, and exact match may be nested values - e.g. Agrovoc
	 * Even with Agrovoc, they may be nested sometimes and not be nested other times
	 * The code below handles both situations so the URIs can be returned
	 */
	
	//Broader and narrower values
	//Attribute name will be language tag

	
	public static List<String> getBroaderOrNarrowerURIs(Document doc,
			String tagName) {
		NodeList nodes = doc.getElementsByTagName(tagName);
		List<String> uris = getPossiblyNestedValuesFromXML(nodes, "rdf:resource", "skos:Concept", "rdf:about");
		return uris;
	}	
	
	//Close and exact match
	public static List<String> getCloseOrExactMatchURIs(Document doc,
			String tagName) {
		NodeList nodes = doc.getElementsByTagName(tagName);
		List<String> uris = getPossiblyNestedValuesFromXML(nodes, "rdf:resource", "rdf:Description", "rdf:about");
		return uris;
	}
	
	
	
	public static List<String> getPossiblyNestedValuesFromXML(NodeList nodes, String nodeAttributeName, String childNodeTagName, String childNodeAttributeName ) {
		int len = nodes.getLength();
		int i;

		List<String> values = new ArrayList<String>();
		for (i = 0; i < len; i++) {
			Node node = nodes.item(i);
			//String nodeValue = getNodeValue(node, attributeName, matchAttributeValue);
			String nodeValue = getPossiblyNestedNodeValue(node, nodeAttributeName, childNodeTagName, childNodeAttributeName);
			if(StringUtils.isNotEmpty(nodeValue)) {
				values.add(nodeValue);
			}
		}
		return values;
	}
	//Given node = <tag attrb="attrbvalue">
	//If tag has no attribute that matches attributeName with attributevalue
	//and tag has nested children with a given tag name, i.e. <tag><nestedtag nestedattributename=nestedattributevalue>
	//then retrieve the nested attribute value
	//For example:
	//if the node looks like <skos:closeMatch rdf:resource="x"> then get x
	//but if the node looks like <skos:closeMatch><rdf:description rdf:about="x"> then get x 
	public static String getPossiblyNestedNodeValue(Node node, String nodeAttributeName, String childNodeTagName,
			String childNodeAttributeName) {
		String value = null;
		String attributeValue = getAttributeValue(nodeAttributeName, node);
		if(StringUtils.isNotEmpty(attributeValue)) {
			value = attributeValue;
		} else {
			//Check child nodes and see if any of those have the same name as childNodeTagName
			NodeList childNodes = node.getChildNodes();
			int numberNodes = childNodes.getLength();
			int i;
			for(i = 0; i < numberNodes; i++) {
				Node childNode = childNodes.item(i);
				String nodeName = childNode.getNodeName();
				if(nodeName.equals(childNodeTagName)) {
					value = getAttributeValue(childNodeAttributeName, childNode);
					break; //will only get the first one
				}
				
			}
			
			
		}
		
		
		return value;
	}
	
	//Custom cases for Agrovoc and/or similar patterns if they exist
		//get about URI from <tag> <rdf:Description about="x"> - returns "x"
		public static String getTagNestedAbout(Node n) {
			NodeList childNodes = n.getChildNodes();
			int numberNodes = childNodes.getLength();
			int i;
			for(i = 0; i < numberNodes; i++) {
				Node childNode = childNodes.item(i);
				String nodeName = childNode.getNodeName();
				String aboutValue = getAttributeValue("about", childNode);
			}
			return null;
		}
		
		//get about URI from <tag><skos:Concept about="x">, returns "x"
		public static String getTagNestedSKOSConceptAbout(Node n) {
			NodeList childNodes = n.getChildNodes();
			int numberNodes = childNodes.getLength();
			int i;
			for(i = 0; i < numberNodes; i++) {
				Node childNode = childNodes.item(i);
				String nodeName = childNode.getNodeName();
				String aboutValue = getAttributeValue("about", childNode);

			}
			return null;
		}
	
}