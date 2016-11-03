/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import edu.cornell.mannlib.orcidclient.OrcidClientException;

/**
 * The OrcidConfirmationState is not as we expected. Probably deserves a 500
 * error.
 */
public class OrcidIllegalStateException extends OrcidClientException {
	public OrcidIllegalStateException(String message) {
		super(message);
	}

	public OrcidIllegalStateException(String message, Throwable cause) {
		super(message, cause);
	}

}
