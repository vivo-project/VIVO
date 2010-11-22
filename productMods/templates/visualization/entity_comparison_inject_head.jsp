<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir"><c:out value="${portalBean.themeDir}" /></c:set>
<c:set var="contextPath"><c:out value="${pageContext.request.contextPath}" /></c:set>

<c:url var="jquery" value="/js/jquery.js"/>
<c:url var="flot" value="/js/jquery_plugins/flot/jquery.flot.js"/>
<c:url var="fliptext" value="/js/jquery_plugins/fliptext/jquery.mb.flipText.js"/>
<c:url var="jgrowl" value="/js/jquery_plugins/jgrowl/jquery.jgrowl.js"/>
<c:url var="pagination" value="/js/jquery_plugins/pagination/jquery.pagination.js"/>
<c:url var="livesearch" value="/js/jquery_plugins/jquery.livesearch.js"/>
<c:url var="datatable" value="/js/jquery_plugins/datatable/jquery.dataTables.js"/>
<c:url var="autoellipsis" value="/js/jquery_plugins/jquery.AutoEllipsis.js"/>


<c:url var="entityComparisonUtils" value="/js/visualization/entityComparison/util.js" />
<c:url var="entityComparisonConstants" value="/js/visualization/entityComparison/constants.js" />

<!-- css related to jgrowl and pagination js files. -->
<c:url var="paginationStyle" value="/js/jquery_plugins/pagination/pagination.css" />
<c:url var="jgrowlStyle" value="/js/jquery_plugins/jgrowl/jquery.jgrowl.css" />

<!-- css related to dataTable js files. -->
<c:url var="demopage" value="/js/jquery_plugins/datatable/demo_page.css" />
<c:url var="demoTable" value="/js/jquery_plugins/datatable/demo_table.css" />

<c:url var="entityComparisonStyle" value="/${themeDir}css/visualization/entityComparison/layout.css" />
<c:url var="vizStyle" value="/${themeDir}css/visualization/visualization.css" />

<!-- Including jquery, entity comparison related javascript files -->

<script type="text/javascript" src="${jquery}"></script>
<script type="text/javascript" src="${flot}"></script>
<script type="text/javascript" src="${fliptext}"></script>
<script type="text/javascript" src="${jgrowl}"></script>
<script type="text/javascript" src="${pagination}"></script>
<script type="text/javascript" src="${livesearch}"></script>
<script type="text/javascript" src="${datatable}"></script>
<script type="text/javascript" src="${autoellipsis}"></script>
<script type="text/javascript" src="${entityComparisonUtils}"></script>
<script type="text/javascript" src="${entityComparisonConstants}"></script>


<link href="${entityComparisonStyle}" rel="stylesheet" type="text/css" />
<link href="${paginationStyle}" rel="stylesheet" type="text/css" />
<link href="${jgrowlStyle}" rel="stylesheet" type="text/css" />
<link href="${demopage}" rel="stylesheet" type="text/css" />
<link href="${demoTable}" rel="stylesheet" type="text/css" />


<link rel="stylesheet" type="text/css" href="${vizStyle}" />

<script language="JavaScript" type="text/javascript">
<!--

var contextPath = "${contextPath}";

// -->
</script>