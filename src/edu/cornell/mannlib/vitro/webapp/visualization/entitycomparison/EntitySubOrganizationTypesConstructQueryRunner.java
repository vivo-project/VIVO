/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.entitycomparison;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;

public class EntitySubOrganizationTypesConstructQueryRunner implements ModelConstructor {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private String egoURI;
	
	private Dataset dataset;

	private Log log = LogFactory.getLog(EntitySubOrganizationTypesConstructQueryRunner
											.class.getName());
	
	public EntitySubOrganizationTypesConstructQueryRunner(String egoURI, Dataset dataset, Log log) {
		this.egoURI = egoURI;
		this.dataset = dataset;
		//this.log = log;
	}	
	
	private String generateConstructQueryForOrganizationLabel(String queryURI) {
		
		String sparqlQuery = 
			 "CONSTRUCT { " 
			+	"<" + queryURI + ">  rdfs:label ?organizationLabel ."
			+ "}"	
			+ "WHERE {"
			+	"<" + queryURI + ">  rdfs:label ?organizationLabel "
			+ "}";
		return sparqlQuery;
	}
	
	private String generateConstructQueryForSubOrganizationTypes(String queryURI) {
	    
		String sparqlQuery = 			 
		
			"CONSTRUCT { "
			+	"<" + queryURI + "> core:hasSubOrganization ?subOrganization . "
			+	"?subOrganization rdfs:label ?subOrganizationLabel . "
			+	"?subOrganization rdf:type ?subOrganizationType . "
			+ 	"?subOrganization core:organizationForPosition ?Position . "
			+   "?subOrganizationType rdfs:label ?subOrganizationTypeLabel . "
			+   "?Position core:positionForPerson ?Person ."
			+ 	"?Person rdfs:label ?PersonLabel ."
			+	"?Person rdf:type ?PersonType . "
			+ 	"?PersonType rdfs:label ?PersonTypeLabel  "
			+ "}"
			+ "WHERE { "
			+	"<" + queryURI + "> core:hasSubOrganization ?subOrganization . "
			+	"?subOrganization rdfs:label ?subOrganizationLabel . "
			+	"?subOrganization rdf:type ?subOrganizationType . "
		    +   "?subOrganizationType rdfs:label ?subOrganizationTypeLabel . "
			+ 	"?subOrganization core:organizationForPosition ?Position . "	
			+	"?Position core:positionForPerson ?Person .	"
			+   "?Person rdfs:label ?PersonLabel ."
			+   "?Person rdf:type ?PersonType . "
			+   "?PersonType rdfs:label ?PersonTypeLabel  "         
			+ "}";
					
		return sparqlQuery;
	}
	
	private String generateConstructQueryForPersonTypes(String queryURI) {
		
		String sparqlQuery = 			 
		
			"CONSTRUCT { "
			+	"<" + queryURI + "> core:organizationForPosition ?Position . "
			+   "?Position core:positionForPerson ?Person ."
			+ 	"?Person rdfs:label ?PersonLabel ."
			+	"?Person rdf:type ?PersonType . "
			+ 	"?PersonType rdfs:label ?PersonTypeLabel  "
			+ "}"
			+ "WHERE { "
			+	"<" + queryURI + "> core:organizationForPosition ?Position . "
			+	"?Position core:positionForPerson ?Person ."
			+ 	"?Person rdfs:label ?PersonLabel ."
			+	"?Person rdf:type ?PersonType . "
			+ 	"?PersonType rdfs:label ?PersonTypeLabel  "			
			+ "}";
		
		return sparqlQuery;
	}	
	
	private Model executeQuery(Set<String> constructQueries, Dataset dataset) {
		
        Model constructedModel = ModelFactory.createDefaultModel();
        long before = 0;
        
        for (String queryString : constructQueries) {
            
            before = System.currentTimeMillis();            
           log.debug("CONSTRUCT query string : " + queryString);
            
        	Query query = null;
        	
        	try {
        		query = QueryFactory.create(QueryConstants.getSparqlPrefixQuery() 
        									+ queryString, SYNTAX);
        	} catch (Throwable th) {
                log.error("Could not create CONSTRUCT SPARQL query for query " 
                			+ "string. " + th.getMessage());
                log.error(queryString);
        	}
        	
            QueryExecution qe = QueryExecutionFactory.create(
                    query, dataset);
            try {
                qe.execConstruct(constructedModel);
            } finally {
                qe.close();
            }
        	
            log.debug("Time to run " + (before - System.currentTimeMillis()));
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
            log.error("Ego Co-PI Vis Query " + errorMsg);
            throw new MalformedQueryParametersException(
            		"URI provided for an individual is malformed.");
        }
    } else {
        throw new MalformedQueryParametersException("URI parameter is either null or empty.");
    }
	
	Set<String> constructQueries = new LinkedHashSet<String>();
	
	populateConstructQueries(constructQueries);
	
	Model model	= executeQuery(constructQueries,
									   this.dataset);
	//model.write(System.out);
	return model;
	}

	private void populateConstructQueries(Set<String> constructQueries) {
		constructQueries.add(generateConstructQueryForOrganizationLabel(this.egoURI));
		constructQueries.add(generateConstructQueryForSubOrganizationTypes(this.egoURI));
		constructQueries.add(generateConstructQueryForPersonTypes(this.egoURI));
	}	
}
