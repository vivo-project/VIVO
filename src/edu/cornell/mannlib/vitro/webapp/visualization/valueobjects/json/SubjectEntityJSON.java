/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;

public class SubjectEntityJSON {
	
	private String subjectEntityLabel;
	private String subjectEntityURI;
	private Map<String, String> parentURIToLabel = new HashMap<String, String>();
	
	public SubjectEntityJSON(String subjectEntityURI, String label,
			Set<Individual> parentOrganizations) {
		this.subjectEntityURI = subjectEntityURI;
		this.subjectEntityLabel = label;
		
		this.setParentURIToLabel(parentOrganizations);
	}

	public String getSubjectEntityURI() {
		return subjectEntityURI;
	}

	public void setSubjectEntityURI(String subjectEntityURI) {
		this.subjectEntityURI = subjectEntityURI;
	}

	public String getSubjectEntityLabel() {
		return subjectEntityLabel;
	}

	public void setSubjectEntityLabel(String label) {
		this.subjectEntityLabel = label;
	}

	public Map<String, String> getParentURIToLabel() {
		return parentURIToLabel;
	}

	public void setParentURIToLabel(Set<Individual> parentOrganizations) {
		for (Individual parentOrganization : parentOrganizations) {
			this.parentURIToLabel.put(parentOrganization.getIndividualURI(), parentOrganization.getIndividualLabel());
		}
	}
}
