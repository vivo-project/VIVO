/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.FoafNameToRdfsLabelPreprocessor;

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
    	        "?newInd ?rdfType ?typeOfNew .",
    	        N3_PREFIX + "?newInd foaf:firstName ?firstName .",             
                N3_PREFIX + "?newInd foaf:lastName  ?lastName ."
    	));    
    	                    
    	config.addNewResource("newInd", null);
    	
    	config.addUrisInScope("typeOfNew",list( getTypeOfNew(vreq) ) )
    	      .addUrisInScope("rdfType",  list( VitroVocabulary.RDF_TYPE ));
    	    	
        config.setLiteralsOnForm( list( "firstName", "lastName" ));            	
    	
    	//No SPARQL queries for existing since this is only used to create new, never for edit    	
    	    	
    	config.addField(new FieldVTwo().
    	        setName("firstName").
    	        setRangeDatatypeUri(XSD.xstring.getURI()).
    	        setValidators(list("nonempty")));
    	
    	config.addField(new FieldVTwo().
                setName("lastName").
    	        setRangeDatatypeUri(XSD.xstring.getURI()).
                setValidators(list("nonempty")));    	    	        
    	    	
        addFormSpecificData(config, vreq);        
        
        //This combines the first and last name into the rdfs:label
        config.addModelChangePreprocessor(new FoafNameToRdfsLabelPreprocessor());        

        String formUrl = EditConfigurationUtils.getFormUrlWithoutContext(vreq);       
        config.setFormUrl(formUrl);
        
        //Note, the spaces are important - they were added by ProcessRdfFormController earlier
        //as a means of ensuring the substitution worked correctly - as the regex expects spaces
        config.setEntityToReturnTo(" ?newInd ");
        
    	return config;
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
	
	private String N3_PREFIX = "@prefix foaf:<http://xmlns.com/foaf/0.1/> .\n";
}
