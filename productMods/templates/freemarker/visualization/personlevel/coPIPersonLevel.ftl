<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualizationfm">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign egoURI ="${egoURIParam?url}">
<#assign egoCoInvestigationDataFeederURL = '${urls.base}${dataVisualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&vis_mode=copi_network_stream&labelField=label'>

<#assign coauthorshipURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${egoURI}&vis_mode=coauthor'>

<#assign egoCoInvestigatorsListDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&vis_mode=copis'>
<#assign egoCoInvestigationNetworkDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coprincipalinvestigator&uri=${egoURI}&vis_mode=copi_network_download'>

<#assign coAuthorIcon = '${urls.images}/visualization/co_author_icon.png'>

<#assign swfLink = '${urls.images}/visualization/coauthorship/EgoCentric.swf'>
<#assign adobeFlashDetector = '${urls.base}/js/visualization/coauthorship/AC_OETags.js'>
<#assign googleVisualizationAPI = 'http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
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
var egoCoInvestigationDataFeederURL = "${egoCoInvestigationDataFeederURL}";
var egoCoInvestigatorsListDataFileURL = "${egoCoInvestigatorsListDataFileURL}";

var contextPath = "${urls.base}";

var visualizationDataRoot = "${dataVisualizationURLRoot}";

// -->
</script>

<script type="text/javascript" src="${coInvestigatorPersonLevelJavaScript}"></script>
<script type="text/javascript" src="${commonPersonLevelJavaScript}"></script>

<#assign coInvestigatorStyle = "${urls.base}/css/visualization/personlevel/coinvestigator_style.css" />
<#assign pageStyle = "${urls.base}/css/visualization/personlevel/page.css" />
<#assign vizStyle = "${urls.base}/css/visualization/visualization.css" />

<link href="${coInvestigatorStyle}" rel="stylesheet" type="text/css" />
<link href="${pageStyle}" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${vizStyle}" />

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
					
});
</script>


<div id="body">
	<div id="ego_profile">
			
		<#-- Label -->
			<h2 style="width:20%"><span id="ego_label" class="investigator_name"></span></h2>
	
		<#-- Moniker-->
			<em id="ego_moniker" class="moniker"></em>
		
		<div class = "toggle_visualization">
			<div style="float:left;margin-top: 5%;margin-right: 10px; height:100%;"><img src="${coAuthorIcon}"/></div>
    		<div><h3>Co-Author Network <br/><a class="view-all-style" href="${coauthorshipURL}">View <span class= "pictos-arrow-10">4</span></a></h3></div>
		</div>
		
		<div style=" width: 20%; margin-top:25px;"><span class="pictos-arrow-14">4</span><a href="${egoVivoProfileURL}" style="color:#171717;text-decoration:none;">Back to Profile</a></div>	
		
	
		<div style="clear:both;"></div>
	
			<#if (numOfInvestigators?? && numOfInvestigators > 0) >
			
				<div class="sub_headings"><h2 >Co-Investigator Network </h2></div>
				
				<#if (numOfCoInvestigations?? && numOfCoInvestigations > 0) || (numOfInvestigators?? && numOfInvestigators > 0) > 
					   	<div class = "fileDownloadPlaceHolder"><a href="${egoCoInvestigationNetworkDataFileURL}">(GraphML File)</a></div>
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
			
	</div>		



	<#if (numOfCoInvestigations?? && numOfCoInvestigations > 0) || (numOfInvestigators?? && numOfInvestigators > 0) >
	
		<div id="bodyPannel">
			<div id="visPanel" style="float: right; width: 600px;">
				<script language="JavaScript" type="text/javascript">
					<!--
					renderCoInvestigationVisualization();
					//-->
				</script>
			</div>
			<div id="dataPanel">
				<h4 id ="profileTitle"> <b>Profile</b></h4>	
				<div id="profileImage" class="thumbnail"></div>
			
				<div><h2><span id="investigatorName" class="neutral_investigator_name">&nbsp;</span></h2></div>
				
				<div class="italicize"><span id="profileMoniker" class="investigator_moniker"></span></div>
				<div><a href="#" id="profileUrl">VIVO profile</a> | <a href="#" id="coInvestigationVisUrl">Co-investigator network</a></div> 
				<br />
				<div class="investigator_stats" id="num_works"><span class="numbers" style="width: 40px;" id="works"></span>&nbsp;&nbsp;
				<span class="investigator_stats_text">Grant(s)</span></div>
				<div class="investigator_stats" id="num_investigators"><span class="numbers" style="width: 40px;" id="coInvestigators"></span>
				&nbsp;&nbsp;<span class="investigator_stats_text">Co-investigator(s)</span></div>
				
				<div class="investigator_stats" id="fGrant" style="visibility:hidden">
					<span class="numbers" style="width:40px;" id="firstGrant"></span>&nbsp;&nbsp;<span>First Grant</span></div>
				<div class="investigator_stats" id="lGrant" style="visibility:hidden"><span class="numbers" style="width:40px;" id="lastGrant"></span>
				&nbsp;&nbsp;<span>Last Grant</span></div>
				<div id="incomplete-data">Note: This information is based solely on grants which have been loaded into the VIVO system. 
				This may only be a small sample of the person's total work. </div>
			</div>
		</div>
	</#if>


	<#-- Sparkline -->
	<div style="width: 67%; height: 175px; margin-left: 33%;">
		<#assign sparklineVO = egoGrantSparklineVO />
		<#include "personGrantSparklineContent.ftl">

		<#assign sparklineVO = uniqueCoInvestigatorsSparklineVO />
		<#include "coInvestigationSparklineContent.ftl">
	</div>	
	
	<#if (numOfInvestigators?? && numOfInvestigators > 0) >

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