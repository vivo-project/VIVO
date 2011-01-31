<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#-- Determine whether this person is an author -->
<#assign authorship = propertyGroups.getProperty("${core}authorInAuthorship")!>
<#assign isAuthor = p.hasStatements(authorship)!false />

<#-- Determine whether this person is involved in any grants -->
<#assign investigatorRole = propertyGroups.getProperty("${core}hasInvestigatorRole")!>
<#assign piRole = propertyGroups.getProperty("${core}hasPrincipalInvestigatorRole")!>
<#assign coPiRole = propertyGroups.getProperty("${core}hasCo-PrincipalInvestigatorRole")!>
<#if (p.hasStatements(investigatorRole) || p.hasStatements(piRole) || p.hasStatements(coPiRole))>
    <#assign isInvestigator = true />
<#else>
    <#assign isInvestigator = false />
</#if>

<#assign coAuthorIcon = '${urls.images}/visualization/co_author_icon.png'>
<#assign coInvestigatorIcon = '${urls.images}/visualization/co_investigator_icon.png'>
<#assign informationIcon = '${urls.images}/iconInfo.gif'>
<#assign standardVisualizationURLRoot ="/visualization">
<#assign googleJSAPI = 'https://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22imagesparkline%22%5D%7D%5D%7D'>
<#assign coAuthorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=coauthor'>
<#assign coInvestigatorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=copi'>
<#assign visualizationHelperJavaScript = 'js/visualization/visualization-helper-functions.js'>

<#if (isAuthor || isInvestigator)>

${stylesheets.add("css/visualization/visualization.css")}


<section id="visualization-container" role="region">

    <#if isAuthor>

    <img class="infoIcon" src="${informationIcon}" alt="information icon." title="The publication and grant information may be incomplete." width="20px" height="21px" />
    
    <div id="vis_container_coauthor">&nbsp;</div>
    
    <div class="collaboratorship-link-separator"></div>
    
    <div id="coauthorship_link_container" class="collaboratorship-link-container">
        <div class="collaboratorship-icon">
            <a href="${coAuthorURL}"><img src="${coAuthorIcon}" alt="Co-author Network icon" width="30px" height="30px" /></a>
        </div>
        <div class="collaboratorship-link"><a href="${coAuthorURL}">Co-Author Network</a></div>
    </div>
    
    ${scripts.add(googleJSAPI)}
    ${scripts.add(visualizationHelperJavaScript)}
    ${scripts.add("/js/visualization/sparkline.js")}
    
    <#-- Aside from the variable declarations, this should be moved to an external js file -->
    <script type="text/javascript">
        var visualizationUrl = '${individual.visualizationUrl}';
    </script>
    
    <#if isInvestigator>
        <div class="collaboratorship-link-separator"></div>
    </#if>
    
    </#if>
    
    <#if isInvestigator>
    
    <div id="coinvestigator_link_container" class="collaboratorship-link-container">
        <div class="collaboratorship-icon">
            <a href="${coInvestigatorURL}"><img src="${coInvestigatorIcon}" alt="Co-investigator Network icon" width="30px" height="30px" /></a>
        </div>
        <div class="collaboratorship-link"><a href="${coInvestigatorURL}">Co-Investigator Network</a></div>
    </div>
    
    </#if>
    
</section>

</#if>