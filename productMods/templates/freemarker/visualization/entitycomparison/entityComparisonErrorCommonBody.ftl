<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign shortVisualizationURLRoot ="/vis">

<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">

<#if organizationLocalName?has_content >
    <#assign temporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/${otherVisType}/${organizationLocalName}'>
<#else>
    <#assign temporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/${otherVisType}/?uri=${organizationURI}'>
</#if>

<div id="error-container">

<h1 id="noPubsOrGrants-header">${organizationLabel}</h1>

    <h3 id="alternative-vis-info">${textForCurrentEntityComparisonType?capitalize} Temporal Graph 
        <span id="noPubsOrGrants-span">|&nbsp;<a  href="${temporalGraphURL}">view ${textForOtherEntityComparisonType} temporal graph</a></span>
    </h3>
    <div id="error-body">
        <p>This organization has neither sub-organizations nor people with 
        <span id="comparison-parameter-unavailable-label">${textForCurrentEntityComparisonType}</span> in the system. 
        Please visit the full ${organizationLabel} <a href="${organizationVivoProfileURL}">profile page</a> for a more complete overview.</p>
    </div>

</div>