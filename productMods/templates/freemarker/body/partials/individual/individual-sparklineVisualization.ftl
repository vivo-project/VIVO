<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#-- Determine whether this person is an author -->
<#assign authorship = propertyGroups.getProperty("${core}authorInAuthorship")!false>
<#assign isAuthor = p.hasStatements(authorship)!false />
<#-- Uncomment the following line for a quick confirmation on Publications logic -->
<#-- <h1>Publications? <#if isAuthor>Ding Ding Ding!<#else>Wah Wuh!</#if></h1> -->

<#-- Determine whether this person is involved in any grants -->
<#assign investigatorRole = propertyGroups.getProperty("${core}hasInvestigatorRole")!false>
<#assign piRole = propertyGroups.getProperty("${core}hasPrincipalInvestigatorRole")!false>
<#assign coPiRole = propertyGroups.getProperty("${core}hasCo-PrincipalInvestigatorRole")!false>
<#if ( p.hasStatements(investigatorRole) || p.hasStatements(piRole) || p.hasStatements(coPiRole) )>
    <#assign isInvestigator = true />
<#else>
    <#assign isInvestigator = false />
</#if>
<#-- Uncomment the following line for a quick confirmation on Grants logic -->
<#-- <h1>Grants? <#if isInvestigator>Ding Ding Ding!<#else>Wah Wuh!</#if></h1> -->

<#-- NO LONGER NEEDED! GET RID OF THIS AND ANY OTHER VIVO REFERENCE IN IndividualTemplateModel.java -->
<#-- <#if individual.visualizationUrl??> -->

<#if (isAuthor || isInvestigator)>
    <#assign coAuthorIcon = '${urls.images}/visualization/co_author_icon.png'>
    <#assign coInvestigatorIcon = '${urls.images}/visualization/co_investigator_icon.png'>
    <#assign informationIcon = '${urls.images}/iconInfo.png'>
    <#assign standardVisualizationURLRoot ="/visualization">
    <#assign googleJSAPI = 'https://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22imagesparkline%22%5D%7D%5D%7D'>
    <#assign coAuthorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=coauthor'>
    <#assign coInvestigatorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${individual.uri}&vis_mode=copi'>
    <#assign visualizationHelperJavaScript = 'js/visualization/visualization-helper-functions.js'>
    
    <section id="sparklines-publications" role="region">
    
        <img class="infoIcon" src="${informationIcon}" alt="information icon." title="The publication and grant information may be incomplete." width="20px" height="21px" />
        
        <div id="vis_container_coauthor">&nbsp;</div>
        
        <div id="coauthorship_link_container" class="collaboratorship-link-container">
            <div class="collaboratorship-icon">
                <a href="${coAuthorURL}"><img src="${coAuthorIcon}" alt="Co-author Network icon" width="30px" height="30px" /></a>
            </div>
            <div class="collaboratorship-link"><a href="${coAuthorURL}">Co-Author Network</a></div>
        </div>
        
        <div id="coinvestigator_link_container" class="collaboratorship-link-container">
            <div class="collaboratorship-icon">
                <a href="${coInvestigatorURL}"><img src="${coInvestigatorIcon}" alt="Co-investigator Network icon" width="30px" height="30px" /></a>
            </div>
            <div class="collaboratorship-link"><a href="${coInvestigatorURL}">Co-Investigator Network</a></div>
        </div>
        
        ${stylesheets.add("css/visualization/visualization.css")}
        ${scripts.add(googleJSAPI)}
        ${scripts.add(visualizationHelperJavaScript)}
        ${scripts.add("/js/visualization/sparkline.js")}
        
        <#-- Aside from the variable declarations, this should be moved to an external js file -->
        <script type="text/javascript">
            var visualizationUrl = '${individual.visualizationUrl}';
            
            $(document).ready(function(){
                $.ajax({
                    url: "${urls.base}/visualizationAjax",
                    data: ({vis: "utilities", vis_mode: "SHOW_GRANTS_LINK", uri: '${individual.uri}'}),
                    dataType: "json",
                    success:function(data){
                        /*
                        Collaboratorship links do not show up by default. They should show up only if there any data to
                        show on that page. 
                        */
                        if (data.numOfGrants !== undefined && data.numOfGrants > 0) {
                               $("#coinvestigator_link_container").show();
                        }
                    }
                });
            });
        </script>
    </section>
</#if>