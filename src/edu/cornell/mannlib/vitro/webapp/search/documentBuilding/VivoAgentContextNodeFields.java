/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.documentBuilding;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.search.documentBuilding.ContextNodeFields;

/**
 * Class that adds text from context nodes to Search Documents for 
 * foaf:Agent individuals.
 */
public class VivoAgentContextNodeFields extends ContextNodeFields{
    
    static List<String> queriesForAgent = new ArrayList<String>();    
    
    public VivoAgentContextNodeFields(RDFServiceFactory rdfServiceFactory){        
        super(queriesForAgent,rdfServiceFactory);
    }
      
  protected static final String prefix = 
        "prefix owl: <http://www.w3.org/2002/07/owl#> "
      + " prefix vitroDisplay: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#>  "
      + " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
      + " prefix core: <http://vivoweb.org/ontology/core#>  "
      + " prefix foaf: <http://xmlns.com/foaf/0.1/> "
      + " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
      + " prefix localNav: <http://vitro.mannlib.cornell.edu/ns/localnav#>  "
      + " prefix bibo: <http://purl.org/ontology/bibo/>  "
      + " prefix obo: <http://purl.obolibrary.org/obo/> \n" ;
  

  //queries for foaf:Agent
  static {
      
      /*  Positions for People */
    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  . " +
            " ?uri ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:hrJobTitle ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  . " +
            " ?uri ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:relates ?i . " +
            " ?i rdf:type foaf:Organization . " +
            " ?i rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  . " +
            " ?uri  ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:titleOrRole ?ContextNodeProperty .  }");
    
    /* HR Job Title */
    
    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?HRJobTitle) as ?hrJobTitle)  " +
            "(str(?PositionInOrganization) as ?positionInOrganization) " +
            "(str(?TitleOrRole) as ?titleOrRole) WHERE {" 
            
            + "?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:Position . "
            
            + " OPTIONAL { ?c core:hrJobTitle ?HRJobTitle . } . "
            + " OPTIONAL { ?c core:relates ?i . ?i rdf:type foaf:Organization . ?i rdfs:label ?PositionInOrganization .  } . "
            + " OPTIONAL { ?c core:titleOrRole ?TitleOrRole . } . "
            + " }");
    
    /* Advisor */
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:AdvisingRelationship . " +
            " ?c rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:AdvisingRelationship . " +
            " ?c core:degreeCandidacy ?e . ?e rdfs:label ?ContextNodeProperty . }");

    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?label) as ?adviseeLabel) WHERE {" +
            " ?uri rdf:type foaf:Agent  ." +            
            " ?c rdf:type core:AdvisingRelationship . " +
            " ?c core:relates ?uri . " +
            " ?uri obo:RO_0000053 ?advisorRole . " +
            " ?advisorRole rdf:type core:AdvisorRole . " +
            " ?c core:relates ?d . " +
            " ?d rdf:type foaf:Person . " +
            " ?d obo:RO_0000053 ?adviseeRole . " +
            " ?adviseeRole rdf:type core:AdviseeRole . " +
            " ?d rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?label) as ?advisorLabel) WHERE {" +
            " ?uri rdf:type foaf:Agent  ." +            
            " ?c rdf:type core:AdvisingRelationship . " +
            " ?c core:relates ?uri . " +
            " ?uri obo:RO_0000053 ?adviseeRole . " +
            " ?adviseeRole rdf:type core:AdviseeRole . " +
            " ?c core:relates ?d . " +
            " ?d rdf:type foaf:Person . " +
            " ?d obo:RO_0000053 ?advisorRole . " +
            " ?advisorRole rdf:type core:AdvisorRole . " +
            " ?d rdfs:label ?ContextNodeProperty . }");
    
    /* Author */
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Authorship . " +
            " ?c core:relates ?f . " +            
            " ?f rdf:type foaf:Person . " +
            " ?f rdfs:label ?ContextNodeProperty . " +
            " FILTER( ?f != ?uri  ) " +
            "}");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Authorship . " +
            " ?c core:relates ?h . " +
            " ?h rdf:type obo:IAO_0000030 . ?h rdfs:label ?ContextNodeProperty . }");
    
    /* Award */        

    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?AwardLabel) as ?awardLabel) " +
            "(str(?AwardConferredBy) as ?awardConferredBy)  " +
            "(str(?Description) as ?description)   " +                        
            "WHERE {"            
            + " ?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:AwardReceipt . "            
            + " OPTIONAL { ?c core:relates ?e . ?e rdf:type core:Award . ?e rdfs:label ?AwardLabel . } . "
            + " OPTIONAL { ?c core:assignedBy ?d . ?d rdf:type foaf:Organization . ?d rdfs:label ?AwardConferredBy . } . "
            + " OPTIONAL { ?c core:description ?Description . } . "
            + " }");
    
    /* Role In Organization */
    
    queriesForAgent.add(prefix +
            "SELECT (str(?OrganizationLabel) as ?organizationLabel)  WHERE {" 
            + " ?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type obo:BFO_0000023 ; core:roleContributesTo ?Organization ."
            + " ?Organization rdf:type core:Organization . "
            + " ?Organization rdfs:label ?OrganizationLabel . "
            + " }");    
                
    /* Academic Degree / Educational Training */
    
    queriesForAgent.add(prefix + 
            "SELECT  " +
            "(str(?AcademicDegreeLabel) as ?academicDegreeLabel) " +
            "(str(?AcademicDegreeAbbreviation) as ?academicDegreeAbbreviation) " +
            "(str(?MajorField) as ?majorField) " +
            "(str(?DepartmentOrSchool) as ?departmentOrSchool) " +
            "(str(?TrainingAtOrganizationLabel) as ?trainingAtOrganizationLabel) WHERE {"
                
                + " ?uri rdf:type foaf:Agent ; ?b ?c . "
                + " ?c rdf:type core:EducationalProcess . "
                  
                +  "OPTIONAL { ?c core:relates ?d . "
                +  "           ?d rdf:type core:AwardedDegree . "
                +  "           ?d core:relates ?e . "
                +  "           ?e rdf:type core:AcademicDegree . "
                +  "           ?e rdfs:label ?AcademicDegreeLabel . } . "
                +  "OPTIONAL { ?c core:majorField ?MajorField .} ."           
                + " OPTIONAL { ?c core:departmentOrSchool ?DepartmentOrSchool . }"            
                + " OPTIONAL { ?c obo:RO_0000057 ?f . ?f rdf:type foaf:organization . ?f rdfs:label ?TrainingAtOrganizationLabel . } . " 
                +"}");                 
  }
}
