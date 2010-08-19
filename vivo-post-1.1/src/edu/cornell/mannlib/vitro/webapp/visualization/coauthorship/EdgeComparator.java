/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.util.Comparator;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Edge;


/**
 * This Comparator is used to sort the edges based on their IDs in ascending order.
 * @author cdtank
 *
 */
public class EdgeComparator implements Comparator<Edge> {

	@Override
	public int compare(Edge arg0, Edge arg1) {
		return arg0.getEdgeID() - arg1.getEdgeID();
	}

}
