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

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>

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

    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");
    Individual infoResource = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);
    List<Individual> authorships = infoResource.getRelatedIndividuals(predicateUri);
    vreq.setAttribute("infoResourceName", infoResource.getName());
    
    String linkedAuthorProperty = "http://vivoweb.org/ontology/core#linkedAuthor";

    List<String> customJs = new ArrayList<String>(Arrays.asList("forms/js/addAuthorsToInformationResource.js"));            
    request.setAttribute("customJs", customJs);

    List<String> customCss = new ArrayList<String>(Arrays.asList("forms/css/customForm.css",
                                                                 "forms/css/addAuthorsToInformationResource.css"                                                                
                                                                ));                                                                                                                                 
    request.setAttribute("customCss", customCss); 
%>

<c:set var="title" value="Manage authors of <em>${infoResourceName}</em>" />
<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="initialHint" value="<span class='hint'>initial okay</span>" />

<jsp:include page="${preForm}" />

<h2>${title}</h2>
       
<ul class="authors">
    <%
        for ( Individual authorship : authorships ) {
            List<Individual> authors = authorship.getRelatedIndividuals(linkedAuthorProperty);
            if ( !authors.isEmpty() ) {
                Individual author = authors.get(0);   
                String authorName = getAuthorName(author);
                %> 
                
                <li><span class="authorName"><%= authorName %></span><a href="" class="remove">Remove</a></li> 
                
                <% 
            }
        }

    %>
    
</ul>

<input type="button" value="Add Author" id="showAddForm" />

<form id="addAuthorForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
    
    <p class="inline"><v:input type="text" id="lastName" label="Last name ${requiredHint}" size="30" /></p>
    <p class="inline"><v:input type="text" id="firstName" label="First name ${requiredHint}" size="20" />${initialHint}</p>
    <p class="inline"><v:input type="text" id="middleName" label="Middle name" size="20" />${initialHint}</p>
    
    <input type="hidden" name="newAuthor" value="true" />
    
    <p class="submit"><v:input type="submit" id="submit" value="Add Author" cancel="${param.subjectUri}"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>

<%!
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

%>
