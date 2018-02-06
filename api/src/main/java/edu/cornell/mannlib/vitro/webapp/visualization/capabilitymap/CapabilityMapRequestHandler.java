/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.capabilitymap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.model.ConceptLabelMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.ConceptPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.OrganizationPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationCaches;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;
import org.apache.commons.logging.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CapabilityMapRequestHandler implements VisualizationRequestHandler {
    @Override
    public AuthorizationRequest getRequiredPrivileges() {
        return null;
    }

    @Override
    public ResponseValues generateStandardVisualization(VitroRequest vitroRequest, Log log, Dataset dataSource) throws MalformedQueryParametersException {
        return null;
    }

    @Override
    public ResponseValues generateVisualizationForShortURLRequests(Map<String, String> parameters, VitroRequest vitroRequest, Log log, Dataset dataSource) throws MalformedQueryParametersException {
        return prepareMarkup(vitroRequest);
    }

    @Override
    public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log, Dataset dataSource) throws MalformedQueryParametersException, JsonProcessingException {
        ConceptLabelMap       conceptLabelMap = VisualizationCaches.conceptToLabel.getNoWait(vitroRequest.getRDFService());
        ConceptPeopleMap      conceptPeopleMap = VisualizationCaches.conceptToPeopleMap.getNoWait(vitroRequest.getRDFService());
        OrganizationPeopleMap organizationPeopleMap = VisualizationCaches.organisationToPeopleMap.getNoWait(vitroRequest.getRDFService());
        Map<String, String>   organizationLabels = VisualizationCaches.organizationLabels.getNoWait(vitroRequest.getRDFService());

        String data = vitroRequest.getParameter("data");
        if (!StringUtils.isEmpty(data)) {
            if ("concepts".equalsIgnoreCase(data)) {
                Set<String> concepts = new HashSet<String>();

                for (String conceptKey : conceptPeopleMap.conceptToPeople.keySet()) {
                    String label = conceptLabelMap.conceptToLabel.get(conceptKey);
                    if (!StringUtils.isEmpty(label)) {
                        concepts.add(conceptLabelMap.conceptToLabel.get(conceptKey));
                    }
                }

                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(concepts);
            }
            return "";
        }

        String personParam = vitroRequest.getParameter("person");
        if (!StringUtils.isEmpty(personParam)) {
            CapabilityMapResponse response = new CapabilityMapResponse();
            CapabilityMapResult result = new CapabilityMapResult();
            fillPersonDetails(vitroRequest.getRDFService(), personParam, result);
            if (StringUtils.isEmpty(result.firstName) && StringUtils.isEmpty(result.lastName)) {
                result.lastName = "Missing Name";
            }
            Set<String> concepts = conceptPeopleMap.personToConcepts.get(personParam);
            if (concepts != null) {
                result.subjectArea = concepts.toArray(new String[concepts.size()]);
            }
            Set<String> organizations = organizationPeopleMap.organizationToPeople.get(personParam);
            if (organizations != null) {
                for (String org : organizations) {
                    result.department = organizationLabels.get(org);
                    if (!StringUtils.isEmpty(result.department)) {
                        break;
                    }
                }
            }
            response.results.add(result);

            ObjectMapper mapper = new ObjectMapper();

            String callback = vitroRequest.getParameter("callback");
            if (!StringUtils.isEmpty(callback)) {
                return callback + "(" + mapper.writeValueAsString(response) + ");";
            }
            return mapper.writeValueAsString(response);
        }

        String query = vitroRequest.getParameter("query");
        if (!StringUtils.isEmpty(query)) {
            CapabilityMapResponse response = new CapabilityMapResponse();

            Set<String> matchedConcepts = conceptLabelMap.lowerLabelToConcepts.get(query.toLowerCase());

            Set<String> people = new HashSet<String>();
            if (matchedConcepts != null) {
                for (String uri : matchedConcepts) {
                    Set<String> peopleSet = conceptPeopleMap.conceptToPeople.get(uri);
                    if (peopleSet != null) {
                        people.addAll(peopleSet);
                    }
                }
            }

            Set<String> clusterConcepts = new HashSet<String>();
            for (String person : people) {
                if (conceptPeopleMap.personToConcepts.containsKey(person)) {
                    clusterConcepts.addAll(conceptPeopleMap.personToConcepts.get(person));
                }
            }

            if (matchedConcepts != null) {
                clusterConcepts.removeAll(matchedConcepts);
            }

            Set<String> clusterLabels = new HashSet<String>();
            for (String clusterConcept : clusterConcepts) {
                String label = conceptLabelMap.conceptToLabel.get(clusterConcept);
                if (!StringUtils.isEmpty(label)) {
                    clusterLabels.add(label);
                }
            }

            String[] clusters = clusterLabels.toArray(new String[clusterLabels.size()]);

            for (String person : people) {
                CapabilityMapResult result = new CapabilityMapResult();
                result.profileId = person;
                result.query = query;
                result.clusters = clusters;
                response.results.add(result);
            }

            ObjectMapper mapper = new ObjectMapper();
            String callback = vitroRequest.getParameter("callback");
            if (!StringUtils.isEmpty(callback)) {
                return callback + "(" + mapper.writeValueAsString(response) + ");";
            }
            return mapper.writeValueAsString(response);
        }

        return "";
    }

    @Override
    public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log, Dataset dataset) throws MalformedQueryParametersException {
        return null;
    }

    private ResponseValues prepareMarkup(VitroRequest vreq) {
        String standaloneTemplate = "capabilityMap.ftl";

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title", "Capability Map");
        body.put("vivoDefaultNamespace", vreq.getWebappDaoFactory().getDefaultNamespace());
        return new TemplateResponseValues(standaloneTemplate, body);
    }

    private void fillPersonDetails(final RDFService rdfService, final String personUri, final CapabilityMapResult result) {
        try {
            String construct = QueryConstants.getSparqlPrefixQuery() +
                    "CONSTRUCT {\n" +
                    "  <" + personUri + "> a foaf:Person .\n" +
                    "  <" + personUri + "> foaf:lastName ?lastName .\n" +
                    "  <" + personUri + "> foaf:firstName ?firstName .\n" +
                    "  <" + personUri + "> obo:ARG_2000028 ?contactInfo .\n" +
                    "  ?contactInfo vcard:hasName ?contactName .\n" +
                    "  ?contactName vcard:familyName ?familyName .\n" +
                    "  ?contactName vcard:givenName ?givenName .\n" +
                    "  ?contactInfo vcard:hasTitle ?contactTitle .\n" +
                    "  ?contactTitle vcard:title ?contactTitleLabel  .\n" +
                    "  <" + personUri + "> public:thumbnailImage ?directDownloadUrl .\n" +
                    "} WHERE {\n" +
                    "  { \n" +
                    "    <" + personUri + "> foaf:lastName ?lastName .\n" +
                    "  } UNION { \n" +
                    "    <" + personUri + "> foaf:firstName ?firstName .\n" +
                    "  } UNION { \n" +
                    "    <" + personUri + "> obo:ARG_2000028 ?contactInfo .\n" +
                    "    ?contactInfo vcard:hasName ?contactName .\n" +
                    "    ?contactName vcard:familyName ?familyName .\n" +
                    "  } UNION { \n" +
                    "    <" + personUri + "> obo:ARG_2000028 ?contactInfo .\n" +
                    "    ?contactInfo vcard:hasName ?contactName .\n" +
                    "    ?contactName vcard:givenName ?givenName .\n" +
                    "  } UNION { \n" +
                    "    <" + personUri + "> obo:ARG_2000028 ?contactInfo .\n" +
                    "    ?contactInfo vcard:hasTitle ?contactTitle .\n" +
                    "    ?contactTitle vcard:title ?contactTitleLabel .\n" +
                    "  } UNION { \n" +
                    "    <" + personUri + "> public:mainImage ?mainImage .\n" +
                    "    ?mainImage public:thumbnailImage ?thumbnailImage .\n" +
                    "    ?thumbnailImage public:downloadLocation ?downloadLocation .\n" +
                    "    ?downloadLocation public:directDownloadUrl ?directDownloadUrl .\n" +
                    "  } \n" +
                    "}\n";

            Model constructedModel = ModelFactory.createDefaultModel();
            rdfService.sparqlConstructQuery(construct, constructedModel);

            String nameQuery = QueryConstants.getSparqlPrefixQuery() +
                    "SELECT ?familyName ?givenName ?lastName ?firstName ?title ?thumbnailUrl\n" +
                    "WHERE\n" +
                    "{\n" +
                    "  <" + personUri + "> a foaf:Person .\n" + // ?person
                    "  OPTIONAL {\n" +
                    "    <" + personUri + ">  obo:ARG_2000028 ?contactInfo .\n" +
                    "    ?contactInfo vcard:hasName ?contactName .\n" +
                    "    OPTIONAL { ?contactName vcard:familyName ?familyName . }\n" +
                    "    OPTIONAL { ?contactName vcard:givenName  ?givenName . }\n" +
                    "  }\n" +
                    "  OPTIONAL {\n" +
                    "    <" + personUri + ">  obo:ARG_2000028 ?contactInfo .\n" +
                    "    ?contactInfo vcard:hasTitle ?contactTitle .\n" +
                    "    ?contactTitle vcard:title ?title .\n" +
                    "  }\n" +
                    "  OPTIONAL { <" + personUri + ">  foaf:lastName ?lastName . }\n" +
                    "  OPTIONAL { <" + personUri + ">  foaf:firstName  ?firstName . }\n" +
                    "  OPTIONAL { <" + personUri + ">  public:thumbnailImage ?thumbnailUrl . }\n" +
                    "}\n";

            QueryExecution qe = QueryExecutionFactory.create(nameQuery, constructedModel);

            try {
                new ResultSetConsumer() {
                    @Override
                    protected void processQuerySolution(QuerySolution qs) {
                        result.profileId = personUri;
                        result.firstName = null;
                        result.lastName = null;
                        result.thumbNail = null;
                        result.preferredTitle = null;

                        Literal familyNameNode = qs.getLiteral("familyName");
                        if (familyNameNode != null) {
                            result.lastName = familyNameNode.getString();
                        } else {
                            Literal lastNameNode = qs.getLiteral("lastName");
                            result.lastName = lastNameNode == null ? null : lastNameNode.getString();
                        }

                        Literal givenNameNode = qs.getLiteral("givenName");
                        if (givenNameNode != null) {
                            result.firstName = givenNameNode.getString();
                        } else {
                            Literal firstNameNode = qs.getLiteral("firstName");
                            result.firstName = firstNameNode == null ? null : firstNameNode.getString();
                        }

                        Literal thumbnailUrlNode = qs.getLiteral("thumbnailUrl");
                        result.thumbNail = thumbnailUrlNode == null ? null : thumbnailUrlNode.getString();

                        Literal titleNode = qs.getLiteral("title");
                        result.preferredTitle = titleNode == null ? null : titleNode.getString();
                    }
                }.processResultSet(qe.execSelect());
            } finally {
                qe.close();
            }
        } catch (RDFServiceException e) {

        }
    }
}
