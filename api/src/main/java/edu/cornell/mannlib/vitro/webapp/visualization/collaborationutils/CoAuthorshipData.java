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

public class CoAuthorshipData extends CollaborationData {
	private Map<String, Activity> documentMap = new HashMap<>();

	public CoAuthorshipData(Collaborator egoCollaborator,
			Set<Collaborator> collaborators, Set<Collaboration> collaborations, Map<String, Activity> documentMap) {
		super(egoCollaborator, collaborators, collaborations);
		this.documentMap = documentMap;
	}

	public Map<String, Activity> getDocuments() {
		return documentMap;
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

			schemaAttributes.put("id", "number_of_coauthored_works");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "number_of_coauthored_works");
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

			schemaAttributes.put("id", "number_of_authored_works");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "number_of_authored_works");
			schemaAttributes.put("attr.type", "int");

		nodeSchema.add(schemaAttributes);

			schemaAttributes = new LinkedHashMap<String, String>();

			schemaAttributes.put("id", "earliest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "earliest_publication");
			schemaAttributes.put("attr.type", "int");

		nodeSchema.add(schemaAttributes);

			schemaAttributes = new LinkedHashMap<String, String>();

			schemaAttributes.put("id", "num_earliest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_earliest_publication");
			schemaAttributes.put("attr.type", "int");

		nodeSchema.add(schemaAttributes);

			schemaAttributes = new LinkedHashMap<String, String>();

			schemaAttributes.put("id", "latest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "latest_publication");
			schemaAttributes.put("attr.type", "int");

		nodeSchema.add(schemaAttributes);

			schemaAttributes = new LinkedHashMap<String, String>();

			schemaAttributes.put("id", "num_latest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_latest_publication");
			schemaAttributes.put("attr.type", "int");

		nodeSchema.add(schemaAttributes);

			schemaAttributes = new LinkedHashMap<String, String>();

			schemaAttributes.put("id", "num_unknown_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_unknown_publication");
			schemaAttributes.put("attr.type", "int");

		nodeSchema.add(schemaAttributes);


		return nodeSchema;
	}

}
