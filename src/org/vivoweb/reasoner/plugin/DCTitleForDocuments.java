package org.vivoweb.reasoner.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.reasoner.ReasonerPlugin;

public class DCTitleForDocuments implements ReasonerPlugin {

	private final static Log log = LogFactory.getLog(DCTitleForDocuments.class);
	
	private final Resource BIBO_DOCUMENT = ResourceFactory.createResource(
			                        "http://purl.org/ontology/bibo/Document");
	private final Property DCTERMS_TITLE = ResourceFactory.createProperty(
			                        "http://purl.org/dc/terms/title");
		   
	public boolean isInterestedInAddedStatement(Statement stmt) {
		return (isRelevantType(stmt) || isRelevantPredicate(stmt));
	}
	
	public boolean isInterestedInRemovedStatement(Statement stmt) {
		return (isRelevantType(stmt) || isRelevantPredicate(stmt));
	}
	
	public void addedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
		boolean relevantType = isRelevantType(stmt);
		boolean relevantPredicate = isRelevantPredicate(stmt);

		if (relevantType) {
			StmtIterator stmtIt = aboxAssertionsModel.listStatements(
					stmt.getSubject(), RDFS.label, (RDFNode)null);
			while (stmtIt.hasNext()) {
				Statement s = stmtIt.nextStatement();
				tryToInfer(stmt.getSubject(), 
						   DCTERMS_TITLE, 
						   s.getObject(), 
						   aboxAssertionsModel, 
						   aboxInferencesModel);
			}
		} else if (relevantPredicate) {
			if(aboxAssertionsModel.contains(
					stmt.getSubject(), RDF.type, BIBO_DOCUMENT) 
			  || aboxInferencesModel.contains(
					  stmt.getSubject(), RDF.type, BIBO_DOCUMENT)) {
				tryToInfer(stmt.getSubject(), 
						   DCTERMS_TITLE, 
						   stmt.getObject(), 
						   aboxAssertionsModel, 
						   aboxInferencesModel);
			}
		}
	}
	
	private void tryToInfer(Resource subject, 
			                Property predicate, 
			                RDFNode object, 
			                Model aboxAssertionsModel, 
			                Model aboxInferencesModel) {
		// this should be part of a superclass or some class that provides
		// reasoning framework functions
		Statement s = ResourceFactory.createStatement(subject, predicate, object);
		if (!aboxAssertionsModel.contains(s) && !aboxInferencesModel.contains(s)) {
			aboxInferencesModel.add(s);
		}
	}

    public void removedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
    	
    	if (isRelevantPredicate(stmt)) {
//    		if (aboxAssertionsModel.contains(
//    				stmt.getSubject(), RDF.type, BIBO_DOCUMENT)
//    			        || aboxInferencesModel.contains(
//    						stmt.getSubject(), RDF.type, BIBO_DOCUMENT)) {
    			aboxInferencesModel.remove(
    					stmt.getSubject(), DCTERMS_TITLE, stmt.getObject());
//    		}
    	} else if (isRelevantType(stmt)) {
    		if(!aboxInferencesModel.contains(
    				stmt.getSubject(), RDF.type, BIBO_DOCUMENT)) {
    			StmtIterator labelIt = aboxAssertionsModel.listStatements(
    					stmt.getSubject(), RDFS.label, (RDFNode) null);
    			while (labelIt.hasNext()) {
    				Statement labelStmt = labelIt.nextStatement();
    				aboxInferencesModel.remove(
    						labelStmt.getSubject(), DCTERMS_TITLE, labelStmt.getObject());
    			}
    		}
    	}
    }
    
    private boolean isRelevantType(Statement stmt) {
		return (RDF.type.equals(stmt.getPredicate()) 
				&& BIBO_DOCUMENT.equals(stmt.getObject()));
    }	
	
    private boolean isRelevantPredicate(Statement stmt) {
		return (RDFS.label.equals(stmt.getPredicate()));
    }
	
}
