<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for research, teaching, service, and outreach activities --%>

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
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.TitleCase" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.StartDateBeforeEndDate"%>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.personHasActivity.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior

    String flagUri = null;
    if (wdf.getApplicationDao().isFlag1Active()) {
        flagUri = VitroVocabulary.vitroURI+"Flag1Value"+vreq.getPortal().getPortalId()+"Thing";
    } else {
        flagUri = wdf.getVClassDao().getTopConcept().getURI();  // fall back to owl:Thing if not portal filtering
    }
    vreq.setAttribute("flagUri",flagUri);
    
    request.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    request.setAttribute("gYearMonthDatatypeUriJson", MiscWebUtils.escape(XSD.gYearMonth.toString()));
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="vitroNs" value="<%= VitroVocabulary.vitroURI %>" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="labelUri" value="${rdfs}label" />

<c:set var="researchActivityUri" value="${vivoCore}hasResearchActivity" />
<c:set var="teachingActivityUri" value="${vivoCore}hasTeachingActivity" />
<c:set var="serviceActivityUri" value="${vivoCore}hasProfessionalServiceActivity" />
<c:set var="outreachActivityUri" value="${vivoCore}hasOutreachActivity" />

<c:choose>
    <c:when test="${predicateUri == researchActivityUri}">
        <c:set var="inverseUri" value="${vivoCore}researchActivityBy" />
        <c:set var="activityClass" value="${vivoCore}ResearchActivity" />
    </c:when>
    <c:when test="${predicateUri == teachingActivityUri}">
        <c:set var="inverseUri" value="${vivoCore}teachingActivityBy" />
        <c:set var="activityClass" value="${vivoCore}TeachingActivity" />
    </c:when>
    <c:when test="${predicateUri == serviceActivityUri}">
        <c:set var="inverseUri" value="${vivoCore}professionalServiceActivityBy" />
        <c:set var="activityClass" value="${vivoCore}ServiceActivity" />
    </c:when>
    <c:when test="${predicateUri == outreachActivityUri}">
        <c:set var="inverseUri" value="${vivoCore}outreachActivityBy" />
        <c:set var="activityClass" value="${vivoCore}OutreachActivity" />
    </c:when>
</c:choose>

<c:set var="activitySuperClass" value="${vivoCore}Activity" />
<c:set var="superPropertyUri" value="${vivoCore}hasActivity" />
<c:set var="inverseSuperPropertyUri" value="${vivoCore}activityBy" />

<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<v:jsonset var="labelExisting" >  
    SELECT ?existingLabel WHERE {
          ?activityUri <${labelUri}> ?existingLabel }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="labelAssertion" >      
    ?activityUri <${labelUri}> ?label . 
</v:jsonset>

<c:set var="descriptionUri" value="${vitroNs}description" />
<v:jsonset var="descriptionExisting" >  
    SELECT ?existingDescription WHERE {
          ?activityUri <${descriptionUri}> ?existingDescription }
</v:jsonset>
<v:jsonset var="descriptionAssertion" >      
    ?activityUri <${descriptionUri}> ?description . 
</v:jsonset>

<c:set var="roleUri" value="${vivoCore}role" />
<v:jsonset var="roleExisting" >  
    SELECT ?existingRole WHERE {
          ?activityUri <${roleUri}> ?existingRole }
</v:jsonset>
<v:jsonset var="roleAssertion" >      
    ?activityUri <${roleUri}> ?role . 
</v:jsonset>

<c:set var="startYearMonthUri" value="${vivoCore}startYearMonth" />
<v:jsonset var="startYearMonthExisting" >      
      SELECT ?existingStartYearMonth WHERE {  
        ?activityUri <${startYearMonthUri}> ?existingStartYearMonth }
</v:jsonset>
<v:jsonset var="startYearMonthAssertion" >
      ?activityUri <${startYearMonthUri}> ?startYearMonth .
</v:jsonset>

<c:set var="endYearMonthUri" value="${vivoCore}endYearMonth" /> 
<v:jsonset var="endYearMonthExisting">     
      SELECT ?existingEndYearMonth WHERE {  
        ?activityUri <${endYearMonthUri}> ?existingEndYearMonth }
</v:jsonset>
<v:jsonset var="endYearMonthAssertion" >
      ?activityUri <${endYearMonthUri}> ?endYearMonth .
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson">  

    ?activityUri <${labelUri}> ?label ;
                 a <${activityClass}> ;
                 a <${activitySuperClass}> ;
                 a <${flagUri}> ;   
                 <${inverseUri}> ?person ;
                 <${inverseSuperPropertyUri}> ?person .

    ?person <${predicateUri}>  ?activityUri ;
            <${superPropertyUri}> ?activityUri .
    
</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["activityUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToPerson}", "${labelAssertion}", "${descriptionAssertion}", "${roleAssertion}" ],
    
    "n3optional"    : [ "${startYearMonthAssertion}", "${endYearMonthAssertion}" ],                        
                        
    "newResources"  : { "activityUri" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ ],
    "literalsOnForm" :  [ "label", "description", "role", "startYearMonth", "endYearMonth" ],                          
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "label"              : "${labelExisting}",
        "description"        : "${descriptionExisting}",
        "role"               : "${roleExisting}",
        "startYearMonth"     : "${startYearMonthExisting}",
        "endYearMonth"       : "${endYearMonthExisting}",
    },
    "sparqlForExistingUris" : { },

    "fields" : {
      "label" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${labelAssertion}" ]
      },
     "description" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${descriptionAssertion}" ]
      },         
     "role" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${roleAssertion}" ]
      }, 
      "startYearMonth" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${gYearMonthDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${gYearMonthDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${startYearMonthAssertion}"]
      },
      "endYearMonth" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${gYearMonthDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${gYearMonthDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${endYearMonthAssertion}"]
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
    
    editConfig.addValidator(new StartDateBeforeEndDate("startYearMonth","endYearMonth") ); 
    
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

    <c:choose>
        <c:when test="${predicateUri == researchActivityUri}">
            <c:set var="propertyName" value="research focus and activity" scope="request" />
        </c:when>
        <c:when test="${predicateUri == teachingActivityUri}">
            <c:set var="propertyName" value="teaching focus and activity" scope="request" />
        </c:when>            
        <c:when test="${predicateUri == serviceActivityUri}">
            <c:set var="propertyName" value="professional service" scope="request" />
        </c:when>           
        <c:when test="${predicateUri == outreachActivityUri}">
            <c:set var="propertyName" value="outreach and community service" scope="request"  />    
        </c:when>               
    </c:choose> 
<%
    // RY put propertyName in page context instead, and get a PageContext object
    String propName = (String)request.getAttribute("propertyName");
    vreq.setAttribute("submitPropertyName", TitleCase.toTitleCase(propName));

    if (objectUri != null) { // editing existing entry
%>
        <c:set var="title" value="Edit ${propertyName} entry for ${subjectName}" />
        <c:set var="submitLabel" value="Save Changes" />
<% 
    } else { // adding new entry
%>
        <c:set var="title" value="Create a new ${propertyName} entry for ${subjectName}" />
        <c:set var="submitLabel" value="Create New ${submitPropertyName}" />
<%  } 
    
    
    List<String> customCss = new ArrayList<String>(Arrays.asList("forms/css/customForm.css"                                                                
                                                                 ));
    request.setAttribute("customCss", customCss);   
%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="yearMonthHint" value="<span class='hint'>(YYYY-MM)</span>" />

<jsp:include page="${preForm}"/>

<h2>${title}</h2>

<form action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <v:input type="text" label="Activity Name ${requiredHint}" id="label" size="30" />
    <v:input type="textarea" label="Description  ${requiredHint}" id="description" rows="5" cols="20" />
    <v:input type="text" label="Role ${requiredHint}" id="role" size="30" />
    
    <v:input type="text" label="Start Year and Month ${yearMonthHint}" id="startYearMonth" size="7"/>    
    <v:input type="text" label="End Year and Month ${yearMonthHint}" id="endYearMonth" size="7"/>

    <p class="submit"><v:input type="submit" id="submit" value="${submitLabel}" cancel="${param.subjectUri}"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>

