<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>
<%-- This is a temporary file and will be removed once we have completed the transition to freemarker --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.*" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet" %>

<% 
FreemarkerHttpServlet.getFreemarkerComponentsForJsp(request);
%>

<%
  VitroRequest vreq = new VitroRequest(request);  
  Portal portal = vreq.getPortal();
  
  String contextRoot = vreq.getContextPath();
  
  String themeDir = portal != null ? portal.getThemeDir() : Portal.DEFAULT_THEME_DIR_FROM_CONTEXT;
  themeDir = contextRoot + '/' + themeDir;
%>


<c:set var="portal" value="${requestScope.portalBean}"/>
<c:set var="themeDir"><c:out value="${themeDir}" /></c:set>
<c:set var="bodyJsp"><c:out value="${requestScope.bodyJsp}" default="/debug.jsp"/></c:set>
<c:set var="title"><c:out value="${requestScope.title}" /></c:set>


    </div><!-- #wrapper-content -->
    
    ${ftl_footer}
    
    <script type="text/javascript" src="<c:url value="/js/extensions/String.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery_plugins/jquery.bgiframe.pack.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery_plugins/thickbox/thickbox-compressed.js"/>"></script>
    <!-- <script type="text/javascript" src="<c:url value="/js/jquery_plugins/ui.datepicker.js"/>"></script> -->
    
<%  String useAutoComplete = (useAutoComplete=request.getParameter("useAutoComplete")) != null && !(useAutoComplete.equals("")) ? useAutoComplete : "false";
    if (useAutoComplete.equalsIgnoreCase("true")) { %>
        <script type="text/javascript" src="<c:url value="/js/jquery_plugins/jquery-autocomplete/jquery.autocomplete.pack.js"/>"></script> 
<%  } %>
        
    <c:forEach var="jsFile" items="${customJs}">
        <script type="text/javascript" src="<c:url value="${jsFile}"/>"></script>
    </c:forEach>

</body>
</html>