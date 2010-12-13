/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UniqueIDGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.UtilityFunctions;

/**
 * This stores edge information for Co-PI vis.
 * @author bkoniden
 * Deepak Konidena
 */
public class CoPIEdge {
	
	private int edgeID;
	private Map<String, Integer> yearToGrantCount;
	private Set<Grant> collaboratorGrants = new HashSet<Grant>();
	private CoPINode sourceNode;
	private CoPINode targetNode;
	
	public CoPIEdge(CoPINode sourceNode, CoPINode targetNode, Grant seedCoPIedGrant, UniqueIDGenerator uniqueIDGenerator){
		edgeID = uniqueIDGenerator.getNextNumericID();
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.collaboratorGrants.add(seedCoPIedGrant);
	}
	
	public int getEdgeID() {
		return edgeID;
	}
	public Set<Grant> getCollaboratorGrants() {
		return collaboratorGrants;
	}
	public CoPINode getSourceNode() {
		return sourceNode;
	}
	public CoPINode getTargetNode() {
		return targetNode;
	}
	
	public int getNumberOfCoInvestigatedGrants(){
		return collaboratorGrants.size();
	}
	
	public void addCollaboratorGrant(Grant grant){
		this.collaboratorGrants.add(grant);
	}
	
	/*
	 * getEarliest, Latest & Unknown Grant YearCount should only be used after 
	 * the parsing of the entire sparql is done. Else it will give results based on
	 * incomplete dataset.
	 * */
	@SuppressWarnings("serial")
	public Map<String, Integer> getEarliestCollaborationYearCount() {
		if (yearToGrantCount == null) {
			yearToGrantCount = UtilityFunctions.getYearToGrantCount(collaboratorGrants);
		}
		
		/*
		 * We do not want to consider the default grant year when we are checking 
		 * for the min or max grant year. 
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(yearToGrantCount.keySet());
		yearsToBeConsidered.remove(VOConstants.DEFAULT_PUBLICATION_YEAR);
		
		/*
		 * There can be a case when the only publication the author has no attached year to it
		 * so essentially an "Unknown". In that case Collections.max or min will throw an 
		 * NoSuchElementException.
		 * 
		 * If there is no maximum year available then we should imply so by returning a "null".
		 * */
		if (yearsToBeConsidered.size() > 0) {
			final String earliestYear = Collections.min(yearsToBeConsidered);
			final Integer earliestYearGrantCount = yearToGrantCount.get(earliestYear);
			
			return new HashMap<String, Integer>() { {
				put(earliestYear, earliestYearGrantCount);
			} };
		} else {
			return null;
		}
	}
	
	
	@SuppressWarnings("serial")
	public Map<String, Integer> getLatestCollaborationYearCount() {
		if (yearToGrantCount == null) {
			yearToGrantCount = UtilityFunctions.getYearToGrantCount(collaboratorGrants);
		}
		
		/*
		 * We do not want to consider the default grant year when we are checking 
		 * for the min or max grant year. 
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(yearToGrantCount.keySet());
		yearsToBeConsidered.remove(VOConstants.DEFAULT_PUBLICATION_YEAR);
		
		/*
		 * There can be a case when the only grant the PI has no attached year to it
		 * so essentially an "Unknown". In that case Collections.max or min will throw an 
		 * NoSuchElementException.
		 * 
		 * If there is no maximum year available then we should imply so by returning a "null".
		 * */
		if (yearsToBeConsidered.size() > 0) {
			final String latestYear = Collections.max(yearsToBeConsidered);
			final Integer latestYearGrantCount = yearToGrantCount.get(latestYear);
			
			return new HashMap<String, Integer>() { {
				put(latestYear, latestYearGrantCount);
			} };
		} else {
			return null;
		}
		
	}
	
	public Integer getUnknownCollaborationYearCount() {
		if (yearToGrantCount == null) {
			yearToGrantCount = UtilityFunctions.getYearToGrantCount(collaboratorGrants);
		}
		
		Integer unknownYearGrantCount = yearToGrantCount
											.get(VOConstants.DEFAULT_PUBLICATION_YEAR);
		
		/*
		 * If there is no unknown year available then we should imply so by returning a "null".
		 * */
		if (unknownYearGrantCount != null) {
			return unknownYearGrantCount;
		} else {
			return null;
		}
	}
	
}
