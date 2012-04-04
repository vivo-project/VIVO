/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.N3ValidatorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;

public class PersonHasPublicationValidator implements N3ValidatorVTwo {

    private static String MISSING_FIRST_NAME_ERROR = "You must enter a value in the First Name field.";
    private static String MISSING_LAST_NAME_ERROR = "You must enter a value in the Last Name field.";
    private static String MALFORMED_LAST_NAME_ERROR = "The last name field may not contain a comma. Please enter first name in First Name field.";
    
    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {

        Map<String,List<String>> urisFromForm = editSub.getUrisFromForm();
        Map<String,List<Literal>> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();   
        
        // If there's a pubUri, then we're done. The other fields are disabled and so don't get submitted.
        List<String> editorUriList = urisFromForm.get("editorUri");
        //This method will return null if the list is null or empty, otherwise returns first element
        //Assumption is that only one value for uri, type, or title will be sent back
        String editorUri = (String) getFirstElement(editorUriList);
        if (StringUtils.isEmpty(editorUri) || editorUri.equals(">SUBMITTED VALUE WAS BLANK<")) {
            editorUri = null;
        }
        if ( editorUri != null ) {
            return null;
        }

        //Expecting only one first name in this case
        //To Do: update logic if multiple first names considered
        Literal firstName = null;
        List<Literal> firstNameList = literalsFromForm.get("firstName");
        if(firstNameList != null && firstNameList.size() > 0) {
        	firstName = firstNameList.get(0);
        }
        if( firstName != null && 
        		firstName.getLexicalForm() != null && 
        		"".equals(firstName.getLexicalForm()) )
            firstName = null;


        List<Literal> lastNameList = literalsFromForm.get("lastName");
        Literal lastName = null;
        if(lastNameList != null && lastNameList.size() > 0) {
        	lastName = lastNameList.get(0);
        }
        String lastNameValue = "";
        if (lastName != null) {
            lastNameValue = lastName.getLexicalForm();
            if( "".equals(lastNameValue) ) {
                lastName = null;
            }
        }

        if (lastName == null) {
            errors.put("lastName", MISSING_LAST_NAME_ERROR);
        // Don't reject space in the last name: de Vries, etc.
        } else if (lastNameValue.contains(",")) {            
            errors.put("lastName", MALFORMED_LAST_NAME_ERROR);
        }
        
        if (firstName == null) {
            errors.put("firstName", MISSING_FIRST_NAME_ERROR);
        }               
        
        return errors.size() != 0 ? errors : null;
    }
    
    private Object getFirstElement(List checkList) {
    	if(checkList == null || checkList.size() == 0) {
    		return null;
    	}
    	return checkList.get(0);
    }
    

}
