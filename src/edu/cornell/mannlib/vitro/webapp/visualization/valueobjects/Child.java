/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Set;
import java.util.HashSet;
/**
 * @author bkoniden
 * Deepak Konidena
 *
 */
public class Child extends Individual {

	Set<BiboDocument> documents = new HashSet<BiboDocument>();
	
	public Child(String individualURI) {
		super(individualURI);
	}
	
	public Set<BiboDocument> getDocuments() {
		return documents;
	}

	public Child(String individualURI, String individualLabel) {
		super(individualURI, individualLabel);
	}
	
	@Override
	public boolean equals(Object other){
		boolean result = false;
		if (other instanceof Child){
			Child person = (Child) other;
			result = (this.getIndividualLabel().equals(person.getIndividualLabel())
						&& this.getIndividualURI().equals(person.getIndividualURI()));
		}
		return result;
	}
	
	@Override 
	public int hashCode(){
		return(41*(getIndividualLabel().hashCode() + 41*(getIndividualURI().hashCode())));
	}
}
