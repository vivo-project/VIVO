/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddResearcherRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	
	private static String template = "addResearcherRoleToPerson.ftl";
	    
	@Override
	String getTemplate() {
		return template;
	}

	@Override
	public String getRoleType() {
		return "http://vivoweb.org/ontology/core#ResearcherRole";
	}
	
	@Override
	RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.HARDCODED_LITERALS;
	}
	
	@Override
	String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return null;
	}
	
	//Researcher role involves hard-coded options for the "right side" of the role or activity
	@Override
	HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select one");
        literalOptions.put("http://vivoweb.org/ontology/core#Grant", "Grant");
        literalOptions.put("http://vivoweb.org/ontology/core#Project","Project");
		return literalOptions;
	}

	@Override  
    boolean isShowRoleLabelField() { return true;  }
}
