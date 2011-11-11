/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddReviewerRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {		
	
	@Override
	String getTemplate() { return "addReviewerRoleToPerson.ftl"; }


    //The default activityToRolePredicate and roleToActivityPredicates are 
	//correct for this subclass so they don't need to be overwritten
	public String getActivityToRolePredicate(VitroRequest vreq) {
		return "http://vivoweb.org/ontology/core#linkedRole";
	}

	public String getRoleToActivityPredicate(VitroRequest vreq) {
		return "<http://vivoweb.org/ontology/core#forInformationResource>";
	}
	
	//role type will always be set based on particular form
	public String getRoleType() {
		//TODO: Get dynamic way of including vivoweb ontology
		return "http://vivoweb.org/ontology/core#ReviewerRole";
	}
	
	//Each subclass generator will return its own type of option here:
	//whether literal hardcoded, based on class group, or subclasses of a specific class
	//The latter two will apparently lend some kind of uri to objectClassUri ?
	public RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.CHILD_VCLASSES;
	}
	
	//This too will depend on the specific subclass of generator
	public String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return "http://vivoweb.org/ontology/core#InformationResource";
	}
	

	//Reviewer role involves hard-coded options for the "right side" of the role or activity
	protected HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select type");
		return literalOptions;
	}

	//isShowRoleLabelField remains true for this so doesn't need to be overwritten
	public boolean isShowRoleLabelField() {
		return false;
	}

    
}
