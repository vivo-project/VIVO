/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.controller.visualization;

import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class VisualizationInjector {
	private Map<String, VisualizationRequestHandler> visualizationIDToClass;

	public Map<String, VisualizationRequestHandler> getVisualizationIDToClass() {
		return visualizationIDToClass;
	}

	public void setVisualizations(Map<String, VisualizationRequestHandler> visualizationIDToClass) {
		this.visualizationIDToClass = visualizationIDToClass;
	}

}
