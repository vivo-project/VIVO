/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;

public class CoInvestigationData extends CollaborationData {
	private Map<String, Activity> grantMap = new HashMap<>();

	public CoInvestigationData(Collaborator egoCollaborator,
			Set<Collaborator> collaborators, Set<Collaboration> collaborations, Map<String, Activity> grantMap) {
		super(egoCollaborator, collaborators, collaborations);
		this.grantMap = grantMap;
	}

	public Map<String, Activity> getGrants() {
		return grantMap;
	}

	public Set<Map<String, String>> initializeEdgeSchema() {

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


	public Set<Map<String, String>> initializeNodeSchema() {

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
