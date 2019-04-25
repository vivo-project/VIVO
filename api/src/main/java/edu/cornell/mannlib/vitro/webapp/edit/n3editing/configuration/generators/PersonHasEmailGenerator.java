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

public class PersonHasEmailGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {
    private Log log = LogFactory.getLog(PersonHasEmailGenerator.class);

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);
        String emailUri = getEmailUri(vreq);
        String rangeUri = getRangeUri(vreq);

        conf.setTemplate("personHasEmailAddress.ftl");

        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("individualVcard");

        if ( rangeUri.equals("http://www.w3.org/2006/vcard/ns#Work") ) {
            conf.setN3Required( Arrays.asList( n3ForNewPrimaryEmail ) );
        }
        else {
            conf.setN3Required( Arrays.asList( n3ForNewEmail ) );
        }

        conf.setN3Optional( Arrays.asList( emailAddressAssertion ) );

        conf.addNewResource("email", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("individualVcard", DEFAULT_NS_FOR_NEW_RESOURCE);

        conf.setLiteralsOnForm(Arrays.asList("emailAddress" ));

        conf.addSparqlForExistingLiteral("emailAddress", emailAddressQuery);
        conf.addSparqlForAdditionalUrisInScope("individualVcard", individualVcardQuery);

        if ( conf.isUpdate() ) {
            HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
            urisInScope.put("email", Arrays.asList(new String[]{emailUri}));
            conf.addUrisInScope(urisInScope);
        }

        conf.addField( new FieldVTwo().
                setName("emailAddress")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addValidator(new AntiXssValidation());

        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewEmail =
        "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?individualVcard . \n" +
        "?individualVcard a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?individualVcard <http://purl.obolibrary.org/obo/ARG_2000029> ?person . \n" +
        "?individualVcard <http://www.w3.org/2006/vcard/ns#hasEmail> ?email . \n" +
        "?email a <http://www.w3.org/2006/vcard/ns#Email> . " ;

    final static String n3ForNewPrimaryEmail =
        "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?individualVcard . \n" +
        "?individualVcard a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?individualVcard <http://purl.obolibrary.org/obo/ARG_2000029> ?person . \n" +
        "?individualVcard <http://www.w3.org/2006/vcard/ns#hasEmail> ?email . \n" +
        "?email a <http://www.w3.org/2006/vcard/ns#Email> . \n" +
        "?email a <http://www.w3.org/2006/vcard/ns#Work> ." ;

    final static String emailAddressAssertion  =
        "?email <http://www.w3.org/2006/vcard/ns#email> ?emailAddress .";

    /* Queries for editing an existing entry */

    final static String individualVcardQuery =
        "SELECT ?existingIndividualVcard WHERE { \n" +
        "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?existingIndividualVcard . \n" +
        "}";

    final static String emailAddressQuery  =
        "SELECT ?existingEmailAddress WHERE {\n"+
        "?email <http://www.w3.org/2006/vcard/ns#email> ?existingEmailAddress . }";

	private String getRangeUri(VitroRequest vreq) {
        String rangeUri = vreq.getParameter("rangeUri");

		return rangeUri;
	}
	private String getEmailUri(VitroRequest vreq) {
	    String emailUri = vreq.getParameter("emailUri");

		return emailUri;
	}
}
