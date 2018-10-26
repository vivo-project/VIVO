/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.auth.policy;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vitro.webapp.utils.RelationshipCheckerRegistry;

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
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			RelationshipCheckerRegistry.registerRelationshipChecker(new InfoContentEntityChecker());
			RelationshipCheckerRegistry.registerRelationshipChecker(new GrantChecker());
			RelationshipCheckerRegistry.registerRelationshipChecker(new ProjectOrServiceChecker());
			RelationshipCheckerRegistry.registerRelationshipChecker(new PresentationChecker());
			RelationshipCheckerRegistry.registerRelationshipChecker(new CourseChecker());
			RelationshipCheckerRegistry.registerRelationshipChecker(new AdvisingRelationshipChecker());
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) { /* nothing */
		}
	}
}
