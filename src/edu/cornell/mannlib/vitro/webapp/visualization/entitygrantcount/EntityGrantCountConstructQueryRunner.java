/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.entitygrantcount;

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

public class EntityGrantCountConstructQueryRunner implements ModelConstructor {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String egoURI;

	private Dataset dataset;

	private long before, after;

	private Log log = LogFactory
			.getLog(EntityGrantCountConstructQueryRunner.class.getName());

	public EntityGrantCountConstructQueryRunner(String egoURI, Dataset dataset,
			Log log) {
		this.egoURI = egoURI;
		this.dataset = dataset;
		// this.log = log;
	}

	private String generateConstructQueryForOrganizationLabel(String queryURI) {

		String sparqlQuery = "CONSTRUCT { " + "<" + queryURI
				+ ">  rdfs:label ?organizationLabel ." + "}" + "WHERE {" + "<"
				+ queryURI + ">  rdfs:label ?organizationLabel " + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForDateTimeValueofRoleForOneLevelDeep(
			String queryURI, String preboundProperty) {

		String sparqlQuery = "CONSTRUCT { " + "<" + queryURI
				+ "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person . " + "?Person "
				+ preboundProperty + " ?Role . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:start ?startDate .	"
				+ "?startDate core:dateTime ?startDateTimeValue . "
				+ "?dateTimeIntervalValue core:end ?endDate . "
				+ "?endDate core:dateTime ?endDateTimeValue . " + "}"
				+ "WHERE { " + "{" + "<" + queryURI
				+ "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	" + "?Person "
				+ preboundProperty + " ?Role . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:start ?startDate . "
				+ "?startDate core:dateTime ?startDateTimeValue . "
				+ "} UNION " + "{" + "<" + queryURI
				+ "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	" + "?Person "
				+ preboundProperty + " ?Role . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:end ?endDate . "
				+ "?endDate core:dateTime ?endDateTimeValue . " + "}" + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForDateTimeValueofGrantForOneLevelDeep(
			String queryURI, String preboundProperty) {

		String sparqlQuery = "CONSTRUCT { " + "<"
				+ queryURI
				+ "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person . "
				+ "?Person "
				+ preboundProperty
				+ " ?Role . "
				+ "?Role core:roleIn ?Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
				+ "?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
				+ "?endDateForGrant core:dateTime ?endDateTimeValueForGrant  "
				+ "}"
				+ "WHERE { "
				+ "{"
				+ "<"
				+ queryURI
				+ "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	"
				+ "?Person "
				+ preboundProperty
				+ " ?Role . "
				+ "?Role core:roleIn ?Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
				+ "?startDateForGrant core:dateTime ?startDateTimeValueForGrant  "
				+ "} UNION "
				+ "{"
				+ "<"
				+ queryURI
				+ "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	"
				+ "?Person "
				+ preboundProperty
				+ " ?Role . "
				+ "?Role core:roleIn ?Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
				+ "?endDateForGrant core:dateTime ?endDateTimeValueForGrant  "
				+ "}" + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForDateTimeValueofRole(
			String queryURI, String preboundProperty) {

		String sparqlQuery = "CONSTRUCT { " + "<" + queryURI
				+ "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person . " + "?Person "
				+ preboundProperty + " ?Role . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:start ?startDate .	"
				+ "?startDate core:dateTime ?startDateTimeValue . "
				+ "?dateTimeIntervalValue core:end ?endDate . "
				+ "?endDate core:dateTime ?endDateTimeValue  " + "}"
				+ "WHERE { " + "{" + "<" + queryURI
				+ "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	" + "?Person "
				+ preboundProperty + " ?Role . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:start ?startDate . "
				+ "?startDate core:dateTime ?startDateTimeValue  " + "} UNION "
				+ "{" + "<" + queryURI
				+ "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	" + "?Person "
				+ preboundProperty + " ?Role . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:end ?endDate . "
				+ "?endDate core:dateTime ?endDateTimeValue  " + "}" + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForDateTimeValueofGrant(
			String queryURI, String preboundProperty) {

		String sparqlQuery = "CONSTRUCT { " + "<"
				+ queryURI
				+ "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person . "
				+ "?Person "
				+ preboundProperty
				+ " ?Role . "
				+ "?Role core:roleIn ?Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
				+ "?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
				+ "?endDateForGrant core:dateTime ?endDateTimeValueForGrant  "
				+ "}"
				+ "WHERE { "
				+ "{"
				+ "<"
				+ queryURI
				+ "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	"
				+ "?Person "
				+ preboundProperty
				+ " ?Role . "
				+ "?Role core:roleIn ?Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
				+ "?startDateForGrant core:dateTime ?startDateTimeValueForGrant  "
				+ "} UNION "
				+ "{"
				+ "<"
				+ queryURI
				+ "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	"
				+ "?Person "
				+ preboundProperty
				+ " ?Role . "
				+ "?Role core:roleIn ?Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
				+ "?endDateForGrant core:dateTime ?endDateTimeValueForGrant  "
				+ "}" + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForSubOrganizations(String queryURI,
			String preboundProperty) {

		String sparqlQuery =

		"CONSTRUCT { " + "<" + queryURI
				+ "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization rdfs:label ?subOrganizationLabel . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person . "
				+ "?Person rdfs:label ?PersonLabel ." + "?Person "
				+ preboundProperty + " ?Role . " + "?Role core:roleIn ?Grant ."
				+ "?Grant rdfs:label ?GrantLabel " + "}" + "WHERE { " + "<"
				+ queryURI + "> core:hasSubOrganization ?subOrganization . "
				+ "?subOrganization rdfs:label ?subOrganizationLabel . "
				+ "?subOrganization core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	"
				+ "?Person rdfs:label ?PersonLabel ." + "?Person "
				+ preboundProperty + " ?Role . " + "?Role core:roleIn ?Grant ."
				+ "?Grant rdfs:label ?GrantLabel " + "}";

		return sparqlQuery;

	}

	private String generateConstructQueryForPersons(String queryURI,
			String preboundProperty) {

		String sparqlQuery =

		"CONSTRUCT { " + "<" + queryURI
				+ "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person . "
				+ "?Person rdfs:label ?PersonLabel ." + "?Person "
				+ preboundProperty + " ?Role . " + "?Role core:roleIn ?Grant ."
				+ "?Grant rdfs:label ?GrantLabel " + "}" + "WHERE { " + "<"
				+ queryURI + "> core:organizationForPosition ?Position . "
				+ "?Position core:positionForPerson ?Person .	"
				+ "?Person rdfs:label ?PersonLabel ." + "?Person "
				+ preboundProperty + " ?Role . " + "?Role core:roleIn ?Grant ."
				+ "?Grant rdfs:label ?GrantLabel " + "}";

		return sparqlQuery;

	}

	private Model executeQuery(Set<String> constructQueries, Dataset dataset) {

		Model constructedModel = ModelFactory.createDefaultModel();

		before = System.currentTimeMillis();

		for (String queryString : constructQueries) {

			log.debug("CONSTRUCT query string : " + queryString);

			Query query = null;

			try {
				query = QueryFactory.create(QueryConstants
						.getSparqlPrefixQuery()
						+ queryString, SYNTAX);
				// log.debug("query: "+ queryString);
			} catch (Throwable th) {
				log.error("Could not create CONSTRUCT SPARQL query for query "
						+ "string. " + th.getMessage());
				log.error(queryString);
			}

			QueryExecution qe = QueryExecutionFactory.create(query, dataset);
			try {
				qe.execConstruct(constructedModel);
			} finally {
				qe.close();
			}

		}

		after = System.currentTimeMillis();

		// log.debug("Statements for constructed model of EntityGrantCount : "+
		// constructedModel.listStatements().toString());
		log
				.debug("Time taken to execute the CONSTRUCT queries is in milliseconds: "
						+ (after - before));
		// constructedModel.write(System.out);
		return constructedModel;
	}

	public Model getConstructedModel() throws MalformedQueryParametersException {

		if (StringUtils.isNotBlank(this.egoURI)) {
			/*
			 * To test for the validity of the URI submitted.
			 */
			IRIFactory iRIFactory = IRIFactory.jenaImplementation();
			IRI iri = iRIFactory.create(this.egoURI);
			if (iri.hasViolation(false)) {
				String errorMsg = ((Violation) iri.violations(false).next())
						.getShortMessage();
				log.error("Entity Grant Count Construct Query " + errorMsg);
				throw new MalformedQueryParametersException(
						"URI provided for an individual is malformed.");
			}
		} else {
			throw new MalformedQueryParametersException(
					"URI parameter is either null or empty.");
		}

		Set<String> constructQueries = new LinkedHashSet<String>();

		populateConstructQueries(constructQueries);

		Model model = executeQuery(constructQueries, this.dataset);

		return model;

	}

	private void populateConstructQueries(Set<String> constructQueries) {

		constructQueries
				.add(generateConstructQueryForOrganizationLabel(this.egoURI));

		constructQueries.add(generateConstructQueryForSubOrganizations(
				this.egoURI, "core:hasInvestigatorRole"));
		constructQueries.add(generateConstructQueryForPersons(this.egoURI,
				"core:hasInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForDateTimeValueofRoleForOneLevelDeep(
						this.egoURI, "core:hasInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofRole(
				this.egoURI, "core:hasInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForDateTimeValueofGrantForOneLevelDeep(
						this.egoURI, "core:hasInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofGrant(
				this.egoURI, "core:hasInvestigatorRole"));

		constructQueries.add(generateConstructQueryForSubOrganizations(
				this.egoURI, "core:hasPrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForPersons(this.egoURI,
				"core:hasPrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForDateTimeValueofRoleForOneLevelDeep(
						this.egoURI, "core:hasPrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofRole(
				this.egoURI, "core:hasPrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForDateTimeValueofGrantForOneLevelDeep(
						this.egoURI, "core:hasPrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofGrant(
				this.egoURI, "core:hasPrincipalInvestigatorRole"));

		constructQueries.add(generateConstructQueryForSubOrganizations(
				this.egoURI, "core:hasCo-PrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForPersons(this.egoURI,
				"core:hasCo-PrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForDateTimeValueofRoleForOneLevelDeep(
						this.egoURI, "core:hasCo-PrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofRole(
				this.egoURI, "core:hasCo-PrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForDateTimeValueofGrantForOneLevelDeep(
						this.egoURI, "core:hasCo-PrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofGrant(
				this.egoURI, "core:hasCo-PrincipalInvestigatorRole"));

	}

}
