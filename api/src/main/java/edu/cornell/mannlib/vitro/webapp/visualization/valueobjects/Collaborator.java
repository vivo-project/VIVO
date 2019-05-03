/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UniqueIDGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;

/**
 *
 * This stores collbaorator's information involved in ego-centric networks & represents
 * a collaborator's activities.
 *
 * @author cdtank
 */
public class Collaborator extends Individual {

	private int collaboratorID;
	private Map<String, Integer> yearToActivityCount;

	private Set<Activity> activities = new HashSet<Activity>();

	public Collaborator(String collaboratorURI,
				UniqueIDGenerator uniqueIDGenerator) {
		super(collaboratorURI);
		collaboratorID = uniqueIDGenerator.getNextNumericID();
	}

	public int getCollaboratorID() {
		return collaboratorID;
	}

	public String getCollaboratorURI() {
		return this.getIndividualURI();
	}

	public String getCollaboratorName() {
		return this.getIndividualLabel();
	}

	public void setCollaboratorName(String collaboratorName) {
		this.setIndividualLabel(collaboratorName);
	}

	public Set<Activity> getCollaboratorActivities() {
		return activities;
	}

	public int getNumOfActivities() {
		return activities.size();
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}

	public Map<String, Integer> getYearToActivityCount() {
		if (yearToActivityCount == null) {
			yearToActivityCount = UtilityFunctions.getYearToActivityCount(activities);
		}
		return yearToActivityCount;
	}

	/*
	 * getEarliest, Latest & Unknown Collaborator YearCount should only be used after
	 * the parsing of the entire sparql is done. Else it will give results based on
	 * incomplete dataset.
	 * */
	@SuppressWarnings("serial")
	public Map<String, Integer> getEarliestActivityYearCount() {

		/*
		 * We do not want to consider the default activity year when we are checking
		 * for the min or max activity year.
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(this.getYearToActivityCount()
																	.keySet());
		yearsToBeConsidered.remove(VOConstants.DEFAULT_ACTIVITY_YEAR);

		/*
		 * There can be a case when the only activity the collaborator has no attached year to it
		 * so essentially an "Unknown". In that case Collections.max or min will throw an
		 * NoSuchElementException.
		 *
		 * If there is no maximum year available then we should imply so by returning a "null".
		 * */
		if (yearsToBeConsidered.size() > 0) {
			final String earliestYear = Collections.min(yearsToBeConsidered);
			final Integer earliestYearActivityCount = this.getYearToActivityCount()
															.get(earliestYear);

			return new HashMap<String, Integer>() { {
				put(earliestYear, earliestYearActivityCount);
			} };
		} else {
			return null;
		}
	}

	@SuppressWarnings("serial")
	public Map<String, Integer> getLatestActivityYearCount() {

		/*
		 * We do not want to consider the default Activity year when we are checking
		 * for the min or max Activity year.
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(this.getYearToActivityCount()
																	.keySet());
		yearsToBeConsidered.remove(VOConstants.DEFAULT_ACTIVITY_YEAR);

		/*
		 * There can be a case when the only Activity the author has no attached year to it
		 * so essentially an "Unknown". In that case Collections.max or min will throw an
		 * NoSuchElementException.
		 *
		 * If there is no maximum year available then we should imply so by returning a "null".
		 * */
		if (yearsToBeConsidered.size() > 0) {
			final String latestYear = Collections.max(yearsToBeConsidered);
			final Integer latestYearActivityCount = this.getYearToActivityCount().get(latestYear);

			return new HashMap<String, Integer>() { {
				put(latestYear, latestYearActivityCount);
			} };
		} else {
			return null;
		}
	}

	public Integer getUnknownActivityYearCount() {

		Integer unknownYearActivityCount = this.getYearToActivityCount()
											.get(VOConstants.DEFAULT_ACTIVITY_YEAR);

		/*
		 * If there is no unknown year available then we should imply so by returning a "null".
		 * */
		if (unknownYearActivityCount != null) {
			return unknownYearActivityCount;
		} else {
			return null;
		}
	}
}
