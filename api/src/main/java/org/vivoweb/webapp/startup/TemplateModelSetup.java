package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModelBuilder;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.VIVOIndividualTemplateModelBuilder;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.ListedIndividualBuilder;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.VIVOListedIndividualBuilder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TemplateModelSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        IndividualTemplateModelBuilder.setCustomBuilder(new VIVOIndividualTemplateModelBuilder());
        ListedIndividualBuilder.setCustomBuilder(new VIVOListedIndividualBuilder());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
