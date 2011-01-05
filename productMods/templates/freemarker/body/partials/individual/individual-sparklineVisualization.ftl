<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#if individual.visualizationUrl??>

<#assign coAuthorIcon = '${urls.images}/visualization/co_author_icon.png'>
<#assign coInvestigatorIcon = '${urls.images}/visualization/co_investigator_icon.png'>
<#assign standardVisualizationURLRoot ="/visualizationfm">
<#assign googleJSAPI = 'http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
<#assign coAuthorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=coauthor'>
<#assign coInvestigatorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=copi'>

    <div id="vis_container_coauthor">&nbsp;</div>
    
	<div id="coauthorship_link_container" style="display: none;">
    	<div class="collaboratorship-icon"><a href="${coAuthorURL}"><img src="${coAuthorIcon}" /></a></div>
    	<div class="collaboratorship-link">
    		<h3><a href="${coAuthorURL}">Co-Author Network</a></h3><a class="view-all-style" href="${coAuthorURL}">View <span class= "pictos-arrow-10">4</span></a>
		</div>
    </div>

    <div id="coinvestigator_link_container">
    	<div class="collaboratorship-icon"><a href="${coInvestigatorURL}"><img src="${coInvestigatorIcon}" /></a></div>
    	<div class="collaboratorship-link">
    		<h3><a href="${coInvestigatorURL}">Co-Investigator Network</a></h3><a class="view-all-style" href="${coInvestigatorURL}">View <span class= "pictos-arrow-10">4</span></a>
		</div>
	</div>

    ${stylesheets.add("css/visualization/visualization.css")}
	${scripts.add(googleJSAPI)}
    ${scripts.add("/js/visualization/sparkline.js")}
    
    <script type="text/javascript">
        var visualizationUrl = '${individual.visualizationUrl}';
    </script>
  
</#if>