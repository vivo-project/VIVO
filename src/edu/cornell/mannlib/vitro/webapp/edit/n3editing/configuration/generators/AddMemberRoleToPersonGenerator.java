/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.IndividualsViaClassGroupOptions;

public class AddMemberRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	
	private static String template = "addMemberRoleToPerson.ftl";
	private static String VCLASS_GROUP_URI = "http://vivoweb.org/ontology#vitroClassGrouporganizations";
	@Override
	String getTemplate() {
		return template;
	}   
	
	@Override
	String getRoleType() {	
		return "http://vivoweb.org/ontology/core#MemberRole";
	}
	
    @Override
    FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {     
        return new IndividualsViaClassGroupOptions(VCLASS_GROUP_URI)
            .setDefaultOptionLabel( "Select one");		
	}
	
	@Override
	boolean isShowRoleLabelField(){return true;}    
}
