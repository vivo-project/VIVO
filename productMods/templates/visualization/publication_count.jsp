<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineVOContainer" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var='sparkline' value='${requestScope.sparklineVO}'/>

<div class="staticPageBackground">


<div id="pub_count_vis_container">
${sparkline.sparklineContent}
</div>
${sparkline.sparklineContext}
</div>
