<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- sparqlForAcFilter must be all one line for JavaScript. --%>
<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="sparqlForAcFilter" value="PREFIX core: <http://vivoweb.org/ontology/core#> SELECT ?pubUri WHERE {<${subjectUri}> core:authorInAuthorship ?authorshipUri .?authorshipUri core:linkedInformationResource ?pubUri .}" />	
	<jsp:param name="roleActivityTypeLabel" value="presentation" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#PresenterRole" />
	<jsp:param name="roleActivitySuperType" value="http://vivoweb.org/ontology/core#Presentation" />
</jsp:include>