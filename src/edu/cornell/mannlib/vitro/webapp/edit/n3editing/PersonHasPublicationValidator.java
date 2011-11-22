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

    private static String MISSING_PUB_TYPE_ERROR = "Must specify a publication type.";
    private static String MISSING_PUB_TITLE_ERROR = "Must specify a publication title."; 
    
    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {

        Map<String,List<String>> urisFromForm = editSub.getUrisFromForm();
        Map<String,List<Literal>> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();   
        
        // If there's a pubUri, then we're done. The other fields are disabled and so don't get submitted.
        List<String> pubUriList = urisFromForm.get("pubUri");
        //This method will return null if the list is null or empty, otherwise returns first element
        //Assumption is that only one value for uri, type, or title will be sent back
        String pubUri = (String) getFirstElement(pubUriList);
        if (!StringUtils.isEmpty(pubUri)) {
            return null;
        }
        
        List<String> pubTypeList = urisFromForm.get("pubType");
        String pubType = (String) getFirstElement(pubTypeList);
        if ("".equals(pubType)) {
            pubType = null;
        }
        
        List<Literal> titleList = literalsFromForm.get("title");
        Literal title = (Literal) getFirstElement(titleList);
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
    
    private Object getFirstElement(List checkList) {
    	if(checkList == null || checkList.size() == 0) {
    		return null;
    	}
    	return checkList.get(0);
    }
    

}
