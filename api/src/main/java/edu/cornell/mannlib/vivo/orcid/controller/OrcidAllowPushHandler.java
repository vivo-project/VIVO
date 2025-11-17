package edu.cornell.mannlib.vivo.orcid.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrcidAllowPushHandler extends OrcidAbstractHandler {

    private static final Log log = LogFactory
        .getLog(OrcidAllowPushHandler.class);

    private final HttpServletResponse httpServletResponse;

    public OrcidAllowPushHandler(VitroRequest vreq, HttpServletResponse resp) {
        super(vreq);
        this.httpServletResponse = resp;
    }

    public ResponseValues exec() throws URISyntaxException,
        OrcidClientException, UnsupportedEncodingException {
        vreq.getSession().setAttribute("profileUri", vreq.getParameter("profileUri"));

        ConfigurationProperties config = ConfigurationProperties.getInstance();
        if (Objects.isNull(config)) {
            throw new OrcidClientException("Error fetching configuration.");
        }

        String clientId = occ.getSetting(OrcidClientContext.Setting.CLIENT_ID);

        String authUrl = "https://sandbox.orcid.org/oauth/authorize" +
            "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
            "&response_type=code" +
            "&scope=" + URLEncoder.encode("/activities/update /person/update", StandardCharsets.UTF_8) +
            "&redirect_uri=" +
            URLEncoder.encode("http://127.0.0.1/bind/oauth2/code/orcid", StandardCharsets.UTF_8);

        return new RedirectResponseValues(authUrl);
    }

    public void handleCallback() throws OrcidClientException {
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
            System.out.println(individualUri);
            System.out.println(tokenResponse.getOrcid());
            System.out.println(tokenResponse.getAccessToken());
            System.out.println(tokenResponse.getRefreshToken());

            httpServletResponse.sendRedirect(
                occ.getSetting(OrcidClientContext.Setting.WEBAPP_BASE_URL) + "individual?uri=" + individualUri);
        } catch (IOException e) {
            log.error("An error occurred. Cause: " + e.getMessage());
        }
    }
}
