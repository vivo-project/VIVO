package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.api.sparqlquery.SparqlQueryApiExecutor;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

public class IndividualApiSparqlUtility {

    public static String getSparqlQueryResponse(SparqlQueryApiExecutor core) throws IOException, RDFServiceException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        core.executeAndFormat(outputStream);

        String sparqlResponse = outputStream.toString("UTF-8");

        outputStream.close();

        return sparqlResponse;
    }

    public static List<Map<String, String>> parseBindings(String jsonResponse) throws IOException {
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

    public static void addPrefixClauses(StringBuilder queryBuilder) {
        queryBuilder
            .append("PREFIX rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n")
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

    public static void executeUpdate(GraphStore graphStore, String query) {
        UpdateRequest updateRequest = UpdateFactory.create(query);
        UpdateAction.execute(updateRequest, graphStore);
    }

    public static String buildIndividualUri(String entityId) {
        String defaultNamespace =
            Objects.requireNonNull(ConfigurationProperties.getInstance().getProperty("Vitro.defaultNamespace"));
        return defaultNamespace + StringEscapeUtils.escapeJava(entityId);
    }
}
