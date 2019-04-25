/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.persongrantcount;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

import java.util.HashSet;
import java.util.Set;


/**
 * This query runner is used to execute a sparql query that will fetch all the publications
 * defined by bibo:Document property for a particular individual.
 *
 * @author cdtank
 */
public class PersonGrantCountQueryRunner implements QueryRunner<Set<Activity>> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String personURI;
	private RDFService rdfService;

	private Log log;

	public PersonGrantCountQueryRunner(String personURI,
									   RDFService rdfService, Log log) {

		this.personURI = personURI;
		this.rdfService = rdfService;
		this.log = log;
	}

	private String getSparqlConstruct(String queryURI) {
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
				+ "CONSTRUCT \n"
				+ "{\n"
				+ "    <" + queryURI + "> core:investigatorOn ?grant .\n"
				+ "    ?grant core:roleDate  ?roleDate .\n"
				+ "    ?grant core:grantDate ?grantDate .\n"
				+ "}\n"
				+ "WHERE"
				+ "{\n"
				+ "    {\n"
				+ "    <" + queryURI + "> rdf:type foaf:Person ;\n"
				+ "                       <http://purl.obolibrary.org/obo/RO_0000053> ?role .  \n"
				+ "    ?role core:relatedBy ?grant . \n"
				+ "	   ?grant rdf:type core:Grant ; \n"
				+ "              rdfs:label ?grantLabel .\n"
				+ "    } UNION {\n"
				+ "    <" + queryURI + "> rdf:type foaf:Person ;\n"
				+ "                       <http://purl.obolibrary.org/obo/RO_0000053> ?role .  \n"
				+ "    ?role core:relatedBy ?grant . \n"
				+ "	   ?grant rdf:type core:Grant .\n"
				+ "    ?role core:dateTimeInterval ?dateTimeInterval . \n"
				+ "    ?dateTimeInterval core:start ?dateTimeValue . \n"
				+ "    ?dateTimeValue core:dateTime ?roleDate .\n"
				+ "    } UNION {\n"
				+ "    <" + queryURI + "> rdf:type foaf:Person ;\n"
				+ "                       <http://purl.obolibrary.org/obo/RO_0000053> ?role .  \n"
				+ "    ?role core:relatedBy ?grant . \n"
				+ "	   ?grant rdf:type core:Grant .\n"
				+ "    ?grant core:dateTimeInterval ?dateTimeInterval . \n"
				+ "    ?dateTimeInterval core:start ?dateTimeValue . \n"
				+ "    ?dateTimeValue core:dateTime ?grantDate .\n"
				+ "    }\n"
				+ "}\n";


//		Set<String> investigatorRoleQuery = constructPersonGrantsQueryTemplate("hasGrantAsAnInvestigator", "InvestigatorRole");
//		Set<String> piRoleQuery = constructPersonGrantsQueryTemplate("hasGrantAsPI", "PrincipalInvestigatorRole");
//		Set<String> coPIRoleQuery = constructPersonGrantsQueryTemplate("hasGrantAsCoPI", "CoPrincipalInvestigatorRole");

		log.debug(sparqlQuery);

		return sparqlQuery;
	}

	private String getSparqlQuery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ "SELECT DISTINCT ?grant (SAMPLE(?sampleRoleDate) AS ?roleDate) (SAMPLE(?sampleGrantDate) AS ?grantDate)\n"
							+ "WHERE { \n"
							+ "    <" + queryURI + "> core:investigatorOn ?grant . \n"
							+ "	   OPTIONAL { ?grant core:roleDate ?sampleRoleDate . } .\n"
							+ "	   OPTIONAL { ?grant core:grantDate ?sampleGrantDate . } .\n"
							+ "} GROUP BY ?grant\n";

		log.debug(sparqlQuery);

		return sparqlQuery;
	}

	public Set<Activity> getQueryResult()
		throws MalformedQueryParametersException {

        if (StringUtils.isNotBlank(this.personURI)) {

        	/*
        	 * To test for the validity of the URI submitted.
        	 * */
        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
    		IRI iri = iRIFactory.create(this.personURI);
            if (iri.hasViolation(false)) {
                String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
                log.error("Pub Count vis Query " + errorMsg);
                throw new MalformedQueryParametersException(
                		"URI provided for an individual is malformed.");
            }

        } else {
        	throw new MalformedQueryParametersException("URL parameter is either null or empty.");
        }

		PersonGrantConsumer consumer = new PersonGrantConsumer();
		try {
			Model model = ModelFactory.createDefaultModel();
			rdfService.sparqlConstructQuery(getSparqlConstruct(this.personURI), model);

			Query q = QueryFactory.create(getSparqlQuery(this.personURI));
			QueryExecution qe = QueryExecutionFactory.create(q, model);
			try {
				consumer.processResultSet(qe.execSelect());
			} finally {
				qe.close();
			}
		} catch (RDFServiceException r) {
			throw new RuntimeException(r);
		}

		return consumer.getAuthorGrants();
	}

	private static class PersonGrantConsumer extends ResultSetConsumer {
		Set<Activity> authorGrants = new HashSet<Activity>();

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			Activity biboDocument = new Activity(qs.get("grant").asResource().getURI());

			RDFNode roleDateNode = qs.get("roleDate");
			if (roleDateNode != null) {
				biboDocument.setActivityDate(roleDateNode.asLiteral().getString());
			} else {
				RDFNode grantDateNode = qs.get("grantDate");
				if (grantDateNode != null) {
					biboDocument.setActivityDate(grantDateNode.asLiteral().getString());
				}
			}

			authorGrants.add(biboDocument);
		}

		public Set<Activity> getAuthorGrants() {
			return authorGrants;
		}
	}
}
