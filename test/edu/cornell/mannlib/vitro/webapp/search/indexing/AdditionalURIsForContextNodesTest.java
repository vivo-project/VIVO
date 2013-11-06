/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.search.indexing;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.search.indexing.AdditionalURIsForContextNodes;


public class AdditionalURIsForContextNodesTest {

    @Test 
    public void testPositionChanges(){
        String n3 = 
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> <http://vivoweb.org/ontology/core#relatedBy> <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#AcademicDepartment> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Department> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Organization> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#AcademicDepartment> . \n" +
                        
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#DependentResource> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#DependentResource> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Position> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#FacultyPosition> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> <http://vivoweb.org/ontology/core#relates> <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> <http://vivoweb.org/ontology/core#relates> <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932> . \n" +

        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932> <http://vivoweb.org/ontology/core#relatedBy> <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5431> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#FacultyMember> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#FacultyMember> . \n" +                       
                
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5398> <http://vivoweb.org/ontology/core#start> <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5425> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5398> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#DateTimeInterval> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5398> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5398> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#DateTimeInterval> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5425> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5425> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#DateTimeValue> . \n" +

        "<http://vivoweb.org/ontology/core#AcademicDepartment> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" +        
        "<http://vivoweb.org/ontology/core#Position> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" +
        "<http://vivoweb.org/ontology/core#FacultyMember> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" +
        "<http://xmlns.com/foaf/0.1/Organization> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" +
        "<http://xmlns.com/foaf/0.1/Person> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" +
        "<http://xmlns.com/foaf/0.1/Agent> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" +
        "<http://vivoweb.org/ontology/core#DateTimeInterval> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" +
        "<http://vivoweb.org/ontology/core#Department> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . \n" ;
        
        //make a test model with an person, an authorship context node and a book 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the org needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932");       
        assertTrue("did not find org for context node", uris.contains("http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423" ));
        
        //if the org changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n5423");       
        assertTrue("did not find person for context node", uris.contains("http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n932" ));                        
    }
    
    @Test
    public void testPersonOnOrgChange() {                

        String n3 ="@prefix dc:      <http://purl.org/dc/elements/1.1/> . \n" +
        "@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> . \n" +
        "@prefix swrl:    <http://www.w3.org/2003/11/swrl#> . \n" +
        "@prefix vitro:   <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> . \n" +
        "@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> . \n" +
        "@prefix swrlb:   <http://www.w3.org/2003/11/swrlb#> . \n" +
        "@prefix owl:     <http://www.w3.org/2002/07/owl#> . \n" +
        "@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n" +
        "@prefix core:    <http://vivoweb.org/ontology/core#> . \n" +
        "@prefix vivo:    <http://vivo.library.cornell.edu/ns/0.1#> . \n" +
        "@prefix obo:     <http://purl.obolibrary.org/obo/> . \n" +
        " " +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n2577> \n" +
        "      a       owl:Thing , core:Role , core:LeaderRole ; \n" +
        "      rdfs:label \"head\"^^xsd:string ; \n" +
        "      vitro:mostSpecificType \n" +
        "              core:LeaderRole ; \n" +
        "      core:dateTimeInterval \n" +
        "              <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n2594> ; \n" +
        "      obo:RO_0000052 <http://vivo.scripps.edu/individual/n14979> ; \n" +
        "      core:roleContributesTo <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n2592> . \n" +
        "<http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n2592> \n" +
        "      a       <http://xmlns.com/foaf/0.1/Organization> , owl:Thing , <http://xmlns.com/foaf/0.1/Agent> , core:ClinicalOrganization ; \n" +
        "      rdfs:label \"Organization XYZ\"^^xsd:string ; \n" +
        "      vitro:mostSpecificType \n" +
        "              core:ClinicalOrganization ; \n" +
        "      core:contributingRole <http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n2577> . \n" + 
        "      <http://vivo.scripps.edu/individual/n14979> a <http://xmlns.com/foaf/0.1/Person> , owl:Thing , <http://xmlns.com/foaf/0.1/Agent>   . \n"; 
        
        
        //make a test model with an person, an authorship context node and a book 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
       
         
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //get additional uris for org
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n2592");
       
        assertTrue("did not find person for context node", uris.contains("http://vivo.scripps.edu/individual/n14979" ));
                
    }
    
    @Test
    public void testLeaderRoleChanges(){
    	String n3=
    		
    		"<http://vivo.scripps.edu/individual/n2027> <http://www.w3.org/2000/01/rdf-schema#label> \"1, Test\" . \n " +
//			"<http://vivo.scripps.edu/individual/n2027> <http://xmlns.com/foaf/0.1/lastName> \"1\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
//			"<http://vivo.scripps.edu/individual/n2027> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
			"<http://vivo.scripps.edu/individual/n2027> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n " +
			"<http://vivo.scripps.edu/individual/n2027> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n7067> . \n " +
			"<http://vivo.scripps.edu/individual/n2027> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n " +
			"<http://vivo.scripps.edu/individual/n2027> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n " +
			"<http://vivo.scripps.edu/individual/n2027> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " +
			
			
			"<http://vivo.scripps.edu/individual/n7067> <http://www.w3.org/2000/01/rdf-schema#label> \"Leader Role\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
			"<http://vivo.scripps.edu/individual/n7067> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n2027> . \n " +
			"<http://vivo.scripps.edu/individual/n7067> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n7083> . \n " +
			"<http://vivo.scripps.edu/individual/n7067> <http://vivoweb.org/ontology/core#roleContributesTo> <http://vivo.scripps.edu/individual/n7080> . \n " +
			"<http://vivo.scripps.edu/individual/n7067> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#LeaderRole> . \n " +
			"<http://vivo.scripps.edu/individual/n7067> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#LeaderRole> . \n " +
			"<http://vivo.scripps.edu/individual/n7067> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " +
			
			"<http://vivo.scripps.edu/individual/n7080> <http://www.w3.org/2000/01/rdf-schema#label> \"University1\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
			"<http://vivo.scripps.edu/individual/n7080> <http://vivoweb.org/ontology/core#contributingRole> <http://vivo.scripps.edu/individual/n7067> . \n " +
			"<http://vivo.scripps.edu/individual/n7080> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#University> .\n " +
			"<http://vivo.scripps.edu/individual/n7080> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#University> . \n " +
			"<http://vivo.scripps.edu/individual/n7080> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n " +
			"<http://vivo.scripps.edu/individual/n7080> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " +
			"<http://vivo.scripps.edu/individual/n7080> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Organization> . \n " ;
            
			
        //make a test model with an person, a leader role node and a university 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the university needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n2027");       
        assertTrue("did not find org for context node", uris.contains("http://vivo.scripps.edu/individual/n7080" ));
        
        //if the university changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n7080");       
        assertTrue("did not find person for context node", uris.contains("http://vivo.scripps.edu/individual/n2027" ));                        


    }
    
    
    @Test
    public void testMemberRoleChanges(){
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n4519> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n6040> . \n " + 
			"<http://vivo.scripps.edu/individual/n4519> <http://www.w3.org/2000/01/rdf-schema#label> \"2, Test\" . \n " + 
//			"<http://vivo.scripps.edu/individual/n4519> <http://xmlns.com/foaf/0.1/lastName> \"2\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
//			"<http://vivo.scripps.edu/individual/n4519> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
			"<http://vivo.scripps.edu/individual/n4519> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n " + 
			"<http://vivo.scripps.edu/individual/n4519> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n " + 
			"<http://vivo.scripps.edu/individual/n4519> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n " + 
			"<http://vivo.scripps.edu/individual/n4519> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " + 
				
			"<http://vivo.scripps.edu/individual/n6040> <http://www.w3.org/2000/01/rdf-schema#label> \"Member Role\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
			"<http://vivo.scripps.edu/individual/n6040> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n6031> . \n " + 
			"<http://vivo.scripps.edu/individual/n6040> <http://vivoweb.org/ontology/core#roleContributesTo> <http://vivo.scripps.edu/individual/n6004> . \n " + 
			"<http://vivo.scripps.edu/individual/n6040> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#MemberRole> . \n " + 
			"<http://vivo.scripps.edu/individual/n6040> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n4519> . \n " + 
			"<http://vivo.scripps.edu/individual/n6040> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#MemberRole> . \n " + 
			"<http://vivo.scripps.edu/individual/n6040> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n " + 
			"<http://vivo.scripps.edu/individual/n6040> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " + 
				
				
			"<http://vivo.scripps.edu/individual/n6004> <http://www.w3.org/2000/01/rdf-schema#label> \"University2\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
			"<http://vivo.scripps.edu/individual/n6004> <http://vivoweb.org/ontology/core#contributingRole> <http://vivo.scripps.edu/individual/n6040> . \n " + 
			"<http://vivo.scripps.edu/individual/n6004> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#University> . \n " + 
			"<http://vivo.scripps.edu/individual/n6004> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#University> . \n " + 
			"<http://vivo.scripps.edu/individual/n6004> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n " + 
			"<http://vivo.scripps.edu/individual/n6004> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " + 
			"<http://vivo.scripps.edu/individual/n6004> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Organization> . \n " ;
				
		
				
        //make a test model with an person, a member role node and a university 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the university needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4519");       
        assertTrue("did not find org for context node", uris.contains("http://vivo.scripps.edu/individual/n6004" ));
        
        //if the university changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n6004");       
        assertTrue("did not find person for context node", uris.contains("http://vivo.scripps.edu/individual/n4519" ));     		
		
		
    }
    
    
    @Test
    public void testClinicalRoleChangesForProject(){
    	
    	String n3 = 
    		
    		"<http://vivo.scripps.edu/individual/n4858> <http://www.w3.org/2000/01/rdf-schema#label> \"3, Test\" . \n" +
//			"<http://vivo.scripps.edu/individual/n4858> <http://xmlns.com/foaf/0.1/lastName> \"3\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" +
//			"<http://vivo.scripps.edu/individual/n4858> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n4858> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n5185> . \n" +
			"<http://vivo.scripps.edu/individual/n4858> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n4858> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" +
			"<http://vivo.scripps.edu/individual/n4858> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n4858> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
    				 
			"<http://vivo.scripps.edu/individual/n5185> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n4858> . \n" +
			"<http://vivo.scripps.edu/individual/n5185> <http://www.w3.org/2000/01/rdf-schema#label> \"Clinical Role\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n5185> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n5180> . \n" +
			"<http://vivo.scripps.edu/individual/n5185> <http://purl.obolibrary.org/obo/BFO_0000054> <http://vivo.scripps.edu/individual/n5177> . \n" +
			"<http://vivo.scripps.edu/individual/n5185> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#ClinicalRole> . \n" +
			"<http://vivo.scripps.edu/individual/n5185> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n" +
			"<http://vivo.scripps.edu/individual/n5185> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n5185> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#ClinicalRole> . \n" +
    				
			"<http://vivo.scripps.edu/individual/n5177> <http://www.w3.org/2000/01/rdf-schema#label> \"Project1\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n5177> <http://purl.obolibrary.org/obo/BFO_0000055> <http://vivo.scripps.edu/individual/n5185> . \n" +
			"<http://vivo.scripps.edu/individual/n5177> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#Project> . \n" +
			"<http://vivo.scripps.edu/individual/n5177> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Project> . \n" +
			"<http://vivo.scripps.edu/individual/n5177> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n5177> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Process> . \n" ;
    		
    	
		
        //make a test model with an person, a clinical role node and a project 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the project needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4858");       
        assertTrue("did not find project for clinical role", uris.contains("http://vivo.scripps.edu/individual/n5177" ));
        
        //if the project changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n5177");       
        assertTrue("did not find person for clinical role", uris.contains("http://vivo.scripps.edu/individual/n4858" ));     
    	
    }
    
    @Test
    public void testClinicalRoleChangesForService(){
    	
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n5651> <http://www.w3.org/2000/01/rdf-schema#label> \"4, Test\" . \n" +
//			"<http://vivo.scripps.edu/individual/n5651> <http://xmlns.com/foaf/0.1/lastName> \"4\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
//			"<http://vivo.scripps.edu/individual/n5651> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n5651> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n4428> . \n" +
			"<http://vivo.scripps.edu/individual/n5651> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n5651> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" +
			"<http://vivo.scripps.edu/individual/n5651> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n5651> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
				
			"<http://vivo.scripps.edu/individual/n4428> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n5651> . \n" +
			"<http://vivo.scripps.edu/individual/n4428> <http://www.w3.org/2000/01/rdf-schema#label> \"Clinical Role 2\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n4428> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n4444> . \n" +
			"<http://vivo.scripps.edu/individual/n4428> <http://vivoweb.org/ontology/core#roleContributesTo> <http://vivo.scripps.edu/individual/n4442> . \n" +
			"<http://vivo.scripps.edu/individual/n4428> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#ClinicalRole> . \n" +
			"<http://vivo.scripps.edu/individual/n4428> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n" +
			"<http://vivo.scripps.edu/individual/n4428> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n4428> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#ClinicalRole> . \n" +
				
			"<http://vivo.scripps.edu/individual/n4442> <http://www.w3.org/2000/01/rdf-schema#label> \"Service1\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n4442> <http://vivoweb.org/ontology/core#contributingRole> <http://vivo.scripps.edu/individual/n4428> . \n" +
			"<http://vivo.scripps.edu/individual/n4442> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://purl.obolibrary.org/obo/ERO_0000005> . \n" +
			"<http://vivo.scripps.edu/individual/n4442> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n4442> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.obolibrary.org/obo/ERO_0000005> . \n" ;
			
        //make a test model with an person, a clinical role node and a service 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the service needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n5651");       
        assertTrue("did not find service for clinical role", uris.contains("http://vivo.scripps.edu/individual/n4442" ));
        
        //if the service changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4442");       
        assertTrue("did not find person for clinical role", uris.contains("http://vivo.scripps.edu/individual/n5651" ));   

    
    }
    
    
    @Test
    public void testPresenterRoleChangesForPresentation(){
    	String n3 =
    		"<http://vivo.scripps.edu/individual/n5596> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n1294> . \n" +
			"<http://vivo.scripps.edu/individual/n5596> <http://www.w3.org/2000/01/rdf-schema#label> \"5, Test\" . \n" +
//			"<http://vivo.scripps.edu/individual/n5596> <http://xmlns.com/foaf/0.1/lastName> \"5\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
//			"<http://vivo.scripps.edu/individual/n5596> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n5596> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n5596> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" +
			"<http://vivo.scripps.edu/individual/n5596> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n5596> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
    				
			"<http://vivo.scripps.edu/individual/n1294> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n5596> . \n" +
			"<http://vivo.scripps.edu/individual/n1294> <http://www.w3.org/2000/01/rdf-schema#label> \"Presenter Role\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n1294> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n1304> . \n" +
			"<http://vivo.scripps.edu/individual/n1294> <http://purl.obolibrary.org/obo/BFO_0000054> <http://vivo.scripps.edu/individual/n1305> . \n" +
			"<http://vivo.scripps.edu/individual/n1294> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#PresenterRole> . \n" +
			"<http://vivo.scripps.edu/individual/n1294> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n" +
			"<http://vivo.scripps.edu/individual/n1294> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#PresenterRole> . \n" +
			"<http://vivo.scripps.edu/individual/n1294> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
    						
			"<http://vivo.scripps.edu/individual/n1305> <http://www.w3.org/2000/01/rdf-schema#label> \"Presentation 1\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n1305> <http://purl.obolibrary.org/obo/BFO_0000055> <http://vivo.scripps.edu/individual/n1294> . \n" +
			"<http://vivo.scripps.edu/individual/n1305> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#Presentation> . \n" +
			"<http://vivo.scripps.edu/individual/n1305> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Presentation> . \n" +
			"<http://vivo.scripps.edu/individual/n1305> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/c4dm/event.owl#Event> . \n" +
			"<http://vivo.scripps.edu/individual/n1305> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" ;
    	
    	
        //make a test model with an person, a presenter role node and a presentation 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the presentation needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n5596");       
        assertTrue("did not find service for clinical role", uris.contains("http://vivo.scripps.edu/individual/n1305" ));
        
        //if the presentation changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n1305");       
        assertTrue("did not find person for clinical role", uris.contains("http://vivo.scripps.edu/individual/n5596" ));   

    }
    
    
    @Test
    public void testPresenterRoleChangesForInvitedTalk(){
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n4112> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n4144> . \n " + 
			"<http://vivo.scripps.edu/individual/n4112> <http://www.w3.org/2000/01/rdf-schema#label> \"6, Test\" . \n " + 
//			"<http://vivo.scripps.edu/individual/n4112> <http://xmlns.com/foaf/0.1/lastName> \"6\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
//			"<http://vivo.scripps.edu/individual/n4112> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
			"<http://vivo.scripps.edu/individual/n4112> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n " + 
			"<http://vivo.scripps.edu/individual/n4112> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n " + 
			"<http://vivo.scripps.edu/individual/n4112> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n " + 
			"<http://vivo.scripps.edu/individual/n4112> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " + 
    				
			"<http://vivo.scripps.edu/individual/n4144> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n4112> . \n " + 
			"<http://vivo.scripps.edu/individual/n4144> <http://www.w3.org/2000/01/rdf-schema#label> \"Presenter Role 2\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
			"<http://vivo.scripps.edu/individual/n4144> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n4136> . \n " + 
			"<http://vivo.scripps.edu/individual/n4144> <http://purl.obolibrary.org/obo/BFO_0000054> <http://vivo.scripps.edu/individual/n4107> . \n " + 
			"<http://vivo.scripps.edu/individual/n4144> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#PresenterRole> . \n " + 
			"<http://vivo.scripps.edu/individual/n4144> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n " + 
			"<http://vivo.scripps.edu/individual/n4144> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#PresenterRole> . \n " + 
			"<http://vivo.scripps.edu/individual/n4144> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " + 
			
			"<http://vivo.scripps.edu/individual/n4107> <http://www.w3.org/2000/01/rdf-schema#label> \"Invited Talk 1\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " + 
			"<http://vivo.scripps.edu/individual/n4107> <http://purl.obolibrary.org/obo/BFO_0000055> <http://vivo.scripps.edu/individual/n4144> . \n " + 
			"<http://vivo.scripps.edu/individual/n4107> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#InvitedTalk> . \n " + 
			"<http://vivo.scripps.edu/individual/n4107> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Presentation> . \n " + 
			"<http://vivo.scripps.edu/individual/n4107> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/c4dm/event.owl#Event> . \n " + 
			"<http://vivo.scripps.edu/individual/n4107> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " + 
			"<http://vivo.scripps.edu/individual/n4107> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#InvitedTalk> . \n " ;

        //make a test model with an person, a presenter role node and an invited talk 
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the invited talk needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4112");       
        assertTrue("did not find invited talk for person", uris.contains("http://vivo.scripps.edu/individual/n4107" ));
        
        //if the invited talk changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4107");       
        assertTrue("did not find person for invited talk", uris.contains("http://vivo.scripps.edu/individual/n4112" ));  
    				
    }
    
    
    @Test
    public void testResearcherRoleForGrant(){
    	
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n4957> <http://www.w3.org/2000/01/rdf-schema#label> \"7, Test\" . \n" +
//			"<http://vivo.scripps.edu/individual/n4957> <http://xmlns.com/foaf/0.1/lastName> \"7\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
//			"<http://vivo.scripps.edu/individual/n4957> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n4957> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n4288> . \n" +
			"<http://vivo.scripps.edu/individual/n4957> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n4957> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" +
			"<http://vivo.scripps.edu/individual/n4957> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n4957> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
    				
			"<http://vivo.scripps.edu/individual/n4288> <http://www.w3.org/2000/01/rdf-schema#label> \"Researcher Role\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n4288> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n4251> . \n" +
			"<http://vivo.scripps.edu/individual/n4288> <http://vivoweb.org/ontology/core#relatedBy> <http://vivo.scripps.edu/individual/n4252> . \n" +
			"<http://vivo.scripps.edu/individual/n4288> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#ResearcherRole> . \n" +
			"<http://vivo.scripps.edu/individual/n4288> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n4957> . \n" +
			"<http://vivo.scripps.edu/individual/n4288> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n" +
			"<http://vivo.scripps.edu/individual/n4288> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#ResearcherRole> . \n" +
			"<http://vivo.scripps.edu/individual/n4288> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
    						
			"<http://vivo.scripps.edu/individual/n4252> <http://www.w3.org/2000/01/rdf-schema#label> \"Grant1\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n4252> <http://vivoweb.org/ontology/core#relates> <http://vivo.scripps.edu/individual/n4288> . \n" +
			"<http://vivo.scripps.edu/individual/n4252> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#Grant> . \n" +
			"<http://vivo.scripps.edu/individual/n4252> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Agreement> . \n" +
			"<http://vivo.scripps.edu/individual/n4252> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n4252> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Grant> . \n" ;
    	
    	
        //make a test model with an person, a researcher role node and a grant
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the grant needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4957");       
        assertTrue("did not find service for clinical role", uris.contains("http://vivo.scripps.edu/individual/n4252" ));
        
        //if the grant changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4252");       
        assertTrue("did not find person for clinical role", uris.contains("http://vivo.scripps.edu/individual/n4957" ));  

    		
    }
    
    @Test
    public void testResearcherRoleForProject(){
    	
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n2029> <http://www.w3.org/2000/01/rdf-schema#label> \"8, Test\" . \n " +
//			"<http://vivo.scripps.edu/individual/n2029> <http://xmlns.com/foaf/0.1/lastName> \"8\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
//			"<http://vivo.scripps.edu/individual/n2029> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
			"<http://vivo.scripps.edu/individual/n2029> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n556> . \n " +
			"<http://vivo.scripps.edu/individual/n2029> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n " +
			"<http://vivo.scripps.edu/individual/n2029> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n " +
			"<http://vivo.scripps.edu/individual/n2029> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n " +
			"<http://vivo.scripps.edu/individual/n2029> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " +
    				
			"<http://vivo.scripps.edu/individual/n556> <http://www.w3.org/2000/01/rdf-schema#label> \"Researcher Role 2\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
			"<http://vivo.scripps.edu/individual/n556> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n568> . \n " +
			"<http://vivo.scripps.edu/individual/n556> <http://purl.obolibrary.org/obo/BFO_0000054> <http://vivo.scripps.edu/individual/n564> . \n " +
			"<http://vivo.scripps.edu/individual/n556> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#ResearcherRole> . \n " +
			"<http://vivo.scripps.edu/individual/n556> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n2029> . \n " +
			"<http://vivo.scripps.edu/individual/n556> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n " +
			"<http://vivo.scripps.edu/individual/n556> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#ResearcherRole> . \n " +
			"<http://vivo.scripps.edu/individual/n556> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " +

    						
			"<http://vivo.scripps.edu/individual/n564> <http://www.w3.org/2000/01/rdf-schema#label> \"Project2\"^^<http://www.w3.org/2001/XMLSchema#string> . \n " +
			"<http://vivo.scripps.edu/individual/n564> <http://purl.obolibrary.org/obo/BFO_0000055> <http://vivo.scripps.edu/individual/n556> . \n " +
			"<http://vivo.scripps.edu/individual/n564> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#Project> . \n " +
			"<http://vivo.scripps.edu/individual/n564> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Project> . \n " +
			"<http://vivo.scripps.edu/individual/n564> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n " +
			"<http://vivo.scripps.edu/individual/n564> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Process> . \n " ;
    	
    	
    	
        //make a test model with an person, a researcher role node and a project
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the project needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n2029");       
        assertTrue("did not find service for clinical role", uris.contains("http://vivo.scripps.edu/individual/n564" ));
        
        //if the project changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n564");       
        assertTrue("did not find person for clinical role", uris.contains("http://vivo.scripps.edu/individual/n2029" ));  

    	
    }
    
    @Test
    public void testPrincipalInvestigatorRoleChanges(){
    	
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n2368> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n1740> . \n" + 
			"<http://vivo.scripps.edu/individual/n2368> <http://www.w3.org/2000/01/rdf-schema#label> \"8, Test\" . \n" + 
//			"<http://vivo.scripps.edu/individual/n2368> <http://xmlns.com/foaf/0.1/lastName> \"8\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" + 
//			"<http://vivo.scripps.edu/individual/n2368> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" + 
			"<http://vivo.scripps.edu/individual/n2368> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n" + 
			"<http://vivo.scripps.edu/individual/n2368> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" + 
			"<http://vivo.scripps.edu/individual/n2368> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" + 
			"<http://vivo.scripps.edu/individual/n2368> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" + 
    				
			"<http://vivo.scripps.edu/individual/n1740> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n2368> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n1756> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://vivoweb.org/ontology/core#relatedBy> <http://vivo.scripps.edu/individual/n1742> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#PrincipalInvestigatorRole> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#PrincipalInvestigatorRole> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#ResearcherRole> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" + 
			"<http://vivo.scripps.edu/individual/n1740> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#InvestigatorRole> . \n" + 

			"<http://vivo.scripps.edu/individual/n1742> <http://www.w3.org/2000/01/rdf-schema#label> \"Grant 2\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" + 
			"<http://vivo.scripps.edu/individual/n1742> <http://vivoweb.org/ontology/core#relates> <http://vivo.scripps.edu/individual/n1740> . \n" + 
			"<http://vivo.scripps.edu/individual/n1742> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#Grant> . \n" + 
			"<http://vivo.scripps.edu/individual/n1742> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Agreement> . \n" + 
			"<http://vivo.scripps.edu/individual/n1742> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" + 
			"<http://vivo.scripps.edu/individual/n1742> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Grant> . \n" ;
    	
    	
        //make a test model with an person, a principal investigator role node and a grant
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the person changes then the grant needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n2368");       
        assertTrue("did not find grant for pi", uris.contains("http://vivo.scripps.edu/individual/n1742" ));
        
        //if the grant changes then the person needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n1742");       
        assertTrue("did not find pi for grant", uris.contains("http://vivo.scripps.edu/individual/n2368" ));  


    }
    
    @Test
    public void testCoPrincipalInvestigatorRoleChanges(){
    	
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n1373> <http://www.w3.org/2000/01/rdf-schema#label> \"9, Test\" . \n" + 
//			"<http://vivo.scripps.edu/individual/n1373> <http://xmlns.com/foaf/0.1/lastName> \"9\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" + 
//			"<http://vivo.scripps.edu/individual/n1373> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" + 
			"<http://vivo.scripps.edu/individual/n1373> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n" + 
			"<http://vivo.scripps.edu/individual/n1373> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" + 
			"<http://vivo.scripps.edu/individual/n1373> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" + 
			"<http://vivo.scripps.edu/individual/n1373> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" + 
			"<http://vivo.scripps.edu/individual/n1373> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n4928> . \n" + 
				
			"<http://vivo.scripps.edu/individual/n4928> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n1373> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n4895> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://vivoweb.org/ontology/core#relatedBy> <http://vivo.scripps.edu/individual/n4931> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#CoPrincipalInvestigatorRole> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#CoPrincipalInvestigatorRole> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#ResearcherRole> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" + 
			"<http://vivo.scripps.edu/individual/n4928> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#InvestigatorRole> . \n" + 
						
			"<http://vivo.scripps.edu/individual/n4931> <http://www.w3.org/2000/01/rdf-schema#label> \"Grant 3\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" + 
			"<http://vivo.scripps.edu/individual/n4931> <http://vivoweb.org/ontology/core#relates> <http://vivo.scripps.edu/individual/n4928> . \n" + 
			"<http://vivo.scripps.edu/individual/n4931> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#Grant> . \n" + 
			"<http://vivo.scripps.edu/individual/n4931> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Agreement> . \n" + 
			"<http://vivo.scripps.edu/individual/n4931> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" + 
			"<http://vivo.scripps.edu/individual/n4931> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Grant> . \n" ;
						
        //make a test model with an person, a co-principal investigator role node and a grant
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the copi changes then the grant needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n1373");       
        assertTrue("did not find grant for co-pi", uris.contains("http://vivo.scripps.edu/individual/n4931" ));
        
        //if the grant changes then the copi needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n4931");       
        assertTrue("did not find co-pi for grant", uris.contains("http://vivo.scripps.edu/individual/n1373" ));  

    }
    
    
    @Test
    public void testInvestigatorRoleChanges(){
    	
    	String n3 =
    		
    		"<http://vivo.scripps.edu/individual/n5282> <http://www.w3.org/2000/01/rdf-schema#label> \"10, Test\" . \n" +
//			"<http://vivo.scripps.edu/individual/n5282> <http://xmlns.com/foaf/0.1/lastName> \"10\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
//			"<http://vivo.scripps.edu/individual/n5282> <http://xmlns.com/foaf/0.1/firstName> \"Test\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n5282> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n5282> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent> . \n" +
			"<http://vivo.scripps.edu/individual/n5282> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> . \n" +
			"<http://vivo.scripps.edu/individual/n5282> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n5282> <http://purl.obolibrary.org/obo/RO_0000053> <http://vivo.scripps.edu/individual/n157> . \n" +
    				
			"<http://vivo.scripps.edu/individual/n157> <http://vivoweb.org/ontology/core#dateTimeInterval> <http://vivo.scripps.edu/individual/n127> . \n" +
			"<http://vivo.scripps.edu/individual/n157> <http://vivoweb.org/ontology/core#relatedBy> <http://vivo.scripps.edu/individual/n160> . \n" +
			"<http://vivo.scripps.edu/individual/n157> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#InvestigatorRole> . \n" +
			"<http://vivo.scripps.edu/individual/n157> <http://purl.obolibrary.org/obo/RO_0000052> <http://vivo.scripps.edu/individual/n5282> . \n" +
			"<http://vivo.scripps.edu/individual/n157> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Role> . \n" +
			"<http://vivo.scripps.edu/individual/n157> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#ResearcherRole> . \n" +
			"<http://vivo.scripps.edu/individual/n157> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n157> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#InvestigatorRole> . \n" +

			"<http://vivo.scripps.edu/individual/n160> <http://www.w3.org/2000/01/rdf-schema#label> \"Grant 4\"^^<http://www.w3.org/2001/XMLSchema#string> . \n" +
			"<http://vivo.scripps.edu/individual/n160> <http://vivoweb.org/ontology/core#relates> <http://vivo.scripps.edu/individual/n157> . \n" +
			"<http://vivo.scripps.edu/individual/n160> <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#mostSpecificType> <http://vivoweb.org/ontology/core#Grant> . \n" +
			"<http://vivo.scripps.edu/individual/n160> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Agreement> . \n" +
			"<http://vivo.scripps.edu/individual/n160> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> . \n" +
			"<http://vivo.scripps.edu/individual/n160> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://vivoweb.org/ontology/core#Grant> . \n" ;
    	
    	
        //make a test model with an person, a investigator role node and a grant
        OntModel model = ModelFactory.createOntologyModel();
        model.read( new StringReader(n3), null,  "N3");
                
        //make an AdditionalURIsForContextNodesTest object with that model
        AdditionalURIsForContextNodes uriFinder = new AdditionalURIsForContextNodes( model );
        
        //if the investigator changes then the grant needs to be updated
        List<String> uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n5282");       
        assertTrue("did not find grant for investigator", uris.contains("http://vivo.scripps.edu/individual/n160" ));
        
        //if the grant changes then the investigator needs to be updated
        uris = uriFinder.findAdditionalURIsToIndex( "http://vivo.scripps.edu/individual/n160");       
        assertTrue("did not find investigator for grant", uris.contains("http://vivo.scripps.edu/individual/n5282" ));  


    	
    }
    
    
}
