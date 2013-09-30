/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

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
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.ModelConstructorUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationAssociatedPeopleModelWithTypesConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationModelWithTypesConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationToGrantsForSubOrganizationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationToPublicationsForSubOrganizationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.PeopleToGrantsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.PeopleToPublicationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.PersonToGrantsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.PersonToPublicationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.SubOrganizationWithinModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.MapOfScienceActivity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;

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
			+ " <" + subjectEntityURI + "> <http://purl.obolibrary.org/obo/BFO_0000051> ?subOrganization . "
            + " ?subOrganization rdf:type foaf:Organization .  "
			+ " ?subOrganization rdfs:label ?subOrganizationLabel . "
			+ " ?subOrganization rdf:type ?subOrgType . "
			+ " ?subOrgType rdfs:label ?subOrganizationTypeLabel . ";
		
		QueryRunner<ResultSet> subOrganizationsWithTypesQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									organizationModel);
		
		Entity entityWithSubOrganizations = getEntityWithSubOrganizations(subjectEntityURI, 
												   subOrganizationsWithTypesQuery.getQueryResult());
		
		Entity entityWithParentOrganizations = getAllParentOrganizations(dataset, subjectEntityURI);
		
		entityWithSubOrganizations.addParents(entityWithParentOrganizations.getParents());
		
		return entityWithSubOrganizations;
	}
	
	public static Entity getSubjectPersonEntity(Dataset dataset,
			String subjectEntityURI) throws MalformedQueryParametersException {
		
		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("authorLabel", QueryFieldLabels.AUTHOR_LABEL);
		
		String whereClause = ""
			+ " <" + subjectEntityURI + "> rdfs:label ?authorLabel . ";
		
		QueryRunner<ResultSet> personQuery = 
			new GenericQueryRunner(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									dataset);
		
		Entity personEntity = new Entity(subjectEntityURI);
		
		ResultSet queryResult = personQuery.getQueryResult();
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
				
			RDFNode personLabelNode = solution.get(QueryFieldLabels.AUTHOR_LABEL);
			if (personLabelNode != null) {
				personEntity.setEntityLabel(personLabelNode.toString());
			}
			
		}
		
		/*
		 * We are adding A person as it's own subentity in order to make our code for geenrating csv, json 
		 * & other data as streamlined as possible between entities of type Organization & Person. 
		 * */
		SubEntity subEntity = new SubEntity(subjectEntityURI, personEntity.getEntityLabel());
		subEntity.setEntityClass(VOConstants.EntityClassType.PERSON);
		
		personEntity.addSubEntity(subEntity);
		
//		Entity entityWithParentOrganizations = getAllParentOrganizations(dataset, subjectEntityURI);
//		
//		personEntity.addParents(entityWithParentOrganizations.getParents());
		
		return personEntity;
	}
	
	public static Entity getAllParentOrganizations(Dataset dataset,
			String subjectEntityURI) throws MalformedQueryParametersException {
		Model organizationModel = ModelConstructorUtilities
										.getOrConstructModel(
												null, 
												SubOrganizationWithinModelConstructor.MODEL_TYPE, 
												dataset);
		
		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("organizationLabel", QueryFieldLabels.ORGANIZATION_LABEL);
		fieldLabelToOutputFieldLabel.put("parentOrganization", QueryFieldLabels.PARENT_ORGANIZATION_URL);
		fieldLabelToOutputFieldLabel.put("parentOrganizationLabel", QueryFieldLabels.PARENT_ORGANIZATION_LABEL);
		
		String whereClause = ""
			+ " <" + subjectEntityURI + "> rdfs:label ?organizationLabel . "
			+ " <" + subjectEntityURI + "> <http://purl.obolibrary.org/obo/BFO_0000050> ?parentOrganization . "
            + " ?parentOrganization rdf:type foaf:Organization .  "
			+ " ?parentOrganization rdfs:label ?parentOrganizationLabel . ";
		
		QueryRunner<ResultSet> parentOrganizationsQuery = 
			new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
									"",
									whereClause,
									"",
									organizationModel);
		
		return getEntityWithParentOrganizations(subjectEntityURI, 
												   parentOrganizationsQuery.getQueryResult());
	}
	
	private static Entity getEntityWithParentOrganizations(String subjectEntityURI, ResultSet queryResult) {

		Entity entity = new Entity(subjectEntityURI);
		Map<String, Individual> parentOrganizationURIToVO = new HashMap<String, Individual>();
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(entity.getEntityLabel())) {
				
				RDFNode organizationLabelNode = solution.get(QueryFieldLabels.ORGANIZATION_LABEL);
				if (organizationLabelNode != null) {
					entity.setIndividualLabel(organizationLabelNode.toString());
				}
			}
			
			RDFNode parentOrganizationNode = solution.get(QueryFieldLabels.PARENT_ORGANIZATION_URL);
			
			Individual parent;
			
			if (!parentOrganizationURIToVO.containsKey(parentOrganizationNode.toString())) {
				
				parent = new Individual(parentOrganizationNode.toString());
				
				parentOrganizationURIToVO.put(parentOrganizationNode.toString(), parent);
				
				RDFNode parentOrganizationLabelNode = solution.get(QueryFieldLabels.PARENT_ORGANIZATION_LABEL);
				if (parentOrganizationLabelNode != null) {
					parent.setIndividualLabel(parentOrganizationLabelNode.toString());
				}
			}
		}
		
		entity.addParents(parentOrganizationURIToVO.values());
		
		return entity;
	}
	
	private static Entity getEntityWithSubOrganizations(String subjectEntityURI, ResultSet queryResult) {

		Entity entity = new Entity(subjectEntityURI);
		Map<String, SubEntity> subOrganizationURIToVO = new HashMap<String, SubEntity>();
		
		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(entity.getEntityLabel())) {
				
				RDFNode organizationLabelNode = solution.get(QueryFieldLabels.ORGANIZATION_LABEL);
				if (organizationLabelNode != null) {
					
					entity.setEntityLabel(organizationLabelNode.toString());
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
			
//			System.out.println("getting publications for " + subOrganization.getIndividualLabel());
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("document", QueryFieldLabels.DOCUMENT_URL);
			fieldLabelToOutputFieldLabel.put("documentLabel", QueryFieldLabels.DOCUMENT_LABEL);
			fieldLabelToOutputFieldLabel.put("documentPublicationDate", QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			
			String whereClause = ""
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:hasPersonWithPublication ?document . "
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
			
			getPublicationForEntity(subOrganizationPublicationsQuery.getQueryResult(),
									subOrganization,
									allDocumentURIToVOs);
			
			String lastCachedAtForEntity = getLastCachedAtDateTimeForEntityInModel(
					subOrganization, 
					subOrganizationPublicationsModel);

			subOrganization.setLastCachedAtDateTime(lastCachedAtForEntity);
			
		}
		return allDocumentURIToVOs;
	}
	
	public static Map<String, Activity> getPublicationsWithJournalForAllSubOrganizations(
			Dataset dataset, Entity organizationEntity)
			throws MalformedQueryParametersException {
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		for (SubEntity subOrganization : organizationEntity.getSubEntities()) {
			
			Model subOrganizationPublicationsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	subOrganization.getIndividualURI(),
																	OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE,
																	dataset);
			
//			System.out.println("getting publications for " + subOrganization.getIndividualLabel());
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("document", QueryFieldLabels.DOCUMENT_URL);
			fieldLabelToOutputFieldLabel.put("documentLabel", QueryFieldLabels.DOCUMENT_LABEL);
			fieldLabelToOutputFieldLabel.put("documentPublicationDate", QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			fieldLabelToOutputFieldLabel.put("journalLabel", QueryFieldLabels.DOCUMENT_JOURNAL_LABEL);
			
			String whereClause = ""
				+ " <" + subOrganization.getIndividualURI() + "> vivosocnet:hasPersonWithPublication ?document . "
				+ " ?document rdfs:label ?documentLabel . "
				+ " OPTIONAL { "
				+ " 	?document core:dateTimeValue ?dateTimeValue . "
				+ "     ?dateTimeValue core:dateTime ?documentPublicationDate } . "
				+ " OPTIONAL { "
				+ " 	?document core:hasPublicationVenue ?journal . "
				+ "     ?journal rdfs:label ?journalLabel . } ";
			
			QueryRunner<ResultSet> subOrganizationPublicationsQuery = 
				new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
										"",
										whereClause,
										"",
										subOrganizationPublicationsModel);
			
			getPublicationWithJournalForEntity(subOrganizationPublicationsQuery.getQueryResult(),
									subOrganization,
									allDocumentURIToVOs);
			
			String lastCachedAtForEntity = getLastCachedAtDateTimeForEntityInModel(
					subOrganization, 
					subOrganizationPublicationsModel);

			subOrganization.setLastCachedAtDateTime(lastCachedAtForEntity);
			
		}
		return allDocumentURIToVOs;
	}
	
	private static void getPublicationForEntity(
			ResultSet queryResult,
			SubEntity subEntity, 
			Map<String, Activity> allDocumentURIToVOs) {
		
		Set<Activity> currentEntityPublications = new HashSet<Activity>();

		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(subEntity.getLastCachedAtDateTime())) {
				
				RDFNode lastCachedAtNode = solution.get(QueryFieldLabels.LAST_CACHED_AT_DATETIME);
				if (lastCachedAtNode != null) {
					subEntity.setLastCachedAtDateTime(lastCachedAtNode.toString());
				}
			}
			
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
		subEntity.addActivities(currentEntityPublications);
	}
	
	private static void getPublicationWithJournalForEntity(
			ResultSet queryResult,
			SubEntity subEntity, 
			Map<String, Activity> allDocumentURIToVOs) {
		
		Set<Activity> currentEntityPublications = new HashSet<Activity>();

		while (queryResult.hasNext()) {
			
			QuerySolution solution = queryResult.nextSolution();
			
			if (StringUtils.isEmpty(subEntity.getLastCachedAtDateTime())) {
				
				RDFNode lastCachedAtNode = solution.get(QueryFieldLabels.LAST_CACHED_AT_DATETIME);
				if (lastCachedAtNode != null) {
					subEntity.setLastCachedAtDateTime(lastCachedAtNode.toString());
				}
			}
			
			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			Activity biboDocument;
			
			if (allDocumentURIToVOs.containsKey(documentNode.toString())) {
				biboDocument = allDocumentURIToVOs.get(documentNode.toString());

			} else {

				biboDocument = new MapOfScienceActivity(documentNode.toString());
				allDocumentURIToVOs.put(documentNode.toString(), biboDocument);

				RDFNode publicationDateNode = solution.get(QueryFieldLabels
																.DOCUMENT_PUBLICATION_DATE);
				if (publicationDateNode != null) {
					biboDocument.setActivityDate(publicationDateNode.toString());
				}
				
				RDFNode publicationJournalNode = solution.get(QueryFieldLabels
						.DOCUMENT_JOURNAL_LABEL);
				
				if (publicationJournalNode != null) {
					((MapOfScienceActivity) biboDocument).setPublishedInJournal(publicationJournalNode.toString());
				}
			}
			currentEntityPublications.add(biboDocument);
			
		}
		subEntity.addActivities(currentEntityPublications);
	}
	
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
	
	public static Map<String, Activity> getGrantsForAllSubOrganizations(
			Dataset dataset, Entity organizationEntity)
			throws MalformedQueryParametersException {
		Map<String, Activity> allGrantURIToVO = new HashMap<String, Activity>();
		
		for (SubEntity subOrganization : organizationEntity.getSubEntities()) {
			
//			System.out.println("constructing grants for " + subOrganization.getIndividualLabel() + " :: " + subOrganization.getIndividualURI());
			
			long before = System.currentTimeMillis();
			
			Model subOrganizationGrantsModel = ModelConstructorUtilities
															.getOrConstructModel(
																	subOrganization.getIndividualURI(),
																	OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE,
																	dataset);
			
//			System.out.println("\t construct -> " + (System.currentTimeMillis() - before));
			
			before = System.currentTimeMillis();
			
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
			
			/*
			 * This method side-affects the subOrganization entity & the map containing all the grants for 
			 * the subject organization.
			 * */
			getGrantForEntity(subOrganizationGrantsQuery.getQueryResult(), subOrganization, allGrantURIToVO);
			
			String lastCachedAtForEntity = getLastCachedAtDateTimeForEntityInModel(
													subOrganization, 
													subOrganizationGrantsModel);
			
			subOrganization.setLastCachedAtDateTime(lastCachedAtForEntity);
			
//			System.out.println("\t select -> " + (System.currentTimeMillis() - before));
		}
		return allGrantURIToVO;
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
	
	public static Map<String, Activity> getGrantsForAssociatedPeople(
			Dataset dataset, Collection<SubEntity> people)
			throws MalformedQueryParametersException {
		Map<String, Activity> allGrantURIToVOs = new HashMap<String, Activity>();

		Model peopleGrantsModel = ModelConstructorUtilities
										.getOrConstructModel(
												null,
												PeopleToGrantsModelConstructor.MODEL_TYPE,
												dataset);

		for (SubEntity person : people) {
			updateGrantsForPerson(person, allGrantURIToVOs, peopleGrantsModel);
		}
		return allGrantURIToVOs;
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
			Dataset dataset, SubEntity person, boolean doCache)
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
													dataset);	
		} else {
			
			ModelConstructor model = new PersonToGrantsModelConstructor(person.getIndividualURI(), dataset);
			personGrantsModel = model.getConstructedModel();
		}
		
		
		updateGrantsForPerson(person, allGrantURIToVOs, personGrantsModel);
			
			
		return allGrantURIToVOs;
	}
	
	
	public static Map<String, Activity> getPublicationsForPerson(
			Dataset dataset, SubEntity person, boolean doCache)
			throws MalformedQueryParametersException {
		
		Map<String, Activity> allPublicationURIToVOs = new HashMap<String, Activity>();
		
		Model personPublicationsModel = null;
		

		/*
		 * If we dont want to cache the results then create the model directly without
		 * using the ModelConstructorUtilities. Use case is the co-author ego-centric 
		 * visualization. 
		 * */
		if (doCache) {
			personPublicationsModel = ModelConstructorUtilities
											.getOrConstructModel(
													person.getIndividualURI(),
													PersonToPublicationsModelConstructor.MODEL_TYPE,
													dataset);	
		} else {
			
			ModelConstructor model = new PersonToPublicationsModelConstructor(person.getIndividualURI(), dataset);
			personPublicationsModel = model.getConstructedModel();
		}
		
		
		updatePublicationsForPerson(person, allPublicationURIToVOs, personPublicationsModel);
			
		return allPublicationURIToVOs;
	}
	
	public static Map<String, Activity> getPublicationsForAssociatedPeople(
			Dataset dataset, Collection<SubEntity> people)
			throws MalformedQueryParametersException {
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		Model peoplePublicationsModel = ModelConstructorUtilities
											.getOrConstructModel(
													null,
													PeopleToPublicationsModelConstructor.MODEL_TYPE,
													dataset);
		
		for (SubEntity person : people) {
			
			updatePublicationsForPerson(person, allDocumentURIToVOs,
					peoplePublicationsModel);
			
		}
		return allDocumentURIToVOs;
	}

	/**
	 * This method side-effects the person and the central documents map.
	 * @param person
	 * @param allDocumentURIToVOs
	 * @param peoplePublicationsModel
	 * @throws MalformedQueryParametersException
	 */
	private static void updatePublicationsForPerson(SubEntity person,
			Map<String, Activity> allDocumentURIToVOs,
			Model peoplePublicationsModel)
			throws MalformedQueryParametersException {
		
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
									peoplePublicationsModel);
		
		getPublicationForEntity(personPublicationsQuery.getQueryResult(),
								person,
								allDocumentURIToVOs);
		
		String lastCachedAtForEntity = getLastCachedAtDateTimeForEntityInModel(
				person, 
				peoplePublicationsModel);

		person.setLastCachedAtDateTime(lastCachedAtForEntity);
	}
	
	public static Map<String, Activity> getPublicationsWithJournalForAssociatedPeople(
			Dataset dataset, Collection<SubEntity> people)
			throws MalformedQueryParametersException {
		Map<String, Activity> allDocumentURIToVOs = new HashMap<String, Activity>();
		
		Model peoplePublicationsModel = ModelConstructorUtilities
											.getOrConstructModel(
													null,
													PeopleToPublicationsModelConstructor.MODEL_TYPE,
													dataset);
		
		for (SubEntity person : people) {
			
//			System.out.println("getting publications for " + person.getIndividualLabel());
			
			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("document", QueryFieldLabels.DOCUMENT_URL);
			fieldLabelToOutputFieldLabel.put("documentLabel", QueryFieldLabels.DOCUMENT_LABEL);
			fieldLabelToOutputFieldLabel.put("documentPublicationDate", QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			fieldLabelToOutputFieldLabel.put("journalLabel", QueryFieldLabels.DOCUMENT_JOURNAL_LABEL);
			
			String whereClause = ""
				+ " <" + person.getIndividualURI() + "> vivosocnet:hasPublication ?document . "
				+ " ?document rdfs:label ?documentLabel . "
				+ " OPTIONAL { "
				+ " 	?document core:dateTimeValue ?dateTimeValue . "
				+ "     ?dateTimeValue core:dateTime ?documentPublicationDate } . "
				+ " OPTIONAL { "
				+ " 	?document core:hasPublicationVenue ?journal . "
				+ "     ?journal rdfs:label ?journalLabel . } ";
			
			QueryRunner<ResultSet> personPublicationsQuery = 
				new GenericQueryRunnerOnModel(fieldLabelToOutputFieldLabel,
										"",
										whereClause,
										"",
										peoplePublicationsModel);
			
			getPublicationWithJournalForEntity(personPublicationsQuery.getQueryResult(),
									person,
									allDocumentURIToVOs);
			
			String lastCachedAtForEntity = getLastCachedAtDateTimeForEntityInModel(
					person, 
					peoplePublicationsModel);

			person.setLastCachedAtDateTime(lastCachedAtForEntity);
			
		}
		return allDocumentURIToVOs;
	}

}
