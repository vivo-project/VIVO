/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;
/**
 * Generates the edit configuration for adding a Role to a Person.  
  
  Stage one is selecting the type of the non-person thing 
  associated with the Role with the intention of reducing the 
  number of Individuals that the user has to select from.
  Stage two is selecting the non-person Individual to associate
  with the Role. 

  This is intended to create a set of statements like:

  ?person  core:hasResearchActivityRole ?newRole.
  ?newRole rdf:type core:ResearchActivityRole ;         
           roleToActivityPredicate ?someActivity .
  ?someActivity rdf:type core:ResearchActivity .
  ?someActivity rdfs:label "activity title" .
  
  
  Each subclass of the abstract two stage Generator class will have the option of overriding certain
  methods, and must always implement the following methods:
  getRoleType
  getRoleActivityTypeOptionsType
  getRoleActivityTypeObjectClassUri
  getRoleActivityTypeLiteralOptions
     
 *
 */
public class AddEditorRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	private static String TEMPLATE = "addEditorRoleToPerson.ftl";
	private static String OPTION_CLASS_URI = "http://purl.org/ontology/bibo/Collection";
	
	@Override
	String getTemplate(){ return TEMPLATE; }

	@Override
	String getRoleType() {
		return "http://vivoweb.org/ontology/core#EditorRole";
	}

    @Override
    FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {
        return new 
        ChildVClassesOptions(OPTION_CLASS_URI)
            .setDefaultOptionLabel("Select type");
    }
    
	/** Do not show the role label field for the AddEditorRoleToPerson form */
	@Override	
	boolean isShowRoleLabelField() { return false;	}

}
