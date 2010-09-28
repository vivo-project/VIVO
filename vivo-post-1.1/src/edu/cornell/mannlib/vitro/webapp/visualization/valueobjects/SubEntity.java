package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Set;
import java.util.HashSet;

public class SubEntity extends Individual {

	Set<BiboDocument> publications = new HashSet<BiboDocument>();
	
	public SubEntity(String individualURI) {
		super(individualURI);
	}
	
	public Set<BiboDocument> getDocuments() {
		return publications;
	}

	public SubEntity(String individualURI, String individualLabel) {
		super(individualURI, individualLabel);
	}
	
	@Override
	public boolean equals(Object other){
		boolean result = false;
		if (other instanceof SubEntity){
			SubEntity person = (SubEntity) other;
			result = (this.getIndividualLabel().equals(person.getIndividualLabel())
						&& this.getIndividualURI().equals(person.getIndividualURI()));
		}
		return result;
	}
	
	@Override 
	public int hashCode(){
		return(41*(getIndividualLabel().hashCode() + 41*(getIndividualURI().hashCode())));
	}

	public void addPublications(BiboDocument biboDocument) {
		this.publications.add(biboDocument);
	}
}
