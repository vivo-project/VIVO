/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;

/**
 * Does the requested action involve a change to an Info Content Entity that the
 * self-editor is authorized to modify?
 */
public class InfoContentEntityChecker extends RelationshipChecker {
	private static final String URI_INFO_CONTENT_TYPE = NS_OBO + "IAO_0000030";
	private static final String URI_FEATURES_PROPERTY = NS_CORE + "features";
	private static final String URI_AUTHORSHIP_TYPE = NS_CORE + "Authorship";
	private static final String URI_EDITORSHIP_TYPE = NS_CORE + "Editorship";

	private final String[] resourceUris;

	public InfoContentEntityChecker(AbstractPropertyStatementAction action) {
		super(action.getOntModel());
		this.resourceUris = action.getResourceUris();
	}

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to an Info Content Entity, and if the
	 * self-editor:
	 *
	 * 1) is an Author of that Info Content Entity,
	 *
	 * 2) is an Editor of that Info Content Entity, or
	 *
	 * 3) is Featured in that Info Content Entity.
	 */
	public PolicyDecision isAuthorized(List<String> userUris) {
		for (String resourceUri : resourceUris) {
			if (isInfoContentEntity(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfAuthors(resourceUri))) {
					return authorizedAuthor(resourceUri);
				}
				if (anyUrisInCommon(userUris, getUrisOfEditors(resourceUri))) {
					return authorizedEditor(resourceUri);
				}
				if (anyUrisInCommon(userUris, getUrisOfFeatured(resourceUri))) {
					return authorizedFeatured(resourceUri);
				}
			}
		}
		return null;
	}

	private boolean isInfoContentEntity(String resourceUri) {
		return isResourceOfType(resourceUri, URI_INFO_CONTENT_TYPE);
	}

	private List<String> getUrisOfEditors(String resourceUri) {
		List<String> allRelatedUris = getObjectsThroughLinkingNode(resourceUri,
				URI_RELATED_BY, URI_EDITORSHIP_TYPE, URI_RELATES);
		// The editorship relates to the editors and to the resource itself.
		allRelatedUris.remove(resourceUri);
		return allRelatedUris;
	}

	private List<String> getUrisOfFeatured(String resourceUri) {
		return getObjectsOfProperty(resourceUri, URI_FEATURES_PROPERTY);
	}

	private List<String> getUrisOfAuthors(String resourceUri) {
		List<String> allRelatedUris = getObjectsThroughLinkingNode(resourceUri,
				URI_RELATED_BY, URI_AUTHORSHIP_TYPE, URI_RELATES);
		// The authorship relates to the authors and to the resource itself.
		allRelatedUris.remove(resourceUri);
		return allRelatedUris;
	}

	private PolicyDecision authorizedEditor(String uri) {
		return authorizedDecision("User is an editor of " + uri);
	}

	private PolicyDecision authorizedAuthor(String uri) {
		return authorizedDecision("User is author of " + uri);
	}

	private PolicyDecision authorizedFeatured(String uri) {
		return authorizedDecision("User is featured in " + uri);
	}

}
