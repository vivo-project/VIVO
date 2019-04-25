<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#--Two stage form for teacher role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddTeacherRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments for Add Clinical Role To Person-->
<#assign roleDescriptor = "${i18n().teaching_activity}" />
<#assign typeSelectorLabel = "${i18n().teaching_activity_type}" />
<#assign roleExamples = "<span class='hint'>&nbsp;(${i18n().teaching_role_hint})</span>" />

<#assign genericLabel = "${i18n().teaching_activity?capitalize}" />

<#assign acMultipleTypes = "'true'" />

<#assign acTypes = "{activity: 'http://purl.org/ontology/bibo/Conference,http://vivoweb.org/ontology/core#Course,http://purl.org/ontology/bibo/Workshop'}" />

<#assign editTitle = "${i18n().edit_entry_for_teacher_role}"/>

<#assign createTitle = "${i18n().create_entry_for_teacher_role}"/>

<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage.ftl">
