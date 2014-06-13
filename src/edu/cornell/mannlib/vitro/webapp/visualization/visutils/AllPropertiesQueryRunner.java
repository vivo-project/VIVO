/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private Dataset dataset;

	private Log log = LogFactory.getLog(AllPropertiesQueryRunner.class.getName());

	public AllPropertiesQueryRunner(String individualURI,
							   String filterRule,
							   Dataset dataset, 
							   Log log) {

		this.individualURI = individualURI;
		this.filterRule = filterRule;
		this.dataset = dataset;
		this.log = log;
		
	}

	private GenericQueryMap createJavaValueObjects(ResultSet resultSet) {
		
		GenericQueryMap queryResult = new GenericQueryMap();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			
			
			RDFNode predicateNode = solution.get(QueryFieldLabels.PREDICATE);
			RDFNode objectNode = solution.get(QueryFieldLabels.OBJECT);
			
			if (predicateNode != null && objectNode != null) {
				queryResult.addEntry(predicateNode.toString(), 
									   objectNode.toString());
			} 
			
		}
		
		return queryResult;
	}

	private ResultSet executeQuery(String queryText,
								   Dataset dataset) {

        QueryExecution queryExecution = null;
        Query query = QueryFactory.create(queryText, SYNTAX);

        queryExecution = QueryExecutionFactory.create(query, dataset);
        return queryExecution.execSelect();
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
            	
        log.debug("sparqlQuery = " + sparqlQuery.toString());
        
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

		ResultSet resultSet	= executeQuery(generateGenericSparqlQuery(
												this.individualURI, 
												this.filterRule),
										   this.dataset);

		return createJavaValueObjects(resultSet);
	}
}
