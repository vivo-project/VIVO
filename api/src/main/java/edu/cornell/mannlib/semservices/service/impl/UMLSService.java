/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.semservices.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.exceptions.ConceptsNotFoundException;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.vitro.webapp.utils.json.JacksonUtils;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.util.StringUtils;

/**
 * @author jaf30
 *
 */
public class UMLSService implements ExternalConceptService {
    protected final Log logger = LogFactory.getLog(getClass());

    private static String UTS_REST_API_URL = "https://uts-ws.nlm.nih.gov/rest";
    private static String SEARCH_PATH = "/search/current";
    private static String SEARCH_PARAMETER = "string";
    private static String SEARCH_TYPE_PARAMETER = "searchType";
    private static String SEARCH_TYPE = "rightTruncation";
    private static String PAGE_SIZE_PARAMETER = "pageSize";
    private static String RETURN_TYPE_PARAMETER = "returnIdType";
    private static String RETURN_TYPE = "concept";
    private static String TICKET_PARAMETER = "ticket";

    private static String ticketGrantingTicketURL = null;

    private static long lastUpdate = -1;

    private static String username = null;
    private static String password = null;
    private static String apikey = null;

    private static String pageSize = "50";

    private static String UMLS_AUTH_USER_URL = "https://utslogin.nlm.nih.gov/cas/v1/tickets";
    private static String UMLS_AUTH_KEY_URL = "https://utslogin.nlm.nih.gov/cas/v1/api-key";
    private static String UTS_SERVICE_URL   = "http://umlsks.nlm.nih.gov";

    {
        if (username == null || apikey == null) {
            final Properties properties = new Properties();
            try (InputStream stream = getClass().getResourceAsStream("/umls.properties")) {
                properties.load(stream);
                username = properties.getProperty("username");
                password = properties.getProperty("password");
                apikey   = properties.getProperty("apikey");

                String exPageSize = properties.getProperty("pagesize");
                try {
                    if (!StringUtils.isEmpty(exPageSize)) {
                        int iPageSize = Integer.parseInt(exPageSize, 10);
                        if (iPageSize > 5 && iPageSize < 200) {
                            pageSize = Integer.toString(iPageSize, 10);
                        }
                    }
                } catch (Exception e) {
                }
            } catch (IOException e) {
            }
        }
    }

    public boolean isConfigured() {
        return !(StringUtils.isEmpty(username) && StringUtils.isEmpty(apikey));
    }

    @Override
    public List<Concept> getConcepts(String term) throws Exception {
        String ticket = getSingleUseTicket();

        List<Concept> conceptList = new ArrayList<Concept>();

        String results = null;

        try {
            URIBuilder b = new URIBuilder(UTS_REST_API_URL + SEARCH_PATH);
            b.addParameter(SEARCH_PARAMETER, term);
            b.addParameter(RETURN_TYPE_PARAMETER, RETURN_TYPE);
            b.addParameter(SEARCH_TYPE_PARAMETER, SEARCH_TYPE);
            b.addParameter(PAGE_SIZE_PARAMETER, pageSize);
            b.addParameter(TICKET_PARAMETER, ticket);

            results = Request.Get(b.build())
                    .connectTimeout(3000)
                    .socketTimeout(3000)
                    .execute().returnContent().asString();

            conceptList = processOutput(results);
            return conceptList;

        } catch (Exception ex) {
            logger.error("error occurred in servlet", ex);
            return null;
        }
    }

    public List<Concept> processResults(String term) throws Exception {
        return getConcepts(term);
    }

    /**
     * @param uri URI
     */
    public List<Concept> getConceptsByURIWithSparql(String uri) throws Exception {
        // deprecating this method...just return an empty list
        List<Concept> conceptList = new ArrayList<Concept>();
        return conceptList;
    }

    /**
     * @param results Results to process
     */
    private List<Concept> processOutput(String results) throws Exception {
        List<Concept> conceptList = new ArrayList<Concept>();
        List<String> bestMatchIdList = new ArrayList<String>();
        String bestMatchId = "";

        try {
            ObjectNode json = (ObjectNode) JacksonUtils.parseJson(results);
            ArrayNode allArray = (ArrayNode) json.get("result").get("results");
            int len = allArray.size();
            int i;
            for (i = 0; i < len; i++) {
                ObjectNode o = (ObjectNode) allArray.get(i);

                Concept concept = new Concept();
                concept.setDefinedBy(UTS_SERVICE_URL);
                concept.setSchemeURI(UTS_SERVICE_URL);

                concept.setType(RETURN_TYPE);
                concept.setConceptId(getJsonValue(o, "ui"));
                concept.setLabel(getJsonValue(o, "name"));
                concept.setUri(getJsonValue(o, "uri"));

                concept.setBestMatch("false");
                conceptList.add(concept);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Could not get concepts", ex);
            throw ex;
        }

        if (conceptList.size() == 0) {
            throw new ConceptsNotFoundException();
        }

        //
        return conceptList;

    }

    /**
     * Get a string from a json object or an empty string if there is no value for the given key
     *
     * @param obj JSON Object
     * @param key Key to retrieve
     */
    protected String getJsonValue(ObjectNode obj, String key) {
        if (obj.has(key)) {
            return obj.get(key).asText();
        } else {
            return "";
        }
    }


    protected String stripConceptId(String uri) {
        String conceptId = "";
        int lastslash = uri.lastIndexOf('/');
        conceptId = uri.substring(lastslash + 1, uri.length());
        return conceptId;
    }

    private synchronized void getTicketGrantingTicket() {
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(apikey)) {
            throw new IllegalStateException("Unable to read umls.properties");
        }

        if (ticketGrantingTicketURL == null || lastUpdate + 28700000L < System.currentTimeMillis()) {
            try {
                if (!StringUtils.isEmpty(apikey)) {
                    ticketGrantingTicketURL = Request.Post(UMLS_AUTH_KEY_URL).useExpectContinue().version(HttpVersion.HTTP_1_1)
                            .bodyForm(Form.form().add("apikey", apikey).build())
                            .execute().returnResponse().getFirstHeader("location").getValue();
                } else {
                    ticketGrantingTicketURL = Request.Post(UMLS_AUTH_USER_URL).useExpectContinue().version(HttpVersion.HTTP_1_1)
                            .bodyForm(Form.form().add("username", username).add("password", password).build())
                            .execute().returnResponse().getFirstHeader("location").getValue();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to get ticket granting ticket.");
            }
            lastUpdate = System.currentTimeMillis();
        }
    }

    private String getSingleUseTicket() {
        getTicketGrantingTicket();
        String ticket = "";
        try {
            ticket = Request.Post(ticketGrantingTicketURL).useExpectContinue().version(HttpVersion.HTTP_1_1)
                    .bodyForm(Form.form().add("service", UTS_SERVICE_URL).build())
                    .execute().returnContent().asString();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get ticket.");
        }
        return ticket;
    }
}
