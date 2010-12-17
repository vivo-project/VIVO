/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.persongrantcount;

import java.util.HashSet;
import java.util.Set;

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
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Grant;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;


/**
 * This query runner is used to execute Sparql query that will fetch all the grants for an individual
 * @author bkoniden
 * Deepak Konidena
 *
 */
public class PersonGrantCountQueryRunner implements QueryRunner<Set<Grant>>{
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private String personURI;
	private DataSource dataSource;
	private Individual principalInvestigator;
	
	public Individual getPrincipalInvestigator(){
		return principalInvestigator;
	}
	
	private Log log;
	
	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = ""
						+ "SELECT (str(?PILabel) as ?PILabelLit) "
						+ "(str(?Grant) as ?grantLit)"
						+ "(str(?GrantLabel) as ?grantLabelLit)"
						+ "(str(?GrantStartDate) as ?grantStartDateLit)"
						+ "(str(?GrantEndDate) as ?grantEndDateLit)" ;
	
	public PersonGrantCountQueryRunner(String personURI, DataSource dataSource, Log log){
		
		this.personURI = personURI;
		this.dataSource = dataSource;
		this.log = log;
	}
	
	private Set<Grant> createJavaValueObjects(ResultSet resultSet){
		Set<Grant> PIGrant = new HashSet<Grant>();
		
		while(resultSet.hasNext()){
			QuerySolution solution = resultSet.nextSolution();
			
			Grant grant = new Grant(solution.get(QueryFieldLabels.GRANT_URL).toString());
			
			RDFNode grantLabelNode = solution.get(QueryFieldLabels.GRANT_LABEL);
			if(grantLabelNode != null){
				grant.setIndividualLabel(grantLabelNode.toString());
			}
			
			RDFNode grantStartDateNode = solution.get(QueryFieldLabels.GRANT_START_DATE);
			if(grantStartDateNode != null){
				grant.setGrantStartDate(grantStartDateNode.toString());
			}
			
			RDFNode grantEndDateNode = solution.get(QueryFieldLabels.GRANT_END_DATE);
			if(grantEndDateNode != null){
				grant.setGrantEndDate(grantEndDateNode.toString());
			}
			
			/*
			 * Since we are getting grant count for just one PI at a time we need
			 * to create only one "Individual" instance. We test against the null for "PI" to
			 * make sure that it has not already been instantiated. 
			 * */
			RDFNode PIURLNode = solution.get(QueryFieldLabels.PI_URL);
			if (PIURLNode != null && principalInvestigator == null) {
				principalInvestigator = new Individual(PIURLNode.toString());
				RDFNode PILabelNode = solution.get(QueryFieldLabels.PI_LABEL);
				if (PILabelNode != null) {
					principalInvestigator.setIndividualLabel(PILabelNode.toString());
				}
			}
			
			PIGrant.add(grant);
		}
		return PIGrant;
	}
	
	private ResultSet executeQuery(String queryURI, DataSource dataSource){
		
		QueryExecution queryExecution = null;
		
		Query query = QueryFactory.create(getSparqlQuery(queryURI), SYNTAX);
		queryExecution = QueryExecutionFactory.create(query,dataSource);
		
		return queryExecution.execSelect();
	}
	


	private String getSparqlQuery(String queryURI){
		
		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ SPARQL_QUERY_COMMON_SELECT_CLAUSE
							+ "(str(<" + queryURI + ">) as ?PILit) "
							+ "WHERE {"
							+ "<" + queryURI + "> rdfs:label ?PILabel;"
							+ "core:hasCo-PrincipalInvestigatorRole ?Role ."
							+ "?Role core:roleIn ?Grant ."
							+ "?Grant rdfs:label ?GrantLabel ; core:startDate ?GrantStartDate ; core:endDate ?GrantEndDate ."
							+ "}";
		
		log.debug("SPARQL query for person grant count -> \n"+ sparqlQuery);
		return sparqlQuery;
	}
	
	public Set<Grant> getQueryResult() throws MalformedQueryParametersException{
		
		if(StringUtils.isNotBlank(this.personURI)){
			
			/*
			 * To test the validity of the URI submitted
			 */
			IRIFactory iriFactory = IRIFactory.jenaImplementation();
			IRI iri = iriFactory.create(this.personURI);
			
			if(iri.hasViolation(false)){
				String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
                log.error("Grant Count vis Query " + errorMsg);
                throw new MalformedQueryParametersException(
                		"URI provided for an individual is malformed.");				
			}
		} else {
			throw new MalformedQueryParametersException("URL parameter is either null or empty.");
		}
		
		ResultSet resultSet = executeQuery(this.personURI, this.dataSource);
		
		return createJavaValueObjects(resultSet);
	}
	
}
