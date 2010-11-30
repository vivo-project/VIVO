/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UniqueIDGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;

/**
 * CoPINode is the node in a CoPI vis.
 * @author bkoniden
 * Deepak Konidena
 */
public class CoPINode extends Individual {
	
	private int nodeID;
	private Map<String, Integer> yearToGrantCount;
	
	private Set<Grant> pIGrants = new HashSet<Grant>();
	
	public CoPINode(String nodeURI, UniqueIDGenerator uniqueIDGenerator){
		super(nodeURI);
		nodeID = uniqueIDGenerator.getNextNumericID();
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public String getNodeURI(){
		return this.getIndividualURI();
	}
	
	public String getNodeName(){
		return this.getIndividualLabel();
	}
	
	public void setNodeName(String nodeName) {
		this.setIndividualLabel(nodeName);
	}
	
	public Set<Grant> getInvestigatedGrants(){
		return pIGrants;
	}
	
	public int getNumberOfInvestigatedGrants(){
		return pIGrants.size();
	}
	
	public void addGrant(Grant grant){
		this.pIGrants.add(grant);
	}
	
	public Map<String, Integer> getYearToGrantCount(){
		if(yearToGrantCount == null){
			yearToGrantCount = UtilityFunctions.getYearToGrantCount(pIGrants);
		}
		return yearToGrantCount;
	}
	
	/*
	 * getEarliest, Latest & Unknown Grant YearCount should only be used after 
	 * the parsing of the entire sparql is done. Else it will give results based on
	 * incomplete dataset.
	 * */
	@SuppressWarnings("serial")
	public Map<String, Integer> getEarliestGrantYearCount() {
		if (yearToGrantCount == null) {
			yearToGrantCount = UtilityFunctions.getYearToGrantCount(pIGrants);
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
	public Map<String, Integer> getLatestGrantYearCount() {
		if (yearToGrantCount == null) {
			yearToGrantCount = UtilityFunctions.getYearToGrantCount(pIGrants);
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
	
	public Integer getUnknownGrantYearCount() {
		if (yearToGrantCount == null) {
			yearToGrantCount = UtilityFunctions.getYearToGrantCount(pIGrants);
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
