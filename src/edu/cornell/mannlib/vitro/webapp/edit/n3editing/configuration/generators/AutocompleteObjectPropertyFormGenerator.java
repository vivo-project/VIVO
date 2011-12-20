/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;

/**
 * Generates the edit configuration for a default property form.
 *
 */
public class AutocompleteObjectPropertyFormGenerator extends DefaultObjectPropertyFormGenerator {

	//The only thing that changes here are the templates
	private Log log = LogFactory.getLog(AutocompleteObjectPropertyFormGenerator.class);

	private String objectPropertyTemplate = "autoCompleteObjectPropForm.ftl";
	private String dataPropertyTemplate = "autoCompleteDataPropForm.ftl";
	
	@Override
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		//Get the edit mode
		formSpecificData.put("editMode", getEditMode(vreq).toString().toLowerCase());
		//We also need the type of the object itself
		formSpecificData.put("objectTypes", StringUtils.join(getTypes(vreq), ","));
		//Get label for individual if it exists
		if(EditConfigurationUtils.getObjectIndividual(vreq) != null) {
			String objectLabel = EditConfigurationUtils.getObjectIndividual(vreq).getName();
			formSpecificData.put("objectLabel", objectLabel);
		}
		formSpecificData.put("sparqlForAcFilter", getSparqlForAcFilter(vreq));
		editConfiguration.setTemplate(objectPropertyTemplate);
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	private List<String> getTypes(VitroRequest vreq) {
		Individual subject = EditConfigurationUtils.getSubjectIndividual(vreq);
		String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
		WebappDaoFactory wDaoFact = vreq.getWebappDaoFactory();
		List<String> types = new ArrayList<String>();
		List <VClass> vclasses = new ArrayList<VClass>();
        vclasses = wDaoFact.getVClassDao().getVClassesForProperty(subject.getVClassURI(),predicateUri);
        for(VClass v: vclasses) {
        	types.add(v.getURI());
        }
        return types;
	}
	
	public EditMode getEditMode(VitroRequest vreq) {
		//In this case, the original jsp didn't rely on FrontEndEditingUtils
		//but instead relied on whether or not the object Uri existed
		String objectUri = EditConfigurationUtils.getObjectUri(vreq);
		EditMode editMode = FrontEndEditingUtils.EditMode.ADD;
		if(objectUri != null && !objectUri.isEmpty()) {
			editMode = FrontEndEditingUtils.EditMode.EDIT;
			
		}
		return editMode;
	}
    
	public String getSparqlForAcFilter(VitroRequest vreq) {
		String subject = EditConfigurationUtils.getSubjectUri(vreq);			
		String predicate = EditConfigurationUtils.getPredicateUri(vreq);
		//Get all objects for existing predicate, filters out results from addition and edit
		String query =  "SELECT ?objectVar WHERE { " + 
			"<" + subject + "> <" + predicate + "> ?objectVar .} ";
		return query;
	}
	
}
