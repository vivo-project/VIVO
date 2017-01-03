/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* We are no longer using the SKOS API since Vitro has moved to V 4.0 of OWL API which does not appear to be compatible.
 This file will contain methods used for reading SKOS as XML and parsing it for the properties
 we want to extract*/

package edu.cornell.mannlib.semservices.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

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
	// No language tag support here but can be specified if need be at this
	// level as well
	public static Concept createConceptUsingXMLFromURL(Concept concept,
			String conceptURLString, String langTagValue, boolean addNotes) {
		String results = getConceptXML(conceptURLString);
		if (StringUtils.isEmpty(results)) {
			return null;
		}

		// return createConceptUsingXML(concept, results, langTagValue);
		return createConceptUsingXMLModel(concept, results, langTagValue,
				addNotes);

	}

	// Because of the fact the xml returns matches by tag name, and the XML may
	// look like <skos:narrower><skos:Concept ..><skos:broader
	// rdf:resource:"conceptURI">
	// where conceptURI is the concept that is the subject of skos:narrower, we
	// need to ensure we are not returning the same uri as that of the main
	// concept
	public static List<String> removeConceptURIFromList(List<String> uris,
			String conceptURI) {
		// remove will return a boolean if the value exists in the list and is
		// removed
		// if/when it returns false, the URI is not in the list
		while (uris.remove(conceptURI)) {
		}
		;
		return uris;
	}

	/**
	 * The above code, although functional, does not take advantage of the fact
	 * that we can actually read and query the RDF in precisely the manner we
	 * wish.
	 */

	public static Concept createConceptUsingXMLModel(Concept concept,
			String results, String langTagValue, boolean addNotes) {

		try {
			String conceptURI = concept.getUri();

			// Load Model from RDF
			StringReader reader = new StringReader(results);
			Model model = ModelFactory.createDefaultModel();
			model.read(reader, null, "RDF/XML");

			// Execute the following query to get the information we want for
			// this resource

			// Preferred label
			List<String> labelLiterals = getPrefLabelsFromModel(conceptURI,
					model, langTagValue);
			if (labelLiterals.size() > 0) {
				concept.setLabel(labelLiterals.get(0));
			} else {
				// This is an error because there should be at least one label
				// returned
				log.debug("The number of preferred labels is not greater than zero");
			}

			// Alternate label

			List<String> altLabelList = getAltLabelsFromModel(conceptURI,
					model, langTagValue);
			concept.setAltLabelList(altLabelList);

			// Broder, narrower, exact match, and close match properties

			List<String> broaderURIList = getBroaderURIsFromModel(conceptURI,
					model);
			// broaderURIList = removeConceptURIFromList(broaderURIList,
			// conceptURI);
			concept.setBroaderURIList(broaderURIList);
			List<String> narrowerURIList = getNarrowerURIsFromModel(conceptURI,
					model);
			// narrowerURIList = removeConceptURIFromList(narrowerURIList,
			// conceptURI);
			concept.setNarrowerURIList(narrowerURIList);

			List<String> exactMatchURIList = getExactMatchURIsFromModel(
					conceptURI, model);
			// exactMatchURIList = removeConceptURIFromList(exactMatchURIList,
			// conceptURI);
			concept.setExactMatchURIList(exactMatchURIList);
			List<String> closeMatchURIList = getCloseMatchURIsFromModel(
					conceptURI, model);
			// closeMatchURIList = removeConceptURIFromList(closeMatchURIList,
			// conceptURI);
			concept.setCloseMatchURIList(closeMatchURIList);

			// Notes may exist, in which case they should be employed
			if (addNotes) {
				List<String> notes = getNotesFromModel(conceptURI, model,
						langTagValue);
				if (notes.size() > 0) {
					concept.setDefinition(notes.get(0));
				}
			}

		} catch (Exception e) {
			log.error("error occurred in parsing " + results, e);
		}

		return concept;

	}

	private static List<String> getPrefLabelsFromModel(String conceptURI,
			Model model, String langTagValue) {
		String propertyURI = "http://www.w3.org/2004/02/skos/core#prefLabel";
		return getLabelsFromModel(conceptURI, propertyURI, model, langTagValue);
	}

	private static List<String> getAltLabelsFromModel(String conceptURI,
			Model model, String langTagValue) {
		String propertyURI = "http://www.w3.org/2004/02/skos/core#altLabel";
		return getLabelsFromModel(conceptURI, propertyURI, model, langTagValue);
	}

	private static List<String> getLabelsFromModel(String conceptURI,
			String propertyURI, Model model, String langTagValue) {
		List<String> labels = new ArrayList<String>();
		StmtIterator statements = model.listStatements(
				ResourceFactory.createResource(conceptURI),
				ResourceFactory.createProperty(propertyURI), (RDFNode) null);
		while (statements.hasNext()) {
			Statement statement = statements.nextStatement();
			RDFNode node = statement.getObject();
			if (node != null && node.isLiteral()) {
				String label = node.asLiteral().getString();
				if (StringUtils.isNotEmpty(langTagValue)) {
					String language = node.asLiteral().getLanguage();
					if (language != null && language.equals(langTagValue)) {
						labels.add(label);
					}
				} else {
					labels.add(label);
				}
			}

		}
		return labels;
	}

	private static List<String> getNotesFromModel(String conceptURI,
			Model model, String langTagValue) {
		String propertyURI = "http://www.w3.org/2004/02/skos/core#note";
		return getLabelsFromModel(conceptURI, propertyURI, model, langTagValue);
	}

	private static List<String> getCloseMatchURIsFromModel(String conceptURI,
			Model model) {
		String propertyURI = "http://www.w3.org/2004/02/skos/core#closeMatch";
		return getRelatedURIsFromModel(conceptURI, propertyURI, model);

	}

	private static List<String> getExactMatchURIsFromModel(String conceptURI,
			Model model) {
		String propertyURI = "http://www.w3.org/2004/02/skos/core#exactMatch";
		return getRelatedURIsFromModel(conceptURI, propertyURI, model);
	}

	private static List<String> getNarrowerURIsFromModel(String conceptURI,
			Model model) {
		String propertyURI = "http://www.w3.org/2004/02/skos/core#narrower";
		return getRelatedURIsFromModel(conceptURI, propertyURI, model);
	}

	private static List<String> getBroaderURIsFromModel(String conceptURI,
			Model model) {
		String propertyURI = "http://www.w3.org/2004/02/skos/core#broader";
		return getRelatedURIsFromModel(conceptURI, propertyURI, model);
	}

	private static List<String> getRelatedURIsFromModel(String conceptURI,
			String propertyURI, Model model) {
		List<String> URIs = new ArrayList<String>();
		NodeIterator nodeIterator = model.listObjectsOfProperty(
				ResourceFactory.createResource(conceptURI),
				ResourceFactory.createProperty(propertyURI));

		while (nodeIterator.hasNext()) {
			RDFNode node = nodeIterator.nextNode();
			if (node.isResource() && node.asResource().getURI() != null) {
				String URI = node.asResource().getURI();
				URIs.add(URI);
			}
		}

		return URIs;
	}

}