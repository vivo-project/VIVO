/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.constants;

/**
 * This contains constants related to the visualization code.
 * @author cdtank
 */
public class VisConstants {

	public static final int MAX_NAME_TEXT_LENGTH = 20;
	public static final int MINIMUM_YEARS_CONSIDERED_FOR_SPARKLINE = 10;

	public static final String RESULT_FORMAT_PARAM = "RS_TEXT";
	public static final String RDF_RESULT_FORMAT_PARAM = "RDF/XML-ABBREV";

	public static enum DataVisMode {
		CSV, JSON
	};

}
