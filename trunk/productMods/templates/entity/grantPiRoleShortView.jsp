<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="roleShortView.jsp">
	<jsp:param name="personToRolePredicate" value="http://vivoweb.org/ontology/core#hasPrincipalInvestigatorRole"/>
	<jsp:param name="roleToPersonPredicate" value="http://vivoweb.org/ontology/core#principalInvestigatorRoleOf"/>
	<jsp:param name="roleActivityToRolePredicate" value="http://vivoweb.org/ontology/core#relatedRole"/>
	<jsp:param name="roleActivityLabel" value="grant"/>
	<jsp:param name="roleLabelForPerson" value="Principal Investigator"/>
</jsp:include>
