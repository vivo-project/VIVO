<%@ page import="com.hp.hpl.jena.rdf.model.Literal"%>
<%@ page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page
	import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page
	import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

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
%>


<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<v:jsonset var="titleExisting" >      
	SELECT ?titleExisting WHERE {
	  	?positionUri <http://vivoweb.org/ontology/core#titleOrRole> ?titleExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="titleAssertion" >      
	?positionUri <http://vivoweb.org/ontology/core#titleOrRole> ?title .
	?positionUri <http://www.w3.org/2000/01/rdf-schema#label> ?title. 
</v:jsonset>

<v:jsonset var="organizationNameExisting" >      
      SELECT ?existingOrgName WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#involvedOrganizationName> ?existingOrgName }
</v:jsonset>
<v:jsonset var="organizationNameAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#involvedOrganizationName> ?organizationName .
</v:jsonset>

<v:jsonset var="startYearExisting" >      
      SELECT ?startYearExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#startYear> ?startYearExisting }
</v:jsonset>
<v:jsonset var="startYearAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#startYear> ?startYear .
</v:jsonset>

<v:jsonset var="startYearMonthExisting" >      
      SELECT ?startYearMonthExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#startYearMonth> ?startYearMonthExisting }
</v:jsonset>
<v:jsonset var="startYearMonthAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#startYearMonth> ?startYearMonth .
</v:jsonset>

<v:jsonset var="startDateExisting" >      
      SELECT ?startDateExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#startDate> ?startDateExisting }
</v:jsonset>
<v:jsonset var="startDateAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#startDate> ?startDate .
</v:jsonset>

<v:jsonset var="endYearExisting" >      
      SELECT ?endYearExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#endYear> ?endYearExisting }
</v:jsonset>
<v:jsonset var="endYearAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#endYear> ?endYear .
</v:jsonset>

<v:jsonset var="endYearMonthExisting" >      
      SELECT ?endYearMonthExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#endYearMonth> ?endYearMonthExisting }
</v:jsonset>
<v:jsonset var="endYearMonthAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#endYearMonth> ?endYearMonth .
</v:jsonset>

<v:jsonset var="endDateExisting" >      
      SELECT ?endDateExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#endDate> ?endDateExisting }
</v:jsonset>
<v:jsonset var="endDateAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#endDate> ?endDate .
</v:jsonset>

<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<v:jsonset var="organizationUriExisting" >      
	SELECT ?existingOrgUri WHERE {
		?positionUri <http://vivoweb.org/ontology/core#positionInOrganization> ?existingOrgUri }
</v:jsonset>
<v:jsonset var="organizationUriAssertion" >      
	?positionUri <http://vivoweb.org/ontology/core#positionInOrganization> ?organizationUri .
	?organizationUri <http://vivoweb.org/ontology/core#organizationForPosition> ?positionUri .
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson"  >
    @prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.    
    @prefix core: <http://vivoweb.org/ontology/core#>.    

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
    
    "n3required"    : [ "${n3ForStmtToPerson}", "${titleAssertion}" ],
    "n3optional"    : [ "${organizationNameAssertion}","${organizationUriAssertion}",
                        "${startYearAssertion}","${startYearMonthAssertion}","${startDateAssertion}",
                        "${endYearAssertion}","${endYearMonthAssertion}","${endDateAssertion}"],
    "newResources"  : { "positionUri" : "${defaultNamespace}/position" },
    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "organizationUri" ],
    "literalsOnForm" :  [ "title", "organizationName", 
    					  "startYear", "startYearMonth", "startDate",
                          "endYear",   "endYearMonth",   "endDate" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "title"              : "${titleExisting}",
        "organizationName"   : "${organizationNameExisting}",
        "startYear"          : "${startYearExisting}",
        "startYearMonth"     : "${startYearMonthExisting}",
        "startDate"          : "${startDateExisting}",
        "endYear"            : "${endYearExisting}",
        "endYearMonth"       : "${endYearMonthExisting}",
        "endDate"            : "${endDateExisting}"
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
         "literalOptions"   : [ "--" ],
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
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#string",
         "rangeLang"        : "",         
         "assertions"       : [ "${organizationNameAssertion}" ]
      },
      "startYear" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYear",
         "rangeLang"        : "",         
         "assertions"       : ["${startYearAssertion}"]
      },
      "startYearMonth" : {
         "newResource"      : "false",
         "validators"       : [],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYearMonth";
         "rangeLang"        : "",         
         "assertions"       : [ "${startYearMonthAssertion}" ]
      },
      "startDate" : {
         "newResource"      : "false",
         "validators"       : [],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#Date",
         "rangeLang"        : "",         
         "assertions"       : [ "${startDateAssertion}" ]
      },
      "endYear" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYear",
         "rangeLang"        : "",         
         "assertions"       : ["${endYearAssertion}"]
      },
      "endYearMonth" : {
         "newResource"      : "false",
         "validators"       : [],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYearMonth";
         "rangeLang"        : "",         
         "assertions"       : [ "${endYearMonthAssertion}" ]
      },
      "endDate" : {
         "newResource"      : "false",
         "validators"       : [],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#Date",
         "rangeLang"        : "",         
         "assertions"       : [ "${endDateAssertion}" ]
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
	
	/* prepare the <title> and text for the submit button */
	Individual subject = (Individual) request.getAttribute("subject");	
	String submitLabel = ""; 	
	if (objectUri != null) {
		request.setAttribute("title","Edit position history entry for "+ subject.getName());
		submitLabel = "Save changes";
	} else {
		request.setAttribute("title","Create a new position history entry for " + subject.getName());
		submitLabel = "Create new position history entry";
	}
%>

<jsp:include page="${preForm}"/>

<h2>${title}</h2>
<form action="<c:url value="/edit/processRdfForm2.jsp"/>" >
	<v:input type="text" label="title" id="title" size="30" />
	<v:input type="select" label="organization" id="organizationUri"  />
	<v:input type="text" label="organization name (if not in dropdown above)" id="organizationName" size="30" />

    <v:input type="text" label="start year (YYYY)" id="startYear" size="4"/>
    
    <%--
    <v:input type="text" label="startYearMonth" id="startYearMonth" size="7"/>
    <v:input type="text" label="start date" id="startDate" size="10"/>
    --%>
    
    <v:input type="text" label="end year (YYYY)" id="endYear" size="4"/>
    
    <%--
    <v:input type="text" label="end year-month" id="endYearMonth" size="7"/>
    <v:input type="text" label="end date" id="endDate" size="10"/>
    --%>
    <p class="submit"><v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="${param.subjectUri}"/></p>
</form>

<jsp:include page="${postForm}"/>

