/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.controller.freemarker;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.NewURIMakerVitro;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeSet;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.vivoweb.webapp.createandlink.Citation;
import org.vivoweb.webapp.createandlink.CreateAndLinkResourceProvider;
import org.vivoweb.webapp.createandlink.CreateAndLinkUtils;
import org.vivoweb.webapp.createandlink.ExternalIdentifiers;
import org.vivoweb.webapp.createandlink.ResourceModel;
import org.vivoweb.webapp.createandlink.crossref.CrossrefCreateAndLinkResourceProvider;
import org.vivoweb.webapp.createandlink.pubmed.PubMedCreateAndLinkResourceProvider;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction.SOME_LITERAL;
import static edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction.SOME_PREDICATE;
import static edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction.SOME_URI;

/**
 * Main controller class for claiming (creating and/or linking) resources to a profile
 */
@WebServlet(name = "CreateAndLinkResource", urlPatterns = {"/createAndLink/*"} )
public class CreateAndLinkResourceController extends FreemarkerHttpServlet {
    // Must be able to edit your own account to claim publications
    public static final AuthorizationRequest REQUIRED_ACTIONS = SimplePermission.EDIT_OWN_ACCOUNT.ACTION;

    // Mappings for publication type to ontology types / classes
    private static final Map<String, String> typeToClassMap = new HashMap<>();

    // Providers for resolving ids in different resource providers (e.g. CrossRef, PubMed)
    private static final Map<String, CreateAndLinkResourceProvider> providers = new HashMap<>();

    private static boolean providersRegistered = false;

    /**
     * URIs of types and predicates in the VIVO ontology that we need for creating resources
     */
    public static final String BIBO_ABSTRACT = "http://purl.org/ontology/bibo/abstract";
    public static final String BIBO_ARTICLE = "http://purl.org/ontology/bibo/Article";
    public static final String BIBO_BOOK = "http://purl.org/ontology/bibo/Book";
    public static final String BIBO_DOI = "http://purl.org/ontology/bibo/doi";
    public static final String BIBO_ISBN10 = "http://purl.org/ontology/bibo/isbn10";
    public static final String BIBO_ISBN13 = "http://purl.org/ontology/bibo/isbn13";
    public static final String BIBO_ISSN = "http://purl.org/ontology/bibo/issn";
    public static final String BIBO_ISSUE = "http://purl.org/ontology/bibo/issue";
    public static final String BIBO_JOURNAL = "http://purl.org/ontology/bibo/Journal";
    public static final String BIBO_PAGE_COUNT = "http://purl.org/ontology/bibo/numPages";
    public static final String BIBO_PAGE_END = "http://purl.org/ontology/bibo/pageEnd";
    public static final String BIBO_PAGE_START = "http://purl.org/ontology/bibo/pageStart";
    public static final String BIBO_PMID = "http://purl.org/ontology/bibo/pmid";
    public static final String BIBO_VOLUME = "http://purl.org/ontology/bibo/volume";

    public static final String FOAF_FIRSTNAME = "http://xmlns.com/foaf/0.1/firstName";
    public static final String FOAF_LASTNAME = "http://xmlns.com/foaf/0.1/lastName";

    public static final String OBO_CONTACT_INFO_FOR = "http://purl.obolibrary.org/obo/ARG_2000029";
    public static final String OBO_HAS_CONTACT_INFO = "http://purl.obolibrary.org/obo/ARG_2000028";

    public static final String OBO_INHERES_IN = "http://purl.obolibrary.org/obo/RO_0000052";
    public static final String OBO_BEARER_OF  = "http://purl.obolibrary.org/obo/RO_0000053";

    public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";

    public static final String VIVO_AUTHORSHIP = "http://vivoweb.org/ontology/core#Authorship";
    public static final String VIVO_DATETIME = "http://vivoweb.org/ontology/core#dateTime";
    public static final String VIVO_DATETIMEPRECISION = "http://vivoweb.org/ontology/core#dateTimePrecision";
    public static final String VIVO_DATETIMEVALUE = "http://vivoweb.org/ontology/core#dateTimeValue";
    public static final String VIVO_EDITORSHIP = "http://vivoweb.org/ontology/core#Editorship";
    public static final String VIVO_HASPUBLICATIONVENUE = "http://vivoweb.org/ontology/core#hasPublicationVenue";
    public static final String VIVO_PMCID = "http://vivoweb.org/ontology/core#pmcid";
    public static final String VIVO_PUBLICATIONVENUEFOR = "http://vivoweb.org/ontology/core#publicationVenueFor";
    public static final String VIVO_PUBLISHER = "http://vivoweb.org/ontology/core#publisher";
    public static final String VIVO_PUBLISHER_CLASS = "http://vivoweb.org/ontology/core#Publisher";
    public static final String VIVO_PUBLISHER_OF = "http://vivoweb.org/ontology/core#publisherOf";
    public static final String VIVO_RANK = "http://vivoweb.org/ontology/core#rank";
    public static final String VIVO_RELATEDBY = "http://vivoweb.org/ontology/core#relatedBy";
    public static final String VIVO_RELATES = "http://vivoweb.org/ontology/core#relates";

    public static final String VCARD_FAMILYNAME = "http://www.w3.org/2006/vcard/ns#familyName";
    public static final String VCARD_GIVENNAME = "http://www.w3.org/2006/vcard/ns#givenName";
    public static final String VCARD_HAS_NAME = "http://www.w3.org/2006/vcard/ns#hasName";
    public static final String VCARD_HAS_URL = "http://www.w3.org/2006/vcard/ns#hasURL";
    public static final String VCARD_INDIVIDUAL = "http://www.w3.org/2006/vcard/ns#Individual";
    public static final String VCARD_KIND = "http://www.w3.org/2006/vcard/ns#Kind";
    public static final String VCARD_NAME = "http://www.w3.org/2006/vcard/ns#Name";
    public static final String VCARD_URL_CLASS = "http://www.w3.org/2006/vcard/ns#URL";
    public static final String VCARD_URL_PROPERTY = "http://www.w3.org/2006/vcard/ns#url";

    private static final String PROVIDER_DOI = "doi";
    private static final String PROVIDER_PMID = "pmid";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ServletContext ctx = config.getServletContext();
        ConfigurationProperties props = ConfigurationProperties.getBean(ctx);

        // One-off initialization of class state
        // Add recognized publication types to the type map, along with the corresponding ontology URI

        typeToClassMap.put("article", "http://purl.org/ontology/bibo/Article");
        typeToClassMap.put("article-journal", "http://purl.org/ontology/bibo/AcademicArticle");
        typeToClassMap.put("book", "http://purl.org/ontology/bibo/Book");
        typeToClassMap.put("chapter", "http://purl.org/ontology/bibo/Chapter");
        typeToClassMap.put("dataset", "http://vivoweb.org/ontology/core#Dataset");
        typeToClassMap.put("figure", "http://purl.org/ontology/bibo/Image");
        typeToClassMap.put("graphic", "http://purl.org/ontology/bibo/Image");
        typeToClassMap.put("legal_case", "http://purl.org/ontology/bibo/LegalCaseDocument");
        typeToClassMap.put("legislation", "http://purl.org/ontology/bibo/Legislation");
        typeToClassMap.put("manuscript", "http://purl.org/ontology/bibo/Manuscript");
        typeToClassMap.put("map", "http://purl.org/ontology/bibo/Map");
        typeToClassMap.put("musical_score", "http://vivoweb.org/ontology/core#Score");
        typeToClassMap.put("paper-conference", "http://vivoweb.org/ontology/core#ConferencePaper");
        typeToClassMap.put("patent", "http://purl.org/ontology/bibo/Patent");
        typeToClassMap.put("personal_communication", "http://purl.org/ontology/bibo/PersonalCommunicationDocument");
        typeToClassMap.put("post-weblog", "http://vivoweb.org/ontology/core#BlogPosting");
        typeToClassMap.put("report", "http://purl.org/ontology/bibo/Report");
        typeToClassMap.put("review", "http://vivoweb.org/ontology/core#Review");
        typeToClassMap.put("speech", "http://vivoweb.org/ontology/core#Speech");
        typeToClassMap.put("thesis", "http://purl.org/ontology/bibo/Thesis");
        typeToClassMap.put("webpage", "http://purl.org/ontology/bibo/Webpage");

        // Populate the registry of resource providers
        registerProviders(props);
    }

    public static Set<String> getEnabledProviders(ConfigurationProperties props) {
        if (!providersRegistered) {
            registerProviders(props);
        }

        return providers.keySet();
    }

    public static synchronized  void registerProviders(ConfigurationProperties props) {
        if (!providersRegistered) {
            String provStr = props.getProperty("createAndLink.providers", "doi, pmid");
            if (!StringUtils.isEmpty(provStr)) {
                String[] provArr = provStr.split("[,]");
                for (String provId : provArr) {
                    if (PROVIDER_DOI.equalsIgnoreCase(provId.trim())) {
                        providers.put(PROVIDER_DOI,  new CrossrefCreateAndLinkResourceProvider());
                    } else if (PROVIDER_PMID.equalsIgnoreCase(provId.trim())) {
                        providers.put(PROVIDER_PMID, new PubMedCreateAndLinkResourceProvider());
                    }
                }
            }

            providersRegistered = true;
        }
    }

    /**
     * Ensure that we can only be called if the user has the correct permissions
     *
     * @param vreq
     * @return
     */
    @Override
    protected AuthorizationRequest requiredActions(VitroRequest vreq) {
        return REQUIRED_ACTIONS;
    }

    /**
     * Main method for the resource claiming (create and link) workflow
     *
     * @param vreq
     * @return
     */
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        // Get the current URL for parsing
        String requestURI = vreq.getRequestURI();

        CreateAndLinkResourceProvider provider = null;

        // First part of URL path after /createAndLink/ is used to identify the resource type (DOI, PubMed ID, etc)
        String externalProvider = null;
        int typePos = requestURI.indexOf("/createAndLink/") + 15;
        if (typePos < requestURI.length()) {
            if (requestURI.indexOf('/', typePos) > typePos) {
                externalProvider = requestURI.substring(typePos, requestURI.indexOf('/', typePos) - 1);
            } else {
                externalProvider = requestURI.substring(typePos);
            }

            // Normalize the resource type key, and get the appropriate provider
            externalProvider = externalProvider.trim().toLowerCase();
            if (providers.containsKey(externalProvider)) {
                provider = providers.get(externalProvider);
            }
        }

        // If no provider was found (invalid path), return an error to the user
        if (provider == null) {
            return new TemplateResponseValues("unknownResourceType.ftl");
        }

        // Obtain the DAO for getting an individual (that represents a person profile)
        IndividualDao individualDao = vreq.getWebappDaoFactory().getIndividualDao();
        Individual person = null;

        // If a person profile URI has been passed as a paremeter, ensure that the individual exists
        String profileUri = vreq.getParameter("profileUri");
        if (!StringUtils.isEmpty(profileUri)) {
            person = individualDao.getIndividualByURI(profileUri);
        }

        boolean isProfileUriForLoggedIn = false;

        // Get the currently logged in user
        UserAccount loggedInAccount = LoginStatusBean.getCurrentUser(vreq);
        SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(vreq);

        // Find the profile(s) associated with this user
        List<Individual> assocInds = sec.getAssociatedIndividuals(vreq.getWebappDaoFactory().getIndividualDao(), loggedInAccount.getExternalAuthId());
        if (!assocInds.isEmpty()) {
            if (person == null) {
                // If we have associated profiles, ensure that a valid person profile really does exist
                profileUri = assocInds.get(0).getURI();
                if (!StringUtils.isEmpty(profileUri)) {
                    person = individualDao.getIndividualByURI(profileUri);
                    isProfileUriForLoggedIn = true;
                }
            } else if (!StringUtils.isEmpty(profileUri)){
                for (Individual ind : assocInds) {
                    if (ind.getURI().equalsIgnoreCase(profileUri)) {
                        isProfileUriForLoggedIn = true;
                    }
                }
            }
        }

        // If we still haven't got a person, return an error to the user
        if (person == null) {
            return new TemplateResponseValues("unknownProfile.ftl");
        }

        // If the profile isn't associated with the logged in user
        if (!isProfileUriForLoggedIn) {
            // Check that we have back end editing priveleges
            if (!PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.DO_BACK_END_EDITING.ACTION)) {
                // If all else fails, can we add statements to this individual?
                AddDataPropertyStatement adps = new AddDataPropertyStatement(vreq.getJenaOntModel(), profileUri, SOME_URI, SOME_LITERAL);
                AddObjectPropertyStatement aops = new AddObjectPropertyStatement(vreq.getJenaOntModel(), profileUri, SOME_PREDICATE, SOME_URI);
                if (!PolicyHelper.isAuthorizedForActions(vreq, adps.or(aops))) {
                    return new TemplateResponseValues("unauthorizedForProfile.ftl");
                }
            }
        }

            // Create a map of common values to pass to the templates
        Map<String, Object> templateValues = new HashMap<>();
        templateValues.put("link", profileUri);
        templateValues.put("label", provider.getLabel());
        templateValues.put("provider", externalProvider);
        templateValues.put("profileUri", profileUri);
        templateValues.put("personLabel",    person.getRdfsLabel());
        templateValues.put("personThumbUrl", person.getThumbUrl());

        // Get the requested action (e.g. find, confirm)
        String action = vreq.getParameter("action");
        if (action == null) {
            action = "";
        }

        String externalIdsToFind = null;

        // If the user has pressed a "confirm" button
        if ("confirmID".equals(action)) {
            // Get all of the external IDs represented on the page
            String[] externalIds = vreq.getParameterValues("externalId");

            // Check that we have IDs to process
            if (!ArrayUtils.isEmpty(externalIds)) {
                // Create a holder for statements already in the triple store, and another for the changes
                Model existingModel = ModelFactory.createDefaultModel();
                Model updatedModel = ModelFactory.createDefaultModel();

                // Loop through each external ID that was on the page
                for (String externalId : externalIds) {
                    // Get the normalized ID from the resource provider
                    externalId = provider.normalize(externalId);

                    // Ensure that we have an ID
                    if (!StringUtils.isEmpty(externalId)) {
                        // Check that the user is claiming a relationship to the resource
                        if (!"notmine".equalsIgnoreCase(vreq.getParameter("contributor" + externalId))) {
                            // If we are processing a resource that is already in VIVO, get the Vivo URI from the form
                            String vivoUri = vreq.getParameter("vivoUri" + externalId);

                            // If we don't already know that the resource has been created in VIVO
                            if (StringUtils.isEmpty(vivoUri)) {
                                // Check that it hasn't been created since when we first rendered the page
                                ExternalIdentifiers allExternalIds = provider.allExternalIDsForFind(externalId);
                                vivoUri = findInVIVO(vreq, allExternalIds, profileUri, null);
                            }

                            // If we haven't got an existing VIVO resource by this point, create it
                            if (StringUtils.isEmpty(vivoUri)) {
                                ResourceModel resourceModel = null;

                                // Get the publication type from the form
                                String typeUri = vreq.getParameter("type" + externalId);

                                // Get the appropriate resource provider for the external ID from the form
                                String resourceProvider = vreq.getParameter("externalProvider" + externalId);

                                // Get an intermediate ResourceModel from the provider
                                if (providers.containsKey(resourceProvider)) {
                                    resourceModel = providers.get(resourceProvider).makeResourceModel(externalId, vreq.getParameter("externalResource" + externalId));
                                } else {
                                    resourceModel = provider.makeResourceModel(externalId, vreq.getParameter("externalResource" + externalId));
                                }

                                // If we have an intermediate model, create the VIVO representation from the model
                                if (resourceModel != null) {
                                    vivoUri = createVIVOObject(vreq, updatedModel, resourceModel, typeUri);
                                }
                            } else {
                                // Get the existing statements for the model, and add them to the both in-memory models
                                Model existingResourceModel = getExistingResource(vreq, vivoUri);
                                existingModel.add(existingResourceModel);
                                updatedModel.add(existingResourceModel);
                            }

                            // Process the user's chosen relationship with the resource, updating the updated model
                            processRelationships(vreq, updatedModel, vivoUri, profileUri, vreq.getParameter("contributor" + externalId));
                        }
                    }
                }

                // Finished processing confirmation, write the differences between the existing and updated model
                writeChanges(vreq.getRDFService(), existingModel, updatedModel);
            }

            // Get any IDs that have not yet been processed from the form
            externalIdsToFind = vreq.getParameter("remainderIds");

            // If There are no IDs left to process, go back to the entry screen
            if (StringUtils.isEmpty(externalIdsToFind)) {
                templateValues.put("showConfirmation", true);
                return new TemplateResponseValues("createAndLinkResourceEnterID.ftl", templateValues);
            }
        } else if ("findID".equals(action)) {
            // User has pressed a "findID" button - e.g. has entered IDs of resources on the initial entry screen
            externalIdsToFind = vreq.getParameter("externalIds");
        }

        // If we have external IDs to find (either directly from the entry form, or unprocessed from a long list)
        if (!StringUtils.isEmpty(externalIdsToFind)) {
            Set<String> uniqueIds = new HashSet<>();
            Set<String> remainderIds = new HashSet<>();
            List<Citation> citations = new ArrayList<>();

            // Split the passed IDs into a parseable array (separated by whitespace, oe comma)
            String[] externalIdArr = externalIdsToFind.split("[\\s,]+");

            // Go through each identifier
            for (String externalId : externalIdArr) {
                // Normalize the identifier, and create a set of unique identifiers (remove duplicates)
                externalId = provider.normalize(externalId);
                if (!StringUtils.isEmpty(externalId) && !uniqueIds.contains(externalId)) {
                    uniqueIds.add(externalId);
                }
            }

            int idCount = 0;
            // Loop through all the unique identifiers
            for (String externalId : uniqueIds) {
                // If we've already processed 5 or more identifiers
                if (idCount > 4) {
                    // Add the identifier to the remainder list to be processed on the next page
                    remainderIds.add(externalId);
                } else {
                    // Prepare a citation object for this identifier
                    Citation citation = new Citation();
                    citation.externalId = externalId;

                    // First, resolve all known identifiers for the identifier processed
                    ExternalIdentifiers allExternalIds = provider.allExternalIDsForFind(externalId);

                    // Try to find an existing resource that has one of the known external identifiers
                    // Note, this will populate the citation object if it exists
                    citation.vivoUri = findInVIVO(vreq, allExternalIds, profileUri, citation);

                    // If we did not find a resource in VIVO
                    if (StringUtils.isEmpty(citation.vivoUri)) {
                        // If we have a DOI for the resource, first attempt to find the metadata via DOI
                        if (!StringUtils.isEmpty(allExternalIds.DOI)) {
                            CreateAndLinkResourceProvider doiProvider = providers.get("doi");
                            if (doiProvider != null) {
                                // Attempt to find the DOI in via the doi resource provider (fills the citation object)
                                citation.externalResource = doiProvider.findInExternal(allExternalIds.DOI, citation);

                                // If we were successful, record that the record was looked up via DOI
                                if (!StringUtils.isEmpty(citation.externalResource)) {
                                    citation.externalProvider = "doi";
                                }
                            }
                        }

                        // Did not resolve the resource via DOI, so look in the provider for the original identifier
                        if (StringUtils.isEmpty(citation.externalResource)) {
                            // Only if the original identifier was not a DOI
                            if (!"doi".equalsIgnoreCase(externalProvider)) {
                                citation.externalResource = provider.findInExternal(externalId, citation);
                                citation.externalProvider = externalProvider;
                            }
                        }
                    }

                    // Guess which author in the available metadata is the user claiming the work
                    proposeAuthorToLink(vreq, citation, profileUri);

                    // Conver the type in the citation to a VIVO type uri for use in the confirmation form
                    citation.typeUri = typeToClassMap.getOrDefault(citation.type, BIBO_ARTICLE);

                    // If we have found a citation, add it to the list of citations to display
                    if (citation.vivoUri != null || citation.externalResource != null) {
                        citations.add(citation);

                        // Increment the count of processed identifiers
                        idCount++;
                    } else {
                        citation.showError = true;
                        citations.add(citation);
                    }
                }
            }

            // If we have found records to claim
            if (citations.size() > 0) {
                // Add the citations to the values to pass to the template
                templateValues.put("citations", citations);

                // Add the list of known publication types
                templateValues.put("publicationTypes", getPublicationTypes(vreq));

                // If there are IDs still left to process, add them to the values passed to the template
                if (remainderIds.size() > 0) {
                    templateValues.put("remainderIds", StringUtils.join(remainderIds, "\n"));
                    templateValues.put("remainderCount", remainderIds.size());
                }

                // Show the confirmation page for the processed identifiers
                return new TemplateResponseValues("createAndLinkResourceConfirm.ftl", templateValues);
            } else {
                // Nothing to show, so go back to the form, passing an indicator that nothing was found
                templateValues.put("notfound", true);
                return new TemplateResponseValues("createAndLinkResourceEnterID.ftl", templateValues);
            }
        }

        // Show the entry form for a user to enter a set of identifiers
        return new TemplateResponseValues("createAndLinkResourceEnterID.ftl", templateValues);
    }

    private String getFormattedProfileName(VitroRequest vreq, String profileUri) {
        final Citation.Name name = new Citation.Name();

        name.name = null;

        String vcardQuery = "SELECT ?givenName ?familyName\n" +
                "WHERE\n" +
                "{\n" +
                "  <" + profileUri + "> <" + OBO_HAS_CONTACT_INFO + "> ?vCard .\n" +
                "  ?vCard <" + VCARD_HAS_NAME + "> ?vCardName .\n" +
                "  ?vCardName <" + VCARD_FAMILYNAME + "> ?familyName .\n" +
                "  OPTIONAL { ?vCardName <" + VCARD_GIVENNAME + "> ?givenName . }\n" +
                "}\n";

        try {
            // Process the query
            vreq.getRDFService().sparqlSelectQuery(vcardQuery, new ResultSetConsumer() {
                @Override
                protected void processQuerySolution(QuerySolution qs) {
                    // Get the name(s) from the result set
                    Literal familyName = qs.contains("familyName") ? qs.getLiteral("familyName") : null;
                    Literal givenName  = qs.contains("givenName") ? qs.getLiteral("givenName") : null;

                    if (StringUtils.isEmpty(name.name)) {
                        // If we have a first / last name, create a formatted author string
                        if (familyName != null) {
                            if (givenName != null) {
                                name.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), givenName.getString());
                            } else {
                                name.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), null);
                            }
                        }
                    }
                }
            });
        } catch (RDFServiceException e) {
            e.printStackTrace();
        }

        String foafQuery = "SELECT ?givenName ?familyName\n" +
                "WHERE\n" +
                "{\n" +
                "  <" + profileUri + "> <" + FOAF_LASTNAME + "> ?familyName .\n" +
                "  OPTIONAL { <" + profileUri + "> <" + FOAF_FIRSTNAME + "> ?givenName . }\n" +
                "}";

        if (StringUtils.isEmpty(name.name)) {
            try {
                // Process the query
                vreq.getRDFService().sparqlSelectQuery(foafQuery, new ResultSetConsumer() {
                    @Override
                    protected void processQuerySolution(QuerySolution qs) {
                        // Get the name(s) from the result set
                        Literal familyName = qs.contains("familyName") ? qs.getLiteral("familyName") : null;
                        Literal givenName  = qs.contains("givenName") ? qs.getLiteral("givenName") : null;

                        if (StringUtils.isEmpty(name.name)) {
                            // If we have a first / last name, create a formatted author string
                            if (familyName != null) {
                                if (givenName != null) {
                                    name.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), givenName.getString());
                                } else {
                                    name.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), null);
                                }
                            }
                        }
                    }
                });
            } catch (RDFServiceException e) {
                e.printStackTrace();
            }
        }

        String labelQuery = "SELECT ?label\n" +
                "WHERE\n" +
                "{\n" +
                "  <" + profileUri + "> <" + RDFS_LABEL + "> ?label .\n" +
                "}\n";

        if (StringUtils.isEmpty(name.name)) {
            try {
                // Process the query
                vreq.getRDFService().sparqlSelectQuery(labelQuery, new ResultSetConsumer() {
                    @Override
                    protected void processQuerySolution(QuerySolution qs) {
                        // Get the name(s) from the result set
                        Literal label      = qs.contains("label") ? qs.getLiteral("label") : null;

                        String authorStr = null;
                        if (label != null) {
                            // If we have a formatted label, normalize it to last name, initials
                            authorStr = label.getString();
                            if (authorStr.indexOf(',') > -1) {
                                int endIdx = authorStr.indexOf(',');
                                while (endIdx < authorStr.length()) {
                                    if (Character.isAlphabetic(authorStr.charAt(endIdx))) {
                                        break;
                                    }
                                    endIdx++;
                                }

                                if (endIdx < authorStr.length()) {
                                    authorStr = authorStr.substring(0, endIdx + 1);
                                } else {
                                    authorStr = authorStr.substring(0, authorStr.indexOf(','));
                                }
                            }
                        }

                        // If we have a formatted author string
                        if (authorStr != null) {
                            name.name = authorStr;
                        }

                    }
                });
            } catch (RDFServiceException e) {
                e.printStackTrace();
            }
        }

        return name.name;
    }

    /**
     * Method to find an author to propose for linking
     *
     * @param vreq
     * @param citation
     * @param profileUri
     */
    protected void proposeAuthorToLink(VitroRequest vreq, final Citation citation, String profileUri) {
        // If the resource has no idnetifiers, we have nothing to do
        if (citation.authors == null) {
            return;
        }

        String authorStr = getFormattedProfileName(vreq, profileUri);
        if (StringUtils.isEmpty(authorStr)) {
            return;
        }

        // If we have a formatted author string
        String authorStrLwr = authorStr.toLowerCase();

        // Find a match for the author string in the resource
        for (Citation.Name author : citation.authors) {
            if (author != null && author.name != null) {
                String nameLwr = author.name.toLowerCase();
                if (nameLwr.startsWith(authorStrLwr) || authorStrLwr.startsWith(nameLwr)) {
                    author.proposed = true;
                    break;
                }
            }
        }
    }

    /**
     * Find an existing resource in VIVO, and return a Model with the appropriate statements
     *
     * @param vreq
     * @param uri
     * @return
     */
    protected Model getExistingResource(VitroRequest vreq, String uri) {
        Model model = ModelFactory.createDefaultModel();

        try {
            String query =
                    "PREFIX vcard:    <http://www.w3.org/2006/vcard/ns#>\n" +
                    "PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n" +
                    "PREFIX obo:     <http://purl.obolibrary.org/obo/>\n" +
                    "\n" +
                    "CONSTRUCT\n" +
                    "{\n" +
                    "  <" + uri + "> ?pWork ?oWork .\n" +
                    "  ?sJournal ?pJournal ?oJournal .\n" +
                    "  ?sDateTime ?pDateTime ?oDateTime .\n" +
                    "  ?sRel ?pRel ?oRel .\n" +
                    "  ?sVCard a vcard:Individual .\n" +
                    "  ?sVCard vcard:hasName ?sVCardName .\n" +
                    "  ?sVCardName ?pVCardName ?oVCardName .\n" +
                    "  ?sPerson ?pPerson ?oPerson .\n" +
                    "}\n" +
                    "WHERE\n" +
                    "{\n" +
                    "  {\n" +
                    "    <" + uri + "> ?pWork ?oWork .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "    <" + uri + "> vivo:hasPublicationVenue ?sJournal .\n" +
                    "    ?sJournal ?pJournal ?oJournal .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "    <" + uri + "> vivo:dateTimeValue ?sDateTime .\n" +
                    "    ?sDateTime ?pDateTime ?oDateTime .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "    <" + uri + "> vivo:relatedBy ?sRel .\n" +
                    "    ?sRel ?pRel ?oRel .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "    <" + uri + "> vivo:relatedBy ?relationship .\n" +
                    "    ?relationship ?pRel ?sPerson .\n" +
                    "    ?sPerson ?pPerson ?oPerson .\n" +
                    "    FILTER (?sPerson != <" + uri + ">)\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "    <" + uri + "> vivo:relatedBy ?relationship .\n" +
                    "    ?relationship ?pRel ?sPerson .\n" +
                    "    ?sPerson obo:ARG_2000028 ?sVCard .\n" +
                    "    ?sVCard vcard:hasName ?sVCardName .\n" +
                    "    ?sVCardName ?pVCardName ?oVCardName .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "    <" + uri + "> vivo:relatedBy ?relationship .\n" +
                    "    ?relationship ?pRel ?sVCard .\n" +
                    "    ?sVCard vcard:hasName ?sVCardName .\n" +
                    "    ?sVCardName ?pVCardName ?oVCardName .\n" +
                    "  }\n" +
                    "}\n";

            vreq.getRDFService().sparqlConstructQuery(query, model);
        } catch (RDFServiceException e) {
        }

        return model;
    }

    /**
     * Adjust the in-memory model to create the appropriate relationships for the claimed user role (authorship, editorship, etc)
     *
     * @param vreq
     * @param model
     * @param vivoUri
     * @param userUri
     * @param relationship
     */
    protected void processRelationships(VitroRequest vreq, Model model, String vivoUri, String userUri, String relationship) {
        if (relationship != null) {
            // If authorship is being claimed
            if (relationship.startsWith("author")) {
                // Create an authorship context object
                Resource authorship = model.createResource(getUnusedUri(vreq));
                authorship.addProperty(RDF.type, model.getResource(VIVO_AUTHORSHIP));

                // Add the resource and the user as relates predicates of the context
                authorship.addProperty(model.createProperty(VIVO_RELATES), model.getResource(vivoUri));
                authorship.addProperty(model.createProperty(VIVO_RELATES), model.getResource(userUri));

                // Add related by predicates to the user and resource, linking to the context
                model.getResource(vivoUri).addProperty(model.createProperty(VIVO_RELATEDBY), authorship);
                model.getResource(userUri).addProperty(model.createProperty(VIVO_RELATEDBY), authorship);

                // If the relationship contains an author position
                if (relationship.length() > 6) {
                    // Parse out the author position to a numeric rank
                    String posStr = relationship.substring(6);
                    int rank = Integer.parseInt(posStr, 10);
                    // Remove an existing authorship at that rank
                    removeAuthorship(vreq, model, vivoUri, rank);
                    try {
                        // Add the chosen rank to the authorship context created
                        authorship.addLiteral(model.createProperty(VIVO_RANK), rank);
                    } catch (NumberFormatException nfe) {
                    }
                }
            } else if (relationship.startsWith("editor")) {
                // User is claiming editorship
                Resource editorship = model.createResource(getUnusedUri(vreq));
                editorship.addProperty(RDF.type, model.getResource(VIVO_EDITORSHIP));

                // Add the resource and the user as relates predicates of the context
                editorship.addProperty(model.createProperty(VIVO_RELATES), model.getResource(vivoUri));
                editorship.addProperty(model.createProperty(VIVO_RELATES), model.getResource(userUri));

                // Add related by predicates to the user and resource, linking to the context
                model.getResource(vivoUri).addProperty(model.createProperty(VIVO_RELATEDBY), editorship);
                model.getResource(userUri).addProperty(model.createProperty(VIVO_RELATEDBY), editorship);
            }
        }
    }

    /**
     * Removes an existing authorship at a given position, when that position is claimed by the author
     *
     * @param model
     * @param rank
     */
    protected void removeAuthorship(VitroRequest vreq, Model model, String vivoUri, int rank) {
        // Prepare property / resources outsite of the loop
        Property RANK_PREDICATE      = model.getProperty(VIVO_RANK);
        Property RELATES_PREDICATE   = model.getProperty(VIVO_RELATES);
        Resource AUTHORSHIP_RESOURCE = model.getResource(VIVO_AUTHORSHIP);

        // Lookp through all the subjects in the model
        ResIterator iter = model.listSubjects();
        try {
            while (iter.hasNext()) {
                Resource subject = iter.next();
                // If the subject is an Authorship context
                if (subject.hasProperty(RDF.type, AUTHORSHIP_RESOURCE)) {
                    // And the subject is related to the resource we are interested in
                    if (subject.hasProperty(RELATES_PREDICATE, model.getResource(vivoUri))) {
                        // And the subject is for the rank (position that we are interested in
                        if (subject.hasLiteral(RANK_PREDICATE, rank)) {
                            // Remove all the predicates referring to this authorship context
                            model.removeAll(null, null, subject);

                            // List all the relates predicates on authorship context
                            StmtIterator stmtIterator = subject.listProperties(RELATES_PREDICATE);
                            try {
                                while (stmtIterator.hasNext()) {
                                    Statement stmt = stmtIterator.next();
                                    RDFNode rdfNode = stmt.getObject();
                                    // Ensure the object of the statement is a resource
                                    if (rdfNode.isResource()) {
                                        Resource relatedResource = rdfNode.asResource();

                                        // Ensure that the object resource is a VCARD
                                        if (relatedResource.hasProperty(RDF.type, model.getResource(VCARD_INDIVIDUAL))) {
                                            // If the VCARD has no other references
                                            if (isVCardOnlyLinkedToAuthorship(vreq.getRDFService(), subject.getURI(), relatedResource.getURI())) {
                                                // Remove the VCARD
                                                removeVCard(model, relatedResource.getURI());
                                            }
                                        }
                                    }
                                }
                            } finally {
                                stmtIterator.close();
                            }
                            // Remove all statements related to this authorship context
                            subject.removeProperties();
                        }
                    }
                }
            }
        } finally {
            iter.close();
        }
    }

    private boolean isVCardOnlyLinkedToAuthorship(final RDFService rdfService, final String authorshipUri, final String vcardUri) {
        final List<String> subjects = new ArrayList<>();
        String query = "SELECT ?s ?p\n" +
                "WHERE\n" +
                "{\n" +
                "  {\n" +
                "  \t?s ?p <" + vcardUri + "> .\n" +
                "  }\n" +
                "}\n";

        try {
            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                @Override
                protected void processQuerySolution(QuerySolution qs) {
                    Resource subject = qs.getResource("s");
                    if (!authorshipUri.equalsIgnoreCase(subject.getURI())) {
                        subjects.add(subject.getURI());
                    }
                }
            });
        } catch (RDFServiceException e) {
        }

        return subjects.size() == 0;
    }

    /**
     * Attempt to remove a VCARD from VIVO, ensuring that it is not being used first
     *
     * @param model
     * @param vcardUri
     */
    protected void removeVCard(Model model, String vcardUri) {
        // Get the VCARD resource from the model
        Resource vcard = model.getResource(vcardUri);

        // Remove the VCARD name
        Statement vcardName = vcard.getProperty(model.createProperty(VCARD_HAS_NAME));
        if (vcardName != null) {
            if (vcardName.getObject().isResource()) {
                vcardName.getObject().asResource().removeProperties();
            }
        }

        // Remove the VCARD
        vcard.removeProperties();
    }

    /**
     * Create a new resource in VIVO, based on the values of the intermediate model
     *
     * @param vreq
     * @param model
     * @param resourceModel
     * @return
     */
    protected String createVIVOObject(VitroRequest vreq, Model model, ResourceModel resourceModel, String typeUri) {
        String vivoUri = getUnusedUri(vreq);

        // Create the resource in our model
        Resource work = model.createResource(vivoUri);

        // Add the correct type to the resource
        if (!StringUtils.isEmpty(typeUri)) {
            // If the form passed a type uri, ensure that it is a known type URI
            for (PublicationType publicationType: getPublicationTypes(vreq)) {
                // We have a known type, so record it on the work
                if (typeUri.equals(publicationType.getUri())) {
                    work.addProperty(RDF.type, model.getResource(typeUri));
                    break;
                }
            }
        }

        // If the work does not have a type set
        if (!work.hasProperty(RDF.type)) {
            // Try to map the type in the external model, or use academic article if none.
            work.addProperty(RDF.type, model.getResource(typeToClassMap.getOrDefault(resourceModel.type, BIBO_ARTICLE)));
        }

        // Add the title
        if (!StringUtils.isEmpty(resourceModel.title)) {
            work.addProperty(RDFS.label, resourceModel.title);
        }

        // Add a DOI
        if (!StringUtils.isEmpty(resourceModel.DOI)) {
            String doi = new CrossrefCreateAndLinkResourceProvider().normalize(resourceModel.DOI);
            work.addProperty(model.createProperty(BIBO_DOI), doi);
        }

        // Add a PubMed ID
        if (!StringUtils.isEmpty(resourceModel.PubMedID)) {
            work.addProperty(model.createProperty(BIBO_PMID), resourceModel.PubMedID.toLowerCase());
        }

        // Add a PubMed Central ID
        if (!StringUtils.isEmpty(resourceModel.PubMedCentralID)) {
            work.addProperty(model.createProperty(VIVO_PMCID), resourceModel.PubMedCentralID.toLowerCase());
        }

        // Add the journal
        if (!"book".equals(resourceModel.type) && !"chapter".equals(resourceModel.type)) {
            if (resourceModel.ISSN != null && resourceModel.ISSN.length > 0) {
                Resource journal = null;

                // Try to find the ISSN in VIVO
                String journalUri = findVIVOUriForISSNs(vreq.getRDFService(), resourceModel.ISSN);

                if (!StringUtils.isEmpty(journalUri)) {
                    // If we jave a Journal URI, get the resource from the model
                    journal = model.getResource(journalUri);
                } else {
                    // Create a new journal, using the ISSN for a Uri
                    journal = model.createResource(getUnusedUri(vreq));
                    journal.addProperty(RDFS.label, resourceModel.containerTitle);
                    journal.addProperty(RDF.type, model.getResource(BIBO_JOURNAL));
                    for (String issn : resourceModel.ISSN) {
                        journal.addProperty(model.getProperty(BIBO_ISSN), issn);
                    }

                    if (!StringUtils.isEmpty(resourceModel.publisher)) {
                        String publisherUri = findVIVOUriForPublisher(vreq.getRDFService(), resourceModel.publisher);

                        Resource publisher = null;
                        if (!StringUtils.isEmpty(publisherUri)) {
                            publisher = model.getResource(publisherUri);
                        } else {
                            publisher = model.createResource(getPublisherURI(vreq, resourceModel.publisher));
                            publisher.addProperty(RDFS.label, resourceModel.publisher);
                            publisher.addProperty(RDF.type, model.getResource(VIVO_PUBLISHER_CLASS));
                        }

                        if (publisher != null) {
                            publisher.addProperty(model.createProperty(VIVO_PUBLISHER_OF), journal);
                            journal.addProperty(model.createProperty(VIVO_PUBLISHER), publisher);
                        }
                    }
                }

                // Add relationships between our resource and the journal
                if (journal != null) {
                    journal.addProperty(model.getProperty(VIVO_PUBLICATIONVENUEFOR), work);
                    work.addProperty(model.getProperty(VIVO_HASPUBLICATIONVENUE), journal);
                }
            }
        }

        // Add an ISBN
        if (resourceModel.ISBN != null && resourceModel.ISBN.length > 0) {
            for (String isbn : resourceModel.ISBN) {
                int length = getDigitCount(isbn);
                if (length == 10) {
                    work.addProperty(model.getProperty(BIBO_ISBN10), isbn);
                } else {
                    work.addProperty(model.getProperty(BIBO_ISBN13), isbn);
                }
            }

            if ("chapter".equals(resourceModel.type)) {
                Resource book = null;

                String bookUri = findVIVOUriForISBNs(vreq.getRDFService(), resourceModel.ISBN);
                if (StringUtils.isEmpty(bookUri)) {
                    book = model.createResource(getUnusedUri(vreq));

                    book.addProperty(RDFS.label, resourceModel.containerTitle);
                    book.addProperty(RDF.type, model.getResource(BIBO_BOOK));
                    for (String isbn : resourceModel.ISBN) {
                        if (getDigitCount(isbn) == 10) {
                            book.addProperty(model.getProperty(BIBO_ISBN10), isbn);
                        } else {
                            book.addProperty(model.getProperty(BIBO_ISBN13), isbn);
                        }
                    }

                    if (!StringUtils.isEmpty(resourceModel.publisher)) {
                        Resource publisher = model.createResource(getPublisherURI(vreq, resourceModel.publisher));
                        publisher.addProperty(RDFS.label, resourceModel.publisher);
                        publisher.addProperty(RDF.type, model.getResource(VIVO_PUBLISHER_CLASS));
                        publisher.addProperty(model.createProperty(VIVO_PUBLISHER_OF), book);
                        book.addProperty(model.createProperty(VIVO_PUBLISHER), publisher);
                    }
                } else {
                    book = model.getResource(bookUri);
                }

                if (book != null) {
                    book.addProperty(model.getProperty(VIVO_PUBLICATIONVENUEFOR), work);
                    work.addProperty(model.getProperty(VIVO_HASPUBLICATIONVENUE), book);
                }
            }
        }

        // Add the volume
        if (!StringUtils.isEmpty(resourceModel.volume)) {
            work.addProperty(model.createProperty(BIBO_VOLUME), resourceModel.volume);
        }

        // Add the issue
        if (!StringUtils.isEmpty(resourceModel.issue)) {
            work.addProperty(model.createProperty(BIBO_ISSUE), resourceModel.issue);
        }

        // Add the page start
        if (!StringUtils.isEmpty(resourceModel.pageStart)) {
            work.addProperty(model.createProperty(BIBO_PAGE_START), resourceModel.pageStart);
        }

        // Add the page end
        if (!StringUtils.isEmpty(resourceModel.pageEnd)) {
            work.addProperty(model.createProperty(BIBO_PAGE_END), resourceModel.pageEnd);
        }

        // Add a page count
        if (!StringUtils.isEmpty(resourceModel.pageStart) && !StringUtils.isEmpty(resourceModel.pageEnd)) {
            try {
                int pageStart = Integer.parseInt(resourceModel.pageStart, 10);
                int pageEnd   = Integer.parseInt(resourceModel.pageEnd, 10);

                if (pageStart > 0) {
                    if (pageEnd > pageStart) {
                        work.addLiteral(model.createProperty(BIBO_PAGE_COUNT), pageEnd - pageStart);
                    } else if (pageEnd == pageStart) {
                        work.addLiteral(model.createProperty(BIBO_PAGE_COUNT), 1);
                    }
                }
            } catch (NumberFormatException nfe) {
            }
        }

        // Add the publication date
        addDateToResource(vreq, work, resourceModel.publicationDate);

        if (!StringUtils.isEmpty(resourceModel.abstractText)) {
            work.addProperty(model.createProperty(BIBO_ABSTRACT), resourceModel.abstractText);
        }

        // Add the authors
        // Note - we start by creating VCARDs for all of the authors
        // If the user has chosen an author position, this will be replaced later
        if (resourceModel.author != null) {
            int rank = 1;
            for (ResourceModel.NameField author : resourceModel.author) {
                if (author != null) {
                    Resource vcard = model.createResource(getVCardURI(vreq, author.family, author.given));
                    vcard.addProperty(RDF.type, model.getResource(VCARD_INDIVIDUAL));

                    Resource name = model.createResource(getUnusedUri(vreq));
                    vcard.addProperty(model.createProperty(VCARD_HAS_NAME), name);
                    name.addProperty(RDF.type, model.getResource(VCARD_NAME));
                    if (!StringUtils.isEmpty(author.given)) {
                        name.addProperty(model.createProperty(VCARD_GIVENNAME), author.given);
                    }
                    if (!StringUtils.isEmpty(author.family)) {
                        name.addProperty(model.createProperty(VCARD_FAMILYNAME), author.family);
                    }

                    Resource authorship = model.createResource(getUnusedUri(vreq));
                    authorship.addProperty(RDF.type, model.getResource(VIVO_AUTHORSHIP));

                    authorship.addProperty(model.createProperty(VIVO_RELATES), model.getResource(vivoUri));
                    authorship.addProperty(model.createProperty(VIVO_RELATES), model.getResource(vcard.getURI()));

                    model.getResource(vivoUri).addProperty(model.createProperty(VIVO_RELATEDBY), authorship);
                    vcard.addProperty(model.createProperty(VIVO_RELATEDBY), authorship);
                    authorship.addLiteral(model.createProperty(VIVO_RANK), rank);
                }
                rank++;
            }
        }

        // Add a URL
        if (!StringUtils.isEmpty(resourceModel.URL)) {
            try {
                URL url = new URL(resourceModel.URL);
                Resource urlModel = model.createResource(getUnusedUri(vreq));
                urlModel.addProperty(RDF.type, model.getResource(VCARD_URL_CLASS));
                urlModel.addLiteral(model.createProperty(VCARD_URL_PROPERTY), url);

                Resource kindModel = model.createResource(getUnusedUri(vreq));
                kindModel.addProperty(RDF.type, model.getResource(VCARD_KIND));
                kindModel.addProperty(model.createProperty(VCARD_HAS_URL), urlModel);
                kindModel.addProperty(model.createProperty(OBO_CONTACT_INFO_FOR), work);

                work.addProperty(model.createProperty(OBO_HAS_CONTACT_INFO), kindModel);
            } catch (MalformedURLException e) {
            }
        }

        // editor
        // translator
        // subject
        // status
        // presented at
        // keyword

        // http://purl.org/ontology/bibo/status
        // http://vivoweb.org/ontology/core#hasSubjectArea
        // http://purl.org/ontology/bibo/presentedAt
        // http://vivoweb.org/ontology/core#freetextKeyword
        // http://purl.org/ontology/bibo/translator (Direct to user)

        // http://vivoweb.org/ontology/core#Editorship

        return vivoUri;
    }

    /**
     * Get a URI for the publisher object
     *
     * @param vreq
     * @param publisher
     * @return
     */
    protected String getPublisherURI(VitroRequest vreq, String publisher) {
        return getUnusedUri(vreq);
    }

    /**
     * Get a URI for a VCARD object
     *
     * @param vreq
     * @param familyName
     * @param givenName
     * @return
     */
    protected String getVCardURI(VitroRequest vreq, String familyName, String givenName) {
        return getUnusedUri(vreq);
    }

    /**
     * Add a date object to the resource
     *
     * @param vreq
     * @param work
     * @param date
     * @return
     */
    protected boolean addDateToResource(VitroRequest vreq, Resource work, ResourceModel.DateField date) {
        Model model = work.getModel();

        if (date == null || date.year == null) {
            return false;
        }

        String formattedDate = null;
        String precision = null;

        if (date.month != null) {
            if (date.day != null) {
                formattedDate = String.format("%04d-%02d-%02dT00:00:00", date.year, date.month, date.day);
                precision = "http://vivoweb.org/ontology/core#dayPrecision";
            } else {
                formattedDate = String.format("%04d-%02d-01T00:00:00", date.year, date.month);
                precision = "http://vivoweb.org/ontology/core#monthPrecision";
            }
        } else {
            formattedDate = String.format("%04d-01-01T00:00:00", date.year);
            precision = "http://vivoweb.org/ontology/core#yearPrecision";
        }

        Resource dateResource = model.createResource(getUnusedUri(vreq)).addProperty(RDF.type, model.getResource("http://vivoweb.org/ontology/core#DateTimeValue"));
        dateResource.addProperty(model.createProperty(VIVO_DATETIME), formattedDate);
        dateResource.addProperty(model.createProperty(VIVO_DATETIMEPRECISION), precision);

        work.addProperty(model.createProperty(VIVO_DATETIMEVALUE), dateResource);
        return true;
    }

    /**
     * Get an unused standard Uri from VIVO
     *
     * @param vreq
     * @return
     */
    private String getUnusedUri(VitroRequest vreq) {
        NewURIMakerVitro uriMaker = new NewURIMakerVitro(vreq.getWebappDaoFactory());
        try {
            return uriMaker.getUnusedNewURI(null);
        } catch (InsertException e) {
        }

        return null;
    }

    /**
     * Find a Uri for an ISSN
     *
     * @param rdfService
     * @param issns
     * @return
     */
    private String findVIVOUriForISSNs(RDFService rdfService, String[] issns) {
        // First look to see if any journals already define the ISSN
        if (issns != null && issns.length > 0) {
            for (String issn : issns) {
                final List<String> journals = new ArrayList<>();
                String query = "SELECT ?journal\n" +
                        "WHERE\n" +
                        "{\n" +
                        "  {\n" +
                        "  \t?journal <http://purl.org/ontology/bibo/issn> \"" + issn + "\" .\n" +
                        "  }\n" +
                        "}\n";

                try {
                    rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                        @Override
                        protected void processQuerySolution(QuerySolution qs) {
                            Resource journal = qs.getResource("journal");
                            if (journal != null) {
                                journals.add(journal.getURI());
                            }
                        }
                    });
                } catch (RDFServiceException e) {
                }

                // We've found a journal that matches, so use that
                if (journals.size() > 0) {
                    return journals.get(0);
                }
            }
        }

        // No journal found, so return null
        return null;
    }

    /**
     * Find a Uri for an ISBN
     *
     * @param rdfService
     * @param isbns
     * @return
     */
    private String findVIVOUriForISBNs(RDFService rdfService, String[] isbns) {
        // First look to see if any journals already define the ISSN
        if (isbns != null && isbns.length > 0) {
            for (String isbn : isbns) {
                final List<String> books = new ArrayList<>();
                String query = "SELECT ?book\n" +
                        "WHERE\n" +
                        "{\n" +
                        "  {\n" +
                        (getDigitCount(isbn) == 10 ?
                        "  \t?book <http://purl.org/ontology/bibo/isbn10> \"" + isbn + "\" .\n" :
                        "  \t?book <http://purl.org/ontology/bibo/isbn13> \"" + isbn + "\" .\n" ) +
                        "  \t?book a <http://purl.org/ontology/bibo/Book> .\n" +
                        "  }\n" +
                        "}\n";

                try {
                    rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                        @Override
                        protected void processQuerySolution(QuerySolution qs) {
                            Resource book = qs.getResource("book");
                            if (book != null) {
                                books.add(book.getURI());
                            }
                        }
                    });
                } catch (RDFServiceException e) {
                }

                // We've found a book that matches, so use that
                if (books.size() > 0) {
                    for (String url : books) {
                        if (url.contains("doi")) {
                            return url;
                        }
                    }

                    return books.get(0);
                }
            }
        }

        // No books found, so return null
        return null;
    }

    /**
     * Find a Uri for an Publisher
     *
     * @param rdfService
     * @param publisher The name of the publisher
     * @return
     */
    private String findVIVOUriForPublisher(RDFService rdfService, String publisher) {
        // First look to see if any publishers already have that label
        final List<String> publisherUris = new ArrayList<>();
        String query = "SELECT ?publisher\n" +
                "WHERE\n" +
                "{\n" +
                "  {\n" +
                "  \t?publisher a <" + VIVO_PUBLISHER_CLASS + "> .\n" +
                "  \t?publisher <" + RDFS.label + "> \"" + publisher + "\" .\n" +
                "  }\n" +
                "}\n";

        try {
            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                @Override
                protected void processQuerySolution(QuerySolution qs) {
                    Resource publisherUri = qs.getResource("publisher");
                    if (publisherUri != null) {
                        publisherUris.add(publisherUri.getURI());
                    }
                }
            });
        } catch (RDFServiceException e) {
        }

        // We've found a publisher that matches, so use that
        if (publisherUris.size() > 0) {
            return publisherUris.get(0);
        }

        return null;
    }

    /**
     * Find a Uri for a DOI
     *
     * @param rdfService
     * @param doi
     * @return
     */
    private String findVIVOUriForDOI(RDFService rdfService, String doi) {
        // First, find a resource that already defines the DOI
        if (!StringUtils.isEmpty(doi)) {
            final List<String> works = new ArrayList<>();
            String query = "SELECT ?work\n" +
                    "WHERE\n" +
                    "{\n" +
                    "  {\n" +
                    "  \t?work <http://purl.org/ontology/bibo/doi> \"" + doi + "\" .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "  \t?work <http://purl.org/ontology/bibo/doi> \"http://doi.org/" + doi + "\" .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "  \t?work <http://purl.org/ontology/bibo/doi> \"https://doi.org/" + doi + "\" .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "  \t?work <http://purl.org/ontology/bibo/doi> \"http://dx.doi.org/" + doi + "\" .\n" +
                    "  }\n" +
                    "  UNION\n" +
                    "  {\n" +
                    "  \t?work <http://purl.org/ontology/bibo/doi> \"https://dx.doi.org/" + doi + "\" .\n" +
                    "  }\n" +
                    "}\n";

            try {
                rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                    @Override
                    protected void processQuerySolution(QuerySolution qs) {
                        Resource work = qs.getResource("work");
                        if (work != null) {
                            works.add(work.getURI());
                        }
                    }
                });
            } catch (RDFServiceException e) {
            }

            // We've found a resource, so return it's Uri
            if (works.size() > 0) {
                return works.get(0);
            }
        }

        // No resource found with the DOI, so return null
        return null;
    }

    /**
     * Count the number of digits in a string
     *
     * @param id
     * @return
     */
    private int getDigitCount(String id) {
        int digits = 0;

        if (id != null) {
            for (char ch : id.toCharArray()) {
                if (Character.isDigit(ch)) {
                    digits++;
                }
            }
        }

        return digits;
    }

    /**
     * Find a Uri for a resource that defines a PubMed ID
     * @param rdfService
     * @param pmid
     * @return
     */
    private String findVIVOUriForPubMedID(RDFService rdfService, String pmid) {
        // Look for a resource that defines the PubMed ID
        if (!StringUtils.isEmpty(pmid)) {
            final List<String> works = new ArrayList<>();
            String query = "SELECT ?work\n" +
                    "WHERE\n" +
                    "{\n" +
                    "  {\n" +
                    "  \t?work <http://purl.org/ontology/bibo/pmid> \"" + pmid + "\" .\n" +
                    "  }\n" +
                    "}\n";

            try {
                rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                    @Override
                    protected void processQuerySolution(QuerySolution qs) {
                        Resource work = qs.getResource("work");
                        if (work != null) {
                            works.add(work.getURI());
                        }
                    }
                });
            } catch (RDFServiceException e) {
            }

            // If we have a resource, return the Uri
            if (works.size() == 1) {
                return works.get(0);
            }
        }

        // No resource found, so return null
        return null;
    }

    /**
     * Try to find a resource in VIVO that defines one of the external identifiers
     * @param vreq
     * @param ids
     * @param profileUri
     * @param citation
     * @return
     */
    protected String findInVIVO(VitroRequest vreq, ExternalIdentifiers ids, String profileUri, Citation citation) {
        // First, look for a resource that defines the DOI
        String vivoUri = findVIVOUriForDOI(vreq.getRDFService(), ids.DOI);

        // No DOI, so look for a resource that defines the PubMed ID
        if (StringUtils.isEmpty(vivoUri)) {
            vivoUri = findVIVOUriForPubMedID(vreq.getRDFService(), ids.PubMedID);
        }

        // If we have been passed a citation object, and have found a resource, populate the citation object
        if (citation != null && !StringUtils.isEmpty(vivoUri)) {
            // Get a moel for the resource
            Model model = getExistingResource(vreq, vivoUri);

            // Get the resource from the model
            Resource work = model.getResource(vivoUri);
            String pageStart = null;
            String pageEnd = null;
            Citation.Name[] rankedAuthors = null;
            ArrayList<Citation.Name> unrankedAuthors = new ArrayList<>();

            // Loop through all the statements on the resource
            StmtIterator stmtIterator = work.listProperties();
            try {
                while (stmtIterator.hasNext()) {
                    Statement stmt = stmtIterator.next();

                    switch (stmt.getPredicate().getURI()) {
                        case RDFS_LABEL:
                            citation.title = stmt.getString();
                            break;

                        case BIBO_VOLUME:
                            citation.volume = stmt.getString();
                            break;

                        case BIBO_ISSUE:
                            citation.issue = stmt.getString();
                            break;

                        case BIBO_PAGE_START:
                            pageStart = stmt.getString();
                            break;

                        case BIBO_PAGE_END:
                            pageEnd = stmt.getString();
                            break;

                        // Publication date
                        case VIVO_DATETIMEVALUE:
                            Resource dateTime = stmt.getResource();
                            if (dateTime != null) {
                                Statement stmtDate = dateTime.getProperty(model.getProperty(VIVO_DATETIME));
                                if (stmtDate != null) {
                                    String dateTimeValue = stmtDate.getString();
                                    if (dateTimeValue != null && dateTimeValue.length() > 3) {
                                        citation.publicationYear = Integer.parseInt(dateTimeValue.substring(0, 4), 10);
                                    }
                                }
                            }
                            break;

                        // Journal
                        case VIVO_HASPUBLICATIONVENUE:
                            Resource journal = stmt.getResource();
                            if (journal != null) {
                                Statement stmtJournalName = journal.getProperty(RDFS.label);
                                if (stmtJournalName != null) {
                                    citation.journal = stmtJournalName.getString();
                                }
                            }
                            break;

                        // Relationships - we are really interested in authors and editors
                        case VIVO_RELATEDBY:
                            // Get the relationship context
                            Resource relationship = stmt.getResource();
                            if (relationship != null) {
                                Integer rank = null;

                                // If it isn't an authorship or editorship, skip it
                                if (!isResourceOfType(relationship, VIVO_AUTHORSHIP) &&
                                    !isResourceOfType(relationship, VIVO_EDITORSHIP)) {
                                    break;
                                }

                                // Now loop over the properties of the author/editorship context
                                Resource personResource = null;
                                StmtIterator relationshipIter = relationship.listProperties();
                                try {
                                    while (relationshipIter.hasNext()) {
                                        Statement relationshipStmt = relationshipIter.next();
                                        switch (relationshipStmt.getPredicate().getURI()) {
                                            // If it is a relates property
                                            case VIVO_RELATES:
                                                // If it isn't pointing to the resource, it must be pointing to a person
                                                if (!vivoUri.equals(relationshipStmt.getResource().getURI())) {
                                                    personResource = relationshipStmt.getResource();
                                                }
                                                break;

                                            // Author position
                                            case VIVO_RANK:
                                                rank = relationshipStmt.getInt();
                                                break;
                                        }
                                    }
                                } finally {
                                    relationshipIter.close();
                                }

                                Citation.Name newAuthor = null;

                                // If we've got an author
                                if (personResource != null) {
                                    // If the author is the user, then they have already claimed this publication
                                    if (profileUri.equals(personResource.getURI())) {
                                        citation.alreadyClaimed = true;
                                    }

                                    if (isResourceOfType(relationship, VIVO_AUTHORSHIP)) {
                                        boolean linked = false;

                                        // Now get the name of the author, from either the VCARD or the foaf:Person
                                        Statement familyName = null;
                                        Statement givenName = null;
                                        if (isResourceOfType(personResource, VCARD_INDIVIDUAL)) {
                                            if (personResource.hasProperty(model.getProperty(VCARD_HAS_NAME))) {
                                                Resource vcardName = personResource.getPropertyResourceValue(model.getProperty(VCARD_HAS_NAME));
                                                if (vcardName != null) {
                                                    if (vcardName.hasProperty(model.getProperty(VCARD_GIVENNAME))) {
                                                        givenName = vcardName.getProperty(model.getProperty(VCARD_GIVENNAME));
                                                    }
                                                    if (vcardName.hasProperty(model.getProperty(VCARD_FAMILYNAME))) {
                                                        familyName = vcardName.getProperty(model.getProperty(VCARD_FAMILYNAME));
                                                    }
                                                }
                                            }
                                        } else if (personResource.hasProperty(model.getProperty(OBO_HAS_CONTACT_INFO))) {
                                            Resource vCard = personResource.getPropertyResourceValue(model.getProperty(OBO_HAS_CONTACT_INFO));
                                            if (vCard.hasProperty(model.getProperty(VCARD_HAS_NAME))) {
                                                Resource vcardName = vCard.getPropertyResourceValue(model.getProperty(VCARD_HAS_NAME));
                                                if (vcardName != null) {
                                                    if (vcardName.hasProperty(model.getProperty(VCARD_GIVENNAME))) {
                                                        givenName = vcardName.getProperty(model.getProperty(VCARD_GIVENNAME));
                                                    }
                                                    if (vcardName.hasProperty(model.getProperty(VCARD_FAMILYNAME))) {
                                                        familyName = vcardName.getProperty(model.getProperty(VCARD_FAMILYNAME));
                                                    }
                                                }
                                                linked = true;
                                            }
                                        }

                                        if (givenName == null) {
                                            // It's a foaf person, which means it is already linked to a full profile in VIVO
                                            if (personResource.hasProperty(model.getProperty(FOAF_FIRSTNAME))) {
                                                givenName = personResource.getProperty(model.getProperty(FOAF_FIRSTNAME));
                                            }
                                            if (personResource.hasProperty(model.getProperty(FOAF_LASTNAME))) {
                                                familyName = personResource.getProperty(model.getProperty(FOAF_LASTNAME));
                                            }
                                            linked = true;
                                        }

                                        // If we have an author name, format it
                                        if (familyName != null) {
                                            newAuthor = new Citation.Name();
                                            if (givenName != null) {
                                                newAuthor.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), givenName.getString());
                                            } else {
                                                newAuthor.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), null);
                                            }

                                            // Record whether the author is a full profile, or just a VCARD
                                            newAuthor.linked = linked;
                                        } else {
                                            Statement label = personResource.getProperty(RDFS.label);
                                            if (label != null) {
                                                String name = label.getString();
                                                if (name.contains(",")) {
                                                    String[] parts = name.split("\\s*,\\s*");
                                                    if (parts.length > 1) {
                                                        name = CreateAndLinkUtils.formatAuthorString(parts[0], parts[parts.length - 1]);
                                                    }
                                                } else {
                                                    String[] parts = name.split("\\s*");
                                                    if (parts.length > 1) {
                                                        name = CreateAndLinkUtils.formatAuthorString(parts[parts.length - 1], parts[0]);
                                                    }
                                                }

                                                newAuthor = new Citation.Name();
                                                newAuthor.name = name;
                                                newAuthor.linked = linked;
                                            }
                                        }
                                    }
                                } else {
                                    if (isResourceOfType(relationship, VIVO_AUTHORSHIP)) {
                                        newAuthor = new Citation.Name();
                                        newAuthor.name = "Deleted Author";
                                    }
                                }

                                // If we have an author
                                if (newAuthor != null) {
                                    // If we have an author position, insert it in the correct place of the ranked authors
                                    if (rank != null) {
                                        if (rankedAuthors == null) {
                                            rankedAuthors = new Citation.Name[rank];
                                        } else if (rankedAuthors.length < rank) {
                                            Citation.Name[] newAuthors = new Citation.Name[rank];
                                            for (int i = 0; i < rankedAuthors.length; i++) {
                                                newAuthors[i] = rankedAuthors[i];
                                            }
                                            rankedAuthors = newAuthors;
                                        }
                                        rankedAuthors[rank - 1] = newAuthor;
                                    } else {
                                        // Unranked author, so just keep hold of it to add at the end
                                        unrankedAuthors.add(newAuthor);
                                    }
                                }
                            }
                            break;
                    }
                }
            } finally {
                stmtIterator.close();
            }

            // Create the pagination field
            if (!StringUtils.isEmpty(pageStart)) {
                if (!StringUtils.isEmpty(pageEnd)) {
                    citation.pagination = pageStart + "-" + pageEnd;
                } else {
                    citation.pagination = pageStart;
                }
            }

            // If we have unranked authors, add them to the end of the ranked authors
            if (unrankedAuthors.size() > 0) {
                if (rankedAuthors == null) {
                    citation.authors = unrankedAuthors.toArray(new Citation.Name[unrankedAuthors.size()]);
                } else {
                    Citation.Name[] newAuthors = new Citation.Name[rankedAuthors.length + unrankedAuthors.size()];
                    int i = 0;
                    while (i < rankedAuthors.length) {
                        newAuthors[i] = rankedAuthors[i];
                        i++;
                    }
                    while (i < newAuthors.length && unrankedAuthors.size() > 0) {
                        newAuthors[i] = unrankedAuthors.remove(0);
                        i++;
                    }
                    citation.authors = newAuthors;
                }
            } else {
                citation.authors = rankedAuthors;
            }
        }

        // Return the uri of the resource (or null)
        return vivoUri;
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
        try {
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                if (typeUri.equals(stmt.getResource().getURI())) {
                    return true;
                }
            }
        } finally {
            iter.close();
        }

        return false;
    }

    /**
     * Determine the difference between the "existing" and "updated" models, and write the changes to VIVO
     *
     * @param rdfService
     * @param existingModel
     * @param updatedModel
     */
    protected void writeChanges(RDFService rdfService, Model existingModel, Model updatedModel) {
        Model removeModel = existingModel.difference(updatedModel);
        Model addModel = updatedModel.difference(existingModel);

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
            } finally {
                if (addStream != null) {
                    try { addStream.close(); } catch (IOException e) { }
                }

                if (removeStream != null) {
                    try { removeStream.close(); } catch (IOException e) { }
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
     * Get a list of publication types - classes that are a bibo:Document - from the triple store
     * @param vreq
     * @return
     */
    private List<PublicationType> getPublicationTypes(VitroRequest vreq) {
        // List of publication types to return (final so it can be used in the ResultSetConsumer
        final List<PublicationType> types = new ArrayList<>();

        try {
            // Find all classes that are a subclass of Document, and their labels
            String query = "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "\n" +
                    "SELECT ?uri ?label\n" +
                    "WHERE {\n" +
                    "\t?uri rdfs:label ?label .\n" +
                    "\t?uri rdfs:subClassOf  <http://purl.org/ontology/bibo/Document> .\n" +
                    "}\n";

            vreq.getRDFService().sparqlSelectQuery(query, new ResultSetConsumer() {
                @Override
                protected void processQuerySolution(QuerySolution qs) {
                    // Add a publication type to the list
                    types.add(new PublicationType(
                        qs.getLiteral("label").getString(),
                        qs.getResource("uri").getURI()
                    ));
                }
            });
        } catch (RDFServiceException e) {
        }

        // Sort the publication type list
        types.sort(new Comparator<PublicationType>() {
            @Override
            public int compare(PublicationType o1, PublicationType o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });

        return types;
    }

    public class PublicationType {
        private String label;
        private String uri;

        public PublicationType(String label, String uri) {
            this.label = label;
            this.uri = uri;
        }

        public String getLabel() {
            return label;
        }

        public String getUri() {
            return uri;
        }
    }
}


