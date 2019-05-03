/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.exceptions;

public class DocumentFieldNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public DocumentFieldNotFoundException(String message) {
		super(message);
	}

	public DocumentFieldNotFoundException(Exception cause) {
		super(createMessage(cause), cause);
	}

	private static String createMessage(Exception cause) {
		return "Document Field is empty " + cause.getMessage();
	}

}
