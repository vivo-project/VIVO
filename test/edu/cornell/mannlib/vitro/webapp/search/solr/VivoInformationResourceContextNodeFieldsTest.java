/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;

public class VivoInformationResourceContextNodeFieldsTest extends AbstractTestClass {

    String TEST_NO_LABLE_N3_FILE = "VivoInformationResourceContextNodeFieldsTest.n3";
    String RDFS_LABEL_VALUE = "Test Document X";
    String DOCUMENT_URI = "http://example.com/vivo/individual/n7474";
    
    
    @Test
    public void testNoLabel() throws IOException{
        //Test that rdfs:label is NOT added by the VivoInformationResourceContextNodeFields
        
        //setup a model & wdf with test RDF file
        InputStream stream = VivoInformationResourceContextNodeFieldsTest.class.getResourceAsStream(TEST_NO_LABLE_N3_FILE);
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "N3");
        stream.close();

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
        ontModel.prepare();
        Assert.assertTrue("ontModel had no statements" , ontModel.size() > 0 );
                
        WebappDaoFactory wadf = new WebappDaoFactoryJena(ontModel);        
        Individual ind = wadf.getIndividualDao().getIndividualByURI(DOCUMENT_URI);
        Assert.assertNotNull(ind);
        
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ALLTEXT", "");                
        
        VivoInformationResourceContextNodeFields vircnf = new VivoInformationResourceContextNodeFields(ontModel);
        vircnf.modifyDocument(ind, doc, new StringBuffer());                
        
        Collection values = doc.getFieldValues("ALLTEXT");
        for( Object value : values){
            Assert.assertFalse("rdf:label erroneously added by document modifier:", value.toString().contains(RDFS_LABEL_VALUE));
        }
        
        VivoAgentContextNodeFields vacnf = new VivoAgentContextNodeFields(ontModel);
        vacnf.modifyDocument(ind, doc, new StringBuffer());
        
        
        
        
    }

}
