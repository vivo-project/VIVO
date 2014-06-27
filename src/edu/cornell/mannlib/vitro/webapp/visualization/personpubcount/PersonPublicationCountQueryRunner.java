/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import java.util.HashSet;
import java.util.Set;

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
	private Dataset dataset;

	private Individual author; 

	public Individual getAuthor() {
		return author;
	}

	private Log log;

	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = "" 
		+ "SELECT (str(?authorLabel) as ?" + QueryFieldLabels.AUTHOR_LABEL + ") \n" 
		+ "		(str(?document) as ?" + QueryFieldLabels.DOCUMENT_URL + ") \n"
		+ "		(str(?publicationDate) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ")\n";

	private static final String SPARQL_QUERY_COMMON_WHERE_CLAUSE = "" 
			+ "?document rdfs:label ?documentLabel .\n" 
			+ "OPTIONAL {  ?document core:dateTimeValue ?dateTimeValue . \n" 
			+ "				?dateTimeValue core:dateTime ?publicationDate } .\n";
	
	public PersonPublicationCountQueryRunner(String personURI,
			Dataset dataset, Log log) {

		this.personURI = personURI;
		this.dataset = dataset;
		this.log = log;

	}

	private Set<Activity> createJavaValueObjects(ResultSet resultSet) {
		Set<Activity> authorDocuments = new HashSet<Activity>();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();

			Activity biboDocument = new Activity(
											solution.get(QueryFieldLabels.DOCUMENT_URL)
												.toString());

			RDFNode publicationDateNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			if (publicationDateNode != null) {
				biboDocument.setActivityDate(publicationDateNode.toString());
			}

			/*
			 * Since we are getting publication count for just one author at a time we need
			 * to create only one "Individual" instance. We test against the null for "author" to
			 * make sure that it has not already been instantiated. 
			 * */
			RDFNode authorURLNode = solution.get(QueryFieldLabels.AUTHOR_URL);
			if (authorURLNode != null && author == null) {
				author = new Individual(authorURLNode.toString());
				RDFNode authorLabelNode = solution.get(QueryFieldLabels.AUTHOR_LABEL);
				if (authorLabelNode != null) {
					author.setIndividualLabel(authorLabelNode.toString());
				}
			}

			authorDocuments.add(biboDocument);
		}
		return authorDocuments;
	}

	private ResultSet executeQuery(String queryURI,
            Dataset dataset) {

        QueryExecution queryExecution = null;
        Query query = QueryFactory.create(getSparqlQuery(queryURI), SYNTAX);
        queryExecution = QueryExecutionFactory.create(query, dataset);
        return queryExecution.execSelect();
    }

	private String getSparqlQuery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ SPARQL_QUERY_COMMON_SELECT_CLAUSE
							+ "(str(<" + queryURI + ">) as ?authPersonLit)\n "
							+ "WHERE { \n"
							+ "<" + queryURI + "> rdf:type foaf:Person ;\n" 
							+ 					" rdfs:label ?authorLabel \n;" 
							+ 					" core:relatedBy ?authorshipNode .  \n" 
							+ "	?authorshipNode rdf:type core:Authorship ;" 
							+ 					" core:relates ?document . \n"
							+ "	?document rdf:type bibo:Document . \n" 
							+  SPARQL_QUERY_COMMON_WHERE_CLAUSE
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

		ResultSet resultSet	= executeQuery(this.personURI,
										   this.dataset);

		return createJavaValueObjects(resultSet);
	}

}
