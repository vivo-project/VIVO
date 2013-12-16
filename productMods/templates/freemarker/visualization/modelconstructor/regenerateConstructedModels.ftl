<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->


<#if (currentModels?size > 0)>

	
	${i18n().cached_models_regenerated}<hr />

	<ul>
	<#list currentModels as model>
		<li>
		
		<#if model.uri == "">
			
			${i18n().uri_independent_model}
		
		<#else>
		
			<a href="${urls.base}/individual?uri=${model.uri?url}" title="${i18n().label}">${model.individualLabel}</a>
			
		</#if>
		<span class="display-title">${model.humanReadableType}</span> 
		
		</li>
	</#list>
	</ul>

<#else>

	${i18n().currently_no_constructed_models}

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