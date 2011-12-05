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

public class PersonHasPositionHistoryGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    final static String positionClass = vivoCore + "Position";
    final static String orgClass = "http://xmlns.com/foaf/0.1/Organization";
    final static String positionInOrgPred = vivoCore + "positionInOrganization";
    final static String orgForPositionPred = vivoCore + "organizationForPosition";
    final static String positionToInterval = vivoCore + "dateTimeInterval";
    final static String intervalType = vivoCore + "DateTimeInterval";
    final static String intervalToStart = vivoCore + "start";
    final static String intervalToEnd = vivoCore + "end";
    final static String dateTimeValueType = vivoCore + "DateTimeValue";
    final static String dateTimeValue = vivoCore + "dateTime";
    final static String dateTimePrecision = vivoCore + "dateTimePrecision";
    
    public PersonHasPositionHistoryGenerator() {}

    // There are 4 modes that this form can be in: 
    //  1. Add. There is a subject and a predicate but no position and 
    //     nothing else. 
    //        
    //  2. Normal edit where everything should already be filled out.  
    //     There is a subject, a object and an individual on
    //     the other end of the object's core:personInOrganization stmt. 
    //  
    //  3. Repair a bad role node.  There is a subject, predicate and object 
    //     but there is no individual on the other end of the object's 
    //     core:personInOrganization stmt.  This should be similar to an add 
    //     but the form should be expanded.
    //     
    //  4. Really bad node. multiple core:personInOrganization statements.   
    
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) {
        
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
        
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("personHasPositionHistory.ftl");
        
        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("position");
        
        conf.setN3Required( Arrays.asList( n3ForNewPosition, 
                                           positionTitleAssertion, 
                                           positionTypeAssertion, 
                                           orgLabelAssertion, 
                                           orgTypeAssertion ) );
        conf.setN3Optional( Arrays.asList( n3ForStart, n3ForEnd ) );
        
        conf.addNewResource("position", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("org", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        //uris in scope: none   
        //literals in scope: none
        
        conf.setUrisOnform(Arrays.asList("org", "orgType", "positionType"));
        conf.setLiteralsOnForm(Arrays.asList("positionTitle", "orgLabel"));
        
        conf.addSparqlForExistingLiteral("orgLabel", orgLabelQuery);
        conf.addSparqlForExistingLiteral("positionTitle", positionTitleQuery);
        conf.addSparqlForExistingLiteral(
                "startField-value", existingStartDateQuery);
        conf.addSparqlForExistingLiteral(
                "endField-value", existingEndDateQuery);
     
        conf.addSparqlForExistingUris("org", orgQuery);
        conf.addSparqlForExistingUris("orgType", orgTypeQuery);
        conf.addSparqlForExistingUris("positionType", positionTypeQuery);
        conf.addSparqlForExistingUris(
                "intervalNode", existingIntervalNodeQuery);
        conf.addSparqlForExistingUris("startNode", existingStartNodeQuery);
        conf.addSparqlForExistingUris("endNode", existingEndNodeQuery);
        conf.addSparqlForExistingUris("startField-precision", 
                existingStartPrecisionQuery);
        conf.addSparqlForExistingUris("endField-precision", 
                existingEndPrecisionQuery);
        
        conf.addField( new FieldVTwo().                        
                setName("positionTitle")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") )
                );
        
        conf.addField( new FieldVTwo().
                setName("positionType").
                setOptionsType(FieldVTwo.OptionsType.CHILD_VCLASSES_WITH_PARENT).
                setObjectClassUri(positionClass).
                setValidators( list("nonempty") )
                );
 
        conf.addField( new FieldVTwo().
                setName("org").
                setOptionsType(FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS).
                setObjectClassUri(orgClass)
                );
        
        conf.addField( new FieldVTwo().
                setName("orgLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("nonempty") )
                );
        
        conf.addField( new FieldVTwo().
                setName("orgType").
                setOptionsType(FieldVTwo.OptionsType.CHILD_VCLASSES).
                setObjectClassUri(orgClass)
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
        
        //Adding additional data, specifically edit mode
        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }

    final static String n3ForNewPosition = 
        "@prefix core: <" + vivoCore + "> . \n\n" +   
        "?person core:personInPosition  ?position . \n" +
        "?position a  ?positionType ; \n" +              
        "          core:positionForPerson ?person ; \n" +
        "          <" + positionInOrgPred + "> ?org . \n" + 
        "?org <" + orgForPositionPred + "> ?position .";    
    
    final static String positionTitleAssertion =
        "?position <" + RDFS.label.getURI() + "> ?positionTitle .";
    
    final static String positionTypeAssertion =
        "?position a ?positionType .";
    
    final static String orgLabelAssertion =
        "?org <" + RDFS.label.getURI() + "> ?orgLabel .";
    
    final static String orgTypeAssertion = 
        "?org a ?orgType .";
    
    final static String n3ForStart =
        "?position <" + positionToInterval + "> ?intervalNode . \n" +    
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?startNode . \n" +    
        "?startNode a <" + dateTimeValueType + "> . \n" +
        "?startNode  <" + dateTimeValue + "> ?startField-value . \n" +
        "?startNode  <" + dateTimePrecision + "> ?startField-precision . \n";
    
    final static String n3ForEnd =
        "?position <" + positionToInterval + "> ?intervalNode . \n" +    
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n" +
        "?endNode  <" + dateTimeValue + "> ?endField-value . \n" +
        "?endNode  <" + dateTimePrecision + "> ?endField-precision . \n";
    
    final static String orgLabelQuery =
        "SELECT ?existingOrgLabel WHERE { \n" +
        "  ?position <" + positionInOrgPred + "> ?existingOrg . \n" +
        "  ?existingOrg <" + RDFS.label.getURI() + "> ?existingOrgLabel . \n" +
        "}";
    
    final static String positionTitleQuery =
        "SELECT ?existingPositionTitle WHERE { \n" +
        "?position <" + RDFS.label.getURI() + "> ?existingPositionTitle . }";
    
    final static String existingStartDateQuery =
        "SELECT ?existingDateStart WHERE { \n" +
        "  ?position <" + positionToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a <" + dateTimeValueType +"> . \n" +
        "  ?startNode <" + dateTimeValue + "> ?existingDateStart . }";
    
    final static String existingEndDateQuery =
        "SELECT ?existingEndDate WHERE { \n" +
        "  ?position <" + positionToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n " +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimeValue + "> ?existingEndDate . }";

    final static String orgQuery = 
        "SELECT ?existingOrg WHERE { \n" +
        "  ?position <" + positionInOrgPred + "> ?existingOrg . }";
    
    final static String orgTypeQuery = 
        "PREFIX rdfs: <" + RDFS.getURI() + "> \n" +   
        "SELECT ?existingOrgType WHERE { \n" +
        "  ?position <" + positionInOrgPred + "> ?existingOrg . \n" +
        "  ?existingOrg a ?existingOrgType . \n" +
        "  ?existingOrgType rdfs:subClassOf <" + orgClass + "> " +
        "} ";
    
    //Huda: changed this from rdf:type to vitro:mostSpecificType since returning thing
    final static String positionTypeQuery = 
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?existingPositionType WHERE { \n" + 
        "  ?position vitro:mostSpecificType ?existingPositionType . }";
        
    final static String existingIntervalNodeQuery =
        "SELECT ?existingIntervalNode WHERE { \n" + 
        "  ?position <" + positionToInterval + "> ?existingIntervalNode . \n" +
        "  ?existingIntervalNode a <" + intervalType + "> . }";
    
    final static String existingStartNodeQuery = 
        "SELECT ?existingStartNode WHERE { \n" +
        "  ?position <" + positionToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?existingStartNode . \n" + 
        "  ?existingStartNode a <" + dateTimeValueType + "> .}   ";
    
    final static String existingEndNodeQuery = 
        "SELECT ?existingEndNode WHERE { \n" +
        "  ?position <" + positionToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?existingEndNode . \n" + 
        "  ?existingEndNode a <" + dateTimeValueType + "> } ";              
    
    final static String existingStartPrecisionQuery = 
        "SELECT ?existingStartPrecision WHERE { \n" +
        "  ?position <" + positionToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a  <" + dateTimeValueType + "> . \n" +           
        "  ?startNode <" + dateTimePrecision + "> ?existingStartPrecision . }";
    
    final static String existingEndPrecisionQuery = 
        "SELECT ?existingEndPrecision WHERE { \n" +
        "  ?position <" + positionToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +          
        "  ?endNode <" + dateTimePrecision + "> ?existingEndPrecision . }";
    
    //Adding form specific data such as edit mode
  	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
  		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
  		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
  		editConfiguration.setFormSpecificData(formSpecificData);
  	}

  	public EditMode getEditMode(VitroRequest vreq) {
  		List<String> predicates = new ArrayList<String>();
  		predicates.add(positionInOrgPred);
  		return EditModeUtils.getEditMode(vreq, predicates);
  	}

}
