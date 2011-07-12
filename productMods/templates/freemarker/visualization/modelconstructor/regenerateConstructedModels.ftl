<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->


<#if (currentModels?size > 0)>

	
	Below cached models will be regenerated.<hr />

	<ul>
	<#list currentModels as model>
		<li>
		
		<#if model.uri == "">
			
			URI Independent Model
		
		<#else>
		
			<a href="${urls.base}/individual?uri=${model.uri?url}">${model.individualLabel}</a>
			
		</#if>
		<span class="display-title">${model.humanReadableType}</span> 
		
		</li>
	</#list>
	</ul>

<#else>

	Currently there are no constructed models for use by visualization.

</#if>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/search.css" />')}

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">
<#assign shortVisualizationURLRoot ="/vis">

<#assign refreshCacheVisTypeParam = "refresh-cache">

<#assign regenerateCacheURL = "${urls.base}${ajaxVisualizationURLRoot}?vis=${refreshCacheVisTypeParam}">

<script language="JavaScript" type="text/javascript">

$(document).ready(function() {
    	$.ajax({
            url: "${regenerateCacheURL}",
            dataType: "json",
            timeout: 5 * 60 * 1000
        });
});


</script>