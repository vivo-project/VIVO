<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="roleDescriptor" value="teaching activity" />
	<jsp:param name="typeSelectorLabel" value="teaching activity type" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#TeacherRole" />
	<jsp:param name="roleExamples" value="Instructor, Facilitator, Assistant" />
	<jsp:param name="roleActivityType_optionsType" value="HARDCODED_LITERALS" />
	<jsp:param name="roleActivityType_objectClassUri" value="" /> 
	<jsp:param name="roleActivityType_literalOptions" 
	           value='["", "Select one"], 
	                  ["http://purl.org/ontology/bibo/Conference", "Conference" ], 
	                  [ "http://vivoweb.org/ontology/core#Course","Course"], 
	                  [ "http://purl.org/ontology/bibo/Workshop","Workshop"] ' />
</jsp:include>