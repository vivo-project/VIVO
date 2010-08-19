<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- sparqlForAcFilter must be all one line for JavaScript. --%>
<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="sparqlForAcFilter" value="PREFIX core: &lt;http://vivoweb.org/ontology/core#&gt; SELECT ?pubUri WHERE {&lt;${subjectUri}&gt; core:authorInAuthorship ?authorshipUri .?authorshipUri core:linkedInformationResource ?pubUri .}" />	
	<jsp:param name="roleActivityTypeLabel" value="teaching activity" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#TeacherRole" />
	
	<jsp:param name="roleActivityType_optionsType" value="LITERALS" />
	<jsp:param name="roleActivityType_objectClassUri" value="" /> 
	<jsp:param name="roleActivityType_literalOptions" value='["http://purl.org/ontology/bibo/Conference", "Conference" ], [ "http://vivoweb.org/ontology/core#Course","Course"], [ "http://purl.org/ontology/bibo/Workshop","Workshop"] ' />
</jsp:include>