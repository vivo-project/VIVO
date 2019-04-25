/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.searchindex.documentBuilding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchInputDocument;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ContextModelsUser;


public class CalculateParameters implements DocumentModifier, ContextModelsUser {

    private boolean shutdown = false;
	private volatile Dataset dataset;
   // public static int totalInd=1;

    private static final String prefix = "prefix owl: <http://www.w3.org/2002/07/owl#> "
		+ " prefix vitroDisplay: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#>  "
		+ " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
		+ " prefix core: <http://vivoweb.org/ontology/core#>  "
		+ " prefix foaf: <http://xmlns.com/foaf/0.1/> "
		+ " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
		+ " prefix localNav: <http://vitro.mannlib.cornell.edu/ns/localnav#>  "
		+ " prefix obo: <http://purl.obolibrary.org/obo/>  "
		+ " prefix bibo: <http://purl.org/ontology/bibo/>  ";

    private static final String betaQuery = prefix + " SELECT count(distinct ?inLinks) " +
    		" WHERE { " +
    		" ?uri rdf:type owl:Thing . " +
    		" ?inLinks ?prop ?uri . " +
    		" } ";

    private static final String totalCountQuery = prefix + " SELECT count(distinct ?ind) " +
	" WHERE { " +
	" ?ind rdf:type owl:Thing . " +
	" } ";

    private static Log log = LogFactory.getLog(CalculateParameters.class);

	@Override
	public void setContextModels(ContextModelAccess models) {
		 this.dataset = DatasetFactory.create(models.getOntModel());
	}

	public float calculateBeta(String uri){
		float beta=0;
		int Conn=0;
		Query query;
		QuerySolutionMap initialBinding = new QuerySolutionMap();
		QuerySolution soln = null;
		Resource uriResource = ResourceFactory.createResource(uri);
		initialBinding.add("uri", uriResource);
		dataset.getLock().enterCriticalSection(Lock.READ);
		QueryExecution qexec=null;
		try{
			query = QueryFactory.create(betaQuery,Syntax.syntaxARQ);
			qexec = QueryExecutionFactory.create(query,dataset,initialBinding);
			ResultSet results = qexec.execSelect();
			List<String> resultVars = results.getResultVars();
			if(resultVars!=null && resultVars.size()!=0){
				soln = results.next();
				Conn = Integer.parseInt(soln.getLiteral(resultVars.get(0)).getLexicalForm());
			}
		}catch(Throwable t){
		    if( ! shutdown )
		        log.error(t,t);
		}finally{
		    if( qexec != null )
		        qexec.close();
			dataset.getLock().leaveCriticalSection();
		}

		beta = (float)Conn;
		//beta *= 100;
		beta += 1;

		// sigmoid function to keep beta between 0 to 1;

		beta = (float) (1 / ( 1 + Math.pow(Math.E,(-beta))));

		if(beta > 1)
			log.info("Beta higher than 1 : " + beta);
		else if(beta <= 0)
			log.info("Beta lower < = 0 : " + beta);
		return beta;
    }


    public String[] getAdjacentNodes(String uri){

    	List<String> queryList = new ArrayList<String>();
    	Set<String> adjacentNodes = new HashSet<String>();
    	Set<String> coauthorNames = new HashSet<String>();
    	String[] info = new String[]{"",""};
    	StringBuffer adjacentNodesConcat = new StringBuffer();
    	StringBuffer coauthorBuff = new StringBuffer();
    	adjacentNodesConcat.append("");
    	coauthorBuff.append("");

    	queryList.add(prefix +
    			" SELECT ?adjobj (str(?adjobjLabel) as ?coauthor) " +
    			" WHERE { " +
    			" ?uri rdf:type <http://xmlns.com/foaf/0.1/Person> . " +
    			" ?uri ?prop ?obj . " +
    			" ?obj rdf:type <http://vivoweb.org/ontology/core#Relationship> . " +
    			" ?obj ?prop2 ?obj2 . " +
    			" ?obj2 rdf:type obo:IAO_0000030 . " +
    			" ?obj2 ?prop3 ?obj3 . " +
    			" ?obj3 rdf:type <http://vivoweb.org/ontology/core#Relationship> . " +
    			" ?obj3 ?prop4 ?adjobj . " +
    			" ?adjobj rdfs:label ?adjobjLabel . " +
    			" ?adjobj rdf:type <http://xmlns.com/foaf/0.1/Person> . " +

    			" FILTER (?prop !=rdf:type) . " +
    			" FILTER (?prop2!=rdf:type) . " +
    			" FILTER (?prop3!=rdf:type) . " +
    			" FILTER (?prop4!=rdf:type) . " +
    			" FILTER (?adjobj != ?uri) . " +
    	"}");

    	queryList.add(prefix +
    			" SELECT ?adjobj " +
    			" WHERE{ " +

    			" ?uri rdf:type foaf:Agent . " +
    			" ?uri ?prop ?obj . " +
    			" ?obj ?prop2 ?adjobj . " +


    			" FILTER (?prop !=rdf:type) . " +
    			" FILTER isURI(?obj) . " +

    			" FILTER (?prop2!=rdf:type) . " +
    			" FILTER (?adjobj != ?uri) . " +
    			" FILTER isURI(?adjobj) . " +

    			" { ?adjobj rdf:type <http://xmlns.com/foaf/0.1/Organization> . } " +
    			" UNION " +
    			" { ?adjobj rdf:type <http://xmlns.com/foaf/0.1/Person> . } " +
    			" UNION " +
    			" { ?adjobj rdf:type obo:IAO_0000030 . } " +
    			" UNION " +
    			" { ?adjobj rdf:type <http://vivoweb.org/ontology/core#Location> . } ." +
    	"}");

    	Query query;

    	QuerySolution soln;
    	QuerySolutionMap initialBinding = new QuerySolutionMap();
		Resource uriResource = ResourceFactory.createResource(uri);

		initialBinding.add("uri", uriResource);

    	Iterator<String> queryItr = queryList.iterator();

    	dataset.getLock().enterCriticalSection(Lock.READ);
    	Resource adjacentIndividual = null;
    	RDFNode coauthor = null;
    	try{
    		while(queryItr.hasNext()){
    			/*if(!isPerson){
    				queryItr.next(); // we don't want first query to execute if the ind is not a person.
    			}*/
    			query = QueryFactory.create(queryItr.next(),Syntax.syntaxARQ);
    			QueryExecution qexec = QueryExecutionFactory.create(query,dataset,initialBinding);
    			try{
    					ResultSet results = qexec.execSelect();
    					while(results.hasNext()){
    						soln = results.nextSolution();

    						adjacentIndividual = (Resource)soln.get("adjobj");
    						if(adjacentIndividual!=null){
    							adjacentNodes.add(adjacentIndividual.getURI());
    						}

    						coauthor = soln.get("coauthor");
    						if(coauthor!=null){
    							coauthorNames.add(" co-authors " + coauthor.toString() + " co-authors ");
    						}
    					}
    			}catch(Exception e){
    			    if( ! shutdown )
    			        log.error("Error found in getAdjacentNodes method of SearchQueryHandler");
    			}finally{
    				qexec.close();
    			}
    		}
    		queryList = null;
    		Iterator<String> itr = adjacentNodes.iterator();
    		while(itr.hasNext()){
    			adjacentNodesConcat.append(itr.next()).append(" ");
    		}

    		info[0] = adjacentNodesConcat.toString();

    		itr = coauthorNames.iterator();
    		while(itr.hasNext()){
    			coauthorBuff.append(itr.next());
    		}

    		info[1] = coauthorBuff.toString();

    	}
    	catch(Throwable t){
    	    if( ! shutdown )
    	        log.error(t,t);
    	}finally{
    		dataset.getLock().leaveCriticalSection();
    		adjacentNodes = null;
    		adjacentNodesConcat = null;
    		coauthorBuff = null;
    	}
    	return info;
	}

	@Override
	public void modifyDocument(Individual individual, SearchInputDocument doc) {
		// TODO Auto-generated method stub
		 // calculate beta value.
        log.debug("Parameter calculation starts..");
        float beta = calculateBeta(individual.getURI());
        doc.addField(VitroSearchTermNames.BETA, (Object) beta);
        doc.setDocumentBoost(beta + doc.getDocumentBoost() );
        log.debug("Parameter calculation is done");
	}


	@Override
	public void shutdown(){
        shutdown=true;
    }


	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[]";
	}

}

class TotalInd implements Runnable{
	private Dataset dataset;
	private String totalCountQuery;
	private static Log log = LogFactory.getLog(TotalInd.class);

	public TotalInd(Dataset dataset,String totalCountQuery){
		this.dataset = dataset;
		this.totalCountQuery = totalCountQuery;

	}
	@Override
	public void run(){
		    int totalInd=0;
	        Query query;
	    	QuerySolution soln = null;
			dataset.getLock().enterCriticalSection(Lock.READ);
			QueryExecution qexec = null;

			try{
				query = QueryFactory.create(totalCountQuery,Syntax.syntaxARQ);
				qexec = QueryExecutionFactory.create(query,dataset);
				ResultSet results = qexec.execSelect();
				List<String> resultVars = results.getResultVars();

				if(resultVars!=null && resultVars.size()!=0){
					soln = results.next();
					totalInd = Integer.parseInt(soln.getLiteral(resultVars.get(0)).getLexicalForm());
				}
				//CalculateParameters.totalInd = totalInd;
				//log.info("Total number of individuals in the system are : " + CalculateParameters.totalInd);
			}catch(Throwable t){
				log.error(t,t);
			}finally{
			    if( qexec != null )
			        qexec.close();
				dataset.getLock().leaveCriticalSection();
			}

	}
}
