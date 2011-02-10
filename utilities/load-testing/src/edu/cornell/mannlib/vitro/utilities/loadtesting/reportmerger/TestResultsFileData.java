/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.loadtesting.reportmerger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Holds the data that was parsed from a single results file.
 */
public class TestResultsFileData {
	private final String vivoVersion;
	private final String resultsFilename;
	private final long created;
	private final LinkedHashMap<String, TestResultInfo> testMap;

	public TestResultsFileData(String vivoVersion, String resultsFilename,
			long created, Map<String, TestResultInfo> testMap) {
		this.vivoVersion = vivoVersion;
		this.resultsFilename = resultsFilename;
		this.created = created;
		this.testMap = new LinkedHashMap<String, TestResultInfo>(testMap);
	}

	public String getTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
				created));
	}

	public String getVivoVersion() {
		return vivoVersion;
	}

	public String getResultsFilename() {
		return resultsFilename;
	}

	public long getCreated() {
		return created;
	}

	public LinkedHashMap<String, TestResultInfo> getTestMap() {
		return testMap;
	}

	@Override
	public String toString() {
		return "TestResultsFileData[vivoVersion=" + vivoVersion
				+ ", resultsFilename=" + resultsFilename + ", created="
				+ getTimeStamp() + ", testMap=" + testMap + "]";
	}

}
