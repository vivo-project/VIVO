/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.model.ConceptLabelMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.ConceptPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.OrganizationPeopleMap;
import edu.cornell.mannlib.vitro.webapp.visualization.model.Person;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holder for the caches we are using in the visualizations
 *
 * String.intern() was a problem pre-Java 7, but has greater utility now.
 * Please see the following guide for information on the implementation of String.intern()
 *
 * http://java-performance.info/string-intern-in-java-6-7-8/
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
        conceptToLabel.build(rdfService);
        conceptToPeopleMap.build(rdfService);
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
//        people.build(rdfService);
    }

    public static void buildMissing() {
        if (!conceptToLabel.isCached())                  { conceptToLabel.build(null); }
        if (!conceptToPeopleMap.isCached())              { conceptToPeopleMap.build(null); }
        if (!organizationLabels.isCached())              { organizationLabels.build(null); }
        if (!organizationSubOrgs.isCached())             { organizationSubOrgs.build(null); }
        if (!organizationToMostSpecificLabel.isCached()) { organizationToMostSpecificLabel.build(null); }
        if (!organisationToPeopleMap.isCached())         { organisationToPeopleMap.build(null); }
        if (!personLabels.isCached())                    { personLabels.build(null); }
        if (!personToMostSpecificLabel.isCached())       { personToMostSpecificLabel.build(null); }
        if (!personToPublication.isCached())             { personToPublication.build(null); }
        if (!publicationToJournal.isCached())            { publicationToJournal.build(null); }
        if (!publicationToYear.isCached())               { publicationToYear.build(null); }
        if (!personToGrant.isCached())                   { personToGrant.build(null); }
        if (!grantToYear.isCached())                     { grantToYear.build(null); }
//        if (!people.isCached())                          { people.build(null); }
    }

    /**
     * Rebuild the specifield caches
     * @param executors  Caching RDF executors
     */
    public static void rebuild(CachingRDFServiceExecutor... executors) {
        if (executors != null) {
            for (CachingRDFServiceExecutor e : executors) {
                e.build(null);
            }
        }
    }

    /**
     * Cache of people
     */
    public static final CachingRDFServiceExecutor<Map<String, Person>> people =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<Map<String, Person>>(visualizationAffinity) {
                        @Override
                        protected Map<String, Person> callWithService(RDFService rdfService) throws Exception {
                            final Map<String, Person> map = new HashMap<String, Person>();

                            String construct = QueryConstants.getSparqlPrefixQuery() +
                                    "CONSTRUCT {\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  ?person foaf:lastName ?lastName .\n" +
                                    "  ?person foaf:firstName ?firstName .\n" +
                                    "  ?person obo:ARG_2000028 ?contactInfo .\n" +
                                    "  ?contactInfo vcard:hasName ?contactName .\n" +
                                    "  ?contactName vcard:familyName ?familyName .\n" +
                                    "  ?contactName vcard:givenName ?givenName .\n" +
                                    "  ?contactInfo vcard:hasTitle ?contactTitle .\n" +
                                    "  ?contactTitle vcard:title ?contactTitleLabel  .\n" +
                                    "  ?person public:thumbnailImage ?directDownloadUrl .\n" +
                                    "} WHERE {\n" +
                                    "  { \n" +
                                    "    ?person a foaf:Person .\n" +
                                    "  } UNION { \n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person foaf:lastName ?lastName .\n" +
                                    "  } UNION { \n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person foaf:firstName ?firstName .\n" +
                                    "  } UNION { \n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person obo:ARG_2000028 ?contactInfo .\n" +
                                    "    ?contactInfo vcard:hasName ?contactName .\n" +
                                    "    ?contactName vcard:familyName ?familyName .\n" +
                                    "  } UNION { \n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person obo:ARG_2000028 ?contactInfo .\n" +
                                    "    ?contactInfo vcard:hasName ?contactName .\n" +
                                    "    ?contactName vcard:givenName ?givenName .\n" +
                                    "  } UNION { \n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person obo:ARG_2000028 ?contactInfo .\n" +
                                    "    ?contactInfo vcard:hasTitle ?contactTitle .\n" +
                                    "    ?contactTitle vcard:title ?contactTitleLabel .\n" +
                                    "  } UNION { \n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person public:mainImage ?mainImage .\n" +
                                    "    ?mainImage public:thumbnailImage ?thumbnailImage .\n" +
                                    "    ?thumbnailImage public:downloadLocation ?downloadLocation .\n" +
                                    "    ?downloadLocation public:directDownloadUrl ?directDownloadUrl .\n" +
                                    "  } \n" +
                                    "}\n";

                            Model constructedModel = ModelFactory.createDefaultModel();
                            rdfService.sparqlConstructQuery(construct, constructedModel);

                            String nameQuery = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?familyName ?givenName ?lastName ?firstName ?title ?thumbnailUrl\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  OPTIONAL {\n" +
                                    "    ?person obo:ARG_2000028 ?contactInfo .\n" +
                                    "    ?contactInfo vcard:hasName ?contactName .\n" +
                                    "    OPTIONAL { ?contactName vcard:familyName ?familyName . }\n" +
                                    "    OPTIONAL { ?contactName vcard:givenName  ?givenName . }\n" +
                                    "  }\n" +
                                    "  OPTIONAL {\n" +
                                    "    ?person obo:ARG_2000028 ?contactInfo .\n" +
                                    "    ?contactInfo vcard:hasTitle ?contactTitle .\n" +
                                    "    ?contactTitle vcard:title ?title .\n" +
                                    "  }\n" +
                                    "  OPTIONAL { ?person foaf:lastName ?lastName . }\n" +
                                    "  OPTIONAL { ?person foaf:firstName  ?firstName . }\n" +
                                    "  OPTIONAL { ?person public:thumbnailImage ?thumbnailUrl . }\n" +
                                    "}\n";

                            QueryExecution qe = QueryExecutionFactory.create(nameQuery, constructedModel);
                            try {
                                new ResultSetConsumer() {
                                    @Override
                                    protected void processQuerySolution(QuerySolution qs) {
                                        String personUri = qs.getResource("person").getURI();
                                        String familyName = null;
                                        String givenName = null;
                                        String thumbnailUrl = null;
                                        String title = null;

                                        Literal familyNameNode = qs.getLiteral("familyName");
                                        if (familyNameNode != null) {
                                            familyName = familyNameNode.getString();
                                        } else {
                                            Literal lastNameNode = qs.getLiteral("lastName");
                                            familyName = lastNameNode == null ? null : lastNameNode.getString();
                                        }

                                        Literal givenNameNode = qs.getLiteral("givenName");
                                        if (givenNameNode != null) {
                                            givenName = givenNameNode.getString();
                                        } else {
                                            Literal firstNameNode = qs.getLiteral("firstName");
                                            givenName = firstNameNode == null ? null : firstNameNode.getString();
                                        }

                                        Literal thumbnailUrlNode = qs.getLiteral("thumbnailUrl");
                                        thumbnailUrl = thumbnailUrlNode == null ? null : thumbnailUrlNode.getString();

                                        Literal titleNode = qs.getLiteral("title");
                                        title = titleNode == null ? null : titleNode.getString();

                                        Person person = map.get(personUri);
                                        if (person == null) {
                                            person = new Person();
                                            map.put(personUri.intern(), person);
                                        }

                                        person.firstName = givenName == null ? null : givenName.intern();
                                        person.lastName  = familyName == null ? null : familyName.intern();
                                        person.preferredTitle = title == null ? null : title.intern();
                                        person.thumbnailUrl = thumbnailUrl;
                                    }
                                }.processResultSet(qe.execSelect());
                            } finally {
                                qe.close();
                            }

                            return map;
                        }
                    }
            );

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

                                    map.put(org.intern(), orgLabel.intern());
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
                                        subOrgs.add(subOrg.intern());
                                        map.put(org.intern(), subOrgs);
                                    } else {
                                        subOrgs.add(subOrg.intern());
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
                                    map.put(org.intern(), typeLabel.intern());
                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Map of people within an organisation (org uri -> list of person uri)
     */
    public static final CachingRDFServiceExecutor<OrganizationPeopleMap> organisationToPeopleMap =
            new CachingRDFServiceExecutor<OrganizationPeopleMap>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<OrganizationPeopleMap>(visualizationAffinity) {
                        @Override
                        protected OrganizationPeopleMap callWithService(RDFService rdfService) throws Exception {
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

                            final OrganizationPeopleMap orgToPeopleMap = new OrganizationPeopleMap();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String org    = qs.getResource("organisation").getURI().intern();
                                    String person = qs.getResource("person").getURI().intern();

                                    Set<String> people = orgToPeopleMap.organizationToPeople.get(org);
                                    if (people == null) {
                                        people = new HashSet<String>();
                                        people.add(person);
                                        orgToPeopleMap.organizationToPeople.put(org, people);
                                    } else {
                                        people.add(person);
                                    }

                                    Set<String> organizations = orgToPeopleMap.personToOrganizations.get(org);
                                    if (organizations == null) {
                                        organizations = new HashSet<String>();
                                        organizations.add(org);
                                        orgToPeopleMap.organizationToPeople.put(person, organizations);
                                    } else {
                                        organizations.add(org);
                                    }
                                }
                            });

                            return orgToPeopleMap;
                        }
                    }
            );

    /**
     * Concept to label
     */
    public static final CachingRDFServiceExecutor<ConceptLabelMap> conceptToLabel =
            new CachingRDFServiceExecutor<>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<ConceptLabelMap>() {
                        @Override
                        protected ConceptLabelMap callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?concept ?label\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "    ?person a foaf:Person .\n" +
                                    "    ?person core:hasResearchArea ?concept .\n" +
                                    "    ?concept a skos:Concept .\n" +
                                    "    ?concept rdfs:label ?label .\n" +
                                    "}\n";

//                            final Map<String, String> map = new HashMap<>();
                            final ConceptLabelMap map = new ConceptLabelMap();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String conceptURI = qs.getResource("concept").getURI().intern();
                                    String label  = qs.getLiteral("label").getString().intern();
                                    String labelLower = label.toLowerCase().intern();

                                    map.conceptToLabel.put(conceptURI, label);

                                    Set<String> conceptSet = map.lowerLabelToConcepts.get(labelLower);
                                    if (conceptSet == null) {
                                        conceptSet = new HashSet<String>();
                                        conceptSet.add(conceptURI);
                                        map.lowerLabelToConcepts.put(labelLower, conceptSet);
                                    } else {
                                        conceptSet.add(conceptURI);
                                    }

                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Map of people associated with a concept
     */
    public static final CachingRDFServiceExecutor<ConceptPeopleMap> conceptToPeopleMap =
            new CachingRDFServiceExecutor<ConceptPeopleMap>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<ConceptPeopleMap>(visualizationAffinity) {
                        @Override
                        protected ConceptPeopleMap callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?concept\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  ?person core:hasResearchArea ?concept .\n" +
                                    "  ?concept a skos:Concept .\n" +
                                    "}\n";

                            final ConceptPeopleMap conceptPeopleMap = new ConceptPeopleMap();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String concept = qs.getResource("concept").getURI().intern();
                                    String person  = qs.getResource("person").getURI().intern();

                                    Set<String> people = conceptPeopleMap.conceptToPeople.get(concept);
                                    if (people == null) {
                                        people = new HashSet<String>();
                                        people.add(person);
                                        conceptPeopleMap.conceptToPeople.put(concept, people);
                                    } else {
                                        people.add(person);
                                    }

                                    Set<String> concepts = conceptPeopleMap.personToConcepts.get(person);
                                    if (concepts == null) {
                                        concepts = new HashSet<String>();
                                        concepts.add(concept);
                                        conceptPeopleMap.personToConcepts.put(person, concepts);
                                    } else {
                                        concepts.add(concept);
                                    }
                                }
                            });

                            return conceptPeopleMap;
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

                                    map.put(person.intern(), personLabel.intern());
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
                                    map.put(person.intern(), String.valueOf(typeLabel).intern());
                                }
                            });

                            return map;
                        }
                    }
            );

    /**
     * Person to publication Map (person uri -> list of publication uri)
     */
    public static final CachingRDFServiceExecutor<PersonPublicationMaps> personToPublication =
            new CachingRDFServiceExecutor<PersonPublicationMaps>(
                    new CachingRDFServiceExecutor.RDFServiceCallable<PersonPublicationMaps>(visualizationAffinity) {
                        @Override
                        protected PersonPublicationMaps callWithService(RDFService rdfService) throws Exception {
                            String query = QueryConstants.getSparqlPrefixQuery() +
                                    "SELECT ?person ?document\n" +
                                    "WHERE\n" +
                                    "{\n" +
                                    "  ?person a foaf:Person .\n" +
                                    "  ?person core:relatedBy ?authorship .\n" +
                                    "  ?authorship a core:Authorship .\n" +
                                    "  ?authorship core:relates ?document .\n" +
                                    "  ?document a <http://purl.obolibrary.org/obo/IAO_0000030> .\n" +
                                    "}\n";

                            final PersonPublicationMaps map = new PersonPublicationMaps();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    Resource person   = qs.getResource("person");
                                    Resource document = qs.getResource("document");

                                    if (person != null && document != null) {
                                        String personURI = person.getURI();
                                        String documentURI = document.getURI();

                                        map.put(personURI.intern(), documentURI.intern());
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
                                    "  ?document a <http://purl.obolibrary.org/obo/IAO_0000030> .\n" +
                                    "  ?document core:hasPublicationVenue ?journal . \n" +
                                    "  ?journal rdfs:label ?journalLabel . \n" +
                                    "}\n";

                            final Map<String, String> map = new HashMap<>();

                            rdfService.sparqlSelectQuery(query, new ResultSetConsumer() {
                                @Override
                                protected void processQuerySolution(QuerySolution qs) {
                                    String document      = qs.getResource("document").getURI();
                                    String journalLabel = qs.getLiteral("journalLabel").getString();

                                    map.put(document.intern(), journalLabel.intern());
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
                                    "  ?document a <http://purl.obolibrary.org/obo/IAO_0000030> .\n" +
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
                                            map.put(document.intern(), String.valueOf(validParsedDateTimeObject.getYear()).intern());
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
                                            documents.add(grant.getURI().intern());
                                            map.put(personURI.intern(), documents);
                                        } else {
                                            documents.add(grant.getURI().intern());
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
                                            map.put(grant.intern(), String.valueOf(validParsedDateTimeObject.getYear()).intern());
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
                                            map.put(grant.intern(), String.valueOf(validParsedDateTimeObject.getYear()).intern());
                                        }
                                    }
                                }
                            });

                            return map;
                        }
                    }
            );
}
