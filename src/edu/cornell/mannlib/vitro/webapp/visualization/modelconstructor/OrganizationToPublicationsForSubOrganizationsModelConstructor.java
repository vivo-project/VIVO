/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

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

public class OrganizationToPublicationsForSubOrganizationsModelConstructor implements ModelConstructor {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private Dataset dataset;
	
	public static final String MODEL_TYPE = "ORGANIZATION_TO_PUBLICATIONS_FOR_SUBORGANIZATIONS"; 
	public static final String MODEL_TYPE_HUMAN_READABLE = "Publications for specific organization via all descendants";
	
	private String organizationURI;
	
	private Log log = LogFactory.getLog(OrganizationToPublicationsForSubOrganizationsModelConstructor.class.getName());
	
	private long before, after;
	
	public OrganizationToPublicationsForSubOrganizationsModelConstructor(String organizationURI, Dataset dataset) {
		this.organizationURI = organizationURI;
		this.dataset = dataset;
	}
	
	private String constructOrganizationToPublicationsPublicationInformationQuery() {
		
		return ""
		+ " CONSTRUCT { "
		+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
		+ "     <" + organizationURI + "> vivosocnet:lastCachedAt ?now . "
		+ "     <" + organizationURI + "> vivosocnet:hasPersonWithPublication ?Document . "
		+ "     ?Document rdf:type bibo:Document .  "
		+ "     ?Document rdfs:label ?DocumentLabel .  "
		+ "     ?Document core:dateTimeValue ?dateTimeValue .  "
		+ "     ?dateTimeValue core:dateTime ?publicationDate .  "
		+ "     ?Document core:hasPublicationVenue ?journal ."
		+ "     ?journal rdfs:label ?journalLabel .  "
		+ " } "
		+ " WHERE {  "
		+ "         <" + organizationURI + "> rdfs:label ?organizationLabel .  "
		+ "         <" + organizationURI + "> <http://purl.obolibrary.org/obo/BFO_0000051>* ?subOrganization .  "
	    + "         ?subOrganization rdf:type foaf:Organization .  "
		+ "         ?subOrganization core:relatedBy ?Position .  "
		+ "         ?Position rdf:type core:Position .  "
		+ "         ?Position core:relates ?Person .  "
		+ "         ?Person  rdf:type foaf:Person .  "
		+ "         ?Person  core:relatedBy ?Resource .  "
		+ "         ?Resource  rdf:type core:Authorship .  "
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
		+ "         LET(?now := afn:now()) "
		+ " } ";

	}
	
	private Model executeQuery(String constructQuery) {
		
		log.debug("[VIS CACHE] SubOrganizations Publications" + organizationURI);
		
		Model constructedModel = ModelFactory.createDefaultModel();

		before = System.currentTimeMillis();
		log.debug("CONSTRUCT query string : " + constructQuery);

		Query query = null;

		try {
			query = QueryFactory.create(QueryConstants.getSparqlPrefixQuery()
					+ constructQuery, SYNTAX);
		} catch (Throwable th) {
			log.error("Could not create CONSTRUCT SPARQL query for query "
					+ "string. " + th.getMessage());
			log.error(constructQuery);
		}

		QueryExecution qe = QueryExecutionFactory.create(query, dataset);

		try {
			qe.execConstruct(constructedModel);
		} finally {
			qe.close();
		}

		after = System.currentTimeMillis();
		log.debug("Time taken to execute the CONSTRUCT queries is in milliseconds: "
				+ (after - before));

		return constructedModel;
	}	
	
	public Model getConstructedModel() throws MalformedQueryParametersException {
		return executeQuery(constructOrganizationToPublicationsPublicationInformationQuery());
	}
}
