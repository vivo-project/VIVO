/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils;

import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;

public abstract class CollaborationData {
	
	private Set<Collaborator> collaborators;
	private Set<Collaboration> collaborations;
	private Collaborator egoCollaborator;
	private Set<Map<String, String>> NODE_SCHEMA;
	private Set<Map<String, String>> EDGE_SCHEMA;
		
	public CollaborationData(Collaborator egoCollaborator, 
							Set<Collaborator> collaborators, 
							Set<Collaboration> collaborations) {
		this.egoCollaborator = egoCollaborator;
		this.collaborators = collaborators;
		this.collaborations = collaborations;
	}
	
	public Set<Collaborator> getCollaborators() {
		return collaborators;
	}

	public Set<Collaboration> getCollaborations() {
		return collaborations;
	}	
	
	public Collaborator getEgoCollaborator() {
		return egoCollaborator;
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

	abstract Set<Map<String, String>> initializeEdgeSchema();
	
	abstract Set<Map<String, String>> initializeNodeSchema();	
}
