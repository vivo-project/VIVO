/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.concurrent.locks.Lock;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.CustomLock;

/**
 * No longer used - will be removed
 */
@Deprecated
@SuppressWarnings("deprecation")
public class OrganizationToPublicationsForSubOrganizationsFactory implements ModelFactoryInterface {
	private Log log = LogFactory.getLog(OrganizationToPublicationsForSubOrganizationsFactory.class);

	@Override
	public Model getOrCreateModel(String uri, RDFService rdfService) throws MalformedQueryParametersException {
		Model candidateModel = ConstructedModelTracker.getModel(
				ConstructedModelTracker
				.generateModelIdentifier(
						uri,
						edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE));
			
		if (candidateModel != null) {
			
			return candidateModel;
			
		} else {
			Lock customLock = CustomLock.getLock();
			if (customLock.tryLock())	//Acquiring lock if available to construct the model
			{
				try
				{
                        	ModelConstructor model = new edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationToPublicationsForSubOrganizationsModelConstructor(uri, rdfService);

                        	Model constructedModel = model.getConstructedModel();
                        	ConstructedModelTracker.trackModel(
                                ConstructedModelTracker
                                                .generateModelIdentifier(
                                                                uri,
														edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE),
                                                                constructedModel);
                        	return constructedModel;
				} finally {
					customLock.unlock();
				}
			}
			else
			{
				log.info("The Model construction process is going on");
				return null;
			}
		}	
		}
	
	}


