<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#--Two stage form for service provider role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddAttendeeRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments-->
<#assign roleDescriptor = "${i18n().attended}" />
<#assign typeSelectorLabel = "${i18n().event_type}" />
<#assign genericLabel = "${i18n().event_capitalized}" />
<#assign acMultipleTypes = "'true'" />
<#assign acTypes = "{activity: 'http://vivoweb.org/ontology/core#EventSeries,http://purl.org/NET/c4dm/event.owl#Event'}" />

<#assign editTitle = "${i18n().edit_entry_for_attendee_role}"/>

<#assign createTitle = "${i18n().create_entry_for_attendee_role}"/>

<#include "addRoleToPersonTwoStage.ftl">

