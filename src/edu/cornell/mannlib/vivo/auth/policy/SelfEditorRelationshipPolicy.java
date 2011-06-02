/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasAssociatedIndividual;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.AbstractRelationshipPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractDataPropertyAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractObjectPropertyAction;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.AbortStartup;

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
 * relationship (to be used in the decision). We could go even farther and drive
 * this from an XML config file, so site administrators could configure it
 * themselves. A great tool for this is the one used to process the Tomcat
 * server.xml file, see http://commons.apache.org/digester/
 */
public class SelfEditorRelationshipPolicy extends AbstractRelationshipPolicy
		implements PolicyIface {
	private static final Log log = LogFactory
			.getLog(SelfEditorRelationshipPolicy.class);

	private static final String NS_CORE = "http://vivoweb.org/ontology/core#";
	private static final String URI_INFORMATION_RESOURCE_TYPE = NS_CORE
			+ "InformationResource";
	private static final String URI_EDITOR_PROPERTY = "http://purl.org/ontology/bibo/editor";
	private static final String URI_FEATURES_PROPERTY = NS_CORE + "features";
	private static final String URI_IN_AUTHORSHIP_PROPERTY = NS_CORE
			+ "informationResourceInAuthorship";
	private static final String URI_LINKED_AUTHOR_PROPERTY = NS_CORE
			+ "linkedAuthor";

	private static final String URI_GRANT_TYPE = NS_CORE + "Grant";
	private static final String URI_RELATED_ROLE_PROPERTY = NS_CORE
			+ "relatedRole";
	private static final String URI_PRINCIPAL_INVESTIGATOR_OF_PROPERTY = NS_CORE
			+ "principalInvestigatorRoleOf";
	private static final String URI_CO_PRINCIPAL_INVESTIGATOR_OF_PROPERTY = NS_CORE
			+ "co-PrincipalInvestigatorRoleOf";

	private static final String URI_PROJECT_TYPE = NS_CORE + "Project";
	private static final String URI_SERVICE_TYPE = NS_CORE + "Service";
	private static final String URI_CLINICAL_ROLE_OF_PROPERTY = NS_CORE
			+ "clinicalRoleOf";

	private static final String URI_PRESENTATION_TYPE = NS_CORE
			+ "Presentation";
	private static final String URI_PRESENTER_ROLE_OF_PROPERTY = NS_CORE
			+ "presenterRoleOf";

	private static final String URI_COURSE_TYPE = NS_CORE + "Course";
	private static final String URI_TEACHER_ROLE_OF_PROPERTY = NS_CORE
			+ "teacherRoleOf";

	private static final String URI_ADVISING_RELATIONSHIP_TYPE = NS_CORE
			+ "AdvisingRelationship";
	private static final String URI_ADVISOR_PROPERTY = NS_CORE + "advisor";

	public SelfEditorRelationshipPolicy(ServletContext ctx, OntModel model) {
		super(ctx, model);
	}

	@Override
	public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
			RequestedAction whatToAuth) {
		PolicyDecision decision = null;

		if (whatToAuth == null) {
			decision = inconclusiveDecision("whatToAuth was null");
		} else if (whatToAuth instanceof AbstractDataPropertyAction) {
			decision = isAuthorized(whoToAuth,
					distill((AbstractDataPropertyAction) whatToAuth));
		} else if (whatToAuth instanceof AbstractObjectPropertyAction) {
			decision = isAuthorized(whoToAuth,
					distill((AbstractObjectPropertyAction) whatToAuth));
		} else {
			decision = inconclusiveDecision("Does not authorize "
					+ whatToAuth.getClass().getSimpleName() + " actions");
		}

		if (decision == null) {
			return userNotAuthorizedToStatement();
		} else {
			return decision;
		}
	}

	private DistilledAction distill(AbstractDataPropertyAction action) {
		return new DistilledAction(action.getPredicateUri(),
				action.getSubjectUri());
	}

	private DistilledAction distill(AbstractObjectPropertyAction action) {
		return new DistilledAction(action.uriOfPredicate, action.uriOfSubject,
				action.uriOfObject);
	}

	private PolicyDecision isAuthorized(IdentifierBundle ids,
			DistilledAction action) {
		List<String> userUris = new ArrayList<String>(
				HasAssociatedIndividual.getIndividualUris(ids));

		if (userUris.isEmpty()) {
			return inconclusiveDecision("Not self-editing.");
		}

		if (!canModifyPredicate(action.predicateUri)) {
			return cantModifyPredicate(action.predicateUri);
		}

		for (String resourceUri : action.resourceUris) {
			if (!canModifyResource(resourceUri)) {
				return cantModifyResource(resourceUri);
			}
		}

		for (String resourceUri : action.resourceUris) {
			if (isInformationResource(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfEditors(resourceUri))) {
					return authorizedEditor(resourceUri);
				}
				if (anyUrisInCommon(userUris, getUrisOfAuthors(resourceUri))) {
					return authorizedAuthor(resourceUri);
				}
				if (anyUrisInCommon(userUris, getUrisOfFeatured(resourceUri))) {
					return authorizedFeatured(resourceUri);
				}
			}
			if (isGrant(resourceUri)) {
				if (anyUrisInCommon(userUris,
						getUrisOfPrincipalInvestigators(resourceUri))) {
					return authorizedPI(resourceUri);
				}
				if (anyUrisInCommon(userUris,
						getUrisOfCoPrincipalInvestigators(resourceUri))) {
					return authorizedCoPI(resourceUri);
				}
			}
			if (isProject(resourceUri) || isService(resourceUri)) {
				if (anyUrisInCommon(userUris,
						getUrisOfClinicalAgents(resourceUri))) {
					return authorizedClinicalAgent(resourceUri);
				}
			}
			if (isPresentation(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfPresenters(resourceUri))) {
					return authorizedPresenter(resourceUri);
				}
			}
			if (isCourse(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfTeachers(resourceUri))) {
					return authorizedTeacher(resourceUri);
				}
			}
			if (isAdvisingRelationship(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfAdvisors(resourceUri))) {
					return authorizedAdvisor(resourceUri);
				}
			}
		}

		return userNotAuthorizedToStatement();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName()
				+ ": information resources, grants, projects, etc. - "
				+ hashCode();
	}

	// ----------------------------------------------------------------------
	// methods for InformationResource
	// ----------------------------------------------------------------------

	private boolean isInformationResource(String resourceUri) {
		return isResourceOfType(resourceUri, URI_INFORMATION_RESOURCE_TYPE);
	}

	private List<String> getUrisOfEditors(String resourceUri) {
		return getObjectsOfProperty(resourceUri, URI_EDITOR_PROPERTY);
	}

	private List<String> getUrisOfFeatured(String resourceUri) {
		return getObjectsOfProperty(resourceUri, URI_FEATURES_PROPERTY);
	}

	private List<String> getUrisOfAuthors(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_IN_AUTHORSHIP_PROPERTY, URI_LINKED_AUTHOR_PROPERTY);
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

	// ----------------------------------------------------------------------
	// methods for Grant
	// ----------------------------------------------------------------------

	private boolean isGrant(String resourceUri) {
		return isResourceOfType(resourceUri, URI_GRANT_TYPE);
	}

	private List<String> getUrisOfPrincipalInvestigators(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_RELATED_ROLE_PROPERTY,
				URI_PRINCIPAL_INVESTIGATOR_OF_PROPERTY);
	}

	private List<String> getUrisOfCoPrincipalInvestigators(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_RELATED_ROLE_PROPERTY,
				URI_CO_PRINCIPAL_INVESTIGATOR_OF_PROPERTY);
	}

	private PolicyDecision authorizedPI(String resourceUri) {
		return authorizedDecision("User is Principal Investigator of "
				+ resourceUri);
	}

	private PolicyDecision authorizedCoPI(String resourceUri) {
		return authorizedDecision("User is Co-Principal Investigator of "
				+ resourceUri);
	}

	// ----------------------------------------------------------------------
	// methods for Project or Service
	// ----------------------------------------------------------------------

	private boolean isProject(String resourceUri) {
		return isResourceOfType(resourceUri, URI_PROJECT_TYPE);
	}

	private boolean isService(String resourceUri) {
		return isResourceOfType(resourceUri, URI_SERVICE_TYPE);
	}

	private List<String> getUrisOfClinicalAgents(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_RELATED_ROLE_PROPERTY, URI_CLINICAL_ROLE_OF_PROPERTY);
	}

	private PolicyDecision authorizedClinicalAgent(String resourceUri) {
		return authorizedDecision("User has a Clinical Role on " + resourceUri);
	}

	// ----------------------------------------------------------------------
	// methods for Presentation
	// ----------------------------------------------------------------------

	private boolean isPresentation(String resourceUri) {
		return isResourceOfType(resourceUri, URI_PRESENTATION_TYPE);
	}

	private List<String> getUrisOfPresenters(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_RELATED_ROLE_PROPERTY, URI_PRESENTER_ROLE_OF_PROPERTY);
	}

	private PolicyDecision authorizedPresenter(String resourceUri) {
		return authorizedDecision("User is a Presenter of " + resourceUri);
	}

	// ----------------------------------------------------------------------
	// methods for Course
	// ----------------------------------------------------------------------

	private boolean isCourse(String resourceUri) {
		return isResourceOfType(resourceUri, URI_COURSE_TYPE);
	}

	private List<String> getUrisOfTeachers(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_RELATED_ROLE_PROPERTY, URI_TEACHER_ROLE_OF_PROPERTY);
	}

	private PolicyDecision authorizedTeacher(String resourceUri) {
		return authorizedDecision("User is a Teacher of " + resourceUri);
	}

	// ----------------------------------------------------------------------
	// methods for AdvisingRelationship
	// ----------------------------------------------------------------------

	private boolean isAdvisingRelationship(String resourceUri) {
		return isResourceOfType(resourceUri, URI_ADVISING_RELATIONSHIP_TYPE);
	}

	private List<String> getUrisOfAdvisors(String resourceUri) {
		return getObjectsOfProperty(resourceUri, URI_ADVISOR_PROPERTY);
	}

	private PolicyDecision authorizedAdvisor(String resourceUri) {
		return authorizedDecision("User is an Advisor of " + resourceUri);
	}

	// ----------------------------------------------------------------------
	// helper classes
	// ----------------------------------------------------------------------

	/**
	 * This allows us to treat data properties and object properties the same.
	 * It's just that object properties have more resourceUris.
	 */
	static class DistilledAction {
		final String[] resourceUris;
		final String predicateUri;

		public DistilledAction(String predicateUri, String... resourceUris) {
			this.resourceUris = resourceUris;
			this.predicateUri = predicateUri;
		}
	}

	/**
	 * When the system starts up, install the policy. This class must be a
	 * listener in web.xml
	 * 
	 * The CommonIdentifierBundleFactory already creates the IDs we need.
	 */
	public static class Setup implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			ServletContext ctx = sce.getServletContext();

			if (AbortStartup.isStartupAborted(ctx)) {
				return;
			}

			try {
				OntModel ontModel = (OntModel) sce.getServletContext()
						.getAttribute("jenaOntModel");

				ServletPolicyList.addPolicy(ctx,
						new SelfEditorRelationshipPolicy(ctx, ontModel));
			} catch (Exception e) {
				log.error("could not run " + this.getClass().getSimpleName()
						+ ": " + e);
				AbortStartup.abortStartup(ctx);
				throw new RuntimeException(e);
			}
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) { /* nothing */
		}
	}
}
