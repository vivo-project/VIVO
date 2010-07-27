package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;

public interface QueryHandler<QueryResponse> {
	
	QueryResponse getVisualizationJavaValueObjects() throws MalformedQueryParametersException; 

}
