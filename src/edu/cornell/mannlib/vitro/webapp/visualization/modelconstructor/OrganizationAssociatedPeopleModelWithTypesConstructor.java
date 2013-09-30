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

public class OrganizationAssociatedPeopleModelWithTypesConstructor implements ModelConstructor {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	public static final String MODEL_TYPE = "ORGANIZATION_ASSOCIATED_MODEL_WITH_TYPES";
	public static final String MODEL_TYPE_HUMAN_READABLE = "People for specific organization"; 
	
	private Dataset dataset;
	
	private Model constructedModel;
	
	private Log log = LogFactory.getLog(OrganizationAssociatedPeopleModelWithTypesConstructor.class.getName());
	
	private long before, after;

	private String organizationURI;
	
	public OrganizationAssociatedPeopleModelWithTypesConstructor(String organizationURI, Dataset dataset) {
		this.dataset = dataset;
		this.organizationURI = organizationURI;
	}
	
	private String constructAssociatedPeopleForOrganizationWithTypesQuery() {
		return ""
		+ " CONSTRUCT { "
		+ "     <" + organizationURI + "> rdf:type foaf:Organization . "
		+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
		+ "     <" + organizationURI + "> vivosocnet:hasPersonWithActivity ?Person .  "
		+ "     ?Person rdfs:label ?personLabel. "
		+ "     ?Person rdf:type ?personType . "
		+ "     ?personType rdfs:label ?personTypeLabel . "
		+ " } "
		+ " WHERE {  "
		+ "     <" + organizationURI + "> rdf:type foaf:Organization . "
		+ "     <" + organizationURI + "> rdfs:label ?organizationLabel . "
		+ "     OPTIONAL { "
		+ "         <" + organizationURI + "> core:relatedBy ?Position .  "
		+ "         ?Position rdf:type core:Position .  "
		+ "         ?Position core:relates ?Person .  "
		+ "         ?Person rdfs:label ?personLabel. "
		+ "         ?Person rdf:type ?personType . "
		+ "         ?personType rdfs:label ?personTypeLabel . "
		+ "     } "
		+ " } ";
		
	}
	
	private Model executeQuery(String constructQuery) {
		
		log.debug("in constructed model for associated people for organization");
		
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
			constructedModel = executeQuery(constructAssociatedPeopleForOrganizationWithTypesQuery());
			return constructedModel;
		}
		
	}

}
