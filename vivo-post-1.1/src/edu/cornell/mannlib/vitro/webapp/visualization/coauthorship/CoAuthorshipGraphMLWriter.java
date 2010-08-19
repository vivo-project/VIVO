/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Edge;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;

public class CoAuthorshipGraphMLWriter {
	
	private StringBuilder coAuthorshipGraphMLContent;

	private final String GRAPHML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
			+ "	<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
			+ "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
	        + "  xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n"
	        + "  http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n\n";

	private final String GRAPHML_FOOTER = "</graphml>";
	
	public CoAuthorshipGraphMLWriter(CoAuthorshipData visVOContainer) {
		
		coAuthorshipGraphMLContent = createCoAuthorshipGraphMLContent(visVOContainer);
		
	}

	public StringBuilder getCoAuthorshipGraphMLContent() {
		return coAuthorshipGraphMLContent;
	}

	private StringBuilder createCoAuthorshipGraphMLContent(
			CoAuthorshipData coAuthorshipData) {
		
		StringBuilder graphMLContent = new StringBuilder();
		
		graphMLContent.append(GRAPHML_HEADER);
		
		/*
		 * We are side-effecting "graphMLContent" object in this method since creating 
		 * another String object to hold key definition data will be redundant & will
		 * not serve the purpose.
		 * */
		generateKeyDefinitionContent(coAuthorshipData, graphMLContent);
		
		/*
		 * Used to generate graph content. It will contain both the nodes & edge information.
		 * We are side-effecting "graphMLContent".
		 * */
		generateGraphContent(coAuthorshipData, graphMLContent);
		
		graphMLContent.append(GRAPHML_FOOTER);
		
		return graphMLContent;
	}

	private void generateGraphContent(CoAuthorshipData coAuthorshipData,
			StringBuilder graphMLContent) {

		graphMLContent.append("\n<graph edgedefault=\"undirected\">\n");
		
		if (coAuthorshipData.getNodes() != null & coAuthorshipData.getNodes().size() > 0) {
			generateNodeSectionContent(coAuthorshipData, graphMLContent);
		}
		
		if (coAuthorshipData.getEdges() != null & coAuthorshipData.getEdges().size() > 0) {
			generateEdgeSectionContent(coAuthorshipData, graphMLContent);
		}
		
		graphMLContent.append("</graph>\n");
		  
		
		
		
	}

	private void generateEdgeSectionContent(CoAuthorshipData coAuthorshipData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("<!-- edges -->\n");
		
		Set<Edge> edges = coAuthorshipData.getEdges();
		
		List<Edge> orderedEdges = new ArrayList<Edge>(edges);
		
		Collections.sort(orderedEdges, new EdgeComparator());

		for (Edge currentEdge : orderedEdges) {
			
			/*
			 * This method actually creates the XML code for a single edge. "graphMLContent"
			 * is being side-effected. 
			 * */
			getEdgeContent(graphMLContent, currentEdge);
			
		}
		
	}

	private void getEdgeContent(StringBuilder graphMLContent, Edge currentEdge) {
		
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
		
		graphMLContent.append("\t<data key=\"number_of_coauthored_works\">" 
								+ currentEdge.getNumOfCoAuthoredWorks()
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

	private void generateNodeSectionContent(CoAuthorshipData coAuthorshipData,
			StringBuilder graphMLContent) {
		
		graphMLContent.append("<!-- nodes -->\n");
		
		Node egoNode = coAuthorshipData.getEgoNode();
		Set<Node> authorNodes = coAuthorshipData.getNodes();
		
		/*
		 * This method actually creates the XML code for a single node. "graphMLContent"
		 * is being side-effected. The egoNode is added first because this is the "requirement"
		 * of the co-author vis. Ego should always come first.
		 * 
		 * */
		getNodeContent(graphMLContent, egoNode);
		
		List<Node> orderedAuthorNodes = new ArrayList<Node>(authorNodes);
		orderedAuthorNodes.remove(egoNode);
		
		Collections.sort(orderedAuthorNodes, new NodeComparator());
		
		
		for (Node currNode : orderedAuthorNodes) {
			
			/*
			 * We have already printed the Ego Node info.
			 * */
			if (currNode != egoNode) {
				
				getNodeContent(graphMLContent, currNode);
				
			}
			
		}
		
	}

	private void getNodeContent(StringBuilder graphMLContent, Node node) {
		
		String profileURL = null;
		try {
			profileURL = VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX + "?" 
								+ VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY 
								+ "=" + URLEncoder.encode(node.getNodeURI(),
														  VisualizationController
														  		.URL_ENCODING_SCHEME).toString();
		} catch (UnsupportedEncodingException e) {
			System.err.println("URL Encoding ERRor. Move this to use log.error ASAP");
		}
		
		graphMLContent.append("<node id=\"" + node.getNodeID() + "\">\n");
		graphMLContent.append("\t<data key=\"url\">" + node.getNodeURI() + "</data>\n");
		graphMLContent.append("\t<data key=\"label\">" + node.getNodeName() + "</data>\n");
		
		if (profileURL != null) {
			graphMLContent.append("\t<data key=\"profile_url\">" + profileURL + "</data>\n");
		}
		
		
		graphMLContent.append("\t<data key=\"number_of_authored_works\">" 
								+ node.getNumOfAuthoredWorks() 
							+ "</data>\n");
		
		if (node.getEarliestPublicationYearCount() != null) {
			
			/*
			 * There is no clean way of getting the map contents in java even though
			 * we are sure to have only one entry on the map. So using the for loop.
			 * I am feeling dirty just about now. 
			 * */
			for (Map.Entry<String, Integer> publicationInfo 
						: node.getEarliestPublicationYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"earliest_publication\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_earliest_publication\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (node.getLatestPublicationYearCount() != null) {
			
			for (Map.Entry<String, Integer> publicationInfo 
						: node.getLatestPublicationYearCount().entrySet()) {
				
				graphMLContent.append("\t<data key=\"latest_publication\">" 
											+ publicationInfo.getKey() 
										+ "</data>\n");

				graphMLContent.append("\t<data key=\"num_latest_publication\">" 
											+ publicationInfo.getValue() 
										+ "</data>\n");
			}
			
		}
		
		if (node.getUnknownPublicationYearCount() != null) {
			
				graphMLContent.append("\t<data key=\"num_unknown_publication\">" 
											+ node.getUnknownPublicationYearCount() 
										+ "</data>\n");
				
		}
		
		graphMLContent.append("</node>\n");
	}

	private void generateKeyDefinitionContent(CoAuthorshipData visVOContainer, 
											  StringBuilder graphMLContent) {
		
		/*
		 * Generate the key definition content for node. 
		 * */
		getKeyDefinitionFromSchema(visVOContainer.getNodeSchema(), graphMLContent);
		
		/*
		 * Generate the key definition content for edge. 
		 * */
		getKeyDefinitionFromSchema(visVOContainer.getEdgeSchema(), graphMLContent);
		
		
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
