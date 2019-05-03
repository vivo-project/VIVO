/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.LogoutRedirector;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;

/**
 * New workflow:
 *
 * <pre>
 *    Default: clear status for both readProfile and addExternalIDs
 *      show intro screen orcidOffer.ftl
 *    	The click "do it", goes to /getProfileAuth
 *      Or "return to profile"
 *    /getProfileAuth: If already authorized, redirect to /readProfile
 *      Else, do the dance, ending with /readProfile callback
 *      Denied? show orcidDenied.ftl
 *      Failed? show orcidFailed.ftl
 *    /readProfile: read the profile, store in status
 *    	figure external ID options, show orcidOfferIds.ftl
 *      If they click "do it", goes /authExternalIds
 *      If they click "nah", return to profile
 *    /authExternalIds: if already authorized, redirect to /addExternalIds
 *      Else, do the dance, ending with /addExternalIds callback
 *    /addExternalIds add one or both IDs, store new profile in status
 *      show orcidSuccess.ftl with "return to profile" and "view profile" links.
 * </pre>
 */
@WebServlet(name = "OrcidIntegrationController", urlPatterns = {"/orcid/*"})
public class OrcidIntegrationController extends FreemarkerHttpServlet {
	private static final Log log = LogFactory
			.getLog(OrcidIntegrationController.class);

	private final static String PATHINFO_CALLBACK = "/callback";
	private final static String PATHINFO_AUTH_AUTHENTICATE = "/getAuthticateAuth";
	private final static String PATHINFO_READ_PROFILE = "/readProfile";
	private final static String PATHINFO_AUTH_EXTERNAL_ID = "/authExternalId";
	private final static String PATHINFO_ADD_EXTERNAL_ID = "/addExternalId";

	public final static String PATH_DEFAULT = "orcid";

	final static String PATH_AUTH_AUTHENTICATE = path(PATHINFO_AUTH_AUTHENTICATE);
	final static String PATH_READ_PROFILE = path(PATHINFO_READ_PROFILE);
	final static String PATH_AUTH_EXTERNAL_ID = path(PATHINFO_AUTH_EXTERNAL_ID);
	final static String PATH_ADD_EXTERNAL_ID = path(PATHINFO_ADD_EXTERNAL_ID);

	static String path(String pathInfo) {
		return PATH_DEFAULT + pathInfo;
	}

	final static String TEMPLATE_CONFIRM = "orcidConfirm.ftl";

	public static final String PROPERTY_EXTERNAL_ID_COMMON_NAME = "orcid.externalIdCommonName";
	public static final String DEFAULT_EXTERNAL_ID_COMMON_NAME = "VIVO Identifier";

	/**
	 * Get in before FreemarkerHttpServlet for special handling.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		if (!isOrcidConfigured()) {
			show404NotFound(resp);
		}
		if (PATHINFO_CALLBACK.equals(req.getPathInfo())) {
			new OrcidCallbackHandler(req, resp).exec();
		} else {
			super.doGet(req, resp);
		}
	}

	/**
	 * We return AUTHORIZED here, but we want the LogoutRedirector to know that
	 * the user should not remain on this page after logging out.
	 */
	@Override
	protected AuthorizationRequest requiredActions(VitroRequest vreq) {
		LogoutRedirector.recordRestrictedPageUri(vreq);
		return AuthorizationRequest.AUTHORIZED;
	}

	/**
	 * Look at the path info and delegate to a handler.
	 */
	@Override
	protected ResponseValues processRequest(VitroRequest vreq) throws Exception {
		try {
			String pathInfo = vreq.getPathInfo();
			log.debug("Path info: " + pathInfo);
			if (PATHINFO_AUTH_AUTHENTICATE.equals(pathInfo)) {
				return new OrcidAuthAuthenticateHandler(vreq).exec();
			} else if (PATHINFO_READ_PROFILE.equals(pathInfo)) {
				return new OrcidReadProfileHandler(vreq).exec();
			} else if (PATHINFO_AUTH_EXTERNAL_ID.equals(pathInfo)) {
				return new OrcidAuthExternalIdsHandler(vreq).exec();
			} else if (PATHINFO_ADD_EXTERNAL_ID.equals(pathInfo)) {
				return new OrcidAddExternalIdHandler(vreq).exec();
			} else {
				return new OrcidDefaultHandler(vreq).exec();
			}
		} catch (Exception e) {
			return new ExceptionResponseValues(e, SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * If the ORCID interface is configured, it should not throw an exception
	 * when asked for the value of a setting.
	 */
	private boolean isOrcidConfigured() {
		try {
			OrcidClientContext.getInstance().getSetting(Setting.CLIENT_ID);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void show404NotFound(HttpServletResponse resp) throws IOException {
		resp.sendError(SC_NOT_FOUND);
	}
}
