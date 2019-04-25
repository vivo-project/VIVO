/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.BaseEditSubmissionPreprocessorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.utils.json.JacksonUtils;

public class AddAssociatedConceptsPreprocessor extends
		BaseEditSubmissionPreprocessorVTwo {

	protected static final Log log = LogFactory
			.getLog(AddAssociatedConceptsPreprocessor.class.getName());
	protected OntModel ontModel = null;
	protected WebappDaoFactory wdf = null;
	// Field names/variables names for n3 - these will have numbers added as
	// suffix if more than one term
	private static String conceptNodeBase = "conceptNode";
	private static String sourceBase = "conceptSource";
	private static String labelBase = "conceptLabel";
	private static String conceptSemanticTypeLabelBase = "conceptSemanticTypeLabel";
	private static String conceptSemanticTypeURIBase = "conceptSemanticTypeURI";
	private static String conceptBroaderURIBase = "conceptBroaderURI";
	private static String conceptNarrowerURIBase = "conceptNarrowerURI";

	//keyed off label variable, specifies which uri variable should be used, useful if same label repeated twice
	private HashMap<String, String> labelVarToUriVarHash = null;
	private HashMap<String, List<String>> conceptSemanticTypeURIVarToValueMap = null;
	//Also storing submission values
	private static String conceptNodeValues = null;
	private static String conceptLabelValues = null;
	private static String conceptSourceValues = null;
	private static String conceptSemanticTypeLabelValues = null;
	private static String conceptSemanticTypeURIValues = null;
	private static List<String> conceptBroaderURIValues =  null;
	private static List<String> conceptNarrowerURIValues = null;
	private static MultiValueEditSubmission submission = null;

	private static String SKOSBroaderURI = "http://www.w3.org/2004/02/skos/core#broader";
	private static String SKOSNarrowerURI = "http://www.w3.org/2004/02/skos/core#narrower";
	private static String SKOSConceptType = "http://www.w3.org/2004/02/skos/core#Concept";

	// String datatype

	// Will be editing the edit configuration as well as edit submission here

	public AddAssociatedConceptsPreprocessor(EditConfigurationVTwo editConfig) {
		super(editConfig);
		this.labelVarToUriVarHash = new HashMap<String, String>();
		//Saves values of concept type uris
		this.conceptSemanticTypeURIVarToValueMap = new HashMap<String, List<String>>();
	}

	public void preprocess(MultiValueEditSubmission inputSubmission, VitroRequest vreq) {
		submission = inputSubmission;
		this.wdf = vreq.getWebappDaoFactory();
		this.ontModel = ModelAccess.on(vreq).getOntModel();
		//Set the models that we need here
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
			//Also need to see if any broader or narrower uris for the concepts that already exist in the system
			//and set up the appropriate relationships between this concept and the broader/narrower uri
			getExistingConceptRelationships();
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
		conceptBroaderURIValues = getConceptBroaderURIValues();
		conceptNarrowerURIValues = getConceptNarrowerURIValues();
		log.debug("concept label values are " + conceptLabelValues);

	}


	//For broader and narrower relationships, we will be
	//linking the concept to broader and narrower terms where those terms already
	//exist in the system
	//This method or approach may change later in which case this method should change
	private void getExistingConceptRelationships() {
		List<String> existingNarrowerURIs = getExistingNarrowerURIs(conceptNarrowerURIValues);
		List<String> existingBroaderURIs = getExistingBroaderURIs(conceptBroaderURIValues);
		//Now set the submission values to these, overwriting the original
		Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
		if(existingNarrowerURIs.size() > 0) {
			urisFromForm.put("conceptNarrowerURI", existingNarrowerURIs);
		} else {
			//The original code for submission wouldn't put in a key if the values were null or size 0
			urisFromForm.remove("conceptNarrowerURI");
		}
		//Set the copied values to this value as well so when if there are multiple
		//concepts, the inputs get copied correctly for each of them
		this.conceptNarrowerURIValues = existingNarrowerURIs;
		if(existingBroaderURIs.size() > 0) {
			urisFromForm.put("conceptBroaderURI", existingBroaderURIs);
		} else {
			urisFromForm.remove("conceptBroaderURI");
		}
		this.conceptBroaderURIValues = existingBroaderURIs;
	}

	//get the broader and narrower uri values that already exist in the system from the ones returned in the search
	//and use those to populate relationships between the concept and other concepts already in the system
	//We should also make sure to use bidirectional n3 so the graph has both sets of relationships represented
	private List<String> getConceptNarrowerURIValues() {
		return this.getJSONFormURIValues("conceptNarrowerURI");
	}

	private List<String> getConceptBroaderURIValues() {
		return this.getJSONFormURIValues("conceptBroaderURI");
	}

	private List<String> getJSONFormURIValues(String varName) {
		Map<String, List<String>> urisFromForm = submission.getUrisFromForm();
		List<String> uris =  urisFromForm.get(varName);
		//This should be a JSON object stringified
		if(uris.size() > 0) {
			String jsonString = uris.get(0);
			if(jsonString != null && !jsonString.isEmpty()) {
				JsonNode json = JacksonUtils.parseJson(jsonString);
				//This should be an array
				if(json.isArray()) {
					ArrayNode jsonArray = (ArrayNode) JacksonUtils.parseJson(jsonString);
					//Convert to list of strings
					return convertJsonArrayToList(jsonArray);
				}
			}
		}
		return uris;
	}

	private List<String> convertJsonArrayToList(ArrayNode jsonArray) {
		List<String> stringList = new ArrayList<String>();
		int len = jsonArray.size();
		int i = 0;
		for(i = 0; i < len; i++) {
			stringList.add(jsonArray.get(i).asText());
		}
		return stringList;
	}

	private List<String> getExistingBroaderURIs(List<String> broaderURIs) {
		if(broaderURIs == null) {
			return new ArrayList<String>();
		}
		List<String> existingBroaderURIs = this.getExistingURIs(broaderURIs);
		return existingBroaderURIs;
	}

	private List<String> getExistingNarrowerURIs(List<String> narrowerURIs) {
		if(narrowerURIs == null)
			return new ArrayList<String>();
		List<String> existingNarrowerURIs = this.getExistingURIs(narrowerURIs);
		return existingNarrowerURIs;
	}

	//We need to keep the number of elements the same if there are any entries at all in the original
	//So we will use an empty string or null
	private List<String> getExistingURIs(List<String> uris) {
		//Important to keep the same formatting as original, because a comma delimited string as an element in the array
		//refers to a list of uris appropriate for a given concept, where each element in the array corresponds to a different
		//concept
		List<String> existingURIs = new ArrayList<String>();
		for(String uri:uris) {
			if(uri.indexOf(",") != -1) {
				List<String> existingURISet = new ArrayList<String>();
				String[] uriSet = uri.split(",");
				for(String u: uriSet) {
					if(u !=  null && !u.isEmpty() && this.wdf.hasExistingURI(u)) {
						existingURISet.add(u);
					}
				}
				//Now add the comma delimited version back to the array
				if(existingURISet.size() > 0) {
					existingURIs.add(StringUtils.join(existingURISet, ","));
				} else {
					//add empty string to indicate no value here
					existingURIs.add("");
				}
			} else {
				if(uri != null && !uri.isEmpty() && this.wdf.hasExistingURI(uri)) {
					existingURIs.add(uri);
				}
				else
				{
					existingURIs.add("");
				}

			}
		}
		return existingURIs;
	}


	//Process the semantic type label and URI values for the concepts
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
		//Iterate through the labels and get the corresponding uris
		HashSet<String> urisToAdd = new HashSet<String>();
		String[] conceptSemanticTypeLabels= convertDelimitedStringToArray(conceptSemanticTypeLabelValues);
		//the number of existing values may not match up, or at least existing populated ones
		//Now we can't determine whether all concepts will have semantic types - at some point what if
		//we ran a search across all external vocabularies? So we can't compare labels to number of concepts
		//but we can ensure that it isn't greater than then number of concepts
		if(conceptSemanticTypeLabels != null && conceptSemanticTypeLabels.length <= numberConcepts) {
			int i;
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String conceptSemanticTypeLabelVar = conceptSemanticTypeLabelBase + suffix;
				if(this.labelVarToUriVarHash.containsKey(conceptSemanticTypeLabelVar)) {
					String newResourceName = this.labelVarToUriVarHash.get(conceptSemanticTypeLabelVar);
					if(!urisToAdd.contains(newResourceName)) {
						urisToAdd.add(newResourceName);
						editConfiguration.addNewResource(newResourceName, null);
					}
				}

			}
		} else if(conceptSemanticTypeLabels != null && conceptSemanticTypeLabels.length > numberConcepts){
			log.error("Number of concept semantic type labels is greater than number of concepts");
		} else{
			log.error("Concept semantic type labels returned are null");
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
		addConceptSemanticTypeLabelAndURIInputs(numberConcepts);
		//For broader and narrower uris where they exist (this of course is in the case of multiple broader and narrower uris
		addConceptBroaderURIInputs(numberConcepts);
		addConceptNarrowerURIInputs(numberConcepts);
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

	private void addConceptSemanticTypeLabelAndURIInputs(int numberConcepts) {
		String[] labels = convertDelimitedStringToArray(conceptSemanticTypeLabelValues);
		HashSet<String> uniqueLabelValues = new HashSet<String>();
		if(labels != null && labels.length == numberConcepts) {
			int i;
			for(i = 0; i < numberConcepts; i++) {
				String thisLabel = labels[i];
				int suffix = i + 1;
				String labelInputName = conceptSemanticTypeLabelBase + suffix;
				String[] labelValues = new String[1];
				labelValues[0] = thisLabel;
				//TODO: Check if there are no funky typed information also stored
				//At this point the field should already have been added to edit configuration
				FieldVTwo labelField = editConfiguration.getField(labelInputName);
				//TODO: Also check to see whether the label is actually populate or will n3 editing take care of that?
				if(labelField != null) {
					submission.addLiteralToForm(editConfiguration, labelField, labelInputName, labelValues);
					//Associate URI
					if(!uniqueLabelValues.contains(thisLabel)) {
						uniqueLabelValues.add(thisLabel);
						this.addConceptSemanticTypeURIInputForLabel(labelInputName, suffix);
					}
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

	private void addConceptSemanticTypeURIInputForLabel(String conceptSemanticTypeLabel, int suffix) {
		//String[] conceptSemanticTypeURIs= convertDelimitedStringToArray(conceptSemanticTypeURIValues);

		//Get the semantic type URI variable name associated with this label
		String uriInputName = this.getConceptSemanticTypeURIFieldName(conceptSemanticTypeLabel, suffix);
		//List<>
		if(this.conceptSemanticTypeURIVarToValueMap.containsKey(uriInputName)) {
			List<String> uriVals = this.conceptSemanticTypeURIVarToValueMap.get(uriInputName);
			String[] uriValuesArray = uriVals.toArray(new String[uriVals.size()]);
			submission.addUriToForm(editConfiguration, uriInputName, uriValuesArray);
		}
	}

	private void addConceptBroaderURIInputs(int numberConcepts) {
		int i;
		//Add inputs based on if there are any broader uris to add
		//Can't really compare number of existing broader uris to concepts
		//as each concept may or may not have a broader uri
		if(this.conceptBroaderURIValues.size() > 0 && this.conceptBroaderURIValues.size() <= numberConcepts) {
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String conceptBroaderURIInputName = conceptBroaderURIBase + suffix;
				String broaderURIs = this.conceptBroaderURIValues.get(i);
				if(broaderURIs != null && !broaderURIs.isEmpty()) {
					String[] broaderURISet = new String[1];
					if(broaderURIs.indexOf(",") != -1) {
						broaderURISet = broaderURIs.split(",");
					} else {
						broaderURISet[0] = broaderURIs;
					}
					//Add value for uri to form
					submission.addUriToForm(editConfiguration, conceptBroaderURIInputName, broaderURISet);
				}
			}
		}

	}
	private void addConceptNarrowerURIInputs(int numberConcepts) {
		int i;
		if(this.conceptNarrowerURIValues.size() > 0 && this.conceptNarrowerURIValues.size() <= numberConcepts) {
			for(i = 0; i < numberConcepts; i++) {
				int suffix = i + 1;
				String conceptNarrowerURIInputName = conceptNarrowerURIBase + suffix;
				String narrowerURIs = this.conceptNarrowerURIValues.get(i);
				if(narrowerURIs != null && !narrowerURIs.isEmpty()) {
					String[] narrowerURISet = new String[1];
					if(narrowerURIs.indexOf(",") != -1) {
						narrowerURISet = narrowerURIs.split(",");
					} else {
						narrowerURISet[0] = narrowerURIs;
					}
					//Add value for uri to form
					submission.addUriToForm(editConfiguration, conceptNarrowerURIInputName, narrowerURISet);
				}
			}
		}
	}

	//Fields

	private void addFields(int numberConcepts) {
		//Clear out all fields in edit configuration first
		editConfiguration.setFields(new HashMap<String, FieldVTwo>());
		int index;
		HashSet<String> conceptSemanticTypeUris = new HashSet<String>();
		// First one already included in generator so add additional ones here
		for (index = 1; index <= numberConcepts; index++) {
			int suffix = index;
			String conceptNode = conceptNodeBase + suffix;
			String label = labelBase + suffix;
			String source = sourceBase + suffix;
			String conceptSemanticTypeLabel = conceptSemanticTypeLabelBase + suffix;
			String conceptSemanticTypeURI = this.getConceptSemanticTypeURIFieldName(conceptSemanticTypeLabel, suffix);
			String conceptBroaderURI = conceptBroaderURIBase + suffix;
			String conceptNarrowerURI = conceptNarrowerURIBase + suffix;
			addConceptNodeField(conceptNode);
			addLabelField(label);
			addSourceField(source);
			//Also add fields for concept semantic type label
			addConceptSemanticTypeLabelField(conceptSemanticTypeLabel);
			//and concept semantic type URI
			if(!conceptSemanticTypeUris.contains(conceptSemanticTypeURI)) {
				conceptSemanticTypeUris.add(conceptSemanticTypeURI);
				addConceptSemanticTypeURIField(conceptSemanticTypeURI);
			}

			//add fields for concept broader and narrower uris
			addConceptBroaderURIField(conceptBroaderURI);
			addConceptNarrowerURIField(conceptNarrowerURI);
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

	//TODO: Do we need to check if label is empty string?
	private void addConceptSemanticTypeLabelField(String label) {
		if(label != null) {
			editConfiguration.addField(new FieldVTwo().
					setName(label).
					setRangeDatatypeUri(XSD.xstring.toString())
					);
		}

	}

	private void addConceptSemanticTypeURIField(String conceptSemanticTypeURI) {
		if(conceptSemanticTypeURI != null) {
			editConfiguration.addField(new FieldVTwo().
					setName(conceptSemanticTypeURI));
		}
	}

	private void addConceptNarrowerURIField(String conceptNarrowerURI) {
		editConfiguration.addField(new FieldVTwo().
				setName(conceptNarrowerURI));

	}

	private void addConceptBroaderURIField(String conceptBroaderURI) {
		editConfiguration.addField(new FieldVTwo().
				setName(conceptBroaderURI));

	}

	//original literals on form: label, uris on form: conceptNode and conceptSource
	//This will overwrite the original values in the edit configuration
	private void addLiteralsAndUrisOnForm(int numberTerms) {
		List<String> urisOnForm = new ArrayList<String>();
		List<String> literalsOnForm = new ArrayList<String>();

		int index;
		HashSet<String> conceptSemanticTypeURIs = new HashSet<String>();
		// First one already included so add new ones here
		for (index = 1; index <= numberTerms; index++) {
			int suffix = index;
			String conceptNode = conceptNodeBase + suffix;
			String label = labelBase + suffix;
			String source = sourceBase + suffix;
			String conceptSemanticTypeLabel = conceptSemanticTypeLabelBase + suffix;
			//String conceptSemanticTypeURI = conceptSemanticTypeURIBase + suffix;
			String conceptSemanticTypeURI = this.getConceptSemanticTypeURIFieldName(conceptSemanticTypeLabel, suffix);
			String conceptBroaderURI = conceptBroaderURIBase + suffix;
			String conceptNarrowerURI = conceptNarrowerURIBase + suffix;
			urisOnForm.add(conceptNode);
			urisOnForm.add(source);
			if(!conceptSemanticTypeURIs.contains(conceptSemanticTypeURI)) {
				conceptSemanticTypeURIs.add(conceptSemanticTypeURI);
				urisOnForm.add(conceptSemanticTypeURI);
			}
			urisOnForm.add(conceptBroaderURI);
			urisOnForm.add(conceptNarrowerURI);
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
			n3String += "?subject ?predicate " + node + " . \n" +
			node + " <" + RDF.type.getURI() + "> <" + this.SKOSConceptType + "> .";
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
		String labelVar = "?" + labelBase;
		String sourceVar = "?" + sourceBase;
		String conceptSemanticTypeLabelVar = "?" + conceptSemanticTypeLabelBase;
		String conceptBroaderURIVar = "?" + conceptBroaderURIBase;
		String conceptNarrowerURIVar = "?" + conceptNarrowerURIBase;
		String prefixStr = "@prefix core: <http://vivoweb.org/ontology/core#> .";
		// First one already included so add new ones here
		//We already have a label var to uri var setup
		for (index = 1; index <= numberConcepts; index++) {
			//Set up the variables based on which concept node
			int suffix = index;
			String node = nodeBase + suffix;
			String label = labelVar + suffix;
			String source = sourceVar + suffix;
			String conceptSemanticTypeLabel = conceptSemanticTypeLabelVar + suffix;
			//get the URI appropriate for the concept semantic type label var
			String conceptSemanticTypeURI = getConceptSemanticTypeURIVar(conceptSemanticTypeLabelBase + suffix, suffix);
			String conceptBroaderURI = conceptBroaderURIVar + suffix;
			String conceptNarrowerURI = conceptNarrowerURIVar + suffix;
			//Set up the n3 strings
			String n3String = prefixStr;
			n3String += node + " <" + RDFS.label.getURI() + "> " + label + " .\n" +
			        node + " <" + RDFS.isDefinedBy.getURI() + "> " + source + " .";
			String n3ConceptTypeString = prefixStr;
			n3ConceptTypeString += node + " <" + RDF.type.getURI() + "> " + conceptSemanticTypeURI + " ." +
			conceptSemanticTypeURI +  " <" + RDFS.label.getURI() + "> " + conceptSemanticTypeLabel + " .\n" +
	        conceptSemanticTypeURI +  " <" + RDFS.subClassOf.getURI() + "> <http://www.w3.org/2004/02/skos/core#Concept> .\n"  ;
			//String representing the broader and narrower uri(s) for each of the concepts - these may or may not exist
			String n3ConceptBroaderURI = prefixStr + node + " <" + this.SKOSNarrowerURI + "> " + conceptNarrowerURI + " ." +
		 	        conceptNarrowerURI + " <" + this.SKOSBroaderURI + "> " + node + " .";
			String n3ConceptNarrowerURI = prefixStr + node + " <" + this.SKOSBroaderURI + "> " + conceptBroaderURI + " ." +
		 	    	conceptBroaderURI + " <" + this.SKOSNarrowerURI + "> " + node + " .";

			n3Optional.add(n3String);
			//adding separately so their resolution does not depend on each other
			n3Optional.add(n3ConceptTypeString);
			n3Optional.add(n3ConceptBroaderURI);
			n3Optional.add(n3ConceptNarrowerURI);

		}
		//Already have n3 required so need to add to that

		editConfiguration.setN3Optional(n3Optional);
	}

	//get the URI variable that is associated with this concept type URI, which might not be
	//the same suffix because the same label value might be repeated and we need to use the same URI
	//representing that concept semantic type
	private String getConceptSemanticTypeURIVar(String labelVar, int suffix) {
		// TODO Auto-generated method stub
		return "?" + this.getConceptSemanticTypeURIFieldName(labelVar, suffix);
	}

	private String getConceptSemanticTypeURIFieldName(String labelVar, int suffix) {
		// TODO Auto-generated method stub
		if(this.labelVarToUriVarHash.containsKey(labelVar)) {
			return this.labelVarToUriVarHash.get(labelVar);
		}
		return this.conceptSemanticTypeURIBase + suffix;
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
		String label = (String) getFirstElement(transformed.get("conceptSemanticTypeLabel"));
		if(label == null) {
			label = "";
		}

		return label;
	}

	//This will either generate or retrieve URIs for the concept semantic type labels if they exist
	//We will then update the submission to include this
		private String getConceptSemanticTypeURIValues() {
			StringBuilder pseudoInputString = new StringBuilder();
			if(conceptSemanticTypeLabelValues !=  null && !conceptSemanticTypeLabelValues.isEmpty()) {
				String[] conceptSemanticTypeLabels = convertDelimitedStringToArray(conceptSemanticTypeLabelValues);
				//keep track of what label values already exist and to which label variables they map
				HashMap<String, List<Integer>> labelValueToVarSuffix = new HashMap<String, List<Integer>>();
				int numberLabels = conceptSemanticTypeLabels.length;

				//The rest of this code is really only relevant for multiple values, so we could break out the old code above
				//as we don't need to set up hashes etc. if there is only one concept node being added
				if(numberLabels == 1) {
					String label = conceptSemanticTypeLabels[0];
					String  uri = getURIForSemanticTypeLabel(label);
					if(!StringUtils.isEmpty(uri)) {
						String[] urisToAdd = new String[1];
						urisToAdd[0] = uri;
						pseudoInputString = new StringBuilder(uri);
						log.debug("uris to add" + uri);
						submission.addUriToForm(this.editConfiguration, "conceptSemanticTypeURI", urisToAdd);
					}

				}
				//if there is more than one concept node, we may have duplicate semantic types
				//which will need to be referred to by the same semantic type uri
				else if (numberLabels > 1){

					for(int i = 0; i < numberLabels; i++) {
						int suffix = i + 1;
						String label = conceptSemanticTypeLabels[i];
						String labelVar = this.conceptSemanticTypeLabelBase + suffix;
						//if label has not already been encountered, create entry for label value
						//and list with the label variables that would refer to it
						//for unique values, the uri variable will be the same as label
						Integer thisSuffix = new Integer(suffix);
						if(!labelValueToVarSuffix.containsKey(label)) {
							labelValueToVarSuffix.put(label, new ArrayList<Integer>());
							//Add suffix to list if not already there
							labelValueToVarSuffix.get(label).add(thisSuffix);
						} else {
							//in this case, the label already exists, get the very first element in the list
							//and use that as the uri variable
							List<Integer> suffixList = labelValueToVarSuffix.get(label);
							if(suffixList != null && suffixList.size() > 0) {
								thisSuffix = suffixList.get(0);
							}

						}

						//Now add the uri var to the hash mapping label variable to uri variable
						String uriVar = this.conceptSemanticTypeURIBase + thisSuffix.intValue();
						this.labelVarToUriVarHash.put(labelVar, uriVar);


						//Make or retrieve URI for this label
						//TODO: Do we create this string with empty inputs ?
						String  uri = getURIForSemanticTypeLabel(label);
						if(!StringUtils.isEmpty(uri)) {
							//uri var shouldn't be repeated?
							if(!this.conceptSemanticTypeURIVarToValueMap.containsKey(uriVar)) {
							this.conceptSemanticTypeURIVarToValueMap.put(uriVar, new ArrayList<String>());
							this.conceptSemanticTypeURIVarToValueMap.get(uriVar).add(uri);
							}
						}
						if(i != 0) {
							pseudoInputString.append(",");
						}
						pseudoInputString.append(uri);

					}

					//Add this string to the uris for the form
					String[] urisToAdd = new String[1];
					urisToAdd[0] = pseudoInputString.toString();
					log.debug("uris to add" + pseudoInputString);
					submission.addUriToForm(this.editConfiguration, "conceptSemanticTypeURI", urisToAdd);

				}
			}
			return pseudoInputString.toString();
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
		String queryStr = "SELECT ?semanticType WHERE { ?semanticType <" + RDF.type.getURI() + "> <" + OWL.Class.getURI() + "> . " +
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
