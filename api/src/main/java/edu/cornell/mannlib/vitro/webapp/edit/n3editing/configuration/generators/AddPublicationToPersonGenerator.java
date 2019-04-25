/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.resultset.ResultSetMem;
import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.AutocompleteRequiredInputValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasPublicationValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;


/**
 * On an add/new, this will show a form, on an edit/update this will skip to the
 * profile page of the publication.
 */
public class AddPublicationToPersonGenerator extends VivoBaseGenerator implements EditConfigurationGenerator {

    final static String collectionClass = bibo + "Journal";
    final static String bookClass = bibo + "Book";
    final static String documentClass = "http://purl.obolibrary.org/obo/IAO_0000030";
    final static String conferenceClass = bibo + "Conference";
    final static String editorClass = foaf + "Person";
    final static String publisherClass = vivoCore + "Publisher";
    final static String presentedAtPred = bibo + "presentedAt";
    final static String localePred = vivoCore + "placeOfPublication";
    final static String volumePred = bibo + "volume";
    final static String numberPred = bibo + "number";
    final static String issuePred = bibo + "issue";
    final static String chapterNbrPred = bibo + "chapter";
    final static String startPagePred = bibo + "pageStart";
    final static String endPagePred = bibo + "pageEnd";
    final static String dateTimePred = vivoCore + "dateTimeValue";
    final static String dateTimeValueType = vivoCore + "DateTimeValue";
    final static String dateTimeValue = vivoCore + "dateTime";
    final static String dateTimePrecision = vivoCore + "dateTimePrecision";
    final static String relatesPred = vivoCore + "relates";

    public AddPublicationToPersonGenerator() {}

	@Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) throws Exception {

     if( EditConfigurationUtils.getObjectUri(vreq) == null ){
         return doAddNew(vreq,session);
     }else{
         return doSkipToPublication(vreq);
     }
    }

    private EditConfigurationVTwo doSkipToPublication(VitroRequest vreq) {
        Individual authorshipNode = EditConfigurationUtils.getObjectIndividual(vreq);

        //try to get the publication
        String pubQueryStr = "SELECT ?obj \n" +
                             "WHERE { <" + authorshipNode.getURI() + "> <" + relatesPred + "> ?obj . \n" +
                             "    ?obj a <" + documentClass + "> . } \n";
        Query pubQuery = QueryFactory.create(pubQueryStr);
        QueryExecution qe = QueryExecutionFactory.create(pubQuery, ModelAccess.on(vreq).getOntModel());
        try {
            ResultSetMem rs = new ResultSetMem(qe.execSelect());
            if(!rs.hasNext()){
                return doBadAuthorshipNoPub( vreq );
            }else if( rs.size() > 1 ){
                return doBadAuthorshipMultiplePubs(vreq);
            }else{
                //skip to publication
                RDFNode objNode = rs.next().get("obj");
                if (!objNode.isResource() || objNode.isAnon()) {
                    return doBadAuthorshipNoPub( vreq );
                }
                EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
                editConfiguration.setSkipToUrl(UrlBuilder.getIndividualProfileUrl(((Resource) objNode).getURI(), vreq));
                return editConfiguration;
            }
        } finally {
            qe.close();
        }
    }

    protected EditConfigurationVTwo doAddNew(VitroRequest vreq,
            HttpSession session) throws Exception {
        EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
        initBasics(editConfiguration, vreq);
        initPropertyParameters(vreq, session, editConfiguration);
        initObjectPropForm(editConfiguration, vreq);
        setVarNames(editConfiguration);

        // Required N3
        editConfiguration.setN3Required(generateN3Required());

        // Optional N3
        editConfiguration.setN3Optional(generateN3Optional());

        editConfiguration.setNewResources(generateNewResources(vreq));

        // In scope
        setUrisAndLiteralsInScope(editConfiguration, vreq);

        // on Form
        setUrisAndLiteralsOnForm(editConfiguration, vreq);

        // Sparql queries
        setSparqlQueries(editConfiguration, vreq);

        // set fields
        setFields(editConfiguration, vreq);

        // template file
        editConfiguration.setTemplate("addPublicationToPerson.ftl");
        // adding person has publication validator
        editConfiguration.addValidator(new AntiXssValidation());
        editConfiguration.addValidator(new AutocompleteRequiredInputValidator("pubUri", "title"));
        editConfiguration.addValidator(new PersonHasPublicationValidator());

        // Adding additional data, specifically edit mode
        addFormSpecificData(editConfiguration, vreq);
        prepare(vreq, editConfiguration);
        return editConfiguration;
    }

    private EditConfigurationVTwo doBadAuthorshipMultiplePubs(VitroRequest vreq) {
        // TODO Auto-generated method stub
        return null;
    }

    private EditConfigurationVTwo doBadAuthorshipNoPub(VitroRequest vreq) {
        // TODO Auto-generated method stub
        return null;
    }

    private void setVarNames(EditConfigurationVTwo editConfiguration) {
        editConfiguration.setVarNameForSubject("person");
        editConfiguration.setVarNameForPredicate("predicate");
        editConfiguration.setVarNameForObject("authorshipUri");

    }

    /***N3 strings both required and optional***/
    private List<String> generateN3Optional() {
        return list(getN3ForNewPub(),
                    getN3ForExistingPub(),
                    getN3ForNewCollection(),
                    getN3ForNewBook(),
                    getN3ForNewConference(),
                    getN3ForNewEvent(),
                    getN3ForNewEditor(),
                    getN3ForNewPublisher(),
                    getN3ForNewCollectionNewPub(),
                    getN3ForNewBookNewPub(),
                    getN3ForNewConferenceNewPub(),
                    getN3ForNewEventNewPub(),
                    getN3ForNewEditorNewPub(),
                    getN3ForNewPublisherNewPub(),
                    getN3ForCollection(),
                    getN3ForBook(),
                    getN3ForConference(),
                    getN3ForEvent(),
                    getN3ForEditor(),
                    getN3ForPublisher(),
                    getN3ForCollectionNewPub(),
                    getN3ForBookNewPub(),
                    getN3ForConferenceNewPub(),
                    getN3ForEventNewPub(),
                    getN3ForEditorNewPub(),
                    getN3ForPublisherNewPub(),
                    getN3FirstNameAssertion(),
                    getN3LastNameAssertion(),
                    getN3ForLocaleAssertion(),
                    getN3ForVolumeAssertion(),
                    getN3ForNumberAssertion(),
                    getN3ForIssueAssertion(),
                    getN3ForChapterNbrAssertion(),
                    getN3ForStartPageAssertion(),
                    getN3ForEndPageAssertion(),
                    getN3ForDateTimeAssertion(),
                    getN3ForNewBookNewEditor(),
                    getN3ForNewBookEditor(),
                    getN3ForNewBookNewPublisher(),
                    getN3ForNewBookPublisher(),
                    getN3ForNewBookVolume(),
                    getN3ForNewBookLocale(),
                    getN3ForNewBookPubDate()
                );
    }

    private List<String> generateN3Required() {
        return list(getAuthorshipN3()
                );
    }

    private String getAuthorshipN3() {
        return "@prefix core: <" + vivoCore + "> . " +
        "?authorshipUri a core:Authorship ;" +
        "core:relates ?person ." +
        "?person core:relatedBy ?authorshipUri .";
    }

    private String getN3ForNewPub() {
        return "@prefix core: <" + vivoCore + "> ." +
        "?newPublication a ?pubType ." +
        "?newPublication <" + label + "> ?title ." +
        "?authorshipUri core:relates ?newPublication ." +
        "?newPublication core:relatedBy ?authorshipUri .";
    }

    private String getN3ForExistingPub() {
        return "@prefix core: <" + vivoCore + "> ." +
        "?authorshipUri core:relates ?pubUri ." +
        "?pubUri core:relatedBy ?authorshipUri .";
    }

    private String getN3ForNewCollectionNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:hasPublicationVenue ?newCollection . \n" +
        "?newCollection a <" + collectionClass + "> . \n" +
        "?newCollection vivo:publicationVenueFor ?newPublication . \n" +
        "?newCollection <" + label + "> ?collection .";
    }

    private String getN3ForNewCollection() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:hasPublicationVenue ?newCollection . \n" +
        "?newCollection a <" + collectionClass + ">  . \n" +
        "?newCollection vivo:publicationVenueFor ?pubUri . \n" +
        "?newCollection <" + label + "> ?collection .";
    }

    private String getN3ForCollectionNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:hasPublicationVenue ?collectionUri . \n" +
        "?collectionUri vivo:publicationVenueFor ?newPublication . ";
    }

    private String getN3ForCollection() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:hasPublicationVenue ?collectionUri . \n" +
        "?collectionUri vivo:publicationVenueFor ?pubUri . ";
    }

    private String getN3ForNewBook() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:hasPublicationVenue ?newBook . \n" +
        "?newBook a <" + bookClass + ">  . \n" +
        "?newBook vivo:publicationVenueFor ?pubUri . \n " +
        "?newBook <" + label + "> ?book .";
    }

    private String getN3ForBook() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:hasPublicationVenue ?bookUri . \n" +
        "?bookUri vivo:publicationVenueFor ?pubUri . ";
    }

    private String getN3ForNewBookNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:hasPublicationVenue ?newBook . \n" +
        "?newBook a <" + bookClass + ">  . \n" +
        "?newBook vivo:publicationVenueFor ?newPublication . \n " +
        "?newBook <" + label + "> ?book . ";
    }

    private String getN3ForNewBookVolume() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newBook <" + volumePred + "> ?volume . ";
    }

    private String getN3ForNewBookLocale() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newBook <" + localePred + "> ?locale . ";
    }

    private String getN3ForNewBookPubDate() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newBook <" + dateTimePred + "> ?dateTimeNode . \n" +
        "?dateTimeNode a <" + dateTimeValueType + "> . \n" +
        "?dateTimeNode <" + dateTimeValue + "> ?dateTime-value . \n" +
        "?dateTimeNode <" + dateTimePrecision + "> ?dateTime-precision .";
    }

    private String getN3ForNewBookNewEditor() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newBook vivo:relatedBy ?editorship . \n" +
        "?editorship vivo:relates ?newBook . \n" +
        "?newBook <" + label + "> ?book . \n " +
        "?editorship a vivo:Editorship . \n" +
        "?editorship vivo:relates ?newEditor . \n" +
        "?newEditor a <" + editorClass + ">  . \n" +
        "?newEditor vivo:relatedBy ?editorship . \n" +
        "?newEditor <" + label + "> ?editor .";
    }

    private String getN3ForNewBookEditor() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newBook vivo:relatedBy ?editorship . \n" +
        "?editorship vivo:relates ?newBook . \n" +
        "?newBook <" + label + "> ?book . \n " +
        "?editorship a vivo:Editorship . \n" +
        "?editorship vivo:relates ?editorUri . \n" +
        "?editorUri vivo:relatedBy ?editorship . ";
    }

    private String getN3ForNewBookNewPublisher() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newBook vivo:publisher ?newPublisher . \n " +
        "?newPublisher vivo:publisherOf ?newBook . \n" +
        "?newPublisher <" + label + "> ?publisher .";
     }

    private String getN3ForNewBookPublisher() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newBook vivo:publisher ?publisherUri . \n" +
        "?publisherUri vivo:publisherOf ?newBook . ";
    }

    private String getN3ForBookNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:hasPublicationVenue ?bookUri . \n" +
        "?bookUri vivo:publicationVenueFor ?newPublication . ";
    }

    private String getN3ForNewConference() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri <" + presentedAtPred + "> ?newConference . \n" +
        "?newConference a <" + conferenceClass + ">  . \n" +
        "?newConference <http://purl.obolibrary.org/obo/BFO_0000051> ?pubUri . \n" +
        "?newConference <" + label + "> ?conference .";
    }

    private String getN3ForConference() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri <" + presentedAtPred + "> ?conferenceUri . \n" +
        "?conferenceUri <http://purl.obolibrary.org/obo/BFO_0000051> ?pubUri . ";
    }

    private String getN3ForNewConferenceNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + presentedAtPred + "> ?newConference . \n" +
        "?newConference a <" + conferenceClass + ">  . \n" +
        "?newConference <http://purl.obolibrary.org/obo/BFO_0000051> ?newPublication . \n" +
        "?newConference <" + label + "> ?conference .";
    }

    private String getN3ForConferenceNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + presentedAtPred + "> ?conferenceUri . \n" +
        "?conferenceUri <http://purl.obolibrary.org/obo/BFO_0000051> ?newPublication . ";
    }

    private String getN3ForNewEvent() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:proceedingsOf ?newEvent . \n" +
        "?newEvent a <" + conferenceClass + ">  . \n" +
        "?newEvent vivo:hasProceedings ?pubUri . \n" +
        "?newEvent <" + label + "> ?event .";
    }

    private String getN3ForEvent() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:proceedingsOf ?eventUri . \n" +
        "?eventUri vivo:hasProceedings ?pubUri . ";
    }

    private String getN3ForNewEventNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:proceedingsOf ?newEvent . \n" +
        "?newEvent a <" + conferenceClass + ">  . \n" +
        "?newEvent vivo:hasProceedings ?newPublication . \n" +
        "?newEvent <" + label + "> ?event .";
    }

    private String getN3ForEventNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:proceedingsOf ?eventUri . \n" +
        "?eventUri vivo:hasProceedings ?newPublication . ";
    }

    private String getN3ForNewEditor() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:relatedBy ?editorship . \n" +
        "?editorship vivo:relates ?pubUri . \n" +
        "?editorship a vivo:Editorship . \n" +
        "?editorship vivo:relates ?newEditor . \n" +
        "?newEditor a <" + editorClass + ">  . \n" +
        "?newEditor vivo:relatedBy ?editorship . \n" +
        "?newEditor <" + label + "> ?editor .";
    }

    private String getN3ForEditor() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:relatedBy ?editorship . \n" +
        "?editorship vivo:relates ?pubUri . \n" +
        "?editorship a vivo:Editorship . \n" +
        "?editorship vivo:relates ?editorUri . \n" +
        "?editorUri vivo:relatedBy ?editorship . ";
    }

    private String getN3ForNewEditorNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:relatedBy ?editorship . \n" +
        "?editorship vivo:relates ?newPublication . \n" +
        "?newPublication <" + label + "> ?title ." +
        "?editorship a vivo:Editorship . \n" +
        "?editorship vivo:relates ?newEditor . \n" +
        "?newEditor a <" + editorClass + ">  . \n" +
        "?newEditor vivo:relatedBy ?editorship . \n" +
        "?newEditor <" + label + "> ?editor .";
    }

    private String getN3ForEditorNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:relatedBy ?editorship . \n" +
        "?editorship vivo:relates ?newPublication . \n" +
        "?newPublication <" + label + "> ?title ." +
        "?editorship vivo:relates ?editorUri . \n" +
        "?editorship a vivo:Editorship . \n" +
        "?editorUri vivo:relatedBy ?editorship . ";
    }

    private String getN3ForNewPublisher() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:publisher ?newPublisher . \n" +
        "?newPublisher a <" + publisherClass + ">  . \n" +
        "?newPublisher vivo:publisherOf ?pubUri . \n" +
        "?newPublisher <" + label + "> ?publisher .";
    }

    private String getN3ForPublisher() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?pubUri vivo:publisher ?publisherUri . \n" +
        "?publisherUri vivo:publisherOf ?pubUri . ";
    }

    private String getN3ForNewPublisherNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:publisher ?newPublisher . \n" +
        "?newPublisher a <" + publisherClass + ">  . \n" +
        "?newPublisher vivo:publisherOf ?newPublication . \n" +
        "?newPublisher <" + label + "> ?publisher .";
    }

    private String getN3ForPublisherNewPub() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication vivo:publisher ?publisherUri . \n" +
        "?publisherUri vivo:publisherOf ?newPublication . ";
    }

    private String getN3FirstNameAssertion() {
        return "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
        "?newEditor <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardEditor . \n" +
        "?vcardEditor <http://purl.obolibrary.org/obo/ARG_2000029>  ?newEditor . \n" +
        "?vcardEditor a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?vcardEditor vcard:hasName  ?vcardName . \n" +
        "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n" +
        "?vcardName vcard:givenName ?firstName .";
    }

    private String getN3LastNameAssertion() {
        return "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
        "?newEditor <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardEditor . \n" +
        "?vcardEditor <http://purl.obolibrary.org/obo/ARG_2000029>  ?newEditor . \n" +
        "?vcardEditor a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
        "?vcardEditor vcard:hasName  ?vcardName . \n" +
        "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n" +
        "?vcardName vcard:familyName ?lastName .";
    }

    private String getN3ForLocaleAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + localePred + "> ?locale .  ";
    }

    private String getN3ForVolumeAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + volumePred + "> ?volume .  ";
    }

    private String getN3ForNumberAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + numberPred + "> ?number .  ";
    }

    private String getN3ForIssueAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + issuePred + "> ?issue .  ";
    }

    private String getN3ForChapterNbrAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + chapterNbrPred + "> ?chapterNbr .  ";
    }

    private String getN3ForStartPageAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + startPagePred + "> ?startPage . ";
    }

    private String getN3ForEndPageAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + endPagePred + ">?endPage . ";
    }

    private String getN3ForDateTimeAssertion() {
        return "@prefix vivo: <" + vivoCore + "> . \n" +
        "?newPublication <" + dateTimePred + "> ?dateTimeNode . \n" +
        "?dateTimeNode a <" + dateTimeValueType + "> . \n" +
        "?dateTimeNode <" + dateTimeValue + "> ?dateTime-value . \n" +
        "?dateTimeNode <" + dateTimePrecision + "> ?dateTime-precision . ";
    }

    /**  Get new resources	 */
    private Map<String, String> generateNewResources(VitroRequest vreq) {
        String DEFAULT_NS_TOKEN=null; //null forces the default NS

        HashMap<String, String> newResources = new HashMap<String, String>();
        newResources.put("authorshipUri", DEFAULT_NS_TOKEN);
        newResources.put("newPublication", DEFAULT_NS_TOKEN);
        newResources.put("newCollection", DEFAULT_NS_TOKEN);
        newResources.put("newBook", DEFAULT_NS_TOKEN);
        newResources.put("newConference", DEFAULT_NS_TOKEN);
        newResources.put("newEvent", DEFAULT_NS_TOKEN);
        newResources.put("newEditor", DEFAULT_NS_TOKEN);
        newResources.put("editorship", DEFAULT_NS_TOKEN);
        newResources.put("vcardEditor", DEFAULT_NS_TOKEN);
        newResources.put("vcardName", DEFAULT_NS_TOKEN);
        newResources.put("newPublisher", DEFAULT_NS_TOKEN);
        newResources.put("dateTimeNode", DEFAULT_NS_TOKEN);
        return newResources;
    }

    /** Set URIS and Literals In Scope and on form and supporting methods	 */
    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
        HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
        urisInScope.put(editConfiguration.getVarNameForSubject(),
                Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
        urisInScope.put(editConfiguration.getVarNameForPredicate(),
                Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
        editConfiguration.setUrisInScope(urisInScope);
        HashMap<String, List<Literal>> literalsInScope = new HashMap<String, List<Literal>>();
        editConfiguration.setLiteralsInScope(literalsInScope);

    }

    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
        List<String> urisOnForm = new ArrayList<String>();
        //add role activity and roleActivityType to uris on form
        urisOnForm.add("pubType");
        urisOnForm.add("pubUri");
        urisOnForm.add("collectionUri");
        urisOnForm.add("bookUri");
        urisOnForm.add("conferenceUri");
        urisOnForm.add("eventUri");
        urisOnForm.add("editorUri");
        urisOnForm.add("publisherUri");
        editConfiguration.setUrisOnform(urisOnForm);

        //activity label and role label are literals on form
        List<String> literalsOnForm = new ArrayList<String>();
        literalsOnForm.add("title");
        literalsOnForm.add("collection");
        literalsOnForm.add("book");
        literalsOnForm.add("conference");
        literalsOnForm.add("event");
        literalsOnForm.add("editor");
        literalsOnForm.add("publisher");
        literalsOnForm.add("collectionDisplay");
        literalsOnForm.add("bookDisplay");
        literalsOnForm.add("conferenceDisplay");
        literalsOnForm.add("eventDisplay");
        literalsOnForm.add("editorDisplay");
        literalsOnForm.add("publisherDisplay");
        literalsOnForm.add("locale");
        literalsOnForm.add("volume");
        literalsOnForm.add("number");
        literalsOnForm.add("issue");
        literalsOnForm.add("chapterNbr");
        literalsOnForm.add("startPage");
        literalsOnForm.add("endPage");
        literalsOnForm.add("firstName");
        literalsOnForm.add("lastName");
        editConfiguration.setLiteralsOnForm(literalsOnForm);
    }

    /** Set SPARQL Queries and supporting methods. */
    //In this case no queries for existing
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
        editConfiguration.setSparqlForExistingUris(new HashMap<String, String>());
        editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
        editConfiguration.setSparqlForAdditionalUrisInScope(new HashMap<String, String>());
        editConfiguration.setSparqlForExistingLiterals(new HashMap<String, String>());
    }

    /**
     *
     * Set Fields and supporting methods
     * @throws Exception
     */

    private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq) throws Exception {
        setTitleField(editConfiguration);
        setPubTypeField(editConfiguration);
        setPubUriField(editConfiguration);
        setCollectionLabelField(editConfiguration);
        setCollectionDisplayField(editConfiguration);
        setCollectionUriField(editConfiguration);
        setBookLabelField(editConfiguration);
        setBookDisplayField(editConfiguration);
        setBookUriField(editConfiguration);
        setConferenceLabelField(editConfiguration);
        setConferenceDisplayField(editConfiguration);
        setConferenceUriField(editConfiguration);
        setEventLabelField(editConfiguration);
        setEventDisplayField(editConfiguration);
        setEventUriField(editConfiguration);
        setEditorLabelField(editConfiguration);
        setEditorDisplayField(editConfiguration);
        setFirstNameField(editConfiguration);
        setLastNameField(editConfiguration);
        setEditorUriField(editConfiguration);
        setPublisherLabelField(editConfiguration);
        setPublisherDisplayField(editConfiguration);
        setPublisherUriField(editConfiguration);
        setLocaleField(editConfiguration);
        setVolumeField(editConfiguration);
        setNumberField(editConfiguration);
        setIssueField(editConfiguration);
        setChapterNbrField(editConfiguration);
        setStartPageField(editConfiguration);
        setEndPageField(editConfiguration);
        setDateTimeField(editConfiguration);
    }

    private void setTitleField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("title").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setPubTypeField(EditConfigurationVTwo editConfiguration) throws Exception {
        editConfiguration.addField(new FieldVTwo().
                setName("pubType").
                setValidators( list("nonempty") ).
                setOptions( new ConstantFieldOptions("pubType", getPublicationTypeLiteralOptions() ))
                );
    }

    private void setPubUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("pubUri"));
    }

    private void setCollectionLabelField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("collection").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setCollectionDisplayField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("collectionDisplay").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setCollectionUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("collectionUri"));
    }

    private void setBookLabelField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("book").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setBookDisplayField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("bookDisplay").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setBookUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("bookUri"));
    }

    private void setConferenceLabelField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("conference").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setConferenceDisplayField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("conferenceDisplay").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setConferenceUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("conferenceUri"));
    }

    private void setEventLabelField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("event").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setEventDisplayField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("eventDisplay").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }


    private void setFirstNameField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("firstName").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setLastNameField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("lastName").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }
    private void setEventUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("eventUri"));
    }

    private void setEditorLabelField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("editor").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setEditorDisplayField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("editorDisplay").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setEditorUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("editorUri"));
    }

    private void setPublisherLabelField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("publisher").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setPublisherDisplayField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("publisherDisplay").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setPublisherUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("publisherUri"));
    }

    private void setLocaleField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("locale").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setVolumeField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("volume").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setNumberField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("number").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setIssueField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("issue").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setChapterNbrField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("chapterNbr").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setStartPageField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("startPage").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setEndPageField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("endPage").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));
    }

    private void setDateTimeField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("dateTime").
                setEditElement(
                new DateTimeWithPrecisionVTwo(null,
                        VitroVocabulary.Precision.YEAR.uri(),
                        VitroVocabulary.Precision.NONE.uri())
                        )
                );
    }

    private List<List<String>> getPublicationTypeLiteralOptions() {
        List<List<String>> literalOptions = new ArrayList<List<String>>();
        literalOptions.add(list("http://vivoweb.org/ontology/core#Abstract", "Abstract"));
        literalOptions.add(list("http://purl.org/ontology/bibo/AcademicArticle", "Academic Article"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Article", "Article"));
        literalOptions.add(list("http://purl.org/ontology/bibo/AudioDocument", "Audio Document"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#BlogPosting", "Blog Posting"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Book", "Book"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#CaseStudy", "Case Study"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Catalog", "Catalog"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Chapter", "Chapter"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#ConferencePaper", "Conference Paper"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#ConferencePoster", "Conference Poster"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Database", "Database"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Dataset", "Dataset"));
        literalOptions.add(list("http://purl.org/ontology/bibo/EditedBook", "Edited Book"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#EditorialArticle", "Editorial Article"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Film", "Film"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Newsletter", "Newsletter"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#NewsRelease", "News Release"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Patent", "Patent"));
        literalOptions.add(list("http://purl.obolibrary.org/obo/OBI_0000272", "Protocol"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Report", "Report"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#ResearchProposal", "Research Proposal"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Review", "Review"));
        literalOptions.add(list("http://purl.obolibrary.org/obo/ERO_0000071 ", "Software"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Speech", "Speech"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Thesis", "Thesis"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Video", "Video"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Webpage", "Webpage"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Website", "Website"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#WorkingPaper", "Working Paper"));
        return literalOptions;
    }

    //Form specific data
    public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
        HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
        formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
        formSpecificData.put("sparqlForAcFilter", getSparqlForAcFilter(vreq));
        editConfiguration.setFormSpecificData(formSpecificData);
    }

    public String getSparqlForAcFilter(VitroRequest vreq) {
        String subject = EditConfigurationUtils.getSubjectUri(vreq);

        String query = "PREFIX core:<" + vivoCore + "> " +
        "SELECT ?pubUri WHERE { " +
        "<" + subject + "> core:relatedBy ?authorshipUri . " +
        "?authorshipUri a core:Authorship . " +
        "?authorshipUri core:relates ?pubUri . }";
        return query;
    }

    public EditMode getEditMode(VitroRequest vreq) {
        return EditModeUtils.getEditMode(vreq, list("http://vivoweb.org/ontology/core#relates"));
    }

}
