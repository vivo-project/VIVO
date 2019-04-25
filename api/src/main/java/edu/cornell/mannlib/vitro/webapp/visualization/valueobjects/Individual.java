/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

public class Individual {

	private String individualLabel;
	private String individualURI;

	public Individual(String individualURI, String individualLabel) {
		this.individualURI = individualURI;
		this.individualLabel = individualLabel;
	}

	public Individual(String individualURI) {
		this(individualURI, "");
	}

	public String getIndividualLabel() {
		return individualLabel;
	}

	public void setIndividualLabel(String individualLabel) {
		this.individualLabel = individualLabel;
	}

	public String getIndividualURI() {
		return individualURI;
	}


}
