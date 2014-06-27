/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;

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

public class CoPIGrantCountConstructQueryRunner implements ModelConstructor {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String egoURI;

	private Dataset dataset;

	private long before, after;

	private Log log = LogFactory
			.getLog(CoPIGrantCountConstructQueryRunner.class.getName());

	private static final String SPARQL_QUERY_COMMON_CONSTRUCT_AND_WHERE_STRING = 
			"?Role core:relatedBy ?Grant . "
			+ "?Grant rdf:type core:Grant ."
			+ "?Grant core:relates ?RelatedRole . ";

	public CoPIGrantCountConstructQueryRunner(String egoURI, Dataset dataset,
			Log log) {
		this.egoURI = egoURI;
		this.dataset = dataset;
		// this.log = log;
	}

	private String generateConstructQueryForInvestigatorLabel(String queryURI) {

		String sparqlQuery = "CONSTRUCT { " + "<" + queryURI
				+ ">  rdfs:label ?investigatorLabel ." + "}" + "WHERE {" + "<"
				+ queryURI + ">  rdfs:label ?investigatorLabel " + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForInvestigatorRoleOfProperty(
			String queryURI, String preboundProperty, String preboundRoleType) {

		String sparqlQuery = "CONSTRUCT { " + "<" + queryURI + ">"
				+ preboundProperty + " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ SPARQL_QUERY_COMMON_CONSTRUCT_AND_WHERE_STRING
				+ "?RelatedRole rdf:type core:InvestigatorRole ."
				+ "?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?coInvestigator ."
			    + "?coInvestigator rdf:type foaf:Person  ."
				+ "?coInvestigator rdfs:label ?coInvestigatorLabel . " + "}"
				+ "WHERE { " + "<" + queryURI + ">" + preboundProperty + " ?Role . "  
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ SPARQL_QUERY_COMMON_CONSTRUCT_AND_WHERE_STRING
				+ "?RelatedRole rdf:type core:InvestigatorRole ."
				+ "?RelatedRole vitro:mostSpecificType ?subclass ."
				+ "?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?coInvestigator ."
		        + "?coInvestigator rdf:type foaf:Person  ."
				+ "?coInvestigator rdfs:label ?coInvestigatorLabel . " 
				+ "FILTER (?subclass != core:PrincipalInvestigatorRole && ?subclass != core:CoPrincipalInvestigatorRole)}";

		return sparqlQuery;
	}

	private String generateConstructQueryForPrincipalInvestigatorRoleOfProperty(
			String queryURI, String preboundProperty, String preboundRoleType) {

		String sparqlQuery = "CONSTRUCT { "
				+ "<"
				+ queryURI
				+ ">"
				+ preboundProperty
				+ " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ SPARQL_QUERY_COMMON_CONSTRUCT_AND_WHERE_STRING
				+ "?RelatedRole rdf:type core:PrincipalInvestigatorRole ."
				+ "?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?coInvestigator ."
		        + "?coInvestigator rdf:type foaf:Person  ."
				+ "?coInvestigator rdfs:label ?coInvestigatorLabel . "
				+ "}"
				+ "WHERE { "
				+ "<"
				+ queryURI
				+ ">"
				+ preboundProperty
				+ " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ SPARQL_QUERY_COMMON_CONSTRUCT_AND_WHERE_STRING
				+ "?RelatedRole rdf:type core:PrincipalInvestigatorRole ."
				+ "?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?coInvestigator ."
		        + "?coInvestigator rdf:type foaf:Person  ."
				+ "?coInvestigator rdfs:label ?coInvestigatorLabel . " + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForCoPrincipalInvestigatorRoleOfProperty(
			String queryURI, String preboundProperty, String preboundRoleType) {

		String sparqlQuery = "CONSTRUCT { "
				+ "<"
				+ queryURI
				+ ">"
				+ preboundProperty
				+ " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ SPARQL_QUERY_COMMON_CONSTRUCT_AND_WHERE_STRING
				+ "?RelatedRole rdf:type core:CoPrincipalInvestigatorRole ."
				+ "?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?coInvestigator ."
		        + "?coInvestigator rdf:type foaf:Person  ."
				+ "?coInvestigator rdfs:label ?coInvestigatorLabel . "
				+ "}"
				+ "WHERE { "
				+ "<"
				+ queryURI
				+ ">"
				+ preboundProperty
				+ " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ SPARQL_QUERY_COMMON_CONSTRUCT_AND_WHERE_STRING
				+ "?RelatedRole rdf:type core:CoPrincipalInvestigatorRole ."
				+ "?RelatedRole <http://purl.obolibrary.org/obo/RO_0000052> ?coInvestigator ."
		        + "?coInvestigator rdf:type foaf:Person  ."
				+ "?coInvestigator rdfs:label ?coInvestigatorLabel . " + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForDateTimeValueofRole(
			String queryURI, String preboundProperty, String preboundRoleType) {

		String sparqlQuery = "CONSTRUCT { " + "<" + queryURI + ">"
				+ preboundProperty + " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:start ?startDate . "
				+ "?startDate core:dateTime ?startDateTimeValue . "
//				+ "?dateTimeIntervalValue core:end ?endDate . "
//				+ "?endDate core:dateTime ?endDateTimeValue . " 
				+ "}"
				+ "WHERE { " + "{" + "<" + queryURI + ">" + preboundProperty
				+ " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
				+ "?dateTimeIntervalValue core:start ?startDate . "
				+ "?startDate core:dateTime ?startDateTimeValue . "
//				+ "} UNION " + "{" + "<" + queryURI + ">" + preboundProperty
//				+ " ?Role . "
//				+ "?Role core:dateTimeInterval ?dateTimeIntervalValue . "
//				+ "?dateTimeIntervalValue core:end ?endDate . "
//				+ "?endDate core:dateTime ?endDateTimeValue . " 
				+ "}" + "}";

		return sparqlQuery;
	}

	private String generateConstructQueryForDateTimeValueofGrant(
			String queryURI, String preboundProperty, String preboundRoleType) {

		String sparqlQuery = "CONSTRUCT { " + "<"
				+ queryURI
				+ ">"
				+ preboundProperty
				+ " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ "?Role core:relatedBy ?Grant ."
    			+ "?Grant rdf:type core:Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
				+ "?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
//				+ "?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
//				+ "?endDateForGrant core:dateTime ?endDateTimeValueForGrant . "
				+ "}"
				+ "WHERE { "
				+ "{"
				+ "<"
				+ queryURI
				+ ">"
				+ preboundProperty
				+ " ?Role . "
				+ "?Role rdf:type " + preboundRoleType + " . "
				+ "?Role core:relatedBy ?Grant ."
    			+ "?Grant rdf:type core:Grant ."
				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
				+ "?dateTimeIntervalValueForGrant core:start ?startDateForGrant . "
				+ "?startDateForGrant core:dateTime ?startDateTimeValueForGrant . "
//				+ "} UNION "
//				+ "{"
//				+ "<"
//				+ queryURI
//				+ ">"
//				+ preboundProperty
//				+ " ?Role . "
//				+ "?Role core:roleContributesTo ?Grant ."
//				+ "?Grant core:dateTimeInterval ?dateTimeIntervalValueForGrant . "
//				+ "?dateTimeIntervalValueForGrant core:end ?endDateForGrant . "
//				+ "?endDateForGrant core:dateTime ?endDateTimeValueForGrant . "
				+ "}" + "}";

		return sparqlQuery;
	}

	private Model executeQuery(Set<String> constructQueries, Dataset dataset) {

		Model constructedModel = ModelFactory.createDefaultModel();

		for (String queryString : constructQueries) {

			before = System.currentTimeMillis();

			log.debug("CONSTRUCT query string : " + queryString);

			Query query = null;

			try {
				query = QueryFactory.create(
						QueryConstants.getSparqlPrefixQuery() + queryString,
						SYNTAX);
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

			after = System.currentTimeMillis();
			log.debug("Time taken to execute the CONSTRUCT query is in milliseconds: "
					+ (after - before));

		}

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
				log.error("Ego Co-PI Vis Query " + errorMsg);
				throw new MalformedQueryParametersException(
						"URI provided for an individual is malformed.");
			}
		} else {
			throw new MalformedQueryParametersException(
					"URI parameter is either null or empty.");
		}

		Set<String> constructQueries = new HashSet<String>();

		populateConstructQueries(constructQueries);

		Model model = executeQuery(constructQueries, this.dataset);

		return model;

	}

	private void populateConstructQueries(Set<String> constructQueries) {

		constructQueries
				.add(generateConstructQueryForInvestigatorLabel(this.egoURI));
		constructQueries
				.add(generateConstructQueryForInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:InvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForCoPrincipalInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:InvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForPrincipalInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:InvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofRole(
				this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:InvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofGrant(
				this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:InvestigatorRole"));

		constructQueries
				.add(generateConstructQueryForInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:PrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForCoPrincipalInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:PrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForPrincipalInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:PrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofRole(
				this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:PrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofGrant(
				this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:PrincipalInvestigatorRole"));

		constructQueries
				.add(generateConstructQueryForInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:CoPrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForCoPrincipalInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:CoPrincipalInvestigatorRole"));
		constructQueries
				.add(generateConstructQueryForPrincipalInvestigatorRoleOfProperty(
						this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:CoPrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofRole(
				this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:CoPrincipalInvestigatorRole"));
		constructQueries.add(generateConstructQueryForDateTimeValueofGrant(
				this.egoURI, "<http://purl.obolibrary.org/obo/RO_0000053>", "core:CoPrincipalInvestigatorRole"));

	}
}