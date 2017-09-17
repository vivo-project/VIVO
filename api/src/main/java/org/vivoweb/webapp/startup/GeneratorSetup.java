package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.JspToGeneratorMapping;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class GeneratorSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        JspToGeneratorMapping.jspsToGenerators.put("defaultAddMissingIndividualForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.VIVODefaultAddMissingIndividualFormGenerator.class.getName());

        JspToGeneratorMapping.jspsToGenerators.put("newIndividualForm.jsp",
                        edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.VIVONewIndividualFormGenerator.class.getName());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
