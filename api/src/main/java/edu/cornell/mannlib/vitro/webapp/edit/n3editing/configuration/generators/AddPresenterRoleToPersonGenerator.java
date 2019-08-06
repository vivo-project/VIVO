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
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesWithParent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

public class AddPresenterRoleToPersonGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    final static String presentationClass = vivoCore + "Presentation";
    final static String roleClass = vivoCore + "PresenterRole";
    final static String conferenceClass = bibo + "Conference";
    final static String hasRolePred = "http://purl.obolibrary.org/obo/RO_0000053";
    final static String roleOfPred = "http://purl.obolibrary.org/obo/RO_0000052";
    final static String roleRealizedInPred = "http://purl.obolibrary.org/obo/BFO_0000054";
    final static String realizedRolePred = "http://purl.obolibrary.org/obo/BFO_0000055";
    final static String includesEventPred = "http://purl.obolibrary.org/obo/BFO_0000051";
    final static String eventWithinPred = "http://purl.obolibrary.org/obo/BFO_0000050";
    final static String roleToInterval = vivoCore + "dateTimeInterval";
    final static String intervalType = vivoCore + "DateTimeInterval";
    final static String intervalToStart = vivoCore + "start";
    final static String intervalToEnd = vivoCore + "end";
    final static String dateTimeValueType = vivoCore + "DateTimeValue";
    final static String dateTimeValue = vivoCore + "dateTime";
    final static String dateTimePrecision = vivoCore + "dateTimePrecision";

    public AddPresenterRoleToPersonGenerator() {}

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);

        conf.setTemplate("addPresenterRoleToPerson.ftl");

        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("role");

        conf.setN3Required( Arrays.asList( n3ForNewRole ) );
        conf.setN3Optional( Arrays.asList( n3ForRoleLabelAssertion,
										   n3ForNewPresentation,
                                           n3ForExistingPresentation,
                                           n3ForNewConferenceNewPres,
                                           n3ForNewConferenceExistingPres,
                                           n3ForExistingConferenceNewPres,
                                           n3ForExistingConferenceExistingPres,
                                           n3ForStart,
                                           n3ForEnd ) );

        conf.addNewResource("presentation", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("newConference", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("role", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);

        //uris in scope: none
        //literals in scope: none

        conf.setUrisOnform(Arrays.asList("existingPresentation", "existingConference", "presentationType"));
        conf.setLiteralsOnForm(Arrays.asList("presentationLabel", "presentationLabelDisplay", "conferenceLabel", "conferenceLabelDisplay", "roleLabel"));

        conf.addSparqlForExistingLiteral("presentationLabel", presentationLabelQuery);
        conf.addSparqlForExistingLiteral("conferenceLabel", conferenceLabelQuery);
        conf.addSparqlForExistingLiteral("roleLabel", roleLabelQuery);
        conf.addSparqlForExistingUris("existingPresentation", presentationQuery);
        conf.addSparqlForExistingUris("existingConference", existingConferenceQuery);
        conf.addSparqlForExistingUris("presentationType", presentationTypeQuery);
        conf.addSparqlForExistingLiteral(
                "startField-value", existingStartDateQuery);
        conf.addSparqlForExistingLiteral(
                "endField-value", existingEndDateQuery);
        conf.addSparqlForExistingUris(
                "intervalNode", existingIntervalNodeQuery);
        conf.addSparqlForExistingUris("startNode", existingStartNodeQuery);
        conf.addSparqlForExistingUris("endNode", existingEndNodeQuery);
        conf.addSparqlForExistingUris("startField-precision",
                existingStartPrecisionQuery);
        conf.addSparqlForExistingUris("endField-precision",
                existingEndPrecisionQuery);

        conf.addField( new FieldVTwo(). // an autocomplete field
                setName("existingPresentation")
                );

        conf.addField( new FieldVTwo().
                setName("presentationLabelDisplay")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("presentationLabel")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("presentationType").
                setValidators( list("nonempty") ).
                setOptions( new ChildVClassesWithParent(
                        presentationClass))
                );

        conf.addField( new FieldVTwo().
                setName("roleLabel").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));

        conf.addField( new FieldVTwo(). // an autocomplete field
                setName("existingConference")
                );

        conf.addField( new FieldVTwo().
                setName("conferenceLabel").
                setRangeDatatypeUri(XSD.xstring.toString() )
                );

        conf.addField( new FieldVTwo().
                setName("conferenceLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() )
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
        conf.addValidator(new AutocompleteRequiredInputValidator("existingPresentation", "presentationLabel"));
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */
    final static String n3ForNewRole =
        "@prefix core: <" + vivoCore + "> . \n" +
        "?person <" + hasRolePred + ">  ?role . \n" +
        "?role a  <" + roleClass + "> . \n" +
        "?role <" + roleOfPred + "> ?person . ";

	final static String n3ForRoleLabelAssertion =
		"?role <" + label + "> ?roleLabel . ";

    final static String n3ForNewPresentation =
        "?role <" + roleRealizedInPred + "> ?presentation . \n" +
        "?presentation <" + realizedRolePred + "> ?role . \n" +
        "?presentation <" + label + "> ?presentationLabel . \n" +
        "?presentation a ?presentationType .";

    final static String n3ForExistingPresentation =
        "?role <" + roleRealizedInPred + "> ?existingPresentation . \n" +
        "?existingPresentation <" + realizedRolePred + "> ?role . \n" +
        "?existingPresentation a ?presentationType .";

    final static String n3ForNewConferenceNewPres =
        "?presentation <" + eventWithinPred + "> ?newConference . \n" +
        "?newConference <" + includesEventPred + "> ?presentation . \n" +
        "?newConference a <" +  conferenceClass + "> . \n" +
        "?newConference <" + label + "> ?conferenceLabel .";

    final static String n3ForNewConferenceExistingPres =
        "?existingPresentation <" + eventWithinPred + "> ?newConference . \n" +
        "?newConference <" + includesEventPred + "> ?existingPresentation . \n" +
        "?newConference a <" +  conferenceClass + "> . \n" +
        "?newConference <" + label + "> ?conferenceLabel .";

    final static String n3ForExistingConferenceNewPres =
        "?existingConference <" + includesEventPred + "> ?presentation . \n" +
        "?presentation <" + eventWithinPred + "> ?existingConference . \n" +
        "?presentation <" + label + "> ?presentationLabel . ";

    final static String n3ForExistingConferenceExistingPres =
        "?existingConference <" + includesEventPred + "> ?existingPresentation . \n" +
        "?existingPresentation <" + eventWithinPred + "> ?existingConference . ";

    final static String n3ForStart =
        "?role <" + roleToInterval + "> ?intervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "?startNode a <" + dateTimeValueType + "> . \n" +
        "?startNode  <" + dateTimeValue + "> ?startField-value . \n" +
        "?startNode  <" + dateTimePrecision + "> ?startField-precision . \n";

    final static String n3ForEnd =
        "?role <" + roleToInterval + "> ?intervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n" +
        "?endNode  <" + dateTimeValue + "> ?endField-value . \n" +
        "?endNode  <" + dateTimePrecision + "> ?endField-precision . \n";

    /* Queries for editing an existing entry */
    final static String roleLabelQuery =
        "SELECT ?existingRoleLabel WHERE { \n" +
        "?role <" + label + "> ?existingRoleLabel . }";

    final static String presentationQuery =
        "SELECT ?existingPresentation WHERE { \n" +
        "?role <" + roleRealizedInPred + "> ?existingPresentation . }";

    final static String presentationLabelQuery =
        "SELECT ?existingPresentationLabel WHERE { \n" +
        "?role <" + roleRealizedInPred + "> ?existingPresentation . " +
        "?existingPresentation <" + label + "> ?existingPresentationLabel . }";

    final static String presentationTypeQuery =
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?existingPresentationType WHERE { \n" +
        "?role <" + roleRealizedInPred + "> ?existingPresentation . " +
        "?existingPresentation vitro:mostSpecificType ?existingPresentationType . }";

    final static String existingConferenceQuery =
        "SELECT ?existingConference WHERE { \n" +
        "?role <" + roleRealizedInPred + "> ?existingPresentation . " +
        "?existingPresentation <" + eventWithinPred + "> ?existingConference . }";

    final static String conferenceLabelQuery =
        "SELECT ?existingConferenceLabel WHERE { \n" +
        "?role <" + roleRealizedInPred + "> ?existingPresentation . " +
        "?existingPresentation <" + eventWithinPred + "> ?existingConference . \n" +
        "?existingConference <" + label + "> ?existingConferenceLabel . }";

    final static String existingStartDateQuery =
        "SELECT ?existingDateStart WHERE { \n" +
        "  ?role <" + roleToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a <" + dateTimeValueType +"> . \n" +
        "  ?startNode <" + dateTimeValue + "> ?existingDateStart . }";

    final static String existingEndDateQuery =
        "SELECT ?existingEndDate WHERE { \n" +
        "  ?role <" + roleToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n " +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimeValue + "> ?existingEndDate . }";

    final static String existingIntervalNodeQuery =
        "SELECT ?existingIntervalNode WHERE { \n" +
        "  ?role <" + roleToInterval + "> ?existingIntervalNode . \n" +
        "  ?existingIntervalNode a <" + intervalType + "> . }";

    final static String existingStartNodeQuery =
        "SELECT ?existingStartNode WHERE { \n" +
        "  ?role <" + roleToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?existingStartNode . \n" +
        "  ?existingStartNode a <" + dateTimeValueType + "> . }   ";

    final static String existingEndNodeQuery =
        "SELECT ?existingEndNode WHERE { \n" +
        "  ?role <" + roleToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?existingEndNode . \n" +
        "  ?existingEndNode a <" + dateTimeValueType + "> .} ";

    final static String existingStartPrecisionQuery =
        "SELECT ?existingStartPrecision WHERE { \n" +
        "  ?role <" + roleToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a  <" + dateTimeValueType + "> . \n" +
        "  ?startNode <" + dateTimePrecision + "> ?existingStartPrecision . }";

    final static String existingEndPrecisionQuery =
        "SELECT ?existingEndPrecision WHERE { \n" +
        "  ?role <" + roleToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimePrecision + "> ?existingEndPrecision . }";

}

