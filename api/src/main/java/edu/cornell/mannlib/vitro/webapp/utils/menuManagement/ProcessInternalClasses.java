/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.utils.menuManagement;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.DataGetterUtils;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter;

/*
 * Handle processing of data retrieved from IndividualsForClasses data getter to return to form template
 * and handle processing of form submission to create the appropriate individuals for classes data getter
 */
public class ProcessInternalClasses extends ProcessIndividualsForClasses {
    private static final Log log = LogFactory.getLog(ProcessInternalClasses.class);

   /**Retrieve and populate**/

  //Based on institutional internal page and not general individualsForClasses
    @Override
    protected void populateRestrictedClasses(Map<String, Object> pageData, Map<String, Object> templateData) {
    	//for internal page, restrict results by internal is true or false, otherwise get
		//actual restriction classes?
		//Get internal class
		String internalClassUris = (String) pageData.get("internalClass");
		if(internalClassUris != null && !internalClassUris.isEmpty()) {
			templateData.put("isInternal", "true");
		}
    }

	/**Process submission**/
	//Check and see if we should use this process
	//Use this if either internal class is selected or all classes have been selected
	public boolean useProcessor(VitroRequest vreq) {
		return(internalClassSelected(vreq) || !allClassesSelected(vreq));
	}
	public  Model processSubmission(VitroRequest vreq, Resource dataGetterResource) {
		String dataGetterTypeUri = DataGetterUtils.generateDataGetterTypeURI(InternalClassesDataGetter.class.getName());
		String[] selectedClasses = vreq.getParameterValues("classInClassGroup");
		Model dgModel = ModelFactory.createDefaultModel();
		dgModel.add(dgModel.createStatement(dataGetterResource,
				RDF.type,
				ResourceFactory.createResource(dataGetterTypeUri)));
		for(String classUri: selectedClasses) {
			dgModel.add(dgModel.createStatement(
					dataGetterResource,
					ResourceFactory.createProperty(DisplayVocabulary.GETINDIVIDUALS_FOR_CLASS),
					ResourceFactory.createResource(classUri)));
		}

		//Also check if internal class checked
		if(internalClassSelected(vreq)) {
			dgModel.add(dgModel.createStatement(
					dataGetterResource,
					ResourceFactory.createProperty(DisplayVocabulary.RESTRICT_RESULTS_BY_INTERNAL),
					dgModel.createLiteral("true")));
		}
		return dgModel;
	}

	private  boolean allClassesSelected(VitroRequest vreq) {
			String allClasses = vreq.getParameter("allSelected");
			return (allClasses != null && !allClasses.isEmpty());
	}

	private  boolean internalClassSelected(VitroRequest vreq) {
	    String internalClass = vreq.getParameter("display-internalClass");
	    return (internalClass != null && !internalClass.isEmpty());
	}

}
