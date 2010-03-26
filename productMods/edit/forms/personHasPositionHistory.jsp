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

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

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

<c:set var="vivo" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdf" value="<%= VitroVocabulary.RDF %>" />
<c:set var="rdfs" value="<% VitroVocabulary.RDFS %>" />

<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<c:set var="titlePred" value="${vivo}titleOrRole" />
<v:jsonset var="titleExisting" >    
    SELECT ?titleExisting WHERE {
          ?positionUri <${titlePred}> ?titleExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="titleAssertion" >      
    ?positionUri <${titlePred}> ?title .
    ?positionUri <${rdf}label> ?title. 
</v:jsonset>

<c:set var="involvedOrgNamePred" value="${vivo}involvedOrganizationName" />
<v:jsonset var="organizationNameExisting" >      
      SELECT ?existingOrgName WHERE {  
          ?positionUri <${involvedOrgNamePred}> ?existingOrgName }
</v:jsonset>
<v:jsonset var="organizationNameAssertion" >
      ?positionUri <${involvedOrgNamePred}> ?organizationName .
</v:jsonset>

<c:set var="startYearPred" value="${vivo}startYear" />
<v:jsonset var="startYearExisting" >      
      SELECT ?startYearExisting WHERE {  
          ?positionUri <${startYearPred}> ?startYearExisting }
</v:jsonset>
<v:jsonset var="startYearAssertion" >
      ?positionUri <${startYearPred}> ?startYear .
</v:jsonset>

<c:set var="endYearPred" value="${vivo}endYear" />
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
<c:set var="positionInOrgPred" value="${vivo}positionInOrganization" />
<v:jsonset var="organizationUriExisting" >      
    SELECT ?existingOrgUri WHERE {
        ?positionUri <${positionInOrgPred}> ?existingOrgUri }
</v:jsonset>
<v:jsonset var="organizationUriAssertion" >      
    ?positionUri <${positionInOrgPred}> ?organizationUri .
    ?organizationUri <${vivo}organizationForPosition> ?positionUri .
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson"  >
    @prefix rdf:  <${rdf}>.    
    @prefix core: <${vivo}>.    

    ?person      core:personInPosition  ?positionUri .
    ?positionUri core:positionForPerson ?person .
    ?positionUri rdf:type               core:Position .
    ?positionUri rdf:type <${flagURI}> .
</v:jsonset>

<v:jsonset var="postionClass">http://vivoweb.org/ontology/core#Position</v:jsonset>
<v:jsonset var="organizationClass">http://xmlns.com/foaf/0.1/Organization</v:jsonset>


<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["positionUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToPerson}", "${titleAssertion}", "${startYearAssertion}" ],
    "n3optional"    : [ "${organizationNameAssertion}","${organizationUriAssertion}",
                        "${endYearAssertion}"],
    "newResources"  : { "positionUri" : "${defaultNamespace}" },
    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "organizationUri" ],
    "literalsOnForm" :  [ "title", "organizationName", 
                          "startYear", "endYear" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "title"              : "${titleExisting}",
        "organizationName"   : "${organizationNameExisting}",
        "startYear"          : "${startYearExisting}",
        "endYear"            : "${endYearExisting}"
    },
    "sparqlForExistingUris" : {
        "organizationUri"   : "${organizationUriExisting}"
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
     "organizationUri" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${organizationClass}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${organizationUriAssertion}" ]
      },      
      "organizationName" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : [ "${organizationNameAssertion}" ]
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

    EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
    if (editConfig == null) {
        editConfig = new EditConfiguration(
                (String) request
                .getAttribute("editjson"));
        EditConfiguration.putConfigInSession(editConfig,session);
    }
    
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    if (objectUri != null) {
        editConfig.prepareForObjPropUpdate(model);
    } else {
        editConfig.prepareForNonUpdate(model);
    }
    
    /* prepare the page title and text for the submit button */
    String subjectName = ((Individual) request.getAttribute("subject")).getName();
    String submitLabel = "";     
    if (objectUri != null) {
        request.setAttribute("title","Edit position entry for "+ subjectName);
        submitLabel = "Save changes";
    } else {
        request.setAttribute("title","Create a new position entry for " + subjectName);
        submitLabel = "Create new position history entry";
    }
    
    List<String> customJs = new ArrayList<String>(Arrays.asList("../js/customForms/personHasPositionHistory.js"));
    request.setAttribute("customJs", customJs);
%>

<jsp:include page="${preForm}" />

<h2>${title}</h2>

<form action="<c:url value="/edit/processRdfForm2.jsp"/>" >
    <div id="orgNotListed">
    If your organization is not listed, please <a href="#">add a new organization</a>
    </div>
    
    <div id="existingOrg">
        <v:input type="select" label="Organization" id="organizationUri"  />  
    </div>
    
    <div id="newOrg">
    
    </div>
    
    <div id="position"> 
        <v:input type="text" label="Position Title" id="title" size="30" />
        <v:input type="select" label="Position Type" id="type" />

        <v:input type="text" label="Start Year" id="startYear" size="4"/>    
        <v:input type="text" label="End Year" id="endYear" size="4"/>
    </div>
    
    <p class="submit"><v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="${param.subjectUri}"/></p>
</form>

<jsp:include page="${postForm}"/>

