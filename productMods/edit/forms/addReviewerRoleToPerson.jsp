<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="roleDescriptor" value="reviewer of" />
    <jsp:param name="typeSelectorLabel" value="reviewer of" />	
    <jsp:param name="buttonText" value="reviewer role" />
    <jsp:param name="showRoleLabelField" value="false" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#ReviewerRole" />	
    <jsp:param name="roleToActivityPredicate" value="http://vivoweb.org/ontology/core#forInformationResource" />
    <jsp:param name="activityToRolePredicate" value="http://vivoweb.org/ontology/core#linkedRole" />	
	<jsp:param name="roleActivityType_optionsType" value="CHILD_VCLASSES" />
	<jsp:param name="roleActivityType_objectClassUri" value="http://vivoweb.org/ontology/core#InformationResource" /> 
	<jsp:param name="roleActivityType_literalOptions" value="[ 'Select type' ]" />
</jsp:include>