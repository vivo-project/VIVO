<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#--Two stage form for service provider role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddServiceProviderRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments for Add Service Provider Role To Person-->
<#assign roleDescriptor = "${i18n().service_to_profession}" />
<#assign typeSelectorLabel = "${i18n().service_to_profession_in}" />
<#assign genericLabel = "${i18n().organization_capitalized}" />

<#assign acMultipleTypes = "'true'" />
<#assign acTypes = "{activity: 'http://xmlns.com/foaf/0.1/Organization,http://xmlns.com/foaf/0.1/Group,http://purl.obolibrary.org/obo/OBI_0000835,http://purl.org/NET/c4dm/event.owl#Event'}" />


<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage.ftl">