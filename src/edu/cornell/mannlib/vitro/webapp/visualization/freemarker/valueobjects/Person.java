/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.Set;
import java.util.HashSet;

/**
 * @author bkoniden
 * Deepak Konidena
 */
public class Person extends Individual {

	Set<BiboDocument> documents = new HashSet<BiboDocument>();
	
	public Person(String individualURI) {
		super(individualURI);
	}
	
	public Set<BiboDocument> getDocuments() {
		return documents;
	}

	public Person(String individualURI, String individualLabel) {
		super(individualURI, individualLabel);
	}

}
