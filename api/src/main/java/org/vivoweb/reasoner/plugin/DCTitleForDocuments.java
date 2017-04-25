/* $This file is distributed under the terms of the license in LICENSE$ */

package org.vivoweb.reasoner.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.reasoner.ReasonerPlugin;
import edu.cornell.mannlib.vitro.webapp.reasoner.plugin.SimplePropertyAndTypeRule;

public class DCTitleForDocuments extends SimplePropertyAndTypeRule implements ReasonerPlugin {

	public DCTitleForDocuments() {
		super(RDFS.label.getURI(), 
		      "http://purl.org/ontology/bibo/Document", 
		      "http://purl.org/dc/terms/title");
	}
			   
}
