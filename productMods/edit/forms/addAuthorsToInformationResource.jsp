<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding authors to information resources

Classes: 
core:InformationResource - the information resource being edited
core:Authorship - primary new individual being created
foaf:Person - new or existing individual being linked to

Data properties of Authorship:
core:authorRank

Object properties (domain : range)

core:informationResourceInAuthorship (InformationResource : Authorship) 
core:linkedInformationResource (Authorship : InformationResource) - inverse of informationResourceInAuthorship

core:linkedAuthor (Authorship : Person) 
core:authorInAuthorship (Person : Authorship) - inverse of linkedAuthor

--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.net.URLEncoder" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.DataPropertyComparator" %>
<%-- <%@ page import="edu.cornell.mannlib.vitro.webapp.controller.EntityMergedPropertyListController.PropertyRanker" %> --%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.PublicationHasAuthorValidator" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.StringUtils" %>
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

    String flagUri = null;
    if (wdf.getApplicationDao().isFlag1Active()) {
        flagUri = VitroVocabulary.vitroURI+"Flag1Value"+vreq.getPortal().getPortalId()+"Thing";
    } else {
        flagUri = wdf.getVClassDao().getTopConcept().getURI();  // fall back to owl:Thing if not portal filtering
    }
    vreq.setAttribute("flagUri",flagUri);
    
    vreq.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    
    String intDatatypeUri = XSD.xint.toString();    
    vreq.setAttribute("intDatatypeUri", intDatatypeUri);
    vreq.setAttribute("intDatatypeUriJson", MiscWebUtils.escape(intDatatypeUri));
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="foaf" value="http://xmlns.com/foaf/0.1/" />
<c:set var="personClassUri" value="${foaf}Person" />

<%-- Unlike other custom forms, this form does not allow edits of existing authors, so there are no
SPARQL queries for existing values. --%>
<%-- RY Is this claim correct, or do we need them to retrieve an existing individual? --%>
<%-- Data properties --%>

<v:jsonset var="newPersonFirstNameAssertion">
    @prefix foaf: <${foaf}> .     
    ?newPerson foaf:firstName ?firstName .
</v:jsonset>

<v:jsonset var="newPersonMiddleNameAssertion">
    @prefix core: <${vivoCore}> .   
    ?newPerson core:middleName ?middleName .
</v:jsonset>

<v:jsonset var="newPersonLastNameAssertion">
    @prefix foaf: <${foaf}> .     
    ?newPerson foaf:lastName ?lastName .
</v:jsonset>

<v:jsonset var="authorshipRankAssertion">
    @prefix core: <${vivoCore}> .   
    ?authorshipUri core:authorRank ?rank .
</v:jsonset>

<%-- This applies to both new and existing person --%>
<v:jsonset var="n3ForNewAuthorship">
    @prefix core: <${vivoCore}> .
    
    ?authorshipUri a core:Authorship ,
                     <${flagUri}> ;
                   core:linkedInformationResource ?infoResource ;
                   core:authorRank ?rank .
                   
    ?infoResource core:informationResourceInAuthorship ?authorshipUri .      
</v:jsonset>

<v:jsonset var="n3ForExistingPerson">
    @prefix core: <${vivoCore}> .
    ?authorshipUri core:linkedAuthor ?personUri .
    ?personUri core:authorInAuthorship ?authorshipUri .
</v:jsonset>

<v:jsonset var="n3ForNewPerson">
    @prefix foaf: <${foaf}> . 
    @prefix core: <${vivoCore}> .
    
    ?newPerson a foaf:Person ,
                 <${flagUri}> ;
               <${label}> ?label .
               
    ?authorshipUri core:linkedAuthor ?newPerson .
    ?newPerson core:authorInAuthorship ?authorshipUri .               
</v:jsonset>

<v:jsonset var="personClassUriJson">${personClassUri}</v:jsonset>

<c:set var="returnPathAfterSubmit" value="/edit/editRequestDispatch.jsp?subjectUri=${subjectUri}&predicateUri=${predicateUri}" />

<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "${returnPathAfterSubmit}",

    "subject"   : ["infoResource", "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["authorshipUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForNewAuthorship}", "${authorshipRankAssertion}" ],
    
    "n3optional"    : [ "${newPersonFirstNameAssertion}", "${newPersonMiddleNameAssertion}", 
                        "${newPersonLastNameAssertion}", 
                        "${n3ForNewPerson}", "${n3ForExistingPerson}" ],                                                
                        
    "newResources"  : { "authorshipUri" : "${defaultNamespace}",
                        "newPerson" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "authorshipUri", "personUri" ],
    "literalsOnForm" : [ "firstName", "middleName", "lastName", "rank", "label" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : { },
    "sparqlForExistingUris" : { },
    "fields" : {
      "label" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForNewPerson}" ]
      },   
      "firstName" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${newPersonFirstNameAssertion}" ]
      },   
      "middleName" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${newPersonMiddleNameAssertion}" ]
      },
      "lastName" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${newPersonLastNameAssertion}" ]
      },  
      "rank" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${intDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${authorshipRankAssertion}"]
      },  
      "personUri" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "${personClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : ["${n3ForExistingPerson}"]
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
    
    editConfig.addValidator(new PublicationHasAuthorValidator());
    
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    editConfig.prepareForNonUpdate(model); // we're only adding new, not editing existing
    
    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");
    
    Individual infoResource = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);   
    List<Individual> authorships = infoResource.getRelatedIndividuals(predicateUri);
    String rankPredicateUri = "http://vivoweb.org/ontology/core#authorRank";
    
    vreq.setAttribute("infoResourceName", infoResource.getName());
    
    String linkedAuthorProperty = "http://vivoweb.org/ontology/core#linkedAuthor";
    
    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
                                                                JavaScript.UTILS.path(),
                                                                JavaScript.CUSTOM_FORM_UTILS.path(),
                                                                "/edit/forms/js/addAuthorsToInformationResource.js"
                                                                ));            
    request.setAttribute("customJs", customJs);

    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
                                                                 Css.CUSTOM_FORM.path(),
                                                                 "/edit/forms/css/addAuthorsToInformationResource.css"                                                                
                                                                ));                                                                                                                                 
    request.setAttribute("customCss", customCss); 
    
    String ulClass = "";
    List<String> ulClasses = new ArrayList<String>();
    
    if (authorships.size() > 1) {
        // This class triggers application of dd styles. Don't wait for js to add 
        // the ui-sortable class, because then the page flashes as the styles are updated.
        ulClasses.add("dd");
    }

    if (ulClasses.size() > 0) {
        ulClass="class=\"" + StringUtils.join(ulClasses, " ") + "\"";
    }
%>

<c:set var="title" value="<em>${infoResourceName}</em>: Authors" />
<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="initialHint" value="<span class='hint'>(initial okay)</span>" />

<jsp:include page="${preForm}" />

<h2>${title}</h2>

<ul id="authorships" <%= ulClass %>>
<%

    // RY Should get authorRank properties rather than Authorship individuals; then can use
    // existing PropertyRanker
    DataPropertyComparator comp = new DataPropertyComparator(rankPredicateUri);
    Collections.sort(authorships, comp);
        
    int maxRank = 0;
    int position = 0;  
    
    for ( Individual authorship : authorships ) {
        Individual author = authorship.getRelatedIndividual(linkedAuthorProperty);
        if ( author != null ) {
            position++;
            String rankValue = "";
            DataPropertyStatement rankStmt = authorship.getDataPropertyStatement(rankPredicateUri);
            if (rankStmt != null) {
                rankValue = rankStmt.getData();
                maxRank = Integer.valueOf(rankValue);
                String rankDatatypeUri = rankStmt.getDatatypeURI();
                if ( !StringUtils.isEmpty(rankDatatypeUri) ) {
                    rankValue += "_" + rankDatatypeUri;
                }                                                
            }
            request.setAttribute("rankValue", rankValue);
            request.setAttribute("authorName", author.getName());
            // Doesn't seem to need urlencoding to add as id attribute value
            //request.setAttribute("authorUri", URLEncoder.encode(author.getURI(), "UTF-8"));
            request.setAttribute("authorUri", author.getURI());
            request.setAttribute("authorshipUri", authorship.getURI());

            // This value is used to replace a moved element after a failed reorder.
            // It's not the same as rank, because ranks may have gaps. 
            request.setAttribute("position", position);
                
            %> 
            <c:url var="authorHref" value="/individual">
                <c:param name="uri" value="${authorUri}"/>
            </c:url>
            <c:url var="deleteAuthorshipHref" value="/edit/primitiveDelete" />
        
            <li class="authorship" id="${authorshipUri}">
                <span class="rank" id="${rankValue}"></span> 
                <span class="position" id="${position}"></span> 
                <%-- This span will be used in the next phase, when we display a message that the author has been
                removed. That text will replace the a.authorLink, which will be removed. --%>
                <span class="author"><a href="${authorHref}" id="${authorUri}" class="authorLink">${authorName}</a>
                <a href="${deleteAuthorshipHref}" class="remove">Remove</a>
                <%-- <a href="${undoHref}" class="undo">Undo</a>  --%></span>
            </li> 
                
            <% 
        }          
    }
    // A new author will be ranked last when added.
    // This wouldn't handle gaps in the ranking: vreq.setAttribute("rank", authorships.size()+1);
    request.setAttribute("newRank", maxRank + 1);
    request.setAttribute("rankPred", rankPredicateUri);
%>
    
</ul>

<%
    if (authorships.size() == 0) {
        %><p>This publication currently has no authors specified.</p><% 
    }
%>

<div id="showAddForm">
    <v:input type="submit" value="Add Author" id="showAddFormButton" cancel="true" cancelLabel="Return to Publication" cancelUrl="/individual" />
</div> 

<div id="infoForJs">
    <span class="rankPred" id="${rankPred}"></span>
    <span class="rankXsdType" id="${intDatatypeUri}"></span>
    <span class="acUrl" id="<c:url value="/autocomplete?type=${foaf}Person&stem=false" />"></span>
    <span class="reorderUrl" id="<c:url value="/edit/primitiveRdfEdit" />"></span>
</div>

<form id="addAuthorForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <h3>Add an Author</h3>
    
    <p class="inline"><v:input type="text" id="lastName" label="Name ${requiredHint}" size="30" /></p>
    <p class="inline"><v:input type="text" id="firstName" label="First name ${requiredHint} ${initialHint}" size="20" /></p>
    <p class="inline"><v:input type="text" id="middleName" label="Middle name ${initialHint}" size="20" /></p>
    <input type="hidden" id="label" name="label" value="" />  <!-- Field value populated by JavaScript -->
    
    <div id="selectedAuthor">
        <%-- RY maybe make this a label and input field. See what looks best. --%>
        <p class="inline"><label>Selected author: </label><span id="selectedAuthorName"></span></p>
        <input type="hidden" id="personUri" name="personUri" value="" /> <!-- Field value populated by JavaScript -->
    </div>
    
    <input type="hidden" name="rank" id="rank" value="${newRank}" />

    <p class="submit"><v:input type="submit" id="submit" value="Add Author" cancel="true" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>

