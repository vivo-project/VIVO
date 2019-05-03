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

public class SubjectHasMailingAddressGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {
    private Log log = LogFactory.getLog(SubjectHasMailingAddressGenerator.class);

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);
        String addressUri = vreq.getParameter("addressUri");

        conf.setTemplate("subjectHasMailingAddress.ftl");

        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("individualVcard");

        conf.setN3Required( Arrays.asList( n3ForNewAddress ) );
        conf.setN3Optional( Arrays.asList( streetAddressAssertion,
                                           localityAssertion,
                                           regionAssertion,
                                           countryAssertion,
                                           postalCodeAssertion ) );

        conf.addNewResource("address", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("individualVcard", DEFAULT_NS_FOR_NEW_RESOURCE);

        conf.setLiteralsOnForm(Arrays.asList("streetAddress", "locality", "postalCode", "country", "region" ));

        conf.addSparqlForExistingLiteral("streetAddress", streetAddressQuery);
        conf.addSparqlForExistingLiteral("locality", localityQuery);
        conf.addSparqlForExistingLiteral("postalCode", postalCodeQuery);
        conf.addSparqlForExistingLiteral("region", regionQuery);
        conf.addSparqlForExistingLiteral("country", countryQuery);

        if ( conf.isUpdate() ) {
            HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
            urisInScope.put("address", Arrays.asList(new String[]{addressUri}));
            conf.addUrisInScope(urisInScope);
        }
        else {
            conf.addSparqlForAdditionalUrisInScope("individualVcard", individualVcardQuery);
        }

        conf.addField( new FieldVTwo().
                setName("streetAddress")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addField( new FieldVTwo().
                setName("country")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addField( new FieldVTwo().
                setName("postalCode")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addField( new FieldVTwo().
                setName("locality")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ) );

        conf.addField( new FieldVTwo().
                setName("region")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) ) );

        conf.addValidator(new AntiXssValidation());

        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewAddress =
        "?subject <http://purl.obolibrary.org/obo/ARG_2000028>  ?individualVcard . \n" +
        "?individualVcard a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?individualVcard <http://purl.obolibrary.org/obo/ARG_2000029> ?subject . \n" +
        "?individualVcard <http://www.w3.org/2006/vcard/ns#hasAddress> ?address . \n" +
        "?address a <http://www.w3.org/2006/vcard/ns#Address> . " ;

    final static String streetAddressAssertion  =
        "?address <http://www.w3.org/2006/vcard/ns#streetAddress> ?streetAddress .";

    final static String localityAssertion  =
        "?address <http://www.w3.org/2006/vcard/ns#locality> ?locality .";

    final static String postalCodeAssertion  =
        "?address <http://www.w3.org/2006/vcard/ns#postalCode> ?postalCode .";

    final static String regionAssertion  =
        "?address <http://www.w3.org/2006/vcard/ns#region> ?region .";

    final static String countryAssertion =
        "?address <http://www.w3.org/2006/vcard/ns#country> ?country .";


    /* Queries for editing an existing entry */

    final static String individualVcardQuery =
        "SELECT ?individualVcard WHERE { \n" +
        "?subject <http://purl.obolibrary.org/obo/ARG_2000028>  ?individualVcard . \n" +
        "}";

    final static String streetAddressQuery  =
        "SELECT ?existingStreetAddress WHERE {\n"+
        "?address <http://www.w3.org/2006/vcard/ns#streetAddress> ?existingStreetAddress . }";

    final static String localityQuery  =
        "SELECT ?existingLocality WHERE {\n"+
        "?address <http://www.w3.org/2006/vcard/ns#locality> ?existingLocality . }";

    final static String regionQuery  =
        "SELECT ?existingRegion WHERE {\n"+
        "?address <http://www.w3.org/2006/vcard/ns#region> ?existingRegion . }";

    final static String postalCodeQuery  =
        "SELECT ?existingPostalCode WHERE {\n"+
        "?address <http://www.w3.org/2006/vcard/ns#postalCode> ?existingPostalCode . }";

    final static String countryQuery  =
        "SELECT ?existingCountry WHERE {\n"+
        "?address <http://www.w3.org/2006/vcard/ns#country> ?existingCountry . }";

}
