/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddMemberRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	
	private static String template = "addMemberRoleToPerson.ftl";
	
	@Override
	String getTemplate() {
		return template;
	}   
	
	@Override
	String getRoleType() {	
		return "http://vivoweb.org/ontology/core#MemberRole";
	}
	
	@Override
	RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.VCLASSGROUP;
	}
	
	@Override
	public String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
	    //this is needed since the OptionTypes is VCLASSGROUP
		return "http://vivoweb.org/ontology#vitroClassGrouporganizations";
	}
	

	@Override
	HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select type");
		return literalOptions;
	}

	@Override
	boolean isShowRoleLabelField(){return true;}
    
}
