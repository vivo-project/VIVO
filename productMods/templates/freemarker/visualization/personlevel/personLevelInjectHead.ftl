<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign visualizationURLRoot ="/visualization">

<#assign egoURI ="${egoURIParam?url}">

<#assign egoCoAuthorshipDataFeederURL = '${urls.base}${visualizationURLRoot}?vis=coauthorship&uri=${egoURI}&render_mode=data&labelField=label'>

<#assign egoCoPIDataFeederURL = '${urls.base}${visualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&render_mode=data&labelField=label'>

<#assign egoCoAuthorsListDataFileURL = '${urls.base}${visualizationURLRoot}?vis=person_level&uri=${egoURI}&render_mode=data&vis_mode=coauthors'>

<#assign swfLink = '${urls.images}/visualization/coauthorship/EgoCentric.swf'>

<c:url var="adobeFlashDetector" value="/js/visualization/coauthorship/AC_OETags.js" />
<c:url var="googleVisualizationAPI" value="http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D"/>
<c:url var="coAuthorShipJavaScript" value="/js/visualization/personlevel/person_level.js" />
