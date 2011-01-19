<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign organizationURI ="${organizationURI?url}">
<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">
<#assign visualizationType = "${visualization}">
<#assign TemporalGraphGrantsURL = '${urls.base}${standardVisualizationURLRoot}?vis=entity_grant_count&uri=${organizationURI}&labelField=label'>
<#assign TemporalGraphPubsURL = '${urls.base}${standardVisualizationURLRoot}?vis=entity_comparison&uri=${organizationURI}&labelField=label'>


<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
	
var contextPath = "${urls.base}";
var visualization = "${visualizationType}";
var temporalGraphGrantsURL = "${TemporalGraphGrantsURL}";
var temporalGraphPubsURL = "${TemporalGraphPubsURL}";
var visualizationURL = "", visualizationString = "";

</script>

<script type="text/javascript">

    $(document).ready(function() {
 
		if(visualization == "ENTITY_PUB_COUNT"){
			visualizationURL = temporalGraphGrantsURL;
			visualizationString = "grants";
		} else {
			visualizationURL = temporalGraphPubsURL;	
			visualizationString = "publications";
		}
		
		$("a#visualizationID").text(visualizationString);
		$("a#visualizationID").attr("href", visualizationURL);

    });
</script>

<div id="body">
	<p>Visit the Temporal Graph for <a id ="visualizationID" href="">grants</a> of the Organization</p>
	<p>This Organization has neither Sub-Organizations nor People. Please visit the Organization's <a href="${organizationVivoProfileURL}">profile page.</a></p>
</div>

