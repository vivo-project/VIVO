<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">

<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI?url}">
<#assign temporalGraphURL = '${urls.base}${standardVisualizationURLRoot}?vis=${otherVisType}&uri=${organizationURI}&labelField=label'>

<h1 id="noPubsOrGrants-header">${organizationLabel}</h1>

    <h3>${textForCurrentEntityComparisonType?capitalize} Temporal Graph
        <span class="rdfLink">|&nbsp;<a href="${temporalGraphURL}">${textForOtherEntityComparisonType?capitalize} Temporal Graph</a></span>
    </h3>
    <div id="error-body">
	    <p>This organization has neither sub-organizations nor people with ${textForCurrentEntityComparisonType} in the system. Please visit the full ${organizationLabel} <a href="${organizationVivoProfileURL}">profile page</a> for a more complete overview.</p>
    </div>
