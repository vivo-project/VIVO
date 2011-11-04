/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.webapp.util.ModelUtils;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import com.hp.hpl.jena.ontology.OntModel;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.Field;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.RoleToActivityPredicatePreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.RdfLiteralHash;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditN3GeneratorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.SelectListGeneratorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.AddRoleUtils;
/**
 * Generates the edit configuration for adding a Role to a Person.  
  
  Stage one is selecting the type of the non-person thing 
  associated with the Role with the intention of reducing the 
  number of Individuals that the user has to select from.
  Stage two is selecting the non-person Individual to associate
  with the Role. 

  This is intended to create a set of statements like:

  ?person  core:hasResearchActivityRole ?newRole.
  ?newRole rdf:type core:ResearchActivityRole ;         
           roleToActivityPredicate ?someActivity .
  ?someActivity rdf:type core:ResearchActivity .
  ?someActivity rdfs:label "activity title" .
  
  
  Important: This form cannot be directly used as a custom form.  It has parameters that must be set.
  See addClinicalRoleToPerson.jsp for an example.
     
    roleToActivityPredicate and activityToRolePredicate are both dependent on the type of
    the activity itself. For a new statement, the predicate type is not known.
    For an existing statement, the predicate is known but may change based on the type of the activity newly selected.  
     
 *
 */
public abstract class AddRoleToPersonTwoStageGenerator implements EditConfigurationGenerator {
	
	private Log log = LogFactory.getLog(AddRoleToPersonTwoStageGenerator.class);
	private boolean isObjectPropForm = false;
	private String subjectUri = null;
	private String predicateUri = null;
	private String objectUri = null;
	private String datapropKeyStr= null;
	private int dataHash = 0;
	private DataPropertyStatement dps = null;
	private String dataLiteral = null;
	private String template = "addRoleToPersonTwoStage.ftl";
	private static HashMap<String,String> defaultsForXSDtypes ;
	
	//Types of options to populate drop-down for types for the "right side" of the role
	public static enum RoleActivityOptionTypes {
		VCLASSGROUP,
		CHILD_VCLASSES,
		HARDCODED_LITERALS
	};
	
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
    	EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
    	//Set n3 generator
    	editConfiguration.setN3Generator(new EditN3GeneratorVTwo(editConfiguration));
    	
    	//process subject, predicate, object parameters
    	this.initProcessParameters(vreq, session, editConfiguration);
    	
      	//Assumes this is a simple case of subject predicate var
    	editConfiguration.setN3Required(this.generateN3Required(vreq));
    	    	
    	//n3 optional
    	editConfiguration.setN3Optional(this.generateN3Optional());
    	
    	//Todo: what do new resources depend on here?
    	//In original form, these variables start off empty
    	editConfiguration.setNewResources(generateNewResources(vreq));
    	//In scope
    	this.setUrisAndLiteralsInScope(editConfiguration, vreq);
    	
    	//on Form
    	this.setUrisAndLiteralsOnForm(editConfiguration, vreq);
    	
    	editConfiguration.setFilesOnForm(new ArrayList<String>());
    	
    	//Sparql queries
    	this.setSparqlQueries(editConfiguration, vreq);
    	
    	//set fields
    	setFields(editConfiguration, vreq, EditConfigurationUtils.getPredicateUri(vreq));
    	
    //	No need to put in session here b/c put in session within edit request dispatch controller instead
    	//placing in session depends on having edit key which is handled in edit request dispatch controller
    //	editConfiguration.putConfigInSession(editConfiguration, session);

    	prepareForUpdate(vreq, session, editConfiguration);
    	
    	//Form title and submit label now moved to edit configuration template
    	//TODO: check if edit configuration template correct place to set those or whether
    	//additional methods here should be used and reference instead, e.g. edit configuration template could call
    	//default obj property form.populateTemplate or some such method
    	//Select from existing also set within template itself
    	setTemplate(editConfiguration, vreq);
    	//Set edit key
    	setEditKey(editConfiguration, vreq);
    	//Add validator
        editConfiguration.addValidator(new DateTimeIntervalValidationVTwo("startField","endField") ); 
        //Add preprocessors
        addPreprocessors(editConfiguration, vreq.getWebappDaoFactory());
        //Adding additional data, specifically edit mode
        addFormSpecificData(editConfiguration, vreq);
    	return editConfiguration;
    }
    
 

	private void setEditKey(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	String editKey = EditConfigurationUtils.getEditKey(vreq);	
    	editConfiguration.setEditKey(editKey);
    }
    
	protected void setTemplate(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
    	editConfiguration.setTemplate(template);
		
	}

	//Initialize setup: process parameters
	//There will be specialized parameters as well, we may include them here or in a
	//separate method
    private void initProcessParameters(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	String formUrl = EditConfigurationUtils.getFormUrl(vreq);

    	subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
    	predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
    
    	editConfiguration.setFormUrl(formUrl);
    	
    	editConfiguration.setUrlPatternToReturnTo("/individual");
    	
    	editConfiguration.setVarNameForSubject("person");
    	editConfiguration.setSubjectUri(subjectUri);
    	editConfiguration.setEntityToReturnTo(subjectUri);
    	editConfiguration.setVarNameForPredicate("rolePredicate");
    	editConfiguration.setPredicateUri(predicateUri);
    	//by definition, this is an object property
		this.initObjectParameters(vreq);
		this.processObjectPropForm(vreq, editConfiguration);
    	
    }
    

    
	private void initObjectParameters(VitroRequest vreq) {
		//in case of object property
    	objectUri = EditConfigurationUtils.getObjectUri(vreq);
	}

	private void processObjectPropForm(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
    	editConfiguration.setVarNameForObject("role");    	
    	editConfiguration.setObject(objectUri);
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//TODO: Check if null in case no object uri exists but this is still an object property
    	if(objectUri != null) {
    		editConfiguration.setObjectResource(true);
    	}
    }
   
    
   /*
    * N3 Required and Optional Generators as well as supporting methods 
    */
	
	private String getPrefixesString() {
		//TODO: Include dynamic way of including this
		return "@prefix core: <http://vivoweb.org/ontology/core#> .";
	}
	
	//TODO: Check if single string or multiple strings - check rdfslabel form etc. for prefix
	//processing
    private List<String> generateN3Required(VitroRequest vreq) {
    	List<String> n3ForEdit = new ArrayList<String>();
    	String editString = getPrefixesString();
    	editString += "?person ?rolePredicate ?role .";
    	editString += "?role a <" + getRoleType(vreq) + "> .";
    	editString += "?role " + getRoleToActivityPredicate(vreq) + " ?roleActivity .";
    	editString += "?roleActivity " + getActivityToRolePredicate(vreq) + " ?role .";
    	n3ForEdit.add(editString);
    	return n3ForEdit;
    }
    
   
	private List<String> generateN3Optional() {
    	List<String> n3Optional = new ArrayList<String>();
    	//n3 for activity label
		n3Optional.add(getN3ForActivityLabel());
		//n3 for activity type
		n3Optional.add(getN3ForActivityType());
		//n3 for inverse
		n3Optional.add("?role ?inverseRolePredicate ?person .");
		//N3ForStart
		n3Optional.addAll(getN3ForStart());
		//N3 For End
		n3Optional.addAll(getN3ForEnd());
		//role label assertion
		n3Optional.add(getN3RoleLabelAssertion());
    	return n3Optional;	
    }
	
	
    public String getN3ForActivityLabel() {
    	return "?roleActivity <" + RDFS.label.getURI() + "> ?activityLabel .";
    }
    
    public String getN3ForActivityType() {
    	return "?roleActivity a ?roleActivityType .";
    }
    
    public String getN3RoleLabelAssertion() {
    	return "?role <" + RDFS.label.getURI() + "> ?roleLabel .";
    }
	
	//Method b/c used in two locations, n3 optional and n3 assertions
	private List<String> getN3ForStart() {
		List<String> n3ForStart = new ArrayList<String>();
		n3ForStart.add("?role  <" + getRoleToIntervalURI() + "> ?intervalNode ." +     
			    "?intervalNode  <" + RDF.type.getURI() + "> <" + getIntervalTypeURI() + "> ." + 
			    "?intervalNode <" + getIntervalToStartURI() + "> ?startNode ." +     
			    "?startNode  <" + RDF.type.getURI() + "> <" + getDateTimeValueTypeURI() + "> ." + 
			    "?startNode  <" + getDateTimeValueURI() + "> ?startField-value ." + 
			    "?startNode  <" + getDateTimePrecisionURI() + "> ?startField-precision .");
		return n3ForStart;
	}
	
	private List<String> getN3ForEnd() {
		List<String> n3ForEnd = new ArrayList<String>();
		n3ForEnd.add("?role      <" + getRoleToIntervalURI() + "> ?intervalNode .  " +   
			    "?intervalNode  <" + RDF.type.getURI() + "> <" + getIntervalTypeURI() + "> ." + 
			    "?intervalNode <" + getIntervalToEndURI() + "> ?endNode ." + 
			    "?endNode  <" + RDF.type.getURI() + "> <" + getDateTimeValueTypeURI() + "> ." + 
			    "?endNode  <" + getDateTimeValueURI() + "> ?endField-value ." + 
			    "?endNode  <" + getDateTimePrecisionURI() + "> ?endField-precision .");
		return n3ForEnd;
		
	}
	
	
	/*
	 * Get new resources
	 */
	 private Map<String, String> generateNewResources(VitroRequest vreq) {
			HashMap<String, String> newResources = new HashMap<String, String>();
			//TODO: Get default namespace
			String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
			newResources.put("role", defaultNamespace + "individual");
			newResources.put("roleActivity", defaultNamespace + "individual");
			newResources.put("intervalNode", defaultNamespace + "individual");
			newResources.put("startNode", defaultNamespace + "individual");
			newResources.put("endNode", defaultNamespace + "individual");
			return newResources;
		}
    

	
	
	/*
	 * Set URIS and Literals In Scope and on form and supporting methods
	 */
    
    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
    	//note that at this point the subject, predicate, and object var parameters have already been processed
    	//these two were always set when instantiating an edit configuration object from json,
    	//although the json itself did not specify subject/predicate as part of uris in scope
    	urisInScope.put(editConfiguration.getVarNameForSubject(), 
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(), 
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	//Setting inverse role predicate
    	urisInScope.put("inverseRolePredicate", getInversePredicate(vreq));
    	
    	
    	editConfiguration.setUrisInScope(urisInScope);
    	//Uris in scope include subject, predicate, and object var
    	//literals in scope empty initially, usually populated by code in prepare for update
    	//with existing values for variables
    	editConfiguration.setLiteralsInScope(new HashMap<String, List<Literal>>());
    }
    
    private List<String> getInversePredicate(VitroRequest vreq) {
		List<String> inversePredicateArray = new ArrayList<String>();
		ObjectProperty op = EditConfigurationUtils.getObjectProperty(vreq);
		if(op != null && op.getURIInverse() != null) {
			inversePredicateArray.add(op.getURIInverse());
		}
		return inversePredicateArray;
	}

	//n3 should look as follows
    //?subject ?predicate ?objectVar 
    
    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();
    	List<String> literalsOnForm = new ArrayList<String>();
    	//add role activity and roleActivityType to uris on form
    	urisOnForm.add("roleActivity");
    	urisOnForm.add("roleActivityType");
    	//Also adding the predicates
    	//TODO: Check how to override this in case of default parameter? Just write hidden input to form?
    	urisOnForm.add("roleToActivityPredicate");
    	urisOnForm.add("activityToRolePredicate");
    	editConfiguration.setUrisOnform(urisOnForm);
    	//activity label and role label are literals on form
    	literalsOnForm.add("activityLabel");
    	literalsOnForm.add("roleLabel");
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }
    
    
    /**
     * Set SPARQL Queries and supporting methods
     */
    
    
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	//Sparql queries defining retrieval of literals etc.
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    	
    	Map<String, String> urisInScope = new HashMap<String, String>();
    	editConfiguration.setSparqlForAdditionalUrisInScope(urisInScope);
    	
    	editConfiguration.setSparqlForExistingLiterals(generateSparqlForExistingLiterals(vreq));
    	editConfiguration.setSparqlForExistingUris(generateSparqlForExistingUris(vreq));
    }
    
    
    //Get page uri for object
    private HashMap<String, String> generateSparqlForExistingUris(VitroRequest vreq) {
    	HashMap<String, String> map = new HashMap<String, String>();
    	//Queries for role activity, activity type query, interval node, start node, end node, start field precision, endfield precision
    	map.put("roleActivity", getRoleActivityQuery(vreq));
    	map.put("roleActivityType", getActivityTypeQuery(vreq));
    	map.put("intervalNode", getIntervalNodeQuery(vreq));
    	map.put("startNode", getStartNodeQuery(vreq));
    	map.put("endNode", getEndNodeQuery(vreq));
    	map.put("startField-precision", getStartPrecisionQuery(vreq));
    	map.put("endField-precision", getEndPrecisionQuery(vreq));
    	//Also need sparql queries for roleToActivityPredicate and activityToRolePredicate
    	map.put("roleToActivityPredicate", getRoleToActivityPredicateQuery(vreq));
    	map.put("activityToRolePredicate", getActivityToRolePredicateQuery(vreq));

    	return map;
    }
    
    private String getActivityToRolePredicateQuery(VitroRequest vreq) {
    	String query = "SELECT ?existingActivityToRolePredicate \n " + 
		"WHERE { \n" +
	      "?roleActivity ?existingActivityToRolePredicate ?role .";
		//Get possible predicates
		List<String> addToQuery = new ArrayList<String>();
		List<String> predicates = getPossibleActivityToRolePredicates();
		for(String p:predicates) {
			addToQuery.add("(?existingActivityToRolePredicate=<" + p + ">)");
		}
		query += "FILTER (" + StringUtils.join(addToQuery, " || ") + ")";
		query += "}";
		return query;
	}



	private String getRoleToActivityPredicateQuery(VitroRequest vreq) {
		String query = "SELECT ?existingRoleToActivityPredicate \n " + 
		"WHERE { \n" +
	      "?role ?existingRoleToActivityPredicate ?roleActivity .";
		//Get possible predicates
		query += getFilterRoleToActivityPredicate("existingRoleToActivityPredicate");
		query += "}";
		return query;
	}



	private String getEndPrecisionQuery(VitroRequest vreq) {
		String query = "SELECT ?existingEndPrecision WHERE {" +
		      "?role <" + getRoleToIntervalURI() + "> ?intervalNode ." +
		    	      "?intervalNode <" + VitroVocabulary.RDF_TYPE + "> <" + getIntervalTypeURI() + "> ." +
		    	      "?intervalNode <" + getIntervalToEndURI() + "> ?endNode ." +
		    	      "?endNode <" + VitroVocabulary.RDF_TYPE + "> <" + getDateTimeValueTypeURI() + "> .  " +        
		    	      "?endNode <" + getDateTimePrecisionURI() + "> ?existingEndPrecision . }";
		return query;
	}

	private String getStartPrecisionQuery(VitroRequest vreq) {
		String query =  "SELECT ?existingStartPrecision WHERE {" +
	      "?role <" + getRoleToIntervalURI() + "> ?intervalNode ." +
	    	      "?intervalNode <" + VitroVocabulary.RDF_TYPE + "> <" + getIntervalTypeURI() + "> ." +
	    	      "?intervalNode <" + getIntervalToStartURI() + "> ?startNode ." +
	    	      "?startNode <" + VitroVocabulary.RDF_TYPE + "> <" + getDateTimeValueTypeURI() + "> .  " +        
	    	      "?startNode <" + getDateTimePrecisionURI() + "> ?existingStartPrecision . }";
		return query;
	}

	private String getEndNodeQuery(VitroRequest vreq) {
		String query =  "SELECT ?existingEndNode WHERE {"+
		      "?role <" + getRoleToIntervalURI() + "> ?intervalNode ."+
		    	      "?intervalNode <" + VitroVocabulary.RDF_TYPE + "> <" + getIntervalTypeURI() + "> ."+
		    	     " ?intervalNode <" + getIntervalToEndURI() + "> ?existingEndNode . "+
		    	      "?existingEndNode <" + VitroVocabulary.RDF_TYPE + "> <" + getDateTimeValueTypeURI() + "> .}";      
		return query;
	}

	private String getStartNodeQuery(VitroRequest vreq) {
		String query =  "SELECT ?existingStartNode WHERE {"+ 
		      "?role <" + getRoleToIntervalURI() + "> ?intervalNode ."+ 
		      "?intervalNode <" + VitroVocabulary.RDF_TYPE + "> <" + getIntervalTypeURI() + "> ."+ 
		      "?intervalNode <" + getIntervalToStartURI() + "> ?existingStartNode . "+ 
		      "?existingStartNode <" + VitroVocabulary.RDF_TYPE + "> <" + getDateTimeValueTypeURI() + "> .}";      
		return query;
	}

	private String getIntervalNodeQuery(VitroRequest vreq) {
		String query =  "SELECT ?existingIntervalNode WHERE { " + 
	          "?role <" + getRoleToIntervalURI() + "> ?existingIntervalNode . " + 
	          " ?existingIntervalNode <" + VitroVocabulary.RDF_TYPE + "> <" + getIntervalTypeURI() + "> . }";
		return query;
	}

	
	/*
	 * The activity type query results must be limited to the values in the activity type select element. 
	 * Sometimes the query returns a superclass such as owl:Thing instead.
	 * Make use of vitro:mostSpecificType so that, for example, an individual is both a 
	 * core:InvitedTalk and a core:Presentation, core:InvitedTalk is selected.
	 * vitro:mostSpecificType alone may not suffice, since it does not guarantee that the value returned
	 * is in the select list.
	 * We could still have problems if the value from the select list is not a vitro:mostSpecificType, 
	 * but that is unlikely.
	 */
	//This method had some code already setup in the jsp file
	private String getActivityTypeQuery(VitroRequest vreq) {
		String activityTypeQuery = null;

		//roleActivityType_optionsType: This gets you whether this is a literal
		//
		RoleActivityOptionTypes optionsType = getRoleActivityTypeOptionsType(vreq);

	    // Note that this value is overloaded to specify either object class uri or classgroup uri
	    String objectClassUri = getRoleActivityTypeObjectClassUri(vreq);
	    
	    if (StringUtils.isNotBlank(objectClassUri)) { 
	        log.debug("objectClassUri = " + objectClassUri);
	        
			if (RoleActivityOptionTypes.VCLASSGROUP.equals(optionsType)) {
			    activityTypeQuery = getClassgroupActivityTypeQuery(vreq);
			    activityTypeQuery = QueryUtils.subUriForQueryVar(activityTypeQuery, "classgroup", objectClassUri);    	
			    
			} else if (RoleActivityOptionTypes.CHILD_VCLASSES.equals(optionsType)) { 
			    activityTypeQuery = getSubclassActivityTypeQuery(vreq);
			    activityTypeQuery = QueryUtils.subUriForQueryVar(activityTypeQuery, "objectClassUri", objectClassUri); 
			    
			} else {
			    activityTypeQuery = getDefaultActivityTypeQuery(vreq);  
			}
			
		// Select options are hardcoded
		} else if (RoleActivityOptionTypes.HARDCODED_LITERALS.equals(optionsType)) { 	    
		  
			//literal options
	        HashMap<String, String> typeLiteralOptions = getRoleActivityTypeLiteralOptions(vreq);
	        if (typeLiteralOptions.size() > 0) {           
	            try {
	                List<String> typeUris = new ArrayList<String>();
	                Set<String> optionUris = typeLiteralOptions.keySet();
	                for(String uri: optionUris) {
	                	if(!uri.isEmpty()) {
	                		typeUris.add("(?existingActivityType = <" + uri + ">)");
	                	}
	                }
	                String typeFilters = "FILTER (" + StringUtils.join(typeUris, "||") + ")";
	                String defaultActivityTypeQuery = getDefaultActivityTypeQuery(vreq);
	                activityTypeQuery = defaultActivityTypeQuery.replaceAll("}$", "") + typeFilters + "}";
	            } catch (Exception e) {
	                activityTypeQuery = getDefaultActivityTypeQuery(vreq);
	            }

		    } else { 
		        activityTypeQuery = getDefaultActivityTypeQuery(vreq);	    
		    } 

		} else {
		    activityTypeQuery = getDefaultActivityTypeQuery(vreq);   
		}

	    //The replacement of activity type query's predicate was only relevant when we actually
	    //know which predicate is definitely being used here
	    //Here we have multiple values possible for predicate so the original 
	    //Replacement should only happen when we have an actual predicate
	    
		String replaceRoleToActivityPredicate = getRoleToActivityPredicate(vreq);
		activityTypeQuery = QueryUtils.subUriForQueryVar(activityTypeQuery, "predicate", replaceRoleToActivityPredicate);
		log.debug("Activity type query: " + activityTypeQuery);
		
	    return activityTypeQuery;
	}

	
	private String getDefaultActivityTypeQuery(VitroRequest vreq) {
		String query =   "PREFIX core: <" + getVivoCoreNamespace() + ">\n" +
	    "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
	    "SELECT ?existingActivityType WHERE { \n" +
	    "    ?role ?predicate ?existingActivity . \n" +
	    "    ?existingActivity vitro:mostSpecificType ?existingActivityType . \n";
		query += getFilterRoleToActivityPredicate("predicate");
	    query+= "}"; 
		return query;
	}

	private String getSubclassActivityTypeQuery(VitroRequest vreq) {
		String query = "PREFIX core: <" + getVivoCoreNamespace() + ">\n" +
	    "PREFIX rdfs: <" + VitroVocabulary.RDFS + ">\n" +
	    "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
	    "SELECT ?existingActivityType WHERE {\n" +
	    "    ?role ?predicate ?existingActivity . \n" +
	    "    ?existingActivity vitro:mostSpecificType ?existingActivityType . \n" +
	    "    ?existingActivityType rdfs:subClassOf ?objectClassUri . \n";
		query += getFilterRoleToActivityPredicate("predicate");
	    query+= "}"; 
		return query;
	}

	private String getClassgroupActivityTypeQuery(VitroRequest vreq) {
		String query = "PREFIX core: <" + getVivoCoreNamespace() + ">\n" +
	    "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
	    "SELECT ?existingActivityType WHERE { \n" +
	    "    ?role ?predicate ?existingActivity . \n" +
	    "    ?existingActivity vitro:mostSpecificType ?existingActivityType . \n" +
	    "    ?existingActivityType vitro:inClassGroup ?classgroup . \n";
		query += getFilterRoleToActivityPredicate("predicate");
	    query+= "}"; 
		return query;
	}


	private String getRoleActivityQuery(VitroRequest vreq) {
		//If role to activity predicate is the default query, then we need to replace with a union
		//of both realizedIn and the other
		String query =  "PREFIX core: <" + getVivoCoreNamespace() + ">"; 

		String roleToActivityPredicate = getRoleToActivityPredicate(vreq);
		//Portion below for multiple possible predicates
		List<String> predicates = getPossibleRoleToActivityPredicates();
		List<String> addToQuery = new ArrayList<String>();
		query += "SELECT ?existingActivity WHERE { \n" + 
		" ?role ?predicate ?existingActivity . \n ";	
		query += getFilterRoleToActivityPredicate("predicate");
		query += "}";
		return query;
	}

	private HashMap<String, String> generateSparqlForExistingLiterals(VitroRequest vreq) {
    	HashMap<String, String> map = new HashMap<String, String>();
    	//Queries for activity label, role label, start Field value, end Field value
    	map.put("activityLabel", getActivityLabelQuery(vreq));
    	map.put("roleLabel", getRoleLabelQuery(vreq));
    	map.put("startField-value", getExistingStartDateQuery(vreq));
    	map.put("endField-value", getExistingEndDateQuery(vreq));
    	return map;
    }

    
    private String getExistingEndDateQuery(VitroRequest vreq) {
    	String query = " SELECT ?existingEndDate WHERE {\n" + 
    		"?role <" + getRoleToIntervalURI() + "> ?intervalNode .\n" + 
    		"?intervalNode <" + VitroVocabulary.RDF_TYPE + "> <" + getIntervalTypeURI() + "> .\n" + 
    		"?intervalNode <" + getIntervalToEndURI() + "> ?endNode .\n" + 
    		"?endNode <" + VitroVocabulary.RDF_TYPE + "> <" + getDateTimeValueTypeURI() + "> .\n" + 
    		"?endNode <" + getDateTimeValueURI() + "> ?existingEndDate . }";
    	return query;
	}

	private String getExistingStartDateQuery(VitroRequest vreq) {
		String query = "SELECT ?existingDateStart WHERE {\n" + 
	     "?role <" + getRoleToIntervalURI() + "> ?intervalNode .\n" + 
	     "?intervalNode <" + VitroVocabulary.RDF_TYPE + "> <" + getIntervalTypeURI() + "> .\n" + 
	     "?intervalNode <" +  getIntervalToStartURI() + "> ?startNode .\n" + 
	     "?startNode <" + VitroVocabulary.RDF_TYPE + "> <" + getDateTimeValueTypeURI() + "> .\n" + 
	     "?startNode <" +  getDateTimeValueURI() + "> ?existingDateStart . }";

	return query;
	}

	private String getRoleLabelQuery(VitroRequest vreq) {
		String query = "SELECT ?existingRoleLabel WHERE { ?role  <" + VitroVocabulary.LABEL + "> ?existingRoleLabel . }";
		return query;
	}

	private String getActivityLabelQuery(VitroRequest vreq) {
		String query =  "PREFIX core: <" + getVivoCoreNamespace() + ">" + 
		"PREFIX rdfs: <" + RDFS.getURI() + "> \n";

		String roleToActivityPredicate = getRoleToActivityPredicate(vreq);
		query +=  "SELECT ?existingTitle WHERE { \n" + 
		"?role ?predicate ?existingActivity . \n" +		
		"?existingActivity rdfs:label ?existingTitle . \n";
		query += getFilterRoleToActivityPredicate("predicate");
    	query += "}"; 
		return query;
	}

	/**
	 * 
	 * Set Fields and supporting methods
	 */
	
	private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	//Multiple fields
    	getActivityLabelField(editConfiguration, vreq, fields);
    	getRoleActivityTypeField(editConfiguration, vreq, fields);
    	getRoleActivityField(editConfiguration, vreq, fields);
    	getRoleLabelField(editConfiguration, vreq, fields);
    	getStartField(editConfiguration, vreq, fields);
    	getEndField(editConfiguration, vreq, fields);
    	//These fields are for the predicates that will be set later
    	//TODO: Do these only if not using a parameter for the predicate?
    	getRoleToActivityPredicateField(editConfiguration, vreq, fields);
    	getActivityToRolePredicateField(editConfiguration, vreq, fields);
    	editConfiguration.setFields(fields);
    }
    
	//This is a literal technically?
	private void getActivityToRolePredicateField(
			EditConfigurationVTwo editConfiguration, VitroRequest vreq,
			Map<String, FieldVTwo> fields) {
		String fieldName = "activityToRolePredicate";
		//get range data type uri and range language
		String stringDatatypeUri = XSD.xstring.toString();
		
		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(false);
    	//queryForExisting is not being used anywhere in Field
    	
    	//Not really interested in validators here
    	List<String> validators = new ArrayList<String>();
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	
    	field.setOptionsType("UNDEFINED");
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(null);
    	field.setRangeDatatypeUri(null);
    	
    	field.setLiteralOptions(new ArrayList<List<String>>());
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	assertions.add("?roleActivity ?activityToRolePredicate ?role .");
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);	
		
	}



	private void getRoleToActivityPredicateField(
			EditConfigurationVTwo editConfiguration, VitroRequest vreq,
			Map<String, FieldVTwo> fields) {
		String fieldName = "roleToActivityPredicate";
		//get range data type uri and range language
		String stringDatatypeUri = XSD.xstring.toString();
		
		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(false);
    	//queryForExisting is not being used anywhere in Field
    	
    	//Not really interested in validators here
    	List<String> validators = new ArrayList<String>();
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	
    	field.setOptionsType("UNDEFINED");
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(null);
    	field.setRangeDatatypeUri(null);
    	
    	field.setLiteralOptions(new ArrayList<List<String>>());
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	assertions.add("?role ?roleToActivityPredicate ?roleActivity .");
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);	
		
	}



	//Label of "right side" of role, i.e. label for role roleIn Activity
	private void getActivityLabelField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "activityLabel";
		//get range data type uri and range language
		String stringDatatypeUri = XSD.xstring.toString();
		
		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(false);
    	//queryForExisting is not being used anywhere in Field
    	
    	
    	List<String> validators = new ArrayList<String>();
    	//If add mode or repair, etc. need to add label required validator
    	if(isAddMode(vreq) || isRepairMode(vreq)) {
    		validators.add("nonempty");
    	}
    	validators.add("datatype:" + stringDatatypeUri);
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	
    	field.setOptionsType("UNDEFINED");
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(null);
    	field.setRangeDatatypeUri(stringDatatypeUri);
    	
    	
    	field.setLiteralOptions(new ArrayList<List<String>>());
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	assertions.add(getN3ForActivityLabel());
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);	
	}
	
	//type of "right side" of role, i.e. type of activity from role roleIn activity
	private void getRoleActivityTypeField(
			EditConfigurationVTwo editConfiguration, VitroRequest vreq,
			Map<String, FieldVTwo> fields) {
		String fieldName = "roleActivityType";
		//get range data type uri and range language
		
		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(true);
    	//queryForExisting is not being used anywhere in Field
    	
    	
    	List<String> validators = new ArrayList<String>();
    	if(isAddMode(vreq) || isRepairMode(vreq)) {
    		validators.add("nonempty");
    	}
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	//TODO: Check if this is correct
    	field.setOptionsType(getRoleActivityTypeOptionsType(vreq).toString());
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(getRoleActivityTypeObjectClassUri(vreq));
    	field.setRangeDatatypeUri(null);
    	
    	
    	HashMap<String, String> literalOptionsMap = getRoleActivityTypeLiteralOptions(vreq);
    	List<List<String>> fieldLiteralOptions = new ArrayList<List<String>>();
    	Set<String> optionUris = literalOptionsMap.keySet();
    	for(String optionUri: optionUris) {
    		List<String> uriLabelArray = new ArrayList<String>();
    		uriLabelArray.add(optionUri);
    		uriLabelArray.add(literalOptionsMap.get(optionUri));
    		fieldLiteralOptions.add(uriLabelArray);
    	}
    	field.setLiteralOptions(fieldLiteralOptions);
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	assertions.add(getN3ForActivityType());
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);
		
	}    
	
	//Assuming URI for activity for role?
	private void getRoleActivityField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "roleActivity";
		//get range data type uri and range language
		
		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(true);    	
    	
    	List<String> validators = new ArrayList<String>();
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	
    	field.setOptionsType("UNDEFINED");
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(null);
    	field.setRangeDatatypeUri(null);
    	//empty
    	field.setLiteralOptions(new ArrayList<List<String>>());
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	//N3ForRoleToActivity
    	String n3ForRoleToActivity = "@prefix core: <" + getVivoCoreNamespace() + "> ." +     
        "?role " + getRoleToActivityPredicate(vreq) + " ?roleActivity ." +
        "?roleActivity " + getActivityToRolePredicate(vreq) + " ?role .";   
    	assertions.add(n3ForRoleToActivity);
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);
		
	}
	
	private void getRoleLabelField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "roleLabel";
		String stringDatatypeUri = XSD.xstring.toString();

		
		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(false);    	
    	
    	List<String> validators = new ArrayList<String>();
    	validators.add("datatype:" + stringDatatypeUri);
    	if(isShowRoleLabelField(vreq)) {
    		validators.add("nonempty");
    	}
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	
    	field.setOptionsType("UNDEFINED");
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(null);
    	field.setRangeDatatypeUri(stringDatatypeUri);
    	//empty
    	field.setLiteralOptions(new ArrayList<List<String>>());
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	assertions.add(getN3RoleLabelAssertion());
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);
		
	}
	
	

	private void getStartField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "startField";

		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(false);    	
    	
    	List<String> validators = new ArrayList<String>();
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	
    	field.setOptionsType("UNDEFINED");
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(null);
    	field.setRangeDatatypeUri(null);
    	//empty
    	field.setLiteralOptions(new ArrayList<List<String>>());
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	assertions.addAll(getN3ForStart());
    	field.setAssertions(assertions);
    	
    	//This logic was originally after edit configuration object created from json in original jsp
    	field.setEditElement(
                new DateTimeWithPrecisionVTwo(field, 
                        VitroVocabulary.Precision.YEAR.uri(),
                        VitroVocabulary.Precision.NONE.uri()));   
    	
    	fields.put(field.getName(), field);
		
	}

	private void getEndField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "endField";

		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	field.setNewResource(false);    	
    	
    	List<String> validators = new ArrayList<String>();
    	field.setValidators(validators);
    	
    	//subjectUri and subjectClassUri are not being used in Field
    	
    	field.setOptionsType("UNDEFINED");
    	//why isn't predicate uri set for data properties?
    	field.setPredicateUri(null);
    	field.setObjectClassUri(null);
    	field.setRangeDatatypeUri(null);
    	//empty
    	field.setLiteralOptions(new ArrayList<List<String>>());
    	
    	//set assertions
    	List<String> assertions = new ArrayList<String>();
    	assertions.addAll(getN3ForEnd());
    	field.setAssertions(assertions);
    	//Set edit element
    	 field.setEditElement(
                 new DateTimeWithPrecisionVTwo(field, 
                         VitroVocabulary.Precision.YEAR.uri(),
                         VitroVocabulary.Precision.NONE.uri()));
    	
    	fields.put(field.getName(), field);
		
	}

	/**
	 * Prepare edit configuration for update
	 * @param vreq
	 * @param session
	 * @param editConfiguration
	 */
	
	private void prepareForUpdate(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	//Here, retrieve model from 
    	Model model = (Model) session.getServletContext().getAttribute("jenaOntModel");
    	//Object property by definition
    	String objectUri = EditConfigurationUtils.getObjectUri(vreq);
    	if(objectUri != null) {
    		//update existing object
    		editConfiguration.prepareForObjPropUpdate(model);
    	}  else {
    		//new object to be created
            editConfiguration.prepareForNonUpdate( model );
        }
    }
    
   
    //Add preprocessor
	
   private void addPreprocessors(EditConfigurationVTwo editConfiguration, WebappDaoFactory wadf) {
	   //Add preprocessor that will replace the role to activity predicate and inverse
	   //with correct properties based on the activity type
	   editConfiguration.addEditSubmissionPreprocessor(
			   new RoleToActivityPredicatePreprocessor(editConfiguration, wadf));
	   
	}
     
    /**
     * Methods that are REQUIRED to be implemented in subclasses
     **/
  //role type will always be set based on particular form
	abstract public String getRoleType(VitroRequest vreq);
	//In the case of literal options, subclass generator will set the options to be returned
	abstract protected HashMap<String, String> getRoleActivityTypeLiteralOptions(VitroRequest vreq);
	//Each subclass generator will return its own type of option here:
	//whether literal hardcoded, based on class group, or subclasses of a specific class
	//The latter two will apparently lend some kind of uri to objectClassUri ?
	abstract public RoleActivityOptionTypes getRoleActivityTypeOptionsType(VitroRequest vreq);
	//This too will depend on the specific subclass of generator
	abstract public String getRoleActivityTypeObjectClassUri(VitroRequest vreq);
	
	/**
	 * Methods with default values that may be overwritten when required by a subclass
	 * Both Default value and method that can be overwritten are included below
	 **/
    
	public boolean isShowRoleLabelField(VitroRequest vreq) {
		return true;
	}
	
	public String getActivityToRolePredicate(VitroRequest vreq) {
		return getActivityToRolePlaceholder();
    }
    
    //This has a default value, but note that even that will not be used
    //in the update with realized in or contributes to
    //Overridden when need be in subclassed generator
	//Also note that for now we're going to actually going to return a 
	//placeholder value by default
	public String getRoleToActivityPredicate(VitroRequest vreq) {
		return getRoleToActivityPlaceholder();
	}
	//Ensure when overwritten that this includes the <> b/c otherwise the query won't work

	//Some values will have a default value

	public List<String> getPossibleRoleToActivityPredicates() {
		return ModelUtils.getPossiblePropertiesForRole();
	}
	
	public List<String> getPossibleActivityToRolePredicates() {
		return ModelUtils.getPossibleInversePropertiesForRole();
	}
	
	/**
	 * Methods that check edit mode
	 */
	
	
	/**Methods for checking edit mode **
	 * 
	 */
	public EditMode getEditMode(VitroRequest vreq) {
		List<String> roleToGrantPredicates = getPossibleRoleToActivityPredicates();
		return AddRoleUtils.getEditMode(vreq, roleToGrantPredicates);
	}

	private boolean isAddMode(VitroRequest vreq) {
    	return AddRoleUtils.isAddMode(getEditMode(vreq));
    }
    
    private boolean isEditMode(VitroRequest vreq) {
    	return AddRoleUtils.isEditMode(getEditMode(vreq));
    }
    
    private boolean isRepairMode(VitroRequest vreq) {
    	return AddRoleUtils.isRepairMode(getEditMode(vreq));
    }
    
	/**
	 * Methods to return URIS for various predicates
	**/
	public String getVivoCoreNamespace() {
		return "http://vivoweb.org/ontology/core#";
	}
	
	public String getRoleToIntervalURI() {
		return getVivoCoreNamespace() + "dateTimeInterval";
	}
	
	public String getIntervalTypeURI() {
		return getVivoCoreNamespace() + "DateTimeInterval";
	}
	
	public String getIntervalToStartURI() {
		return getVivoCoreNamespace() + "start";
	}
	
	public String getIntervalToEndURI() {
		return getVivoCoreNamespace() + "end";
	}
	
	public String getStartYearPredURI() {
		return getVivoCoreNamespace() + "startYear";
	}
	
	public String getEndYearPredURI() {
		return getVivoCoreNamespace() + "endYear";
	}
	
	public String getDateTimeValueTypeURI() {
		return getVivoCoreNamespace() + "DateTimeValue";
	}
	
	public String getDateTimePrecisionURI() {
		return getVivoCoreNamespace() + "dateTimePrecision";
	}
	
	public String getDateTimeValueURI() {
		return getVivoCoreNamespace() + "dateTime";
	}
	
	//Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
		//Fields that will need select lists generated
		//Store field names
		List<String> objectSelect = new ArrayList<String>();
		objectSelect.add("roleActivityType");
		//TODO: Check if this is the proper way to do this?
		formSpecificData.put("objectSelect", objectSelect);
		//Also put in show role label field
		formSpecificData.put("showRoleLabelField", isShowRoleLabelField(vreq));
		//Put in the fact that we require field
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	
	public String getFilterRoleToActivityPredicate(String predicateVar) {
		String addFilter = "FILTER (";
		List<String> predicates = getPossibleRoleToActivityPredicates();
		List<String> filterPortions = new ArrayList<String>();
		for(String p: predicates) {
			filterPortions.add("(?" + predicateVar + "=<" + p + ">)");
		}
		addFilter += StringUtils.join(filterPortions, " || ");
		addFilter += ")";
		System.out.println("Add filter is " + addFilter);
		return addFilter;
	}
	
	public String getRoleToActivityPlaceholder() {
		return "?roleToActivityPredicate";
	}
	
	public String getActivityToRolePlaceholder() {
		return "?activityToRolePredicate";
	}
	
}
