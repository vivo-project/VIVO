package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.web.templatemodels.searchresult.VIVOIndividualSearchResult;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SearchResultTemplateModelSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        VIVOIndividualSearchResult.register();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
