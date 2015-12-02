<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
${headScripts.add('<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>',
                  '<script type="text/javascript" src="https://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D"></script>')}

This is Dummy Vis Client. For Real!
Really Re!

${urls.base}

<c:url var="loadingImageLink" value="/${themeDir}site_icons/visualization/ajax-loader.gif"></c:url>

<#assign loadingImageLink = '/${themeDir}site_icons/visualization/ajax-loader.gif'>

<#assign uri="http://vivo-trunk.indiana.edu/individual/n6079">
<#assign testURL = '${urls.base}/visualization?vis=person_pub_count&container=ajax_recipient&render_mode=dynamic&vis_mode=wth&uri=${uri?url}'>

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

		$("#ajax_recipient").empty().html('<img src="${loadingImageLink?url}" />');

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
			   url: '${testURL}',
			   dataType: "html",
			   success:function(data){


			     $("#ajax_recipient").html(data);

			   }
			 });
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




<h2 id="ajax_activator">Hello World!</h2>

<a href="${testURL}" title="query">vis query for person -> "Crane, Brian"</a>


<div id="ajax_recipient">iioio</div>