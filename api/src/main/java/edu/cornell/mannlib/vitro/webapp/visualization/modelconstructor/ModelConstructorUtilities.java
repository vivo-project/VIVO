/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.ModelFactoryInterface;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PersonToGrantsFactory;

@SuppressWarnings("deprecation,serial")
public class ModelConstructorUtilities {

	/**
	 * @deprecated
	 */
	private static final Map<String, ModelFactoryInterface> modelTypeIdentifierToFactory = new HashMap<String, ModelFactoryInterface>() {{
		// Currently in use, but probably should be deprecated with the others
		put(PersonToGrantsModelConstructor.MODEL_TYPE, new PersonToGrantsFactory());

		/**
		 * The following models are deprecated and will be removed
		 */
		put(PersonToPublicationsModelConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PersonToPublicationsFactory());
		put(PeopleToPublicationsModelConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PeopleToPublicationsFactory());
		put(PeopleToGrantsModelConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.PeopleToGrantsFactory());
		put(OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationToPublicationsForSubOrganizationsFactory());
		put(OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationToGrantsForSubOrganizationsFactory());
		put(OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationAssociatedPeopleModelWithTypesFactory());
		put(OrganizationModelWithTypesConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.OrganizationModelWithTypesFactory());
		put(SubOrganizationWithinModelConstructor.MODEL_TYPE, new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory.SubOrganizationWithinModelFactory());
	}};

	/**
	 * @deprecated
	 */
	public static final Map<String, String> modelTypeToHumanReadableName = new HashMap<String, String>() {{
		// Currently in use, but probably should be deprecated with the others
		put(PersonToGrantsModelConstructor.MODEL_TYPE, PersonToGrantsModelConstructor.MODEL_TYPE_HUMAN_READABLE);

		/**
		 * The following models are deprecated and will be removed
		 */
		put(PersonToPublicationsModelConstructor.MODEL_TYPE, PersonToPublicationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(PeopleToPublicationsModelConstructor.MODEL_TYPE, PeopleToPublicationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(PeopleToGrantsModelConstructor.MODEL_TYPE, PeopleToGrantsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE, OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE, OrganizationToGrantsForSubOrganizationsModelConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE, OrganizationAssociatedPeopleModelWithTypesConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(OrganizationModelWithTypesConstructor.MODEL_TYPE, OrganizationModelWithTypesConstructor.MODEL_TYPE_HUMAN_READABLE);
		put(SubOrganizationWithinModelConstructor.MODEL_TYPE, SubOrganizationWithinModelConstructor.MODEL_TYPE_HUMAN_READABLE);
	}};

	public static Model getOrConstructModel(String uri, String modelType, RDFService rdfService)
			throws MalformedQueryParametersException {
		return modelTypeIdentifierToFactory.get(modelType).getOrCreateModel(uri, rdfService);
	}
}