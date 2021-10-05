package edu.cornell.mannlib.vitro.webapp.sparql.function;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * @filename hasNewIRI.java
 * @date 30 sept. 2021
 * Generate a new uniq ID to form a new IRI 
 */
public class hasNewIRI extends PFuncSimple
{
	private static final Log log = LogFactory.getLog(hasNewIRI.class.getName());

	public hasNewIRI() {
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

		// Initialize variables

		String baseUri = subject.getURI();
		String uri = null;
		String errMsg = null;
		Random random = new Random();
		boolean uriIsGood = false;
		boolean uriIsUniq = false;

		//
		// Extract graph an translated them in OntModel
		//
		Graph activeGraph = execCxt.getActiveGraph();
		Model model = ModelFactory.createModelForGraph(activeGraph);
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);

		//
		// Generate new IRI
		int attempts = 0;
		while(!uriIsUniq && attempts < 30 ){
			uriIsGood=false ;
			uri = baseUri + random.nextInt( Math.min(Integer.MAX_VALUE,(int)Math.pow(2,attempts + 13)) );
			errMsg = checkURI(uri);
			if(  errMsg != null) {
				log.debug(errMsg);
				uri = null;
			} else {
				uriIsGood = true;
			}
			if (uriIsGood && !URIUtils.hasExistingURI(uri, ontModel)){
				uriIsUniq = true;
			}
			attempts++;
		}
		Node uriVal = NodeFactory.createURI(uri);
		log.debug("new IRI: "+uriVal.getURI());
		return IterLib.oneResult(binding, Var.alloc(object), uriVal, execCxt) ;
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