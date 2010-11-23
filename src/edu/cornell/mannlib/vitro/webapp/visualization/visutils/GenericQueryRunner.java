/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.DataSource;
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

	private String whereClause, individualURLParam;
	private DataSource dataSource;

	private Log log;

	private Map<String, String> fieldLabelToOutputFieldLabel;

	public GenericQueryRunner(String individualURLParam,
							   Map<String, String> fieldLabelToOutputFieldLabel, 
							   String whereClause,
							   DataSource dataSource, 
							   Log log) {

		this.individualURLParam = individualURLParam;
		this.fieldLabelToOutputFieldLabel = fieldLabelToOutputFieldLabel;
		this.whereClause = whereClause;
		this.dataSource = dataSource;
		this.log = log;
		
	}

	private ResultSet executeQuery(String queryText,
								   DataSource dataSource) {

        QueryExecution queryExecution = null;
//        try {
            Query query = QueryFactory.create(queryText, SYNTAX);

//            QuerySolutionMap qs = new QuerySolutionMap();
//            qs.add("authPerson", queryParam); // bind resource to s
            
            queryExecution = QueryExecutionFactory.create(query, dataSource);
            

            //remocve this if loop after knowing what is describe & construct sparql stuff.
//            if (query.isSelectType()) {
                return queryExecution.execSelect();
//            }
//        } finally {
//            if (queryExecution != null) {
//            	queryExecution.close();
//            }
//
//        }
//		return null;
    }

	private String generateGenericSparqlQuery() {
//		Resource uri1 = ResourceFactory.createResource(queryURI);

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append(QueryConstants.getSparqlPrefixQuery());
		
		sparqlQuery.append("SELECT\n");
		
		for (Map.Entry<String, String> currentfieldLabelToOutputFieldLabel 
				: this.fieldLabelToOutputFieldLabel.entrySet()) {
			
			sparqlQuery.append("\t(str(?" + currentfieldLabelToOutputFieldLabel.getKey() + ") as ?" 
									+ currentfieldLabelToOutputFieldLabel.getValue() + ")\n");
			
		}
		
		sparqlQuery.append("WHERE {\n");
		
		sparqlQuery.append(this.whereClause);
		
		sparqlQuery.append("}\n");
		
		return sparqlQuery.toString();
	}
	
	public ResultSet getQueryResult()
			throws MalformedQueryParametersException {
		if (StringUtils.isNotBlank(this.individualURLParam)) {
        	/*
        	 * To test for the validity of the URI submitted.
        	 * */
        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
    		IRI iri = iRIFactory.create(this.individualURLParam);
            if (iri.hasViolation(false)) {
                String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
                log.error("Generic Query " + errorMsg);
                throw new MalformedQueryParametersException(
                		"URI provided for an individual is malformed.");
            }        	
        } else {
            throw new MalformedQueryParametersException("URI parameter is either null or empty.");
        }

		ResultSet resultSet	= executeQuery(generateGenericSparqlQuery(),
										   this.dataSource);

		return resultSet;
	}
}
