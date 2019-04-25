<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#--Two stage form for service provider role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddHeadOfRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments for Add Head Role To Person-->
<#assign roleDescriptor = "${i18n().leadership}" />
<#assign typeSelectorLabel = "${i18n().organization_type}" />
<#assign genericLabel = "${i18n().organization_capitalized}" />

<#assign acTypes = "{activity: 'http://xmlns.com/foaf/0.1/Organization'}" />

<#assign editTitle = "${i18n().edit_entry_for_head_role}"/>

<#assign createTitle = "${i18n().create_entry_for_head_role}"/>

<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage.ftl">
