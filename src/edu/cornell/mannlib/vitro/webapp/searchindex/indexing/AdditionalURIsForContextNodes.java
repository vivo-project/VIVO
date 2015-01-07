/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.searchindex.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ContextModelsUser;

public class AdditionalURIsForContextNodes implements IndexingUriFinder, ContextModelsUser {
	private Log log = LogFactory.getLog(AdditionalURIsForContextNodes.class);
	
    private static final List<String> multiValuedQueriesForAgent = new ArrayList<String>();	
	private static final String multiValuedQueryForInformationContentEntity;
	private static final List<String> multiValuedQueriesForRole = new ArrayList<String>();
	private static final List<String>queryList;	
	
    private RDFService rdfService;
    private Set<String> alreadyChecked;
    private long accumulatedTime = 0;
    
    
    @Override
	public void setContextModels(ContextModelAccess models) {
    	this.rdfService = models.getRDFService();
	}
    
    @Override
    public List<String> findAdditionalURIsToIndex(Statement stmt) {
                
        if( stmt != null ){
            long start = System.currentTimeMillis();
            
            List<String>urisToIndex = new ArrayList<String>();
            if(stmt.getSubject() != null && stmt.getSubject().isURIResource() ){        
                String subjUri = stmt.getSubject().getURI();
                if( subjUri != null && ! alreadyChecked.contains( subjUri )){
                    urisToIndex.addAll( findAdditionalURIsToIndex(subjUri));
                    alreadyChecked.add(subjUri);    
                }
            }
            
            if( stmt.getObject() != null && stmt.getObject().isURIResource() ){
                String objUri = stmt.getSubject().getURI();
                if( objUri != null && ! alreadyChecked.contains(objUri)){
                    urisToIndex.addAll( findAdditionalURIsToIndex(objUri));
                    alreadyChecked.add(objUri);
                }
            }
            
            accumulatedTime += (System.currentTimeMillis() - start ) ;
            return urisToIndex;
        }else{
            return Collections.emptyList();
        }                
    }
    
    @Override
    public void startIndexing() { 
        alreadyChecked = new HashSet<String>();
        accumulatedTime = 0L;
    }

    @Override
    public void endIndexing() {
        log.debug( "Accumulated time for this run of the index: " + accumulatedTime + " msec");
        alreadyChecked = null;        
    }
    
    protected List<String> findAdditionalURIsToIndex(String uri) {    	        
    	
    	List<String> uriList = new ArrayList<String>();

		for (String query : queryList) {
			QuerySolutionMap initialBinding = new QuerySolutionMap();
			Resource uriResource = ResourceFactory.createResource(uri);
			initialBinding.add("uri", uriResource);

			ResultSet results = QueryUtils.getQueryResults(query,
					initialBinding, rdfService);
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				Iterator<String> iter = soln.varNames();
				while (iter.hasNext()) {
					String name = iter.next();
					RDFNode node = soln.get(name);
					if (node != null) {
						uriList.add("" + node.toString());
					} else {
						log.debug(name + " is null");
					}
				}
			}
		}
        
    	if( log.isDebugEnabled() )
    	    log.debug( "additional uris for " + uri + " are " + uriList);
    	
        return uriList;
    }
    
    
    private static final String prefix = "prefix owl: <http://www.w3.org/2002/07/owl#> \n"
        + " prefix vitroDisplay: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#>  \n"
        + " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  \n"
        + " prefix core: <http://vivoweb.org/ontology/core#>  \n"
        + " prefix foaf: <http://xmlns.com/foaf/0.1/> \n"
        + " prefix obo: <http://purl.obolibrary.org/obo/> \n"
        + " prefix vcard: <http://www.w3.org/2006/vcard/ns#> \n"
        + " prefix event: <http://purl.org/NET/c4dm/event.owl#> \n"
        + " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
        + " prefix localNav: <http://vitro.mannlib.cornell.edu/ns/localnav#>  \n"
        + " prefix bibo: <http://purl.org/ontology/bibo/>  \n";
    
	static{
	    
	    // If a person changes then update
	    // organizations for positions
		multiValuedQueriesForAgent.add(prefix +
				"SELECT DISTINCT \n" +
				" (str(?i) as ?positionInOrganization) \n" +
				" WHERE {\n" 
				
				+ "?uri rdf:type foaf:Agent  ; core:relatedBy ?c . \n"
				+ " ?c rdf:type core:Position . \n"
							
				+ " OPTIONAL { ?c core:relates ?i . ?i rdf:type foaf:Organization } . \n"
				+ " }");
		
        // If a person changes then update
		// advisee, linkedAuthor and informationResource
		multiValuedQueriesForAgent.add(prefix +
				"SELECT (str(?d) as ?advisee) \n" +
				" (str(?f) as ?linkedAuthor) (str(?h) as ?linkedInformationResource)  WHERE { {\n" 
				
				+ "?uri rdf:type foaf:Agent . \n"
				+ "?uri core:relatedBy ?c . \n"
				+ "?c rdf:type core:AdvisingRelationship .  \n"
				+ "?c core:relates ?d .  \n"
				+ "?d rdf:type core:AdviseeRole  .  \n"
				+ "?d obo:RO_0000052 ?e .  \n"
				+ "?e rdf:type foaf:Person .  \n"
				+ "}  \n"
				+ "UNION {  \n"
				+ " ?uri rdf:type foaf:Agent .  \n"
				+ " ?uri core:relatedBy ?c .  \n"
				+ " ?c rdf:type core:Authorship . \n"
				+ " OPTIONAL {?c core:relates ?f . \n"
				+ "           ?f rdf:type foaf:Person . } \n"
				+ " OPTIONAL  { ?c core:relates ?h . \n"
				+ "             ?h rdf:type obo:IAO_0000030 . } \n"
				+ " } } ");
		
	    // If a person changes then update
		// award giver
		multiValuedQueriesForAgent.add(prefix +
				"SELECT (str(?d) as ?awardConferredBy)  \n" +
				"WHERE {\n"
				
				+ "?uri rdf:type foaf:Agent  ; ?b ?c . \n"
				+ " ?c rdf:type core:AwardReceipt . \n"
				
				+ " OPTIONAL { ?c core:assignedBy ?d . } . \n"
				+ " }");
		
        // If a person changes then update
		// organization for role
		multiValuedQueriesForAgent.add(prefix +
				"SELECT (str(?Organization) as ?organization)  \n" +
				"WHERE {\n"
				
				+ "?uri rdf:type foaf:Agent  ; ?b ?c . \n"
				+ " ?c rdf:type obo:BFO_0000023 ; obo:BFO_0000054 ?Organization .\n"
				+ " }");
		
        // If a person changes then update
		// organization in educational training
		multiValuedQueriesForAgent.add(prefix + 
				"SELECT  \n" +
		         	"(str(?e) as ?trainingAtOrganization) WHERE {\n"
					
					+ " ?uri rdf:type foaf:Agent ; ?b ?c . \n"
					+ " ?c rdf:type core:EducationalProcess . \n"
					  
					+ " OPTIONAL { ?c obo:RO_0000057 ?e  . \n"
					+ "            ?e rdf:type foaf:Organization . } . "					
					+"}");
		
		// If an organization changes then update
        // people in head of relations
        multiValuedQueriesForAgent.add(
                " # for organization, get leader  \n" +
                prefix + 
                "SELECT  \n" +
                    "(str(?e) as ?LeaderPerson ) WHERE {\n"
                    
                    + " ?uri rdf:type foaf:Agent  . \n"
                    + " ?uri core:contributingRole ?c . \n"
                    + " ?c rdf:type core:LeaderRole . \n"
                      
                    + " OPTIONAL { ?c obo:RO_0000052 ?e  . \n"                    
					+ "            ?e rdf:type foaf:Person . } . "					
                    +"}");
        
	}
	
	//multivalued query for obo:IAO_0000030 (Information Content Entity)
	static {
		
		multiValuedQueryForInformationContentEntity = prefix + 
				"SELECT  (str(?b) as ?linkedAuthor) (str(?d) as ?linkedInformationResource) \n"
		         + "(str(?f) as ?editor) \n" +
		         		"(str(?i) as ?features) WHERE {\n"
					
					+ " ?uri rdf:type obo:IAO_0000030 . \n"
					  
					+ " OPTIONAL { ?uri core:relatedBy ?a . \n"
					+ "            ?a rdf:type core:Authorship . \n"
					+ "            ?a core:relates ?b . ?b rdf:type foaf:Person .\n" 
					+ "            ?a core:relates ?d . ?d rdf:type obo:IAO_0000030 .\n"
				    +            "} . "

        			+ " OPTIONAL { ?uri core:relatedBy ?e . \n"
        			+ "            ?e rdf:type core:Editorship . \n"
    				+ "            ?e core:relates ?f . ?f rdf:type foaf:Person .\n" 
    				+			"} . "
					+ " OPTIONAL { ?uri core:features ?i . } . \n" 
					
					+"}" ;
	
	}

	protected static List<String> queriesForAuthorship(){
	    List<String> queries = new ArrayList<String>();
	    
	    //get additional URIs of information resources from author side
	    queries.add(
	     prefix  
         + "SELECT  (str(?a) as ?infoResource) WHERE {\n"
            
         + " ?uri rdf:type foaf:Person . \n"
         + " ?uri core:relatedBy ?aship .\n"                       
         + " ?aship rdf:type core:Authorship  .\n"                       
         +  "OPTIONAL { ?aship core:relates ?a . ?a rdf:type obo:IAO_0000030 } .\n"            
         +"}" );
	    
	    //get additional URIs of authors from information resource side
        queries.add(
         prefix  
         + "SELECT  (str(?a) as ?author ) WHERE {\n"
            
         + " ?uri rdf:type obo:IAO_0000030 . \n"
         + " ?uri core:relatedBy ?aship . ?aship rdf:type core:Authorship . \n"                       
         +  "OPTIONAL { ?aship core:relates ?a . ?a rdf:type foaf:Person  } .\n"            
         +"}" );
	    return queries;
	}
	
	protected static List<String> queriesForURLLink(){
	    List<String> queries = new ArrayList<String>();
        
        //get additional URIs when URLLink is changed
        queries.add(
         prefix  
         + "SELECT  (str(?x) as ?individual) WHERE {\n"
         
         + " ?i rdf:type  vcard:Individual . \n"
         + " ?i vcard:hasURL ?uri . \n"
         + " ?i obo:ARG_2000029 ?x . \n"
         +"}" );
        
        return queries;	    
	}
	
	protected static List<String> queriesForEducationalTraining(){
        List<String> queries = new ArrayList<String>();
        
        //if person changes, no additional URIs need to be
        //changed because the person is not displayed on the
        //degree individual or on the degree granting organization
        
        //if the degree changes, the person needs to be updated
        //since the degree name is shown on the person page.
        queries.add(
            prefix  
            + " SELECT  (str(?person) as ?personUri) WHERE {\n"
            
            + " ?uri rdf:type core:AcademicDegree . \n"                        
            + " ?uri core:relatedBy ?awardedDegree .\n" 
            + " ?awardedDegree rdf:type core:AwardedDegree .\n" 
            + " ?awardedDegree core:relates ?person .\n" 
            + " ?person rdf:type foaf:Person .\n" 
            +"}" );
        
        //if the organization changes the person needs to be updated
        //since the organization name is shown on the person page.
        queries.add(
            prefix  
            + " SELECT  (str(?person) as ?personUri) WHERE {\n"
            
            + " ?uri rdf:type foaf:Organization . \n"                        
            + " ?uri obo:RO_0000056 ?edTrainingNode .\n" 
            + " ?edTrainingNode rdf:type core:EducationalProcess . \n"
            + " ?edTrainingNode obo:RO_0000057 ?person . \n"
            + " ?person rdf:type foaf:Person ."
            +"}" );
        return queries;     
    }
	
	protected static List<String> queriesForPosition(){
        List<String> queries = new ArrayList<String>();  
                
        //If an organization changes, update people
        queries.add(
            prefix  
            + " SELECT  (str(?person) as ?personUri) WHERE {\n"
            
            + " ?uri rdf:type foaf:Organization . \n"                        
            + " ?uri core:relatedBy ?positionNode .\n" 
            + " ?positionNode rdf:type core:Position .\n" 
            + " ?positionNode core:relates ?person . \n"
            + " ?person rdf:type foaf:Person .\n" 
            +"}" );
        
        
        //if people change, update organizations 
        queries.add(
            prefix  
            + " SELECT  (str(?org) as ?orgUri) WHERE {\n"
            
            + " ?uri rdf:type foaf:Person . \n"       
            + " ?uri core:relatedBy ?positionNode .\n" 
            + " ?positionNode rdf:type core:Position .\n" 
            + " ?positionNode core:relates ?org . \n"
            + " ?org rdf:type foaf:Organization .\n" 
            +"}" );
        return queries;     
    }
	
	static{
		//	core:AttendeeRole
		// If the person changes, update the attendee role in organization
		// core:AttendeeRole applies to events, not organizations; updating accordingly - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?event) \n " +
				"WHERE {\n"			
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ "?c rdf:type core:AttendeeRole . \n"
				+ "?c obo:BFO_0000054 ?d . \n"
				+ "?d rdf:type event:Event .\n"
				+ " }");
		
		// If the organization changes, update the attendee role of person 
		// core:AttendeeRole applies to events, not organizations; updating accordingly - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				
				+ "?uri rdf:type event:Event . \n"
				+ "?uri obo:BFO_0000055 ?c . \n"
				+ "?c rdf:type core:AttendeeRole . \n"
				+ "?c obo:RO_0000052 ?d . \n"
				+ "?d rdf:type foaf:Person .\n"
				+ " }");	
			
		//	core:ClinicalRole  -- core:clinicalRoleOf
		
		// If the person changes, update the clinical role in project
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?project)  \n" +
				"WHERE {\n"				
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ "?c rdf:type core:ClinicalRole . \n"
				+ "?c obo:BFO_0000054 ?d .\n"
				+ "?d rdf:type core:Project .\n"
				+ " }");
		

	   // If the person changes, update the clinical role in service
	   multiValuedQueriesForRole.add(prefix +
			   "SELECT (str(?d) as ?service)  \n" +
			   "WHERE {\n"				
			   + "?uri rdf:type foaf:Person . \n"
			   + "?uri obo:RO_0000053 ?c . \n"
			   + "?c rdf:type core:ClinicalRole . \n"
			   + "?c core:roleContributesTo ?d .\n"
			   + "?d rdf:type obo:ERO_0000005 .\n"
			   + " }");
	
		// If the project changes, update the clinical role of person 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type core:Project  . \n"
				+ "?uri obo:BFO_0000055 ?c . \n"
				+ "?c rdf:type core:ClinicalRole . \n"
				+ "?c obo:RO_0000052 ?d .\n "
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
		// If the service changes, update the clinical role of person 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"			
				+ "?uri rdf:type obo:ERO_0000005 . \n"
				+ "?uri core:contributingRole ?c . \n"
				+ "?c rdf:type core:ClinicalRole . \n"
				+ "?c obo:RO_0000052 ?d .\n "
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
				
		// If the person changes, update the leader role in organization
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?organization)  \n" +
				"WHERE {\n"				
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ "?c rdf:type core:LeaderRole . \n"
				+ "?c core:roleContributesTo ?d .\n"
				+ "?d rdf:type foaf:Organization .\n "
				+ " }");
		
		// If the organization changes, update the leader role of person 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"				
				+ "?uri rdf:type foaf:Organization . \n"
				+ "?uri core:contributingRole ?c . \n"
				+ "?c rdf:type core:LeaderRole . \n"
				+ "?c obo:RO_0000052 ?d .\n "
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
		//	core:MemberRole -- core:memberRoleOf
		
		// If the person changes, update the member role in organization
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?organization)  \n" +
				"WHERE \n{"
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ "?c rdf:type core:MemberRole . \n"
				+ "?c core:roleContributesTo ?d .\n"
				+ "?d rdf:type foaf:Organization .\n "
				+ " }");
		
		// If the organization changes, update the member role of person 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {"
				+ "?uri rdf:type foaf:Organization . \n"
				+ "?uri core:contributingRole ?c . \n"
				+ "?c rdf:type core:MemberRole . \n"
				+ "?c obo:RO_0000052 ?d .\n "
				+ "?d rdf:type foaf:Person .\n "
				+ " }");

		//	core:OrganizerRole -- core:organizerRoleOf
		
		// If the person changes, update the organizer role in organization
		// organizerRole appplies to events not organizations; updating accordingly - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?event)  \n" +
				"WHERE {"
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ "?c rdf:type core:OrganizerRole .\n"
				+ "?c obo:BFO_0000054 ?d .\n"
				+ "?d rdf:type event:Event .\n "
				+ " }");
		
		// If the organization changes, update the organizer role of person 
		// organizerRole appplies to events not organizations; updating accordingly - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type event:Event . \n"
				+ "?uri obo:BFO_0000055 ?c . \n"
				+ "?c rdf:type core:OrganizerRole . \n"
				+ "?c obo:RO_0000052 ?d .\n "
				+ "?d rdf:type foaf:Person .\n "
				+ " }");

		//	core:OutreachProviderRole -- core:outreachProviderRoleOf
		
		// If the person changes, update the outreach provider role in organization
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?organization)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ "?c rdf:type core:OutreachProviderRole .\n"
				+ "?c core:roleContributesTo ?d .\n"
				+ "?d rdf:type foaf:Organization .\n "
				+ " }");
		
		// If the organization changes, update the outreach provider role of person 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type foaf:Organization . \n"
				+ "?uri core:contributingRole ?c . \n"
				+ "?c rdf:type core:OutreachProviderRole . \n"
				+ "?c obo:RO_0000052 ?d .\n "
				+ "?d rdf:type foaf:Person .\n "
				+ " }");

		//	core:PresenterRole -- core:presenterRoleOf
		
		// If the person changes, update the presentation
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?presentation)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ " ?c rdf:type core:PresenterRole . \n"
				+ " ?c obo:BFO_0000054 ?d .\n"
				+ " ?d rdf:type core:Presentation . \n"
				+ " }");
		
		// If the presentation changes, update the person 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type core:Presentation . \n"
				+ "?uri obo:BFO_0000055 ?c . \n"
				+ " ?c rdf:type core:PresenterRole . \n "
				+ "?c obo:RO_0000052 ?d .\n "
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
		//	core:ResearcherRole -- core:researcherRoleOf
		
		// If the person changes, update the grant
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?grant)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type foaf:Person . \n"
				+ "?uri obo:RO_0000053 ?c . \n"
				+ " ?c rdf:type core:ResearcherRole . \n "
				+ " ?c core:relatedBy ?d .\n"
				+ " ?d rdf:type core:Grant . \n"
				+ " }");
		
		// If the grant changes, update the researcher 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type core:Grant . \n"
				+ "?uri core:relates ?c . \n"
				+ " ?c rdf:type core:ResearcherRole . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
		// If the grant changes, update the principal investigator 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type core:Grant . \n"
				+ " ?uri core:relates ?c . \n"
				+ " ?c rdf:type core:PrincipalInvestigatorRole . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");

		// If the grant changes, update the co-principal investigator 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type core:Grant . \n"
				+ " ?uri core:relates ?c . \n"
				+ " ?c rdf:type core:CoPrincipalInvestigatorRole . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
		
		// If the grant changes, update the investigator 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type core:Grant . \n"
				+ " ?uri core:relates ?c . \n"
				+ " ?c rdf:type core:InvestigatorRole . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
    	// If the person changes, update the project
    	multiValuedQueriesForRole.add(prefix +
    			"SELECT (str(?d) as ?project)  \n" +
    			"WHERE {\n"
    			+ "?uri rdf:type foaf:Person . \n"
    			+ "?uri obo:RO_0000053 ?c . \n"
    			+ " ?c rdf:type core:ResearcherRole . \n "
    			+ " ?c obo:BFO_0000054 ?d .\n"
    			+ " ?d rdf:type core:Project . \n"
    			+ " }");

		// If the project changes, update the researcher 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type core:Project . \n"
    			+ " ?uri obo:BFO_0000055 ?c .\n"
				+ " ?c rdf:type core:ResearcherRole . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");		

		//	core:EditorRole -- core:editorRoleOf, core:forInformationResource (person, informationresource)
		
		// If the person changes, update the editor role of the info resource
		// changing foaf:Organization to info content entity. Org no longer applies here - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?informationResource)  \n" +
				"WHERE {\n"
    			+ "?uri rdf:type foaf:Person . \n"
    			+ "?uri obo:RO_0000053 ?c . \n"
    			+ " ?c rdf:type core:EditorRole . \n "
				+ " ?c core:roleContributesTo ?d .\n"
				+ "?d rdf:type obo:IAO_0000030 .\n "
				+ " }");
		
		
		// If the info respource changes, update the editor role of person 
		// changing foaf:Organization to info content entity. Org no longer applies here - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type obo:IAO_0000030 . \n"
    			+ "?uri core:contributingRole ?c . \n"
    			+ " ?c rdf:type core:EditorRole . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
		// Next two queries are covered by the previous two. Commenting them out - tlw72
		// If the person changes, update the information resource associated with editor role
/*		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?informationResource)  \n" +
				"WHERE {\n"
				
				+ "?uri rdf:type foaf:Person  ; ?b ?c . \n"
				+ " ?c rdf:type core:EditorRole ; core:forInformationResource ?d .\n"
				+ " }");
		
		// If the organization changes, update the information resource associated with editor role
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?informationResource)  \n" +
				"WHERE {\n"
				
				+ "?uri rdf:type foaf:Organization  ; ?b ?c . \n"
				+ " ?c rdf:type core:EditorRole ; core:forInformationResource ?d .\n"
				+ " }");
*/		
		//	core:ServiceProviderRole -- core:serviceProviderRoleOf
		
		// If the person changes, update the service provider role in organization
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?organization)  \n" +
				"WHERE {\n"
    			+ "?uri rdf:type foaf:Person . \n"
    			+ "?uri obo:RO_0000053 ?c . \n"
				+ " ?c rdf:type obo:ERO_0000012 . \n"
				+ " ?c core:roleContributesTo ?d .\n"
				+ " ?d rdf:type foaf:Organization .\n "
				+ " }");
		
		// If the organization changes, update the service provider role of person 
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type foaf:Organization . \n"
    			+ "?uri core:contributingRole ?c . \n"
    			+ " ?c rdf:type obo:ERO_0000012 . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
		
		//	core:TeacherRole -- core:teacherRoleOf
		
		// If the person changes, update the teacher role in organization
		// updated to make this an Event (e.g., a course) not an organization - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?event)  \n" +
				"WHERE {\n"
    			+ "?uri rdf:type foaf:Person . \n"
    			+ "?uri obo:RO_0000053 ?c . \n"
				+ " ?c rdf:type core:TeacherRole . \n"
				+ " ?c obo:BFO_0000054 ?d .\n"
				+ " ?d rdf:type event:Event .\n "
				+ " }");
		
		// If the organization changes, update the teacher role of person 
		// updated to make this an Event (e.g., a course) not an organization - tlw72
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?person)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type event:Event . \n"
    			+ "?uri obo:BFO_0000055 ?c . \n"
    			+ " ?c rdf:type core:TeacherRole . \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		

		//	core:ReviewerRole -- core:forInformationResource, core:reviewerRoleOf
//		core:PeerReviewerRole -- core:forInformationResource, core:reviewerRoleOf
		
		// If the person changes, update the information resource associated with reviewer role
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?informationResource) \n " +
				"WHERE {\n"
    			+ "?uri rdf:type foaf:Person . \n"
    			+ "?uri obo:RO_0000053 ?c . \n"
				+ " ?c rdf:type core:ReviewerRole . \n"
				+ " ?c core:roleContributesTo ?d .\n"
				+ " ?d rdf:type obo:IAO_0000030 .\n "
				+ " }");
		
		// If the organization changes, update the information resource associated with reviewer role
		multiValuedQueriesForRole.add(prefix +
				"SELECT (str(?d) as ?informationResource)  \n" +
				"WHERE {\n"
				+ "?uri rdf:type obo:IAO_0000030 . \n"
    			+ "?uri core:contributingRole ?c . \n"
    			+ " ?c rdf:type core:ReviewerRole. \n "
				+ " ?c obo:RO_0000052 ?d .\n"
				+ "?d rdf:type foaf:Person .\n "
				+ " }");
		
	}
	
	static{
	    List<String> tmpList = new ArrayList<String>();
	    tmpList.add(multiValuedQueryForInformationContentEntity);
	    tmpList.addAll(multiValuedQueriesForAgent);
	    tmpList.addAll(multiValuedQueriesForRole);
	    tmpList.addAll( queriesForAuthorship());
	    tmpList.addAll(queriesForURLLink());
	    tmpList.addAll(queriesForEducationalTraining());
	    tmpList.addAll(queriesForPosition());
	    
        queryList = Collections.unmodifiableList(tmpList);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
