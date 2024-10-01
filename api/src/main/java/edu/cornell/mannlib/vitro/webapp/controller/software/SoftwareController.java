package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.cornell.mannlib.vitro.webapp.application.ApplicationUtils;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.api.sparqlquery.InvalidQueryTypeException;
import edu.cornell.mannlib.vitro.webapp.controller.api.sparqlquery.SparqlQueryApiExecutor;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.dao.jena.RDFServiceDataset;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexer;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.GraphStoreFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

@WebServlet(name = "softwareController", urlPatterns = {"/software", "/software/*"}, loadOnStartup = 5)
public class SoftwareController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(SoftwareController.class);

    private static final String CREATED_GRAPH_BASE_URI = "http://vitro.mannlib.cornell.edu/a/graph/";

    private final String FIND_ALL_QUERY =
        "PREFIX rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:     <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd:      <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX owl:      <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX swrl:     <http://www.w3.org/2003/11/swrl#>\n" +
            "PREFIX swrlb:    <http://www.w3.org/2003/11/swrlb#>\n" +
            "PREFIX vitro:    <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#>\n" +
            "PREFIX bibo:     <http://purl.org/ontology/bibo/>\n" +
            "PREFIX c4o:      <http://purl.org/spar/c4o/>\n" +
            "PREFIX cito:     <http://purl.org/spar/cito/>\n" +
            "PREFIX dcterms:  <http://purl.org/dc/terms/>\n" +
            "PREFIX event:    <http://purl.org/NET/c4dm/event.owl#>\n" +
            "PREFIX fabio:    <http://purl.org/spar/fabio/>\n" +
            "PREFIX foaf:     <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX geo:      <http://aims.fao.org/aos/geopolitical.owl#>\n" +
            "PREFIX obo:      <http://purl.obolibrary.org/obo/>\n" +
            "PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n" +
            "PREFIX vcard:    <http://www.w3.org/2006/vcard/ns#>\n" +
            "\n" +
            "SELECT ?software ?label ?author ?authorType ?authorIdentifier ?datePublished ?funding ?funder ?keywords " +
            "?version ?abstract ?identifier ?doi ?sameAs ?url ?funderType\n" +
            "WHERE\n" +
            "{\n" +
            "    ?software rdf:type obo:ERO_0000071\n" +
            "    OPTIONAL { ?software rdfs:label ?label }\n" +
            "    OPTIONAL { ?software vivo:relatedBy ?relatedObject .\n" +
            "        ?relatedObject vitro:mostSpecificType vivo:Authorship .\n" +
            "        OPTIONAL { ?relatedObject vivo:relates ?authorObject .\n" +
            "            OPTIONAL { ?authorObject rdfs:label ?author }\n" +
            "            OPTIONAL { ?authorObject vitro:mostSpecificType ?authorType }\n" +
            "            OPTIONAL { ?authorObject vivo:orcidId ?authorIdentifier }\n" +
            "        }\n" +
            "        FILTER (?author != ?label)\n" +
            "    }\n" +
            "    OPTIONAL { ?software vivo:dateTimeValue ?dateObject .\n" +
            "        OPTIONAL { ?dateObject vivo:dateTime ?datePublished }\n" +
            "    }\n" +
            "    OPTIONAL { ?software vivo:informationResourceSupportedBy ?fundingObject .\n" +
            "      OPTIONAL { ?fundingObject vivo:assignedBy ?funderObject ." +
            "        OPTIONAL { ?funderObject rdfs:label ?funder }\n" +
            "        OPTIONAL { ?funderObject vitro:mostSpecificType ?funderType }\n" +
            "    }\n" +
            "      OPTIONAL { ?fundingObject rdfs:label ?funding }\n" +
            "    }\n" +
            "    OPTIONAL { ?software vivo:freetextKeyword ?keywords }\n" +
            "    OPTIONAL { ?software obo:ERO_0000072 ?version }\n" +
            "    OPTIONAL { ?software bibo:abstract ?abstract }\n" +
            "    OPTIONAL { ?software obo:BFO_0000050 ?isPartOf }\n" +
            "    OPTIONAL { ?software obo:BFO_0000050 ?hasPart }\n" +
            "    OPTIONAL { ?software vivo:swhid ?identifier }\n" +
            "    OPTIONAL { ?software bibo:doi ?doi }\n" +
            "    OPTIONAL { ?software owl:sameAs ?sameAsObject .\n" +
            "        OPTIONAL { ?sameAsObject rdfs:label ?sameAs }\n" +
            "    }\n" +
            "    OPTIONAL { ?software obo:ARG_2000028 ?contactInfo .\n" +
            "        OPTIONAL { ?contactInfo vcard:hasURL ?url }\n" +
            "    }\n" +
            "}\n";

    private final String FIND_BY_ID_QUERY_TEMPLATE =
        "PREFIX rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:     <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd:      <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX owl:      <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX swrl:     <http://www.w3.org/2003/11/swrl#>\n" +
            "PREFIX swrlb:    <http://www.w3.org/2003/11/swrlb#>\n" +
            "PREFIX vitro:    <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#>\n" +
            "PREFIX bibo:     <http://purl.org/ontology/bibo/>\n" +
            "PREFIX c4o:      <http://purl.org/spar/c4o/>\n" +
            "PREFIX cito:     <http://purl.org/spar/cito/>\n" +
            "PREFIX dcterms:  <http://purl.org/dc/terms/>\n" +
            "PREFIX event:    <http://purl.org/NET/c4dm/event.owl#>\n" +
            "PREFIX fabio:    <http://purl.org/spar/fabio/>\n" +
            "PREFIX foaf:     <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX geo:      <http://aims.fao.org/aos/geopolitical.owl#>\n" +
            "PREFIX obo:      <http://purl.obolibrary.org/obo/>\n" +
            "PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n" +
            "PREFIX vcard:    <http://www.w3.org/2006/vcard/ns#>\n" +
            "\n" +
            "SELECT ?software ?label ?author ?authorType ?authorIdentifier ?datePublished ?funding ?funder ?keywords " +
            "?version ?abstract ?identifier ?doi ?sameAs ?url ?funderType\n" +
            "WHERE\n" +
            "{\n" +
            "    BIND (<%s> AS ?software)\n" +
            "    ?software rdf:type obo:ERO_0000071\n" +
            "    OPTIONAL { ?software rdfs:label ?label }\n" +
            "    OPTIONAL { ?software vivo:relatedBy ?relatedObject .\n" +
            "        ?relatedObject vitro:mostSpecificType vivo:Authorship .\n" +
            "        OPTIONAL { ?relatedObject vivo:relates ?authorObject .\n" +
            "            OPTIONAL { ?authorObject rdfs:label ?author }\n" +
            "            OPTIONAL { ?authorObject vitro:mostSpecificType ?authorType }\n" +
            "            OPTIONAL { ?authorObject vivo:orcidId ?authorIdentifier }\n" +
            "        }\n" +
            "        FILTER (?author != ?label)\n" +
            "    }\n" +
            "    OPTIONAL { ?software vivo:dateTimeValue ?dateObject .\n" +
            "        OPTIONAL { ?dateObject vivo:dateTime ?datePublished }\n" +
            "    }\n" +
            "    OPTIONAL { ?software vivo:informationResourceSupportedBy ?fundingObject .\n" +
            "      OPTIONAL { ?fundingObject vivo:assignedBy ?funderObject ." +
            "        OPTIONAL { ?funderObject rdfs:label ?funder }\n" +
            "        OPTIONAL { ?funderObject vitro:mostSpecificType ?funderType }\n" +
            "    }\n" +
            "      OPTIONAL { ?fundingObject rdfs:label ?funding }\n" +
            "    }\n" +
            "    OPTIONAL { ?software vivo:freetextKeyword ?keywords }\n" +
            "    OPTIONAL { ?software obo:ERO_0000072 ?version }\n" +
            "    OPTIONAL { ?software bibo:abstract ?abstract }\n" +
            "    OPTIONAL { ?software obo:BFO_0000050 ?isPartOf }\n" +
            "    OPTIONAL { ?software obo:BFO_0000050 ?hasPart }\n" +
            "    OPTIONAL { ?software vivo:swhid ?identifier }\n" +
            "    OPTIONAL { ?software bibo:doi ?doi }\n" +
            "    OPTIONAL { ?software owl:sameAs ?sameAsObject .\n" +
            "        OPTIONAL { ?sameAsObject rdfs:label ?sameAs }\n" +
            "    }\n" +
            "    OPTIONAL { ?software obo:ARG_2000028 ?contactInfo .\n" +
            "        OPTIONAL { ?contactInfo vcard:hasURL ?url }\n" +
            "    }\n" +
            "}\n";


    private static List<Map<String, String>> parseBindings(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        JsonNode bindingsNode = rootNode.path("results").path("bindings");

        List<Map<String, String>> recordsList = new ArrayList<>();

        for (JsonNode bindingNode : bindingsNode) {
            Map<String, String> recordMap = new HashMap<>();

            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = bindingNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIterator.next();

                String fieldName = field.getKey();
                String fieldValue = field.getValue().path("value").asText();

                recordMap.put(fieldName, fieldValue);
            }

            recordsList.add(recordMap);
        }

        return recordsList;
    }

    private String serializeToJSON(Object serializationObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper.writeValueAsString(serializationObject);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String queryString = (pathInfo != null && pathInfo.length() > 1)
            ? String.format(FIND_BY_ID_QUERY_TEMPLATE, buildSoftwareUri(pathInfo.substring(1)))
            : FIND_ALL_QUERY;

        RDFService rdfService = ModelAccess.on(getServletContext()).getRDFService();

        try {
            SparqlQueryApiExecutor core =
                SparqlQueryApiExecutor.instance(rdfService, queryString, "application/sparql-results+json");
            handleResponseContentType(req, resp);

            if (isJsonRequest(req)) {
                List<SoftwareResponseDTO> response = handleDTOConversion(core);
                resp.getWriter().println(serializeToJSON(response.size() == 1 ? response.get(0) : response));
            } else {
                core.executeAndFormat(resp.getOutputStream());
            }
        } catch (Exception e) {
            handleException(e, queryString, resp);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAuthorizedToDisplayPage(req, resp, requiredActions(new VitroRequest(req)))) {
            return;
        }

        executeWithTransaction(req, resp, (graphStore, softwareUri) -> {

            SoftwareRequestDTO softwareDTO = null;
            try {
                softwareDTO = parseRequestBody(req, SoftwareRequestDTO.class);
            } catch (IOException e) {
                try {
                    do400BadRequest("Error while parsing request body.", resp);
                } catch (IOException ex) {
                    log.error("Error while handling exception.", ex);
                }
            }

            if (Objects.isNull(softwareDTO)) {
                return;
            }

            StringBuilder insertSoftwareQuery = buildInsertSoftwareQuery(softwareDTO, softwareUri, ModelAccess.on(
                new VitroRequest(req)).getOntModel());
            executeUpdate(graphStore, insertSoftwareQuery.toString());

            softwareDTO.internalIdentifier = softwareUri;
            try {
                resp.setContentType("application/json");
                resp.getWriter().println(serializeToJSON(softwareDTO));
            } catch (IOException e) {
                try {
                    do400BadRequest("Error while writing response body.", resp);
                } catch (IOException ex) {
                    log.error("Error while handling exception.", ex);
                }
            }
            resp.setStatus(HttpServletResponse.SC_CREATED);
        });
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAuthorizedToDisplayPage(req, resp, requiredActions(new VitroRequest(req)))) {
            return;
        }

        String pathInfo = req.getPathInfo();
        if (Objects.isNull(pathInfo) || pathInfo.isEmpty()) {
            do400BadRequest("You have to provide a record identifier.", resp);
            return;
        }

        VitroRequest vreq = new VitroRequest(req);
        ApplicationBean appBean = vreq.getAppBean();
        String appName = appBean.getApplicationName().toLowerCase();
        URL url = new URL("http://" + req.getServerName() + ":" + req.getServerPort() + "/" + appName +
            "/deleteIndividualController?individualUri=" +
            URLEncoder.encode(buildSoftwareUri(pathInfo.substring(1))) + "&redirectUrl=%2F");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        addCookiesToRequest(req, connection);

        connection.getResponseCode();

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        connection.disconnect();
    }

    private void addCookiesToRequest(HttpServletRequest req, HttpURLConnection connection) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            StringBuilder cookieHeader = new StringBuilder();
            for (Cookie cookie : cookies) {
                cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
            }
            // Remove the trailing "; " at the end of the cookie string
            if (cookieHeader.length() > 0) {
                cookieHeader.setLength(cookieHeader.length() - 2);
            }
            connection.setRequestProperty("Cookie", cookieHeader.toString());
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAuthorizedToDisplayPage(req, resp, requiredActions(new VitroRequest(req)))) {
            return;
        }

        doDelete(req, resp);

        executeWithTransaction(req, resp, (graphStore, softwareUri) -> {
            SoftwareRequestDTO softwareDTO;
            try {
                softwareDTO = parseRequestBody(req, SoftwareRequestDTO.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            StringBuilder insertSoftwareQuery = buildInsertSoftwareQuery(softwareDTO, softwareUri, ModelAccess.on(
                new VitroRequest(req)).getOntModel());
            executeUpdate(graphStore, insertSoftwareQuery.toString());

            softwareDTO.internalIdentifier = softwareUri;
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        });
    }

    private String buildSoftwareUri(String softwareId) {
        String defaultNamespace =
            Objects.requireNonNull(ConfigurationProperties.getInstance().getProperty("Vitro.defaultNamespace"));
        return defaultNamespace + StringEscapeUtils.escapeJava(softwareId);
    }

    private void handleResponseContentType(HttpServletRequest req, HttpServletResponse resp) {
        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && !acceptHeader.isEmpty()) {
            resp.setContentType(acceptHeader);
        } else {
            resp.setContentType("application/json");
        }
    }

    private boolean isJsonRequest(HttpServletRequest req) {
        String acceptHeader = req.getHeader("Accept");
        return acceptHeader != null && acceptHeader.equals("application/json");
    }

    private <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(req.getInputStream(), clazz);
    }

    private StringBuilder buildInsertSoftwareQuery(SoftwareRequestDTO softwareDTO, String softwareUri,
                                                   OntModel ontModel) {
        StringBuilder insertSoftwareQuery = new StringBuilder();
        addPrefixClauses(insertSoftwareQuery);
        insertSoftwareQuery.append("\n")
            .append("INSERT DATA\n")
            .append("{\n")
            .append("GRAPH ").append("<" + CREATED_GRAPH_BASE_URI + ">")
            .append("\n")
            .append("{\n");

        String defaultNamespace = ConfigurationProperties.getInstance().getProperty("Vitro.defaultNamespace");
        addSoftwareRelatedFields(insertSoftwareQuery, softwareDTO, softwareUri);
        addPublicationDate(insertSoftwareQuery, softwareDTO.datePublished, defaultNamespace, softwareUri);
        addAuthors(insertSoftwareQuery, softwareDTO.authors, defaultNamespace, softwareUri, ontModel);
        addFunding(insertSoftwareQuery, softwareDTO.fundings, defaultNamespace, softwareUri);
        addFunders(insertSoftwareQuery, softwareDTO.funders, defaultNamespace, softwareUri);

        insertSoftwareQuery.append("    }\n").append("}\n");

        return insertSoftwareQuery;
    }

    private void executeUpdate(GraphStore graphStore, String query) {
        UpdateRequest updateRequest = UpdateFactory.create(query);
        UpdateAction.execute(updateRequest, graphStore);
    }

    private void handleException(Exception e, String queryString, HttpServletResponse resp) throws IOException {
        if (e instanceof InvalidQueryTypeException) {
            do400BadRequest("Invalid query type: '" + queryString + "'", resp);
        } else if (e instanceof QueryParseException) {
            do400BadRequest("Failed to parse query: '" + queryString + "'", resp);
        } else if (e instanceof RDFServiceException) {
            do500InternalServerError("Problem executing the query.", e, resp);
        }
    }

    private void executeWithTransaction(HttpServletRequest req, HttpServletResponse resp,
                                        BiConsumer<GraphStore, String> action) throws IOException {
        VitroRequest vreq = new VitroRequest(req);
        SearchIndexer indexer = ApplicationUtils.instance().getSearchIndexer();
        Dataset ds = new RDFServiceDataset(vreq.getUnfilteredRDFService());
        GraphStore graphStore = GraphStoreFactory.create(ds);

        String softwareUri;
        if (req.getMethod().equalsIgnoreCase("POST")) {
            softwareUri = buildSoftwareUri(UUID.randomUUID().toString());
        } else {
            String pathInfo = req.getPathInfo();
            if (Objects.isNull(pathInfo) || pathInfo.isEmpty()) {
                do400BadRequest("You have to provide a record identifier.", resp);
                return;
            }

            softwareUri = buildSoftwareUri(pathInfo.substring(1));
        }

        try {
            pauseIndexer(indexer);
            beginTransaction(ds);

            action.accept(graphStore, softwareUri);

        } finally {
            commitTransaction(ds);
            unpauseIndexer(indexer);
        }
    }

    private void pauseIndexer(SearchIndexer indexer) {
        if (indexer != null) {
            indexer.pause();
        }
    }

    private void unpauseIndexer(SearchIndexer indexer) {
        if (indexer != null) {
            indexer.unpause();
        }
    }

    private void beginTransaction(Dataset ds) {
        if (ds.supportsTransactions()) {
            ds.begin(ReadWrite.WRITE);
        }
    }

    private void commitTransaction(Dataset ds) {
        if (ds.supportsTransactions()) {
            ds.commit();
            ds.end();
        }
    }

    private void addSoftwareRelatedFields(StringBuilder query, SoftwareRequestDTO softwareDTO, String softwareUri) {
        query.append("<").append(softwareUri).append("> rdf:type obo:ERO_0000071 ;\n");

        if (Objects.nonNull(softwareDTO.keywords) && !softwareDTO.keywords.isEmpty()) {
            query.append("vivo:freetextKeyword \"").append(StringEscapeUtils.escapeJava(softwareDTO.keywords))
                .append("\" ;\n");
        }

        if (Objects.nonNull(softwareDTO.version) && !softwareDTO.version.isEmpty()) {
            query.append("obo:ERO_0000072 \"").append(StringEscapeUtils.escapeJava(softwareDTO.version))
                .append("\" ;\n");
        }

        if (Objects.nonNull(softwareDTO.description) && !softwareDTO.description.isEmpty()) {
            query.append("bibo:abstract \"").append(StringEscapeUtils.escapeJava(softwareDTO.description))
                .append("\" ;\n");
        }

        String doiPattern = "^10\\.\\d{4,9}/[-,._;()/:A-Z0-9]+$";
        Pattern compiledPattern = Pattern.compile(doiPattern, Pattern.CASE_INSENSITIVE);
        for (String identifier : softwareDTO.identifiers) {
            if (compiledPattern.matcher(identifier).matches()) {
                query.append("bibo:doi \"").append(StringEscapeUtils.escapeJava(identifier)).append("\" ;\n");
            } else {
                query.append("vivo:swhid \"").append(StringEscapeUtils.escapeJava(identifier)).append("\" ;\n");
            }
        }

        query.append("rdfs:label \"").append(StringEscapeUtils.escapeJava(softwareDTO.name)).append("\"@en-US .\n");
    }

    private void addAuthors(StringBuilder query, List<AuthorDTO> authors, String defaultNamespace, String documentUri,
                            OntModel ontModel) {
        for (AuthorDTO author : authors) {
            String authorUri = null;
            boolean personFound = false;

            if (author.identifier != null && !author.identifier.isEmpty() && author.type.endsWith("Person")) {
                String checkAuthorQuery = String.format(
                    "PREFIX rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n" +
                        "PREFIX foaf:     <http://xmlns.com/foaf/0.1/>\n" +
                        "SELECT ?author WHERE { ?author rdf:type foaf:Person . ?author vivo:orcidId \"%s\" . }",
                    StringEscapeUtils.escapeJava(author.identifier)
                );

                try (QueryExecution qe = QueryExecutionFactory.create(checkAuthorQuery, ontModel)) {
                    ResultSet results = qe.execSelect();

                    if (results.hasNext()) {
                        QuerySolution solution = results.nextSolution();
                        authorUri = solution.getResource("author").getURI();
                        personFound = true;
                    }
                } catch (Exception e) {
                    log.error("Error when looking for existing person.", e);
                }
            }

            if (!personFound) {
                authorUri = defaultNamespace + UUID.randomUUID();

                query.append("<").append(authorUri).append("> rdf:type <")
                    .append(StringEscapeUtils.escapeJava(author.type)).append("> ;\n")
                    .append("rdfs:label \"").append(StringEscapeUtils.escapeJava(author.name)).append("\"@en-US ;\n");

                if (author.identifier != null && !author.identifier.isEmpty() && author.type.endsWith("Person")) {
                    query.append("vivo:orcidId \"").append(StringEscapeUtils.escapeJava(author.identifier))
                        .append("\" ;\n");
                }

                query.append(".\n");
            }

            String relatedObjectUri = defaultNamespace + UUID.randomUUID();
            query.append("<").append(documentUri).append("> vivo:relatedBy <").append(relatedObjectUri).append("> .\n")
                .append("<").append(relatedObjectUri).append("> rdf:type vivo:Authorship ;\n")
                .append("vivo:relates <").append(authorUri).append("> .\n");
        }
    }


    private void addPublicationDate(StringBuilder query, String dateString, String defaultNamespace,
                                    String documentUri) {
        if (dateString != null && !dateString.isEmpty()) {
            String dateObjectUri = defaultNamespace + UUID.randomUUID();

            query.append("<").append(documentUri).append("> vivo:dateTimeValue <")
                .append(dateObjectUri).append("> .\n")
                .append("<").append(dateObjectUri).append("> rdf:type vivo:DateTimeValue ;\n")
                .append("vivo:dateTime \"").append(StringEscapeUtils.escapeJava(dateString)).append("\"^^xsd:date .\n");
        }
    }

    private void addFunding(StringBuilder query, List<String> fundings, String defaultNamespace,
                            String documentUri) {
        for (String funding : fundings) {
            String funderObjectUri = defaultNamespace + UUID.randomUUID();

            query.append("<").append(documentUri).append("> vivo:informationResourceSupportedBy <")
                .append(funderObjectUri).append("> .\n")
                .append("<").append(funderObjectUri).append("> rdf:type vivo:Funding ;\n");

            query.append("rdfs:label \"").append(StringEscapeUtils.escapeJava(funding)).append("\"@en-US .\n");
        }
    }

    private void addFunders(StringBuilder query, List<FunderRequestDTO> funders, String defaultNamespace,
                            String documentUri) {
        for (FunderRequestDTO funder : funders) {
            if (Objects.isNull(funder.name) || funder.name.isEmpty() || Objects.isNull(funder.type) ||
                funder.type.isEmpty()) {
                continue;
            }

            String grantObjectUri = defaultNamespace + UUID.randomUUID();
            String funderObjectUri = defaultNamespace + UUID.randomUUID();

            query.append("<").append(documentUri).append("> vivo:informationResourceSupportedBy <")
                .append(grantObjectUri).append("> .\n")
                .append("<").append(grantObjectUri).append("> rdf:type vivo:Grant ;\n");

            if (Objects.nonNull(funder.grantName) && !funder.grantName.isEmpty()) {
                query.append("rdfs:label \"").append(StringEscapeUtils.escapeJava(funder.grantName));
            } else {
                query.append("rdfs:label \"").append(StringEscapeUtils.escapeJava(funder.name)).append(" Grant");
            }

            query
                .append("\"@en-US .\n");

            query
                .append("<").append(funderObjectUri).append("> rdf:type <")
                .append(StringEscapeUtils.escapeJava(funder.type)).append("> ;\n")
                .append("rdfs:label \"").append(StringEscapeUtils.escapeJava(funder.name)).append("\"@en-US .\n")
                .append("<").append(grantObjectUri).append("> vivo:assignedBy <")
                .append(funderObjectUri).append("> .\n");

        }
    }

    private void addPrefixClauses(StringBuilder sb) {
        sb.append("PREFIX rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n")
            .append("PREFIX rdfs:     <http://www.w3.org/2000/01/rdf-schema#>\n")
            .append("PREFIX xsd:      <http://www.w3.org/2001/XMLSchema#>\n")
            .append("PREFIX owl:      <http://www.w3.org/2002/07/owl#>\n")
            .append("PREFIX swrl:     <http://www.w3.org/2003/11/swrl#>\n")
            .append("PREFIX swrlb:    <http://www.w3.org/2003/11/swrlb#>\n")
            .append("PREFIX vitro:    <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#>\n")
            .append("PREFIX bibo:     <http://purl.org/ontology/bibo/>\n")
            .append("PREFIX c4o:      <http://purl.org/spar/c4o/>\n")
            .append("PREFIX cito:     <http://purl.org/spar/cito/>\n")
            .append("PREFIX dcterms:  <http://purl.org/dc/terms/>\n")
            .append("PREFIX event:    <http://purl.org/NET/c4dm/event.owl#>\n")
            .append("PREFIX fabio:    <http://purl.org/spar/fabio/>\n")
            .append("PREFIX foaf:     <http://xmlns.com/foaf/0.1/>\n")
            .append("PREFIX geo:      <http://aims.fao.org/aos/geopolitical.owl#>\n")
            .append("PREFIX obo:      <http://purl.obolibrary.org/obo/>\n")
            .append("PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n")
            .append("PREFIX vcard:    <http://www.w3.org/2006/vcard/ns#>\n");
    }

    private List<SoftwareResponseDTO> handleDTOConversion(SparqlQueryApiExecutor core)
        throws RDFServiceException, IOException {
        String sparqlQueryResponse = getSparqlQueryResponse(core);

        List<Map<String, String>> bindings = parseBindings(sparqlQueryResponse);

        List<SoftwareResponseDTO> softwareResponse = new ArrayList<>();
        for (Map<String, String> binding : bindings) {
            Optional<SoftwareResponseDTO> existingRecord =
                softwareResponse.stream()
                    .filter(software -> software.internalIdentifier.equals(binding.get("software"))).findFirst();
            if (existingRecord.isPresent()) {
                addAuthorToSoftware(binding, existingRecord.get());
                addFundingToSoftware(binding, existingRecord.get());
                addFundersToSoftware(binding, existingRecord.get());
                continue;
            }

            SoftwareResponseDTO software = new SoftwareResponseDTO();
            software.internalIdentifier = binding.getOrDefault("software", null);
            software.name = binding.getOrDefault("label", null);
            software.description = binding.getOrDefault("abstract", null);
            software.version = binding.getOrDefault("version", null);
            software.sameAs = binding.getOrDefault("sameAs", null);

            String swhid = binding.getOrDefault("identifier", null);
            if (Objects.nonNull(swhid)) {
                software.identifiers.add(swhid);
            }

            String doi = binding.getOrDefault("doi", null);
            if (Objects.nonNull(doi)) {
                software.identifiers.add(doi);
            }

            software.url = binding.getOrDefault("url", null);
            software.keywords = binding.getOrDefault("keywords", null);
            software.isPartOf = binding.getOrDefault("isPartOf", null);
            software.hasPart = binding.getOrDefault("hasPart", null);

            String dateTimeString = binding.getOrDefault("datePublished", null);
            if (Objects.nonNull(dateTimeString)) {
                software.datePublished = dateTimeString.split("T")[0];
            }

            addAuthorToSoftware(binding, software);
            addFundingToSoftware(binding, software);
            addFundersToSoftware(binding, software);

            softwareResponse.add(software);
        }

        return softwareResponse;
    }

    private void addAuthorToSoftware(Map<String, String> binding, SoftwareResponseDTO software) {
        if (!Objects.nonNull(binding.get("author"))) {
            return;
        }

        if (software.authors.stream().anyMatch(author -> author.name.equals(binding.get("author")))) {
            return;
        }

        AuthorDTO author = new AuthorDTO();
        author.name = binding.getOrDefault("author", null);
        author.type = binding.getOrDefault("authorType", null);
        author.identifier = binding.getOrDefault("authorIdentifier", null);
        software.authors.add(author);
    }

    private void addFundingToSoftware(Map<String, String> binding, SoftwareResponseDTO software) {
        if (!Objects.nonNull(binding.get("funding"))) {
            return;
        }

        if (software.fundings.stream().anyMatch(funding -> funding.equals(binding.get("funding")))) {
            return;
        }

        software.fundings.add(binding.get("funding"));
    }

    private void addFundersToSoftware(Map<String, String> binding, SoftwareResponseDTO software) {
        if (!Objects.nonNull(binding.get("funder"))) {
            return;
        }

        if (software.funders.stream().anyMatch(funder -> funder.name.equals(binding.get("funder")))) {
            return;
        }

        FunderResponseDTO funder = new FunderResponseDTO();
        funder.name = binding.get("funder");
        funder.type = binding.getOrDefault("funderType", null);
        software.funders.add(funder);
    }

    private String getSparqlQueryResponse(SparqlQueryApiExecutor core) throws IOException, RDFServiceException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        core.executeAndFormat(outputStream);

        String sparqlResponse = outputStream.toString("UTF-8");

        outputStream.close();

        return sparqlResponse;
    }

    private void do400BadRequest(String message, HttpServletResponse resp)
        throws IOException {
        resp.setStatus(400);
        resp.getWriter().println(message);
    }

    private void do400BadRequest(String message, Exception e,
                                 HttpServletResponse resp) throws IOException {
        resp.setStatus(400);
        PrintWriter w = resp.getWriter();
        w.println(message);
        e.printStackTrace(w);
    }

    private void do500InternalServerError(String message, Exception e,
                                          HttpServletResponse resp) throws IOException {
        resp.setStatus(500);
        PrintWriter w = resp.getWriter();
        w.println(message);
        e.printStackTrace(w);
    }

    @Override
    protected AuthorizationRequest requiredActions(VitroRequest vreq) {
        return SimplePermission.USE_SPARQL_UPDATE_API.ACTION;
    }
}
