/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.controller.visualization;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class VisualizationsDependencyInjector {

	private static Map<String, VisualizationRequestHandler> visualizationIDsToClass;

	/**
	 * This method is used to inject vis dependencies i.e. the vis algorithms that are
     * being implemented into the vis controller. Modified Dependency Injection pattern is
     * used here. XML file containing the location of all the vis is saved in accessible folder.
	 * @param servletContext Servlet context
	 */
	private synchronized static Map<String, VisualizationRequestHandler> initVisualizations(
			ServletContext servletContext) {

		/*
		 * A visualization request has already been made causing the visualizationIDsToClass to be
		 * initiated & populated with visualization ids to its request handlers.
		 * */
		if (visualizationIDsToClass != null) {
			return visualizationIDsToClass;
		}

		String resourcePath =
			servletContext
				.getRealPath(VisualizationFrameworkConstants
						.RELATIVE_LOCATION_OF_FM_VISUALIZATIONS_BEAN);

		ApplicationContext context = new ClassPathXmlApplicationContext(
											"file:" + resourcePath);

		BeanFactory factory = context;

		VisualizationInjector visualizationInjector =
				(VisualizationInjector) factory.getBean("visualizationInjector");

		visualizationIDsToClass = visualizationInjector.getVisualizationIDToClass();


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
