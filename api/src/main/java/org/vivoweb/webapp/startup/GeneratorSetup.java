package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.VIVODefaultAddMissingIndividualFormGenerator;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.searchresult.VIVOIndividualSearchResult;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class GeneratorSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        VIVODefaultAddMissingIndividualFormGenerator.register();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
