/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

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


public class PersonPublicationCountVisCodeGenerator {

	/*
	 * There are 2 modes of sparkline that are available via this visualization.
	 * 		1. Short Sparkline - This sparkline will render all the data points (or sparks),
	 * 			which in this case are the publications over the years, from the last 10 years.
	 *
	 * 		2. Full Sparkline - This sparkline will render all the data points (or sparks)
	 * 			spanning the career of the person & last 10 years at the minimum, in case if
	 * 			the person started his career in the last 10 yeras.
	 * */

	private static final String DEFAULT_VIS_CONTAINER_DIV_ID = "pub_count_vis_container";

	private Map<String, Integer> yearToPublicationCount;

	private Log log;

	private String individualURI;

	private SparklineData sparklineParameterVO;

	public PersonPublicationCountVisCodeGenerator(String individualURIParam,
									  String visMode,
									  String visContainer,
									  Map<String, Integer> yearToPublicationCount,
									  Log log) {

		this.individualURI = individualURIParam;

		this.yearToPublicationCount = yearToPublicationCount;

		this.log = log;

		this.sparklineParameterVO = setupSparklineParameters(visMode, visContainer);

	}

	/**
	 * This method is used to setup parameters for the sparkline value object. These parameters
	 * will be used in the template to construct the actual html/javascript code.
	 * @param visMode Visualization mode
	 * @param providedVisContainerID container id
	 */
	private SparklineData setupSparklineParameters(String visMode,
			  							  String providedVisContainerID) {

		SparklineData sparklineData = new SparklineData();
		sparklineData.setYearToActivityCount(yearToPublicationCount);


		int numOfYearsToBeRendered = 0;

		/*
		 * It was decided that to prevent downward curve that happens if there are no publications
		 * in the current year seems a bit harsh, so we consider only publications from the last 10
		 * complete years.
		 * */
		int currentYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
		int shortSparkMinYear = currentYear
									- VisConstants.MINIMUM_YEARS_CONSIDERED_FOR_SPARKLINE
									+ 1;

    	/*
    	 * This is required because when deciding the range of years over which the vis
    	 * was rendered we dont want to be influenced by the "DEFAULT_PUBLICATION_YEAR".
    	 * */
		Set<String> publishedYears = new HashSet<String>(yearToPublicationCount.keySet());
    	publishedYears.remove(VOConstants.DEFAULT_PUBLICATION_YEAR);

		/*
		 * We are setting the default value of minPublishedYear to be 10 years before
		 * the current year (which is suitably represented by the shortSparkMinYear),
		 * this in case we run into invalid set of published years.
		 * */
		int minPublishedYear = shortSparkMinYear;

		String visContainerID = null;

		if (yearToPublicationCount.size() > 0) {
			try {
				minPublishedYear = Integer.parseInt(Collections.min(publishedYears));
			} catch (NoSuchElementException | NumberFormatException e1) {
				log.debug("vis: " + e1.getMessage() + " error occurred for "
								+ yearToPublicationCount.toString());
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

		sparklineData.setNumOfYearsToBeRendered(numOfYearsToBeRendered);

		int publicationCounter = 0;

		/*
		 * For the purpose of this visualization I have come up with a term "Sparks" which
		 * essentially means data points.
		 * Sparks that will be rendered in full mode will always be the one's which have any year
		 * associated with it. Hence.
		 * */
		int renderedFullSparks = 0;

		List<YearToEntityCountDataElement> yearToPublicationCountDataTable =
				new ArrayList<YearToEntityCountDataElement>();

		for (int publicationYear = minPubYearConsidered;
					publicationYear <= currentYear;
					publicationYear++) {

				String stringPublishedYear = String.valueOf(publicationYear);
				Integer currentPublications = yearToPublicationCount.get(stringPublishedYear);

				if (currentPublications == null) {
					currentPublications = 0;
				}

				yearToPublicationCountDataTable.add(new YearToEntityCountDataElement(
															publicationCounter,
															stringPublishedYear,
															currentPublications));

				/*
				 * Sparks that will be rendered will always be the one's which has
				 * any year associated with it. Hence.
				 * */
				renderedFullSparks += currentPublications;
				publicationCounter++;
		}

		sparklineData.setYearToEntityCountDataTable(yearToPublicationCountDataTable);
		sparklineData.setRenderedSparks(renderedFullSparks);

		/*
		 * Total publications will also consider publications that have no year associated with
		 * it. Hence.
		 * */
		Integer unknownYearPublications = 0;
		if (yearToPublicationCount.get(VOConstants.DEFAULT_PUBLICATION_YEAR) != null) {
			unknownYearPublications = yearToPublicationCount
											.get(VOConstants.DEFAULT_PUBLICATION_YEAR);
		}

		sparklineData.setUnknownYearPublications(unknownYearPublications);

		if (providedVisContainerID != null) {
			visContainerID = providedVisContainerID;
		} else {
			visContainerID = DEFAULT_VIS_CONTAINER_DIV_ID;
		}

		sparklineData.setVisContainerDivID(visContainerID);

		/*
		 * By default these represents the range of the rendered sparks. Only in case of
		 * "short" sparkline mode we will set the Earliest RenderedPublication year to
		 * "currentYear - 10".
		 * */
		sparklineData.setEarliestYearConsidered(minPubYearConsidered);
		sparklineData.setEarliestRenderedPublicationYear(minPublishedYear);
		sparklineData.setLatestRenderedPublicationYear(currentYear);

		if (yearToPublicationCount.size() > 0) {

			sparklineData.setFullTimelineNetworkLink(
					UtilityFunctions.getCollaboratorshipNetworkLink(
						individualURI,
						VisualizationFrameworkConstants.PERSON_LEVEL_VIS,
						VisualizationFrameworkConstants.COAUTHOR_VIS_MODE));

			sparklineData.setDownloadDataLink(
					UtilityFunctions.getCSVDownloadURL(
									individualURI,
									VisualizationFrameworkConstants.PERSON_PUBLICATION_COUNT_VIS,
									""));
		}

		/*
		 * The Full Sparkline will be rendered by default. Only if the url has specific mention of
		 * SHORT_SPARKLINE_MODE_URL_HANDLE then we render the short sparkline and not otherwise.
		 * */
		if (VisualizationFrameworkConstants.SHORT_SPARKLINE_VIS_MODE.equalsIgnoreCase(visMode)) {

			sparklineData.setEarliestRenderedPublicationYear(shortSparkMinYear);
			sparklineData.setShortVisMode(true);

		} else {
			sparklineData.setShortVisMode(false);
		}

		return sparklineData;
	}

	public SparklineData getValueObjectContainer() {
		return this.sparklineParameterVO;
	}
}
