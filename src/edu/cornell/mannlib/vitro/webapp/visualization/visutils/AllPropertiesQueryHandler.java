/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

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
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;



/**
 * Very dumb name of the class. change it.
 * @author cdtank
 *
 */
public class AllPropertiesQueryHandler implements QueryHandler<GenericQueryMap> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String filterRule, individualURLParam;
	private DataSource dataSource;

	private Log log;

	public AllPropertiesQueryHandler(String individualURLParam,
							   String filterRule,
							   DataSource dataSource, 
							   Log log) {

		this.individualURLParam = individualURLParam;
		this.filterRule = filterRule;
		this.dataSource = dataSource;
		this.log = log;
		
	}

	private GenericQueryMap createJavaValueObjects(ResultSet resultSet) {
		
		GenericQueryMap queryResultVO = new GenericQueryMap();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();
			
			
			RDFNode predicateNode = solution.get(QueryFieldLabels.PREDICATE);
			RDFNode objectNode = solution.get(QueryFieldLabels.OBJECT);
			
			if (predicateNode != null && objectNode != null) {
				queryResultVO.addEntry(predicateNode.toString(), 
									   objectNode.toString());
			} 
			
		}
		
		return queryResultVO;
	}

	
	private ResultSet executeQuery(String queryText,
								   DataSource dataSource) {

        QueryExecution queryExecution = null;
        try {
            Query query = QueryFactory.create(queryText, SYNTAX);

//            QuerySolutionMap qs = new QuerySolutionMap();
//            qs.add("authPerson", queryParam); // bind resource to s
            
            queryExecution = QueryExecutionFactory.create(query, dataSource);
            
            if (query.isSelectType()) {
                return queryExecution.execSelect();
            }
        } finally {
            if (queryExecution != null) {
            	queryExecution.close();
            }

        }
		return null;
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
							+ "WHERE { "
							+ "<" + queryURI + "> ?predicate ?object.  "
							+ filterClause
							+ "}";

		return sparqlQuery;
	}

	
	public GenericQueryMap getVisualizationJavaValueObjects()
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

		ResultSet resultSet	= executeQuery(generateGenericSparqlQuery(
												this.individualURLParam, 
												this.filterRule),
										   this.dataSource);

		return createJavaValueObjects(resultSet);
	}


}
