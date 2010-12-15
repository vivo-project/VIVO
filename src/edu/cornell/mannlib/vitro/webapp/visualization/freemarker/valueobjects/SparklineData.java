/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.List;
import java.util.Map;

public class SparklineData {
	
	/*
	 * For now sparklineNumPublicationsText & sparklinePublicationRangeText is left 
	 * as empty but later on we would want to leverage the granularity that this 
	 * provides.
	 * */
	private String sparklineNumPublicationsText = "";
	private String sparklinePublicationRangeText = "";
	
	private Integer earliestYearConsidered;
	private Integer earliestRenderedPublicationYear;
	private Integer latestRenderedPublicationYear;
	private Integer earliestRenderedGrantYear;
	private Integer latestRenderedGrantYear;
	
	private Integer renderedSparks;
	private Integer unknownYearPublications;
	private Integer unknownYearGrants;
	
	
	private Map<String, Integer> yearToActivityCount;
	
	private String table = "";
	
	private String downloadDataLink = "";
	private String fullTimelineNetworkLink = "";
	
	private String visContainerDivID = "pub_count_vis_container";
	private String sparklineContent;
	private String sparklineContext;
	
	private boolean isShortVisMode = true;
	
	private List<YearPublicationCountDataElement> yearToPublicationCountDataTable;
	private List<YearGrantCountDataElement> yearToGrantCountDataTable;	
	
	private int numOfYearsToBeRendered;
	
	public String getSparklineNumPublicationsText() {
		return sparklineNumPublicationsText;
	}
	
	public void setSparklineNumPublicationsText(String sparklineNumPublicationsText) {
		this.sparklineNumPublicationsText = sparklineNumPublicationsText;
	}
	
	public String getSparklinePublicationRangeText() {
		return sparklinePublicationRangeText;
	}
	
	public void setSparklinePublicationRangeText(
			String sparklinePublicationRangeText) {
		this.sparklinePublicationRangeText = sparklinePublicationRangeText;
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

	public List<YearGrantCountDataElement> getYearToGrantCountDataTable() {
		return yearToGrantCountDataTable;
	}

	public void setYearToGrantCountDataTable(
			List<YearGrantCountDataElement> yearToGrantCountDataTable) {
		this.yearToGrantCountDataTable = yearToGrantCountDataTable;
	}

	public void setNumOfYearsToBeRendered(int numOfYearsToBeRendered) {
		this.numOfYearsToBeRendered = numOfYearsToBeRendered;
	}

	public int getNumOfYearsToBeRendered() {
		return numOfYearsToBeRendered;
	}

	public void setYearToPublicationCountDataTable(
			List<YearPublicationCountDataElement> yearToPublicationCountDataTable) {
		this.yearToPublicationCountDataTable = yearToPublicationCountDataTable;
	}

	public List<YearPublicationCountDataElement> getYearToPublicationCountDataTable() {
		return yearToPublicationCountDataTable;
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

	public String getTable() {
		return table;
	}
	
	public void setTable(String table) {
		this.table = table;
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

	public String getSparklineContent() {
		return sparklineContent;
	}
	
	public void setSparklineContent(String shortSparklineContent) {
		this.sparklineContent = shortSparklineContent;
	}

	public void setShortVisMode(boolean isShortVisMode) {
		this.isShortVisMode = isShortVisMode;
	}

	public boolean isShortVisMode() {
		return isShortVisMode;
	}

	public String getSparklineContext() {
		return sparklineContext;
	}
	
	public void setSparklineContext(String shortSparklineContext) {
		this.sparklineContext = shortSparklineContext;
	}
}
