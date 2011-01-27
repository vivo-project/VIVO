/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import org.joda.time.DateTime;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;

/**
 * @author bkoniden
 * Deepak Konidena
 *
 */

public class Grant extends Individual {
	
	private String grantStartDate;
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
	
	public void setGrantLabel(String grantLabel) {
		this.setIndividualLabel(grantLabel);
	}
	

	/**
	 * This method will be called to get the inferred start year for the grant. 
	 * The 3 choices, in order, are,
	 * 		1. parsed year from xs:DateTime object saved in core:dateTimeValue 
	 * 		2. core:year which was property used in vivo 1.1 ontology
	 * 		3. Default Grant Start Year 
	 * @return
	 */
	public String getParsedGrantStartYear() {

		if (grantStartDate != null) {

			DateTime validParsedDateTimeObject = UtilityFunctions
					.getValidParsedDateTimeObject(grantStartDate);

			if (validParsedDateTimeObject != null) {
				return String.valueOf(validParsedDateTimeObject.getYear());
			} else {
				return VOConstants.DEFAULT_GRANT_YEAR;
			}
		} else {
			return VOConstants.DEFAULT_GRANT_YEAR;
		}

	}
	

	public String getGrantStartDate() {
		return grantStartDate;
	}

	public void setGrantStartDate(String grantStartDate) {
		this.grantStartDate = grantStartDate;
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

//	/**
//	 * This method will be called when there is no usable core:year value found
//	 * for the core:Grant. It will first check & parse core:yearMonth failing
//	 * which it will try core:date
//	 * @return
//	 */
//	public String getParsedGrantStartYear() {
//		
//		/*
//		 * We are assuming that core:yearMonth has "YYYY-MM-DD" format. This is based 
//		 * off of http://www.w3.org/TR/xmlschema-2/#gYearMonth , which is what
//		 * core:yearMonth points to internally.
//		 * */
//		if (grantStartYearMonth != null 
//				&& grantStartYearMonth.length() >= VOConstants.NUM_CHARS_IN_YEAR_FORMAT
//				&& isValidPublicationYear(grantStartYearMonth.substring(
//													0,
//													VOConstants.NUM_CHARS_IN_YEAR_FORMAT))) {
//			
//			return grantStartYearMonth.substring(0, VOConstants.NUM_CHARS_IN_YEAR_FORMAT); 
//			
//		}
//		
//		if (grantStartDate != null 
//				&& grantStartDate.length() >= VOConstants.NUM_CHARS_IN_YEAR_FORMAT
//				&& isValidPublicationYear(grantStartDate
//												.substring(0,
//														   VOConstants.NUM_CHARS_IN_YEAR_FORMAT))) {
//			
//			return grantStartDate.substring(0, VOConstants.NUM_CHARS_IN_YEAR_FORMAT); 
//		}
//		
//		/*
//		 * If all else fails return default unknown year identifier
//		 * */
//		return VOConstants.DEFAULT_GRANT_YEAR;
//	}	
	
}
