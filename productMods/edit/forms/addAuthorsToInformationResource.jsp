<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding authors to information resources

Classes: 
core:InformationResource - the information resource being edited
core:Authorship - primary new individual being created
foaf:Person - new or existing individual

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

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.DataPropertyComparator"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.CreateLabelFromNameFields"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
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
    vreq.setAttribute("intDatatypeUriJson", MiscWebUtils.escape(XSD.xint.toString()));
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="foaf" value="http://xmlns.com/foaf/0.1/" />

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

<c:set var="returnPath" value="/edit/editRequestDispatch.jsp?subjectUri=${subjectUri}&predicateUri=${predicateUri}" />

<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "${returnPath}",

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
         "validators"       : [ "nonempty", "datatype:${stringDatatypeUriJson}" ],
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
         "validators"       : [ "nonempty", "datatype:${stringDatatypeUriJson}" ],
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

    // For now the field names in CreateLabelFromFieldNames.processEditSubmission() are
    // hard-coded. If we want the flexibility in naming them, we can pass in a map of
    // the field names when creating the preprocessor. 
    editConfig.addEditSubmissionPreprocessor(new CreateLabelFromNameFields(editConfig));
    
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    editConfig.prepareForNonUpdate(model); // we're only adding new, not editing existing
    
    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");
    
    Individual infoResource = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);   
    List<Individual> authorships = infoResource.getRelatedIndividuals(predicateUri);
    String rankUri = "http://vivoweb.org/ontology/core#authorRank";
    DataPropertyComparator comp = new DataPropertyComparator(rankUri);
    Collections.sort(authorships, comp);
    
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
%>

<c:set var="title" value="<em>${infoResourceName}</em>: Authors" />
<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="initialHint" value="<span class='hint'>initial okay</span>" />

<jsp:include page="${preForm}" />

<h2>${title}</h2>
      
<ul id="authors">
    <%
        int rank = 0;
        for ( Individual authorship : authorships ) {
            rank = Integer.valueOf(authorship.getDataValue(rankUri));
            Individual author = authorship.getRelatedIndividual(linkedAuthorProperty);
            if ( author != null ) {
                request.setAttribute("author", author);
                %> 
                <%-- RY Should use author short view here instead? --%>
                <c:url var="authorHref" value="/individual">
                    <c:param name="uri" value="${author.URI}"/>
                </c:url> 
                <li><a href="${authorHref}" class="authorName">${author.name}</a><a href="" class="remove">Remove</a></li> 
                
                <% 
            }
            
        }
        // A new author will be ranked last when added.
        // This doesn't handle gaps in the ranking: vreq.setAttribute("rank", authorships.size()+1);
        vreq.setAttribute("rank", rank + 1);
    %>
    
</ul>

<div id="showAddForm">
    <v:input type="submit" value="Add Author" id="showAddFormButton" cancel="${param.subjectUri}" cancelLabel="Return to Publication" />
</div> 


<form id="addAuthorForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
    
    <p class="inline"><v:input type="text" id="lastName" label="Last name ${requiredHint}" size="30" /></p>
    <p class="inline"><v:input type="text" id="firstName" label="First name ${requiredHint}" size="20" />${initialHint}</p>
    <p class="inline"><v:input type="text" id="middleName" label="Middle name" size="20" />${initialHint}</p>
    
    <input type="hidden" name="personUri" value="" /> <!-- Field value populated by JavaScript -->   
    <input type="hidden" name="rank" value="${rank}" />
    
    <p class="submit"><v:input type="submit" id="submit" value="Add Author" cancel="${param.subjectUri}" /></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>

<%!
// We'll just rely on rdfs:label for now. In future, the label will be created by the app from
// last name, first name, and middle name fields, so we don't have to worry about inconsistent
// ordering.
/*
public String getAuthorName(Individual author) {
    String name;
    
    String lastName = author.getDataValue("http://xmlns.com/foaf/0.1/lastName");
    String firstName = author.getDataValue("http://xmlns.com/foaf/0.1/firstName");

    if (lastName != null && firstName != null) {
        name = lastName + ", " + firstName; 
        String middleName = author.getDataValue("http://vivoweb.org/ontology/core#middleName");
        if (middleName != null) {
            name += " " + middleName;
        }
    }
    else {
        name = author.getName();
    }
    return name;
}
*/
%>
