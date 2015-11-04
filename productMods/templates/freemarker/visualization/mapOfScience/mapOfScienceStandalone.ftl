<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->


<#include "mapOfScienceSetup.ftl">

<div id="map-of-science-response">
<#--
<div id="subject-parent-entity" class="hide-dom-on-init">
	<a id="subject-parent-entity-profile-url" href="#" title="${i18n().parent_entity}"></a>&nbsp;
    <a id="subject-parent-entity-temporal-url" href="#" title="${i18n().map_of_science}"><img src="${mapOfScienceIcon}" width="15px" height="15px" alt="${i18n().map_of_science_icon}"/></a>
</div>
-->

<#if (builtFromCacheTime??) >
    <div class="cache-info-vis">${i18n().using_cache_time} ${builtFromCacheTime?time} (${builtFromCacheTime?date?string("MMM dd yyyy")})</div>
</#if>
<h2 id="header-entity-label" class="hide-dom-on-init"><span><a id="entityMoniker" href="${entityVivoProfileURL}" title="${i18n().entity_label}">${entityLabel}</a></span></h2>

<div id="map-of-science-info" class="hide-dom-on-init"> ${i18n().explore_activity} (<span id="mapped-publications" style="font-weight: bold"></span> ${i18n().publications}) ${i18n().across_subdisciplines} 
	<img class="filterInfoIcon" id="imageIconOne"  src="${urls.images}/iconInfo.png" 
		alt="${i18n().info_icon}" /> 
</div>

<div id="left-column" class="hide-dom-on-init">
	<div id="notification-container" style="display:none">
        <div id="error-notification" class="ui-state-error" style="padding:10px; -moz-box-shadow:0 0 6px #980000; -webkit-box-shadow:0 0 6px #980000; box-shadow:0 0 6px #980000;">
            <a class="ui-notify-close" href="#" title="${i18n().error_notification}"><span class="ui-icon ui-icon-close" style="float:right"></span></a>
            <span style="float:left; margin:0 5px 0 0;" class="ui-icon ui-icon-alert"></span>
            <h1>&#035;{title!}</h1>
            <p>&#035;{text}</p>
            <p style="text-align:center"><a class="ui-notify-close" href="#">${i18n().close_me}</a></p>
        </div>
        
        <div id="warning-notification" class="ui-state-highlight ui-corner-all" >
	        <a class="ui-notify-close ui-notify-cross" href="#" title="${i18n().error_notification}">x</a>
	        <span style="float: left; margin-right: 0.3em;" class="ui-icon ui-icon-info"></span>
	        <h1>&#035;{title!}</h1>
	        <p>&#035;{text}</p>
        </div>
    </div>
    
    <#-- VIEW TYPE FILTER -->
    <div id="view-type-filter" style="display:${viewTypeFilterDisplay};">
    	<input type="radio" name="view-type" value="ENTITY"> ${i18n().explore_capitalized} ${entityLabel} </input>
    	<img class="filterInfoIcon" id="exploreInfoIcon" src="${urls.images}/iconInfo.png" alt="${i18n().info_icon}" title="" /><br>
		<input type="radio" name="view-type" value="COMPARISON"> ${i18n().compare_organizations} <#--/ people --></input>
		<img class="filterInfoIcon" id="compareInfoIcon" src="${urls.images}/iconInfo.png" alt="${i18n().info_icon}" title="" /><br><br>
    </div>
    
    <!-- <h3>${i18n().what_to_compare}</h3> -->
    
    <div id="main-science-areas-table-container"></div>

</div>		

<div id="right-column">
	<div id="map_area"></div>
	<div id="percent-mapped-info">
		${i18n().mapped} <span id="percent-mapped"></span>% ${i18n().of} <span id="total-publications"></span> ${i18n().publications}
		<img class="filterInfoIcon" id="imageIconThree" src="${urls.images}/iconInfo.png" alt="${i18n().info_icon}"/>
			
		<div id="download-unlocated-journal-info">
			<a href="${entityMapOfScienceUnlocatedJournalsCSVURL}" title="${i18n().save_unmapped_publications}">${i18n().save_unmapped_publications}</a>
		</div>
	</div>
	<br />
	<div id="subEntityTableArea"></div>
</div>


</div>

<#include "mapOfScienceTooltips.ftl">

<div id="error-container">

<h1 id="noPublications-header">${entityLabel}</h1>
    <h3 id="vis-title">${i18n().map_of_science_visualization}</h3>
    <div id="error-body">
        <p><span id="variable-error-text">${i18n().no_publications_for_this_organization}</span><hr /> 
        ${i18n().please_visit} ${entityLabel} <a href="${entityVivoProfileURL}">${i18n().profile_page}</a> ${i18n().for_complete_overview}</p>
    </div>
</div>

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>')}

${stylesheets.add('<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/visualization.css" />')}
