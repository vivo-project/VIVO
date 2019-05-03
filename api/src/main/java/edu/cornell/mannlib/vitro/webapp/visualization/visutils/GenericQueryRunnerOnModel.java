/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;



/**
 * This query runner is used to run a generic sparql query based on the "select",
 * "where" & "filter" rules provided to it.
 *
 * @author cdtank
 */
public class GenericQueryRunnerOnModel implements QueryRunner<ResultSet> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String whereClause;
	private Model model;

	private Map<String, String> fieldLabelToOutputFieldLabel;

	private Log log = LogFactory.getLog(GenericQueryRunnerOnModel.class.getName());

	private String groupOrderClause;

	private String aggregationRules;

	public GenericQueryRunnerOnModel(Map<String, String> fieldLabelToOutputFieldLabel,
							   String aggregationRules,
							   String whereClause,
							   String groupOrderClause,
							   Model model) {

		this.fieldLabelToOutputFieldLabel = fieldLabelToOutputFieldLabel;
		this.aggregationRules = aggregationRules;
		this.whereClause = whereClause;
		this.groupOrderClause = groupOrderClause;
		this.model = model;
	}

	private ResultSet executeQuery(String queryText,
								   Model dataset) {

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
										   this.model);

		return resultSet;
	}
}
