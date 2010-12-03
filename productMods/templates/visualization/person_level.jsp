<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData"%>

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

<c:url var="egoSparklineDataURL" value="/visualization">
	<c:param name="render_mode" value="data" />
	<c:param name="vis" value="person_pub_count" />
	<c:param name="uri" value="${requestScope.egoURIParam}" />
</c:url>

<c:url var="coAuthorshipDownloadFile" value="/visualization">
	<c:param name="vis" value="person_level" />
	<c:param name="render_mode" value="data" />
	<c:param name="uri" value="${requestScope.egoURIParam}" />
</c:url>

<c:url var="coprincipalinvestigator" value="/visualization">
	<c:param name="vis" value="coprincipalinvestigator"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="${requestScope.egoURIParam}"/>
</c:url>

<script language="JavaScript" type="text/javascript">
$(document).ready(function(){
		
		<c:if test='${numOfCoAuthorShips > 0}'>
	    	$("#coauth_table_container").empty().html('<img id="loadingData" with="auto" src="${loadingImageLink}" />');
	    </c:if>

		    	
	processProfileInformation("ego_label", 
							  "ego_moniker",
							  "ego_profile_image",
							  jQuery.parseJSON(getWellFormedURLs("${requestScope.egoURIParam}", "profile_info")));

	<c:if test='${empty numOfCoAuthorShips || empty numOfAuthors}'>

		if ($('#ego_label').text().length > 0) {
			setProfileName('no_coauthorships_person', $('#ego_label').text());
		}
		
	</c:if>	

});
</script>

<div id="body">

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
			<h1><a href="${egoVivoProfileURL}"><span id="ego_label" class="author_name"></span></a></h1>
	
	<%-- Moniker--%>
			<span id="ego_moniker" class="author_moniker"></span>


	<div style="clear:both;"></div>
	
	<c:choose>
	<c:when test='${numOfAuthors > 0}'>
	
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
		        	<a href="${egoVivoProfileURL}"><span id="no_coauthorships_person" class="author_name">this author</span></a> in the VIVO database.</span>
		    </c:otherwise>
		</c:choose>
		
	</c:when>
	<c:otherwise>
		<span id="no_coauthorships">Currently there are no papers for <a href="${egoVivoProfileURL}"><span id="no_coauthorships_person" class="author_name">
		this author</span></a> in the VIVO database.</span>
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
		<div class="author_stats" id="num_works"><span class="numbers" style="width: 40px;" id="works"></span>&nbsp;&nbsp;<span class="author_stats_text">Publication(s)</span></div>
		<div class="author_stats" id="num_authors"><span class="numbers" style="width: 40px;" id="coAuthors"></span>&nbsp;&nbsp;<span class="author_stats_text">Co-author(s)</span></div>
		
		<div class="author_stats" id="fPub" style="visibility:hidden"><span class="numbers" style="width:40px;" id="firstPublication"></span>&nbsp;&nbsp;<span>First Publication</span></div>
		<div class="author_stats" id="lPub" style="visibility:hidden"><span class="numbers" style="width:40px;" id="lastPublication"></span>&nbsp;&nbsp;<span>Last Publication</span></div>
		
	</div>

</div>

</c:if>
<div style="text-align:center; clear: left;">
<a href='<c:out value="${coprincipalinvestigator}"/>'> View all grants and corresponding co-pi network for this author</a>
</div>
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