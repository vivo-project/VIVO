/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.util.Comparator;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoPIEdge;


/**
 * This Comparator is used to sort the edges based on their IDs in ascending order.
 * @author bkoniden
 *
 */
public class CoPIEdgeComparator implements Comparator<CoPIEdge> {

	@Override
	public int compare(CoPIEdge arg0, CoPIEdge arg1) {
		return arg0.getEdgeID() - arg1.getEdgeID();
	}

}
