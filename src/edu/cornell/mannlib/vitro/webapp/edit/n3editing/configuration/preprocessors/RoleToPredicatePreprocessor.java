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
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import org.vivoweb.webapp.util.ModelUtils;

public abstract class RoleToPredicatePreprocessor extends BaseEditSubmissionPreprocessorVTwo {

    protected static final Log log = LogFactory.getLog(RoleToPredicatePreprocessor.class.getName());
    protected WebappDaoFactory wadf = null;
    protected static String itemType;
    protected static String roleToItemPredicate;
    protected static String itemToRolePredicate;
    //Need the webapp dao factory to try to figure out what the predicate should be
    public RoleToPredicatePreprocessor(EditConfigurationVTwo editConfig, WebappDaoFactory wadf) {
        super(editConfig);
        this.wadf = wadf;
        setupVariableNames();
    }

    //Instantiate itemType etc. based on which version of preprocessor required
    abstract protected void setupVariableNames();

	public void preprocess(MultiValueEditSubmission submission, VitroRequest vreq) {
    	//Query for all statements using the original roleIn predicate replace
    	//with the appropriate roleRealizedIn or roleContributesTo
    	//In addition, need to ensure the inverse predicate is also set correctly
	
    	try {
    		//Get the uris from form
    		String type = getItemType(submission);
    		Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
    		if(type != null) {
    			ObjectProperty roleToItemProperty = getCorrectProperty(type, wadf);
    			String roleToItemPredicateURI = roleToItemProperty.getURI();
    			String itemToRolePredicateURI = roleToItemProperty.getURIInverse();
    			List<String> predicates = new ArrayList<String>();
    			predicates.add(roleToItemPredicateURI);

    			List<String> inversePredicates = new ArrayList<String>();
    			inversePredicates.add(itemToRolePredicateURI);
    			//Populate the two fields in edit submission
    			if(urisFromForm.containsKey(roleToItemPredicate)) {
    				urisFromForm.remove(roleToItemPredicate);
    			}
    			
    			urisFromForm.put(roleToItemPredicate, predicates);
    			
    			if(urisFromForm.containsKey(itemToRolePredicate)) {
    				urisFromForm.remove(itemToRolePredicate);
    			}
    			urisFromForm.put(itemToRolePredicate, inversePredicates);

    		}
    		
        } catch (Exception e) {
            log.error("Error retrieving name values from edit submission.");
        }
        
    }
	
	abstract protected String getItemType(MultiValueEditSubmission submission);
    
	private ObjectProperty getCorrectProperty(String uri, WebappDaoFactory wadf) {
    	ObjectProperty correctProperty = 	ModelUtils.getPropertyForRoleInClass(uri, wadf);
		return correctProperty;
	}

}
