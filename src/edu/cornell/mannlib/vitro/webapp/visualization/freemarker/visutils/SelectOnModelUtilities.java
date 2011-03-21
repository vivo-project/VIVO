/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.ModelConstructorUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.OrganizationAssociatedPeopleModelWithTypesConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.OrganizationModelWithTypesConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.OrganizationToGrantsForSubOrganizationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.OrganizationToPublicationsForSubOrganizationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.PersonToGrantsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.PersonToPublicationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SubEntity;

public class SelectOnModelUtilities {
	
	public static Entity getSubjectOrganizationHierarchy(Dataset dataset,
			String subjectEntityURI) throws MalformedQueryParametersException {
		Model organizationModel = ModelConstructorUtilities
										.getOrConstructModel(
												null, 
												OrganizationModelWithTypesConstructor.MODEL_TYPE, 
												dataset);
		
		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("organizationLabel", QueryFieldLabels.ORGANIZATION_LABEL);
		fieldLabelToOutputFieldLabel.put("subOrganization", QueryFieldLabels.SUBORGANIZATION_URL);
		fieldLabelToOutputFieldLabel.put("subOrganizationLabel", QueryFieldLabels.SUBORGANIZATION_LABEL);
		fieldLabelToOutputFieldLabel.put("subOrganizationTypeLabel", QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL);
		
		String whereClause = ""
			+ " <" + subjectEntityURI + "> rdfs:label ?organizationLabel . "
			+ " <" + subjectEntityURI + "> core:hasSubOrganization ?subOrganization . "
			+ " ?subOrganization rdfs:label ?subOrganizationLabel . "
			+ " ?subOrganization rdf:type ?subOrgType . "
			+ " ?subOrgType rdfs:label ?subOrganizationTypeLabel . ";
		
		QueryRunner<ResultSet> subOrganizationsWithTypesQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									organizationModel);
		
		return getEntityWithSubOrganizations(subjectEntityURI, 
												   subOrganizationsWithTypesQuery.getQueryResult());
	}
	
	private static Entity getEntityWithSubOrganizations(String subjectEntityURI, ResultSet queryResult) {

		Entity entity = new Entity(subjectEntityURI);
		Map<String, SubEntity> subOrganizationURIToVO = new HashMap<String, SubEntity>();
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(entity.getEntityLabel())) {
				
				RDFNode organizationLabelNode = solution.get(QueryFieldLabels.ORGANIZATION_LABEL);
				if (organizationLabelNode != null) {
					entity.setIndividualLabel(organizationLabelNode.toString());
				}
			}
			
			RDFNode subOrganizationNode = solution.get(QueryFieldLabels.SUBORGANIZATION_URL);
			
			SubEntity subEntity;
			
			if (subOrganizationURIToVO.containsKey(subOrganizationNode.toString())) {
				
				subEntity = subOrganizationURIToVO.get(subOrganizationNode.toString());
				
			} else {
				
				subEntity = new SubEntity(subOrganizationNode.toString());
				
				subEntity.setEntityClass(VOConstants.EntityClassType.ORGANIZATION);
				
				subOrganizationURIToVO.put(subOrganizationNode.toString(), subEntity);
				
				RDFNode subOrganizationLabelNode = solution.get(QueryFieldLabels.SUBORGANIZATION_LABEL);
				if (subOrganizationLabelNode != null) {
					subEntity.setIndividualLabel(subOrganizationLabelNode.toString());
				}
			}

			RDFNode subOrganizationTypeLabelNode = solution.get(QueryFieldLabels.SUBORGANIZATION_TYPE_LABEL);
			if (subOrganizationTypeLabelNode != null) {
				subEntity.addEntityTypeLabel(subOrganizationTypeLabelNode.toString());
			}
		}
		
		entity.addSubEntitities(subOrganizationURIToVO.values());
		
		return entity;
	}
	
	public static Entity getSubjectOrganizationAssociatedPeople(Dataset dataset,
			String subjectEntityURI) throws MalformedQueryParametersException {
		Model associatedPeopleModel = ModelConstructorUtilities
										.getOrConstructModel(
												subjectEntityURI, 
												OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE, 
												dataset);
		
		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("organizationLabel", QueryFieldLabels.ORGANIZATION_LABEL);
		fieldLabelToOutputFieldLabel.put("person", QueryFieldLabels.PERSON_URL);
		fieldLabelToOutputFieldLabel.put("personLabel", QueryFieldLabels.PERSON_LABEL);
		fieldLabelToOutputFieldLabel.put("personTypeLabel", QueryFieldLabels.PERSON_TYPE_LABEL);
		
		String whereClause = ""
			+ " <" + subjectEntityURI + "> rdfs:label ?organizationLabel . "
			+ " <" + subjectEntityURI + "> vivosocnet:hasPersonWithActivity ?person . "
			+ " ?person rdfs:label ?personLabel . "
			+ " ?person rdf:type ?personType . "
			+ " ?personType rdfs:label ?personTypeLabel . ";
		
		QueryRunner<ResultSet> associatedPeopleWithTypesQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									associatedPeopleModel);
		
		return getEntityWithAssociatedPeopleSubEntitities(subjectEntityURI, associatedPeopleWithTypesQuery.getQueryResult());
	}
	
	private static Entity getEntityWithAssociatedPeopleSubEntitities(
			String subjectEntityURI, ResultSet queryResult) {

		Entity entity = new Entity(subjectEntityURI);
		Map<String, SubEntity> associatedPeopleURIToVO = new HashMap<String, SubEntity>();
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(entity.getEntityLabel())) {
				
				RDFNode organizationLabelNode = solution.get(QueryFieldLabels.ORGANIZATION_LABEL);
				if (organizationLabelNode != null) {
					entity.setIndividualLabel(organizationLabelNode.toString());
				}
			}
			
			RDFNode personNode = solution.get(QueryFieldLabels.PERSON_URL);
			
			SubEntity subEntity;
			
			if (associatedPeopleURIToVO.containsKey(personNode.toString())) {
				
				subEntity = associatedPeopleURIToVO.get(personNode.toString());
				
			} else {
				
				subEntity = new SubEntity(personNode.toString());
				subEntity.setEntityClass(VOConstants.EntityClassType.PERSON);
				associatedPeopleURIToVO.put(personNode.toString(), subEntity);
				
				RDFNode personLabelNode = solution.get(QueryFieldLabels.PERSON_LABEL);
				if (personLabelNode != null) {
					subEntity.setIndividualLabel(personLabelNode.toString());
				}
			}

			RDFNode personTypeLabelNode = solution.get(QueryFieldLabels.PERSON_TYPE_LABEL);
			if (personTypeLabelNode != null) {
				subEntity.addEntityTypeLabel(personTypeLabelNode.toString());
			}
		}
		
		entity.addSubEntitities(associatedPeopleURIToVO.values());
		return entity;
	}
	
	public static Map<String, Activity> getPublicationsForAllSubOrganizations(
			Dataset dataset, Entity organizationEntity)
			throws MalformedQueryParametersException {
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		for (SubEntity subOrganization : organizationEntity.getSubEntities()) {
			
			Model subOrganizationPublicationsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	subOrganization.getIndividualURI(),
																	OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE,
																	dataset);
			
			System.out.println("getting publications for " + subOrganization.getIndividualLabel());
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("document", QueryFieldLabels.DOCUMENT_URL);
			fieldLabelToOutputFieldLabel.put("documentLabel", QueryFieldLabels.DOCUMENT_LABEL);
			fieldLabelToOutputFieldLabel.put("documentPublicationDate", QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			fieldLabelToOutputFieldLabel.put("lastCachedDateTime", QueryFieldLabels.LAST_CACHED_AT_DATETIME);
			
			String whereClause = ""
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:hasPersonWithPublication ?document . "
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:lastCachedAt ?lastCachedDateTime . "
				+ " ?document rdfs:label ?documentLabel . "
				+ " OPTIONAL { "
				+ " 	?document core:dateTimeValue ?dateTimeValue . "
				+ "     ?dateTimeValue core:dateTime ?documentPublicationDate } . ";
			
			QueryRunner<ResultSet> subOrganizationPublicationsQuery = 
				new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
										"",
										whereClause,
										"",
										subOrganizationPublicationsModel);
			
			subOrganization.addActivities(getPublicationForEntity(
												subOrganizationPublicationsQuery.getQueryResult(),
												allDocumentURIToVOs));
			
		}
		return allDocumentURIToVOs;
	}
	
	private static Collection<Activity> getPublicationForEntity(
			ResultSet queryResult,
			Map<String, Activity> allDocumentURIToVOs) {
		
		Set<Activity> currentEntityPublications = new HashSet<Activity>();

		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			Activity biboDocument;
			
			if (allDocumentURIToVOs.containsKey(documentNode.toString())) {
				biboDocument = allDocumentURIToVOs.get(documentNode.toString());

			} else {

				biboDocument = new Activity(documentNode.toString());
				allDocumentURIToVOs.put(documentNode.toString(), biboDocument);

				RDFNode publicationDateNode = solution.get(QueryFieldLabels
																.DOCUMENT_PUBLICATION_DATE);
				if (publicationDateNode != null) {
					biboDocument.setActivityDate(publicationDateNode.toString());
				}
			}
			
			currentEntityPublications.add(biboDocument);
			
		}
		
		return currentEntityPublications;
	}
	
	
	private static Collection<Activity> getGrantForEntity(
			ResultSet queryResult,
			Map<String, Activity> allGrantURIToVO) {
		
		Set<Activity> currentEntityGrants = new HashSet<Activity>();

		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
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
		return currentEntityGrants;
	}
	
	public static Map<String, Activity> getGrantsForAllSubOrganizations(
			Dataset dataset, Entity organizationEntity)
			throws MalformedQueryParametersException {
		Map<String, Activity> allGrantURIToVO = new HashMap<String, Activity>();
		
		for (SubEntity subOrganization : organizationEntity.getSubEntities()) {
			
			System.out.println("constructing grants for " + subOrganization.getIndividualLabel() + " :: " + subOrganization.getIndividualURI());
			
			Model subOrganizationGrantsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	subOrganization.getIndividualURI(),
																	OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE,
																	dataset);
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("grant", QueryFieldLabels.GRANT_URL);
			fieldLabelToOutputFieldLabel.put("grantLabel", QueryFieldLabels.GRANT_LABEL);
			fieldLabelToOutputFieldLabel.put("grantStartDate", QueryFieldLabels.GRANT_START_DATE);
			fieldLabelToOutputFieldLabel.put("roleStartDate", QueryFieldLabels.ROLE_START_DATE);
			
			String whereClause = ""
				+ "{"
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:hasInvestigatorWithGrant ?grant . "
				+ " ?grant rdfs:label ?grantLabel . "
				+ " OPTIONAL { "
				+ " 	?grant vivosocnet:startDateTimeOnGrant ?grantStartDate } . "
				+ " OPTIONAL { "
				+ " 	?grant vivosocnet:startDateTimeOnRole ?roleStartDate } . "
				+ "}"
				+ "UNION"
				+ "{"
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:hasPIWithGrant ?grant . "
				+ " ?grant rdfs:label ?grantLabel . "
				+ " OPTIONAL { "
				+ " 	?grant vivosocnet:startDateTimeOnGrant ?grantStartDate } . "
				+ " OPTIONAL { "
				+ " 	?grant vivosocnet:startDateTimeOnRole ?roleStartDate } . "
				+ "}"
				+ "UNION"
				+ "{"
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:hascoPIWithGrant ?grant . "
				+ " ?grant rdfs:label ?grantLabel . "
				+ " OPTIONAL { "
				+ " 	?grant vivosocnet:startDateTimeOnGrant ?grantStartDate } . "
				+ " OPTIONAL { "
				+ " 	?grant vivosocnet:startDateTimeOnRole ?roleStartDate } . "
				+ "}";
			
			QueryRunner<ResultSet> subOrganizationGrantsQuery = 
				new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
										"",
										whereClause,
										"",
										subOrganizationGrantsModel);
			
			subOrganization.addActivities(getGrantForEntity(
												subOrganizationGrantsQuery.getQueryResult(),
												allGrantURIToVO));
			
		}
		return allGrantURIToVO;
	}
	
	public static Map<String, Activity> getGrantsForAssociatedPeople(
			Dataset dataset, Collection<SubEntity> people)
			throws MalformedQueryParametersException {
		Map<String, Activity> allGrantURIToVOs = new HashMap<String, Activity>();
		
		System.out.println("peopel for grants under consideration are ");
		for (SubEntity person : people) {
		System.out.println(person.getIndividualURI() + " -- " + person.getIndividualLabel());	
		}
		
		for (SubEntity person : people) {
		
			System.out.println("constructing grants for " + person.getIndividualLabel() + " :: " + person.getIndividualURI());
			
			long before = System.currentTimeMillis();
			
			Model personGrantsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	person.getIndividualURI(),
																	PersonToGrantsModelConstructor.MODEL_TYPE,
																	dataset);
			
			System.out.print("\t construct took " + (System.currentTimeMillis() - before));
			
			before = System.currentTimeMillis();
			
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
			
			person.addActivities(getGrantForEntity(
												personGrantsQuery.getQueryResult(),
												allGrantURIToVOs));
			
			System.out.println("\t || select took " + (System.currentTimeMillis() - before));
			
		}
		return allGrantURIToVOs;
	}
	
	public static Map<String, Activity> getPublicationsForAssociatedPeople(
			Dataset dataset, Collection<SubEntity> people)
			throws MalformedQueryParametersException {
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		for (SubEntity person : people) {
			
			Model personPublicationsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	person.getIndividualURI(),
																	PersonToPublicationsModelConstructor.MODEL_TYPE,
																	dataset);
			
			System.out.println("getting publications for " + person.getIndividualLabel());
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("document", QueryFieldLabels.DOCUMENT_URL);
			fieldLabelToOutputFieldLabel.put("documentLabel", QueryFieldLabels.DOCUMENT_LABEL);
			fieldLabelToOutputFieldLabel.put("documentPublicationDate", QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			
			String whereClause = ""
				+ " <" + person.getIndividualURI() + "> vivosocnet:hasPublication ?document . "
				+ " ?document rdfs:label ?documentLabel . "
				+ " OPTIONAL { "
				+ " 	?document core:dateTimeValue ?dateTimeValue . "
				+ "     ?dateTimeValue core:dateTime ?documentPublicationDate } . ";
			
			QueryRunner<ResultSet> personPublicationsQuery = 
				new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
										"",
										whereClause,
										"",
										personPublicationsModel);
			
			person.addActivities(getPublicationForEntity(
												personPublicationsQuery.getQueryResult(),
												allDocumentURIToVOs));
		}
		return allDocumentURIToVOs;
	}
}