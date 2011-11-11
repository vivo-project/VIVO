/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddClinicalRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
		
	private static String template = "addClinicalRoleToPerson.ftl";
	
    //Should this be overridden
	@Override
	String getTemplate() { 
	    return template; 
	}

	@Override
	String getRoleType() {
		return "http://vivoweb.org/ontology/core#ClinicalRole";
	}
	
	@Override
	RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.HARDCODED_LITERALS;
	}
		
	@Override
	String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return null; //not needed since the options are hard coded
	}
	
	//Clinical role involves hard-coded options for the "right side" of the role or activity
	@Override
	HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select one");
		literalOptions.put("http://vivoweb.org/ontology/core#Project", "Project");
		literalOptions.put("http://vivoweb.org/ontology/core#Service","Service");
		return literalOptions;
	}

	//isShowRoleLabelField remains true for this so doesn't need to be overwritten
	@Override
	boolean isShowRoleLabelField(){
	    return true;
	}
    
}
