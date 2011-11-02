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
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import org.vivoweb.webapp.util.ModelUtils;

public class RoleToActivityPredicatePreprocessor implements ModelChangePreprocessor {

    private static final Log log = LogFactory.getLog(CreateLabelFromNameFields.class.getName());
    public RoleToActivityPredicatePreprocessor() {
        super();
    }

    public void preprocess(Model retractionsModel, Model additionsModel, HttpServletRequest request) {
    	//Query for all statements using the original roleIn predicate replace
    	//with the appropriate roleRealizedIn or roleContributesTo
    	//In addition, need to ensure the inverse predicate is also set correctly
	
    	try {
    		VitroRequest vreq = new VitroRequest(request);
    		WebappDaoFactory wadf = vreq.getWebappDaoFactory();
    		replacePredicates(retractionsModel, wadf);
    		replacePredicates(additionsModel, wadf);
    		
        } catch (Exception e) {
            log.error("Error retrieving name values from edit submission.");
        }
        
    }
    
    private void replacePredicates(Model inputModel, WebappDaoFactory wadf) {
		executeQueryAndReplace(inputModel, wadf);
	}
  private void executeQueryAndReplace(Model inputModel, WebappDaoFactory wadf) {
    	String queryString= getRoleAndActivityQuery();
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, inputModel);
        ResultSet rs = qe.execSelect();
     
        while(rs.hasNext()) {
        	QuerySolution qs = rs.nextSolution();
        	Resource role = null, activity = null, mostSpecificType = null;
        	role = getResourceFromSolution(qs, "role");
        	activity = getResourceFromSolution(qs, "activity");
        	mostSpecificType = getResourceFromSolution(qs, "mostSpecificType");
        	
        	//Within the input model, replace predicate linking role and activity and vice versa
        	//based on the most specific type of the activity
        	replacePredicatesForRoleAndActivity(inputModel, role, activity, mostSpecificType, wadf);
        	
        }
    }
   private void replacePredicatesForRoleAndActivity(Model inputModel,
			Resource role, Resource activity, Resource mostSpecificType,
			WebappDaoFactory wadf) {
    	Property roleToActivityPredicate = ResourceFactory.createProperty(getGenericRoleToActivityPredicate());
    	Property activityToRolePredicate = ResourceFactory.createProperty(getGenericActivityToRolePredicate());
		if(role != null && activity != null && mostSpecificType != null) {
			
			ObjectProperty newRoleToActivityProperty = getCorrectProperty(mostSpecificType.getURI(), wadf);
			String propertyURI = newRoleToActivityProperty.getURI();
			String inversePropertyURI = newRoleToActivityProperty.getURIInverse();
			//Remove all the old statements connecting role and activity
			inputModel.enterCriticalSection(Lock.WRITE);
			try {
				Model removeRoleToActivityModel = ModelFactory.createDefaultModel();
				removeRoleToActivityModel.add(inputModel.listStatements(
						role, 
						roleToActivityPredicate, 
						activity));
				Model removeActivityToRoleModel = ModelFactory.createDefaultModel();
				removeActivityToRoleModel.add(inputModel.listStatements(
						activity, 
						activityToRolePredicate, 
						role));
				//Add statements
				inputModel.add(inputModel.createStatement(
						role, 
						ResourceFactory.createProperty(propertyURI), 
						activity));
				
				inputModel.add(inputModel.createStatement(
						activity, 
						ResourceFactory.createProperty(inversePropertyURI), 
						role));
				
				//Remove all roleToActivityPredicates and replace with the new predicate
				inputModel.remove(removeRoleToActivityModel);
				//Remove all activity to role predicates and replace with new predicate
				inputModel.remove(removeActivityToRoleModel);
			} catch(Exception ex) {
				log.error("Exception occurred in replacing predicates in model ", ex);
			} finally {
				inputModel.leaveCriticalSection();
			}
		}
		
	}

	private ObjectProperty getCorrectProperty(String uri, WebappDaoFactory wadf) {
    	//ObjectProperty correctProperty = 				ModelUtils.getPropertyForRoleInClass(uri, wadf);
    	ObjectProperty op = new ObjectProperty();
    	op.setURI( "http://vivoweb.org/ontology/core#roleRealizedIn");
		op.setURIInverse("http://vivoweb.org/ontology/core#realizedRole"); 
		return op;
}

	private String getRoleAndActivityQuery() {
    	String roleToActivityPredicate = getGenericRoleToActivityPredicate();
    	String query = "PREFIX core: <http://vivoweb.org/ontology/core#>" +   
		  "SELECT ?role ?activity ?mostSpecificType WHERE { ?role  <" + roleToActivityPredicate + "> ?activity . \n" +
		  "?activity <" + VitroVocabulary.RDF_TYPE + "> ?mostSpecificType. \n" + 
		  "}";
    	return query;
    }
    
    private Resource getResourceFromSolution(QuerySolution qs, String variableName) {
    	Resource resource = null;
    	if(qs.get(variableName) != null && qs.get(variableName).isResource()) {
    		resource = qs.getResource(variableName);
    	}
    	return resource;
    }
    

	//Values used in the forms
    private static String getGenericRoleToActivityPredicate() {
    	return "http://vivoweb.org/ontology/core#roleIn";
    }
    
    private static String getGenericActivityToRolePredicate() {
    	return "http://vivoweb.org/ontology/core#relatedRole";

    }
}
