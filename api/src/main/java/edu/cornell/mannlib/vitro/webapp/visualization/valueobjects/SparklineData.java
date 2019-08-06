/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.List;
import java.util.Map;

public class SparklineData {

	private Integer earliestYearConsidered;
	private Integer earliestRenderedPublicationYear;
	private Integer latestRenderedPublicationYear;
	private Integer earliestRenderedGrantYear;
	private Integer latestRenderedGrantYear;

	private Integer renderedSparks;
	private Integer unknownYearPublications;
	private Integer unknownYearGrants;

	private Integer totalCollaborationshipCount;

	private Map<String, Integer> yearToActivityCount;

	private String downloadDataLink = "";
	private String fullTimelineNetworkLink = "";

	private String visContainerDivID = "pub_count_vis_container";

	private boolean isShortVisMode = true;

	private List<YearToEntityCountDataElement> yearToEntityCountDataTable;

	private int numOfYearsToBeRendered;

	public void setTotalCollaborationshipCount(
			Integer totalCollaborationshipCount) {
		this.totalCollaborationshipCount = totalCollaborationshipCount;
	}

	public Integer getTotalCollaborationshipCount() {
		return totalCollaborationshipCount;
	}

	public Integer getEarliestRenderedGrantYear() {
		return earliestRenderedGrantYear;
	}

	public void setEarliestRenderedGrantYear(Integer earliestRenderedGrantYear) {
		this.earliestRenderedGrantYear = earliestRenderedGrantYear;
	}

	public Integer getLatestRenderedGrantYear() {
		return latestRenderedGrantYear;
	}

	public void setLatestRenderedGrantYear(Integer latestRenderedGrantYear) {
		this.latestRenderedGrantYear = latestRenderedGrantYear;
	}

	public Integer getUnknownYearGrants() {
		return unknownYearGrants;
	}

	public void setUnknownYearGrants(Integer unknownYearGrants) {
		this.unknownYearGrants = unknownYearGrants;
	}

	public void setNumOfYearsToBeRendered(int numOfYearsToBeRendered) {
		this.numOfYearsToBeRendered = numOfYearsToBeRendered;
	}

	public int getNumOfYearsToBeRendered() {
		return numOfYearsToBeRendered;
	}

	public void setYearToEntityCountDataTable(
			List<YearToEntityCountDataElement> yearToEntityCountDataTable) {
		this.yearToEntityCountDataTable = yearToEntityCountDataTable;
	}

	public List<YearToEntityCountDataElement> getYearToEntityCountDataTable() {
		return yearToEntityCountDataTable;
	}

	public void setYearToActivityCount(Map<String, Integer> yearToActivityCount) {
		this.yearToActivityCount = yearToActivityCount;
	}

	public Map<String, Integer> getYearToActivityCount() {
		return yearToActivityCount;
	}

	public void setEarliestYearConsidered(Integer earliestYearConsidered) {
		this.earliestYearConsidered = earliestYearConsidered;
	}

	public Integer getEarliestYearConsidered() {
		return earliestYearConsidered;
	}

	public Integer getEarliestRenderedPublicationYear() {
		return earliestRenderedPublicationYear;
	}

	public void setEarliestRenderedPublicationYear(
			Integer earliestRenderedPublicationYear) {
		this.earliestRenderedPublicationYear = earliestRenderedPublicationYear;
	}

	public Integer getLatestRenderedPublicationYear() {
		return latestRenderedPublicationYear;
	}

	public void setLatestRenderedPublicationYear(
			Integer latestRenderedPublicationYear) {
		this.latestRenderedPublicationYear = latestRenderedPublicationYear;
	}

	public void setUnknownYearPublications(Integer unknownYearPublications) {
		this.unknownYearPublications = unknownYearPublications;
	}

	public Integer getUnknownYearPublications() {
		return unknownYearPublications;
	}

	public void setRenderedSparks(Integer renderedSparks) {
		this.renderedSparks = renderedSparks;
	}

	public Integer getRenderedSparks() {
		return renderedSparks;
	}

	public String getDownloadDataLink() {
		return downloadDataLink;
	}

	public void setDownloadDataLink(String downloadDataLink) {
		this.downloadDataLink = downloadDataLink;
	}

	public String getFullTimelineNetworkLink() {
		return fullTimelineNetworkLink;
	}

	public void setFullTimelineNetworkLink(String fullTimelineNetworkLink) {
		this.fullTimelineNetworkLink = fullTimelineNetworkLink;
	}

	public void setVisContainerDivID(String visContainerDivID) {
		this.visContainerDivID = visContainerDivID;
	}

	public String getVisContainerDivID() {
		return visContainerDivID;
	}

	public void setShortVisMode(boolean isShortVisMode) {
		this.isShortVisMode = isShortVisMode;
	}

	public boolean isShortVisMode() {
		return isShortVisMode;
	}

}
