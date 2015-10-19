/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import java.util.HashSet;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;
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

	private String getSparqlQuery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ "SELECT ?authorName ?document ?publicationDate\n"
							+ "WHERE { \n"
							+ "    <" + queryURI + "> rdf:type foaf:Person ;\n"
							+ "                       rdfs:label ?authorName ;  \n"
							+ "                       core:relatedBy ?authorshipNode .  \n"
							+ "    ?authorshipNode rdf:type core:Authorship ;"
							+ "                    core:relates ?document . \n"
							+ "	   ?document rdf:type bibo:Document . \n"
							+ "    ?document rdfs:label ?documentLabel .\n"
							+ "    OPTIONAL { ?document core:dateTimeValue ?dateTimeValue . OPTIONAL { ?dateTimeValue core:dateTime ?publicationDate } } .\n"
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
			rdfService.sparqlSelectQuery(getSparqlQuery(this.personURI), consumer);
		} catch (RDFServiceException r) {
			throw new RuntimeException(r);
		}

		authorName = consumer.getAuthorName();
		return consumer.getAuthorDocuments();
	}

	private static class PersonPublicationConsumer extends ResultSetConsumer {
		Set<Activity> authorDocuments = new HashSet<Activity>();
		String authorName = null;

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			Activity biboDocument = new Activity(qs.get("document").asResource().getURI());

			RDFNode publicationDateNode = qs.get("publicationDate");
			if (publicationDateNode != null) {
				biboDocument.setActivityDate(publicationDateNode.asLiteral().getString());
			}

			if (authorName == null) {
				RDFNode authorNameNode = qs.get("authorName");
				if (authorNameNode != null) {
					authorName = authorNameNode.asLiteral().getString();
				}
			}

			authorDocuments.add(biboDocument);
		}

		public Set<Activity> getAuthorDocuments() {
			return authorDocuments;
		}

		public String getAuthorName() {
			return authorName;
		}
	}
}
