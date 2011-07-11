<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Landing page for managing web pages associated with an individual. From here, we do one of three things:

1. If arriving here by clicking add link on profile, go directly to add form.
2. If arriving here by edit link on the profile page, stay here for web page management (can add, edit, or delete).   
3. If arriving here after an add/edit form submission, stay here for additional web page management.

--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal" %>
<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.VClass" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.DataProperty" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>
<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.manageWebpagesForIndividual.jsp");
%>

<%

    String objectUri = (String) request.getAttribute("objectUri");  
    
    String view = request.getParameter("view");
    
    if ( "form".equals(view) || // the url specifies form view
        ( view == null && objectUri == null ) ) {  // add form always starts with form
        
        %> <jsp:forward page="addEditWebpageForm.jsp" /> <% 
        
    } // else stay here for manage view

    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    vreq.setAttribute("defaultNamespace", wdf.getDefaultNamespace());
    
    String subjectName = ((Individual)request.getAttribute("subject")).getName();

%>

<jsp:include page="${preForm}"/>

<h2><em><%= subjectName %></em></h2>

<h3>Manage Web Pages</h3>


<jsp:include page="${postForm}"/>
