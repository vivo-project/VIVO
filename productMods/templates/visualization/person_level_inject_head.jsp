<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir"><c:out value="${portalBean.themeDir}" /></c:set>
<c:set var="contextPath"><c:out value="${pageContext.request.contextPath}" /></c:set>

<c:url var="egoCoAuthorshipDataFeederURL" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship" />
	<c:param name="render_mode" value="data" />
	<c:param name="uri" value="${requestScope.egoURIParam}" />
	<c:param name="labelField" value="name" />
</c:url>

<c:url var="egoCoAuthorsListDataFileURL" value="/admin/visQuery">
	<c:param name="vis" value="person_level" />
	<c:param name="render_mode" value="data" />
	<c:param name="vis_mode" value="coauthors" />
	<c:param name="uri" value="${requestScope.egoURIParam}" />
</c:url>

<c:url var="swfLink" value="/${themeDir}site_icons/visualization/coauthorship/CoAuthor.swf" />

<c:url var="jquery" value="/js/jquery.js"/>
<c:url var="adobeFlashDetector" value="/js/visualization/coauthorship/AC_OETags.js" />
<c:url var="googleVisualizationAPI" value="http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D"/>
<c:url var="coAuthorShipJavaScript" value="/js/visualization/personlevel/person_level.js" />

<script type="text/javascript" src="${adobeFlashDetector}"></script>
<script type="text/javascript" src="${jquery}"></script>
<script type="text/javascript" src="${googleVisualizationAPI}"></script>

<script language="JavaScript" type="text/javascript">
<!--
// -----------------------------------------------------------------------------
// Globals
// Major version of Flash required
var requiredMajorVersion = 10;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 0;
// -----------------------------------------------------------------------------


var swfLink = "${swfLink}";
var egoURI = "${requestScope.egoURIParam}";
var egoCoAuthorshipDataFeederURL = "${egoCoAuthorshipDataFeederURL}";
var egoCoAuthorsListDataFileURL = "${egoCoAuthorsListDataFileURL}";
var contextPath = "${contextPath}";

// -->
</script>
<script type="text/javascript" src="${coAuthorShipJavaScript}"></script>


<c:url var="coAuthorStyle" value="/${themeDir}css/visualization/personlevel/coauthor_style.css" />
<c:url var="pageStyle" value="/${themeDir}css/visualization/personlevel/page.css" />
<c:url var="vizStyle" value="/${themeDir}css/visualization/visualization.css" />

<link href="${coAuthorStyle}" rel="stylesheet" type="text/css" />
<link href="${pageStyle}" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${vizStyle}" />