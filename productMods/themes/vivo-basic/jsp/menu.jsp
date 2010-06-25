<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- DO NOT MODIFY THIS FILE. IT IS NOT USED IN THEME CUSTOMIZATION. --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.BreadCrumbsUtil" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreeMarkerHttpServlet" %>
<% 
    // This is here as a safety net. We should have gotten the values in identity.jsp,
    // since it's the first jsp we hit.
    String menu = (String) request.getAttribute("ftl_menu");
    if (menu == null) {
        FreeMarkerHttpServlet.getFreeMarkerComponentsForJsp(request);
    } 
%>

<div id="navAndSearch" class="block">
    ${ftl_menu}
    ${ftl_search}
</div> <!--  end navAndSearch -->

<div id="breadcrumbs" class="small"><%=BreadCrumbsUtil.getBreadCrumbsDiv(request)%></div>

