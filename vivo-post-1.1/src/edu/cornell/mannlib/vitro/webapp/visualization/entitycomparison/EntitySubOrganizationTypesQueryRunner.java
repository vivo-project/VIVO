package edu.cornell.mannlib.vitro.webapp.visualization.entitycomparison;

import java.util.HashMap;
import java.util.Map;

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
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;

import java.util.Set;
import java.util.HashSet;

public class EntitySubOrganizationTypesQueryRunner implements QueryRunner<Map<String, Set<String>>> {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String entityURI;
	private DataSource dataSource;
	private Log log;
	
	private static final String SPARQL_QUERY_SELECT_CLAUSE = ""
		+ "		(str(?organizationLabel) as ?"+QueryFieldLabels.ORGANIZATION_LABEL+") "
		+ "		(str(?subOrganizationLabel) as ?"+QueryFieldLabels.SUBORGANIZATION_LABEL+") "
		+ "		(str(?subOrganizationType) as ?"+QueryFieldLabels.SUBORGANIZATION_TYPE +")"
		+ "		(str(?subOrganizationTypeLabel) as ?"+QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL+") ";

	
	public EntitySubOrganizationTypesQueryRunner(String entityURI,
			DataSource dataSource, Log log){
		
		this.entityURI = entityURI;
		this.dataSource = dataSource;
		this.log = log;
	}
	
	private ResultSet executeQuery(String queryURI, DataSource dataSource) {

		QueryExecution queryExecution = null;
		try {
			Query query = QueryFactory.create(
					getSparqlQuery(queryURI), SYNTAX);
			queryExecution = QueryExecutionFactory.create(query, dataSource);

			if (query.isSelectType()) {
				return queryExecution.execSelect();
			}
		} finally {
			if (queryExecution != null) {
				queryExecution.close();
			}
		}
		return null;
	}
	
	private String getSparqlQuery(String queryURI) {

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
				+ "SELECT "
				+ SPARQL_QUERY_SELECT_CLAUSE
				+ " WHERE { " + "<" + queryURI + "> rdf:type foaf:Organization ;"
				+ " rdfs:label ?organizationLabel ;"
				+ " core:hasSubOrganization ?subOrganization .  "
				+ "	?subOrganization rdfs:label ?subOrganizationLabel ;"
				+ " rdf:type ?subOrganizationType .  "
				+ " ?subOrganizationType rdfs:label ?subOrganizationTypeLabel ."
				+ "}";
		System.out.println("\nThe sparql query is :\n" + sparqlQuery);
		
		return sparqlQuery;

	}
	
	private Map<String, Set<String>> createJavaValueObjects(ResultSet resultSet) {

		Map<String, Set<String>> subOrganizationLabelToTypes = new HashMap<String, Set<String>>();
		
		while(resultSet.hasNext()){
			
			QuerySolution solution = resultSet.nextSolution();
			
			RDFNode subOrganizationLabel = solution.get(QueryFieldLabels.SUBORGANIZATION_LABEL);
			
			if(subOrganizationLabelToTypes.containsKey(subOrganizationLabel.toString())){
				RDFNode subOrganizationType = solution.get(QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL);
				if(subOrganizationType != null){
					subOrganizationLabelToTypes.get(subOrganizationLabel.toString()).add(subOrganizationType.toString());
				}
			}else{
				RDFNode subOrganizationType = solution.get(QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL);
				if(subOrganizationType != null){
					subOrganizationLabelToTypes.put(subOrganizationLabel.toString(), new HashSet<String>());
					subOrganizationLabelToTypes.get(subOrganizationLabel.toString()).add(subOrganizationType.toString());
				}
			}
		}
		
		return subOrganizationLabelToTypes;
	}

	
	public Map<String, Set<String>> getQueryResult() throws MalformedQueryParametersException {

		if (StringUtils.isNotBlank(this.entityURI)) {

			/*
			 * To test for the validity of the URI submitted.
			 */
			IRIFactory iRIFactory = IRIFactory.jenaImplementation();
			IRI iri = iRIFactory.create(this.entityURI);
			if (iri.hasViolation(false)) {
				String errorMsg = ((Violation) iri.violations(false).next())
						.getShortMessage();
				log.error("Entity Comparison sub organization types query " + errorMsg);
				throw new MalformedQueryParametersException(
						"URI provided for an entity is malformed.");
			}

		} else {
			throw new MalformedQueryParametersException(
					"URL parameter is either null or empty.");
		}

		ResultSet resultSet = executeQuery(this.entityURI, this.dataSource);

		return createJavaValueObjects(resultSet);
	}

}
