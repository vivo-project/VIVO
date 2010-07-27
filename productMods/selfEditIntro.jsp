<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.*" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<c:set var="portal" value="${requestScope.portalBean}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">	
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
   <link rel="stylesheet" type="text/css" href="<c:url value="${themeDir}css/screen.css"/>" media="screen"/>

   <c:out value="${requestScope.css}" escapeXml="false"/>
   <title>Edit Your Profile</title>
   
</head>
<body>
<div id="wrap">
	<jsp:include page="/${themeDir}jsp/identity.jsp" flush="true"/>
	<div id="contentwrap">
		<jsp:include page="/${themeDir}jsp/menu.jsp" flush="true"/>
		<div id="content" class="full">
            <div align="center">
                If you are a member of the Cornell community and would like to edit you profile
                in the Vivo system please login using your netId.
            </div>
            <div align="center">
<c:url value="/edit/login.jsp" var="loginUrl"/>	
                <button type="button" onclick="javascript:document.location.href='${loginUrl}'">Login</button>
            </div>

        </div>
		<!-- END div 'content' -->
	</div><!-- END div 'contentwrap' -->
	<jsp:include page="/${themeDir}jsp/footer.jsp" flush="true"/>
</div><!-- END div 'wrap' -->
</body>
</html>
