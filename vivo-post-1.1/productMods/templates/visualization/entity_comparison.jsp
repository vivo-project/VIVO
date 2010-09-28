<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir"><c:out value="${portalBean.themeDir}" /></c:set>

<c:set var='jsonContent' value='${requestScope.JsonContent}'/>

<div id="entity_comparison_vis_container">
<c:out value="${jsonContent}" />
<br/>
<c:out value="${portalBean.themeDir}" />
<br/>


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