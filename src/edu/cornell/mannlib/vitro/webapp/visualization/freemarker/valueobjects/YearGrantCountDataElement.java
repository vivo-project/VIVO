/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

/**
 * This object is used to store information about the yearToGrantCount Map in the format
 * easily expressed to Google Visualization's DataTableAPI. 
 * @author bkoniden
 * Deepak Konidena
 */

public class YearGrantCountDataElement {
	
	private int grantCounter;
	private String investigatedYear;
	private int currentGrants;

	public YearGrantCountDataElement(int grantCounter,
			String investigatedYear, int currentGrants) {
		this.grantCounter = grantCounter;
		this.investigatedYear = investigatedYear;
		this.currentGrants = currentGrants;
	}

	public int getGrantCounter() {
		return grantCounter;
	}

	public String getInvestigatedYear() {
		return investigatedYear;
	}

	public int getCurrentGrants() {
		return currentGrants;
	}

}
