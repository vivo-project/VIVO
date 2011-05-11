<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">
<#assign shortVisualizationURLRoot ="/vis">

<#assign organizationURI ="${organizationURI?url}">
<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">

<#assign subOrganizationVivoProfileURL = "${urls.base}/individual?">

<#assign subOrganizationMapOfScienceCommonURL = "${urls.base}${shortVisualizationURLRoot}/map-of-science/">


<#if organizationLocalName?has_content >
    
    <#assign organizationMapOfScienceURL = "${urls.base}${shortVisualizationURLRoot}/map-of-science/${organizationLocalName}">
    
<#else>

    <#assign organizationMapOfScienceURL = '${urls.base}${shortVisualizationURLRoot}/map-of-science/?uri=${organizationURI}'>

</#if>

<#assign organizationMapOfScienceDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=map-of-science&uri=${organizationURI}&vis_mode=json">