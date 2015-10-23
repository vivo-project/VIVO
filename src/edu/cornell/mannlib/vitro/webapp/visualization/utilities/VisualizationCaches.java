/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Resource;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holder for the caches we are using in the visualizations
 */
final public class VisualizationCaches {
    // Affinity object to ensure that only one background thread can be running at once when updating the caches
    private static final CachingRDFServiceExecutor.Affinity visualizationAffinity = new CachingRDFServiceExecutor.Affinity();

    /**
     * Rebuild all the caches
     */
    public static void rebuildAll() { rebuildAll(null); }

    /**
     * Rebuild all the caches
     * @param rdfService if not null, use this service in foreground, otherwise may use the background thread
     */
    public static void rebuildAll(RDFService rdfService) {
        organizationLabels.build(rdfService);
        organizationSubOrgs.build(rdfService);
        organizationToMostSpecificLabel.build(rdfService);
        organisationToPeopleMap.build(rdfService);
        personLabels.build(rdfService);
        personToMostSpecificLabel.build(rdfService);
        personToPublication.build(rdfService);
        publicationToJournal.build(rdfService);
        publicationToYear.build(rdfService);
        personToGrant.build(rdfService);
        grantToYear.build(rdfService);
    }

    /**
     * Cache of organization labels (uri -> label)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> organizationLabels =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>(visualizationAffinity) {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?org ?orgLabel\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?org a foaf:Organization .\n" +
                                    "  ?org rdfs:label ?orgLabel .\n" +
                                    "}\n";

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String org      = qs.getResource("org").getURI();
                                    String orgLabel = qs.getLiteral("orgLabel").getString();

                                    map.put(org, orgLabel);
                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Cache of organization to sub organizations (uri -> list of uris)
     */
    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> organizationSubOrgs =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>(visualizationAffinity) {
                        @Override
                        protected Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?org ?subOrg\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?org a foaf:Organization .\n" +
                                    "  ?org <http://purl.obolibrary.org/obo/BFO_0000051> ?subOrg .\n" +
                                    "}\n";

                            final Map<String, Set<String>> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
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
                            });

                            return map;
                        }
                    }
            );

    /**
     * Organization most specific type label (uri -> string)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> organizationToMostSpecificLabel =
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

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String org = qs.getResource("org").getURI();
                                    String typeLabel  = qs.getLiteral("typeLabel").getString();
                                    map.put(org, String.valueOf(typeLabel));
                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Map of people within an organisation (org uri -> list of person uri)
     */
    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> organisationToPeopleMap =
            new CachingRDFServiceExecutor<Map<String, Set<String>>>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>(visualizationAffinity) {
                        @Override
                        protected Map<String, Set<String>> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?organisation ?person\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?organisation a foaf:Organization .\n" +
                                    "  ?organisation core:relatedBy ?position .\n" +
                                    "  ?position a core:Position .\n" +
                                    "  ?position core:relates ?person .\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "}\n";

                            final Map<String, Set<String>> orgToPeopleMap = new HashMap<String, Set<String>>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
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
                            });

                            return orgToPeopleMap;
                        }
                    }
            );

    /**
     * Display labels for people (uri -> label)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> personLabels =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>(visualizationAffinity) {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?personLabel\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  ?person rdfs:label ?personLabel .\n" +
                                    "}\n";

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String person      = qs.getResource("person").getURI();
                                    String personLabel = qs.getLiteral("personLabel").getString();

                                    map.put(person, personLabel);
                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Most specific type for person (uri -> label)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> personToMostSpecificLabel =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>(visualizationAffinity) {
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

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String person = qs.getResource("person").getURI();
                                    String typeLabel  = qs.getLiteral("typeLabel").getString();
                                    map.put(person, String.valueOf(typeLabel));
                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Person to publication Map (person uri -> list of publication uri)
     */
    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> personToPublication =
            new CachingRDFServiceExecutor<Map<String, Set<String>>>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>(visualizationAffinity) {
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

                            final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
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
                            });

                            return map;
                        }
                    }
            );

    /**
     * Publication to journal (publication uri -> journal label)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> publicationToJournal =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>(visualizationAffinity) {
                        @Override
                        protected Map<String, String> callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?document ?journalLabel\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?document a bibo:Document .\n" +
                                    "  ?document core:hasPublicationVenue ?journal . \n" +
                                    "  ?journal rdfs:label ?journalLabel . \n" +
                                    "}\n";

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String document      = qs.getResource("document").getURI();
                                    String journalLabel = qs.getLiteral("journalLabel").getString();

                                    map.put(document, journalLabel);
                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Publication to year (publication uri -> year)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> publicationToYear =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>(visualizationAffinity) {
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

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
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
                            });

                            return map;
                        }
                    }
            );

    /**
     * Person to grant (person uri -> grant uri)
     */
    public static final CachingRDFServiceExecutor<Map<String, Set<String>>> personToGrant =
            new CachingRDFServiceExecutor<Map<String, Set<String>>>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Set<String>>>(visualizationAffinity) {
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

                            final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
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
                            });

                            return map;
                        }
                    }
            );

    /**
     * Grant to year (grant uri -> year)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> grantToYear =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>(visualizationAffinity) {
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

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
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
                            });

                            return map;
                        }
                    }
            );

    /**
     * Grant to year of start in role (grant uri -> year)
     */
    public static final CachingRDFServiceExecutor<Map<String, String>> grantToRoleYear =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, String>>(visualizationAffinity) {
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

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
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
                            });

                            return map;
                        }
                    }
            );
}
