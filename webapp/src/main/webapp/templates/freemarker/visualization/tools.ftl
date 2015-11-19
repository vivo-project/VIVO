<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign shortVisualizationURLRoot ="/vis">
<#assign refreshCacheURL = "${urls.base}${shortVisualizationURLRoot}/refresh-cache">

<h2>${i18n().visualization_tools}</h2>

<a href="${refreshCacheURL}">${i18n().refresh_cached_vis_models}</a> 
<section class="visualizationTools">
<h3>${i18n().why_needed}</h3>
<p>${i18n().vis_tools_note_one}</p>

<h3>${i18n().vis_caching_process}</h3>
<p>${i18n().vis_tools_note_two}</p>

<p>${i18n().vis_tools_note_three}</p>

<p>${i18n().vis_tools_note_four}</p>
</section>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/visualization/visualization.css" />')}
