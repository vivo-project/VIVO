<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign shortVisualizationURLRoot ="/vis">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign egoURI ="${egoURIParam?url}">
<#assign egoCoInvestigationDataFeederURL = '${urls.base}${dataVisualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&vis_mode=copi_network_stream&labelField=label'>

<#assign coauthorshipURL = '${urls.base}${shortVisualizationURLRoot}/author-network/?uri=${egoURI}'>

<#if egoLocalName?has_content >
    
    <#assign coauthorshipURL = '${urls.base}${shortVisualizationURLRoot}/author-network/${egoLocalName}'>
    
<#else>

    <#assign coauthorshipURL = '${urls.base}${shortVisualizationURLRoot}/author-network/?uri=${egoURI}'>

</#if>

<#assign egoCoInvestigatorsListDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&vis_mode=copis'>
<#assign egoCoInvestigationNetworkDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&vis_mode=copi_network_download'>

<#assign coAuthorIcon = '${urls.images}/visualization/co_author_icon.png'>

<#assign swfLink = '${urls.images}/visualization/coauthorship/EgoCentric.swf'>
<#assign adobeFlashDetector = '${urls.base}/js/visualization/coauthorship/AC_OETags.js'>
<#assign googleVisualizationAPI = 'https://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
<#assign coInvestigatorPersonLevelJavaScript = '${urls.base}/js/visualization/coPIship/coPIship-person-level.js'>
<#assign commonPersonLevelJavaScript = '${urls.base}/js/visualization/personlevel/person-level.js'>

<script type="text/javascript" src="${adobeFlashDetector}"></script>
<script type="text/javascript" src="${googleVisualizationAPI}"></script>

<script language="JavaScript" type="text/javascript">
<!--
// -----------------------------------------------------------------------------
// Globals
// Major version of Flash required
var requiredMajorVersion = 10;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 0;
// -----------------------------------------------------------------------------

var swfLink = "${swfLink}";
var egoURI = "${egoURI}";
var unEncodedEgoURI = "${egoURIParam}";
var egoCoInvestigationDataFeederURL = "${egoCoInvestigationDataFeederURL}";
var egoCoInvestigatorsListDataFileURL = "${egoCoInvestigatorsListDataFileURL}";

var contextPath = "${urls.base}";

var visualizationDataRoot = "${dataVisualizationURLRoot}";

// -->
</script>

<script type="text/javascript" src="${coInvestigatorPersonLevelJavaScript}"></script>
<script type="text/javascript" src="${commonPersonLevelJavaScript}"></script>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>')}

${stylesheets.add('<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/personlevel/page.css" />',
                  '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/visualization.css" />')}

<#assign loadingImageLink = "${urls.images}/visualization/ajax-loader.gif">

<#assign egoVivoProfileURL = "${urls.base}/individual?uri=${egoURI}" />

<script language="JavaScript" type="text/javascript">

$(document).ready(function(){
        
    <#if (numOfCoInvestigations?? && numOfCoInvestigations > 0) >
        $("#coinve_table_container").empty().html('<img id="loadingData" width="auto" src="${loadingImageLink}" />');
    </#if>
                
        
    processProfileInformation("ego_label", 
                              "ego_moniker",
                              "ego_profile_image",
                              jQuery.parseJSON(getWellFormedURLs("${egoURIParam}", "profile_info")));
    
    <#if (numOfCoInvestigations?? && numOfCoInvestigations <= 0) || (numOfInvestigators?? && numOfInvestigators <= 0) >  
            if ($('#ego_label').text().length > 0) {
                setProfileName('no_coinvestigations_person', $('#ego_label').text());
            }
    </#if>
    
        
            $.ajax({
                url: "${urls.base}/visualizationAjax",
                data: ({vis: "utilities", vis_mode: "SHOW_AUTHORSHIP_LINK", uri: '${egoURIParam}'}),
                dataType: "json",
                success:function(data){
                
                    /*
                    Collaboratorship links do not show up by default. They should show up only if there any data to
                    show on that page. 
                    */
                    if (data.numOfPublications !== undefined && data.numOfPublications > 0) {
                           $(".toggle_visualization").show();                    
                    }
                
                }
            });        
                    
});
</script>


<div id="body">
    <div id="ego_profile">
            
        <#-- Label -->
            <h2><a href="${egoVivoProfileURL}"><span id="ego_label" class="investigator_name"></span></a></h2>
    
        <#-- Moniker-->
            <em id="ego_moniker" class="moniker"></em>
        
    </div>
    
    <div class = "toggle_visualization">
        <div id="coauthorship_link_container" class="collaboratorship-link-container">
            <div class="collaboratorship-icon"><a href="${coauthorshipURL}"><img src="${coAuthorIcon}" /></a></div>
            <div class="collaboratorship-link">
                <h3><a href="${coauthorshipURL}">Co-Author Network</a></h3>
            </div>
        </div>
    </div>
        
    <div style="clear:both;"></div>
    
    <#if (numOfInvestigators?? && numOfInvestigators > 0) >
    
        <div class="sub_headings"><h3 >Co-Investigator Network </h3></div>
        
        <#if (numOfCoInvestigations?? && numOfCoInvestigations > 0) || (numOfInvestigators?? && numOfInvestigators > 0) > 
                <div class = "graphml-file-link"><a href="${egoCoInvestigationNetworkDataFileURL}">(GraphML File)</a></div>
        <#else>

            <#if numOfInvestigators?? && numOfInvestigators <= 0 >
                <#assign investigatorsText = "multi-investigator" />
            </#if>
            
            <span id="no_coinvestigations">Currently there are no ${investigatorsText!} grants for 
                <a href="${egoVivoProfileURL}"><span id="no_coinvestigations_person" class="investigator_name">this investigator</span></a> 
                in the VIVO database.
            </span>                     
        </#if>
    
    <#else>
    
        <span id="no_coinvestigations">Currently there are no grants for 
            <a href="${egoVivoProfileURL}"><span id="no_coinvestigations_person" class="investigator_name">this investigator</span></a> in the 
            VIVO database.
        </span>
    
    </#if>
            
    <#if (numOfCoInvestigations?? && numOfCoInvestigations > 0) || (numOfInvestigators?? && numOfInvestigators > 0) >
    
        <div id="bodyPannel">
            <div id="visPanel">
                <script language="JavaScript" type="text/javascript">
                    <!--
                    renderCollaborationshipVisualization();
                    //-->
                </script>
            </div>
            <div id="dataPanel">
                <h4 id ="profileTitle">Profile</h4>
                    
                <div id="data-panel-content">
                <div id="profileImage" class="thumbnail"></div>
            
                <h4><span id="investigatorName" class="neutral_investigator_name">&nbsp;</span></h4>
                
                <em id="profileMoniker" class="moniker"></em>
                
                <div id="profile-links"><a href="#" id="profileUrl">VIVO profile</a></div> 

                <div class="investigator_stats" id="num_works"><span class="numbers" style="width: 40px;" id="works"></span>&nbsp;&nbsp;
                <span class="investigator_stats_text">Grant(s)</span></div>
                <div class="investigator_stats" id="num_investigators"><span class="numbers" style="width: 40px;" id="coInvestigators"></span>
                &nbsp;&nbsp;<span class="investigator_stats_text">Co-investigator(s)</span></div>
                
                <div class="investigator_stats" id="fGrant" style="visibility:hidden">
                    <span class="numbers" style="width:40px;" id="firstGrant"></span>&nbsp;&nbsp;<span>First Grant</span></div>
                <div class="investigator_stats" id="lGrant" style="visibility:hidden"><span class="numbers" style="width:40px;" id="lastGrant"></span>
                &nbsp;&nbsp;<span>Last Grant</span></div>
                <div id="incomplete-data">Note: This information is based solely on grants that have been loaded into the VIVO system. 
                This may only be a small sample of the person's total work.<p></p><p></p>
                <#if user.loggedIn > 
                    Go to your profile page to enter additional details about your grants.
                <#else> 
                    Log in to enter additional details about your grants on your profile page.
                </#if>
                </div>
                </div>
            </div>
        </div>
    </#if>


    <#if (numOfInvestigators?? && numOfInvestigators > 0) >

        <#-- Sparkline -->
        <div id="sparkline-container">
            
            <#assign displayTable = false />
            
            <#assign sparklineVO = egoGrantSparklineVO />
            <div id="grant-count-sparkline-include"><#include "personGrantSparklineContent.ftl"></div>
    
            <#assign sparklineVO = uniqueCoInvestigatorsSparklineVO />
            <div id="coinvestigator-count-sparkline-include"><#include "coInvestigationSparklineContent.ftl"></div>
        </div>  
    

        <div class="vis_stats">
        
        <div class="sub_headings" id="table_heading"><h3>Tables</h3></div>
        
            <div class="vis-tables">

                <p id="grants_table_container" class="datatable">

                <#assign tableID = "grant_data_table" />
                <#assign tableCaption = "Grants per year " />
                <#assign tableActivityColumnName = "Grants" />
                <#assign tableContent = egoGrantSparklineVO.yearToActivityCount />
                <#assign fileDownloadLink = egoGrantSparklineVO.downloadDataLink />
                
                <#include "yearToActivityCountTable.ftl">

                </p>
                
            </div>
            
            <#if (numOfCoInvestigations?? && numOfCoInvestigations > 0) >
        
                <div class="vis-tables">
                
                <p id="coinve_table_container" class="datatable"></p>
                </div>
            
            </#if>
            
            <div style="clear:both"></div>
        
        </div>
        
    </#if>
    
</div>