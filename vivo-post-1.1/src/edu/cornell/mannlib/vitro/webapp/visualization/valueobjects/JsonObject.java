package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * JsonObject is used for creating data in JSON format, 
 * by just using the fields that are required to be included.
 * @author bkoniden
 *
 */
public class JsonObject {
	
	private String label;
	//ssprivate Map<String, Integer> yearToPublicationCount = new HashMap<String, Integer>();
	private List<List<Integer>> yearToPublicationCount = new ArrayList<List<Integer>>();
	private String entityURI;
	private String visMode;
	
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

	public JsonObject(String label){
		this.label = label;
	}
	
	public void setYearToPublicationCount(List<List<Integer>> yearToPublicationCount){
		this.yearToPublicationCount = yearToPublicationCount;
	}
	
}
