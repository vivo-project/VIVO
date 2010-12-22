/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.coprincipalinvestigator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.DataSource;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoPIData;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.CoPINode;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;
/**
 * @author bkoniden
 * Deepak Konidena
 */
public class CoPIGrantCountRequestHandler implements VisualizationRequestHandler{
	

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			DataSource dataSource) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Co-PI Grant Count does not provide Ajax Response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {
		
		String egoURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		String visMode = vitroRequest.getParameter(VisualizationFrameworkConstants.VIS_MODE_KEY);
		
		QueryRunner<CoPIData> queryManager = new CoPIGrantCountQueryRunner(egoURI, dataSource, log);
		
		CoPIData PINodesAndEdges = queryManager.getQueryResult();
		
			if (VisualizationFrameworkConstants.COPI_VIS_MODE
					.equalsIgnoreCase(visMode)) { 

					return prepareCoPIDataResponse(PINodesAndEdges);
	    		
			} else {
	    			/*
	    			 * When the graphML file is required - based on which copi network 
	    			 * visualization will be rendered.
	    			 * */
	    			return prepareNetworkDataResponse(PINodesAndEdges);
					
			}
	}
	
	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, DataSource dataSource)
			throws MalformedQueryParametersException {
		
		String egoURI = vitroRequest.getParameter(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);
		
		QueryRunner<CoPIData> queryManager = new CoPIGrantCountQueryRunner(egoURI, dataSource, log);
		
		CoPIData PINodesAndEdges = queryManager.getQueryResult();
		
		return prepareStandaloneResponse(egoURI,
				 						  PINodesAndEdges,
				 						  vitroRequest);
	}
	
	/**
	 * When the page for person level visualization is requested.
	 * @param egoURI
	 * @param vitroRequest
	 * @param coPIVO
	 */
	private TemplateResponseValues prepareStandaloneResponse(String egoURI,
			CoPIData pINodesAndEdges, VitroRequest vitroRequest) {
		
        Portal portal = vitroRequest.getPortal();
        
        String title = "";
        Map<String, Object> body = new HashMap<String, Object>();


        if (pINodesAndEdges.getNodes() != null
				&& pINodesAndEdges.getNodes().size() > 0) {
        	body.put("numOfAuthors", pINodesAndEdges.getNodes().size());
			
			title = pINodesAndEdges.getEgoNode().getNodeName() + " - ";
		}

		if (pINodesAndEdges.getEdges() != null
				&& pINodesAndEdges.getEdges().size() > 0) {
			body.put("numOfCoPIs", pINodesAndEdges.getEdges().size());
		}

		String standaloneTemplate = "/visualization/coauthorship/coAuthorship.ftl";
		
        body.put("portalBean", portal);
        body.put("egoURIParam", egoURI);
        body.put("title", title + "Co-PI Visualization");
        
        return new TemplateResponseValues(standaloneTemplate, body);
	}

	/**
	 * Provides a response when graphml formatted co-pi network is requested, typically by 
	 * the flash vis.
	 * @param pINodesAndEdges
	 */
	private Map<String, String> prepareNetworkDataResponse(CoPIData pINodesAndEdges) {
		
		CoPIGraphMLWriter coPIGraphMLWriter = 
				new CoPIGraphMLWriter(pINodesAndEdges);
		
	    Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "text/xml");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 coPIGraphMLWriter.getCoPIGraphMLContent().toString());
	
		return fileData;
	}
	
	/**
	 * Provides response when a csv file containing number & names of unique co-pis per 
	 * year is requested. 
	 * @param pINodesAndEdges
	 */
	private Map<String, String> prepareCoPIDataResponse(CoPIData pINodesAndEdges) {
		
		String outputFileName;
		Map<String, Set<CoPINode>> yearToCoPI = new TreeMap<String, Set<CoPINode>>();
		
		if (pINodesAndEdges.getNodes() != null && pINodesAndEdges.getNodes().size() > 0) {
			
			outputFileName = UtilityFunctions.slugify(pINodesAndEdges
									.getEgoNode().getNodeName())
			+ "_copis-per-year" + ".csv";
			
			yearToCoPI = UtilityFunctions.getGrantYearToCoPI(pINodesAndEdges);
			
		} else {
			
			outputFileName = "no_copis-per-year" + ".csv";			
		}
		
		
        Map<String, String> fileData = new HashMap<String, String>();
		fileData.put(DataVisualizationController.FILE_NAME_KEY, 
					 outputFileName);
		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY, 
					 "application/octet-stream");
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY, 
					 getCoPIsPerYearCSVContent(yearToCoPI));

		return fileData;
	}

	private String getCoPIsPerYearCSVContent(Map<String, Set<CoPINode>> yearToCoPI) {

		StringBuilder csvFileContent = new StringBuilder();
		
		csvFileContent.append("Year, Count, Co-PI(s)\n");

		for (Map.Entry<String, Set<CoPINode>> currentEntry : yearToCoPI.entrySet()) {
			
			csvFileContent.append(StringEscapeUtils.escapeCsv(currentEntry.getKey()));
			csvFileContent.append(",");
			csvFileContent.append(currentEntry.getValue().size());
			csvFileContent.append(",");
			csvFileContent.append(StringEscapeUtils.escapeCsv(getCoPINamesAsString(currentEntry.getValue())));
			csvFileContent.append("\n");
		}
		
		return csvFileContent.toString();
	}

	private String getCoPINamesAsString(Set<CoPINode> CoPIs) {
		
		StringBuilder coPIsMerged = new StringBuilder();
		
		String coPISeparator = ";";
		for(CoPINode currentCoPI : CoPIs){
			coPIsMerged.append(currentCoPI.getNodeName() + coPISeparator);
		}
		
		return StringUtils.removeEnd(coPIsMerged.toString(), coPISeparator);
	}

}
