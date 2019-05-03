/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;



/**
 * This query runner is used to execute a sparql query that will fetch all the publications
 * defined by bibo:Document property for a particular individual.
 *
 * @author cdtank
 */
public class PersonPublicationCountQueryRunner implements QueryRunner<Set<Activity>> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String personURI;
	private RDFService rdfService;

	private String authorName;

	public String getAuthorName() {
		return authorName;
	}

	private Log log;

	public PersonPublicationCountQueryRunner(String personURI,
			RDFService rdfService, Log log) {

		this.personURI = personURI;
		this.rdfService = rdfService;
		this.log = log;

	}

	private String getSparqlConstruct(String queryURI) {
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
				+ "CONSTRUCT \n"
				+ "{\n"
				+ "    <" + queryURI + "> rdfs:label ?authorName .\n"
				+ "    <" + queryURI + "> core:authorOf ?document .\n"
				+ "    ?document vitro:mostSpecificType ?publicationType .\n"
				+ "    ?document core:publicationDate ?publicationDate .\n"
				+ "}\n"
				+ "WHERE"
				+ "{\n"
				+ "    {\n"
				+ "    <" + queryURI + "> rdf:type foaf:Person ;\n"
				+ "                       rdfs:label ?authorName ;  \n"
				+ "                       core:relatedBy ?authorshipNode .  \n"
				+ "    ?authorshipNode rdf:type core:Authorship ; \n"
				+ "                    core:relates ?document . \n"
				+ "	   ?document rdf:type bibo:Document ; \n"
				+ "              rdfs:label ?documentLabel ;\n"
				+ "              vitro:mostSpecificType ?publicationType .\n"
				+ "    } UNION {\n"
				+ "    <" + queryURI + "> rdf:type foaf:Person ;\n"
				+ "                       core:relatedBy ?authorshipNode .  \n"
				+ "    ?authorshipNode rdf:type core:Authorship ;"
				+ "                    core:relates ?document . \n"
				+ "	   ?document rdf:type bibo:Document . \n"
				+ "    ?document core:dateTimeValue ?dateTimeValue . \n"
				+ "    ?dateTimeValue core:dateTime ?publicationDate .\n"
				+ "    }\n"
				+ "}\n";

		log.debug(sparqlQuery);

		return sparqlQuery;
	}

	private String getSparqlQuery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ "SELECT ?document ?publicationType ?publicationDate\n"
							+ "WHERE { \n"
							+ "    <" + queryURI + "> core:authorOf ?document . \n"
							+ "	   OPTIONAL { ?document vitro:mostSpecificType ?publicationType . } .\n"
							+ "	   OPTIONAL { ?document core:publicationDate ?publicationDate . } .\n"
							+ "}\n";

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

		PersonPublicationConsumer consumer = new PersonPublicationConsumer();
		try {
			Model model = ModelFactory.createDefaultModel();
			rdfService.sparqlConstructQuery(getSparqlConstruct(this.personURI), model);

			Query q = QueryFactory.create(getSparqlQuery(this.personURI));
			QueryExecution qe = QueryExecutionFactory.create(q, model);
			try {
				consumer.processResultSet(qe.execSelect());

				Statement authorLabel = model.getProperty(ResourceFactory.createResource(this.personURI), RDFS.label);
				authorName = authorLabel.getObject().asLiteral().getString();
			} finally {
				qe.close();
			}
		} catch (RDFServiceException r) {
			throw new RuntimeException(r);
		}

		return consumer.getAuthorDocuments();
	}

	private static class PersonPublicationConsumer extends ResultSetConsumer {
		Set<Activity> authorDocuments = new HashSet<Activity>();

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			Activity biboDocument = new Activity(qs.get("document").asResource().getURI());

			RDFNode publicationDateNode = qs.get("publicationDate");
			if (publicationDateNode != null) {
				biboDocument.setActivityDate(publicationDateNode.asLiteral().getString());
			}

			RDFNode publicationType = qs.get("publicationType");
			if (publicationType != null) {
				biboDocument.setActivityType(publicationType.asResource().getURI());
			}

			authorDocuments.add(biboDocument);
		}

		public Set<Activity> getAuthorDocuments() {
			return authorDocuments;
		}
	}
}
