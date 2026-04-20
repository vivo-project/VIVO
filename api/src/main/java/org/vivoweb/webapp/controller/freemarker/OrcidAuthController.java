/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.controller.freemarker;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.PermissionSets;
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
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.NewURIMakerVitro;
import edu.cornell.mannlib.vitro.webapp.dao.UserAccountsDao;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeSet;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.utils.http.HttpClientFactory;
//import static edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary.;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.annotation.WebServlet;
import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@WebServlet("/orcidAuth/*")
public class OrcidAuthController extends FreemarkerHttpServlet {

	private static final String JSON_TOKEN_PARAM = "json_token";
	private static final String AGREEMENTS = "agreements";
	private static final String SANDBOX = "sandbox";
	private static final String API_VERSION = "v3.0";
	private static final String CONFIGURATION_ORCID_API = "orcid.api";
	private static final String CONFIGURATION_AUTH_ORCID_CALLBACK_URL = "auth.orcid.callbackUrl";
	private static final String CONFIGURATION_AUTH_ORCID_CLIENT_PASSWORD = "auth.orcid.clientPassword";
	private static final String CONFIGURATION_AUTH_ORCID_CLIENT_ID = "auth.orcid.clientId";
	private static final String BIBO_DOI = "http://purl.org/ontology/bibo/doi";
	private static final String CODE_PARAM = "code";
	private static final String CALLBACK = "callback";
	private static final String CLAIM_DOIS_FTL = "claimDOIs.ftl";
	private static final String PROFILE_ERROR_FTL = "profileError.ftl";
	private static final String NO_PROFILE_FTL = "noProfile.ftl";
	private static final String NOT_AUTHENTICATED_FTL = "notAuthenticated.ftl";
	private static final String UNKNOWN_PROFILE_FTL = "unknownProfile.ftl";
	private static final String ORCID_NOT_CONFIGURED_FTL = "orcidNotConfigured.ftl";
	private static final String AGREEMENT_FTL = "usageAgreement.ftl";
	private static final String OWL_THING = "http://www.w3.org/2002/07/owl#Thing";
	private static final String VIVO = "http://vivoweb.org/ontology/core#";
	private static final String VIVO_OVERVIEW = VIVO + "overview";
	private static final String VCARD = "http://www.w3.org/2006/vcard/ns#";
	private static final String OBO = "http://purl.obolibrary.org/obo/";
	private static final String FOAF = "http://xmlns.com/foaf/0.1/";
	private static final String FOAF_LAST_NAME = FOAF + "lastName";
	private static final String FOAF_FIRST_NAME = FOAF + "firstName";
    private static final String VIVO_REJECTED_DOI = "http://vivoweb.org/ontology/core#rejectedDoi";

	
	private static final String VCARD_HAS_URL = VCARD + "hasURL";
	private static final String MESSAGE = "message";

	private Log log = LogFactory.getLog(this.getClass());
	private String clientId;
	private String clientSecret;
	private String callbackUrl;
	private String apiPrefix;
	private String apiVersion;
	private Gson gson;

	@Override
	public void init() throws ServletException {
		super.init();
		gson = new Gson();
		ConfigurationProperties configProperties = ConfigurationProperties.getBean(getServletContext());
		clientId = configProperties.getProperty(CONFIGURATION_AUTH_ORCID_CLIENT_ID);
		clientSecret = configProperties.getProperty(CONFIGURATION_AUTH_ORCID_CLIENT_PASSWORD);
		callbackUrl = configProperties.getProperty(CONFIGURATION_AUTH_ORCID_CALLBACK_URL);
		String apiType = configProperties.getProperty(CONFIGURATION_ORCID_API);
		apiVersion = API_VERSION;

		if (clientSecret != null && callbackUrl != null && clientId != null && apiType != null) {
			if (apiType.equals(SANDBOX)) {
				apiPrefix = "sandbox.";
			} else {
				apiPrefix = "";
			}
		} else {
			String errstr = "ORCiD variables missing in properties file!";
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
			if (userAccountExists(vreq, orcidToken) ) {
				return login(vreq, orcidToken);
//			} else {
//				if (isAgreementAccepted(vreq)) {
//					return register(vreq, orcidToken);
//				} else {
//					return proposeAgrement(vreq, orcidToken);
//				}
			} else {
				return null;
			}
			
		} catch (OrcidNotConfiguredException e) {
			return new TemplateResponseValues(ORCID_NOT_CONFIGURED_FTL);
		} catch (UnexpectedLoggedInUserException e) {
			if (e.getUrl() == null) {
				return new TemplateResponseValues(UNKNOWN_PROFILE_FTL);
			} else {
				return new DirectRedirectResponseValues(e.getUrl());
			}
		} catch( AuthenticationException e) {
			String message = e.getMessage();
			Map<String,Object> templateValues = new HashMap();
			templateValues.put(MESSAGE, message);
			return new TemplateResponseValues(NOT_AUTHENTICATED_FTL, templateValues);
		} 
	}
	
	private boolean isAgreementAccepted(VitroRequest vreq) {
		String agreements = vreq.getParameter(AGREEMENTS);
		if ( "on".equals(agreements) ){
			return true;
		}
		return false;
	}

	private TemplateResponseValues proposeAgrement(VitroRequest vreq, OrcidTokenResponse orcidToken) {
		Map<String,Object> templateValues = new HashMap();
		if (orcidToken != null) {
			templateValues.put(JSON_TOKEN_PARAM, gson.toJson(orcidToken));
		}
		TemplateResponseValues agreement = new TemplateResponseValues(AGREEMENT_FTL, templateValues);
		return agreement;
	}

	private boolean userAccountExists(VitroRequest vreq, OrcidTokenResponse orcidToken) {
		return getAuthenticator(vreq).getAccountForExternalAuth(orcidToken.orcid) != null;
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
	
	private ResponseValues register(VitroRequest vreq, OrcidTokenResponse orcidToken) {
		try {
			OrcidPerson orcidBio = getPersonDetails(orcidToken);
			String familyName = getFamilyName(orcidBio); 
			String givenName = getGivenName(orcidBio);
			UserAccount userAccount = createUserAccount(vreq, orcidToken, familyName, givenName);
			String profileUri = createProfile(vreq, orcidToken, orcidBio, familyName, givenName, userAccount);
			getAuthenticator(vreq).recordLoginAgainstUserAccount(userAccount, LoginStatusBean.AuthenticationSource.EXTERNAL);
			String[] dois = getOrcidDOIs(vreq.getRDFService(), profileUri, orcidToken);
			return claimDoi(true, familyName, givenName, profileUri, dois);

		} catch(Authenticator.LoginNotPermitted e) {
			return new TemplateResponseValues(NOT_AUTHENTICATED_FTL);
		} catch (NotEnoughInfoForNewProfileException e) {
			return new TemplateResponseValues(NO_PROFILE_FTL);
		}
	}

	private ResponseValues login(VitroRequest vreq, OrcidTokenResponse orcidToken) {
		try {
			OrcidPerson orcidBio = getPersonDetails(orcidToken);
			String familyName = getFamilyName(orcidBio); 
			String givenName = getGivenName(orcidBio);
			UserAccount userAccount = getAuthenticator(vreq).getAccountForExternalAuth(orcidToken.orcid);
			String profileUri = getProfileUri(vreq, userAccount);
//			if (profileUri == null) {
//				profileUri = createProfile(vreq, orcidToken, orcidBio, familyName, givenName, userAccount);
//			}

			getAuthenticator(vreq).recordLoginAgainstUserAccount(userAccount, LoginStatusBean.AuthenticationSource.EXTERNAL);
			String[] dois = getOrcidDOIs(vreq.getRDFService(), profileUri, orcidToken);

//			if (shouldGoToClaimDoi(dois)) {
//				return claimDoi(false, familyName, givenName, profileUri, dois);
//			}

			if (profileUri == null) {
				return goToHomePage();
			} else {
				return goToUserProfile(vreq, profileUri);
			}

				
		} catch(Authenticator.LoginNotPermitted e) {
			return new TemplateResponseValues(NOT_AUTHENTICATED_FTL);
//		} catch (NotEnoughInfoForNewProfileException e) {
//			return new TemplateResponseValues(NO_PROFILE_FTL);
		}
	}

	private String createProfile(VitroRequest vreq, OrcidTokenResponse orcidToken, OrcidPerson orcidBio,
			String familyName, String givenName, UserAccount userAccount) throws NotEnoughInfoForNewProfileException {
		if (orcidBio == null || StringUtils.isEmpty(familyName)) {
			throw new NotEnoughInfoForNewProfileException();
		}
		// Get the default namespace to create a URI
		String defaultNamespace = vreq.getUnfilteredWebappDaoFactory().getDefaultNamespace();
		if (!defaultNamespace.endsWith("/")) {
			defaultNamespace += "/";
		}

		String profileUri = defaultNamespace + "orcid" + orcidToken.orcid;
		Model existingModel = getExistingProfile(vreq, profileUri);
		Model model = ModelFactory.createDefaultModel();
		model.add(existingModel);

		Resource profile = model.getResource(profileUri);
		addPersonType(model, profile);
		Resource contactDetails = getContactDetails(vreq, model, profile);
		setContactDetailsName(vreq, familyName, givenName, model, profile, contactDetails);
		addOrcidBioDataToProfile(vreq, orcidBio, profileUri, model, profile, contactDetails);
		writeChanges(vreq.getRDFService(), existingModel, model);

		SelfEditingConfiguration.getBean(vreq).associateIndividualWithUserAccount(
				vreq.getWebappDaoFactory().getIndividualDao(),
				vreq.getWebappDaoFactory().getDataPropertyStatementDao(), userAccount, profileUri);
		return profileUri;
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

	private ResponseValues claimDoi(boolean newAccount, String familyName, String givenName, String profileUri,
			String[] dois) {
		Map<String, Object> templateValues = new HashMap<>();
		templateValues.put("newAccount", newAccount);
		if (!StringUtils.isEmpty(familyName)) {
			if (!StringUtils.isEmpty(givenName)) {
				templateValues.put("profileName", givenName + " " + familyName);
			} else {
				templateValues.put("profileName", familyName);
			}
		} else if (!StringUtils.isEmpty(givenName)) {
			templateValues.put("profileName", givenName);
		}
		templateValues.put("profileUri", profileUri);

		if (dois != null && dois.length > 0) {
			templateValues.put("externalIds", StringUtils.join(dois, "\n"));
			templateValues.put("externalCount", dois.length);
			templateValues.put("profileUri", profileUri);
		}

		return new TemplateResponseValues(CLAIM_DOIS_FTL, templateValues);
	}

	private boolean shouldGoToClaimDoi( String[] dois) {
		return dois != null && dois.length > 0;
	}

	private void addOrcidBioDataToProfile(VitroRequest vreq, OrcidPerson orcidBio, String profileUri, Model model,
			Resource profile, Resource contactDetails) {

			if (orcidBio.biography != null && !StringUtils.isEmpty(orcidBio.biography.value)) {
				if (!profile.hasProperty(model.getProperty(VIVO_OVERVIEW))) {
					addBiographyToProfile(orcidBio, model, profile);
				}
			} else {
				log.debug("No ORCiD biography value");
			}

			if (orcidBio.keywords != null) {
				if (orcidBio.keywords.keyword != null) {
					if (!profile.hasProperty(model.getProperty(VIVO + "freetextKeyword"))) {
						for (ValueString keyword : orcidBio.keywords.keyword) {
							if (!StringUtils.isEmpty(keyword.value)) {
								// Split only on the literal comma
								String[] splitKeywords = keyword.value.split(",");
								for (int i = 0; i < splitKeywords.length; i++) {
									// Clean up the whitespace for each element
									splitKeywords[i] = splitKeywords[i].trim();
								}
								for (String splitKeyword : splitKeywords) {
									addKeywordToProfile(model, profile, splitKeyword);
								}
							}
						}
					}
				}
			} else {
				log.debug("No keywords found.");
			}
			if (orcidBio.contactDetails != null) {
				if (orcidBio.contactDetails.address != null && orcidBio.contactDetails.address.length > 0) {
					if (!StringUtils.isEmpty(orcidBio.contactDetails.address[0].country.value)) {
						String countryUri = findCountryFor(vreq.getRDFService(),
								orcidBio.contactDetails.address[0].country.value);
						if (!StringUtils.isEmpty(countryUri)) {
							if (!profile.hasProperty(model.getProperty(OBO + "RO_0001025"))) {
								addCountryUrlToProfile(model, profile, countryUri);
							}
						}
					}
				} else {
					log.debug("No contact details found");
				}

				if (orcidBio.emails != null || orcidBio.researcherUrls != null) {
					if (!contactDetails.hasProperty(model.getProperty(VCARD + "hasEmail"))) {
						if (orcidBio.emails != null) {
							getEmailInformation(vreq, orcidBio, model, contactDetails);
						} else {
							log.debug("No email information");
						}
					}

					if (!contactDetails.hasProperty(model.getProperty(VCARD_HAS_URL))) {
						if (orcidBio.researcherUrls != null && orcidBio.researcherUrls.researcherUrl != null) {
							getResearcherUrlsInformation(vreq, orcidBio, model, contactDetails);
						} else {
							log.debug("No researcher Urls");
						}
					}
				} else {
					log.debug("ORCiD emails or researcherUrls are not found.");
				}

				if (orcidBio.orcidIdentifier != null) {
					String orcidUri = orcidBio.orcidIdentifier.uri;
					if (StringUtils.isNotBlank(orcidUri)) {
						setOrcidId(profileUri, orcidUri, model, profile);
					}
				} else {
					log.debug("ORCiDBio contactDetails address does not contain any data");
				}
			}
	}

	private void setContactDetailsName(VitroRequest vreq, String familyName, String givenName, Model model,
			Resource profile, Resource contactDetails) {
		if (!contactDetails.hasProperty(model.getProperty(VCARD + "hasName"))) {
			if (!StringUtils.isEmpty(familyName)) {
				Resource contactName = model.createResource(getUnusedUri(vreq));
				contactName.addProperty(RDF.type, model.getResource(VCARD + "Name"));
				contactDetails.addProperty(model.getProperty(VCARD + "hasName"), contactName);

				contactName.addLiteral(model.getProperty(VCARD + "familyName"), familyName);

				if (!profile.hasProperty(model.getProperty(FOAF_LAST_NAME))) {
					profile.addLiteral(model.getProperty(FOAF_LAST_NAME), familyName);
				}

				if (!StringUtils.isEmpty(givenName)) {
					contactName.addLiteral(model.getProperty(VCARD + "givenName"), givenName);

					if (!profile.hasProperty(model.getProperty(FOAF_FIRST_NAME))) {
						profile.addLiteral(model.getProperty(FOAF_FIRST_NAME), givenName);
					}

					if (!profile.hasProperty(RDFS.label)) {
						profile.addProperty(RDFS.label, familyName + ", " + givenName);
					}
				} else {
					if (!profile.hasProperty(RDFS.label)) {
						profile.addProperty(RDFS.label, familyName);
					}
				}
			}
		}
	}

	private Resource getContactDetails(VitroRequest vreq, Model model, Resource profile) {
		Resource contactDetails;

		if (profile.hasProperty(model.getProperty(OBO + "ARG_2000028"))) {
			contactDetails = profile
					.getPropertyResourceValue(model.getProperty(OBO + "ARG_2000028"));
		} else {
			contactDetails = model.createResource(getUnusedUri(vreq));
			contactDetails.addProperty(RDF.type, model.getResource(VCARD + "Individual"));
			contactDetails.addProperty(model.getProperty(OBO + "ARG_2000029"),
					profile);
			profile.addProperty(model.getProperty(OBO + "ARG_2000028"),
					contactDetails);
		}
		return contactDetails;
	}

	private void addPersonType(Model model, Resource profile) {
		if (!profile.hasProperty(RDF.type)) {
			profile.addProperty(RDF.type, model.getResource(FOAF + "Person"));
		}
	}

	private String getGivenName(OrcidPerson orcidBio) {
		String givenName = "";
		if (orcidBio != null && orcidBio.orcidName != null) {
			if (orcidBio.orcidName.givenNames != null) {
				givenName = orcidBio.orcidName.givenNames.value;
			}
		}
		return givenName;
	}

	private String getFamilyName(OrcidPerson orcidBio) {
		String familyName = "";
		if (orcidBio != null && orcidBio.orcidName != null) {
			if (orcidBio.orcidName.familyName != null) {
				familyName = orcidBio.orcidName.familyName.value;
			}
		}
		return familyName;
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
			orcidToken = gson.fromJson(json, OrcidTokenResponse.class);
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
			orcidToken = gson.fromJson(json, OrcidTokenResponse.class);
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

	private UserAccount createUserAccount(VitroRequest vreq, OrcidTokenResponse orcidToken, String familyName, String givenName) {
		UserAccount userAccount;
		userAccount = new UserAccount();
		userAccount.setEmailAddress(orcidToken.orcid + "@forschungsatlas");
		userAccount.setFirstName(givenName);
		userAccount.setLastName(familyName);
		userAccount.setExternalAuthId(orcidToken.orcid);
		userAccount.setPasswordChangeRequired(false);
		userAccount.setPasswordLinkExpires(0);
		userAccount.setExternalAuthOnly(true);
		userAccount.setLoginCount(0);
		userAccount.setStatus(UserAccount.Status.ACTIVE);
		userAccount.setPermissionSetUris(Collections.singleton(PermissionSets.URI_SELF_EDITOR));

		UserAccountsDao userAccountsDao = vreq.getWebappDaoFactory().getUserAccountsDao();
		userAccountsDao.insertUserAccount(userAccount);
		return userAccount;
	}

	private String getRedirectLocation() {
		return "https://" + apiPrefix + "orcid.org/oauth/authorize" + "?client_id=" + clientId
				+ "&response_type=code" + "&scope=/authenticate" + "&redirect_uri=" + callbackUrl;
	}

	private void setOrcidId(String profileUri, String orcidUri, Model model, Resource profile) {
		if (!profile.hasProperty(model.getProperty(VIVO + "orcidId"))) {
			Resource orcid = model.createResource(orcidUri);
			orcid.addProperty(RDF.type, model.getProperty(OWL_THING));
			orcid.addProperty(model.getProperty(VIVO + "confirmedOrcidId"), profileUri);
			profile.addProperty(model.getProperty(VIVO + "orcidId"), orcid);
		}
	}

	private void getEmailInformation(VitroRequest vreq, OrcidPerson orcidBio, Model model, Resource contactDetails) {
		for (OrcidPerson.Emails.Email email : orcidBio.emails.email) {
			if (!StringUtils.isEmpty(email.value)) {
				addEmailToProfile(vreq, model, contactDetails, email);
			}
		}
	}

	private void addEmailToProfile(VitroRequest vreq, Model model, Resource contactDetails,
			OrcidPerson.Emails.Email email) {
		Resource emailResource = model.createResource(getUnusedUri(vreq));
		emailResource.addProperty(RDF.type, model.getResource(VCARD + "Email"));
		emailResource.addLiteral(model.getProperty(VCARD + "email"), email.value);
		contactDetails.addProperty(model.getProperty(VCARD + "hasEmail"), emailResource);
	}

	private void addCountryUrlToProfile(Model model, Resource profile, String countryUri) {
		profile.addProperty(
				model.getProperty(OBO + "RO_0001025"),
				model.getResource(countryUri));
	}

	private void getResearcherUrlsInformation(VitroRequest vreq, OrcidPerson orcidBio, Model model,
			Resource contactDetails) {
		for (OrcidPerson.ResearcherUrls.ResearcherUrl researcherUrl : orcidBio.researcherUrls.researcherUrl) {
			if (researcherUrl.url != null
					&& !StringUtils.isEmpty(researcherUrl.url.value)) {
				Resource url = model.createResource(getUnusedUri(vreq));

				url.addProperty(RDF.type, model.getResource(VCARD + "URL"));
				url.addProperty(model.getProperty(VCARD + "url"), researcherUrl.url.value);

				if (researcherUrl.url != null
						&& !StringUtils.isEmpty(researcherUrl.url.value)) {
					url.addProperty(RDFS.label, researcherUrl.url.value);
				}

				contactDetails.addProperty(model.getProperty(VCARD_HAS_URL), url);
			}
		}
	}

	private void addBiographyToProfile(OrcidPerson orcidBio, Model model, Resource profile) {
		profile.addLiteral(model.getProperty(VIVO_OVERVIEW), orcidBio.biography.value);
	}

	private void addKeywordToProfile(Model model, Resource profile, String splitKeyword) {
		profile.addLiteral(model.getProperty(VIVO + "freetextKeyword"), splitKeyword);
	}

	private String[] getOrcidDOIs(RDFService rdfService, String profileUri, OrcidTokenResponse token) {
		OrcidWorks orcidResponse = null;
		try {
			String url = getWorksUrl(token);

			HttpClient client = HttpClientFactory.getHttpClient();
			HttpGet request = new HttpGet(url);
			request.setHeader("Authorization", "Bearer " + token.accessToken);
			request.setHeader("Accept", "application/json");

			HttpResponse response = client.execute(request);
			switch (response.getStatusLine().getStatusCode()) {
			case 200:
				try (InputStream in = response.getEntity().getContent()) {
					StringWriter writer = new StringWriter();
					IOUtils.copy(in, writer, "UTF-8");

					String json = writer.toString();
					orcidResponse = gson.fromJson(json, OrcidWorks.class);

				}
			}
		} catch (IOException e) {
			log.error(e, e);
		}

		if (orcidResponse == null) {
			return null;
		}

		if (orcidResponse.orcidWork == null) {
			return null;
		}

		Model relationships = getKnownDoi(rdfService, profileUri);

		List<String> dois = new ArrayList<String>();

		for (OrcidWorks.OrcidWork work : orcidResponse.orcidWork) {
			if (work.workExternalIdentifiers != null && work.workExternalIdentifiers.workExternalIdentifier != null) {
				for (OrcidWorks.OrcidWork.WorkExternalIdentifiers.WorkExternalIdentifier externalIdentifier : work.workExternalIdentifiers.workExternalIdentifier) {
					if ("DOI".equalsIgnoreCase(externalIdentifier.workExternalIdentifierType)) {
						if (externalIdentifier.workExternalIdentifierId != null	&& !StringUtils.isEmpty(externalIdentifier.workExternalIdentifierId)) {
							if (isNotKnownDoi(relationships, externalIdentifier.workExternalIdentifierId)) {
								dois.add(externalIdentifier.workExternalIdentifierId.toLowerCase());
							}
						}
					}
				}
			}
		}

		return dois.toArray(new String[dois.size()]);
	}

	private boolean isNotKnownDoi(Model relationships, String doi) {
		return !relationships.contains(null,relationships.getProperty(BIBO_DOI), doi.toLowerCase());
	}

	private String getWorksUrl(OrcidTokenResponse token) {
		return "https://pub." + apiPrefix + "orcid.org/" + apiVersion + "/" + token.orcid + "/works";
	}

	private Authenticator getAuthenticator(HttpServletRequest req) {
		return Authenticator.getInstance(req);
	}

	private OrcidPerson getPersonDetails(OrcidTokenResponse token) {
		try {
			String url = "https://pub." + apiPrefix + "orcid.org/" + apiVersion + "/" + token.orcid + "/record";

			HttpClient client = HttpClientFactory.getHttpClient();
			HttpGet request = new HttpGet(url);
			request.setHeader("Authorization", "Bearer " + token.accessToken);
			request.setHeader("Accept", "application/json");

			HttpResponse response = client.execute(request);

			switch (response.getStatusLine().getStatusCode()) {
			case 200:
				try (InputStream in = response.getEntity().getContent()) {
					StringWriter writer = new StringWriter();
					IOUtils.copy(in, writer, "UTF-8");
					String json = writer.toString();
					if (log.isDebugEnabled()) {
						log.debug("Person json " + json);
					}
					OrcidRecord record = gson.fromJson(json, OrcidRecord.class);
					if (record != null && record.person != null) {
						record.person.orcidIdentifier = record.orcidIdentifier;
					}
					return record.person;
				}
			default:
				break;
			}
		} catch (IOException e) {
			log.error(e,e);
		}
		return null;
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
			request.setEntity(new UrlEncodedFormEntity(nvps));

			// Content negotiate for csl / citeproc JSON
			request.setHeader("Accept", "application/json");

			HttpResponse response = client.execute(request);
			switch (response.getStatusLine().getStatusCode()) {
			case 200:
				try (InputStream in = response.getEntity().getContent()) {
					StringWriter writer = new StringWriter();
					IOUtils.copy(in, writer, "UTF-8");
					return writer.toString();
				}
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

	private boolean profileExists(VitroRequest vreq, String uri) {
		IndividualDao dao = vreq.getWebappDaoFactory().getIndividualDao();
		return dao.getIndividualByURI(uri) != null;
	}

	/**
	 * @param vreq
	 * @param uri
	 * @return
	 */
	private Model getExistingProfile(VitroRequest vreq, String uri) {
		Model model = ModelFactory.createDefaultModel();

		try {
			String query = "PREFIX vcard:    <http://www.w3.org/2006/vcard/ns#>\n"
					+ "PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n" + "\n" + "CONSTRUCT\n" + "{\n" + "  <"
					+ uri + "> ?pPerson ?oPerson .\n" + "  ?sContactInfo ?pContactInfo ?oContactInfo .\n"
					+ "  ?sEmail ?pEmail ?oEmail .\n" + "  ?sUrl ?pUrl ?oUrl .\n" + "  ?sOrcid ?pOrcid ?oOrcid .\n"
					+ "  ?sResearchArea ?pResearchArea ?oResearchArea .\n" + "}\n" + "WHERE\n" + "{\n" + "  {\n"
					+ "    <" + uri + "> ?pPerson ?oPerson .\n" + "  }\n" + "  UNION\n" + "  {\n" + "    <" + uri
					+ "> <http://purl.obolibrary.org/obo/ARG_2000028> ?sContactInfo .\n"
					+ "    ?sContactInfo ?pContactInfo ?oContactInfo .\n" + "  }\n" + "  UNION\n" + "  {\n" + "    <"
					+ uri + "> <http://purl.obolibrary.org/obo/ARG_2000028> ?sContactInfo .\n"
					+ "    ?sContactInfo <http://www.w3.org/2006/vcard/ns#hasEmail> ?sEmail .\n"
					+ "    ?sEmail ?pEmail ?oEmail .\n" + "  }\n" + "  UNION\n" + "  {\n" + "    <" + uri
					+ "> <http://purl.obolibrary.org/obo/ARG_2000028> ?sContactInfo .\n"
					+ "    ?sContactInfo <http://www.w3.org/2006/vcard/ns#hasURL> ?sUrl .\n"
					+ "    ?sUrl ?pUrl ?oUrl .\n" + "  }\n" + "  UNION\n" + "  {\n" + "    <" + uri
					+ "> <http://vivoweb.org/ontology/core#orcidId> ?sOrcid .\n" + "    ?sOrcid ?pOrcid ?oOrcid .\n"
					+ "  }\n" + "  UNION\n" + "  {\n" + "    <" + uri
					+ "> <http://vivoweb.org/ontology/core#hasResearchArea> ?sResearchArea .\n"
					+ "    ?sResearchArea ?pResearchArea ?oResearchArea .\n" + "  }\n" + "}\n";

			vreq.getRDFService().sparqlConstructQuery(query, model);
		} catch (RDFServiceException e) {
			log.error(e, e);
		}

		return model;
	}

	private String getUnusedUri(VitroRequest vreq) {
		NewURIMakerVitro uriMaker = new NewURIMakerVitro(vreq.getWebappDaoFactory());
		try {
			return uriMaker.getUnusedNewURI(null);
		} catch (InsertException e) {
			log.error(e, e);
		}
		return null;
	}

	private Model getKnownDoi(RDFService rdfService, String uri) {
		Model model = ModelFactory.createDefaultModel();

		try {
			String query = 
					  "PREFIX vcard:    <http://www.w3.org/2006/vcard/ns#>\n"
					+ "PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n" + "\n" + "CONSTRUCT\n" + "{\n"
					+ " <" + uri + "> <http://purl.org/ontology/bibo/doi> ?oDoi .\n" + "}\n" + "WHERE\n" + "{\n" + " {\n"
					+ " ?sRelationship vivo:relates <" + uri + "> .\n"
					+ " ?sRelationship ?pRelationship ?oRelationship .\n"
					+ " ?oRelationship <http://purl.org/ontology/bibo/doi> ?oDoi .\n" + "  }\n" 
					+ "UNION { <" + uri + "> vivo:rejectedDoi ?oDoi . }\n }\n";
			rdfService.sparqlConstructQuery(query, model);
		} catch (RDFServiceException e) {
			log.error(e, e);
		}
		return model;
	}

	private String findCountryFor(RDFService rdfService, String code) {
		final List<String> countries = new ArrayList<String>();
		String query = "SELECT ?country\n" + "WHERE\n" + "{\n" + "  {\n"
				+ "  \t?country <http://aims.fao.org/aos/geopolitical.owl#codeISO2> \"" + code
				+ "\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" + "  }\n" + "}\n";

		try {
			rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
				@Override
				protected void processQuerySolution(QuerySolution qs) {
					Resource country = qs.getResource("country");
					if (country != null) {
						countries.add(country.getURI());
					}
				}
			});
		} catch (RDFServiceException e) {
			log.error(e, e);
		}

		if (countries.size() > 0) {
			return countries.get(0);
		}

		return null;
	}

	/**
	 * Determine the difference between the "existing" and "updated" models, and
	 * write the changes to VIVO
	 *
	 * @param rdfService
	 * @param existingModel
	 * @param updatedModel
	 */
	protected void writeChanges(RDFService rdfService, Model existingModel, Model updatedModel) {
		Model removeModel = existingModel == null ? ModelFactory.createDefaultModel()
				: existingModel.difference(updatedModel);
		Model addModel = existingModel == null ? updatedModel : updatedModel.difference(existingModel);

		if (!addModel.isEmpty() || !removeModel.isEmpty()) {
			InputStream addStream = null;
			InputStream removeStream = null;

			InputStream is = makeN3InputStream(updatedModel);
			ChangeSet changeSet = rdfService.manufactureChangeSet();

			if (!addModel.isEmpty()) {
				addStream = makeN3InputStream(addModel);
				changeSet.addAddition(addStream, RDFService.ModelSerializationFormat.N3, ModelNames.ABOX_ASSERTIONS);
			}

			if (!removeModel.isEmpty()) {
				removeStream = makeN3InputStream(removeModel);
				changeSet.addRemoval(removeStream, RDFService.ModelSerializationFormat.N3, ModelNames.ABOX_ASSERTIONS);
			}

			try {
				rdfService.changeSetUpdate(changeSet);
			} catch (RDFServiceException e) {
				log.error(e, e);
			} finally {
				if (addStream != null) {
					try {
						addStream.close();
					} catch (IOException e) {
						log.error(e, e);
					}
				}

				if (removeStream != null) {
					try {
						removeStream.close();
					} catch (IOException e) {
						log.error(e, e);
					}
				}
			}
		}
	}

	/**
	 * Convert the model into an N3 stream
	 *
	 * @param m
	 * @return
	 */
	private InputStream makeN3InputStream(Model m) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		m.write(out, "N3");
		return new ByteArrayInputStream(out.toByteArray());
	}

	/**
	 * Check that the resource is declared to be of a particular type
	 *
	 * @param resource
	 * @param typeUri
	 * @return
	 */
	protected boolean isResourceOfType(Resource resource, String typeUri) {
		if (resource == null) {
			return false;
		}

		StmtIterator iter = resource.listProperties(RDF.type);
		while (iter.hasNext()) {
			Statement stmt = iter.next();
			if (typeUri.equals(stmt.getResource().getURI())) {
				return true;
			}
		}

		return false;
	}

	private class OrcidTokenResponse {
		@SerializedName("access_token")
		String accessToken;

		String name;
		String orcid;

		@SerializedName("refresh_token")
		String refreshToken;

		OrcidTokenResponse() {
			// no-args constructor
		}
	}
	
	private class OrcidRecord {
		@SerializedName("orcid-identifier")
		OrcidIdentifier orcidIdentifier;

		@SerializedName("person")
		OrcidPerson person;
	}

	private class OrcidPerson {
		@SerializedName("name")
		OrcidName orcidName;

		VisibilityString biography;

		@SerializedName("researcher-urls")
		ResearcherUrls researcherUrls;

		@SerializedName("emails")
		Emails emails;

		@SerializedName("addresses")
		ContactDetails contactDetails;

		@SerializedName("keywords")
		Keywords keywords;
		
		@SerializedName("orcid-identifier")
		OrcidIdentifier orcidIdentifier;

		private class OrcidName {
			@SerializedName("given-names")
			ValueString givenNames;

			@SerializedName("family-name")
			ValueString familyName;

			String visibility;
		}

		private class ResearcherUrls {
			@SerializedName("researcher-url")
			ResearcherUrl[] researcherUrl;
			String visibility;

			private class ResearcherUrl {
				ValueString url;
			}
		}

		private class Emails {
			@SerializedName("email")
			Email[] email;

			private class Email {
				String value;
				String visibility;
			}
		}

		private class ContactDetails {
			@SerializedName("address")
			Address[] address;

			private class Address {
				@SerializedName("source")
				Source source;

				ValueString country;
				String visibility;

				private class Source {
					@SerializedName("source-orcid")
					OrcidIdentifier orcidIdentifier;
				}
			}
		}
		
	}
	
	private class OrcidIdentifier {
		String host;
		String path;
		String uri;
	}

	private class Keywords {
		ValueString[] keyword;
		String visibility;
	}

	private class OrcidWorks {
		@SerializedName("group")
		OrcidWork[] orcidWork;

		private class OrcidWork {
			@SerializedName("external-ids")
			WorkExternalIdentifiers workExternalIdentifiers;

			private class WorkExternalIdentifiers {
				@SerializedName("external-id")
				WorkExternalIdentifier[] workExternalIdentifier;

				private class WorkExternalIdentifier {
					@SerializedName("external-id-type")
					String workExternalIdentifierType;

					@SerializedName("external-id-value")
					String workExternalIdentifierId;
				}
			}
		}
	}

	private class ValueString {
		String value;
	}

	private class VisibilityString {
		String value;
		String visibility;
	}
	
	
	static class OrcidNotConfiguredException extends Exception{}

	static class UnexpectedLoggedInUserException extends Exception{
		private String url = null;
		
		UnexpectedLoggedInUserException(String message){
			super(message);
		}
		
		UnexpectedLoggedInUserException(String message, String url){
			super(message);
			this.url = url;
		}
		
		public String getUrl() {
			return url;
		}
	}
	
	static class NotEnoughInfoForNewProfileException extends Exception{}
	 
}
