/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

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
			
			sparqlQuery.append("\t(str(?" + currentfieldLabelToOutputFieldLabel.getKey() + ") as ?" 
									+ currentfieldLabelToOutputFieldLabel.getValue() + ")\n");
			
		}
		
		sparqlQuery.append("\n" + this.aggregationRules + "\n");
		
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
}
