/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationModelWithTypesConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;

public class OrganizationModelWithTypesFactory implements ModelFactoryInterface {

	@Override
	public Model getOrCreateModel(String uri, Dataset dataset) throws MalformedQueryParametersException {
		
		Model candidateModel = ConstructedModelTracker.getModel(
					ConstructedModelTracker
					.generateModelIdentifier(
							null, 
							OrganizationModelWithTypesConstructor.MODEL_TYPE));

		if (candidateModel != null) {
		
			return candidateModel;
		
		} else {
		
			ModelConstructor model = new OrganizationModelWithTypesConstructor(dataset);
			Model constructedModel = model.getConstructedModel();
			
			ConstructedModelTracker.trackModel(
					ConstructedModelTracker
						.generateModelIdentifier(
								null, 
								OrganizationModelWithTypesConstructor.MODEL_TYPE),
								constructedModel);
			
			return constructedModel;
		}
	}
}
