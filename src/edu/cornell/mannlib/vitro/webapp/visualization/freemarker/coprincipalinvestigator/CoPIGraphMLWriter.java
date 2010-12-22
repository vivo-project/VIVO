/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coprincipalinvestigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoPIData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoPIEdge;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoPINode;
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
	
	public CoPIGraphMLWriter(CoPIData coPIData){
		coPIGraphMLContent = createCoPIGraphMLContent(coPIData);
	}

	private StringBuilder createCoPIGraphMLContent(CoPIData coPIData) {
		
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

	private void generateGraphContent(CoPIData coPIData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("\n<graph edgedefault=\"undirected\">\n");
		
		if (coPIData.getNodes() != null & coPIData.getNodes().size() > 0) {
			generateNodeSectionContent(coPIData, graphMLContent);
		}
		
		if (coPIData.getEdges() != null & coPIData.getEdges().size() > 0) {
			generateEdgeSectionContent(coPIData, graphMLContent);
		}
		
		graphMLContent.append("</graph>\n");
	}

	private void generateEdgeSectionContent(CoPIData coPIData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("<!-- edges -->\n");
		
		Set<CoPIEdge> edges = coPIData.getEdges();
		
		List<CoPIEdge> orderedEdges = new ArrayList<CoPIEdge>(edges);
		
		Collections.sort(orderedEdges, new CoPIEdgeComparator());

		for (CoPIEdge currentEdge : orderedEdges) {
			
			/*
			 * This method actually creates the XML code for a single edge. "graphMLContent"
			 * is being side-effected. 
			 * */
			getEdgeContent(graphMLContent, currentEdge);
		}
	}
	
	private void getEdgeContent(StringBuilder graphMLContent, CoPIEdge currentEdge) {
		
		graphMLContent.append("<edge " 
									+ "id=\"" + currentEdge.getEdgeID() + "\" " 
									+ "source=\"" + currentEdge.getSourceNode().getNodeID() + "\" "
									+ "target=\"" + currentEdge.getTargetNode().getNodeID() + "\" "
									+ ">\n");
		
		graphMLContent.append("\t<data key=\"collaborator1\">" 
								+ currentEdge.getSourceNode().getNodeName() 
								+ "</data>\n");
		
		graphMLContent.append("\t<data key=\"collaborator2\">" 
								+ currentEdge.getTargetNode().getNodeName() 
								+ "</data>\n");
		
		graphMLContent.append("\t<data key=\"number_of_coinvestigated_grants\">" 
								+ currentEdge.getNumberOfCoInvestigatedGrants()
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

	
	private void generateNodeSectionContent(CoPIData coPIData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("<!-- nodes -->\n");
		
		CoPINode egoNode = coPIData.getEgoNode();
		Set<CoPINode> piNodes = coPIData.getNodes();
		
		/*
		 * This method actually creates the XML code for a single node. "graphMLContent"
		 * is being side-effected. The egoNode is added first because this is the "requirement"
		 * of the co-pi vis. Ego should always come first.
		 * 
		 * */
		getNodeContent(graphMLContent, egoNode);
		
		List<CoPINode> orderedPINodes = new ArrayList<CoPINode>(piNodes);
		orderedPINodes.remove(egoNode);
		
		Collections.sort(orderedPINodes, new CoPINodeComparator());
		
		
		for (CoPINode currNode : orderedPINodes) {
			
			/*
			 * We have already printed the Ego Node info.
			 * */
			if (currNode != egoNode) {
				
				getNodeContent(graphMLContent, currNode);
				
			}
			
		}
		
	}

	private void getNodeContent(StringBuilder graphMLContent, CoPINode node) {
		
		ParamMap individualProfileURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
				   node.getNodeURI());

		String profileURL = UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX,
		individualProfileURLParams);
		
		graphMLContent.append("<node id=\"" + node.getNodeID() + "\">\n");
		graphMLContent.append("\t<data key=\"url\">" + node.getNodeURI() + "</data>\n");
		graphMLContent.append("\t<data key=\"label\">" + node.getNodeName() + "</data>\n");
		
		if (profileURL != null) {
			graphMLContent.append("\t<data key=\"profile_url\">" + profileURL + "</data>\n");
		}
		
		
		graphMLContent.append("\t<data key=\"number_of_investigated_grants\">" 
								+ node.getNumberOfInvestigatedGrants() 
							+ "</data>\n");
		
		if (node.getEarliestGrantYearCount() != null) {
			
			/*
			 * There is no clean way of getting the map contents in java even though
			 * we are sure to have only one entry on the map. So using the for loop.
			 * I am feeling dirty just about now. 
			 * */
			for (Map.Entry<String, Integer> publicationInfo 
						: node.getEarliestGrantYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"earliest_grant\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_earliest_grant\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (node.getLatestGrantYearCount() != null) {
			
			for (Map.Entry<String, Integer> publicationInfo 
						: node.getLatestGrantYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"latest_grant\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_latest_grant\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (node.getUnknownGrantYearCount() != null) {
			
				graphMLContent.append("\t<data key=\"num_unknown_grant\">" 
											+ node.getUnknownGrantYearCount() 
										+ "</data>\n");
				
		}
		
		graphMLContent.append("</node>\n");
	}

	private void generateKeyDefinitionContent(CoPIData coPIData,
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
