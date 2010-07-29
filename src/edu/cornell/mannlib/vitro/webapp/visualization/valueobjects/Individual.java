/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

public class Individual {
	
	private String individualLabel;
	private String individualURL;
	
	public Individual(String individualURL, String individualLabel) {
		this.individualURL = individualURL;
		this.individualLabel = individualLabel;
	}
	
	public Individual(String individualURL) {
		this(individualURL, "");
	}
	
	public String getIndividualLabel() {
		return individualLabel;
	}
	
	public void setIndividualLabel(String individualLabel) {
		this.individualLabel = individualLabel;
	}

	public String getIndividualURL() {
		return individualURL;
	}
	

}
