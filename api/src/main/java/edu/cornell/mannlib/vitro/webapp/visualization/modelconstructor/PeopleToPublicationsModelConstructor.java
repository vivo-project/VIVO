/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

/**
 * No longer used - will be removed
 */
@Deprecated
public class PeopleToPublicationsModelConstructor implements ModelConstructor {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private RDFService rdfService;
	
	public static final String MODEL_TYPE = "PEOPLE_TO_PUBLICATIONS"; 
	public static final String MODEL_TYPE_HUMAN_READABLE = "Publications for all people";
	
	private Log log = LogFactory.getLog(PeopleToPublicationsModelConstructor.class.getName());
	
	private long before, after;
	
	public PeopleToPublicationsModelConstructor(RDFService rdfService) {
		this.rdfService = rdfService;
	}
	
	private String constructPeopleToPublicationsQuery() {
		
		return ""
		+ " CONSTRUCT { "
		+ "     ?person vivosocnet:lastCachedAt ?now . "
		+ "     ?person vivosocnet:hasPublication ?Document . "
		+ "     ?Document rdf:type bibo:Document .  "
		+ "     ?Document rdfs:label ?DocumentLabel .  "
		+ "     ?Document core:dateTimeValue ?dateTimeValue .  "
		+ "     ?dateTimeValue core:dateTime ?publicationDate .  "
		+ "     ?Document core:hasPublicationVenue ?journal ."
		+ "     ?journal rdfs:label ?journalLabel .  "
		+ " } "
		+ " WHERE {  "
		+ "         ?person core:relatedBy ?Resource .  "
		+ "         ?person rdf:type foaf:Person .  "
		+ "         ?Resource rdf:type core:Authorship .  "
		+ "         ?Resource core:relates ?Document .  "
		+ "         ?Document rdf:type bibo:Document . "
		+ "         ?Document rdfs:label ?DocumentLabel . "
		+ "          "
		+ "         OPTIONAL { "
		+ "             ?Document core:dateTimeValue ?dateTimeValue .  "
		+ "             ?dateTimeValue core:dateTime ?publicationDate . "
		+ "         }  "
		+ "          "
		+ "         OPTIONAL { "
		+ "             ?Document core:hasPublicationVenue ?journal ."
		+ "     		?journal rdfs:label ?journalLabel .  "
		+ "         }  "
		+ "          "
		+ "         LET(?now := now()) "
		+ " } ";
	}
	
	private Model executeQuery(String constructQuery) {
		
		log.debug("in constructed model for ALL people publications ");
		
		Model constructedModel = ModelFactory.createDefaultModel();

		before = System.currentTimeMillis();
		log.debug("CONSTRUCT query string : " + constructQuery);

		try {
			rdfService.sparqlConstructQuery(QueryConstants.getSparqlPrefixQuery() + constructQuery, constructedModel);
		} catch (Throwable th) {
			log.error("Could not create CONSTRUCT SPARQL query for query "
					+ "string. " + th.getMessage());
			log.error(constructQuery);
		}

		after = System.currentTimeMillis();
		log.debug("Try to see Time taken to execute the CONSTRUCT queries is in milliseconds: "
				+ (after - before));

		return constructedModel;
	}	
	
	public Model getConstructedModel() throws MalformedQueryParametersException {
		return executeQuery(constructPeopleToPublicationsQuery());
	}
}
