<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualizationfm">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">
<#assign egoURI ="${egoURIParam?url}">
<#assign egoCoAuthorshipDataFeederURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&labelField=label'>
<#assign egoCoPIDataFeederURL = '${urls.base}${dataVisualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&labelField=label'>
<#assign egoCoAuthorsListDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=person_level&uri=${egoURI}&vis_mode=coauthors'>
<#assign swfLink = '${urls.images}/visualization/coauthorship/EgoCentric.swf'>
<#assign adobeFlashDetector = '${urls.base}/js/visualization/coauthorship/AC_OETags.js'>
<#assign googleVisualizationAPI = 'http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
<#assign coAuthorShipJavaScript = '${urls.base}/js/visualization/personlevel/person_level.js'>


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
var egoURI = "${egoURI}";
var egoCoAuthorshipDataFeederURL = "${egoCoAuthorshipDataFeederURL}";
var egoCoAuthorsListDataFileURL = "${egoCoAuthorsListDataFileURL}";
var egoCoPIDataFeederURL = "${egoCoPIDataFeederURL}";
var domainParam = "${completeURL}";
var visMode = "${visMode}";

// -->
</script>

<script type="text/javascript" src="${coAuthorShipJavaScript}"></script>


<c:url var="coAuthorStyle" value="/${themeDir}css/visualization/personlevel/coauthor_style.css" />
<c:url var="pageStyle" value="/${themeDir}css/visualization/personlevel/page.css" />
<c:url var="vizStyle" value="/${themeDir}css/visualization/visualization.css" />

<link href="${coAuthorStyle}" rel="stylesheet" type="text/css" />
<link href="${pageStyle}" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${vizStyle}" />