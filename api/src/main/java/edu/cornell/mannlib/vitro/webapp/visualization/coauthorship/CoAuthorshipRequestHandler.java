/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.coauthorship;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import org.apache.jena.query.Dataset;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * This request handler is used when information related to co-authorship network
 * for an individual is requested. It currently provides 2 outputs,
 * 		1. Graphml content representing the individual's co-authorship network
 * 		1. CSV file containing the list(& count) of unique co-authors with which
 * the individual has worked over the years. This data powers the related sparkline.
 *
 * @author cdtank
 */
public class CoAuthorshipRequestHandler implements VisualizationRequestHandler {

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("CoAuthorship does not provide Ajax Response.");
	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("CoAuthorship does not provide Short URL Response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {


		String egoURI = vitroRequest.getParameter(
				VisualizationFrameworkConstants
						.INDIVIDUAL_URI_KEY);

		String visMode = vitroRequest.getParameter(
						VisualizationFrameworkConstants
								.VIS_MODE_KEY);

		CoAuthorshipQueryRunner queryManager =
		new CoAuthorshipQueryRunner(egoURI, vitroRequest, log);

		CollaborationData authorNodesAndEdges =
		queryManager.getQueryResult();

    	/*
    	 * We will be using the same visualization package for both sparkline & coauthorship
    	 * flash vis. We will use "VIS_MODE_KEY" as a modifier to differentiate
    	 * between these two. The default will be to render the coauthorship network vis.
    	 * */
		if (VisualizationFrameworkConstants.COAUTHORS_COUNT_PER_YEAR_VIS_MODE
				.equalsIgnoreCase(visMode)) {
			/*
			 * When the csv file is required - based on which sparkline visualization will
			 * be rendered.
			 * */
				return prepareCoauthorsCountPerYearDataResponse(authorNodesAndEdges);

		} else if (VisualizationFrameworkConstants.COAUTHORS_LIST_VIS_MODE
				.equalsIgnoreCase(visMode)) {
			/*
			 * When the csv file is required - based on which sparkline visualization will
			 * be rendered.
			 * */
				return prepareCoauthorsListDataResponse(authorNodesAndEdges);

		} else if (VisualizationFrameworkConstants.COAUTHOR_NETWORK_DOWNLOAD_VIS_MODE
				.equalsIgnoreCase(visMode)) {
			/*
			 * When the csv file is required - based on which sparkline visualization will
			 * be rendered.
			 * */
				return prepareNetworkDownloadDataResponse(authorNodesAndEdges);

		} else {
    			/*
    			 * When the graphML file is required - based on which coauthorship network
    			 * visualization will be rendered.
    			 * */
    			return prepareNetworkStreamDataResponse(authorNodesAndEdges);
		}

	}

	public ResponseValues generateStandardVisualization(VitroRequest vitroRequest,
											  	Log log,
											    Dataset dataset)
		throws MalformedQueryParametersException {

		throw new UnsupportedOperationException("CoAuthorship Visualization "
						+ "does not provide Standalone response.");
	}

	private String getCoauthorsListCSVContent(CollaborationData coAuthorshipData) {

		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Co-author, Count\n");

		for (Collaborator currNode : coAuthorshipData.getCollaborators()) {
			/*
			 * We have already printed the Ego Node info.
			 * */
			if (currNode != coAuthorshipData.getEgoCollaborator()) {


			csvFileContent.append(StringEscapeUtils.escapeCsv(currNode.getCollaboratorName()));
			csvFileContent.append(",");
			csvFileContent.append(currNode.getNumOfActivities());
			csvFileContent.append("\n");

			}
		}

		return csvFileContent.toString();
	}

	private String getCoauthorsPerYearCSVContent(Map<String, Set<Collaborator>> yearToCoauthors) {

		StringBuilder csvFileContent = new StringBuilder();

		csvFileContent.append("Year, Count, Co-author(s)\n");

		for (Entry<String, Set<Collaborator>> currentEntry : yearToCoauthors.entrySet()) {
			csvFileContent.append(StringEscapeUtils.escapeCsv(currentEntry.getKey()));
			csvFileContent.append(",");
			csvFileContent.append(currentEntry.getValue().size());
			csvFileContent.append(",");
			csvFileContent.append(StringEscapeUtils.escapeCsv(
										getCoauthorNamesAsString(currentEntry.getValue())));
			csvFileContent.append("\n");
		}

		return csvFileContent.toString();

	}

	private String getCoauthorNamesAsString(Set<Collaborator> coAuthors) {

		StringBuilder coAuthorsMerged = new StringBuilder();

		String coAuthorSeparator = "; ";
		for (Collaborator currCoAuthor : coAuthors) {
			coAuthorsMerged.append(currCoAuthor.getCollaboratorName()).append(coAuthorSeparator);
		}

		return StringUtils.removeEnd(coAuthorsMerged.toString(), coAuthorSeparator);
	}

	/**
	 * Provides response when a csv file containing number & names of unique co-authors per
	 * year is requested.
	 * @param authorNodesAndEdges Author nodes and edges
	 */
	private Map<String, String> prepareCoauthorsCountPerYearDataResponse(
					CollaborationData authorNodesAndEdges) {

		String outputFileName;
		Map<String, Set<Collaborator>> yearToCoauthors = new TreeMap<String, Set<Collaborator>>();

		if (authorNodesAndEdges.getCollaborators() != null
					&& authorNodesAndEdges.getCollaborators().size() > 0) {

			outputFileName = UtilityFunctions.slugify(authorNodesAndEdges
									.getEgoCollaborator().getCollaboratorName())
			+ "_co-authors-per-year" + ".csv";

			yearToCoauthors = UtilityFunctions.getActivityYearToCollaborators(authorNodesAndEdges);

		} else {

			outputFileName = "no_co-authors-per-year" + ".csv";
		}

        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY,
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 getCoauthorsPerYearCSVContent(yearToCoauthors));

		return fileData;
	}

	/**
	 * Provides response when a csv file containing number & names of unique co-authors per
	 * year is requested.
	 * @param coAuthorshipData   Co authorship data
	 */
	private Map<String, String> prepareCoauthorsListDataResponse(
					CollaborationData coAuthorshipData) {

		String outputFileName = "";

		if (coAuthorshipData.getCollaborators() != null
					&& coAuthorshipData.getCollaborators().size() > 0) {

			outputFileName = UtilityFunctions.slugify(coAuthorshipData.getEgoCollaborator()
															.getCollaboratorName())
									+ "_co-authors" + ".csv";
		} else {
			outputFileName = "no_co-authors" + ".csv";
		}

        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY,
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 getCoauthorsListCSVContent(coAuthorshipData));

		return fileData;
	}

	/**
	 * Provides a response when graphml formatted co-authorship network is requested, typically by
	 * the flash vis.
	 * @param authorNodesAndEdges Author nodes and edges
	 */
	private Map<String, String> prepareNetworkStreamDataResponse(
									CollaborationData authorNodesAndEdges) {

		CoAuthorshipGraphMLWriter coAuthorshipGraphMLWriter =
				new CoAuthorshipGraphMLWriter(authorNodesAndEdges);

        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "text/xml");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 coAuthorshipGraphMLWriter.getCoAuthorshipGraphMLContent().toString());

		return fileData;

	}

	private Map<String, String> prepareNetworkDownloadDataResponse(
									CollaborationData authorNodesAndEdges) {

		String outputFileName = "";

		if (authorNodesAndEdges.getCollaborators() != null
					&& authorNodesAndEdges.getCollaborators().size() > 0) {

			outputFileName = UtilityFunctions.slugify(authorNodesAndEdges
									.getEgoCollaborator().getCollaboratorName())
									+ "_co-author-network.graphml" + ".xml";

		} else {
			outputFileName = "no_co-author-network.graphml" + ".xml";
		}

		CoAuthorshipGraphMLWriter coAuthorshipGraphMLWriter =
				new CoAuthorshipGraphMLWriter(authorNodesAndEdges);

        Map<String, String> fileData = new HashMap<String, String>();
        fileData.put(DataVisualizationController.FILE_NAME_KEY,
				 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
					 "text/xml");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
					 coAuthorshipGraphMLWriter.getCoAuthorshipGraphMLContent().toString());

		return fileData;
	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		return null;
	}

}
