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
	private String visMode;
	static String SUBORGANISATION_LABEL;
	static String SUBORGANISATION_TYPE_LABEL;
//	public static Map<String, Integer> subOrganizationTypesToCount = new HashMap<String, Integer>();
//	public static Set<String> stopWords = new HashSet<String>();
//	public static Set<String> subOrganizations = new HashSet<String>();
//	public static Set<String> STOP_WORDS = new HashSet<String>() {
//		{
//			add("Person");
//			add("Organization");
//		}
//	};
	
	private static final String SPARQL_QUERY_SELECT_CLAUSE = ""
		+ "		(str(?organizationLabel) as ?"+QueryFieldLabels.ORGANIZATION_LABEL+") "
		+ "		(str(?subOrganizationLabel) as ?"+QueryFieldLabels.SUBORGANIZATION_LABEL+") "
		+ "		(str(?subOrganizationType) as ?"+QueryFieldLabels.SUBORGANIZATION_TYPE +")"
		+ "		(str(?subOrganizationTypeLabel) as ?"+QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL+") ";

	
	public EntitySubOrganizationTypesQueryRunner(String entityURI,
			DataSource dataSource, Log log, String visMode){
		
		this.entityURI = entityURI;
		this.dataSource = dataSource;
		this.log = log;
		this.visMode = visMode;
//		stopWords.clear();
//		subOrganizations.clear();
//		subOrganizationTypesToCount.clear();
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
		
		if (!this.visMode.equals("DEPARTMENT")) {
			
			SUBORGANISATION_LABEL = QueryFieldLabels.SUBORGANIZATION_LABEL;
			SUBORGANISATION_TYPE_LABEL = QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL;
			sparqlQuery = QueryConstants.getSparqlPrefixQuery()
					+ "SELECT "
					+ SPARQL_QUERY_SELECT_CLAUSE
					+ " WHERE { "
					+ "<"
					+ queryURI
					+ "> rdf:type foaf:Organization ;"
					+ " rdfs:label ?organizationLabel ;"
					+ " core:hasSubOrganization ?subOrganization .  "
					+ "	?subOrganization rdfs:label ?subOrganizationLabel ;"
					+ " rdf:type ?subOrganizationType .  "
					+ " ?subOrganizationType rdfs:label ?subOrganizationTypeLabel ."
					+ "}";
			
		} else{
			
			SUBORGANISATION_LABEL = QueryFieldLabels.PERSON_LABEL;
			SUBORGANISATION_TYPE_LABEL = QueryFieldLabels.PERSON_TYPE_LABEL;
			sparqlQuery = QueryConstants.getSparqlPrefixQuery()
						  + "SELECT "
							+ "		(str(?departmentLabel) as ?"+QueryFieldLabels.DEPARTMENT_LABEL+") "
							+ "		(str(?personLabel) as ?"+QueryFieldLabels.PERSON_LABEL+") "
							+ "		(str(?personType) as ?"+QueryFieldLabels.PERSON_TYPE +")"
							+ "		(str(?personTypeLabel) as ?"+QueryFieldLabels.PERSON_TYPE_LABEL+") "
							+ " WHERE { "
							+ "<"
							+ queryURI
							+ "> rdf:type core:Department ;"
							+ " rdfs:label ?departmentLabel ;"
							+ " core:organizationForPosition ?position .  "
							+ " ?position rdf:type core:Position ; core:positionForPerson ?person . "
							+ "	?person rdfs:label ?personLabel ;"
							+ " rdf:type ?personType .  "
							+ " ?personType rdfs:label ?personTypeLabel ."
							+ "}";;
		}
		log.debug("\nThe sparql query is :\n" + sparqlQuery);
		return sparqlQuery;

	}
	
	private Map<String, Set<String>> createJavaValueObjects(ResultSet resultSet) {

		Map<String, Set<String>> subOrganizationLabelToTypes = new HashMap<String, Set<String>>();
		
		while(resultSet.hasNext()){
			
			QuerySolution solution = resultSet.nextSolution();
			
			RDFNode subOrganizationLabel = solution.get(SUBORGANISATION_LABEL);
			
			if(subOrganizationLabelToTypes.containsKey(subOrganizationLabel.toString())){
				RDFNode subOrganizationType = solution.get(SUBORGANISATION_TYPE_LABEL);
				if(subOrganizationType != null){
					subOrganizationLabelToTypes.get(subOrganizationLabel.toString()).add(subOrganizationType.toString());
//					updateSubOrganizationTypesToCount(subOrganizationType.toString());
//					subOrganizations.add(subOrganizationLabel.toString());
				}
			}else{
				RDFNode subOrganizationType = solution.get(SUBORGANISATION_TYPE_LABEL);
				if(subOrganizationType != null){
					subOrganizationLabelToTypes.put(subOrganizationLabel.toString(), new HashSet<String>());
					subOrganizationLabelToTypes.get(subOrganizationLabel.toString()).add(subOrganizationType.toString());
//					updateSubOrganizationTypesToCount(subOrganizationType.toString());
//					subOrganizations.add(subOrganizationLabel.toString());
				}
			}
		}
		
//		collectStopWords();
		
		return subOrganizationLabelToTypes;
	}

	
//	private void collectStopWords() {
//		System.out.println("Inside collectStopWords \n-----------------------------\n");
//		for(Map.Entry<String, Integer> typesCount : subOrganizationTypesToCount.entrySet()){
//			System.out.println(typesCount.getKey() + ": "+ typesCount.getValue());
//			if(typesCount.getValue() >= subOrganizations.size()){
//				stopWords.add(typesCount.getKey());
//			}
//		}
//	}
//
//	private void updateSubOrganizationTypesToCount(String typeLabel) {
//		int count = 0;
//		if(subOrganizationTypesToCount.containsKey(typeLabel)){
//			count = subOrganizationTypesToCount.get(typeLabel);
//			subOrganizationTypesToCount.put(typeLabel, ++count);
//		}else{
//			subOrganizationTypesToCount.put(typeLabel, 1);
//		}
//	}

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

