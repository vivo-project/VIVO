/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

/**
 * Generates the edit configuration for a default property form.
 *
 */
public class VIVODefaultAddMissingIndividualFormGenerator extends DefaultAddMissingIndividualFormGenerator {

	private Log log = LogFactory.getLog(VIVODefaultAddMissingIndividualFormGenerator.class);

    protected Map<String, String> generateNewResources(VitroRequest vreq) {
		Map<String, String> newResources = super.generateNewResources(vreq);
		newResources.put("newVcardInd", null);
		newResources.put("newVcardName", null);
		return newResources;
	}

    protected List<String> getN3Prefixes() {
    	List<String> prefixStrings = super.getN3Prefixes();
    	prefixStrings.add("@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .");
    	return prefixStrings;
    }

    protected String getN3ForName() {
    	return "?" + objectVarName + " rdfs:label ?label .";
    }

    protected List<String> generateN3Optional(VitroRequest vreq) {
    	List<String> n3Optional = super.generateN3Optional(vreq);
    	n3Optional.add(getN3PrefixesAsString()
    	    + "?" + objectVarName +  "<http://purl.obolibrary.org/obo/ARG_2000028> ?newVcardInd . \n"
            + " ?newVcardInd <http://purl.obolibrary.org/obo/ARG_2000029> ?" + objectVarName +  " . \n"
            + " ?newVcardInd a vcard:Individual . \n"
            + " ?newVcardInd vcard:hasName  ?newVcardName . \n"
            + " ?newVcardName a vcard:Name . \n"
            + " ?newVcardName vcard:givenName ?firstName . \n"
            + " ?newVcardName vcard:familyName ?lastName . \n");
    	n3Optional.add(getN3PrefixesAsString()
    	    + "?" + objectVarName +  "<http://purl.obolibrary.org/obo/ARG_2000028> ?newVcardInd . \n"
            + " ?newVcardInd a vcard:Individual . \n"
            + " ?newVcardInd vcard:hasName  ?newVcardName . \n"
            + " ?newVcardName a vcard:Name . \n"
            + " ?newVcardName <http://vivoweb.org/ontology/core#middleName> ?middleName .");
    	return n3Optional;
    }

    protected void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();
    	List<String> literalsOnForm = new ArrayList<String>();
    	literalsOnForm.add("label");
    	literalsOnForm.add("firstName");
    	literalsOnForm.add("middleName");
    	literalsOnForm.add("lastName");
    	editConfiguration.setUrisOnform(urisOnForm);
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }

    protected void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	if(EditConfigurationUtils.isObjectProperty(EditConfigurationUtils.getPredicateUri(vreq), vreq)) {

    	    //make name field
    	    FieldVTwo labelField = new FieldVTwo();
	        labelField.setName("label");

    	    FieldVTwo firstNameField = new FieldVTwo();
	        firstNameField.setName("firstName");

    	    FieldVTwo middleNameField = new FieldVTwo();
	        middleNameField.setName("middleName");

    	    FieldVTwo lastNameField = new FieldVTwo();
	        lastNameField.setName("lastName");

	        List<String> validators = new ArrayList<String>();
	        validators.add("nonempty");
	        if(!isPersonType(vreq)) {
	            labelField.setValidators(validators);
	        }
	        if(isPersonType(vreq)) {
	            firstNameField.setValidators(validators);
	            lastNameField.setValidators(validators);
	        }

	        fields.put(labelField.getName(), labelField);
	        fields.put(firstNameField.getName(), firstNameField);
	        fields.put(middleNameField.getName(), middleNameField);
	        fields.put(lastNameField.getName(), lastNameField);

    	} else {
    		log.error("Is not object property so fields not set");
    	}

    	editConfiguration.setFields(fields);
    }

    //Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("typeName", getTypeName(vreq));
		//Put in whether or not person type
		if(isPersonType(vreq)) {
			//Doing this b/c unsure how freemarker will handle boolean value from JAVA
			formSpecificData.put("isPersonType", "true");
		} else {
			formSpecificData.put("isPersonType", "false");

		}
		editConfiguration.setFormSpecificData(formSpecificData);
	}

	private String getTypeName(VitroRequest vreq) {
		String typeOfNew = getTypeOfNew(vreq);
		VClass type = vreq.getWebappDaoFactory().getVClassDao().getVClassByURI(typeOfNew);
		return type.getName();
	}

	public String getFOAFPersonClassURI() {
		return "http://xmlns.com/foaf/0.1/Person";
	}

	public boolean isPersonType(VitroRequest vreq) {
		WebappDaoFactory wdf = vreq.getWebappDaoFactory();
		Boolean isPersonType = Boolean.FALSE;
		String foafPersonType = getFOAFPersonClassURI();
		String typeOfNew = getTypeOfNew(vreq);
	    List<String> superTypes = wdf.getVClassDao().getAllSuperClassURIs(typeOfNew);
	    //add the actual type as well so we can add that for the list to be checked
	    superTypes.add(typeOfNew);
	    if( superTypes != null ){
	    	for( String typeUri : superTypes){
	    		if( foafPersonType.equals(typeUri)) {
	    			isPersonType = Boolean.TRUE;
	    			break;
	    		}
	    	}
	    }
	    return isPersonType;
	}
}
