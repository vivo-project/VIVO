/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.util.Comparator;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoPINode;

/**
 * This Comparator is used to sort the CoPINodes based on their IDs in ascending order.
 * @author bkoniden
 * Deepak Konidena
 */

public class CoPINodeComparator implements Comparator<CoPINode>{
	@Override
	public int compare(CoPINode arg0, CoPINode arg1) {
		return arg0.getNodeID() - arg1.getNodeID();
	}
}
