<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- DO NOT MODIFY THIS FILE. IT IS NOT USED IN THEME CUSTOMIZATION. --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.BreadCrumbsUtil" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.FreeMarkerHttpServlet" %>
<% 
    // If the request was for a jsp rather than a servlet, we didn't get these yet.
    // This is here as a safety net. We should have gotten the values in identity.jsp,
    // since it's the first one we hit.
    String menu = (String) request.getAttribute("ftl_menu");
    if (menu == null) {
        FreeMarkerHttpServlet.getFreeMarkerComponentsForJsp(request, response);
    } 
%>

<div id="navAndSearch" class="block">
    ${ftl_menu}
    ${ftl_search}
</div> <!--  end navAndSearch -->

<div id="breadcrumbs" class="small"><%=BreadCrumbsUtil.getBreadCrumbsDiv(request)%></div>

