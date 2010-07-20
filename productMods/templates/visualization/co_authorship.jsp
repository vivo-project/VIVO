<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir">
	<c:out value="${portalBean.themeDir}" />
</c:set>
<c:set var="contextPath">
	<c:out value="${pageContext.request.contextPath}" />
</c:set>

<c:url var="egoCoAuthorshipDataURL" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship" />
	<c:param name="render_mode" value="data" />
	<c:param name="uri" value="${requestScope.egoURIParam}" />
	<c:param name="labelField" value="name" />
</c:url>


<c:url var="egoSparklineVisURL" value="/admin/visQuery">
	<c:param name="render_mode" value="dynamic"/>
	<c:param name="container" value="ego_sparkline"/>
	<c:param name="vis" value="person_pub_count"/>
	<c:param name="vis_mode" value="full"/>
	<c:param name="uri" value="${requestScope.egoURIParam}"/>
</c:url>

<c:url var="jquery" value="/js/jquery.js" />
<c:url var="adobeFlashDetector" value="/js/visualization/coauthorship/AC_OETags.js" />
<c:url var="coAuthorShipJavaScript" value="/js/visualization/coauthorship/co_authorship.js" />
<c:url var="googleVisualizationAPI" value="http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D"/>
<c:url var="style" value="/${themeDir}css/visualization/coauthorship/style.css" />
<c:url var="noImage" value="/${themeDir}site_icons/visualization/coauthorship/no_image.png" />
<c:url var="swfLink" value="/${themeDir}site_icons/visualization/coauthorship/CoAuthor.swf" />



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />


<title>Co-Authorship Visualization</title>

<script type="text/javascript" src="${adobeFlashDetector}"></script>


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
var egoCoAuthorshipDataURL = "${egoCoAuthorshipDataURL}";
var contextPath = "${contextPath}";


// -->
</script>

<script type="text/javascript" src="${jquery}"></script>
<script type="text/javascript" src="${googleVisualizationAPI}"></script>
<link href="${style}" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${coAuthorShipJavaScript}"></script>

<style type="text/css">
		#ego_sparkline {
			cursor:pointer;
			height:36px;
			margin-left:24%;
			/*margin-top:-18%;*/
			position:absolute;
			width:471px;
		}
	</style>
	
</head>

<body>


<div id="body">

                    
<%-- Label --%>
<div class="datatypePropertyValue">
	<div class="statementWrap">
		<span id="ego_label"></span>
    </div>
</div>

<%-- Moniker--%>                       
<div class="datatypeProperties">
    <div class="datatypePropertyValue">
        <div class="statementWrap">
            <span id="ego_moniker" class="moniker"></span>                       
        </div>
    </div>
</div>
    
<%-- Image --%>  
<div class="datatypeProperties">
    <div class="datatypePropertyValue">
        <div id="ego_profile-image" class="statementWrap thumbnail"> 
        </div>
    </div>
</div> 

<%-- Sparkline --%>  
<div class="datatypeProperties">
    <div class="datatypePropertyValue">
        <div id="ego_sparkline">
        
        ${requestScope.egoURIParam}
         
        </div>
    </div>
</div> 

	             
<div id="topShadow"></div>
<div id="bodyPannel" style="height: 900px;"><br class="spacer" />
<div id="visPanel" style="float: left; width: 610px;">

<script type="text/javascript">

<!--

renderCoAuthorshipVisualization();

//-->

</script>


</div>
<div id="dataPanel" style="float: left; width: 150px;"><br />
<br />
<br />
<br />
<br />
<br />

	<div id="newsLetter" style="visibility: hidden">
		<span class="nltop"></span>
		<div class="middle" id="nodeData">
		<div id="profileImage"></div>
		<div class="bold"><strong><span id="authorName">&nbsp;</span></strong></div>
		<div class="italicize"><span id="profileMoniker"></span></div>
		<div class="works"><span class="numbers" style="width: 40px;"
			id="works">6</span>&nbsp;&nbsp;<span class="title">Works</span></div>
		<div class="works"><span class="numbers" style="width: 40px;"
			id="coAuthors">78</span>&nbsp;&nbsp;<span>Co-author(s)</span></div>
		<br />
		<div id="firstPublication"><span></span>&nbsp;<span>First
		Publication</span></div>
		<div id="lastPublication"><span></span>&nbsp;Last Publication</div>
		<br />
		<div><a href="#" id="profileUrl">VIVO profile</a></div>
		<br />
		<div><a href="#" id="coAuthorshipVisUrl">Co-author network of <span id="coAuthorName"></span></a></div>
		</div>
		<br class="spacer"> <span class="nlbottom"></span>
	</div>

</div>

Download co-authorship newtwork as <a href="/vivo1/admin/visQuery?uri=http%3A%2F%2Fvivo.library.cornell.edu%2Fns%2F0.1%23individual5748&amp;vis=person_pub_count&amp;render_mode=data">.graphml</a> file.

<div id="bottomShadow"></div>


</div>

<br class="spacer" />

<table id="publications_data_table">
	<caption>Publications per year</caption>
	<thead>
		<tr>
			<th>Year</th>
			<th>Publications</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>2004</td>
			<td>4</td>
		</tr>
		<tr>
			<td>2005</td>
			<td>2</td>
		</tr>
		<tr>
			<td>11</td>
		</tr>
		<tr>
			<td>Unknown</td>
			<td>1</td>
		</tr>
	</tbody>
</table>

Download data as <a href="/vivo1/admin/visQuery?uri=http%3A%2F%2Fvivo.library.cornell.edu%2Fns%2F0.1%23individual5748&amp;vis=person_pub_count&amp;render_mode=data">.csv</a> file.

<table id="coauthorships_data_table">
	<caption>Co - Authorhips</caption>
	<thead>
		<tr>
			<th>Name</th>
			<th>Publications</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>2004</td>
			<td>4</td>
		</tr>
		<tr>
			<td>2005</td>
			<td>2</td>
		</tr>
		<tr>
			<td>11</td>
		</tr>
		<tr>
			<td>Unknown</td>
			<td>1</td>
		</tr>
	</tbody>
</table>




</div>
<script>
$(document).ready(function(){

	processProfileInformation("ego_label", 
							  "ego_moniker",
							  "ego_profile-image",
							  jQuery.parseJSON(getWellFormedURLs("${requestScope.egoURIParam}", "profile_info")));

	renderSparklineVisualization("${egoSparklineVisURL}");
	

	var obj = jQuery.parseJSON('{"name":"John"}');
	//console.log(obj)

	var obj = jQuery.parseJSON('{"imageOffset2":["sup"],"A":["2001","2002","2003","2090","Unknown"],"B":["2001","2002","2003","2090","Unknown"],"C":["2001","2002","2003","2090","Unknown"],"imageOffset":["2090","2002","2003","2001"]}');
	//console.log(obj)

	 $.each(obj, function(i, item){
		 //console.log("i - " + i + " item - " + item);
				$.each(item, function(index, vals) {
						//console.log(index + " - val - " + vals);
					});
		 
       });
	
});
</script>

</body>
</html>
