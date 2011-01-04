<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#if individual.visualizationUrl??>

<#assign coAuthorIcon = '${urls.images}/visualization/co_author_icon.png'>
<#assign coInvestigatorIcon = '${urls.images}/visualization/co_investigator_icon.png'>
<#assign standardVisualizationURLRoot ="/visualizationfm">
<#assign googleJSAPI = 'http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
<#assign CoInvestigatorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=copi'>

<style>

.collaboratorship-icon {
	float: left;
	padding-right: 5px;
}

.collaboratorship-link a {
	font-size: 0.9em;
}

.collaboratorship-link h3 {
	line-height: 1em;
	font-size: 1em !important;
	margin-bottom: 0px !important;
	padding-top: 0px !important;
	padding-right: 0px !important;
	padding-left: 0px !important;
	padding-bottom: 0px !important;
}


</style>
	
    <div id="vis_container_coauthor">&nbsp;</div>
    
    <div id="vis_container_coinvestigator">
    	<div class="collaboratorship-icon"><img src="${coInvestigatorIcon}" /></div>
    	<div class="collaboratorship-link"><h3>Co-Investigator Network </h3><a class="view-all-style" href="${CoInvestigatorURL}">View <span class= "pictos-arrow-10">4</span></a>
		</div>
    </div>

    ${stylesheets.addFromTheme("/visualization/visualization.css")}
	${scripts.add(googleJSAPI)}
    ${scripts.add("/js/visualization/sparkline.js")}
    
    <script type="text/javascript">
        var visualizationUrl = '${individual.visualizationUrl}';
        
        var coAuthorIcon = $("<img>");
        coAuthorIcon.attr("src", '${coAuthorIcon}');
        
    </script>
  
</#if>