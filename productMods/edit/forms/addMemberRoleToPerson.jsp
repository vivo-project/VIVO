<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="roleActivityTypeLabel" value="membership" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#MemberRole" />
	<jsp:param name="roleToActivityPredicate" value="http://vivoweb.org/ontology/core#roleIn" />
    <jsp:param name="activityToRolePredicate" value="http://vivoweb.org/ontology/core#relatedRole" />	
	<jsp:param name="roleActivityType_optionsType" value="VCLASSGROUP" />
	<jsp:param name="roleActivityType_objectClassUri" value="http://vivoweb.org/ontology#vitroClassGrouporganizations" /> 
	<jsp:param name="roleActivityType_literalOptions" value="[ 'Select one' ]" />
</jsp:include>