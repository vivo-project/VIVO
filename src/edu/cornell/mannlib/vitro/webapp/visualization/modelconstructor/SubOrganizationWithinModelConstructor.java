/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;

public class SubOrganizationWithinModelConstructor implements ModelConstructor {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	public static final String MODEL_TYPE = "SUBORGANIZATION_WITHIN_HIERARCHY_MODEL"; 
	public static final String MODEL_TYPE_HUMAN_READABLE = "Bottom-up Organization hierarchy";
	
	private Dataset dataset;
	
	private Model constructedModel;
	
	private Log log = LogFactory.getLog(SubOrganizationWithinModelConstructor.class.getName());
	
	private long before, after;
	
	public SubOrganizationWithinModelConstructor(Dataset dataset) {
		this.dataset = dataset;
	}
	
	/**
	 * This constructor is present so that we can abstract out the model construction process.
	 * @param uri
	 * @param dataset
	 */
	public SubOrganizationWithinModelConstructor(String uri, Dataset dataset) {
		this.dataset = dataset;
	}
	
	private String constructAllSubOrganizationsWithinQuery() {
		return ""
		+ " CONSTRUCT { "
		+ "     ?organization rdf:type foaf:Organization . "
		+ "     ?organization rdfs:label ?organizationLabel . "
		+ "     ?organization <http://purl.obolibrary.org/obo/BFO_0000050> ?parentOrganization . "
	    + "     ?parentOrganization rdf:type foaf:Organization .     "
		+ "     ?parentOrganization rdfs:label ?parentOrganizationLabel .     "
		+ " }  "
		+ " WHERE { "
		+ "     ?organization rdf:type foaf:Organization . "
		+ "     ?organization rdfs:label ?organizationLabel . "
		+ "      "
		+ "     OPTIONAL { "
		+ "         ?organization <http://purl.obolibrary.org/obo/BFO_0000050> ?parentOrganization . "
	    + "         ?parentOrganization rdf:type foaf:Organization .     "
		+ "         ?parentOrganization rdfs:label ?parentOrganizationLabel . "
		+ "     }     "
		+ " } ";
		
	}
	
	private Model executeQuery(String constructQuery) {
		
		Model constructedModel = ModelFactory.createDefaultModel();

		before = System.currentTimeMillis();
		log.debug("CONSTRUCT query string : " + constructQuery);

		Query query = null;

		try {
			query = QueryFactory.create(QueryConstants.getSparqlPrefixQuery()
					+ constructQuery, SYNTAX);
		} catch (Throwable th) {
			log.error("Could not create CONSTRUCT SPARQL query for query "
					+ "string. " + th.getMessage());
			log.error(constructQuery);
		}

		QueryExecution qe = QueryExecutionFactory.create(query, dataset);

		try {
			qe.execConstruct(constructedModel);
		} finally {
			qe.close();
		}

		after = System.currentTimeMillis();
		log.debug("Time taken to execute the CONSTRUCT queries is in milliseconds: "
				+ (after - before));

		return constructedModel;
	}	
	
	public Model getConstructedModel() throws MalformedQueryParametersException {
	
		if (constructedModel != null && !constructedModel.isEmpty()) {
			return constructedModel;
		} else {
			constructedModel = executeQuery(constructAllSubOrganizationsWithinQuery());
			return constructedModel;
		}
		
	}

}
