/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.personpubcount;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Syntax;

public class PersonPublicationCountConstructQueryRunner {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;
	
	private String egoURI;
	
	private Dataset Dataset;
	
	private Log log = LogFactory.getLog(PersonPublicationCountConstructQueryRunner.class.getName());
	
	private long before, after;
	
	public PersonPublicationCountConstructQueryRunner(String egoURI, Dataset Dataset, Log log){
		this.egoURI = egoURI;
		this.Dataset = Dataset;
		//this.log = log;		
	}
	
	private String generateConstructQueryForAuthorLabel(String queryURI) {
		
		String sparqlQuery = 
			 "CONSTRUCT { " 
			+	"<"+queryURI+ "> rdfs:label ?authorLabel ."
			+ "}"	
			+ "WHERE {"
			+	"<"+queryURI+ "> rdfs:label ?authorLabel ."
			+ "}";
				
		return sparqlQuery;
	} 
}
