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
 	<c:set var="submitButtonLabel">Create principal investigator</c:set>
 	<c:set var="formHeading">Create a new principal investigator entry for <%= subjectName %></c:set>
 <% }else{ %>
 	<v:jsonset var="roleType">http://vivoweb.org/ontology/core#CoPrincipalInvestigatorRole</v:jsonset>
 	<c:set var="submitButtonLabel">Create co-principal investigator</c:set>
 	<c:set var="formHeading">Create a new co-principal investigator entry for <%= subjectName %></c:set>
 <% } %>
 
<v:jsonset var="n3ForGrantRole">
    @prefix core: <${vivoCore}> .
    @prefix rdf: <${rdf}> .
       
	?person ?rolePredicate ?role.	
	?role   rdf:type ?roleType .		  
    ?role   core:relatedRole ?grant .
    ?grant  core:inRole ?role .
</v:jsonset>

<v:jsonset var="n3ForInverse"> 
	?role   ?inverseRolePredicate ?person.
</v:jsonset>

<v:jsonset var="n3ForNewGrant">
    @prefix core: <${vivoCore}> .
    @prefix rdf: <${rdf}> .
    @prefix rdfs: <${rdfs}> .
    	
    ?grant rdf:type core:Grant .
    ?grant rdfs:label ?grantLabel .               
</v:jsonset>

<%-- Must be all one line for JavaScript. Must use ?individual since Javascript will look for that property in the data returned. --%>
<c:set var="sparqlForAcFilter">
PREFIX core: <${vivoCore}> SELECT ?individual WHERE {<${subjectUri}> core:hasPrincipalInvestigatorRole ?grantRole .?grantRole core:relatedRole ?individual .}
</c:set>

<v:jsonset var="grantTypeUriJson">${vivoOnt}#Grant</v:jsonset>
<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/individual", 

    "subject"   : ["person", "${subjectUriJson}" ],
    "predicate" : ["rolePredicate", "${predicateUriJson}" ],
    "object"    : ["role", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForGrantRole}" ],
    
    "n3optional"    : [ "${n3ForNewGrant}" , "${n3ForInverse}" ],        
                                                                                        
    "newResources"  : { "role" : "${defaultNamespace}",
                        "grant" : "${defaultNamespace}" },

    "urisInScope"    : { "roleType" : "${roleType}",
    					 "inverseRolePredicate" : "${inversePredicate}" },
    "literalsInScope": { },
    "urisOnForm"     : [ "grant" ],
    "literalsOnForm" : [ "grantLabel" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : { },
    "sparqlForExistingUris" : { },
    "fields" : {  
      "grant" : {
         "newResource"      : "true",
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
         "validators"       :  [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "${personClassUriJson}",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${n3ForExistingPub}"]
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
    
    //validator for addGrantRoleToPerson.jsp? 
	//editConfig.addValidator(new AddGrantRoleToPersonValidator());
        
    Model model = (Model) application.getAttribute("jenaOntModel");
    editConfig.prepareForNonUpdate(model); 
    
    //this will return the browser to the new grant entity after an edit.
    editConfig.setEntityToReturnTo("?grant");
    
    String subjectUri = vreq.getParameter("subjectUri");       
  
    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
                                                                JavaScript.UTILS.path(),
                                                                JavaScript.CUSTOM_FORM_UTILS.path(),
                                                                "/edit/forms/js/customFormWithAdvanceTypeSelection.js"
                                                               ));            
    request.setAttribute("customJs", customJs);
    
    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
                                                                 Css.CUSTOM_FORM.path(),
                                                                 "/edit/forms/css/autocomplete.css",
                                                                 "/edit/forms/css/customFormWithAdvanceTypeSelection.css"
                                                                ));                                                                                                                                   
    request.setAttribute("customCss", customCss); 
%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />

<jsp:include page="${preForm}" />

<c:url var="acUrl" value="/autocomplete?stem=true" />
<c:url var="sparqlQueryUrl" value="/admin/sparqlquery" />


<h2>${formHeading}</h2>

<form id="addGrantRoleToPerson" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
        
    <v:input type="text" id="label" name="grantLabel" label="Grant Name ${requiredHint}" cssClass="acSelector" size="50" />

    <div class="acSelection">
        <p class="inline"><label></label><span class="acSelectionInfo"></span></p>
        <%-- bdc34: for some odd reason id and name should not be grant in this input element. --%>
        <input type="hidden" class="acReceiver" value="" /> <!-- Field value populated by JavaScript -->
    </div>
            
    <p class="submit"><v:input type="submit" id="submit" value="${submitButtonLabel}" cancel="true" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<script>
var customFormData  = {
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acUrl: '${acUrl}',
    acType: '${vivoCore}Grant'       
}
</script>

<jsp:include page="${postForm}"/>