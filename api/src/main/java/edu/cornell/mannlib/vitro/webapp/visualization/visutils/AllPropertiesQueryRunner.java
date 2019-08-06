/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;



/**
 * This query runner is used to execute a sparql query that will fetch all the
 * properties  available for the provided individual URI.
 *
 * @author cdtank
 */
public class AllPropertiesQueryRunner implements QueryRunner<GenericQueryMap> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String filterRule, individualURI;
	private RDFService rdfService;

	private Log log = LogFactory.getLog(AllPropertiesQueryRunner.class.getName());

	public AllPropertiesQueryRunner(String individualURI,
							   String filterRule,
							   RDFService rdfService,
							   Log log) {

		this.individualURI = individualURI;
		this.filterRule = filterRule;
		this.rdfService = rdfService;
		this.log = log;

	}

	private String generateGenericSparqlQuery(String queryURI, String filterRule) {
//		Resource uri1 = ResourceFactory.createResource(queryURI);
		String filterClause;

		if (StringUtils.isNotBlank(filterRule)) {
			filterClause = "FILTER ( " + filterRule + " ) . ";
		} else {
			filterClause = "";
		}

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ "SELECT "
							+ "		(str(?predicate) as ?" + QueryFieldLabels.PREDICATE + ") "
							+ "		(str(?object) as ?" + QueryFieldLabels.OBJECT + ") "
							+ "WHERE { {"
							+ "<" + queryURI + "> ?predicate ?object.  }"
							+ "UNION {<" + queryURI + ">  <http://purl.obolibrary.org/obo/ARG_2000028> ?vCard . "
							+ "?vCard <http://www.w3.org/2006/vcard/ns#hasTitle> ?vTitle . "
							+ "?vTitle ?predicate ?object . }"
							+ filterClause
							+ "}";

        log.debug("sparqlQuery = " + sparqlQuery);

		return sparqlQuery;
	}

	public GenericQueryMap getQueryResult()
			throws MalformedQueryParametersException {
		if (StringUtils.isNotBlank(this.individualURI)) {
        	/*
        	 * To test for the validity of the URI submitted.
        	 * */
        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
    		IRI iri = iRIFactory.create(this.individualURI);
            if (iri.hasViolation(false)) {
                String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
                log.error("Generic Query " + errorMsg);
                throw new MalformedQueryParametersException(
                			"URI provided for an individual is malformed.");
            }

        } else {
            throw new MalformedQueryParametersException("URI parameter is either null or empty.");
        }

		CreateJavaVOConsumer consumer = new CreateJavaVOConsumer();
		try {
			rdfService.sparqlSelectQuery(generateGenericSparqlQuery(
					this.individualURI,
					this.filterRule),
					consumer);
		} catch (RDFServiceException e) {
			log.error("Unable to execute query", e);
			throw new MalformedQueryParametersException(e);
		}
		return consumer.getMap();
	}

	private class CreateJavaVOConsumer extends ResultSetConsumer {
		GenericQueryMap queryResult = new GenericQueryMap();

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			RDFNode predicateNode = qs.get(QueryFieldLabels.PREDICATE);
			RDFNode objectNode = qs.get(QueryFieldLabels.OBJECT);

			if (predicateNode != null && objectNode != null) {
				queryResult.addEntry(predicateNode.toString(),
						objectNode.toString());
			}
		}

		public GenericQueryMap getMap() {
			return queryResult;
		}
	}
}
