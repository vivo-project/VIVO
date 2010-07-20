/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;

/**
 * @author cdtank
 *
 */
public class BiboDocument extends Individual{

	public static final int MINIMUM_PUBLICATION_YEAR = 1800;
	private static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	private String documentMoniker;
	private String documentBlurb;
	private String documentDescription;
	private String publicationYear;
	private String publicationYearMonth;
	private String publicationDate;
	private String parsedPublicationYear = VOConstants.DEFAULT_PUBLICATION_YEAR;

	public BiboDocument(String documentURL) {
		super(documentURL);
	}
	
	public String getDocumentURL() {
		return this.getIndividualURL();
	}
	
	public String getDocumentMoniker() {
		return documentMoniker;
	}
	
	public void setDocumentMoniker(String documentMoniker) {
		this.documentMoniker = documentMoniker;
	}
	
	public String getDocumentLabel() {
		return this.getIndividualLabel();
	}
	
	public void setDocumentLabel(String documentLabel) {
		this.setIndividualLabel(documentLabel);
	}
	
	public String getDocumentBlurb() {
		return documentBlurb;
	}
	
	public void setDocumentBlurb(String documentBlurb) {
		this.documentBlurb = documentBlurb;

//		if (documentBlurb != null) {
//			this.setParsedPublicationYear(parsePublicationYear(documentBlurb));
//		}
	}
	
	private String parsePublicationYear(String documentBlurb) {

		/*
		 * This pattern will match all group of numbers which have only 4 digits
		 * delimited by the word boundary.
		 * */
//		String pattern = "\\b\\d{4}\\b";
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
			if (candidateYearInteger <= CURRENT_YEAR
					&& candidateYearInteger >= MINIMUM_PUBLICATION_YEAR) {
            	publishedYear = candidateYearInteger.toString();
            }

		}

		return publishedYear;
	}

	public String getDocumentDescription() {
		return documentDescription;
	}
	public void setDocumentDescription(String documentDescription) {
		this.documentDescription = documentDescription;
	}

	/**
	 * This method will be called when there is no usable core:year value found
	 * for the bibo:Document. It will first check & parse core:yearMonth failing
	 * which it will try core:date
	 * @return
	 */
	public String getParsedPublicationYear() {
		
		/*
		 * We are assuming that core:yearMonth has "YYYY-MM" format. This is based 
		 * off of http://www.w3.org/TR/xmlschema-2/#gYearMonth , which is what
		 * core:yearMonth points to internally.
		 * */
		if (publicationYearMonth != null 
				&& publicationYearMonth.length() >= 4
				&& isValidPublicationYear(publicationYearMonth.substring(0, 4))) {
			
			return publicationYearMonth.substring(0, 4); 
			
		} 
		
		if (publicationDate != null 
				&& publicationDate.length() >= 4
				&& isValidPublicationYear(publicationDate.substring(0, 4))) {
			
			return publicationDate.substring(0, 4); 
		}
		
		/*
		 * If all else fails return default unknown year identifier
		 * */
		return VOConstants.DEFAULT_PUBLICATION_YEAR;
	}

	/*
	 * This publicationYear value is directly from the data supported by the ontology. If this is empty only 
	 * then use the parsedPublicationYear.
	 * */
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

	public String getPublicationYearMonth() {
		return publicationYearMonth;
	}

	public void setPublicationYearMonth(String publicationYearMonth) {
		this.publicationYearMonth = publicationYearMonth;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	
	private boolean isValidPublicationYear(String testPublicationYear) {
		
		if (testPublicationYear.length() != 0 
				&& testPublicationYear.trim().length() == 4
				&& testPublicationYear.matches("\\d+")
				&& Integer.parseInt(testPublicationYear) >= MINIMUM_PUBLICATION_YEAR) {
			return true;
		}
		
		return false;
	}

}
