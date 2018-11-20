/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import org.apache.jena.ontology.OntModel;

/**
 * Does the requested action involve a change to an Info Content Entity that the
 * self-editor is authorized to modify?
 */
public class InfoContentEntityChecker extends RelationshipChecker {
	private static final String URI_INFO_CONTENT_TYPE = NS_OBO + "IAO_0000030";
	private static final String URI_FEATURES_PROPERTY = NS_CORE + "features";
	private static final String URI_AUTHORSHIP_TYPE = NS_CORE + "Authorship";
	private static final String URI_EDITORSHIP_TYPE = NS_CORE + "Editorship";

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
	public boolean isRelated(OntModel ontModel, List<String> fromUris, List<String> toUris) {
		for (String resourceUri : fromUris) {
			if (isInfoContentEntity(ontModel, resourceUri)) {
				if (anyUrisInCommon(toUris, getUrisOfAuthors(ontModel, resourceUri))) {
					return true;
				}
				if (anyUrisInCommon(toUris, getUrisOfEditors(ontModel, resourceUri))) {
					return true;
				}
				if (anyUrisInCommon(toUris, getUrisOfFeatured(ontModel, resourceUri))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isInfoContentEntity(OntModel ontModel, String resourceUri) {
		return isResourceOfType(ontModel, resourceUri, URI_INFO_CONTENT_TYPE);
	}

	private List<String> getUrisOfEditors(OntModel ontModel, String resourceUri) {
		List<String> allRelatedUris = getObjectsThroughLinkingNode(ontModel, resourceUri,
				URI_RELATED_BY, URI_EDITORSHIP_TYPE, URI_RELATES);
		// The editorship relates to the editors and to the resource itself.
		allRelatedUris.remove(resourceUri);
		return allRelatedUris;
	}

	private List<String> getUrisOfFeatured(OntModel ontModel, String resourceUri) {
		return getObjectsOfProperty(ontModel, resourceUri, URI_FEATURES_PROPERTY);
	}

	private List<String> getUrisOfAuthors(OntModel ontModel, String resourceUri) {
		List<String> allRelatedUris = getObjectsThroughLinkingNode(ontModel, resourceUri,
				URI_RELATED_BY, URI_AUTHORSHIP_TYPE, URI_RELATES);
		// The authorship relates to the authors and to the resource itself. 
		allRelatedUris.remove(resourceUri);
		return allRelatedUris;
	}
}
