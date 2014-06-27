/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.documentBuilding;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceFactorySingle;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.jena.model.RDFServiceModel;

/**
 * Second test for VivoAgentContextNodeFields. This is duplicated
 * due to using a different data set in the test. 
 *
 */
public class VivoAgentContextNodeFieldsTest2  extends AbstractTestClass{
	    	
	static String HISTORY_DEPT = "http://vivo.colorado.edu/deptid_10238" ;
	
	static RDFServiceFactory rdfServiceFactory;
	
	@BeforeClass
	public static void setup(){
		Model m = ModelFactory.createDefaultModel();
		InputStream stream = VivoAgentContextNodeFieldsTest2
		        .class.getResourceAsStream("./VIVO146_DataSet1.n3");
		assertTrue("Expected to find RDF data file " , stream != null );
		
		long preloadSize = m.size();
		
		m.read(stream, null, "N3");
		assertTrue("expected to load statements from file", m.size() > preloadSize );		
		  		
		System.out.println("size of m : "  + m.size());
		
		assertTrue("expect statements about HISTORY_DEPT", 
				m.contains(ResourceFactory.createResource(HISTORY_DEPT),(Property) null,(RDFNode) null));
		
        RDFService rdfService = new RDFServiceModel(m);
        rdfServiceFactory = new RDFServiceFactorySingle(rdfService);
	}
	
	/**
	 * Test how many times history is returned for context nodes
	 * of the History department. 
	 */
	@Test
	public void testHistory(){
		Individual ind = new IndividualImpl();
        ind.setURI(HISTORY_DEPT);
        
        VivoAgentContextNodeFields vacnf = new VivoAgentContextNodeFields(rdfServiceFactory);
        StringBuffer sb = vacnf.getValues( ind );
        
        assertNotNull( sb );        
        String value = sb.toString();
        
        assertTrue("Expected to get some text back from "
                + "VivoAgentContextNodeFields but got none" , !value.trim().isEmpty());
        
        int count = StringUtils.countMatches(value.toLowerCase(),"history");
        System.out.println("histories: " + count);
        System.out.println("'" + value + "'");
//        assertTrue("expected to have jane because SPCA advises jane", hasJane);                       
	}	
}
