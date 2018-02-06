package edu.cornell.mannlib.semservices.service.impl;

import edu.cornell.mannlib.semservices.bo.Concept;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class UMLSServiceTest {
    @Test
    public void testUmls() throws Exception {
        UMLSService service = new UMLSService();

        if (service.isConfigured()) {
            List<Concept> concepts = service.getConcepts("diabetes");
            Assert.assertNotNull(concepts);
        }
    }
}
