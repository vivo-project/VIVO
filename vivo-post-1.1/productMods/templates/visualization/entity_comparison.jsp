<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir">
	<c:out value="${portalBean.themeDir}" />
</c:set>

<c:set var='jsonContent' value='${requestScope.JsonContent}' />

<body>
<h1>Entity Comparison Visualization</h1>
<div id="leftblock">
<h2>How do you want to compare?</h2>
<select class="comparisonValues">
	<option value="Publications">Publications</option>
	<option value="Grants">Grants</option>
	<option value="People">People</option>
	<option value="Item4">Item4</option>
	<option value="Item5">Item5</option>

</select> <!-- pagination div is for the top navigating buttons -->
<h2 id="heading">Select schools to compare</h2>
<div id="pagination"></div>
<!-- #searchresult is for inserting the data from schools -->
<dl id="searchresult">
</dl>
</div>
<div id="rightblock"><span class="yaxislabel"></span>
<div id="graphContainer" style="width: 500px; height: 250px;"></div>
<div id="bottom" style="width: 500px;">
<div class="xaxislabel">Year</div>
<h3><span id="comparisonParameter"></span></h3>
<p>You have selected <span id="counter">0</span> of a maximum <span
	id="total">10</span> schools to compare.</p>
<a id="file"
	href="json/school-of-library-an_publications-per-year-5.json"></a></div>
</div>

<div id="entity_comparison_vis_container"><c:out
	value="${jsonContent}" /> <br />
<c:out value="${portalBean.themeDir}" /> <br />
</div>

<script type="text/javascript">
	
	$(document).ready(function() {

		var jsonString = '${jsonContent}';
		var jsonData = jQuery.parseJSON(jsonString);

		console.log(jsonData);
		$.each(jsonData, function(index, value) {
			console.log(value.entityURI);
			});		

	});
	
</script>
</body>