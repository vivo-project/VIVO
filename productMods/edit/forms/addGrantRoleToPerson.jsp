<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding a grant to an person for the predicates hasCo-PrincipalInvestigatorRole
     and hasPrincipalInvestigatorRole.
     
This is intended to create a set of statements like:

?person  core:hasPrincipalInvestigatorRole ?newRole.
?newRole rdf:type core:PrincipalInvestigatorRole ;
         core:relatedRole ?someGrant . 
--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    vreq.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    
    String intDatatypeUri = XSD.xint.toString();    
    vreq.setAttribute("intDatatypeUri", intDatatypeUri);
    vreq.setAttribute("intDatatypeUriJson", MiscWebUtils.escape(intDatatypeUri));
    
    vreq.setAttribute("gYearDatatypeUriJson", MiscWebUtils.escape(XSD.gYear.toString()));
    
    String predicateUri = (String)request.getAttribute("predicateUri");
    ObjectProperty op = wdf.getObjectPropertyDao().getObjectPropertyByURI( predicateUri ); 
    if( op != null &&  op.getURIInverse() != null ){
		%> 
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.Field"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.elements.DateTimeWithPrecision"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.DateTimeIntervalValidation"%><c:set var="inversePredicate"><%=op.getURIInverse()%></c:set> <%
    }else{
    	%> <c:set var="inversePredicate"></c:set> <%
    }
    
    /* 
    There are 4 modes that this form can be in: 
     1.  Add, there is a subject and a predicate but no role and nothing else. 
           
     2. normal edit where everything should already be filled out.  There is a subject, a object and an individual on
        the other end of the object's core:roleIn stmt. 
     
     3. Repair a bad role node.  There is a subject, prediate and object but there is no individual on the 
        other end of the object's core:roleIn stmt.  This should be similar to an add but the form should be expanded.
        
     4. Really bad node. multiple core:roleIn statements.
   */

    EditMode mode = FrontEndEditingUtils.getEditMode(request, "http://vivoweb.org/ontology/core#roleIn");

    if( mode == EditMode.ADD ) {
       %> <c:set var="editMode" value="add"/><%
    } else if(mode == EditMode.EDIT){
        %> <c:set var="editMode" value="edit"/><%
    } else if(mode == EditMode.REPAIR){
        %> <c:set var="editMode" value="repair"/><%
    }
   %>
   
%>

<%@page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty"%><c:set var="vivoOnt" value="http://vivoweb.org/ontology" />
<c:set var="vivoCore" value="${vivoOnt}/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="type" value="<%= VitroVocabulary.RDF_TYPE %>" />
<c:set var="rdf" value="<%= VitroVocabulary.RDF %>" />
<c:set var="label" value="${rdfs}label" />

<c:set var="startYearPred" value="${vivoCore}startYear" />
<c:set var="endYearPred" value="${vivoCore}endYear" />
<c:set var="dateTimeValueType" value="${vivoCore}DateTimeValue"/>
<c:set var="dateTimePrecision" value="${vivoCore}dateTimePrecision"/>
<c:set var="dateTimeValue" value="${vivoCore}dateTime"/>

<c:set var="roleToInterval" value="${vivoCore}dateTimeInterval"/>
<c:set var="intervalType" value="${vivoCore}DateTimeInterval"/>
<c:set var="intervalToStart" value="${vivoCore}start"/>
<c:set var="intervalToEnd" value="${vivoCore}end"/>


<%  // set role type based on predicate
String subjectName = ((Individual) request.getAttribute("subject")).getName();
if ( ((String)request.getAttribute("predicateUri")).endsWith("hasPrincipalInvestigatorRole") ) { %>
 	<v:jsonset var="roleType">http://vivoweb.org/ontology/core#PrincipalInvestigatorRole</v:jsonset>
 	<c:set var="submitButtonLabel">Principal Investigator</c:set>
 	<c:set var="formHeading">principal investigator entry for <%= subjectName %></c:set>
 <% }else if ( ((String)request.getAttribute("predicateUri")).endsWith("hasCo-PrincipalInvestigatorRole") ) { %>
 	<v:jsonset var="roleType">http://vivoweb.org/ontology/core#CoPrincipalInvestigatorRole</v:jsonset>
 	<c:set var="submitButtonLabel">Co-Principal Investigator</c:set>
 	<c:set var="formHeading">co-principal investigator entry for <%= subjectName %></c:set>
 <% }else { %>
 	<v:jsonset var="roleType">http://vivoweb.org/ontology/core#InvestigatorRole</v:jsonset>
 	<c:set var="submitButtonLabel">Investigator</c:set>
 	<c:set var="formHeading">investigator entry for <%= subjectName %></c:set>
 <% } %>

<%-- Configure add vs. edit --%> 
<c:choose>
    <c:when test='${editMode == "add"}'>
        <c:set var="formHeading" value="Create ${formHeading}" />
        <c:set var="labelRequired" value="\"nonempty\"," />
        <c:set var="disabledVal" value="" />
    </c:when>
    <c:otherwise>
        <c:set var="formHeading" value="Edit ${formHeading}" />
        <c:set var="submitButtonLabel" value="Edit ${submitButtonLabel}" />
        <c:choose>
            <c:when test='{editMode == "edit"}'>
                <c:set var="labelRequired" value="" />
                <c:set var="disabledVal" value="disabled" />
            </c:when>
            <c:otherwise> <%-- editMode == "repair" --%>
                <c:set var="labelRequired" value="\"nonempty\"," />
                <c:set var="disabledVal" value="" />
            </c:otherwise>            
        </c:choose>   
    </c:otherwise>
</c:choose>

<v:jsonset var="n3ForGrantRole">
    @prefix core: <${vivoCore}> .
    @prefix rdf: <${rdf}> .
       
	?person ?rolePredicate ?role.
		
	?role  a ?roleType ;		  
           core:roleIn ?grant .
           
    ?grant a core:Grant ;
           core:relatedRole ?role .
</v:jsonset>


<v:jsonset var="n3ForInverse"> 
	?role   ?inverseRolePredicate ?person.
</v:jsonset>

<v:jsonset var="n3ForStart">
    ?role      <${roleToInterval}> ?intervalNode .    
    ?intervalNode  <${type}> <${intervalType}> .
    ?intervalNode <${intervalToStart}> ?startNode .    
    ?startNode  <${type}> <${dateTimeValueType}> .
    ?startNode  <${dateTimeValue}> ?startField-value .
    ?startNode  <${dateTimePrecision}> ?startField-precision .
</v:jsonset>

<v:jsonset var="n3ForEnd">
    ?role      <${roleToInterval}> ?intervalNode .    
    ?intervalNode  <${type}> <${intervalType}> .
    ?intervalNode <${intervalToEnd}> ?endNode .
    ?endNode  <${type}> <${dateTimeValueType}> .
    ?endNode  <${dateTimeValue}> ?endField-value .
    ?endNode  <${dateTimePrecision}> ?endField-precision .
</v:jsonset>

<v:jsonset var="n3ForGrantLabel">
    @prefix rdfs: <${rdfs}> .    	
    ?grant rdfs:label ?grantLabel .               
</v:jsonset>

<v:jsonset var="grantLabelQuery">
  PREFIX core: <${vivoCore}>
  PREFIX rdfs: <${rdfs}> 
  SELECT ?existingGrantLabel WHERE {
        ?role  core:roleIn ?existingGrant .
        ?existingGrant rdfs:label ?existingGrantLabel . }
</v:jsonset>

<v:jsonset var="grantQuery">
  PREFIX core: <${vivoCore}>  
  SELECT ?existingGrant WHERE { ?role  core:roleIn ?existingGrant . }
</v:jsonset>

<v:jsonset var="grantTypeUriJson">${vivoOnt}#Grant</v:jsonset>

<v:jsonset var="existingIntervalNodeQuery" >  
    SELECT ?existingIntervalNode WHERE {
          ?role <${roleToInterval}> ?existingIntervalNode .
          ?existingIntervalNode <${type}> <${intervalType}> . }
</v:jsonset>
 
<v:jsonset var="existingStartNodeQuery" >  
    SELECT ?existingStartNode WHERE {
      ?role <${roleToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToStart}> ?existingStartNode . 
      ?existingStartNode <${type}> <${dateTimeValueType}> .}              
</v:jsonset>

<v:jsonset var="existingStartDateQuery" >  
    SELECT ?existingDateStart WHERE {
     ?role <${roleToInterval}> ?intervalNode .
     ?intervalNode <${type}> <${intervalType}> .
     ?intervalNode <${intervalToStart}> ?startNode .
     ?startNode <${type}> <${dateTimeValueType}> .
     ?startNode <${dateTimeValue}> ?existingDateStart . }
</v:jsonset>

<v:jsonset var="existingStartPrecisionQuery" >  
    SELECT ?existingStartPrecision WHERE {
      ?role <${roleToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToStart}> ?startNode .
      ?startNode <${type}> <${dateTimeValueType}> .          
      ?startNode <${dateTimePrecision}> ?existingStartPrecision . }
</v:jsonset>

<v:jsonset var="existingEndNodeQuery" >  
    SELECT ?existingEndNode WHERE {
      ?role <${roleToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToEnd}> ?existingEndNode . 
      ?existingEndNode <${type}> <${dateTimeValueType}> .}              
</v:jsonset>

<v:jsonset var="existingEndDateQuery" >  
    SELECT ?existingEndDate WHERE {
     ?role <${roleToInterval}> ?intervalNode .
     ?intervalNode <${type}> <${intervalType}> .
     ?intervalNode <${intervalToEnd}> ?endNode .
     ?endNode <${type}> <${dateTimeValueType}> .
     ?endNode <${dateTimeValue}> ?existingEndDate . }
</v:jsonset>

<v:jsonset var="existingEndPrecisionQuery" >  
    SELECT ?existingEndPrecision WHERE {
      ?role <${roleToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToEnd}> ?endNode .
      ?endNode <${type}> <${dateTimeValueType}> .          
      ?endNode <${dateTimePrecision}> ?existingEndPrecision . }
</v:jsonset>


<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/individual", 

    "subject"   : ["person", "${subjectUriJson}" ],
    "predicate" : ["rolePredicate", "${predicateUriJson}" ],
    "object"    : ["role", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForGrantRole}" ],
    
    "n3optional"    : [ "${n3ForGrantLabel}", "${n3ForInverse}", "${n3ForStart}", "${n3ForEnd}" ],        
                                                                                        
    "newResources"  : { "role" : "${defaultNamespace}",
                        "grant" : "${defaultNamespace}",
                        "intervalNode" : "${defaultNamespace}",
                        "startNode" : "${defaultNamespace}",
                        "endNode" : "${defaultNamespace}"  },
    "urisInScope"    : { "roleType" : "${roleType}",
    					 "inverseRolePredicate" : "${inversePredicate}" },
    "literalsInScope": { },
    "urisOnForm"     : [ "grant" ],
    "literalsOnForm" : [ "grantLabel", "existingGrantLabel" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : {  },
    "sparqlForUris" : {   },
    "sparqlForExistingLiterals" : { 
        "grantLabel":"${grantLabelQuery}" , 
        "startField-value"   : "${existingStartDateQuery}",
        "endField-value"     : "${existingEndDateQuery}"  
    },
    "sparqlForExistingUris" : { 
        "grant":"${grantQuery}",
        "intervalNode"      : "${existingIntervalNodeQuery}",
        "startNode"         : "${existingStartNodeQuery}",
        "endNode"           : "${existingEndNodeQuery}",
        "startField-precision": "${existingStartPrecisionQuery}",
        "endField-precision"  : "${existingEndPrecisionQuery}" 
    },
    "fields" : {  
      "grant" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "${grantTypeUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [  "${n3ForGrantRole}" ]
      },               
      "grantLabel" : {
         "newResource"      : "false",
         "validators"       :  [ ${labelRequired} "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${n3ForGrantLabel}", "${n3ForGrantRole}" ]
      },
      "existingGrantLabel" : { /* Needed iff we return from an invalid submission */
         "newResource"      : "false",
         "validators"       :  [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : [ ]
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
         "assertions"       : [ "${n3ForStart}" ]
      },
      "endField" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : ["${n3ForEnd}" ]
      }
  }
}
</c:set>
   
<%

    EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
    
    if (editConfig == null) {
        editConfig = new EditConfiguration((String) request.getAttribute("editjson"));     
        EditConfiguration.putConfigInSession(editConfig,session);
        //setup date time edit elements
        Field startField = editConfig.getField("startField");
        startField.setEditElement(
                new DateTimeWithPrecision(startField, 
                        VitroVocabulary.Precision.YEAR.uri(),
                        VitroVocabulary.Precision.NONE.uri()));        
        Field endField = editConfig.getField("endField");
        endField.setEditElement(
                new DateTimeWithPrecision(endField, 
                        VitroVocabulary.Precision.YEAR.uri(),
                        VitroVocabulary.Precision.NONE.uri()));
        editConfig.addValidator(new DateTimeIntervalValidation("startField","endField") );
    }               
        
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    if (objectUri != null) { 
        editConfig.prepareForObjPropUpdate(model);
        // Return browser to person individual after editing an existing role.
    } else { 
        editConfig.prepareForNonUpdate(model);
        // NIHVIVO-1014 Return browser to person individual after editing an existing role.
        // Return the browser to the new activity entity after adding a new role.
        // editConfig.setEntityToReturnTo("?grant");
    }
    
    String subjectUri = vreq.getParameter("subjectUri");       
  
    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
                                                                JavaScript.CUSTOM_FORM_UTILS.path(),
                                                                "/js/browserUtils.js",
                                                                "/edit/forms/js/customFormWithAutocomplete.js"
                                                               ));            
    request.setAttribute("customJs", customJs);
    
    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
                                                                 Css.CUSTOM_FORM.path(),
                                                                 "/edit/forms/css/customFormWithAutocomplete.css"
                                                                ));                                                                                                                                   
    request.setAttribute("customCss", customCss); 
%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="yearHint" value="<span class='hint'>(YYYY)</span>" />

<jsp:include page="${preForm}" />

<% if( mode == EditMode.ERROR ){ %>
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<% }else{ %>

<h2>${formHeading}</h2>

<%@ include file="unsupportedBrowserMessage.jsp" %>

<%-- DO NOT CHANGE IDS, CLASSES, OR HTML STRUCTURE IN THIS FORM WITHOUT UNDERSTANDING THE IMPACT ON THE JAVASCRIPT! --%>
<form id="addGrantRoleToPerson" class="customForm noIE67" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
        
    <p><v:input type="text" id="relatedIndLabel" name="grantLabel" label="Grant Name ${requiredHint}" cssClass="acSelector" size="50" disabled="${disabledVal}" /></p>

    <%-- Store this value in a hidden field, because the displayed field is disabled and doesn't submit. This ensures that when
    returning from a validation error, we retain the value. --%>
    <c:if test="${editMode == 'edit'}">
       <v:input type="hidden" id="grantLabel" />
    </c:if>
            
    <div class="acSelection">
        <p class="inline"><label>Selected Grant:</label><span class="acSelectionInfo"></span><a href="<c:url value="/individual?uri=" />" class="verifyMatch">(Verify this match)</a></p>
        <v:input type="hidden" id="grant" name="grant" cssClass="acUriReceiver"  /> <%-- Field value populated by JavaScript --%>
        <v:input type="hidden" id="existingGrantLabel" name="existingGrantLabel" cssClass="acLabelReceiver" /> <%-- Needed iff we return from an invalid submission --%> 
    </div>

    <h4>Years of Participation in Grant</h4>
    <v:input id="startField" label="Start Year ${yearHint}" />   
    <v:input id="endField" label="End Year ${yearHint}" />        
                   
    <p class="submit"><v:input type="submit" id="submit" value="${submitButtonLabel}" cancel="true" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>


<c:url var="acUrl" value="/autocomplete?tokenize=true" />
<c:url var="sparqlQueryUrl" value="/ajax/sparqlQuery" />

<%-- Must be all one line for JavaScript. --%>
<c:set var="sparqlForAcFilter">
PREFIX core: <${vivoCore}> SELECT ?grantUri WHERE {<${subjectUri}> <${predicateUri}> ?grantRole . ?grantRole core:roleIn ?grantUri .}
</c:set>

<script type="text/javascript">
var customFormData  = {
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acUrl: '${acUrl}',
    acType: '${vivoCore}Grant',
    editMode: '${editMode}',
    submitButtonTextType: 'compound',
    typeName: 'Grant'         
};
</script>

<% } %>

<jsp:include page="${postForm}"/>