/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CoPIData {
	
	private Set<CoPINode> nodes;
	private Set<CoPIEdge> edges;
	private CoPINode egoNode;
	private Set<Map<String, String>> NODE_SCHEMA;
	private Set<Map<String, String>> EDGE_SCHEMA;
		
	public CoPIData(CoPINode egoNode, Set<CoPINode> nodes, Set<CoPIEdge> edges) {
		this.egoNode = egoNode;
		this.nodes = nodes;
		this.edges = edges;
	}
	
	public Set<CoPINode> getNodes() {
		return nodes;
	}

	public Set<CoPIEdge> getEdges() {
		return edges;
	}	
	
	public CoPINode getEgoNode() {
		return egoNode;
	}
	
	/*
	 * Node Schema for graphML
	 * */
	public Set<Map<String, String>> getNodeSchema() {
		
		if (NODE_SCHEMA == null) {
			NODE_SCHEMA = initializeNodeSchema();			
		}
		
		return NODE_SCHEMA;
	}
	
	/*
	 * Edge Schema for graphML
	 * */
	public Set<Map<String, String>> getEdgeSchema() {
		
		if (EDGE_SCHEMA == null) {
			EDGE_SCHEMA = initializeEdgeSchema();			
		}
		
		return EDGE_SCHEMA;
	}
	
	public void print(){
		
		System.out.println("\n-----------------------------");
		
		System.out.println("Ego node is "+ this.getEgoNode().getNodeName());
		
		System.out.println("\nNodes are: ");
		
		for(CoPINode node : this.getNodes()){
			System.out.println(node.getNodeName());
		}
		
		System.out.println("\nEdges are: ");
		
		for(CoPIEdge edge : this.getEdges()){
			System.out.println(edge.getSourceNode() + "-->" + edge.getTargetNode());
		}
		
		System.out.println("\n-----------------------------");
		
	}
	
	private Set<Map<String, String>> initializeEdgeSchema() {

		Set<Map<String, String>> edgeSchema = new HashSet<Map<String, String>>();
		
			Map<String, String> schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "collaborator1");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "collaborator1");
			schemaAttributes.put("attr.type", "string");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "collaborator2");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "collaborator2");
			schemaAttributes.put("attr.type", "string");
		
		edgeSchema.add(schemaAttributes);		
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "number_of_coinvestigated_grants");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "number_of_coinvestigated_grants");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "earliest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "earliest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_earliest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "num_earliest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "latest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "latest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_latest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "num_latest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_unknown_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "num_unknown_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
		return edgeSchema;
	}
	

	private Set<Map<String, String>> initializeNodeSchema() {
		
		Set<Map<String, String>> nodeSchema = new HashSet<Map<String, String>>();

			Map<String, String> schemaAttributes = new LinkedHashMap<String, String>();   
			
			schemaAttributes.put("id", "url");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "url");
			schemaAttributes.put("attr.type", "string");
		
		nodeSchema.add(schemaAttributes);
	
			schemaAttributes = new LinkedHashMap<String, String>();
		
			schemaAttributes.put("id", "label");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "label");
			schemaAttributes.put("attr.type", "string");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
		
			schemaAttributes.put("id", "profile_url");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "profile_url");
			schemaAttributes.put("attr.type", "string");
		
		nodeSchema.add(schemaAttributes);
	
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "number_of_investigated_grants");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "number_of_investigated_grants");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "earliest_grant");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "earliest_grant");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_earliest_grant");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_earliest_grant");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "latest_grant");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "latest_grant");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_latest_grant");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_latest_grant");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_unknown_grant");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_unknown_grant");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
		
		return nodeSchema;
	}
	
}
