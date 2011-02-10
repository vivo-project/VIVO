/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.persongrantcount;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;

public class PersonGrantCountConstructQueryRunner {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private String egoURI;
	
	private DataSource dataSource;
	
	private Log log = LogFactory.getLog(PersonGrantCountConstructQueryRunner.class.getName());
	
	public PersonGrantCountConstructQueryRunner(String egoURI, DataSource dataSource, Log log){
		this.egoURI = egoURI;
		this.dataSource = dataSource;
		//this.log = log;
	}
	
	private String generateConstructQueryForInvestigatorLabel(String queryURI) {
		
		String sparqlQuery = 
			 "CONSTRUCT { " 
			+	"<"+queryURI+ ">  rdfs:label ?investigatorLabel ."
			+ "}"	
			+ "WHERE {"
			+	"<"+queryURI+ ">  rdfs:label ?investigatorLabel "
			+ "}";
				
		return sparqlQuery;
	}

	private String generateConstructQueryForInvestigatorGrants(String queryURI){
		
		String sparqlQuery = ""
			+ "CONSTRUCT { " 
			+	"<"+queryURI+ "> ?preboundProperty ?Role . "
			+	"?Role core:roleIn ?Grant ."
			+	"?Grant rdfs:label ?GrantLabel "
			+ "} "
			+ "WHERE { "
			+	"<"+queryURI+ "> ?preboundProperty ?Role . "
			+	"?Role core:roleIn ?Grant ."
			+	"?Grant rdfs:label ?GrantLabel "
			+ "} ";
		
		return sparqlQuery;

	}
	
	private String generateConstructQueryForDateTimeValueofRole(String queryURI){
		
		String sparqlQuery = 
			 "CONSTRUCT { "
			+	"<"+queryURI+ "> ?preboundProperty ?Role . "
			+ 	"?Role core:dateTimeInterval ?dateTimeIntervalValue . "
			+   "?dateTimeIntervalValue core:start ?startDate . "
			+	"?startDate core:dateTime ?startDateTimeValue . "
			+	"?dateTimeIntervalValue core:end ?endDate . "
			+	"?endDate core:dateTime ?endDateTimeValue . "
			+ "}"
			+ "WHERE { "
			+	"{"
				+	"<"+queryURI+ "> ?preboundProperty ?Role . "
				+ 	"?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+   "?dateTimeIntervalValue core:start ?startDate . "
				+	"?startDate core:dateTime ?startDateTimeValue . "
			+	"} UNION "
			+	"{"
				+	"<"+queryURI+ "> ?preboundProperty ?Role . "
				+ 	"?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+	"?dateTimeIntervalValue core:end ?endDate . "
				+	"?endDate core:dateTime ?endDateTimeValue . "			
			+ 	"}"
			+ "}";
		
		return sparqlQuery;
	}	
	
	private String generateConstructQueryForDateTimeValueofGrant(String queryURI){
		
		String sparqlQuery = 
			 "CONSTRUCT { "
			+	"<"+queryURI+ "> ?preboundProperty ?Role . "
			+	"?Role core:roleIn ?Grant ."
			+ 	"?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
			+   "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
			+	"?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
			+	"?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
			+	"?endDateForGrant core:dateTime ?endDateTimeValueForGrant . "
			+ "}"
			+ "WHERE { "
			+	"{"
				+	"<"+queryURI+ "> ?preboundProperty ?Role . "
				+	"?Role core:roleIn ?Grant ."
				+ 	"?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+   "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
				+	"?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
			+	"} UNION "
			+	"{"
				+	"<"+queryURI+ "> ?preboundProperty ?Role . "
				+	"?Role core:roleIn ?Grant ."
				+ 	"?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+	"?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
				+	"?endDateForGrant core:dateTime ?endDateTimeValueForGrant . "			
			+ 	"}"
			+ "}";
		
		return sparqlQuery;
	}	
	
	private Model executeQuery(Set<String> constructQueries, DataSource dataSource) {
		
        Model constructedModel = ModelFactory.createDefaultModel();

        for (String queryString : constructQueries) {
            
        	log.debug("CONSTRUCT query string : " + queryString);
            
        	Query query = null;
        	
        	try{
        		query = QueryFactory.create(QueryConstants.getSparqlPrefixQuery() + queryString, SYNTAX);
        	}catch(Throwable th){
                log.error("Could not create CONSTRUCT SPARQL query for query " +
                        "string. " + th.getMessage());
                log.error(queryString);
        	}
        	
            QueryExecution qe = QueryExecutionFactory.create(
                    query, dataSource);
            try {
                qe.execConstruct(constructedModel);
            } finally {
                qe.close();
            }
        	
        }	

		return constructedModel;
	}	
	
	public Model getConstructedModel()
	throws MalformedQueryParametersException {

	if (StringUtils.isNotBlank(this.egoURI)) {
		/*
    	 * To test for the validity of the URI submitted.
    	 * */
    	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
		IRI iri = iRIFactory.create(this.egoURI);
        if (iri.hasViolation(false)) {
            String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
            log.error("Person Grant Count Construct Query " + errorMsg);
            throw new MalformedQueryParametersException(
            		"URI provided for an individual is malformed.");
        }
    } else {
        throw new MalformedQueryParametersException("URI parameter is either null or empty.");
    }
	
	Set<String> constructQueries = new HashSet<String>();
	
	populateConstructQueries(constructQueries);
	
	Model model	= executeQuery(constructQueries,
									   this.dataSource);
	
	return model;
		
	}

	private void populateConstructQueries(Set<String> constructQueries) {
		
		constructQueries.add(generateConstructQueryForInvestigatorLabel(this.egoURI));
		constructQueries.add(generateConstructQueryForInvestigatorGrants(this.egoURI));
		constructQueries.add(generateConstructQueryForDateTimeValueofRole(this.egoURI));
		constructQueries.add(generateConstructQueryForDateTimeValueofGrant(this.egoURI));
		
		
	}	
	
}
