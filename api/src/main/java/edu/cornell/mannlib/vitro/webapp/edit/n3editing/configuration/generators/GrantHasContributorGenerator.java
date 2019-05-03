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
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.FirstAndLastNameValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesWithParent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

public class GrantHasContributorGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator{

	// NOTE:  This generator is for contract as well as grants.
    //TODO: can we get rid of the session and get it form the vreq?
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);

        conf.setTemplate("grantHasContributor.ftl");

        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("theRole");

        conf.setN3Required( Arrays.asList( n3ForNewProjectRole, roleTypeAssertion ) );
        conf.setN3Optional(Arrays.asList( n3ForNewPerson, n3ForExistingPerson, firstNameAssertion, lastNameAssertion ) );

        conf.addNewResource("theRole", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("newPerson",DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("vcardPerson", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("vcardName", DEFAULT_NS_FOR_NEW_RESOURCE);

        //uris in scope: none
        //literals in scope: none

        conf.setUrisOnform( Arrays.asList( "existingPerson", "roleType"));
        conf.setLiteralsOnForm( Arrays.asList("personLabel", "personLabelDisplay", "firstName", "lastName"));

        conf.addSparqlForExistingLiteral("personLabel", personLabelQuery);

        conf.addSparqlForExistingUris("existingPerson", existingPersonQuery);
		conf.addSparqlForExistingUris("roleType", roleTypeQuery);

        conf.addField( new FieldVTwo().
                setName("existingPerson")
                //options will be added in browser by auto complete JS
                );

        conf.addField( new FieldVTwo().
                setName("personLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString())));

        conf.addField( new FieldVTwo().
                setName("personLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ));

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
	            setName("roleType").
	            setValidators( list("nonempty") ).
	               setOptions(
	                        new ChildVClassesWithParent("http://vivoweb.org/ontology/core#ResearcherRole")));


        //Add validator
        conf.addValidator(new AntiXssValidation());
        conf.addValidator(new FirstAndLastNameValidator("existingPerson"));

        //Adding additional data, specifically edit mode
        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions for working with educational training */

    final static String n3ForNewProjectRole =
        "@prefix core: <"+ vivoCore +"> .\n" +
		"@prefix rdfs: <"+ rdfs +">  . \n"+
        "?subject core:relates  ?theRole .\n" +
        "?theRole core:relatedBy ?subject . \n" ;

	final static String roleTypeAssertion =
	    "?theRole a ?roleType .";

    final static String n3ForNewPerson  =
        "@prefix core: <"+ vivoCore +"> .\n" +
        "?theRole <http://purl.obolibrary.org/obo/RO_0000052> ?newPerson . \n" +
        "?newPerson <http://purl.obolibrary.org/obo/RO_0000053> ?theRole . \n" +
        "?subject core:relates ?newPerson . \n" +
        "?newPerson core:relatedBy ?subject . \n" +
        "?newPerson a <http://xmlns.com/foaf/0.1/Person> . \n" +
        "?newPerson <"+ label +"> ?personLabel . ";

    final static String n3ForExistingPerson  =
        "@prefix core: <"+ vivoCore +"> .\n" +
        "?theRole <http://purl.obolibrary.org/obo/RO_0000052> ?existingPerson . \n" +
        "?existingPerson <http://purl.obolibrary.org/obo/RO_0000053> ?theRole . \n" +
        "?subject core:relates ?newPerson . \n" +
        "?newPerson core:relatedBy ?subject . \n" +
        " ";

    final static String firstNameAssertion  =
        "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
        "?newPerson <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardPerson . \n" +
        "?vcardPerson <http://purl.obolibrary.org/obo/ARG_2000029>  ?newPerson . \n" +
        "?vcardPerson a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?vcardPerson vcard:hasName  ?vcardName . \n" +
        "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n" +
        "?vcardName vcard:givenName ?firstName .";

    final static String lastNameAssertion  =
        "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
        "?newPerson <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardPerson . \n" +
        "?vcardPerson <http://purl.obolibrary.org/obo/ARG_2000029>  ?newPerson . \n" +
        "?vcardPerson a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?vcardPerson vcard:hasName  ?vcardName . \n" +
        "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n" +
        "?vcardName vcard:familyName ?lastName .";

    /* Queries for editing an existing educational training entry */

    final static String roleTypeQuery =
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?roleType WHERE { \n" +
        "  ?theRole vitro:mostSpecificType ?roleType . }";

    final static String existingPersonQuery  =
        "PREFIX rdfs: <"+ rdfs +">   \n"+
        "SELECT ?existingPerson WHERE {\n"+
        "?theRole <http://purl.obolibrary.org/obo/RO_0000052> ?existingPerson . \n" +
        "?existingPerson <http://purl.obolibrary.org/obo/RO_0000053> ?theRole . \n" +
        "?existingPerson a <http://xmlns.com/foaf/0.1/Person> . \n " +
        " }";

    final static String personLabelQuery  =
        "PREFIX rdfs: <"+ rdfs +">   \n"+
        "SELECT ?existingPersonLabel WHERE {\n"+
        "?theRole <http://purl.obolibrary.org/obo/RO_0000052> ?existingPerson . \n" +
        "?existingPerson <http://purl.obolibrary.org/obo/RO_0000053> ?theRole .\n"+
        "?existingPerson <"+ label +"> ?existingPersonLabel .\n"+
        "?existingPerson a <http://xmlns.com/foaf/0.1/Person> . \n " +
        " }";


  //Adding form specific data such as edit mode
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		editConfiguration.setFormSpecificData(formSpecificData);
	}

	public EditMode getEditMode(VitroRequest vreq) {
		List<String> predicates = new ArrayList<String>();
		predicates.add("http://purl.obolibrary.org/obo/RO_0000053");
		return EditModeUtils.getEditMode(vreq, predicates);
	}
}
