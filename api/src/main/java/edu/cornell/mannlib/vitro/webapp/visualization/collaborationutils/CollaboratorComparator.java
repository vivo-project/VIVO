/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils;

import java.util.Comparator;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;


/**
 * This Comparator is used to sort the nodes based on their IDs in ascending order.
 * @author cdtank
 */
public class CollaboratorComparator implements Comparator<Collaborator> {

	@Override
	public int compare(Collaborator arg0, Collaborator arg1) {
		return arg0.getCollaboratorID() - arg1.getCollaboratorID();
	}

}
