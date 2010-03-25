<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

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

<%-- RY *** SET VARIABLES for uris & namespaces --%>

<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<v:jsonset var="titleExisting" >      
	SELECT ?titleExisting WHERE {
	  	?activityUri <http://vivoweb.org/ontology/core#titleOrRole> ?titleExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="titleAssertion" >      
	?activityUri <http://vivoweb.org/ontology/core#titleOrRole> ?title .
	?activityUri <http://www.w3.org/2000/01/rdf-schema#label> ?title. 
</v:jsonset>

<v:jsonset var="descriptionExisting" >      
    SELECT ?descriptionExisting WHERE {
        ?activityUri <http://vivoweb.org/ontology/core#description> ?descriptionExisting }
</v:jsonset>
<v:jsonset var="descriptionAssertion" >      
    ?activityUri <http://vivoweb.org/ontology/core#description> ?description . 
</v:jsonset>

<v:jsonset var="organizationNameExisting" >      
      SELECT ?existingOrgName WHERE {  
      	?activityUri <http://vivoweb.org/ontology/core#involvedOrganizationName> ?existingOrgName }
</v:jsonset>
<v:jsonset var="organizationNameAssertion" >
      ?activityUri <http://vivoweb.org/ontology/core#involvedOrganizationName> ?organizationName .
</v:jsonset>

<v:jsonset var="startYearMonthExisting" >      
      SELECT ?startYearMonthExisting WHERE {  
      	?activityUri <http://vivoweb.org/ontology/core#startYearMonth> ?startYearMonthExisting }
</v:jsonset>
<v:jsonset var="startYearMonthAssertion" >
      ?activityUri <http://vivoweb.org/ontology/core#startYearMonth> ?startYearMonth .
</v:jsonset>

<v:jsonset var="endYearMonthExisting" >      
      SELECT ?endYearMonthExisting WHERE {  
      	?activityUri <http://vivoweb.org/ontology/core#endYearMonth> ?endYearMonthExisting }
</v:jsonset>
<v:jsonset var="endYearMonthAssertion" >
      ?activityUri <http://vivoweb.org/ontology/core#endYearMonth> ?endYearMonth .
</v:jsonset>

<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<v:jsonset var="organizationUriExisting" >      
	SELECT ?existingOrgUri WHERE {
		?activityUri <http://vivoweb.org/ontology/core#activityRelatedOrganization> ?existingOrgUri }
</v:jsonset>
<v:jsonset var="organizationUriAssertion" >      
    ?activityUri <http://vivoweb.org/ontology/core#activityRelatedOrganization> ?organizationUri .
    ?organizationUri <http://vivoweb.org/ontology/core#organizationRelatedActivity> ?activityUri .
</v:jsonset>

<v:jsonset var="serviceSubClassAssertion">
	?activityUri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?serviceSubClass . 
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson"  >
    @prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.    
    @prefix core: <http://vivoweb.org/ontology/core#>.    

    ?person      core:professionalServiceActivity ?activityUri .
    ?activityUri core:professionalServiceActivityBy ?person .
    ?activityUri rdf:type               core:ServiceActivity .
    ?activityUri rdf:type <${flagURI}> .
</v:jsonset>

<v:jsonset var="activityClass">http://vivoweb.org/ontology/core#ServiceActivity</v:jsonset>
<v:jsonset var="organizationClass">http://xmlns.com/foaf/0.1/Organization</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["activityUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToPerson}", "${titleAssertion}", "${startYearMonthAssertion}" ],
    "n3optional"    : [ "${descriptionAssertion}", "${organizationNameAssertion}","${organizationUriAssertion}",
                        "${endYearMonthAssertion}", "${serviceSubClassAssertion}"],
    "newResources"  : { "activityUri" : "${defaultNamespace}" },
    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "organizationUri" , "serviceSubClass" ],
    "literalsOnForm" :  [ "title", "description", "organizationName", 
    					  "startYearMonth", "endYearMonth" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "title"              : "${titleExisting}",
        "description"        : "${descriptionExisting}",
        "organizationName"   : "${organizationNameExisting}",
        "startYearMonth"     : "${startYearMonthExisting}",
        "endYearMonth"       : "${endYearMonthExisting}"
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
      "description" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "", 
         "rangeLang"        : "",
         "assertions"       : [ "${descriptionAssertion}" ]
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
     "serviceSubClass" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "CHILD_VCLASSES",
         "literalOptions"   : [ "--" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${activityClass}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${serviceSubClassAssertion}" ]
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
      "startYearMonth" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty", "datatype:http://www.w3.org/2001/XMLSchema#gYearMonth" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYearMonth",
         "rangeLang"        : "",         
         "assertions"       : ["${startYearMonthAssertion}"]
      },
      "endYearMonth" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:http://www.w3.org/2001/XMLSchema#gYearMonth" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYearMonth",
         "rangeLang"        : "",         
         "assertions"       : ["${endYearMonthAssertion}"]
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
		request.setAttribute("title","Edit professional service activity entry for "+ subject.getName());
		submitLabel = "Save changes";
	} else {
		request.setAttribute("title","Create a new professional service activity entry for " + subject.getName());
		submitLabel = "Create new professional service activity entry";
	}
%>

<jsp:include page="${preForm}"/>

<h2>${title}</h2>
<form action="<c:url value="/edit/processRdfForm2.jsp"/>" >
	<v:input type="text" label="title" id="title" size="30" />
	<v:input type="select" label="activity type" id="serviceSubClass"/>
	<v:input type="textarea" label="description" id="description" rows="5" cols="30" />
	<v:input type="select" label="organization" id="organizationUri"  />
	<v:input type="text" label="organization name (if not in dropdown above)" id="organizationName" size="30" />
    <v:input type="text" label="start year and month (YYYY-MM)" id="startYearMonth" size="7"/>    
    <v:input type="text" label="end year and month (YYYY-MM)" id="endYearMonth" size="7"/>
    <p class="submit"><v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="${param.subjectUri}"/></p>
</form>

<jsp:include page="${postForm}"/>

