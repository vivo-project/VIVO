<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="roleShortView.jsp">
	<jsp:param name="personToRolePredicate" value="http://vivoweb.org/ontology/core#hasPresenterRole"/>
	<jsp:param name="roleToPersonPredicate" value="http://vivoweb.org/ontology/core#presenterRoleOf"/>
	<jsp:param name="roleActivityToRolePredicate" value="http://vivoweb.org/ontology/core#relatedRole"/>
	<jsp:param name="roleActivityLabel" value="presenter role"/>
	<jsp:param name="startYearPredicate" value="http://vivoweb.org/ontology/core#year" />
</jsp:include>

