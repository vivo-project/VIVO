/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class CoPIGrantCountRequestHandler implements VisualizationRequestHandler {


	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Co-PI Grant Count"
				+ " does not provide Ajax response.");
	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Co-PI Grant Count"
				+ " does not provide Short URL response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		String egoURI = vitroRequest.getParameter(VisualizationFrameworkConstants
														.INDIVIDUAL_URI_KEY);
		String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);

		ModelConstructor constructQueryRunner =
				new CoPIGrantCountConstructQueryRunner(egoURI, vitroRequest.getRDFService(), log);
		Model constructedModel = constructQueryRunner.getConstructedModel();

		QueryRunner<CollaborationData> queryManager =
				new CoPIGrantCountQueryRunner(egoURI, vitroRequest, log);

		CollaborationData investigatorNodesAndEdges = queryManager.getQueryResult();

    	/*
    	 * We will be using the same visualization package for both sparkline & co-pi
    	 * flash vis. We will use "VIS_MODE_KEY" as a modifier to differentiate
    	 * between these two. The default will be to render the co-pi network vis.
    	 * */
		if (VisualizationFrameworkConstants.COPIS_COUNT_PER_YEAR_VIS_MODE
				.equalsIgnoreCase(visMode)) {
			/*
			 * When the csv file is required - based on which sparkline visualization will
			 * be rendered.
			 * */
				return prepareCoPIsCountPerYearDataResponse(investigatorNodesAndEdges);

		} else if (VisualizationFrameworkConstants.COPIS_LIST_VIS_MODE
				.equalsIgnoreCase(visMode)) {
			/*
			 * When the csv file is required - based on which sparkline visualization will
			 * be rendered.
			 * */
				return prepareCoPIsListDataResponse(investigatorNodesAndEdges);

		} else if (VisualizationFrameworkConstants.COPI_NETWORK_DOWNLOAD_VIS_MODE
				.equalsIgnoreCase(visMode)) {
			/*
			 * When the csv file is required - based on which sparkline visualization will
			 * be rendered.
			 * */
				return prepareNetworkDownloadDataResponse(investigatorNodesAndEdges);

		} else {
    			/*
    			 * When the graphML file is required - based on which co-pi network
    			 * visualization will be rendered.
    			 * */
    			return prepareNetworkStreamDataResponse(investigatorNodesAndEdges);
		}

	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		/*
		 * Support for this has ceased to exist. Standalone mode was created only for demo
		 * purposes for VIVO Conf.
		 * */
		throw new UnsupportedOperationException("CoPI does not provide Standalone Response.");
	}

	private String getCoPIsListCSVContent(CollaborationData coPIData) {

		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Co-investigator, Count\n");

		for (Collaborator currNode : coPIData.getCollaborators()) {

			/*
			 * We have already printed the Ego Node info.
			 * */
			if (currNode != coPIData.getEgoCollaborator()) {

			csvFileContent.append(StringEscapeUtils.escapeCsv(currNode.getCollaboratorName()));
			csvFileContent.append(",");
			csvFileContent.append(currNode.getNumOfActivities());
			csvFileContent.append("\n");

			}

		}

		return csvFileContent.toString();
	}


	private String getCoPIsPerYearCSVContent(Map<String, Set<Collaborator>> yearToCoPI) {

		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Year, Count, Co-investigator(s)\n");

		for (Map.Entry<String, Set<Collaborator>> currentEntry : yearToCoPI.entrySet()) {

			csvFileContent.append(StringEscapeUtils.escapeCsv(currentEntry.getKey()));
			csvFileContent.append(",");
			csvFileContent.append(currentEntry.getValue().size());
			csvFileContent.append(",");
			csvFileContent.append(StringEscapeUtils.escapeCsv(
										getCoPINamesAsString(currentEntry.getValue())));
			csvFileContent.append("\n");
		}

		return csvFileContent.toString();
	}

	private String getCoPINamesAsString(Set<Collaborator> coInvestigators) {

		StringBuilder coPIsMerged = new StringBuilder();

		String coPISeparator = ";";
		for (Collaborator currentCoPI : coInvestigators) {
			coPIsMerged.append(currentCoPI.getCollaboratorName()).append(coPISeparator);
		}

		return StringUtils.removeEnd(coPIsMerged.toString(), coPISeparator);
	}


	/**
	 * Provides response when a csv file containing number & names of unique co-pis per
	 * year is requested.
	 * @param piNodesAndEdges PI nodes and edges
	 */
	private Map<String, String> prepareCoPIsCountPerYearDataResponse(
					CollaborationData piNodesAndEdges) {

		String outputFileName;
		Map<String, Set<Collaborator>> yearToCoPIs = new TreeMap<String, Set<Collaborator>>();

		if (piNodesAndEdges.getCollaborators() != null
				&& piNodesAndEdges.getCollaborators().size() > 0) {

			outputFileName = UtilityFunctions.slugify(piNodesAndEdges
									.getEgoCollaborator().getCollaboratorName())
			+ "_co-investigators-per-year" + ".csv";

			yearToCoPIs = UtilityFunctions.getActivityYearToCollaborators(piNodesAndEdges);

		} else {

			outputFileName = "no_co-investigators-per-year" + ".csv";
		}

        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY,
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 getCoPIsPerYearCSVContent(yearToCoPIs));

		return fileData;
	}

	/**
	 * Provides response when a csv file containing number & names of unique co-pis per
	 * year is requested.
	 * @param coPIData Co Principal Investigator Data
	 */
	private Map<String, String> prepareCoPIsListDataResponse(CollaborationData coPIData) {

		String outputFileName = "";

		if (coPIData.getCollaborators() != null && coPIData.getCollaborators().size() > 0) {

			outputFileName = UtilityFunctions.slugify(coPIData.getEgoCollaborator()
															.getCollaboratorName())
									+ "_co-investigators" + ".csv";

		} else {
			outputFileName = "no_co-investigators" + ".csv";
		}

        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY,
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 getCoPIsListCSVContent(coPIData));

		return fileData;
	}

	/**
	 * Provides a response when graphml formatted co-pi network is requested, typically by
	 * the flash vis.
	 * @param coPIData Co Investigator data
	 */
	private Map<String, String> prepareNetworkStreamDataResponse(CollaborationData coPIData) {

		CoPIGraphMLWriter coPIGraphMLWriter =
				new CoPIGraphMLWriter(coPIData);

        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "text/xml");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 coPIGraphMLWriter.getCoPIGraphMLContent().toString());

		return fileData;

	}

	private Map<String, String> prepareNetworkDownloadDataResponse(CollaborationData coPIData) {

		String outputFileName = "";

		if (coPIData.getCollaborators() != null && coPIData.getCollaborators().size() > 0) {

			outputFileName = UtilityFunctions.slugify(coPIData.getEgoCollaborator()
															.getCollaboratorName())
									+ "_co-investigator-network.graphml" + ".xml";

		} else {
			outputFileName = "no_co-investigator-network.graphml" + ".xml";
		}

		CoPIGraphMLWriter coPIGraphMLWriter =
				new CoPIGraphMLWriter(coPIData);

        Map<String, String> fileData = new HashMap<String, String>();
        fileData.put(DataVisualizationController.FILE_NAME_KEY,
				 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "text/xml");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 coPIGraphMLWriter.getCoPIGraphMLContent().toString());

		return fileData;

	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		return AuthorizationRequest.AUTHORIZED;
	}

}
