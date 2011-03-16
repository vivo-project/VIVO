/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

public class ConstructedModelTracker {

	private static Map<String, Model> modelIdentifierToConstructedModel = new HashMap<String, Model>();
	
	public static void trackModel(String identifier, Model model) {
		modelIdentifierToConstructedModel.put(identifier, model);
	}
	
	public static Model getModel(String identifier) {
		return modelIdentifierToConstructedModel.get(identifier);
	}
	
	public static String generateModelIdentifier(String uri, String modelType) {
		
		if (uri == null) {
			uri = "";
		}
		return modelType +  "$" + uri;
	}
	
	public static Map<String, Model> getAllModels() {
		return modelIdentifierToConstructedModel;
	}
	
}
