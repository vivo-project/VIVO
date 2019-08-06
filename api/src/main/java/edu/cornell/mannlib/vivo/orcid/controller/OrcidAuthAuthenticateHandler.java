/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.AUTHENTICATE;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.DENIED_AUTHENTICATE;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.FAILED_AUTHENTICATE;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.PATH_READ_PROFILE;

import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;

/**
 * We offered the confirmation screen, and they decided to go ahead. Get
 * authorization to authenticate them.
 *
 * We can't assume that they haven't been here before, so they might already
 * have authorized, or denied authorization.
 */
public class OrcidAuthAuthenticateHandler extends OrcidAbstractHandler {
	private static final Log log = LogFactory
			.getLog(OrcidAuthAuthenticateHandler.class);

	private AuthorizationStatus status;

	public OrcidAuthAuthenticateHandler(VitroRequest vreq) {
		super(vreq);
	}

	public ResponseValues exec() throws URISyntaxException,
			OrcidClientException {
		status = auth.getAuthorizationStatus(AUTHENTICATE);
		if (status.isNone()) {
			return seekAuthorizationForAuthenticate();
		} else if (status.isSuccess()) {
			return redirectToReadProfile();
		} else if (status.isDenied()) {
			return showConfirmationPage(DENIED_AUTHENTICATE);
		} else {
			return showConfirmationPage(FAILED_AUTHENTICATE);
		}
	}

	private ResponseValues seekAuthorizationForAuthenticate()
			throws OrcidClientException, URISyntaxException {
		log.debug("Seeking authorization to authenticate.");
		String returnUrl = occ.resolvePathWithWebapp(PATH_READ_PROFILE);
		String seekUrl = auth.seekAuthorization(AUTHENTICATE, returnUrl);
		return new RedirectResponseValues(seekUrl);
	}

	private ResponseValues redirectToReadProfile() throws URISyntaxException {
		log.debug("Already authorized to authenticate.");
		return new RedirectResponseValues(
				occ.resolvePathWithWebapp(PATH_READ_PROFILE));
	}

}
