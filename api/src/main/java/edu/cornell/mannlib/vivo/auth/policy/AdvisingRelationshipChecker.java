/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;

/**
 * Does the requested action involve a change to an Advising Relationship that
 * the self-editor is authorized to modify?
 */
public class AdvisingRelationshipChecker extends RelationshipChecker {
	private static final String URI_ADVISING_RELATIONSHIP_TYPE = NS_CORE
			+ "AdvisingRelationship";
	private static final String URI_ADVISOR_ROLE = NS_CORE + "AdvisorRole";

	private final String[] resourceUris;

	public AdvisingRelationshipChecker(AbstractPropertyStatementAction action) {
		super(action.getOntModel());
		this.resourceUris = action.getResourceUris();
	}

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to an Advising Relationship, and if the
	 * self-editor:
	 *
	 * 1) is an Advisor in that Relationship
	 */
	public PolicyDecision isAuthorized(List<String> userUris) {
		for (String resourceUri : resourceUris) {
			if (isAdvisingRelationship(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfAdvisors(resourceUri))) {
					return authorizedAdvisor(resourceUri);
				}
			}
		}
		return null;
	}

	private boolean isAdvisingRelationship(String resourceUri) {
		return isResourceOfType(resourceUri, URI_ADVISING_RELATIONSHIP_TYPE);
	}

	private List<String> getUrisOfAdvisors(String resourceUri) {
		return getObjectsThroughLinkingNode(resourceUri, URI_RELATES,
				URI_ADVISOR_ROLE, URI_INHERES_IN);
	}

	private PolicyDecision authorizedAdvisor(String resourceUri) {
		return authorizedDecision("User is an Advisor of " + resourceUri);
	}

}
