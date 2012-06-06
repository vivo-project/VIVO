/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
//Returns the appropriate n3 for selection of classes from within class group
public  class ProcessInternalClassDataGetterN3 extends ProcessIndividualsForClassesDataGetterN3 {
	private static String classType = "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter";

	private static String internalClassVarNameBase = "isInternal";
	public ProcessInternalClassDataGetterN3(JSONObject jsonObject){
		super(jsonObject);
		
	}
	//Pass in variable that represents the counter 

	//Original menu managemenet didn't include the top level class group as type for internal classes
    //That can be included here if need be, but for now just adding the type alone
    public List<String> retrieveN3Required(int counter) {
    	List<String> requiredN3 = new ArrayList<String>();
    	String partialN3 = this.getN3ForTypePartial(counter) + ".";
    	requiredN3.add(getPrefixes() + partialN3); 
    	requiredN3.addAll(this.addIndividualClassesN3(counter));
    	return requiredN3;
    	
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
    
     
    
   public List<FieldVTwo> retrieveFields(int counter) {
	   List<FieldVTwo> fields = super.retrieveFields(counter);
	   fields.add(new FieldVTwo().setName(getVarName(internalClassVarNameBase, counter)));
	 
	   return fields;
   }
   
   //These var names  match the names of the elements within the json object returned with the info required for the data getter
   
   public List<String> getLiteralVarNamesBase() {
	   return Arrays.asList(internalClassVarNameBase);   
   }

   //these are for the fields ON the form
   public List<String> getUriVarNamesBase() {
	   return Arrays.asList(individualClassVarNameBase);   
   }
   
   @Override
   public String getClassType() {
	   return classType;
   }


}


