<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="roleDescriptor" value="clinical activity" />
	<jsp:param name="typeSelectorLabel" value="clinical activity type" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#ClinicalRole" />
	<jsp:param name="roleActivityType_optionsType" value="HARDCODED_LITERALS" />
	<jsp:param name="roleActivityType_objectClassUri" value="" /> 
	<jsp:param name="roleActivityType_literalOptions" 
	           value='["", "Select one"], 
	                  ["http://vivoweb.org/ontology/core#Project", "Project" ], 
	                  [ "http://vivoweb.org/ontology/core#Service","Service"] ' />
</jsp:include>