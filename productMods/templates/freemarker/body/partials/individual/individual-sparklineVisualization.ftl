<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#-- Determine whether this person is an author -->
<#assign isAuthor = p.hasStatements(propertyGroups, "${core}authorInAuthorship") />

<#-- Determine whether this person is involved in any grants -->
<#assign isInvestigator = ( p.hasStatements(propertyGroups, "${core}hasInvestigatorRole") ||
                            p.hasStatements(propertyGroups, "${core}hasPrincipalInvestigatorRole") || 
                            p.hasStatements(propertyGroups, "${core}hasCo-PrincipalInvestigatorRole") ) >

<#if (isAuthor || isInvestigator)>

    ${stylesheets.add("css/visualization/visualization.css")} 
    <#assign standardVisualizationURLRoot ="/visualization">
    
    <section id="visualization-container" role="region">

        <#if isAuthor>
            <#assign coAuthorIcon = "${urls.images}/visualization/co_author_icon.png">
            <#assign coAuthorURL = "${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=coauthor">
            <#assign googleJSAPI = "https://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22imagesparkline%22%5D%7D%5D%7D">
 
            <img class="infoIcon" src="${urls.images}/iconInfo.png" alt="information icon." 
                 title="The publication and grant information may be incomplete." width="20px" height="21px" />
        
            <div id="vis_container_coauthor">&nbsp;</div>
        
            <div class="collaboratorship-link-separator"></div>
        
            <div id="coauthorship_link_container" class="collaboratorship-link-container">
                <div class="collaboratorship-icon">
                    <a href="${coAuthorURL}"><img src="${coAuthorIcon}" alt="Co-author Network icon" width="30px" height="30px" /></a>
                </div>
                <div class="collaboratorship-link"><a href="${coAuthorURL}">Co-Author Network</a></div>
            </div>
        
            ${scripts.add(googleJSAPI)}
            ${scripts.add("js/visualization/visualization-helper-functions.js")}
            ${scripts.add("/js/visualization/sparkline.js")}
    
            <script type="text/javascript">
                var visualizationUrl = '${individual.visualizationUrl}';
            </script>
        
            <#if isInvestigator>
                <div class="collaboratorship-link-separator"></div>
            </#if>
        
        </#if>
        
        <#if isInvestigator>
            <#assign coInvestigatorURL = "${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=copi">
            <#assign coInvestigatorIcon = "${urls.images}/visualization/co_investigator_icon.png">
            
            <div id="coinvestigator_link_container" class="collaboratorship-link-container">
                <div class="collaboratorship-icon">
                    <a href="${coInvestigatorURL}"><img src="${coInvestigatorIcon}" alt="Co-investigator Network icon" width="30px" height="30px" /></a>
                </div>
                <div class="collaboratorship-link"><a href="${coInvestigatorURL}">Co-Investigator Network</a></div>
            </div>
        
        </#if>    
    </section>
</#if>