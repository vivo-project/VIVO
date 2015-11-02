/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.ModelConstructorUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.PersonToGrantsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;

public class SelectOnModelUtilities {
	public static String getLastCachedAtForEntity(ResultSet queryResult) {
		
		String lastCachedAtDateTime = null;
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
				
			RDFNode lastCachedAtNode = solution.get(QueryFieldLabels.LAST_CACHED_AT_DATETIME);
			if (lastCachedAtNode != null) {
				lastCachedAtDateTime = lastCachedAtNode.toString();
			}
		}
		
		return lastCachedAtDateTime;
	}
	
	private static void getGrantForEntity(
			ResultSet queryResult,
			SubEntity subEntity, 
			Map<String, Activity> allGrantURIToVO) {
		
		Set<Activity> currentEntityGrants = new HashSet<Activity>();

		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(subEntity.getLastCachedAtDateTime())) {
				
				RDFNode lastCachedAtNode = solution.get(QueryFieldLabels.LAST_CACHED_AT_DATETIME);
				if (lastCachedAtNode != null) {
					subEntity.setLastCachedAtDateTime(lastCachedAtNode.toString());
				}
			}
			
			RDFNode grantNode = solution.get(QueryFieldLabels.GRANT_URL);
			Activity coreGrant;
			
			if (allGrantURIToVO.containsKey(grantNode.toString())) {
				coreGrant = allGrantURIToVO.get(grantNode.toString());

			} else {

				coreGrant = new Activity(grantNode.toString());
				allGrantURIToVO.put(grantNode.toString(), coreGrant);

				RDFNode grantStartDateNode = solution.get(QueryFieldLabels.ROLE_START_DATE);
				
				if (grantStartDateNode != null) {
					coreGrant.setActivityDate(grantStartDateNode.toString());
				} else {
					grantStartDateNode = solution
							.get(QueryFieldLabels.GRANT_START_DATE);
					if (grantStartDateNode != null) {
						coreGrant.setActivityDate(grantStartDateNode.toString());
					}
				}
			}
			currentEntityGrants.add(coreGrant);
		}
		
		subEntity.addActivities(currentEntityGrants);
	}

	private static String getLastCachedAtDateTimeForEntityInModel(
			SubEntity entity, Model subOrganizationGrantsModel)
			throws MalformedQueryParametersException {

		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("lastCachedAtDateTime", QueryFieldLabels.LAST_CACHED_AT_DATETIME);
		
		String whereClause = ""
			+ "{"
			+ " <" + entity.getIndividualURI() + "> vivosocnet:lastCachedAt ?lastCachedAtDateTime . "
			+ "}";
		
		QueryRunner<ResultSet> entityLastCachedAtQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									subOrganizationGrantsModel);
		
		String lastCachedAtForEntity = getLastCachedAtForEntity(entityLastCachedAtQuery.getQueryResult());
		return lastCachedAtForEntity;
	}

	/**
	 * This method side-effects person and the central grants map.
	 * @param person
	 * @param allGrantURIToVOs
	 * @param personGrantsModel
	 * @throws MalformedQueryParametersException
	 */
	private static void updateGrantsForPerson(SubEntity person,
			Map<String, Activity> allGrantURIToVOs, Model personGrantsModel)
			throws MalformedQueryParametersException {
		
		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("grant", QueryFieldLabels.GRANT_URL);
		fieldLabelToOutputFieldLabel.put("grantLabel", QueryFieldLabels.GRANT_LABEL);
		fieldLabelToOutputFieldLabel.put("grantStartDate", QueryFieldLabels.GRANT_START_DATE);
		fieldLabelToOutputFieldLabel.put("roleStartDate", QueryFieldLabels.ROLE_START_DATE);
		
		String whereClause = ""
			+ "{"
			+ " <" + person.getIndividualURI() + "> vivosocnet:hasGrantAsAnInvestigator ?grant . "
			+ " ?grant rdfs:label ?grantLabel . "
			+ " OPTIONAL { "
			+ " 	?grant vivosocnet:startDateTimeOnGrant ?grantStartDate } . "
			+ " OPTIONAL { "
			+ " 	?grant vivosocnet:startDateTimeOnRole ?roleStartDate } . "
			+ "}"
			+ "UNION"
			+ "{"
			+ " <" + person.getIndividualURI() + "> vivosocnet:hasGrantAsPI ?grant . "
			+ " ?grant rdfs:label ?grantLabel . "
			+ " OPTIONAL { "
			+ " 	?grant vivosocnet:startDateTimeOnGrant ?grantStartDate } . "
			+ " OPTIONAL { "
			+ " 	?grant vivosocnet:startDateTimeOnRole ?roleStartDate } . "
			+ "}"
			+ "UNION"
			+ "{"
			+ " <" + person.getIndividualURI() + "> vivosocnet:hasGrantAsCoPI ?grant . "
			+ " ?grant rdfs:label ?grantLabel . "
			+ " OPTIONAL { "
			+ " 	?grant vivosocnet:startDateTimeOnGrant ?grantStartDate } . "
			+ " OPTIONAL { "
			+ " 	?grant vivosocnet:startDateTimeOnRole ?roleStartDate } . "
			+ "}";
		
		QueryRunner<ResultSet> personGrantsQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									personGrantsModel);
		
		getGrantForEntity(personGrantsQuery.getQueryResult(), person, allGrantURIToVOs);
		
		String lastCachedAtForEntity = getLastCachedAtDateTimeForEntityInModel(
				person, 
				personGrantsModel);

		person.setLastCachedAtDateTime(lastCachedAtForEntity);
	}
	
	public static Map<String, Activity> getGrantsForPerson(
			RDFService rdfService, SubEntity person, boolean doCache)
			throws MalformedQueryParametersException {
		
		Map<String, Activity> allGrantURIToVOs = new HashMap<String, Activity>();
		
		Model personGrantsModel = null;
		

		/*
		 * If we dont want to cache the results then create the model directly without
		 * using the ModelConstructorUtilities. Use case is the co-pi ego-centric 
		 * visualization. 
		 * */
		if (doCache) {
			personGrantsModel = ModelConstructorUtilities
											.getOrConstructModel(
													person.getIndividualURI(),
													PersonToGrantsModelConstructor.MODEL_TYPE,
													rdfService);
		} else {
			
			ModelConstructor model = new PersonToGrantsModelConstructor(person.getIndividualURI(), rdfService);
			personGrantsModel = model.getConstructedModel();
		}
		
		
		updateGrantsForPerson(person, allGrantURIToVOs, personGrantsModel);
			
			
		return allGrantURIToVOs;
	}
}
