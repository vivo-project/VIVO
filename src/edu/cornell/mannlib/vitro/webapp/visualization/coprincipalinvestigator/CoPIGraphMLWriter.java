/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationComparator;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaboratorComparator;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;
/**
 * @author bkoniden
 * Deepak Konidena
 */
public class CoPIGraphMLWriter {
	
	private StringBuilder coPIGraphMLContent;
	
	private final String GRAPHML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
		+ "	<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
		+ "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
        + "  xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n"
        + "  http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n\n";
	
	private final String GRAPHML_FOOTER = "</graphml>";
	
	public CoPIGraphMLWriter(CollaborationData coPIData) {
		coPIGraphMLContent = createCoPIGraphMLContent(coPIData);
	}

	private StringBuilder createCoPIGraphMLContent(CollaborationData coPIData) {
		
		StringBuilder graphMLContent = new StringBuilder();
		
		graphMLContent.append(GRAPHML_HEADER);
		
		/*
		 * We are side-effecting "graphMLContent" object in this method since creating 
		 * another String object to hold key definition data will be redundant & will
		 * not serve the purpose.
		 * */
		generateKeyDefinitionContent(coPIData, graphMLContent);
		
		/*
		 * Used to generate graph content. It will contain both the nodes & edge information.
		 * We are side-effecting "graphMLContent".
		 * */
		generateGraphContent(coPIData, graphMLContent);
		
		graphMLContent.append(GRAPHML_FOOTER);
		
		return graphMLContent;
	}
	
	public StringBuilder getCoPIGraphMLContent(){
		return coPIGraphMLContent;
	}

	private void generateGraphContent(CollaborationData coPIData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("\n<graph edgedefault=\"undirected\">\n");
		
		if (coPIData.getCollaborators() != null & coPIData.getCollaborators().size() > 0) {
			generateNodeSectionContent(coPIData, graphMLContent);
		}
		
		if (coPIData.getCollaborations() != null & coPIData.getCollaborations().size() > 0) {
			generateEdgeSectionContent(coPIData, graphMLContent);
		}
		
		graphMLContent.append("</graph>\n");
	}

	private void generateEdgeSectionContent(CollaborationData coPIData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("<!-- edges -->\n");
		
		Set<Collaboration> edges = coPIData.getCollaborations();
		
		List<Collaboration> orderedEdges = new ArrayList<Collaboration>(edges);
		
		Collections.sort(orderedEdges, new CollaborationComparator());

		for (Collaboration currentEdge : orderedEdges) {
			
			/*
			 * This method actually creates the XML code for a single edge. "graphMLContent"
			 * is being side-effected. 
			 * */
			getEdgeContent(graphMLContent, currentEdge);
		}
	}
	
	private void getEdgeContent(StringBuilder graphMLContent, Collaboration currentEdge) {
		
		graphMLContent.append("<edge " 
									+ "id=\"" + currentEdge.getCollaborationID() + "\" " 
									+ "source=\"" + currentEdge.getSourceCollaborator()
														.getCollaboratorID() + "\" "
									+ "target=\"" + currentEdge.getTargetCollaborator()
														.getCollaboratorID() + "\" "
									+ ">\n");
		
		graphMLContent.append("\t<data key=\"collaborator1\">" 
								+ currentEdge.getSourceCollaborator().getCollaboratorName() 
								+ "</data>\n");
		
		graphMLContent.append("\t<data key=\"collaborator2\">" 
								+ currentEdge.getTargetCollaborator().getCollaboratorName() 
								+ "</data>\n");
		
		graphMLContent.append("\t<data key=\"number_of_coinvestigated_grants\">" 
								+ currentEdge.getNumOfCollaborations()
							+ "</data>\n");
		
		if (currentEdge.getEarliestCollaborationYearCount() != null) {
			
			/*
			 * There is no clean way of getting the map contents in java even though
			 * we are sure to have only one entry on the map. So using the for loop.
			 * */
			for (Map.Entry<String, Integer> publicationInfo
						: currentEdge.getEarliestCollaborationYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"earliest_collaboration\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_earliest_collaboration\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (currentEdge.getLatestCollaborationYearCount() != null) {
			
			for (Map.Entry<String, Integer> publicationInfo 
						: currentEdge.getLatestCollaborationYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"latest_collaboration\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_latest_collaboration\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (currentEdge.getUnknownCollaborationYearCount() != null) {
			
				graphMLContent.append("\t<data key=\"num_unknown_collaboration\">" 
											+ currentEdge.getUnknownCollaborationYearCount() 
										+ "</data>\n");
				
		}
		
		graphMLContent.append("</edge>\n");
	}

	
	private void generateNodeSectionContent(CollaborationData coPIData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("<!-- nodes -->\n");
		
		Collaborator egoNode = coPIData.getEgoCollaborator();
		Set<Collaborator> piNodes = coPIData.getCollaborators();
		
		/*
		 * This method actually creates the XML code for a single node. "graphMLContent"
		 * is being side-effected. The egoNode is added first because this is the "requirement"
		 * of the co-pi vis. Ego should always come first.
		 * 
		 * */
		getNodeContent(graphMLContent, egoNode);
		
		List<Collaborator> orderedPINodes = new ArrayList<Collaborator>(piNodes);
		orderedPINodes.remove(egoNode);
		
		Collections.sort(orderedPINodes, new CollaboratorComparator());
		
		
		for (Collaborator currNode : orderedPINodes) {
			
			/*
			 * We have already printed the Ego Node info.
			 * */
			if (currNode != egoNode) {
				
				getNodeContent(graphMLContent, currNode);
				
			}
			
		}
		
	}

	private void getNodeContent(StringBuilder graphMLContent, Collaborator node) {
		
		ParamMap individualProfileURLParams = new ParamMap(
					VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
					node.getCollaboratorURI());

		String profileURL = UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX,
		individualProfileURLParams);
		
		graphMLContent.append("<node id=\"" + node.getCollaboratorID() + "\">\n");
		graphMLContent.append("\t<data key=\"url\">" + node.getCollaboratorURI() + "</data>\n");
		graphMLContent.append("\t<data key=\"label\">" + node.getCollaboratorName() + "</data>\n");
		
		if (profileURL != null) {
			graphMLContent.append("\t<data key=\"profile_url\">" + profileURL + "</data>\n");
		}
		
		
		graphMLContent.append("\t<data key=\"number_of_investigated_grants\">" 
								+ node.getNumOfActivities() 
							+ "</data>\n");
		
		if (node.getEarliestActivityYearCount() != null) {
			
			/*
			 * There is no clean way of getting the map contents in java even though
			 * we are sure to have only one entry on the map. So using the for loop.
			 * I am feeling dirty just about now. 
			 * */
			for (Map.Entry<String, Integer> publicationInfo 
						: node.getEarliestActivityYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"earliest_grant\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_earliest_grant\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (node.getLatestActivityYearCount() != null) {
			
			for (Map.Entry<String, Integer> publicationInfo 
						: node.getLatestActivityYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"latest_grant\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_latest_grant\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (node.getUnknownActivityYearCount() != null) {
			
				graphMLContent.append("\t<data key=\"num_unknown_grant\">" 
											+ node.getUnknownActivityYearCount() 
										+ "</data>\n");
				
		}
		
		graphMLContent.append("</node>\n");
	}

	private void generateKeyDefinitionContent(CollaborationData coPIData,
			StringBuilder graphMLContent) {
		/*
		 * Generate the key definition content for node. 
		 * */
		getKeyDefinitionFromSchema(coPIData.getNodeSchema(), graphMLContent);
		
		/*
		 * Generate the key definition content for edge. 
		 * */
		getKeyDefinitionFromSchema(coPIData.getEdgeSchema(), graphMLContent);
	}
	
	private void getKeyDefinitionFromSchema(Set<Map<String, String>> schema,
			StringBuilder graphMLContent) {
		
		for (Map<String, String> currentNodeSchemaAttribute : schema) {
			
			graphMLContent.append("\n<key ");
			
			for (Map.Entry<String, String> currentAttributeKey 
						: currentNodeSchemaAttribute.entrySet()) {
				
				graphMLContent.append(currentAttributeKey.getKey() 
										+ "=\"" + currentAttributeKey.getValue() 
										+ "\" ");

			}
			
			if (currentNodeSchemaAttribute.containsKey("default")) {
				
				graphMLContent.append(">\n");
				graphMLContent.append("<default>");
				graphMLContent.append(currentNodeSchemaAttribute.get("default"));
				graphMLContent.append("</default>\n");
				graphMLContent.append("</key>\n");
				
			} else {
				graphMLContent.append("/>\n");
			}
		}
	}

}
