<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">

<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI?url}">
<#assign temporalGraphURL = '${urls.base}${standardVisualizationURLRoot}?vis=${otherVisType}&uri=${organizationURI}&labelField=label'>

<div id="error-body">

${organizationLabel}
	<p>Visit the Temporal Graph for <a href="${temporalGraphURL}">${textForOtherEntityComparisonType}</a> of the Organization.</p>
	<p>This Organization has neither Sub-Organizations nor People with ${textForCurrentEntityComparisonType} in VIVO. Please visit the 
	Organization's <a href="${organizationVivoProfileURL}">profile page.</a></p>
</div>