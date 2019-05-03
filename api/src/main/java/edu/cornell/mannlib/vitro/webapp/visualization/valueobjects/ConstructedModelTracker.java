/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.apache.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.IllegalConstructedModelIdentifierException;

public class ConstructedModelTracker {

	private static Map<String, Model> modelIdentifierToConstructedModel = new HashMap<String, Model>();

	public static void trackModel(String identifier, Model model) {
		modelIdentifierToConstructedModel.put(identifier, model);
	}

	public static Model getModel(String identifier) {
		return modelIdentifierToConstructedModel.get(identifier);
	}

	public static Model removeModel(String uri, String modelType) {
		return modelIdentifierToConstructedModel.remove(generateModelIdentifier(uri, modelType));
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

	public static ConstructedModel parseModelIdentifier(String modelIdentifier)
			throws IllegalConstructedModelIdentifierException {

		String[] parts = StringUtils.split(modelIdentifier, '$');

		if (parts.length == 0) {
			throw new IllegalConstructedModelIdentifierException(modelIdentifier + " provided.");
		} else if (parts.length == 1) {
			return new ConstructedModel(parts[0], null);
		} else {
			return new ConstructedModel(parts[0], parts[1]);
		}
	}
}
