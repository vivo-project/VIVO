package edu.cornell.mannlib.vitro.webapp.sparql.function;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.expr.nodevalue.NodeFunctions;
import org.apache.jena.sparql.pfunction.PropFuncArg ;
import org.apache.jena.sparql.pfunction.PropertyFunction ;
import org.apache.jena.sparql.util.IterLib;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.query.QueryExecException ;
import org.apache.jena.sparql.core.Var ;
import org.apache.jena.sparql.engine.ExecutionContext ;
import org.apache.jena.sparql.engine.QueryIterator ;
import org.apache.jena.sparql.engine.binding.Binding ;
import org.apache.jena.sparql.expr.nodevalue.NodeFunctions ;
import org.apache.jena.sparql.pfunction.PFuncSimple ;
import org.apache.jena.sparql.pfunction.PropFuncArg ;
import org.apache.jena.sparql.util.IterLib ;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.utils.jena.URIUtils;

/**
 * @author Michel Héon; Université du Québec à Montréal
 * @filename isIriExist.java
 * @date 30 sept. 2021
 * Checks on the existing IRI in the model 
 */
public class isIriExist extends PFuncSimple
{
	private static final Log log = LogFactory.getLog(isIriExist.class.getName());

	public isIriExist() {
		super();
	}

	@Override
	public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt) {
	}

	@Override
	public QueryIterator execEvaluated(Binding binding, Node subject, Node predicate, Node object,
			ExecutionContext execCxt) {
		// Validation of argument types
		//
		// The object must be a variable 
		if ( !Var.isVar(object) )
			throw new QueryExecException("hasNewIRI: The (object) of statement must be an unbound variable") ;

		if ( ! subject.isURI()  )
			throw new QueryExecException("hasNewIRI: The (subject) of statement must be an IRI") ;

		//
		// Extract graph an translated them in OntModel
		//
		Graph activeGraph = execCxt.getActiveGraph();
		Model model = ModelFactory.createModelForGraph(activeGraph);
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
		//
		// Verify if IRI exist 
		boolean isIRIExist = URIUtils.hasExistingURI(subject.getURI(), ontModel);
		Node isIriExistNode = NodeFactory.createLiteralByValue(Boolean.valueOf(isIRIExist), XSDDatatype.XSDboolean);
		log.debug("isIriExist Value: "+isIriExistNode.getLiteralLexicalForm());
		return IterLib.oneResult(binding, Var.alloc(object), isIriExistNode, execCxt) ;
	}
	protected String checkURI( String uri ) {
		IRIFactory factory = IRIFactory.jenaImplementation();
		IRI iri = factory.create( uri );
		if (iri.hasViolation(false) ) {
			String errorStr = ("Bad URI: "+ uri +
					"\nOnly well-formed absolute URIrefs can be included in RDF/XML output: "
					+ (iri.violations(false).next()).getShortMessage());
			return errorStr;
		} else {
			return null;
		}
	}
}

