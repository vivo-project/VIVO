/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.XSD;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.BaseEditSubmissionPreprocessorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import org.vivoweb.webapp.util.ModelUtils;

public class RoleToActivityPredicatePreprocessor extends BaseEditSubmissionPreprocessorVTwo {

    private static final Log log = LogFactory.getLog(CreateLabelFromNameFields.class.getName());
    private WebappDaoFactory wadf = null;
    //Need the webapp dao factory to try to figure out what the predicate should be
    public RoleToActivityPredicatePreprocessor(EditConfigurationVTwo editConfig, WebappDaoFactory wadf) {
        super(editConfig);
        this.wadf = wadf;
    }

    public void preprocess(MultiValueEditSubmission submission) {
    	//Query for all statements using the original roleIn predicate replace
    	//with the appropriate roleRealizedIn or roleContributesTo
    	//In addition, need to ensure the inverse predicate is also set correctly
	
    	try {
    		//Get the uris from form
    		Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
    		//Get the type of the activity selected
    		List<String> activityTypes = urisFromForm.get("roleActivityType");
    		//Really should just be one here
    		if(activityTypes != null && activityTypes.size() > 0) {
    			String type = activityTypes.get(0);
    			ObjectProperty roleToActivityProperty = getCorrectProperty(type, wadf);
    			String roleToActivityPredicate = roleToActivityProperty.getURI();
    			String activityToRolePredicate = roleToActivityProperty.getURIInverse();
    			List<String> predicates = new ArrayList<String>();
    			predicates.add(roleToActivityPredicate);

    			List<String> inversePredicates = new ArrayList<String>();
    			inversePredicates.add(activityToRolePredicate);
    			//Populate the two fields in edit submission
    			if(urisFromForm.containsKey("roleToActivityPredicate")) {
    				urisFromForm.remove("roleToActivityPredicate");
    			}
    			
    			urisFromForm.put("roleToActivityPredicate", predicates);
    			
    			if(urisFromForm.containsKey("activityToRolePredicate")) {
    				urisFromForm.remove("activityToRolePredicate");
    			}
    			urisFromForm.put("activityToRolePredicate", inversePredicates);

    		}
    		
        } catch (Exception e) {
            log.error("Error retrieving name values from edit submission.");
        }
        
    }
    
	private ObjectProperty getCorrectProperty(String uri, WebappDaoFactory wadf) {
    	ObjectProperty correctProperty = 	ModelUtils.getPropertyForRoleInClass(uri, wadf);
		return correctProperty;
	}

}
