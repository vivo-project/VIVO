<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#--Two stage form for service provider role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddResearcherRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments for Add Clinical Role To Person-->
<#assign roleDescriptor = "${i18n().research_activity}" />
<#assign typeSelectorLabel = "${i18n().research_activity_type}" />
<#assign genericLabel = "${i18n().research_activity?capitalize}" />

<#assign acMultipleTypes = "'true'" />
<#assign acTypes = "{activity: 'http://vivoweb.org/ontology/core#Project,http://vivoweb.org/ontology/core#Grant'}" />

<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage.ftl">