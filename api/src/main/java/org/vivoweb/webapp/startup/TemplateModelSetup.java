package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.VIVOIndividualTemplateModel;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModelBuilder;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.ListedIndividual;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.ListedIndividualBuilder;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.VIVOListedIndividual;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TemplateModelSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        VIVOIndividualTemplateModel.setAsDefault();
        VIVOListedIndividual.setAsDefault();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
