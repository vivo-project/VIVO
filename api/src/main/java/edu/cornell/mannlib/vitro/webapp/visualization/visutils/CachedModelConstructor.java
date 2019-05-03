/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import org.apache.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;

public interface CachedModelConstructor {

	Model getConstructedModel() throws MalformedQueryParametersException;

	String getModelType();

}
