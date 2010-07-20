/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UniqueIDGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;

/**
 * 
 * This is the Value Object for storing node information mainly for co-author vis.
 * @author cdtank
 *
 */
public class Node extends Individual {

	private int nodeID;
	private Map<String, Integer> yearToPublicationCount;

	private Set<BiboDocument> authorDocuments = new HashSet<BiboDocument>();

	public Node(String nodeURL,
				UniqueIDGenerator uniqueIDGenerator) {
		super(nodeURL);
		nodeID = uniqueIDGenerator.getNextNumericID();
	}

	public int getNodeID() {
		return nodeID;
	}
	
	public String getNodeURL() {
		return this.getIndividualURL();
	}

	public String getNodeName() {
		return this.getIndividualLabel();
	}
	
	public void setNodeName(String nodeName) {
		this.setIndividualLabel(nodeName);
	}
	
	public Set<BiboDocument> getAuthorDocuments() {
		return authorDocuments;
	}
	
	public int getNumOfAuthoredWorks() {
		return authorDocuments.size();
	}

	public void addAuthorDocument(BiboDocument authorDocument) {
		this.authorDocuments.add(authorDocument);
	}
	
	
	public Map<String, Integer> getYearToPublicationCount() {
		if (yearToPublicationCount == null) {
			yearToPublicationCount = UtilityFunctions.getYearToPublicationCount(authorDocuments);
		}
		return yearToPublicationCount;
	}
	/*
	 * getEarliest, Latest & Unknown Publication YearCount should only be used after 
	 * the parsing of the entire sparql is done. Else it will give results based on
	 * incomplete dataset.
	 * */
	@SuppressWarnings("serial")
	public Map<String, Integer> getEarliestPublicationYearCount() {
		if (yearToPublicationCount == null) {
			yearToPublicationCount = UtilityFunctions.getYearToPublicationCount(authorDocuments);
		}
		
		/*
		 * We do not want to consider the default publication year when we are checking 
		 * for the min or max publication year. 
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(yearToPublicationCount.keySet());
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
			final Integer earliestYearPubCount = yearToPublicationCount.get(earliestYear);
			
			return new HashMap<String, Integer>(){{
				put(earliestYear, earliestYearPubCount);
			}};
		} else {
			return null;
		}
	}

	@SuppressWarnings("serial")
	public Map<String, Integer> getLatestPublicationYearCount() {
		if (yearToPublicationCount == null) {
			yearToPublicationCount = UtilityFunctions.getYearToPublicationCount(authorDocuments);
		}
		
		/*
		 * We do not want to consider the default publication year when we are checking 
		 * for the min or max publication year. 
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(yearToPublicationCount.keySet());
		yearsToBeConsidered.remove(VOConstants.DEFAULT_PUBLICATION_YEAR);
		
		/*
		 * There can be a case when the only publication the author has no attached year to it
		 * so essentially an "Unknown". In that case Collections.max or min will throw an 
		 * NoSuchElementException.
		 * 
		 * If there is no maximum year available then we should imply so by returning a "null".
		 * */
		if (yearsToBeConsidered.size() > 0) {
			final String latestYear = Collections.max(yearsToBeConsidered);
			final Integer latestYearPubCount = yearToPublicationCount.get(latestYear);
			
			return new HashMap<String, Integer>(){{
				put(latestYear, latestYearPubCount);
			}};
		} else {
			return null;
		}
		
	}
	
	public Integer getUnknownPublicationYearCount() {
		if (yearToPublicationCount == null) {
			yearToPublicationCount = UtilityFunctions.getYearToPublicationCount(authorDocuments);
		}
		
		Integer unknownYearPubCount = yearToPublicationCount.get(VOConstants.DEFAULT_PUBLICATION_YEAR);
		
		/*
		 * If there is no unknown year available then we should imply so by returning a "null".
		 * */
		if (unknownYearPubCount != null) {
			return unknownYearPubCount;
		} else {
			return null;
		}
	}
	

}
