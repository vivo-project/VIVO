<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">
<#assign shortVisualizationURLRoot ="/vis">

<#assign organizationURI ="${organizationURI?url}">
<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">

<#assign subOrganizationVivoProfileURL = "${urls.base}/individual?">

<#assign subOrganizationGrantTemporalGraphCommonURL = "${urls.base}${shortVisualizationURLRoot}/grant-graph/">
<#assign subOrganizationPublicationTemporalGraphCommonURL = "${urls.base}${shortVisualizationURLRoot}/publication-graph/">


<#if organizationLocalName?has_content >
    
    <#assign organizationPublicationTemporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/publication-graph/${organizationLocalName}'>
    <#assign organizationGrantTemporalGraphURL = "${urls.base}${shortVisualizationURLRoot}/grant-graph/${organizationLocalName}">
    
<#else>

    <#assign organizationPublicationTemporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/publication-graph/?uri=${organizationURI}'>
    <#assign organizationGrantTemporalGraphURL = "${urls.base}${shortVisualizationURLRoot}/grant-graph/?uri=${organizationURI}">

</#if>

<#assign organizationPublicationTemporalGraphDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=entity_comparison&uri=${organizationURI}&vis_mode=json">
<#assign organizationGrantTemporalGraphDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=entity_grant_count&uri=${organizationURI}&vis_mode=json">

<#assign temporalGraphDrillUpIcon = '${urls.images}/visualization/temporalgraph/temporal-drill-up.png'>
<#assign temporalGraphDrillDownIcon = '${urls.images}/visualization/temporalgraph/temporal-drill-down.png'>

<#assign temporalGraphDownloadCSVCommonURL = '${urls.base}${dataVisualizationURLRoot}?uri=${organizationURI}&labelField=label'>

<#assign publicationParameter = {   "name": "${i18n().publication}",
                                    "pluralName": "${i18n().publications}",
                                    "verbName": "${i18n().published}",
                                    "dropDownText": "${i18n().by_publications}", 
                                    "viewLink": "${organizationPublicationTemporalGraphURL}",
                                    "viewBaseLink": "${subOrganizationPublicationTemporalGraphCommonURL}",
                                    "dataLink": "${organizationPublicationTemporalGraphDataURL}",
                                    "csvLink": "${temporalGraphDownloadCSVCommonURL}&vis=entity_comparison", 
                                    "value": "${i18n().publications}" }>
                                    
<#assign grantParameter = {   "name": "${i18n().grant}",
                              "pluralName": "${i18n().grants}",
                              "verbName": "${i18n().granted}",
                              "dropDownText": "${i18n().by_grants}", 
                              "viewLink": "${organizationGrantTemporalGraphURL}",
                              "viewBaseLink": "${subOrganizationGrantTemporalGraphCommonURL}", 
                              "dataLink": "${organizationGrantTemporalGraphDataURL}",
                              "csvLink": "${temporalGraphDownloadCSVCommonURL}&vis=entity_grant_count",
                              "value": "${i18n().grants}" }>
                              
<#assign parameterOptions = [publicationParameter, grantParameter]>

<#assign entityCheckboxSelectorDOMClass = "entity-selector-checkbox">
                                    
<#-- Javascript files -->

<#-- Currently we are using the developer build version for both flot & excanvas libraries,
this is because IE 9 complains about certain properties. After testing it seems that dev 
build version is stable enough. If in next couple of days we feel that there are some issues
we will default to using the stable version unless the request comes from IE 9 in which case
we will use rev 293 (dev build version) of the flot & excanvas files.
-->

<script language="JavaScript" type="text/javascript">

var activitiesLabel = {
    singular: '${i18n().activity}',
    plural: '${i18n().activities}'
};
var i18nStringsGuiEvents = {
    temporalGraphCapped: '${i18n().temporal_graph_capitalized}',
    temporalGraphLower: '${i18n().temporal_graph}',
    viewString: '${i18n().view}',
    entityMaxNote: '${i18n().max_entity_note}',
    organizationsCappedString: '${i18n().organizations_capitalized}',
    peopleCappedString: '${i18n().people_capitalized}',
    organizationsAndPeople: '${i18n().organizations_and_people}',
    loadingDataFor: '${i18n().loading_data_for}',
    dataForString: '${i18n().data_for}',
    refreshingDataMsg: '${i18n().refreshing_data_message}',
    disclaimerTextOne: '${i18n().disclaimer_text_one}',
    disclaimerTextTwo: '${i18n().disclaimer_text_two}',
    levelUndefinedError: '${i18n().level_undefined_error}',
    sundayString: '${i18n().sunday}',
    mondayString: '${i18n().monday}',
    tuesdayString: '${i18n().tuesday}',
    wednesdayString: '${i18n().wednesday}',
    thursdayString: '${i18n().thursday}',
    fridayString: '${i18n().friday}',
    saturdayString: '${i18n().saturday}',
    januaryString: '${i18n().january}',
    februaryString: '${i18n().february}',
    marchString: '${i18n().march}',
    aprilString: '${i18n().april}',
    mayString: '${i18n().may}',
    juneString: '${i18n().june}',
    julyString: '${i18n().july}',
    augustString: '${i18n().august}',
    septemberString: '${i18n().september}',
    octoberString: '${i18n().october}',
    novemberString: '${i18n().november}'
};
var i18nStringsUtil = {
    firstString: '${i18n().vis_first_link}',
    lastString: '${i18n().vis_last_link}',
    previousString: '${i18n().vis_previous_link}',
    nextString: '${i18n().vis_next_link}',
    totalNumberOf: '${i18n().total_number_of}',
    numberOf: '${i18n().number_of}',
    withUnknownYear: '${i18n().with_unknown_year}',
    withKnownYear: '${i18n().with_known_year}',
    fromIncompleteYear: '${i18n().from_current_incomplete_year}',
    ofString: '${i18n().of}',
    inCompletedYear: '${i18n().in_completed_year}',
    haveAnUnknown: '${i18n().have_an_unknown}',
    yearNotChartered: '${i18n().year_not_chartered}',
    wereString: '${i18n().were}',
    inIncompleteYear: '${i18n().in_current_incomplete_year}',
    byPublications: '${i18n().by_publications}',
    publicationCount: '${i18n().publication_count}',
    grantCount: '${i18n().grant_count}',
    entityLabel: '${i18n().entity_label}',
    entityType: '${i18n().entity_type}',
    noMatchingEntities: '${i18n().no_matching_entities_found}',
    clerSearchQuery: '${i18n().clear_search_query}',
    shortMaxEntityNote: '${i18n().short_max_entity_note}',
    informationString: '${i18n().information_capitalized}',
    entityTypeString: '${i18n().entity_type}'
};
</script>

${scripts.add('<!--[if IE]><script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/r293/excanvas.min.js"></script><![endif]-->',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/r293/jquery.flot.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/fliptext/jquery.mb.flipText.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.blockUI.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.dataTables.min.js"></script>', 
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/util.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/constants.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.notify.min.js"></script>')}              

<#-- CSS files -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/visualization/entitycomparison/layout.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/visualization/visualization.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/jquery_plugins/ui.notify.css" />',
                  '<!--[if IE]><link href="${urls.base}/css/visualization/entitycomparison/layout-ie.css" rel="stylesheet" type="text/css" /><![endif]-->',
                  '<!--[if lt IE 8]><link href="${urls.base}/css/visualization/entitycomparison/layout-ie-67.css" rel="stylesheet" type="text/css" /><![endif]-->')}                  
 
<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
    
var contextPath = "${urls.base}";
var temporalGraphDrillUpIcon = "${temporalGraphDrillUpIcon}";
var temporalGraphDrillDownIcon = "${temporalGraphDrillDownIcon}";
var subOrganizationVivoProfileURL = "${subOrganizationVivoProfileURL}";

var subOrganizationGrantTemporalGraphCommonURL = "${subOrganizationGrantTemporalGraphCommonURL}";
var subOrganizationPublicationTemporalGraphCommonURL = "${subOrganizationPublicationTemporalGraphCommonURL}";

var jsonString = '${jsonContent!}';
var vivoDefaultNamespace = '${vivoDefaultNamespace!}';
var organizationLabel = '${organizationLabel}';
var organizationVIVOProfileURL = "${organizationVivoProfileURL}";

var loadingImageLink = contextPath + "/images/visualization/ajax-loader-indicator.gif";
var refreshPageImageLink = contextPath + "/images/visualization/refresh-green.png";

var entityCheckboxSelectorDOMClass = "${entityCheckboxSelectorDOMClass}";

var isDataRequestSentViaAJAX = false;

var csvDownloadURL, temporalGraphProcessor;

/*
This has to be declared before making a call to GUI event manager JS.
*/
var COMPARISON_PARAMETERS_INFO = {

<#list parameterOptions as parameter>

    ${parameter.name}: {

    <#list parameter?keys as key>
        ${key}:"${parameter[key]}"<#if key_has_next>,</#if>
    </#list>
    
    }<#if parameter_has_next>,</#if>

</#list>
    
}

</script>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/gui-event-manager.js"></script>')}
