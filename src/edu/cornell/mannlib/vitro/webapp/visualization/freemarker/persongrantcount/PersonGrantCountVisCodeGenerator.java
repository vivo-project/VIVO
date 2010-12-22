/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.persongrantcount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.logging.Log;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Grant;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.YearToEntityCountDataElement;


@SuppressWarnings("serial")
public class PersonGrantCountVisCodeGenerator {
	
	/*
	 * There are 2 modes of sparkline that are available via this visualization.
	 * 		1. Short Sparkline - This sparkline will render all the data points (or sparks),
	 * 			which in this case are the grants over the years, from the last 10 years.
	 * 
	 * 		2. Full Sparkline - This sparkline will render all the data points (or sparks) 
	 * 			spanning the career of the person & last 10 years at the minimum, in case if
	 * 			the person started his career in the last 10 yeras.
	 * */
	
	private static final Map<String, String> VIS_DIV_NAMES = new HashMap<String, String>() { {

		put("SHORT_SPARK", "grant_count_short_sparkline_vis");
		put("FULL_SPARK", "grant_count_full_sparkline_vis");

	} };
	
	private static final String VISUALIZATION_STYLE_CLASS = "sparkline_style";
	
	private static final String DEFAULT_VIS_CONTAINER_DIV_ID = "grant_count_vis_container";
	
	private Map<String, Integer> yearToGrantCount;

	private Log log;

	private SparklineData sparklineData;

	private String individualURI;
	
	public PersonGrantCountVisCodeGenerator(String individualURIParam,
			String visMode, String visContainer, Set<Grant> piGrants,
			Map<String, Integer> yearToGrantCount,
			Log log) {

		this.individualURI = individualURIParam;

		this.yearToGrantCount = yearToGrantCount;
		this.sparklineData = new SparklineData();

		sparklineData.setYearToActivityCount(yearToGrantCount);

		this.log = log;

		generateVisualizationCode(visMode, visContainer, piGrants);
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
	 * @param piGrants
	 */
	private void generateVisualizationCode(String visMode,
										   String visContainer, 
										   Set<Grant> piGrants) {
		
    	sparklineData.setSparklineContent(getMainVisualizationCode(piGrants, 
																   visMode, 
																   visContainer));
    	
    	
    	sparklineData.setSparklineContext(getVisualizationContextCode(visMode));
    	
	}
	
	private String getMainVisualizationCode(Set<Grant> piGrants,
			String visMode, String providedVisContainerID) {

		int numOfYearsToBeRendered = 0;
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int shortSparkMinYear = currentYear
				- VisConstants.MINIMUM_YEARS_CONSIDERED_FOR_SPARKLINE + 1;

		/*
		 * This is required because when deciding the range of years over which
		 * the vis was rendered we dont want to be influenced by the
		 * "DEFAULT_GRANT_YEAR".
		 */
		Set<String> grantYears = new HashSet<String>(yearToGrantCount
				.keySet());
		grantYears.remove(VOConstants.DEFAULT_GRANT_YEAR);

		/*
		 * We are setting the default value of minGrantYear to be 10 years
		 * before the current year (which is suitably represented by the
		 * shortSparkMinYear), this in case we run into invalid set of grant
		 * years.
		 */
		int minGrantYear = shortSparkMinYear;

		String visContainerID = null;

		StringBuilder visualizationCode = new StringBuilder();

		if (yearToGrantCount.size() > 0) {
			try {
				minGrantYear = Integer.parseInt(Collections
						.min(grantYears));
			} catch (NoSuchElementException e1) {
				log.debug("vis: " + e1.getMessage() + " error occurred for "
						+ yearToGrantCount.toString());
			} catch (NumberFormatException e2) {
				log.debug("vis: " + e2.getMessage() + " error occurred for "
						+ yearToGrantCount.toString());
			}
		}

		int minGrantYearConsidered = 0;

		/*
		 * There might be a case that the author investigated his first grant
		 * within the last 10 years but we want to make sure that the sparkline
		 * is representative of at least the last 10 years, so we will set the
		 * minGrantYearConsidered to "currentYear - 10" which is also given by
		 * "shortSparkMinYear".
		 */
		if (minGrantYear > shortSparkMinYear) {
			minGrantYearConsidered = shortSparkMinYear;
		} else {
			minGrantYearConsidered = minGrantYear;
		}

		numOfYearsToBeRendered = currentYear - minGrantYearConsidered + 1;

		sparklineData.setNumOfYearsToBeRendered(numOfYearsToBeRendered);

		visualizationCode.append("<style type='text/css'>" + "."
				+ VISUALIZATION_STYLE_CLASS + " table{" + "		margin: 0;"
				+ "  		padding: 0;" + "  		width: auto;"
				+ "  		border-collapse: collapse;" + "    	border-spacing: 0;"
				+ "    	vertical-align: inherit;" + "}"
				+ "table.sparkline_wrapper_table td, th {"
				+ "	vertical-align: bottom;" + "}" + ".vis_link a{"
				+ "	padding-top: 5px;" + "}"
				+ "td.sparkline_number { text-align:right; "
				+ "padding-right:5px; }"
				+ "td.sparkline_text   {text-align:left;}"
				+ ".incomplete-data-holder {" + "" + "}" + "</style>\n");

		visualizationCode.append("<script type=\"text/javascript\">\n"
				+ "function drawGrantCountVisualization(providedSparklineImgTD) "
				+ "{\n" + "var data = new google.visualization.DataTable();\n"
				+ "data.addColumn('string', 'Year');\n"
				+ "data.addColumn('number', 'Publications');\n"
				+ "data.addRows(" + numOfYearsToBeRendered + ");\n");

		int grantCounter = 0;

		/*
		 * For the purpose of this visualization I have come up with a term
		 * "Sparks" which essentially means data points. Sparks that will be
		 * rendered in full mode will always be the one's which have any year
		 * associated with it. Hence.
		 */
		int renderedFullSparks = 0;

		List<YearToEntityCountDataElement> yearToGrantCountDataTable = new ArrayList<YearToEntityCountDataElement>();

		for (int grantYear = minGrantYearConsidered; grantYear <= currentYear; grantYear++) {

			String stringInvestigatedYear = String.valueOf(grantYear);
			Integer currentGrants = yearToGrantCount.get(stringInvestigatedYear);

			if (currentGrants == null) {
				currentGrants = 0;
			}

			visualizationCode.append("data.setValue(" + grantCounter
					+ ", 0, '" + stringInvestigatedYear + "');\n");

			visualizationCode.append("data.setValue(" + grantCounter
					+ ", 1, " + currentGrants + ");\n");

			yearToGrantCountDataTable
					.add(new YearToEntityCountDataElement(
							grantCounter, stringInvestigatedYear,
							currentGrants));

			/*
			 * Sparks that will be rendered will always be the one's which has
			 * any year associated with it. Hence.
			 */
			renderedFullSparks += currentGrants;
			grantCounter++;

		}

		sparklineData
				.setYearToEntityCountDataTable(yearToGrantCountDataTable);

		sparklineData.setRenderedSparks(renderedFullSparks);

		/*
		 * Total grants will also consider publications that have no year
		 * associated with it. Hence.
		 */
		Integer unknownYearGrants = 0;
		if (yearToGrantCount.get(VOConstants.DEFAULT_GRANT_YEAR) != null) {
			unknownYearGrants = yearToGrantCount
					.get(VOConstants.DEFAULT_GRANT_YEAR);
		}

		sparklineData.setUnknownYearGrants(unknownYearGrants);

		String sparklineDisplayOptions = "{width: 65, height: 30, showAxisLines: false, "
				+ "showValueLabels: false, labelPosition: 'none'}";

		if (providedVisContainerID != null) {
			visContainerID = providedVisContainerID;
		} else {
			visContainerID = DEFAULT_VIS_CONTAINER_DIV_ID;
		}

		sparklineData.setVisContainerDivID(visContainerID);

		/*
		 * By default these represents the range of the rendered sparks. Only in
		 * case of "short" sparkline mode we will set the Earliest
		 * RenderedGrant year to "currentYear - 10".
		 */
		sparklineData.setEarliestYearConsidered(minGrantYearConsidered);
		sparklineData.setEarliestRenderedGrantYear(minGrantYear);
		sparklineData.setLatestRenderedGrantYear(currentYear);

		/*
		 * The Full Sparkline will be rendered by default. Only if the url has
		 * specific mention of SHORT_SPARKLINE_MODE_URL_HANDLE then we render
		 * the short sparkline and not otherwise.
		 */

		/*
		 * Since building StringBuilder objects (which is being used to store
		 * the vis code) is essentially a side-effecting process, we have both
		 * the activators method as side- effecting. They both side-effect
		 * "visualizationCode"
		 */
		if (VisualizationFrameworkConstants.SHORT_SPARKLINE_VIS_MODE
				.equalsIgnoreCase(visMode)) {

			sparklineData.setEarliestRenderedGrantYear(shortSparkMinYear);

			sparklineData.setShortVisMode(true);

			generateShortSparklineVisualizationContent(currentYear,
					shortSparkMinYear, visContainerID, visualizationCode,
					unknownYearGrants, sparklineDisplayOptions);
		} else {

			sparklineData.setShortVisMode(false);
			generateFullSparklineVisualizationContent(currentYear,
					minGrantYearConsidered, visContainerID, visualizationCode,
					unknownYearGrants, renderedFullSparks,
					sparklineDisplayOptions);
		}
		log.debug(visualizationCode);
		return visualizationCode.toString();
	}
	
	private void generateShortSparklineVisualizationContent(int currentYear,
			int shortSparkMinYear, String visContainerID,
			StringBuilder visualizationCode, int unknownYearGrants,
			String sparklineDisplayOptions) {

		/*
		 * Create a view of the data containing only the column pertaining to
		 * grant count.
		 */
		visualizationCode.append("var shortSparklineView = "
				+ "new google.visualization.DataView(data);\n"
				+ "shortSparklineView.setColumns([1]);\n");

		/*
		 * For the short view we only want the last 10 year's view of
		 * grant count, hence we filter the data we actually want to use
		 * for render.
		 */
		visualizationCode.append("shortSparklineView.setRows("
				+ "data.getFilteredRows([{column: 0, " + "minValue: '"
				+ shortSparkMinYear + "', " + "maxValue: '" + currentYear
				+ "'}])" + ");\n");

		/*
		 * Create the vis object and draw it in the div pertaining to
		 * short-sparkline.
		 */
		visualizationCode
				.append("var short_spark = new google.visualization.ImageSparkLine("
						+ "providedSparklineImgTD[0]"
						+ ");\n"
						+ "short_spark.draw(shortSparklineView, "
						+ sparklineDisplayOptions + ");\n");

		/*
		 * We want to display how many grant counts were considered, so
		 * this is used to calculate this.
		 */
		visualizationCode
				.append("var shortSparkRows = shortSparklineView.getViewRows();\n"
						+ "var renderedShortSparks = 0;\n"
						+ "$.each(shortSparkRows, function(index, value) {"
						+ "renderedShortSparks += data.getValue(value, 1);"
						+ "});\n");

		/*
		 * Generate the text introducing the vis.
		 */

		String imcompleteDataText = "This information is based solely on grants which "
				+ "have been loaded into the VIVO system. "
				+ "This may only be a small sample of the person\\'s "
				+ "total work.";

		visualizationCode.append("$('#" + VIS_DIV_NAMES.get("SHORT_SPARK")
				+ " td.sparkline_number').text("
				+ "parseInt(renderedShortSparks) " + "+ parseInt("
				+ unknownYearGrants + "));");

		visualizationCode.append("var shortSparksText = ''"
				+ "+ ' grant(s) within the last 10 years "
				+ "<span class=\"incomplete-data-holder\" title=\""
				+ imcompleteDataText + "\">incomplete data</span>'" + "+ '';"
				+ "$('#" + VIS_DIV_NAMES.get("SHORT_SPARK") + " "
				+ "td.sparkline_text').html(shortSparksText);");

		visualizationCode.append("}\n ");

		/*
		 * Generate the code that will activate the visualization. It takes care
		 * of creating div elements to hold the actual sparkline image and then
		 * calling the drawGrantCountVisualization function.
		 */
		visualizationCode.append(generateVisualizationActivator(VIS_DIV_NAMES
				.get("SHORT_SPARK"), visContainerID));

	}
	
	private void generateFullSparklineVisualizationContent(
			int currentYear, 
			int minGrantYearConsidered, 
			String visContainerID, 
			StringBuilder visualizationCode,
			int unknownYearGrants,
			int renderedFullSparks,
			String sparklineDisplayOptions) {
		
		String csvDownloadURLHref = ""; 
		
		if (getCSVDownloadURL() != null) {
			
			csvDownloadURLHref = "<a href=\"" + getCSVDownloadURL() 
										+ "\" class=\"inline_href\">(.CSV File)</a>";
			
		} else {
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
									+ " td.sparkline_number').text('" + (renderedFullSparks 
									+ unknownYearGrants) + "');");
		
		visualizationCode.append("var allSparksText = ''" 
									+ "+ ' grant(s) '" 
									+ "+ ' from " 
									+ "<span class=\"sparkline_range\">" 
									+ "" + minGrantYearConsidered + " to " + currentYear + "" 
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
				+ "sparklineImgTD.attr('align', 'right');" 
				+ "sparklineImgTD.attr('class', '" + VISUALIZATION_STYLE_CLASS + "');" 
				+ "row.append(sparklineImgTD);" 
				+ "var sparklineNumberTD = $('<td>');" 
				+ "sparklineNumberTD.attr('width', '30');" 
				+ "sparklineNumberTD.attr('align', 'right');" 
				+ "sparklineNumberTD.attr('class', 'sparkline_number');" 
				+ "row.append(sparklineNumberTD);" 
				+ "var sparklineTextTD = $('<td>');" 
				+ "sparklineTextTD.attr('width', '450');" 
				+ "sparklineTextTD.attr('class', 'sparkline_text');" 
				+ "row.append(sparklineTextTD);" 
				+ "table.append(row);" 
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
				+ "drawPubCountVisualization(sparklineImgTD);" 
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
		
		if (yearToGrantCount.size() > 0) {
			
			if (getCSVDownloadURL() != null) {
				
				csvDownloadURLHref = "Download data as <a href='" 
										+ getCSVDownloadURL() + "'>.csv</a> file.<br />";
				sparklineData.setDownloadDataLink(getCSVDownloadURL());
				
			} else {
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
	

	private String getCSVDownloadURL() {
		
		if (yearToGrantCount.size() > 0) {
			
			ParamMap CSVDownloadURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
										 individualURI,
										 VisualizationFrameworkConstants.VIS_TYPE_KEY,
										 VisualizationFrameworkConstants.PERSON_GRANT_COUNT_VIS);

			return UrlBuilder.getUrl(VisualizationFrameworkConstants.DATA_VISUALIZATION_SERVICE_URL_PREFIX,
					 CSVDownloadURLParams);

		} else {
			return null;
		}
	}
	
	
	private String generateShortVisContext() {

		StringBuilder divContextCode = new StringBuilder();
		
		String fullTimelineLink;
		if (yearToGrantCount.size() > 0) {
			
			ParamMap fullTimelineNetworkURLParams = new ParamMap(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
					 individualURI,
					 VisualizationFrameworkConstants.VIS_TYPE_KEY,
					 VisualizationFrameworkConstants.PERSON_LEVEL_VIS);

			String fullTimelineNetworkURL = UrlBuilder.getUrl(
											VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
											fullTimelineNetworkURLParams);

			fullTimelineLink = "<a href='" + fullTimelineNetworkURL + "'>View all VIVO " 
									+ "grants and corresponding co-pi network.</a>";
			
			sparklineData.setFullTimelineNetworkLink(fullTimelineNetworkURL);
			
			
			
		} else {
			fullTimelineLink = "No data available to render full timeline.<br />";
		}
		
		divContextCode.append("<span class=\"vis_link\">" + fullTimelineLink + "</span>");
		return divContextCode.toString();
	}
	
	private String generateDataTable() {
		
		String csvDownloadURLHref = ""; 
		
		if (getCSVDownloadURL() != null) {
			csvDownloadURLHref = "<a href=\"" + getCSVDownloadURL() + "\">(.CSV File)</a>";
		} else {
			csvDownloadURLHref = "";
		}
		
		StringBuilder dataTable = new StringBuilder();
		
		dataTable.append("<table id='sparkline_data_table'>" 
							+ "<caption>Grants per year " + csvDownloadURLHref + "</caption>" 
							+ "<thead>" 
							+ "<tr>" 
							+ "<th>Year</th>" 
							+ "<th>Grants</th>" 
							+ "</tr>" 
							+ "</thead>" 
							+ "<tbody>");
		
		for (Entry<String, Integer> currentEntry : yearToGrantCount.entrySet()) {
			dataTable.append("<tr>" 
								+ "<td>" + currentEntry.getKey() + "</td>" 
								+ "<td>" + currentEntry.getValue() + "</td>" 
								+ "</tr>");
		}
										
		dataTable.append("</tbody>\n </table>\n");
		
		return dataTable.toString();
	}
	
	public SparklineData getValueObjectContainer() {
		return sparklineData;
	}
}
