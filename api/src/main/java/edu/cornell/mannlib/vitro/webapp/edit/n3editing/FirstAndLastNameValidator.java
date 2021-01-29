/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.N3ValidatorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.i18n.I18nBundle;

public class FirstAndLastNameValidator implements N3ValidatorVTwo {

    private static String MISSING_FIRST_NAME_ERROR = "first_name_empty_msg";
    private static String MISSING_LAST_NAME_ERROR = "last_name_empty_msg";
    private static String MALFORMED_LAST_NAME_ERROR = "malformed_last_name_msg";
    private I18nBundle i18n;
    private String uriReceiver;

    public FirstAndLastNameValidator(String uriReceiver, I18nBundle i18n) {
        this.i18n = i18n;
        this.uriReceiver = uriReceiver;
    }

    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {
        Map<String,List<String>> urisFromForm = editSub.getUrisFromForm();
        Map<String,List<Literal>> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();

        List<String> personUri = urisFromForm.get(uriReceiver);
        if (allListElementsEmpty(personUri) || personUri.contains(">SUBMITTED VALUE WAS BLANK<")) {
            personUri = null;
        }
        // If there's an personUri, then we're done. The firstName and lastName fields are
        // disabled and so don't get submitted.
        if (personUri != null) {
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
            errors.put("lastName", i18n.text(MISSING_LAST_NAME_ERROR));
        // Don't reject space in the last name: de Vries, etc.
        } else if (lastNameValue.contains(",")) {
            errors.put("lastName", i18n.text(MALFORMED_LAST_NAME_ERROR));
        }

        if (firstName == null) {
            errors.put("firstName", i18n.text(MISSING_FIRST_NAME_ERROR));
        }

        return errors.size() != 0 ? errors : null;
    }

    private boolean allListElementsEmpty(List<String> checkList) {
    	if(checkList == null)
    		return true;
    	if(checkList.isEmpty()) {
    		return true;
    	}
    	boolean allEmpty = true;
    	for(String s: checkList) {
    		if(s.length() != 0){
    			allEmpty = false;
    			break;
    		}
    	}
    	return allEmpty;
    }

}
