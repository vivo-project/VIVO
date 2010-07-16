<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="roleActivityTypeLabel" value="membership" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#MemberRole" />
	
	<jsp:param name="roleActivityType_optionsType" value="CHILD_VCLASSES" />
	<jsp:param name="roleActivityType_objectClassUri" value="http://xmlns.com/foaf/0.1/Organization" /> 
	<jsp:param name="roleActivityType_literalOptions" value="[ 'Select one' ]" />
</jsp:include>