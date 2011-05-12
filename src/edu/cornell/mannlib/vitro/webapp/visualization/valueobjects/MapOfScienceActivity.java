package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

public class MapOfScienceActivity extends Activity {

	private String publishedInJournal = "NONE";
	
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
