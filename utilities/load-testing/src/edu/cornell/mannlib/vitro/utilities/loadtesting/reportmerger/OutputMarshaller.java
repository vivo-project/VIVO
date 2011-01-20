/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.loadtesting.reportmerger;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Write the merged data to an HTML page.
 */
public class OutputMarshaller {
	private final List<TestResultsFileData> reportData;
	private final PrintWriter w;
	private final List<String> testNames;

	public OutputMarshaller(List<TestResultsFileData> reportData, PrintWriter w) {
		this.reportData = reportData;
		this.w = w;
		this.testNames = assembleListOfTestNames();
	}

	public void marshall() {
		writePageHeader();
		writeTestDataTable();
		writePageFooter();
	}

	private List<String> assembleListOfTestNames() {
		Set<String> names = new TreeSet<String>();
		for (TestResultsFileData filedata : reportData) {
			names.addAll(filedata.getTestMap().keySet());
		}
		return new ArrayList<String>(names);
	}

	private void writePageHeader() {
		w.println("<html>");
		w.println("<head>");
		w.println("  <link REL='STYLESHEET' TYPE='text/css' HREF='./mergedResults.css'>");
		w.println("</head>");
		w.println("<body>");
	}

	private void writeTestDataTable() {
		w.println("<table class='testData' cellspacing='0'>");
		writeTestDataHeader();
		for (String testName : testNames) {
			writeTestDataRow(testName);
		}
		w.println("</table>");
	}

	private void writeTestDataHeader() {
		w.println("  <tr>");
		w.println("    <th>&nbsp;</th>");
		for (TestResultsFileData fileData : reportData) {
			w.println("    <th colspan='3'>" + fileData.getVivoVersion()
					+ "<br/>" + fileData.getResultsFilename() + "<br/>"
					+ formatDate(fileData.getCreated()) + "</th>");
		}
		w.println("  </tr>");

		w.println("  <tr>");
		w.println("    <th>Test Name</th>");
		for (TestResultsFileData fileData : reportData) {
			w.println("    <th>iterations</th>");
			w.println("    <th>time (min/max)</th>");
			w.println("    <th>ratio</th>");
		}
		w.println("  </tr>");
	}

	private void writeTestDataRow(String testName) {
		w.println("  <tr>");
		w.println("    <td class='left'>" + testName + "</td>");
		for (TestResultsFileData fileData : reportData) {
			writeTestDataCellForFile(fileData, testName);
		}
		w.println("  </tr>");
	}

	private void writeTestDataCellForFile(TestResultsFileData fileData,
			String testName) {
		TestResultInfo testData = fileData.getTestMap().get(testName);
		TestResultInfo baselineTestData = reportData.get(0).getTestMap()
				.get(testName);

		String count = (testData == null) ? "&nbsp;" : ("" + testData
				.getCount());
		String averageTime = (testData == null) ? "&nbsp;"
				: ("" + formatTime(testData.getAverageTime()));
		String minTime = (testData == null) ? "&nbsp;"
				: ("" + formatTime(testData.getMinTime()));
		String maxTime = (testData == null) ? "&nbsp;"
				: ("" + formatTime(testData.getMaxTime()));

		String ratioWithBaseline = "&nbsp";
		if ((testData != null) && (baselineTestData != null)) {
			ratioWithBaseline = percentage(testData.getAverageTime(),
					baselineTestData.getAverageTime());
		}

		w.println("    <td class='open'>" + count + "</td>");
		w.println("    <td>");
		w.println("      <table class='oneResult middle' cellspacing=0>");
		w.println("        <tr>");
		w.println("          <td rowspan='2'>" + averageTime + "</td>");
		w.println("          <td class='minmax'>" + minTime + "</td>");
		w.println("        </tr>");
		w.println("        <tr>");
		w.println("          <td class='minmax'>" + maxTime + "</td>");
		w.println("        </tr>");
		w.println("      </table>");
		w.println("    <td class='close'>" + ratioWithBaseline + "</td>");
		w.println("    </td>");
	}

	private String percentage(float value, float baseline) {
		float ratio = value / baseline;
		return String.format("%1$8.2f%%", ratio * 100.0);
	}

	public String formatTime(float time) {
		return String.format("%1$8.3f", time);
	}

	public String formatDate(long date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(date));
	}

	private void writePageFooter() {
		w.println("</body>");
		w.println("</html>");
	}

}
