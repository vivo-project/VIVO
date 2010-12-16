/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

/**
 * This object is used to store information about the yearToPublicationCount Map in the format
 * easily expressed to Google Visualization's DataTableAPI. 
 * @author cdtank
 *
 */
public class YearPublicationCountDataElement {

	private int publicationCounter;
	private String publishedYear;
	private int currentPublications;
	
	public YearPublicationCountDataElement(int publicationCounter,
			String publishedYear, int currentPublications) {
		this.publicationCounter = publicationCounter;
		this.publishedYear = publishedYear;
		this.currentPublications = currentPublications;
	}

	public int getPublicationCounter() {
		return publicationCounter;
	}

	public String getPublishedYear() {
		return publishedYear;
	}

	public int getCurrentPublications() {
		return currentPublications;
	}
	
}
