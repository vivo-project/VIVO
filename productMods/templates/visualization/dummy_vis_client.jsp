<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="portal" value="${requestScope.portalBean}"/>
<c:set var="themeDir"><c:out value="${portal.themeDir}" /></c:set>

<c:url var="staticHref" value="/admin/visQuery">
	<c:param name="vis" value="person_pub_count"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="vis_mode" value="wth"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5156"/>
</c:url>

<c:url var="staticHref2" value="/admin/visQuery">
	<c:param name="vis" value="person_pub_count"/>
	<c:param name="vis_mode" value="short"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5611"/>
</c:url>

<c:url var="staticHref_d" value="/admin/visQuery">
	<c:param name="render_mode" value="dynamic"/>
	<c:param name="container" value="ajax_recipient"/>
	<c:param name="vis" value="person_pub_count"/>
	<c:param name="vis_mode" value="short"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5156"/>
</c:url>

<c:url var="staticHref2_d" value="/admin/visQuery">
	<c:param name="render_mode" value="dynamic"/>
	<c:param name="container" value="ajax_recipient"/>
	<c:param name="vis" value="person_pub_count"/>
	<c:param name="vis_mode" value="full"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5611"/>
</c:url>


<c:url var="staticHref3" value="/admin/visQuery">
	<c:param name="vis" value="person_pub_count"/>
	<c:param name="vis_mode" value="short"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5748"/>
</c:url>


<c:url var="staticHref3_d" value="/admin/visQuery">
	<c:param name="render_mode" value="dynamic"/>
	<c:param name="vis" value="person_pub_count"/>
	<c:param name="container" value="ajax_recipient"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual22919"/>
</c:url>



<c:url var="collegeCSV" value="/admin/visQuery">
	<c:param name="vis" value="college_pub_count"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="vis_mode" value="wth"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5249"/>
</c:url>

<c:url var="collegeCSV2" value="/admin/visQuery">
	<c:param name="vis" value="college_pub_count"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="vis_mode" value="wth"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual134"/>
</c:url>


<c:url var="collegeCSV3" value="/admin/visQuery">
	<c:param name="vis" value="college_pub_count"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="vis_mode" value="wth"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual249"/>
</c:url>


<c:url var="collegeCSV4" value="/admin/visQuery">
	<c:param name="vis" value="college_pub_count"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="vis_mode" value="wth"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual250"/>
</c:url>

<c:url var="collegeCSV5" value="/admin/visQuery">
	<c:param name="vis" value="college_pub_count"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="vis_mode" value="wth"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual192"/>
</c:url>

<c:url var="coAuthorship1Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5748"/>
</c:url>

<c:url var="coAuthorship2Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5156"/>
</c:url>

<c:url var="coAuthorship3Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5611"/>
</c:url>


<c:url var="coAuthorship4Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5412"/>
</c:url>

<c:url var="coAuthorship1" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5748"/>
</c:url>

<c:url var="coAuthorship2" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5156"/>
</c:url>

<c:url var="coAuthorship3" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5611"/>
</c:url>

<c:url var="coAuthorship4" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5412"/>
</c:url>


<c:url var="coAuthorship5" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5714"/>
</c:url>

<c:url var="coAuthorship5Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5714"/>
</c:url>


<c:url var="coAuthorship6" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual240"/>
</c:url>

<c:url var="coAuthorship6Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual240"/>
</c:url>

<c:url var="coAuthorship7" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual37"/>
</c:url>

<c:url var="coAuthorship7Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual37"/>
</c:url>

<c:url var="coAuthorship8" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5355"/>
</c:url>

<c:url var="coAuthorship8Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5355"/>
</c:url>

<c:url var="coAuthorship9" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5734"/>
</c:url>


<c:url var="coAuthorship9Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5734"/>
</c:url>

<c:url var="coAuthorship10" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual12053"/>
</c:url>

<c:url var="coAuthorship10Data" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual12053"/>
</c:url>

<c:url var="coAuthorshipSparklineData" value="/admin/visQuery">
	<c:param name="vis" value="coauthorship"/>
	<c:param name="vis_mode" value="sparkline"/>
	<c:param name="render_mode" value="data"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5748"/>
</c:url>

<c:url var="personLevel1" value="/admin/visQuery">
	<c:param name="vis" value="person_level"/>
	<c:param name="render_mode" value="standalone"/>
	<c:param name="container" value="ego_sparkline"/>
	<c:param name="uri" value="http://vivo.library.cornell.edu/ns/0.1#individual5748"/>
</c:url>

<c:url var="loadingImageLink" value="/${themeDir}site_icons/visualization/ajax-loader.gif"></c:url>

<style type="text/css">
	.get_vis {
		background-color:Yellow;
		color:blue;
		cursor:pointer;
		height:36px;
		width:225px;
	}
</style>


<script type="text/javascript">
<!--

$(document).ready(function() {

	function renderVisualization(visualizationURL) {

		$("#ajax_recipient").empty().html('<img src="${loadingImageLink}" />');

		   $.ajax({
			   url: visualizationURL,
			   dataType: "html",
			   success:function(data){
			     $("#ajax_recipient").html(data);

			   }
			 });

	}

	   $("#ajax_activator").click(function() {
		   $.ajax({
			   url: '${staticHref3_d}',
			   dataType: "html",
			   success:function(data){


			     $("#ajax_recipient").text(data);

			   }
			 });
	   });


	   $("#ajax_activator_1").click(function() {

		   renderVisualization('${staticHref_d}');

	   });


	   $("#ajax_activator_2").click(function() {

		   renderVisualization('${staticHref2_d}');

	   });


	   $("#ajax_activator_3").click(function() {

		   renderVisualization('${staticHref3_d}');

	   });


	 });



//-->
</script>

<div class="staticPageBackground">

<style type="text/css">

#test-bed {
	background-color:red;
	color:white;
	text-align:center;
}

</style>

<h1 id="test-bed">Visualization Testbed (Not to be seen by eventual end users)</h1>



<a href='<c:out value="${coAuthorship1}"/>'>vis link for coauthorship -> "Erb, Hollis Nancy"</a>
&nbsp;&nbsp;<a href='<c:out value="${coAuthorship1Data}"/>'>Data</a>
&nbsp;&nbsp;<a href='<c:out value="${personLevel1}"/>'>Person Level</a>
&nbsp;&nbsp;<a href='<c:out value="${coAuthorshipSparklineData}"/>'>Unique Coauthors CSV Data</a>
<br />
<a href='<c:out value="${coAuthorship2}"/>'>vis link for coauthorship -> "Not Working" {"Crane, Brian"}</a>&nbsp;
<a href='<c:out value="${coAuthorship2Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship3}"/>'>vis link for coauthorship -> "Merwin, Ian A"</a>&nbsp;
<a href='<c:out value="${coAuthorship3Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship4}"/>'>vis link for coauthorship -> "Thies, Janice"</a>&nbsp;
<a href='<c:out value="${coAuthorship4Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship5}"/>'>vis link for coauthorship -> "Not Working"</a>&nbsp;
<a href='<c:out value="${coAuthorship5Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship6}"/>'>vis link for coauthorship -> "Boor, Kathryn Jean"</a>&nbsp;
<a href='<c:out value="${coAuthorship6Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship7}"/>'>vis link for coauthorship -> "Wiedmann, Martin"</a>&nbsp;
<a href='<c:out value="${coAuthorship7Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship8}"/>'>vis link for coauthorship -> "Not Working"</a>&nbsp;
<a href='<c:out value="${coAuthorship8Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship9}"/>'>vis link for coauthorship -> "Not Working"</a>&nbsp;
<a href='<c:out value="${coAuthorship9Data}"/>'>Data</a><br />
<a href='<c:out value="${coAuthorship10}"/>'>vis link for coauthorship -> "Not Working"</a>&nbsp;
<a href='<c:out value="${coAuthorship10Data}"/>'>Data</a><br />

<br /><br /><br />

<a href='<c:out value="${collegeCSV}"/>'>vis data query for college -> "School of Industrial and Labor Relations (ILR)"</a><br />
<a href='<c:out value="${collegeCSV2}"/>'>vis data query for college -> "College of Agriculture and Life Sciences (CALS)"</a><br />
<a href='<c:out value="${collegeCSV3}"/>'>vis data query for college -> "College of Arts and Sciences"</a><br />
<a href='<c:out value="${collegeCSV4}"/>'>vis data query for college -> "College of Engineering"</a><br />
<a href='<c:out value="${collegeCSV5}"/>'>vis data query for college -> "Joan and Sanford I. Weill Medical College"</a><br />

<a href='<c:out value="${staticHref}"/>'>vis query for person -> "Crane, Brian"</a>


<div id="ajax_activator_1" class="get_vis">
Click to render this Person's vis via AJAX.
</div>

<br />

<a href='<c:out value="${staticHref2}"/>'>vis query for person -> "Merwin, Ian A"</a>

<div id="ajax_activator_2" class="get_vis">
Click to render this Person's vis via AJAX.
</div>

<br />


<a href='<c:out value="${staticHref3}"/>'>vis query for person -> "Erb, Hollis Nancy"</a>

<div id="ajax_activator_3" class="get_vis">
Click to render this Person's vis via AJAX.
</div>

<br />

<br />
<div id="ajax_activator" class="get_vis">
AJAX Content in Text (only for research)
</div>


<br />
<br />



<div id="ajax_recipient">
vis Content via AJAX here

<!--

<div id="pub_count_areachart_vis">AREA CHART</div>

<div id="pub_count_sparkline_vis">pub sparkline chart</div>


-->





</div>

</div>

