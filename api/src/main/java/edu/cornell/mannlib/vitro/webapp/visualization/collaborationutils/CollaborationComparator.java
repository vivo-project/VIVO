/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils;

import java.util.Comparator;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;


/**
 * This Comparator is used to sort the edges based on their IDs in ascending order.
 * @author cdtank
 *
 */
public class CollaborationComparator implements Comparator<Collaboration> {

	@Override
	public int compare(Collaboration arg0, Collaboration arg1) {
		return arg0.getCollaborationID() - arg1.getCollaborationID();
	}

}
