/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectEntityJSON {
	@JsonProperty
	private String subjectEntityLabel;

	@JsonProperty
	private String subjectEntityURI;

	@JsonProperty
	private Map<String, String> parentURIToLabel = new HashMap<String, String>();

	public SubjectEntityJSON(String subjectEntityURI, String label,
			Set<Individual> parentOrganizations) {
		this.subjectEntityURI = subjectEntityURI;
		this.subjectEntityLabel = label;

		this.setParentURIToLabel(parentOrganizations);
	}

	public SubjectEntityJSON(String subjectEntityURI, String label,
							 Map<String, String> parentURIToLabel) {
		this.subjectEntityURI = subjectEntityURI;
		this.subjectEntityLabel = label;
		this.parentURIToLabel = parentURIToLabel;
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
