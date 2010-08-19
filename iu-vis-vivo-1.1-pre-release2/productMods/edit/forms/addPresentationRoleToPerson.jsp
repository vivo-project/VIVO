<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>


<%-- sparqlForAcFilter must be all one line for JavaScript. --%>
<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="sparqlForAcFilter" value="PREFIX core: &lt;http://vivoweb.org/ontology/core#&gt; SELECT ?pubUri WHERE {&lt;${subjectUri}&gt; core:authorInAuthorship ?authorshipUri .?authorshipUri core:linkedInformationResource ?pubUri .}" />	
	<jsp:param name="roleActivityTypeLabel" value="presentation" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#PresenterRole" />	
	
	<jsp:param name="roleActivityType_optionsType" value="LITERALS" />
	<jsp:param name="roleActivityType_objectClassUri" value="" /> 
	<jsp:param name="roleActivityType_literalOptions" value='["http://vivoweb.org/ontology/core#Presentation", "Presentation" ], [ "http://vivoweb.org/ontology/core#InvitedTalk","Invited Talk"] ' />
</jsp:include>