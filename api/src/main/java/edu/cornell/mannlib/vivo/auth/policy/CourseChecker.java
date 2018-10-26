/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import org.apache.jena.ontology.OntModel;

/**
 * Does the requested action involve a change to a Course that the self-editor
 * is authorized to modify?
 */
public class CourseChecker extends RelationshipChecker {
	private static final String URI_COURSE_TYPE = NS_CORE + "Course";
	private static final String URI_TEACHER_ROLE_TYPE = NS_CORE + "TeacherRole";

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to a Course, and if the self-editor:
	 * 
	 * 1) is a Teacher of that Course
	 */
	public boolean isRelated(OntModel ontModel, List<String> fromUris, List<String> toUris) {
		for (String resourceUri : fromUris) {
			if (isCourse(ontModel, resourceUri)) {
				if (anyUrisInCommon(ontModel, toUris, getUrisOfTeachers(ontModel, resourceUri))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isCourse(OntModel ontModel, String resourceUri) {
		return isResourceOfType(ontModel, resourceUri, URI_COURSE_TYPE);
	}

	private List<String> getUrisOfTeachers(OntModel ontModel, String resourceUri) {
		return getObjectsThroughLinkingNode(ontModel, resourceUri, URI_REALIZES,
				URI_TEACHER_ROLE_TYPE, URI_INHERES_IN);
	}
}
