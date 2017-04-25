/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.BaseEditSubmissionPreprocessorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import org.vivoweb.webapp.util.ModelUtils;

public class RoleToActivityPredicatePreprocessor extends RoleToPredicatePreprocessor {
	public RoleToActivityPredicatePreprocessor(EditConfigurationVTwo editConfig, WebappDaoFactory wadf) {
        super(editConfig, wadf);
    }

    protected void setupVariableNames() {
    	this.itemType = "roleActivityType";
    	this.roleToItemPredicate = "roleToActivityPredicate";
    	this.itemToRolePredicate = "activityToRolePredicate";
    }
    
    protected String getItemType(MultiValueEditSubmission submission) {
    	String type = null;
    	Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
		//Get the type of the activity selected
		List<String> itemTypes = urisFromForm.get(itemType);
		//Really should just be one here
		if(itemTypes != null && itemTypes.size() > 0) {
			type = itemTypes.get(0);
		}
		return type;
    }
}
