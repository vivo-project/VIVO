<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.TabMenu" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.BreadCrumbsUtil" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.TabWebUtil" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.PortalWebUtil" %>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session" />

<%
    /***********************************************
     Make the Tab menu list and search block

     mw542 2009-04-24 moved search from identity.jsp, updated with new code from bdc34
     bdc34 2006-01-03 created
     **********************************************/
     final Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.web.menu.jsp");
     
     Portal portal = (Portal)request.getAttribute("portalBean");
     int portalId = -1;
     if (portal==null) {
     	portalId=1;
     } else {
     	portalId=portal.getPortalId();
     }

     VitroRequest vreq = new VitroRequest(request);
     
     List primaryTabs = vreq.getWebappDaoFactory().getTabDao().getPrimaryTabs(portalId);
     request.setAttribute("primaryTabs", primaryTabs);
     
     int tabId = TabWebUtil.getTabIdFromRequest(vreq); 
     int rootId = TabWebUtil.getRootTabId(vreq); 
     List tabLevels = vreq.getWebappDaoFactory().getTabDao().getTabHierarcy(tabId,rootId);
     request.setAttribute("tabLevels", tabLevels);

     String uri = (String)request.getAttribute("javax.servlet.forward.request_uri");
     if(uri != null){
    	 request.setAttribute("indexClass", uri.endsWith("browsecontroller") ? "class=\"activeTab\"" : "");
         request.setAttribute("indexJspClass", uri.endsWith("browsecontroller-jsp") ? "class=\"activeTab\"" : "");         
         request.setAttribute("indexStringTemplateClass", uri.endsWith("browsecontroller-stringtemplate") ? "class=\"activeTab\"" : "");
         request.setAttribute("indexStringTemplateGroupFileClass", uri.endsWith("browsecontroller-stringtemplategroupfile") ? "class=\"activeTab\"" : "");
         request.setAttribute("indexFreeMarkerClass", uri.endsWith("browsecontroller-freemarker") ? "class=\"activeTab\"" : "");
         request.setAttribute("indexVelocityClass", uri.endsWith("browsecontroller-velocity") ? "class=\"activeTab\"" : "");
         request.setAttribute("indexWicketClass", uri.endsWith("browsecontroller-wicket") ? "class=\"activeTab\"" : "");

         if ( uri.indexOf("about") > 0) {
           request.setAttribute("aboutClass","class=\"activeTab\"");
         }
         if ( uri.indexOf("comments") > 0) {
           request.setAttribute("commentsClass","class=\"activeTab\"");
         }
     }
     
   // application variables not stored in application bean
     final String DEFAULT_SEARCH_METHOD = "fulltext";
     final int FILTER_SECURITY_LEVEL = 4;
     final int VIVO_SEARCHBOX_SIZE = 20;
     
     ApplicationBean appBean = vreq.getAppBean();
     PortalWebUtil.populateSearchOptions(portal, appBean, vreq.getWebappDaoFactory().getPortalDao());
     PortalWebUtil.populateNavigationChoices(portal, request, appBean, vreq.getWebappDaoFactory().getPortalDao());
     
     HttpSession currentSession = request.getSession();
     String currentSessionIdStr = currentSession.getId();
     int securityLevel = -1;
     String loginName = null;
     if (loginHandler.testSessionLevel(request) > -1) {
         securityLevel = Integer.parseInt(loginHandler.getLoginRole());
         loginName = loginHandler.getLoginName();
     }

%>
<c:set var="uri" value="<%= uri %>" />
<c:set var="themeDir">
  <c:out value="<%=portal.getThemeDir()%>" default="themes/vivo-basic" />
</c:set>
<c:url var="themePath" value="/${themeDir}" />
<c:url var="searchURL" value="/search"/>
<c:set var="currentPortal" value="<%=portal.getPortalId()%>"/>
<c:set var="rootTab" value="<%=rootId%>"/>


<!-- ************** START menu.jsp ************** -->
<div id="navAndSearch" class="block">
  <div id="primaryAndOther">
    <ul id="primary">
      <c:forEach items="${primaryTabs}" var="tab">
        <li>
          <c:remove var="activeClass"/>
          <c:if test="${param.primary==tab.tabId}">
            <c:set var="activeClass"> class="activeTab" </c:set>
          </c:if>
          <c:forEach items="${tabLevels}" var="subTab">
            <c:if test="${subTab==tab.tabId && subTab != rootTab}">
              <c:set var="activeClass"> class="activeTab" </c:set>
            </c:if>
          </c:forEach>
          
          <c:url var="tabHref" value="/index.jsp"><c:param name="primary" value="${tab.tabId}"/></c:url>
          <a ${activeClass} href="${tabHref}">
             <c:out value="${tab.title}"/></a>
        </li>
      </c:forEach>
      <li>
         <a ${indexClass} href="<c:url value="/browsecontroller"/>"
            title="list all contents by type">
            Index</a>
      </li>
      <li>
         <a ${indexJspClass} href="<c:url value="/browsecontroller-jsp"/>"
            title="list all contents by type">
            Index - JSP</a>
      </li>
      <li>
         <a ${indexStringTemplateClass} href="<c:url value="/browsecontroller-stringtemplate"/>"
            title="list all contents by type">
            Index - ST</a>
      </li>
      <%-- 
      <li>
         <a ${indexStringTemplateClass} href="<c:url value="/browsecontroller-stringtemplategroupfile"/>"
            title="list all contents by type">
            Index - STGF</a>
      </li>
      --%>
      <li>
         <a ${indexVelocityClass} href="<c:url value="/browsecontroller-velocity"/>"
            title="list all contents by type">
            Index - Velocity</a>
      </li>
      <li>
         <a ${indexFreeMarkerClass} href="<c:url value="/browsecontroller-freemarker"/>"
            title="list all contents by type">
            Index - FM</a>
      </li>
      <li>
         <a ${indexWicketClass} href="<c:url value="/browsecontroller-wicket"/>"
            title="list all contents by type">
            Index - Wicket</a>
      </li>
    </ul>
  
  </div><!--END 'primaryAndOther'-->
  
  <%-- TabMenu.getSecondaryTabMenu(vreq) --%> 


  <%------------- Search Form -------------%>
  <div id="searchBlock">
    <form id="searchForm" action="${searchURL}" >                	
      <label for="search">Search </label>
      <%  if (securityLevel>=FILTER_SECURITY_LEVEL && appBean.isFlag1Active()) { %>
      <select id="search-form-modifier" name="flag1" class="form-item" >
        <option value="nofiltering" selected="selected">entire database (<%=loginName%>)</option>
      	<option value="${currentPortal}"><%=portal.getShortHand()%></option>
      </select>
      <%  } else {%>
      <input type="hidden" name="flag1" value="${currentPortal}" />
      <%  } %>
      <input type="text" name="querytext" id="search" class="search-form-item" value="<c:out value="${requestScope.querytext}"/>" size="<%=VIVO_SEARCHBOX_SIZE%>" />
    	<input class="search-form-submit" name="submit" type="submit"  value="Search" />
  	</form>
  </div>

<%-- this div is needed for clearing floats --%>
<%-- <div class="clear"></div> --%>

</div><!-- END 'navigation' -->
<div id="breadcrumbs" class="small"><%=BreadCrumbsUtil.getBreadCrumbsDiv(request)%></div>


<!-- ************************ END menu.jsp ************************ -->


