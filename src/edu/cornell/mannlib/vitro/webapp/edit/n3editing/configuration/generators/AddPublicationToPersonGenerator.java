/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

public class AddPublicationToPersonGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {

	 @Override
	    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
	            HttpSession session) {
	    	 EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();    	    	
	    	 initBasics(editConfiguration, vreq);
	         initPropertyParameters(vreq, session, editConfiguration);
	         initObjectPropForm(editConfiguration, vreq);               
	     		//Overriding url to return to
	         setUrlToReturnTo(editConfiguration, vreq);
	         setVarNames(editConfiguration);
	        
	         
	     	// Required N3
	     	editConfiguration.setN3Required(generateN3Required());    
	     	
	     	// Optional N3 
	     	editConfiguration.setN3Optional( generateN3Optional());	
	         	    	        
	     	editConfiguration.setNewResources( generateNewResources(vreq) );
	     	
	     	//In scope
	     	setUrisAndLiteralsInScope(editConfiguration, vreq);
	     	
	     	//on Form
	     	setUrisAndLiteralsOnForm(editConfiguration, vreq);
	     	    	
	     	//Sparql queries
	     	setSparqlQueries(editConfiguration, vreq);
	     	
	     	//set fields
	     	setFields(editConfiguration, vreq, EditConfigurationUtils.getPredicateUri(vreq));
	     	
	     	//template file
	     	editConfiguration.setTemplate("addAuthorsToInformationResource.ftl");
	     	//no validators or preprocessors
	     	
	         //Adding additional data, specifically edit mode
	         addFormSpecificData(editConfiguration, vreq);
	     	return editConfiguration;
	    }





		private void setVarNames(EditConfigurationVTwo editConfiguration) {
			 editConfiguration.setVarNameForSubject("infoResource");               
	         editConfiguration.setVarNameForPredicate("predicate");      
	         editConfiguration.setVarNameForObject("authorshipUri");
			
		}
		
		
		
		private void setUrlToReturnTo(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
			editConfiguration.setUrlPatternToReturnTo(EditConfigurationUtils.getFormUrlWithoutContext(vreq));
			
		}
		
		/***N3 strings both required and optional***/
		private List<String> generateN3Optional() {
			// TODO Auto-generated method stub
			return null;
		}


		private List<String> generateN3Required() {
			// TODO Auto-generated method stub
			return null;
		}
		
		/**  Get new resources	 */
		 private Map<String, String> generateNewResources(VitroRequest vreq) {					
				String DEFAULT_NS_TOKEN=null; //null forces the default NS
				
				HashMap<String, String> newResources = new HashMap<String, String>();			
				newResources.put("role", DEFAULT_NS_TOKEN);
				newResources.put("roleActivity", DEFAULT_NS_TOKEN);
				newResources.put("intervalNode", DEFAULT_NS_TOKEN);
				newResources.put("startNode", DEFAULT_NS_TOKEN);
				newResources.put("endNode", DEFAULT_NS_TOKEN);
				return newResources;
			}
		
		/** Set URIS and Literals In Scope and on form and supporting methods	 */   
	    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
	    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
	    	editConfiguration.setUrisInScope(urisInScope);    	
	    	HashMap<String, List<Literal>> literalsInScope = new HashMap<String, List<Literal>>();
	    	editConfiguration.setLiteralsInScope(literalsInScope);    	

	    }
		
	    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
	    	List<String> urisOnForm = new ArrayList<String>();    	
	    	//add role activity and roleActivityType to uris on form
	    	urisOnForm.add("roleActivity");
	    	urisOnForm.add("roleActivityType");
	    	//Also adding the predicates
	    	//TODO: Check how to override this in case of default parameter? Just write hidden input to form?
	    	urisOnForm.add("roleToActivityPredicate");
	    	urisOnForm.add("activityToRolePredicate");
	    	editConfiguration.setUrisOnform(urisOnForm);
	    	
	    	//activity label and role label are literals on form
	    	List<String> literalsOnForm = new ArrayList<String>();
	    	literalsOnForm.add("activityLabel");
	    	literalsOnForm.add("roleLabel");
	    	editConfiguration.setLiteralsOnForm(literalsOnForm);
	    }   
	    
	    /** Set SPARQL Queries and supporting methods. */        
	    private void setSparqlQueries(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {        
	        //Queries for activity label, role label, start Field value, end Field value
	        HashMap<String, String> map = new HashMap<String, String>();
	    	editConfiguration.setSparqlForExistingUris(map);
	    }
	    
	    /**
		 * 
		 * Set Fields and supporting methods
		 */
		
		private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
	    	
	    }
		
		//Form specific data
		public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
			HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
			editConfiguration.setFormSpecificData(formSpecificData);
		}


}
