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

<#assign mapOfScienceIcon = '${urls.images}/visualization/mapofscience/vivo-scimap.png'>

<#assign entityMapOfScienceDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=json">
<#assign entityMapOfScienceDisciplineCSVURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=csv&vis_mode=discipline">
<#assign entityMapOfScienceSubDisciplineCSVURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=csv&vis_mode=subdiscipline">

<script language="JavaScript" type="text/javascript">

var contextPath = "${urls.base}";

var scienceMapDataURL = "${entityMapOfScienceDataURL}";

var baseImageFolderPrefix = "${urls.images}/";
var imageFolderPrefix = "${urls.images}/visualization/";
var mapOfScienceImageFolderPrefix  = imageFolderPrefix 
											+ "mapofscience/";
var disciplineLabelImageUrlPrefix = mapOfScienceImageFolderPrefix + "labels/";
											
var entityLabel = '${entityLabel}';

var ENTITY_TYPE = '${entityType}';

var loadingImageLink = contextPath + "/images/visualization/ajax-loader-indicator.gif";
var refreshPageImageLink = contextPath + "/images/visualization/refresh-green.png";
											
											
var entityMapOfScienceDisciplineCSVURL = "${entityMapOfScienceDisciplineCSVURL}";
var entityMapOfScienceSubDisciplineCSVURL = "${entityMapOfScienceSubDisciplineCSVURL}";											

</script>

${scripts.add('<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.blockUI.min.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/datatable/1.7.6/jquery.dataTables.min.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/dataTables.helper.js"></script>', 
			  '<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.notify.min.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ClassExtendUtils.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ErrorDisplayWidget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/DownloadManager.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/NumberUtils.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/Tooltip.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/GMapAPI.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ControlPanel.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ColorStrategy.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/SizeCoding.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/CustomScimaps.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/Marker.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/Polygon.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/CustomMarker.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/MarkerManager.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ScimapWidget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/DataTableWidget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/VisModeControllers.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/VisCommonControl.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/InitializeMap.js"></script>')}              

<#-- CSS files -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/visualization/mapofscience/layout.css" />')}