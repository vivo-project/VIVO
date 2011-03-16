/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.factory.ModelFactoryInterface;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.factory.OrganizationAssociatedPeopleModelWithTypesFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.factory.OrganizationModelWithTypesFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.factory.OrganizationToPublicationsForSubOrganizationsFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.modelconstructor.factory.PersonToPublicationsFactory;

public class ModelConstructorUtilities {
	
	@SuppressWarnings("serial")
	private static final Map<String, ModelFactoryInterface> modelTypeIdentifierToFactory = new HashMap<String, ModelFactoryInterface>() {{
		put(PersonToPublicationsModelConstructor.MODEL_TYPE, new PersonToPublicationsFactory());
		put(OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE, new OrganizationToPublicationsForSubOrganizationsFactory());
		put(OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE, new OrganizationAssociatedPeopleModelWithTypesFactory());
		put(OrganizationModelWithTypesConstructor.MODEL_TYPE, new OrganizationModelWithTypesFactory());
	}};
	
	public static Model getOrConstructModel(String uri, String modelType, Dataset dataset) 
				throws MalformedQueryParametersException {
		return modelTypeIdentifierToFactory.get(modelType).getOrCreateModel(uri, dataset);
	}
}
