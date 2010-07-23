package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.util.Comparator;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Edge;


public class EdgeComparator implements Comparator<Edge> {

	@Override
	public int compare(Edge arg0, Edge arg1) {
		return arg1.getEdgeID() - arg0.getEdgeID();
	}

}
