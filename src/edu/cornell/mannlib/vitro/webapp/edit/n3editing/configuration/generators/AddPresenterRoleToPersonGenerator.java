/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

public class AddPresenterRoleToPersonGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    final static String presentationClass = vivoCore + "Presentation";
    final static String roleClass = vivoCore + "PresenterRole";
    final static String conferenceClass = bibo + "Conference";
    final static String hasRolePred = vivoCore + "hasPresenterRole";
    final static String roleOfPred = vivoCore + "presenterRoleOf";
    final static String roleRealizedInPred = vivoCore + "roleRealizedIn";
    final static String realizedRolePred = vivoCore + "realizedRole";
    final static String includesEventPred = vivoCore + "includesEvent";
    final static String eventWithinPred = vivoCore + "eventWithin";
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
            HttpSession session) {
        
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
        
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("addPresenterRoleToPerson.ftl");
        
        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("role");
        
        conf.setN3Required( Arrays.asList( n3ForNewRole, 
                                           roleLabelAssertion, 
                                           presTypeAssertion, 
                                           presLabelAssertion ) );
        conf.setN3Optional( Arrays.asList( n3ForConference, n3ForStart, n3ForEnd ) );
        
        conf.addNewResource("presentation", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("conference", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("role", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        //uris in scope: none   
        //literals in scope: none
        
        conf.setUrisOnform(Arrays.asList("presentation", "conference", "role", "presentationType"));
        conf.setLiteralsOnForm(Arrays.asList("presentationLabel", "conferenceLabel", "roleLabel"));
        
        conf.addSparqlForExistingLiteral("presentationLabel", presentationLabelQuery);
        conf.addSparqlForExistingLiteral("conferenceLabel", conferenceLabelQuery);
        conf.addSparqlForExistingLiteral("roleLabel", roleLabelQuery);
        conf.addSparqlForExistingUris("presentation", presentationQuery);
        conf.addSparqlForExistingUris("conference", conferenceQuery);
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
        
        conf.addField( new FieldVTwo().
                setName("presentation").
                setOptionsType(FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS).
                setObjectClassUri(presentationClass)
                );

        conf.addField( new FieldVTwo().                        
                setName("presentationLabel")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") )
                );
        
        conf.addField( new FieldVTwo().
                setName("presentationType").
                setOptionsType(FieldVTwo.OptionsType.CHILD_VCLASSES_WITH_PARENT).
                setObjectClassUri(presentationClass).
                setValidators( list("nonempty") )
                );
 
        conf.addField( new FieldVTwo().                        
                setName("roleLabel")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") )
                );

        conf.addField( new FieldVTwo().
                setName("conference").
                setOptionsType(FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS).
                setObjectClassUri(conferenceClass)
                );
        
        conf.addField( new FieldVTwo().
                setName("conferenceLabel").
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
        
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */
    final static String n3ForNewRole = 
        "@prefix core: <" + vivoCore + "> . \n\n" +   
        "?person <" + hasRolePred + ">  ?role . \n" +
        "?role a  <" + roleClass + "> . \n" +              
        "?role <" + roleOfPred + "> ?person . \n" +
        "?role <" + roleRealizedInPred + "> ?presentation . \n" + 
        "?presentation <" + realizedRolePred + "> ?role .";    
    
    final static String roleLabelAssertion =
        "?role <" + label + "> ?roleLabel .";
        
    final static String presLabelAssertion =
        "?presentation <" + label + "> ?presentationLabel .";
    
    final static String presTypeAssertion =
        "?presentation a ?presentationType .";
    
    final static String n3ForConference =
        "?conference a <" +  conferenceClass + "> . \n" +
        "?conference <" + includesEventPred + "> ?presentation . \n" +
        "?presentation <" + eventWithinPred + "> ?conference . \n" +
        "?conference <" + label + "> ?conferenceLabel .";
    
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
    
    final static String conferenceQuery = 
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

