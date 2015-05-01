/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.DefaultAddMissingIndividualFormModelPreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

/**
 * Generates the edit configuration for a default property form.
 *
 */
public class DefaultAddMissingIndividualFormGenerator implements EditConfigurationGenerator {
	
	private Log log = LogFactory.getLog(DefaultAddMissingIndividualFormGenerator.class);
	private boolean isObjectPropForm = false;
	private String subjectUri = null;
	private String predicateUri = null;
	private String objectUri = null;

	private String template = "defaultAddMissingIndividualForm.ftl";
	private static String createCommand = "create";
	private static String objectVarName = "newIndividual";
	private static HashMap<String,String> defaultsForXSDtypes ;
	  static {
		defaultsForXSDtypes = new HashMap<String,String>();
		//defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","2001-01-01T12:00:00");
		defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","#Unparseable datetime defaults to now");
	  }
	  
	//Method which checks whether this particular generator should be employed  
	public static boolean isCreateNewIndividual(VitroRequest vreq, HttpSession session) {
		String command = vreq.getParameter("cmd");
		String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
		//This method also looks at domain and range uris and so is different than just getting the
		//object property based on predicate uri alone
		ObjectProperty objProp = EditConfigurationUtils.getObjectPropertyForPredicate(vreq, 
	            predicateUri);
		if(objProp != null) {
			return(objProp.getOfferCreateNewOption() && 
					(
							(command != null && command.equals(createCommand)) || 
							objProp.getSelectFromExisting() == false
					)
				);
		}
		return false;
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
    	
    	//form specific data
    	addFormSpecificData(editConfiguration, vreq);
    	
    	//add preprocesoors
    	addPreprocessors(vreq, editConfiguration);

    	prepareForUpdate(vreq, session, editConfiguration);
    	
    	//Form title and submit label now moved to edit configuration template
    	//TODO: check if edit configuration template correct place to set those or whether
    	//additional methods here should be used and reference instead, e.g. edit configuration template could call
    	//default obj property form.populateTemplate or some such method
    	//Select from existing also set within template itself
    	setTemplate(editConfiguration, vreq);
    	
    	editConfiguration.addValidator(new AntiXssValidation());
    	
    	//edit key now set in the edit request dispatch controller
    	return editConfiguration;
    }
    
    private Map<String, String> generateNewResources(VitroRequest vreq) {
		HashMap<String, String> newResources = new HashMap<String, String>();
		//Null triggers default namespace
		newResources.put(objectVarName, null);
		newResources.put("newVcardInd", null);
		newResources.put("newVcardName", null);
		return newResources;
	}
	//Need to replace edit key
    //TODO:Check if we need to recheck forward to create new or assume that is the case since
    //we're using this generator
    //In this case we always set a new edit key as the original jsp checked 'isForwardToCreateNew'
    //which condition would require that an entirely new edit key be created
    private void setEditKey(HttpSession session, EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	String editKey = EditConfigurationVTwo.newEditKey(session);
    	editConfiguration.setEditKey(editKey);
    }
    
	private void setTemplate(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
    	editConfiguration.setTemplate(template);
		
	}

	//Initialize setup: process parameters
	//Doesn't look like we need to set up separate processing for data property form
    private void initProcessParameters(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	String formUrl = EditConfigurationUtils.getFormUrlWithoutContext(vreq);

    	subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
    	predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
    
    	editConfiguration.setFormUrl(formUrl);
    	
    	
    	editConfiguration.setUrlPatternToReturnTo("/individual");
    	
    	editConfiguration.setVarNameForSubject("subject");
    	editConfiguration.setSubjectUri(subjectUri);
    	editConfiguration.setEntityToReturnTo(subjectUri);
    	editConfiguration.setVarNameForPredicate("predicate");
    	editConfiguration.setPredicateUri(predicateUri);
    	
    	
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//"object"       : [ "newIndividual" ,  "${objectUriJson}" , "URI"],
    	if(EditConfigurationUtils.isObjectProperty(predicateUri, vreq)) {
    		//not concerned about remainder, can move into default obj prop form if required
    		this.isObjectPropForm = true;
    		this.initObjectParameters(vreq);
    		this.processObjectPropForm(vreq, editConfiguration);
    	} else {
    		log.error("Add missing individual called for a data property instead of object property");
    	}
    }
    
    


    
	private void initObjectParameters(VitroRequest vreq) {
		//in case of object property
		String thisObjectUri = EditConfigurationUtils.getObjectUri(vreq);
		if(thisObjectUri != null && !thisObjectUri.isEmpty()) {
			objectUri = EditConfigurationUtils.getObjectUri(vreq);
		}
		//otherwise object uri will stay null - since don't want to set it to empty string
	}

	//this particular form uses a different var name for object "newIndividual"
	private void processObjectPropForm(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
    	editConfiguration.setVarNameForObject(objectVarName);    	
    	//If is replace with new, set Object resource to null
    	if(isReplaceWithNew(vreq)) {
    		editConfiguration.setObject(null);
    	} else {
    		editConfiguration.setObject(objectUri);
    	}
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//TODO: Check if null in case no object uri exists but this is still an object property    	
    }
    
    
    
    //Get N3 required 
    //Handles both object and data property    
    private List<String> generateN3Required(VitroRequest vreq) {
    	List<String> n3ForEdit = new ArrayList<String>();
    	n3ForEdit.add(getN3PrefixesAsString() + "\n" + getN3ForName());
    	n3ForEdit.add("?subject ?predicate ?" + objectVarName + " .");
    	n3ForEdit.add(getN3PrefixesAsString() + "\n" + "?" + objectVarName + " rdf:type <" + getRangeClassUri(vreq) + "> . ");
    	return n3ForEdit;
    }
    
    private List<String> getN3Prefixes() {
    	List<String> prefixStrings = new ArrayList<String>();
    	prefixStrings.add("@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
    	prefixStrings.add("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .");
    	prefixStrings.add("@prefix vcard:<http://www.w3.org/2006/vcard/ns#> .");
    	return prefixStrings;
    }
    
    private String getN3PrefixesAsString() {
    	String prefixes = StringUtils.join(getN3Prefixes(), "\n");
    	return prefixes;
    }
    
    private String getN3ForName() {
    	return "?" + objectVarName + " rdfs:label ?label .";
    }
    
    private List<String> generateN3Optional(VitroRequest vreq) {
    	//flag uri and asserted types need to be added here
    	List<String> n3Optional = new ArrayList<String>();
    	n3Optional.add("?" + objectVarName + " ?inverseProp ?subject .");
    	//asserted types string buffer is empty in the original jsp
    	//TODO: Review original comments in jsp to see what could go here
    	//n3Optional.add(getN3AssertedTypes(vreq));
    	n3Optional.add(getN3PrefixesAsString() + "\n" + "?" + objectVarName + " rdf:type <" + getFlagURI(vreq) + "> . ");
    	n3Optional.add(getN3PrefixesAsString() 
    	    + "?" + objectVarName +  "<http://purl.obolibrary.org/obo/ARG_2000028> ?newVcardInd . \n"
            + " ?newVcardInd <http://purl.obolibrary.org/obo/ARG_2000029> ?" + objectVarName +  " . \n"
            + " ?newVcardInd a vcard:Individual . \n"
            + " ?newVcardInd vcard:hasName  ?newVcardName . \n"
            + " ?newVcardName a vcard:Name . \n"
            + " ?newVcardName vcard:givenName ?firstName . \n"
            + " ?newVcardName vcard:familyName ?lastName . \n");
    	n3Optional.add(getN3PrefixesAsString() 
    	    + "?" + objectVarName +  "<http://purl.obolibrary.org/obo/ARG_2000028> ?newVcardInd . \n"
            + " ?newVcardInd a vcard:Individual . \n"
            + " ?newVcardInd vcard:hasName  ?newVcardName . \n"
            + " ?newVcardName a vcard:Name . \n"
            + " ?newVcardName <http://vivoweb.org/ontology/core#middleName> ?middleName .");
    	return n3Optional;
    	
    }
    
    private String getFlagURI(VitroRequest vreq) {
    	WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    	String flagURI = wdf.getVClassDao().getTopConcept().getURI(); 
    	return flagURI;
	}
	private String getN3AssertedTypes(VitroRequest vreq) {
		return null;
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
    	//Add subject uri and predicate turo to uris in scope
    	urisInScope.put(editConfiguration.getVarNameForSubject(), 
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(), 
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	editConfiguration.setUrisInScope(urisInScope);    	
    	editConfiguration.setLiteralsInScope(new HashMap<String, List<Literal>>());
    }
    
    //n3 should look as follows
    //?subject ?predicate ?objectVar 
    
    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();
    	List<String> literalsOnForm = new ArrayList<String>();
    	literalsOnForm.add("label");
    	literalsOnForm.add("firstName");
    	literalsOnForm.add("middleName");
    	literalsOnForm.add("lastName");
    	editConfiguration.setUrisOnform(urisOnForm);
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }
   
    
    //This is for various items
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration) {
    	//Sparql queries defining retrieval of literals etc.
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    	
    	Map<String, String> urisInScope = new HashMap<String, String>();
    	urisInScope.put("inverseProp", this.retrieveQueryForInverse());
    	editConfiguration.setSparqlForAdditionalUrisInScope(urisInScope);
    	
    	editConfiguration.setSparqlForExistingLiterals(generateSparqlForExistingLiterals());
    	editConfiguration.setSparqlForExistingUris(generateSparqlForExistingUris());
    }
    
    
    //Sparql queries
   
    
    private HashMap<String, String> generateSparqlForExistingUris() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	return map;
    }
    
    private HashMap<String, String> generateSparqlForExistingLiterals() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	String query = "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ";
    	query += "SELECT ?existingName WHERE { ?" + objectVarName + " rdfs:label ?existingName . }";
    	map.put("name", query);
    	return map;
    }

    
    private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	if(EditConfigurationUtils.isObjectProperty(EditConfigurationUtils.getPredicateUri(vreq), vreq)) {
    		    			      
    	    //make name field
    	    FieldVTwo labelField = new FieldVTwo();
	        labelField.setName("label");      	        
	        
    	    FieldVTwo firstNameField = new FieldVTwo();
	        firstNameField.setName("firstName");      	        
	        
    	    FieldVTwo middleNameField = new FieldVTwo();
	        middleNameField.setName("middleName");      	        
	        
    	    FieldVTwo lastNameField = new FieldVTwo();
	        lastNameField.setName("lastName");      	        
	        
	        List<String> validators = new ArrayList<String>();
	        validators.add("nonempty");
	        if(!isPersonType(vreq)) { 
	            labelField.setValidators(validators); 
	        }
	        if(isPersonType(vreq)) {  
	            firstNameField.setValidators(validators);   
	            lastNameField.setValidators(validators);   
	        }
	                            
	        fields.put(labelField.getName(), labelField);
	        fields.put(firstNameField.getName(), firstNameField);
	        fields.put(middleNameField.getName(), middleNameField);
	        fields.put(lastNameField.getName(), lastNameField);
    	        
    	} else {
    		log.error("Is not object property so fields not set");
    	}
    	
    	editConfiguration.setFields(fields);
    }

	private String getRangeClassUri(VitroRequest vreq) {
		Individual subject = EditConfigurationUtils.getSubjectIndividual(vreq);
		ObjectProperty prop = EditConfigurationUtils.getObjectProperty(vreq);
		
	    WebappDaoFactory wdf = vreq.getWebappDaoFactory();
	    if( prop.getRangeVClassURI() == null ) {
	        // If property has no explicit range, we will use e.g. owl:Thing.
	        // Typically an allValuesFrom restriction will come into play later.
	        VClass top = wdf.getVClassDao().getTopConcept();
	        prop.setRangeVClassURI(top.getURI());
	    }

	    VClass rangeClass = null;
	    String typeOfNew = getTypeOfNew(vreq);
	    if(typeOfNew != null )
	    	rangeClass = wdf.getVClassDao().getVClassByURI( typeOfNew );
	    if( rangeClass == null ){
	    	rangeClass = wdf.getVClassDao().getVClassByURI(prop.getRangeVClassURI());
	    	if( rangeClass == null ) throw new Error ("Cannot find class for range for property.  Looking for " + prop.getRangeVClassURI() );    
	    }
		return rangeClass.getURI();
	}

	private void prepareForUpdate(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	//Here, retrieve model from 
		OntModel model = ModelAccess.on(session.getServletContext()).getOntModel();
    	//if object property
    	if(EditConfigurationUtils.isObjectProperty(EditConfigurationUtils.getPredicateUri(vreq), vreq)){
	    	Individual objectIndividual = EditConfigurationUtils.getObjectIndividual(vreq);
	    	if(!isReplaceWithNew(vreq) && 
	    			(isForwardToCreateButEdit(vreq) || 
	    			objectIndividual != null)
	    		) {
	    		editConfiguration.prepareForObjPropUpdate(model);
	    	}  else {
	    		//new object to be created
	            editConfiguration.prepareForNonUpdate( model );
	        }
    	} else {
    		log.error("Data property not object property so update can't be done correctly");
    		
    	}
    }
    
	private void addPreprocessors(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
		if(isReplaceWithNew(vreq)) {
			//String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
			//String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
			//String objectUri = EditConfigurationUtils.getObjectUri(vreq);
			editConfiguration.addModelChangePreprocessor(
					new DefaultAddMissingIndividualFormModelPreprocessor(
							subjectUri, predicateUri, objectUri));
			
		}
	}
	
    //Command processing
    private boolean isTypeOfNew(VitroRequest vreq) {
    	String typeOfNew = getTypeOfNew(vreq);
    	return (typeOfNew != null && !typeOfNew.isEmpty());
    }
    
    private String getTypeOfNew(VitroRequest vreq) {
    	return  vreq.getParameter("typeOfNew");
    }
    // The default object proepty form offers the option to create a new item
    // instead of selecting from existing individuals in the system.
    // This is where the request to create a new indivdiual is handled.
    //We don't really need this again b/c we wouldn't be using this generator unless we want
    //to create a new individual so commenting out for now
    /*
    private boolean isForwardToCreateNew(VitroRequest vreq) {
    	String command = vreq.getParameter("cmd");
    	ObjectProperty objectProp = EditConfigurationUtils.getObjectProperty(vreq);
    	if(hasCustomForm(objectProp)) {
    		return false;
    	}
    	
       boolean isForwardToCreateNew = 
           ( objectProp != null && objectProp.getOfferCreateNewOption() )
           && ( objectProp.getSelectFromExisting() == false
           ||   "create".equals(command));

       return isForwardToCreateNew;
	   
    }
    
    private boolean hasCustomForm(ObjectProperty objectProp) {
    	return( objectProp != null && 
    			objectProp.getCustomEntryForm() != null && 
    			!objectProp.getCustomEntryForm().isEmpty());
	     
    }*/
    
    private boolean isReplaceWithNew(VitroRequest vreq) {
    	ObjectProperty objectProp = EditConfigurationUtils.getObjectProperty(vreq);
    	boolean isEditOfExistingStmt = isEditOfExistingStatement(vreq);
    	String command = vreq.getParameter("cmd");
    	return (isEditOfExistingStmt 
    			&& "create".equals(command)) 
    	       && (objectProp != null)
    	       && (objectProp.getOfferCreateNewOption() == true);                
    }
    
    private boolean isForwardToCreateButEdit(VitroRequest vreq) {
    	boolean isEditOfExistingStmt = isEditOfExistingStatement(vreq);
    	ObjectProperty objectProp = EditConfigurationUtils.getObjectProperty(vreq);
    	String command = vreq.getParameter("cmd");
    	return (isEditOfExistingStmt 
    			&& (! "create".equals(command))
    			&& (objectProp != null) 
    	       && (objectProp.getOfferCreateNewOption() == true) 
    	       && (objectProp.getSelectFromExisting() == false)
    	     );
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
		String typeOfNew = getTypeOfNew(vreq);
	    List<String> superTypes = wdf.getVClassDao().getAllSuperClassURIs(typeOfNew);
	    //add the actual type as well so we can add that for the list to be checked
	    superTypes.add(typeOfNew);
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

    //Is edit of existing statement only applicable to object properties
    private boolean isEditOfExistingStatement(VitroRequest vreq) {
    	//TODO: Check if also applicable to data property, currently returning false
    	if(EditConfigurationUtils.isDataProperty(EditConfigurationUtils.getPredicateUri(vreq), vreq)) {
    		return false;
    	}
    	Individual object = EditConfigurationUtils.getObjectIndividual(vreq);
    	return (object != null);

    }

    


}
