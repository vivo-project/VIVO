/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

public class AddFullNameToPersonGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {
    private Log log = LogFactory.getLog(AddFullNameToPersonGenerator.class);

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);
        String fullNameUri = getFullNameUri(vreq);

        conf.setTemplate("addFullNameToPerson.ftl");

        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("individualVcard");

        conf.setN3Required( Arrays.asList( n3ForNewName ) );
        conf.setN3Optional( Arrays.asList( firstNameAssertion, middleNameAssertion, lastNameAssertion, suffixAssertion, prefixAssertion ) );

        conf.addNewResource("fullName", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("individualVcard", DEFAULT_NS_FOR_NEW_RESOURCE);

        conf.setLiteralsOnForm(Arrays.asList("firstName", "middleName", "lastName", "suffix", "prefix" ));

        conf.addSparqlForExistingLiteral("firstName", firstNameQuery);
        conf.addSparqlForExistingLiteral("middleName", middleNameQuery);
        conf.addSparqlForExistingLiteral("lastName", lastNameQuery);
        conf.addSparqlForExistingLiteral("suffix", suffixQuery);
        conf.addSparqlForExistingLiteral("prefix", prefixQuery);
        conf.addSparqlForAdditionalUrisInScope("individualVcard", individualVcardQuery);

        if ( conf.isUpdate() ) {
            HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
            urisInScope.put("fullName", Arrays.asList(new String[]{fullNameUri}));
            conf.addUrisInScope(urisInScope);
        }

        conf.addField( new FieldVTwo().
                setName("firstName")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addField( new FieldVTwo().
                setName("middleName")
                .setRangeDatatypeUri( XSD.xstring.toString()) );

        conf.addField( new FieldVTwo().
                setName("lastName")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addField( new FieldVTwo().
                setName("suffix")
                .setRangeDatatypeUri( XSD.xstring.toString()) );

            conf.addField( new FieldVTwo().
                setName("prefix")
                .setRangeDatatypeUri( XSD.xstring.toString()) );

        conf.addValidator(new AntiXssValidation());

        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewName =
        "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?individualVcard . \n" +
        "?individualVcard a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?individualVcard <http://purl.obolibrary.org/obo/ARG_2000029> ?person . \n" +
        "?individualVcard <http://www.w3.org/2006/vcard/ns#hasName> ?fullName . \n" +
        "?fullName a <http://www.w3.org/2006/vcard/ns#Name> . " ;

    final static String firstNameAssertion  =
        "?fullName <http://www.w3.org/2006/vcard/ns#givenName> ?firstName .";

    final static String middleNameAssertion  =
        "?fullName <http://vivoweb.org/ontology/core#middleName> ?middleName .";

    final static String lastNameAssertion  =
        "?fullName <http://www.w3.org/2006/vcard/ns#familyName> ?lastName .";

    final static String suffixAssertion  =
        "?fullName <http://www.w3.org/2006/vcard/ns#honorificSuffix> ?suffix .";

    final static String prefixAssertion  =
        "?fullName <http://www.w3.org/2006/vcard/ns#honorificPrefix> ?prefix .";

    /* Queries for editing an existing entry */

    final static String individualVcardQuery =
        "SELECT ?existingIndividualVcard WHERE { \n" +
        "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?existingIndividualVcard . \n" +
        "}";

    final static String firstNameQuery  =
        "SELECT ?existingFirstName WHERE {\n"+
        "?fullName <http://www.w3.org/2006/vcard/ns#givenName> ?existingFirstName . }";

    final static String middleNameQuery  =
        "SELECT ?existingMiddleName WHERE {\n"+
        "?fullName <http://vivoweb.org/ontology/core#middleName> ?existingMiddleName . }";

    final static String lastNameQuery  =
        "SELECT ?existingLastName WHERE {\n"+
        "?fullName <http://www.w3.org/2006/vcard/ns#familyName> ?existingLastName . }";

    final static String suffixQuery  =
        "SELECT ?existingSuffix WHERE {\n"+
        "?fullName <http://www.w3.org/2006/vcard/ns#honorificSuffix> ?existingSuffix . }";

    final static String prefixQuery  =
        "SELECT ?existingPrefix WHERE {\n"+
        "?fullName <http://www.w3.org/2006/vcard/ns#honorificPrefix> ?existingPrefix . }";

	private String getFullNameUri(VitroRequest vreq) {
        String fullNameUri = vreq.getParameter("fullNameUri");

		return fullNameUri;
	}
}
