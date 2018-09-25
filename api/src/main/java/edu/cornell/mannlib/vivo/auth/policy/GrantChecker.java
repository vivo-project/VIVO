/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;
import org.apache.jena.ontology.OntModel;

/**
 * Does the requested action involve a change to a Grant that the self-editor is
 * authorized to modify?
 */
public class GrantChecker extends RelationshipChecker {
	private static final String URI_GRANT_TYPE = NS_CORE + "Grant";
	private static final String URI_PI_ROLE_TYPE = NS_CORE
			+ "PrincipalInvestigatorRole";
	private static final String URI_CO_PI_ROLE_TYPE = NS_CORE
			+ "CoPrincipalInvestigatorRole";

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to a Grant, and if the self-editor:
	 * 
	 * 1) is a Principal Investigator (PI) of that Grant, or
	 * 
	 * 2) is a co-Principal Investigator (co-PI) of that Grant
	 */
	public boolean isRelated(OntModel ontModel, List<String> fromUris, List<String> toUris) {
		for (String resourceUri : fromUris) {
			if (isGrant(ontModel, resourceUri)) {
				if (anyUrisInCommon(ontModel, toUris,
						getUrisOfPrincipalInvestigators(ontModel, resourceUri))) {
					return true;
				}
				if (anyUrisInCommon(ontModel, toUris,
						getUrisOfCoPrincipalInvestigators(ontModel, resourceUri))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isGrant(OntModel ontModel, String resourceUri) {
		return isResourceOfType(ontModel, resourceUri, URI_GRANT_TYPE);
	}

	private List<String> getUrisOfPrincipalInvestigators(OntModel ontModel, String resourceUri) {
		return getObjectsThroughLinkingNode(ontModel, resourceUri, URI_RELATES,
				URI_PI_ROLE_TYPE, URI_INHERES_IN);
	}

	private List<String> getUrisOfCoPrincipalInvestigators(OntModel ontModel, String resourceUri) {
		return getObjectsThroughLinkingNode(ontModel, resourceUri, URI_RELATES,
				URI_CO_PI_ROLE_TYPE, URI_INHERES_IN);
	}
}
