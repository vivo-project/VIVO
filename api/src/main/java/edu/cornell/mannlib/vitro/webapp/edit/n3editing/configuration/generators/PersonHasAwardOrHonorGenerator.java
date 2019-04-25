/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.AutocompleteRequiredInputValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

public class PersonHasAwardOrHonorGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    final static String awardReceiptClass = vivoCore + "AwardReceipt";
    final static String awardClass = vivoCore + "Award";
    final static String orgClass = "http://xmlns.com/foaf/0.1/Organization";
    final static String awardReceiptPred = vivoCore + "relatedBy";
    final static String awardForPred = vivoCore + "relates";
    final static String receiptPred =vivoCore+"relatedBy" ;
    final static String receiptOfPred =vivoCore+"relates" ;
    final static String awardConferredByPred =vivoCore+"assignedBy" ;
    final static String awardConferredPred =vivoCore+"assigns" ;
    final static String descriptionPred = vivoCore + "description";
    final static String yearAwardedPred = vivoCore + "dateTimeValue";
    final static String awardReceiptToInterval = vivoCore + "dateTimeInterval";
    final static String intervalType = vivoCore + "DateTimeInterval";
    final static String intervalToStart = vivoCore + "start";
    final static String intervalToEnd = vivoCore + "end";
    final static String dateTimeValueType = vivoCore + "DateTimeValue";
    final static String dateTimeValue = vivoCore + "dateTime";
    final static String dateTimePrecision = vivoCore + "dateTimePrecision";

    public PersonHasAwardOrHonorGenerator() {}

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);

        conf.setTemplate("personHasAwardOrHonor.ftl");

        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("awardReceipt");

        conf.setN3Required( Arrays.asList( n3ForNewAwardReceipt,
                                           awardReceiptLabelAssertion  ) );
        conf.setN3Optional( Arrays.asList( n3ForNewAwardAssertion,
                                           n3ForExistingAwardAssertion,
                                           descriptionAssertion,
                                           n3ForNewOrgNewAwardAssertion,
                                           n3ForExistingOrgNewAwardAssertion,
                                           n3ForNewOrgExistingAwardAssertion,
                                           n3ForExistingOrgExistingAwardAssertion,
                                           n3ForYearAwarded,
                                           n3ForStart,
                                           n3ForEnd ) );

        conf.addNewResource("award", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("awardReceipt", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("newOrg", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("yearAwardedNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);

        //uris in scope: none
        //literals in scope: none

        conf.setUrisOnform(Arrays.asList("existingAward", "existingOrg"));
        conf.setLiteralsOnForm(Arrays.asList("description", "awardReceiptLabel", "awardLabel", "orgLabel", "yearAwardedDisplay", "orgLabelDisplay", "awardLabelDisplay" ));

        conf.addSparqlForExistingLiteral("awardReceiptLabel", awardReceiptLabelQuery);
        conf.addSparqlForExistingLiteral("awardLabel", awardLabelQuery);
        conf.addSparqlForExistingLiteral("orgLabel", orgLabelQuery);
        conf.addSparqlForExistingLiteral("description", descriptionQuery);
        conf.addSparqlForExistingLiteral("yearAwarded-value", existingYearAwardedQuery);
        conf.addSparqlForExistingLiteral("startField-value", existingStartDateQuery);
        conf.addSparqlForExistingLiteral("endField-value", existingEndDateQuery);

        conf.addSparqlForExistingUris("existingAward", existingAwardQuery);
        conf.addSparqlForExistingUris("existingOrg", existingOrgQuery);
        conf.addSparqlForExistingUris("yearAwardedNode",existingYearAwardedNodeQuery);
        conf.addSparqlForExistingUris("intervalNode",existingIntervalNodeQuery);
        conf.addSparqlForExistingUris("startNode", existingStartNodeQuery);
        conf.addSparqlForExistingUris("endNode", existingEndNodeQuery);
        conf.addSparqlForExistingUris("yearAwarded-precision", existingYearAwardedPrecisionQuery);
        conf.addSparqlForExistingUris("startField-precision", existingStartPrecisionQuery);
        conf.addSparqlForExistingUris("endField-precision", existingEndPrecisionQuery);

        conf.addField( new FieldVTwo().
                setName("description")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo(). // options will be added in browser by auto complete JS
                setName("existingOrg")
        );

        conf.addField( new FieldVTwo(). // options will be added in browser by auto complete JS
                setName("existingAward")
        );

        conf.addField( new FieldVTwo().
                setName("awardReceiptLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("orgLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("awardLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addField( new FieldVTwo().
                setName("yearAwardedDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addField( new FieldVTwo().
                setName("orgLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addField( new FieldVTwo().
                setName("awardLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addField( new FieldVTwo().setName("yearAwarded").
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
        conf.addValidator(new AutocompleteRequiredInputValidator("existingAward", "awardLabel"));
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewAwardReceipt =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?person <" + awardReceiptPred + ">  ?awardReceipt . \n" +
        "?awardReceipt a  <" + awardReceiptClass + "> . \n" +
        "?awardReceipt <" + awardForPred + "> ?person . " ;

    final static String awardReceiptLabelAssertion  =
        "?awardReceipt <"+ label + "> ?awardReceiptLabel .";

    final static String n3ForNewAwardAssertion  =
        "?awardReceipt <" + receiptOfPred + "> ?award . \n" +
        "?award a <" + awardClass + ">  . \n" +
         "?award <" + receiptPred + "> ?awardReceipt . \n" +
        "?award <"+ label + "> ?awardLabel .";

    final static String n3ForExistingAwardAssertion  =
        "?awardReceipt <" + receiptOfPred + "> ?existingAward . \n" +
        "?existingAward <" + receiptPred + "> ?awardReceipt . " ;

    final static String descriptionAssertion  =
        "?awardReceipt <"+ descriptionPred +"> ?description .";

    final static String n3ForExistingOrgNewAwardAssertion  =
        "?awardReceipt <" + awardConferredByPred +"> ?existingOrg . \n" +
        "?existingOrg <" + awardConferredPred + "> ?awardReceipt . \n" +
        "?award <"+ label + "> ?awardLabel .";

    final static String n3ForExistingOrgExistingAwardAssertion  =
        "?awardReceipt <" + awardConferredByPred +"> ?existingOrg . \n" +
        "?existingOrg <" + awardConferredPred + "> ?awardReceipt . ";

    final static String n3ForNewOrgNewAwardAssertion  =
        "?newOrg a <" + orgClass + "> . \n" +
        "?awardReceipt <" + awardConferredByPred +"> ?newOrg . \n" +
        "?newOrg <" + awardConferredPred + "> ?awardReceipt . \n" +
        "?award <"+ label + "> ?awardLabel . \n" +
        "?newOrg <"+ label + "> ?orgLabel .";

    final static String n3ForNewOrgExistingAwardAssertion  =
        "?newOrg a <" + orgClass + "> . \n" +
        "?awardReceipt <" + awardConferredByPred +"> ?newOrg . \n" +
        "?newOrg <" + awardConferredPred + "> ?awardReceipt . \n" +
        "?newOrg <"+ label + "> ?orgLabel .";

	final static String n3ForYearAwarded =
        "?awardReceipt <" + yearAwardedPred + "> ?yearAwardedNode . \n" +
        "?yearAwardedNode a <" + dateTimeValueType + "> . \n" +
        "?yearAwardedNode  <" + dateTimeValue + "> ?yearAwarded-value . \n" +
        "?yearAwardedNode  <" + dateTimePrecision + "> ?yearAwarded-precision .";

    final static String n3ForStart =
        "?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "?startNode a <" + dateTimeValueType + "> . \n" +
        "?startNode  <" + dateTimeValue + "> ?startField-value . \n" +
        "?startNode  <" + dateTimePrecision + "> ?startField-precision . \n";

    final static String n3ForEnd =
        "?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n" +
        "?endNode  <" + dateTimeValue + "> ?endField-value . \n" +
        "?endNode  <" + dateTimePrecision + "> ?endField-precision . \n";

    /* Queries for editing an existing entry */

    final static String existingAwardQuery =
        "SELECT ?existingAward WHERE { \n" +
        " ?awardReceipt <" + receiptOfPred + "> ?existingAward . \n" +
        " ?existingAward a <" + awardClass + "> . \n" +
        "}";

    final static String existingOrgQuery  =
        "SELECT ?existingOrg WHERE { \n" +
        " ?awardReceipt <" + awardConferredByPred + "> ?existingOrg . \n" +
        " ?existingOrg a <" + orgClass + ">  . \n" +
        " ?existingOrg <" + awardConferredPred + "> ?existingAward . }";

    final static String awardReceiptLabelQuery =
        "SELECT ?existingAwardReceiptLabel WHERE { \n" +
        " ?awardReceipt <"  + label + "> ?existingAwardReceiptLabel . \n" +
        "}";

    final static String awardLabelQuery =
        "SELECT ?existingAwardLabel WHERE { \n" +
        " ?awardReceipt <" + receiptOfPred + "> ?existingAward . \n" +
        " ?existingAward a <" + awardClass + "> . \n" +
        " ?existingAward <" + label + "> ?existingAwardLabel . \n" +
        "}";

    final static String orgLabelQuery  =
        "SELECT ?existingOrgLabel WHERE { \n" +
        " ?awardReceipt <" + awardConferredByPred + "> ?existingOrg . \n" +
        " ?existingOrg a <" + orgClass + ">  . \n" +
        " ?existingOrg <" + label + "> ?existingOrgLabel . \n" +
        "}";

    final static String descriptionQuery  =
        "SELECT ?existingDescription WHERE {\n"+
        " ?awardReceipt <"+ descriptionPred +"> ?existingDescription . }";

    final static String existingYearAwardedQuery =
        "SELECT ?existingYearAwardedValue WHERE { \n" +
        "  ?awardReceipt <" + yearAwardedPred + "> ?yearAwardedNode . \n" +
        "  ?yearAwardedNode a <" + dateTimeValueType + "> . \n" +
        "  ?yearAwardedNode <" + dateTimeValue + "> ?existingYearAwardedValue }";

    final static String existingYearAwardedNodeQuery =
        "SELECT ?existingYearAwardedNode WHERE { \n" +
        "  ?awardReceipt <" + yearAwardedPred + "> ?existingYearAwardedNode . }";

    final static String existingStartDateQuery =
        "SELECT ?existingStartDate WHERE { \n" +
        "  ?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a <" + dateTimeValueType +"> . \n" +
        "  ?startNode <" + dateTimeValue + "> ?existingStartDate . }";

    final static String existingEndDateQuery =
        "SELECT ?existingEndDate WHERE { \n" +
        "  ?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n " +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimeValue + "> ?existingEndDate . }";

    final static String existingIntervalNodeQuery =
        "SELECT ?existingIntervalNode WHERE { \n" +
        "  ?awardReceipt <" + awardReceiptToInterval + "> ?existingIntervalNode . \n" +
        "  ?existingIntervalNode a <" + intervalType + "> . }";

    final static String existingStartNodeQuery =
        "SELECT ?existingStartNode WHERE { \n" +
        "  ?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?existingStartNode . \n" +
        "  ?existingStartNode a <" + dateTimeValueType + "> . }   ";

    final static String existingEndNodeQuery =
        "SELECT ?existingEndNode WHERE { \n" +
        "  ?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?existingEndNode . \n" +
        "  ?existingEndNode a <" + dateTimeValueType + "> } ";

    final static String existingYearAwardedPrecisionQuery =
        "SELECT ?existingYearAwardedPrecision WHERE { \n" +
        "  ?awardReceipt <" + yearAwardedPred + "> ?yearAwarded . \n" +
        "  ?yearAwarded a  <" + dateTimeValueType + "> . \n" +
        "  ?yearAwarded <" + dateTimePrecision + "> ?existingYearAwardedPrecision . }";

    final static String existingStartPrecisionQuery =
        "SELECT ?existingStartPrecision WHERE { \n" +
        "  ?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a  <" + dateTimeValueType + "> . \n" +
        "  ?startNode <" + dateTimePrecision + "> ?existingStartPrecision . }";

    final static String existingEndPrecisionQuery =
        "SELECT ?existingEndPrecision WHERE { \n" +
        "  ?awardReceipt <" + awardReceiptToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimePrecision + "> ?existingEndPrecision . }";

}
