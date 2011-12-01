/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasPublicationValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

/**
 * On an add/new, this will show a form, on an edit/update this will skip to the
 * profile page of the publication.  
 */
public class AddPublicationToPersonGenerator extends VivoBaseGenerator implements EditConfigurationGenerator {

	@Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
     
     if( EditConfigurationUtils.getObjectUri(vreq) == null ){
         return doAddNew(vreq,session);
     }else{
         return doSkipToPublication(vreq);
     }
    }

    private EditConfigurationVTwo doSkipToPublication(VitroRequest vreq) {
        Individual authorshipNode = EditConfigurationUtils.getObjectIndividual(vreq);
        
        //try to get the publication
        List<ObjectPropertyStatement> stmts = 
            authorshipNode.getObjectPropertyStatements("http://vivoweb.org/ontology/core#linkedInformationResource");
        if( stmts == null || stmts.isEmpty() ){
            return doBadAuthorshipNoPub( vreq );
        }else if( stmts.size() > 1 ){
            return doBadAuthorshipMultiplePubs(vreq);
        }else{ 
            //skip to publication 
            EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
            editConfiguration.setSkipToUrl(UrlBuilder.getIndividualProfileUrl(stmts.get(0).getObjectURI(), vreq));
            return editConfiguration;
        }
    }

    protected EditConfigurationVTwo doAddNew(VitroRequest vreq,
            HttpSession session) {
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
        return list(getN3ForExistingPub(),
                getN3ForNewPub(),
                getN3NewPubNameAssertion(),
                getN3NewPubTypeAssertion());
    }


    private List<String> generateN3Required() {
        return list(getAuthorshipN3());
    }

    private String getAuthorshipN3() {
        return "@prefix core: <" + vivoCore + "> . " + 
        "?authorshipUri a core:Authorship ;" + 
        "core:linkedAuthor ?person ." +   
        "?person core:authorInAuthorship ?authorshipUri .";
    }

    private String getN3ForExistingPub() {
        return "@prefix core: <" + vivoCore + "> ." +
        "?authorshipUri core:linkedInformationResource ?pubUri ." +
        "?pubUri core:informationResourceInAuthorship ?authorshipUri .";
    }

    private String getN3ForNewPub() {
        return "@prefix core: <" + vivoCore + "> ." +
        "?pubUri a ?pubType ;" +
        "<" + label + "> ?title ." +  
        "?authorshipUri core:linkedInformationResource ?pubUri ." +
        "?pubUri core:informationResourceInAuthorship ?authorshipUri .";   
    }

    private String getN3NewPubNameAssertion() {
        return "?pubUri <" + label + "> ?title .";
    }

    private String getN3NewPubTypeAssertion() {
        return "?pubUri a ?pubType . ";

    }

    /**  Get new resources	 */
    private Map<String, String> generateNewResources(VitroRequest vreq) {					
        String DEFAULT_NS_TOKEN=null; //null forces the default NS

        HashMap<String, String> newResources = new HashMap<String, String>();			
        newResources.put("authorshipUri", DEFAULT_NS_TOKEN);
        newResources.put("pubUri", DEFAULT_NS_TOKEN);
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
        urisOnForm.add("pubUri");
        urisOnForm.add("pubType");
        editConfiguration.setUrisOnform(urisOnForm);

        //activity label and role label are literals on form
        List<String> literalsOnForm = new ArrayList<String>();
        literalsOnForm.add("title");
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
     */

    private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
        setTitleField(editConfiguration);
        setPubTypeField(editConfiguration);
        setPubUriField(editConfiguration);
    }

    private void setTitleField(EditConfigurationVTwo editConfiguration) {
        String stringDatatypeUri = XSD.xstring.toString();
        editConfiguration.addField(new FieldVTwo().
                setName("title").
                setValidators(list("datatype:" + stringDatatypeUri)).
                setRangeDatatypeUri(stringDatatypeUri));

    }





    private void setPubTypeField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("pubType").
                setOptionsType("HARDCODED_LITERALS").
                setLiteralOptions(getPublicationTypeLiteralOptions()));
    }





    private void setPubUriField(EditConfigurationVTwo editConfiguration) {
        editConfiguration.addField(new FieldVTwo().
                setName("pubUri").
                setObjectClassUri(personClass));			
    }


    private List<List<String>> getPublicationTypeLiteralOptions() {
        List<List<String>> literalOptions = new ArrayList<List<String>>();
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
        literalOptions.add(list("http://vivoweb.org/ontology/core#Software", "Software"));
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
        "<" + subject + "> core:authorInAuthorship ?authorshipUri ." + 
        "?authorshipUri core:linkedInformationResource ?pubUri . }";
        return query;
    }

    public EditMode getEditMode(VitroRequest vreq) {
        return EditModeUtils.getEditMode(vreq, list("http://vivoweb.org/ontology/core#linkedInformationResource"));
    }

}
