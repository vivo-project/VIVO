<#-- $This file is distributed under the terms of the license in LICENSE$ -->

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

<#assign publicationParameter = {   "name": "publication",
                                    "pluralName": "${i18n().publications?js_string}",
                                    "verbName": "${i18n().published?js_string}",
                                    "dropDownText": "${i18n().by_publications?js_string}",
                                    "viewLink": "${organizationPublicationTemporalGraphURL}",
                                    "viewBaseLink": "${subOrganizationPublicationTemporalGraphCommonURL}",
                                    "dataLink": "${organizationPublicationTemporalGraphDataURL}",
                                    "csvLink": "${temporalGraphDownloadCSVCommonURL}&vis=entity_comparison",
                                    "value": "${i18n().publications?js_string}" }>

<#assign grantParameter = {   "name": "grant",
                              "pluralName": "${i18n().grants?js_string}",
                              "verbName": "${i18n().granted?js_string}",
                              "dropDownText": "${i18n().by_grants?js_string}",
                              "viewLink": "${organizationGrantTemporalGraphURL}",
                              "viewBaseLink": "${subOrganizationGrantTemporalGraphCommonURL}",
                              "dataLink": "${organizationGrantTemporalGraphDataURL}",
                              "csvLink": "${temporalGraphDownloadCSVCommonURL}&vis=entity_grant_count",
                              "value": "${i18n().grants?js_string}" }>

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
    singular: '${i18n().activity?js_string}',
    plural: '${i18n().activities?js_string}'
};
var i18nStringsGuiEvents = {
    temporalGraphCapped: '${i18n().temporal_graph_capitalized?js_string?js_string}',
    temporalGraphLower: '${i18n().temporal_graph?js_string?js_string}',
    viewString: '${i18n().view?js_string}',
    entityMaxNote: '${i18n().max_entity_note?js_string}',
    organizationsCappedString: '${i18n().organizations_capitalized?js_string}',
    peopleCappedString: '${i18n().people_capitalized?js_string}',
    organizationsAndPeople: '${i18n().organizations_and_people?js_string}',
    loadingDataFor: '${i18n().loading_data_for?js_string}',
    dataForString: '${i18n().data_for?js_string}',
    refreshingDataMsg: '${i18n().refreshing_data_message?js_string}',
    disclaimerTextOne: '${i18n().disclaimer_text_one?js_string}',
    disclaimerTextTwo: '${i18n().disclaimer_text_two?js_string}',
    levelUndefinedError: '${i18n().level_undefined_error?js_string}',
    sundayString: '${i18n().sunday?js_string}',
    mondayString: '${i18n().monday?js_string}',
    tuesdayString: '${i18n().tuesday?js_string}',
    wednesdayString: '${i18n().wednesday?js_string}',
    thursdayString: '${i18n().thursday?js_string}',
    fridayString: '${i18n().friday?js_string}',
    saturdayString: '${i18n().saturday?js_string}',
    januaryString: '${i18n().january?js_string}',
    februaryString: '${i18n().february?js_string}',
    marchString: '${i18n().march?js_string}',
    aprilString: '${i18n().april?js_string}',
    mayString: '${i18n().may?js_string}',
    juneString: '${i18n().june?js_string}',
    julyString: '${i18n().july?js_string}',
    augustString: '${i18n().august?js_string}',
    septemberString: '${i18n().september?js_string}',
    octoberString: '${i18n().october?js_string}',
    novemberString: '${i18n().november?js_string}'
};
var i18nStringsUtil = {
    firstString: '${i18n().vis_first_link?js_string}',
    lastString: '${i18n().vis_last_link?js_string}',
    previousString: '${i18n().vis_previous_link?js_string}',
    nextString: '${i18n().vis_next_link?js_string}',
    totalNumberOf: '${i18n().total_number_of?js_string}',
    numberOf: '${i18n().number_of?js_string}',
    withUnknownYear: '${i18n().with_unknown_year?js_string}',
    withKnownYear: '${i18n().with_known_year?js_string}',
    fromIncompleteYear: '${i18n().from_current_incomplete_year?js_string}',
    ofString: '${i18n().of?js_string}',
    inCompletedYear: '${i18n().in_completed_year?js_string}',
    haveAnUnknown: '${i18n().have_an_unknown?js_string}',
    yearNotChartered: '${i18n().year_not_chartered?js_string}',
    wereString: '${i18n().were?js_string}',
    inIncompleteYear: '${i18n().in_current_incomplete_year?js_string}',
    byPublications: '${i18n().by_publications?js_string}',
    publicationCount: '${i18n().publication_count?js_string}',
    grantCount: '${i18n().grant_count?js_string}',
    entityLabel: '${i18n().entity_label?js_string}',
    entityType: '${i18n().entity_type?js_string}',
    noMatchingEntities: '${i18n().no_matching_entities_found?js_string}',
    clerSearchQuery: '${i18n().clear_search_query?js_string}',
    shortMaxEntityNote: '${i18n().short_max_entity_note?js_string}',
    informationString: '${i18n().information_capitalized?js_string}',
    recordsStartEndOfTotal: '${i18n().vis_records_start_end_of_total?js_string}',
    searchButton: '${i18n().search_button?js_string}',
    entityTypeString: '${i18n().entity_type?js_string}'
};
</script>

${scripts.add('<!--[if IE]><script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/r293/excanvas.min.js"></script><![endif]-->',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/r293/jquery.flot.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/jquery-jangle.js"></script>',
              '<script type="text/javascript" src="${urls.base}/webjars/jquery-ui/jquery-ui.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.blockUI.js"></script>',
              '<script type="text/javascript" src="${urls.base}/webjars/datatables/js/dataTables.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/util.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/constants.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.notify.min.js"></script>')}

<#-- CSS files -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/webjars/jquery-ui-themes/smoothness/jquery-ui.min.css" />',
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
var organizationLabel = '${organizationLabel?js_string}';
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
