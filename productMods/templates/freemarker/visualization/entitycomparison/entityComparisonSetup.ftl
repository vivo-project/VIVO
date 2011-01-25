<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign organizationURI ="${organizationURI?url}">
<#assign jsonContent ="${jsonContent}">
<#assign organizationLabel = "${organizationLabel}">
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
                                    
<#-- Javascript files -->

<#assign excanvas = '${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/excanvas.js'>
<#assign flot = 'js/visualization/entitycomparison/jquery_plugins/flot/jquery.flot.js'>
<#assign fliptext = 'js/visualization/entitycomparison/jquery_plugins/fliptext/jquery.mb.flipText.js'>
<#assign jqueryNotify = 'js/jquery_plugins/jquery.notify.min.js'>
<#assign jqueryUI = 'js/jquery-ui/js/jquery-ui-1.8.4.custom.min.js'>
<#assign datatable = 'js/jquery_plugins/jquery.dataTables.min.js'>
<#assign entityComparisonUtils = 'js/visualization/entitycomparison/util.js'>
<#assign entityComparisonConstants = 'js/visualization/entitycomparison/constants.js'>
<#assign guiEventManager = 'js/visualization/entitycomparison/gui-event-manager.js'>


<!--[if IE]><script type="text/javascript" src="${excanvas}"></script><![endif]-->
${scripts.add(flot)}
${scripts.add(fliptext)}
${scripts.add(jqueryUI)}
${scripts.add(datatable)}
${scripts.add(entityComparisonUtils)}
${scripts.add(entityComparisonConstants)}
${scripts.add(jqueryNotify)}

<#-- CSS files -->

<#assign demoTable = "js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />
<#assign jqueryUIStyle = "js/jquery-ui/css/smoothness/jquery-ui-1.8.4.custom.css" />
<#assign jqueryNotifyStyle = "css/jquery_plugins/ui.notify.css" />
<#assign entityComparisonStyle = "css/visualization/entitycomparison/layout.css" />
<#assign entityComparisonStyleIEHack = "${urls.base}/css/visualization/entitycomparison/layout-ie.css" />
<#assign vizStyle = "css/visualization/visualization.css" />

${stylesheets.add(jqueryUIStyle)}
${stylesheets.add(demoTable)}
${stylesheets.add(entityComparisonStyle)}
${stylesheets.add(vizStyle)}
${stylesheets.add(jqueryNotifyStyle)}
<!--[if IE]><link href="${entityComparisonStyleIEHack}" rel="stylesheet" type="text/css" /><![endif]-->

<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
    
var contextPath = "${urls.base}";
var temporalGraphSmallIcon = "${temporalGraphSmallIcon}";
var subOrganizationVivoProfileURL = "${subOrganizationVivoProfileURL}";

var jsonString = '${jsonContent}';
var organizationLabel = '${organizationLabel}';
var organizationVIVOProfileURL = "${organizationVivoProfileURL}";

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

${scripts.add(guiEventManager)}