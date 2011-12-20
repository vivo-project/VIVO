/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.reasoner.plugin;

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

/**
 * handles rules of the form
 * assertedProp(?x, ?y) ^ type(?x) -> inferredProp(?x, ?y)
 *	
 * @author bjl23
 *
 */
public abstract class SimplePropertyAndTypeRule implements ReasonerPlugin {

	private Property ASSERTED_PROP;
	private Resource TYPE;
	private Property INFERRED_PROP;
	
	protected SimplePropertyAndTypeRule(String assertedProp, String type, String inferredProp) {
		TYPE = ResourceFactory.createResource(type);
        ASSERTED_PROP = ResourceFactory.createProperty(assertedProp);
        INFERRED_PROP = ResourceFactory.createProperty(inferredProp);
	}
	
	public boolean isInterestedInAddedStatement(Statement stmt) {
		return (RDF.type.equals(stmt.getPredicate()) || isRelevantPredicate(stmt));
	}
	
	public boolean isInterestedInRemovedStatement(Statement stmt) {
		return (RDF.type.equals(stmt.getPredicate()) || isRelevantPredicate(stmt));
	}
	
	public void addedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
		boolean relevantType = isRelevantType(stmt, TBoxInferencesModel);
		boolean relevantPredicate = isRelevantPredicate(stmt);

		if (relevantType) {
			StmtIterator stmtIt = aboxAssertionsModel.listStatements(
					stmt.getSubject(), ASSERTED_PROP, (RDFNode)null);
			while (stmtIt.hasNext()) {
				Statement s = stmtIt.nextStatement();
				tryToInfer(stmt.getSubject(), 
						   INFERRED_PROP, 
						   s.getObject(), 
						   aboxAssertionsModel, 
						   aboxInferencesModel);
			}
		} else if (relevantPredicate) {
			if(aboxAssertionsModel.contains(
					stmt.getSubject(), RDF.type, TYPE) 
			  || aboxInferencesModel.contains(
					  stmt.getSubject(), RDF.type, TYPE)) {
				tryToInfer(stmt.getSubject(), 
						   INFERRED_PROP, 
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
    					stmt.getSubject(), INFERRED_PROP, stmt.getObject());
//    		}
    	} else if (isRelevantType(stmt, TBoxInferencesModel)) {
    		if(!aboxInferencesModel.contains(
    				stmt.getSubject(), RDF.type, TYPE)) {
    			StmtIterator groundIt = aboxAssertionsModel.listStatements(
    					stmt.getSubject(), ASSERTED_PROP, (RDFNode) null);
    			while (groundIt.hasNext()) {
    				Statement groundStmt = groundIt.nextStatement();
    				aboxInferencesModel.remove(
    						groundStmt.getSubject(), INFERRED_PROP, groundStmt.getObject());
    			}
    		}
    	}
    }
    
    private boolean isRelevantType(Statement stmt, Model TBoxInferencesModel) {
		return (RDF.type.equals(stmt.getPredicate()) 
				&& (TYPE.equals(stmt.getObject()) 
						|| TBoxInferencesModel.contains(
								(Resource) stmt.getObject(), RDFS.subClassOf, TYPE)));
    }	
	
    private boolean isRelevantPredicate(Statement stmt) {
		return (ASSERTED_PROP.equals(stmt.getPredicate()));
    }

}
