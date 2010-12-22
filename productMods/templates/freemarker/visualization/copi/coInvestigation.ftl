<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign visImageContextPath = "${urls.images}/visualization/" >
<#assign loadingImageLink = "${visImageContextPath}ajax-loader.gif">

<#assign egoVivoProfileURL = "${urls.base}/individual?uri=${egoURI}">

<#assign egoSparklineDataURL = '${urls.base}${dataVisualizationURLRoot}?vis=person_grant_count&uri=${egoURI}&labelField=label'>
<#assign coInvestigationDownloadFile = '${urls.base}${dataVisualizationURLRoot}?vis=person_level&uri=${egoURI}&labelField=label'>

<div id="body">


<#--

Support for this has ended. Only created for the VIVO Conf Demo.
 
<style type="text/css">

#profileImage img{
	width: 90px;
	height: auto;
}

#body h1 {
	margin:0.0em;
} 

.sparkline_wrapper_table {
	display: inline;
	vertical-align: bottom;
}

.investigator_name {
	color: #13968c;
	font-weight: bold;
}

.neutral_investigator_name {
	color: black;
	font-weight: bold;
}

.investigator_moniker {
	color: #9C9C9C;
}

.sub_headings {
	color: #121b3c;
	padding-top: 10px;
	margin-bottom: 0.3em;
}

.sub_headings a {
	font-size:0.7em;
	font-weight:normal;
}

table.sparkline_wrapper_table td, th {
	vertical-align: bottom;
}

.inline_href {
}

#ego_profile {
	padding-left:10px;
	padding-top:10px;
	min-height: 100px;
}

#ego_label {
	font-size:1.1em;
}

#ego_profile_image {
	float:left;
	padding-right: 5px;
}

#ego_profile_image img{
	width: 90px;
	height: auto;
}

#ego_sparkline {
	cursor:pointer;
	height:36px;
	width:471px;
}

#coinvestigations_table th {
	vertical-align: top;
}

</style>


<!--[if IE]>
	<style type="text/css">
	
	#${egoGrantSparklineContainerID},
	#${uniqueCoPIsSparklineVisContainerID} {
		padding-bottom:15px;
	}
	
	#ego_label {
		margin-left:-3px;
	}
	</style>
<![endif]-->

<div id="ego_profile">


	<%-- Image --%>
			<div id="ego_profile_image" class="thumbnail"></div>
			
	<%-- Label --%>
			<a href="${egoVivoProfileURL}"><h1><span id="ego_label" class="investigator_name"></span></h1></a>
	
	<%-- Moniker--%>
			<span id="ego_moniker" class="investigator_moniker"></span>


	<div style="clear:both;"></div>

	<c:choose>
		<c:when test='${numOfInvestigators > 0}'>
	
	<div id="incomplete-data">This information is based solely on grants which have been loaded into the VIVO system. 
	This may only be a small sample of the person's total work. </div>
	
		<h2 class="sub_headings">Co-Investigator Network 
		<c:choose>
		    <c:when test="${numOfCoInvestigations > 0 || numOfInvestigators > 0}">
		       <a href="${coInvestigationDownloadFile}">(GraphML File)</a></h2>
		    </c:when>
		    <c:otherwise>
		        </h2>
		        
		        <c:if test='${numOfInvestigators > 0}'>
		        	<c:set var='investigatorsText' value='multi-investigator' />
		        </c:if>
		        
		        <span id="no_coinvestigations">Currently there are no ${investigatorsText} grants for 
		        	<a href="${egoVivoProfileURL}"><span id="no_coinvestigations_person" class="investigator_name">this investigator</span></a> in the VIVO database.</span>
		    </c:otherwise>
		</c:choose>
		
	</c:when>
	<c:otherwise>
		<span id="no_coinvestigations">Currently there are no grants for <a href="${egoVivoProfileURL}"><span id="no_coinvestigations_person" class="investigator_name">
		this investigator</span></a> in the VIVO database.</span>
	</c:otherwise>
	</c:choose>
	
</div>

	
<c:if test='${numOfCoInvestigations > 0 || numOfInvestigators > 0}'>

<div id="bodyPannel">	
	
	<div id="visPanel" style="float: left; width: 600px;">
		<script language="JavaScript" type="text/javascript">
			<!--
			renderCoInvestigationVisualization();
			//-->
		</script>
	</div>	
	
	<div id="dataPanel">
		
		<br/>
		<br />
		<div id="profileImage" class="thumbnail"></div>
		
		<div class="bold"><strong><span id="investigatorName" class="neutral_investigator_name">&nbsp;</span></strong></div>
	
		<div class="italicize"><span id="profileMoniker" class="investigator_moniker"></span></div>
		<div><a href="#" id="profileUrl">VIVO profile</a> | <a href="#" id="coInvestigationVisUrl">Co-Investigator network</a></div>
		<br />
		<div class="investigator_stats" id="num_works"><span class="numbers" style="width: 40px;" id="works"></span>&nbsp;&nbsp;<span class="investigator_stats_text">Grant(s)</span></div>
		<div class="investigator_stats" id="num_investigators"><span class="numbers" style="width: 40px;" id="coInvestigators"></span>&nbsp;&nbsp;<span class="investigator_stats_text">Co-Investigator(s)</span></div>
		
		<div class="investigator_stats" id="fGrant" style="visibility:hidden"><span class="numbers" style="width:40px;" id="firstGrant"></span>&nbsp;&nbsp;<span>First Grant</span></div>
		<div class="investigator_stats" id="lGrant" style="visibility:hidden"><span class="numbers" style="width:40px;" id="lastGrant"></span>&nbsp;&nbsp;<span>Last Grant</span></div>	
	
	</div>

</div>

</c:if>	


<c:if test='${numOfCoInvestigations > 0}'>

	<div class="vis_stats">
	
	<h2 class="sub_headings">Table</h2>
	
		<c:if test='${numOfCoInvestigations > 0}'>
	
			<div class="vis-tables">
				<p id="coinve_table_container" class="datatable"></p>
			</div>
		
		</c:if>
		
		<div style="clear:both;"></div>
	
	</div>
</c:if>

</div>

<script language="JavaScript" type="text/javascript">
$(document).ready(function(){

		<c:if test='${numOfCoInvestigations > 0}'>
	    	$("#coinve_table_container").empty().html('<img id="loadingData" with="auto" src="${loadingImageLink}" />');
	    </c:if>
	    	
	processProfileInformation("ego_label", 
							  "ego_moniker",
							  "ego_profile_image",
							  jQuery.parseJSON(getWellFormedURLs("${requestScope.egoURIParam}", "profile_info")));

	<c:if test='${empty numOfCoInvestigations || empty numOfInvestigators}'>

		if ($('#ego_label').text().length > 0) {
			setProfileName('no_coinvestigations_person', $('#ego_label').text());
		}
		
	</c:if>	

});
</script>

-->