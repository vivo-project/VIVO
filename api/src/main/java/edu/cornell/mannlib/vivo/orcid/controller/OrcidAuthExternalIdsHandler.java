/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.ADD_EXTERNAL_ID;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.DENIED_ID;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.FAILED_ID;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.PATH_ADD_EXTERNAL_ID;

import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;

/**
 * We offered to add external IDs and they decided to go ahead. Get
 * authorization.
 *
 * We can't assume that they haven't been here before, so they might already
 * have authorized, or denied authorization.
 */
public class OrcidAuthExternalIdsHandler extends OrcidAbstractHandler {
	private static final Log log = LogFactory
			.getLog(OrcidAuthExternalIdsHandler.class);

	private AuthorizationStatus status;

	public OrcidAuthExternalIdsHandler(VitroRequest vreq) {
		super(vreq);
	}

	public ResponseValues exec() throws URISyntaxException,
			OrcidClientException {
		status = auth.getAuthorizationStatus(ADD_EXTERNAL_ID);
		if (status.isNone()) {
			return seekAuthorizationForExternalId();
		} else if (status.isSuccess()) {
			return redirectToAddExternalId();
		} else if (status.isDenied()) {
			return showConfirmationPage(DENIED_ID);
		} else {
			return showConfirmationPage(FAILED_ID);
		}
	}

	private ResponseValues seekAuthorizationForExternalId()
			throws OrcidClientException, URISyntaxException {
		log.debug("Seeking authorization to add external ID");
		String returnUrl = occ.resolvePathWithWebapp(PATH_ADD_EXTERNAL_ID);
		String seekUrl = auth.seekAuthorization(ADD_EXTERNAL_ID, returnUrl);
		return new RedirectResponseValues(seekUrl);
	}

	private ResponseValues redirectToAddExternalId() throws URISyntaxException {
		log.debug("Already authorized to add external ID.");
		return new RedirectResponseValues(
				occ.resolvePathWithWebapp(PATH_ADD_EXTERNAL_ID));
	}

}
