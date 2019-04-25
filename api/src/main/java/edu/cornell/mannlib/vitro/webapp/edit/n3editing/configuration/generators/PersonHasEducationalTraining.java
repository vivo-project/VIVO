/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesWithParent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.IndividualsViaVClassOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

/**
    Form for adding an educational attainment to an individual

    Classes:
    core:EducationalProcess - primary new individual being created
    foaf:Person - existing individual
    foaf:Organization - new or existing individual
    core:AcademicDegree - existing individual
    core:AwardedDegree - new or existing individual



    There are 4 modes that this form can be in:
     1.  Add, there is a subject and a predicate but no position and nothing else.

     2. normal edit where everything should already be filled out.  There is a subject, a object and an individual on
        the other end of the object's relationship.

     3. Repair a bad role node.  There is a subject, prediate and object but there is no individual on the
        other end of the object's  relationship.  This should be similar to an add but the form should be expanded.

     4. Really bad node. multiple statements on the other end of the object's  relationship.

 * @author bdc34
 *
 */
public class PersonHasEducationalTraining  extends VivoBaseGenerator implements EditConfigurationGenerator{

    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);

        conf.setTemplate("personHasEducationalTraining.ftl");

        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("edTraining");

        conf.setN3Required( Arrays.asList( n3ForNewEdTraining, trainingTypeAssertion ) );
        conf.setN3Optional(Arrays.asList( majorFieldAssertion,  n3ForAwardedDegree, n3ForNewOrganization, n3ForExistingOrganization,
                n3ForNewOrgAwardedDegree, n3ForExistingOrgAwardedDegree, deptAssertion, infoAssertion, n3ForStart, n3ForEnd ));

        conf.addNewResource("edTraining", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("awardedDegree",DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("newOrg",DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("intervalNode",DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode",DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode",DEFAULT_NS_FOR_NEW_RESOURCE);

        //uris in scope: none
        //literals in scope: none

        conf.setUrisOnform( Arrays.asList( "existingOrg", "orgType", "degreeType", "trainingType"));
        conf.setLiteralsOnForm( Arrays.asList("orgLabel", "orgLabelDisplay", "awardedDegreeLabel",
                                              "majorField", "dept", "info"));

        conf.addSparqlForExistingLiteral("orgLabel", orgLabelQuery);
//        conf.addSparqlForExistingLiteral("existingAwardedDegreeLabel", existingAwardedDegreeLabelQuery);
        conf.addSparqlForExistingLiteral("majorField", majorFieldQuery);
        conf.addSparqlForExistingLiteral("dept", deptQuery);
        conf.addSparqlForExistingLiteral("info", infoQuery);
        conf.addSparqlForExistingLiteral("startField-value", existingStartDateQuery);
        conf.addSparqlForExistingLiteral("endField-value", existingEndDateQuery);


        conf.addSparqlForExistingUris("awardedDegree", existingAwardedDegreeQuery);
        conf.addSparqlForExistingUris("existingOrg", existingOrgQuery);
        conf.addSparqlForExistingUris("orgType", orgTypeQuery);
        conf.addSparqlForExistingUris("trainingType", trainingTypeQuery);
        conf.addSparqlForExistingUris("degreeType", degreeTypeQuery);
        conf.addSparqlForExistingUris("intervalNode",existingIntervalNodeQuery);
        conf.addSparqlForExistingUris("startNode", existingStartNodeQuery);
        conf.addSparqlForExistingUris("endNode", existingEndNodeQuery);
        conf.addSparqlForExistingUris("startField-precision", existingStartPrecisionQuery);
        conf.addSparqlForExistingUris("endField-precision", existingEndPrecisionQuery);
        //Add sparql to include inverse property as well
        conf.addSparqlForAdditionalUrisInScope("inverseTrainingAtOrg", inverseTrainingAtOrgQuery);

        conf.addField( new FieldVTwo().
                setName("degreeType").
                setOptions( new IndividualsViaVClassOptions(
                        degreeTypeClass)));

        conf.addField( new FieldVTwo().
                setName("majorField").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));

        conf.addField( new FieldVTwo().
                setName("existingOrg")
                //options will be added in browser by auto complete JS
                );

        conf.addField( new FieldVTwo().
                setName("awardedDegree")
                //options will be added in browser by auto complete JS
                );

        conf.addField( new FieldVTwo().
                setName("orgLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString())));

        conf.addField( new FieldVTwo().
                setName("awardedDegreeLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString())));

        conf.addField( new FieldVTwo().
                setName("existingAwardedDegreeLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString())));

        conf.addField( new FieldVTwo().
                setName("orgLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ));

        conf.addField( new FieldVTwo().
                setName("orgType").
                setValidators( list("nonempty")).
                setOptions( new ChildVClassesOptions(
                        orgClass)));

        conf.addField( new FieldVTwo().
                setName("trainingType").
                setValidators( list("nonempty") ).
                setOptions(
                        new ChildVClassesWithParent(edProcessClass)));

        conf.addField( new FieldVTwo().
                setName("dept").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));

        conf.addField( new FieldVTwo().
                setName("info").
                setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators(list("datatype:" + XSD.xstring.toString())));

        FieldVTwo startField = new FieldVTwo().
        						setName("startField");
        conf.addField(startField.
            setEditElement(
                    new DateTimeWithPrecisionVTwo(startField,
                    VitroVocabulary.Precision.YEAR.uri(),
                    VitroVocabulary.Precision.NONE.uri())));

        FieldVTwo endField = new FieldVTwo().
								setName("endField");
        conf.addField( endField.
                setEditElement(
                        new DateTimeWithPrecisionVTwo(endField,
                        VitroVocabulary.Precision.YEAR.uri(),
                        VitroVocabulary.Precision.NONE.uri())));
        //Add validator
        conf.addValidator(new DateTimeIntervalValidationVTwo("startField","endField"));
        conf.addValidator(new AntiXssValidation());

        //Adding additional data, specifically edit mode
        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions for working with educational training */

    final static String n3ForNewEdTraining =
        "@prefix core: <"+ vivoCore +"> .\n"+
        "?person <http://purl.obolibrary.org/obo/RO_0000056>  ?edTraining .\n" +
        "?edTraining  a core:EducationalProcess .\n" +
        "?edTraining <http://purl.obolibrary.org/obo/RO_0000057> ?person .";

    final static String trainingTypeAssertion =
        "?edTraining a ?trainingType .";

    final static String n3ForAwardedDegree  =
        "@prefix core: <"+ vivoCore +"> .\n"+
        "?edTraining <http://purl.obolibrary.org/obo/RO_0002234> ?awardedDegree . \n" +
        "?awardedDegree <http://purl.obolibrary.org/obo/RO_0002353> ?edTraining . \n" +
        "?awardedDegree <http://vivoweb.org/ontology/core#relates> ?person . \n" +
        "?person <http://vivoweb.org/ontology/core#relatedBy> ?awardedDegree . \n" +
        "?awardedDegree <"+ label +"> ?awardedDegreeLabel . \n" +
        "?awardedDegree <http://vivoweb.org/ontology/core#relates> ?degreeType .\n"+
        "?degreeType <http://vivoweb.org/ontology/core#relatedBy> ?awardedDegree . \n"+
        "?awardedDegree a core:AwardedDegree .";

    final static String n3ForNewOrganization  =
        "?edTraining <http://purl.obolibrary.org/obo/RO_0000057> ?newOrg . \n" +
        "?newOrg <http://purl.obolibrary.org/obo/RO_0000056> ?edTraining . \n" +
        "?newOrg a ?orgType . \n" +
        "?newOrg <"+ label +"> ?orgLabel . ";

    final static String n3ForExistingOrganization  =
        "?edTraining <http://purl.obolibrary.org/obo/RO_0000057> ?existingOrg . \n" +
        "?existingOrg <http://purl.obolibrary.org/obo/RO_0000056> ?edTraining . \n" +
        "?existingOrg a ?orgType . ";

    final static String n3ForNewOrgAwardedDegree  =
        "?awardedDegree <http://vivoweb.org/ontology/core#assignedBy> ?newOrg . \n" +
        "?newOrg <http://vivoweb.org/ontology/core#assigns> ?awardedDegree . \n" +
        "?newOrg a ?orgType . \n" +
        "?awardedDegree <"+ label +"> ?awardedDegreeLabel . \n" +
        "?newOrg <"+ label +"> ?orgLabel . ";

    final static String n3ForExistingOrgAwardedDegree  =
        "?awardedDegree <http://vivoweb.org/ontology/core#assignedBy> ?existingOrg . \n" +
        "?existingOrg <http://vivoweb.org/ontology/core#assigns> ?awardedDegree . \n" +
        "?awardedDegree <"+ label +"> ?awardedDegreeLabel . \n" +
        "?existingOrg a ?orgType . ";

    final static String majorFieldAssertion  =
        "?edTraining <"+ majorFieldPred +"> ?majorField .";

    final static String n3ForStart =
        "?edTraining      <"+ toInterval +"> ?intervalNode .\n"+
        "?intervalNode  <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToStart +"> ?startNode .\n"+
        "?startNode  <"+ type +"> <"+ dateTimeValueType +"> .\n"+
        "?startNode  <"+ dateTimeValue +"> ?startField-value .\n"+
        "?startNode  <"+ dateTimePrecision +"> ?startField-precision .";

    final static String n3ForEnd =
        "?edTraining      <"+ toInterval +"> ?intervalNode . \n"+
        "?intervalNode  <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToEnd +"> ?endNode .\n"+
        "?endNode  <"+ type +"> <"+ dateTimeValueType +"> .\n"+
        "?endNode  <"+ dateTimeValue +"> ?endField-value .\n"+
        "?endNode  <"+ dateTimePrecision +"> ?endField-precision .";

    final static String deptAssertion  =
        "?edTraining <"+ deptPred +"> ?dept .";

    final static String infoAssertion  =
        "?edTraining <"+ infoPred +"> ?info .";

    /* Queries for editing an existing educational training entry */

    final static String existingAwardedDegreeQuery =
        "SELECT ?existingAwardedDegree WHERE {\n"+
        "?edTraining <http://purl.obolibrary.org/obo/RO_0002234> ?existingAwardedDegree . }\n";

    final static String existingAwardedDegreeLabelQuery =
        "SELECT ?existingAwardedDegreeLabel WHERE {\n"+
        "?edTraining <http://purl.obolibrary.org/obo/RO_0002234> ?existingAwardedDegree . \n" +
        "?existingAwardedDegree <"+ label +"> ?existingAwardedDegreeLabel }\n";

    final static String existingOrgQuery  =
        "PREFIX rdfs: <"+ rdfs +">   \n"+
        "SELECT ?existingOrg WHERE {\n"+
        "?edTraining <http://purl.obolibrary.org/obo/RO_0000057> ?existingOrg . \n" +
        "?existingOrg <http://purl.obolibrary.org/obo/RO_0000056> ?edTraining . \n" +
        "?existingOrg a ?existingOrgType . \n " +
        "?existingOrgType rdfs:subClassOf <"+ orgClass +"> . }";

    final static String orgLabelQuery  =
        "PREFIX rdfs: <"+ rdfs +">   \n"+
        "SELECT ?existingOrgLabel WHERE {\n"+
        "?edTraining <http://purl.obolibrary.org/obo/RO_0000057> ?existingOrg . \n" +
        "?existingOrg <http://purl.obolibrary.org/obo/RO_0000056> ?edTraining .\n"+
        "?existingOrg <"+ label +"> ?existingOrgLabel .\n"+
        "?existingOrg a ?existingOrgType . \n " +
        "?existingOrgType rdfs:subClassOf <"+ orgClass +"> . }";

    /* Limit type to subclasses of foaf:Organization. Otherwise, sometimes owl:Thing or another
    type is returned and we don't get a match to the select element options. */
    final static String orgTypeQuery  =
        "PREFIX rdfs: <"+ rdfs +">   \n"+
        "SELECT ?existingOrgType WHERE {\n"+
        "?edTraining <http://purl.obolibrary.org/obo/RO_0000057> ?existingOrg . \n" +
        "?existingOrg <http://purl.obolibrary.org/obo/RO_0000056> ?edTraining .\n"+
        "?existingOrg a ?existingOrgType .\n"+
        "?existingOrgType rdfs:subClassOf <"+ orgClass +"> .\n"+
        "}";

    final static String trainingTypeQuery =
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?existingTrainingType WHERE { \n" +
        "  ?edTraining vitro:mostSpecificType ?existingTrainingType .  }";

    final static String degreeTypeQuery  =
        "PREFIX core: <"+ vivoCore +"> \n"+
        "SELECT ?existingDegreeType WHERE {\n"+
        "?edTraining <http://purl.obolibrary.org/obo/RO_0002234> ?existingAwardedDegree . \n"+
        "?existingAwardedDegree a core:AwardedDegree . \n"+
        "?existingAwardedDegree core:relates ?existingDegreeType .  \n" +
        "?existingDegreeType a core:AcademicDegree }";

    final static String majorFieldQuery  =
        "SELECT ?existingMajorField WHERE {\n"+
        "?edTraining <"+ majorFieldPred +"> ?existingMajorField . }";

    final static String deptQuery  =
        "SELECT ?existingDept WHERE {\n"+
        "?edTraining <"+ deptPred +"> ?existingDept . }";

    final static String infoQuery  =
        "SELECT ?existingInfo WHERE {\n"+
        "?edTraining <"+ infoPred +"> ?existingInfo . }";

    final static String existingIntervalNodeQuery  =
        "SELECT ?existingIntervalNode WHERE {\n"+
        "?edTraining <"+ toInterval +"> ?existingIntervalNode .\n"+
        "?existingIntervalNode <"+ type +"> <"+ intervalType +"> . }";

    final static String existingStartNodeQuery  =
        "SELECT ?existingStartNode WHERE {\n"+
        "?edTraining <"+ toInterval +"> ?intervalNode .\n"+
        "?intervalNode <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToStart +"> ?existingStartNode . \n"+
        "?existingStartNode <"+ type +"> <"+ dateTimeValueType +"> .}";

    final static String existingStartDateQuery  =
        "SELECT ?existingDateStart WHERE {\n"+
        "?edTraining <"+ toInterval +"> ?intervalNode .\n"+
        "?intervalNode <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToStart +"> ?startNode .\n"+
        "?startNode <"+ type +"> <"+ dateTimeValueType +"> .\n"+
        "?startNode <"+ dateTimeValue +"> ?existingDateStart . }";

    final static String existingStartPrecisionQuery  =
        "SELECT ?existingStartPrecision WHERE {\n"+
        "?edTraining <"+ toInterval +"> ?intervalNode .\n"+
        "?intervalNode <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToStart +"> ?startNode .\n"+
        "?startNode <"+ type +"> <"+ dateTimeValueType +"> . \n"+
        "?startNode <"+ dateTimePrecision +"> ?existingStartPrecision . }";

    final static String existingEndNodeQuery  =
        "SELECT ?existingEndNode WHERE { \n"+
        "?edTraining <"+ toInterval +"> ?intervalNode .\n"+
        "?intervalNode <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToEnd +"> ?existingEndNode . \n"+
        "?existingEndNode <"+ type +"> <"+ dateTimeValueType +"> .}";

    final static String existingEndDateQuery  =
        "SELECT ?existingEndDate WHERE {\n"+
        "?edTraining <"+ toInterval +"> ?intervalNode .\n"+
        "?intervalNode <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToEnd +"> ?endNode .\n"+
        "?endNode <"+ type +"> <"+ dateTimeValueType +"> .\n"+
        "?endNode <"+ dateTimeValue +"> ?existingEndDate . }";

    final static String existingEndPrecisionQuery  =
        "SELECT ?existingEndPrecision WHERE {\n"+
        "?edTraining <"+ toInterval +"> ?intervalNode .\n"+
        "?intervalNode <"+ type +"> <"+ intervalType +"> .\n"+
        "?intervalNode <"+ intervalToEnd +"> ?endNode .\n"+
        "?endNode <"+ type +"> <"+ dateTimeValueType +"> .\n"+
        "?endNode <"+ dateTimePrecision +"> ?existingEndPrecision . }";

    //Query for inverse property
    final static String inverseTrainingAtOrgQuery  =
    	  "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
			+ " SELECT ?inverseTrainingAtOrg "
			+ "    WHERE { ?inverseTrainingAtOrg owl:inverseOf <http://vivoweb.org/ontology/core#relates> . } ";


  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		editConfiguration.setFormSpecificData(formSpecificData);
	}

	public EditMode getEditMode(VitroRequest vreq) {
		List<String> predicates = new ArrayList<String>();
		predicates.add("http://vivoweb.org/ontology/core#relates");
		return EditModeUtils.getEditMode(vreq, predicates);
	}
}
