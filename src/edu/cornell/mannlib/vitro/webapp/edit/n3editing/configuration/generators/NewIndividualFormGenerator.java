/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDFS;
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
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.RdfLiteralHash;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditN3GeneratorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.SelectListGeneratorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;

/**
 * Generates the edit configuration for a default property form.
 *
 */
public class NewIndividualFormGenerator implements EditConfigurationGenerator {
	
	private Log log = LogFactory.getLog(NewIndividualFormGenerator.class);
	private boolean isObjectPropForm = false;
	private String subjectUri = null;
	private String predicateUri = null;
	private String objectUri = null;
	private String datapropKeyStr= null;
	private int dataHash = 0;
	private DataPropertyStatement dps = null;
	private String dataLiteral = null;
	private String template = "newIndividualForm.ftl";
	private static HashMap<String,String> defaultsForXSDtypes ;
	  static {
		defaultsForXSDtypes = new HashMap<String,String>();
		//defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","2001-01-01T12:00:00");
		defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","#Unparseable datetime defaults to now");
	  }
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
    	editConfiguration.setNewResources(this.generateNewResources(vreq));
    	//In scope
    	this.setUrisAndLiteralsInScope(editConfiguration);
    	
    	//on Form
    	this.setUrisAndLiteralsOnForm(editConfiguration, vreq);
    	
    	editConfiguration.setFilesOnForm(new ArrayList<String>());
    	
    	//Sparql queries
    	this.setSparqlQueries(editConfiguration);
    	
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
        addFormSpecificData(editConfiguration, vreq);

    	return editConfiguration;
    	

    }
    
    private Map<String, String> generateNewResources(VitroRequest vreq) {
    	HashMap<String, String> newResources = new HashMap<String, String>();
		//TODO: Get default namespace
		String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
		newResources.put("newInd", defaultNamespace + "individual");
		return newResources;
	}

	private void setEditKey(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	String editKey = EditConfigurationUtils.getEditKey(vreq);	
    	editConfiguration.setEditKey(editKey);
    }
    
	private void setTemplate(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
    	editConfiguration.setTemplate(template);
		
	}

	//Initialize setup: process parameters
    private void initProcessParameters(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	String formUrl = EditConfigurationUtils.getFormUrl(vreq);

    	subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
    	predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
    
    	editConfiguration.setFormUrl(formUrl);
    	
    	editConfiguration.setUrlPatternToReturnTo("/individual");
    	
    	editConfiguration.setVarNameForSubject("subjectNotUsed");
    	editConfiguration.setSubjectUri(subjectUri);
    	editConfiguration.setEntityToReturnTo("?newInd");
    	editConfiguration.setVarNameForPredicate("predicateNotUsed");
    	editConfiguration.setPredicateUri(predicateUri);
    	
		//not concerned about remainder, can move into default obj prop form if required
		this.isObjectPropForm = true;
		this.initObjectParameters(vreq);
		this.processObjectPropForm(vreq, editConfiguration);
    }

    
	private void initObjectParameters(VitroRequest vreq) {
		//in case of object property
    	objectUri = EditConfigurationUtils.getObjectUri(vreq);
	}

	private void processObjectPropForm(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
    	editConfiguration.setVarNameForObject("objectNotUsed");    	
    	editConfiguration.setObject(objectUri);
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//TODO: Check if null in case no object uri exists but this is still an object property
    	if(objectUri != null) {
    		editConfiguration.setObjectResource(true);
    	}
    }
    
    private void processDataPropForm(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
    	editConfiguration.setObjectResource(false);
    	//set data prop value, data prop key str, 
    	editConfiguration.setDatapropKey((datapropKeyStr==null)?"":datapropKeyStr);
    	editConfiguration.setVarNameForObject(dataLiteral);
    	//original set datapropValue, which in this case would be empty string but no way here
    	editConfiguration.setDatapropValue("");
    	editConfiguration.setUrlPatternToReturnTo("/entity");
    }
    
    //Get N3 required 
    //Handles both object and data property    
    private List<String> generateN3Required(VitroRequest vreq) {
    	List<String> n3ForEdit = new ArrayList<String>();
    	String editString = "?newInd <" + VitroVocabulary.RDF_TYPE + "> <" + getTypeOfNew(vreq) + "> .";
    	n3ForEdit.add(editString);
    	return n3ForEdit;
    }
    
    private List<String> generateN3Optional() {
    	List<String> n3Optional = new ArrayList<String>();
    	String editString = "@prefix foaf:<http://xmlns.com/foaf/0.1/> ." + 
    	 " ?newInd foaf:firstName ?firstName ; " +
    	 " ?newInd foaf:lastName ?lastName .";
    	n3Optional.add(editString);
    	n3Optional.add("?newInd <" + RDFS.label.getURI() + "> ?label .");
    	return n3Optional;
    	
    }
    
    //Set queries
    private String retrieveQueryForInverse () {
    	String queryForInverse =  "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
			+ " SELECT ?inverse_property "
			+ "    WHERE { ?inverse_property owl:inverseOf ?predicate } ";
    	return queryForInverse;
    }
    
    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration) {
    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
    	//note that at this point the subject, predicate, and object var parameters have already been processed
    	urisInScope.put(editConfiguration.getVarNameForSubject(), 
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(), 
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	editConfiguration.setUrisInScope(urisInScope);
    	//Uris in scope include subject, predicate, and object var
    	
    	editConfiguration.setLiteralsInScope(new HashMap<String, List<Literal>>());
    }
    
    //n3 should look as follows
    //?subject ?predicate ?objectVar 
    
    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();
    	List<String> literalsOnForm = new ArrayList<String>();
    	literalsOnForm.add("label");
    	literalsOnForm.add("firstName");
    	literalsOnForm.add("lastName");
    	editConfiguration.setUrisOnform(urisOnForm);
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }
    
    private String getDataLiteral(VitroRequest vreq) {
    	DataProperty prop = EditConfigurationUtils.getDataProperty(vreq);
    	return prop.getLocalName() + "Edited";
    }
    
    //This is for various items
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration) {
    	//Sparql queries defining retrieval of literals etc.
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    	
    	Map<String, String> urisInScope = new HashMap<String, String>();
    	editConfiguration.setSparqlForAdditionalUrisInScope(urisInScope);
    	
    	editConfiguration.setSparqlForExistingLiterals(generateSparqlForExistingLiterals());
    	editConfiguration.setSparqlForExistingUris(generateSparqlForExistingUris());
    }
    
    
    //Get page uri for object
    private HashMap<String, String> generateSparqlForExistingUris() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	return map;
    }
    
    private HashMap<String, String> generateSparqlForExistingLiterals() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	return map;
    }

    
    private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	getLabelField(editConfiguration, vreq, fields);
    	getFirstNameField(editConfiguration, vreq, fields);
    	getLastNameField(editConfiguration, vreq, fields);
    }
    
    private void getLastNameField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
    	FieldVTwo field = new FieldVTwo();
    	field.setName("lastName");
    	field.setNewResource(false);
    	//queryForExisting is not being used anywhere in Field
		String stringDatatypeUri = XSD.xstring.toString();

    	
    	List<String> validators = new ArrayList<String>();
    	if(isPersonType(vreq)) {
    		validators.add("nonempty");
    	}
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
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);
		
	}

	private void getFirstNameField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
		FieldVTwo field = new FieldVTwo();
    	field.setName("firstName");
    	field.setNewResource(false);
    	//queryForExisting is not being used anywhere in Field
		String stringDatatypeUri = XSD.xstring.toString();

    	
    	List<String> validators = new ArrayList<String>();
    	if(isPersonType(vreq)) {
    		validators.add("nonempty");
    	}
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
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);
		
		
	}

	private void getLabelField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq, Map<String, FieldVTwo> fields) {
    	FieldVTwo field = new FieldVTwo();
    	field.setName("label");
    	field.setNewResource(false);
    	//queryForExisting is not being used anywhere in Field
		String stringDatatypeUri = XSD.xstring.toString();

    	
    	List<String> validators = new ArrayList<String>();
    	if(!isPersonType(vreq)) {
    		validators.add("nonempty");
    	}
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
    	field.setAssertions(assertions);
    	fields.put(field.getName(), field);
		
	}

	

	private void prepareForUpdate(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	//Here, retrieve model from 
    	Model model = (Model) session.getServletContext().getAttribute("jenaOntModel");
    	//This form is always doing a non-update
    	editConfiguration.prepareForNonUpdate( model );
	      
    }
    
    
    //Get parameter
    private String getTypeOfNew(VitroRequest vreq) {
    	return vreq.getParameter("typeOfNew");
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
	    List<String> superTypes = wdf.getVClassDao().getAllSuperClassURIs(getTypeOfNew(vreq));    
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
