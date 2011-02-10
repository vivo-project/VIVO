/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.loadtesting.reportmerger;

/**
 * Info about the executions of a single test in a single file.
 */
public class TestResultInfo {
	private final String testName;
	private final boolean success;
	private final int count;
	private final float averageTime;
	private final float maxTime;
	private final float minTime;

	public TestResultInfo(String testName, boolean success, int count,
			float averageTime, float maxTime, float minTime) {
		this.testName = testName;
		this.success = success;
		this.count = count;
		this.averageTime = averageTime;
		this.maxTime = maxTime;
		this.minTime = minTime;
	}

	public String getTestName() {
		return testName;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getCount() {
		return count;
	}

	public float getAverageTime() {
		return averageTime;
	}

	public float getMaxTime() {
		return maxTime;
	}

	public float getMinTime() {
		return minTime;
	}

	@Override
	public String toString() {
		return "TestResultInfo[testName=" + testName + ", success=" + success
				+ ", count=" + count + ", averageTime=" + averageTime
				+ ", maxTime=" + maxTime + ", minTime=" + minTime + "]";
	}
}
