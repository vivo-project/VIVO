/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.personlevel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coauthorship.CoAuthorshipQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coauthorship.CoAuthorshipVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coprincipalinvestigator.CoPIGrantCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coprincipalinvestigator.CoPIVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.persongrantcount.PersonGrantCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.persongrantcount.PersonGrantCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.personpubcount.PersonPublicationCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.personpubcount.PersonPublicationCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoPIData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Grant;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;

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
			DataSource dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Person Level does not provide Ajax Response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Person Level does not provide Data Response.");
	}
    
	
	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {

        String egoURI = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

        String renderMode = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.RENDER_MODE_KEY);
        
        String visMode = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.VIS_MODE_KEY);
        
        
        if (VisualizationFrameworkConstants.CO_PI_VIS.equalsIgnoreCase(visMode)) { 
        	
        	QueryRunner<CoPIData> coPIQueryManager = new CoPIGrantCountQueryRunner(egoURI, dataSource, log);
            
            QueryRunner<Set<Grant>> grantQueryManager = new PersonGrantCountQueryRunner(egoURI, dataSource, log);
            
            CoPIData coPIData = coPIQueryManager.getQueryResult();
            
	    	/*
	    	 * grants over time sparkline
	    	 */
			
			Set<Grant> piGrants = grantQueryManager.getQueryResult();
			
	    	/*
	    	 * Create a map from the year to number of grants. Use the Grant's
	    	 * parsedGrantYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToGrantCount = 
	    			UtilityFunctions.getYearToGrantCount(piGrants);	    	
	    	
	    	PersonGrantCountVisCodeGenerator personGrantCountVisCodeGenerator = 
	    		new PersonGrantCountVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_GRANT_SPARKLINE_VIS_CONTAINER_ID,
	    			piGrants,
	    			yearToGrantCount,
	    			log);
	    	
	    	SparklineData grantSparklineVO = personGrantCountVisCodeGenerator
			.getValueObjectContainer();
	    	
	    	
	    	/*
	    	 * Co-PI's over time sparkline
	    	 */
	    	CoPIVisCodeGenerator uniqueCopisVisCodeGenerator = 
	    		new CoPIVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COPIS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getGrantYearToCoPI(coPIData),
	    			log);
	    	
	    	SparklineData uniqueCopisSparklineVO = uniqueCopisVisCodeGenerator
			.getValueObjectContainer();
	    	
	    	
	    	return prepareCoPIStandaloneResponse(
					egoURI, 
					grantSparklineVO,
					uniqueCopisSparklineVO,
					coPIData,
	    			vitroRequest);
	    	
        	
        } else {
        	
        	QueryRunner<CoAuthorshipData> coAuthorshipQueryManager = new CoAuthorshipQueryRunner(egoURI, dataSource, log);
        
        	QueryRunner<Set<BiboDocument>> publicationQueryManager = new PersonPublicationCountQueryRunner(egoURI, dataSource, log);
        	
        	CoAuthorshipData coAuthorshipData = coAuthorshipQueryManager.getQueryResult();
        	
        	/*
			 * When the front-end for the person level vis has to be displayed we render couple of 
			 * sparklines. This will prepare all the data for the sparklines & other requested 
			 * files.
			 * */
			Set<BiboDocument> authorDocuments = publicationQueryManager.getQueryResult();
			
	    	/*
	    	 * Create a map from the year to number of publications. Use the BiboDocument's
	    	 * parsedPublicationYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToPublicationCount = 
	    			UtilityFunctions.getYearToPublicationCount(authorDocuments);
	    														
	    	/*
	    	 * Computations required to generate HTML for the sparklines & related context.
	    	 * */
	    	PersonPublicationCountVisCodeGenerator personPubCountVisCodeGenerator = 
	    		new PersonPublicationCountVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_PUB_SPARKLINE_VIS_CONTAINER_ID,
	    			authorDocuments,
	    			yearToPublicationCount,
	    			log);	  
	    	
	    	SparklineData publicationSparklineVO = personPubCountVisCodeGenerator
	    														.getValueObjectContainer();
	    	
            CoAuthorshipVisCodeGenerator uniqueCoauthorsVisCodeGenerator = 
	    		new CoAuthorshipVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getPublicationYearToCoAuthors(coAuthorshipData),
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
	
	private TemplateResponseValues prepareCoAuthorStandaloneResponse (
					String egoURI, 
					SparklineData egoPubSparklineVO, 
					SparklineData uniqueCoauthorsSparklineVO, 
					CoAuthorshipData coAuthorshipVO, 
					VitroRequest vitroRequest) {
		
		Map<String, Object> body = new HashMap<String, Object>();
		
        Portal portal = vitroRequest.getPortal();
        String	standaloneTemplate = "coAuthorPersonLevel.ftl";
        
        body.put("egoURIParam", egoURI);
        
        String title = "";
        
        if (coAuthorshipVO.getNodes() != null && coAuthorshipVO.getNodes().size() > 0) {
        	body.put("numOfAuthors", coAuthorshipVO.getNodes().size());
        	title = coAuthorshipVO.getEgoNode().getNodeName() + " - ";
		}
		
		if (coAuthorshipVO.getEdges() != null && coAuthorshipVO.getEdges().size() > 0) {
			body.put("numOfCoAuthorShips", coAuthorshipVO.getEdges().size());
		}
		
		body.put("egoPubSparklineVO", egoPubSparklineVO);
		body.put("uniqueCoauthorsSparklineVO", uniqueCoauthorsSparklineVO);

		body.put("portalBean", portal);
		body.put("title",  title + "Person Level Visualization");

		return new TemplateResponseValues(standaloneTemplate, body);
		
	}
	
	private TemplateResponseValues prepareCoPIStandaloneResponse (
					String egoURI, 
					SparklineData egoGrantSparklineVO, 
					SparklineData uniqueCopisSparklineVO, 
					CoPIData coPIVO, 
					VitroRequest vitroRequest) {
		
		Map<String, Object> body = new HashMap<String, Object>();

		Portal portal = vitroRequest.getPortal();
        
        body.put("egoURIParam", egoURI);
        
        String title = "";
        
        if (coPIVO.getNodes() != null && coPIVO.getNodes().size() > 0) {
        	body.put("numOfInvestigators", coPIVO.getNodes().size());
        	title = coPIVO.getEgoNode().getNodeName() + " - ";
		}
		
		if (coPIVO.getEdges() != null && coPIVO.getEdges().size() > 0) {
			body.put("numOfCoPIs", coPIVO.getEdges().size());
		}
		
        String	standaloneTemplate = "coPIPersonLevel.ftl";
		
		body.put("egoGrantSparklineVO", egoGrantSparklineVO);
		body.put("uniqueCopisSparklineVO", uniqueCopisSparklineVO);        	

		body.put("portalBean", portal);
		body.put("title",  title + "Person Level Visualization");

		return new TemplateResponseValues(standaloneTemplate, body);
		
	}

	private String getCompleteURL(HttpServletRequest request) throws MalformedURLException {
		
		String file = request.getRequestURI();
//		System.out.println("\ngetRequestURI() -->  "+ file + "\ngetQueryString() -->  "+request.getQueryString()+ "\ngetScheme() -->  "+ request.getScheme());
//		System.out.println("\ngetServerName() -->  "+ request.getServerName() + "\ngetServerPort() -->  "+request.getServerPort());

		URL reconstructedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), file);
		
//		System.out.println("\nReconstructed URL is -->  " + reconstructedURL);
		
		return reconstructedURL.toString();
	}

	
}
