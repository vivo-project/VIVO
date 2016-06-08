/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.capabilitymap;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Dataset;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.model.ConceptLabelMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.ConceptPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.OrganizationPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.Person;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationCaches;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;
import org.apache.axis.utils.StringUtils;
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
    public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log, Dataset dataSource) throws MalformedQueryParametersException {
        CapabilityMapResponse response = new CapabilityMapResponse();

        String query = vitroRequest.getParameter("query");

        ConceptLabelMap conceptLabelMap = VisualizationCaches.conceptToLabel.get(vitroRequest.getRDFService());
        ConceptPeopleMap conceptPeopleMap = VisualizationCaches.conceptToPeopleMap.get(vitroRequest.getRDFService());
        OrganizationPeopleMap organizationPeopleMap = VisualizationCaches.organisationToPeopleMap.get(vitroRequest.getRDFService());
        Map<String, String> organizationLabels = VisualizationCaches.organizationLabels.get(vitroRequest.getRDFService());
        Map<String, Person> peopleMap = VisualizationCaches.people.get(vitroRequest.getRDFService());

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
            clusterLabels.add(conceptLabelMap.conceptToLabel.get(clusterConcept));
        }

        String[] clusters = clusterLabels.toArray(new String[clusterLabels.size()]);

        for (String person : people) {
            Person personObj = peopleMap.get(person);
            if (personObj != null) {
                CapabilityMapResult result = new CapabilityMapResult();
                result.profileId = person;
                result.query = query;
                result.firstName = personObj.firstName;
                result.lastName = personObj.lastName;
                result.thumbNail = personObj.thumbnailUrl;
                result.preferredTitle = personObj.preferredTitle;
                Set<String> concepts = conceptPeopleMap.personToConcepts.get(person);
                if (concepts != null) {
                    result.subjectArea = concepts.toArray(new String[concepts.size()]);
                }
                Set<String> organizations = organizationPeopleMap.organizationToPeople.get(person);
                if (organizations != null) {
                    for (String org : organizations) {
                        result.department = organizationLabels.get(org);
                        if (!StringUtils.isEmpty(result.department)) {
                            break;
                        }
                    }
                }
                result.clusters = clusters;
                response.results.add(result);
            }
        }

        Gson gson = new Gson();

        String callback = vitroRequest.getParameter("callback");
        if (!StringUtils.isEmpty(callback)) {
            return callback + "(" + gson.toJson(response) + ");";
        }
        return gson.toJson(response);
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
/*
        if (VisualizationCaches.personToGrant.isCached()) {
            body.put("builtFromCacheTime", VisualizationCaches.personToGrant.cachedWhen());
        }
*/
        return new TemplateResponseValues(standaloneTemplate, body);
    }
}
