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

public class OrganizationAdministersGrantGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    public OrganizationAdministersGrantGenerator() {}

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);

        conf.setTemplate("organizationAdministersGrant.ftl");

        conf.setVarNameForSubject("organization");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("adminRole");

        conf.setN3Required( Arrays.asList( n3ForNewAdminRole) );
        conf.setN3Optional( Arrays.asList( n3ForNewAdminGrant,
                                           n3ForExistingAdminGrant ) );

        conf.addNewResource("newGrant", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("adminRole", DEFAULT_NS_FOR_NEW_RESOURCE);

        conf.setUrisOnform(Arrays.asList("existingGrant"));
        conf.setLiteralsOnForm(Arrays.asList("grantLabel", "grantLabelDisplay" ));

        conf.addSparqlForExistingLiteral("grantLabel", grantLabelQuery);
        conf.addSparqlForExistingUris("existingGrant", existingGrantQuery);


        conf.addField( new FieldVTwo(). // options will be added in browser by auto complete JS
                setName("existingGrant")
        );

        conf.addField( new FieldVTwo().
                setName("grantLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addField( new FieldVTwo().
                setName("grantLabelDisplay").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()))
                );

        conf.addValidator(new AntiXssValidation());
        conf.addValidator(new AutocompleteRequiredInputValidator("existingGrant", "grantLabel"));

//        addFormSpecificData(conf, vreq);
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewAdminRole =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?organization <http://purl.obolibrary.org/obo/RO_0000053>  ?adminRole . \n" +
        "?adminRole a  vivo:AdministratorRole . \n" +
        "?adminRole <http://purl.obolibrary.org/obo/RO_0000052> ?organization . " ;

    final static String n3ForNewAdminGrant  =
        "@prefix vivo: <" + vivoCore + "> . \n" +
        "?adminRole vivo:relatedBy ?newGrant . \n" +
        "?newGrant a vivo:Grant  . \n" +
        "?newGrant vivo:relates ?adminRole . \n" +
        "?organization vivo:relatedBy  ?newGrant . \n" +
        "?newGrant vivo:relates ?organization  . \n" +
        "?newGrant <"+ label + "> ?grantLabel .";

    final static String n3ForExistingAdminGrant  =
        "@prefix vivo: <" + vivoCore + "> . \n\n" +
        "?adminRole vivo:relatedBy ?existingGrant . \n" +
        "?existingGrant a <http://xmlns.com/foaf/0.1/Grant>  . \n" +
        "?existingGrant vivo:relates ?adminRole . \n" +
        "?organization vivo:relatedBy  ?newGrant . \n" +
        "?newGrant vivo:relates ?organization  . " ;

    /* Queries for editing an existing entry */

    final static String existingGrantQuery =
        "PREFIX vivo: <http://vivoweb.org/ontology/core#>  \n" +
        "SELECT ?existingGrant WHERE { \n" +
        " ?adminRole vivo:relatedBy ?existingGrant . \n" +
        " ?existingGrant a vivo:Grant . \n" +
        "}";

    final static String grantLabelQuery =
        "PREFIX vivo: <http://vivoweb.org/ontology/core#> \n" +
        "SELECT ?existingGrantLabel WHERE { \n" +
        " ?adminRole vivo:relatedBy ?existingGrant . \n" +
        " ?existingGrant a vivo:Grant . \n" +
        " ?existingGrant <" + label + "> ?existingGrantLabel . \n" +
        "}";

}
