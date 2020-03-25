/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.auth.policy;

import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.IsRootUser;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount.Status;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.dao.UserAccountsDao;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * If the user has an IsRootUser identifier, they can do anything!
 *
 * On setup, check to see that the specified root user exists. If not, create
 * it. If we can't create it, abort.
 *
 * If any other root users exist, warn about them.
 */
public class RootUserPolicy implements PolicyIface {
	private static final Log log = LogFactory.getLog(RootUserPolicy.class);

	private static final String PROPERTY_ROOT_USER_EMAIL = "rootUser.emailAddress";
	/*
	 * UQAM For parameterization of rootUser
	 */
	private static final String PROPERTY_ROOT_USER_PASSWORD = "rootUser.password";
	private static final String PROPERTY_ROOT_USER_PASSWORD_CHANGE_REQUIRED = "rootUser.passwordChangeRequired";

	private static final String ROOT_USER_INITIAL_PASSWORD = "rootPassword";
	private static final String ROOT_USER_INITIAL_PASSWORD_CHANGE_REQUIRED = "true";

	/**
	 * This is the entire policy. If you are a root user, you are authorized.
	 */
	@Override
	public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
			RequestedAction whatToAuth) {
		if (IsRootUser.isRootUser(whoToAuth)) {
			return new BasicPolicyDecision(Authorization.AUTHORIZED,
					"RootUserPolicy: approved");
		} else {
			return new BasicPolicyDecision(Authorization.INCONCLUSIVE,
					"not root user");
		}
	}

	@Override
	public String toString() {
		return "RootUserPolicy - " + hashCode();
	}

	// ----------------------------------------------------------------------
	// Setup class
	// ----------------------------------------------------------------------

	public static class Setup implements ServletContextListener {
		private ServletContext ctx;
		private StartupStatus ss;
		private UserAccountsDao uaDao;
		private ConfigurationProperties cp;
		private String configuredRootUser;
		private boolean configuredRootUserExists;
		private TreeSet<String> otherRootUsers;

		@Override
		public void contextInitialized(ServletContextEvent sce) {
			ctx = sce.getServletContext();
			ss = StartupStatus.getBean(ctx);
			cp = ConfigurationProperties.getBean(ctx);

			try {
				uaDao = ModelAccess.on(ctx).getWebappDaoFactory()
						.getUserAccountsDao();
				configuredRootUser = getRootEmailFromConfig();

				otherRootUsers = getEmailsOfAllRootUsers();
				configuredRootUserExists = otherRootUsers
						.remove(configuredRootUser);

				if (configuredRootUserExists) {
					if (otherRootUsers.isEmpty()) {
						informThatRootUserExists();
					} else {
						complainAboutMultipleRootUsers();
					}
				} else {
					createRootUser();
					if (!otherRootUsers.isEmpty()) {
						complainAboutWrongRootUsers();
					}
				}

				ServletPolicyList.addPolicy(ctx, new RootUserPolicy());
			} catch (Exception e) {
				ss.fatal(this, "Failed to set up the RootUserPolicy", e);
			}
		}

		private String getRootEmailFromConfig() {
			String email = ConfigurationProperties.getBean(ctx).getProperty(
					PROPERTY_ROOT_USER_EMAIL);
			if (email == null) {
				throw new IllegalStateException(
						"runtime.properties must contain a value for '"
								+ PROPERTY_ROOT_USER_EMAIL + "'");
			} else {
				return email;
			}
		}


		private TreeSet<String> getEmailsOfAllRootUsers() {
			TreeSet<String> rootUsers = new TreeSet<String>();
			for (UserAccount ua : uaDao.getAllUserAccounts()) {
				if (ua.isRootUser()) {
					rootUsers.add(ua.getEmailAddress());
				}
			}
			return rootUsers;
		}

		/**
		 * TODO The first and last name should be left blank, so the user will
		 * be forced to edit them. However, that's not in place yet.
		 */
		private void createRootUser() {
			if (!Authenticator.isValidEmailAddress(configuredRootUser)) {
				throw new IllegalStateException("Value for '"
						+ PROPERTY_ROOT_USER_EMAIL
						+ "' is not a valid email address: '"
						+ configuredRootUser + "'");
			}

			if (null != uaDao.getUserAccountByEmail(configuredRootUser)) {
				throw new IllegalStateException("Can't create root user - "
						+ "an account already exists with email address '"
						+ configuredRootUser + "'");
			}

			UserAccount ua = new UserAccount();
			ua.setEmailAddress(configuredRootUser);
			ua.setFirstName("root");
			ua.setLastName("user");
			// UQAM using getRootPasswordFromConfig()
			ua.setArgon2Password(Authenticator.applyArgon2iEncoding(
					getRootPasswordFromConfig()));
			ua.setMd5Password("");
			Boolean toto;
			// UQAM using getRootPasswdChangeRequiredFromConfig()
			ua.setPasswordChangeRequired(getRootPasswdChangeRequiredFromConfig().booleanValue());
			ua.setStatus(Status.ACTIVE);
			ua.setRootUser(true);

			uaDao.insertUserAccount(ua);

			StartupStatus.getBean(ctx).info(this,
					"Created root user '" + configuredRootUser + "'");
		}

		private void informThatRootUserExists() {
			ss.info(this, "Root user is " + configuredRootUser);
		}

		private void complainAboutMultipleRootUsers() {
			for (String other : otherRootUsers) {
				ss.warning(this, "runtime.properties specifies '"
						+ configuredRootUser + "' as the value for '"
						+ PROPERTY_ROOT_USER_EMAIL
						+ "', but the system also contains this root user: "
						+ other);
			}
			ss.warning(this, "For security, "
					+ "it is best to delete unneeded root user accounts.");
		}

		private void complainAboutWrongRootUsers() {
			for (String other : otherRootUsers) {
				ss.warning(this, "runtime.properties specifies '"
						+ configuredRootUser + "' as the value for '"
						+ PROPERTY_ROOT_USER_EMAIL
						+ "', but the system contains this root user instead: "
						+ other);
			}
			ss.warning(this, "Creating root user '" + configuredRootUser + "'");
			ss.warning(this, "For security, "
					+ "it is best to delete unneeded root user accounts.");
		}
		/*
		 * UQAM
		 * Add for getting rootUser.password property value from runtime.properties
		 */
		private String getRootPasswordFromConfig() {
			String passwd = ConfigurationProperties.getBean(ctx).getProperty(
					PROPERTY_ROOT_USER_PASSWORD);
			if (passwd == null) {
				passwd = ROOT_USER_INITIAL_PASSWORD;
			}
			return passwd;
		}

		/*
		 * UQAM
		 * Add for getting rootUser.passwordChangeRequired  property value  from runtime.properties
		 */
		private Boolean getRootPasswdChangeRequiredFromConfig() {
			String passwdCR = ConfigurationProperties.getBean(ctx).getProperty(
					PROPERTY_ROOT_USER_PASSWORD_CHANGE_REQUIRED);
			if (passwdCR == null) {
				passwdCR = ROOT_USER_INITIAL_PASSWORD_CHANGE_REQUIRED;
			}
			return new Boolean(passwdCR);
		}
		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			// Nothing to destroy
		}
	}
}
