/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;

/**
 * Does the requested action involve a change to a Project or Service that the
 * self-editor is authorized to modify?
 */
public class ProjectOrServiceChecker extends RelationshipChecker {
	private static final String URI_PROJECT_TYPE = NS_CORE + "Project";
	private static final String URI_SERVICE_TYPE = NS_CORE + "Service";
	private static final String URI_CLINICAL_ROLE_TYPE = NS_CORE
			+ "ClinicalRole";
	private static final String URI_CONTRIBUTING_ROLE_PROPERTY = NS_CORE
			+ "contributingRole";

	private final String[] resourceUris;

	public ProjectOrServiceChecker(AbstractPropertyStatementAction action) {
		super(action.getOntModel());
		this.resourceUris = action.getResourceUris();
	}

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to a Project or a Service, and if the
	 * self-editor:
	 *
	 * 1) is a Clinical Agent of that Project or Service
	 */
	public PolicyDecision isAuthorized(List<String> userUris) {
		for (String resourceUri : resourceUris) {
			if (isProject(resourceUri)) {
				if (anyUrisInCommon(userUris,
						getClinicalAgentsOfProject(resourceUri))) {
					return authorizedClinicalAgent(resourceUri);
				}
			}
			if (isService(resourceUri)) {
				if (anyUrisInCommon(userUris,
						getClinicalAgentsOfService(resourceUri))) {
					return authorizedClinicalAgent(resourceUri);
				}
			}
		}
		return null;
	}

	private boolean isProject(String resourceUri) {
		return isResourceOfType(resourceUri, URI_PROJECT_TYPE);
	}

	private boolean isService(String resourceUri) {
		return isResourceOfType(resourceUri, URI_SERVICE_TYPE);
	}

	private List<String> getClinicalAgentsOfProject(String resourceUri) {
		return getObjectsThroughLinkingNode(resourceUri, URI_REALIZES,
				URI_CLINICAL_ROLE_TYPE, URI_INHERES_IN);
	}

	private List<String> getClinicalAgentsOfService(String resourceUri) {
		return getObjectsThroughLinkingNode(resourceUri,
				URI_CONTRIBUTING_ROLE_PROPERTY, URI_CLINICAL_ROLE_TYPE,
				URI_INHERES_IN);
	}

	private PolicyDecision authorizedClinicalAgent(String resourceUri) {
		return authorizedDecision("User has a Clinical Role on " + resourceUri);
	}

}
