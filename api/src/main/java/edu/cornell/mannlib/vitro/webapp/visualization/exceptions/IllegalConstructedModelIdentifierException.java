/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.exceptions;

public class IllegalConstructedModelIdentifierException extends Exception {

	private static final long serialVersionUID = 1L;

	public IllegalConstructedModelIdentifierException(String message) {
		super(message);
	}

	public IllegalConstructedModelIdentifierException(Exception cause) {
		super(createMessage(cause), cause);
	}

	private static String createMessage(Exception cause) {
		return "Illegal Constructed Model Identifier provided. It should be of the form <TYPE>$<URI>. " + cause.getMessage();
	}

}
