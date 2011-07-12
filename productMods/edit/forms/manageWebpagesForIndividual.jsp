<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Landing page for managing web pages associated with an individual. From here, we do one of three things:

1. If arriving here by clicking add link on profile, go directly to add form.
2. If arriving here by edit link on the profile page, stay here for web page management (can add, edit, or delete).   
3. If arriving here after an add/edit form submission, stay here for additional web page management.

--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Map" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>
<%@ page import="com.hp.hpl.jena.query.ResultSet" %>
<%@ page import="com.hp.hpl.jena.rdf.model.RDFNode" %>
<%@ page import="com.hp.hpl.jena.query.QuerySolution" %>
<%@ page import="com.hp.hpl.jena.query.Dataset" %>

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
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils" %>

<%@ page import="org.apache.commons.lang.StringUtils" %>
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
        
    } // else stay here to manage webpages

    VitroRequest vreq = new VitroRequest(request);
    //WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    //vreq.setAttribute("defaultNamespace", wdf.getDefaultNamespace());
    
    String subjectName = ((Individual)request.getAttribute("subject")).getName();
    String subjectUri = (String) request.getAttribute("subjectUri");
    String predicateUri = (String) request.getAttribute("predicateUri");
    
    List<Map<String, String>> webpages = getWebpages(subjectUri, vreq);
    vreq.setAttribute("webpages", webpages);
    
    String ulClass = "";
    List<String> ulClasses = new ArrayList<String>();   
    if (webpages.size() > 1) {
        // This class triggers application of dd styles. Don't wait for js to add 
        // the ui-sortable class, because then the page flashes as the styles are updated.
        ulClasses.add("dd");
    }
    if (ulClasses.size() > 0) {
        ulClass="class=\"" + StringUtils.join(ulClasses, " ") + "\"";
    }
    
    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
            "/edit/forms/js/manageWebpagesForIndividual.js"
           ));            
    request.setAttribute("customJs", customJs);

    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
             Css.CUSTOM_FORM.path(),
             "/edit/forms/css/manageWebpagesForIndividual.css"                                                                
            ));                                                                                                                                 
    request.setAttribute("customCss", customCss);
%>

<c:set var="subjectUri" value="<%= subjectUri %>" />
<c:set var="rankPredicate" value="http://vivoweb.org/ontology/core#rank" />

<c:url var="returnToIndividualUrl" value="/individual?uri=${subjectUri}" />
<c:url var="baseEditWebpageUrl" value="/edit/editRequestDispatch.jsp">
    <c:param name="subjectUri" value="<%= subjectUri %>" />
    <c:param name="predicateUri" value="<%= predicateUri %>" />
    <c:param name="view" value="form" />
</c:url>
<c:url var="deleteWebpageUrl" value="/edit/primitiveDelete" />
<c:url var="reorderUrl" value="/edit/reorder" />
<c:url var="showAddFormUrl" value="/edit/editRequestDispatch.jsp">
    <c:param name="subjectUri" value="<%= subjectUri %>" />
    <c:param name="predicateUri" value="<%= predicateUri %>" />
    <c:param name="cancelTo" value="manage" />
</c:url>

<jsp:include page="${preForm}"/>

<h2><em><%= subjectName %></em></h2>

<h3>Manage Web Pages</h3>

<script type="text/javascript">
    var webpageData = [];
</script>
    
<ul id="webpageList" <%= ulClass %>>

    <c:if test="${ empty webpages }">
        <p>This individual currently has no web pages specified. Add a new web page by clicking on the button below.</p>
    </c:if>
    
    <c:forEach var="webpage" items="${webpages}">
        <li class="webpage">
	        <c:set var="anchor">${ empty webpage.anchor ? webpage.url : webpage.anchor }</c:set>
	        <span class="webpageName">
	            <a href="${webpage.url}">${anchor}</a>
	        </span>
            <span class="editingLinks">
                <a href="${baseEditWebpageUrl}&objectUri=${webpage.link}" class="edit">Edit</a> | 
                <a href="${deleteWebpageUrl}" class="remove">Delete</a> 
            </span>
        </li>    
        
        <script type="text/javascript">
            webpageData.push({
                "webpageUri": "${webpage.link}"              
            });
        </script>             
    </c:forEach>
</ul>

<div id="addAndCancelLinks">
    <%-- There is no editConfig at this stage, so we don't need to go through postEditCleanup.jsp on cancel.
         These can just be ordinary links, rather than a v:input element, as in 
         addAuthorsToInformationResource.jsp. --%>
    <a href="${showAddFormUrl}" id="showAddForm" class="button green">Add Web Page</a>
    <a href="${returnToIndividualUrl}" id="returnToIndividual" class="return">Return to Individual</a>
</div>



<script type="text/javascript">
var customFormData = {
    rankPredicate: '${rankPredicate}',
    reorderUrl: '${reorderUrl}'
};
</script>

<jsp:include page="${postForm}"/>

<%!

private static String WEBPAGE_QUERY = ""
    + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
    + "SELECT DISTINCT ?link ?url ?anchor ?rank WHERE { \n"
    + "    ?subject core:webpage ?link . \n"
    + "    OPTIONAL { ?link core:linkURI ?url } \n"
    + "    OPTIONAL { ?link core:linkAnchorText ?anchor } \n"
    + "    OPTIONAL { ?link core:rank ?rank } \n"
    + "} ORDER BY ?rank";
    
    
private List<Map<String, String>> getWebpages(String subjectUri, VitroRequest vreq) {
      
    String queryStr = QueryUtils.subUriForQueryVar(WEBPAGE_QUERY, "subject", subjectUri);
    log.debug("Query string is: " + queryStr);
    List<Map<String, String>> webpages = new ArrayList<Map<String, String>>();
    try {
        ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            RDFNode node = soln.get("link");
            if (node.isURIResource()) {
                webpages.add(QueryUtils.querySolutionToStringValueMap(soln));        
            }
        }
    } catch (Exception e) {
        log.error(e, e);
    }    
    
    return webpages;
}

%>
