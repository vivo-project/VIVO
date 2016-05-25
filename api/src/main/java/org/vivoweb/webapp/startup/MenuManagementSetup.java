package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.utils.menuManagement.MenuManagementDataUtils;
import edu.cornell.mannlib.vitro.webapp.utils.menuManagement.VIVOMenuManagementDataUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MenuManagementSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        MenuManagementDataUtils.setImplementation(new VIVOMenuManagementDataUtils());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
