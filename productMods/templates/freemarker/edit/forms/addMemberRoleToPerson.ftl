<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#--Two stage form for service provider role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddMemberRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments for Add Clinical Role To Person-->
<#assign roleDescriptor = "${i18n().membership}" />
<#assign typeSelectorLabel = "${i18n().membership_in}" />
<#assign roleActivityVClass = "${i18n().organizations}" />
<#assign genericLabel = "${i18n().organization_capitalized}" />
<#assign acMultipleTypes = "'true'" />
<#assign acTypes = "{activity: 'http://xmlns.com/foaf/0.1/Organization,http://xmlns.com/foaf/0.1/Group,http://purl.obolibrary.org/obo/OBI_0000835'}" />
<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage.ftl">