<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->

<#assign currentParameter = "grant">

<script language="JavaScript" type="text/javascript">

var currentParameter = "${currentParameter}";

</script>

<#include "entityComparisonSetup.ftl">

<#assign temporalGraphDownloadFileLink = '${temporalGraphDownloadCSVCommonURL}&vis=entity_grant_count'>
<#assign temporalGraphDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=entity_grant_count&uri=${organizationURI}&vis_mode=json">

<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
<!--

/*
This is used in util.js to print grant temporal graph links for all sub-organizations.
*/    
var temporalGraphCommonURL = subOrganizationGrantTemporalGraphCommonURL;

var temporalGraphDataURL = '${temporalGraphDataURL}';

$(document).ready(function () {

	options = {
		responseContainer: $("div#temporal-graph-response"),
		bodyContainer: $("#body"),
		errorContainer: $("#error-container"),
		dataURL: temporalGraphDataURL	
	};
	
	renderTemporalGraphVisualization(options);

});

// -->
</script>

<#assign currentParameterObject = grantParameter>

<div id="temporal-graph-response">

<#include "entityComparisonBody.ftl">

<#-- 
Right now we include the error message by default because currently I could not devise any more smarted solution. By default
the CSS of the #error-container is display:none; so it will be hidden unless explicitly commanded to be shown which we do in 
via JavaScript.
-->
<#include "entityGrantComparisonError.ftl">

</div>
