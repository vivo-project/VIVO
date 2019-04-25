/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary.Precision;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.FirstAndLastNameValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesWithParent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

public class OrganizationHasPositionHistoryGenerator extends VivoBaseGenerator
		implements EditConfigurationGenerator {

	private final static String NS_VIVO_CORE = "http://vivoweb.org/ontology/core#";

	private static final String URI_PRECISION_NONE = Precision.NONE.uri();
	private static final String URI_PRECISION_YEAR = Precision.YEAR.uri();

	private final static String URI_POSITION_CLASS = NS_VIVO_CORE + "Position";

	private static final String QUERY_EXISTING_POSITION_TITLE = ""
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?existingPositionTitle WHERE { \n"
			+ "  ?position rdfs:label ?existingPositionTitle . }";

	private static final String QUERY_EXISTING_POSITION_TYPE = ""
            + "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n"
			+ "SELECT ?existingPositionType WHERE { \n"
			+ "  ?position vitro:mostSpecificType ?existingPositionType . }";

	private static final String QUERY_EXISTING_PERSON = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?existingPerson WHERE { \n"
			+ "  ?position core:relates ?existingPerson .}";

	private static final String QUERY_EXISTING_PERSON_LABEL = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT ?existingPersonLabel WHERE { \n"
			+ "  ?position core:relates ?existingPerson . \n"
			+ "  ?existingPerson rdfs:label ?existingPersonLabel . }";

	private static final String QUERY_EXISTING_INTERVAL_NODE = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingIntervalNode WHERE { \n"
			+ "  ?position core:dateTimeInterval ?existingIntervalNode . \n"
			+ "  ?existingIntervalNode a core:DateTimeInterval . }";

	private static final String QUERY_EXISTING_START_NODE = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingStartNode WHERE { \n"
			+ "  ?position core:dateTimeInterval ?intervalNode .\n"
			+ "  ?intervalNode a core:DateTimeInterval ;\n"
			+ "      core:start ?existingStartNode . \n"
			+ "  ?existingStartNode a core:DateTimeValue . }";

	private static final String QUERY_EXISTING_START_VALUE = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingDateStart WHERE { \n"
			+ "  ?position core:dateTimeInterval ?intervalNode .\n"
			+ "  ?intervalNode a core:DateTimeInterval ; \n"
			+ "      core:start ?startNode . \n"
			+ "  ?startNode a core:DateTimeValue ; \n"
			+ "      core:dateTime ?existingDateStart . }";

	private static final String QUERY_EXISTING_START_PRECISION = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingStartPrecision WHERE { \n"
			+ "  ?position core:dateTimeInterval ?intervalNode .\n"
			+ "  ?intervalNode a core:DateTimeInterval ;\n"
			+ "      core:start ?startNode . \n"
			+ "  ?startNode a core:DateTimeValue ; \n"
			+ "      core:dateTimePrecision ?existingStartPrecision . }";

	private static final String QUERY_EXISTING_END_NODE = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingEndNode WHERE { \n"
			+ "  ?position core:dateTimeInterval ?intervalNode .\n"
			+ "  ?intervalNode a core:DateTimeInterval ;\n"
			+ "      core:end ?existingEndNode . \n"
			+ "  ?existingEndNode a core:DateTimeValue . }";

	private static final String QUERY_EXISTING_END_VALUE = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingDateEnd WHERE { \n"
			+ "  ?position core:dateTimeInterval ?intervalNode .\n"
			+ "  ?intervalNode a core:DateTimeInterval ; \n"
			+ "        core:end ?endNode . \n"
			+ "  ?endNode a core:DateTimeValue ; \n"
			+ "        core:dateTime ?existingDateEnd . }";

	private static final String QUERY_EXISTING_END_PRECISION = ""
			+ "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
			+ "SELECT ?existingEndPrecision WHERE { \n"
			+ "  ?position core:dateTimeInterval ?intervalNode .\n"
			+ "  ?intervalNode a core:DateTimeInterval ;\n"
			+ "      core:end ?endNode . \n"
			+ "  ?endNode a core:DateTimeValue ; \n"
			+ "      core:dateTimePrecision ?existingEndPrecision . }";

	private static final String N3_NEW_POSITION = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
			+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . \n"
			+ "?organization core:relatedBy ?position . \n"
			+ "?position a core:Position . \n"
			+ "?position a  ?positionType . \n"
			+ "?position rdfs:label ?positionTitle . \n"
			+ "?position core:relates ?organization . ";

    private static final String N3_NEW_PERSON = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
			+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . \n"
			+ "@prefix foaf: <http://xmlns.com/foaf/0.1/> . \n"
    		+ "?position core:relates ?person . \n"
    		+ "?person core:relatedBy ?position . \n"
    		+ "?person a foaf:Person . \n"
    		+ "?person rdfs:label ?personLabel . ";

    private static final String N3_NEW_FIRST_NAME = ""
    		+ "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n"
        	+ "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardPerson . \n"
        	+ "?vcardPerson <http://purl.obolibrary.org/obo/ARG_2000029>  ?person . \n"
        	+ "?vcardPerson a <http://www.w3.org/2006/vcard/ns#Individual> . \n"
        	+ "?vcardPerson vcard:hasName  ?vcardName . \n"
        	+ "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n"
        	+ "?vcardName vcard:givenName ?firstName .";

    private static final String N3_NEW_LAST_NAME = ""
		    + "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n"
    	    + "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardPerson . \n"
    	    + "?vcardPerson <http://purl.obolibrary.org/obo/ARG_2000029>  ?person . \n"
    	    + "?vcardPerson a <http://www.w3.org/2006/vcard/ns#Individual> . \n"
    	    + "?vcardPerson vcard:hasName  ?vcardName . \n"
    	    + "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n"
    	    + "?vcardName vcard:familyName ?lastName .";

    private static final String N3_EXISTING_PERSON = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
        	+ "?position core:relates ?existingPerson . \n"
        	+ "?existingPerson core:relatedBy ?position . \n";

	private static final String N3_NEW_START_NODE = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
			+ "?position core:dateTimeInterval ?intervalNode . \n"
			+ "?intervalNode a core:DateTimeInterval . \n"
			+ "?intervalNode core:start ?startNode . \n "
			+ "?startNode a core:DateTimeValue . \n"
			+ "?startNode core:dateTime ?startField-value. \n"
			+ "?startNode core:dateTimePrecision ?startField-precision . ";

	private static final String N3_NEW_END_NODE = ""
			+ "@prefix core: <http://vivoweb.org/ontology/core#> . \n"
			+ "?position core:dateTimeInterval ?intervalNode . \n"
			+ "?intervalNode a core:DateTimeInterval . \n"
			+ "?intervalNode core:end ?endNode . \n "
			+ "?endNode a core:DateTimeValue . \n"
			+ "?endNode core:dateTime ?endField-value . \n"
			+ "?endNode core:dateTimePrecision ?endField-precision . ";

	@Override
	public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
			HttpSession session) throws Exception {
		EditConfigurationVTwo conf = new EditConfigurationVTwo();

		initBasics(conf, vreq);
		initPropertyParameters(vreq, session, conf);
		initObjectPropForm(conf, vreq);

		conf.setVarNameForSubject("organization");
		conf.setVarNameForPredicate("predicate");
		conf.setVarNameForObject("position");

		conf.setTemplate("organizationHasPositionHistory.ftl");

		conf.setN3Required(Arrays.asList(N3_NEW_POSITION));
		conf.setN3Optional(Arrays.asList(N3_NEW_PERSON, N3_EXISTING_PERSON, N3_NEW_START_NODE, N3_NEW_END_NODE, N3_NEW_FIRST_NAME, N3_NEW_LAST_NAME));

		conf.addNewResource("position", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("person", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("vcardName", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("vcardPerson", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
		conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);

		conf.setUrisOnform(Arrays.asList("existingPerson", "position", "positionType"));
		conf.addSparqlForExistingUris("positionType",
				QUERY_EXISTING_POSITION_TYPE);
		conf.addSparqlForExistingUris("intervalNode",
				QUERY_EXISTING_INTERVAL_NODE);
		conf.addSparqlForExistingUris("startNode", QUERY_EXISTING_START_NODE);
		conf.addSparqlForExistingUris("endNode", QUERY_EXISTING_END_NODE);

		conf.setLiteralsOnForm(Arrays.asList("positionTitle", "personLabelDisplay", "personLabel", "firstName", "lastName"));
		conf.addSparqlForExistingLiteral("positionTitle",
				QUERY_EXISTING_POSITION_TITLE);
		conf.addSparqlForExistingLiteral("personLabel",
				QUERY_EXISTING_PERSON_LABEL);
		conf.addSparqlForExistingUris("existingPerson",
				QUERY_EXISTING_PERSON);
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
				.setValidators(list("nonempty"))
				.setOptions(
				        new ChildVClassesWithParent(URI_POSITION_CLASS))
				);

		conf.addField(new FieldVTwo().setName("positionTitle")
				.setRangeDatatypeUri(XSD.xstring.toString())
				.setValidators(list("nonempty")));

		//options for existingPerson will be added in browser by auto complete JS
		conf.addField(new FieldVTwo().setName("existingPerson"));

		conf.addField(new FieldVTwo().setName("personLabel")
				.setRangeDatatypeUri(XSD.xstring.toString())
				.setValidators( list("datatype:" + XSD.xstring.toString()) ));

    	conf.addField(new FieldVTwo().setName("firstName")
    			.setRangeDatatypeUri(XSD.xstring.toString())
				.setValidators( list("datatype:" + XSD.xstring.toString()) ));

    	conf.addField(new FieldVTwo().setName("lastName")
    			.setRangeDatatypeUri(XSD.xstring.toString())
				.setValidators( list("datatype:" + XSD.xstring.toString()) ));

    	conf.addField(new FieldVTwo().setName("personLabelDisplay")
    			.setRangeDatatypeUri(XSD.xstring.toString())
    			.setValidators( list("datatype:" + XSD.xstring.toString()) ));

		FieldVTwo startField = new FieldVTwo().setName("startField");
		conf.addField(startField.setEditElement(new DateTimeWithPrecisionVTwo(
				startField, URI_PRECISION_YEAR, URI_PRECISION_NONE)));

		FieldVTwo endField = new FieldVTwo().setName("endField");
		conf.addField(endField.setEditElement(new DateTimeWithPrecisionVTwo(
				endField, URI_PRECISION_YEAR, URI_PRECISION_NONE)));

        conf.addValidator(new FirstAndLastNameValidator("existingPerson"));
		conf.addValidator(new AntiXssValidation());
		conf.addValidator(new DateTimeIntervalValidationVTwo("startField",
				"endField"));

		prepare(vreq, conf);
		return conf;
	}
}
