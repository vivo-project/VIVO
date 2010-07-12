<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="roleShortView.jsp">
	<jsp:param name="personToRolePredicate" value="http://vivoweb.org/ontology/core#hasPrincipalInvestigatorRole"/>
	<jsp:param name="roleActivityToRolePredicate" value="http://vivoweb.org/ontology/core#roleIn"/>
	<jsp:param name="roleActivityLabel" value="grant"/>
</jsp:include>
