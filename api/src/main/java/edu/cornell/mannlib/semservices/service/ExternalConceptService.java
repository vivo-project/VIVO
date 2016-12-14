/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.service;

import java.util.List;

import edu.cornell.mannlib.semservices.bo.Concept;

public interface ExternalConceptService {

	/**
	 * @param term Term
	 */
	List<Concept> processResults(String term) throws Exception;

	/**
	 * @param term Term
	 * @throws Exception
	 */
	List<Concept> getConcepts(String term) throws Exception;

	/**
	 * @param uri URI
	 */
	List<Concept> getConceptsByURIWithSparql(String uri) throws Exception;

}
