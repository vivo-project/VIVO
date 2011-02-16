/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.loadtesting.reportmerger;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;


/**
 * Parse the command-line parameters for the ReportManager
 */
public abstract class ReportsMergerParameters {

	public static ReportsMergerParameters getInstance(String[] args) {
		return new ReportsMergerParametersImpl(args);
	}

	public abstract List<File> getReportFiles();

	public abstract PrintWriter getOutputWriter(); 
}
