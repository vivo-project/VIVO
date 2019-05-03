/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

/**
 * This object is used to store information about the yearToEntityCount Map in the format
 * easily expressed to Google Visualization's DataTableAPI.
 * @author cdtank
 *
 */
public class YearToEntityCountDataElement {

	private int yearToEntityCounter;
	private String year;
	private int currentEntitiesCount;

	public YearToEntityCountDataElement(int yearToEntityCounter,
			String year, int currentEntitiesCount) {
		this.yearToEntityCounter = yearToEntityCounter;
		this.year = year;
		this.currentEntitiesCount = currentEntitiesCount;
	}

	public int getYearToEntityCounter() {
		return yearToEntityCounter;
	}

	public String getYear() {
		return year;
	}

	public int getCurrentEntitiesCount() {
		return currentEntitiesCount;
	}

}
