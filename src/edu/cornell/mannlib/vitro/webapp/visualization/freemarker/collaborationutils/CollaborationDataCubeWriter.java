/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.collaborationutils;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.output.StringBuilderWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Collaborator;

public class CollaborationDataCubeWriter {
	
	private StringBuilder collaboratorDataCube;

	public CollaborationDataCubeWriter(CollaborationData visVOContainer) {
		collaboratorDataCube = createCollaborationDataCubeContent(visVOContainer);
	}

	private StringBuilder createCollaborationDataCubeContent(
			CollaborationData collaboratorData) {
		
		StringBuilder dataCubeContent = new StringBuilder();
		
		/*
		 * Used to generate graph content. It will contain both the nodes & edge information.
		 * We are side-effecting "graphMLContent".
		 * */
		generateGraphContent(collaboratorData, dataCubeContent);
		
		return dataCubeContent;
	}
	
	public StringBuilder getCollaboratorDataCubeContent() {
		return collaboratorDataCube;
	}

	private void generateGraphContent(CollaborationData collaboratorData,
			StringBuilder dataCubeContent) {
		
		Model collaboratorModel = ModelFactory.createDefaultModel();

		collaboratorModel.setNsPrefixes(QueryConstants.getPrefixToNameSpace());
		collaboratorModel.setNsPrefix("qb", "http://purl.org/linked-data/cube#");
		collaboratorModel.setNsPrefix("know", "http://xcite.hackerceo.org/vocab/histograms#");
		collaboratorModel.setNsPrefix("xcite", "http://xcite.hackerceo.org/instance/"
												+ UUID.randomUUID().toString() + "#");

		Property rdfType = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("rdf") + "type");
		Property rdfsLabel = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("rdfs") + "label");
		Resource dimensionProperty = collaboratorModel.createResource(collaboratorModel.getNsPrefixURI("qb") + "DimensionProperty");
		
		Resource knowCollaborator = collaboratorModel
										.createProperty(collaboratorModel.getNsPrefixURI("know") + "collaborator");
		
		knowCollaborator.addProperty(
				rdfType, 
				dimensionProperty);
		
		Resource knowTally = collaboratorModel
								.createProperty(collaboratorModel.getNsPrefixURI("know") + "tally");
		knowTally.addProperty(
				rdfType, 
				dimensionProperty);	
		
		Resource knowBehindTally = collaboratorModel
										.createProperty(collaboratorModel.getNsPrefixURI("know") + "behind-tally");
		
		knowBehindTally.addProperty(
				rdfType, 
				dimensionProperty);
		
		Resource knowInvariant = collaboratorModel
										.createProperty(collaboratorModel.getNsPrefixURI("know") + "invariant");
		
		knowInvariant.addProperty(
				rdfType, 
				dimensionProperty);

		if (collaboratorData.getCollaborators() != null 
					&& collaboratorData.getCollaborators().size() > 0) {
			generateNodeSectionContent(
					collaboratorData,
					collaboratorModel,
					rdfType,
					rdfsLabel,
					knowCollaborator,
					knowTally,
					knowBehindTally,
					knowInvariant
					);
		}
		
		Writer newWrite = new StringBuilderWriter();
		
		collaboratorModel.write(newWrite);
		
//		System.out.println(newWrite);
		
		dataCubeContent.append(newWrite.toString());
	}

	private void generateNodeSectionContent(
						CollaborationData coAuthorshipData, 
						Model collaboratorModel, 
						Property rdfType, 
						Property rdfsLabel, 
						Resource knowCollaborator, 
						Resource knowTally, 
						Resource knowBehindTally, 
						Resource knowInvariant) {
		
		Collaborator egoNode = coAuthorshipData.getEgoCollaborator();
		Set<Collaborator> authorNodes = coAuthorshipData.getCollaborators();
		
		List<Collaborator> orderedAuthorNodes = new ArrayList<Collaborator>(authorNodes);
		orderedAuthorNodes.remove(egoNode);
		
		Collections.sort(orderedAuthorNodes, new CollaboratorComparator());
		
		
		for (Collaborator currNode : orderedAuthorNodes) {
			
				getNodeContent(
						egoNode,
						currNode, 
						collaboratorModel, 
						rdfType, 
						rdfsLabel, 
						knowCollaborator, 
						knowTally,
						knowBehindTally,
						knowInvariant);
				
		}
		
	}

	private void getNodeContent(Collaborator egoNode, 
								Collaborator node, 
								Model collaboratorModel, 
								Property rdfType, 
								Property rdfsLabel, 
								Resource knowCollaborator, 
								Resource knowTally, 
								Resource knowBehindTally, 
								Resource knowInvariant) {

		ParamMap individualProfileURLParams = 
					new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
								 node.getCollaboratorURI());

		String profileURL = UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX,
			individualProfileURLParams);
		
		Resource foafPerson = collaboratorModel
										.createResource(
												collaboratorModel
													.getNsPrefixURI("foaf") + "Person");
		
		Resource collaborator = collaboratorModel.createResource(node.getCollaboratorURI());
		collaborator.addProperty(rdfType, foafPerson);
		collaborator.addProperty(rdfsLabel, node.getCollaboratorName());
		
		Resource qbObservation = collaboratorModel.createResource(collaboratorModel.getNsPrefixURI("qb") + "Observation");
		
		Resource observation = collaboratorModel.createResource(collaboratorModel.getNsPrefixURI("xcite") + node.getCollaboratorID());
		
//		if (profileURL != null) {
//			graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:profileUrl " + profileURL + " .\n");
//		}
		
		observation.addProperty(
				rdfType, 
				qbObservation);
		
		observation.addProperty(
				(Property) knowCollaborator, 
				collaborator);
		
		observation.addProperty(
				(Property) knowTally, 
				String.valueOf(node.getNumOfActivities()));
		
		Resource egoCollaborator = collaboratorModel.createResource(egoNode.getCollaboratorURI());
		
		observation.addProperty( 
				(Property) knowInvariant, 
				egoCollaborator);
		
		for (Activity activity : node.getCollaboratorActivities()) {
			
			Resource publication = collaboratorModel.createResource(activity.getActivityURI());
//			publication.addProperty(rdfsLabel, activity.getActivityLabel());
			
			observation.addProperty(
					(Property) knowBehindTally, 
					publication);
			
		}
		
//		if (node.getEarliestActivityYearCount() != null) {
//			
//			/*
//			 * There is no clean way of getting the map contents in java even though
//			 * we are sure to have only one entry on the map. So using the for loop.
//			 * I am feeling dirty just about now. 
//			 * */
//			for (Map.Entry<String, Integer> publicationInfo 
//						: node.getEarliestActivityYearCount().entrySet()) {
//				
//				graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:earliestPublication " + publicationInfo.getKey() + " . \n");
//
//				graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:numberOfEarliestPublication " + publicationInfo.getValue() + " . \n");
//				
//			}
//			
//		}
//		
//		if (node.getLatestActivityYearCount() != null) {
//			
//			for (Map.Entry<String, Integer> publicationInfo 
//						: node.getLatestActivityYearCount().entrySet()) {
//				
//				graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:latestPublication " + publicationInfo.getKey() + " . \n");
//
//				graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:numberOfLatestPublication " + publicationInfo.getValue() + " . \n");
//				
//			}
//			
//		}
//		
//		if (node.getUnknownActivityYearCount() != null) {
//			
//			graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:numberOfLatestPublication " + node.getUnknownActivityYearCount() + " . \n");
//				
//		}
		
	}
}
