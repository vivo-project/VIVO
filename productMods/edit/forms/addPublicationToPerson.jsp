<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding a publication to an author

Classes: 
foaf:Person - the individual being edited
core:Authorship - primary new individual being created

Object properties (domain : range):

core:authorInAuthorship (Person : Authorship) 
core:linkedAuthor (Authorship : Person) - inverse of authorInAuthorship

core:linkedInformationResource (Authorship : InformationResource) 
core:informationResourceInAuthorship (InformationResource : Authorship) - inverse of linkedInformationResource

--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasPublicationValidator" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.addAuthorsToInformationResource.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");    
    String subjectName = ((Individual) request.getAttribute("subject")).getName();
    vreq.setAttribute("subjectUriJson", MiscWebUtils.escape(subjectUri));
    
    vreq.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    
    String intDatatypeUri = XSD.xint.toString();    
    vreq.setAttribute("intDatatypeUri", intDatatypeUri);
    vreq.setAttribute("intDatatypeUriJson", MiscWebUtils.escape(intDatatypeUri));
%>
<c:set var="vivoOnt" value="http://vivoweb.org/ontology" />
<c:set var="vivoCore" value="${vivoOnt}/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="infoResourceClassUri" value="${vivoCore}InformationResource" />

<%-- Unlike other custom forms, this form does not allow edits of existing authors, so there are no
SPARQL queries for existing values. --%>

<v:jsonset var="newPubTypeAssertion">
    ?pubUri a ?pubType .    
</v:jsonset>

<v:jsonset var="newPubNameAssertion">
    ?pubUri <${label}> ?title .   
</v:jsonset>

<%-- This applies to both a new and an existing publication --%>
<v:jsonset var="n3ForNewAuthorship">
    @prefix core: <${vivoCore}> .
    
    ?authorshipUri a core:Authorship ;
                   core:linkedAuthor ?person .  
                     
    ?person core:authorInAuthorship ?authorshipUri .                
</v:jsonset>

<v:jsonset var="n3ForExistingPub">
    @prefix core: <${vivoCore}> .
        
    ?authorshipUri core:linkedInformationResource ?pubUri .
    ?pubUri core:informationResourceInAuthorship ?authorshipUri .
</v:jsonset>

<v:jsonset var="n3ForNewPub">
    @prefix core: <${vivoCore}> .
    
    ?pubUri a ?pubType ;
            <${label}> ?title .
               
    ?authorshipUri core:linkedInformationResource ?pubUri .
    ?pubUri core:informationResourceInAuthorship ?authorshipUri .               
</v:jsonset>


<c:set var="publicationsClassGroupUri" value="${vivoOnt}#vitroClassGrouppublications" />
<v:jsonset var="publicationsClassGroupUriJson">${publicationsClassGroupUri}</v:jsonset>

<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/individual",

    "subject"   : ["person", "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["authorshipUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForNewAuthorship}" ],
    
    "n3optional"    : [ "${n3ForExistingPub}", "${n3ForNewPub}",
                        "${newPubNameAssertion}", "${newPubTypeAssertion}" ],        
                                                                                        
    "newResources"  : { "authorshipUri" : "${defaultNamespace}",
                        "pubUri" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "pubUri", "pubType" ],
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
         "assertions"       : [ "${n3ForNewPub}" ]
      },   
      "pubType" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "VCLASSGROUP",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${publicationsClassGroupUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${newPubTypeAssertion}" ]
      },               
      "pubUri" : {
         "newResource"      : "true",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "${personClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : ["${n3ForExistingPub}"]
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
    
    editConfig.addValidator(new PersonHasPublicationValidator());
    
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    editConfig.prepareForNonUpdate(model); // we're only adding new, not editing existing
  	editConfig.setEntityToReturnTo("?pubUri");
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

<h2>Create a new publication entry for <%= subjectName %></h2>

<%-- DO NOT CHANGE IDS, CLASSES, OR HTML STRUCTURE IN THIS FORM WITHOUT UNDERSTANDING THE IMPACT ON THE JAVASCRIPT! --%>
<form id="addPublicationForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <p class="inline"><v:input type="select" label="Publication Type ${requiredHint}" name="pubType" id="typeSelector" /></p>
    
    <div class="fullViewOnly">
        
	   <p><v:input type="text" id="label" name="title" label="Title" cssClass="acSelector" size="50" /></p>

	    <div class="acSelection">
	        <%-- RY maybe make this a label and input field. See what looks best. --%>
	        <p class="inline"><label></label><span class="acSelectionInfo"></span> <a href="<c:url value="/individual?uri=" />" class="verifyMatch">(Verify this match)</a></p>
	        <input type="hidden" id="pubUri" name="pubUri" class="acReceiver" value="" /> <!-- Field value populated by JavaScript -->
	    </div>
    </div>   
     
    <p class="submit"><v:input type="submit" id="submit" value="Publication" cancel="true" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<c:url var="acUrl" value="/autocomplete?tokenize=true&stem=true" />
<c:url var="sparqlQueryUrl" value="/admin/sparqlquery" />

<%-- Must be all one line for JavaScript. --%>
<c:set var="sparqlForAcFilter">
PREFIX core: <${vivoCore}> SELECT ?pubUri WHERE {<${subjectUri}> core:authorInAuthorship ?authorshipUri . ?authorshipUri core:linkedInformationResource ?pubUri .}
</c:set>

<script type="text/javascript">
var customFormData  = {
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acUrl: '${acUrl}'
};
</script>
<jsp:include page="${postForm}"/>