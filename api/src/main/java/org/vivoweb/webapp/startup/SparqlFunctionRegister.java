package org.vivoweb.webapp.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;

import edu.cornell.mannlib.vitro.webapp.sparql.function.hasNewIRI;
import edu.cornell.mannlib.vitro.webapp.sparql.function.isIriExist;

/**
 * @author Michel Héon; Université du Québec à Montréal
 * @filename SparqlFunctionRegister.java
 * @date 30 sept. 2021
 */
public class SparqlFunctionRegister  implements ServletContextListener{
	private static final Log log = LogFactory.getLog(SparqlFunctionRegister.class);


	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		PropertyFunctionRegistry.get().put("http://vivoweb.org/sparql/function#hasNewIRI", hasNewIRI.class) ;
		PropertyFunctionRegistry.get().put("http://vivoweb.org/sparql/function#isIriExist", isIriExist.class) ;
		log.debug("SPARQL Function: hasNewIRI is registered");
	}

}
