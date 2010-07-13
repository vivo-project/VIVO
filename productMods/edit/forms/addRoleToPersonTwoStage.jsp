<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- 
  Custom two stage form for adding a Role to a Person.  
  
  Stage one is selecting the type of the non-person thing 
  associated with the Role with the intention of reducing the 
  number of Individuals that the user has to select from.
  Stage two is selecting the non-person Individual to associate
  with the Role. 

  This is intended to create a set of statements like:

  ?person  core:hasResearchActivityRole ?newRole.
  ?newRole rdf:type core:ResearchActivityRole ;         
           core:relatedRole ?someActivity .
  ?someActivity rdf:type core:ResearchActivity .
  ?someActivity rdfs:label "activity title" .
  
  Important: This form cannot be directly used as a custom form.  It has parameters that must be set.
  See addClinicalRoleToPerson.jsp for an example.
  
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

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>


<%@page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty"%><c:set var="vivoOnt" value="http://vivoweb.org/ontology" />
<c:set var="vivoCore" value="${vivoOnt}/core#" />

<%--
  These are the parameters that MUST be set of this form:
   sparqlForAcFilter
   role type
   predicate inverse          
   role activity type label (should be singular)
   super type of role types for roleActivityType select list generation 
--%>
<c:set var="sparqlForAcFilter">${param.sparqlForAcFilter}</c:set>
<c:set var="roleActivityTypeLabel">${param.roleActivityTypeLabel}</c:set>
<c:set var="roleType">${param.roleType}</c:set>
<c:set var="roleActivitySuperType">${param.roleActivitySuperType}</c:set>

<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    //vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");    
    String subjectName = ((Individual) request.getAttribute("subject")).getName();
    vreq.setAttribute("subjectUriJson", MiscWebUtils.escape(subjectUri));
    
    vreq.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    
    String intDatatypeUri = XSD.xint.toString();    
    vreq.setAttribute("intDatatypeUri", intDatatypeUri);
    vreq.setAttribute("intDatatypeUriJson", MiscWebUtils.escape(intDatatypeUri));
    
    ObjectProperty op = wdf.getObjectPropertyDao().getObjectPropertyByURI( predicateUri ); 
    if( op != null &&  op.getURIInverse() != null ){
		%> <c:set var="inversePredicate"><%=op.getURIInverse()%></c:set> <%
    }else{
    	%> <c:set var="inversePredicate"></c:set> <%
    }
%>
<c:set var="vivoOnt" value="http://vivoweb.org/ontology" />
<c:set var="vivoCore" value="${vivoOnt}/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="defualtNamespace" value=""/> <%--blank triggers default URI generation behavior --%>

<c:set var="infoResourceClassUri" value="${vivoCore}InformationResource" />

<v:jsonset var="n3ForNewRole">
	@prefix core: <${vivoCore}> .
       
	?person ?rolePredicate ?role.	
	?role   a <${roleType}> .		  
    ?role   core:roleIn ?roleActivity .
    ?roleActivity  core:relatedRole ?role .    
</v:jsonset>

<v:jsonset var="n3ForNewActivity">
    ?roleActivity <${label}> ?title .  
    ?roleActivity a ?roleActivityType .
</v:jsonset>

<v:jsonset var="n3ForInverse"> 
	?role  ?inverseRolePredicate ?person.
</v:jsonset>

<c:set var="publicationsClassGroupUri" value="${vivoOnt}#vitroClassGrouppublications" />
<v:jsonset var="publicationsClassGroupUriJson">${publicationsClassGroupUri}</v:jsonset>

<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/individual",

    "subject"   : ["person", "${subjectUriJson}" ],
    "predicate" : ["rolePredicate", "${predicateUriJson}" ],
    "object"    : ["role", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForNewRole}" ],    
    "n3optional"    : [ "${n3ForNewActivity}", "${n3ForInverse}" ],        
                                                                                        
    "newResources"  : { "role" : "${defaultNamespace}",
                        "roleActivity" : "${defaultNamespace}" },

    "urisInScope"    : { "inverseRolePredicate" : "${inversePredicate}" },
    "literalsInScope": { },
    "urisOnForm"     : [ "roleActivity", "roleActivityType" ],
    "literalsOnForm" : [ "title" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : { },
    "sparqlForExistingUris" : { },
    "fields" : {
      "title" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ ]
      },   
      "roleActivityType" : {
         "newResource"      : "true",
         "validators"       : [ ],
         "optionsType"      : "CHILD_VCLASSES",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${roleActivitySuperType}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ ]
      },               
      "roleActivity" : {
         "newResource"      : "true",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : [ ]
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
    
    //editConfig.addValidator(new PersonHasPublicationValidator());
    
    //this will return the browser to the new activity entity after an edit.
    editConfig.setEntityToReturnTo("?roleActivity");
    
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    editConfig.prepareForNonUpdate(model); // we're only adding new, not editing existing

    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
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

<h2>Create a new ${roleActivityTypeLabel} entry for <%= subjectName %></h2>

<form id="addPublicationForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <p class="inline"><v:input type="select" label="${roleActivityTypeLabel} Type ${requiredHint}" name="roleActivityType" id="typeSelector" /></p>
    
    <div id="fullViewOnly">
        
	   <p><v:input type="text" id="label" name="title" label="Title" cssClass="acSelector" size="50" /></p>

	    <div class="acSelection">
	        <%-- RY maybe make this a label and input field. See what looks best. --%>
	        <p class="inline"><label></label><span class="acSelectionInfo"></span> <a href="<c:url value="/individual?uri=" />" class="verifyMatch">(Verify this match)</a></p>
	        <input type="hidden" id="roleActivityURI" name="roleActivity" class="acReceiver" value="" /> <!-- Field value populated by JavaScript -->
	    </div>
    </div>   
     
    <p class="submit"><v:input type="submit" id="submit" value="${roleActivityTypeLabel}" cancel="true" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<c:url var="acUrl" value="/autocomplete?tokenize=true&stem=true" />
<c:url var="sparqlQueryUrl" value="/admin/sparqlquery" />

<script type="text/javascript">
var customFormData  = {
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acUrl: '${acUrl}'
};
</script>
<jsp:include page="${postForm}"/>