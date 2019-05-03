/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import java.util.HashSet;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;

public class PersonToGrantsModelConstructor implements ModelConstructor {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private RDFService rdfService;

	public static final String MODEL_TYPE = "PERSON_TO_GRANTS";
	public static final String MODEL_TYPE_HUMAN_READABLE = "Grants for specific person via all roles";

	private String personURI;

	private Log log = LogFactory.getLog(PersonToGrantsModelConstructor.class.getName());

	private long before, after;

	public PersonToGrantsModelConstructor(String personURI, RDFService rdfService) {
		this.personURI = personURI;
		this.rdfService = rdfService;
	}

private Set<String> constructPersonGrantsQueryTemplate(String constructProperty, String roleType) {

		Set<String> differentPerspectiveQueries = new HashSet<String>();

		String justGrantsQuery = ""
			+ " CONSTRUCT {  "
			+ "     <" + personURI + "> vivosocnet:lastCachedAt ?now . "
			+ "     <" + personURI + "> vivosocnet:" + constructProperty + " ?Grant . "
			+ "      "
			+ "     ?Grant rdf:type core:Grant . "
			+ "     ?Grant rdfs:label ?grantLabel . "
			+ "      "
			+ " } "
			+ " WHERE { "
			+ "     <" + personURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
		    + "     ?Role rdf:type core:" + roleType + " . "
    		+ "     ?Role core:relatedBy ?Grant . "
    		+ "     ?Grant rdf:type core:Grant . "
			+ "     ?Grant rdfs:label ?grantLabel . "
			+ "      "
			+ "     LET(?now := now()) "
			+ " } ";

		String justDateTimeOnGrantsQuery = ""
			+ " CONSTRUCT {  "
			+ "     <" + personURI + "> vivosocnet:lastCachedAt ?now . "
			+ "     ?Grant vivosocnet:startDateTimeOnGrant ?startDateTimeValueForGrant . "
//			+ "     ?Grant vivosocnet:endDateTimeOnGrant ?endDateTimeValueForGrant . "
			+ "      "
			+ " } "
			+ " WHERE { "
			+ "     <" + personURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
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
			+ "     LET(?now := now()) "
			+ " } ";

		String justDateTimeOnRolesQuery = ""
			+ " CONSTRUCT {  "
			+ "     <" + personURI + "> vivosocnet:lastCachedAt ?now . "
			+ "     ?Grant vivosocnet:startDateTimeOnRole ?startDateTimeValue . "
//			+ "     ?Grant vivosocnet:endDateTimeOnRole ?endDateTimeValue . "
			+ " } "
			+ " WHERE { "
			+ "     <" + personURI + "> <http://purl.obolibrary.org/obo/RO_0000053> ?Role . "
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
			+ "     LET(?now := now()) "
			+ " } ";

		differentPerspectiveQueries.add(justGrantsQuery);
		differentPerspectiveQueries.add(justDateTimeOnGrantsQuery);
		differentPerspectiveQueries.add(justDateTimeOnRolesQuery);

		return differentPerspectiveQueries;
	}

	private Set<String> constructPersonToGrantsQuery() {

		Set<String> differentInvestigatorTypeQueries = new HashSet<String>();

		Set<String> investigatorRoleQuery = constructPersonGrantsQueryTemplate("hasGrantAsAnInvestigator", "InvestigatorRole");
		Set<String> piRoleQuery = constructPersonGrantsQueryTemplate("hasGrantAsPI", "PrincipalInvestigatorRole");
		Set<String> coPIRoleQuery = constructPersonGrantsQueryTemplate("hasGrantAsCoPI", "CoPrincipalInvestigatorRole");

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
			try {
				rdfService.sparqlConstructQuery(QueryConstants.getSparqlPrefixQuery() + currentQuery, constructedModel);
			} catch (Throwable th) {
				log.error("Could not create CONSTRUCT SPARQL query for query "
						+ "string. " + th.getMessage());
				log.error(currentQuery);
			}

		}

		after = System.currentTimeMillis();
		log.debug("Time taken to execute the CONSTRUCT queries is in milliseconds: "
				+ (after - before));

		return constructedModel;
	}

	public Model getConstructedModel() throws MalformedQueryParametersException {
		return executeQuery(constructPersonToGrantsQuery());
	}
}
