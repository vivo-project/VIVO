package edu.cornell.mannlib.vivo.orcid.controller;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Objects;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;
import edu.cornell.mannlib.vivo.orcid.util.OrcidInternalOperationsUtil;

public class OrcidAllowPushHandler extends OrcidAbstractHandler {

    public OrcidAllowPushHandler(VitroRequest vreq) {
        super(vreq);
    }

    public ResponseValues exec() throws URISyntaxException,
        OrcidClientException, UnsupportedEncodingException {
        String individualUri = vreq.getParameter("profileUri");

        if (OrcidInternalOperationsUtil.hasConfiguredPushCredentials(individualUri)) {
            OrcidInternalOperationsUtil.setAllowPushStatusForIndividual(individualUri, true);

            return new RedirectResponseValues(
                occ.getSetting(OrcidClientContext.Setting.WEBAPP_BASE_URL) +
                    "individual?uri=" + individualUri
            );
        }

        vreq.getSession().setAttribute("profileUri", individualUri);

        ConfigurationProperties config = ConfigurationProperties.getInstance();
        if (Objects.isNull(config)) {
            throw new OrcidClientException("Error fetching configuration.");
        }

        String clientId = occ.getSetting(OrcidClientContext.Setting.CLIENT_ID);
        String redirectUri =
            occ.getSetting(OrcidClientContext.Setting.WEBAPP_BASE_URL) +
                occ.getSetting(OrcidClientContext.Setting.CALLBACK_PATH);

        String authUrl = "https://sandbox.orcid.org/oauth/authorize" +
            "?client_id=" + URLEncoder.encode(clientId) +
            "&response_type=code" +
            "&scope=" + URLEncoder.encode("/activities/update /person/update") +
            "&redirect_uri=" +
            URLEncoder.encode(redirectUri);

        return new RedirectResponseValues(authUrl);
    }
}
