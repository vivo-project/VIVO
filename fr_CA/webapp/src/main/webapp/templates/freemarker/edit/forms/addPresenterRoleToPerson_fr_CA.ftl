<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#if editConfiguration.objectUri?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#assign blankSentinel = "" />
<#if editConfigurationConstants?has_content && editConfigurationConstants?keys?seq_contains("BLANK_SENTINEL")>
	<#assign blankSentinel = editConfigurationConstants["BLANK_SENTINEL"] />
</#if>

<#--This flag is for clearing the label field on submission for an existing object being selected from autocomplete.
Set this flag on the input acUriReceiver where you would like this behavior to occur. -->
<#assign flagClearLabelForExisting = "flagClearLabelForExisting" />


<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#--Retrieve variables needed-->
<#assign existingPresentationValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingPresentation") />
<#assign presentationLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "presentationLabel") />
<#assign presentationLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "presentationLabelDisplay") />
<#assign presentationTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "presentationType") />
<#assign roleLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleLabel") />
<#assign conferenceValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingConference") />
<#assign conferenceLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conferenceLabel") />
<#assign conferenceLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conferenceLabelDisplay") />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit">
        <#assign titleVerb="${i18n().edit_capitalized}">
        <#assign submitButtonText="${i18n().save_changes}">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="${i18n().create_capitalized}">
        <#assign submitButtonText="${i18n().create_entry}">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(${i18n().year_hint_format})</span>" />

<h2>${titleVerb}&nbsp;${i18n().presentation_entry_for} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if conferenceLabelDisplayValue?has_content >
        <#assign conferenceLabelValue = conferenceLabelDisplayValue />
    </#if>
    <#if presentationLabelDisplayValue?has_content >
        <#assign presentationLabelValue = presentationLabelDisplayValue />
    </#if>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#--Checking if any required fields are empty-->
        <#if lvf.submissionErrorExists(editSubmission, "presentationType")>
 	        Please select a presentation type.
        </#if>
        <#if lvf.submissionErrorExists(editSubmission, "presentationLabel")>
 	        ${i18n().select_or_enter_name}
        </#if>
        <#list submissionErrors?keys as errorFieldName>
        	<#if errorFieldName == "startField">
        	    <#if submissionErrors[errorFieldName]?contains("before")>
        	        ${i18n().start_year_must_precede_end}
        	    <#else>
        	        ${submissionErrors[errorFieldName]}
        	    </#if>

        	<#elseif errorFieldName == "endField">
    	        <#if submissionErrors[errorFieldName]?contains("after")>
    	            ${i18n().end_year_must_be_later}
    	        <#else>
    	            ${submissionErrors[errorFieldName]}
    	        </#if>
	        </#if><br />
        </#list>
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base />

<section id="addPresenterRoleToPerson" role="region">

    <form id="addPresenterRoleToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit Presentation">
    <p class="inline">
      <label for="presentationType">${i18n().presentation_type}<#if editMode != "edit"> ${requiredHint}<#else>:</#if></label>
      <#assign presentationTypeOpts = editConfiguration.pageData.presentationType />
      <select id="typeSelector" name="presentationType" acGroupName="presentation">
        <option value="" selected="selected">${i18n().select_one}</option>
        <#list presentationTypeOpts?keys as key>
            <option value="${key}" <#if presentationTypeValue = key>selected</#if>><#if presentationTypeOpts[key] == "Other">${i18n().presentation_capitalized}<#else>${presentationTypeOpts[key]}</#if></option>
        </#list>
    </select>
    </p>

    <p>
        <label for="presentation">${i18n().presentation_name_capitalized} ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="presentation" acGroupName="presentation" name="presentationLabel" value="${presentationLabelValue}">
            <input class="display" type="hidden" id="presentationDisplay" acGroupName="presentation" name="presentationLabelDisplay" value="${presentationLabelDisplayValue}">
    </p>

    <div class="acSelection" acGroupName="presentation">
        <p class="inline">
            <label>${i18n().selected}:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or}
            <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="presentationUri" name="existingPresentation" value="${existingPresentationValue}" ${flagClearLabelForExisting}="true" />
    </div>

    <p><label for="roleLabel">${i18n().role_in_presentation_capitalized} <span class="hint">(${i18n().presentation_hint})</span></label>
        <input  size="50"  type="text" id="roleLabel" name="roleLabel" value="${roleLabelValue}" />
    </p>
  <p>
      <label for="org">${i18n().presented_at}</label>
      <input  class="acSelector" size="50" acGroupName="conference" type="text" id="conference" name="conferenceLabel" value="${conferenceLabelValue}" />
      <input  class="display" acGroupName="conference" type="hidden" id="conferenceDisplay" name="conferenceLabelDisplay" value="${conferenceLabelDisplayValue}" />
  </p>
  <div class="acSelection" acGroupName="conference">
      <p class="inline">
          <label>${i18n().selected_conference}:</label>
          <span class="acSelectionInfo"></span>
          <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or}
          <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
      </p>
      <input class="acUriReceiver" type="hidden" id="conferenceUri" name="existingConference" value="${conferenceValue}" ${flagClearLabelForExisting}="true" />
  </div>
    <p>
        <h4 class="label">${i18n().years_participating}</h4>
    </p>
    <#--Need to draw edit elements for dates here-->
    <#assign htmlForElements = editConfiguration.pageData.htmlForElements />
    <#if htmlForElements?keys?seq_contains("startField")>
        <label class="dateTime" for="startField">${i18n().start_capitalized}</label>
		${htmlForElements["startField"]} ${yearHint}
    </#if>
    <br/>
    <#if htmlForElements?keys?seq_contains("endField")>
		<label class="dateTime" for="endField">${i18n().end_capitalized}</label>
	 	${htmlForElements["endField"]} ${yearHint}
    </#if>
	<#--End draw elements-->

    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

    <p class="submit">
        <input type="submit" class="submit" value="${submitButtonText}"/><span class="or"> ${i18n().or} </span>
        <a class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>

</form>

</section>


<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true&stem=true',
    acTypes: {presentation: 'http://vivoweb.org/ontology/core#Presentation', conference: 'http://purl.org/ontology/bibo/Conference'},
    editMode: '${editMode}',
    defaultTypeName: 'presentation',
    multipleTypeNames: {presentation: 'presentation', conference: 'conference'},
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
};
var i18nStrings = {
    selectAnExisting: "${i18n().select_an_existing?js_string}",
    orCreateNewOne: "${i18n().or_create_new_one?js_string}",
    selectedString: "${i18n().selected?js_string}"
};
</script>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.12.1.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.12.1.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}
