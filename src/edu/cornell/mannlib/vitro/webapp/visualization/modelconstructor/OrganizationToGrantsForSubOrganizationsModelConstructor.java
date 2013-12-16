/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import java.util.HashSet;
import java.util.Set;

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

public class OrganizationToGrantsForSubOrganizationsModelConstructor implements ModelConstructor {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private Dataset dataset;
	
	public static final String MODEL_TYPE = "ORGANIZATION_TO_GRANTS_FOR_SUBORGANIZATIONS";
	public static final String MODEL_TYPE_HUMAN_READABLE = "Grants for specific organization via all descendants"; 
	
	private String organizationURI;
	
	private Log log = LogFactory.getLog(OrganizationToGrantsForSubOrganizationsModelConstructor.class.getName());
	
	private long before, after;
	
	public OrganizationToGrantsForSubOrganizationsModelConstructor(String organizationURI, Dataset dataset) {
		this.organizationURI = organizationURI;
		this.dataset = dataset;
	}
	
	private Set<String> constructOrganizationGrantsQueryTemplate(String constructProperty, String roleType) {
		
		Set<String> differentPerspectiveQueries = new HashSet<String>();
		
		String justGrantsQuery = ""
		+ " CONSTRUCT {  "
		+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
		+ "     <" + organizationURI + "> vivosocnet:lastCachedAt ?now . "
		+ "     <" + organizationURI + "> vivosocnet:" + constructProperty + " ?Grant . "
		+ "      "
		+ "     ?Grant rdf:type core:Grant . "
		+ "     ?Grant rdfs:label ?grantLabel . "
		+ "      "
		+ " } "
		+ " WHERE { "
		+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
		+ "     <" + organizationURI + "> <http://purl.obolibrary.org/obo/BFO_0000051>* ?subOrganization . "
        + "     ?subOrganization rdf:type foaf:Organization .  "
		+ "     ?subOrganization core:relatedBy ?Position .  "
		+ "     ?Position rdf:type core:Position .  "
		+ "     ?Position core:relates ?Person .  "
		+ "     ?Person  rdf:type foaf:Person .  "
		+ "     ?Person <http://purl.obolibrary.org/obo/RO_0000053> ?Role .  "
	    + "     ?Role rdf:type core:" + roleType + " . "
		+ "     ?Role core:relatedBy ?Grant . "
		+ "     ?Grant rdf:type core:Grant . "
		+ "     ?Grant rdfs:label ?grantLabel . "
		+ "      "
		+ "     LET(?now := afn:now()) "
		+ " } ";

		String justDateTimeOnGrantsQuery = ""
			+ " CONSTRUCT {  "
			+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
			+ "     <" + organizationURI + "> vivosocnet:lastCachedAt ?now . "
			+ "      "
			+ "     ?Grant vivosocnet:startDateTimeOnGrant ?startDateTimeValueForGrant . "
//			+ "     ?Grant vivosocnet:endDateTimeOnGrant ?endDateTimeValueForGrant . "
			+ "      "
			+ " } "
			+ " WHERE { "
			+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
			+ "     <" + organizationURI + "> <http://purl.obolibrary.org/obo/BFO_0000051>* ?subOrganization . "
            + "     ?subOrganization rdf:type foaf:Organization .  "
		    + "     ?subOrganization core:relatedBy ?Position .  "
		    + "     ?Position rdf:type core:Position .  "
	        + "     ?Position core:relates ?Person .  "
	        + "     ?Person  rdf:type foaf:Person .  "
    		+ "     ?Person <http://purl.obolibrary.org/obo/RO_0000053> ?Role .  "
    	    + "     ?Role rdf:type core:" + roleType + " . "
    		+ "     ?Role core:relatedBy ?Grant . "
    		+ "     ?Grant rdf:type core:Grant . "
			+ "      "
			+ "         ?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant .          "
//			+ "         OPTIONAL { "
			+ "             ?dateTimeIntervalValueForGrant core:start ?startDateForGrant .  "
			+ "             ?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
//			+ "         } "
//			+ "         OPTIONAL { "
//			+ "             ?dateTimeIntervalValueForGrant core:end ?endDateForGrant .  "
//			+ "             ?endDateForGrant core:dateTime ?endDateTimeValueForGrant   "
//			+ "         }     "
			+ "      "
			+ "     LET(?now := afn:now()) "
			+ " } ";
		
		String justDateTimeOnRolesQuery = ""
			+ " CONSTRUCT {  "
			+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
			+ "     <" + organizationURI + "> vivosocnet:lastCachedAt ?now . "
			+ "      "
			+ "     ?Grant vivosocnet:startDateTimeOnRole ?startDateTimeValue . "
//			+ "     ?Grant vivosocnet:endDateTimeOnRole ?endDateTimeValue . "
			+ " } "
			+ " WHERE { "
			+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
			+ "     <" + organizationURI + "> <http://purl.obolibrary.org/obo/BFO_0000051>* ?subOrganization . "
            + "     ?subOrganization rdf:type foaf:Organization .  "
		    + "     ?subOrganization core:relatedBy ?Position .  "
		    + "     ?Position rdf:type core:Position .  "
	        + "     ?Position core:relates ?Person .  "
	        + "     ?Person  rdf:type foaf:Person .  "
    		+ "     ?Person <http://purl.obolibrary.org/obo/RO_0000053> ?Role .  "
    	    + "     ?Role rdf:type core:" + roleType + " . "
    		+ "     ?Role core:relatedBy ?Grant . "
    		+ "     ?Grant rdf:type core:Grant . "
			+ "      "
			+ "         ?Role core:dateTimeInterval ?dateTimeIntervalValue . "
//			+ "         OPTIONAL { "
			+ "             ?dateTimeIntervalValue core:start ?startDate .  "
			+ "             ?startDate core:dateTime ?startDateTimeValue . "
//			+ "         } "
//			+ "          "
//			+ "         OPTIONAL { "
//			+ "             ?dateTimeIntervalValue core:end ?endDate .  "
//			+ "             ?endDate core:dateTime ?endDateTimeValue .           "
//			+ "         }     "
			+ "      "
			+ "     LET(?now := afn:now()) "
			+ " } ";
		
		differentPerspectiveQueries.add(justGrantsQuery);
		differentPerspectiveQueries.add(justDateTimeOnGrantsQuery);
		differentPerspectiveQueries.add(justDateTimeOnRolesQuery);
		
		return differentPerspectiveQueries;
	}
	
	private Set<String> constructOrganizationToGrantsQuery() {

		Set<String> differentInvestigatorTypeQueries = new HashSet<String>();
		
		Set<String> investigatorRoleQuery = constructOrganizationGrantsQueryTemplate("hasInvestigatorWithGrant", "InvestigatorRole");
		Set<String> piRoleQuery = constructOrganizationGrantsQueryTemplate("hasPIWithGrant", "PrincipalInvestigatorRole");
		Set<String> coPIRoleQuery = constructOrganizationGrantsQueryTemplate("hascoPIWithGrant", "CoPrincipalInvestigatorRole");

		differentInvestigatorTypeQueries.addAll(investigatorRoleQuery);
		differentInvestigatorTypeQueries.addAll(piRoleQuery);
		differentInvestigatorTypeQueries.addAll(coPIRoleQuery);
		
		return differentInvestigatorTypeQueries;
	}
	
	private Model executeQuery(Set<String> constructQueries) {
		
		Model constructedModel = ModelFactory.createDefaultModel();

		before = System.currentTimeMillis();
		log.debug("CONSTRUCT query string : " + constructQueries);
		
		for (String currentQuery : constructQueries) {

			Query query = null;

			try {
				query = QueryFactory.create(QueryConstants.getSparqlPrefixQuery() + currentQuery, SYNTAX);
			} catch (Throwable th) {
				log.error("Could not create CONSTRUCT SPARQL query for query "
						+ "string. " + th.getMessage());
				log.error(currentQuery);
			}

			QueryExecution qe = QueryExecutionFactory.create(query, dataset);
			
			try {
				qe.execConstruct(constructedModel);
			} finally {
				qe.close();
			}
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
