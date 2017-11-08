/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.setup;

import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.CachingRDFServiceExecutor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Setup class for Visualizations, in order to record a context-referenced RDFService
 *
 * If this class is missing, caches can not be refreshed in the background
 */
public class VisualizationSetup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        RDFService rdfService = ModelAccess.on(ctx).getRDFService();

        CachingRDFServiceExecutor.setBackgroundRDFService(rdfService);

        /**
         * Currently disabled, but if  you want the Visualization caches to be "warmed" during startup,
         * uncomment the line below.
         *
         * NB: Caches will refresh in the background, it won't pause the startup
         */

        // VisualizationCaches.rebuildAll();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
