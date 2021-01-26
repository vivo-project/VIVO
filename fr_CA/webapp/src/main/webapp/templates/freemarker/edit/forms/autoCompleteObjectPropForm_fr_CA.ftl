<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#--Assign variables from editConfig-->
<#assign rangeOptions = editConfiguration.pageData.objectVar />
<#--
<#assign rangeOptionsExist = false />
<#if (rangeOptions?keys?size > 0)>
	<#assign rangeOptionsExist = true/>
</#if>
 -->

<#assign rangeOptionsExist = true />
<#assign rangeVClassURI = editConfiguration.objectPredicateProperty.rangeVClassURI!"" />
<#assign objectTypes = editConfiguration.pageData.objectTypes />
<#assign objectTypesSize = objectTypes?length />
<#assign objectTypesExist = false />
<#assign multipleTypes = false />
<#if (objectTypesSize > 1)>
	<#assign objectTypesExist = true />
</#if>
<#if objectTypes?contains(",")>
	<#assign multipleTypes = true/>
</#if>
<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#assign editMode = editConfiguration.pageData.editMode />
<#assign propertyNameForDisplay = "" />
<#if editConfiguration.objectPropertyNameForDisplay?has_content>
	<#assign propertyNameForDisplay = editConfiguration.objectPropertyNameForDisplay />
</#if>
<#if editMode = "edit" >
	<#assign titleVerb = "${i18n().edit_capitalized}" />
	<#assign objectLabel = editConfiguration.pageData.objectLabel />
	<#assign selectedObjectUri = editConfiguration.objectUri />
	<#assign submitButtonText = "${i18n().save_button}" />
<#else>
	<#assign titleVerb = "${i18n().add_capitalized}" >
	<#assign objectLabel = "" />
	<#assign selectedObjectUri = ""/>
	<#assign submitButtonText = "${i18n().create_entry}" />
</#if>
<#assign formTitle = editConfiguration.formTitle />
<#if editConfiguration.formTitle?contains("collaborator") >
    <#assign formTitle = "${i18n().select_existing_collaborator(editConfiguration.subjectName)}" />
<#elseif rangeVClassURI?contains("IAO_0000030")>
    <#assign formTitle = "${i18n().select_an_existing_document}" + " ${i18n().for} " + editConfiguration.subjectName/>
</#if>
<#--In order to fill out the subject-->
<#assign acFilterForIndividuals =  "['" + editConfiguration.subjectUri + "']" />

<h2>${formTitle}</h2>

<#if editConfiguration.propertySelectFromExisting = true>
    <#if rangeOptionsExist  = true >
        <form class="customForm" action = "${submitUrl}">
            <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
            <#if editConfiguration.propertyPublicDescription?has_content>
                <p>${editConfiguration.propertyPublicDescription}</p>
             </#if>

            <#---This section should become autocomplete instead-->
            <p>
				<label for="object">  ${i18n().name_capitalized?cap_first} <span class='requiredHint'> *</span></label>
				<input class="acSelector" size="50"  type="text" id="object" name="objectLabel" acGroupName="object" value="${objectLabel}" />
			</p>

			<div class="acSelection" acGroupName="object" >
				<p class="inline">
					<label>${i18n().selected}:</label>
					<span class="acSelectionInfo"></span>
					<a href="" class="verifyMatch"  title="${i18n().verify_this_match}">(${i18n().verify_this_match}</a> ${i18n().or}
                    <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
                </p>
                <input class="acUriReceiver" type="hidden" id="objectVar" name="objectVar" value="${selectedObjectUri}" />
			</div>

            <#--The above section should be autocomplete-->

            <p>
                <input type="submit" id="submit" value="${submitButtonText}" role="button" disabled="disabled"/>

                <span class="or"> ${i18n().or} </span>
                <a title="${i18n().cancel_title}" class="cancel" href="${cancelUrl}">${i18n().cancel_link}</a>
            </p>
        </form>
    <#else>
        <p> ${i18n().there_are_no_entries_for_selection} </p>
    </#if>
</#if>
<p>&nbsp;</p>
<#if editConfiguration.propertyOfferCreateNewOption = true>
<#include "defaultOfferCreateNewOptionForm.ftl">

</#if>

<#if editConfiguration.propertySelectFromExisting = false && editConfiguration.propertyOfferCreateNewOption = false>
<p>${i18n().editing_prohibited} </p>
</#if>


<#if editConfiguration.includeDeletionForm = true>
<#include "defaultDeletePropertyForm.ftl">
</#if>


<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >
<#--Passing in object types only if there are any types returned, otherwise
the parameter should not be passed at all to the search.
Also multiple types parameter set to true only if more than one type returned-->
    <script type="text/javascript">
    var customFormData  = {
        acUrl: "${urls.base}/autocomplete?tokenize=true",
        <#if objectTypesExist = true>
            acTypes: {object: "${objectTypes}"},
        </#if>
        <#if multipleTypes = true>
            acMultipleTypes: "true",
        </#if>
        editMode: "${editMode}",
        typeName:"${propertyNameForDisplay}",
        acSelectOnly: "true",
        sparqlForAcFilter: "${sparqlForAcFilter}",
        sparqlQueryUrl: "${sparqlQueryUrl}",
        acFilterForIndividuals: ${acFilterForIndividuals},
        defaultTypeName: "${propertyNameForDisplay}", // used in repair mode to generate button text
        baseHref: "${urls.base}/individual?uri="
    };
    var i18nStrings = {
        selectAnExisting: "${i18n().select_an_existing?js_string}",
        orCreateNewOne: "${i18n().or_create_new_one?js_string}",
        selectedString: "${i18n().selected?js_string}"
    };
    </script>
<#--
	 edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AutocompleteObjectPropertyFormGenerator
	 edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddAttendeeRoleToPersonGenerator
-->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.12.1.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.12.1.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}
