package edu.cornell.mannlib.vivo.orcid.export;

public class OrcidExportQueries {

    public static String FIND_ALL_EDUCATION =
        "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:       <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd:        <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX owl:        <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX vitro:      <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#>\n" +
            "PREFIX foaf:       <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX obo:        <http://purl.obolibrary.org/obo/>\n" +
            "PREFIX vivo:       <http://vivoweb.org/ontology/core#>\n" +
            "PREFIX vcard:      <http://www.w3.org/2006/vcard/ns#>\n" +
            "PREFIX bibo:       <http://purl.org/ontology/bibo/>\n" +
            "PREFIX dcterms:    <http://purl.org/dc/terms/>\n" +
            "\n" +
            "SELECT ?resource\n" +
            "       ?startDate\n" +
            "       ?endDate\n" +
            "       ?position\n" +
            "       ?department\n" +
            "       ?institution\n" +
            "       ?institutionName\n" +
            "       ?city\n" +
            "       ?region\n" +
            "       ?country\n" +
            "       ?urlValue\n" +
            "       ?ror\n" +
            "WHERE {\n" +
            "    <%s> obo:RO_0000056 ?resource .\n" +
            "\n" +
            "    FILTER (STR(?resource) > \"%s\")" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource obo:RO_0002234 ?output .\n" +
            "        ?output rdfs:label ?position .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:departmentOrSchool ?department .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource obo:RO_0000057 ?institution .\n" +
            "        ?institution a foaf:Organization .\n" +
            "        ?institution rdfs:label ?institutionName .\n" +
            "\n" +
            "        OPTIONAL {\n" +
            "            ?institution vivo:rorId ?ror .\n" +
            "        }\n" +
            "\n" +
            "        OPTIONAL {\n" +
            "            ?institution obo:ARG_2000028 ?vcardIndividual .\n" +
            "            ?vcardIndividual vcard:hasAddress ?address .\n" +
            "            \n" +
            "            OPTIONAL { ?address vcard:locality ?city . }\n" +
            "            OPTIONAL { ?address vcard:region ?region . }\n" +
            "            OPTIONAL { ?address vcard:country ?country . }\n" +
            "        }\n" +
            "\n" +
            "        OPTIONAL {\n" +
            "            ?institution obo:ARG_2000028 ?vcardInd .\n" +
            "            ?vcardInd vcard:hasURL ?urlNode .\n" +
            "            ?urlNode vcard:url ?urlValue .\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:dateTimeInterval ?interval .\n" +
            "        \n" +
            "        OPTIONAL { \n" +
            "            ?interval vivo:start ?startDT .\n" +
            "            ?startDT vivo:dateTime ?startDate .\n" +
            "        }\n" +
            "        \n" +
            "        OPTIONAL {\n" +
            "            ?interval vivo:end ?endDT .\n" +
            "            ?endDT vivo:dateTime ?endDate .\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "ORDER BY ?resource\n" +
            "LIMIT %s";

    public static String FIND_ALL_EMPLOYMENTS =
        "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:       <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX foaf:       <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX obo:        <http://purl.obolibrary.org/obo/>\n" +
            "PREFIX vivo:       <http://vivoweb.org/ontology/core#>\n" +
            "PREFIX vcard:      <http://www.w3.org/2006/vcard/ns#>\n" +
            "PREFIX vitro:      <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#>\n" +
            "\n" +
            "SELECT ?resource\n" +
            "       ?position\n" +
            "       ?institution\n" +
            "       ?institutionName\n" +
            "       ?startDate\n" +
            "       ?endDate\n" +
            "       ?city\n" +
            "       ?region\n" +
            "       ?country\n" +
            "       ?urlValue\n" +
            "       ?ror\n" +
            "WHERE {\n" +
            "    <%s> vivo:relatedBy ?resource .\n" +
            "\n" +
            "    FILTER (STR(?resource) > \"%s\")" +
            "\n" +
            "    ?resource vitro:mostSpecificType vivo:FacultyPosition .\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource rdfs:label ?position .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:relates ?institution .\n" +
            "        ?institution a foaf:Organization .\n" +
            "        ?institution rdfs:label ?institutionName .\n" +
            "\n" +
            "        OPTIONAL {\n" +
            "            ?institution vivo:rorId ?ror .\n" +
            "        }\n" +
            "\n" +
            "        OPTIONAL {\n" +
            "            ?institution obo:ARG_2000028 ?vcardIndividual .\n" +
            "            ?vcardIndividual vcard:hasAddress ?address .\n" +
            "            \n" +
            "            OPTIONAL { ?address vcard:locality ?city . }\n" +
            "            OPTIONAL { ?address vcard:region ?region . }\n" +
            "            OPTIONAL { ?address vcard:country ?country . }\n" +
            "        }\n" +
            "\n" +
            "        OPTIONAL {\n" +
            "            ?institution obo:ARG_2000028 ?vcardInd .\n" +
            "            ?vcardInd vcard:hasURL ?urlNode .\n" +
            "            ?urlNode vcard:url ?urlValue .\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:dateTimeInterval ?interval .\n" +
            "    \n" +
            "        OPTIONAL { \n" +
            "            ?interval vivo:start ?startDT .\n" +
            "            ?startDT vivo:dateTime ?startDate .\n" +
            "        }\n" +
            "        \n" +
            "        OPTIONAL {\n" +
            "            ?interval vivo:end ?endDT .\n" +
            "            ?endDT vivo:dateTime ?endDate .\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "ORDER BY ?resource\n" +
            "LIMIT %s";

    public static String FIND_ALL_WORKS =
        "PREFIX rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:     <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX vivo:     <http://vivoweb.org/ontology/core#>\n" +
            "PREFIX bibo:     <http://purl.org/ontology/bibo/>\n" +
            "PREFIX obo:      <http://purl.obolibrary.org/obo/>\n" +
            "PREFIX vcard:    <http://www.w3.org/2006/vcard/ns#>\n" +
            "PREFIX foaf:     <http://xmlns.com/foaf/0.1/>\n" +
            "\n" +
            "SELECT ?resource\n" +
            "       ?resourceLabel\n" +
            "       ?workType\n" +
            "       ?journal\n" +
            "       ?journalLabel\n" +
            "       ?magazine\n" +
            "       ?newspaper\n" +
            "       ?publicationDate\n" +
            "       ?urlValue\n" +
            "       ?abstract\n" +
            "       ?performance\n" +
            "       ?doi\n" +
            "       (GROUP_CONCAT(?authorEmailWithDefault; SEPARATOR=\"; \") AS ?authorEmails)\n" +
            "       (GROUP_CONCAT(?authorName; SEPARATOR=\"; \") AS ?authors)\n" +
            "       (GROUP_CONCAT(?authorRole; SEPARATOR=\"; \") AS ?authorRoles)\n" +
            "       (GROUP_CONCAT(?authorRank; SEPARATOR=\"; \") AS ?authorRanks)\n" +
            "       (GROUP_CONCAT(?orcidIdWithDefault; SEPARATOR=\"; \") AS ?authorOrcidIds)\n" +
            "WHERE {\n" +
            "  \n" +
            "    {\n" +
            "        SELECT DISTINCT ?resource ?workType ?resourceLabel\n" +
            "        WHERE {\n" +
            "            <%s> vivo:relatedBy ?authorship .\n" +
            "            ?authorship vivo:relates ?resource .\n" +
            "            ?resource <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> ?workType .\n" +
            "            ?resource rdf:type bibo:Document .\n" +
            "            OPTIONAL { ?resource rdfs:label ?resourceLabel . }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    FILTER (STR(?resource) > \"%s\")" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:hasPublicationVenue ?journal .\n" +
            "        ?journal rdf:type bibo:Journal .\n" +
            "        ?journal rdfs:label ?journalLabel .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:hasPublicationVenue ?magazine .\n" +
            "        ?magazine rdf:type bibo:Magazine .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:hasPublicationVenue ?newspaper .\n" +
            "        ?newspaper rdf:type bibo:Newspaper .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:dateTimeValue ?dtValue .\n" +
            "        ?dtValue vivo:dateTime ?publicationDate .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource obo:ARG_2000028 ?vcardInd .\n" +
            "        ?vcardInd vcard:hasURL ?urlNode .\n" +
            "        ?urlNode vcard:url ?urlValue .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource bibo:abstract ?abstract .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource bibo:doi ?doi .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource obo:RO_0002353 ?performance .\n" +
            "        ?performance a bibo:Performance .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:relatedBy ?anyAuthorship .\n" +
            "        ?anyAuthorship vivo:relates ?author .\n" +
            "        ?author a foaf:Person .\n" +
            "        OPTIONAL { ?author rdfs:label ?authorName . }\n" +
            "    \n" +
            "        OPTIONAL {\n" +
            "            ?author obo:ARG_2000028 ?vcardIndividual .\n" +
            "            ?vcardIndividual vcard:hasEmail ?emailNode .\n" +
            "            ?emailNode vcard:email ?authorEmail .\n" +
            "            OPTIONAL { ?emailNode vcard:type ?emailType . }\n" +
            "        }\n" +
            "    \n" +
            "        BIND(COALESCE(?authorEmail, \"NONE\") AS ?authorEmailWithDefault)\n" +
            "        \n" +
            "        OPTIONAL { \n" +
            "            ?anyAuthorship a ?authorRole .\n" +
            "            FILTER(?authorRole = vivo:Authorship || ?authorRole = vivo:Editorship)\n" +
            "        }\n" +
            "        \n" +
            "        OPTIONAL { ?anyAuthorship vivo:rank ?authorRank . }\n" +
            "        \n" +
            "        OPTIONAL {\n" +
            "            ?author vivo:orcidId ?orcidNode .\n" +
            "            OPTIONAL { ?orcidNode vivo:confirmedOrcidId ?orcidId . }\n" +
            "        }\n" +
            "        \n" +
            "        BIND(COALESCE(?orcidNode, \"NONE\") AS ?orcidIdWithDefault)\n" +
            "    }\n" +
            "}\n" +
            "GROUP BY ?resource ?resourceLabel ?journal ?journalLabel " +
            "?publicationDate ?urlValue ?workType ?abstract ?performance " +
            "?doi ?magazine ?newspaper\n" +
            "ORDER BY ?resource\n" +
            "LIMIT %s";
}
