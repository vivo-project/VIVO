/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.N3ValidatorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;

public class AutocompleteRequiredInputValidator implements N3ValidatorVTwo {

    private static String MISSING_LABEL_ERROR = "Please select an existing value or enter a new value in the Name field.";

    private String uriReceiver;
    private String labelInput;

    public AutocompleteRequiredInputValidator(String uriReceiver, String labelInput) {
        this.uriReceiver = uriReceiver;
        this.labelInput = labelInput;
    }

    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {
        Map<String,List<String>> urisFromForm = editSub.getUrisFromForm();
        Map<String,List<Literal>> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();

        List<String> selectedUri = urisFromForm.get(uriReceiver);

        // If there's a presentationUri, then we're done. If not, check to see if the label exists.
        // If that's null, too, it's an error.
        if (allListElementsEmpty(selectedUri) || selectedUri.contains(">SUBMITTED VALUE WAS BLANK<")) {
            selectedUri = null;
        }
        if (selectedUri != null) {
            return null;
        }
        else {
            List<Literal> specifiedLabel = literalsFromForm.get(labelInput);
            if (specifiedLabel != null && specifiedLabel.size() > 0) {
                return null;
            }
            else {
                errors.put(labelInput, MISSING_LABEL_ERROR);
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
