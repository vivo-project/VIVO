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
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataFactory;
import org.semanticweb.skos.SKOSDataProperty;
import org.semanticweb.skos.SKOSDataRelationAssertion;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSEntity;
import org.semanticweb.skos.SKOSLiteral;
import org.semanticweb.skos.SKOSObjectRelationAssertion;
import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skos.properties.*;
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
import edu.cornell.mannlib.semservices.exceptions.ConceptsNotFoundException;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.semservices.util.XMLUtils;

public class LCSHService implements ExternalConceptService {

	protected final Log log = LogFactory.getLog(getClass());
	private final String skosSuffix = ".skos.rdf";
	private final String hostUri = "http://id.loc.gov";
	private java.lang.String LCSHWS_address = hostUri + "/authorities/subjects";
	private final String schemeUri = hostUri + "/authorities/subjects";
	private final String baseUri = hostUri + "/search/";
	private final String ontologyName = "LCSH";
	private final String format = "SKOS";
	private final String lang = "en";
	private final String codeName = "hasCodeAgrovoc";
	private final String searchMode = "Exact Match";
	protected final String dbpedia_endpoint = " http://dbpedia.org/sparql";
	//Property uris used for SKOS
	protected final String SKOSNotePropertyURI = "http://www.w3.org/2004/02/skos/core#note";
	protected final String SKOSPrefLabelURI = "http://www.w3.org/2004/02/skos/core#prefLabel";
	protected final String SKOSAltLabelURI = "http://www.w3.org/2008/05/skos-xl#altLabel";
	protected final String SKOSBroaderURI = "http://www.w3.org/2004/02/skos/core#broader";
	protected final String SKOSNarrowerURI = "http://www.w3.org/2004/02/skos/core#narrower";
	protected final String SKOSExactMatchURI = "http://www.w3.org/2004/02/skos/core#exactMatch";
	protected final String SKOSCloseMatchURI = "http://www.w3.org/2004/02/skos/core#closeMatch";

	@Override
	public List<Concept> getConcepts(String term) throws Exception {
		List<Concept> conceptList = new ArrayList<Concept>();
		String results = null;
		String dataUrl = baseUri + "?q=" + URLEncoder.encode(term, "UTF-8")
				+ "&q=cs%3Ahttp%3A%2F%2Fid.loc.gov%2Fauthorities%2Fsubjects"
				+ "&format=XML";
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

	// Results are in json format (atom) - atom entries need to be extracted
	// retrieve the URIs and get the SKOS version of the entry, getting broader
	// and narrower terms as applicable as well as any description (skos:note)
	// that might exist
	private List<Concept> processOutput(String results) throws Exception {
		List<Concept> conceptList = new ArrayList<Concept>();
		SKOSManager manager = new SKOSManager();
		// Get uris from the results
		// Properties we will be querying for
		SKOSDataFactory sdf = manager.getSKOSDataFactory();
		

		List<String> uris = getConceptURIFromXML(results);
		String bestMatch = "true";
		int i = 0;
		for (String uri : uris) {
			if(i > 0) {
				bestMatch = "false";
			}
			log.debug("-" + uri + "-");
			String conceptUriString = getSKOSURI(uri);
			String baseConceptURI = getConceptURI(uri);
			URI conceptURI = null;
			try {
				conceptURI = new URI(conceptUriString);
			} catch (URISyntaxException e) {
				log.error("URI syntax exception in trying to get concept uri " + conceptUriString, e);
				return conceptList;
			}
			log.debug("loading concept uri " + conceptUriString);
			SKOSDataset dataset = manager.loadDataset(conceptURI);
			Set<SKOSConcept> skosConcepts = dataset.getSKOSConcepts();
			log.debug("Number of skos concepts " + skosConcepts.size());
			
			for (SKOSConcept skosConcept : skosConcepts) {
				//Close matches are also being returned in list of skos concepts and 
				//we are interested in getting the main concept we requested only
				if(skosConcept.getURI().toString().equals(baseConceptURI)) {
					Concept c = this.createConcept(sdf, bestMatch, skosConcept, dataset);
					if(c != null) {
						conceptList.add(c);
					}
				}
			}
			i++;
			
		}
		return conceptList;
	}

	
	
	//Will use skos if does not encounter error from skos api, otherwise will use regular XML parsing techniques
	public Concept createConcept(SKOSDataFactory skosDataFactory, String bestMatch, SKOSConcept skosConcept, SKOSDataset dataset) {

		Concept concept = new Concept();
		String skosConceptURI = skosConcept.getURI().toString();
		log.debug("SKOSConceptURI is " + skosConceptURI);
		// get skos version of uri

		concept.setUri(skosConceptURI);
		concept.setConceptId(stripConceptId(skosConceptURI));
		concept.setBestMatch(bestMatch);
		concept.setDefinedBy(schemeUri);
		concept.setSchemeURI(schemeUri);
		concept.setType("");
		
		//Get the skos annotations first to see if there is an error triggered, if so try and see if we can instead utilize XML
		//For some of the SKOS concepts, a null pointer exception occurs while XML processing still works
		//I do not yet know the reasons, hjk54
		try {
			Set<SKOSAnnotation> skosAnnots = skosConcept
					.getSKOSAnnotations(dataset);
		} catch(NullPointerException ex) {
			concept = createConceptUsingXML(concept, bestMatch, skosConcept);
			return concept;
		} catch(Exception ex) {
			log.debug("Error occurred for annotation retrieval for skos concept " + skosConceptURI, ex);
			return null;
		}
		
		concept = this.createConceptUsingSKOS(skosDataFactory, concept, skosConcept, dataset);
		return concept;
	}
	
	private Concept createConceptUsingSKOS(SKOSDataFactory skosDataFactory, Concept concept, SKOSConcept skosConcept, SKOSDataset dataset) {
		
		SKOSPrefLabelProperty prefLabelProperty = skosDataFactory.getSKOSPrefLabelProperty();
		SKOSAltLabelProperty altLabelProperty = skosDataFactory.getSKOSAltLabelProperty();

		try {
			List<String> labelLiterals = this.getSKOSLiteralValues(skosConcept
					.getSKOSRelatedConstantByProperty(dataset,
							prefLabelProperty)); 
			if(labelLiterals.size() > 0) {
				concept.setLabel(labelLiterals.get(0));
			} else {
				//This is an error because there should be at least one label returned
				log.debug("The number of preferred labels is not greater than zero");
			}
							
			// get altLabels
			List<String> altLabelList = this.getSKOSLiteralValues(skosConcept
					.getSKOSRelatedConstantByProperty(dataset, altLabelProperty));
			concept.setAltLabelList(altLabelList);

			// See if we can get a description as well
			List<String> notes = this.getSKOSAnnotationValues(skosConcept
				.getSKOSAnnotationsByURI(dataset, new URI(this.SKOSNotePropertyURI)));
			
			concept.setDefinition(StringUtils.join(notes, ","));
			
			// get the broader property URI
			List<String> broaderURIList = this.getSKOSAnnotationValues(skosConcept
					.getSKOSAnnotationsByURI(dataset, new URI(this.SKOSBroaderURI)));
			concept.setBroaderURIList(broaderURIList);

			// get the narrower property URI
			List<String> narrowerURIList = this.getSKOSAnnotationValues(skosConcept
					.getSKOSAnnotationsByURI(dataset, new URI(this.SKOSNarrowerURI)));
			concept.setNarrowerURIList(narrowerURIList);

			// exact match
			List<String> exactMatchURIList = this.getSKOSAnnotationValues(skosConcept
					.getSKOSAnnotationsByURI(dataset,
							new URI(this.SKOSExactMatchURI)));
			concept.setExactMatchURIList(exactMatchURIList);

			// close match
			List<String> closeMatchURIList = this.getSKOSAnnotationValues(skosConcept
					.getSKOSAnnotationsByURI(dataset,
							new URI(this.SKOSCloseMatchURI)));
			concept.setCloseMatchURIList(closeMatchURIList);
			log.debug("add concept to list");
		} catch (Exception ex) {
			log.debug("Exception occurred for -" + skosConcept.getURI()
					+ "- " + ex.getMessage(), ex);
			return null;
		}
		return concept;
	}
	
	
	private List<String> getSKOSLiteralValues(Set<SKOSLiteral> skosLiterals) {
		String lang = "";
		List<String> literalValues = new ArrayList<String>();
		for (SKOSLiteral literal : skosLiterals) {
			if(literal != null) {
				if (!literal.isTyped()) {
					// if it has language
					SKOSUntypedLiteral untypedLiteral = literal
							.getAsSKOSUntypedLiteral();
					if (untypedLiteral.hasLang()) {
						lang = untypedLiteral.getLang();
					} else {
						lang = "";
					}
				}
				// log.debug("literal: "+ literal.getLiteral());
				if (lang.equals("en")) {
					log.debug("literal value: " + literal.getLiteral());
					literalValues.add(literal.getLiteral());
				}
			} else {
				log.debug("Literal returned was null so was ignored");
			}
		}
		return literalValues;
	}
	
	//For a given set of annotations (for example, for a specific property)
	private List<String> getSKOSAnnotationValues(Set<SKOSAnnotation> skosAnnotations) {
		List<String> valuesList = new ArrayList<String>();
		for (SKOSAnnotation annotation : skosAnnotations) {
			String value = this.getSKOSAnnotationStringValue(annotation);
			valuesList.add(value);
		}
		return valuesList;
	}
	
	//Get string value for annotation
	private String getSKOSAnnotationStringValue(SKOSAnnotation annotation) {
		String value = new String();
		if (annotation.isAnnotationByConstant()) {
			SKOSLiteral literal = annotation
					.getAnnotationValueAsConstant();
			value = literal.getLiteral();
			log.debug("broder uri: " + value);
		} else {
			// annotation is some resource
			SKOSEntity entity = annotation.getAnnotationValue();
			value = entity.getURI().toString();
		}
		return value;
	}

	//this method relies on the XML of the single SKOS rdf concept in case the SKOS api throws a null pointer exception
	private Concept createConceptUsingXML(Concept concept, String bestMatch,
			SKOSConcept skosConcept) {
		String conceptUriString = skosConcept.getURI().toString() + this.skosSuffix;;
		
		URL conceptURL = null;
		try {
			conceptURL = new URL(conceptUriString);
		} catch (Exception e) {
			log.error("Exception occurred in instantiating URL for " + conceptUriString, e);
			//If the url is having trouble, just return null for the concept
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
			log.error("Error occurred in getting concept from the URL " + conceptUriString, ex);
			return null;
		}

		
		try {
			Document doc = XMLUtils.parse(results);
			List<String> labelLiterals = this.getValuesFromXMLNodes(doc, "skos:prefLabel", null);
			if(labelLiterals.size() > 0) {
				concept.setLabel(labelLiterals.get(0));
			} else {
				//This is an error because there should be at least one label returned
				log.debug("The number of preferred labels is not greater than zero");
			}
			List<String> altLabelList = this.getValuesFromXMLNodes(doc, "skos:altLabel", null);
			concept.setAltLabelList(altLabelList);
			
			List<String> broaderURIList = this.getValuesFromXMLNodes(doc, "skos:broader", "rdf:resource");
			concept.setBroaderURIList(broaderURIList);
			List<String> narrowerURIList = this.getValuesFromXMLNodes(doc, "skos:narrower", "rdf:resource");
			concept.setNarrowerURIList(narrowerURIList);
			
			List<String> exactMatchURIList = this.getValuesFromXMLNodes(doc, "skos:exactMatch", "rdf:resource");
			concept.setExactMatchURIList(exactMatchURIList);
			List<String> closeMatchURIList = this.getValuesFromXMLNodes(doc, "skos:closeMatch", "rdf:resource");
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

	private String getSKOSURI(String uri) {
		// Strip .xml at the end and replace with .skos.rdf
		String skosURI = uri;
		if (uri.endsWith(".xml")) {
			skosURI = uri.substring(0, uri.length() - 4);
			skosURI += skosSuffix;
		}
		return hostUri + skosURI;
	}
	
	//Given the URI from the xml, get just the base URI
	private String getConceptURI(String uri) {
		String skosURI = uri;
		if (uri.endsWith(".xml")) {
			skosURI = uri.substring(0, uri.length() - 4);
		}
		return hostUri + skosURI;
	}

	public List<String> getConceptURISFromJSON(String results) {
		List<String> uris = new ArrayList<String>();
		try {
			JSONObject json = (JSONObject) JSONSerializer.toJSON(results);
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
		String conceptUri = new String();
		try {
			Document doc = XMLUtils.parse(rdf);
			NodeList nodes = doc.getElementsByTagName("search:result");
			int len = nodes.getLength();
			int i;
			for (i = 0; i < len; i++) {
				Node node = nodes.item(i);
				NamedNodeMap attrs = node.getAttributes();
				Attr idAttr = (Attr) attrs.getNamedItem("uri");
				conceptUri = idAttr.getTextContent();
				log.debug("concept uri is " + conceptUri);
				uris.add(conceptUri);
			}

		} catch (IOException e) {
			log.error("error occurred in parsing " +rdf, e);
		} catch (SAXException e) {
			log.error("error occurred in parsing " +rdf, e);
		} catch (ParserConfigurationException e) {
			log.error("error occurred in parsing " +rdf, e);

		}
		return uris;

	}

	public List<Concept> processResults(String term) throws Exception {
		return getConcepts(term);
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
	
	
	
	
	public List<String> getValuesFromXMLNodes(Document doc, String tagName, String attributeName) {
		NodeList nodes = doc.getElementsByTagName(tagName);
		
		return getValuesFromXML(nodes, attributeName);
	}
	
	//Returns list of values based on nodes and whether or not a specific attribute name should be used or just the text content
	public List<String> getValuesFromXML(NodeList nodes, String attributeName) {
		int len = nodes.getLength();
		int i;
		List<String> values = new ArrayList<String>();
		for (i = 0; i < len; i++) {
			Node node = nodes.item(i);
			if(attributeName != null && !attributeName.isEmpty()) {
				NamedNodeMap attrs = node.getAttributes();
				Attr a = (Attr)attrs.getNamedItem(attributeName);
				if(a != null) {
					values.add(a.getTextContent());
				}
			} else {
				values.add(node.getTextContent());
			}
		}
		return values;
	}

	

}
