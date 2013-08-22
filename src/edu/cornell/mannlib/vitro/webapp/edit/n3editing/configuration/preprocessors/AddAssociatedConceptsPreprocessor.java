/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.BaseEditSubmissionPreprocessorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

public class AddAssociatedConceptsPreprocessor extends
		BaseEditSubmissionPreprocessorVTwo {

	protected static final Log log = LogFactory
			.getLog(AddAssociatedConceptsPreprocessor.class.getName());
	//TODO: Check if better way to do this?
	protected OntModel ontModel = null;
	// Field names/variables names for n3 - these will have numbers added as
	// suffix if more than one term
	private static String conceptNodeBase = "conceptNode";
	private static String sourceBase = "conceptSource";
	private static String labelBase = "conceptLabel";
	private static String conceptSemanticTypeLabelBase = "conceptSemanticTypeLabel";
	private static String conceptSemanticTypeURIBase = "conceptSemanticTypeURI";
	//keyed off label variable, specifies which uri variable should be used, useful if same label repeated twice
	private HashMap<String, String> labelVarToUriVarHash = null;
	
	//Also storing submission values
	private static String conceptNodeValues = null;
	private static String conceptLabelValues = null;
	private static String conceptSourceValues = null;
	private static String conceptSemanticTypeLabelValues = null;
	private static String conceptSemanticTypeURIValues = null;
	private static MultiValueEditSubmission submission = null;

	// String datatype

	// Will be editing the edit configuration as well as edit submission here

	public AddAssociatedConceptsPreprocessor(EditConfigurationVTwo editConfig, OntModel ontModel) {
		super(editConfig);
		this.ontModel = ontModel;
		this.labelVarToUriVarHash = new HashMap<String, String>();
	}

	public void preprocess(MultiValueEditSubmission inputSubmission) {
		submission = inputSubmission;
		// Get the input elements for concept node and concept label as well
		// as vocab uri (which is based on thge
		// For query parameters, check whether CUI
		copySubmissionValues();
		
		
		if (conceptNodeValues != null) {
			String[] conceptNodes = convertDelimitedStringToArray(conceptNodeValues);
			int numberConcepts = conceptNodes.length;
			//This will put the URI value in scope for the first semantic type label
			//and generate the rest if need be
			processConceptSemanticValues();
			if (numberConcepts > 1) {
				processConceptNodes(numberConcepts);
			}

		} else {
			log.error("No concept nodes were added from the service");
		}

	}
	
	//Since we will change the uris and literals from form, we should make copies
	//of the original values and store them, this will also make iterations
	//and updates to the submission independent from accessing the values
	private void copySubmissionValues() {
		conceptLabelValues = getConceptLabelValues();
		conceptNodeValues = getConceptNodeValues();
		conceptSourceValues = getConceptSourceValues();
		log.debug("concept label values are " + conceptLabelValues);

	}
	
	//
	private void processConceptSemanticValues() {
		conceptSemanticTypeLabelValues = getConceptSemanticTypeLabelValues();
		conceptSemanticTypeURIValues = getConceptSemanticTypeURIValues();
		
		//We are first going to handle the single value case and then handle additional values
		//where the rest of the additional values are handled
		
	}


	//This is for additional concept nodes (i.e. if user selects more than one concept)
	private void processConceptNodes(int numberConcepts) {
		//There are no "new" resources b/c the concept nodes are URIs from external vocabularies
		//New resources for concept semantic type uris
		addNewResources(numberConcepts);
		// Add N3Required
		addN3Required(numberConcepts);
		//Add N3 Optional as well
		addN3Optional(numberConcepts);
		// Add URIs on Form and Add Literals On Form
		addLiteralsAndUrisOnForm(numberConcepts);
		// Add fields
		addFields(numberConcepts);
		//Add input values to submission
		addInputsToSubmission(numberConcepts);

	}

	//This is specifically for concept semantic type URIs which may need to be generated
	private void addNewResources(int numberConcepts) {
		// TODO Auto-generated method stub
		addConceptSemanticTypeURIResources(numberConcepts);
	}

	private void addConceptSemanticTypeURIResources(int numberConcepts) {
		String[] conceptSemanticTypeURIs= convertDelimitedStringToArray(conceptSemanticTypeURIValues);
		//the number of existing values may not match up, or at least existing populated ones
		if(conceptSemanticTypeURIs != null && conceptSemanticTypeURIs.length == numberConcepts) {
			int i;
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String newResourceName = conceptSemanticTypeURIBase + suffix;
				editConfiguration.addNewResource(newResourceName, null);
				
			}
		} else if(conceptSemanticTypeURIs != null && conceptSemanticTypeURIs.length != numberConcepts){
			log.error("Number of concept semantic type uris does not match the number of concepts");
		} else{
			log.error("Concept semantic type uris returned as null");
		}		
		
	}

	//This is where the actual values will be submitted as if they were separate input fields
	//Each field name will correspond to the names of the fileds/uris on form/literals on form
	//generated here

	private void addInputsToSubmission(int numberConcepts) {
		//This will essentially manufacture a set of query parameters
		//And will add the appropriate fields to the multivalue submission
		addConceptNodeInputs(numberConcepts);
		addConceptSourceInputs(numberConcepts);
		addConceptLabelInputs(numberConcepts);
		//for concept semantic type  labels and uris where they exist
		addConceptSemanticTypeLabelInputs(numberConcepts);
		addConceptSemanticTypeURIInputs(numberConcepts);
		
	}

	private void addConceptNodeInputs(int numberConcepts) {
		
		String[] conceptNodes = convertDelimitedStringToArray(conceptNodeValues);
		if(conceptNodes != null && conceptNodes.length == numberConcepts) {
			int i;
			//iterate through the concept nodes converted string array
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String conceptInputName = conceptNodeBase + suffix;
				String[] nodeValues = new String[1];
				nodeValues[0] = conceptNodes[i];
				//Add value for uri to form
				submission.addUriToForm(editConfiguration, conceptInputName, nodeValues);
			}
		} else if(conceptNodes != null && conceptNodes.length != numberConcepts){
			log.error("Number of concept nodes did not match the number of concepts to be added");
		} else{
			log.error("Concept nodes returned were null");
		}
		
	}

	private void addConceptSourceInputs(int numberConcepts) {
		String[] conceptSources = convertDelimitedStringToArray(conceptSourceValues);
		if(conceptSources != null && conceptSources.length == numberConcepts) {
			int i;
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String conceptInputName = sourceBase + suffix;
				String[] sourceValues = new String[1];
				sourceValues[0] = conceptSources[i];
				//Add value for uri to form
				submission.addUriToForm(editConfiguration, conceptInputName, sourceValues);
			}
		} else if(conceptSources != null && conceptSources.length != numberConcepts){
			log.error("Number of concept nodes did not match the number of concepts to be added");
		} else{
			log.error("Concept nodes returned were null");
		}
	}

	private void addConceptLabelInputs(int numberConcepts) {
		String[] labels = convertDelimitedStringToArray(conceptLabelValues);
		if(labels != null && labels.length == numberConcepts) {
			int i;
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String labelInputName = labelBase + suffix;
				String[] labelValues = new String[1];
				labelValues[0] = labels[i];
				//TODO: Check if there are no funky typed information also stored
				//At this point the field should already have been added to edit configuration
				FieldVTwo labelField = editConfiguration.getField(labelInputName);
				if(labelField != null) {
					submission.addLiteralToForm(editConfiguration, labelField, labelInputName, labelValues);
				} else {
					log.error("Corresponding field for " + labelInputName + " was not added to edit configuration");
				}
				
			}
		} else if(labels != null && labels.length != numberConcepts){
			log.error("Number of concept labels did not match the number of concepts to be added");
		} else{
			log.error("Concept labels returned were null");
		}
	}
	
	private void addConceptSemanticTypeLabelInputs(int numberConcepts) {
		String[] labels = convertDelimitedStringToArray(conceptSemanticTypeLabelValues);
		if(labels != null && labels.length == numberConcepts) {
			int i;
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String labelInputName = conceptSemanticTypeLabelBase + suffix;
				String[] labelValues = new String[1];
				labelValues[0] = labels[i];
				//TODO: Check if there are no funky typed information also stored
				//At this point the field should already have been added to edit configuration
				FieldVTwo labelField = editConfiguration.getField(labelInputName);
				//TODO: Also check to see whether the label is actually populate or will n3 editing take care of that?
				if(labelField != null) {
					submission.addLiteralToForm(editConfiguration, labelField, labelInputName, labelValues);
				} else {
					log.error("Corresponding field for " + labelInputName + " was not added to edit configuration");
				}
				
			}
		} else if(labels != null && labels.length != numberConcepts){
			log.error("Number of concept semantic type labels did not match the number of concepts to be added");
		} else{
			log.error("Concept labels returned were null");
		}	
	}
	
	private void addConceptSemanticTypeURIInputs(int numberConcepts) {
		String[] conceptSemanticTypeURIs= convertDelimitedStringToArray(conceptSemanticTypeURIValues);
		//the number of existing values may not match up, or at least existing populated ones
		if(conceptSemanticTypeURIs != null && conceptSemanticTypeURIs.length == numberConcepts) {
			int i;
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String conceptInputName = conceptSemanticTypeURIBase + suffix;
				String[] uriValues = new String[1];
				uriValues[0] = conceptSemanticTypeURIs[i];
				//Add value for uri to form
				//TODO: Check if value is empty in which case don't add to submission
				submission.addUriToForm(editConfiguration, conceptInputName, uriValues);
			}
		} else if(conceptSemanticTypeURIs != null && conceptSemanticTypeURIs.length != numberConcepts){
			log.error("Number of concept nodes did not match the number of concepts to be added");
		} else{
			log.error("Concept nodes returned were null");
		}	
	}

	//Fields
	
	private void addFields(int numberConcepts) {
		//Clear out all fields in edit configuration first
		editConfiguration.setFields(new HashMap<String, FieldVTwo>());
		int index;
		// First one already included in generator so add additional ones here
		for (index = 1; index <= numberConcepts; index++) {
			int suffix = index;
			String conceptNode = conceptNodeBase + suffix;
			String label = labelBase + suffix;
			String source = sourceBase + suffix;
			String conceptSemanticTypeLabel = conceptSemanticTypeLabelBase + suffix;
			String conceptSemanticTypeURI = conceptSemanticTypeURIBase + suffix;
			
			addConceptNodeField(conceptNode);
			addLabelField(label);
			addSourceField(source);
			//Also add fields for concept semantic type label
			addConceptSemanticTypeLabelField(conceptSemanticTypeLabel);
			//and concept semantic type URI
			addConceptSemanticTypeURIField(conceptSemanticTypeURI);
		}
	}
	
	private void addConceptNodeField(String conceptNode) {
		List<String> validators = new ArrayList<String>();
		validators.add("nonempty");
		editConfiguration.addField(new FieldVTwo().
				setName(conceptNode).
				setValidators(validators));		
	}

	private void addLabelField(String label) {
		editConfiguration.addField(new FieldVTwo().
				setName(label).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
		
	}

	private void addSourceField(String source) {
		editConfiguration.addField(new FieldVTwo().
				setName(source));
		
	}
	
	//Will need to check whether or not semantic type label even exists though?
	private void addConceptSemanticTypeLabelField(String label) {
		editConfiguration.addField(new FieldVTwo().
				setName(label).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
		
	}
	
	private void addConceptSemanticTypeURIField(String conceptSemanticTypeURI) {
		editConfiguration.addField(new FieldVTwo().
				setName(conceptSemanticTypeURI));		
	}


	//original literals on form: label, uris on form: conceptNode and conceptSource
	//This will overwrite the original values in the edit configuration
	private void addLiteralsAndUrisOnForm(int numberTerms) {
		List<String> urisOnForm = new ArrayList<String>();
		List<String> literalsOnForm = new ArrayList<String>();

		int index;

		// First one already included so add new ones here
		for (index = 1; index <= numberTerms; index++) {
			int suffix = index;
			String conceptNode = conceptNodeBase + suffix;
			String label = labelBase + suffix;
			String source = sourceBase + suffix;
			String conceptSemanticTypeLabel = conceptSemanticTypeLabelBase + suffix;
			String conceptSemanticTypeURI = conceptSemanticTypeURIBase + suffix;
			urisOnForm.add(conceptNode);
			urisOnForm.add(source);
			urisOnForm.add(conceptSemanticTypeURI);
			literalsOnForm.add(label);
			literalsOnForm.add(conceptSemanticTypeLabel);
		}
		editConfiguration.setUrisOnform(urisOnForm);
		editConfiguration.setLiteralsOnForm(literalsOnForm);
	}

	// N3 being reproduced
	/*
	 * ?subject ?predicate ?conceptNode .
	 */
	//This will overwrite the original with the set of new n3 required
	private void addN3Required(int numberConcepts) {
		// List<String> n3Required = editConfig.getN3Required();
		List<String> n3Required = new ArrayList<String>();
		int index;
		String nodeBase = "?" + conceptNodeBase;
		
		String prefixStr = "@prefix core: <http://vivoweb.org/ontology/core#> .";
		// First one already included so add new ones here
		for (index = 1; index <= numberConcepts; index++) {
			int suffix = index;
			String node = nodeBase + suffix;
			String n3String = prefixStr;
			n3String += "?subject ?predicate " + node + " . ";
			n3Required.add(n3String);
		}
		editConfiguration.setN3Required(n3Required);
	}
	//Add n3 optional
	//TODO: Rewrite optional N3
	private void addN3Optional(int numberConcepts) {
		List<String> n3Optional = new ArrayList<String>();
		int index;
		String nodeBase = "?" + conceptNodeBase;
		
		String conceptSemanticTypeLabelVar = "?" + conceptSemanticTypeLabelBase;
		String conceptSemanticTypeURIVar = "?" + conceptSemanticTypeURIBase;
		String prefixStr = "@prefix core: <http://vivoweb.org/ontology/core#> .";
		// First one already included so add new ones here
		for (index = 1; index <= numberConcepts; index++) {
			int suffix = index;
			String node = nodeBase + suffix;
			
			String conceptSemanticTypeLabel = conceptSemanticTypeLabelVar + suffix;
			String conceptSemanticTypeURI = conceptSemanticTypeURIVar + suffix;
			String n3String = prefixStr;
			
			
			n3String += node + " core:hasConceptSemanticType " + conceptSemanticTypeURI + " ." + 
			conceptSemanticTypeURI + " core:isConceptSemanticTypeOf " + node + ". " + 
	        conceptSemanticTypeURI +  " <" + RDFS.label.getURI() + "> " + conceptSemanticTypeLabel + " .\n" + 
	        conceptSemanticTypeURI +  " <" + RDF.type.getURI() + "> core:ConceptSemanticType .\n"  ;
			
			n3Optional.add(n3String);
		}
		//Already have n3 required so need to add to that
		
		editConfiguration.setN3Optional(n3Optional);
	}

	private String[] convertDelimitedStringToArray(String inputString) {
		String[] inputArray = new String[1];
		if (inputString.indexOf(",") != -1) {
			inputArray = inputString.split(",");
		} else {
			inputArray[0] = inputString;
		}
		return inputArray;

	}
	
	
	//Get values from submission 
	private String getConceptNodeValues() {
		Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
		List<String> conceptNodes =  urisFromForm.get("conceptNode");
		return (String) getFirstElement(conceptNodes);
	}
	
	private String getConceptSourceValues() {
		Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
		return (String) getFirstElement(urisFromForm.get("conceptSource"));
	}
	
	private String getConceptLabelValues() {
		Map<String, List<Literal>> literalsFromForm = submission.getLiteralsFromForm();
		Map<String, List<String>> transformed = EditConfigurationUtils.transformLiteralMap(literalsFromForm);
		return (String) getFirstElement(transformed.get("conceptLabel"));

	}
	
	private String getConceptSemanticTypeLabelValues() {
		Map<String, List<Literal>> literalsFromForm = submission.getLiteralsFromForm();
		Map<String, List<String>> transformed = EditConfigurationUtils.transformLiteralMap(literalsFromForm);
		return (String) getFirstElement(transformed.get("conceptSemanticTypeLabel"));
	}
	
	//This will either generate or retrieve URIs for the concept semantic type labels if they exist
	//We will then update the submission to include this 
		private String getConceptSemanticTypeURIValues() {
			String[] conceptSemanticTypeLabels = convertDelimitedStringToArray(conceptSemanticTypeLabelValues);
			//keep track of unique labels
			HashSet<String> allSemanticTypeLabels = new HashSet<String>();
			int numberLabels = conceptSemanticTypeLabels.length;
			String pseudoInputString = "";
			for(int i = 0; i < numberLabels; i++) {
				String label = conceptSemanticTypeLabels[i];
				//if label not already in the hash, then create key
				if(!allSemanticTypeLabels.contains(label)) {
					
				}
				//Make or retrieve URI for this label
				String  uri = getURIForSemanticTypeLabel(label);
				if(i != 0) {
					pseudoInputString += ","; 
				}
				pseudoInputString += uri;
			
			}
			
			String[] urisToAdd = new String[1];
			urisToAdd[0] = pseudoInputString;
			log.debug("uris to add" + pseudoInputString);
			submission.addUriToForm(this.editConfiguration, "conceptSemanticTypeURI", urisToAdd);
			return pseudoInputString;
		}
		
	private String getURIForSemanticTypeLabel(String label) {
		String existingURI = this.getExistingSemanticTypeURI(label);
		if(existingURI != null) {
			return existingURI;
		}
		//if we leave this as null, we should be able to generate a new resource
		//empty string because there may be more than one value returned for labels
		else return "";
		
	}
	
	private String getExistingSemanticTypeURI(String label) {
		String queryStr = "SELECT ?semanticType WHERE { ?semanticType <" + RDF.type.getURI() + "> <http://vivoweb.org/ontology/core#ConceptSemanticType> . " + 
				"?semanticType <" + RDFS.label.getURI() + "> \"" + label + "\"^^<http://www.w3.org/2001/XMLSchema#string>  . }";
		 QueryExecution qe = null;
	        try{
	            Query query = QueryFactory.create(queryStr);
	            qe = QueryExecutionFactory.create(query, this.ontModel);
                ResultSet results = null;
                results = qe.execSelect();
                
                while( results.hasNext()){
                	QuerySolution qs = results.nextSolution();
                	if(qs.get("semanticType") != null) {
                		Resource semanticTypeURI = qs.getResource("semanticType");
                		log.debug("Semantic Type URI returned " + semanticTypeURI.getURI());
                		return semanticTypeURI.getURI();
                	}
                }
	        }catch(Exception ex){
	            throw new Error("Error in executing query string: \n" + queryStr + '\n' + ex.getMessage());
	        }finally{
	            if( qe != null)
	                qe.close();
	        }
		return null;
	}
	
	private Object getFirstElement(List inputList) {
		if(inputList == null || inputList.size() == 0)
			return null;
		return inputList.get(0);
	}
	
	

}
