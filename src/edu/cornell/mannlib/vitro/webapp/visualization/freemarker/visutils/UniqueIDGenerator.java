/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils;

public class UniqueIDGenerator {
	
	private int nextNumericID = 1;

	public int getNextNumericID() {
		int nextNumericID = this.nextNumericID;
		this.nextNumericID++;

		return nextNumericID;
	}

}
