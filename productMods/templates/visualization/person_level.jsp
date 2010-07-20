<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineVOContainer"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir"><c:out value="${portalBean.themeDir}" /></c:set>
<c:url var="visImageContextPath" value="/${themeDir}site_icons/visualization/" />
<c:url var="loadingImageLink" value="/${themeDir}site_icons/visualization/ajax-loader.gif"></c:url>

<c:set var='egoPubSparkline' value='${requestScope.egoPubSparklineVO}' />
<c:set var='uniqueCoauthorsSparkline' value='${requestScope.uniqueCoauthorsSparklineVO}' />

<c:set var='egoPubSparklineContainerID' value='${requestScope.egoPubSparklineContainerID}' />
<c:set var='uniqueCoauthorsSparklineVisContainerID' value='${requestScope.uniqueCoauthorsSparklineVisContainerID}' />

<c:set var='numOfAuthors' value='${requestScope.numOfAuthors}' />
<c:set var='numOfCoAuthorShips' value='${requestScope.numOfCoAuthorShips}' />

<c:url var="egoVivoProfileURL" value="/individual">
	<c:param name="uri" value="${requestScope.egoURIParam}" />
</c:url>

<c:url var="egoSparklineDataURL" value="/admin/visQuery">
	<c:param name="render_mode" value="data" />
	<c:param name="vis" value="person_pub_count" />
	<c:param name="uri" value="${requestScope.egoURIParam}" />
</c:url>

<c:url var="coAuthorshipDownloadFile" value="/admin/visQuery">
	<c:param name="vis" value="person_level" />
	<c:param name="render_mode" value="data" />
	<c:param name="uri" value="${requestScope.egoURIParam}" />
</c:url>

<div id="body">


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

.author_name {
	color: #13968c;
	font-weight: bold;
}

.neutral_author_name {
	color: black;
	font-weight: bold;
}

.author_moniker {
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


#coauthorships_table th {
	vertical-align: top;
}

</style>

<!--[if IE]>
	<style type="text/css">
	
	#${egoPubSparklineContainerID},
	#${uniqueCoauthorsSparklineVisContainerID} {
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
			<a href="${egoVivoProfileURL}"><h1><span id="ego_label" class="author_name"></span></h1></a>
	
	<%-- Moniker--%>
			<span id="ego_moniker" class="author_moniker"></span>


	<div style="clear:both;"></div>
	
	<div id="incomplete-data">This information is based solely on publications which have been loaded into the VIVO system. 
	This may only be a small sample of the person's total work. </div>
	
	<%-- Sparkline --%>
		<h2 class="sub_headings">General Statistics</h2>
			<div id="${egoPubSparklineContainerID}">
				${egoPubSparkline.sparklineContent}
			</div>
			
			<div id="${uniqueCoauthorsSparklineVisContainerID}">
				${uniqueCoauthorsSparkline.sparklineContent}
			</div>
			
		<h2 class="sub_headings">Co-Author Network 
		<c:choose>
		    <c:when test="${numOfCoAuthorShips > 0 || numOfAuthors > 0}">
		       <a href="${coAuthorshipDownloadFile}">(GraphML File)</a></h2>
		    </c:when>
		    <c:otherwise>
		        </h2>
		        
		        <c:if test='${numOfAuthors > 0}'>
		        	<c:set var='authorsText' value='multi-author' />
		        </c:if>
		        
		        <span id="no_coauthorships">Currently there are no ${authorsText} papers for 
		        	<span id="no_coauthorships_person" class="author_name">this author</span> in the VIVO database.</span>
		    </c:otherwise>
		</c:choose>
		


</div>	

<c:if test='${numOfCoAuthorShips > 0 || numOfAuthors > 0}'>

<div id="bodyPannel">
	
	
	<div id="visPanel" style="float: left; width: 600px;">
		<script language="JavaScript" type="text/javascript">
			<!--
			renderCoAuthorshipVisualization();
			//-->
		</script>
	</div>
	
	<div id="dataPanel">
		
		<br />
		<br />
		<div id="profileImage" class="thumbnail"></div>
		
		<div class="bold"><strong><span id="authorName" class="neutral_author_name">&nbsp;</span></strong></div>
		
		<div class="italicize"><span id="profileMoniker" class="author_moniker"></span></div>
		<div><a href="#" id="profileUrl">VIVO profile</a> | <a href="#" id="coAuthorshipVisUrl">Co-author network</a></div> 
		<br />
		<div class="author_stats" id="num_works"><span class="numbers" style="width: 40px;" id="works"></span>&nbsp;&nbsp;<span class="author_stats_text">Work(s)</span></div>
		<div class="author_stats" id="num_authors"><span class="numbers" style="width: 40px;" id="coAuthors"></span>&nbsp;&nbsp;<span class="author_stats_text">Co-author(s)</span></div>
		
		<div class="author_stats" id="fPub" style="visibility:hidden"><span class="numbers" style="width:40px;" id="firstPublication"></span>&nbsp;&nbsp;<span>First Publication</span></div>
		<div class="author_stats" id="lPub" style="visibility:hidden"><span class="numbers" style="width:40px;" id="lastPublication"></span>&nbsp;&nbsp;<span>Last Publication</span></div>
		
	</div>

</div>

</c:if>

<c:if test='${numOfAuthors > 0}'>

	<div class="vis_stats">
	
	<h2 class="sub_headings">Tables</h2>
	
		<div class="vis-tables">
			<p id="publications_table_container" class="datatable">
				${egoPubSparkline.table} 
			</p>
		</div>
		
		<c:if test='${numOfCoAuthorShips > 0}'>
	
			<div class="vis-tables">
				<p id="coauth_table_container" class="datatable"></p>
			</div>
		
		</c:if>
		
		<div style="clear:both;"></div>
	
	</div>
</c:if>

</div>

<script language="JavaScript" type="text/javascript">
$(document).ready(function(){

		<c:if test='${numOfCoAuthorShips > 0}'>
	    	$("#coauth_table_container").empty().html('<img id="loadingData" with="auto" src="${loadingImageLink}" />');
	    </c:if>
	    	
	processProfileInformation("ego_label", 
							  "ego_moniker",
							  "ego_profile_image",
							  jQuery.parseJSON(getWellFormedURLs("${requestScope.egoURIParam}", "profile_info")));

	<c:if test='${numOfCoAuthorShips <= 0}'>

		if ($('#ego_label').text().length > 0) {
			setProfileName('no_coauthorships_person', $('#ego_label').text());
		}
		
	</c:if>	

});
</script>