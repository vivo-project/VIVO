/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.persongrantcount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.logging.Log;

import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.YearToEntityCountDataElement;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;


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

	private static final String DEFAULT_VIS_CONTAINER_DIV_ID = "grant_count_vis_container";

	private Map<String, Integer> yearToGrantCount;

	private Log log;

	private SparklineData sparklineParameterVO;

	private String individualURI;

	public PersonGrantCountVisCodeGenerator(String individualURIParam,
			String visMode, String visContainer, Map<String, Integer> yearToGrantCount,
			Log log) {

		this.individualURI = individualURIParam;

		this.yearToGrantCount = yearToGrantCount;

		this.log = log;

		this.sparklineParameterVO = setupSparklineParameters(visMode, visContainer);

	}

	/**
	 * This method is used to setup parameters for the sparkline value object. These parameters
	 * will be used in the template to construct the actual html/javascript code.
	 * @param visMode Visualisation mode
	 * @param providedVisContainerID Container ID
	 */
	private SparklineData setupSparklineParameters(String visMode,
			  							  String providedVisContainerID) {

		SparklineData sparklineData = new SparklineData();
		sparklineData.setYearToActivityCount(yearToGrantCount);

		int numOfYearsToBeRendered = 0;

		/*
		 * It was decided that to prevent downward curve that happens if there are no publications
		 * in the current year seems a bit harsh, so we consider only publications from the last 10
		 * complete years.
		 * */
		int currentYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
		int shortSparkMinYear = currentYear
				- VisConstants.MINIMUM_YEARS_CONSIDERED_FOR_SPARKLINE + 1;

		/*
		 * This is required because when deciding the range of years over which
		 * the vis was rendered we dont want to be influenced by the
		 * "DEFAULT_GRANT_YEAR".
		 */
		Set<String> grantYears = new HashSet<String>(yearToGrantCount.keySet());
		grantYears.remove(VOConstants.DEFAULT_GRANT_YEAR);

		/*
		 * We are setting the default value of minGrantYear to be 10 years
		 * before the current year (which is suitably represented by the
		 * shortSparkMinYear), this in case we run into invalid set of grant
		 * years.
		 */
		int minGrantYear = shortSparkMinYear;

		String visContainerID = null;

		if (yearToGrantCount.size() > 0) {
			try {
				minGrantYear = Integer.parseInt(Collections
						.min(grantYears));
			} catch (NoSuchElementException | NumberFormatException e1) {
				log.debug("vis: " + e1.getMessage() + " error occurred for "
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

		int grantCounter = 0;

		/*
		 * For the purpose of this visualization I have come up with a term
		 * "Sparks" which essentially means data points. Sparks that will be
		 * rendered in full mode will always be the one's which have any year
		 * associated with it. Hence.
		 */
		int renderedFullSparks = 0;

		List<YearToEntityCountDataElement> yearToGrantCountDataTable =
					new ArrayList<YearToEntityCountDataElement>();

		for (int grantYear = minGrantYearConsidered; grantYear <= currentYear; grantYear++) {

			String stringInvestigatedYear = String.valueOf(grantYear);
			Integer currentGrants = yearToGrantCount.get(stringInvestigatedYear);

			if (currentGrants == null) {
				currentGrants = 0;
			}

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

		sparklineData.setYearToEntityCountDataTable(yearToGrantCountDataTable);
		sparklineData.setRenderedSparks(renderedFullSparks);

		/*
		 * Total grants will also consider grants that have no year
		 * associated with it. Hence.
		 */
		Integer unknownYearGrants = 0;
		if (yearToGrantCount.get(VOConstants.DEFAULT_GRANT_YEAR) != null) {
			unknownYearGrants = yearToGrantCount
					.get(VOConstants.DEFAULT_GRANT_YEAR);
		}

		sparklineData.setUnknownYearGrants(unknownYearGrants);

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
		if (VisualizationFrameworkConstants.SHORT_SPARKLINE_VIS_MODE
				.equalsIgnoreCase(visMode)) {

			sparklineData.setEarliestRenderedGrantYear(shortSparkMinYear);
			sparklineData.setShortVisMode(true);

		} else {
			sparklineData.setShortVisMode(false);
		}

		if (yearToGrantCount.size() > 0) {

			sparklineData.setFullTimelineNetworkLink(
					UtilityFunctions.getCollaboratorshipNetworkLink(individualURI,
						VisualizationFrameworkConstants.PERSON_LEVEL_VIS,
						VisualizationFrameworkConstants.COPI_VIS_MODE));

			sparklineData.setDownloadDataLink(
					UtilityFunctions.getCSVDownloadURL(
							individualURI,
							VisualizationFrameworkConstants.PERSON_GRANT_COUNT_VIS,
							""));
		}
		return sparklineData;
	}

	public SparklineData getValueObjectContainer() {
		return this.sparklineParameterVO;
	}
}
