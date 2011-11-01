/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Class that adds text from context nodes to Solr Documents for 
 * foaf:Agent individuals.
 */
public class VivoAgentContextNodeFields extends ContextNodeFields{
    
    static List<String> queriesForAgent = new ArrayList<String>();    
    
    public VivoAgentContextNodeFields(Model model){        
        super(model,queriesForAgent);
    }
      
  protected static final String prefix = 
        "prefix owl: <http://www.w3.org/2002/07/owl#> "
      + " prefix vitroDisplay: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#>  "
      + " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
      + " prefix core: <http://vivoweb.org/ontology/core#>  "
      + " prefix foaf: <http://xmlns.com/foaf/0.1/> "
      + " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
      + " prefix localNav: <http://vitro.mannlib.cornell.edu/ns/localnav#>  "
      + " prefix bibo: <http://purl.org/ontology/bibo/>  ";
  

  //queries for foaf:Agent
  static {
      
      /*  Position */
    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:hrJobTitle ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:involvedOrganizationName ?ContextNodeProperty . }");       
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:positionInOrganization ?i . ?i rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:titleOrRole ?ContextNodeProperty .  }");
    
    /* HR Job Title */
    
    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?HRJobTitle) as ?hrJobTitle)  " +
            "(str(?InvolvedOrganizationName) as ?involvedOrganizationName) " +
            "(str(?PositionInOrganization) as ?positionInOrganization) " +
            "(str(?TitleOrRole) as ?titleOrRole) WHERE {" 
            
            + "?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:Position . "
            
            + " OPTIONAL { ?c core:hrJobTitle ?HRJobTitle . } . "
            + " OPTIONAL { ?c core:involvedOrganizationName ?InvolvedOrganizationName . } ."            
            + " OPTIONAL { ?c core:positionInOrganization ?i . ?i rdfs:label ?PositionInOrganization .  } . "
            + " OPTIONAL { ?c core:titleOrRole ?TitleOrRole . } . "
            + " }");
    
    /* Advisor */
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:advisee ?d . ?d rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:degreeCandidacy ?e . ?e rdfs:label ?ContextNodeProperty . }");
    
    /* Author */
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:linkedAuthor ?f . " +            
            " ?f rdfs:label ?ContextNodeProperty . " +
            " FILTER( ?f != ?uri  ) " +
            "}");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:linkedInformationResource ?h . ?h rdfs:label ?ContextNodeProperty . }");
    
    /* Award */        
    
    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?AwardLabel) as ?awardLabel) " +
            "(str(?AwardConferredBy) as ?awardConferredBy)  " +
            "(str(?Description) as ?description)   WHERE {"
            
            + "?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:AwardReceipt . "
            
            + " OPTIONAL { ?c rdfs:label ?AwardLabel . } . "
            + " OPTIONAL { ?c core:awardConferredBy ?d . ?d rdfs:label ?AwardConferredBy . } . "
            + " OPTIONAL { ?c core:description ?Description . } . "
            + " }");
    
    /* Role In Organization */
    
    queriesForAgent.add(prefix +
            "SELECT (str(?OrganizationLabel) as ?organizationLabel)  WHERE {" 
            + "?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:Role ; core:roleIn ?Organization ."
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
                + " ?c rdf:type core:EducationalTraining . "
                  
                +  "OPTIONAL { ?c core:degreeEarned ?d . ?d rdfs:label ?AcademicDegreeLabel ; core:abbreviation ?AcademicDegreeAbbreviation . } . "
                +  "OPTIONAL { ?c core:majorField ?MajorField .} ."           
                + " OPTIONAL { ?c core:departmentOrSchool ?DepartmentOrSchool . }"            
                + " OPTIONAL { ?c core:trainingAtOrganization ?e . ?e rdfs:label ?TrainingAtOrganizationLabel . } . " 
                +"}");                 
  }
}
