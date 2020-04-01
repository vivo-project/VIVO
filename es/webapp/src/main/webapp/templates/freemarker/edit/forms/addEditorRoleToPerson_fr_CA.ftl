<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#--Two stage form for service provider role-->

<#--
Required Values to be set for each form that includes addRoleToPersonTwoStage.ftl are:
roleDescriptor.
The other required values (roleType, optionsType, objectClassUri, and literalOptions are
set in the JAVA class corresponding to the form, e.g. AddEditorRoleToPersonGenerator.java.

Optional values can be set, but each of these has default values
set in addRoleToPersonTwoStage.ftl:

buttonText
typeSelectorLabel
numDateFields
showRoleLAbelField
roleExamples-->


<#--Variable assignments for Add Clinical Role To Person-->
<#setting url_escaping_charset="UTF-8">
<#setting output_encoding="UTF-8">
<#assign roleDescriptor = "${i18n().collection_series_editor_role}" />
<#assign typeSelectorLabel = "${i18n().editor_role_in}" />
<#assign genericLabel =   "${i18n().collection_or_series}" />

<#assign acTypes = "{activity: 'http://purl.org/ontology/bibo/Collection'}" />


<#assign editTitle = "${i18n().edit_entry_for_editor_role}"/>

<#assign createTitle = "${i18n().create_entry_for_editor_role}"/>

<#--Each of the two stage forms will include the form below-->
<#include "addRoleToPersonTwoStage_fr_CA.ftl">
