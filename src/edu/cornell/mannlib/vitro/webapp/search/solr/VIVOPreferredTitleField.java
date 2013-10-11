/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.search.solr;

import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.ALLTEXT;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.ALLTEXTUNSTEMMED;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.PREFERRED_TITLE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;
import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.DocumentModifier;

/**
 * If there are any VCards on this Individual with Title objects, store the text
 * in the Preferred Title search field.
 */
public class VIVOPreferredTitleField implements DocumentModifier {
	private static final Log log = LogFactory
			.getLog(VIVOPreferredTitleField.class);

	private static final String QUERY_TEMPLATE = ""
			+ "prefix vcard: <http://www.w3.org/2006/vcard/ns#> \n"
			+ "prefix obo: <http://purl.obolibrary.org/obo/> \n\n"
			+ "SELECT ?title WHERE { \n" //
			+ "  ?uri obo:ARG_2000028 ?card . \n"
			+ "  ?card a vcard:Individual . \n"
			+ "  ?card vcard:hasTitle ?titleHolder . \n"
			+ "  ?titleHolder vcard:title ?title . \n" //
			+ "}";

	private RDFServiceFactory rdfServiceFactory;
	private boolean shutdown = false;

	public VIVOPreferredTitleField(RDFServiceFactory rdfServiceFactory) {
		this.rdfServiceFactory = rdfServiceFactory;
	}

	@Override
	public void modifyDocument(Individual individual, SolrInputDocument doc,
			StringBuffer addUri) {
		if (individual == null)
			return;

		String uri = "<" + individual.getURI() + "> ";
		String query = QUERY_TEMPLATE.replaceAll("\\?uri", uri);
		log.debug("Get preferred title(s) for " + uri);

		try {
			RDFService rdfService = rdfServiceFactory.getRDFService();
			ResultSet results = RDFServiceUtils.sparqlSelectQuery(query,
					rdfService);
			if (results != null) {
				while (results.hasNext()) {
					log.debug("Next solution");
					RDFNode node = results.nextSolution().get("title");
					if ((node != null) && (node.isLiteral())) {
						String title = node.asLiteral().getString();
						doc.addField(PREFERRED_TITLE, title);
						doc.addField(ALLTEXT, title);
						doc.addField(ALLTEXTUNSTEMMED, title);
						log.debug("Preferred Title for " + uri + ": '" + title
								+ "', '" + node.toString() + "'");
					}
				}
			}
		} catch (Exception e) {
			if (!shutdown) {
				log.error("problem while running query '" + query + "'", e);
			}
		}
	}

	@Override
	public void shutdown() {
		shutdown = true;
	}

}
