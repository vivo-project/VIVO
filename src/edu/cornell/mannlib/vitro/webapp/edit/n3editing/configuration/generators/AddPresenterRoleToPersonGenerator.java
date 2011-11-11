/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddPresenterRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	
	private static String template = "addPresenterRoleToPerson.ftl";
	    
	@Override
	String getTemplate() {
		return template;
	}

	
	@Override
	String getRoleType() {	
		return "http://vivoweb.org/ontology/core#PresenterRole";
	}
	
	@Override
	RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.HARDCODED_LITERALS;
	}
	
	
	@Override
	String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return null;
	}
	

	//Presenter role involves hard-coded options for the "right side" of the role or activity
	@Override
	protected HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select type");
        literalOptions.put("http://vivoweb.org/ontology/core#Presentation", "Presentation");
        literalOptions.put("http://vivoweb.org/ontology/core#InvitedTalk","Invited Talk");
		return literalOptions;
	}

	@Override
	boolean isShowRoleLabelField(){return true;}
}
