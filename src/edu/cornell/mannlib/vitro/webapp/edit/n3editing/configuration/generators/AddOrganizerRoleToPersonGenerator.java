/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddOrganizerRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	
	private static String template = "addOrganizerRoleToPerson.ftl";
	
    
	@Override
	String getTemplate() {
		return template;
	}

	@Override
	String getRoleType() {
		return "http://vivoweb.org/ontology/core#OrganizerRole";
	}
	
	@Override
	RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.HARDCODED_LITERALS;
	}
	
	@Override
	String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return null;
	}	

	//Organizer role involves hard-coded options for the "right side" of the role or activity
	@Override
	HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select type");
        literalOptions.put("http://purl.org/NET/c4dm/event.owl#Event", "Event");
        literalOptions.put("http://vivoweb.org/ontology/core#Competition", "Competition");
        literalOptions.put("http://purl.org/ontology/bibo/Conference", "Conference");
        literalOptions.put("http://vivoweb.org/ontology/core#Course", "Course");
        literalOptions.put("http://vivoweb.org/ontology/core#Exhibit", "Exhibit");                     
        literalOptions.put("http://vivoweb.org/ontology/core#Meeting", "Meeting");
        literalOptions.put("http://vivoweb.org/ontology/core#Presentation", "Presentation");
        literalOptions.put("http://vivoweb.org/ontology/core#InvitedTalk", "Invited Talk");
        literalOptions.put("http://purl.org/ontology/bibo/Workshop", "Workshop");
        literalOptions.put("http://vivoweb.org/ontology/core#EventSeries", "Event Series");
        literalOptions.put("http://vivoweb.org/ontology/core#ConferenceSeries", "Conference Series");
        literalOptions.put("http://vivoweb.org/ontology/core#SeminarSeries", "Seminar Series");
        literalOptions.put("http://vivoweb.org/ontology/core#WorkshopSeries", "Workshop Series");
		return literalOptions;
	}

	@Override
	boolean isShowRoleLabelField() {
		return false;
	}
    
}
