/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.searchindex.extensions;

import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.ALLTEXT;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.ALLTEXTUNSTEMMED;
import static edu.cornell.mannlib.vitro.webapp.utils.sparqlrunner.SparqlQueryRunner.createSelectQueryContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchInputDocument;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.searchindex.documentBuilding.DocumentModifier;
import edu.cornell.mannlib.vitro.webapp.searchindex.indexing.IndexingUriFinder;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ContextModelsUser;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.Property;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.Validation;

/**
 * If an individual has context nodes then the search document for that
 * individual should include the labels of the partners across those nodes. The
 * labels will be added to the ALLTEXT and ALLTEXTUNSTEMMED fields.
 *
 * We must specify what property leads to a context node (incoming), and what
 * property leads from a context node (outgoing). We may add restrictions to say
 * that this only applies to individuals of certain types. We may also restrict
 * the type of the applicable context nodes.
 *
 * An instance of this class acts as both a DocumentModifier and an
 * IndexingUriFinder:
 *
 * As a DocumentModifier, it looks across approved context nodes to fetch the
 * labels of the partners.
 *
 * As an IndexingUriFinder, it recognizes that this relationship can be changed
 * by a change to a "label" statement, or to a "relates" property, and finds all
 * partners as candidates for reindexing.
 *
 * <pre>
 * Configuration:
 *     rdfs:label -- Optional. Appears in the timings and debug statements.
 *     :hasIncomingProperty -- Required. Property leading to the context node.
 *     :hasOutgoingProperty -- Required. Property leading from the context node.
 *     :hasTypeRestriction -- Optional. Match any. If none, then no restriction.
 *     :appliesToContextNodeType -- Optional. Match any. If none, then no restriction.
 * </pre>
 */
public class LabelsAcrossContextNodes implements IndexingUriFinder,
		DocumentModifier, ContextModelsUser {
	private static final Log log = LogFactory
			.getLog(LabelsAcrossContextNodes.class);

	private RDFService rdfService;

	/**
	 * A name to be used in logging, to identify this instance. If not provided,
	 * then a descriptive label will be created.
	 */
	private String label;

	/**
	 * The URI of the property that leads into the context node. Required.
	 */
	private String incomingPropertyUri;

	/**
	 * The URI of the property that leads from the context node. Required.
	 */
	private String outgoingPropertyUri;

	/**
	 * URIs of the types of individuals to whom this instance applies.
	 *
	 * If this is not empty and an individual does not have any of these types,
	 * then skip that individual.
	 */
	private Set<String> typeRestrictions = new HashSet<>();

	/**
	 * URIs of the types of acceptable context nodes.
	 *
	 * If this is not empty and a context node does not have any of these types,
	 * then skip that context node's label.
	 */
	private Set<String> contextNodeClasses = new HashSet<>();

	@Override
	public void setContextModels(ContextModelAccess models) {
		this.rdfService = models.getRDFService();
	}

	@Property(uri = "http://www.w3.org/2000/01/rdf-schema#label", maxOccurs = 1)
	public void setLabel(String l) {
		label = l;
	}

	@Property(uri = "http://vitro.mannlib.cornell.edu/ns/vitro/ApplicationSetup#hasIncomingProperty", minOccurs = 1, maxOccurs = 1)
	public void setIncomingProperty(String incomingUri) {
		incomingPropertyUri = incomingUri;
	}

	@Property(uri = "http://vitro.mannlib.cornell.edu/ns/vitro/ApplicationSetup#hasOutgoingProperty", minOccurs = 1, maxOccurs = 1)
	public void setOutgoingProperty(String outgoingUri) {
		outgoingPropertyUri = outgoingUri;
	}

	@Property(uri = "http://vitro.mannlib.cornell.edu/ns/vitro/ApplicationSetup#hasTypeRestriction")
	public void addTypeRestriction(String typeUri) {
		typeRestrictions.add(typeUri);
	}

	@Property(uri = "http://vitro.mannlib.cornell.edu/ns/vitro/ApplicationSetup#appliesToContextNodeType")
	public void addContextNodeClass(String cnc) {
		contextNodeClasses.add(cnc);
	}

	@Validation
	public void validate() {
		if (label == null) {
			label = String.format("%s[types=%s, contextNodeTypes=%s]", this
					.getClass().getSimpleName(),
					formatRestrictions(typeRestrictions),
					formatRestrictions(contextNodeClasses));
		}
	}

	private String formatRestrictions(Set<String> uris) {
		if (uris.isEmpty()) {
			return "ALL";
		} else {
			return localNames(uris).toString();
		}
	}

	private Set<String> localNames(Set<String> uris) {
		Set<String> names = new HashSet<>();
		for (String uri : uris) {
			try {
				names.add(ResourceFactory.createResource(uri).getLocalName());
			} catch (Exception e) {
				log.warn("Failed to parse URI: " + uri, e);
				names.add(uri);
			}
		}
		return names;
	}

	@Override
	public String toString() {
		return (label == null) ? super.toString() : label;
	}

	// ----------------------------------------------------------------------
	// DocumentModifier
	// ----------------------------------------------------------------------

	private static final String LABELS_WITHOUT_RESTRICTION = ""
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?label  \n" //
			+ "WHERE { \n" //
			+ "   ?uri ?incoming ?contextNode . \n" //
			+ "   ?contextNode ?outgoing ?partner . \n" //
			+ "   ?partner rdfs:label ?label . \n" //
			+ "   FILTER( ?uri != ?partner  ) \n" //
			+ "} \n";
	private static final String LABELS_FOR_SPECIFIC_CONTEXT_NODE_TYPE = ""
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?label  \n" //
			+ "WHERE { \n" //
			+ "   ?uri ?incoming ?contextNode . \n" //
			+ "   ?contextNode a ?nodeType . \n" //
			+ "   ?contextNode ?outgoing ?partner . \n" //
			+ "   ?partner rdfs:label ?label . \n" //
			+ "   FILTER( ?uri != ?partner  ) \n" //
			+ "} \n";

	/**
	 * If this individual is acceptable, locate the labels of any context
	 * partners across acceptable context nodes. Add those labels to the text
	 * fields of the search document.
	 */
	@Override
	public void modifyDocument(Individual ind, SearchInputDocument doc) {
		if (passesTypeRestriction(ind)) {
			if (contextNodeClasses.isEmpty()) {
				addLabelsFromAllContextNodeClasses(ind, doc);
			} else {
				for (String contextNodeClass : contextNodeClasses) {
					addLabelsFromContextNodeClass(ind, doc, contextNodeClass);
				}
			}
		}
	}

	private boolean passesTypeRestriction(Individual ind) {
		if (typeRestrictions.isEmpty()) {
			return true;
		} else {
			for (VClass type : ind.getVClasses()) {
				if (typeRestrictions.contains(type.getURI())) {
					return true;
				}
			}
		}
		return false;
	}

	private void addLabelsFromAllContextNodeClasses(Individual ind,
			SearchInputDocument doc) {
		addValuesToTextFields(
				doc,
				createSelectQueryContext(rdfService, LABELS_WITHOUT_RESTRICTION)
						.bindVariableToUri("uri", ind.getURI())
						.bindVariableToUri("incoming", incomingPropertyUri)
						.bindVariableToUri("outgoing", outgoingPropertyUri)
						.execute().toStringFields("label").flatten());
	}

	private void addLabelsFromContextNodeClass(Individual ind,
			SearchInputDocument doc, String contextNodeClass) {
		addValuesToTextFields(
				doc,
				createSelectQueryContext(rdfService,
						LABELS_FOR_SPECIFIC_CONTEXT_NODE_TYPE)
						.bindVariableToUri("uri", ind.getURI())
						.bindVariableToUri("nodeType", contextNodeClass)
						.bindVariableToUri("incoming", incomingPropertyUri)
						.bindVariableToUri("outgoing", outgoingPropertyUri)
						.execute().toStringFields("label").flatten());
	}

	private void addValuesToTextFields(SearchInputDocument doc,
			List<String> values) {
		for (String value : values) {
			doc.addField(ALLTEXT, value);
			doc.addField(ALLTEXTUNSTEMMED, value);
		}
	}

	@Override
	public void shutdown() {
		// Nothing to shut down.
	}

	// ----------------------------------------------------------------------
	// IndexingUriFinder
	// ----------------------------------------------------------------------

	private static final String LOCATE_PARTNERS_WITHOUT_RESTRICTION = ""
			+ "SELECT ?partner  \n" //
			+ "WHERE { \n" //
			+ "   ?partner ?incoming ?contextNode . \n" //
			+ "   ?contextNode ?outgoing ?uri . \n" //
			+ "   FILTER( ?uri != ?partner  ) \n" //
			+ "} \n";
	private static final String LOCATE_PARTNERS_ON_CONTEXT_NODE_TYPE = ""
			+ "SELECT ?partner  \n" //
			+ "WHERE { \n" //
			+ "   ?partner ?incoming ?contextNode . \n" //
			+ "   ?contextNode ?outgoing ?uri . \n" //
			+ "   ?contextNode a ?nodeType . \n" //
			+ "   FILTER( ?uri != ?partner  ) \n" //
			+ "} \n";
	private static final String LOCATE_OTHER_PARTNERS_ON_THIS_NODE = ""
			+ "SELECT ?partner  \n" //
			+ "WHERE { \n" //
			+ "   ?contextNode ?outgoing ?partner . \n" //
			+ "   FILTER( ?uri != ?partner  ) \n" //
			+ "} \n";
	private static final String GET_TYPES = "" //
			+ "SELECT ?type  \n" //
			+ "WHERE { \n" //
			+ "   ?uri a ?type . \n" //
			+ "} \n";

	@Override
	public void startIndexing() {
		// Nothing to do.
	}

	/**
	 * If this is a "label" statement, check to see if the subject has any
	 * acceptable partners across acceptable context nodes.
	 *
	 * If this is a statement that involves the specified incoming property on
	 * an acceptable context node, check to see if there are any acceptable
	 * partners on this node.
	 */
	@Override
	public List<String> findAdditionalURIsToIndex(Statement stmt) {
		if (isLabelStatement(stmt)) {
			return filterByType(locatePartners(stmt));
		} else if (isIncomingStatementOnAcceptableContextNode(stmt)) {
			return filterByType(locateOtherPartners(stmt));
		}
		return Collections.emptyList();
	}

	private boolean isLabelStatement(Statement stmt) {
		return RDFS.label.getURI().equals(stmt.getPredicate().getURI());
	}

	private Set<String> locatePartners(Statement stmt) {
		String uri = stmt.getSubject().getURI();
		if (contextNodeClasses.isEmpty()) {
			return locatePartnersWithoutRestriction(uri);
		} else {
			Set<String> uris = new HashSet<>();
			for (String contextNodeClass : contextNodeClasses) {
				uris.addAll(locatePartnersAcrossContextNodeClass(uri,
						contextNodeClass));
			}
			return uris;
		}
	}

	private Set<String> locatePartnersWithoutRestriction(String uri) {
		return createSelectQueryContext(rdfService,
				LOCATE_PARTNERS_WITHOUT_RESTRICTION)
				.bindVariableToUri("uri", uri)
				.bindVariableToUri("incoming", incomingPropertyUri)
				.bindVariableToUri("outgoing", outgoingPropertyUri).execute()
				.toStringFields("partner").flattenToSet();
	}

	private Collection<? extends String> locatePartnersAcrossContextNodeClass(
			String uri, String contextNodeClass) {
		return createSelectQueryContext(rdfService,
				LOCATE_PARTNERS_ON_CONTEXT_NODE_TYPE)
				.bindVariableToUri("uri", uri)
				.bindVariableToUri("nodeType", contextNodeClass)
				.bindVariableToUri("incoming", incomingPropertyUri)
				.bindVariableToUri("outgoing", outgoingPropertyUri).execute()
				.toStringFields("partner").flattenToSet();
	}

	private boolean isIncomingStatementOnAcceptableContextNode(Statement stmt) {
		String subjectUri = stmt.getSubject().getURI();
		String predicateUri = stmt.getPredicate().getURI();

		return incomingPropertyUri.equals(predicateUri)
				&& (contextNodeClasses.isEmpty() || isAnyMatch(
				contextNodeClasses, getTypes(subjectUri)));
	}

	private boolean isAnyMatch(Set<String> set1, Set<String> set2) {
		Set<String> matches = new HashSet<>(set1);
		matches.retainAll(set2);
		return !matches.isEmpty();
	}

	private Set<String> getTypes(String uri) {
		return createSelectQueryContext(rdfService, GET_TYPES)
				.bindVariableToUri("uri", uri).execute().toStringFields("type")
				.flattenToSet();
	}

	private Set<String> locateOtherPartners(Statement stmt) {
		if (!stmt.getSubject().isURIResource()) {
			return Collections.emptySet();
		}

		String nodeUri = stmt.getSubject().getURI();
		String objectUri = (stmt.getObject().isURIResource()) ? stmt
				.getObject().asResource().getURI() : "NO_MATCH";

		return createSelectQueryContext(rdfService,
				LOCATE_OTHER_PARTNERS_ON_THIS_NODE)
				.bindVariableToUri("contextNode", nodeUri)
				.bindVariableToUri("uri", objectUri)
				.bindVariableToUri("outgoing", outgoingPropertyUri).execute()
				.toStringFields("partner").flattenToSet();
	}

	private List<String> filterByType(Collection<String> uris) {
		if (typeRestrictions.isEmpty()) {
			return new ArrayList<>(uris);
		} else {
			List<String> filtered = new ArrayList<>();
			for (String uri : uris) {
				if (isAnyMatch(typeRestrictions, getTypes(uri))) {
					filtered.add(uri);
				}
			}
			return filtered;
		}
	}

	@Override
	public void endIndexing() {
		// Nothing to do.
	}

}
