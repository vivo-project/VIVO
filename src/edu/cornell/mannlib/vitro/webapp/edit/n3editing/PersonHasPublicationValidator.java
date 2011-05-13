/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.N3Validator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditSubmission;

public class PersonHasPublicationValidator implements N3Validator {

    private static String MISSING_PUB_TYPE_ERROR = "Must specify a publication type.";
    private static String MISSING_PUB_TITLE_ERROR = "Must specify a publication title."; 
    
    @Override
    public Map<String, String> validate(EditConfiguration editConfig,
            EditSubmission editSub) {

        Map<String,String> urisFromForm = editSub.getUrisFromForm();
        Map<String,Literal> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();   
        
        // If there's a pubUri, then we're done. The other fields are disabled and so don't get submitted.
        String pubUri = urisFromForm.get("pubUri");
        if (!StringUtils.isEmpty(pubUri)) {
            return null;
        }
        
        String pubType = urisFromForm.get("pubType");
        if ("".equals(pubType)) {
            pubType = null;
        }
        
        Literal title = literalsFromForm.get("title");
        if (title != null) {
            String titleValue = title.getLexicalForm();
            if (StringUtils.isEmpty(titleValue)) {
                title = null;
            }
        }
        
        if (pubType == null) {
            errors.put("pubType", MISSING_PUB_TYPE_ERROR);
        }
        if (title == null) {
            errors.put("title", MISSING_PUB_TITLE_ERROR);
        }
        
        return errors.size() != 0 ? errors : null;
    }

}
