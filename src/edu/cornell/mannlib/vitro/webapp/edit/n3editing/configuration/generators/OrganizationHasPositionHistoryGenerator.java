/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary.Precision;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;

public class OrganizationHasPositionHistoryGenerator extends VivoBaseGenerator
		implements EditConfigurationGenerator {

	private final static String NS_VIVO_CORE = "http://vivoweb.org/ontology/core#";

	private static final String URI_PRECISION_NONE = Precision.NONE.uri();
	private static final String URI_PRECISION_YEAR = Precision.YEAR.uri();

	private final static String URI_POSITION_CLASS = vivoCore("Position");
	private final static String URI_INTERVAL_FOR_POSITION = vivoCore("dateTimeInterval");
	private final static String URI_DATE_TIME_INTERVAL_CLASS = vivoCore("DateTimeInterval");
	private final static String URI_DATE_TIME_VALUE_CLASS = vivoCore("DateTimeValue");
	private final static String URI_DATE_TIME_VALUE = vivoCore("dateTime");
	private final static String URI_DATE_TIME_PRECISION = vivoCore("dateTimePrecision");

	private final static String URI_INTERVAL_TO_START = vivoCore("start");
	private final static String URI_INTERVAL_TO_END = vivoCore("end");

	private static String vivoCore(String localName) {
		return NS_VIVO_CORE + localName;
	}

	private static final String QUERY_EXISTING_POSITION_TITLE = ""
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?existingPositionTitle WHERE { \n"
			+ "  ?position rdfs:label ?existingPositionTitle . }";

	private static final String QUERY_EXISTING_POSITION_TYPE = ""
			+ "SELECT ?existingPositionType WHERE { \n"
			+ "  ?position a ?existingPositionType . }";

	private static final String QUERY_EXISTING_PERSON = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?existingPerson WHERE { \n"
			+ "  ?position core:positionForPerson ?existingPerson .}";

	private static final String QUERY_EXISTING_PERSON_LABEL = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?existingPersonLabel WHERE { \n"
			+ "  ?position core:positionForPerson ?existingPerson . \n"
			+ "  ?existingPerson rdfs:label ?existingPersonLabel . }";

	private static final String QUERY_EXISTING_INTERVAL_NODE = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingIntervalNode WHERE { \n"
			+ "  ?position core:dateTimeInterval ?existingIntervalNode . \n"
			+ "  ?existingIntervalNode a core:DateTimeInterval . }";

	private static final String QUERY_EXISTING_START_NODE = String.format(
			"SELECT ?existingStartNode WHERE { \n"
					+ "  ?position <%1$s> ?intervalNode .\n"
					+ "  ?intervalNode a <%2$s> ;\n"
					+ "      <%3$s> ?existingStartNode . \n"
					+ "  ?existingStartNode a <%4$s> . }",
			URI_INTERVAL_FOR_POSITION, URI_DATE_TIME_INTERVAL_CLASS,
			URI_INTERVAL_TO_START, URI_DATE_TIME_VALUE_CLASS);

	private static final String QUERY_EXISTING_START_VALUE = String.format(
			"SELECT ?existingDateStart WHERE { \n"
					+ "  ?position <%1$s> ?intervalNode .\n"
					+ "  ?intervalNode a <%2$s> ; \n"
					+ "      <%3$s> ?startNode . \n"
					+ "  ?startNode a <%4$s> ; \n"
					+ "      <%5$s> ?existingDateStart . }",
			URI_INTERVAL_FOR_POSITION, URI_DATE_TIME_INTERVAL_CLASS,
			URI_INTERVAL_TO_START, URI_DATE_TIME_VALUE_CLASS,
			URI_DATE_TIME_VALUE);

	private static final String QUERY_EXISTING_START_PRECISION = String.format(
			"SELECT ?existingStartPrecision WHERE { \n"
					+ "  ?position <%1$s> ?intervalNode .\n"
					+ "  ?intervalNode a <%2$s> ;\n"
					+ "      <%3$s> ?startNode . \n"
					+ "  ?startNode a <%4$s> ; \n"
					+ "      <%5$s> ?existingStartPrecision . }",
			URI_INTERVAL_FOR_POSITION, URI_DATE_TIME_INTERVAL_CLASS,
			URI_INTERVAL_TO_START, URI_DATE_TIME_VALUE_CLASS,
			URI_DATE_TIME_PRECISION);

	private static final String QUERY_EXISTING_END_VALUE = String
			.format("SELECT ?existingDateEnd WHERE { \n"
					+ "  ?position <%1$s> ?intervalNode .\n"
					+ "  ?intervalNode a <%2$s> ; \n"
					+ "        <%3$s> ?endNode . \n"
					+ "  ?endNode a <%4$s> ; \n"
					+ "        <%5$s> ?existingDateEnd . }",
					URI_INTERVAL_FOR_POSITION, URI_DATE_TIME_INTERVAL_CLASS,
					URI_INTERVAL_TO_END, URI_DATE_TIME_VALUE_CLASS,
					URI_DATE_TIME_VALUE);

	private static final String QUERY_EXISTING_END_PRECISION = String.format(
			"SELECT ?existingEndPrecision WHERE { \n"
					+ "  ?position <%1$s> ?intervalNode .\n"
					+ "  ?intervalNode a <%2$s> ;\n"
					+ "      <%3$s> ?endNode . \n" + "  ?endNode a <%4$s> ; \n"
					+ "      <%5$s> ?existingEndPrecision . }",
			URI_INTERVAL_FOR_POSITION, URI_DATE_TIME_INTERVAL_CLASS,
			URI_INTERVAL_TO_END, URI_DATE_TIME_VALUE_CLASS,
			URI_DATE_TIME_PRECISION);

	private static final String QUERY_EXISTING_END_NODE = String.format(
			"SELECT ?existingEndNode WHERE { \n"
					+ "  ?position <%1$s> ?intervalNode .\n"
					+ "  ?intervalNode a <%2$s> ;\n"
					+ "      <%3$s> ?existingEndNode . \n"
					+ "  ?existingEndNode a <%4$s> .}",
			URI_INTERVAL_FOR_POSITION, URI_DATE_TIME_INTERVAL_CLASS,
			URI_INTERVAL_TO_END, URI_DATE_TIME_VALUE_CLASS);

	private static final String N3_NEW_POSITION = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
			+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . \n"
			+ "\n" //
			+ "?organization core:organizationForPosition ?position . \n"
			+ "\n" //
			+ "?position a core:Position , ?positionType ; \n"
			+ "    rdfs:label ?positionTitle ; \n"
			+ "    core:positionInOrganization ?organization ; \n"
			+ "    core:positionForPerson ?person . \n" //
			+ "\n" //
			+ "?person core:personInPosition ?position .";

	private static final String N3_NEW_START_NODE = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
			+ "\n" //
			+ "?position core:dateTimeInterval ?intervalNode . \n"
			+ "\n" //
			+ "?intervalNode a core:DateTimeInterval ; \n"
			+ "    core:start ?startNode . \n "
			+ "\n" //
			+ "?startNode a core:DateTimeValue ; \n"
			+ "    core:dateTime ?startField-value ; \n"
			+ "    core:dateTimePrecision ?startField-precision . ";

	private static final String N3_NEW_END_NODE = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
			+ "\n" //
			+ "?position core:dateTimeInterval ?intervalNode . \n"
			+ "\n" //
			+ "?intervalNode a core:DateTimeInterval ; \n"
			+ "    core:end ?endNode . \n "
			+ "\n" //
			+ "?endNode a core:DateTimeValue ; \n"
			+ "    core:dateTime ?endField-value ; \n"
			+ "    core:dateTimePrecision ?endField-precision . ";

	@Override
	public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
			HttpSession session) {
		EditConfigurationVTwo conf = new EditConfigurationVTwo();

		initBasics(conf, vreq);
		initPropertyParameters(vreq, session, conf);
		initObjectPropForm(conf, vreq);

		conf.setVarNameForSubject("organization");
		conf.setVarNameForPredicate("predicate");
		conf.setVarNameForObject("position");

		conf.setTemplate("organizationHasPositionHistory.ftl");

		conf.setN3Required(Arrays.asList(N3_NEW_POSITION));
		conf.setN3Optional(Arrays.asList(N3_NEW_START_NODE, N3_NEW_END_NODE));

		conf.addNewResource("position", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("person", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);

		conf.setUrisOnform(Arrays.asList("person", "position", "positionType"));
		conf.addSparqlForExistingUris("positionType",
				QUERY_EXISTING_POSITION_TYPE);
		conf.addSparqlForExistingUris("person", QUERY_EXISTING_PERSON);

		conf.addSparqlForExistingUris("intervalNode",
				QUERY_EXISTING_INTERVAL_NODE);
		conf.addSparqlForExistingUris("startNode", QUERY_EXISTING_START_NODE);
		conf.addSparqlForExistingUris("endNode", QUERY_EXISTING_END_NODE);

		conf.setLiteralsOnForm(Arrays.asList("positionTitle", "personLabel"));
		conf.addSparqlForExistingLiteral("positionTitle",
				QUERY_EXISTING_POSITION_TITLE);
		conf.addSparqlForExistingLiteral("personLabel",
				QUERY_EXISTING_PERSON_LABEL);

		conf.addSparqlForExistingLiteral("startField-value",
				QUERY_EXISTING_START_VALUE);
		conf.addSparqlForExistingUris("startField-precision",
				QUERY_EXISTING_START_PRECISION);
		conf.addSparqlForExistingLiteral("endField-value",
				QUERY_EXISTING_END_VALUE);
		conf.addSparqlForExistingUris("endField-precision",
				QUERY_EXISTING_END_PRECISION);

		conf.addField(new FieldVTwo()
				.setName("positionType")
				.setOptionsType(
						FieldVTwo.OptionsType.CHILD_VCLASSES_WITH_PARENT)
				.setObjectClassUri(URI_POSITION_CLASS)
				.setValidators(list("nonempty")));

		conf.addField(new FieldVTwo().setName("positionTitle")
				.setRangeDatatypeUri(XSD.xstring.toString())
				.setValidators(list("nonempty")));

		conf.addField(new FieldVTwo().setName("person")
				.setOptionsType(FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS)
				.setObjectClassUri(personClass));

		conf.addField(new FieldVTwo().setName("personLabel")
				.setRangeDatatypeUri(XSD.xstring.toString())
				.setValidators(list("nonempty")));

		FieldVTwo startField = new FieldVTwo().setName("startField");
		conf.addField(startField.setEditElement(new DateTimeWithPrecisionVTwo(
				startField, URI_PRECISION_YEAR, URI_PRECISION_NONE)));

		FieldVTwo endField = new FieldVTwo().setName("endField");
		conf.addField(endField.setEditElement(new DateTimeWithPrecisionVTwo(
				endField, URI_PRECISION_YEAR, URI_PRECISION_NONE)));

		conf.addValidator(new DateTimeIntervalValidationVTwo("startField",
				"endField"));

		return conf;
	}
}
