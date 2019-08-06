/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;



/**
 * This query runner is used to run a generic sparql query based on the "select",
 * "where" & "filter" rules provided to it.
 *
 * @author cdtank
 */
public class GenericQueryRunner implements QueryRunner<ResultSet> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String whereClause;
	private Dataset dataset;

	private Map<String, String> fieldLabelToOutputFieldLabel;

	private Log log = LogFactory.getLog(GenericQueryRunner.class.getName());

	private String groupOrderClause;

	private String aggregationRules;

	public GenericQueryRunner(Map<String, String> fieldLabelToOutputFieldLabel,
							   String aggregationRules,
							   String whereClause,
							   String groupOrderClause,
							   Dataset dataset) {

		this.fieldLabelToOutputFieldLabel = fieldLabelToOutputFieldLabel;
		this.aggregationRules = aggregationRules;
		this.whereClause = whereClause;
		this.groupOrderClause = groupOrderClause;
		this.dataset = dataset;
	}

	private ResultSet executeQuery(String queryText,
								   Dataset dataset) {

        QueryExecution queryExecution = null;
        Query query = QueryFactory.create(queryText, SYNTAX);
        queryExecution = QueryExecutionFactory.create(query, dataset);
        return queryExecution.execSelect();
    }

	private String generateGenericSparqlQuery() {

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append(QueryConstants.getSparqlPrefixQuery());

		sparqlQuery.append("SELECT\n");

		for (Map.Entry<String, String> currentfieldLabelToOutputFieldLabel
				: this.fieldLabelToOutputFieldLabel.entrySet()) {

			sparqlQuery.append("\t(str(?").append(currentfieldLabelToOutputFieldLabel.getKey()).append(") as ?").append(currentfieldLabelToOutputFieldLabel.getValue()).append(")\n");

		}

		sparqlQuery.append("\n").append(this.aggregationRules).append("\n");

		sparqlQuery.append("WHERE {\n");

		sparqlQuery.append(this.whereClause);

		sparqlQuery.append("}\n");

		sparqlQuery.append(this.groupOrderClause);

		log.debug("sparqlQuery = " + sparqlQuery.toString());

		return sparqlQuery.toString();
	}

	public ResultSet getQueryResult()
			throws MalformedQueryParametersException {

		ResultSet resultSet	= executeQuery(generateGenericSparqlQuery(),
										   this.dataset);

		return resultSet;
	}

	public void sparqlSelectQuery(RDFService rdfService, ResultSetConsumer consumer) throws MalformedQueryParametersException {
		try {
			rdfService.sparqlSelectQuery(generateGenericSparqlQuery(), consumer);
		} catch (RDFServiceException e) {
			log.error("Unable to execute: [" + generateGenericSparqlQuery() + "]", e);
			throw new MalformedQueryParametersException(e);
		}
	}
}
