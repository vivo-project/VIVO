package edu.cornell.mannlib.vitro.webapp.utilities;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;


public class Describe {
    private static final Log log = LogFactory.getLog(Describe.class.getName());

	public static void main(String[] args) {
		OntModel model = ModelFactory.createOntologyModel();
		model.read("C:\\VIVO-Trad-ecosysteme\\vivo\\home\\rdf\\applicationMetadata\\firsttime\\classgroups_fr_CA.rdf") ;
		model.read("C:\\VIVO-Trad-ecosysteme\\vivo\\home\\rdf\\applicationMetadata\\firsttime\\classgroups.rdf") ;
		Describe.showVitroClassGroupequipmentLabels(model,"test");
		System.out.println("Done!");
	}

	private static void showVitroClassGroupequipmentLabels(OntModel model, String message) {
		String uri = "http://vivoweb.org/ontology#vitroClassGroupequipment";
		List<Statement> stmts = model.listStatements(ResourceFactory.createResource(uri), RDFS.label, (RDFNode)null).toList();
		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
			Statement statement = (Statement) iterator.next();
			log.debug("\t\t"+message + " " +statement);
		}
	}
}
