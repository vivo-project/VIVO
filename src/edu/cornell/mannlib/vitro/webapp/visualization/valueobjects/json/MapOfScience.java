/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json;

import java.util.HashMap;
import java.util.Map;

public class MapOfScience {
	
	private String uri;
	private String label;
	private String type;
	private int pubsMapped;
	private int pubsUnmapped;
	private String lastCachedAtDateTime;
	private Map<String, Integer> subdisciplineActivity = new HashMap<String, Integer>();

	public MapOfScience(String uri) {
		this.uri = uri;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUri() {
		return uri;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setPubsMapped(int pubsMapped) {
		this.pubsMapped = pubsMapped;
	}
	public int getPubsMapped() {
		return pubsMapped;
	}
	public void setPubsUnmapped(int pubsUnmapped) {
		this.pubsUnmapped = pubsUnmapped;
	}
	public int getPubsUnmapped() {
		return pubsUnmapped;
	}
	public void setSubdisciplineActivity(Map<String, Integer> subdisciplineActivity) {
		this.subdisciplineActivity = subdisciplineActivity;
	}
	public Map<String, Integer> getSubdisciplineActivity() {
		return subdisciplineActivity;
	}

	public void setLastCachedAtDateTime(String lastCachedAtDateTime) {
		this.lastCachedAtDateTime = lastCachedAtDateTime;
	}

	public String getLastCachedAtDateTime() {
		return lastCachedAtDateTime;
	}

}
