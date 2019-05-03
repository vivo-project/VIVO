/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;
import org.vivoweb.webapp.util.ModelUtils;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.filestorage.FileServingHelper;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.AllPropertiesQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.GenericQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * This request handler is used when you need helpful information to add more context
 * to the visualization. It does not have any code for generating the visualization,
 * just fires sparql queries to get info for specific cases like,
 * 		1. thumbnail/image location for a particular individual
 * 		2. profile information for a particular individual like label, moniker etc
 * 		3. person level vis url for a particular individual
 * 		etc.
 * @author cdtank
 */
public class UtilitiesRequestHandler implements VisualizationRequestHandler {

	public Object generateAjaxVisualization(VitroRequest vitroRequest,
											Log log,
											Dataset dataset)
			throws MalformedQueryParametersException, JsonProcessingException {

        String individualURI = vitroRequest.getParameter(
        									VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

        String visMode = vitroRequest.getParameter(
        									VisualizationFrameworkConstants.VIS_MODE_KEY);

        /*
		 * If the info being requested is about a profile which includes the name, moniker
		 * & image url.
		 * */
		if (VisualizationFrameworkConstants.PROFILE_INFO_UTILS_VIS_MODE
					.equalsIgnoreCase(visMode)) {


			String filterRule = "?predicate = public:mainImage "
									+ " || ?predicate = rdfs:label "
									+ " || ?predicate =  <http://www.w3.org/2006/vcard/ns#title>";

			AllPropertiesQueryRunner profileQueryHandler =
					new AllPropertiesQueryRunner(individualURI,
												  filterRule,
												  vitroRequest.getRDFService(),
												  log);

			GenericQueryMap profilePropertiesToValues =
						profileQueryHandler.getQueryResult();

			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(profilePropertiesToValues);


		} else if (VisualizationFrameworkConstants.IMAGE_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {
			/*
    		 * If the url being requested is about a standalone image, which is used when we
    		 * want to render an image & other info for a co-author OR ego for that matter.
    		 * */

			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("downloadLocation",
											  QueryFieldLabels.THUMBNAIL_LOCATION_URL);
			fieldLabelToOutputFieldLabel.put("fileName", QueryFieldLabels.THUMBNAIL_FILENAME);

			String whereClause = "<" + individualURI
									+ "> public:thumbnailImage ?thumbnailImage .  "
									+ "?thumbnailImage public:downloadLocation "
									+ "?downloadLocation ; public:filename ?fileName .";



			GenericQueryRunner imageQueryHandler =
					new GenericQueryRunner(fieldLabelToOutputFieldLabel,
											"",
											whereClause,
											"",
											dataset);

			ThumbnailInformationConsumer consumer = new ThumbnailInformationConsumer(vitroRequest, fieldLabelToOutputFieldLabel);
			imageQueryHandler.sparqlSelectQuery(vitroRequest.getRDFService(), consumer);
			return consumer.getInformation();

		} else if (VisualizationFrameworkConstants.ARE_PUBLICATIONS_AVAILABLE_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {

			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();

			String aggregationRules = "(count(DISTINCT ?document) AS ?numOfPublications)";

			String whereClause =
				"<" + individualURI + "> rdf:type foaf:Person ;"
					+ " core:relatedBy ?authorshipNode . \n"
				+ "?authorshipNode rdf:type core:Authorship ;"
					+ " core:relates ?document . \n"
				+ "?document rdf:type bibo:Document .";

			String groupOrderClause = "GROUP BY ?" + QueryFieldLabels.AUTHOR_URL + " \n";

			GenericQueryRunner numberOfPublicationsQueryHandler =
			new GenericQueryRunner(fieldLabelToOutputFieldLabel,
									aggregationRules,
									whereClause,
									groupOrderClause,
									dataset);

			NumPubsForIndividualConsumer consumer = new NumPubsForIndividualConsumer();
			numberOfPublicationsQueryHandler.sparqlSelectQuery(vitroRequest.getRDFService(), consumer);

			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(consumer.getMap());

		} else if (VisualizationFrameworkConstants.ARE_GRANTS_AVAILABLE_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {

			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();

			String aggregationRules = "(count(DISTINCT ?Grant) AS ?numOfGrants)";
			String grantType = "http://vivoweb.org/ontology/core#Grant";

			ObjectProperty predicate = ModelUtils.getPropertyForRoleInClass(grantType, vitroRequest.getWebappDaoFactory());
			String roleToGrantPredicate = "<" + predicate.getURI() + ">";
			String whereClause = "{ <" + individualURI + "> rdf:type foaf:Person ;"
										+ " <http://purl.obolibrary.org/obo/RO_0000053> ?Role . \n"
									+ "?Role rdf:type core:PrincipalInvestigatorRole . \n"
									+ "?Role " + roleToGrantPredicate + " ?Grant . }"
									+ "UNION \n"
									+ "{ <" + individualURI + "> rdf:type foaf:Person ;"
										+ " <http://purl.obolibrary.org/obo/RO_0000053> ?Role . \n"
									+ "?Role rdf:type core:CoPrincipalInvestigatorRole . \n"
									+ "?Role " + roleToGrantPredicate + " ?Grant . }"
									+ "UNION \n"
									+ "{ <" + individualURI + "> rdf:type foaf:Person ;"
										+ " <http://purl.obolibrary.org/obo/RO_0000053> ?Role . \n"
									+ "?Role rdf:type core:InvestigatorRole. \n"
    								+ "?Role vitro:mostSpecificType ?subclass . \n"
									+ "?Role " + roleToGrantPredicate + " ?Grant . \n"
									+ "FILTER (?subclass != core:PrincipalInvestigatorRole && "
									+ "?subclass != core:CoPrincipalInvestigatorRole)}";

			GenericQueryRunner numberOfGrantsQueryHandler =
			new GenericQueryRunner(fieldLabelToOutputFieldLabel,
									aggregationRules,
									whereClause,
									"",
									dataset);

			NumGrantsForIndividualConsumer consumer = new NumGrantsForIndividualConsumer();
			numberOfGrantsQueryHandler.sparqlSelectQuery(vitroRequest.getRDFService(), consumer);

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(consumer.getMap());

		} else if (VisualizationFrameworkConstants.COAUTHOR_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {



			String individualLocalName = UtilityFunctions.getIndividualLocalName(
					individualURI,
					vitroRequest);

			if (StringUtils.isNotBlank(individualLocalName)) {

				return UrlBuilder.getUrl(VisualizationFrameworkConstants.SHORT_URL_VISUALIZATION_REQUEST_PREFIX)
				 			+ "/" + VisualizationFrameworkConstants.COAUTHORSHIP_VIS_SHORT_URL
				 			+ "/" + individualLocalName;

			}

			ParamMap coAuthorProfileURLParams = new ParamMap(
					VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
					individualURI,
					VisualizationFrameworkConstants.VIS_TYPE_KEY,
					VisualizationFrameworkConstants.PERSON_LEVEL_VIS,
					VisualizationFrameworkConstants.VIS_MODE_KEY,
					VisualizationFrameworkConstants.COAUTHOR_VIS_MODE);

			return UrlBuilder.getUrl(
						VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
						coAuthorProfileURLParams);

		} else if (VisualizationFrameworkConstants.COPI_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {


			String individualLocalName = UtilityFunctions.getIndividualLocalName(
					individualURI,
					vitroRequest);

			if (StringUtils.isNotBlank(individualLocalName)) {

				return UrlBuilder.getUrl(VisualizationFrameworkConstants.SHORT_URL_VISUALIZATION_REQUEST_PREFIX)
				 			+ "/" + VisualizationFrameworkConstants.COINVESTIGATOR_VIS_SHORT_URL
				 			+ "/" + individualLocalName;

			}

	    	/*
	    	 * By default we will be generating profile url else some specific url like
	    	 * coPI vis url for that individual.
	    	 * */
			ParamMap coInvestigatorProfileURLParams = new ParamMap(
								VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
								individualURI,
								VisualizationFrameworkConstants.VIS_TYPE_KEY,
								VisualizationFrameworkConstants.PERSON_LEVEL_VIS,
								VisualizationFrameworkConstants.VIS_MODE_KEY,
								VisualizationFrameworkConstants.COPI_VIS_MODE);

			return UrlBuilder.getUrl(
							VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
							coInvestigatorProfileURLParams);

		} else if (VisualizationFrameworkConstants.PERSON_LEVEL_UTILS_VIS_MODE
						.equalsIgnoreCase(visMode)) {
	    	/*
	    	 * By default we will be generating profile url else some specific url like
	    	 * coAuthorShip vis url for that individual.
	    	 * */
			ParamMap personLevelURLParams = new ParamMap(
							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
							individualURI,
							VisualizationFrameworkConstants.VIS_TYPE_KEY,
							VisualizationFrameworkConstants.PERSON_LEVEL_VIS,
							VisualizationFrameworkConstants.RENDER_MODE_KEY,
							VisualizationFrameworkConstants.STANDALONE_RENDER_MODE);

			return UrlBuilder.getUrl(
							VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
							personLevelURLParams);

		} else if (VisualizationFrameworkConstants.HIGHEST_LEVEL_ORGANIZATION_VIS_MODE
						.equalsIgnoreCase(visMode)) {

			String staffProvidedHighestLevelOrganization = ConfigurationProperties
						.getBean(vitroRequest).getProperty("visualization.topLevelOrg");

			/*
			 * First checking if the staff has provided highest level organization in
			 * deploy.properties if so use to temporal graph vis.
			 * */
			if (StringUtils.isNotBlank(staffProvidedHighestLevelOrganization)) {

				/*
	        	 * To test for the validity of the URI submitted.
	        	 * */
	        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
	    		IRI iri = iRIFactory.create(staffProvidedHighestLevelOrganization);

	    		if (iri.hasViolation(false)) {

	                String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
	                log.error("Highest Level Organization URI provided is invalid " + errorMsg);

	            } else {

					ParamMap highestLevelOrganizationTemporalGraphVisURLParams = new ParamMap(
							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
							staffProvidedHighestLevelOrganization,
							VisualizationFrameworkConstants.VIS_TYPE_KEY,
							VisualizationFrameworkConstants.ENTITY_COMPARISON_VIS);

					return UrlBuilder.getUrl(
							VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
							highestLevelOrganizationTemporalGraphVisURLParams);

	            }
			}

			Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
			fieldLabelToOutputFieldLabel.put("organization",
											  QueryFieldLabels.ORGANIZATION_URL);
			fieldLabelToOutputFieldLabel.put("organizationLabel",
											 QueryFieldLabels.ORGANIZATION_LABEL);

			String aggregationRules = "(count(?organization) AS ?numOfChildren)";

			String whereClause = "?organization rdf:type foaf:Organization ;"
								+ " rdfs:label ?organizationLabel . \n"
							    + "OPTIONAL { ?organization core:http://purl.obolibrary.org/obo/BFO_0000051 ?subOrg  . \n"
						        + "           ?subOrg rdf:type foaf:Organization } . \n"
							    + "OPTIONAL { ?organization core:http://purl.obolibrary.org/obo/BFO_0000050 ?parent } . \n"
				                + "           ?parent rdf:type foaf:Organization } . \n"
							    + "FILTER ( !bound(?parent) ). \n";

			String groupOrderClause = "GROUP BY ?organization ?organizationLabel \n"
										+ "ORDER BY DESC(?numOfChildren)\n"
										+ "LIMIT 1\n";

			GenericQueryRunner highestLevelOrganizationQueryHandler =
					new GenericQueryRunner(fieldLabelToOutputFieldLabel,
											aggregationRules,
											whereClause,
											groupOrderClause,
											dataset);

			HighetTopLevelOrgTemporalGraphURLConsumer consumer = new HighetTopLevelOrgTemporalGraphURLConsumer(vitroRequest, fieldLabelToOutputFieldLabel);
			highestLevelOrganizationQueryHandler.sparqlSelectQuery(vitroRequest.getRDFService(), consumer);
			return consumer.getTopLevelURL();

		} else {

			ParamMap individualProfileURLParams = new ParamMap(
							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
							individualURI);

			return UrlBuilder.getUrl(VisualizationFrameworkConstants.INDIVIDUAL_URL_PREFIX,
											individualProfileURLParams);
		}

	}

	private class HighetTopLevelOrgTemporalGraphURLConsumer extends ResultSetConsumer {
		private VitroRequest vitroRequest;
		private Map<String, String> fieldLabelToOutputFieldLabel;
		private String topLevelURL = null;

		HighetTopLevelOrgTemporalGraphURLConsumer(VitroRequest vitroRequest, Map<String, String> fieldLabelToOutputFieldLabel) {
			this.vitroRequest = vitroRequest;
			this.fieldLabelToOutputFieldLabel = fieldLabelToOutputFieldLabel;
		}

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			if (topLevelURL != null) {
				return;
			}

			RDFNode organizationNode = qs.get(fieldLabelToOutputFieldLabel.get("organization"));

			if (organizationNode != null) {
				String individualLocalName = UtilityFunctions.getIndividualLocalName(organizationNode.toString(), vitroRequest);

				if (StringUtils.isNotBlank(individualLocalName)) {

					topLevelURL = UrlBuilder.getUrl(VisualizationFrameworkConstants.SHORT_URL_VISUALIZATION_REQUEST_PREFIX)
							+ "/" + VisualizationFrameworkConstants.PUBLICATION_TEMPORAL_VIS_SHORT_URL
							+ "/" + individualLocalName;
				} else {

					ParamMap highestLevelOrganizationTemporalGraphVisURLParams = new ParamMap(
							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY,
							organizationNode.toString(),
							VisualizationFrameworkConstants.VIS_TYPE_KEY,
							VisualizationFrameworkConstants.ENTITY_COMPARISON_VIS);

					topLevelURL = UrlBuilder.getUrl(
							VisualizationFrameworkConstants.FREEMARKERIZED_VISUALIZATION_URL_PREFIX,
							highestLevelOrganizationTemporalGraphVisURLParams);

				}
			}
		}

		public String getTopLevelURL() {
			return topLevelURL == null ? "" : topLevelURL;
		}
	}

	private static class NumGrantsForIndividualConsumer extends ResultSetConsumer {
		GenericQueryMap queryResult = new GenericQueryMap();

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			RDFNode numberOfGrantsNode = qs.getLiteral("numOfGrants");

			if (numberOfGrantsNode != null) {
				queryResult.addEntry("numOfGrants", String.valueOf(numberOfGrantsNode.asLiteral().getInt()));
			}

		}

		public GenericQueryMap getMap() {
			return queryResult;
		}
	}

	private static class NumPubsForIndividualConsumer extends ResultSetConsumer {
		GenericQueryMap queryResult = new GenericQueryMap();

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			RDFNode numberOfPublicationsNode = qs.getLiteral("numOfPublications");

			if (numberOfPublicationsNode != null) {
				queryResult.addEntry("numOfPublications", String.valueOf(numberOfPublicationsNode.asLiteral().getInt()));
			}

		}

		public GenericQueryMap getMap() {
			return queryResult;
		}
	}

	private static class ThumbnailInformationConsumer extends ResultSetConsumer {
		private VitroRequest vitroRequest;
		private Map<String, String> fieldLabelToOutputFieldLabel;
		private String finalThumbNailLocation = "";

		ThumbnailInformationConsumer(VitroRequest vitroRequest, Map<String, String> fieldLabelToOutputFieldLabel) {
			this.vitroRequest = vitroRequest;
			this.fieldLabelToOutputFieldLabel = fieldLabelToOutputFieldLabel;
		}

		@Override
		protected void processQuerySolution(QuerySolution qs) {
			RDFNode downloadLocationNode = qs.get(
					fieldLabelToOutputFieldLabel
							.get("downloadLocation"));
			RDFNode fileNameNode = qs.get(fieldLabelToOutputFieldLabel.get("fileName"));

			if (downloadLocationNode != null && fileNameNode != null) {
				finalThumbNailLocation =
						FileServingHelper
								.getBytestreamAliasUrl(downloadLocationNode.toString(),
										fileNameNode.toString(),
										vitroRequest.getSession().getServletContext());
			}
		}

		public String getInformation() {
			return finalThumbNailLocation;
		}
	}

	private String getThumbnailInformation(ResultSet resultSet,
										   Map<String, String> fieldLabelToOutputFieldLabel,
										   VitroRequest vitroRequest) {

		String finalThumbNailLocation = "";

		while (resultSet.hasNext())  {
			QuerySolution solution = resultSet.nextSolution();


		}
		return finalThumbNailLocation;
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Utilities does not provide Data Response.");
	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Utilities does not provide Standard Response.");
	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Utilities Visualization does not provide "
					+ "Short URL Response.");
	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}
}

