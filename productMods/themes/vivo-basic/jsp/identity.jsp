<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- DO NOT MODIFY THIS FILE. IT IS NOT USED IN THEME CUSTOMIZATION. --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreeMarkerHttpServlet" %>
<% 
    // If the request was for a jsp rather than a servlet, we didn't get these yet.
    String identity = (String) request.getAttribute("ftl_identity");
    if (identity == null) {
        FreeMarkerHttpServlet.getFreeMarkerComponentsForJsp(request, response);
    } 
%>

${ftl_identity}

