/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.FoafNameToRdfsLabelPreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

/**
 * Generates the edit configuration for a default property form.
 * ModelChangePreprocessor creates the rdfs:label statement. 
 */
public class NewIndividualFormGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
        
    	EditConfigurationVTwo config = new EditConfigurationVTwo();
    	
    	config.setTemplate( "newIndividualForm.ftl" );
    	
    	config.setN3Required( list(
    	        "?newInd <" + VitroVocabulary.RDF_TYPE  + "> <" + getTypeOfNew(vreq) + "> ."
    	));    
    	//Optional because user may have selected either person or individual of another kind
    	//Person uses first name and last name whereas individual of other class would use label 
    	config.setN3Optional(list(
    	        N3_PREFIX + "?newInd foaf:firstName ?firstName ; foaf:lastName ?lastName .",             
                N3_PREFIX + "?newInd <" + RDFS.label.getURI() + "> ?label ."
    	));
    	                    
    	config.addNewResource("newInd", vreq.getWebappDaoFactory().getDefaultNamespace());
    	    	
    	config.setUrisOnform(list ());
        config.setLiteralsOnForm( list( "label", "firstName", "lastName" ));            	
    	setUrisAndLiteralsInScope(config);
    	//No SPARQL queries for existing since this is only used to create new, never for edit    	
    	    	
    	config.addField(new FieldVTwo().
    	        setName("firstName").
    	        setRangeDatatypeUri(XSD.xstring.getURI()).
    	        setValidators(getFirstNameValidators(vreq)));
    	
    	config.addField(new FieldVTwo().
                setName("lastName").
    	        setRangeDatatypeUri(XSD.xstring.getURI()).
                setValidators(getLastNameValidators(vreq)));    	
    
    	config.addField(new FieldVTwo().
                setName("label").
    	        setRangeDatatypeUri(XSD.xstring.getURI()).
                setValidators(getLabelValidators(vreq)));    	  
    	    	
        addFormSpecificData(config, vreq);        
        
        config.addValidator(new AntiXssValidation());
        
        //This combines the first and last name into the rdfs:label
        config.addModelChangePreprocessor(new FoafNameToRdfsLabelPreprocessor());        

        String formUrl = EditConfigurationUtils.getFormUrlWithoutContext(vreq);       
        config.setFormUrl(formUrl);
        
        //Note, the spaces are important - they were added by ProcessRdfFormController earlier
        //as a means of ensuring the substitution worked correctly - as the regex expects spaces
        config.setEntityToReturnTo(" ?newInd ");
        prepare(vreq, config);
    	return config;
    }
    
    //first and last name have validators if is person is true
    private List<String> getFirstNameValidators(VitroRequest vreq) {
		List<String> validators = new ArrayList<String>();
		if(isPersonType(vreq)) {
			validators.add("nonempty");
		}
		return validators;
	}

	private List<String> getLastNameValidators(VitroRequest vreq) {
		List<String> validators = new ArrayList<String>();
		if(isPersonType(vreq)) {
			validators.add("nonempty");
		}
		return validators;
	}

	//validate label if person is not true
	private List<String> getLabelValidators(VitroRequest vreq) {
		List<String> validators = new ArrayList<String>();
		if(!isPersonType(vreq)) {
			validators.add("nonempty");
		}
		return validators;
	}

	//Get parameter from HTTP request for type of new individual
    private String getTypeOfNew(VitroRequest vreq) {
        String typeUri = vreq.getParameter("typeOfNew");
        if( typeUri == null || typeUri.trim().isEmpty() )
            return getFOAFPersonClassURI();
        else
            return typeUri; 
    }
    
    //Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("typeName", getTypeName(vreq));
		//Put in whether or not person type
		if(isPersonType(vreq)) {
			//Doing this b/c unsure how freemarker will handle boolean value from JAVA
			formSpecificData.put("isPersonType", "true");
		} else {
			formSpecificData.put("isPersonType", "false");

		}
		editConfiguration.setFormSpecificData(formSpecificData);
	}

	private String getTypeName(VitroRequest vreq) {
		String typeOfNew = getTypeOfNew(vreq);
		VClass type = vreq.getWebappDaoFactory().getVClassDao().getVClassByURI(typeOfNew);
		return type.getName();
	}
	
	public String getFOAFPersonClassURI() {
		return "http://xmlns.com/foaf/0.1/Person";
	}
	
	public boolean isPersonType(VitroRequest vreq) {
		WebappDaoFactory wdf = vreq.getWebappDaoFactory();
		Boolean isPersonType = Boolean.FALSE;
		String foafPersonType = getFOAFPersonClassURI();
	    List<String> superTypes = wdf.getVClassDao().getAllSuperClassURIs(getTypeOfNew(vreq));    
	    if( superTypes != null ){
	    	for( String typeUri : superTypes){
	    		if( foafPersonType.equals(typeUri)) {
	    			isPersonType = Boolean.TRUE;
	    			break;
	    		}
	    	}    	
	    }
	    return isPersonType;
	}
	 private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration) {
	    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
	    	//note that at this point the subject, predicate, and object var parameters have already been processed
	    	urisInScope.put(editConfiguration.getVarNameForSubject(), 
	    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
	    	urisInScope.put(editConfiguration.getVarNameForPredicate(), 
	    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
	    	editConfiguration.setUrisInScope(urisInScope);
	    	//Uris in scope include subject, predicate, and object var
	    	
	    	editConfiguration.setLiteralsInScope(new HashMap<String, List<Literal>>());
	    }
	
	private String N3_PREFIX = "@prefix foaf:<http://xmlns.com/foaf/0.1/> .\n";
}
