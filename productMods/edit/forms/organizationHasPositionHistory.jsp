<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal"%>
<%@ page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.Field"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.elements.DateTimeWithPrecision"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.DateTimeIntervalValidation"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%-- This form is for the object property between Organizations and Positions. --%>
<%
	VitroRequest vreq = new VitroRequest(request);
	WebappDaoFactory wdf = vreq.getWebappDaoFactory();
	vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior	  
%>

<%-- Define predicates used in n3 assertions and sparql queries --%>
<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="type" value="<%= VitroVocabulary.RDF_TYPE %>" />
<c:set var="label" value="<%= VitroVocabulary.LABEL %>" />

<c:set var="positionInOrgPred" value="${vivoCore}positionInOrganization" />
<c:set var="orgForPositionPred" value="${vivoCore}organizationForPosition" />
<c:set var="positionType" value="${vivoCore}Position" />
<c:set var="positionForPerson" value="${vivoCore}positionForPerson" />
<c:set var="personInPosition" value="${vivoCore}personInPosition" />

<c:set var="dateTimeValue" value="${vivoCore}dateTime"/>
<c:set var="dateTimeValueType" value="${vivoCore}DateTimeValue"/>
<c:set var="dateTimePrecision" value="${vivoCore}dateTimePrecision"/>
<c:set var="edToDateTime" value="${vivoCore}dateTimeInterval"/>

<c:set var="positionToInterval" value="${vivoCore}dateTimeInterval"/>
<c:set var="intervalType" value="${vivoCore}DateTimeInterval"/>
<c:set var="intervalToStart" value="${vivoCore}start"/>
<c:set var="intervalToEnd" value="${vivoCore}end"/>


<v:jsonset var="personClassUri">http://xmlns.com/foaf/0.1/Person</v:jsonset>

<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<v:jsonset var="titleExisting" >      
	SELECT ?titleExisting WHERE {
	  	?positionUri <${label}> ?titleExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="titleAssertion" >      
	?positionUri <${label}> ?title. 
</v:jsonset>


<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<v:jsonset var="personUriExisting" >      
	SELECT ?existingPersonUri WHERE {
		?positionUri <${positionForPerson}> ?existingPersonUri }
</v:jsonset>

<v:jsonset var="personUriAssertion" >      
	?positionUri <${positionForPerson}> ?personUri .
	?personUri   <${personInPosition}>  ?positionUri .
</v:jsonset>

<v:jsonset var="n3ForStmtToOrg"  >
	?organizationUri <${orgForPositionPred}> ?positionUri .
	?positionUri     <${positionInOrgPred}> ?organizationUri .	
    ?positionUri     a  ?positionType .    
</v:jsonset>

<v:jsonset var="n3ForStart">
    ?positionUri <${positionToInterval}> ?intervalNode .    
    ?intervalNode <${type}> <${intervalType}> .
    ?intervalNode <${intervalToStart}> ?startNode .    
    ?startNode  <${type}> <${dateTimeValueType}> .
    ?startNode  <${dateTimeValue}> ?startField-value .
    ?startNode  <${dateTimePrecision}> ?startField-precision .
</v:jsonset>

<v:jsonset var="n3ForEnd">
    ?positionUri <${positionToInterval}> ?intervalNode .    
    ?intervalNode <${type}> <${intervalType}> .
    ?intervalNode <${intervalToEnd}> ?endNode .
    ?endNode  <${type}> <${dateTimeValueType}> .
    ?endNode  <${dateTimeValue}> ?endField-value .
    ?endNode  <${dateTimePrecision}> ?endField-precision .
</v:jsonset>

 <v:jsonset var="existingIntervalNodeQuery" >  
    SELECT ?existingIntervalNode WHERE {
          ?positionUri <${positionToInterval}> ?existingIntervalNode .
          ?existingIntervalNode <${type}> <${intervalType}> . }
</v:jsonset>
 
 <v:jsonset var="existingStartNodeQuery" >  
    SELECT ?existingStartNode WHERE {
      ?positionUri <${positionToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToStart}> ?existingStartNode . 
      ?existingStartNode <${type}> <${dateTimeValueType}> .}              
</v:jsonset>

<v:jsonset var="existingStartDateQuery" >  
    SELECT ?existingDateStart WHERE {
     ?positionUri <${positionToInterval}> ?intervalNode .
     ?intervalNode <${type}> <${intervalType}> .
     ?intervalNode <${intervalToStart}> ?startNode .
     ?startNode <${type}> <${dateTimeValueType}> .
     ?startNode <${dateTimeValue}> ?existingDateStart . }
</v:jsonset>

<v:jsonset var="existingStartPrecisionQuery" >  
    SELECT ?existingStartPrecision WHERE {
      ?positionUri <${positionToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToStart}> ?startNode .
      ?startNode <${type}> <${dateTimeValueType}> .          
      ?startNode <${dateTimePrecision}> ?existingStartPrecision . }
</v:jsonset>

 <v:jsonset var="existingEndNodeQuery" >  
    SELECT ?existingEndNode WHERE {
      ?positionUri <${positionToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToEnd}> ?existingEndNode . 
      ?existingEndNode <${type}> <${dateTimeValueType}> .}              
</v:jsonset>

<v:jsonset var="existingEndDateQuery" >  
    SELECT ?existingEndDate WHERE {
     ?positionUri <${positionToInterval}> ?intervalNode .
     ?intervalNode <${type}> <${intervalType}> .
     ?intervalNode <${intervalToEnd}> ?endNode .
     ?endNode <${type}> <${dateTimeValueType}> .
     ?endNode <${dateTimeValue}> ?existingEndDate . }
</v:jsonset>

<v:jsonset var="existingEndPrecisionQuery" >  
    SELECT ?existingEndPrecision WHERE {
      ?positionUri <${positionToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToEnd}> ?endNode .
      ?endNode <${type}> <${dateTimeValueType}> .          
      ?endNode <${dateTimePrecision}> ?existingEndPrecision . }
</v:jsonset>

<v:jsonset var="positionTypeAssertion">
    ?positionUri a ?positionType .
</v:jsonset>

<v:jsonset var="positionTypeQuery">
    SELECT ?existingPositionType WHERE {
        ?positionUri a ?existingPositionType . }
</v:jsonset>

<c:set var="positionClass" value="${vivoCore}Position" />
<v:jsonset var="positionClassUriJson">${positionClass}</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["organizationUri",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["positionUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToOrg}", "${titleAssertion}" , "${personUriAssertion}", "${positionTypeAssertion}"],
    "n3optional"    : [ "${n3ForStart}" , "${n3ForEnd}" ],
    "newResources"  : { "positionUri" : "${defaultNamespace}",
                        "intervalNode" : "${defaultNamespace}",
                        "startNode" : "${defaultNamespace}",
                        "endNode" : "${defaultNamespace}" },
    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "personUri", "positionType" ],
    "literalsOnForm" :  [ "title" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "title"              : "${titleExisting}",
        "startField-value"   : "${existingStartDateQuery}",
        "endField-value"     : "${existingEndDateQuery}"
    },
    "sparqlForExistingUris" : {
        "personUri"   : "${personUriExisting}",
        "positionType"      : "${positionTypeQuery}" ,
        "intervalNode"      : "${existingIntervalNodeQuery}", 
        "startNode"         : "${existingStartNodeQuery}",
        "endNode"           : "${existingEndNodeQuery}",
        "startField-precision": "${existingStartPrecisionQuery}",
        "endField-precision"  : "${existingEndPrecisionQuery}"
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
     "positionType" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "CHILD_VCLASSES_WITH_PARENT",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${positionClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${positionTypeAssertion}" ]
      },             
      "startField" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : ["${n3ForStart}"]
      },
      "endField" : {
         "newResource"      : "false",
         "validators"       : [],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : ["${n3ForEnd}"]
      }
  }
}
</c:set>
<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="yearHint" value="<span class='hint'>(YYYY)</span>" />
<%

	EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
	if (editConfig == null) {
		editConfig = new EditConfiguration(
				(String) request
				.getAttribute("editjson"));		
		EditConfiguration.putConfigInSession(editConfig,session);
		
		//setup date time edit elements
        Field startField = editConfig.getField("startField");
        startField.setEditElement(new DateTimeWithPrecision(startField, VitroVocabulary.Precision.YEAR.uri(),VitroVocabulary.Precision.NONE.uri()));        
        Field endField = editConfig.getField("endField");
        endField.setEditElement(new DateTimeWithPrecision(endField, VitroVocabulary.Precision.YEAR.uri(),VitroVocabulary.Precision.NONE.uri()));
        
        editConfig.addValidator(new DateTimeIntervalValidation("startField","endField") );
	}		
	
	Model model = (Model) application.getAttribute("jenaOntModel");
	String objectUri = (String) request.getAttribute("objectUri");
	if (objectUri != null) {
		editConfig.prepareForObjPropUpdate(model);
	} else {
		editConfig.prepareForNonUpdate(model);
	}
	
	List<String> customCss = new ArrayList<String>(Arrays.asList(Css.CUSTOM_FORM.path()
                                                                ));
    request.setAttribute("customCss", customCss);
	
	/* prepare the <title> and text for the submit button */
	Individual subject = (Individual) request.getAttribute("subject");	
	String submitLabel = ""; 	
	if (objectUri != null) {
		request.setAttribute("title","Edit position history entry for "+ subject.getName());
		submitLabel = "Save Changes";
	} else {
		request.setAttribute("title","Create position history entry for " + subject.getName());
		submitLabel = "Create Position History";
	}
%>

<jsp:include page="${preForm}"/>

<h2>${title}</h2>
<form class="customForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
	<v:input type="text" label="Position Title ${requiredHint}" id="title" size="30"/>
	<v:input type="select" label="Position Type ${requiredHint}" id="positionType" />
	<v:input type="select" label="Person" id="personUri"  />
	<v:input id="startField"  label="Start Year ${yearHint}" />
    <v:input id="endField" label="End Year ${yearHint}" />    
    <p class="submit"><v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="true"/></p>
</form>

<jsp:include page="${postForm}"/>

