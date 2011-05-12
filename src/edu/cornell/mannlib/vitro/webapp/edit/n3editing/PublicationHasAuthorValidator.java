/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.N3Validator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditSubmission;

public class PublicationHasAuthorValidator implements N3Validator {

    private static String MISSING_FIRST_NAME_ERROR = "Must specify the author's first name.";
    private static String MISSING_LAST_NAME_ERROR = "Must specify the author's last name.";
    private static String MALFORMED_LAST_NAME_ERROR = "Last name may not contain a comma. Please enter first name in first name field.";
;    
    @Override
    public Map<String, String> validate(EditConfiguration editConfig,
            EditSubmission editSub) {
        Map<String,String> urisFromForm = editSub.getUrisFromForm();
        Map<String,Literal> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();   
        
        String personUri = urisFromForm.get("personUri");
        if ("".equals(personUri)) {
            personUri = null;
        }
        // If there's a personUri, then we're done. The firstName and lastName fields are
        // disabled and so don't get submitted.
        if (personUri != null) {
            return null;
        }
        
        Literal firstName = literalsFromForm.get("firstName");
        if( firstName != null && firstName.getLexicalForm() != null && "".equals(firstName.getLexicalForm()) )
            firstName = null;

        Literal lastName = literalsFromForm.get("lastName");
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

}
