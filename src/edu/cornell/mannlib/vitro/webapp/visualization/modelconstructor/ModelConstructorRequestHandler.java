/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Dataset;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.IllegalConstructedModelIdentifierException;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModel;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class ModelConstructorRequestHandler implements
		VisualizationRequestHandler {

    public static final AuthorizationRequest REQUIRED_ACTIONS = SimplePermission.REFRESH_VISUALIZATION_CACHE.ACTION;
    
	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {
		return regenerateConstructedModels(vitroRequest, dataSource);
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException(
				"Cached Model does not provide Data Response.");
	}

	private ResponseValues renderRefreshCacheMarkup(VitroRequest vitroRequest,
			Log log, Dataset dataSource) {

		String standaloneTemplate = "regenerateConstructedModels.ftl";

		List<ConstructedModel> currentConstructedModels = new ArrayList<ConstructedModel>();
		List<String> unidentifiedModels = new ArrayList<String>();

		for (String currentIdentifier : ConstructedModelTracker.getAllModels()
				.keySet()) {
			try {
				ConstructedModel parseModelIdentifier = ConstructedModelTracker
						.parseModelIdentifier(currentIdentifier);

				parseModelIdentifier.setIndividualLabel(UtilityFunctions
						.getIndividualLabelFromDAO(vitroRequest,
								parseModelIdentifier.getUri()));

				currentConstructedModels.add(parseModelIdentifier);
			} catch (IllegalConstructedModelIdentifierException e) {
				unidentifiedModels.add(e.getMessage());
			}
		}

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("title", "Regenerate Constructed Models");
		body.put("vivoDefaultNamespace", vitroRequest.getWebappDaoFactory()
				.getDefaultNamespace());
		body.put("currentModels", currentConstructedModels);
		body.put("unidentifiedModels", unidentifiedModels);

		return new TemplateResponseValues(standaloneTemplate, body);
	}

	private Map<String, String> regenerateConstructedModels(VitroRequest vitroRequest, 
															Dataset dataSource) {

		List<ConstructedModel> refreshedModels = new ArrayList<ConstructedModel>();

		Set<String> currentModelIdentifiers = new HashSet<String>(ConstructedModelTracker.getAllModels().keySet());
		
		for (String currentIdentifier : currentModelIdentifiers) {
			try {

				ConstructedModel parseModelIdentifier = ConstructedModelTracker
																.parseModelIdentifier(currentIdentifier);

				ConstructedModelTracker.removeModel(parseModelIdentifier.getUri(), 
													parseModelIdentifier.getType());

				ModelConstructorUtilities.getOrConstructModel(parseModelIdentifier.getUri(), 
															  parseModelIdentifier.getType(), dataSource);
				refreshedModels.add(parseModelIdentifier);

			} catch (IllegalConstructedModelIdentifierException e) {
				e.printStackTrace();
			} catch (MalformedQueryParametersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Map<String, String> fileData = new HashMap<String, String>();

		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
				"application/octet-stream");
		
		Gson json = new Gson();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
				json.toJson(refreshedModels));
		return fileData;
	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataSource)
			throws MalformedQueryParametersException {

		return renderRefreshCacheMarkup(vitroRequest, log, dataSource);
	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {

		return renderRefreshCacheMarkup(vitroRequest, log, dataSource);
	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		return REQUIRED_ACTIONS;
	}

}
