/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personlevel;

import java.util.HashMap;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CoInvestigationData;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.CollaborationDataViewHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import org.apache.jena.query.Dataset;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIGrantCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.persongrantcount.PersonGrantCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.PersonPublicationCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * This request handler is used to serve content rendered on the person level vis page
 * like,
 * 		1. Front end of the vis including the co-author & publication sparkline.
 * 		2. Downloadable file having the co-author network in graphml format.
 * 		3. Downloadable file having the list of co-authors that the individual has
 * worked with & count of such co-authorships.
 *
 * @author cdtank
 */
public class PersonLevelRequestHandler implements VisualizationRequestHandler {

    private static final String EGO_PUB_SPARKLINE_VIS_CONTAINER_ID = "ego_pub_sparkline";
    private static final String UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID =
    									"unique_coauthors_sparkline";
    private static final String EGO_GRANT_SPARKLINE_VIS_CONTAINER_ID = "ego_grant_sparkline";
    private static final String UNIQUE_COPIS_SPARKLINE_VIS_CONTAINER_ID =
    									"unique_copis_sparkline";

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Person Level does not provide Ajax Response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Person Level does not provide Data Response.");
	}


	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {

        String egoURI = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

        String visMode = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.VIS_MODE_KEY);

        if (!StringUtils.isEmpty(egoURI)) {
			return generateStandardVisualizationForPersonLevelVis(vitroRequest,
					log, dataset, egoURI, visMode);
		} else {
			return UtilityFunctions.handleMalformedParameters(
					"Visualization Query Error",
					"Inappropriate query parameters were submitted.",
					vitroRequest);
		}

	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {

		String egoURI = parameters.get(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		String visMode = parameters.get(VisualizationFrameworkConstants.VIS_MODE_KEY);

		if (!StringUtils.isEmpty(egoURI)) {
			return generateStandardVisualizationForPersonLevelVis(
							vitroRequest,
							log,
							dataset,
							egoURI,
							visMode);
		} else {
			return UtilityFunctions.handleMalformedParameters(
					"Visualization Query Error",
					"Inappropriate query parameters were submitted.",
					vitroRequest);
		}
	}

	private ResponseValues generateStandardVisualizationForPersonLevelVis(
			VitroRequest vitroRequest, Log log, Dataset dataset, String egoURI,
			String visMode) throws MalformedQueryParametersException {

		if (VisualizationFrameworkConstants.COPI_VIS_MODE.equalsIgnoreCase(visMode)) {
			CoPIGrantCountQueryRunner coPIQueryManager = new CoPIGrantCountQueryRunner(egoURI, vitroRequest, log);

			CoInvestigationData coPIData = coPIQueryManager.getQueryResult();

			Map<String, Activity> grantsToURI = coPIData.getGrants();

        	/*
        	 * Create a map from the year to number of grants. Use the Grant's
        	 * parsedGrantYear to populate the data.
        	 * */
        	Map<String, Integer> yearToGrantCount =
    			UtilityFunctions.getYearToActivityCount(grantsToURI.values());


	    	PersonGrantCountVisCodeGenerator personGrantCountVisCodeGenerator =
	    		new PersonGrantCountVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_GRANT_SPARKLINE_VIS_CONTAINER_ID,
	    			yearToGrantCount,
	    			log);

	    	SparklineData grantSparklineVO = personGrantCountVisCodeGenerator.getValueObjectContainer();


	    	/*
	    	 * Co-PI's over time sparkline
	    	 */
	    	CoPIVisCodeGenerator uniqueCopisVisCodeGenerator =
	    		new CoPIVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COPIS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getActivityYearToCollaborators(coPIData),
	    			log);

	    	SparklineData uniqueCopisSparklineVO = uniqueCopisVisCodeGenerator.getValueObjectContainer();

	    	return prepareCoPIStandaloneResponse(
					egoURI,
					grantSparklineVO,
					uniqueCopisSparklineVO,
					coPIData,
	    			vitroRequest);

        } else {

        	CoAuthorshipQueryRunner coAuthorshipQueryManager =
        			new CoAuthorshipQueryRunner(egoURI, vitroRequest, log);

			CoAuthorshipData coAuthorshipData = coAuthorshipQueryManager.getQueryResult();

			Map<String, Activity> publicationsToURI = coAuthorshipData.getDocuments();

	    	/*
	    	 * Create a map from the year to number of publications. Use the BiboDocument's
	    	 * parsedPublicationYear to populate the data.
	    	 * */
			Map<String, Integer> yearToPublicationCount =
					UtilityFunctions.getYearToActivityCount(publicationsToURI.values());
	    	/*
	    	 * Computations required to generate HTML for the sparklines & related context.
	    	 * */
	    	PersonPublicationCountVisCodeGenerator personPubCountVisCodeGenerator =
	    		new PersonPublicationCountVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_PUB_SPARKLINE_VIS_CONTAINER_ID,
	    			yearToPublicationCount,
	    			log);

	    	SparklineData publicationSparklineVO = personPubCountVisCodeGenerator
	    														.getValueObjectContainer();

            CoAuthorshipVisCodeGenerator uniqueCoauthorsVisCodeGenerator =
	    		new CoAuthorshipVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getActivityYearToCollaborators(coAuthorshipData),
	    			log);

	    	SparklineData uniqueCoauthorsSparklineVO = uniqueCoauthorsVisCodeGenerator
	    															.getValueObjectContainer();

	    	return prepareCoAuthorStandaloneResponse(
					egoURI,
	    			publicationSparklineVO,
	    			uniqueCoauthorsSparklineVO,
	    			coAuthorshipData,
	    			vitroRequest);

        }
	}

	private TemplateResponseValues prepareCoAuthorStandaloneResponse(
					String egoURI,
					SparklineData egoPubSparklineVO,
					SparklineData uniqueCoauthorsSparklineVO,
					CollaborationData coAuthorshipVO,
					VitroRequest vitroRequest) {

		Map<String, Object> body = new HashMap<String, Object>();

        String	standaloneTemplate = "coAuthorPersonLevelD3.ftl";
		body.put("coAuthorshipData", new CollaborationDataViewHelper(coAuthorshipVO));

		body.put("egoURIParam", egoURI);

        body.put("egoLocalName", UtilityFunctions.getIndividualLocalName(egoURI, vitroRequest));

        String title = "";

        if (coAuthorshipVO.getCollaborators() != null
        			&& coAuthorshipVO.getCollaborators().size() > 0) {
        	body.put("numOfAuthors", coAuthorshipVO.getCollaborators().size());
        	title = coAuthorshipVO.getEgoCollaborator().getCollaboratorName() + " - ";
		}

		if (coAuthorshipVO.getCollaborations() != null
					&& coAuthorshipVO.getCollaborations().size() > 0) {
			body.put("numOfCoAuthorShips", coAuthorshipVO.getCollaborations().size());
		}

		body.put("egoPubSparklineVO", egoPubSparklineVO);
		body.put("uniqueCoauthorsSparklineVO", uniqueCoauthorsSparklineVO);

		if (coAuthorshipVO.getBuiltFromCacheTime() != null) {
			body.put("builtFromCacheTime", coAuthorshipVO.getBuiltFromCacheTime());
		}

		body.put("title",  title + "Person Level Visualization");

		return new TemplateResponseValues(standaloneTemplate, body);

	}

	private TemplateResponseValues prepareCoPIStandaloneResponse(
					String egoURI,
					SparklineData egoGrantSparklineVO,
					SparklineData uniqueCopisSparklineVO,
					CollaborationData coPIVO,
					VitroRequest vitroRequest) {

		Map<String, Object> body = new HashMap<String, Object>();

        body.put("egoURIParam", egoURI);

        body.put("egoLocalName", UtilityFunctions.getIndividualLocalName(egoURI, vitroRequest));

        String title = "";

        if (coPIVO.getCollaborators() != null && coPIVO.getCollaborators().size() > 0) {
        	body.put("numOfInvestigators", coPIVO.getCollaborators().size());
        	title = coPIVO.getEgoCollaborator().getCollaboratorName() + " - ";
		}

		if (coPIVO.getCollaborations() != null && coPIVO.getCollaborations().size() > 0) {
			body.put("numOfCoInvestigations", coPIVO.getCollaborations().size());
		}

        String	standaloneTemplate = "coPIPersonLevelD3.ftl";
		body.put("coInvestigatorData", new CollaborationDataViewHelper(coPIVO));

		body.put("egoGrantSparklineVO", egoGrantSparklineVO);
		body.put("uniqueCoInvestigatorsSparklineVO", uniqueCopisSparklineVO);

		body.put("title",  title + "Person Level Visualization");

		return new TemplateResponseValues(standaloneTemplate, body);

	}

	@Override
	public AuthorizationRequest getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}
}
