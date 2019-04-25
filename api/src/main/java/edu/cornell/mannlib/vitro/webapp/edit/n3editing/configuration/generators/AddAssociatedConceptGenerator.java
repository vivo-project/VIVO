/* $This file is distributed under the terms of the license in LICENSE$ */


package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.AddAssociatedConceptsPreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.ConceptSemanticTypesPreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService.ConceptSearchServiceUtils;
/**
 * Generates the edit configuration for importing concepts from external
 * search services, e.g. UMLS etc.
 *
 * Since editing/deletion is handled by separate custom code, this generator always assumes
 * property addition mode.
 */
public class AddAssociatedConceptGenerator  extends VivoBaseGenerator implements EditConfigurationGenerator {

	private Log log = LogFactory.getLog(AddAssociatedConceptGenerator.class);
	private String template = "addAssociatedConcept.ftl";
	//TODO: Set this to a dynamic mechanism
	private static String VIVOCore = "http://vivoweb.org/ontology/core#";
	private static String SKOSConceptType = "http://www.w3.org/2004/02/skos/core#Concept";
	private static String SKOSBroaderURI = "http://www.w3.org/2004/02/skos/core#broader";
	private static String SKOSNarrowerURI = "http://www.w3.org/2004/02/skos/core#narrower";
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
    	EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
		initBasics(editConfiguration, vreq);
		initPropertyParameters(vreq, session, editConfiguration);
		initObjectPropForm(editConfiguration, vreq);
		editConfiguration.setTemplate(template);

		setVarNames(editConfiguration);

		// Assumes this is a simple case of subject predicate var
		editConfiguration.setN3Required(this.generateN3Required(vreq));

		// n3 optional
		editConfiguration.setN3Optional(this.generateN3Optional());

		editConfiguration.setNewResources(generateNewResources(vreq));
		// In scope
		this.setUrisAndLiteralsInScope(editConfiguration, vreq);

		// on Form
		this.setUrisAndLiteralsOnForm(editConfiguration, vreq);

		editConfiguration.setFilesOnForm(new ArrayList<String>());

		// Sparql queries
		this.setSparqlQueries(editConfiguration, vreq);

		// set fields
		setFields(editConfiguration, vreq, EditConfigurationUtils
				.getPredicateUri(vreq));

		setTemplate(editConfiguration, vreq);
		// No validators required here
		// Add preprocessors
		//Passing from servlet context for now but will have to see if there's a way to pass vreq
		addPreprocessors(editConfiguration);
		// Adding additional data, specifically edit mode
		addFormSpecificData(editConfiguration, vreq);
		// One override for basic functionality, changing url pattern
		// and entity
		// Adding term should return to this same page, not the subject
		// Return takes the page back to the individual form
		editConfiguration.setUrlPatternToReturnTo(EditConfigurationUtils
				.getFormUrlWithoutContext(vreq));

		editConfiguration.addValidator(new AntiXssValidation());

		// prepare
		prepare(vreq, editConfiguration);
		return editConfiguration;
    }

    //In this case, the generator is not equipped to handle any deletion
    //Editing in the usual sense does not exist for this form
    //So we will disable editing
    @Override
    void initObjectPropForm(EditConfigurationVTwo editConfiguration,VitroRequest vreq) {
        editConfiguration.setObject( null );
    }

    //Ensuring that editing property logic does not get executed on processing
    //since form's deletions are handled separately
    @Override
    void prepare(VitroRequest vreq, EditConfigurationVTwo editConfig) {
    	Model model = vreq.getJenaOntModel();
        //Set subject and predicate uri
        if( editConfig.getSubjectUri() == null)
            editConfig.setSubjectUri( EditConfigurationUtils.getSubjectUri(vreq));
        if( editConfig.getPredicateUri() == null )
            editConfig.setPredicateUri( EditConfigurationUtils.getPredicateUri(vreq));
        //Always set creation
        editConfig.prepareForNonUpdate(model);

    }


	private void setVarNames(EditConfigurationVTwo editConfiguration) {
		  editConfiguration.setVarNameForSubject("subject");
	      editConfiguration.setVarNameForPredicate("predicate");
	      //We are not including concept node here since
	      //we never actually "edit" using this form
	      //the n3 required and optional will still be evaluated based on the form
	      editConfiguration.setVarNameForObject("object");
	}

	protected void setTemplate(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
    	editConfiguration.setTemplate(template);

	}



   /*
    * N3 Required and Optional Generators as well as supporting methods
    */

	private String getPrefixesString() {
		//TODO: Include dynamic way of including this
		return "@prefix core: <http://vivoweb.org/ontology/core#> .";
	}

	//The only string always required is that linking the subject to the concept node
	//Since the concept node from an external vocabulary may already be in the system
	//The label and is defined by may already be defined and don't require re-saving
    private List<String> generateN3Required(VitroRequest vreq) {
    	List<String> n3Required = list(
    	        getPrefixesString() + "\n" +
    	        "?subject ?predicate ?conceptNode .\n" +
    	        "?conceptNode <" + RDF.type.getURI() + "> <" + this.SKOSConceptType + "> ."
    	);
        //"?conceptNode <" + RDF.type.getURI() + "> <http://www.w3.org/2002/07/owl#Thing> ."

    	List<String> inversePredicate = getInversePredicate(vreq);
		//Adding inverse predicate if it exists
		if(inversePredicate.size() > 0) {
			n3Required.add("?conceptNode <" + inversePredicate.get(0) + "> ?subject .");
		}
		return n3Required;
    }

   //Optional N3, includes possibility of semantic type which may or may not be included
    //label and source are independent of semantic type
    //concept semantic type uri is a placeholder which is actually processed in the sparql update preprocessor
	private List<String> generateN3Optional() {
		return list("?conceptNode <" + RDFS.label.getURI() + "> ?conceptLabel .\n" +
    	        "?conceptNode <" + RDFS.isDefinedBy.getURI() + "> ?conceptSource .",
				"?conceptNode <" + RDF.type + "> ?conceptSemanticTypeURI ." +
    	        "?conceptSemanticTypeURI <" + RDFS.label.getURI() + "> ?conceptSemanticTypeLabel ." +
    	        "?conceptSemanticTypeURI <" + RDFS.subClassOf + "> <" + SKOSConceptType + "> .",
    	        "?conceptNode <" + this.SKOSNarrowerURI + "> ?conceptNarrowerURI ." +
    	        "?conceptNarrowerURI <" + this.SKOSBroaderURI + "> ?conceptNode .",
    	        "?conceptNode <" + this.SKOSBroaderURI + "> ?conceptBroaderURI ." +
    	    	"?conceptBroaderURI <" + this.SKOSNarrowerURI + "> ?conceptNode ."
    	);
    }




	/*
	 * Get new resources
	 */
	 private Map<String, String> generateNewResources(VitroRequest vreq) {
			HashMap<String, String> newResources = new HashMap<String, String>();
			//There are no new resources here, the concept node uri doesn't
			//get created but already exists, and vocab uri should already exist as well
			//Adding concept semantic type uri just to test - note this isn't really on the form at all
			newResources.put("conceptSemanticTypeURI", null);
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
    	//The URI of the node that defines the concept
    	urisOnForm.add("conceptNode");
    	urisOnForm.add("conceptSource");
    	urisOnForm.add("conceptSemanticTypeURI");
    	urisOnForm.add("conceptBroaderURI");
    	urisOnForm.add("conceptNarrowerURI");
    	editConfiguration.setUrisOnform(urisOnForm);
    	//Also need to add the label of the concept
    	literalsOnForm.add("conceptLabel");
    	literalsOnForm.add("conceptSemanticTypeLabel");

    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }


    /**
     * Set SPARQL Queries and supporting methods
     */


    private void setSparqlQueries(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	//Sparql queries defining retrieval of literals etc.
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    	editConfiguration.setSparqlForAdditionalUrisInScope(new HashMap<String, String>());
      	editConfiguration.setSparqlForExistingLiterals(new HashMap<String, String>());
    	editConfiguration.setSparqlForExistingUris(new HashMap<String, String>());
    }


	/**
	 *
	 * Set Fields and supporting methods
	 */

	private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	setConceptNodeField(editConfiguration, vreq);
    	setConceptLabelField(editConfiguration, vreq);
    	setVocabURIField(editConfiguration, vreq);
    	setConceptSemanticTypeURIField(editConfiguration,vreq);
    	setConceptSemanticTypeLabelField(editConfiguration,vreq);
    	setConceptBroaderURIField(editConfiguration, vreq);
    	setConceptNarrowerURIField(editConfiguration, vreq);
    }

	private void setConceptNarrowerURIField(
			EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptNarrowerURI"));
	}

	private void setConceptBroaderURIField(
			EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptBroaderURI"));

	}

	//this field will be hidden and include the concept node URI
	private void setConceptNodeField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptNode").
				setValidators(list("nonempty")));
	}



	private void setVocabURIField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptSource"));
	}



	private void setConceptLabelField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptLabel").
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}

	//This will also be a URI
	private void setConceptSemanticTypeURIField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptSemanticTypeURI")
				);
	}

	private void setConceptSemanticTypeLabelField(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		editConfiguration.addField(new FieldVTwo().
				setName("conceptSemanticTypeLabel").
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}


    //Add preprocessor

   private void addPreprocessors(EditConfigurationVTwo editConfiguration) {
	  //An Edit submission preprocessor for enabling addition of multiple terms for a single search
	   //TODO: Check if this is the appropriate way of getting model

	   //Passing model to check for any URIs that are present

	   editConfiguration.addEditSubmissionPreprocessor(
			   new AddAssociatedConceptsPreprocessor(editConfiguration));
	   editConfiguration.addModelChangePreprocessor(new ConceptSemanticTypesPreprocessor());

	}


	//Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		//These are the concepts that already exist currently
		formSpecificData.put("existingConcepts", getExistingConcepts(vreq));
		//Return url for adding user defined concept
		formSpecificData.put("userDefinedConceptUrl", getUserDefinedConceptUrl(vreq));
		//Add URIs and labels for different services
		formSpecificData.put("searchServices", ConceptSearchServiceUtils.getVocabSources());
		List<String> inversePredicate = getInversePredicate(vreq);
		if(inversePredicate.size() > 0) {
			formSpecificData.put("inversePredicate", inversePredicate.get(0));
		} else {
			formSpecificData.put("inversePredicate", "");
		}
		editConfiguration.setFormSpecificData(formSpecificData);
	}


	//
	private Object getUserDefinedConceptUrl(VitroRequest vreq) {
		String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
		String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
		String generatorName = "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddUserDefinedConceptGenerator";
		String editUrl = EditConfigurationUtils.getEditUrl(vreq);

		return editUrl + "?subjectUri=" + UrlBuilder.urlEncode(subjectUri) +
		"&predicateUri=" + UrlBuilder.urlEncode(predicateUri) +
		"&editForm=" + UrlBuilder.urlEncode(generatorName);
	}

	private List<AssociatedConceptInfo> getExistingConcepts(VitroRequest vreq) {
		Individual individual = EditConfigurationUtils.getSubjectIndividual(vreq);
	    List<Individual> concepts = individual.getRelatedIndividuals(
	    		EditConfigurationUtils.getPredicateUri(vreq));
		List<AssociatedConceptInfo> associatedConcepts = getAssociatedConceptInfo(concepts, vreq);
		sortConcepts(associatedConcepts);
		return associatedConcepts;
	}


	private void sortConcepts(List<AssociatedConceptInfo> concepts) {
	    concepts.sort(new AssociatedConceptInfoComparator());
	    log.debug("Concepts should be sorted now" + concepts.toString());
	}


	//To determine whether or not a concept is a user generated or one from an external vocab source.
	//we cannot rely on whether or not it is a skos concept because incorporating UMLS semantic network classes as
	//SKOS concept subclasses means that even concepts from an external vocab source might be considered SKOS concepts
	//Instead, we will simply determine whether a concept is defined by an external vocabulary source and use that
	//as the primary indicator of whether a concept is from an external vocabulary source or a user generated concept
	private List<AssociatedConceptInfo> getAssociatedConceptInfo(
			List<Individual> concepts, VitroRequest vreq) {
		List<AssociatedConceptInfo> info = new ArrayList<AssociatedConceptInfo>();
		 for ( Individual conceptIndividual : concepts ) {
			 	boolean userGenerated = true;
			 	//Note that this isn't technically
			 	String conceptUri =  conceptIndividual.getURI();
			 	String conceptLabel = conceptIndividual.getName();

			 	//Check if defined by an external vocabulary source
		 		List<ObjectPropertyStatement> vocabList = conceptIndividual.getObjectPropertyStatements(RDFS.isDefinedBy.getURI());
		 		String vocabSource = null;
		 		String vocabLabel = null;
		 		if(vocabList != null && vocabList.size() > 0) {
		 			userGenerated = false;
		 			vocabSource = vocabList.get(0).getObjectURI();
		 			Individual sourceIndividual = EditConfigurationUtils.getIndividual(vreq, vocabSource);
		 			//Assuming name will get label
		 			vocabLabel = sourceIndividual.getName();
		 		}



			 	if(userGenerated) {
			 		//if the concept in question is skos - which would imply a user generated concept
			 		info.add(new AssociatedConceptInfo(conceptLabel, conceptUri, null, null, SKOSConceptType, null, null));
			 	} else {
			 		String conceptSemanticTypeURI = null;
			 		String conceptSemanticTypeLabel = null;
			 		//Can a concept have multiple semantic types?  Currently we are only returning the first one
			 		//TODO: Change this into a sparql query that returns all types for the concept that are subclasses of SKOS concepts
			 		HashMap<String, String> typeAndLabel = this.getConceptSemanticTypeQueryResults(conceptIndividual.getURI(), ModelAccess.on(vreq).getOntModel());
			 		if(typeAndLabel.containsKey("semanticTypeURI")) {
			 			conceptSemanticTypeURI = typeAndLabel.get("semanticTypeURI");
			 		}
			 		if(typeAndLabel.containsKey("semanticTypeLabel")) {
			 			conceptSemanticTypeLabel = typeAndLabel.get("semanticTypeLabel");
			 		}

			 		//Assuming this is from an external vocabulary source
			 		info.add(new AssociatedConceptInfo(conceptLabel, conceptUri, vocabSource, vocabLabel, null, conceptSemanticTypeURI, conceptSemanticTypeLabel));

			 	}
		 }
		 return info;
	}

	private HashMap<String, String> getConceptSemanticTypeQueryResults(String conceptURI, OntModel ontModel) {
		HashMap<String, String> typeAndLabel = new HashMap<String, String>();
		String queryStr = "SELECT ?semanticTypeURI ?semanticTypeLabel WHERE { " +
				"<" + conceptURI + "> <" + RDF.type.getURI() + "> ?semanticTypeURI . " +
				"?semanticTypeURI <" + RDFS.subClassOf.getURI() + "> <" + this.SKOSConceptType + ">.  " +
				"?semanticTypeURI <" + RDFS.label.getURI() + "> ?semanticTypeLabel ." +
				"}";
		 QueryExecution qe = null;
	        try{
	            Query query = QueryFactory.create(queryStr);
	            qe = QueryExecutionFactory.create(query, ontModel);
                ResultSet results = null;
                results = qe.execSelect();

                while( results.hasNext()){
                	QuerySolution qs = results.nextSolution();
                	if(qs.get("semanticTypeURI") != null) {
                		Resource semanticTypeURI = qs.getResource("semanticTypeURI");
                		log.debug("Semantic Type URI returned " + semanticTypeURI.getURI());
                		typeAndLabel.put("semanticTypeURI", semanticTypeURI.getURI());
                	}
                	if(qs.get("semanticTypeLabel") != null) {
                		Literal semanticTypeLabel = qs.getLiteral("semanticTypeLabel");
                		log.debug("Semantic Type label returned " + semanticTypeLabel.getString());
                		typeAndLabel.put("semanticTypeLabel", semanticTypeLabel.getString());
                	}


                }
	        }catch(Exception ex){
	            throw new Error("Error in executing query string: \n" + queryStr + '\n' + ex.getMessage());
	        }finally{
	            if( qe != null)
	                qe.close();
	        }
		return typeAndLabel;
	}

	public class AssociatedConceptInfo {
		private String conceptLabel;
		private String conceptURI;
		private String vocabURI;
		private String vocabLabel;
		private String type; //In case of SKOS concept, will have skos concept type
		private String conceptSemanticTypeURI; //For some services, such as UMLS, we have a semantic type associated
		private String conceptSemanticTypeLabel;
		public AssociatedConceptInfo(String inputLabel, String inputURI, String inputVocabURI, String inputVocabLabel, String inputType, String inputConceptSemanticTypeURI, String inputConceptSemanticTypeLabel) {
			this.conceptLabel = inputLabel;
			this.conceptURI = inputURI;
			this.vocabURI = inputVocabURI;
			this.vocabLabel = inputVocabLabel;
			this.type = inputType;
			this.conceptSemanticTypeURI = inputConceptSemanticTypeURI;
			this.conceptSemanticTypeLabel = inputConceptSemanticTypeLabel;
		}

		//Getters
		public String getConceptLabel() {
			return conceptLabel;
		}

		public  String getConceptURI() {
			return conceptURI;
		}

		public  String getVocabURI() {
			return vocabURI;
		}

		public  String getVocabLabel(){
			return vocabLabel;
		}

		public  String getType(){
			return type;
		}

		public  String getConceptSemanticTypeURI(){
			return conceptSemanticTypeURI;
		}

		public  String getConceptSemanticTypeLabel(){
			return conceptSemanticTypeLabel;
		}

	}

	public class AssociatedConceptInfoComparator implements Comparator<AssociatedConceptInfo>{
		public int compare(AssociatedConceptInfo concept1, AssociatedConceptInfo concept2) {
	    	String concept1Label = concept1.getConceptLabel().toLowerCase();
	    	String concept2Label = concept2.getConceptLabel().toLowerCase();
	    	return concept1Label.compareTo(concept2Label);
	    }
	}




}
