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

<%@page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasPublicationValidator" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode"%>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.addAuthorsToInformationResource.jsp");
    public static String nodeToPubProp = "http://vivoweb.org/ontology/core#linkedInformationResource";
%>
<%

    VitroRequest vreq = new VitroRequest(request);

    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");
    String objectUri = vreq.getParameter("objectUri");

	Individual obj = (Individual) request.getAttribute("object");

    EditMode mode = FrontEndEditingUtils.getEditMode(request, nodeToPubProp);

    /*
    There are 3 modes that this form can be in: 
     1.  Add. There is a subject and a predicate but no position and nothing else. 
     
     2. Repair a bad role node.  There is a subject, predicate and object but there is no individual on the 
        other end of the object's core:linkedInformationResource stmt.  This should be similar to an add but the form should be expanded.
        
     3. Really bad node. Multiple core:authorInAuthorship statements.   
     
     This form does not currently support normal edit mode where there is a subject, an object, and an individual on
     the other end of the object's core:linkedInformationResource statement. We redirect to the publication profile
     to edit the publication.
    */
    
    if( mode == EditMode.ADD ) {
       %> <c:set var="editMode" value="add"/><%
    } else if(mode == EditMode.EDIT){
        // Because it's edit mode, we already know there's one and only one statement
        ObjectPropertyStatement ops = obj.getObjectPropertyStatements(nodeToPubProp).get(0);
        String pubUri = ops.getObjectURI();
        String forwardToIndividual = pubUri != null ? pubUri : objectUri;         
        %>  
        <jsp:forward page="/individual">
            <jsp:param value="<%= forwardToIndividual %>" name="uri"/>
        </jsp:forward>  
        <%              
    } else if(mode == EditMode.REPAIR){
        %> <c:set var="editMode" value="repair"/><%
    }
    
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    Individual subject = (Individual) request.getAttribute("subject");
    String subjectName = subject.getName();
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

<c:set var="publicationTypeLiteralOptions">
    ["", "Select one"],
    ["http://purl.org/ontology/bibo/AcademicArticle", "Academic Article"],
    ["http://purl.org/ontology/bibo/Article", "Article"],
    ["http://purl.org/ontology/bibo/AudioDocument", "Audio Document"],
    ["http://vivoweb.org/ontology/core#BlogPosting", "Blog Posting"],
    ["http://purl.org/ontology/bibo/Book", "Book"],
    ["http://vivoweb.org/ontology/core#CaseStudy", "Case Study"],
    ["http://vivoweb.org/ontology/core#Catalog", "Catalog"],
    ["http://purl.org/ontology/bibo/Chapter", "Chapter"],
    ["http://vivoweb.org/ontology/core#ConferencePaper", "Conference Paper"],
    ["http://vivoweb.org/ontology/core#ConferencePoster", "Conference Poster"],
    ["http://vivoweb.org/ontology/core#Database", "Database"],
    ["http://purl.org/ontology/bibo/EditedBook", "Edited Book"],
    ["http://vivoweb.org/ontology/core#EditorialArticle", "Editorial Article"],
    ["http://purl.org/ontology/bibo/Film", "Film"],
    ["http://vivoweb.org/ontology/core#Newsletter", "Newsletter"],
    ["http://vivoweb.org/ontology/core#NewsRelease", "News Release"],
    ["http://purl.org/ontology/bibo/Patent", "Patent"],
    ["http://purl.obolibrary.org/obo/OBI_0000272", "Protocol"],
    ["http://purl.org/ontology/bibo/Report", "Report"],
    ["http://vivoweb.org/ontology/core#ResearchProposal", "Research Proposal"],
    ["http://vivoweb.org/ontology/core#Review", "Review"],
    ["http://vivoweb.org/ontology/core#Software", "Software"],
    ["http://vivoweb.org/ontology/core#Speech", "Speech"],
    ["http://purl.org/ontology/bibo/Thesis", "Thesis"],
    ["http://vivoweb.org/ontology/core#Video", "Video"],
    ["http://purl.org/ontology/bibo/Webpage", "Webpage"],
    ["http://purl.org/ontology/bibo/Website", "Website"],
    ["http://vivoweb.org/ontology/core#WorkingPaper", "Working Paper"]
</c:set>

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
         "optionsType"      : "HARDCODED_LITERALS",
         "literalOptions"   : [ ${publicationTypeLiteralOptions} ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
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
    
    if (objectUri != null) { // editing existing (in this case, only repair is currently provided by the form)
        editConfig.prepareForObjPropUpdate(model);
    } else { // adding new
        editConfig.prepareForNonUpdate(model);
    }
    
    // Return to person, not publication. See NIHVIVO-1464.
  	// editConfig.setEntityToReturnTo("?pubUri"); 
    
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

<%-- Configure add vs. edit --%> 
<c:choose>
    <c:when test='${editMode == "add"}'>
        <c:set var="titleVerb" value="Create" />
        <c:set var="submitButtonText" value="Publication" />
    </c:when>
    <c:otherwise>
        <c:set var="titleVerb" value="Edit" />  
        <c:set var="submitButtonText" value="Edit Publication" />
    </c:otherwise>
</c:choose>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />

<jsp:include page="${preForm}" />

<% if( mode == EditMode.ERROR ){ %>
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<% }else{ %>

<h2>${titleVerb} publication entry for <%= subjectName %></h2>

<%@ include file="unsupportedBrowserMessage.jsp" %>

<%-- DO NOT CHANGE IDS, CLASSES, OR HTML STRUCTURE IN THIS FORM WITHOUT UNDERSTANDING THE IMPACT ON THE JAVASCRIPT! --%>
<form id="addPublicationForm" class="customForm noIE67"  action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <p class="inline"><v:input type="select" label="Publication Type ${requiredHint}" name="pubType" id="typeSelector" /></p>
    
    <div class="fullViewOnly">
        
	   <p><v:input type="text" id="relatedIndLabel" name="title" label="Title ${requiredHint}" cssClass="acSelector" size="50" /></p>

	    <div class="acSelection">
	        <%-- RY maybe make this a label and input field. See what looks best. --%>
	        <p class="inline"><label></label><span class="acSelectionInfo"></span> <a href="<c:url value="/individual?uri=" />" class="verifyMatch">(Verify this match)</a></p>
	        <input type="hidden" id="pubUri" name="pubUri" class="acUriReceiver" value="" /> <!-- Field value populated by JavaScript -->
	    </div>
    </div>   
     
    <p class="submit"><v:input type="submit" id="submit" value="${submitButtonText}" cancel="true" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<c:url var="acUrl" value="/autocomplete?tokenize=true" />
<c:url var="sparqlQueryUrl" value="/ajax/sparqlQuery" />

<%-- Must be all one line for JavaScript. --%>
<c:set var="sparqlForAcFilter">
PREFIX core: <${vivoCore}> SELECT ?pubUri WHERE {<${subjectUri}> core:authorInAuthorship ?authorshipUri . ?authorshipUri core:linkedInformationResource ?pubUri .}
</c:set>

<script type="text/javascript">
var customFormData  = {
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acUrl: '${acUrl}',
    submitButtonTextType: 'simple',
    editMode: '${editMode}',
    defaultTypeName: 'publication' // used in repair mode to generate button text
};
</script>

<% } %>

<jsp:include page="${postForm}"/>