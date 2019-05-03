/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.ADD_EXTERNAL_ID;
import static edu.cornell.mannlib.orcidclient.actions.ApiAction.READ_PROFILE;
import static edu.cornell.mannlib.vivo.orcid.OrcidIdDataGetter.ORCID_ID;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasProfile;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.NotAuthorizedResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;

/**
 * A request came from the "Confirm" button on the individual profile. Get a
 * fresh state object, clear the AuthorizationCache and show the confirmation
 * page.
 */
public class OrcidDefaultHandler extends OrcidAbstractHandler {
	private static final Log log = LogFactory.getLog(OrcidDefaultHandler.class);

	private Individual individual;
	private final Set<String> existingOrcids = new HashSet<>();

	public OrcidDefaultHandler(VitroRequest vreq) {
		super(vreq);
	}

	public ResponseValues exec() {
		try {
			initializeState();
			initializeAuthorizationCache();
		} catch (Exception e) {
			log.error("No proper individual URI on the request", e);
			return show400BadRequest(e);
		}

		if (!isAuthorized()) {
			return showNotAuthorized();
		}

		return showConfirmationPage();
	}

	private void initializeState() {
		String uri = vreq.getParameter("individualUri");
		if (uri == null) {
			throw new IllegalStateException(
					"No 'individualUri' parameter on request.");
		}

		String profilePage = UrlBuilder.getIndividualProfileUrl(uri, vreq);
		state.reset(uri, profilePage);

		individual = findIndividual();
		locateExistingOrcids();
		state.setExistingOrcids(existingOrcids);
	}

	private void locateExistingOrcids() {
		if (individual == null) {
			return;
		}

		List<ObjectPropertyStatement> opss = individual
				.getObjectPropertyStatements(ORCID_ID);
		if (opss == null) {
			return;
		}

		for (ObjectPropertyStatement ops : opss) {
			existingOrcids.add(ops.getObjectURI());
		}

	}

	private void initializeAuthorizationCache() {
		auth.clearStatus(READ_PROFILE);
		auth.clearStatus(ADD_EXTERNAL_ID);
	}

	private ResponseValues show400BadRequest(Exception e) {
		Map<String, Object> map = new HashMap<>();
		map.put("title", "400 Bad Request");
		map.put("errorMessage", e.getMessage());
		return new TemplateResponseValues("error-titled.ftl", map,
				SC_BAD_REQUEST);
	}

	private boolean isAuthorized() {
		// Only a self-editor is authorized.
		IdentifierBundle ids = RequestIdentifiers.getIdBundleForRequest(vreq);
		Collection<String> profileUris = HasProfile.getProfileUris(ids);
		log.debug("Authorized? individualUri=" + state.getIndividualUri()
				+ ", profileUris=" + profileUris);
		return profileUris.contains(state.getIndividualUri());
	}

	private ResponseValues showNotAuthorized() {
		UserAccount user = LoginStatusBean.getCurrentUser(vreq);
		String userName = (user == null) ? "ANONYMOUS" : user.getEmailAddress();
		return new NotAuthorizedResponseValues(userName
				+ "is not authorized for ORCID operations on '" + individual
				+ "'");
	}

}
