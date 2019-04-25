/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.webapp.util.ModelUtils;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.AutocompleteRequiredInputValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

/**
 *  Custom form for adding a grant to an person for the predicates hasCo-PrincipalInvestigatorRole
     and hasPrincipalInvestigatorRole.

 *
 */
public class AddGrantRoleToPersonGenerator implements EditConfigurationGenerator {

	private Log log = LogFactory.getLog(AddGrantRoleToPersonGenerator.class);
	private String subjectUri = null;
	private String predicateUri = null;
	private String objectUri = null;
	private String template = "addGrantRoleToPerson.ftl";

	//Types of options to populate drop-down for types for the "right side" of the role
	public static enum RoleActivityOptionTypes {
		VCLASSGROUP,
		CHILD_VCLASSES,
		HARDCODED_LITERALS
	}

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
    	EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();

    	//process subject, predicate, object parameters
    	this.initProcessParameters(vreq, session, editConfiguration);

      	//Assumes this is a simple case of subject predicate var
    	editConfiguration.setN3Required(this.generateN3Required(vreq));

    	//n3 optional
    	editConfiguration.setN3Optional(this.generateN3Optional(vreq));

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

    	//Add validators
        editConfiguration.addValidator(new DateTimeIntervalValidationVTwo("startField","endField") );
        editConfiguration.addValidator(new AntiXssValidation());
        editConfiguration.addValidator(new AutocompleteRequiredInputValidator("existingGrant","grantLabel"));
        //no preprocessors required here
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
    	String formUrl = EditConfigurationUtils.getFormUrlWithoutContext(vreq);

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
    	objectUri = EditConfigurationUtils.getObjectUri(vreq);

		this.processObjectPropForm(vreq, editConfiguration);
    }

	private void processObjectPropForm(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
    	editConfiguration.setVarNameForObject("role");
    	editConfiguration.setObject(objectUri);
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//TODO: Check if null in case no object uri exists but this is still an object property
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
    	String editString = getN3ForGrantRole(vreq);
    	n3ForEdit.add(editString);
    	return n3ForEdit;
    }


	private List<String> generateN3Optional(VitroRequest vreq) {
    	List<String> n3Optional = new ArrayList<String>();
    	//n3 for new grant
		n3Optional.add(getN3ForNewGrant(vreq));
    	//n3 for existing grant
		n3Optional.add(getN3ForExistingGrant(vreq));
		//n3 for inverse
		n3Optional.add("?role ?inverseRolePredicate ?person .");
		//N3ForStart
		n3Optional.addAll(getN3ForStart());
		//N3 For End
		n3Optional.addAll(getN3ForEnd());
    	return n3Optional;
    }

	public String getN3ForGrantRole(VitroRequest vreq) {
    	String editString = getPrefixesString();
    	editString += "?person ?rolePredicate ?role .";
    	editString += "?role a <" + getRoleType(vreq) + "> .";
    	return editString;
	}

	public String getN3ForNewGrant(VitroRequest vreq) {
    	String editString = getPrefixesString();
    	editString += "?role <" + getRoleToGrantPredicate(vreq) + "> ?grant .";
    	editString += "?grant a core:Grant . ";
    	editString += "?person core:relatedBy ?grant . ";
    	editString += "?grant core:relates ?person . ";
    	editString += "?grant <" + getGrantToRolePredicate(vreq) + "> ?role .";
    	editString += "?grant <" + RDFS.label.getURI() + "> ?grantLabel .";
    	return editString;
	}

	public String getN3ForExistingGrant(VitroRequest vreq) {
    	String editString = getPrefixesString();
    	editString += "?person core:relatedBy ?existingGrant . ";
    	editString += "?existingGrant core:relates ?person . ";
    	editString += "?role <" + getRoleToGrantPredicate(vreq) + "> ?existingGrant . ";
    	editString += "?existingGrant <" + getGrantToRolePredicate(vreq) + "> ?role .";
    	return editString;
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
			newResources.put("grant", defaultNamespace + "individual");
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
    	//Setting role type
    	urisInScope.put("roleType",
    			Arrays.asList(new String[]{getRoleType(vreq)}));
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
    	urisOnForm.add("grant");
    	urisOnForm.add("existingGrant");
    	editConfiguration.setUrisOnform(urisOnForm);
    	//activity label and role label are literals on form
    	literalsOnForm.add("grantLabel");
    	literalsOnForm.add("grantLabelDisplay");
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
    	map.put("existingGrant", getExistingGrantQuery(vreq));
    	map.put("intervalNode", getIntervalNodeQuery(vreq));
    	map.put("startNode", getStartNodeQuery(vreq));
    	map.put("endNode", getEndNodeQuery(vreq));
    	map.put("startField-precision", getStartPrecisionQuery(vreq));
    	map.put("endField-precision", getEndPrecisionQuery(vreq));
    	return map;
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



	private HashMap<String, String> generateSparqlForExistingLiterals(VitroRequest vreq) {
    	HashMap<String, String> map = new HashMap<String, String>();
    	//Queries for activity label, role label, start Field value, end Field value
    	map.put("grantLabel", getGrantLabelQuery(vreq));
    	map.put("startField-value", getExistingStartDateQuery(vreq));
    	map.put("endField-value", getExistingEndDateQuery(vreq));
    	return map;
    }

    private String getGrantLabelQuery(VitroRequest vreq) {
		String query =  "PREFIX core: <" + getVivoCoreNamespace() + ">" +
		"PREFIX rdfs: <" + RDFS.getURI() + "> \n";

		String roleToGrantPredicate = getRoleToGrantPredicate(vreq);
		query +=  "SELECT ?existingGrantLabel WHERE { \n" +
	        "?role  <" + roleToGrantPredicate + "> ?existingGrant . \n" +
	        "?existingGrant rdfs:label ?existingGrantLabel . }";

		return query;
	}

    private String getExistingGrantQuery(VitroRequest vreq) {
		String query =  "PREFIX core: <" + getVivoCoreNamespace() + ">" +
		"PREFIX rdfs: <" + RDFS.getURI() + "> \n";

		String roleToGrantPredicate = getRoleToGrantPredicate(vreq);
		query +=  "SELECT ?existingGrant WHERE { \n" +
	        "?role  <" + roleToGrantPredicate + "> ?existingGrant .  }";
		return query;
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

	/**
	 *
	 * Set Fields and supporting methods
	 */

	private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	//Multiple fields
    	getGrantField(editConfiguration, vreq, fields);
    	getGrantLabelField(editConfiguration, vreq, fields);
    	getGrantLabelDisplayField(editConfiguration, vreq, fields);
    	getExistingGrantField(editConfiguration, vreq, fields);
    	getStartField(editConfiguration, vreq, fields);
    	getEndField(editConfiguration, vreq, fields);
    	editConfiguration.setFields(fields);
    }

	private void getGrantField(EditConfigurationVTwo editConfiguration,
		VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "grant";

		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	//queryForExisting is not being used anywhere in Field

    	List<String> validators = new ArrayList<String>();
    	field.setValidators(validators);

    	fields.put(field.getName(), field);

	}

	private void getGrantLabelField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "grantLabel";
		//get range data type uri and range language
		String stringDatatypeUri = XSD.xstring.toString();

		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	//queryForExisting is not being used anywhere in Field

    	//Not really interested in validators here
    	List<String> validators = new ArrayList<String>();
    	validators.add("datatype:" + stringDatatypeUri);
    	field.setValidators(validators);

    	fields.put(field.getName(), field);

	}

	private void getGrantLabelDisplayField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {

	    FieldVTwo field = new FieldVTwo();

		String fieldName = "grantLabelDisplay";
    	field.setName(fieldName);

    	String stringDatatypeUri = XSD.xstring.toString();
    	field.setRangeDatatypeUri(null);

    	fields.put(field.getName(), field);

	}
	//Need if returning from an invalid submission
	private void getExistingGrantField(
			EditConfigurationVTwo editConfiguration, VitroRequest vreq,
			Map<String, FieldVTwo> fields) {
		String fieldName = "existingGrant";

		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);
    	//queryForExisting is not being used anywhere in Field

    	fields.put(field.getName(), field);
	}

	private void getStartField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		String fieldName = "startField";

		FieldVTwo field = new FieldVTwo();
    	field.setName(fieldName);

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

    	List<String> validators = new ArrayList<String>();
    	field.setValidators(validators);

    	//Set edit element
    	 field.setEditElement(
                 new DateTimeWithPrecisionVTwo(field,
                         VitroVocabulary.Precision.YEAR.uri(),
                         VitroVocabulary.Precision.NONE.uri()));

    	fields.put(field.getName(), field);

	}

	/**
	 * Prepare edit configuration for update
	 * @param vreq - current VitroRequest
	 * @param session - the HTTP session
	 * @param editConfiguration - Edit configuration
	 */

	private void prepareForUpdate(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	//Here, retrieve model from
		OntModel model = ModelAccess.on(session.getServletContext()).getOntModel();
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

	/**Methods for checking edit mode **
	 *
	 */
	public EditMode getEditMode(VitroRequest vreq) {
		List<String> roleToGrantPredicates = getPossibleRoleToGrantPredicates();
		return EditModeUtils.getEditMode(vreq, roleToGrantPredicates);
	}

	private boolean isAddMode(VitroRequest vreq) {
    	return EditModeUtils.isAddMode(getEditMode(vreq));
    }

    private boolean isEditMode(VitroRequest vreq) {
    	return EditModeUtils.isEditMode(getEditMode(vreq));
    }

    private boolean isRepairMode(VitroRequest vreq) {
    	return EditModeUtils.isRepairMode(getEditMode(vreq));
    }

    /**
     * Methods that are REQUIRED to be implemented in subclasses
     **/
  //role type will always be set based on particular form
	public String getRoleType(VitroRequest vreq) {
		String rangeUri = EditConfigurationUtils.getRangeUri(vreq);
		if(rangeUri.equals(getPrincipalInvestigatorURI())) {
			return getVivoOntologyCoreNamespace() + "PrincipalInvestigatorRole";
		}
		else if(rangeUri.equals(getCoPrincipalInvestigatorURI())) {
			return getVivoOntologyCoreNamespace() + "CoPrincipalInvestigatorRole";
		}
		else {
			return getVivoOntologyCoreNamespace() + "InvestigatorRole";
		}
	}

	private Object getCoPrincipalInvestigatorURI() {
		return getVivoOntologyCoreNamespace() + "CoPrincipalInvestigatorRole";
	}


	//TODO: More dynamic way of getting this or standard mechanism
	private String getVivoOntologyCoreNamespace() {
		return "http://vivoweb.org/ontology/core#";
	}

	private Object getPrincipalInvestigatorURI() {
		return getVivoOntologyCoreNamespace() + "PrincipalInvestigatorRole";

	}

	/**
	 * Methods with default values that may be overwritten when required by a subclass
	 * Both Default value and method that can be overwritten are included below
	 **/

	public boolean isShowRoleLabelField(VitroRequest vreq) {
		return true;
	}


    //This has a default value, but note that even that will not be used
    //in the update with realized in or contributes to
    //Overridden when need be in subclassed generator
	//Also note that for now we're going to actually going to return a
	//placeholder value by default
	public String getRoleToGrantPredicate(VitroRequest vreq) {
		ObjectProperty predicate = ModelUtils.getPropertyForRoleInClass(getGrantType(), vreq.getWebappDaoFactory());
		return predicate.getURI();
	}

	public String getGrantToRolePredicate(VitroRequest vreq) {
		ObjectProperty predicate = ModelUtils.getPropertyForRoleInClass(getGrantType(), vreq.getWebappDaoFactory());
		return predicate.getURIInverse();
	}

	public String getGrantType() {
		return "http://vivoweb.org/ontology/core#Grant";
	}
	//Ensure when overwritten that this includes the <> b/c otherwise the query won't work

	//Some values will have a default value
	//grantToRolePredicate
	public String getDefaultgrantToRolePredicate() {
		return "http://vivoweb.org/ontology/core#relates";
	}

	//roleToGrantPredicate
	public String getDefaultroleToGrantPredicate() {
		return "http://purl.obolibrary.org/obo/BFO_0000054";

	}

	public List<String> getPossibleRoleToGrantPredicates() {
		return ModelUtils.getPossiblePropertiesForRole();
	}

	public List<String> getPossibleGrantToRolePredicates() {
		return ModelUtils.getPossibleInversePropertiesForRole();
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
		formSpecificData.put("rangeUri", getRangeUri(vreq));
		//In this case, passing back a sparql query
		formSpecificData.put("sparqlForAcFilter", getSparqlForAcFilter(vreq));
		//Put in the fact that we require field
		editConfiguration.setFormSpecificData(formSpecificData);
	}

	public String getSparqlForAcFilter(VitroRequest vreq) {
		String subject = EditConfigurationUtils.getSubjectUri(vreq);
		String predicate = EditConfigurationUtils.getPredicateUri(vreq);


		String query = "PREFIX core:<" + getVivoCoreNamespace() + "> " +
		"SELECT ?grantUri WHERE { " +
			"<" + subject + "> <" + predicate + "> ?grantRole ." +
			"?grantRole <" + getRoleToGrantPredicate(vreq) + "> ?grantUri . }";
		return query;
	}

	private String getRangeUri(VitroRequest vreq) {
        String rangeUri = vreq.getParameter("rangeUri");

		return rangeUri;
	}

}
