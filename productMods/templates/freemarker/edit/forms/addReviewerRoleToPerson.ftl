<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#--Two stage form for service provider role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddReviewerRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments for Add Clinical Role To Person-->
<#assign roleDescriptor = "${i18n().reviewer_of}" />
<#assign typeSelectorLabel = "${i18n().reviewer_of}" />
<#assign genericLabel = "${i18n().item_capitalized}" />

<#assign acMultipleTypes = "'true'" />
<#assign acTypes = "{activity: 'http://purl.org/ontology/bibo/Document,http://purl.org/ontology/bibo/Collection'}" />

<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage.ftl">

