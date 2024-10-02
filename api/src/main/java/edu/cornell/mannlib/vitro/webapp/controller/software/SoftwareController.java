package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.api.sparqlquery.SparqlQueryApiExecutor;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;

@WebServlet(name = "softwareController", urlPatterns = {"/software", "/software/*"}, loadOnStartup = 5)
public class SoftwareController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(SoftwareController.class);

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

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String queryString = (pathInfo != null && pathInfo.length() > 1)
            ? String.format(FIND_BY_ID_QUERY_TEMPLATE,
            IndividualApiSparqlUtility.buildIndividualUri(pathInfo.substring(1)))
            : FIND_ALL_QUERY;

        RDFService rdfService = ModelAccess.on(getServletContext()).getRDFService();

        try {
            SparqlQueryApiExecutor core =
                SparqlQueryApiExecutor.instance(rdfService, queryString, "application/sparql-results+json");
            IndividualApiNetworkUtility.handleResponseContentType(req, resp);

            if (IndividualApiNetworkUtility.isJsonRequest(req)) {
                List<SoftwareResponseDTO> response = handleDTOConversion(core, resp);

                if (response.isEmpty()) {
                    return;
                }

                resp.getWriter().println(
                    IndividualApiNetworkUtility.serializeToJSON(response.size() == 1 ? response.get(0) : response));
            } else {
                core.executeAndFormat(resp.getOutputStream());
            }
        } catch (Exception e) {
            IndividualApiNetworkUtility.handleException(e, queryString, resp);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAuthorizedToDisplayPage(req, resp, requiredActions(new VitroRequest(req)))) {
            return;
        }

        IndividualApiCommonCRUDUtility.executeWithTransaction(req, resp, (graphStore, softwareUri) -> {

            SoftwareRequestDTO softwareDTO = null;
            try {
                softwareDTO = IndividualApiNetworkUtility.parseRequestBody(req, SoftwareRequestDTO.class);
            } catch (IOException e) {
                try {
                    IndividualApiNetworkUtility.do400BadRequest("Error while parsing request body.", resp);
                } catch (IOException ex) {
                    log.error("Error while handling exception.", ex);
                }
            }

            if (Objects.isNull(softwareDTO)) {
                return;
            }

            String insertSoftwareQuery = buildInsertSoftwareQuery(softwareDTO, softwareUri, ModelAccess.on(
                new VitroRequest(req)).getOntModel());
            IndividualApiSparqlUtility.executeUpdate(graphStore, insertSoftwareQuery);

            softwareDTO.internalIdentifier = softwareUri;
            try {
                resp.setContentType("application/json");
                resp.getWriter().println(IndividualApiNetworkUtility.serializeToJSON(softwareDTO));
            } catch (IOException e) {
                try {
                    IndividualApiNetworkUtility.do400BadRequest("Error while writing response body.", resp);
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

        IndividualApiCommonCRUDUtility.performDeleteOperation(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAuthorizedToDisplayPage(req, resp, requiredActions(new VitroRequest(req)))) {
            return;
        }

        doDelete(req, resp);

        IndividualApiCommonCRUDUtility.executeWithTransaction(req, resp, (graphStore, softwareUri) -> {
            SoftwareRequestDTO softwareDTO;
            try {
                softwareDTO = IndividualApiNetworkUtility.parseRequestBody(req, SoftwareRequestDTO.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String insertSoftwareQuery = buildInsertSoftwareQuery(softwareDTO, softwareUri, ModelAccess.on(
                new VitroRequest(req)).getOntModel());
            IndividualApiSparqlUtility.executeUpdate(graphStore, insertSoftwareQuery);

            softwareDTO.internalIdentifier = softwareUri;
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        });
    }

    private String buildInsertSoftwareQuery(SoftwareRequestDTO softwareDTO, String softwareUri,
                                            OntModel ontModel) {
        InsertQueryBuilder queryBuilder = new InsertQueryBuilder();
        queryBuilder.startInsertQuery();

        String defaultNamespace = ConfigurationProperties.getInstance().getProperty("Vitro.defaultNamespace");
        addSoftwareRelatedFields(queryBuilder.getInsertQuery(), softwareDTO, softwareUri);

        queryBuilder
            .addPublicationDate(softwareDTO.datePublished, defaultNamespace, softwareUri)
            .addAuthors(softwareDTO.authors, defaultNamespace, softwareUri, ontModel)
            .addFunding(softwareDTO.fundings, defaultNamespace, softwareUri)
            .addFunders(softwareDTO.funders, defaultNamespace, softwareUri);

        queryBuilder.getInsertQuery().append("    }\n").append("}\n");

        return queryBuilder.build();
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

    private List<SoftwareResponseDTO> handleDTOConversion(SparqlQueryApiExecutor core, HttpServletResponse resp)
        throws RDFServiceException, IOException {
        String sparqlQueryResponse = IndividualApiSparqlUtility.getSparqlQueryResponse(core);

        List<Map<String, String>> bindings = IndividualApiSparqlUtility.parseBindings(sparqlQueryResponse);
        if (bindings.isEmpty()) {
            IndividualApiNetworkUtility.do404NotFound("Not found.", resp);
        }

        List<SoftwareResponseDTO> softwareResponse = new ArrayList<>();
        for (Map<String, String> binding : bindings) {
            Optional<SoftwareResponseDTO> existingRecord =
                softwareResponse.stream()
                    .filter(software -> software.internalIdentifier.equals(binding.get("software"))).findFirst();
            if (existingRecord.isPresent()) {
                InformationContentEntityResponseUtility.addAuthorToICE(binding, existingRecord.get());
                InformationContentEntityResponseUtility.addFundingToICE(binding, existingRecord.get());
                InformationContentEntityResponseUtility.addFundersToICE(binding, existingRecord.get());
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

            InformationContentEntityResponseUtility.addAuthorToICE(binding, software);
            InformationContentEntityResponseUtility.addFundingToICE(binding, software);
            InformationContentEntityResponseUtility.addFundersToICE(binding, software);

            softwareResponse.add(software);
        }

        return softwareResponse;
    }

    @Override
    protected AuthorizationRequest requiredActions(VitroRequest vreq) {
        return SimplePermission.USE_SPARQL_UPDATE_API.ACTION;
    }
}
