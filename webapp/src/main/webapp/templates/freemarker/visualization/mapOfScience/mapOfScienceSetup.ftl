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

<#assign mapOfScienceIcon = '${urls.images}/visualization/mapofscience/scimap_icon.png'>

<#assign entityMapOfScienceDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=json">

<#assign entityMapOfScienceDisciplineCSVURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=csv&vis_mode=discipline">
<#assign entityMapOfScienceSubDisciplineCSVURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=csv&vis_mode=subdiscipline">
<#assign entityMapOfScienceUnlocatedJournalsCSVURL = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&uri=${entityURI}&output=csv&vis_mode=unlocated_journals">

<script language="JavaScript" type="text/javascript">

var entityVivoProfileURLPrefix = "${urls.base}/individual?uri=";
var entityMapOfScienceURLPrefix = "${urls.base}${shortVisualizationURLRoot}/${mapOfScienceVisParam}?uri=";
var contextPath = "${urls.base}";
var scienceMapDataPrefix = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&output=json&uri=";
var scienceMapDataURL = scienceMapDataPrefix + "${entityURI}";
var entityUnmappedJournalsCSVURLPrefix = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&output=csv&vis_mode=unlocated_journals&uri=";

var imageFolderPrefix = "${urls.images}/visualization/";
var mapOfScienceImageFolderPrefix  = imageFolderPrefix + "mapofscience/";
var disciplineLabelImageUrlPrefix = mapOfScienceImageFolderPrefix + "labels/";
var disciplineBlackLabelImageUrlPrefix = disciplineLabelImageUrlPrefix + "black/";

var infoIconUrl = "${urls.images}/iconInfo.png";
var drillDownIconUrl = "${urls.images}/visualization/drill_down_icon.png";
											
var entityLabel = '${entityLabel}';

var ENTITY_TYPE = '${entityType}';
<#if entityType == "PERSON" >
	<#assign viewTypeFilterDisplay = "none">
<#else>
	<#assign viewTypeFilterDisplay = "block">
</#if>

var loadingImageLink = contextPath + "/images/visualization/ajax-loader-indicator.gif";
var refreshPageImageLink = contextPath + "/images/visualization/refresh-green.png";
											
var comparisonScienceMapCsvDataUrlPrefix = "${urls.base}${dataVisualizationURLRoot}?labelField=label&vis=entity_comparison&uri=";
var entityMapOfScienceSubDisciplineCSVURLPrefix = "${urls.base}${dataVisualizationURLRoot}?vis=${mapOfScienceVisParam}&output=csv&vis_mode=subdiscipline&&uri="
var entityMapOfScienceDisciplineCSVURL = "${entityMapOfScienceDisciplineCSVURL}";
var entityMapOfScienceSubDisciplineCSVURL = "${entityMapOfScienceSubDisciplineCSVURL}";											
var i18nStrings = {
    firstString: '${i18n().vis_first_link}',
    lastString: '${i18n().vis_last_link}',
    previousString: '${i18n().vis_previous_link}',
    nextString: '${i18n().vis_next_link}',
    organizationsString: '${i18n().organizations_capitalized}',
    peopleString: '${i18n().people_capitalized}',
    infoIconString: '${i18n().info_icon}',
    entityTypeString: '${i18n().entity_type}',
    organizationString: '${i18n().organization_capitalized}',
    personString: '${i18n().person_capitalized}',
    numberOfPubs: '${i18n().of_pubs}',
    maxNbrForComp: '${i18n().max_nbr_for_comp}',
    noMatchingScienceAreas: '${i18n().no_matching_science_areas}',
    clearSearchQuery: '${i18n().clear_search_query}',
    saveAllAsCSV: '${i18n().save_all_as_csv}',
    drillDownString: '${i18n().drill_down}',
    subdisciplinesString: '${i18n().subdisciplines}',
    disciplinesString: '${i18n().disciplines}',
    scienceAreaLevel: '${i18n().science_area_level}',
    ofActivityString: '${i18n().of_activity}',
    subdisciplinesLower: '${i18n().subdisciplines_lower}',
    disciplinesLower: '${i18n().disciplines_lower}',
    showDisciplineLabels: '${i18n().show_discipline_labels}',
    noAttributedPubs: '${i18n().no_attributed_publications}',
    noneOfThe: '${i18n().none_of_the}',
    pubsAttributedTo: '${i18n().publications_attributed_to}',
    beenScienceLocated: '${i18n().been_science_located}',
    pubAttributedTo: '${i18n().publication_attributed_to}',
    notScienceLocated: '${i18n().not_science_located}',
    publicationsString: '${i18n().publications}',
    publicationString: '${i18n().publication}',
    noJournalInformation: '${i18n().no_journal_information}',
    noMatchingMapLocation: '${i18n().no_matching_map_location}',
    loadingDataFor: '${i18n().loading_data_for}',
    mapBeingRefreshed: '${i18n().map_being_refreshed_msg}',
    publicationsPubs: '${i18n().publication_pubs}',
    percentActivity: '${i18n().percent_activity}'
};
</script>

<#if googleMapsKey??>
    ${scripts.add('<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${googleMapsKey}"></script>')}
<#else>
    ${scripts.add('<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>')}
</#if>
${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
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
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ComparisonScimapWidget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/SimpleDataTableWidget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/EntityTablesWidget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/ComparisonDataTableWidget.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/VisModeControllers.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/VisCommonControl.js"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/visualization/mapofscience/InitializeMap.js"></script>')}              

<#-- CSS files -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/visualization/mapofscience/layout.css" />')}

