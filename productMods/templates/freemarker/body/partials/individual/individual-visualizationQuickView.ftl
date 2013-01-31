<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#-- Determine whether this person is an author -->
<#assign isAuthor = p.hasStatements(propertyGroups, "${core}authorInAuthorship") />

<#-- Determine whether this person is involved in any grants -->
<#assign isInvestigator = ( p.hasStatements(propertyGroups, "${core}hasInvestigatorRole") ||
                            p.hasStatements(propertyGroups, "${core}hasPrincipalInvestigatorRole") || 
                            p.hasStatements(propertyGroups, "${core}hasCo-PrincipalInvestigatorRole") ) >

<#if (isAuthor || isInvestigator)>
 
    <#assign standardVisualizationURLRoot ="/visualization">
    <#assign coauthor = "">
    <#assign coinvestigator = "">
    <#assign mapofscience = "">
    <h2 class="mainPropGroup">Networks</h2>
        <#if isAuthor>
            <#assign coAuthorIcon = "${urls.images}/visualization/coauthorship/co_author_icon.png">
            <#assign mapOfScienceIcon = "${urls.images}/visualization/mapofscience/scimap_icon.png">
            <#assign coAuthorVisUrl = individual.coAuthorVisUrl()>
            <#assign mapOfScienceVisUrl = individual.mapOfScienceUrl()>
            
            <#assign coauthor = "<li><a href='${coAuthorVisUrl}' title='co-author' style='vertical-align:top;'><img src='${coAuthorIcon}' alt='Co-author network icon' width='20px' height='20px' style='padding-right:8px'/></a><a href='${coAuthorVisUrl}' title='co-author network'>Co-Authors</a></li>" >
             
            <#assign mapofscience = "<li><a href='${mapOfScienceVisUrl}' title='map of science' style='vertical-align:top;'><img src='${mapOfScienceIcon} ' alt='Map Of Science icon' width='20px' height='20px' style='padding-right:8px'/></a><a href='${mapOfScienceVisUrl}' title='map of science'>Map Of Science</a></li>" >
                
         </#if>   
            <#if isInvestigator>
                <#assign coInvestigatorVisUrl = individual.coInvestigatorVisUrl()>
                <#assign coInvestigatorIcon = "${urls.images}/visualization/coauthorship/co_investigator_icon.png">
                <#assign coinvestigator = "<li><a href='${coInvestigatorVisUrl}' title='co-investigator network' style='vertical-align:top;'><img src='${coInvestigatorIcon}' alt='Co-investigator network icon' width='20px' height='20px' style='padding-right:8px'/></a><a href='${coInvestigatorVisUrl}' title='co-investigator network'>Co-Investigators</a></li>">  
            </#if>
            <ul id="individual-visualization">
                ${coauthor}
                ${coinvestigator}
                ${mapofscience}
            </ul>  
            <script type="text/javascript">
                var visualizationUrl = '${urls.base}/visualizationAjax?uri=${individual.uri?url}';
                var infoIconSrc = '${urls.images}/iconInfo.png';
            </script>
            
</#if>