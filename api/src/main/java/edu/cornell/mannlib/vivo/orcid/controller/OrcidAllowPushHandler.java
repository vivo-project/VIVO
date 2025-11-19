package edu.cornell.mannlib.vivo.orcid.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;

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
            "?client_id=" + URLEncoder.encode(clientId) +
            "&response_type=code" +
            "&scope=" + URLEncoder.encode("/activities/update /person/update") +
            "&redirect_uri=" +
            URLEncoder.encode("http://127.0.0.1/bind/oauth2/code/orcid");

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
            updateOrcidIdForUser(individualUri, tokenResponse.getOrcid());
            updateOrcidCredentialsForUser(individualUri, tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken());

            httpServletResponse.sendRedirect(
                occ.getSetting(OrcidClientContext.Setting.WEBAPP_BASE_URL) + "individual?uri=" + individualUri);
        } catch (IOException e) {
            log.error("An error occurred. Cause: " + e.getMessage());
        }
    }

    private OntModel getDisplayModel(boolean isAboxAssertions) {
        ContextModelAccess cma = ModelAccess.getInstance();
        return cma.getOntModel(isAboxAssertions ? ModelNames.ABOX_ASSERTIONS : ModelNames.APPLICATION_METADATA);
    }

    private void updateOrcidIdForUser(String individualUri, String orcidId) {
        OntModel displayModel = getDisplayModel(true);
        Resource person = displayModel.createResource(individualUri);
        Resource orcid = displayModel.createResource(orcidId);

        displayModel.add(person,
            displayModel.createProperty("http://vivoweb.org/ontology/core#orcidId"),
            orcid);

        displayModel.add(person,
            displayModel.createProperty("http://vivoweb.org/ontology/core#confirmedOrcidId"),
            person);
    }

    private void updateOrcidCredentialsForUser(String individualUri, String accessToken, String refreshToken) {
        OntModel displayModel = getDisplayModel(false);
        Resource personResource = ResourceFactory.createResource(individualUri);

        Statement statement =
            new StatementImpl(
                personResource,
                ResourceFactory.createProperty("http://vivoweb.org/ontology/core#orcidAccessToken"),
                ResourceFactory.createTypedLiteral(accessToken)
            );
        displayModel.add(statement);

        statement =
            new StatementImpl(
                personResource,
                ResourceFactory.createProperty("http://vivoweb.org/ontology/core#refreshToken"),
                ResourceFactory.createTypedLiteral(refreshToken)
            );
        displayModel.add(statement);
    }
}
