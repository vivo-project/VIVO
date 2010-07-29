<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="roleShortView.jsp">
	<jsp:param name="personToRolePredicate" value="http://vivoweb.org/ontology/core#hasCo-PrincipalInvestigatorRole"/>
	<jsp:param name="roleToPersonPredicate" value="http://vivoweb.org/ontology/core#co-PrincipalInvestigatorRoleOf"/>
	<jsp:param name="roleActivityToRolePredicate" value="http://vivoweb.org/ontology/core#relatedRole"/>
	<jsp:param name="roleActivityLabel" value="grant"/>
	<jsp:param name="roleLabelForPerson" value="Co-Principal Investigator"/>	
</jsp:include>
