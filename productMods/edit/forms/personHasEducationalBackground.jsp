<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding an educational attainment to an individual

Classes: 
core:EducationalTraining - primary new individual being created
foaf:Person - existing individual
foaf:Organization - new or existing individual
core:AcademicDegree - existing individual

Data properties of EducationalTraining:
core:majorField
core:departmentOrSchool
core:supplementalInformation

Object properties (domain : range)

core:educationalBackground (Person : EducationalTraining) - inverse of educationalBackgroundOf
core:educationalBackgroundOf (EducationalTraining : Person) - inverse of educationalBackground

core:degreeEarned (EducationalTraining : AcademicDegree) - inverse of degreeOutcomeOf
core:degreeOutcomeOf (AcademicDegree : EducationalTraining) - inverse of degreeEarned

core:organizationGrantingDegree (EducationalTraining : Organization) - no inverse

Future version
--------------
Classes:
core:DateTimeValue
core:DateTimeValuePrecision
Object properties:
core:dateTimeValue (EducationalTraining : DateTimeValue)
core:dateTimePrecision (DateTimeValue : DateTimeValuePrecision)
--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal"%>
<%@ page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasPositionValidator"%>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.personHasEducationalBackground.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    request.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    request.setAttribute("gYearDatatypeUriJson", MiscWebUtils.escape(XSD.gYear.toString()));
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="edAttainmentClass" value="${vivoCore}EducationalTraining" />
<c:set var="orgClass" value="http://xmlns.com/foaf/0.1/Organization" />
<c:set var="degreeClass" value="${vivoCore}AcademicDegree" />

<%-- Data properties --%>
<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<c:set var="majorFieldPred" value="${vivoCore}majorField" />
<v:jsonset var="majorFieldExisting" >  
    SELECT ?majorFieldExisting WHERE {
          ?edTrainingUri <${majorFieldPred}> ?majorFieldExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="majorFieldAssertion" >      
    ?edTrainingUri <${majorFieldPred}> ?majorField .
</v:jsonset>

<%-- For new datetime handling in ontology - v1.2
<c:set var="dateTimeValue" value="${vivoCore}DateTimeValue" />
<c:set var="hasDateTimeValue" value="${vivoCore}dateTimeValue" />
<c:set var="precisionValue" value="${vivoCore}YearPrecision" />
<c:set var="hasPrecision" value="${vivoCore}dateTimePrecision" />

<v:jsonset var="yearExisting" >  
    SELECT ?existingYear WHERE {
          ?edTrainingUri <${hasDateTimeValue}> ?existingYear }
</v:jsonset>
<v:jsonset var="yearAssertion" > 
    @prefix core: <${vivoCore}> .   
    ?dateTime a core:DateTimeValue ;
              core:dateTime ?year ;
              core:dateTimeValuePrecision core:YearPrecision .
    ?edTrainingUri core:dateTimeValue ?dateTime .
</v:jsonset>
--%>

<c:set var="yearPred" value="${vivoCore}year" />
<v:jsonset var="yearExisting" >  
    SELECT ?existingYear WHERE {
          ?edTrainingUri <${yearPred}> ?existingYear }
</v:jsonset>
<v:jsonset var="yearAssertion" >      
    ?edTrainingUri <${yearPred}> ?year .
</v:jsonset>

<c:set var="deptPred" value="${vivoCore}departmentOrSchool" />
<v:jsonset var="deptExisting" >  
    SELECT ?existingDept WHERE {
          ?edTrainingUri <${deptPred}> ?existingDept }
</v:jsonset>
<v:jsonset var="deptAssertion" >      
    ?edTrainingUri <${deptPred}> ?dept .
</v:jsonset>

<c:set var="infoPred" value="${vivoCore}supplementalInformation" />
<v:jsonset var="infoExisting" >  
    SELECT ?existingInfo WHERE {
          ?edTrainingUri <${infoPred}> ?existingInfo }
</v:jsonset>
<v:jsonset var="infoAssertion" >      
    ?edTrainingUri <${infoPred}> ?info .
</v:jsonset>

<%-- Object properties --%>
<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<c:set var="degreeEarned" value="${vivoCore}degreeEarned" />
<c:set var="degreeOutcomeOf" value="${vivoCore}degreeOutcomeOf" />
<v:jsonset var="degreeExisting" >      
    SELECT ?existingDegreeUri WHERE {
        ?edTrainingUri <${degreeEarned}> ?existingDegreeUri }
</v:jsonset>
<v:jsonset var="degreeAssertion" >      
    ?edTrainingUri <${degreeEarned}> ?degreeUri .
    ?degreeUri <${degreeOutcomeOf}> ?edTrainingUri .
</v:jsonset>

<c:set var="orgGrantingDegree" value="${vivoCore}organizationGrantingDegree" />
<%-- This property has no inverse --%>
<v:jsonset var="organizationUriExisting" >      
    SELECT ?existingOrgUri WHERE {
        ?edTrainingUri <${orgGrantingDegree}> ?existingOrgUri }
</v:jsonset>
<v:jsonset var="organizationUriAssertion" >      
    ?edTrainingUri <${orgGrantingDegree}> ?organizationUri .
</v:jsonset>

<v:jsonset var="newOrgNameAssertion">
    ?newOrg <${label}> ?newOrgName .
</v:jsonset>
<%-- Break up the new org type and subclass assertions, so that if there is no subclass, 
the org type still gets asserted. --%>
<v:jsonset var="newOrgTypeAssertion">
    ?newOrg a ?newOrgType .
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson">       
    @prefix core: <${vivoCore}> .     

    ?person core:educationalBackground  ?edTrainingUri .
    
    ?edTrainingUri core:educationalBackgroundOf ?person ;
                     a core:EducationalTraining .
</v:jsonset>

<v:jsonset var="n3ForNewOrg">
    ?newOrg <${label}> ?newOrgName ;
            a ?newOrgType .
            
    ?edTrainingUri <${orgGrantingDegree}> ?newOrg .
</v:jsonset>

<v:jsonset var="edAttainmentClassUriJson">${edAttainmentClass}</v:jsonset>
<v:jsonset var="orgClassUriJson">${orgClass}</v:jsonset>
<v:jsonset var="degreeClassUriJson">${degreeClass}</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["edTrainingUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToPerson}",  "${majorFieldAssertion}" ],
    
    "n3optional"    : [ "${organizationUriAssertion}",  "${n3ForNewOrg}",                       
                        "${newOrgNameAssertion}", "${newOrgTypeAssertion}",                       
                        "${degreeAssertion}", "${deptAssertion}", "${infoAssertion}", "${yearAssertion}" ],
                        
    "newResources"  : { "edTrainingUri" : "${defaultNamespace}",
                        "newOrg" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "organizationUri", "newOrgType", "degreeUri" ],
    "literalsOnForm" : [ "majorField", "year", "dept", "info", "newOrgName"],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "majorField"         : "${majorFieldExisting}",
        /*"year"               : "${yearExisting}",*/
        "dept"               : "${deptExisting}",
        "info"               : "${infoExisting}"
    },
    "sparqlForExistingUris" : {
        "organizationUri"   : "${organizationUriExisting}",
        "degreeUri"         : "${degreeExisting}"
    },
    "fields" : {
      "degreeUri" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${degreeClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${degreeAssertion}" ]
      },   
      "majorField" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty", "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${majorFieldAssertion}" ]
      },
      "year" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${gYearDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${gYearDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${yearAssertion}"]
      },     
     "organizationUri" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${organizationUriAssertion}" ]
      },      
      "newOrgName" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : [ "${n3ForNewOrg}" ]
      },
     "newOrgType" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "CHILD_VCLASSES",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${newOrgTypeAssertion}" ]
      },      
      "dept" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${deptAssertion}"]
      },
      "info" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${infoAssertion}"]
      }    
  }
}
</c:set>
<%
    log.debug(request.getAttribute("editjson"));

    EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
    if (editConfig == null) {
        editConfig = new EditConfiguration((String) request.getAttribute("editjson"));     
        EditConfiguration.putConfigInSession(editConfig,session);
    }
    
    editConfig.addValidator(new PersonHasPositionValidator());
    
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    if (objectUri != null) { // editing existing
        editConfig.prepareForObjPropUpdate(model);
    } else { // adding new
        editConfig.prepareForNonUpdate(model);
    }
    
    String subjectName = ((Individual) request.getAttribute("subject")).getName();
%> 

    <c:set var="subjectName" value="<%= subjectName %>" />
<%
    if (objectUri != null) { // editing existing entry
%>
        <c:set var="editType" value="edit" />
        <c:set var="title" value="Edit educational background entry for ${subjectName}" />
        <%-- NB This will be the button text when Javascript is disabled. --%>
        <c:set var="submitLabel" value="Save Changes" />
<% 
    } else { // adding new entry
%>
        <c:set var="editType" value="add" />
        <c:set var="title" value="Create a new educational background entry for ${subjectName}" />
        <%-- NB This will be the button text when Javascript is disabled. --%>
        <c:set var="submitLabel" value="Create New Educational Background" />
<%  } 
    
    List<String> customJs = new ArrayList<String>(Arrays.asList("/js/utils.js",            
                                                                "/js/customFormUtils.js",           
                                                                "/edit/forms/js/customForm.js"
                                                                //, "/edit/forms/js/customFormOneStep.js"
                                                                ));
    request.setAttribute("customJs", customJs);
    
    List<String> customCss = new ArrayList<String>(Arrays.asList("/edit/forms/css/customForm.css",
                                                                 "/edit/forms/css/personHasEducationalBackground.css"
                                                                 ));
    request.setAttribute("customCss", customCss);   
%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="view" value='<%= vreq.getAttribute("view") %>' />

<jsp:include page="${preForm}" />

<h2>${title}</h2>

<form class="${editType}" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <div class="entry"> 
        <v:input type="select" label="Degree" id="degreeUri"  />  
        <v:input type="text" label="Major Field of Degree ${requiredHint}" id="majorField" size="30" />      
        <p class="inline year"><v:input type="text" label="Year <span class='hint'>(YYYY)</span>" id="year" size="4" /></p>  
    </div>
     
    <div class="relatedIndividual">
        <div class="existing">
            <v:input type="select" label="Organization Granting Degree ${requiredHint}" id="organizationUri"  /><span class="existingOrNew">or</span>
        </div>
    
        <div class="addNewLink">
            If your organization is not listed, please <a href="#">add a new organization</a>.    
        </div>
      
        <div class="new">            
            <h6>Add a New Organization</h6>
            <v:input type="text" label="Organization Name ${requiredHint}" id="newOrgName" size="30" />
            <v:input type="select" label="Select Organization Type ${requiredHint}" id="newOrgType" />
        </div>   
    </div> 
    
    <div class="entry"> 
        <v:input type="text" label="Department or School Name within the Organization" id="dept" size="50" />
        <v:input type="text" label="Supplemental Information" id="info" size="50" />
        <p>e.g., <em>Postdoctoral training</em> or <em>Transferred</em></p>    
    </div>
    
    <!-- Processing information for Javascript -->
    <input type="hidden" name="editType" value="${editType}" />
    <input type="hidden" name="entryType" value="educational background" /> 
    <input type="hidden" name="secondaryType" value="organization" />
    <input type="hidden" name="steps" value="1" />
    <input type="hidden" name="view" value="${view}" />
    
    <p class="submit"><v:input type="submit" id="submit" value="${submitLabel}" cancel="true"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>
