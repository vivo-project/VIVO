<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page language="java" %>
<%@ page errorPage="error.jsp"%>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.TabMenu" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.BreadCrumbsUtil" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session" />

<%
    /**
     *
     * @version 1.00
     * @author Jon Corson-Rikert, Brian Caruso, and Brian Lowe
     *
     * UPDATES: 
     * 2009-04-20   MW542   moved search form to menu.jsp
     * 2007-09-27   BJL   moved VIVO and CALS-specific markup to VIVO clone
     * 2006-01-31   BJL   edited to remove deprecated markup
     * 2005-11-06   JCR   put styling on extra search selection box
     * 2005-10-25   JCR   changed local ALL CALS RESEARCH constant to appBean.getSharedPortalFlagNumeric()
     * 2005-10-11   JCR   tweaks to VIVO search label spacing in header
     * 2005-09-15 JCR,BDC converted to use revised ApplicationBean and PortalBean
     * 2005-08-16   JCR   added CALS_IMPACT contant and modified code to use CALS display for that portal
     * 2005-08-01   JCR   changed ordering of other portals being displayed to displayRank instead of appName (affects SGER, CALS portals)
     * 2005-07-05   JCR   retrieving ONLY_CURRENT and ONLY_PUBLIC from database and setting in ApplicationBean
     * 2005-06-20   JCR   enabling a common CALS research portal via ALL CALS RESEARCH
     * 2005-06-20   JCR   removed MIN_STATUS_ID and minstatus parameter from search -- has been changed to interactive-only maxstatus parameter
     * JCR 2005-06-14 : added isInitialized() test for appBean and portalBean
     */
     
     HttpSession currentSession = request.getSession();
     String currentSessionIdStr = currentSession.getId();
     int securityLevel = -1;
     String loginName = null;
     if (loginHandler.testSessionLevel(request) > -1) {
         securityLevel = Integer.parseInt(loginHandler.getLoginRole());
         loginName = loginHandler.getLoginName();
     }
     
      final Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.web.menu.jsp");

      VitroRequest vreq = new VitroRequest(request);
      Portal portal = vreq.getPortal();

      int portalId = -1;
      if (portal==null) {
      log.error("Attribute 'portalBean' missing or null; portalId defaulted to 1");
      portalId=1;
      } else {
      portalId=portal.getPortalId();
      }
      String fixedTabStr=(fixedTabStr=request.getParameter("fixed"))==null?null:fixedTabStr.equals("")?null:fixedTabStr;

%>
<c:set var='context' value="<%=vreq.getContextPath()%>" />
<c:set var='themePath'>
  <c:if test="${!empty context && context != ''}">/${context}</c:if>/<%=portal.getThemeDir()%>
</c:set>
<c:set var='themeDir'><c:out value='${themePath}' default='/themes/vivo-basic/' /></c:set>
<c:set var="currentPortal" value="<%=portal.getPortalId()%>"/>

<%
String homeURL = (portal.getRootBreadCrumbURL()!=null && portal.getRootBreadCrumbURL().length()>0) ?
portal.getRootBreadCrumbURL() : request.getContextPath()+"/";
%>

<!-- uncomment this div to place your institutional branding/identity at the top of every page
<div id="institution">
  
</div>
-->

<div id="identity">
  
  <h1><a title="Home" href="<%=homeURL%>"><%out.print(portal.getAppName());%></a></h1>
  <% if (portal.getShortHand() != null) { %>
    <em><%out.print(portal.getShortHand());%></em>
  <% } %>
   
  <ul id="otherMenu">
  
  <%-- A user is logged in --%>
    <% if (securityLevel > 0) { %>

      <c:url var="logoutHref" value="<%= Controllers.LOGOUT_JSP %>">
        <c:param name="home" value="${currentPortal}" />
        <c:param name="loginSubmitMode" value="Log Out" /> 
      </c:url>
  
      <c:url var="siteAdminHref" value="<%= Controllers.SITE_ADMIN %>">
        <c:param name="home" value="${currentPortal}" />
      </c:url>
 
      <li class="border">
        Logged in as <strong><%= loginName %></strong> (<a href="${logoutHref}">Log out</a>)     
      </li>
      
      <li class="border"><a href="${siteAdminHref}" >Site Admin</a></li>
       
    <%-- A user is not logged in --%>
    <% } else { %>
  
      <c:url var="loginHref" value="<%= Controllers.LOGIN %>">
        <c:param name="home" value="${currentPortal}"/>
        <c:param name="login" value="block"/>
      </c:url>
    
      <li class="border"><a title="log in to manage this site" href="${loginHref}">Log in</a></li>
    <% } %>

    <c:url var="aboutHref" value="<%= Controllers.ABOUT %>">
      <c:param name="home" value="${currentPortal}"/>
    </c:url>
    <c:set var="aboutHref">
      <c:out value="${aboutHref}" escapeXml="true"/>
    </c:set>
  
    <li class="border"><a href="${aboutHref}" title="more about this web site">About</a></li>
    <li><a href='<c:url value="/comments"><c:param name="home" value="${currentPortal}"/></c:url>'>Contact Us</a></li>
  </ul>

</div><!-- end identity -->
