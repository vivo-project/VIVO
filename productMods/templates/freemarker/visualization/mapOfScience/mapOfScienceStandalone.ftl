<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->


<#include "mapOfScienceSetup.ftl">

${scripts.add('<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.blockUI.min.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.dataTables.min.js"></script>', 
			  '<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ClassExtendUtils.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/DownloadManager.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/GMapAPI.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ColorStrategy.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/SizeCoding.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/CustomScimaps.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/Polygon.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/CustomMarker.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/MarkerManager.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/VisModeControllers.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/VisCommonControl.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/InitializeMap.js"></script>')}              

<#-- CSS files -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />')}

<div id="map_area" style="width: 640px; height: 480px;"></div> 