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

public class PersonHasPreferredTitleGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {
    private Log log = LogFactory.getLog(PersonHasPreferredTitleGenerator.class);

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {

        EditConfigurationVTwo conf = new EditConfigurationVTwo();

        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);
        String titleUri = getTitleUri(vreq);

        conf.setTemplate("personHasPreferredTitle.ftl");

        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("individualVcard");

        conf.setN3Required( Arrays.asList( n3ForNewPhone ) );
        conf.setN3Optional( Arrays.asList( preferredTitleAssertion ) );

        conf.addNewResource("title", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("individualVcard", DEFAULT_NS_FOR_NEW_RESOURCE);

        conf.setLiteralsOnForm(Arrays.asList("preferredTitle" ));

        conf.addSparqlForExistingLiteral("preferredTitle", preferredTitleQuery);
        conf.addSparqlForAdditionalUrisInScope("individualVcard", individualVcardQuery);

        if ( conf.isUpdate() ) {
            HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
            urisInScope.put("title", Arrays.asList(new String[]{titleUri}));
            conf.addUrisInScope(urisInScope);
        }

        conf.addField( new FieldVTwo().
                setName("preferredTitle")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addValidator(new AntiXssValidation());

        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewPhone =
        "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?individualVcard . \n" +
        "?individualVcard a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?individualVcard <http://purl.obolibrary.org/obo/ARG_2000029> ?person . \n" +
        "?individualVcard <http://www.w3.org/2006/vcard/ns#hasTitle> ?title . \n" +
        "?title a <http://www.w3.org/2006/vcard/ns#Title> . " ;

    final static String preferredTitleAssertion  =
        "?title <http://www.w3.org/2006/vcard/ns#title> ?preferredTitle .";

    /* Queries for editing an existing entry */

    final static String individualVcardQuery =
        "SELECT ?existingIndividualVcard WHERE { \n" +
        "?person <http://purl.obolibrary.org/obo/ARG_2000028>  ?existingIndividualVcard . \n" +
        "}";

    final static String preferredTitleQuery  =
        "SELECT ?existingPreferredTitle WHERE {\n"+
        "?title <http://www.w3.org/2006/vcard/ns#title> ?existingPreferredTitle . }";

	private String getTitleUri(VitroRequest vreq) {
        String titleUri = vreq.getParameter("titleUri");

		return titleUri;
	}
}
