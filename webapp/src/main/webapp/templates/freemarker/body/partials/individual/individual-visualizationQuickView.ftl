<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#-- Determine whether this person is an author -->
<#assign isAuthor = p.hasVisualizationStatements(propertyGroups, "${core}relatedBy", "${core}Authorship") />

<#-- Determine whether this person is involved in any grants -->
<#assign obo_RO53 = "http://purl.obolibrary.org/obo/RO_0000053">

<#assign isInvestigator = ( p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}InvestigatorRole") ||
                            p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}PrincipalInvestigatorRole") || 
                            p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}CoPrincipalInvestigatorRole") ) >

<#if (isAuthor || isInvestigator)>
 
    <#assign standardVisualizationURLRoot ="/visualization">
    <#assign coauthor = "">
    <#assign coinvestigator = "">
    <#assign mapofscience = "">
    <h2 class="mainPropGroup">${i18n().networks}</h2>
        <#if isAuthor>
            <#assign coAuthorIcon = "${urls.images}/visualization/coauthorship/co_author_icon.png">
            <#assign mapOfScienceIcon = "${urls.images}/visualization/mapofscience/scimap_icon.png">
            <#assign coAuthorVisUrl = individual.coAuthorVisUrl()>
            <#assign mapOfScienceVisUrl = individual.mapOfScienceUrl()>
            
            <#assign coauthor = "<li><a href='${coAuthorVisUrl}' title='${i18n().co_author}' style='vertical-align:top;'><img src='${coAuthorIcon}' alt='${i18n().co_author}' width='20px' height='20px' style='padding-right:8px'/></a><a href='${coAuthorVisUrl}' title='${i18n().co_author_network}'>${i18n().co_authors_capitalized}</a></li>" >
             
            <#assign mapofscience = "<li><a href='${mapOfScienceVisUrl}' title='${i18n().map_of_science}' style='vertical-align:top;'><img src='${mapOfScienceIcon} ' alt='${i18n().map_of_science}' width='20px' height='20px' style='padding-right:8px'/></a><a href='${mapOfScienceVisUrl}' title='${i18n().map_of_science}'>${i18n().map_of_science_capitalized}</a></li>" >
                
         </#if>   
            <#if isInvestigator>
                <#assign coInvestigatorVisUrl = individual.coInvestigatorVisUrl()>
                <#assign coInvestigatorIcon = "${urls.images}/visualization/coauthorship/co_investigator_icon.png">
                <#assign coinvestigator = "<li><a href='${coInvestigatorVisUrl}' title='${i18n().co_investigator_network}' style='vertical-align:top;'><img src='${coInvestigatorIcon}' alt='${i18n().co_investigator_network}' width='20px' height='20px' style='padding-right:8px'/></a><a href='${coInvestigatorVisUrl}' title='${i18n().co_investigator_network}'>${i18n().co_investigator_network_capitalized}</a></li>">  
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