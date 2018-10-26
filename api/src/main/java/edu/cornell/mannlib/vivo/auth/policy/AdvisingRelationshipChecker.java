/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import org.apache.jena.ontology.OntModel;

/**
 * Does the requested action involve a change to an Advising Relationship that
 * the self-editor is authorized to modify?
 */
public class AdvisingRelationshipChecker extends RelationshipChecker {
	private static final String URI_ADVISING_RELATIONSHIP_TYPE = NS_CORE
			+ "AdvisingRelationship";
	private static final String URI_ADVISOR_ROLE = NS_CORE + "AdvisorRole";

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to an Advising Relationship, and if the
	 * self-editor:
	 * 
	 * 1) is an Advisor in that Relationship
	 */
	public boolean isRelated(OntModel ontModel, List<String> fromUris, List<String> toUris) {
		for (String resourceUri : fromUris) {
			if (isAdvisingRelationship(ontModel, resourceUri)) {
				if (anyUrisInCommon(ontModel, toUris, getUrisOfAdvisors(ontModel, resourceUri))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isAdvisingRelationship(OntModel ontModel, String resourceUri) {
		return isResourceOfType(ontModel, resourceUri, URI_ADVISING_RELATIONSHIP_TYPE);
	}

	private List<String> getUrisOfAdvisors(OntModel ontModel, String resourceUri) {
		return getObjectsThroughLinkingNode(ontModel, resourceUri, URI_RELATES,
				URI_ADVISOR_ROLE, URI_INHERES_IN);
	}
}
