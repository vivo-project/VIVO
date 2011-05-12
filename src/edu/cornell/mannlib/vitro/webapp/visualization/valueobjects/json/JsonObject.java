/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * JsonObject is used for creating data in JSON format, 
 * by just using the fields that are required to be included.
 * @author bkoniden
 * Deepak Konidena
 */
public class JsonObject {
	
	private String label;
	private String lastCachedAtDateTime;
	private List<List<Integer>> data = new ArrayList<List<Integer>>();
	private String entityURI;
	private String visMode;
	private List<String> organizationType = new ArrayList<String>();
	
	public List<String> getOrganizationTypes() {
		return organizationType;
	}

	public void setOrganizationTypes(List<String> organizationType) {
		this.organizationType = organizationType;
	}
	
	public void setOrganizationTypes(Set<String> givenOrganizationType) {
		for (String type : givenOrganizationType) {
			this.organizationType.add(type);
		}
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

	public List<List<Integer>> getYearToActivityCount() {
		return data;
	}

	public JsonObject(String label) {
		this.label = label;
	}
	
	public void setYearToActivityCount(List<List<Integer>> yearToPublicationCount) {
		this.data = yearToPublicationCount;
	}

	public void setLastCachedAtDateTime(String lastCachedAtDateTime) {
		this.lastCachedAtDateTime = lastCachedAtDateTime;
	}

	public String getLastCachedAtDateTime() {
		return lastCachedAtDateTime;
	}
}
