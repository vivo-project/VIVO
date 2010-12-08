<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal"%>
<%@ page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%-- This form is for the object property between Organizations and Positions. --%>
<%
	VitroRequest vreq = new VitroRequest(request);
	WebappDaoFactory wdf = vreq.getWebappDaoFactory();
	vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior	  
%>

<v:jsonset var="personClassUri">http://xmlns.com/foaf/0.1/Person</v:jsonset>

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

<v:jsonset var="startYearExisting" >      
      SELECT ?startYearExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#startYear> ?startYearExisting }
</v:jsonset>
<v:jsonset var="startYearAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#startYear> ?startYear .
</v:jsonset>

<v:jsonset var="endYearExisting" >      
      SELECT ?endYearExisting WHERE {  
      	?positionUri <http://vivoweb.org/ontology/core#endYear> ?endYearExisting }
</v:jsonset>
<v:jsonset var="endYearAssertion" >
      ?positionUri <http://vivoweb.org/ontology/core#endYear> ?endYear .
</v:jsonset>

<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<v:jsonset var="personUriExisting" >      
	SELECT ?existingPersonUri WHERE {
		?positionUri <http://vivoweb.org/ontology/core#positionForPerson> ?existingPersonUri }
</v:jsonset>
<v:jsonset var="personUriAssertion" >      
	?positionUri <http://vivoweb.org/ontology/core#positionForPerson> ?personUri .
	?personUri   <http://vivoweb.org/ontology/core#personInPosition>  ?positionUri .
</v:jsonset>

<v:jsonset var="n3ForStmtToOrg"  >
    @prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.    
    @prefix core: <http://vivoweb.org/ontology/core#>.    
	?organizationUri core:organizationForPosition  ?positionUri .
	?positionUri     core:positionInOrganization   ?organizationUri .	
    ?positionUri rdf:type               core:Position .    
</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["organizationUri",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["positionUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToOrg}", "${titleAssertion}" , "${personUriAssertion}", "${startYearAssertion}" ],
    "n3optional"    : [ "${endYearAssertion}" ],
    "newResources"  : { "positionUri" : "${defaultNamespace}" },
    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "personUri" ],
    "literalsOnForm" :  [ "title", "startYear", "endYear" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "title"              : "${titleExisting}",
        "startYear"          : "${startYearExisting}",
        "endYear"            : "${endYearExisting}",
    },
    "sparqlForExistingUris" : {
        "personUri"   : "${personUriExisting}"
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
     "personUri" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "${personClassUri}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${personUriAssertion}" ]
      },
      "startYear" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty", "datatype:http://www.w3.org/2001/XMLSchema#gYear"],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYear",
         "rangeLang"        : "",         
         "assertions"       : ["${startYearAssertion}"]
      },
      "endYear" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:http://www.w3.org/2001/XMLSchema#gYear" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "http://www.w3.org/2001/XMLSchema#gYear",
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
	
	/* prepare the <title> and text for the submit button */
	Individual subject = (Individual) request.getAttribute("subject");	
	String submitLabel = ""; 	
	if (objectUri != null) {
		request.setAttribute("title","Edit position history entry for "+ subject.getName());
		submitLabel = "Save changes";
	} else {
		request.setAttribute("title","Create position history entry for " + subject.getName());
		submitLabel = "Create position history entry";
	}
%>

<jsp:include page="${preForm}"/>

<h2>${title}</h2>
<form class="customForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
	<v:input type="text" label="title" id="title" size="30"/>
	<v:input type="select" label="person" id="personUri"  />
    <v:input type="text" label="start year (YYYY)" id="startYear" size="4"/>
    <v:input type="text" label="end year (YYYY)" id="endYear" size="4"/>
    <p class="submit"><v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="true"/></p>
</form>

<jsp:include page="${postForm}"/>

