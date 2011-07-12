/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.ModelFactoryInterface;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationAssociatedPeopleModelWithTypesFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationModelWithTypesFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationToGrantsForSubOrganizationsFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationToPublicationsForSubOrganizationsFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PeopleToGrantsFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PeopleToPublicationsFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PersonToGrantsFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PersonToPublicationsFactory;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.SubOrganizationWithinModelFactory;

@SuppressWarnings("serial")
public class ModelConstructorUtilities {
	
	private static final Map<String, ModelFactoryInterface> modelTypeIdentifierToFactory = new HashMap<String, ModelFactoryInterface>() {{
		put(PersonToPublicationsModelConstructor.MODEL_TYPE, new PersonToPublicationsFactory());
		put(PeopleToPublicationsModelConstructor.MODEL_TYPE, new PeopleToPublicationsFactory());
		put(PersonToGrantsModelConstructor.MODEL_TYPE, new PersonToGrantsFactory());
		put(PeopleToGrantsModelConstructor.MODEL_TYPE, new PeopleToGrantsFactory());
		put(OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE, new OrganizationToPublicationsForSubOrganizationsFactory());
		put(OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE, new OrganizationToGrantsForSubOrganizationsFactory());
		put(OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE, new OrganizationAssociatedPeopleModelWithTypesFactory());
		put(OrganizationModelWithTypesConstructor.MODEL_TYPE, new OrganizationModelWithTypesFactory());
		put(SubOrganizationWithinModelConstructor.MODEL_TYPE, new SubOrganizationWithinModelFactory());
	}};
	
	public static final Map<String, String> modelTypeToHumanReadableName = new HashMap<String, String>() {{
		put(PersonToPublicationsModelConstructor.MODEL_TYPE, PersonToPublicationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(PeopleToPublicationsModelConstructor.MODEL_TYPE, PeopleToPublicationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(PersonToGrantsModelConstructor.MODEL_TYPE, PersonToGrantsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(PeopleToGrantsModelConstructor.MODEL_TYPE, PeopleToGrantsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE, OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE, OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE, OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationModelWithTypesConstructor.MODEL_TYPE, OrganizationModelWithTypesConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(SubOrganizationWithinModelConstructor.MODEL_TYPE, SubOrganizationWithinModelConstructor.MODEL_TYPE_HUMAN_READABLE);
	}};
	
	public static Model getOrConstructModel(String uri, String modelType, Dataset dataset) 
				throws MalformedQueryParametersException {
		return modelTypeIdentifierToFactory.get(modelType).getOrCreateModel(uri, dataset);
	}
}