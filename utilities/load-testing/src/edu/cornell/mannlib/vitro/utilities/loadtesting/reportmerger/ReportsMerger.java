/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.loadtesting.reportmerger;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Take two or more reports from JMeter's test results, and merge them into a
 * unified HTML report.
 */
public class ReportsMerger {

	/**
	 * Start with list of filenames in command line For each contributing file,
	 * heading is from parsing the filename. Get the one after LoadTesting, and
	 * the last one, minus the extension.
	 * 
	 * For each file, build a structure with header info and a LinkedMap of the
	 * desired info, testname -> info structure Build a list of these. Build a
	 * unified list of testnames.
	 * 
	 * List<TestResultsFile>
	 * 
	 * TestResultsFile: String version; String filename; Date timestamp;
	 * LinkedMap<String, TestResultInfo> testMap;
	 * 
	 * TestResultInfo: boolean success; int count; float averageTime; float
	 * maxTime; float minTime;
	 */

	private final ReportsMergerParameters parms;
	private List<TestResultsFileData> reportData;

	public ReportsMerger(ReportsMergerParameters parms) {
		this.parms = parms;
	}

	private void parseReports() {
		List<TestResultsFileData> reportData = new ArrayList<TestResultsFileData>();
		for (File reportFile : parms.getReportFiles()) {
			TestResultsFileData fileData = new TestResultsParser(reportFile)
					.parse();
			System.out.println("File data: " + fileData);
			reportData.add(fileData);
		}
		this.reportData = reportData;
	}

	private void produceOutput() {
		PrintWriter writer = parms.getOutputWriter();
		new OutputMarshaller2(reportData, writer).marshall();
		writer.flush();
		writer.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReportsMergerParameters parms = ReportsMergerParameters
				.getInstance(args);
		ReportsMerger rm = new ReportsMerger(parms);
		rm.parseReports();
		rm.produceOutput();
	}

}
