<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="roleDescriptor" value="membership" />
    <jsp:param name="typeSelectorLabel" value="membership in" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#MemberRole" />
	<jsp:param name="roleActivityType_optionsType" value="VCLASSGROUP" />
	<jsp:param name="roleActivityType_objectClassUri" value="http://vivoweb.org/ontology#vitroClassGrouporganizations" /> 
	<jsp:param name="roleActivityType_literalOptions" value="[ 'Select type' ]" />
</jsp:include>