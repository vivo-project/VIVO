/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.controller.visualization;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import edu.cornell.mannlib.vitro.webapp.visualization.capabilitymap.CapabilityMapRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIGrantCountRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.mapofscience.MapOfScienceVisualizationRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.ModelConstructorRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.persongrantcount.PersonGrantCountRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.personlevel.PersonLevelRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.CumulativeCountRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.PersonPublicationCountRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph.TemporalGrantVisualizationRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph.TemporalPublicationVisualizationRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.tools.ToolsRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.utilities.UtilitiesRequestHandler;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class VisualizationsDependencyInjector {

	private static Map<String, VisualizationRequestHandler> visualizationIDsToClass;

    /**
     * Instantiate the request handlers for each type of visualization.
     * @return a map of visualization ID string to request handler
     */
	private synchronized static Map<String, VisualizationRequestHandler> initVisualizations(
    		ServletContext servletContext) {
        Map<String, VisualizationRequestHandler> handlers = new HashMap<
                String, VisualizationRequestHandler>() ;
        handlers.put("capabilitymap", new CapabilityMapRequestHandler());
        handlers.put("cumulative_pub_count", new CumulativeCountRequestHandler());
        handlers.put("person_pub_count", new PersonPublicationCountRequestHandler());
        handlers.put("utilities", new UtilitiesRequestHandler());
        handlers.put("coauthorship", new CoAuthorshipRequestHandler());
        handlers.put("person_grant_count", new PersonGrantCountRequestHandler());
        PersonLevelRequestHandler personLevel = new PersonLevelRequestHandler();
        handlers.put("person_level", personLevel);
        handlers.put("author-network", personLevel);
        handlers.put("investigator-network", personLevel);
        TemporalPublicationVisualizationRequestHandler pubTemporal = new
                TemporalPublicationVisualizationRequestHandler();
        handlers.put("entity_comparison", pubTemporal);
        handlers.put("pub_temporal", pubTemporal);
        handlers.put("publication-graph", pubTemporal);
        handlers.put("coprincipalinvestigator", new CoPIGrantCountRequestHandler());
        TemporalGrantVisualizationRequestHandler grantTemporal = new
                TemporalGrantVisualizationRequestHandler();
        handlers.put("entity_grant_count", grantTemporal);
        handlers.put("grant_temporal", grantTemporal);
        handlers.put("grant-graph", grantTemporal);
        handlers.put("map-of-science", new MapOfScienceVisualizationRequestHandler());
        handlers.put("refresh-cache", new ModelConstructorRequestHandler());
        handlers.put("tools", new ToolsRequestHandler());
        visualizationIDsToClass = handlers;
        return visualizationIDsToClass;
	}

	public static Map<String, VisualizationRequestHandler> getVisualizationIDsToClassMap(
			ServletContext servletContext) {
		if (visualizationIDsToClass != null) {
			return visualizationIDsToClass;
		} else {
			return initVisualizations(servletContext);
		}
	}

}
