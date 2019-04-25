/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.AutocompleteRequiredInputValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

public class GrantAdministeredByGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    public GrantAdministeredByGenerator() {}

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);

        conf.setTemplate("grantAdministeredBy.ftl");

        conf.setVarNameForSubject("grant");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("adminRole");

        conf.setN3Required( Arrays.asList( n3ForNewAdminRole) );
        conf.setN3Optional( Arrays.asList( n3ForNewAdminOrganization,
                                           n3ForExistingAdminOrganization ) );

        conf.addNewResource("newOrganization", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("adminRole", DEFAULT_NS_FOR_NEW_RESOURCE);

        conf.setUrisOnform(Arrays.asList("existingOrganization"));
        conf.setLiteralsOnForm(Arrays.asList("orgLabel", "orgLabelDisplay" ));

        conf.addSparqlForExistingLiteral("orgLabel", orgLabelQuery);
        conf.addSparqlForExistingUris("existingOrganization", existingOrganizationQuery);


        conf.addField( new FieldVTwo(). // options will be added in browser by auto complete JS
                setName("existingOrganization")
        );

        conf.addField( new FieldVTwo().
                setName("orgLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("orgLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addValidator(new AntiXssValidation());
        conf.addValidator(new AutocompleteRequiredInputValidator("existingOrganization", "orgLabel"));

//        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewAdminRole =
        "@prefix vivo: <" + vivoCore + "> . \n" +
        "?grant vivo:relates  ?adminRole . \n" +
        "?adminRole a  vivo:AdministratorRole . \n" +
        "?adminRole vivo:relatedBy ?grant . " ;

    final static String n3ForNewAdminOrganization  =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?adminRole <http://purl.obolibrary.org/obo/RO_0000052> ?newOrganization . \n" +
        "?newOrganization a <http://xmlns.com/foaf/0.1/Organization>  . \n" +
        "?newOrganization <http://purl.obolibrary.org/obo/RO_0000053> ?adminRole . \n" +
        "?newOrganization vivo:relatedBy ?grant . \n" +
        "?grant vivo:relates ?newOrganization . \n" +
        "?newOrganization <"+ label + "> ?orgLabel .";

    final static String n3ForExistingAdminOrganization  =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?adminRole <http://purl.obolibrary.org/obo/RO_0000052> ?existingOrganization . \n" +
        "?existingOrganization a <http://xmlns.com/foaf/0.1/Organization>  . \n" +
        "?existingOrganization <http://purl.obolibrary.org/obo/RO_0000053> ?adminRole . " +
        "?existingOrganization vivo:relatedBy ?grant . \n" +
        "?grant vivo:relates ?existingOrganization . \n" ;

    /* Queries for editing an existing entry */

    final static String existingOrganizationQuery =
        "PREFIX vivo: <http://vivoweb.org/ontology/core#>  \n" +
        "SELECT ?existingOrganization WHERE { \n" +
        " ?adminRole <http://purl.obolibrary.org/obo/RO_0000052> ?existingOrganization . \n" +
        " ?existingOrganization a <http://xmlns.com/foaf/0.1/Organization> . \n" +
        "}";

    final static String orgLabelQuery =
        "PREFIX vivo: <http://vivoweb.org/ontology/core#> \n" +
        "SELECT ?existingOrganizationLabel WHERE { \n" +
        " ?adminRole <http://purl.obolibrary.org/obo/RO_0000052> ?existingOrganization . \n" +
        " ?existingOrganization a <http://xmlns.com/foaf/0.1/Organization> . \n" +
        " ?existingOrganization <" + label + "> ?existingOrganizationLabel . \n" +
        "}";

}
