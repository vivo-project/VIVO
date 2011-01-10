/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker;

public class VisualizationFrameworkConstants {

	/*
	 * Contains the location of bean containing info on all the visualizations
	 * available in that instance. Currently it is stored under
	 * "productMods/WEB-INF..."
	 */
	public static final String RELATIVE_LOCATION_OF_VISUALIZATIONS_BEAN = 
		"/WEB-INF/visualization/visualizations-beans-injection.xml";
	
	/*
	 * Freemarker Version
	 * */
	public static final String RELATIVE_LOCATION_OF_FM_VISUALIZATIONS_BEAN = 
		"/WEB-INF/visualization/visualizations-beans-injection-fm.xml";
	
	public static final String ERROR_TEMPLATE = "/visualization/visualizationError.ftl";

	/*
	 * Vis URL prefix that is seen by all the users
	 */
	public static final String VISUALIZATION_URL_PREFIX = "/visualization";
	public static final String FREEMARKERIZED_VISUALIZATION_URL_PREFIX = "/visualizationfm";
	public static final String AJAX_VISUALIZATION_SERVICE_URL_PREFIX = "/visualizationAjax";
	public static final String DATA_VISUALIZATION_SERVICE_URL_PREFIX = "/visualizationData";
	
	public static final String INDIVIDUAL_URL_PREFIX = "/individual";

	/*
	 * These represent possible query keys in a URI for visualization purposes.
	 * Examples, 
	 * 		1. http://vivo.indiana.edu/visualization?uri=http://vivoweb.org/ontology/core/Person10979&vis=person_level&render_mode=standalone
	 * 		2. http://vivo.indiana.edu/visualization?uri=http://vivoweb.org/ontology/core/Person72&vis=person_pub_count&render_mode=dynamic&container=vis_container
	 * */
	public static final String VIS_TYPE_KEY = "vis";
	public static final String VIS_CONTAINER_KEY = "container";
	public static final String INDIVIDUAL_URI_KEY = "uri";
	public static final String VIS_MODE_KEY = "vis_mode";
	public static final String RENDER_MODE_KEY = "render_mode";

	/*
	 * These values represent possible render modes.
	 * */
	public static final String STANDALONE_RENDER_MODE = "standalone";
	public static final String DYNAMIC_RENDER_MODE = "dynamic";
	public static final String DATA_RENDER_MODE = "data";
	public static final String PDF_RENDER_MODE = "pdf";

	/*
	 * These values represent possible sub-vis modes.
	 * */
	public static final String IMAGE_VIS_MODE = "image";
	public static final String SHORT_SPARKLINE_VIS_MODE = "short";
	public static final String FULL_SPARKLINE_VIS_MODE = "full";
	public static final String COPI_VIS_MODE = "copi";
	public static final String COAUTHOR_VIS_MODE = "coauthor";

	/*
	 * Vis modes for CoauthorshipRequest Handler
	 * */
	public static final String COAUTHORS_COUNT_PER_YEAR_VIS_MODE = "coauthors_count_per_year";
	public static final String COAUTHORS_LIST_VIS_MODE = "coauthors";
	public static final String COAUTHOR_NETWORK_STREAM_VIS_MODE = "coauthor_network_stream";
	public static final String COAUTHOR_NETWORK_DOWNLOAD_VIS_MODE = "coauthor_network_download";
	
	/*
	 * Vis modes for CoPIRequest Handler
	 * */
	public static final String COPIS_COUNT_PER_YEAR_VIS_MODE = "copis_count_per_year";
	public static final String COPIS_LIST_VIS_MODE = "copis";
	public static final String COPI_NETWORK_STREAM_VIS_MODE = "copi_network_stream";
	public static final String COPI_NETWORK_DOWNLOAD_VIS_MODE = "copi_network_download";
	
	/*
	 * These values represent possible utilities vis modes.
	 * */
	public static final String PROFILE_INFO_UTILS_VIS_MODE = "PROFILE_INFO";
	public static final String PROFILE_UTILS_VIS_MODE = "PROFILE_URL";
	public static final String COAUTHOR_UTILS_VIS_MODE = "COAUTHORSHIP_URL";
	public static final String PERSON_LEVEL_UTILS_VIS_MODE = "PERSON_LEVEL_URL";
	public static final String COPI_UTILS_VIS_MODE = "COPI_URL";
	public static final String IMAGE_UTILS_VIS_MODE = "IMAGE_URL";
	public static final String UNIVERSITY_COMPARISON_VIS_MODE = "UNIVERSITY";
	public static final String SCHOOL_COMPARISON_VIS_MODE = "SCHOOL";
	public static final String DEPARTMENT_COMPARISON_VIS_MODE = "DEPARTMENT";
	public static final String HIGHEST_LEVEL_ORGANIZATION_VIS_MODE = "HIGHEST_LEVEL_ORGANIZATION";
	

	/*
	 * These values represent possible visualizations provided as values to the "vis" url key.
	 * */
	public static final String PERSON_PUBLICATION_COUNT_VIS = "person_pub_count";
	public static final String PERSON_GRANT_COUNT_VIS = "person_grant_count";
	public static final String PDF_REPORT_VIS = "pdf_report";
	public static final String COLLEGE_PUBLICATION_COUNT_VIS = "college_pub_count";
	public static final String COAUTHORSHIP_VIS = "coauthorship";
	public static final String PERSON_LEVEL_VIS = "person_level";
	public static final String UTILITIES_VIS = "utilities";
	public static final String ENTITY_COMPARISON_VIS = "entity_comparison";
	public static final String CO_PI_VIS = "coprincipalinvestigator";
	

}
