<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign shortVisualizationURLRoot ="/vis">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign egoURI ="${egoURIParam?url}">
<#assign egoCoAuthorshipDataFeederURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthor_network_stream&labelField=label'>

<#if egoLocalName?has_content >
    
    <#assign coprincipalinvestigatorURL = '${urls.base}${shortVisualizationURLRoot}/investigator-network/${egoLocalName}'>
    
<#else>

    <#assign coprincipalinvestigatorURL = '${urls.base}${shortVisualizationURLRoot}/investigator-network/?uri=${egoURI}'>

</#if>



<#assign egoCoAuthorsListDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthors'>
<#assign egoCoAuthorshipNetworkDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthor_network_download'>

<#assign swfLink = '${urls.images}/visualization/coauthorship/EgoCentric.swf'>
<#assign adobeFlashDetector = '${urls.base}/js/visualization/coauthorship/AC_OETags.js'>
<#assign googleVisualizationAPI = 'https://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
<#assign coAuthorPersonLevelJavaScript = '${urls.base}/js/visualization/coauthorship/coauthorship-personlevel.js'>
<#assign commonPersonLevelJavaScript = '${urls.base}/js/visualization/personlevel/person-level.js'>

<#assign coInvestigatorIcon = '${urls.images}/visualization/co_investigator_icon.png'>


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
var egoCoAuthorshipDataFeederURL = "${egoCoAuthorshipDataFeederURL}";
var egoCoAuthorsListDataFileURL = "${egoCoAuthorsListDataFileURL}";
var contextPath = "${urls.base}";

var visualizationDataRoot = "${dataVisualizationURLRoot}";

// -->
</script>

<script type="text/javascript" src="${coAuthorPersonLevelJavaScript}"></script>
<script type="text/javascript" src="${commonPersonLevelJavaScript}"></script>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>')}

${stylesheets.add('<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/personlevel/page.css" />',
                  '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/visualization.css" />')}

<#assign loadingImageLink = "${urls.images}/visualization/ajax-loader.gif">

<#assign egoVivoProfileURL = "${urls.base}/individual?uri=${egoURI}" />

<script language="JavaScript" type="text/javascript">

$(document).ready(function(){

    <#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) >
        $("#coauth_table_container").empty().html('<img id="loadingData" width="auto" src="${loadingImageLink}" />');
    </#if>
                
    processProfileInformation("ego_label", 
                              "ego_moniker",
                              "ego_profile_image",
                              jQuery.parseJSON(getWellFormedURLs("${egoURIParam}", "profile_info")));
    
    <#if (numOfCoAuthorShips?? && numOfCoAuthorShips <= 0) || (numOfAuthors?? && numOfAuthors <= 0) >  
            if ($('#ego_label').text().length > 0) {
                setProfileName('no_coauthorships_person', $('#ego_label').text());
            }
    </#if>
    
    
        $.ajax({
                url: "${urls.base}/visualizationAjax",
                data: ({vis: "utilities", vis_mode: "SHOW_GRANTS_LINK", uri: '${egoURIParam}'}),
                dataType: "json",
                success:function(data){
                
                    /*
                    Collaboratorship links do not show up by default. They should show up only if there any data to
                    show on that page. 
                    */
                    if (data.numOfGrants !== undefined && data.numOfGrants > 0) {
                           $(".toggle_visualization").show();                    
                    }
                
                }
            });
                    
});
</script>



<div id="body">
    <div id="ego_profile">
    
        <#-- Label -->
        <h2><a href="${egoVivoProfileURL}"><span id="ego_label" class="author_name"></span></a></h2>
    
        <#-- Moniker-->
        <em id="ego_moniker" class="moniker"></em>

    </div>
    
    <div class = "toggle_visualization">
        <div id="coinvestigator_link_container" class="collaboratorship-link-container">
            <div class="collaboratorship-icon"><a href="${coprincipalinvestigatorURL}"><img src="${coInvestigatorIcon}" /></a></div>
            <div class="collaboratorship-link">
                <h3><a href="${coprincipalinvestigatorURL}">Co-Investigator Network</a></h3>
            </div>
        </div>
    </div>
    
    <div style="clear:both;"></div>
    
    <#if (numOfAuthors?? && numOfAuthors > 0) >
    
        <div  class="sub_headings"><h3>Co-Author Network </h3></div>
        
        <#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) || (numOfAuthors?? && numOfAuthors > 0) > 
                <div class = "graphml-file-link"><a href="${egoCoAuthorshipNetworkDataFileURL}">(GraphML File)</a></div>
        <#else>

                <#if numOfAuthors?? && numOfAuthors <= 0 >
                    <#assign authorsText = "multi-author" />
                </#if>
                
                <div id="no_coauthorships">Currently there are no ${authorsText!} papers for 
                    <a href="${egoVivoProfileURL}"><span id="no_coauthorships_person" class="author_name">this author</span></a> 
                    in the VIVO database.
                </div>                      
        </#if>
    
    <#else>
    
        <span id="no_coauthorships">Currently there are no papers for 
            <a href="${egoVivoProfileURL}"><span id="no_coauthorships_person" class="author_name">this author</span></a> in the 
            VIVO database.
        </span>
    
    </#if>
            
    <#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) || (numOfAuthors?? && numOfAuthors > 0) >
    
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
            
                <h4><span id="authorName" class="neutral_author_name">&nbsp;</span></h4>
                
                <em id="profileMoniker" class="moniker"></em>
                <div id="profile-links"><a href="#" id="profileUrl">VIVO profile</a></div>

                <div class="author_stats" id="num_works"><span class="numbers" style="width: 40px;" id="works"></span>&nbsp;&nbsp;
                <span class="author_stats_text">Publication(s)</span></div>
                <div class="author_stats" id="num_authors"><span class="numbers" style="width: 40px;" id="coAuthors"></span>
                &nbsp;&nbsp;<span class="author_stats_text">Co-author(s)</span></div>
                
                <div class="author_stats" id="fPub" style="visibility:hidden">
                    <span class="numbers" style="width:40px;" id="firstPublication"></span>&nbsp;&nbsp;<span>First Publication</span></div>
                <div class="author_stats" id="lPub" style="visibility:hidden"><span class="numbers" style="width:40px;" id="lastPublication"></span>
                &nbsp;&nbsp;<span>Last Publication</span></div>
                <div id="incomplete-data">Note: This information is based solely on publications that have been loaded into the VIVO system. 
                This may only be a small sample of the person's total work.<p></p><p></p>
                <#if user.loggedIn > 
                    Go to your profile page to enter additional details about your publications.
                <#else> 
                    Log in to enter additional details about your publications on your profile page.
                </#if>
                </div>
                </div>
            </div>
        </div>
    </#if>

    <#if (numOfAuthors?? && numOfAuthors > 0) >

        <#-- Sparkline -->
        <div id="sparkline-container">
            
            <#assign displayTable = false />
            
            <#assign sparklineVO = egoPubSparklineVO />
            <div id="publication-count-sparkline-include"><#include "personPublicationSparklineContent.ftl"></div>
    
            <#assign sparklineVO = uniqueCoauthorsSparklineVO />
            <div id="coauthor-count-sparkline-include"><#include "coAuthorshipSparklineContent.ftl"></div>
        </div>  
    
        <div class="vis_stats">
        
        <div class="sub_headings" id="table_heading"><h3>Tables</h3></div>
        
            <div class="vis-tables">
                
                <p id="publications_table_container" class="datatable">

                <#assign tableID = "publication_data_table" />
                <#assign tableCaption = "Publications per year " />
                <#assign tableActivityColumnName = "Publications" />
                <#assign tableContent = egoPubSparklineVO.yearToActivityCount />
                <#assign fileDownloadLink = egoPubSparklineVO.downloadDataLink />
                
                <#include "yearToActivityCountTable.ftl">

                </p>
                
            </div>
            
            <#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) >
        
                <div class="vis-tables">
                    <p id="coauth_table_container" class="datatable"></p>
                </div>
            
            </#if>
            
            <div style="clear:both"></div>
        
        </div>
        
    </#if>
    
</div>