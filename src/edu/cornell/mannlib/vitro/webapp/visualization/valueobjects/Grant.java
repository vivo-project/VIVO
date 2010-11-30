/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;

/**
 * @author bkoniden
 * Deepak Konidena
 *
 */

public class Grant extends Individual {
	
	private String grantStartYear;
	private String grantStartYearMonth;
	private String grantStartDate;
	private String grantEndYear;
	private String grantEndYearMonth;
	private String grantEndDate;
	
	public Grant(String grantURL, String grantLabel){
		super(grantURL, grantLabel);
	}
	
	public Grant(String grantURL){
		super(grantURL);
	}
	
	public String getGrantURL() {
		return this.getIndividualURI();
	}
	
	public String getGrantLabel(){
		return this.getIndividualLabel();
	}
	/**
	 * This method will be called when there is no usable core:year value found
	 * for the bibo:Document. It will first check & parse core:yearMonth failing
	 * which it will try core:date
	 * @return
	 */
	public String getParsedGrantStartYear() {
		
		/*
		 * We are assuming that core:yearMonth has "YYYY-MM-DD" format. This is based 
		 * off of http://www.w3.org/TR/xmlschema-2/#gYearMonth , which is what
		 * core:yearMonth points to internally.
		 * */
		if (grantStartYearMonth != null 
				&& grantStartYearMonth.length() >= VOConstants.NUM_CHARS_IN_YEAR_FORMAT
				&& isValidPublicationYear(grantStartYearMonth.substring(
													0,
													VOConstants.NUM_CHARS_IN_YEAR_FORMAT))) {
			
			return grantStartYearMonth.substring(0, VOConstants.NUM_CHARS_IN_YEAR_FORMAT); 
			
		}
		
		if (grantStartDate != null 
				&& grantStartDate.length() >= VOConstants.NUM_CHARS_IN_YEAR_FORMAT
				&& isValidPublicationYear(grantStartDate
												.substring(0,
														   VOConstants.NUM_CHARS_IN_YEAR_FORMAT))) {
			
			return grantStartDate.substring(0, VOConstants.NUM_CHARS_IN_YEAR_FORMAT); 
		}
		
		/*
		 * If all else fails return default unknown year identifier
		 * */
		return VOConstants.DEFAULT_GRANT_YEAR;
	}
	
	public String getGrantStartYear() {
		return grantStartYear;
	}

	public void setGrantStartYear(String grantStartYear) {
		this.grantStartYear = grantStartYear;
	}

	public String getGrantStartYearMonth() {
		return grantStartYearMonth;
	}

	public void setGrantStartYearMonth(String grantStartYearMonth) {
		this.grantStartYearMonth = grantStartYearMonth;
	}

	public String getGrantStartDate() {
		return grantStartDate;
	}

	public void setGrantStartDate(String grantStartDate) {
		this.grantStartDate = grantStartDate;
	}

	public String getGrantEndYear() {
		return grantEndYear;
	}

	public void setGrantEndYear(String grantEndYear) {
		this.grantEndYear = grantEndYear;
	}

	public String getGrantEndYearMonth() {
		return grantEndYearMonth;
	}

	public void setGrantEndYearMonth(String grantEndYearMonth) {
		this.grantEndYearMonth = grantEndYearMonth;
	}

	public String getGrantEndDate() {
		return grantEndDate;
	}

	public void setGrantEndDate(String grantEndDate) {
		this.grantEndDate = grantEndDate;
	}

	private boolean isValidPublicationYear(String testGrantYear) {
		
		if (testGrantYear.length() != 0 
				&& testGrantYear.trim().length() == VOConstants.NUM_CHARS_IN_YEAR_FORMAT
				&& testGrantYear.matches("\\d+")
				&& Integer.parseInt(testGrantYear) >= VOConstants.MINIMUM_PUBLICATION_YEAR) {
			return true;
		}
		
		return false;
	}

}
