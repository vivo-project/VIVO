/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.util.Comparator;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Node;


/**
 * This Comparator is used to sort the nodes based on their IDs in ascending order.
 * @author cdtank
 */
public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node arg0, Node arg1) {
		return arg0.getNodeID() - arg1.getNodeID();
	}

}
