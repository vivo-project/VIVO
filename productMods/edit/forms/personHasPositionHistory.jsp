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
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.personHasPositionHistory.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    vreq.setAttribute("defaultNamespace", wdf.getDefaultNamespace());
    
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
<c:set var="type" value="${rdf}type" />
<c:set var="positionClass" value="${vivoCore}Position" />
<c:set var="orgClass" value="http://xmlns.com/foaf/0.1/Organization" />

<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<c:set var="titlePred" value="${vivoCore}titleOrRole" />
<v:jsonset var="titleExisting" >  
    SELECT ?titleExisting WHERE {
          ?positionUri <${titlePred}> ?titleExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="titleAssertion" >      
    ?positionUri <${titlePred}> ?title .
    ?positionUri <${label}> ?title. 
</v:jsonset>

<c:set var="startYearPred" value="${vivoCore}startYear" />
<v:jsonset var="startYearExisting" >      
      SELECT ?startYearExisting WHERE {  
          ?positionUri <${startYearPred}> ?startYearExisting }
</v:jsonset>
<v:jsonset var="startYearAssertion" >
      ?positionUri <${startYearPred}> ?startYear .
</v:jsonset>

<c:set var="endYearPred" value="${vivoCore}endYear" />
<v:jsonset var="endYearExisting" >      
      SELECT ?endYearExisting WHERE {  
          ?positionUri <${endYearPred}> ?endYearExisting }
</v:jsonset>
<v:jsonset var="endYearAssertion" >
      ?positionUri <${endYearPred}> ?endYear .
</v:jsonset>

<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<c:set var="positionInOrgPred" value="${vivoCore}positionInOrganization" />
<c:set var="orgForPositionPred" value="${vivoCore}organizationForPosition" />
<v:jsonset var="organizationUriExisting" >      
    SELECT ?existingOrgUri WHERE {
        ?positionUri <${positionInOrgPred}> ?existingOrgUri }
</v:jsonset>
<v:jsonset var="organizationUriAssertion" >      
    ?positionUri <${positionInOrgPred}> ?organizationUri .
    ?organizationUri <${orgForPositionPred}> ?positionUri .
</v:jsonset>

<v:jsonset var="positionTypeExisting">
    SELECT ?existingPositionType WHERE {
        ?positionUri <${type}> ?existingPositionType }
</v:jsonset>
<v:jsonset var="positionTypeAssertion">
    ?positionUri <${type}> ?positionType .
</v:jsonset>

<v:jsonset var="newOrgNameAssertion">
    ?newOrg <${label}> ?newOrgName .
</v:jsonset>

<v:jsonset var="newOrgTypeAssertion">
    ?newOrg <${type}> ?newOrgType .
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson">       
    @prefix core: <${vivoCore}> .     

    ?person      core:personInPosition  ?positionUri .
    ?positionUri core:positionForPerson ?person .
    ?positionUri <${type}>  ?positionType .
    ?positionUri <${type}> <${flagURI}> .
</v:jsonset>

<v:jsonset var="n3ForNewOrg">
    ?newOrg <${label}> ?newOrgName .
    ?newOrg <${type}> ?newOrgType .
    ?positionUri <${positionInOrgPred}> ?newOrg .
    ?newOrg <${orgForPositionPred}> ?positionUri .
</v:jsonset>

<v:jsonset var="positionClassUriJson">${positionClass}</v:jsonset>
<v:jsonset var="orgClassUriJson">${orgClass}</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["positionUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToPerson}", "${titleAssertion}", "${startYearAssertion}" ],
    
    "n3optional"    : [ "${organizationUriAssertion}",                         
                        "${n3ForNewOrg}", "${newOrgNameAssertion}", "${newOrgTypeAssertion}",                       
                        "${endYearAssertion}"],
                        
    "newResources"  : { "positionUri" : "${defaultNamespace}",
                        "newOrg" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "organizationUri", "newOrgType", "positionType" ],
    "literalsOnForm" :  [ "title", "newOrgName", 
                          "startYear", "endYear" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "title"              : "${titleExisting}",
        "startYear"          : "${startYearExisting}",
        "endYear"            : "${endYearExisting}"
    },
    "sparqlForExistingUris" : {
        "organizationUri"   : "${organizationUriExisting}",
        "positionType"      : "${positionTypeExisting}"
    },
    "fields" : {
      "title" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${titleAssertion}" ]
      },
     "positionType" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "CHILD_VCLASSES_WITH_PARENT",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${positionClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${positionTypeAssertion}" ]
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
         "newResource"      : "true",
         "validators"       : [  ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : [ "${newOrgNameAssertion}" ]
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
      "startYear" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty", "datatype:${gYearDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${gYearDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${startYearAssertion}"]
      },
      "endYear" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${gYearDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${gYearDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${endYearAssertion}"]
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
        <c:set var="title" value="Edit position entry for ${subjectName}" />
        <%-- NB This will be the button text when Javascript is disabled. --%>
        <c:set var="submitLabel" value="Save changes" />
<% 
    } else { // adding new entry
%>
        <c:set var="editType" value="add" />
        <c:set var="title" value="Create a new position entry for ${subjectName}" />
        <%-- NB This will be the button text when Javascript is disabled. --%>
        <c:set var="submitLabel" value="Create position" />
<%  } 
    
    List<String> customJs = new ArrayList<String>(Arrays.asList("forms/js/customForm.js"
                                                                //, "forms/js/personHasPositionHistory.js"
                                                                ));
    request.setAttribute("customJs", customJs);
    
    List<String> customCss = new ArrayList<String>(Arrays.asList("forms/css/customForm.css", 
                                                                 "forms/css/personHasPositionHistory.css"
                                                                 ));
    request.setAttribute("customCss", customCss);   
%>

<c:set var="yearHint" value="<span class='hint'>&nbsp;(YYYY)</span>" />
<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />

<jsp:include page="${preForm}" />

<h2>${title}</h2>

<form action="<c:url value="/edit/processRdfForm2.jsp"/>" >
    <c:if test="${editType == 'add'}">
        
        <!--  Hide on initial load in case JavaScript turned off. -->
        <div id="addNewLink">
            If your organization is not listed, please <a href="#">add a new organization</a>.
        </div>
    </c:if>
    
    <div id="existing">
        <v:input type="select" label="Organization" labelClass="required" id="organizationUri"  />  
    </div>
    
    <div id="new">
        <h6>Add a New Organization</h6>
        <v:input type="text" label="Organization Name" labelClass="required" id="newOrgName" />
        <v:input type="select" label="Select Organization Type" labelClass="required" id="newOrgType" />
    </div>
    
    <div id="entry"> 
        <v:input type="text" label="Position Title ${requiredHint}" id="title" size="30" />
        <v:input type="select" label="Position Type ${requiredHint}" id="positionType" />

        <p class="inline year"><v:input type="text" label="Start Year ${requiredHint} ${yearHint}" id="startYear" size="4" /></p>    
        <p class="inline year"><v:input type="text" label="End Year ${yearHint}" id="endYear" size="4" /></p>
    </div>
    
    <!-- For Javascript -->
    <input type="hidden" name="editType" value="add" />
    <input type="hidden" name="entryType" value="position" /> 
    <input type="hidden" name="newType" value="organization" />
    
    <p class="submit"><v:input type="submit" id="submit" value="${submitLabel}" cancel="${param.subjectUri}"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>

