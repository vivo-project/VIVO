<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.FreeMarkerHttpServlet" %>
<% 
    // If the request was for a jsp rather than a servlet, we didn't get these yet.
    // This is here as a safety net. We should have gotten the values in identity.jsp,
    // since it's the first one we hit.
    String footer = (String) request.getAttribute("ftl_footer");
    if (footer == null) {
        FreeMarkerHttpServlet.getFreeMarkerComponentsForJsp(request, response);
    } 
%>

${ftl_footer}