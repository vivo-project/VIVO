/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.service;

import java.util.List;

import edu.cornell.mannlib.semservices.bo.Concept;

public interface ExternalConceptService {

	/**
	 * @param term
	 * @return
	 */
	List<Concept> processResults(String term) throws Exception;

	/**
	 * @param term
	 * @return
	 * @throws Exception
	 */
	List<Concept> getConcepts(String term) throws Exception;

	/**
	 * @param uri
	 * @return
	 */
	List<Concept> getConceptsByURIWithSparql(String uri) throws Exception;

}
