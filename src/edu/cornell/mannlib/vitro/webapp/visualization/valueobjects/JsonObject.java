/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonObject is used for creating data in JSON format, 
 * by just using the fields that are required to be included.
 * @author bkoniden
 * Deepak Konidena
 */
public class JsonObject {
	
	private String label;
	private List<List<Integer>> data = new ArrayList<List<Integer>>();
	private String entityURI;
	private String visMode;
	private List<String> organizationType = new ArrayList<String>();
	private List<String> stopWords = new ArrayList<String>();
	
	public List<String> getStopWords() {
		return stopWords;
	}

	public void setStopWords(List<String> stopWords) {
		this.stopWords = stopWords;
	}

	public List<String> getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(List<String> organizationType) {
		this.organizationType = organizationType;
	}

	public String getEntityURI() {
		return entityURI;
	}

	public void setEntityURI(String entityURI) {
		this.entityURI = entityURI;
	}

	public String getVisMode() {
		return visMode;
	}

	public void setVisMode(String visMode) {
		this.visMode = visMode;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<List<Integer>> getYearToPublicationCount() {
		return data;
	}


	
	public JsonObject(String label){
		this.label = label;
	}
	
	public void setYearToPublicationCount(List<List<Integer>> yearToPublicationCount){
		this.data = yearToPublicationCount;
	}
	
}
