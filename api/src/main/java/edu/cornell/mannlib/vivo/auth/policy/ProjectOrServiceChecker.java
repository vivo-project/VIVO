/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;
import org.apache.jena.ontology.OntModel;

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

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to a Project or a Service, and if the
	 * self-editor:
	 *
	 * 1) is a Clinical Agent of that Project or Service
	 */
	public boolean isRelated(OntModel ontModel, List<String> fromUris, List<String> toUris) {
		for (String resourceUri : fromUris) {
			if (isProject(ontModel, resourceUri)) {
				if (anyUrisInCommon(ontModel, toUris,
						getClinicalAgentsOfProject(ontModel, resourceUri))) {
					return true;
				}
			}
			if (isService(ontModel, resourceUri)) {
				if (anyUrisInCommon(ontModel, toUris,
						getClinicalAgentsOfService(ontModel, resourceUri))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isProject(OntModel ontModel, String resourceUri) {
		return isResourceOfType(ontModel, resourceUri, URI_PROJECT_TYPE);
	}

	private boolean isService(OntModel ontModel, String resourceUri) {
		return isResourceOfType(ontModel, resourceUri, URI_SERVICE_TYPE);
	}

	private List<String> getClinicalAgentsOfProject(OntModel ontModel, String resourceUri) {
		return getObjectsThroughLinkingNode(ontModel, resourceUri, URI_REALIZES,
				URI_CLINICAL_ROLE_TYPE, URI_INHERES_IN);
	}

	private List<String> getClinicalAgentsOfService(OntModel ontModel, String resourceUri) {
		return getObjectsThroughLinkingNode(ontModel, resourceUri,
				URI_CONTRIBUTING_ROLE_PROPERTY, URI_CLINICAL_ROLE_TYPE,
				URI_INHERES_IN);
	}
}
