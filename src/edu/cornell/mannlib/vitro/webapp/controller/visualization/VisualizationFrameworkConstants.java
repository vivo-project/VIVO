/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization;

public class VisualizationFrameworkConstants {
	
	/*
	 * Contains the location of bean containing info on all the visualizations available
	 * in that instance. Currently it is stored under "productMods/WEB-INF..."
	 * */
	public static final String RELATIVE_LOCATION_OF_VISUALIZATIONS_BEAN = 
									"/WEB-INF/visualization/visualizations-beans-injection.xml";
	
	/*
	 * Vis URL prefix that is seen by all the users
	 * */
	public static final String VISUALIZATION_URL_PREFIX = "/visualization";
	
	public static final String VIS_TYPE_URL_HANDLE = "vis";
	public static final String VIS_CONTAINER_URL_HANDLE = "container";
	public static final String INDIVIDUAL_URI_URL_HANDLE = "uri";
	public static final String VIS_MODE_URL_HANDLE = "vis_mode";
	public static final String RENDER_MODE_URL_HANDLE = "render_mode";
	
	public static final String STANDALONE_RENDER_MODE_URL_VALUE = "standalone";
	public static final String DYNAMIC_RENDER_MODE_URL_VALUE = "dynamic";
	public static final String DATA_RENDER_MODE_URL_VALUE = "data";
	public static final String PDF_RENDER_MODE_URL_VALUE = "pdf";
	public static final String IMAGE_VIS_MODE_URL_VALUE = "image";
	public static final String SPARKLINE_VIS_MODE_URL_VALUE = "sparkline";
	public static final String COAUTHORSLIST_VIS_MODE_URL_VALUE = "coauthors";
	public static final String PROFILE_INFO_UTILS_VIS_MODE = "PROFILE_INFO";
	public static final String PROFILE_UTILS_VIS_MODE = "PROFILE_URL";
	public static final String COAUTHOR_UTILS_VIS_MODE = "COAUTHORSHIP_URL";
	public static final String PERSON_LEVEL_UTILS_VIS_MODE = "PERSON_LEVEL_URL";
	public static final String IMAGE_UTILS_VIS_MODE = "IMAGE_URL";
	
	
	
	
}
