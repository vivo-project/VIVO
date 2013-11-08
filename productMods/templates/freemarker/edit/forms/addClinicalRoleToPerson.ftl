<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#--Two stage form for clinical role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddClinicalRoleToPersonGenerator.java.

Optional values can be set in Freemarker, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
roleExamples

Optional values set in JAVA include 
ShowRoleLabelField
ActivityToRolePredicate
RoleToActivityPredicate
-->


<#--Variable assignments for Add Clinical Role To Person-->
<#assign roleDescriptor = "${i18n().clinical_activity}" />
<#assign typeSelectorLabel = "${i18n().clinical_activity_type}" />
<#assign genericLabel = "${i18n().clinical_activity?capitalize}" />

<#assign acMultipleTypes = "'true'" />
<#assign acTypes = "{activity: 'http://vivoweb.org/ontology/core#Project,http://purl.obolibrary.org/obo/ERO_0000005'}" />


<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage.ftl">