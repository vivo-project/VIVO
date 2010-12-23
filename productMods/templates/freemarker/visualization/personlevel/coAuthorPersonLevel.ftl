<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualizationfm">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign egoURI ="${egoURIParam?url}">
<#assign egoCoAuthorshipDataFeederURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthor_network_stream&labelField=label'>

<#assign coprincipalinvestigatorURL = '${urls.base}${standardVisualizationURLRoot}?vis=person_level&uri=${egoURI}&vis_mode=copi'>

<#assign egoCoAuthorsListDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthors'>
<#assign egoCoAuthorshipNetworkDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthor_network_download'>

<#assign swfLink = '${urls.images}/visualization/coauthorship/EgoCentric.swf'>
<#assign adobeFlashDetector = '${urls.base}/js/visualization/coauthorship/AC_OETags.js'>
<#assign googleVisualizationAPI = 'http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
<#assign coAuthorPersonLevelJavaScript = '${urls.base}/js/visualization/personlevel/person_level-fm.js'>


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
var egoCoAuthorshipDataFeederURL = "${egoCoAuthorshipDataFeederURL}";
var egoCoAuthorsListDataFileURL = "${egoCoAuthorsListDataFileURL}";
var contextPath = "${urls.base}";

var domainParam = "http://vivo-vis-test.slis.indiana.edu/vivo1/visualizationData";

// -->
</script>

<script type="text/javascript" src="${coAuthorPersonLevelJavaScript}"></script>


<#assign coAuthorStyle = "${urls.base}/css/visualization/personlevel/coauthor_style.css" />
<#assign pageStyle = "${urls.base}/css/visualization/personlevel/page.css" />
<#assign vizStyle = "${urls.base}/css/visualization/visualization.css" />

<link href="${coAuthorStyle}" rel="stylesheet" type="text/css" />
<link href="${pageStyle}" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${vizStyle}" />

<#assign loadingImageLink = "${urls.images}/visualization/ajax-loader.gif">

<#assign egoVivoProfileURL = "/individual?uri=${egoURI}" />

<script language="JavaScript" type="text/javascript">

$(document).ready(function(){

	<#if (numOfCoAuthorShips > 0) >
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
					
});
</script>



<div id="body">
	<div id="ego_profile">
			
		<#-- Label -->
			<h2 style="width:20%"><a href="${egoVivoProfileURL}"><span id="ego_label" class="author_name"></span></a></h2>
	
		<#-- Moniker-->
			<em id="ego_moniker" class="moniker"></em>
		
		<div class = "toggle_visualization">
			<h2>Co-Investigator Network</h2>
			<a style = "margin-top:0px;" class="view-all-style" href='${coprincipalinvestigatorURL}'>View</a>
			<span class="pictos-arrow-10">4</span>
		</div>
	
		<div style="clear:both;"></div>
	
			<#if (numOfAuthors > 0) >
			
				<h2 class="sub_headings">Co-Author Network </h2>
				
				<#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) || (numOfAuthors?? && numOfAuthors > 0) > 
					   	<a class = "fileDownloadPlaceHolder" href="${egoCoAuthorshipNetworkDataFileURL}">(GraphML File)</a>
				<#else>

				        <#if numOfAuthors?? && numOfAuthors <= 0 >
				        	<#assign authorsText = "multi-author" />
				        </#if>
				        
			        	<span id="no_coauthorships">Currently there are no ${authorsText!} papers for 
					    	<a href="${egoVivoProfileURL}"><span id="no_coauthorships_person" class="author_name">this author</span></a> 
					    	in the VIVO database.
				    	</span>				        
				</#if>
			
			<#else>
			
				<span id="no_coauthorships">Currently there are no papers for 
					<a href="${egoVivoProfileURL}"><span id="no_coauthorships_person" class="author_name">this author</span></a> in the 
					VIVO database.
				</span>
			
			</#if>
			
	</div>		
	
	<#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) || (numOfAuthors?? && numOfAuthors > 0) >
	
		<div id="bodyPannel">
			<div id="visPanel" style="float: right; width: 600px;">
				<script language="JavaScript" type="text/javascript">
					<!--
					renderCoAuthorshipVisualization();
					//-->
				</script>
			</div>
			<div id="dataPanel">
				<h4 id ="profileTitle"> <b>Profile</b></h4>	
				<div id="profileImage" class="thumbnail"></div>
			
				<div class="bold"><strong><span id="authorName" class="neutral_author_name">&nbsp;</span></strong></div>
				
				<div class="italicize"><span id="profileMoniker" class="author_moniker"></span></div>
				<div><a href="#" id="profileUrl">VIVO profile</a> | <a href="#" id="coAuthorshipVisUrl">Co-author network</a></div> 
				<br />
				<div class="author_stats" id="num_works"><span class="numbers" style="width: 40px;" id="works"></span>&nbsp;&nbsp;
				<span class="author_stats_text">Publication(s)</span></div>
				<div class="author_stats" id="num_authors"><span class="numbers" style="width: 40px;" id="coAuthors"></span>
				&nbsp;&nbsp;<span class="author_stats_text">Co-author(s)</span></div>
				
				<div class="author_stats" id="fPub" style="visibility:hidden">
					<span class="numbers" style="width:40px;" id="firstPublication"></span>&nbsp;&nbsp;<span>First Publication</span></div>
				<div class="author_stats" id="lPub" style="visibility:hidden"><span class="numbers" style="width:40px;" id="lastPublication"></span>
				&nbsp;&nbsp;<span>Last Publication</span></div>
				<div id="incomplete-data">Note: This information is based solely on publications which have been loaded into the VIVO system. 
				This may only be a small sample of the person's total work. </div>
			</div>
		</div>
	</#if>
	
	<#-- Sparkline -->
	<div style="width: 60%; height: 100px; float:right;">
		
		<#assign displayTable = false />
		
		<#assign sparklineVO = egoPubSparklineVO />
		<#include "personPublicationSparklineContent.ftl">

		<#assign sparklineVO = uniqueCoauthorsSparklineVO />
		<#include "coAuthorshipSparklineContent.ftl">
	</div>	
	
	<#if (numOfAuthors > 0) >

		<div class="vis_stats">
		
		<h3 class="sub_headings" id="table_heading">Tables</h3>
		
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
			
			<#if (numOfCoAuthorShips > 0) >
		
				<div class="vis-tables">
					<p id="coauth_table_container" class="datatable"></p>
				</div>
			
			</#if>
			
			<div style="clear:both"></div>
		
		</div>
		
	</#if>
	
</div>
