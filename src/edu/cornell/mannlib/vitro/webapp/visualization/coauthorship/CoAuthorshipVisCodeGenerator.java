/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;

import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;


@SuppressWarnings("serial")
public class CoAuthorshipVisCodeGenerator {

	/*
	 * There are 2 modes of sparkline that are available via this visualization.
	 * 		1. Short Sparkline - This sparkline will render all the data points (or sparks),
	 * 			which in this case are the coauthors over the years, from the last 10 years.
	 * 
	 * 		2. Full Sparkline - This sparkline will render all the data points (or sparks) 
	 * 			spanning the career of the person & last 10 years at the minimum, in case if
	 * 			the person started his career in the last 10 yeras.
	 * */
	private static final Map<String, String> VIS_DIV_NAMES = new HashMap<String, String>() { {

		put("SHORT_SPARK", "unique_coauthors_short_sparkline_vis");
		put("FULL_SPARK", "unique_coauthors_full_sparkline_vis");

	} };

	private static final String VISUALIZATION_STYLE_CLASS = "sparkline_style";
	
	private static final String DEFAULT_VISCONTAINER_DIV_ID = "unique_coauthors_vis_container";
	
	private Map<String, Set<Node>> yearToUniqueCoauthors;

	private Log log;

	private SparklineData sparklineData;

	private String contextPath;

	private String individualURI;

	public CoAuthorshipVisCodeGenerator(String contextPath, 
									  String individualURI, 
									  String visMode, 
									  String visContainer, 
									  Map<String, Set<Node>> yearToUniqueCoauthors, 
									  Log log) {
		
		this.contextPath = contextPath;
		this.individualURI = individualURI;
		
		this.yearToUniqueCoauthors = yearToUniqueCoauthors;
		this.sparklineData = new SparklineData();
		
		this.log = log;
		
		generateVisualizationCode(visMode, visContainer);
	}
	
	/**
	 * This method is used to generate the visualization code (HMTL, CSS & JavaScript).
	 * There 2 parts to it - 1. Actual Content Code & 2. Context Code.
	 * 		1. Actual Content code in this case is the sparkline image, text related to 
	 * data and the wrapping tables. This is generated via call to google vis API through
	 * JavaScript.
	 * 		2. Context code is generally optional but contains code pertaining to tabulated
	 * data & links to download files etc.
	 * @param visMode
	 * @param visContainer
	 */
	private void generateVisualizationCode(String visMode,
										   String visContainer) {
		
    	sparklineData.setSparklineContent(getMainVisualizationCode(visMode, 
    																	  visContainer));
    	
    	sparklineData.setSparklineContext(getVisualizationContextCode(visMode));
    	
	}

	private String getMainVisualizationCode(String visMode,
										    String providedVisContainerID) {

		int numOfYearsToBeRendered = 0;
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int shortSparkMinYear = currentYear 
									- VisConstants.MINIMUM_YEARS_CONSIDERED_FOR_SPARKLINE 
									+ 1;
		
    	/*
    	 * This is required because when deciding the range of years over which the vis
    	 * was rendered we dont want to be influenced by the "DEFAULT_PUBLICATION_YEAR".
    	 * */
		Set<String> publishedYears = new HashSet<String>(yearToUniqueCoauthors.keySet());
    	publishedYears.remove(VOConstants.DEFAULT_PUBLICATION_YEAR);
		
		/*
		 * We are setting the default value of minPublishedYear to be 10 years before 
		 * the current year (which is suitably represented by the shortSparkMinYear),
		 * this in case we run into invalid set of published years.
		 * */
		int minPublishedYear = shortSparkMinYear;
		
		String visContainerID = null;
		
		StringBuilder visualizationCode = new StringBuilder();

		if (yearToUniqueCoauthors.size() > 0) {
			try {
				minPublishedYear = Integer.parseInt(Collections.min(publishedYears));
			} catch (NoSuchElementException e1) {
				log.debug("vis: " + e1.getMessage() + " error occurred for " 
							+ yearToUniqueCoauthors.toString());
			} catch (NumberFormatException e2) {
				log.debug("vis: " + e2.getMessage() + " error occurred for " 
							+ yearToUniqueCoauthors.toString());
			}
		}
		
		int minPubYearConsidered = 0;
		
		/*
		 * There might be a case that the author has made his first publication within the 
		 * last 10 years but we want to make sure that the sparkline is representative of 
		 * at least the last 10 years, so we will set the minPubYearConsidered to 
		 * "currentYear - 10" which is also given by "shortSparkMinYear".
		 * */
		if (minPublishedYear > shortSparkMinYear) {
			minPubYearConsidered = shortSparkMinYear;
		} else {
			minPubYearConsidered = minPublishedYear;
		}
		
		numOfYearsToBeRendered = currentYear - minPubYearConsidered + 1;
		
		visualizationCode.append("<style type='text/css'>" 
									+ "." + VISUALIZATION_STYLE_CLASS + " table{" 
									+ "		margin: 0;" 
									+ "  		padding: 0;" 
									+ "  		width: auto;" 
									+ "  		border-collapse: collapse;" 
									+ "    	border-spacing: 0;" 
									+ "    	vertical-align: inherit;" 
									+ "}" 
									+ ".incomplete-data-holder {" 
									+ "" 
									+ "}" 
									+ "td.sparkline_number { text-align:right; " 
									+ "padding-right:5px; }" 
									+ "td.sparkline_text   {text-align:left;}" 
									+ "</style>\n");
		
		visualizationCode.append("<script type=\"text/javascript\">\n" 
									+ "function drawUniqueCoauthorCountVisualization(providedSparklineImgTD) {\n" 
									+ "var data = new google.visualization.DataTable();\n" 
									+ "data.addColumn('string', 'Year');\n" 
									+ "data.addColumn('number', 'Unique co-authors');\n" 
									+ "data.addRows(" + numOfYearsToBeRendered + ");\n");

		int uniqueCoAuthorCounter = 0;
		int renderedFullSparks = 0;
		Set<Node> allCoAuthorsWithKnownAuthorshipYears = new HashSet<Node>();
		
		for (int publicationYear = minPubYearConsidered; 
					publicationYear <= currentYear; 
					publicationYear++) {

				String publicationYearAsString = String.valueOf(publicationYear);
				Set<Node> currentCoAuthors = yearToUniqueCoauthors.get(publicationYearAsString);
				
				Integer currentUniqueCoAuthors = null;
				
				if (currentCoAuthors != null) {
					currentUniqueCoAuthors = currentCoAuthors.size();
					allCoAuthorsWithKnownAuthorshipYears.addAll(currentCoAuthors); 
				} else {
					currentUniqueCoAuthors = 0;
				}
				
				visualizationCode.append("data.setValue("
												+ uniqueCoAuthorCounter
												+ ", 0, '"
												+ publicationYearAsString
												+ "');\n");

				visualizationCode.append("data.setValue("
												+ uniqueCoAuthorCounter
												+ ", 1, "
												+ currentUniqueCoAuthors
												+ ");\n");
				uniqueCoAuthorCounter++;
		}

		/*
		 * For the purpose of this visualization I have come up with a term "Sparks" which 
		 * essentially means data points. 
		 * Sparks that will be rendered in full mode will always be the one's which have any year
		 * associated with it. Hence.
		 * */
		renderedFullSparks = allCoAuthorsWithKnownAuthorshipYears.size();

		/*
		 * Total publications will also consider publications that have no year associated with
		 * them. Hence.
		 * */
		Integer unknownYearCoauthors = 0;
		if (yearToUniqueCoauthors.get(VOConstants.DEFAULT_PUBLICATION_YEAR) != null) {
			unknownYearCoauthors = yearToUniqueCoauthors
											.get(VOConstants.DEFAULT_PUBLICATION_YEAR).size();
		}
		
		
		String sparklineDisplayOptions = "{width: 150, height: 30, showAxisLines: false, " 
											+ "showValueLabels: false, labelPosition: 'none'}";
		
		if (providedVisContainerID != null) {
			visContainerID = providedVisContainerID;
		} else {
			visContainerID = DEFAULT_VISCONTAINER_DIV_ID;
		}
		
		/*
		 * By default these represents the range of the rendered sparks. Only in case of
		 * "short" sparkline mode we will set the Earliest RenderedPublication year to
		 * "currentYear - 10". 
		 * */
		sparklineData.setEarliestRenderedPublicationYear(minPublishedYear);
		sparklineData.setLatestRenderedPublicationYear(currentYear);
		
		/*
		 * The Full Sparkline will be rendered by default. Only if the url has specific mention of
		 * SHORT_SPARKLINE_MODE_KEY then we render the short sparkline and not otherwise.
		 * */
		
		
		/*
		 * Since building StringBuilder objects (which is being used to store the vis code) is 
		 * essentially a side-effecting process, we have both the activators method as 
		 * side-effecting. They both side-effect "visualizationCode" 
		 * */
		if (VisualizationFrameworkConstants.SHORT_SPARKLINE_VIS_MODE.equalsIgnoreCase(visMode)) {
			
			sparklineData.setEarliestRenderedPublicationYear(shortSparkMinYear);
			generateShortSparklineVisualizationContent(currentYear,
													   shortSparkMinYear, 
													   visContainerID, 
													   visualizationCode,
													   unknownYearCoauthors,
													   sparklineDisplayOptions);	
		} else {
			generateFullSparklineVisualizationContent(currentYear,
					   								  minPubYearConsidered,
					   								  visContainerID,
													  visualizationCode, 
													  unknownYearCoauthors,
													  renderedFullSparks, 
													  sparklineDisplayOptions);
		}
		
		log.debug(visualizationCode);

		return visualizationCode.toString();
	}
	
	private void generateShortSparklineVisualizationContent(int currentYear,
															int shortSparkMinYear, 
															String visContainerID,
															StringBuilder visualizationCode, 
															int unknownYearCoauthors,
															String sparklineDisplayOptions) {
		
		/*
		 * Create a view of the data containing only the column pertaining to publication count.  
		 * */
		visualizationCode.append("var shortSparklineView = " 
									+ "new google.visualization.DataView(data);\n" 
									+ "shortSparklineView.setColumns([1]);\n");		

		/*
		 * For the short view we only want the last 10 year's view of publication count, 
		 * hence we filter the data we actually want to use for render.
		 * */
		visualizationCode.append("shortSparklineView.setRows(" 
									+ "data.getFilteredRows([{column: 0, " 
									+ "minValue: '" + shortSparkMinYear + "', " 
									+ "maxValue: '" + currentYear + "'}])" 
									+ ");\n");

		/*
		 * Create the vis object and draw it in the div pertaining to short-sparkline.
		 * */
		visualizationCode.append("var short_spark = new google.visualization.ImageSparkLine(" 
									+ "providedSparklineImgTD[0]" 
									+ ");\n" 
									+ "short_spark.draw(shortSparklineView, " 
									+ sparklineDisplayOptions + ");\n");

		/*
		 * We want to display how many publication counts were considered, so this is used 
		 * to calculate this.
		 * */
		visualizationCode.append("var shortSparkRows = shortSparklineView.getViewRows();\n" 
									+ "var renderedShortSparks = 0;\n" 
									+ "$.each(shortSparkRows, function(index, value) {" 
									+ "renderedShortSparks += data.getValue(value, 1);" 
									+ "});\n");

		
		
		/*
		 * Generate the text introducing the vis.
		 * */
		
		String imcompleteDataText = "This information is based solely on publications which "  
										+ "have been loaded into the VIVO system. " 
										+ "This may only be a small sample of the person\\'s " 
										+ "total work.";
		
		
		visualizationCode.append("$('#" + VIS_DIV_NAMES.get("SHORT_SPARK") 
										+ " td.sparkline_number')" + ".text(" 
										+ "parseInt(renderedShortSparks) + " 
										+ "parseInt(" + unknownYearCoauthors + "));");
		
		visualizationCode.append("var shortSparksText = ''" 
									+ "+ ' co-author(s) within the last 10 years '" 
									+ "<span class=\"incomplete-data-holder\" title=\"" 
									+ imcompleteDataText + "\">incomplete data</span>'" 
									+ "+ '';" 
									+ "$('#" + VIS_DIV_NAMES.get("SHORT_SPARK") 
									+ " td.sparkline_text').html(shortSparksText);");

		visualizationCode.append("}\n ");
		
		/*
		 * Generate the code that will activate the visualization. It takes care of creating div 
		 * elements to hold the actual sparkline image and then calling the 
		 * drawUniqueCoauthorCountVisualization function. 
		 * */
		visualizationCode.append(generateVisualizationActivator(VIS_DIV_NAMES.get("SHORT_SPARK"), 
																visContainerID));
		
	}
	
	private void generateFullSparklineVisualizationContent(int currentYear,
														   int minPubYearConsidered,
														   String visContainerID,
														   StringBuilder visualizationCode,
														   int unknownYearCoauthors,
														   int renderedFullSparks,
														   String sparklineDisplayOptions) {
		
		String csvDownloadURLHref = ""; 
		
		try {
			if (getCSVDownloadURL() != null) {
				csvDownloadURLHref = "<a href=\"" + getCSVDownloadURL() 
										+ "\" class=\"inline_href\">(.CSV File)</a>";
			} else {
				csvDownloadURLHref = "";
			}

		} catch (UnsupportedEncodingException e) {
			csvDownloadURLHref = "";
		}
		
		visualizationCode.append("var fullSparklineView = " 
									+ "new google.visualization.DataView(data);\n" 
									+ "fullSparklineView.setColumns([1]);\n");
		
		visualizationCode.append("var full_spark = new google.visualization.ImageSparkLine(" 
									+ "providedSparklineImgTD[0]" 
									+ ");\n" 
									+ "full_spark.draw(fullSparklineView, " 
									+ sparklineDisplayOptions + ");\n");
		
		visualizationCode.append("$('#" + VIS_DIV_NAMES.get("FULL_SPARK") 
									+ " td.sparkline_number')" 
									+ ".text('" + (renderedFullSparks 
									+ unknownYearCoauthors) + "').css('font-weight', 'bold');");
		
		visualizationCode.append("var allSparksText = ''" 
									+ "+ ' <h3>co-author(s)</h3> '" 
									+ "+ ' <span class=\"sparkline_range\">" 
									+ "from " + minPubYearConsidered + " to " + currentYear + "" 
									+ "</span> '" 
									+ "+ ' " + csvDownloadURLHref + " ';" 
									+ "$('#" + VIS_DIV_NAMES.get("FULL_SPARK") 
									+ " td.sparkline_text').html(allSparksText);");
		
		visualizationCode.append("}\n ");
		
		visualizationCode.append(generateVisualizationActivator(VIS_DIV_NAMES.get("FULL_SPARK"), 
								 visContainerID));
		
	}
	
	private String generateVisualizationActivator(String sparklineID, String visContainerID) {
		
		String sparklineTableWrapper = "\n" 
			+ "var table = $('<table>');" 
			+ "table.attr('class', 'sparkline_wrapper_table');" 
			+ "var row = $('<tr>');" 
			+ "sparklineImgTD = $('<td>');" 
			+ "sparklineImgTD.attr('id', '" + sparklineID + "_img');" 
			+ "sparklineImgTD.attr('width', '65');" 
//			+ "sparklineImgTD.attr('align', 'right');" 
			+ "sparklineImgTD.attr('class', '" + VISUALIZATION_STYLE_CLASS + "');" 
			+ "row.append(sparklineImgTD);" 
			+  "var row2 = $('<tr>');"
			+ "var sparklineNumberTD = $('<td>');" 
//			+ "sparklineNumberTD.attr('width', '30');" 
//			+ "sparklineNumberTD.attr('align', 'right');" 
			+ "sparklineNumberTD.attr('class', 'sparkline_number');" 
			+ "sparklineNumberTD.css('text-align', 'center');"
			+ "row2.append(sparklineNumberTD);"
			+  "var row3 = $('<tr>');"
			+ "var sparklineTextTD = $('<td>');" 
//			+ "sparklineTextTD.attr('width', '450');" 
			+ "sparklineTextTD.attr('class', 'sparkline_text');"
			+ "row3.append(sparklineTextTD);" 
			+ "table.append(row);"
			+ "table.append(row2);"
			+ "table.append(row3);"				
			+ "table.prependTo('#" + sparklineID + "');\n";
		
		return "$(document).ready(function() {" 
				+ "var sparklineImgTD; " 
				/*
				 * This is a nuclear option (creating the container in which everything goes)
				 * the only reason this will be ever used is the API user never submitted a 
				 * container ID in which everything goes. The alternative was to let the 
				 * vis not appear in the calling page at all. So now atleast vis appears but 
				 * appended at the bottom of the body.
				 * */
				+ "if ($('#" + visContainerID + "').length === 0) {" 
				+ "	$('<div/>', {'id': '" + visContainerID + "'" 
				+ "     }).appendTo('body');" 
				+ "}" 
				+ "if ($('#" + sparklineID + "').length === 0) {" 
				+ "$('<div/>', {'id': '" + sparklineID + "'," 
				+ "'class': '" + VISUALIZATION_STYLE_CLASS + "'" 
				+ "}).prependTo('#" + visContainerID + "');" 
				+ sparklineTableWrapper 
				+ "}" 
				+ "drawUniqueCoauthorCountVisualization(sparklineImgTD);" 
				+ "});" 
				+ "</script>\n";
	}

	private String getVisualizationContextCode(String visMode) {

		String visualizationContextCode = "";
		if (VisualizationFrameworkConstants.SHORT_SPARKLINE_VIS_MODE.equalsIgnoreCase(visMode)) {
			visualizationContextCode = generateShortVisContext();
		} else {
			visualizationContextCode = generateFullVisContext();
		}

		log.debug(visualizationContextCode);

		return visualizationContextCode;
	}
	
	private String generateFullVisContext() {
		
		StringBuilder divContextCode = new StringBuilder();
		
		String csvDownloadURLHref = ""; 
		
		if (yearToUniqueCoauthors.size() > 0) {
			
			try {
				if (getCSVDownloadURL() != null) {
					
					csvDownloadURLHref = "Download data as <a href='" 
											+ getCSVDownloadURL() + "'>.csv</a> file.<br />";
					sparklineData.setDownloadDataLink(getCSVDownloadURL());
					
				} else {
					csvDownloadURLHref = "";
				}

			} catch (UnsupportedEncodingException e) {
				csvDownloadURLHref = "";
			}
			
		} else {
			csvDownloadURLHref = "No data available to export.<br />";
		}
		
		String tableCode = generateDataTable();
		
		divContextCode.append("<p>" + tableCode + csvDownloadURLHref + "</p>");
		
		sparklineData.setTable(tableCode);
		
		return divContextCode.toString();
	}

	private String getCSVDownloadURL() throws UnsupportedEncodingException {
		
		if (yearToUniqueCoauthors.size() > 0) {
			
		String secondaryContextPath = "";
		if (!contextPath.contains(VisualizationFrameworkConstants.VISUALIZATION_URL_PREFIX)) {
			secondaryContextPath = VisualizationFrameworkConstants.VISUALIZATION_URL_PREFIX;
		}
			
			
		String downloadURL = contextPath
			 + secondaryContextPath
			 + "?" + VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY 
			 + "=" + URLEncoder.encode(individualURI, 
					 				   VisualizationController.URL_ENCODING_SCHEME).toString() 
			 + "&" + VisualizationFrameworkConstants.VIS_TYPE_KEY 
			 + "=" + URLEncoder.encode(VisualizationFrameworkConstants
					 						.COAUTHORSHIP_VIS, 
					 				   VisualizationController.URL_ENCODING_SCHEME).toString() 
			 + "&" + VisualizationFrameworkConstants.VIS_MODE_KEY
			 + "=" + URLEncoder.encode("sparkline", 
					 				   VisualizationController.URL_ENCODING_SCHEME).toString()
			 + "&" + VisualizationFrameworkConstants.RENDER_MODE_KEY 
			 + "=" + URLEncoder.encode(VisualizationFrameworkConstants.DATA_RENDER_MODE, 
	 				 				   VisualizationController.URL_ENCODING_SCHEME).toString();
		
			return downloadURL;
		} else {
			return null;
		}
	}
	
	private String generateShortVisContext() {

		StringBuilder divContextCode = new StringBuilder();
		
		try {
		
		String fullTimelineLink;
		if (yearToUniqueCoauthors.size() > 0) {
			
			String secondaryContextPath = "";
			if (!contextPath.contains(VisualizationFrameworkConstants.VISUALIZATION_URL_PREFIX)) {
				secondaryContextPath = VisualizationFrameworkConstants.VISUALIZATION_URL_PREFIX;
			}
			
			String fullTimelineNetworkURL = contextPath
							+ secondaryContextPath
							+ "?" 
							+ VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY 
							+ "=" + URLEncoder.encode(individualURI, 
					 				 VisualizationController.URL_ENCODING_SCHEME).toString()
					 	    + "&"
		 				    + VisualizationFrameworkConstants.VIS_TYPE_KEY 
							+ "=" + URLEncoder.encode("person_level", 
					 				 VisualizationController.URL_ENCODING_SCHEME).toString()
					 	    + "&"
		 				    + VisualizationFrameworkConstants.VIS_CONTAINER_KEY 
							+ "=" + URLEncoder.encode("ego_sparkline", 
					 				 VisualizationController.URL_ENCODING_SCHEME).toString()
		 				    + "&"
		 				    + VisualizationFrameworkConstants.RENDER_MODE_KEY
							+ "=" + URLEncoder.encode(
											VisualizationFrameworkConstants
													.STANDALONE_RENDER_MODE,
											VisualizationController.URL_ENCODING_SCHEME).toString();
			
			fullTimelineLink = "<a href='" + fullTimelineNetworkURL 
									+ "'>View full timeline and co-author network.</a>";
			
			sparklineData.setFullTimelineNetworkLink(fullTimelineNetworkURL);
			
		} else {
			fullTimelineLink = "No data available to render full timeline.<br />";
		}
		
		divContextCode.append("<p>" + fullTimelineLink + "</p>");
		
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}
		
		return divContextCode.toString();
	}
	
	
	private String generateDataTable() {
		
		StringBuilder dataTable = new StringBuilder();
		
		dataTable.append("<table id='sparkline_data_table'>" 
							+ "<caption>Unique Co-Authors per year</caption>" 
							+ "<thead>" 
							+ "<tr>" 
							+ "<th>Year</th>" 
							+ "<th>Count</th>" 
							+ "</tr>" 
							+ "</thead>" 
							+ "<tbody>");
		
		for (Entry<String, Set<Node>> currentEntry : yearToUniqueCoauthors.entrySet()) {
			dataTable.append("<tr>" 
								+ "<td>" + currentEntry.getKey() + "</td>" 
								+ "<td>" + currentEntry.getValue().size() + "</td>" 
								+ "</tr>");
		}
										
		dataTable.append("</tbody>\n </table>\n");
		
		return dataTable.toString();
	}

	public SparklineData getValueObjectContainer() {
		return sparklineData;
	}
}
