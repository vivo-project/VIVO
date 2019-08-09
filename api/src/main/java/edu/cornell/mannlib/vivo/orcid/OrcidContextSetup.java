/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid;

import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.*;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.DEFAULT_EXTERNAL_ID_COMMON_NAME;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.PROPERTY_EXTERNAL_ID_COMMON_NAME;

import java.util.EnumMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vivo.orcid.controller.OrcidAbstractHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Setup for the ORCID interface.
 *
 * Note that the property for CLIENT_SECRET is "orcid.clientPassword". Since it
 * ends in "password", it will not be displayed on the ShowConfiguration page.
 *
 * The CALLBACK_PATH is hardcoded. It is relative to the WEBAPP_BASE_URL, so it
 * won't change.
 */
public class OrcidContextSetup implements ServletContextListener {
	private static final Log log = LogFactory.getLog(OrcidContextSetup.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		ConfigurationProperties props = ConfigurationProperties.getBean(ctx);
		StartupStatus ss = StartupStatus.getBean(ctx);

		if (props.getProperty("orcid.clientId", "").isEmpty()) {
			ss.info(this, "ORCID Integration is not configured.");
			return;
		}

		initializeOrcidClientContext(props, ss);

		checkForCommonNameProperty(props, ss);
	}

	private void initializeOrcidClientContext(ConfigurationProperties props,
			StartupStatus ss) {
		try {
			if (!"member".equalsIgnoreCase(props.getProperty("orcid.apiLevel", "member"))) {
				OrcidAbstractHandler.setAPiLevelPublic();
			}

			Map<Setting, String> settings = new EnumMap<>(Setting.class);
			settings.put(CLIENT_ID, props.getProperty("orcid.clientId"));
			settings.put(CLIENT_SECRET,
					props.getProperty("orcid.clientPassword"));
			settings.put(API_VERSION,
					props.getProperty("orcid.apiVersion"));
			settings.put(API_ENVIRONMENT,
					props.getProperty("orcid.api"));
			settings.put(WEBAPP_BASE_URL,
					props.getProperty("orcid.webappBaseUrl"));
			settings.put(CALLBACK_PATH, "orcid/callback");

			OrcidClientContext.initialize(settings);
			ss.info(this, "Context is: " + OrcidClientContext.getInstance());

		} catch (OrcidClientException e) {
			ss.warning(this, "Failed to initialize OrcidClientContent", e);
		}
	}

	private void checkForCommonNameProperty(ConfigurationProperties props,
			StartupStatus ss) {
		if (StringUtils.isBlank(props
				.getProperty(PROPERTY_EXTERNAL_ID_COMMON_NAME))) {
			ss.warning(this, "'" + PROPERTY_EXTERNAL_ID_COMMON_NAME
					+ "' is not set. " + "Using default value of '"
					+ DEFAULT_EXTERNAL_ID_COMMON_NAME + "'");

		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to tear down.
	}

}
