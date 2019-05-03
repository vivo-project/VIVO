/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.ModelFactoryInterface;

@SuppressWarnings("deprecation,serial")
public class ModelConstructorUtilities {

	/**
	 * @deprecated
	 */
	private static final Map<String, ModelFactoryInterface> modelTypeIdentifierToFactory = new HashMap<String, ModelFactoryInterface>() {{
	}};

	/**
	 * @deprecated
	 */
	public static final Map<String, String> modelTypeToHumanReadableName = new HashMap<String, String>() {{
	}};

	public static Model getOrConstructModel(String uri, String modelType, RDFService rdfService)
			throws MalformedQueryParametersException {
		return modelTypeIdentifierToFactory.get(modelType).getOrCreateModel(uri, rdfService);
	}
}
