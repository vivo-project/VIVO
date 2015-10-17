package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import org.joda.time.DateTime;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final public class VisualizationCaches {
    public static final CachingRDFServiceExecutor<Map<String, String>> cachedOrganizationLabels =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?org ?orgLabel\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?org a foaf:Organization .\n" +
                                    "  ?org rdfs:label ?orgLabel .\n" +
                                    "}\n";

                            Map<String, String> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String org      = qs.getResource("org").getURI();
                                    String orgLabel = qs.getLiteral("orgLabel").getString();

                                    map.put(org, orgLabel);
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> cachedOrganizationSubOrgs =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>() {
                        @Override
                        protected Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?org ?subOrg\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?org a foaf:Organization .\n" +
                                    "  ?org <http://purl.obolibrary.org/obo/BFO_0000051> ?subOrg .\n" +
                                    "}\n";

                            Map<String, Set<String>> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String org    = qs.getResource("org").getURI();
                                    String subOrg = qs.getResource("subOrg").getURI();

                                    Set<String> subOrgs = map.get(org);
                                    if (subOrgs == null) {
                                        subOrgs = new HashSet<String>();
                                        subOrgs.add(subOrg);
                                        map.put(org, subOrgs);
                                    } else {
                                        subOrgs.add(subOrg);
                                    }
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, String>> cachedOrganizationToMostSpecificLabel =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?org ?typeLabel\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "    ?org a foaf:Organization .\n" +
                                    "    ?org vitro:mostSpecificType ?type .\n" +
                                    "    ?type rdfs:label ?typeLabel .\n" +
                                    "}\n";

                            Map<String, String> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String org = qs.getResource("org").getURI();
                                    String typeLabel  = qs.getLiteral("typeLabel").getString();
                                    map.put(org, String.valueOf(typeLabel));
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> cachedOrganisationToPeopleMap =
            new CachingRDFServiceExecutor<Map<String, Set<String>>>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>() {
                        @Override
                        protected Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?organisation ?person\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?organisation a foaf:Organization .\n" +
                                    "  ?organisation core:relatedBy ?position .\n" +
                                    "  ?position core:relates ?person .\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "}\n";

                            // TODO Critical section?

                            Map<String, Set<String>> orgToPeopleMap = new HashMap<String, Set<String>>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String org    = qs.getResource("organisation").getURI();
                                    String person = qs.getResource("person").getURI();

                                    Set<String> people = orgToPeopleMap.get(org);
                                    if (people == null) {
                                        people = new HashSet<String>();
                                        people.add(person);
                                        orgToPeopleMap.put(org, people);
                                    } else {
                                        people.add(person);
                                    }
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return orgToPeopleMap;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, String>> cachedPersonLabels =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?personLabel\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  ?person rdfs:label ?personLabel .\n" +
                                    "}\n";

                            Map<String, String> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String person      = qs.getResource("person").getURI();
                                    String personLabel = qs.getLiteral("personLabel").getString();

                                    map.put(person, personLabel);
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> cachedPersonToPublication =
            new CachingRDFServiceExecutor<Map<String, Set<String>>>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>() {
                        @Override
                        protected Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?document\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  ?person core:relatedBy ?authorship .\n" +
                                    "  ?authorship a core:Authorship .\n" +
                                    "  ?authorship core:relates ?document .\n" +
                                    "  ?document a bibo:Document .\n" +
                                    "}\n";

                            Map<String, Set<String>> map = new HashMap<String, Set<String>>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();

                                    Resource person   = qs.getResource("person");
                                    Resource document = qs.getResource("document");

                                    if (person != null && document != null) {
                                        String personURI = person.getURI();

                                        Set<String> documents = map.get(personURI);
                                        if (documents == null) {
                                            documents = new HashSet<String>();
                                            documents.add(document.getURI());
                                            map.put(personURI, documents);
                                        } else {
                                            documents.add(document.getURI());
                                        }
                                    }
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, String>> cachedPublicationToYear =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?document ?publicationDate\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?document a bibo:Document .\n" +
                                    "  ?document core:dateTimeValue ?dateTimeValue . \n" +
                                    "  ?dateTimeValue core:dateTime ?publicationDate . \n" +
                                    "}\n";

                            Map<String, String> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String document = qs.getResource("document").getURI();
                                    String pubDate  = qs.getLiteral("publicationDate").getString();
                                    if (pubDate != null) {
                                        DateTime validParsedDateTimeObject = UtilityFunctions
                                                .getValidParsedDateTimeObject(pubDate);

                                        if (validParsedDateTimeObject != null) {
                                            map.put(document, String.valueOf(validParsedDateTimeObject.getYear()));
                                        }
                                    }
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> cachedPersonToGrant =
            new CachingRDFServiceExecutor<Map<String, Set<String>>>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>() {
                        @Override
                        protected Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?grant\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  ?person <http://purl.obolibrary.org/obo/RO_0000053> ?role .\n" +
                                    "  { ?role a core:PrincipalInvestigatorRole . } UNION { ?role a core:CoPrincipalInvestigatorRole . } \n" +
                                    "  ?role core:relatedBy ?grant .\n" +
                                    "  ?grant a core:Grant .\n" +
                                    "}\n";

                            Map<String, Set<String>> map = new HashMap<String, Set<String>>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();

                                    Resource person = qs.getResource("person");
                                    Resource grant  = qs.getResource("grant");

                                    if (person != null && grant != null) {
                                        String personURI = person.getURI();

                                        Set<String> documents = map.get(personURI);
                                        if (documents == null) {
                                            documents = new HashSet<String>();
                                            documents.add(grant.getURI());
                                            map.put(personURI, documents);
                                        } else {
                                            documents.add(grant.getURI());
                                        }
                                    }
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, String>> cachedGrantToYear =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?grant ?startDateTimeValue\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?grant a core:Grant .\n" +
                                    "  ?grant core:dateTimeInterval ?dateTimeIntervalValue . \n" +
                                    "  ?dateTimeIntervalValue core:start ?startDate . \n" +
                                    "  ?startDate core:dateTime ?startDateTimeValue . \n" +
                                    "}\n";

                            Map<String, String> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String grant = qs.getResource("grant").getURI();
                                    String startDate  = qs.getLiteral("startDateTimeValue").getString();
                                    if (startDate != null) {
                                        DateTime validParsedDateTimeObject = UtilityFunctions
                                                .getValidParsedDateTimeObject(startDate);

                                        if (validParsedDateTimeObject != null) {
                                            map.put(grant, String.valueOf(validParsedDateTimeObject.getYear()));
                                        }
                                    }
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, String>> cachedGrantToRoleYear =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?grant ?startDateTimeValue\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?grant a core:Grant .\n" +
                                    "  ?grant core:relates ?role .\n" +
                                    "  ?role core:dateTimeInterval ?dateTimeIntervalValue . \n" +
                                    "  ?dateTimeIntervalValue core:start ?startDate . \n" +
                                    "  ?startDate core:dateTime ?startDateTimeValue . \n" +
                                    "}\n";

                            Map<String, String> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String grant = qs.getResource("grant").getURI();
                                    String startDate  = qs.getLiteral("startDateTimeValue").getString();
                                    if (startDate != null) {
                                        DateTime validParsedDateTimeObject = UtilityFunctions
                                                .getValidParsedDateTimeObject(startDate);

                                        if (validParsedDateTimeObject != null) {
                                            map.put(grant, String.valueOf(validParsedDateTimeObject.getYear()));
                                        }
                                    }
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    public static final CachingRDFServiceExecutor<Map<String, String>> cachedPersonToMostSpecificLabel =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>() {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?typeLabel\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person vitro:mostSpecificType ?type .\n" +
                                    "    ?type rdfs:label ?typeLabel .\n" +
                                    "}\n";

                            Map<String, String> map = new HashMap<>();

                            InputStream is = null;
                            ResultSet rs = null;
                            try {
                                is = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
                                rs = ResultSetFactory.fromJSON(is);

                                while (rs.hasNext()) {
                                    QuerySolution qs = rs.next();
                                    String person = qs.getResource("person").getURI();
                                    String typeLabel  = qs.getLiteral("typeLabel").getString();
                                    map.put(person, String.valueOf(typeLabel));
                                }
                            } finally {
                                silentlyClose(is);
                            }

                            return map;
                        }
                    }
            );

    private static void silentlyClose(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Throwable t) {

        }
    }
}
