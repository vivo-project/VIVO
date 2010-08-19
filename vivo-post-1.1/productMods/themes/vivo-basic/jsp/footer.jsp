<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- DO NOT MODIFY THIS FILE. IT IS NOT USED IN THEME CUSTOMIZATION. --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet" %>
<% 
    // This is here as a safety net. We should have gotten the values in identity.jsp,
    // since it's the first jsp we hit.
    String footer = (String) request.getAttribute("ftl_footer");
    if (footer == null) {
        FreemarkerHttpServlet.getFreemarkerComponentsForJsp(request);
    } 
%>

${ftl_footer}
${ftl_googleAnalytics}
