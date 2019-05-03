/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.AutocompleteRequiredInputValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

public class PersonHasIssuedCredentialGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    final static String issuedCredentialTypeClass = vivoCore + "IssuedCredential";
    final static String credentialTypeClass = vivoCore + "Credential";
    final static String yearCredentialedPred = vivoCore + "dateIssued";
    final static String issuedCredentialToInterval = vivoCore + "dateTimeInterval";
    final static String intervalType = vivoCore + "DateTimeInterval";
    final static String intervalToStart = vivoCore + "start";
    final static String intervalToEnd = vivoCore + "end";
    final static String dateTimeValueType = vivoCore + "DateTimeValue";
    final static String dateTimeValue = vivoCore + "dateTime";
    final static String dateTimePrecision = vivoCore + "dateTimePrecision";

    public PersonHasIssuedCredentialGenerator() {}

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);

        conf.setTemplate("personHasIssuedCredential.ftl");

        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("issuedCredential");

        conf.setN3Required( Arrays.asList( n3ForNewIssuedCredential, n3ForICTypeAssertion) );
        conf.setN3Optional( Arrays.asList( n3ForNewCredentialAssertion,
                                           n3ForExistingCredentialAssertion,
                                           n3ForYearCredentialed,
                                           n3ForStart,
                                           n3ForEnd ) );

        conf.addNewResource("credential", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("issuedCredential", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("yearCredentialedNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);

        //uris in scope: none
        //literals in scope: none

        conf.setUrisOnform(Arrays.asList("existingCredential", "issuedCredentialType", "credentialType"));
        conf.setLiteralsOnForm(Arrays.asList("yearCredentialedDisplay","credentialLabel", "credentialLabelDisplay" ));

        conf.addSparqlForExistingLiteral("credentialLabel", credentialLabelQuery);
        conf.addSparqlForExistingLiteral("yearCredentialed-value", existingYearCredentialedQuery);
        conf.addSparqlForExistingLiteral("startField-value", existingStartDateQuery);
        conf.addSparqlForExistingLiteral("endField-value", existingEndDateQuery);

        conf.addSparqlForExistingUris("existingCredential", existingCredentialQuery);
        conf.addSparqlForExistingUris("credentialType", existingCredentialTypeQuery);
        conf.addSparqlForExistingUris("issuedCredentialType", issuedCredentialTypeQuery);
        conf.addSparqlForExistingUris("yearCredentialedNode",existingYearCredentialedNodeQuery);
        conf.addSparqlForExistingUris("intervalNode",existingIntervalNodeQuery);
        conf.addSparqlForExistingUris("startNode", existingStartNodeQuery);
        conf.addSparqlForExistingUris("endNode", existingEndNodeQuery);
        conf.addSparqlForExistingUris("yearCredentialed-precision", existingYearCredentialedPrecisionQuery);
        conf.addSparqlForExistingUris("startField-precision", existingStartPrecisionQuery);
        conf.addSparqlForExistingUris("endField-precision", existingEndPrecisionQuery);

        conf.addField( new FieldVTwo().
                setName("issuedCredentialType").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addField( new FieldVTwo().
                setName("credentialType").
                setOptions(getCredentialTypeFieldOptions(vreq)));

        conf.addField( new FieldVTwo(). // options will be added in browser by auto complete JS
                setName("existingCredential")
        );

        conf.addField( new FieldVTwo().
                setName("credentialLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("yearCredentialedDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addField( new FieldVTwo().
                setName("credentialLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addField( new FieldVTwo().setName("yearCredentialed").
                setEditElement(
                        new DateTimeWithPrecisionVTwo(null,
                                VitroVocabulary.Precision.YEAR.uri(),
                                VitroVocabulary.Precision.NONE.uri())
                                )
                );

        conf.addField( new FieldVTwo().setName("startField").
                setEditElement(
                        new DateTimeWithPrecisionVTwo(null,
                                VitroVocabulary.Precision.YEAR.uri(),
                                VitroVocabulary.Precision.NONE.uri())
                                )
                );

        conf.addField( new FieldVTwo().setName("endField").
                setEditElement(
                        new DateTimeWithPrecisionVTwo(null,
                                VitroVocabulary.Precision.YEAR.uri(),
                                VitroVocabulary.Precision.NONE.uri())
                                )
                );

        conf.addValidator(new DateTimeIntervalValidationVTwo("startField","endField"));
        conf.addValidator(new AntiXssValidation());
        conf.addValidator(new AutocompleteRequiredInputValidator("existingCredential", "credentialLabel"));

        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewIssuedCredential =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?person vivo:relatedBy  ?issuedCredential . \n" +
        "?issuedCredential a  <" + issuedCredentialTypeClass + "> . \n" +
        "?issuedCredential vivo:relates ?person . " ;

    final static String n3ForICTypeAssertion  =
        "?issuedCredential a ?issuedCredentialType .";

    final static String n3ForNewCredentialAssertion  =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?issuedCredential vivo:relates ?credential . \n" +
        "?credential a <" + credentialTypeClass + ">  . \n" +
        "?credential vivo:relatedBy ?issuedCredential . \n" +
        "?credential a ?credentialType . \n" +
        "?credential <"+ label + "> ?credentialLabel .";

    final static String n3ForExistingCredentialAssertion  =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?issuedCredential vivo:relates ?existingCredential . \n" +
/*        "?existingCredential a <" + credentialTypeClass + ">  . \n" +
        "?existingCredential a ?credentialType . \n" +  */
        "?existingCredential vivo:relatedBy ?issuedCredential . " ;

	final static String n3ForYearCredentialed =
        "?issuedCredential <" + yearCredentialedPred + "> ?yearCredentialedNode . \n" +
        "?yearCredentialedNode a <" + dateTimeValueType + "> . \n" +
        "?yearCredentialedNode  <" + dateTimeValue + "> ?yearCredentialed-value . \n" +
        "?yearCredentialedNode  <" + dateTimePrecision + "> ?yearCredentialed-precision .";

    final static String n3ForStart =
        "?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "?startNode a <" + dateTimeValueType + "> . \n" +
        "?startNode  <" + dateTimeValue + "> ?startField-value . \n" +
        "?startNode  <" + dateTimePrecision + "> ?startField-precision . \n";

    final static String n3ForEnd =
        "?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n" +
        "?endNode  <" + dateTimeValue + "> ?endField-value . \n" +
        "?endNode  <" + dateTimePrecision + "> ?endField-precision . \n";

    /* Queries for editing an existing entry */

    final static String existingCredentialQuery =
        "PREFIX vivo: <http://vivoweb.org/ontology/core#>  \n" +
        "SELECT ?existingCredential WHERE { \n" +
        " ?issuedCredential vivo:relates ?existingCredential . \n" +
        " ?existingCredential a vivo:Credential . \n" +
        "}";

    final static String existingCredentialTypeQuery =
        "PREFIX vivo: <http://vivoweb.org/ontology/core#>  \n" +
        "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?existingCredentialType WHERE { \n" +
        " ?issuedCredential vivo:relates ?existingCredential . \n" +
        " ?existingCredential a vivo:Credential . \n" +
        " ?existingCredential vitro:mostSpecificType ?existingCredentialType . \n" +
        "}";

    final static String issuedCredentialTypeQuery =
        "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?existingICType WHERE { \n" +
        " ?issuedCredential vitro:mostSpecificType ?existingICType . \n" +
        "}";

    final static String credentialLabelQuery =
        "PREFIX vivo: <http://vivoweb.org/ontology/core#> \n" +
        "SELECT ?existingCredentialLabel WHERE { \n" +
        " ?issuedCredential vivo:relates ?existingCredential . \n" +
        " ?existingCredential a <http://vivoweb.org/ontology/core#Credential> . \n" +
        " ?existingCredential <" + label + "> ?existingCredentialLabel . \n" +
        "}";

    final static String existingYearCredentialedQuery =
        "SELECT ?existingYearCredentialedValue WHERE { \n" +
        "  ?issuedCredential <" + yearCredentialedPred + "> ?yearCredentialedNode . \n" +
        "  ?yearCredentialedNode a <" + dateTimeValueType + "> . \n" +
        "  ?yearCredentialedNode <" + dateTimeValue + "> ?existingYearCredentialedValue }";

    final static String existingYearCredentialedNodeQuery =
        "SELECT ?existingYearCredentialedNode WHERE { \n" +
        "  ?issuedCredential <" + yearCredentialedPred + "> ?existingYearCredentialedNode . }";

    final static String existingStartDateQuery =
        "SELECT ?existingStartDate WHERE { \n" +
        "  ?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a <" + dateTimeValueType +"> . \n" +
        "  ?startNode <" + dateTimeValue + "> ?existingStartDate . }";

    final static String existingEndDateQuery =
        "SELECT ?existingEndDate WHERE { \n" +
        "  ?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n " +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimeValue + "> ?existingEndDate . }";

    final static String existingIntervalNodeQuery =
        "SELECT ?existingIntervalNode WHERE { \n" +
        "  ?issuedCredential <" + issuedCredentialToInterval + "> ?existingIntervalNode . \n" +
        "  ?existingIntervalNode a <" + intervalType + "> . }";

    final static String existingStartNodeQuery =
        "SELECT ?existingStartNode WHERE { \n" +
        "  ?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?existingStartNode . \n" +
        "  ?existingStartNode a <" + dateTimeValueType + "> . }   ";

    final static String existingEndNodeQuery =
        "SELECT ?existingEndNode WHERE { \n" +
        "  ?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?existingEndNode . \n" +
        "  ?existingEndNode a <" + dateTimeValueType + "> } ";

    final static String existingYearCredentialedPrecisionQuery =
        "SELECT ?existingYearCredentialedPrecision WHERE { \n" +
        "  ?issuedCredential <" + yearCredentialedPred + "> ?yearCredentialed . \n" +
        "  ?yearCredentialed a  <" + dateTimeValueType + "> . \n" +
        "  ?yearCredentialed <" + dateTimePrecision + "> ?existingYearCredentialedPrecision . }";

    final static String existingStartPrecisionQuery =
        "SELECT ?existingStartPrecision WHERE { \n" +
        "  ?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a  <" + dateTimeValueType + "> . \n" +
        "  ?startNode <" + dateTimePrecision + "> ?existingStartPrecision . }";

    final static String existingEndPrecisionQuery =
        "SELECT ?existingEndPrecision WHERE { \n" +
        "  ?issuedCredential <" + issuedCredentialToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimePrecision + "> ?existingEndPrecision . }";

    //Form specific data
    public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
        HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
        formSpecificData.put("credentialTypeMap", getCredentialTypeMap());
        editConfiguration.setFormSpecificData(formSpecificData);
    }

    // Issued Credentials relate a Credential to a person. The class type of a Credential and its subclasses
    // are different than -- but correspond to -- the class type of an Issued Credential and its subclasses.
    // When a user picks a type of credential in the GUI, we need to set the corresponding type for the issued
    // credential. This map makes that possible. The class name of the credential is on the right, and the URI
    // of th eissued credential is on the left.
    private HashMap<String, String> getCredentialTypeMap() {
        HashMap<String, String> credentials = new HashMap<String, String>();
        credentials.put("Credential","http://vivoweb.org/ontology/core#IssuedCredential");
        credentials.put("Certificate","http://vivoweb.org/ontology/core#Certification");
        credentials.put("License","http://vivoweb.org/ontology/core#Licensure");
        return credentials;
    }
    private FieldOptions getCredentialTypeFieldOptions(VitroRequest vreq) throws Exception {
		return new ConstantFieldOptions(
        "http://vivoweb.org/ontology/core#Certificate", "Certificate",
        "http://vivoweb.org/ontology/core#Credential", "Credential",
        "http://vivoweb.org/ontology/core#License", "License");
	}

}
