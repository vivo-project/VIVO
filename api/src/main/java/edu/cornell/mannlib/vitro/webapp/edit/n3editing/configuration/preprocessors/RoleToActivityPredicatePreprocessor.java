/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import java.util.Map;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;

public class RoleToActivityPredicatePreprocessor extends RoleToPredicatePreprocessor {
	public RoleToActivityPredicatePreprocessor(EditConfigurationVTwo editConfig, WebappDaoFactory wadf) {
        super(editConfig, wadf);
    }

    protected void setupVariableNames() {
    	this.itemType = "roleActivityType";
    	this.roleToItemPredicate = "roleToActivityPredicate";
    	this.itemToRolePredicate = "activityToRolePredicate";
    }

    protected String getItemType(MultiValueEditSubmission submission) {
    	String type = null;
    	Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
		//Get the type of the activity selected
		List<String> itemTypes = urisFromForm.get(itemType);
		//Really should just be one here
		if(itemTypes != null && itemTypes.size() > 0) {
			type = itemTypes.get(0);
		}
		return type;
    }
}
