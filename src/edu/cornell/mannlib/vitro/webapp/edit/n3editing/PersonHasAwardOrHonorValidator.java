/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.N3ValidatorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;

public class PersonHasAwardOrHonorValidator implements N3ValidatorVTwo {

    private static String MISSING_AWARD_LABEL_ERROR = "You must select an existing award or type the name of a new award.";
;    
    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {
        Map<String,List<String>> urisFromForm = editSub.getUrisFromForm();
        Map<String,List<Literal>> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();   
        
        List<String> awardUri = urisFromForm.get("existingAward");
        if (allListElementsEmpty(awardUri)) {
            awardUri = null;
        }
        // If there's an awardUri, then we're done. If not, check to see if the label exists.
        // If that's null, too, it's an error.
        if (awardUri != null) {
            return null;
        }
        else {
            List<String> awardLabel = urisFromForm.get("awardLabel");
            if (allListElementsEmpty(awardLabel)) {
                awardLabel = null;
            }
            if (awardLabel != null) {
                return null;
            }
            else {
                errors.put("awardLabel", MISSING_AWARD_LABEL_ERROR);
            }
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
