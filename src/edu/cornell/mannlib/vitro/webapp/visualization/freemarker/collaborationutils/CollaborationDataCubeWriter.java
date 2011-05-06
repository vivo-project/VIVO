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

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Collaborator;

public class CollaborationDataCubeWriter {
	
	private StringBuilder collaboratorDataCube;
	private String defaultNameSpace;
	private String requestURL;

	public CollaborationDataCubeWriter(CollaborationData visVOContainer, String defaultNameSpace, String requestURL) {
		this.defaultNameSpace = defaultNameSpace;
		this.requestURL = requestURL;
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
		collaboratorModel.setNsPrefix("xcite", defaultNameSpace);

		Property rdfsSeeAlso = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("rdfs") + "seeAlso");
		Property rdfType = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("rdf") + "type");
		Property rdfsLabel = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("rdfs") + "label");
		Resource dimensionProperty = collaboratorModel.createResource(collaboratorModel.getNsPrefixURI("qb") + "DimensionProperty");
		Resource qbDataset = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("qb") + "DataSet");
		Property qbDataSetProperty = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("qb") + "dataSet");
		
		Resource xciteDataset = collaboratorModel.createProperty(
				collaboratorModel.getNsPrefixURI("xcite") 
				+ "dataset-" 
				+ UUID.randomUUID());
		
		xciteDataset.addProperty(rdfType, qbDataset);
		xciteDataset.addProperty(rdfsLabel, "Collaboration Vis DataCube for " + collaboratorData.getEgoCollaborator().getCollaboratorName());
		
		Resource requestURLResource = collaboratorModel.createResource(requestURL);
		
		xciteDataset.addProperty(rdfsSeeAlso, requestURLResource);
		
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
					knowInvariant,
					qbDataSetProperty,
					xciteDataset
					);
		}
		
		Writer newWrite = new StringBuilderWriter();
		
		collaboratorModel.write(newWrite);
		
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
						Resource knowInvariant, 
						Property qbDataSetProperty, 
						Resource xciteDataSet) {
		
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
						knowInvariant,
						qbDataSetProperty,
						xciteDataSet);
				
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
								Resource knowInvariant, 
								Property qbDataSetProperty, 
								Resource xciteDataSet) {

//		ParamMap individualProfileURLParams = 
//					new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
//								 node.getCollaboratorURI());
//
//		String profileURL = UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX,
//			individualProfileURLParams);
//		if (profileURL != null) {
//		graphMLContent.append("\t" + node.getCollaboratorURI() + " vivo:profileUrl " + profileURL + " .\n");
//	}
		
		Resource foafPerson = collaboratorModel
										.createResource(
												collaboratorModel
													.getNsPrefixURI("foaf") + "Person");
		
		Resource collaborator = collaboratorModel.createResource(node.getCollaboratorURI());
		collaborator.addProperty(rdfType, foafPerson);
		collaborator.addProperty(rdfsLabel, node.getCollaboratorName());
		
		Resource qbObservation = collaboratorModel.createResource(collaboratorModel.getNsPrefixURI("qb") + "Observation");
		
		Resource observation = collaboratorModel.createResource(
				xciteDataSet.getURI()
				+ "#observation-"
				+ node.getCollaboratorID());
		
		observation.addProperty(
				rdfType, 
				qbObservation);
		
		observation.addProperty(
				(Property) knowCollaborator, 
				collaborator);
		
		observation.addProperty(
				(Property) knowTally, 
				String.valueOf(node.getNumOfActivities()));
		
		observation.addProperty( 
				(Property) qbDataSetProperty, 
				xciteDataSet);
		
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
		
	}
}
