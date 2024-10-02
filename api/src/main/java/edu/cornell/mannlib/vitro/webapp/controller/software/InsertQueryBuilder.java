package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class InsertQueryBuilder {

    private static final Log log = LogFactory.getLog(InsertQueryBuilder.class);

    private final StringBuilder query = new StringBuilder();

    public InsertQueryBuilder() {
    }


    public String build() {
        return query.toString();
    }

    public InsertQueryBuilder startInsertQuery() {
        IndividualApiSparqlUtility.addPrefixClauses(query);
        query.append("\n")
            .append("INSERT DATA\n")
            .append("{\n")
            .append("GRAPH ").append("<" + ModelNames.ABOX_ASSERTIONS + ">")
            .append("\n")
            .append("{\n");

        return this;
    }

    public InsertQueryBuilder addPublicationDate(String dateString, String defaultNamespace,
                                                 String documentUri) {
        if (dateString != null && !dateString.isEmpty()) {
            String dateObjectUri = defaultNamespace + UUID.randomUUID();

            query.append("<").append(documentUri).append("> vivo:dateTimeValue <")
                .append(dateObjectUri).append("> .\n")
                .append("<").append(dateObjectUri).append("> rdf:type vivo:DateTimeValue ;\n")
                .append("vivo:dateTime \"").append(StringEscapeUtils.escapeJava(dateString)).append("\"^^xsd:date .\n");
        }

        return this;
    }

    public InsertQueryBuilder addAuthors(List<AuthorDTO> authors, String defaultNamespace, String documentUri,
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

        return this;
    }

    public InsertQueryBuilder addFunding(List<String> fundings, String defaultNamespace,
                                         String documentUri) {
        for (String funding : fundings) {
            String funderObjectUri = defaultNamespace + UUID.randomUUID();

            query.append("<").append(documentUri).append("> vivo:informationResourceSupportedBy <")
                .append(funderObjectUri).append("> .\n")
                .append("<").append(funderObjectUri).append("> rdf:type vivo:Funding ;\n");

            query.append("rdfs:label \"").append(StringEscapeUtils.escapeJava(funding)).append("\"@en-US .\n");
        }

        return this;
    }

    public InsertQueryBuilder addFunders(List<FunderRequestDTO> funders, String defaultNamespace, String documentUri) {
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

        return this;
    }

    public StringBuilder getInsertQuery() {
        return query;
    }
}
