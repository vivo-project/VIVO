/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vitro.webapp.utils.RelationshipChecker;

/**
 * Permit self-editors to edit the properties of classes with which they share a
 * special relationship. So for example:
 * 
 * A self-editor may edit properties of an InformationResource for which he is
 * an author, an editor, or in which he is featured.
 * 
 * A self-editor may edit properties of a Project in which he plays a clinical
 * role.
 * 
 * Etc.
 * 
 * NOTE: properties or resources which are restricted by namespace or by access
 * setting will still not be editable, even if this special relationship
 * applies.
 * 
 * NOTE: This could be further generalized by building a list of authorizing
 * relationships, where each relationship may specify a type of object, a
 * relating property (or chain of properties), and a text message describing the
 * relationship (to be used in the decision).
 */
public class SelfEditorRelationship {
	/**
	 * Helper class for initialising relationships for the self-editor functionality
	 *
	 * Note that an inner Setup class is used for consistency with other classes called from the startup_listerner file.
	 */

	/**
	 * When the system starts up, install the policy. This class must be a
	 * listener in web.xml
	 * 
	 * The CommonIdentifierBundleFactory already creates the IDs we need.
	 */
	public static class Setup implements ServletContextListener {
		protected static final String NS_CORE = "http://vivoweb.org/ontology/core#";
		protected static final String NS_OBO = "http://purl.obolibrary.org/obo/";
		protected static final String URI_RELATES = NS_CORE + "relates";
		protected static final String URI_RELATED_BY = NS_CORE + "relatedBy";
		protected static final String URI_BEARER_OF  = NS_OBO + "RO_0000053";
		protected static final String URI_REALIZED_IN = NS_OBO + "BFO_0000054";

		private static final String URI_ADVISING_RELATIONSHIP_TYPE = NS_CORE + "AdvisingRelationship";
		private static final String URI_ADVISOR_ROLE = NS_CORE + "AdvisorRole";

		private static final String URI_COURSE_TYPE = NS_CORE + "Course";
		private static final String URI_TEACHER_ROLE_TYPE = NS_CORE + "TeacherRole";

		private static final String URI_GRANT_TYPE = NS_CORE + "Grant";
		private static final String URI_PI_ROLE_TYPE = NS_CORE + "PrincipalInvestigatorRole";
		private static final String URI_CO_PI_ROLE_TYPE = NS_CORE + "CoPrincipalInvestigatorRole";

		private static final String URI_INFO_CONTENT_TYPE = NS_OBO + "IAO_0000030";
		private static final String URI_AUTHORSHIP_TYPE = NS_CORE + "Authorship";
		private static final String URI_EDITORSHIP_TYPE = NS_CORE + "Editorship";

		private static final String URI_PRESENTATION_TYPE = NS_CORE + "Presentation";
		private static final String URI_PRESENTER_ROLE_TYPE = NS_CORE + "PresenterRole";

		private static final String URI_PROJECT_TYPE = NS_CORE + "Project";
		private static final String URI_SERVICE_TYPE = NS_CORE + "Service";
		private static final String URI_CLINICAL_ROLE_TYPE = NS_CORE + "ClinicalRole";
		private static final String URI_CONTRIBUTING_ROLE_PROPERTY = NS_CORE + "contributingRole";
		private static final String URI_CONTRIBUTES_TO = NS_CORE + "contributesTo";

		@Override
		public void contextInitialized(ServletContextEvent sce) {
			RelationshipChecker.getQueryBuilder()
				// Advising Relationships
				.addRelationshipThroughContext(URI_BEARER_OF, URI_ADVISOR_ROLE, URI_RELATED_BY, URI_ADVISING_RELATIONSHIP_TYPE)
				// Course Relationships
				.addRelationshipThroughContext(URI_BEARER_OF, URI_TEACHER_ROLE_TYPE, URI_REALIZED_IN, URI_COURSE_TYPE)
				// Grant Relationships
				.addRelationshipThroughContext(URI_BEARER_OF, URI_PI_ROLE_TYPE, URI_RELATED_BY, URI_GRANT_TYPE)
				.addRelationshipThroughContext(URI_BEARER_OF, URI_CO_PI_ROLE_TYPE, URI_RELATED_BY, URI_GRANT_TYPE)
				// Information Content Relationships
				.addRelationshipThroughContext(URI_RELATED_BY, URI_AUTHORSHIP_TYPE, URI_RELATES, URI_INFO_CONTENT_TYPE)
				.addRelationshipThroughContext(URI_RELATED_BY, URI_EDITORSHIP_TYPE, URI_RELATES, URI_INFO_CONTENT_TYPE)
				// Presentation Relationships
				.addRelationshipThroughContext(URI_BEARER_OF, URI_PRESENTER_ROLE_TYPE, URI_REALIZED_IN, URI_PRESENTATION_TYPE)
				// Project Relationships
				.addRelationshipThroughContext(URI_BEARER_OF, URI_CLINICAL_ROLE_TYPE, URI_CONTRIBUTES_TO, URI_PROJECT_TYPE)
				.addRelationshipThroughContext(URI_BEARER_OF, URI_CLINICAL_ROLE_TYPE, URI_CONTRIBUTES_TO, URI_SERVICE_TYPE)
				.finish();
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) { /* nothing */
		}
	}
}
