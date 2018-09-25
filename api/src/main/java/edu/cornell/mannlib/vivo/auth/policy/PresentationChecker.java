/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;
import org.apache.jena.ontology.OntModel;

/**
 * Does the requested action involve a change to a Presentation that the
 * self-editor is authorized to modify?
 */
public class PresentationChecker extends RelationshipChecker {
	private static final String URI_PRESENTATION_TYPE = NS_CORE
			+ "Presentation";
	private static final String URI_PRESENTER_ROLE_TYPE = NS_CORE
			+ "PresenterRole";

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to a Presentation, and if the self-editor:
	 * 
	 * 1) is a Presenter of that Presentation
	 */
	public boolean isRelated(OntModel ontModel, List<String> fromUris, List<String> toUris) {
		for (String resourceUri : fromUris) {
			if (isPresentation(ontModel, resourceUri)) {
				if (anyUrisInCommon(ontModel, toUris, getUrisOfPresenters(ontModel, resourceUri))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isPresentation(OntModel ontModel, String resourceUri) {
		return isResourceOfType(ontModel, resourceUri, URI_PRESENTATION_TYPE);
	}

	private List<String> getUrisOfPresenters(OntModel ontModel, String resourceUri) {
		return getObjectsThroughLinkingNode(ontModel, resourceUri, URI_REALIZES,
				URI_PRESENTER_ROLE_TYPE, URI_INHERES_IN);
	}
}
