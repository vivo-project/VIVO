/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

public class MapOfScienceActivity extends Activity {

	private String publishedInJournal;

	public MapOfScienceActivity(String activityURI) {
		super(activityURI);
	}

	public void setPublishedInJournal(String publishedInJournal) {
		this.publishedInJournal = publishedInJournal;
	}

	public String getPublishedInJournal() {
		return publishedInJournal;
	}

}
