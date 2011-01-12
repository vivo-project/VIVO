/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.entitycomparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.JsonObject;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;

public class EntityPublicationCountRequestHandler implements
		VisualizationRequestHandler {
	
	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		QueryRunner<Entity> queryManager = new EntityPublicationCountQueryRunner(
				entityURI, dataSource, log);
		
		Entity entity = queryManager.getQueryResult();
 
		if(entity.getEntityLabel().equals("no-label")){

			return prepareStandaloneErrorResponse(vitroRequest,entityURI);
		
		} else{
	
			QueryRunner<Map<String, Set<String>>> queryManagerForsubOrganisationTypes = new EntitySubOrganizationTypesQueryRunner(
					entityURI, dataSource, log);
	
			Map<String, Set<String>> subOrganizationTypesResult = queryManagerForsubOrganisationTypes
					.getQueryResult();
	
			return prepareStandaloneResponse(vitroRequest, entity, entityURI,
					subOrganizationTypesResult);
		}
	
	}
	

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {

		String entityURI = vitroRequest
				.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
				
		QueryRunner<Entity> queryManager = new EntityPublicationCountQueryRunner(
				entityURI, dataSource, log);	
		
		Entity entity = queryManager.getQueryResult();

		
		QueryRunner<Map<String, Set<String>>> queryManagerForsubOrganisationTypes = new EntitySubOrganizationTypesQueryRunner(
				entityURI, dataSource, log);
		
		Map<String, Set<String>> subOrganizationTypesResult = queryManagerForsubOrganisationTypes.getQueryResult();

		return prepareDataResponse(entity, entity.getSubEntities(),subOrganizationTypesResult);

	}
	
	
	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			DataSource dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Entity Pub Count does not provide Ajax Response.");
	}

	/**
	 * Provides response when json file containing the publication count over the
	 * years is requested.
	 * 
	 * @param entity
	 * @param subentities
	 * @param subOrganizationTypesResult
	 */
	private Map<String, String> prepareDataResponse(Entity entity, Set<SubEntity> subentities,
			Map<String, Set<String>> subOrganizationTypesResult) {

		String entityLabel = entity.getEntityLabel();

		/*
		* To make sure that null/empty records for entity names do not cause any mischief.
		* */
		if (StringUtils.isBlank(entityLabel)) {
			entityLabel = "no-organization";
		}
		
		String outputFileName = UtilityFunctions.slugify(entityLabel)
				+ "_publications-per-year" + ".csv";
		
		
		Map<String, String> fileData = new HashMap<String, String>();
		
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
				getEntityPublicationsPerYearCSVContent(subentities, subOrganizationTypesResult));
		return fileData;
}
	
	/**
	 * 
	 * @param vreq
	 * @param valueObjectContainer
	 * @return
	 */
	private TemplateResponseValues prepareStandaloneResponse(VitroRequest vreq,
			Entity entity, String entityURI, Map<String, Set<String>> subOrganizationTypesResult) {

        Portal portal = vreq.getPortal();
        String standaloneTemplate = "entityComparisonStandaloneActivator.ftl";
		
        String jsonContent = "";
		jsonContent = writePublicationsOverTimeJSON(vreq, entity.getSubEntities(), subOrganizationTypesResult);

		

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("portalBean", portal);
        body.put("title", "Temporal Graph Visualization");
        body.put("organizationURI", entityURI);
        body.put("organizationLabel", entity.getEntityLabel());
        body.put("jsonContent", jsonContent);
        
        return new TemplateResponseValues(standaloneTemplate, body);
        
	}
	
	
	private ResponseValues prepareStandaloneErrorResponse(
			VitroRequest vitroRequest, String entityURI) {
		
        Portal portal = vitroRequest.getPortal();
        String standaloneTemplate = "entityComparisonErrorActivator.ftl";
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("portalBean", portal);
        body.put("title", "Temporal Graph Visualization");
        body.put("organizationURI", entityURI);
        
        return new TemplateResponseValues(standaloneTemplate, body);

	}
	
	
	/**
	 * function to generate a json file for year <-> publication count mapping
	 * @param vreq 
	 * @param subentities
	 * @param subOrganizationTypesResult  
	 */
	private String writePublicationsOverTimeJSON(VitroRequest vreq, Set<SubEntity> subentities, Map<String, Set<String>> subOrganizationTypesResult) {

		Gson json = new Gson();
		Set<JsonObject> subEntitiesJson = new HashSet<JsonObject>();

		for (SubEntity subentity : subentities) {
			JsonObject entityJson = new JsonObject(
					subentity.getIndividualLabel());

			List<List<Integer>> yearPubCount = new ArrayList<List<Integer>>();

			for (Map.Entry<String, Integer> pubEntry : UtilityFunctions
					.getYearToPublicationCount(subentity.getDocuments())
					.entrySet()) {

				List<Integer> currentPubYear = new ArrayList<Integer>();
				if (pubEntry.getKey().equals(
						VOConstants.DEFAULT_PUBLICATION_YEAR))
					currentPubYear.add(-1);
				else
					currentPubYear.add(Integer.parseInt(pubEntry.getKey()));
				currentPubYear.add(pubEntry.getValue());
				yearPubCount.add(currentPubYear);
			}

			entityJson.setYearToActivityCount(yearPubCount);
			entityJson.getOrganizationType().addAll(subOrganizationTypesResult.get(entityJson.getLabel()));

			entityJson.setEntityURI(subentity.getIndividualURI());
			
			boolean isPerson = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subentity.getIndividualURI()).isVClass("http://xmlns.com/foaf/0.1/Person");
			
			if(isPerson){
				entityJson.setVisMode("PERSON");
			} else{
				entityJson.setVisMode("ORGANIZATION");
			}
		//	setEntityVisMode(entityJson);
			subEntitiesJson.add(entityJson);
		}
		
		return json.toJson(subEntitiesJson);

	}
	
	private String getEntityPublicationsPerYearCSVContent(Set<SubEntity> subentities, Map<String, Set<String>> subOrganizationTypesResult) {

		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Entity Name, Publication Count, Entity Type\n");
		
		for(SubEntity subEntity : subentities){
			
			csvFileContent.append(StringEscapeUtils.escapeCsv(subEntity.getIndividualLabel()));
			csvFileContent.append(", ");
			csvFileContent.append(subEntity.getDocuments().size());
			csvFileContent.append(", ");
			
			StringBuilder joinedTypes = new StringBuilder();
			
			for(String subOrganizationType : subOrganizationTypesResult.get(subEntity.getIndividualLabel())){
				joinedTypes.append(subOrganizationType + "; ");
			}
			
			csvFileContent.append(StringEscapeUtils.escapeCsv(joinedTypes.toString()));
			csvFileContent.append("\n");

		}

		return csvFileContent.toString();

	}
	
}	