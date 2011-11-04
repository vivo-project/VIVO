/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.utils.generators;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;

public class AddRoleUtils {
	private static Log log = LogFactory.getLog(AddRoleUtils.class);

    
    /* *************** Static utility methods used in role-based generators *********** */
	public static EditMode getEditMode(VitroRequest vreq, List<String> possiblePredicates) {
    	//We're making some assumptions here: That there is only one role objec tot one activity object
    	//pairing, i.e. the same role object can't be related to a different activity object
    	//That said, there should only be one role to Activity predicate linking a role to an activity
    	//So if 
    	Individual object = EditConfigurationUtils.getObjectIndividual(vreq);
    	boolean foundErrorMode = false;
    	int numberEditModes = 0;
    	int numberRepairModes = 0;
    	int numberPredicates = possiblePredicates.size();
    	for(String predicate:possiblePredicates) {
    		EditMode mode = FrontEndEditingUtils.getEditMode(vreq, object, predicate);
    		//Any error  mode should result in error
    		if(mode == EditMode.ERROR) {
    			foundErrorMode = true;
    			break;
    		}
    		if(mode == EditMode.EDIT) {
    			numberEditModes++;
    		}
    		else if(mode == EditMode.REPAIR) {
    			numberRepairModes++;
    		}
    		
    	}
    	
    	//if found an error or if more than one edit mode returned, incorrect

    	if(foundErrorMode || numberEditModes > 1) 
    	{
    		return EditMode.ERROR;
    	}
    	EditMode mode = EditMode.ADD;
    	//if exactly one edit mode found, then edit mode
    	if(numberEditModes == 1) {
    		mode = EditMode.EDIT;
    	}
    	//if all modes are repair, this means that all of them have zero statements returning
    	//which is incorrect
    	if(numberRepairModes == numberPredicates) {
    		mode = EditMode.REPAIR;
    	}    	
    	//otherwise all the modes are Add and Add will be returned
    	return mode;
	}
	
	public static boolean isAddMode(EditMode mode) {
    	return (mode == EditMode.ADD);
    }
    
    public static boolean isEditMode(EditMode mode) {
    	return (mode == EditMode.EDIT);
    }
    
   public static boolean isRepairMode(EditMode mode) {
    	return (mode == EditMode.REPAIR);
    }

}
