/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;

/**
 * @author cdtank
 *
 */
public class BiboDocument extends Individual {

	private String publicationYear;
	private String publicationDate;
	private String parsedPublicationYear = VOConstants.DEFAULT_PUBLICATION_YEAR;

	public BiboDocument(String documentURL) {
		super(documentURL);
	}
	
	public String getDocumentURL() {
		return this.getIndividualURI();
	}	
	
	private String parsePublicationYear(String documentBlurb) {

		/*
		 * This pattern will match all group of numbers which have only 4 digits
		 * delimited by the word boundary.
		 * */
		String pattern = "(?<!-)\\b\\d{4}\\b(?=[^-])";
		
		Pattern yearPattern = Pattern.compile(pattern);
		String publishedYear = VOConstants.DEFAULT_PUBLICATION_YEAR;

		Matcher yearMatcher = yearPattern.matcher(documentBlurb);

		while (yearMatcher.find()) {

            String yearCandidate = yearMatcher.group();

            Integer candidateYearInteger = Integer.valueOf(yearCandidate);

            /*
             * Published year has to be equal or less than the current year
             * and more than a minimum default year.
             * */
			if (candidateYearInteger <= VOConstants.CURRENT_YEAR
					&& candidateYearInteger >= VOConstants.MINIMUM_PUBLICATION_YEAR) {
            	publishedYear = candidateYearInteger.toString();
            }

		}

		return publishedYear;
	}

	/**
	 * This method will be called to get the final/inferred year for the publication. 
	 * The 3 choices, in order, are,
	 * 		1. parsed year from xs:DateTime object saved in core:dateTimeValue 
	 * 		2. core:year which was property used in vivo 1.1 ontology
	 * 		3. Default Publication Year 
	 * @return
	 */
	public String getParsedPublicationYear() {
		
		if (publicationDate != null) { 
			
			DateTime validParsedDateTimeObject = UtilityFunctions.getValidParsedDateTimeObject(publicationDate);
			
			if (validParsedDateTimeObject != null) {
				return String.valueOf(validParsedDateTimeObject.getYear());
			} else {
				return publicationYear != null ? publicationYear : VOConstants.DEFAULT_PUBLICATION_YEAR;
			}
			
		} else {

			/*
			 * If all else fails return default unknown year identifier if publicationYear is
			 * not mentioned.
			 * */
			return publicationYear != null ? publicationYear : VOConstants.DEFAULT_PUBLICATION_YEAR;
		} 
	}

	/*
	 * This publicationYear value is directly from the data supported by the ontology. 
	 * If this is empty only then use the parsedPublicationYear.
	 * 
	 * @Deprecated Use getParsedPublicationYear() instead.
	 * */
	@Deprecated
	public String getPublicationYear() {
		if (publicationYear != null && isValidPublicationYear(publicationYear)) {
			return publicationYear;
		} else {
			return null;
		}
		
	}

	public void setPublicationYear(String publicationYear) {
		this.publicationYear = publicationYear;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	
	private boolean isValidPublicationYear(String testPublicationYear) {
		
		if (testPublicationYear.length() != 0 
				&& testPublicationYear.trim().length() == VOConstants.NUM_CHARS_IN_YEAR_FORMAT
				&& testPublicationYear.matches("\\d+")
				&& Integer.parseInt(testPublicationYear) >= VOConstants.MINIMUM_PUBLICATION_YEAR) {
			return true;
		}
		
		return false;
	}

}
