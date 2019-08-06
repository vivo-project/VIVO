/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

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
import org.apache.jena.rdf.model.ResourceFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

//Returns the appropriate n3 for selection of classes from within class group
public  class ProcessInternalClassDataGetterN3 extends ProcessIndividualsForClassesDataGetterN3 {
	private static String classType = "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter";

	private static String internalClassVarNameBase = "isInternal";
	private Log log = LogFactory.getLog(ProcessInternalClassDataGetterN3.class);

	public ProcessInternalClassDataGetterN3(){
		super();

	}
	//Pass in variable that represents the counter
	//Saving both type and class group here
    //That can be included here if need be, but for now just adding the type alone
    public List<String> retrieveN3Required(int counter) {
    	return super.retrieveN3Required(counter);
    }


    //returns n3 defining internal class
	private List<String> addInternalClassN3(int counter) {
		List<String> internalClassN3 = new ArrayList<String>();
    	String dataGetterVar = getDataGetterVar(counter);
		internalClassN3.add(dataGetterVar + " <" + DisplayVocabulary.RESTRICT_RESULTS_BY_INTERNAL + "> " +
				this.getN3VarName(internalClassVarNameBase, counter) + " .");
		return internalClassN3;

	}

	public List<String> retrieveN3Optional(int counter) {
		List<String> optionalN3 = new ArrayList<String>();
		//If internal add that as well
    	optionalN3.addAll(this.addInternalClassN3(counter));
    	return optionalN3;
    }

    //These methods will return the literals and uris expected within the n3
    //and the counter is used to ensure they are numbered correctly

    public List<String> retrieveLiteralsOnForm(int counter) {
    	//no literals, just the class group URI
    	List<String> literalsOnForm = new ArrayList<String>();
    	literalsOnForm.add(getVarName(internalClassVarNameBase, counter));
    	return literalsOnForm;

    }

    //URIs on form are same as individuals for class group so no need to reimplement
    //i.e. class groups and individuals selected within class group

   public List<FieldVTwo> retrieveFields(int counter) {
	   List<FieldVTwo> fields = super.retrieveFields(counter);
	   fields.add(new FieldVTwo().setName(getVarName(internalClassVarNameBase, counter)));

	   return fields;
   }

   //These var names  match the names of the elements within the json object returned with the info required for the data getter

   public List<String> getLiteralVarNamesBase() {
	   return Arrays.asList(internalClassVarNameBase);
   }

   //get URI Var Names base is same as ProcessIndividualsForClassGroup: classGroup and individualClassVarNameBase

   @Override
   public String getClassType() {
	   return classType;
   }

   public void populateExistingValues(String dataGetterURI, int counter, OntModel queryModel) {
	   //First, put dataGetterURI within scope as well
	   //((ProcessDataGetterAbstract)this).populateExistingDataGetterURI(dataGetterURI, counter);
	   this.populateExistingDataGetterURI(dataGetterURI, counter);
	   //Put in type
	   this.populateExistingClassType(this.getClassType(), counter);
	   //Sparql queries for values to be executed
	   //And then placed in the correct place/literal or uri
	   String querystr = getExistingValuesInternalClass(dataGetterURI);
	   QueryExecution qe = null;
	   Literal internalClassLiteral = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           String classGroupURI = null;
           List<String> individualsForClasses = new ArrayList<String>();
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   //Class group
        	   Resource classGroupResource = qs.getResource("classGroup");
        	   String classGroupVarName = this.getVarName(classGroupVarBase, counter);
        	   if(classGroupURI == null) {
	        	   //Put both literals in existing literals
	        	   existingUriValues.put(this.getVarName(classGroupVarBase, counter),
	        			   new ArrayList<String>(Arrays.asList(classGroupResource.getURI())));
        	   }
        	   //Individuals For classes
        	   Resource individualForClassResource = qs.getResource("individualForClass");
        	   individualsForClasses.add(individualForClassResource.getURI());
        	   //If internal class value is present and we have not already saved it in a previous result iteration
        	   if(qs.get("internalClass") != null && internalClassLiteral == null) {

        		   internalClassLiteral= qs.getLiteral("internalClass");
        		   existingLiteralValues.put(this.getVarName(internalClassVarNameBase, counter),
	        			   new ArrayList<Literal>(Arrays.asList(internalClassLiteral)));
        	   }
           }
           //Put array of individuals for classes within
           existingUriValues.put(this.getVarName(individualClassVarNameBase, counter),
    			   new ArrayList<String>(individualsForClasses));
           //Final check, in case no internal class flag was returned, set to false
           if(internalClassLiteral == null) {
        	   existingLiteralValues.put(this.getVarName(internalClassVarNameBase, counter),
        			   new ArrayList<Literal>(
        					   Arrays.asList(ResourceFactory.createPlainLiteral("false"))
        					   ));
           }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }


   }


   //?dataGetter a FixedHTMLDataGetter ; display:saveToVar ?saveToVar; display:htmlValue ?htmlValue .
   protected String getExistingValuesInternalClass(String dataGetterURI) {
	   String query = this.getSparqlPrefix() + " SELECT ?classGroup  ?individualForClass ?internalClass WHERE {" +
			   "<" + dataGetterURI + "> <" + DisplayVocabulary.FOR_CLASSGROUP + "> ?classGroup  . \n" +
			   "OPTIONAL {<" + dataGetterURI + "> <" + DisplayVocabulary.GETINDIVIDUALS_FOR_CLASS + "> ?individualForClass . }\n" +
			   "OPTIONAL {<" + dataGetterURI + "> <" + DisplayVocabulary.RESTRICT_RESULTS_BY_INTERNAL + "> ?internalClass .} \n" +
			   "}";
	   return query;
   }


   public ObjectNode getExistingValuesJSON(String dataGetterURI, OntModel queryModel, ServletContext context) {
	   ObjectNode jObject = new ObjectMapper().createObjectNode();
	   jObject.put("dataGetterClass", classType);
	   //Update to include class type as variable
	   jObject.put(classTypeVarBase, classType);
	   //Get selected class group, if internal class, and classes selected from class group
	   getExistingClassGroupAndInternalClass(dataGetterURI, jObject, queryModel);
	   //Get all classes in the class group
	   ((ProcessClassGroupDataGetterN3) this).getExistingClassesInClassGroup(context, dataGetterURI, jObject);
	   return jObject;
   }

   private void getExistingClassGroupAndInternalClass(String dataGetterURI, ObjectNode jObject, OntModel queryModel) {
	   String querystr = getExistingValuesInternalClass(dataGetterURI);
	   QueryExecution qe = null;
	   Literal internalClassLiteral = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           ArrayNode individualsForClasses = new ObjectMapper().createArrayNode();
           String classGroupURI = null;
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   if(classGroupURI == null) {
	        	   Resource classGroupResource = qs.getResource("classGroup");
	        	   classGroupURI = classGroupResource.getURI();
        	   }
        	   //individuals for classes - this may also be optional in case entire class group selected and internal class
        	   if(qs.get("individualForClass") != null ) {
        		   Resource individualForClassResource = qs.getResource("individualForClass");
        		   individualsForClasses.add(individualForClassResource.getURI());
        	   }
        	 //Put both literals in existing literals
        	 //If internal class value is present and we have not already saved it in a previous result iteration
        	   if(qs.get("internalClass") != null && internalClassLiteral == null) {
        		   internalClassLiteral= qs.getLiteral("internalClass");
        	   }
           }


          jObject.put("classGroup", classGroupURI);
          //this is a json array
          jObject.set(individualClassVarNameBase, individualsForClasses);
          //Internal class - if null then add false otherwise use the value
          if(internalClassLiteral != null) {
        	  jObject.put(internalClassVarNameBase, internalClassLiteral.getString());
          } else {
        	  jObject.put(internalClassVarNameBase, "false");
          }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }
   }

}


