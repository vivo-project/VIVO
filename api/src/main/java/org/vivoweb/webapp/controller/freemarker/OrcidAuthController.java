/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.controller.freemarker;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.DirectRedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.utils.http.HttpClientFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 */
@WebServlet("/orcidAuth/*")
public class OrcidAuthController extends FreemarkerHttpServlet {

    private static final String JSON_TOKEN_PARAM = "json_token";
    private static final String SANDBOX = "sandbox";
    private static final String CONFIGURATION_ORCID_API = "orcid.api";
    private static final String CONFIGURATION_AUTH_ORCID_CALLBACK_URL = "orcid.webappBaseUrl";
    private static final String CONFIGURATION_AUTH_ORCID_CLIENT_PASSWORD = "orcid.clientPassword";
    private static final String CONFIGURATION_AUTH_ORCID_CLIENT_ID = "orcid.clientId";
    private static final String CODE_PARAM = "code";
    private static final String CALLBACK = "callback";
    private static final String NOT_AUTHENTICATED_FTL = "notAuthenticated.ftl";
    private static final String UNKNOWN_PROFILE_FTL = "unknownProfile.ftl";
    private static final String ORCID_NOT_CONFIGURED_FTL = "orcidNotConfigured.ftl";
    private static final String ORCID_ID_PROPERTY_URI = "http://vivoweb.org/ontology/core#orcidId";

    private static final String MESSAGE = "message";

    private Log log = LogFactory.getLog(this.getClass());
    private String clientId;
    private String clientSecret;
    private String callbackUrl;
    private String apiPrefix;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        super.init();
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        ConfigurationProperties configProperties = ConfigurationProperties.getInstance();
        clientId = configProperties.getProperty(CONFIGURATION_AUTH_ORCID_CLIENT_ID);
        clientSecret = configProperties.getProperty(CONFIGURATION_AUTH_ORCID_CLIENT_PASSWORD);
        callbackUrl = configProperties.getProperty(CONFIGURATION_AUTH_ORCID_CALLBACK_URL) + "orcidAuth/callback";
        String apiType = configProperties.getProperty(CONFIGURATION_ORCID_API);

        if (clientSecret != null && callbackUrl != null && clientId != null && apiType != null) {
            if (apiType.equals(SANDBOX)) {
                apiPrefix = "sandbox.";
            } else {
                apiPrefix = "";
            }
        } else {
            String errstr = "ORCID variables missing in runtime.properties file!" +
                    "\nPlease check if auth.orcid.clientId, auth.orcid.clientPassword " +
                    "and auth.orcid.callbackUrl are set";
            log.error(errstr);
            throw new ServletException(errstr);
        }

    }

    /**
     * Main method for the resource claiming (create and link) workflow
     *
     * @param vreq
     * @return
     */
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        String requestURI = vreq.getRequestURI();
        if (needOrcidCallBack(requestURI)) {
            String location = getRedirectLocation();
            return new DirectRedirectResponseValues(location);
        }
        try {
            verifyOrcidContextIsAvailable();
            verifyUserIsNotLoggedIn(vreq);
            OrcidTokenResponse orcidToken = getOrcidToken(vreq);
            if (userAccountExists(vreq, orcidToken)) {
                return login(vreq, orcidToken);
            } else {
                return new TemplateResponseValues(UNKNOWN_PROFILE_FTL);
            }

        } catch (OrcidNotConfiguredException e) {
            return new TemplateResponseValues(ORCID_NOT_CONFIGURED_FTL);
        } catch (UnexpectedLoggedInUserException e) {
            if (e.getUrl() == null) {
                return new TemplateResponseValues(UNKNOWN_PROFILE_FTL);
            } else {
                return new DirectRedirectResponseValues(e.getUrl());
            }
        } catch (AuthenticationException e) {
            String message = e.getMessage();
            Map<String, Object> templateValues = new HashMap<String, Object>();
            templateValues.put(MESSAGE, message);
            return new TemplateResponseValues(NOT_AUTHENTICATED_FTL, templateValues);
        }
    }

    private boolean userAccountExists(VitroRequest vreq, OrcidTokenResponse orcidToken) {
        return getLinkedUserAccount(vreq, orcidToken) != null;
    }

    private UserAccount getLinkedUserAccount(VitroRequest vreq, OrcidTokenResponse orcidToken) {
        if (orcidToken == null || StringUtils.isEmpty(orcidToken.orcid)) {
            return null;
        }

        String personUri = getPersonUriByOrcidUri(vreq, "https://orcid.org/" + orcidToken.orcid);
        Individual individual = null;

        if (personUri != null) {
            individual = vreq.getWebappDaoFactory()
                    .getIndividualDao()
                    .getIndividualByURI(personUri);
        }

        if (individual != null) {
            SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(vreq);
            String matchingPropertyUri = sec.getMatchingPropertyUri();
            if (!StringUtils.isBlank(matchingPropertyUri)) {
                String externalAuthId = getExternalAuthIdForIndividual(vreq, individual);
                log.debug("External auth ID: " + externalAuthId);
                if (StringUtils.isNotBlank(externalAuthId)) {
                    return getAuthenticator(vreq).getAccountForExternalAuth(externalAuthId);
                }
            }
        }
        return getAuthenticator(vreq).getAccountForExternalAuth(orcidToken.orcid);
    }

    private String getPersonUriByOrcidUri(VitroRequest vreq, String orcidUri) {
        final List<String> personUris = new ArrayList<String>();

        String query = ""
                + "SELECT ?person WHERE {\n"
                + "  ?person <" + ORCID_ID_PROPERTY_URI + "> <" + orcidUri + "> .\n"
                + "}\n"
                + "LIMIT 1";

        try {
            vreq.getRDFService().sparqlSelectQuery(query, new ResultSetConsumer() {
                @Override
                protected void processQuerySolution(QuerySolution qs) {
                    RDFNode personNode = qs.get("person");
                    if (personNode != null && personNode.isURIResource()) {
                        personUris.add(personNode.asResource().getURI());
                    }
                }
            });
        } catch (RDFServiceException e) {
            log.error("Error finding person by ORCID URI: " + orcidUri, e);
        }

        if (personUris.isEmpty()) {
            return null;
        }

        return personUris.get(0);
    }
    private String getExternalAuthIdForIndividual(VitroRequest vreq, Individual individual) {
        if (individual == null || StringUtils.isBlank(individual.getURI())) {
            return null;
        }

        SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(vreq);
        String matchingPropertyUri = sec.getMatchingPropertyUri();

        log.debug("Matching property URI: " + matchingPropertyUri);

        if (StringUtils.isBlank(matchingPropertyUri)) {
            return null;
        }

        DataPropertyStatementDao dpsDao = vreq.getUnfilteredWebappDaoFactory()
                .getDataPropertyStatementDao();

        Collection<DataPropertyStatement> statements =
                dpsDao.getDataPropertyStatementsForIndividualByDataPropertyURI(
                        individual,
                        matchingPropertyUri);

        if (statements == null || statements.isEmpty()) {
            log.debug("No external auth ID found on individual " + individual.getURI()
                    + " using property " + matchingPropertyUri);
            return null;
        }

        return statements.iterator().next().getData();
    }

    private boolean needOrcidCallBack(String requestURI) {
        return !requestURI.contains(CALLBACK);
    }

    private void verifyOrcidContextIsAvailable() throws OrcidNotConfiguredException {
        OrcidClientContext orcidClientContext = OrcidClientContext.getInstance();
        if (orcidClientContext == null) {
            throw new OrcidNotConfiguredException();
        }
    }

    private ResponseValues login(VitroRequest vreq, OrcidTokenResponse orcidToken) {
        try {
            UserAccount userAccount = getLinkedUserAccount(vreq, orcidToken);
            String profileUri = getProfileUri(vreq, userAccount);

            getAuthenticator(vreq).recordLoginAgainstUserAccount(userAccount,
                    LoginStatusBean.AuthenticationSource.EXTERNAL);

            if (profileUri == null) {
                return goToHomePage();
            } else {
                return goToUserProfile(vreq, profileUri);
            }

        } catch (Authenticator.LoginNotPermitted e) {
            return new TemplateResponseValues(NOT_AUTHENTICATED_FTL);
        }
    }

    private void verifyUserIsNotLoggedIn(VitroRequest vreq) throws UnexpectedLoggedInUserException {
        UserAccount loggedInAccount = LoginStatusBean.getCurrentUser(vreq);
        if (loggedInAccount != null) {
            String profileUri = getProfileUri(vreq, loggedInAccount);
            // We have a user with a profile, so redirect
            if (profileUri == null) {
                throw new UnexpectedLoggedInUserException("User already logged in, no profile found");
            }
            String individualProfileUrl = UrlBuilder.getIndividualProfileUrl(profileUri, vreq);
            throw new UnexpectedLoggedInUserException("User already logged in", individualProfileUrl);
        }
    }

    private ResponseValues goToUserProfile(VitroRequest vreq, String profileUri) {
        return new DirectRedirectResponseValues(UrlBuilder.getIndividualProfileUrl(profileUri, vreq));
    }

    private ResponseValues goToHomePage() {
        return new DirectRedirectResponseValues(UrlBuilder.getHomeUrl());
    }

    private OrcidTokenResponse getOrcidToken(VitroRequest vreq) throws AuthenticationException {
        return getTokenFromTokenParam(vreq);
    }

    private OrcidTokenResponse getTokenFromTokenParam(VitroRequest vreq) throws AuthenticationException {
        OrcidTokenResponse orcidToken = null;
        String json = vreq.getParameter(JSON_TOKEN_PARAM);
        if (json == null) {
            return getTokenFromCodeParam(vreq);
        }
        if (!StringUtils.isEmpty(json)) {
            try {
                orcidToken = mapper.readValue(json, OrcidTokenResponse.class);
            } catch (Exception e) {
                log.error("Error parsing JSON token", e);
            }
        }

        if (orcidToken == null || StringUtils.isEmpty(orcidToken.orcid)) {
            log.debug("OAuth authentication exception. Raw response: " + json);
            throw new AuthenticationException("We did not receive an OAuth token.");
        }
        return orcidToken;
    }

    private OrcidTokenResponse getTokenFromCodeParam(VitroRequest vreq) throws AuthenticationException {
        String code = vreq.getParameter(CODE_PARAM);
        if (code == null) {
            throw new AuthenticationException("No code recieved");
        }
        OrcidTokenResponse orcidToken = null;

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("client_id", clientId));
        nvps.add(new BasicNameValuePair("client_secret", clientSecret));
        nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nvps.add(new BasicNameValuePair("redirect_uri", callbackUrl));
        nvps.add(new BasicNameValuePair(CODE_PARAM, code));

        String json = readJSON(getTokenUrl(), nvps);

        if (!StringUtils.isEmpty(json)) {
            try {
                orcidToken = mapper.readValue(json, OrcidTokenResponse.class);
            } catch (Exception e) {
                log.error("Error parsing JSON token", e);
            }
        }

        if (orcidToken == null || StringUtils.isEmpty(orcidToken.orcid)) {
            log.debug("OAuth authentication exception. Raw response: " + json);
            throw new AuthenticationException("We did not receive an OAuth token.");
        }
        return orcidToken;
    }

    private String getTokenUrl() {
        return "https://" + apiPrefix + "orcid.org/oauth/token";
    }

    private String getRedirectLocation() {
        return "https://" + apiPrefix + "orcid.org/oauth/authorize" + "?client_id=" + clientId
                + "&response_type=code" + "&scope=/authenticate" + "&redirect_uri=" + callbackUrl;
    }

    private Authenticator getAuthenticator(HttpServletRequest req) {
        return Authenticator.getInstance(req);
    }

    /**
     * Read JSON from the URL
     *
     * @param url
     * @return
     */
    private String readJSON(String url, List<NameValuePair> nvps) {
        try {
            HttpClient client = HttpClientFactory.getHttpClient();
            HttpPost request = new HttpPost(url);

            request.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);

            request.setEntity(new UrlEncodedFormEntity(nvps));

            // Content negotiate for csl / citeproc JSON
            request.setHeader("Accept", "application/json");

            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200:
                    try (InputStream in = response.getEntity().getContent()) {
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(in, writer, "UTF-8");
                        return writer.toString();
                    }
                default:
                    try (InputStream in = response.getEntity().getContent()) {
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(in, writer, "UTF-8");
                        log.error("ORCID API returned status " + statusCode + ": " + writer.toString());
                    }
                    break;
            }
        } catch (IOException e) {
            log.error(e, e);
        }
        return null;
    }

    private String getProfileUri(VitroRequest vreq, UserAccount userAccount) {
        SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(vreq);

        // Find the profile(s) associated with this user
        List<Individual> assocInds = sec.getAssociatedIndividuals(vreq.getWebappDaoFactory().getIndividualDao(),
                userAccount.getExternalAuthId());
        if (!assocInds.isEmpty()) {
            // If we have associated profiles, ensure that a valid person profile really
            // does exist
            return assocInds.get(0).getURI();
        }
        return null;
    }

    static class OrcidNotConfiguredException extends Exception {
    }

    static class UnexpectedLoggedInUserException extends Exception {
        private String url = null;

        UnexpectedLoggedInUserException(String message) {
            super(message);
        }

        UnexpectedLoggedInUserException(String message, String url) {
            super(message);
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    private static class OrcidTokenResponse {
        String orcid;

    }

}
