/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.controller.visualization.cacheintercept;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.VCARD;

public class TDBResearch {

	static final String directory = "testdb/" ;
	
public static void getModelInformation() {
		
		// Direct way: Make a TDB-back Jena model in the named directory.
//		String inputFileName = "query/entitygrants/select-all-grants.sparql";
//		String inputFileName = "query/entitypublications/select-all-publications-f.sparql";
//		String inputFileName = "query/organization/select-org-suborg.sparql";
		String inputFileName = "query/test.sparql";
//		String inputFileName = "query/subentitytypes/select-all-types.sparql";
		InputStream in = FileManager.get().open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
        
        Scanner scanner = 
            new Scanner(FileManager.get().open( inputFileName )).useDelimiter("\\Z");
          String contents = scanner.next();
//          System.out.println(contents);
          scanner.close();
          
        Dataset ds = TDBFactory.createDataset(directory);
        
        Model model = ds.getNamedModel("publication");
//         organization publication
//        Model model = ds.getDefaultModel();
        
//        List<Statement> listStatements = model.listStatements().toList();
//        
//        for (Statement stmt : listStatements) {
//        	
//        	System.out.println(stmt);
//        	
//        }
//		System.out.println(listStatements);
        
        // Potentially expensive query.
        String sparqlQueryString;
        
//        sparqlQueryString = "SELECT (count(*) AS ?count) { ?s ?p ?o }" ;
        
        sparqlQueryString = contents.replaceAll(Matcher.quoteReplacement("$URI$"), "http://vivo.ufl.edu/individual/CollegeofDentistry");
        
//        String sparqlQueryString = "SELECT * { GRAPH ?g { ?s ?p ?o } }" ;
        // See http://www.openjena.org/ARQ/app_api.html
//        String sparqlQueryString = "SELECT ?sub ?pre ?obj " + " { ?sub ?pre ?obj . }" ;
        
        Query query = QueryFactory.create(sparqlQueryString) ;

//        QueryExecution qexec = QueryExecutionFactory.create(query, ds);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        
        ResultSet results = qexec.execSelect() ;
        
//        try {
//			OutputStream outputStream = new FileOutputStream("output/test.txt");
//			ResultSetFormatter.out(outputStream, results) ;
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//        
		ResultSetFormatter.out(results) ;
        
        
//        
//        while (results.hasNext()) {
////        	
//        	QuerySolution solution = results.nextSolution();
//        	
//        	System.out.println("solution: " + solution.toString());
////        	System.out.println("------");
//        	//System.out.println(solution.get("count"));
////        	System.out.println(solution.getResource("sub"));
////        	System.out.println(solution.get("pre"));
////        	System.out.println(solution.get("obj"));
//        }
        
//        
        qexec.close() ;
        
        ds.close();
		
	}
	
	public static void createModel() {
		
//		String commonFilePath = "data/entitygrants/"; entitypublications organization
		String commonFilePath = "data/organization/";
		
		String ontologyFilePath = "data/common/prefixes.n3";
		
		String[] inputFileName  = {
			commonFilePath + "test-custom-props.n3",
//			commonFilePath + "college-education-pubs.n3",
//			commonFilePath + "college-pediatric-dentistry-pubs.n3",
//			commonFilePath + "college-dentistry-pubs.n3", 
//			college-education-pubs.n3 test-custom-props.n3 ufl-orgs-types.n3
		};
		
		
		String organizationModel = "";
        Dataset ds = TDBFactory.createDataset(directory) ;
//        Model model = ds.getDefaultModel();
//        Model model = ds.getNamedModel("entitygrants"); organization publication
        Model model = ds.getNamedModel("publication");
        
        long before, after;
        
//        Iterator it = model.listStatements();
//        
//        while (it.hasNext()) {
//        	System.out.println("org - " + it.next());
//        }
//        
        
        System.out.println("model size " + model.size());
        
        before = System.currentTimeMillis();
        
        InputStream in = FileManager.get().open(ontologyFilePath);
        if (in == null) {
            throw new IllegalArgumentException( "File: " + ontologyFilePath + " not found");
        }
        
        // read the RDF/XML file
//        model.read(in, null, "N3");
        
        for (String nm : inputFileName) {
        	
        in = FileManager.get().open( nm );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + nm + " not found");
        }
        
        // read the RDF/XML file
        model.read(in, null, "N3");
		
//        Resource johnSmith  = model.createResource(personURI)
//        .addProperty(VCARD.FN, fullName)
//        .addProperty(VCARD.N, 
//                 model.createResource()
//                      .addProperty(VCARD.Given, givenName)
//                      .addProperty(VCARD.Family, familyName));
//        
//        System.out.println("create model: " + johnSmith);
        
        model.commit();
        
        }
        
        model.close();
        
//        it = model.listStatements();
//        
//        while (it.hasNext()) {
//        	System.out.println("org deux - " + it.next());
//        }
//        
        System.out.println("model empty - " + model.isEmpty());
        
        
        System.out.println("read in model in tdb - " + (System.currentTimeMillis() - before));
        
        ds.close();
	}
	
	public static void printCurrentModels() {
		
		Dataset ds = TDBFactory.createDataset(directory) ;
		
        Iterator<String> listNames = ds.listNames();
        
        for (;listNames.hasNext();) {
        	
        	System.out.println(listNames.next());
        }
        
        ds.close();
        
	}
	
	

}
