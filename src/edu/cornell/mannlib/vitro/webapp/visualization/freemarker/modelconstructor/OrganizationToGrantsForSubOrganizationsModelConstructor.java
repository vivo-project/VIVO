/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor;

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
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.ModelConstructor;

public class OrganizationToGrantsForSubOrganizationsModelConstructor implements ModelConstructor {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private Dataset dataset;
	
	public static final String MODEL_TYPE = "ORGANIZATION_TO_GRANTS_FOR_SUBORGANIZATIONS"; 
	
	private String organizationURI;
	
	private Log log = LogFactory.getLog(OrganizationToGrantsForSubOrganizationsModelConstructor.class.getName());
	
	private long before, after;
	
	public OrganizationToGrantsForSubOrganizationsModelConstructor(String organizationURI, Dataset dataset) {
		this.organizationURI = organizationURI;
		this.dataset = dataset;
	}
	
	private String constructOrganizationToGrantsQuery() {
		
		return ""
		+ " CONSTRUCT {  "
		+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
		+ "     <" + organizationURI + "> vivosocnet:lastCachedAt ?now . "
		+ "     <" + organizationURI + "> vivosocnet:hasPersonWithGrant ?Grant . "
		+ "      "
		+ "     ?Grant rdf:type core:Grant . "
		+ "     ?Grant rdfs:label ?grantLabel . "
		+ "      "
		+ "     ?Grant vivosocnet:startDateTimeOnGrant ?startDateTimeValueForGrant . "
		+ "     ?Grant vivosocnet:endDateTimeOnGrant ?endDateTimeValueForGrant . "
		+ "      "
		+ "     ?Grant vivosocnet:startDateTimeOnRole ?startDateTimeValue . "
		+ "     ?Grant vivosocnet:endDateTimeOnRole ?endDateTimeValue . "
		+ " } "
		+ " WHERE { "
		+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
		+ "     <" + organizationURI + "> core:hasSubOrganization* ?subOrganization . "
		+ "     ?subOrganization core:organizationForPosition ?Position .  "
		+ "     ?Position core:positionForPerson ?Person .        "
		+ "     ?Person core:hasInvestigatorRole ?Role .  "
		+ "     ?Role core:roleIn ?Grant . "
		+ "     ?Grant rdfs:label ?grantLabel . "
		+ "      "
		+ "     OPTIONAL { "
		+ "         ?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant .          "
		+ "         OPTIONAL { "
		+ "             ?dateTimeIntervalValueForGrant core:start ?startDateForGrant .  "
		+ "             ?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
		+ "         } "
		+ "         OPTIONAL { "
		+ "             ?dateTimeIntervalValueForGrant core:end ?endDateForGrant .  "
		+ "             ?endDateForGrant core:dateTime ?endDateTimeValueForGrant   "
		+ "         }     "
		+ "     }     "
		+ "      "
		+ "     OPTIONAL { "
		+ "         ?Role core:dateTimeInterval ?dateTimeIntervalValue . "
		+ "         OPTIONAL { "
		+ "             ?dateTimeIntervalValue core:start ?startDate .  "
		+ "             ?startDate core:dateTime ?startDateTimeValue . "
		+ "         } "
		+ "          "
		+ "         OPTIONAL { "
		+ "             ?dateTimeIntervalValue core:end ?endDate .  "
		+ "             ?endDate core:dateTime ?endDateTimeValue .           "
		+ "         }     "
		+ "     } "
		+ "      "
		+ "     LET(?now := afn:now()) "
		+ " } ";

	}
	
	private Model executeQuery(String constructQuery) {
		
		System.out.println("in execute query - org to grants for - " + organizationURI);
		
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
		return executeQuery(constructOrganizationToGrantsQuery());
	}
}
