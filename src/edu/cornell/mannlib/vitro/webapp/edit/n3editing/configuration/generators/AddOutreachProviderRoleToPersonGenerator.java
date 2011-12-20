/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddOutreachProviderRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	
	private static String template = "addOutreachProviderRoleToPerson.ftl";
	
	@Override
	String getTemplate() {
		return template;
	}

	@Override
	String getRoleType() {	
		return "http://vivoweb.org/ontology/core#OutreachProviderRole";
	}

	@Override
	RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.HARDCODED_LITERALS;
	}
	
	@Override
	String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return null;
	}	

	//Outreach Provider role involves hard-coded options for the "right side" of the role or activity
	@Override
	HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select type");
        literalOptions.put("http://vivoweb.org/ontology/core#Association", "Association");
        literalOptions.put("http://vivoweb.org/ontology/core#Center", "Center");
        literalOptions.put("http://vivoweb.org/ontology/core#ClinicalOrganization", "Clinical Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#College", "College");
        literalOptions.put("http://vivoweb.org/ontology/core#Committee", "Committee");                     
        literalOptions.put("http://vivoweb.org/ontology/core#Consortium", "Consortium");
        literalOptions.put("http://vivoweb.org/ontology/core#Department", "Department");
        literalOptions.put("http://vivoweb.org/ontology/core#Division", "Division"); 
        literalOptions.put("http://purl.org/NET/c4dm/event.owl#Event", "Event"); 
        literalOptions.put("http://vivoweb.org/ontology/core#ExtensionUnit", "Extension Unit");
        literalOptions.put("http://vivoweb.org/ontology/core#Foundation", "Foundation");
        literalOptions.put("http://vivoweb.org/ontology/core#FundingOrganization", "Funding Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#GovernmentAgency", "Government Agency");
        literalOptions.put("http://vivoweb.org/ontology/core#Hospital", "Hospital");
        literalOptions.put("http://vivoweb.org/ontology/core#Institute", "Institute");
        literalOptions.put("http://vivoweb.org/ontology/core#Laboratory", "Laboratory");
        literalOptions.put("http://vivoweb.org/ontology/core#Library", "Library");
        literalOptions.put("http://vivoweb.org/ontology/core#Museum", "Museum");        
        literalOptions.put("http://xmlns.com/foaf/0.1/Organization", "Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#PrivateCompany", "Private Company");
        literalOptions.put("http://vivoweb.org/ontology/core#Program", "Program");
        literalOptions.put("http://vivoweb.org/ontology/core#Project", "Project");
        literalOptions.put("http://vivoweb.org/ontology/core#Publisher", "Publisher");
        literalOptions.put("http://vivoweb.org/ontology/core#ResearchOrganization", "Research Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#Team", "Team");
        literalOptions.put("http://vivoweb.org/ontology/core#School", "School");
        literalOptions.put("http://vivoweb.org/ontology/core#Service","Service");
        literalOptions.put("http://vivoweb.org/ontology/core#StudentOrganization", "Student Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#University", "University");
		return literalOptions;
	}

	@Override
	boolean isShowRoleLabelField(){return true;}
}
