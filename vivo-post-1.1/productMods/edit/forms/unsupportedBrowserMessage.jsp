<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<c:set var="portal" value="${requestScope.portalBean}"/>
<c:set var="contextPath"><c:out value="${pageContext.request.contextPath}" /></c:set>
<c:set var="themeDir" value="${contextPath}/${portal.themeDir}"/>

<div id="ie67DisableWrapper">
	<div id="ie67DisableContent">
		<img src="${themeDir}site_icons/iconAlertBig.png" alt="Alert Icon"/>
		<p>This form is not supported in versions of Internet Explorer below version 8. Please upgrade your browser, or
		switch to another browser, such as FireFox.</p>
	</div>
</div>