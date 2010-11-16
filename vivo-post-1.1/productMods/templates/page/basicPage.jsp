<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>
<%-- This is a temporary file and will be removed once we have completed the transition to freemarker --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.*" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/functions" prefix="fn" %>
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
<c:set var="themeDir"><%=themeDir%></c:set>
<c:set var="bodyJsp"><c:out value="${requestScope.bodyJsp}" default="/debug.jsp"/></c:set>
<c:set var="title"><c:out value="${requestScope.title}" /></c:set>

<%-- test for Wilma theme to help for smooth transition --%>
<c:choose>
    <c:when test="${fn:contains(themeDir,'wilma')}">
        <jsp:include page="/themes/wilma/templates/page/basicPage.jsp" flush="true"/>
    </c:when>
    <c:otherwise>
        <jsp:include page="doctype.jsp"/>
            <head>
                <jsp:include page="headContent.jsp"/>
            </head>
            <body ${requestScope.bodyAttr}>
                <div id="wrap" class="container">
                    <div id="header">
                        <jsp:include page="/templates/page/freemarkerTransition/identity.jsp" flush="true"/>
                        <jsp:include page="/templates/page/freemarkerTransition/menu.jsp" flush="true"/>
                    </div><!-- #header -->
                    <hr class="hidden" />
                    <div id="contentwrap">
                        <c:import url="${bodyJsp}"/>
                    </div> <!-- #contentwrap -->
                    <jsp:include page="/templates/page/freemarkerTransition/footer.jsp" flush="true"/>
                </div> <!-- #wrap -->
            </body>
        </html>
    </c:otherwise>
</c:choose>