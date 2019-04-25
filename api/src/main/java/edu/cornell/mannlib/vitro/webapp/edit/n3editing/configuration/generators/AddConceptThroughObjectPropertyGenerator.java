/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.IndividualsViaSearchQueryOptions;

/**
 * This generator is for the case where a new concept is being added for an object property other than research/subject areas where the
 * default object property form generator would work instead of the generator for managing concepts.
 * In this case, we don't want the dropdown list for types for "add a new item of this type" to show concept subclasses, so we are overriding
 * the fields to just include the Concept class.
 */
public class AddConceptThroughObjectPropertyGenerator extends DefaultObjectPropertyFormGenerator implements EditConfigurationGenerator {

	private Log log = LogFactory.getLog(AddConceptThroughObjectPropertyGenerator.class);

	@Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {
		EditConfigurationVTwo editConfig = super.getEditConfiguration(vreq, session);
		//If this isn't adding a new individual, then override  template/types
		if(!VIVODefaultAddMissingIndividualFormGenerator.isCreateNewIndividual(vreq, session)) {
			//return rangetypes in form specific data
			editConfig.addFormSpecificData("createNewTypes", getCreateNewTypesOptions(vreq));
			//override templates with ones that will override create new types portion
			editConfig.setTemplate(getTemplate(vreq));
		}
		return editConfig;
	}

	private HashMap<String, String> getCreateNewTypesOptions(VitroRequest vreq) {
		HashMap<String, String> options = new HashMap<String, String>();
		List<VClass> rangeTypes = getRangeTypes(vreq);
		for(VClass v: rangeTypes) {
			options.put(v.getURI(), v.getName());
		}
		return options;
	}

    //We will override range types as well so that autocomplete and other fields dependent on range
    //will only consider the main concept type to be the range type
    @Override
    protected List<VClass> getRangeTypes(VitroRequest vreq) {
        // This first part needs a WebappDaoFactory with no filtering/RDFService
        // funny business because it needs to be able to retrieve anonymous union
        // classes by their "pseudo-bnode URIs".
        // Someday we'll need to figure out a different way of doing this.
        //WebappDaoFactory ctxDaoFact = ModelAccess.on(
        //        vreq.getSession().getServletContext()).getWebappDaoFactory();
        WebappDaoFactory ctxDaoFact = vreq.getLanguageNeutralWebappDaoFactory();

        List<VClass> types = new ArrayList<VClass>();
    	Individual subject = EditConfigurationUtils.getSubjectIndividual(vreq);
   		String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
   		String rangeUri = EditConfigurationUtils.getRangeUri(vreq);
   		if (rangeUri != null) {
   		    VClass rangeVClass = ctxDaoFact.getVClassDao().getVClassByURI(rangeUri);
   		    if (!rangeVClass.isUnion()) {
   		        types.add(rangeVClass);
   		    } else {
				types.addAll(rangeVClass.getUnionComponents());
   		    }
	        return types;
   		} else {
   			//This should never happen
   			log.warn("Range not found for this property so employing SKOS concept class");
   			String vclassURI = "http://www.w3.org/2004/02/skos/core#Concept";
   			VClass rangeVClass = ctxDaoFact.getVClassDao().getVClassByURI(vclassURI);
   			types.add(rangeVClass);
   		}

        return types;
	}

    //Should override the method in default object property
    private String getTemplate(
			VitroRequest vreq) {

    	String acObjectPropertyTemplate = "addConceptThroughObjectPropertyAutoComplete.ftl";
    	String objectPropertyTemplate = "addConceptThroughObjectPropertyForm.ftl";
    	String template = objectPropertyTemplate;
		if( doAutoComplete )
			template = acObjectPropertyTemplate;
		return template;

	}

    @Override
    protected void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri, List<VClass> rangeTypes) throws Exception {
		FieldVTwo field = new FieldVTwo();
    	field.setName("objectVar");

    	List<String> validators = new ArrayList<String>();
    	validators.add("nonempty");
    	field.setValidators(validators);

    	if( ! doAutoComplete ){
    		List<String> types = new ArrayList<String>();
    		for(VClass v: rangeTypes) {
    			types.add(v.getURI());
    		}
    		String[] typesArray = types.toArray(new String[types.size()]);
    		field.setOptions( new IndividualsViaSearchQueryOptions(
    				getSubjectUri(),
    				predicateUri,
    				getObjectUri(),
    	        	typesArray));
    	}else{
    		field.setOptions(null);
    	}

    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	fields.put(field.getName(), field);

    	editConfiguration.setFields(fields);
    }

}
