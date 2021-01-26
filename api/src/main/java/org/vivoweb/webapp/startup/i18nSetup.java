/* $This file is distributed under the terms of the license in LICENSE$ */

package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.i18n.VitroResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
  * Configures VIVO to look for i18n properties files prefixed with 'vivo_'.
  * This listener must be run before any other code that uses resource bundles.
  * As this listener does not depend on any others, it should be run at or near
  * the top of the list in startup_listeners.txt.
  */
public class i18nSetup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        VitroResourceBundle.addAppPrefix("vivo");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
