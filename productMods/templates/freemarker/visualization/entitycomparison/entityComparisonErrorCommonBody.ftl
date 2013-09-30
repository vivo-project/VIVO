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

    <h3 id="alternative-vis-info">${textForCurrentEntityComparisonType?capitalize} ${i18n().temporal_graph_capitalized} 
        <span id="noPubsOrGrants-span">|&nbsp;<a  href="${temporalGraphURL}" title="${i18n().view}">${i18n().view}&nbsp${textForOtherEntityComparisonType}&nbsp;${i18n().temporal_graph}</a></span>
    </h3>
    <div id="error-body">
        <p>${i18n().entity_comp_error_text1} 
        <span id="comparison-parameter-unavailable-label">${textForCurrentEntityComparisonType}</span> ${i18n().entity_comp_error_text2} 
        ${i18n().entity_comp_error_text3} ${organizationLabel} <a href="${organizationVivoProfileURL}" title="${i18n().profile_page}">${i18n().profile_page}</a> ${i18n().entity_comp_error_text4}</p>
    </div>

</div>