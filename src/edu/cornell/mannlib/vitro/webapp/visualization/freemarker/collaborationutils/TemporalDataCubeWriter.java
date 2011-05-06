/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.collaborationutils;

import java.io.Writer;
import java.util.UUID;

import org.apache.commons.io.output.StringBuilderWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SubEntity;

public class TemporalDataCubeWriter {
	
	private StringBuilder collaboratorDataCube;

	public TemporalDataCubeWriter(String requestURL, Entity entity) {
		collaboratorDataCube = createTemporalDataCubeContent(requestURL, entity);
	}

	public StringBuilder getDataCubeContent() {
		return collaboratorDataCube;
	}

	private StringBuilder createTemporalDataCubeContent(
			String requestURL, Entity subjectEntity) {
		
		StringBuilder dataCubeContent = new StringBuilder();
		
		Model temporalModel = ModelFactory.createDefaultModel();

		temporalModel.setNsPrefixes(QueryConstants.getPrefixToNameSpace());
		temporalModel.setNsPrefix("qb", "http://purl.org/linked-data/cube#");
		temporalModel.setNsPrefix("know", "http://xcite.hackerceo.org/vocab/histograms#");
		temporalModel.setNsPrefix("xcite", "http://xcite.hackerceo.org/instance/"
												+ UUID.randomUUID().toString() + "#");

		Property xciteRequestURL = temporalModel.createProperty(temporalModel.getNsPrefixURI("xcite") + "requestURL");
		Property rdfType = temporalModel.createProperty(temporalModel.getNsPrefixURI("rdf") + "type");
		Property rdfsLabel = temporalModel.createProperty(temporalModel.getNsPrefixURI("rdfs") + "label");
		Resource dimensionProperty = temporalModel.createResource(temporalModel.getNsPrefixURI("qb") + "DimensionProperty");
		Resource qbDataset = temporalModel.createProperty(temporalModel.getNsPrefixURI("qb") + "DataSet");
		
		
		Resource xciteDataset = temporalModel.createProperty(temporalModel.getNsPrefixURI("know") + "dataset");
		xciteDataset.addProperty(rdfType, qbDataset);
		xciteDataset.addProperty(rdfsLabel, "Temporal Vis DataCube for " + subjectEntity.getEntityLabel());
		xciteDataset.addProperty(xciteRequestURL, requestURL);
		
		Resource knowCollaborator = temporalModel
										.createProperty(temporalModel.getNsPrefixURI("know") + "collaborator");
		
		knowCollaborator.addProperty(
				rdfType, 
				dimensionProperty);
		
		Resource knowTally = temporalModel
								.createProperty(temporalModel.getNsPrefixURI("know") + "tally");
		knowTally.addProperty(
				rdfType, 
				dimensionProperty);	
		
		Resource knowBehindTally = temporalModel
										.createProperty(temporalModel.getNsPrefixURI("know") + "behind-tally");
		
		knowBehindTally.addProperty(
				rdfType, 
				dimensionProperty);
		
		Resource knowInvariant = temporalModel
										.createProperty(temporalModel.getNsPrefixURI("know") + "invariant");
		
		knowInvariant.addProperty(
				rdfType, 
				dimensionProperty);

		if (subjectEntity.getSubEntities() != null 
					&& subjectEntity.getSubEntities().size() > 0) {
			
			getSubEntitiesCubeContent(
					subjectEntity,
					temporalModel,
					rdfType,
					rdfsLabel,
					knowCollaborator,
					knowTally,
					knowBehindTally,
					knowInvariant,
					qbDataset,
					xciteDataset
					);
		}
		
		Writer newWrite = new StringBuilderWriter();
		
		temporalModel.write(newWrite);
		
//		System.out.println(newWrite);
		
		dataCubeContent.append(newWrite.toString());
		
		return dataCubeContent;
		
	}

	private void getSubEntitiesCubeContent(
						Entity entity, 
						Model temporalModel, 
						Property rdfType, 
						Property rdfsLabel, 
						Resource knowCollaborator, 
						Resource knowTally, 
						Resource knowBehindTally, 
						Resource knowInvariant,
						Resource qbDataSet,
						Resource xciteDataset) {
		
		Resource subjectEntity = temporalModel.createResource(
						entity.getEntityURI()); 
		subjectEntity.addProperty(rdfsLabel, entity.getEntityLabel());
		
//		temporalModel.createResource();

		Resource foafPerson = temporalModel
			.createResource(
					temporalModel
						.getNsPrefixURI("foaf") + "Person");
		
		Resource foafOrganization = temporalModel
			.createResource(
					temporalModel
						.getNsPrefixURI("foaf") + "Organization");
		
		Resource subEntityType;
		
		int subEntityindex = 1;
		
		for (SubEntity currSubEntity : entity.getSubEntities()) {
			
			if (VOConstants.EntityClassType.PERSON.equals(currSubEntity.getEntityClass())) {
				subEntityType = foafPerson;
			} else {
				subEntityType = foafOrganization;
			}
			
			createSubEntityCubeContent(
					subEntityindex,
					subjectEntity,
					currSubEntity,
					subEntityType,
					temporalModel, 
					rdfType, 
					rdfsLabel, 
					knowCollaborator, 
					knowTally,
					knowBehindTally,
					knowInvariant,
					qbDataSet,
					xciteDataset);
				
			subEntityindex++;
		}
		
	}

	private void createSubEntityCubeContent(int subEntityindex, 
								Resource egoNode, 
								SubEntity node, 
								Resource subEntityType, 
								Model collaboratorModel, 
								Property rdfType, 
								Property rdfsLabel, 
								Resource knowCollaborator, 
								Resource knowTally, 
								Resource knowBehindTally, 
								Resource knowInvariant,
								Resource qbDataSet,
								Resource xciteDataset) {

//		ParamMap individualProfileURLParams = 
//					new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
//								 node.getIndividualURI());
//		
//		String profileURL = UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX,
//			individualProfileURLParams);
		
//		if (profileURL != null) {
//		graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:profileUrl " + profileURL + " .\n");
//	}
		
		Resource collaborator = collaboratorModel.createResource(node.getIndividualURI());
		collaborator.addProperty(rdfType, subEntityType);
		collaborator.addProperty(rdfsLabel, node.getIndividualLabel());
		
		Resource qbObservation = collaboratorModel.createResource(collaboratorModel.getNsPrefixURI("qb") + "Observation");
		
		Resource observation = collaboratorModel
					.createResource(collaboratorModel.getNsPrefixURI("xcite") 
										+ subEntityindex);
		
		observation.addProperty(
				rdfType, 
				qbObservation);
		
		observation.addProperty(
				(Property) knowCollaborator, 
				collaborator);
		
		observation.addProperty( 
				(Property) knowInvariant, 
				egoNode);
		
		observation.addProperty( 
				(Property) qbDataSet, 
				xciteDataset);
		
		observation.addProperty(
				(Property) knowTally, 
				String.valueOf(node.getActivities().size()));
		
		for (Activity activity : node.getActivities()) {
			
			Resource publication = collaboratorModel.createResource(activity.getActivityURI());
//			publication.addProperty(rdfsLabel, activity.getActivityLabel());
			
			observation.addProperty(
					(Property) knowBehindTally, 
					publication);
		}
		
	}
}
