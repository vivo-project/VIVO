<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

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
    
    String flagURI = null;
    if (vreq.getAppBean().isFlag1Active()) {
        flagURI = VitroVocabulary.vitroURI+"Flag1Value"+vreq.getPortal().getPortalId()+"Thing";
    } else {
        flagURI = wdf.getVClassDao().getTopConcept().getURI();  // fall back to owl:Thing if not portal filtering
    }
    vreq.setAttribute("flagURI",flagURI);
    
    request.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    request.setAttribute("gYearDatatypeUriJson", MiscWebUtils.escape(XSD.gYear.toString()));
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdf" value="<%= VitroVocabulary.RDF %>" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="edBackgroundClass" value="${vivoCore}EducationalBackground" />
<c:set var="orgClass" value="http://xmlns.com/foaf/0.1/Organization" />
<c:set var="degreeClass" value="${vivoCore}AcademicDegree" />
<%--
Classes: 
core:EducationalBackground - primary new individual being created
foaf:Person - existing individual
foaf:Organization - new or existing individual
core:AcademicDegree - existing individual

Data properties of EducationalBackground:
core:majorField
core:year
core:departmentOrSchool
core:supplementalInformation

Object properties (domain : range)

core:educationalBackground (Person : EducationalBackground) - inverse of educationalBackgroundOf
core:educationalBackgroundOf (EducationalBackground : Person) - inverse of educationalBackground

core:degreeTypeAwarded (EducationalBackground : AcademicDegree) - inverse of awardedTo
core:awardedTo (AcademicDegree : EducationalBackground) - inverse of degreeTypeAwarded

core:organizationGrantingDegree (EducationalBackground : Organization) - no inverse

<%-- Data properties --%>
<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<c:set var="majorFieldPred" value="${vivoCore}majorField" />
<v:jsonset var="majorFieldExisting" >  
    SELECT ?majorFieldExisting WHERE {
          ?edBackgroundUri <${majorFieldPred}> ?majorFieldExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="majorFieldAssertion" >      
    ?edBackgroundUri <${majorFieldPred}> ?majorField .
</v:jsonset>

<c:set var="yearPred" value="${vivoCore}year" />
<v:jsonset var="yearExisting" >  
    SELECT ?existingYear WHERE {
          ?edBackgroundUri <${yearPred}> ?existingYear }
</v:jsonset>
<v:jsonset var="yearAssertion" >      
    ?edBackgroundUri <${yearPred}> ?year .
</v:jsonset>

<c:set var="deptPred" value="${vivoCore}departmentOrSchool" />
<v:jsonset var="deptExisting" >  
    SELECT ?existingDept WHERE {
          ?edBackgroundUri <${deptPred}> ?existingDept }
</v:jsonset>
<v:jsonset var="deptAssertion" >      
    ?edBackgroundUri <${deptPred}> ?dept .
</v:jsonset>

<c:set var="infoPred" value="${vivoCore}supplementalInformation" />
<v:jsonset var="infoExisting" >  
    SELECT ?existingInfo WHERE {
          ?edBackgroundUri <${infoPred}> ?existingInfo }
</v:jsonset>
<v:jsonset var="infoAssertion" >      
    ?edBackgroundUri <${infoPred}> ?info .
</v:jsonset>

<%-- Object properties --%>
<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<c:set var="hasDegree" value="${vivoCore}degreeTypeAwarded" />
<c:set var="degreeFor" value="${vivoCore}awardedTo" />
<v:jsonset var="degreeExisting" >      
    SELECT ?existingDegreeUri WHERE {
        ?edBackgroundUri <${hasDegree}> ?existingDegreeUri }
</v:jsonset>
<v:jsonset var="degreeAssertion" >      
    ?edBackgroundUri <${hasDegree}> ?degreeUri .
    ?degreeUri <${degreeFor}> ?edBackgroundUri .
</v:jsonset>

<c:set var="orgGrantingDegree" value="${vivoCore}organizationGrantingDegree" />
<%-- This property has no inverse --%>
<v:jsonset var="organizationUriExisting" >      
    SELECT ?existingOrgUri WHERE {
        ?edBackgroundUri <${orgGrantingDegree}> ?existingOrgUri }
</v:jsonset>
<v:jsonset var="organizationUriAssertion" >      
    ?edBackgroundUri <${orgGrantingDegree}> ?organizationUri .
</v:jsonset>

<%-- Do we need anything like this? EducationalBackground has no subtypes 
<v:jsonset var="positionTypeExisting">
    SELECT ?existingPositionType WHERE {
        ?positionUri <${type}> ?existingPositionType }
</v:jsonset>
<v:jsonset var="positionTypeAssertion">
    ?positionUri <${type}> ?positionType .
</v:jsonset>
--%>

<v:jsonset var="newOrgNameAssertion">
    ?newOrg <${label}> ?newOrgName .
</v:jsonset>

<v:jsonset var="newOrgTypeAssertion">
    ?newOrg a ?newOrgType .
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson">       
    @prefix core: <${vivoCore}> .     

    ?person core:educationalBackground  ?edBackgroundUri .
    
    ?edBackgroundUri core:educationalBackgroundOf ?person ;
                     a core:EducationalBackground ;
                     a <${flagURI}> .
</v:jsonset>

<v:jsonset var="n3ForNewOrg">
    ?newOrg <${label}> ?newOrgName ;
            a ?newOrgType ;
            a <${flagURI}> .
            
    ?edBackgroundUri <${orgGrantingDegree}> ?newOrg .
</v:jsonset>

<v:jsonset var="edBackgroundClassUriJson">${edBackgroundClass}</v:jsonset>
<v:jsonset var="orgClassUriJson">${orgClass}</v:jsonset>
<v:jsonset var="degreeClassUriJson">${degreeClass}</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["edBackgroundUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToPerson}", "${degreeAssertion}", "${majorFieldAssertion}", "${yearAssertion}" ],
    
    "n3optional"    : [ "${organizationUriAssertion}",                         
                        "${n3ForNewOrg}", "${newOrgNameAssertion}", "${newOrgTypeAssertion}",                       
                        "${deptAssertion}", "${infoAssertion}" ],
                        
    "newResources"  : { "edBackgroundUri" : "${defaultNamespace}",
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
        "year"               : "${yearExisting}",
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
         "validators"       : [ "nonempty" ],
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
         "validators"       : [ "nonempty", "datatype:${gYearDatatypeUriJson}" ],
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
    
    List<String> customJs = new ArrayList<String>(Arrays.asList("forms/js/customForm.js"
                                                                //, "forms/js/customFormOneStep.js"
                                                                ));
    request.setAttribute("customJs", customJs);
    
    List<String> customCss = new ArrayList<String>(Arrays.asList("forms/css/customForm.css",
                                                                 "forms/css/personHasEducationalBackground.css"
                                                                 ));
    request.setAttribute("customCss", customCss);   
%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />

<jsp:include page="${preForm}" />

<h2>${title}</h2>

<form class="${editType}" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <div class="entry"> 
        <v:input type="select" label="Degree ${requiredHint}" labelClass="required" id="degreeUri"  />  
        <v:input type="text" label="Major Field of Degree ${requiredHint}" id="majorField" size="30" />      
        <p class="inline year"><v:input type="text" label="Year ${requiredHint} <span class='hint'>(YYYY)</span>" id="year" size="4" /></p>  
    </div>
     
    <div class="relatedIndividual">
        <div class="existing">
            <v:input type="select" label="Organization Granting Degree" labelClass="required" id="organizationUri"  /><span class="existingOrNew">or</span>
        </div>
    
        <div class="addNewLink">
            If your organization is not listed, please <a href="#">add a new organization</a>.    
        </div>
      
        <div class="new">
            <h6>Add a New Organization</h6>
            <v:input type="text" label="Organization Name" labelClass="required" id="newOrgName" />
            <v:input type="select" label="Select Organization Type" labelClass="required" id="newOrgType" />
        </div>   
    </div> 
    
    <div class="entry"> 
        <v:input type="text" label="Department or School Name within the Organization" id="dept" size="50" />
        <v:input type="text" label="Supplemental Information" id="info" size="50" />
        <p>e.g., <em>Magna cum laude</em> or <em>Graduate School Fellowship, 1975-1976</em></p>    
    </div>
    
    <!-- Processing information for Javascript -->
    <input type="hidden" name="editType" value="${editType}" />
    <input type="hidden" name="entryType" value="educational background" /> 
    <input type="hidden" name="secondaryType" value="organization" />
    <input type="hidden" name="steps" value="1" />
    
    <p class="submit"><v:input type="submit" id="submit" value="${submitLabel}" cancel="${param.subjectUri}"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>

