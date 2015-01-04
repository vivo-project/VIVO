/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.searchindex.documentBuilding;

import java.util.ArrayList;
import java.util.List;

/**
 * DocumentModifier to populate Search index fields for the basic ISF relationships. 
 * 
 * This will add the all rdfs:labels of the related individuals to the search document. 
 *  
 * @author bdc34 
 */
public class VivoISFBasicFields extends ContextNodeFields {
    
    private static String VIVONS = "http://vivoweb.org/ontology/core#";
    
    /**
     * Subtypes of vivo:Relationship that get handled by this class.
     */
    private static String[] RELATIONSHIP_TYPES = {
        VIVONS + "Position",
        VIVONS + "Authorship",
        VIVONS + "Collaboration",
        VIVONS + "Affiliation"
    };
    
    static List<String> queries = new ArrayList<String>();    
    
    public VivoISFBasicFields(){        
        super(queries);
    }
      
    protected static final String prefix =               
        " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  \n"
      + " prefix core: <" + VIVONS + ">  \n"
      + " prefix foaf: <http://xmlns.com/foaf/0.1/> \n"
      + " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n" 
      + " prefix obo: <http://purl.obolibrary.org/obo/> \n" ;
      

    
    static {
        
        /* make a string of the RELATIONSHIP_TYPES for use in
         * a SPARQL IN clause */
        String types = "";
        for( int i = 0 ; i < RELATIONSHIP_TYPES.length ; i++ ){
            types += "<" + RELATIONSHIP_TYPES[i] + ">";
            if( i != RELATIONSHIP_TYPES.length - 1 ){
                types += ", ";
            }                    
        }
         
        queries.add(
               prefix +
              "SELECT \n" +
              "(str(?rawresult) as ?result) WHERE \n" +
              "{\n" +              
              " ?uri   core:relatedBy ?rel    . \n" +
              " ?rel   rdf:type       ?type   . \n" +
              " ?rel   core:relates   ?other  . \n" +
              " ?other rdfs:label     ?rawresult . \n" +
              " FILTER ( ?type IN ( " + types + " ) )\n" +
              "}" );        
    }
    
}
