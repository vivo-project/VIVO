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
            "       ?startValue\n" +
            "       ?endValue\n" +
            "       ?position\n" +
            "       ?department\n" +
            "       ?institutionName\n" +
            "       ?city\n" +
            "       ?region\n" +
            "       ?country\n" +
            "       ?urlValue\n" +
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
            "            ?institution obo:ARG_2000028 ?vcardIndividual .\n" +
            "            ?vcardIndividual vcard:hasAddress ?address .\n" +
            "            \n" +
            "            OPTIONAL { ?address vcard:locality ?city . }\n" +
            "            OPTIONAL { ?address vcard:region ?region . }\n" +
            "            OPTIONAL { ?address vcard:country-name ?country . }\n" +
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
            "            ?startDT vivo:dateTime ?startValue .\n" +
            "        }\n" +
            "        \n" +
            "        OPTIONAL {\n" +
            "            ?interval vivo:end ?endDT .\n" +
            "            ?endDT vivo:dateTime ?endValue .\n" +
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
            "       ?positionName\n" +
            "       ?institutionName\n" +
            "       ?startValue\n" +
            "       ?endValue\n" +
            "       ?city\n" +
            "       ?region\n" +
            "       ?country\n" +
            "       ?urlValue\n" +
            "WHERE {\n" +
            "    <%s> vivo:relatedBy ?resource .\n" +
            "\n" +
            "    FILTER (STR(?resource) > \"%s\")" +
            "\n" +
            "    ?resource vitro:mostSpecificType vivo:FacultyPosition .\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource rdfs:label ?positionName .\n" +
            "    }\n" +
            "\n" +
            "    OPTIONAL {\n" +
            "        ?resource vivo:relates ?institution .\n" +
            "        ?institution a foaf:Organization .\n" +
            "        ?institution rdfs:label ?institutionName .\n" +
            "\n" +
            "        OPTIONAL {\n" +
            "            ?institution obo:ARG_2000028 ?vcardIndividual .\n" +
            "            ?vcardIndividual vcard:hasAddress ?address .\n" +
            "            \n" +
            "            OPTIONAL { ?address vcard:locality ?city . }\n" +
            "            OPTIONAL { ?address vcard:region ?region . }\n" +
            "            OPTIONAL { ?address vcard:country-name ?country . }\n" +
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
            "            ?startDT vivo:dateTime ?startValue .\n" +
            "        }\n" +
            "        \n" +
            "        OPTIONAL {\n" +
            "            ?interval vivo:end ?endDT .\n" +
            "            ?endDT vivo:dateTime ?endValue .\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "ORDER BY ?resource\n" +
            "LIMIT %s";

    public static String FIND_ALL_WORKS = "";
}
