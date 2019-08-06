/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.io.StringWriter;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author bkoniden
 * Deepak Konidena
 */
public class CoPIGraphMLWriter {

	private StringBuilder coPIGraphMLContent;

	private final String GRAPHML_NS = "http://graphml.graphdrawing.org/xmlns";

	public CoPIGraphMLWriter(CollaborationData coPIData) {
		coPIGraphMLContent = createCoPIGraphMLContent(coPIData);
	}

	private StringBuilder createCoPIGraphMLContent(CollaborationData coPIData) {
		StringBuilder graphMLContent = new StringBuilder();

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			doc.setXmlVersion("1.0");
			Element rootElement = doc.createElementNS(GRAPHML_NS, "graphml");
			doc.appendChild(rootElement);

			/*
			 * We are side-effecting "graphMLContent" object in this method since creating
			 * another String object to hold key definition data will be redundant & will
			 * not serve the purpose.
			 * */
			generateKeyDefinitionContent(coPIData, rootElement);

			/*
			 * Used to generate graph content. It will contain both the nodes & edge information.
			 * We are side-effecting "graphMLContent".
			 * */
			generateGraphContent(coPIData, rootElement);

			DOMSource source = new DOMSource(doc);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);

			graphMLContent.append(writer.toString());
		} catch (ParserConfigurationException | TransformerException e) {
			throw new IllegalStateException("XML error generating GraphML", e);
		}

        return graphMLContent;
	}

	public StringBuilder getCoPIGraphMLContent(){
		return coPIGraphMLContent;
	}

	private void generateGraphContent(CollaborationData coPIData, Element rootElement) {
		Document doc = rootElement.getOwnerDocument();

		Element graph = doc.createElementNS(GRAPHML_NS, "graph");
		graph.setAttribute("edgedefault", "undirected");
		rootElement.appendChild(graph);

		if (coPIData.getCollaborators() != null & coPIData.getCollaborators().size() > 0) {
			generateNodeSectionContent(coPIData, graph);
		}

		if (coPIData.getCollaborations() != null & coPIData.getCollaborations().size() > 0) {
			generateEdgeSectionContent(coPIData, graph);
		}
	}

	private void generateEdgeSectionContent(CollaborationData coPIData, Element graphElement) {
		Document doc = graphElement.getOwnerDocument();

		graphElement.appendChild(doc.createComment("edges"));

		Set<Collaboration> edges = coPIData.getCollaborations();
		List<Collaboration> orderedEdges = new ArrayList<Collaboration>(edges);
		orderedEdges.sort(new CollaborationComparator());

		for (Collaboration currentEdge : orderedEdges) {
			/*
			 * This method actually creates the XML code for a single edge. "graphMLContent"
			 * is being side-effected.
			 * */
			getEdgeContent(graphElement, currentEdge);
		}
	}

	private void getEdgeContent(Element graphElement, Collaboration currentEdge) {
		Document doc = graphElement.getOwnerDocument();

		Element edge = doc.createElementNS(GRAPHML_NS, "edge");
		edge.setAttribute("id", String.valueOf(currentEdge.getCollaborationID()));
		edge.setAttribute("source", String.valueOf(currentEdge.getSourceCollaborator().getCollaboratorID()));
		edge.setAttribute("target", String.valueOf(currentEdge.getTargetCollaborator().getCollaboratorID()));
		graphElement.appendChild(edge);

		Element collaborator1 = doc.createElementNS(GRAPHML_NS, "data");
		collaborator1.setAttribute("key", "collaborator1");
		collaborator1.setTextContent(currentEdge.getSourceCollaborator().getCollaboratorName());
		edge.appendChild(collaborator1);

		Element collaborator2 = doc.createElementNS(GRAPHML_NS, "data");
		collaborator2.setAttribute("key", "collaborator2");
		collaborator2.setTextContent(currentEdge.getTargetCollaborator().getCollaboratorName());
		edge.appendChild(collaborator2);

		Element works = doc.createElementNS(GRAPHML_NS, "data");
		works.setAttribute("key", "number_of_coinvestigated_grants");
		works.setTextContent(String.valueOf(currentEdge.getNumOfCollaborations()));
		edge.appendChild(works);

		if (currentEdge.getEarliestCollaborationYearCount() != null) {
			/*
			 * There is no clean way of getting the map contents in java even though
			 * we are sure to have only one entry on the map. So using the for loop.
			 * */
			for (Map.Entry<String, Integer> publicationInfo	: currentEdge.getEarliestCollaborationYearCount().entrySet()) {

				Element earliest = doc.createElementNS(GRAPHML_NS, "data");
				earliest.setAttribute("key", "earliest_collaboration");
				earliest.setTextContent(publicationInfo.getKey());
				edge.appendChild(earliest);

				Element earliestCount = doc.createElementNS(GRAPHML_NS, "data");
				earliestCount.setAttribute("key", "num_earliest_collaboration");
				earliestCount.setTextContent(publicationInfo.getValue().toString());
				edge.appendChild(earliestCount);
			}

		}

		if (currentEdge.getLatestCollaborationYearCount() != null) {
			for (Map.Entry<String, Integer> publicationInfo : currentEdge.getLatestCollaborationYearCount().entrySet()) {
				Element latest = doc.createElementNS(GRAPHML_NS, "data");
				latest.setAttribute("key", "latest_collaboration");
				latest.setTextContent(publicationInfo.getKey());
				edge.appendChild(latest);

				Element latestCount = doc.createElementNS(GRAPHML_NS, "data");
				latestCount.setAttribute("key", "num_latest_collaboration");
				latestCount.setTextContent(publicationInfo.getValue().toString());
				edge.appendChild(latestCount);
			}

		}

		if (currentEdge.getUnknownCollaborationYearCount() != null) {
			Element unknown = doc.createElementNS(GRAPHML_NS, "data");
			unknown.setAttribute("key", "num_unknown_collaboration");
			unknown.setTextContent(String.valueOf(currentEdge.getUnknownCollaborationYearCount()));
			edge.appendChild(unknown);
		}
	}

	private void generateNodeSectionContent(CollaborationData coPIData, Element graphElement) {
		Document doc = graphElement.getOwnerDocument();

		graphElement.appendChild(doc.createComment("nodes"));

		Collaborator egoNode = coPIData.getEgoCollaborator();
		Set<Collaborator> piNodes = coPIData.getCollaborators();

		/*
		 * This method actually creates the XML code for a single node. "graphMLContent"
		 * is being side-effected. The egoNode is added first because this is the "requirement"
		 * of the co-pi vis. Ego should always come first.
		 *
		 * */
		getNodeContent(graphElement, egoNode);

		List<Collaborator> orderedPINodes = new ArrayList<Collaborator>(piNodes);
		orderedPINodes.remove(egoNode);

		orderedPINodes.sort(new CollaboratorComparator());


		for (Collaborator currNode : orderedPINodes) {
			/*
			 * We have already printed the Ego Node info.
			 * */
			if (currNode != egoNode) {
				getNodeContent(graphElement, currNode);
			}
		}
	}

	private void getNodeContent(Element graphElement, Collaborator collaborator) {
		Document doc = graphElement.getOwnerDocument();

		ParamMap individualProfileURLParams = new ParamMap(
					VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,	collaborator.getCollaboratorURI());

		String profileURL = UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX, individualProfileURLParams);

		Element node = doc.createElementNS(GRAPHML_NS, "node");
		node.setAttribute("id", String.valueOf(collaborator.getCollaboratorID()));
		graphElement.appendChild(node);

		Element url = doc.createElementNS(GRAPHML_NS, "data");
		url.setAttribute("key", "url");
		url.setTextContent(collaborator.getCollaboratorURI());
		node.appendChild(url);

		Element label = doc.createElementNS(GRAPHML_NS, "data");
		label.setAttribute("key", "label");
		label.setTextContent(collaborator.getCollaboratorName());
		node.appendChild(label);

		if (profileURL != null) {
			Element profile = doc.createElementNS(GRAPHML_NS, "data");
			profile.setAttribute("key", "profile_url");
			profile.setTextContent(profileURL);
			node.appendChild(profile);
		}

		Element works = doc.createElementNS(GRAPHML_NS, "data");
		works.setAttribute("key", "number_of_investigated_grants");
		works.setTextContent(String.valueOf(collaborator.getNumOfActivities()));
		node.appendChild(works);

		if (collaborator.getEarliestActivityYearCount() != null) {
			/*
			 * There is no clean way of getting the map contents in java even though
			 * we are sure to have only one entry on the map. So using the for loop.
			 * I am feeling dirty just about now.
			 * */
			for (Map.Entry<String, Integer> publicationInfo : collaborator.getEarliestActivityYearCount().entrySet()) {
				Element earliest = doc.createElementNS(GRAPHML_NS, "data");
				earliest.setAttribute("key", "earliest_grant");
				earliest.setTextContent(publicationInfo.getKey());
				node.appendChild(earliest);

				Element earliestCount = doc.createElementNS(GRAPHML_NS, "data");
				earliestCount.setAttribute("key", "num_earliest_grant");
				earliestCount.setTextContent(publicationInfo.getValue().toString());
				node.appendChild(earliestCount);
			}
		}

		if (collaborator.getLatestActivityYearCount() != null) {
			for (Map.Entry<String, Integer> publicationInfo : collaborator.getLatestActivityYearCount().entrySet()) {
				Element latest = doc.createElementNS(GRAPHML_NS, "data");
				latest.setAttribute("key", "latest_grant");
				latest.setTextContent(publicationInfo.getKey());
				node.appendChild(latest);

				Element latestCount = doc.createElementNS(GRAPHML_NS, "data");
				latestCount.setAttribute("key", "num_latest_grant");
				latestCount.setTextContent(publicationInfo.getValue().toString());
				node.appendChild(latestCount);
			}
		}

		if (collaborator.getUnknownActivityYearCount() != null) {
			Element unknown = doc.createElementNS(GRAPHML_NS, "data");
			unknown.setAttribute("key", "num_unknown_grant");
			unknown.setTextContent(String.valueOf(collaborator.getUnknownActivityYearCount()));
			node.appendChild(unknown);
		}
	}

	private void generateKeyDefinitionContent(CollaborationData coPIData, Element rootElement) {
		/*
		 * Generate the key definition content for node.
		 * */
		getKeyDefinitionFromSchema(coPIData.getNodeSchema(), rootElement);

		/*
		 * Generate the key definition content for edge.
		 * */
		getKeyDefinitionFromSchema(coPIData.getEdgeSchema(), rootElement);
	}

	private void getKeyDefinitionFromSchema(Set<Map<String, String>> schema, Element rootElement) {
		Document doc = rootElement.getOwnerDocument();

		for (Map<String, String> currentNodeSchemaAttribute : schema) {
			Element key = doc.createElementNS(GRAPHML_NS, "key");

			for (Map.Entry<String, String> currentAttributeKey : currentNodeSchemaAttribute.entrySet()) {
				key.setAttribute(currentAttributeKey.getKey(), currentAttributeKey.getValue());
			}

			if (currentNodeSchemaAttribute.containsKey("default")) {
				Element def = doc.createElementNS(GRAPHML_NS, "default");
				def.setTextContent(currentNodeSchemaAttribute.get("default"));
				key.appendChild(def);
			}

			rootElement.appendChild(key);
		}
	}
}
