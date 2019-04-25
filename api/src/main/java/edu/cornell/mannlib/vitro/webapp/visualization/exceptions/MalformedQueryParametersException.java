/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.exceptions;

public class MalformedQueryParametersException extends Exception {

	private static final long serialVersionUID = 1L;

	public MalformedQueryParametersException(String message) {
		super(message);
	}

	public MalformedQueryParametersException(Exception cause) {
		super(createMessage(cause), cause);
	}

	private static String createMessage(Exception cause) {
		return "Malformed Query Params " + cause.getMessage();
	}

}
