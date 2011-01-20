/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.loadtesting.reportmerger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parse a single test results file.
 */
public class TestResultsParser {
	/** Find the "Pages" table in the file. */
	private static final String PATTERN_PAGES_TABLE = "<h2>Pages</h2><table[^>]*>(.*?)</table>";

	/** Find a row in the table. */
	private static final String PATTERN_TABLE_ROW = "<tr[^>]*>(.*?)</tr>";

	private final File reportFile;
	private final String filePath;

	public TestResultsParser(File reportFile) {
		this.reportFile = reportFile;
		this.filePath = reportFile.getAbsolutePath();
	}

	/**
	 * Get info from the file path, the create date, and the contents of the
	 * file.
	 */
	public TestResultsFileData parse() {
		String[] pathInfo = parseInfoFromFilePath();
		Map<String, TestResultInfo> testMap = extractTestMap();
		return new TestResultsFileData(pathInfo[0], pathInfo[1],
				reportFile.lastModified(), testMap);
	}

	private String[] parseInfoFromFilePath() {
		String vivoVersion = "--";
		String[] pathParts = filePath.split(Pattern.quote(File.separator));
		for (int i = 0; i < pathParts.length; i++) {
			if (pathParts[i].startsWith("ver_")) {
				vivoVersion = pathParts[i].substring(4);
			}
		}

		String trimmedFilename = reportFile.getName().substring(0,
				reportFile.getName().lastIndexOf('.'));

		return new String[] { vivoVersion, trimmedFilename };
	}

	/**
	 * Scan through the contents of the file for info
	 */
	private Map<String, TestResultInfo> extractTestMap() {
		String contents = readEntireFileWithoutLineFeeds();

		String pagesTable = findPagesTableInFile(contents);
		// System.out.println("PagesTable: " + pagesTable);

		return parsePagesTable(pagesTable);
	}

	private String readEntireFileWithoutLineFeeds() {
		StringBuilder result = new StringBuilder();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(reportFile));

			String line;
			while (null != (line = reader.readLine())) {
				result.append(line);
			}
			return result.toString();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"File doesn't exist: '" + filePath + "'", e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read the file: '" + filePath
					+ "'", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String findPagesTableInFile(String contents) {
		Pattern p = Pattern.compile(PATTERN_PAGES_TABLE);
		Matcher m = p.matcher(contents);
		if (m.find()) {
			return m.group(1);
		}
		throw new RuntimeException("Failed to find the 'Pages' "
				+ "table in file: '" + filePath + "'");
	}

	private Map<String, TestResultInfo> parsePagesTable(String pagesTable) {
		Map<String, TestResultInfo> map = new LinkedHashMap<String, TestResultInfo>();

		Pattern p = Pattern.compile(PATTERN_TABLE_ROW);
		Matcher m = p.matcher(pagesTable);

		discardHeaderRowFromPagesTable(m);

		while (m.find()) {
			TestResultInfo info = parseTestRowFromPagesTable(m.group(1));
			map.put(info.getTestName(), info);
		}
		return map;
	}

	private void discardHeaderRowFromPagesTable(Matcher m) {
		if (!m.find()) {
			throw new RuntimeException("Failed to find a header row "
					+ "in the 'Pages' table, in file: '" + filePath + "'");
		}
	}

	private TestResultInfo parseTestRowFromPagesTable(String tableRow) {
		// System.out.println("Table Row: " + tableRow);

		List<String> cells = new ArrayList<String>();

		Pattern p = Pattern.compile("<td.*?>(.*?)</td>");
		Matcher m = p.matcher(tableRow);
		while (m.find()) {
			cells.add(m.group(1));
		}
		// System.out.println("Cells: " + cells);

		if (cells.size() < 7) {
			throw new RuntimeException("Only " + cells.size()
					+ " cells in this table row: '" + tableRow
					+ "', in file: '" + filePath + "'");
		}

		String testName = cells.get(0);
		int count = Integer.parseInt(cells.get(1));
		boolean success = cells.get(2).equals("0");
		float averageTime = parseTimeFromCell(cells.get(4));
		float minTime = parseTimeFromCell(cells.get(5));
		float maxTime = parseTimeFromCell(cells.get(6));

		return new TestResultInfo(testName, success, count, averageTime,
				maxTime, minTime);
	}

	private float parseTimeFromCell(String cell) {
		String[] parts = cell.split(" ");
		return Integer.parseInt(parts[0]) / 1000.0F;
	}

}
