<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign organizationURI ="${organizationURI?url}">
<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">

<#assign subOrganizationVivoProfileURL = "${urls.base}/individual?">

<#assign subOrganizationGrantTemporalGraphCommonURL = "${urls.base}${standardVisualizationURLRoot}?vis=entity_grant_count">
<#assign subOrganizationPublicationTemporalGraphCommonURL = "${urls.base}${standardVisualizationURLRoot}?vis=entity_comparison">

<#assign organizationPublicationTemporalGraphURL = "${urls.base}${standardVisualizationURLRoot}?vis=entity_comparison&uri=${organizationURI}">
<#assign organizationGrantTemporalGraphURL = "${urls.base}${standardVisualizationURLRoot}?vis=entity_grant_count&uri=${organizationURI}">

<#assign temporalGraphSmallIcon = '${urls.images}/visualization/temporal_vis_small_icon.jpg'>

<#assign temporalGraphDownloadCSVCommonURL = '${urls.base}${dataVisualizationURLRoot}?uri=${organizationURI}&labelField=label'>

<#assign publicationParameter = {   "name": "publication",
                                    "dropDownText": "by Publications", 
                                    "viewLink": "${organizationPublicationTemporalGraphURL}", 
                                    "value": "Publications" }>

<#assign grantParameter = {   "name": "grant",
                              "dropDownText": "by Grants", 
                              "viewLink": "${organizationGrantTemporalGraphURL}", 
                              "value": "Grants" }>
                              
<#assign parameterOptions = [publicationParameter, grantParameter]>

<#assign entityCheckboxSelectorDOMClass = "entity-selector-checkbox">
                                    
<#-- Javascript files -->

<#-- Currently we are using the developer build version for both flot & excanvas libraries,
this is because IE 9 complains about certain properties. After testing it seems that dev 
build version is stable enough. If in next couple of days we feel that there are some issues
we will default to using the stable version unless the request comes from IE 9 in which case
we will use rev 293 (dev build version) of the flot & excanvas files.
-->
<#assign excanvas = '${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/r293/excanvas.min.js'>
<#assign flot = 'js/visualization/entitycomparison/jquery_plugins/flot/r293/jquery.flot.min.js'>


<#assign fliptext = 'js/visualization/entitycomparison/jquery_plugins/fliptext/jquery.mb.flipText.js'>
<#assign jqueryNotify = 'js/jquery_plugins/jquery.notify.min.js'>
<#assign jqueryBlockUI = 'js/jquery_plugins/jquery.blockUI.min.js'>
<#assign jqueryUI = 'js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js'>
<#assign datatable = 'js/jquery_plugins/jquery.dataTables.min.js'>
<#assign entityComparisonUtils = 'js/visualization/entitycomparison/util.js'>
<#assign entityComparisonConstants = 'js/visualization/entitycomparison/constants.js'>
<#assign guiEventManager = 'js/visualization/entitycomparison/gui-event-manager.js'>



<!--[if IE]><script type="text/javascript" src="${excanvas}"></script><![endif]-->
${scripts.add(flot)}
${scripts.add(fliptext)}
${scripts.add(jqueryBlockUI)}
${scripts.add(jqueryUI)}
${scripts.add(datatable)}
${scripts.add(entityComparisonUtils)}
${scripts.add(entityComparisonConstants)}
${scripts.add(jqueryNotify)}

<#-- CSS files -->

<#assign demoTable = "js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />
<#assign jqueryUIStyle = "js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />
<#assign jqueryNotifyStyle = "css/jquery_plugins/ui.notify.css" />
<#assign entityComparisonStyle = "css/visualization/entitycomparison/layout.css" />
<#assign entityComparisonStyleIEHack = "${urls.base}/css/visualization/entitycomparison/layout-ie.css" />
<#assign entityComparisonStyleIE_6_7_Hack = "${urls.base}/css/visualization/entitycomparison/layout-ie-67.css" />
<#assign vizStyle = "css/visualization/visualization.css" />

${stylesheets.add(jqueryUIStyle)}
${stylesheets.add(demoTable)}
${stylesheets.add(entityComparisonStyle)}
${stylesheets.add(vizStyle)}
${stylesheets.add(jqueryNotifyStyle)}
<!--[if IE]><link href="${entityComparisonStyleIEHack}" rel="stylesheet" type="text/css" /><![endif]-->
<!--[if lt IE 8]><link href="${entityComparisonStyleIE_6_7_Hack}" rel="stylesheet" type="text/css" /><![endif]-->

<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
    
var contextPath = "${urls.base}";
var temporalGraphSmallIcon = "${temporalGraphSmallIcon}";
var subOrganizationVivoProfileURL = "${subOrganizationVivoProfileURL}";

var jsonString = '${jsonContent!}';
var organizationLabel = '${organizationLabel}';
var organizationVIVOProfileURL = "${organizationVivoProfileURL}";

var loadingImageLink = contextPath + "/images/visualization/ajax-loader-indicator.gif";

var entityCheckboxSelectorDOMClass = "${entityCheckboxSelectorDOMClass}";

var temporalGraphProcessor;

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

${headScripts.add(guiEventManager)}