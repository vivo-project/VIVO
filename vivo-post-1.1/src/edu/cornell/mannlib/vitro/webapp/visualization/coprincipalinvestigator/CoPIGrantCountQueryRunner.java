package edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.CoAuthorshipData;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UniqueIDGenerator;

public class CoPIGrantCountQueryRunner {
	
	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private String egoURI;
	
	private DataSource dataSource;

	private Log log;

	private UniqueIDGenerator nodeIDGenerator;

	private UniqueIDGenerator edgeIDGenerator;
	
	public CoPIGrantCountQueryRunner(String egoURI,
			DataSource dataSource, Log log) {

		this.egoURI = egoURI;
		this.dataSource = dataSource;
		this.log = log;
		
		this.nodeIDGenerator = new UniqueIDGenerator();
		this.edgeIDGenerator = new UniqueIDGenerator();

	}
	
	private String generateEgoCoPIquery(String queryURI) {


		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
			+ "SELECT "
			+ "		(str(<" + queryURI + ">) as ?" + QueryFieldLabels.AUTHOR_URL + ") " 
			+ "		(str(?authorLabel) as ?" + QueryFieldLabels.AUTHOR_LABEL + ") " 
			+ "		(str(?coAuthorPerson) as ?" + QueryFieldLabels.CO_AUTHOR_URL + ") " 
			+ "		(str(?coAuthorPersonLabel) as ?" + QueryFieldLabels.CO_AUTHOR_LABEL + ") "
			+ "		(str(?document) as ?" + QueryFieldLabels.DOCUMENT_URL + ") "
			+ "		(str(?documentLabel) as ?" + QueryFieldLabels.DOCUMENT_LABEL + ") "
			+ "		(str(?documentMoniker) as ?" + QueryFieldLabels.DOCUMENT_MONIKER + ") "
			+ "		(str(?documentBlurb) as ?" + QueryFieldLabels.DOCUMENT_BLURB + ") "
			+ "		(str(?publicationYear) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR + ") "
			+ "		(str(?publicationYearMonth) as ?" 
						+ QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_MONTH + ") " 
			+ "		(str(?publicationDate) as ?" 
						+ QueryFieldLabels.DOCUMENT_PUBLICATION_DATE + ") " 
			+ "WHERE { "
			+ "<" + queryURI + "> rdf:type foaf:Person ;" 
								+ " rdfs:label ?authorLabel ;" 
								+ " core:authorInAuthorship ?authorshipNode . "
			+ "?authorshipNode rdf:type core:Authorship ;" 
								+ " core:linkedInformationResource ?document . "
			+ "?document rdfs:label ?documentLabel . " 
			+ "?document core:informationResourceInAuthorship ?coAuthorshipNode . " 
			+ "?coAuthorshipNode core:linkedAuthor ?coAuthorPerson . " 
			+ "?coAuthorPerson rdfs:label ?coAuthorPersonLabel . "
			+ "OPTIONAL {  ?document core:year ?publicationYear } . " 
			+ "OPTIONAL {  ?document core:yearMonth ?publicationYearMonth } . " 
			+ "OPTIONAL {  ?document core:date ?publicationDate } . "  
			+ "OPTIONAL {  ?document vitro:moniker ?documentMoniker } . " 
			+ "OPTIONAL {  ?document vitro:blurb ?documentBlurb } . " 
			+ "OPTIONAL {  ?document vitro:description ?documentDescription } " 
			+ "} " 
			+ "ORDER BY ?document ?coAuthorPerson";

//		System.out.println("COPI QUERY - " + sparqlQuery);
		
		return sparqlQuery;
	}
	
	private ResultSet executeQuery(String queryText, DataSource dataSource) {

		QueryExecution queryExecution = null;
		try {
			Query query = QueryFactory.create(queryText, SYNTAX);

			queryExecution = QueryExecutionFactory.create(query, dataSource);

			if (query.isSelectType()) {
				return queryExecution.execSelect();
			}
		} finally {
			if (queryExecution != null) {
				queryExecution.close();
			}
		}
		return null;
	}
	
	public CoAuthorshipData getQueryResult()
	throws MalformedQueryParametersException {

	if (StringUtils.isNotBlank(this.egoURI)) {
		/*
    	 * To test for the validity of the URI submitted.
    	 * */
    	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
		IRI iri = iRIFactory.create(this.egoURI);
        if (iri.hasViolation(false)) {
            String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
            log.error("Ego Co-PI Vis Query " + errorMsg);
            throw new MalformedQueryParametersException(
            		"URI provided for an individual is malformed.");
        }
    } else {
        throw new MalformedQueryParametersException("URI parameter is either null or empty.");
    }

	ResultSet resultSet	= executeQuery(generateEgoCoPIquery(this.egoURI),
									   this.dataSource);
	return createQueryResult(resultSet);
}

	private CoAuthorshipData createQueryResult(ResultSet resultSet) {
		
		return null;
	}
}
