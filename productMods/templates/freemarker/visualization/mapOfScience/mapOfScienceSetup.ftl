<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">
<#assign shortVisualizationURLRoot ="/vis">

<#assign entityURI ="${entityURI?url}">
<#assign entityVivoProfileURL = "${urls.base}/individual?uri=${entityURI}">

<#assign mapOfScienceVisParam = "map-of-science">

<#assign subEntityVivoProfileURL = "${urls.base}/individual?">

<#assign subEntityMapOfScienceCommonURL = "${urls.base}${shortVisualizationURLRoot}/${mapOfScienceVisParam}/">


<#if entityLocalName?has_content >
    
    <#assign entityMapOfScienceURL = "${urls.base}${shortVisualizationURLRoot}/${mapOfScienceVisParam}/${entityLocalName}">
    
<#else>

    <#assign entityMapOfScienceURL = '${urls.base}${shortVisualizationURLRoot}/${mapOfScienceVisParam}/?uri=${entityURI}'>

</#if>

<#assign entityMapOfScienceDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=json">

<script language="JavaScript" type="text/javascript">

var scienceMapDataURL = "${entityMapOfScienceDataURL}";
var imageFolderPrefix = "${urls.images}/visualization/";
var mapOfScienceImageFolderPrefix = imageFolderPrefix 
											+ "mapofscience/";

</script>

