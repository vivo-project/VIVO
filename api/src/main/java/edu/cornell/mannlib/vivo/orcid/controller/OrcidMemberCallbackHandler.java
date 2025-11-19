package edu.cornell.mannlib.vivo.orcid.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vivo.orcid.util.OrcidIdOperationsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrcidMemberCallbackHandler extends OrcidAbstractHandler {

    private static final Log log = LogFactory
        .getLog(OrcidMemberCallbackHandler.class);

    private final HttpServletResponse httpServletResponse;


    public OrcidMemberCallbackHandler(VitroRequest vreq, HttpServletResponse resp) {
        super(vreq);
        this.httpServletResponse = resp;
    }

    public void exec() throws OrcidClientException {
        String authorizationCode = vreq.getParameter("code");
        String error = vreq.getParameter("error");

        if (error != null) {
            throw new OrcidClientException("An error occurred:" + error);
        }

        if (authorizationCode == null || authorizationCode.trim().isEmpty()) {
            throw new OrcidClientException("Missing authorization code.");
        }

        try {
            OrcidTokenExchange tokenExchanger = new OrcidTokenExchange(
                occ.getSetting(OrcidClientContext.Setting.CLIENT_ID),
                occ.getSetting(OrcidClientContext.Setting.CLIENT_SECRET),
                occ.getSetting(OrcidClientContext.Setting.API_ENVIRONMENT).equals("sandbox")
            );

            OrcidTokenExchange.OrcidTokenResponse tokenResponse =
                tokenExchanger.exchangeCodeForToken(authorizationCode);

            String individualUri = vreq.getSession().getAttribute("profileUri").toString();
            OrcidIdOperationsUtil.updateOrcidIdForUser(individualUri, tokenResponse.getOrcid());
            OrcidIdOperationsUtil.updateOrcidCredentialsForUser(individualUri, tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(), tokenResponse.getExpiresIn());
            OrcidIdOperationsUtil.setAllowPushStatusForIndividual(individualUri, true);

            httpServletResponse.sendRedirect(
                occ.getSetting(OrcidClientContext.Setting.WEBAPP_BASE_URL) + "individual?uri=" + individualUri);
        } catch (IOException e) {
            log.error("An error occurred. Cause: " + e.getMessage());
        }
    }
}
