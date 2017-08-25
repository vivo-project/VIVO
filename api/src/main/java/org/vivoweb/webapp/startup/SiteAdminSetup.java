package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.InstitutionalInternalClassController;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.SiteAdminController;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.utils.menuManagement.MenuManagementDataUtils;
import edu.cornell.mannlib.vitro.webapp.utils.menuManagement.VIVOMenuManagementDataUtils;
import edu.cornell.mannlib.vitro.webapp.visualization.tools.ToolsRequestHandler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SiteAdminSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        SiteAdminController.registerSiteMaintenanceUrl("rebuildVisCache", UrlBuilder.getUrl("/vis/tools"), ToolsRequestHandler.REQUIRED_ACTIONS);
        SiteAdminController.registerSiteConfigData("internalClass", UrlBuilder.getUrl("/processInstitutionalInternalClass"), InstitutionalInternalClassController.REQUIRED_ACTIONS);
   }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
