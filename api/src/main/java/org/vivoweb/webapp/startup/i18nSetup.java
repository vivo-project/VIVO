/* $This file is distributed under the terms of the license in LICENSE$ */

package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.i18n.VitroResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class i18nSetup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        VitroResourceBundle.addAppPrefix("vivo");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
