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
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.StartYearBeforeEndYear"%>

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
		%> <c:set var="inversePredicate"><%=op.getURIInverse()%></c:set> <%
    }else{
    	%> <c:set var="inversePredicate"></c:set> <%
    }
%>

<%@page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty"%><c:set var="vivoOnt" value="http://vivoweb.org/ontology" />
<c:set var="vivoCore" value="${vivoOnt}/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="rdf" value="<%= VitroVocabulary.RDF %>" />
<c:set var="label" value="${rdfs}label" />

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
 
<c:choose>
    <c:when test="<%= request.getAttribute(\"objectUri\")!=null %>">
        <c:set var="formHeading" value="Edit ${formHeading}" />
        <c:set var="editMode" value="edit" />
        <c:set var="submitButtonLabel" value="Edit ${submitButtonLabel}" />
        <c:set var="labelRequired" value="" />
        <c:set var="disabledVal" value="disabled" />
    </c:when>
    <c:otherwise>
        <c:set var="formHeading" value="Create ${formHeading}" />
        <c:set var="editMode" value="add" />
        <c:set var="labelRequired" value="\"nonempty\"," />
        <c:set var="disabledVal" value="" />
    </c:otherwise>
</c:choose>

<c:set var="startYearUri" value="${vivoCore}startYear" />
<v:jsonset var="startYearAssertion" >
      ?role <${startYearUri}> ?startYear .
</v:jsonset>

<c:set var="endYearUri" value="${vivoCore}endYear" /> 
<v:jsonset var="endYearAssertion" >
      ?role <${endYearUri}> ?endYear .
</v:jsonset>

<v:jsonset var="n3ForGrantRole">
    @prefix core: <${vivoCore}> .
    @prefix rdf: <${rdf}> .
       
	?person ?rolePredicate ?role.	
	?role   rdf:type ?roleType .		  
    ?role   core:roleIn ?grant .
    ?grant  core:relatedRole ?role .
</v:jsonset>

<v:jsonset var="n3ForInverse"> 
	?role   ?inverseRolePredicate ?person.
</v:jsonset>

<v:jsonset var="n3ForGrantType">
    @prefix core: <${vivoCore}> .
    @prefix rdf: <${rdf}> .    	
    ?grant rdf:type core:Grant .              
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

<v:jsonset var="startYearQuery">
  PREFIX core: <${vivoCore}>  
  SELECT ?existingStartYear WHERE { ?role  core:startYear ?existingStartYear .}       
</v:jsonset>

<v:jsonset var="endYearQuery">
PREFIX core: <${vivoCore}>  
  SELECT ?existingStartYear WHERE { ?role  core:endYear ?existingStartYear .}
</v:jsonset>

<v:jsonset var="grantQuery">
  PREFIX core: <${vivoCore}>  
  SELECT ?existingGrant WHERE { ?role  core:roleIn ?existingGrant . }
</v:jsonset>

<v:jsonset var="grantTypeUriJson">${vivoOnt}#Grant</v:jsonset>
<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/individual", 

    "subject"   : ["person", "${subjectUriJson}" ],
    "predicate" : ["rolePredicate", "${predicateUriJson}" ],
    "object"    : ["role", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForGrantRole}", "${startYearAssertion}" ],
    
    "n3optional"    : [ "${n3ForGrantType}", "${n3ForGrantLabel}", "${n3ForInverse}", "${endYearAssertion}" ],        
                                                                                        
    "newResources"  : { "role" : "${defaultNamespace}",
                        "grant" : "${defaultNamespace}" },

    "urisInScope"    : { "roleType" : "${roleType}",
    					 "inverseRolePredicate" : "${inversePredicate}" },
    "literalsInScope": { },
    "urisOnForm"     : [ "grant" ],
    "literalsOnForm" : [ "grantLabel", "startYear", "endYear", "existingGrantLabel" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : {  },
    "sparqlForUris" : {   },
    "sparqlForExistingLiterals" : { "grantLabel":"${grantLabelQuery}" , "startYear":"${startYearQuery}", "endYear":"${endYearQuery}" },
    "sparqlForExistingUris" : { "grant":"${grantQuery}" },
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
         "assertions"       : [  ]
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
         "assertions"       : ["${n3ForGrantLabel}"]
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
        editConfig = new EditConfiguration((String) request.getAttribute("editjson"));     
        EditConfiguration.putConfigInSession(editConfig,session);
    }   
        
    editConfig.addValidator(new StartYearBeforeEndYear("startYear","endYear") );
        
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

<h2>${formHeading}</h2>

<%@ include file="unsupportedBrowserMessage.jsp" %>

<%-- DO NOT CHANGE IDS, CLASSES, OR HTML STRUCTURE IN THIS FORM WITHOUT UNDERSTANDING THE IMPACT ON THE JAVASCRIPT! --%>
<form id="addGrantRoleToPerson" class="customForm noIE67" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
        
    <p><v:input type="text" id="relatedIndLabel" name="grantLabel" label="Grant Name ${requiredHint}" cssClass="acSelector" size="50" disabled="${disabledVal}" /></p>

    <%-- Store this value in a hidden field, because the displayed field is disabled and don't submit. This ensures that when
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
    <v:input type="text" label="Start Year ${requiredHint} ${yearHint}" id="startYear" size="7"/>   
    <v:input type="text" label="End Year ${yearHint}" id="endYear" size="7"/> 
                   
    <p class="submit"><v:input type="submit" id="submit" value="${submitButtonLabel}" cancel="true" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>


<c:url var="acUrl" value="/autocomplete?tokenize=true&stem=true" />
<c:url var="sparqlQueryUrl" value="/admin/sparqlquery" />

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

<jsp:include page="${postForm}"/>