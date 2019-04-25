/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * Handle the callbacks during the OAuth dance.
 *
 * This is not like other handlers. It is created and invoked from doGet(), not
 * from processRequest().
 */
public class OrcidCallbackHandler {
	private static final Log log = LogFactory
			.getLog(OrcidCallbackHandler.class);

	private final HttpServletRequest req;
	private final HttpServletResponse resp;

	public OrcidCallbackHandler(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public void exec() throws IOException {
		OrcidClientContext occ = OrcidClientContext.getInstance();
		AuthorizationManager authManager = occ.getAuthorizationManager(req);
		try {
			AuthorizationStatus auth = authManager
					.processAuthorizationResponse();
			if (auth.isSuccess()) {
				resp.sendRedirect(auth.getSuccessUrl());
			} else {
				resp.sendRedirect(auth.getFailureUrl());
			}
		} catch (OrcidClientException e) {
			log.error("Invalid authorization response", e);
			resp.sendError(SC_INTERNAL_SERVER_ERROR);
		}
	}

}
