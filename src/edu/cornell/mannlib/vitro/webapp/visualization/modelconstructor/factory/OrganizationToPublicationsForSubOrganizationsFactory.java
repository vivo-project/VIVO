/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.OrganizationToPublicationsForSubOrganizationsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.concurrent.locks.Lock;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.CustomLock;

public class OrganizationToPublicationsForSubOrganizationsFactory implements
		ModelFactoryInterface {
	private static final Log log = LogFactory.getLog(OrganizationToPublicationsForSubOrganizationsFactory.class.getName());
	@Override
	public Model getOrCreateModel(String uri, Dataset dataset)
			throws MalformedQueryParametersException {

		Model candidateModel = ConstructedModelTracker.getModel(
				ConstructedModelTracker
				.generateModelIdentifier(
						uri, 
						OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE));
			
		if (candidateModel != null) {
			
			return candidateModel;
			
		} else {
			Lock customLock = CustomLock.getLock();
			if (customLock.tryLock())	//Acquiring lock if available to construct the model
			{
				try
				{
                        	ModelConstructor model = new OrganizationToPublicationsForSubOrganizationsModelConstructor(uri, dataset);

                        	Model constructedModel = model.getConstructedModel();
                        	ConstructedModelTracker.trackModel(
                                ConstructedModelTracker
                                                .generateModelIdentifier(
                                                                uri,
                                                                OrganizationToPublicationsForSubOrganizationsModelConstructor.MODEL_TYPE),
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


