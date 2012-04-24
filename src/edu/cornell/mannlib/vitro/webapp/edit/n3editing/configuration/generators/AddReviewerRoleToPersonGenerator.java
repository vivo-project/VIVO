/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;

public class AddReviewerRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {		
	
    private static String OBJECT_VCLASS_URI = "http://vivoweb.org/ontology/core#InformationResource";
    
	@Override
	String getTemplate() { return "addReviewerRoleToPerson.ftl"; }

    //The default activityToRolePredicate and roleToActivityPredicates are 
	//correct for this subclass so they don't need to be overwritten

	@Override
	public String getRoleToActivityPredicate(VitroRequest vreq) {
		return "<http://vivoweb.org/ontology/core#forInformationResource>";
	}
	
	//role type will always be set based on particular form
	@Override
	public String getRoleType() {
		//TODO: Get dynamic way of including vivoweb ontology
		return "http://vivoweb.org/ontology/core#ReviewerRole";
	}		
	
	/**
	 *  Each subclass generator will return its own type of option here:
	 *   whether literal hardcoded, based on class group, or subclasses of a specific class   
	 */
    @Override
    FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {
        return new ChildVClassesOptions(OBJECT_VCLASS_URI)
            .setDefaultOptionLabel("Select type");
    }
    
	//isShowRoleLabelField remains true for this so doesn't need to be overwritten
	public boolean isShowRoleLabelField() {
		return false;
	}




    
}
