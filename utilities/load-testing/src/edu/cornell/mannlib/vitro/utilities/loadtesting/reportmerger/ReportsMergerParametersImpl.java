/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.loadtesting.reportmerger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A base implementation of ReportsMergerParameters
 */
public class ReportsMergerParametersImpl extends ReportsMergerParameters {

	private final String outputDirectoryPath;
	private final String inputDirectoryPath;
	private final List<String> inputFilenames;

	/**
	 * The first arg is the output directory. The second arg is an input
	 * directory. The third arg is a comma-separated list of input filenames.
	 */
	public ReportsMergerParametersImpl(String[] args) {
		this.outputDirectoryPath = args[0];
		this.inputDirectoryPath = args[1];
		this.inputFilenames = Arrays.asList(args[2].split("[, ]+"));
	}

	@Override
	public List<File> getReportFiles() {
		List<File> files = new ArrayList<File>();
		for (String filename : inputFilenames) {
			files.add(new File(inputDirectoryPath, filename));
		}
		// files.add(new File(
		// "/Development/JIRA issues/NIHVIVO-1129_Load_testing/mergerFiles/LoadTesting/release1.1.1/SecondTests-rel-1-1-1.html"));
		// files.add(new File(
		// "/Development/JIRA issues/NIHVIVO-1129_Load_testing/mergerFiles/LoadTesting/trunkNoSdb/SecondTests-rel-1-2.html"));
		// files.add(new File(
		// "/Development/JIRA issues/NIHVIVO-1129_Load_testing/mergerFiles/LoadTesting/trunkSdb/SecondTests-rel-1-2.html"));
		return files;
	}

	@Override
	public PrintWriter getOutputWriter() {
		try {
			File outputFile = new File(outputDirectoryPath,
					"mergedResults.html");
			return new PrintWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			throw new RuntimeException("Can't open the output writer.", e);
		}
	}

}
