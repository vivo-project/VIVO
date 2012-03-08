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
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasAdviseesValidator;

public class PersonHasAdvisingRelationshipGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    final static String advisingRelClass = vivoCore + "AdvisingRelationship";
    final static String subjAreaClass = "http://www.w3.org/2004/02/skos/core#Concept";
    final static String degreeClass = vivoCore+"AcademicDegree";
    final static String adviseeClass = foaf + "Person";
    final static String advisorInPred = vivoCore + "advisorIn";
    final static String adviseeInPred = vivoCore + "adviseeIn";
    final static String advisorPred = vivoCore + "advisor";
    final static String adviseePred = vivoCore + "advisee" ;
    final static String subjAreaPred = vivoCore + "hasSubjectArea" ;
    final static String degreePred = vivoCore + "degreeCandidacy" ;
    final static String advisingRelToInterval = vivoCore + "dateTimeInterval";
    final static String intervalType = vivoCore + "DateTimeInterval";
    final static String intervalToStart = vivoCore + "start";
    final static String intervalToEnd = vivoCore + "end";
    final static String dateTimeValueType = vivoCore + "DateTimeValue";
    final static String dateTimeValue = vivoCore + "dateTime";
    final static String dateTimePrecision = vivoCore + "dateTimePrecision";
    
    public PersonHasAdvisingRelationshipGenerator() {}
    
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) {
        
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
        
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("personHasAdvisingRelationship.ftl");
        
        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("advisingRelationship");
        
        conf.setN3Required( Arrays.asList( n3ForNewAdvisingRelationship,
                                           advisingRelLabelAssertion,
                                           advisingRelTypeAssertion  ) );
        conf.setN3Optional( Arrays.asList( n3ForAdviseeAssertion,
                                           adviseeLabelAssertion,
                                           degreeAssertion,
                                           firstNameAssertion,
                                           lastNameAssertion,
                                           n3ForSubjAreaAssertion + "\n" + subjAreaLabelAssertion, //putting these statements together to prevent an empty label from generating a new URI
                                           n3ForStart, 
                                           n3ForEnd ) );
        
        conf.addNewResource("advisingRelationship", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("advisee", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("subjArea", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        //uris in scope: none   
        //literals in scope: none
        
        conf.setUrisOnform(Arrays.asList("advisingRelType", "subjArea", "degree", "advisee"));
        conf.setLiteralsOnForm(Arrays.asList("advisingRelLabel", "subjAreaLabel", "adviseeLabel", "firstName", "lastName" ));
        
        conf.addSparqlForExistingLiteral("advisingRelLabel", advisingRelLabelQuery);
        conf.addSparqlForExistingLiteral("adviseeLabel", adviseeLabelQuery);
// may not need these two because in edit mode the display will be an acSelection div
//        conf.addSparqlForExistingLiteral("firstName", firstNameQuery);
//        conf.addSparqlForExistingLiteral("lastName", lastNameQuery);
        conf.addSparqlForExistingLiteral("subjAreaLabel", subjAreaLabelQuery);
        conf.addSparqlForExistingLiteral("startField-value", existingStartDateQuery);
        conf.addSparqlForExistingLiteral("endField-value", existingEndDateQuery);
        
        conf.addSparqlForExistingUris("advisingRelType", advisingRelTypeQuery);
        conf.addSparqlForExistingUris("subjArea", subjAreaQuery);
        conf.addSparqlForExistingUris("advisee", adviseeQuery);
        conf.addSparqlForExistingUris("degree", degreeQuery);
        conf.addSparqlForExistingUris("intervalNode",existingIntervalNodeQuery);
        conf.addSparqlForExistingUris("startNode", existingStartNodeQuery);
        conf.addSparqlForExistingUris("endNode", existingEndNodeQuery);
        conf.addSparqlForExistingUris("startField-precision", existingStartPrecisionQuery);
        conf.addSparqlForExistingUris("endField-precision", existingEndPrecisionQuery);
        
        conf.addField( new FieldVTwo().                        
                setName("advisingRelType").
                setOptionsType(FieldVTwo.OptionsType.CHILD_VCLASSES_WITH_PARENT).
                setObjectClassUri(advisingRelClass).
                setValidators( list("nonempty") )
                );

        conf.addField( new FieldVTwo().
                setName("advisingRelLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("firstName").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("lastName").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("subjArea").
                setOptionsType(FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS).
                setObjectClassUri( subjAreaClass )
                );

        conf.addField( new FieldVTwo().
                setName("subjAreaLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("degree").
                setOptionsType( FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS ).
                setObjectClassUri( degreeClass )
                );

        conf.addField( new FieldVTwo().
                setName("advisee").
                setOptionsType(FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS).
                setObjectClassUri( adviseeClass ) 
                );

        conf.addField( new FieldVTwo().
                setName("adviseeLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
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
        conf.addValidator(new PersonHasAdviseesValidator());
        addFormSpecificData(conf, vreq);
        
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewAdvisingRelationship = 
        "@prefix vivo: <" + vivoCore + "> . \n\n" +   
        "?person <" + advisorInPred + ">  ?advisingRelationship . \n" +
        "?advisingRelationship a  <" + advisingRelClass + "> . \n" +              
        "?advisingRelationship <" + advisorPred + "> ?person . \n" ;    
    
    final static String advisingRelLabelAssertion  =      
        "?advisingRelationship <"+ label + "> ?advisingRelLabel .";
    
    final static String advisingRelTypeAssertion  =      
        "?advisingRelationship a ?advisingRelType .";

    final static String n3ForAdviseeAssertion  =      
        "?advisingRelationship <" + adviseePred + "> ?advisee . \n" +
        "?advisee <" + adviseeInPred + "> ?advisingRelationship . ";
    
    final static String adviseeLabelAssertion  =      
        "?advisee <" + label + "> ?adviseeLabel .";
    
    final static String firstNameAssertion  =      
        "@prefix foaf: <" + foaf + "> .  \n" +
        "?advisee foaf:firstName ?firstName .";
    
    final static String lastNameAssertion  =      
        "@prefix foaf: <" + foaf + "> .  \n" +
        "?advisee foaf:lastName ?lastName .";
    
    final static String degreeAssertion  =      
        "?advisingRelationship <"+ degreePred +"> ?degree .";

    final static String n3ForSubjAreaAssertion  =      
        "?advisingRelationship <"+ subjAreaPred +"> ?subjArea .\n" +
        "?subjArea a <" + subjAreaClass + "> . ";    
    
    final static String subjAreaLabelAssertion  =      
        "?subjArea <"+ label + "> ?subjAreaLabel . ";    

    final static String n3ForStart =
        "?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +    
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?startNode . \n" +    
        "?startNode a <" + dateTimeValueType + "> . \n" +
        "?startNode  <" + dateTimeValue + "> ?startField-value . \n" +
        "?startNode  <" + dateTimePrecision + "> ?startField-precision . \n";
    
    final static String n3ForEnd =
        "?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +    
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n" +
        "?endNode  <" + dateTimeValue + "> ?endField-value . \n" +
        "?endNode  <" + dateTimePrecision + "> ?endField-precision . \n";

    /* Queries for editing an existing entry */

    final static String advisingRelTypeQuery =
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?advisingRelType WHERE { \n" + 
        "  ?advisingRelationship vitro:mostSpecificType ?advisingRelType . \n" + 
        "}";

    final static String adviseeQuery  =      
        "SELECT ?existingAdvisee WHERE { \n" +
        " ?advisingRelationship <" + adviseePred + "> ?existingAdvisee . \n" +
        "}";

    final static String adviseeLabelQuery =
        "SELECT ?existingAdviseeLabel WHERE { \n" +
        " ?advisingRelationship <" + adviseePred + "> ?existingAdvisee . \n" +
        " ?existingAdvisee <"  + label + "> ?existingAdviseeLabel . \n" +
        "}";

/*  May not need these
     final static String firstNameQuery =
        "prefix foaf: <" + foaf + ">   \n" +
        "SELECT ?existingFirstName WHERE { \n" +
        " ?advisingRelationship <" + adviseePred + "> ?existingAdvisee . \n" +
        " ?existingAdvisee foaf:firstName ?existingFirstName . \n" +
        "}";

    final static String lastNameQuery =
        "prefix foaf: <" + foaf + ">   \n" +
        "SELECT ?existingLastName WHERE { \n" +
        " ?advisingRelationship <" + adviseePred + "> ?existingAdvisee . \n" +
        " ?existingAdvisee foaf:lastName ?existingLastName . \n" +
        "}";
*/
    final static String subjAreaQuery =
        "SELECT ?existingSubjArea WHERE { \n" +
        " ?advisingRelationship <" + subjAreaPred + "> ?existingSubjArea . \n" +
        "}";

    final static String subjAreaLabelQuery  =      
        "SELECT ?existingSubjAreaLabel WHERE { \n" +
        " ?advisingRelationship <" + subjAreaPred + "> ?existingSubjArea . \n" +
        " ?existingSubjArea <" + label + "> ?existingSubjAreaLabel . \n" +
        "}";

    final static String advisingRelLabelQuery =
        "SELECT ?existingAdvisingRelLabel WHERE { \n" +
        " ?advisingRelationship <"  + label + "> ?existingAdvisingRelLabel . \n" +
        "}";

    final static String degreeQuery  =  
        "SELECT ?existingDegree WHERE {\n"+
        " ?advisingRelationship <"+ degreePred +"> ?existingDegree . }";

    final static String existingStartDateQuery =
        "SELECT ?existingDateStart WHERE { \n" +
        "  ?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a <" + dateTimeValueType +"> . \n" +
        "  ?startNode <" + dateTimeValue + "> ?existingDateStart . }";
    
    final static String existingEndDateQuery =
        "SELECT ?existingEndDate WHERE { \n" +
        "  ?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n " +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +
        "  ?endNode <" + dateTimeValue + "> ?existingEndDate . }";

    final static String existingIntervalNodeQuery =
        "SELECT ?existingIntervalNode WHERE { \n" + 
        "  ?advisingRelationship <" + advisingRelToInterval + "> ?existingIntervalNode . \n" +
        "  ?existingIntervalNode a <" + intervalType + "> . }";

    final static String existingStartNodeQuery = 
        "SELECT ?existingStartNode WHERE { \n" +
        "  ?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?existingStartNode . \n" + 
        "  ?existingStartNode a <" + dateTimeValueType + "> .}   ";

    final static String existingEndNodeQuery = 
        "SELECT ?existingEndNode WHERE { \n" +
        "  ?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?existingEndNode . \n" + 
        "  ?existingEndNode a <" + dateTimeValueType + "> } ";              

    final static String existingStartPrecisionQuery = 
        "SELECT ?existingStartPrecision WHERE { \n" +
        "  ?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "  ?startNode a  <" + dateTimeValueType + "> . \n" +           
        "  ?startNode <" + dateTimePrecision + "> ?existingStartPrecision . }";

    final static String existingEndPrecisionQuery = 
        "SELECT ?existingEndPrecision WHERE { \n" +
        "  ?advisingRelationship <" + advisingRelToInterval + "> ?intervalNode . \n" +
        "  ?intervalNode a <" + intervalType + "> . \n" +
        "  ?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "  ?endNode a <" + dateTimeValueType + "> . \n" +          
        "  ?endNode <" + dateTimePrecision + "> ?existingEndPrecision . }";

	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("sparqlForAcFilter", getSparqlForAcFilter(vreq));
		editConfiguration.setFormSpecificData(formSpecificData);
	}

	public String getSparqlForAcFilter(VitroRequest vreq) {
		String subject = EditConfigurationUtils.getSubjectUri(vreq);			
		String predicate = EditConfigurationUtils.getPredicateUri(vreq);
		//Get all objects for existing predicate, filters out results from addition and edit
		String query =  "SELECT ?objectVar WHERE { " + 
			"<" + subject + "> <" + predicate + "> ?objectVar .} ";
		return query;
	}

}
