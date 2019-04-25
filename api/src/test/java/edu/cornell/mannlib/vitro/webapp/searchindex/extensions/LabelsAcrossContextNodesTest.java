/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.searchindex.extensions;

import static org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.WhichService.CONTENT;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.ALLTEXT;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.ALLTEXTUNSTEMMED;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import stubs.edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccessStub;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchInputDocument;
import edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchInputField;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.jena.model.RDFServiceModel;

/**
 * TODO
 */
public class LabelsAcrossContextNodesTest extends AbstractTestClass {
	// Types
	private static final String CORE_ACADEMIC_DEGREE = "http://vivoweb.org/ontology/core#AcademicDegree";
	private static final String CORE_ADVISING_RELATIONSHIP = "http://vivoweb.org/ontology/core#AdvisingRelationship";
	private static final String CORE_POSITION = "http://vivoweb.org/ontology/core#Position";
	private static final String FOAF_PERSON = "http://xmlns.com/foaf/0.1/#Person";
	private static final String FOAF_ORGANIZATION = "http://xmlns.com/foaf/0.1/#Organization";

	// Individuals
	// Note: Person3 has no label and so no effect, but doesn't cause a problem.
	private static final String URI_PERSON1 = "http://ns#Person1";
	private static final String NAME_PERSON1 = "Person1";
	private static final String URI_PERSON2 = "http://ns#Person2";
	private static final String NAME_PERSON2 = "Person2";
	private static final String URI_PERSON3 = "http://ns#Person3";
	private static final String NAME_PERSON3 = null;
	private static final String URI_PERSON4 = "http://ns#Person4";
	private static final String NAME_PERSON4 = "Person4";
	private static final String URI_ORGANIZATION1 = "http://ns#Organization1";
	private static final String NAME_ORGANIZATION1 = "Organization1";

	// Context nodes
	private static final String URI_POSITION1 = "http://ns#Position1";
	private static final String NAME_POSITION1 = "Position1";
	private static final String URI_POSITION2 = "http://ns#Position2";
	private static final String NAME_POSITION2 = "Position2";
	private static final String URI_ADVISING1 = "http://ns#Advising1";
	private static final String NAME_ADVISING1 = "Advising1";
	private static final String URI_ADVISING2 = "http://ns#Advising2";
	private static final String NAME_ADVISING2 = "Advising2";

	// Properties
	private static final String CORE_RELATED_BY = "http://vivoweb.org/ontology/core#relatedBy";
	private static final String CORE_RELATES = "http://vivoweb.org/ontology/core#relates";
	private static final String CORE_ASSIGNED_BY = "http://vivoweb.org/ontology/core#assignedBy";

	private Model m;
	private RDFService rdfService;
	private LabelsAcrossContextNodes lacn;
	private MockSearchInputDocument doc;
	private List<String> foundUris;

	/**
	 * Create these relationships (where "r/r" denotes "relatedBy/relates"
	 * combination.
	 *
	 * <pre>
	 * Person1 r/r Position1 r/r Organization1
	 * Person1 r/r AdvisingRelationship1 r/r Person2
	 * Person1 r/r AdvisingRelationship2 r/r Person3(no label!)
	 * Person2 r/r Position2 r/r Organization1
	 * Person4
	 * </pre>
	 */
	@Before
	public void populateModel() {
		m = ModelFactory.createDefaultModel();
		addIndividual(URI_PERSON1, NAME_PERSON1, FOAF_PERSON);
		addIndividual(URI_PERSON2, NAME_PERSON2, FOAF_PERSON);
		addIndividual(URI_PERSON3, NAME_PERSON3, FOAF_PERSON);
		addIndividual(URI_PERSON4, NAME_PERSON4, FOAF_PERSON);
		addIndividual(URI_ORGANIZATION1, NAME_ORGANIZATION1, FOAF_ORGANIZATION);
		addIndividual(URI_POSITION1, NAME_POSITION1, CORE_POSITION);
		addIndividual(URI_POSITION2, NAME_POSITION2, CORE_POSITION);
		addIndividual(URI_ADVISING1, NAME_ADVISING1, CORE_ADVISING_RELATIONSHIP);
		addIndividual(URI_ADVISING2, NAME_ADVISING2, CORE_ADVISING_RELATIONSHIP);

		addRelationship(URI_PERSON1, URI_POSITION1, URI_ORGANIZATION1);
		addRelationship(URI_PERSON1, URI_ADVISING1, URI_PERSON2);
		addRelationship(URI_PERSON1, URI_ADVISING2, URI_PERSON3);
		addRelationship(URI_PERSON2, URI_POSITION2, URI_ORGANIZATION1);

		rdfService = new RDFServiceModel(m);

		ContextModelAccessStub models = new ContextModelAccessStub();
		models.setRDFService(CONTENT, rdfService);

		lacn = new LabelsAcrossContextNodes();
		lacn.setContextModels(models);
		lacn.setIncomingProperty(CORE_RELATED_BY);
		lacn.setOutgoingProperty(CORE_RELATES);
		lacn.validate();
	}

	// ----------------------------------------------------------------------
	// Test DocumentModifier
	// ----------------------------------------------------------------------

	/*
	 * If there is a type restriction and the individual does not meet it, no
	 * change.
	 *
	 * If contextNodeClasses are not specified, get labels from all context
	 * nodes. note that a partner without a label should just be a no-op.
	 *
	 * If contextNodeClasses are specified, accept some and ignore others.
	 *
	 * Confirm that the expected data is written to the text fields.
	 */

	@Test
	public void noRestrictions_returnLabelsOfAllPartners() {
		setTypeRestrictions();
		setContextNodeTypes();
		exerciseDocumentModifier(URI_PERSON1);
		assertExpectedFieldValues(NAME_ORGANIZATION1, NAME_PERSON2);
	}

	@Test
	public void typeRestrictionOnCorrectType_returnAll() {
		setTypeRestrictions(FOAF_PERSON);
		setContextNodeTypes();
		exerciseDocumentModifier(URI_PERSON1);
		assertExpectedFieldValues(NAME_ORGANIZATION1, NAME_PERSON2);
	}

	@Test
	public void typeRestrictionOnWrongType_returnNothing() {
		setTypeRestrictions(FOAF_ORGANIZATION);
		setContextNodeTypes();
		exerciseDocumentModifier(URI_PERSON1);
		assertExpectedFieldValues();
	}

	@Test
	public void limitByContextNodeType_returnLimited() {
		setTypeRestrictions();
		setContextNodeTypes(CORE_POSITION);
		exerciseDocumentModifier(URI_PERSON1);
		assertExpectedFieldValues(NAME_ORGANIZATION1);
	}

	@Test
	public void inclusiveContextNodeTypes_returnAll() {
		setTypeRestrictions();
		setContextNodeTypes(CORE_POSITION, CORE_ADVISING_RELATIONSHIP);
		exerciseDocumentModifier(URI_PERSON1);
		assertExpectedFieldValues(NAME_ORGANIZATION1, NAME_PERSON2);
	}

	@Test
	public void noContextNodes_noResultsNoProblem() {
		setTypeRestrictions();
		setContextNodeTypes();
		exerciseDocumentModifier(URI_PERSON4);
		assertExpectedFieldValues();
	}

	// ----------------------------------------------------------------------
	// Test IndexingUriFinder
	// ----------------------------------------------------------------------

	/**
	 * <pre>
	 * If neither a label nor a relates, ignore it.
	 *
	 * If label, test if no partners, if some partners, if some partners restricted by type.
	 *   test with both contextNodeClasses and not.
	 * Test with typeRestrictions or without.
	 *
	 * If relates, and fails contextNodeClasses, ignore it.
	 * Test with contextNodeClasses and without.
	 * Find partners, both with typeRestrictions and without.
	 * </pre>
	 */

	@Test
	public void irrelevantStatement_returnsNothing() {
		setTypeRestrictions();
		setContextNodeTypes();
		exerciseUriFinder(stmt(URI_POSITION1, CORE_ASSIGNED_BY, URI_PERSON4));
		assertExpectedUris();
	}

	@Test
	public void label_noPartners_returnsNothing() {
		setTypeRestrictions();
		setContextNodeTypes();
		exerciseUriFinder(labelStmt(URI_PERSON4, "New Name"));
		assertExpectedUris();
	}

	@Test
	public void label_returnsAllPartners() {
		setTypeRestrictions();
		setContextNodeTypes();
		exerciseUriFinder(labelStmt(URI_PERSON1, "New Name"));
		assertExpectedUris(URI_PERSON2, URI_PERSON3, URI_ORGANIZATION1);
	}

	@Test
	public void label_restrictByContextNode_returnEligiblePartners() {
		setTypeRestrictions();
		setContextNodeTypes(CORE_ADVISING_RELATIONSHIP);
		exerciseUriFinder(labelStmt(URI_PERSON1, "New Name"));
		assertExpectedUris(URI_PERSON2, URI_PERSON3);
	}

	@Test
	public void label_restrictByContextNodes_returnEligiblePartners() {
		setTypeRestrictions();
		setContextNodeTypes(CORE_ADVISING_RELATIONSHIP, CORE_POSITION);
		exerciseUriFinder(labelStmt(URI_PERSON1, "New Name"));
		assertExpectedUris(URI_PERSON2, URI_PERSON3, URI_ORGANIZATION1);
	}

	@Test
	public void label_typeRestriction_limitsResults() {
		setTypeRestrictions(FOAF_PERSON);
		setContextNodeTypes();
		exerciseUriFinder(labelStmt(URI_PERSON1, "New Name"));
		assertExpectedUris(URI_PERSON2, URI_PERSON3);
	}

	@Test
	public void label_inclusiveTypeRestrictions_allResults() {
		setTypeRestrictions(FOAF_PERSON, FOAF_ORGANIZATION);
		setContextNodeTypes();
		exerciseUriFinder(labelStmt(URI_PERSON1, "New Name"));
		assertExpectedUris(URI_PERSON2, URI_PERSON3, URI_ORGANIZATION1);
	}

	@Test
	public void label_prohibitiveTypeRestrictions_nothing() {
		setTypeRestrictions(CORE_ACADEMIC_DEGREE);
		setContextNodeTypes();
		exerciseUriFinder(labelStmt(URI_PERSON1, "New Name"));
		assertExpectedUris();
	}

	@Test
	public void relatedBy_returnsPartner() {
		setTypeRestrictions();
		setContextNodeTypes();
		exerciseUriFinder(stmt(URI_POSITION1, CORE_RELATED_BY, URI_PERSON1));
		assertExpectedUris(URI_ORGANIZATION1);
	}

	@Test
	public void relatedBy_inclusiveTypeRestriction_returnsPartner() {
		setTypeRestrictions(FOAF_ORGANIZATION);
		setContextNodeTypes();
		exerciseUriFinder(stmt(URI_POSITION1, CORE_RELATED_BY, URI_PERSON1));
		assertExpectedUris(URI_ORGANIZATION1);
	}

	@Test
	public void relatedBy_exclusiveTypeRestriction_returnsNothing() {
		setTypeRestrictions(CORE_ADVISING_RELATIONSHIP);
		setContextNodeTypes();
		exerciseUriFinder(stmt(URI_POSITION1, CORE_RELATED_BY, URI_PERSON1));
		assertExpectedUris();
	}

	@Test
	public void relatedBy_inclusiveContextType_returnsPartner() {
		setTypeRestrictions();
		setContextNodeTypes(CORE_POSITION);
		exerciseUriFinder(stmt(URI_POSITION1, CORE_RELATED_BY, URI_PERSON1));
		assertExpectedUris(URI_ORGANIZATION1);
	}

	@Test
	public void relatedBy_exclusiveContextType_returnsNothing() {
		setTypeRestrictions();
		setContextNodeTypes(CORE_ADVISING_RELATIONSHIP);
		exerciseUriFinder(stmt(URI_POSITION1, CORE_RELATED_BY, URI_PERSON1));
		assertExpectedUris();
	}

	// ----------------------------------------------------------------------
	// Helper methods
	// ----------------------------------------------------------------------

	private void addIndividual(String uri, String label, String... types) {
		Resource subject = createResource(uri);
		if (label != null) {
			m.add(subject, RDFS.label, label);
		}
		for (String type : types) {
			m.add(subject, RDF.type, createResource(type));
		}
	}

	private void addRelationship(String ind1Uri, String contextNodeUri,
			String ind2Uri) {
		Resource ind1 = createResource(ind1Uri);
		Resource contextNode = createResource(contextNodeUri);
		Resource ind2 = createResource(ind2Uri);
		Property relatedBy = createProperty(CORE_RELATED_BY);
		Property relates = createProperty(CORE_RELATES);
		m.add(ind1, relatedBy, contextNode);
		m.add(contextNode, relates, ind1);
		m.add(ind2, relatedBy, contextNode);
		m.add(contextNode, relates, ind2);
	}

	private void setTypeRestrictions(String... uris) {
		for (String uri : uris) {
			lacn.addTypeRestriction(uri);
		}
	}

	private void setContextNodeTypes(String... uris) {
		for (String uri : uris) {
			lacn.addContextNodeClass(uri);
		}
	}

	private void exerciseDocumentModifier(String individualUri) {
		Individual ind = createIndividual(individualUri);
		doc = new MockSearchInputDocument();
		lacn.modifyDocument(ind, doc);
	}

	private Individual createIndividual(String individualUri) {
		Individual ind = new IndividualImpl(individualUri);
		List<VClass> vclasses = new ArrayList<>();
		for (RDFNode node : m.listObjectsOfProperty(
				createResource(individualUri), RDF.type).toList()) {
			if (node.isURIResource()) {
				vclasses.add(new VClass(node.asResource().getURI()));
			}
		}
		ind.setVClasses(vclasses, false);
		return ind;
	}

	private void assertExpectedFieldValues(String... values) {
		List<String> expected = new ArrayList<>(Arrays.asList(values));
		Collections.sort(expected);

		List<String> actual1 = doc.getValues(ALLTEXT);
		Collections.sort(actual1);
		assertEquals("ALLTEXT", expected, actual1);

		List<String> actual2 = doc.getValues(ALLTEXTUNSTEMMED);
		Collections.sort(actual2);
		assertEquals("ALLTEXTUNSTEMMED", expected, actual2);
	}

	private void exerciseUriFinder(Statement stmt) {
		foundUris = lacn.findAdditionalURIsToIndex(stmt);
	}

	private Statement stmt(String subjectUri, String predicateUri,
			String objectUri) {
		return createStatement(createResource(subjectUri),
				createProperty(predicateUri), createResource(objectUri));
	}

	private Statement labelStmt(String subjectUri, String labelText) {
		return createStatement(createResource(subjectUri), RDFS.label,
				createPlainLiteral(labelText));
	}

	private void assertExpectedUris(String... expectedArray) {
		Set<String> expected = new HashSet<>(Arrays.asList(expectedArray));
		Set<String> actual = new HashSet<>(foundUris);
		assertEquals("found URIs", expected, actual);
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	private static class MockSearchInputDocument implements SearchInputDocument {
		// ----------------------------------------------------------------------
		// Stub infrastructure
		// ----------------------------------------------------------------------

		private Map<String, List<String>> fieldValues = new HashMap<>();

		public List<String> getValues(String fieldName) {
			if (fieldValues.containsKey(fieldName)) {
				return new ArrayList<>(fieldValues.get(fieldName));
			} else {
				return new ArrayList<>();
			}
		}

		// ----------------------------------------------------------------------
		// Stub methods
		// ----------------------------------------------------------------------

		// ----------------------------------------------------------------------
		// Un-implemented methods
		// ----------------------------------------------------------------------

		@Override
		public void addField(SearchInputField arg0) {
			throw new RuntimeException("addField() not implemented.");
		}

		@Override
		public void addField(String name, Object... values) {
			List<String> stringValues = getValues(name);
			for (Object value : values) {
				stringValues.add(String.valueOf(value));
			}
			fieldValues.put(name, stringValues);
		}

		@Override
		public void addField(String arg0, Collection<Object> arg1) {
			throw new RuntimeException("addField() not implemented.");
		}

		@Override
		public void addField(String arg0, float arg1, Object... arg2) {
			throw new RuntimeException("addField() not implemented.");
		}

		@Override
		public void addField(String arg0, float arg1, Collection<Object> arg2) {
			throw new RuntimeException("addField() not implemented.");
		}

		@Override
		public SearchInputField createField(String arg0) {
			throw new RuntimeException("createField() not implemented.");
		}

		@Override
		public float getDocumentBoost() {
			throw new RuntimeException("getDocumentBoost() not implemented.");
		}

		@Override
		public SearchInputField getField(String arg0) {
			throw new RuntimeException("getField() not implemented.");
		}

		@Override
		public Map<String, SearchInputField> getFieldMap() {
			throw new RuntimeException("getFieldMap() not implemented.");
		}

		@Override
		public void setDocumentBoost(float arg0) {
			throw new RuntimeException("setDocumentBoost() not implemented.");
		}

	}

}
