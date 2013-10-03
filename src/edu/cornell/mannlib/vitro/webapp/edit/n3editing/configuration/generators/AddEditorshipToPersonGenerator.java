/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasAdviseesValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesWithParent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.IndividualsViaVClassOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

public class AddEditorshipToPersonGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {
    
    public AddEditorshipToPersonGenerator() {}
    
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {
        
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
        
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("addEditorshipToPerson.ftl");
        
        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("editorship");
        
        conf.setN3Required( Arrays.asList( n3ForNewEditorship ) );
        conf.setN3Optional( Arrays.asList( n3ForNewDocumentAssertion,
                                           n3ForExistingDocumentAssertion ) );
        
        conf.addNewResource("editorship", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("newDocument", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.setUrisOnform(Arrays.asList("existingDocument", "documentType"));
        conf.setLiteralsOnForm(Arrays.asList("documentLabel", "documentLabelDisplay" ));
        
        conf.addSparqlForExistingLiteral("documentLabel", documentLabelQuery);
        
        conf.addSparqlForExistingUris("documentType", documentTypeQuery);
        conf.addSparqlForExistingUris("existingDocument", existingDocumentQuery);
        
        conf.addField( new FieldVTwo().                        
                setName("documentType").
                setValidators( list("nonempty") ).
                setOptions( new ConstantFieldOptions("documentType", getDocumentTypeLiteralOptions() ))                                
                );

        conf.addField( new FieldVTwo().
                setName("documentLabel").
                setRangeDatatypeUri(XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) )
                );

        conf.addValidator(new AntiXssValidation());
        addFormSpecificData(conf, vreq);
        
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewEditorship = 
        "@prefix vivo: <" + vivoCore + "> . \n" +   
        "?person ?predicate ?editorship . \n" +
        "?editorship a  vivo:Editorship . \n" +              
        "?editorship vivo:relates ?person . " ;
    
    final static String n3ForNewDocumentAssertion  =      
        "@prefix vivo: <" + vivoCore + "> . \n" +   
        "?editorship vivo:relates ?newDocument . \n" +              
        "?newDocument vivo:editedBy ?editorship . \n" +              
        "?newDocument a ?documentType . \n" +              
        "?newDocument <" + label + "> ?documentLabel. " ;
    
    final static String n3ForExistingDocumentAssertion  =      
        "@prefix vivo: <" + vivoCore + "> . \n" +   
        "?editorship vivo:relates ?existingDocument . \n" +              
        "?existingDocument vivo:editedBy ?editorship . \n" +              
        "?existingDocument a ?documentType . " ;

    /* Queries for editing an existing entry */

    final static String documentTypeQuery =
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "PREFIX vivo: <" + vivoCore + "> . \n" +   
        "PREFIX bibo: <http://purl.org/ontology/bibo/> . \n" +   
        "SELECT ?documentType WHERE { \n" + 
        "  ?editorship vivo:relates ?existingDocument . \n" + 
        "  ?existingDocument a <http://purl.obolibrary.org/obo/IAO_0000030> . \n" + 
        "  ?existingDocument vitro:mostSpecificType ?documentType . \n" + 
        "}";

    final static String documentLabelQuery  =      
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "PREFIX vivo: <" + vivoCore + "> . \n" +   
        "PREFIX bibo: <http://purl.org/ontology/bibo/> . \n" +   
        "SELECT ?documentLabel WHERE { \n" + 
        "  ?editorship vivo:relates ?existingDocument . \n" + 
        "  ?existingDocument a <http://purl.obolibrary.org/obo/IAO_0000030> . \n" + 
        "  ?existingDocument <" + label + "> ?documentLabel . \n" + 
        "}";

    final static String existingDocumentQuery  =      
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "PREFIX vivo: <" + vivoCore + "> . \n" +   
        "PREFIX bibo: <http://purl.org/ontology/bibo/> . \n" +   
        "SELECT existingDocument WHERE { \n" + 
        "  ?editorship vivo:relates ?existingDocument . \n" + 
        "  ?existingDocument a <http://purl.obolibrary.org/obo/IAO_0000030> . \n" + 
        "}";

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

    private List<List<String>> getDocumentTypeLiteralOptions() {
        List<List<String>> literalOptions = new ArrayList<List<String>>();
        literalOptions.add(list("http://purl.org/ontology/bibo/Book", "Book"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Chapter", "Chapter"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Collection", "Collection"));
        literalOptions.add(list("http://purl.org/ontology/bibo/EditedBook", "Edited Book"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Film", "Film"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Magazine", "Magazine"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Newsletter", "Newsletter"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Newspaper", "Newspaper"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#NewsRelease", "News Release"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Periodical", "Periodical"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Report", "Report"));
        literalOptions.add(list("http://vivoweb.org/ontology/core#Video", "Video"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Webpage", "Webpage"));
        literalOptions.add(list("http://purl.org/ontology/bibo/Website", "Website"));
        return literalOptions;
    }

}