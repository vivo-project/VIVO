/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary.OWL_THING;
import static edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary.RDF_TYPE;
import static edu.cornell.mannlib.vivo.orcid.OrcidIdDataGetter.ORCID_ID;
import static edu.cornell.mannlib.vivo.orcid.OrcidIdDataGetter.ORCID_IS_CONFIRMED;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.TEMPLATE_CONFIRM;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.util.HashMap;
import java.util.Map;

import edu.cornell.mannlib.orcidclient.actions.ActionManager;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress;

/**
 * Some utility methods for the handlers.
 */
public abstract class OrcidAbstractHandler {
	private static final Log log = LogFactory
			.getLog(OrcidAbstractHandler.class);

	protected final VitroRequest vreq;
	protected final OrcidClientContext occ;
	protected final AuthorizationManager auth;
	protected final ActionManager manager;
	protected final OrcidConfirmationState state;
	protected final UserAccount currentUser;

	private static String apiLevel = "member";

	protected OrcidAbstractHandler(VitroRequest vreq) {
		this.vreq = vreq;
		this.occ = OrcidClientContext.getInstance();
		this.auth = this.occ.getAuthorizationManager(vreq);
		this.manager = this.occ.getActionManager(vreq);
		this.state = OrcidConfirmationState.fetch(vreq);
		this.currentUser = LoginStatusBean.getCurrentUser(vreq);
	}

	protected Individual findIndividual() {
		String uri = state.getIndividualUri();
		try {
			IndividualDao iDao = vreq.getWebappDaoFactory().getIndividualDao();
			Individual individual = iDao.getIndividualByURI(uri);
			if (individual == null) {
				throw new IllegalStateException("Individual URI not valid: '"
						+ uri + "'");
			}
			return individual;
		} catch (Exception e) {
			throw new IllegalStateException("Individual URI not valid: '" + uri
					+ "'");
		}
	}

	protected void recordConfirmation() {
		String individualUri = state.getIndividualUri();
		String orcidUri = state.getOrcidUri();
		log.debug("Recording confirmation of ORCID '" + orcidUri + "' on '"
				+ individualUri + "'");
		ObjectPropertyStatement ops1 = new ObjectPropertyStatementImpl(
				individualUri, ORCID_ID, orcidUri);
		ObjectPropertyStatement ops2 = new ObjectPropertyStatementImpl(
				orcidUri, RDF_TYPE, OWL_THING);
		ObjectPropertyStatement ops3 = new ObjectPropertyStatementImpl(
				orcidUri, ORCID_IS_CONFIRMED, individualUri);

		ObjectPropertyStatementDao opsd = vreq.getWebappDaoFactory()
				.getObjectPropertyStatementDao();
		opsd.insertNewObjectPropertyStatement(ops1);
		opsd.insertNewObjectPropertyStatement(ops2);
		opsd.insertNewObjectPropertyStatement(ops3);
	}

	protected String cornellNetId() {
		if (currentUser == null) {
			return null;
		}
		String externalId = currentUser.getExternalAuthId();
		if (externalId == null) {
			return null;
		}
		if (externalId.trim().isEmpty()) {
			return null;
		}
		return externalId;
	}

	protected ResponseValues show500InternalServerError(String message) {
		log.error("Problem with ORCID request: " + message);
		Map<String, Object> map = new HashMap<>();
		map.put("title", "500 Internal Server Error");
		map.put("errorMessage", message);
		return new TemplateResponseValues("error-titled.ftl", map,
				SC_INTERNAL_SERVER_ERROR);
	}

	protected ResponseValues showConfirmationPage(Progress p,
			OrcidProfile... profiles) {
		state.progress(p, profiles);
		return showConfirmationPage();
	}

	protected ResponseValues showConfirmationPage() {
		Map<String, Object> map = new HashMap<>();
		map.put("orcidInfo", state.toMap());
		map.put("orcidApiLevel", apiLevel);
		return new TemplateResponseValues(TEMPLATE_CONFIRM, map);
	}

	public static void setAPiLevelPublic() {
		apiLevel = "public";
	}

}
