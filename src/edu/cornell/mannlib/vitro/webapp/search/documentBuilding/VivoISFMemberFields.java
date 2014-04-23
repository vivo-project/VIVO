/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.search.documentBuilding;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.search.documentBuilding.ContextNodeFields;

/**
 * This class will:
 *   add people's names to organization's search Documents.
 *   add organization names to people's search Documents. 
 *   
 * @author bdc34
 *
 */
public class VivoISFMemberFields extends ContextNodeFields {
    private static String VIVONS = "http://vivoweb.org/ontology/core#";
    
    protected static final String prefix =               
            " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  \n"
          + " prefix core: <" + VIVONS + ">  \n"
          + " prefix foaf: <http://xmlns.com/foaf/0.1/> \n"
          + " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n" 
          + " prefix obo: <http://purl.obolibrary.org/obo/> \n" ;
    
    public VivoISFMemberFields(RDFServiceFactory rdfServiceFactory){                
        super(queries,rdfServiceFactory);        
    }
    
    /**
     *   Add people's names to organization's search Documents.
     */
    private static String  peopleForOrganization =         
            prefix +
            "SELECT (str(?result) as ?result) WHERE {\n" +              
            " ?uri    rdf:type               foaf:Organization . \n" +
            " ?role   core:roleContrigutesTo ?uri . \n" +
            " ?person obo:RO_0000053         ?role . \n" +
            " ?person rdfs:label             ?result .\n" +                       
            "}";
    
    /**
     *   add organization names to people's search Documents.
     */
    private static String  organizationForPeople =         
            prefix +
            "SELECT (str(?result) as ?result) WHERE {\n" +              
            " ?uri    rdf:type               foaf:Person . \n" +
            " ?uri obo:RO_0000053 / core:roleContrigutesTo / rdfs:label ?result . \n" +                        
            "}";
    
        
    static List<String> queries = new ArrayList<String>();
    
    static{
        queries.add( peopleForOrganization );
        queries.add( organizationForPeople );
    }
}
