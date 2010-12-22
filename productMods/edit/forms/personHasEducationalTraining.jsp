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

core:educationalTraining (Person : EducationalTraining) - inverse of core:educationalTrainingOf
core:educationalTrainingOf (EducationalTraining : Person) - inverse of core:educationalTraining

core:degreeEarned (EducationalTraining : AcademicDegree) - inverse of core:degreeOutcomeOf
core:degreeOutcomeOf (AcademicDegree : EducationalTraining) - inverse of core:degreeEarned

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
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary.Precision"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.Field"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.elements.DateTimeWithPrecision"%>

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
<c:set var="type" value="<%= VitroVocabulary.RDF_TYPE %>" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="orgClass" value="http://xmlns.com/foaf/0.1/Organization" />
<c:set var="degreeClass" value="${vivoCore}AcademicDegree" />

<%-- Define predicates used in n3 assertions and sparql queries --%>
<c:set var="majorFieldPred" value="${vivoCore}majorField" />
<c:set var="deptPred" value="${vivoCore}departmentOrSchool" />
<c:set var="infoPred" value="${vivoCore}supplementalInformation" />
<c:set var="degreeEarned" value="${vivoCore}degreeEarned" />
<c:set var="degreeOutcomeOf" value="${vivoCore}degreeOutcomeOf" />
<c:set var="trainingAtOrg" value="${vivoCore}trainingAtOrganization" />

<c:set var="dateTimeValue" value="${vivoCore}dateTime"/>
<c:set var="dateTimeValueType" value="${vivoCore}DateTimeValue"/>
<c:set var="dateTimePrecision" value="${vivoCore}dateTimePrecision"/>
<c:set var="edToDateTime" value="${vivoCore}dateTimeInterval"/>

<%-- For new datetime handling in ontology - v1.2
<c:set var="dateTimeValue" value="${vivoCore}DateTimeValue" />
<c:set var="hasDateTimeValue" value="${vivoCore}dateTimeValue" />
<c:set var="precisionValue" value="${vivoCore}YearPrecision" />
<c:set var="hasPrecision" value="${vivoCore}dateTimePrecision" />
--%>

<v:jsonset var="existingYearQuery" >  
    SELECT ?existingYear WHERE {
          ?edTraining <${edToDateTime}> ?dateTimeNode .
          ?dateTimeNode <${dateTimeValue}> ?existingYear . }
</v:jsonset>

<v:jsonset var="existingYearPrecision" >  
    SELECT ?existingPrecision WHERE {
          ?edTraining <${edToDateTime}> ?dateTimeNode .
          ?dateTimeNode <${dateTimePrecision}> ?existingPrecision . }
</v:jsonset>

<v:jsonset var="existingDateTimeQuery" >  
    SELECT ?dateTime WHERE {  ?edTraining <${edToDateTime}> ?dateTime .  }
</v:jsonset>

<%-- Assertions for adding a new educational training entry --%>

<v:jsonset var="orgTypeAssertion">
    ?org a ?orgType .
</v:jsonset>

<v:jsonset var="orgLabelAssertion">
    ?org <${label}> ?orgLabel .
</v:jsonset>

<v:jsonset var="degreeAssertion" >      
    ?edTraining <${degreeEarned}> ?degree .
    ?degree <${degreeOutcomeOf}> ?edTraining .
</v:jsonset>

<v:jsonset var="majorFieldAssertion" >      
    ?edTraining <${majorFieldPred}> ?majorField .
</v:jsonset>

<v:jsonset var="dateTimeAssertions">
    ?edTraining    <${edToDateTime}> ?dateTimeNode .
    ?dateTimeNode  <${type}> <${dateTimeValueType}> .
    ?dateTimeNode  <${dateTimeValue}> ?dateTime.value .
    ?dateTimeNode  <${dateTimePrecision}> ?dateTime.precision . 
</v:jsonset>

<v:jsonset var="deptAssertion" >      
    ?edTraining <${deptPred}> ?dept .
</v:jsonset>

<v:jsonset var="infoAssertion" >      
    ?edTraining <${infoPred}> ?info .
</v:jsonset>

<v:jsonset var="n3ForNewEdTraining">       
    @prefix core: <${vivoCore}> .     

    ?person core:educationalTraining  ?edTraining .
    
    ?edTraining  a core:EducationalTraining ;
                 core:educationalTrainingOf ?person ;
                 <${trainingAtOrg}> ?org .
</v:jsonset>

<%-- This property has no inverse --%>
<v:jsonset var="n3ForEdTrainingToOrg" >      
    ?edTraining <${trainingAtOrg}> ?org .
</v:jsonset>

<%-- Queries for editing an existing educational training entry --%>

<v:jsonset var="orgQuery" >      
    SELECT ?existingOrg WHERE {
        ?edTraining <${trainingAtOrg}> ?existingOrg . }
</v:jsonset>

<v:jsonset var="orgLabelQuery" >      
    SELECT ?existingOrgLabel WHERE {
        ?edTraining <${trainingAtOrg}> ?existingOrg .
        ?existingOrg <${label}> ?existingOrgLabel .
    }
</v:jsonset>

<v:jsonset var="orgTypeQuery" >      
    SELECT ?existingOrgType WHERE {
        ?edTraining <${trainingAtOrg}> ?existingOrg .
        ?existingOrg a ?existingOrgType .
    }
</v:jsonset>

<v:jsonset var="degreeQuery" >      
    SELECT ?existingDegree WHERE {
        ?edTraining <${degreeEarned}> ?existingDegree . }
</v:jsonset>

<v:jsonset var="majorFieldQuery" >  
    SELECT ?existingMajorField WHERE {
          ?edTraining <${majorFieldPred}> ?existingMajorField . }
</v:jsonset>

<v:jsonset var="deptQuery" >  
    SELECT ?existingDept WHERE {
          ?edTraining <${deptPred}> ?existingDept . }
</v:jsonset>

<v:jsonset var="infoQuery" >  
    SELECT ?existingInfo WHERE {
          ?edTraining <${infoPred}> ?existingInfo . }
</v:jsonset>


<v:jsonset var="orgClassUriJson">${orgClass}</v:jsonset>
<v:jsonset var="degreeClassUriJson">${degreeClass}</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["edTraining", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForNewEdTraining}", "${orgLabelAssertion}", "${orgTypeAssertion}", "${dateTimeAssertions}" ],
    
    "n3optional"    : [ "${n3ForEdTrainingToOrg}", "${majorFieldAssertion}",                                          
                        "${degreeAssertion}", "${deptAssertion}", "${infoAssertion}" ],
                        
    "newResources"  : { "edTraining" : "${defaultNamespace}",
                        "org" : "${defaultNamespace}" ,
                        "dateTimeNode" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "org", "orgType", "degree" ],
    "literalsOnForm" : [ "orgLabel", "majorField", "dept", "info" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "orgLabel"           : "${orgLabelQuery}",
        "majorField"         : "${majorFieldQuery}",
        "dept"               : "${deptQuery}",
        "info"               : "${infoQuery}",        
        "dateTime.value"     :  "${existingYearQuery}"        
    },
    "sparqlForExistingUris" : {
        "org"            : "${orgQuery}",
        "orgType"        : "${orgTypeQuery}",
        "degree"         : "${degreeQuery}",
        "dateTimeNode"       : "${existingDateTimeQuery}",
        "dateTime.precision" : "${existingYearPrecision}"
    },
    "fields" : {
      "degree" : {
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
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${majorFieldAssertion}" ]
      },       
      "dateTime" : {
            "newResource"       : "true",
            "validators"        : [  ],
            "optionsType"       : "UNDEFINED",
            "literalOptions"    : [ ],
            "predicateUri"      : "",
            "objectClassUri"    : "",
            "rangeDatatypeUri"  : "",
            "rangeLang"         : "",
            "assertions"        : [ "${dateTimeAssertions}" ]
        },
     "org" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForEdTrainingToOrg}" ]
      },      
      "orgLabel" : {
         "newResource"      : "false",
         "validators"       : [  "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : [ "${orgLabelAssertion}" ]
      },
     "orgType" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "CHILD_VCLASSES",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${orgTypeAssertion}" ]
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
        
        //setup date time edit element
        Field dateTime = editConfig.getField("dateTime");
        dateTime.setEditElement( new DateTimeWithPrecision(dateTime, VitroVocabulary.Precision.YEAR));
        
        EditConfiguration.putConfigInSession(editConfig,session);
    }
        
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    if (objectUri != null) { // editing existing
        editConfig.prepareForObjPropUpdate(model);
    } else { // adding new
        editConfig.prepareForNonUpdate(model);
    }
    
    editConfig.setTemplate("personHasEducationalTraining.ftl"); 
    editConfig.setSubmitToUrl("/edit/processRdfForm2.jsp");
        
    String subjectName = ((Individual) request.getAttribute("subject")).getName();
%> 

    <c:set var="subjectName" value="<%= subjectName %>" />
<%
    if (objectUri != null) { // editing existing entry
%>
        <c:set var="editMode" value="edit" />
        <c:set var="titleVerb" value="Edit" />
        <c:set var="title" value="Edit educational background entry for ${subjectName}" />
        <c:set var="submitButtonText" value="Edit Educational Training" />
        <c:set var="disabledVal" value="disabled" />
<% 
    } else { // adding new entry
%>
        <c:set var="editMode" value="add" />
        <c:set var="titleVerb" value="Create" />
        <c:set var="submitButtonText" value="Educational Training" />
        <c:set var="disabledVal" value="" />
<%  } 

    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
                                                                JavaScript.CUSTOM_FORM_UTILS.path(),
                                                                "/edit/forms/js/customFormWithAutocomplete.js"                                                    
                                                               ));            
    request.setAttribute("customJs", customJs);

    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
                                                   Css.CUSTOM_FORM.path(),
                                                   "/edit/forms/css/customFormWithAutocomplete.css"
                                                  ));                                                                                                                                   
    request.setAttribute("customCss", customCss); 
%>

<%-- 
This goes to an experimental FM based form: 
<jsp:forward page="/N3EditForm"/> 
--%>

 


<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="yearHint" value="<span class='hint'>(YYYY)</span>" />

<jsp:include page="${preForm}" />

<h2>${titleVerb}&nbsp;educational training entry for <%= subjectName %></h2>

<form class="customForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <v:input type="select" label="Degree" id="degree"  />  
    
    <v:input type="text" label="Major Field of Degree" id="majorField" size="30" />   
       
    <v:input id="dateTime" />  
    
    <p class="inline"><v:input type="select" label="Organization Type ${requiredHint}" name="orgType" disabled="${disabledVal}" id="typeSelector" /></p>
           
    <p><v:input type="text" id="relatedIndLabel" name="orgLabel" label="### Name ${requiredHint}" cssClass="acSelector" disabled="${disabledVal}" size="50"  /></p>

    <c:if test="${editMode == 'edit'}">
       <v:input type="hidden" id="orgType" />
       <v:input type="hidden" id="orgLabel" />
    </c:if>

    <div class="acSelection">

        <p class="inline"><label></label><span class="acSelectionInfo"></span> <a href="<c:url value="/individual?uri=" />" class="verifyMatch">(Verify this match)</a></p>
        <v:input type="hidden" id="org" cssClass="acUriReceiver" /> <!-- Field value populated by JavaScript -->
    </div>

    <v:input type="text" label="Department or School Name within the ###" id="dept" size="50" />
    
    <v:input type="text" label="Supplemental Information" id="info" size="50" />
    <p>e.g., <em>Postdoctoral training</em> or <em>Transferred</em></p>    
    
    <p class="submit"><v:input type="submit" id="submit" value="${submitButtonText}" cancel="true"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<c:url var="acUrl" value="/autocomplete?tokenize=true&stem=true" />

<script type="text/javascript">
var customFormData  = {
    acUrl: '${acUrl}',
    editMode: '${editMode}',
    submitButtonTextType: 'compound',
    defaultTypeName: 'organization'
};
</script>

<jsp:include page="${postForm}"/>
