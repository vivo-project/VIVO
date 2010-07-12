/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

public class PersonHasPositionValidator implements N3Validator {
    
    private static String DUPLICATE_ERROR = "Must select an existing organization or create a new one, not both.";
    private static String MISSING_ORG_ERROR = "Must either select an existing organization or create a new one.";
    private static String MISSING_ORG_TYPE_ERROR = "Must select a type for the new organization.";
    private static String MISSING_ORG_NAME_ERROR = "Must specify a name for the new organization.";
	 
	public Map<String,String> validate(EditConfiguration editConfig, EditSubmission editSub){
//		Map<String,String> existingUris = editConfig.getUrisInScope();
//		Map<String,Literal> existingLiterals = editConfig.getLiteralsInScope();					 
		Map<String,String> urisFromForm = editSub.getUrisFromForm();
		Map<String,Literal> literalsFromForm = editSub.getLiteralsFromForm();
		
		Literal newOrgName = literalsFromForm.get("newOrgName");
		if( newOrgName.getLexicalForm() != null && "".equals(newOrgName.getLexicalForm()) )
			newOrgName = null;
		String newOrgType = urisFromForm.get("newOrgType");
		if( "".equals(newOrgType ) )
			newOrgType = null;
		String organizationUri = urisFromForm.get("organizationUri");
		if( "".equals(organizationUri))
			organizationUri = null;
		
		Map<String,String> errors = new HashMap<String,String>();		
		if( organizationUri != null && (newOrgName != null || newOrgType != null)  ){
			errors.put("newOrgName", DUPLICATE_ERROR);	
			errors.put("organizationUri", DUPLICATE_ERROR);
		} else if ( organizationUri == null && newOrgName == null && newOrgType == null) {
            errors.put("newOrgName", MISSING_ORG_ERROR);   
            errors.put("organizationUri", MISSING_ORG_ERROR);		    		    
		}else if( organizationUri == null && newOrgName != null && newOrgType == null) {
			errors.put("newOrgType", MISSING_ORG_TYPE_ERROR);			
		}else if( organizationUri == null && newOrgName == null && newOrgType != null) {
			errors.put("newOrgName", MISSING_ORG_NAME_ERROR);			
		}
		
		if( errors.size() != 0 )
			return errors;
		else 
			return null;
   }
}