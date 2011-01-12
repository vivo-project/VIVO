/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitycomparison;

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
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;

import java.util.Set;
import java.util.HashSet;


/**
 * @author bkoniden
 * Deepak Konidena
 */
public class EntitySubOrganizationTypesQueryRunner implements QueryRunner<Map<String, Set<String>>> {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String entityURI;
	private DataSource dataSource;
	private Log log;
	
	private static final String SPARQL_QUERY_SELECT_CLAUSE = ""
		+ "		(str(?organizationLabel) as ?"+QueryFieldLabels.ORGANIZATION_LABEL+") "
		+ "		(str(?subOrganizationLabel) as ?"+QueryFieldLabels.SUBORGANIZATION_LABEL+") "
		+ "		(str(?subOrganizationType) as ?"+QueryFieldLabels.SUBORGANIZATION_TYPE +")"
		+ "		(str(?subOrganizationTypeLabel) as ?"+QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL+") "
		+ " 	(str(?Person) as ?personLit) "            
		+ "		(str(?PersonLabel) as ?personLabelLit) "                      
		+ "		(str(?PersonTypeLabel) as ?personTypeLabelLit) ";

	
	public EntitySubOrganizationTypesQueryRunner(String entityURI,
			DataSource dataSource, Log log){
		
		this.entityURI = entityURI;
		this.dataSource = dataSource;
		this.log = log;
	}
	
	private ResultSet executeQuery(String queryURI, DataSource dataSource) {

		QueryExecution queryExecution = null;
		Query query = QueryFactory.create(
				getSparqlQuery(queryURI), SYNTAX);
		queryExecution = QueryExecutionFactory.create(query, dataSource);
			return queryExecution.execSelect();
	}
	
	private String getSparqlQuery(String queryURI) {

		String sparqlQuery = "";

		sparqlQuery = QueryConstants.getSparqlPrefixQuery()
				+ "SELECT "
				+ SPARQL_QUERY_SELECT_CLAUSE
				+ " WHERE { "
				+ "<"
				+ queryURI
				+ "> rdf:type foaf:Organization ;"
				+ " rdfs:label ?organizationLabel . "
				+ "{ "
				+ "<"+ queryURI + "> core:hasSubOrganization ?subOrganization .  "
				+ "?subOrganization rdfs:label ?subOrganizationLabel ; rdf:type ?subOrganizationType . "
				+ "?subOrganizationType rdfs:label ?subOrganizationTypeLabel . "
				+ "}"
				+ "UNION "
				+ "{ "
				+ "<"+ queryURI + "> core:organizationForPosition ?Position . "
				+ "?Position rdf:type core:Position ; core:positionForPerson ?Person . "
				+ "?Person  rdfs:label ?PersonLabel ; rdf:type ?PersonType . "
				+ "?PersonType rdfs:label ??PersonTypeLabel . "
				+ "}"
				+ "}";

		
//		System.out.println("\n\nEntity SubOrganizationTypes query is: "+ sparqlQuery);
		log.debug("\nThe sparql query is :\n" + sparqlQuery);

		return sparqlQuery;

	}
	
	private Map<String, Set<String>> createJavaValueObjects(ResultSet resultSet) {

		Map<String, Set<String>> subOrganizationLabelToTypes = new HashMap<String, Set<String>>();
		Map<String, Set<String>> personLabelToTypes = new HashMap<String, Set<String>>();
		
		while(resultSet.hasNext()){
			
			QuerySolution solution = resultSet.nextSolution();
			
			RDFNode subOrganizationLabel = solution.get(QueryFieldLabels.SUBORGANIZATION_LABEL);
			
			if(subOrganizationLabel != null){
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

			RDFNode personLabel = solution.get(QueryFieldLabels.PERSON_LABEL);
			
			if(personLabel != null){
				if(personLabelToTypes.containsKey(personLabel.toString())){
					RDFNode personType = solution.get(QueryFieldLabels.PERSON_TYPE_LABEL);
					if(personType != null && ! personType.toString().startsWith("http")){
						personLabelToTypes.get(personLabel.toString()).add(personType.toString());
					}
				}else{
					RDFNode personType = solution.get(QueryFieldLabels.PERSON_TYPE_LABEL);
					if(personType != null && ! personType.toString().startsWith("http")){
						personLabelToTypes.put(personLabel.toString(), new HashSet<String>());
						personLabelToTypes.get(personLabel.toString()).add(personType.toString());
					}
				}
			}			
		}		
		
//		System.out.println("\n\nSub Organization Label Types Size --> " + subOrganizationLabelToTypes.size());
//		System.out.println("\n\nPeople Label Types Size --> " + personLabelToTypes.size());
		
		return (subOrganizationLabelToTypes.size() != 0 )? subOrganizationLabelToTypes : personLabelToTypes ;
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

