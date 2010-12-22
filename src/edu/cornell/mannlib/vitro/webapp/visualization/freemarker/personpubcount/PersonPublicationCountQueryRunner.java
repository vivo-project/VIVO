/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.personpubcount;

import java.util.HashSet;
import java.util.Set;

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
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects.Individual;
import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.QueryRunner;



/**
 * This query runner is used to execute a sparql query that will fetch all the publications
 * defined by bibo:Document property for a particular individual.
 * 
 * @author cdtank
 */
public class PersonPublicationCountQueryRunner implements QueryRunner<Set<BiboDocument>> {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String personURI;
	private DataSource dataSource;

	private Individual author; 

	public Individual getAuthor() {
		return author;
	}

	private Log log;

	private static final String SPARQL_QUERY_COMMON_SELECT_CLAUSE = "" 
			+ "SELECT (str(?authorLabel) as ?authorLabelLit) " 
			+ "		(str(?document) as ?documentLit) " 
			+ "		(str(?documentMoniker) as ?documentMonikerLit) " 
			+ "		(str(?documentLabel) as ?documentLabelLit) " 
			+ "		(str(?documentBlurb) as ?documentBlurbLit) " 
			+ "		(str(?publicationYear) as ?publicationYearLit) " 
			+ "		(str(?publicationYearMonth) as ?publicationYearMonthLit) " 
			+ "		(str(?publicationDate) as ?publicationDateLit) " 
			+ "		(str(?documentDescription) as ?documentDescriptionLit) ";

	private static final String SPARQL_QUERY_COMMON_WHERE_CLAUSE = "" 
			+ "?document rdfs:label ?documentLabel ." 
			+ "OPTIONAL {  ?document core:year ?publicationYear } ." 
			+ "OPTIONAL {  ?document core:yearMonth ?publicationYearMonth } ." 
			+ "OPTIONAL {  ?document core:date ?publicationDate } ." 
			+ "OPTIONAL {  ?document vitro:moniker ?documentMoniker } ." 
			+ "OPTIONAL {  ?document vitro:blurb ?documentBlurb } ." 
			+ "OPTIONAL {  ?document vitro:description ?documentDescription }";
	
	public PersonPublicationCountQueryRunner(String personURI,
			DataSource dataSource, Log log) {

		this.personURI = personURI;
		this.dataSource = dataSource;
		this.log = log;

	}

	private Set<BiboDocument> createJavaValueObjects(ResultSet resultSet) {
		Set<BiboDocument> authorDocuments = new HashSet<BiboDocument>();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();

			BiboDocument biboDocument = new BiboDocument(
											solution.get(QueryFieldLabels.DOCUMENT_URL)
												.toString());

			RDFNode documentLabelNode = solution.get(QueryFieldLabels.DOCUMENT_LABEL);
			if (documentLabelNode != null) {
				biboDocument.setDocumentLabel(documentLabelNode.toString());
			}


			RDFNode documentBlurbNode = solution.get(QueryFieldLabels.DOCUMENT_BLURB);
			if (documentBlurbNode != null) {
				biboDocument.setDocumentBlurb(documentBlurbNode.toString());
			}

			RDFNode documentmonikerNode = solution.get(QueryFieldLabels.DOCUMENT_MONIKER);
			if (documentmonikerNode != null) {
				biboDocument.setDocumentMoniker(documentmonikerNode.toString());
			}

			RDFNode documentDescriptionNode = solution.get(QueryFieldLabels.DOCUMENT_DESCRIPTION);
			if (documentDescriptionNode != null) {
				biboDocument.setDocumentDescription(documentDescriptionNode.toString());
			}

			RDFNode publicationYearNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR);
			if (publicationYearNode != null) {
				biboDocument.setPublicationYear(publicationYearNode.toString());
			}
			
			RDFNode publicationYearMonthNode = solution.get(
													QueryFieldLabels
															.DOCUMENT_PUBLICATION_YEAR_MONTH);
			if (publicationYearMonthNode != null) {
				biboDocument.setPublicationYearMonth(publicationYearMonthNode.toString());
			}
			
			RDFNode publicationDateNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			if (publicationDateNode != null) {
				biboDocument.setPublicationDate(publicationDateNode.toString());
			}
			
			/*
			 * Since we are getting publication count for just one author at a time we need
			 * to create only one "Individual" instance. We test against the null for "author" to
			 * make sure that it has not already been instantiated. 
			 * */
			RDFNode authorURLNode = solution.get(QueryFieldLabels.AUTHOR_URL);
			if (authorURLNode != null && author == null) {
				author = new Individual(authorURLNode.toString());
				RDFNode authorLabelNode = solution.get(QueryFieldLabels.AUTHOR_LABEL);
				if (authorLabelNode != null) {
					author.setIndividualLabel(authorLabelNode.toString());
				}
			}

			authorDocuments.add(biboDocument);
		}
		return authorDocuments;
	}

	private ResultSet executeQuery(String queryURI,
            DataSource dataSource) {

        QueryExecution queryExecution = null;
//        try {
            Query query = QueryFactory.create(getSparqlQuery(queryURI), SYNTAX);

//            QuerySolutionMap qs = new QuerySolutionMap();
//            qs.add("authPerson", queryParam); // bind resource to s
            
            queryExecution = QueryExecutionFactory.create(query, dataSource);
            

//            if (query.isSelectType()) {
                return queryExecution.execSelect();
//            }
//        } finally {
//            if (queryExecution != null) {
//            	queryExecution.close();
//            }
//        }
//		return null;
    }

	private String getSparqlQuery(String queryURI) {
//		Resource uri1 = ResourceFactory.createResource(queryURI);

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ SPARQL_QUERY_COMMON_SELECT_CLAUSE
							+ "(str(<" + queryURI + ">) as ?authPersonLit) "
							+ "WHERE { "
							+ "<" + queryURI + "> rdf:type foaf:Person ;" 
							+ 					" rdfs:label ?authorLabel ;" 
							+ 					" core:authorInAuthorship ?authorshipNode .  " 
							+ "	?authorshipNode rdf:type core:Authorship ;" 
							+ 					" core:linkedInformationResource ?document . "
							+  SPARQL_QUERY_COMMON_WHERE_CLAUSE
							+ "}";

//		System.out.println("SPARQL query for person pub count -> \n" + sparqlQuery);
		return sparqlQuery;
	}

	public Set<BiboDocument> getQueryResult()
		throws MalformedQueryParametersException {

        if (StringUtils.isNotBlank(this.personURI)) {

        	/*
        	 * To test for the validity of the URI submitted.
        	 * */
        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
    		IRI iri = iRIFactory.create(this.personURI);
            if (iri.hasViolation(false)) {
                String errorMsg = ((Violation) iri.violations(false).next()).getShortMessage();
                log.error("Pub Count vis Query " + errorMsg);
                throw new MalformedQueryParametersException(
                		"URI provided for an individual is malformed.");
            }
        	
        } else {
        	throw new MalformedQueryParametersException("URL parameter is either null or empty.");
        }

		ResultSet resultSet	= executeQuery(this.personURI,
										   this.dataSource);

		return createJavaValueObjects(resultSet);
	}

}
