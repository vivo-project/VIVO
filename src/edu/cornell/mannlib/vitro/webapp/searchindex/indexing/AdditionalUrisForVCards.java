/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.searchindex.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ContextModelsUser;

/**
 * If the property of a VCard object is changed, we should re-index the owner of
 * that VCard.
 */
public class AdditionalUrisForVCards implements IndexingUriFinder, ContextModelsUser {
	private static final Log log = LogFactory
			.getLog(AdditionalUrisForVCards.class);

	private final String QUERY_FOR_RELATED = "" //
			+ "prefix vcard: <http://www.w3.org/2006/vcard/ns#> \n"
			+ "prefix obo:   <http://purl.obolibrary.org/obo/>  \n"
			+ "SELECT ?uri WHERE { \n"
			+ "  ?subject a vcard:Identification .  \n "
			+ "  ?contactInfo ?p ?subject . \n "
			+ "  ?uri obo:ARG_2000028 ?contactInfo . \n " //
			+ "}";

    private RDFService rdfService;
    
    @Override
	public void setContextModels(ContextModelAccess models) {
    	this.rdfService = models.getRDFService();
	}

	@Override
	public void startIndexing() {
		// Nothing to set up.
	}

	@Override
	public List<String> findAdditionalURIsToIndex(Statement stmt) {
		if (stmt == null) {
			return Collections.emptyList();
		}

		RDFNode sNode = stmt.getSubject();
		if (sNode == null) {
			log.warn("subject of modified statement was null.");
			return Collections.emptyList();
		}

		if (!sNode.isURIResource()) {
			return Collections.emptyList();
		}

		Resource subject = sNode.asResource();
		String uri = subject.getURI();
		if (uri == null) {
			log.warn("subject of modified statement had a null URI.");
			return Collections.emptyList();
		}

		return ownersOfVCard(uri);
	}

	/**
	 * If the subject of the statement is a vcard:Identification, then we also
	 * want to index the owner of the vcard:ContactInfo that references this
	 * vcard:Identification.
	 * 
	 * vcard:Identification is a superclass of vcard:Name, vcard:Email,
	 * vcard:Telephone, vcard:Address, and vcard:URL.
	 * 
	 * @see https://wiki.duraspace.org/display/VIVO/VCard+usage+diagram
	 */
	private List<String> ownersOfVCard(String subjectUri) {
		List<String> additionalUris = new ArrayList<>();

		QuerySolutionMap initialBinding = new QuerySolutionMap();
		Resource subjectResource = ResourceFactory.createResource(subjectUri);
		initialBinding.add("subject", subjectResource);

		ResultSet results = QueryUtils.getQueryResults(QUERY_FOR_RELATED,
				initialBinding, rdfService);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			RDFNode node = soln.get("uri");
			if (node != null) {
				if (node.isURIResource()) {
					additionalUris.add(node.asResource().getURI());
				} else {
					log.warn("value from query for 'uri'"
							+ "  was not a URIResource, it was " + node);
				}
			} else {
				log.warn("value for query for 'uri' was null");
			}
		}

		return additionalUris;
	}

	@Override
	public void endIndexing() {
		// Nothing to tear down.
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
