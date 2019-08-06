/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;

/**
 * This interface will make sure that VOs conveying any person's academic output like publications,
 * grants etc implement certain methods which will be used to generalize methods which are just
 * interested in certain common properties like what was the year in which the activity was
 * published (or started).
 * @author cdtank
 */
public class Activity extends Individual {

	private String activityDate;
	private String activityType;

	public Activity(String activityURI) {
		super(activityURI);
	}

	public String getActivityURI() {
		return this.getIndividualURI();
	}

	public String getActivityLabel() {
		return this.getIndividualLabel();
	}

	public void setActivityLabel(String activityLabel) {
		this.setIndividualLabel(activityLabel);
	}

	public String getActivityType() { return this.activityType; }

	public void setActivityType(String activityType) { this.activityType = activityType; }

	/**
	 * This method will be called to get the final/inferred year for the publication.
	 * The 2 choices, in order, are,
	 * 		1. parsed year from xs:DateTime object saved in core:dateTimeValue
	 * 		2. Default Publication Year
	 */
	public String getParsedActivityYear() {

		return UtilityFunctions.getValidYearFromCoreDateTimeString(activityDate,
				VOConstants.DEFAULT_ACTIVITY_YEAR);
	}

	/**
	 * This method should be used to get the raw date & not the parsed publication year.
	 * For the later use getParsedPublicationYear.
	 */
	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}
}
