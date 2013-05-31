<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Macros and functions for form controls -->


<#-- Output: html notifying the user that the browser is an unsupported version -->
<#macro unsupportedBrowser  urlsBase>
<div id="ie67DisableWrapper">
    <div id="ie67DisableContent">
	    <img src="${urlsBase}/images/iconAlertBig.png" alt="${i18n().alert_icon}"/>
	    <p>${i18n().unsupported_ie_version}</p>
    </div>
</div>
</#macro>

<#-- After selecting an individual via autocomplete, display highlighted and with verify link -->
<#macro acSelection urlsBase inputName inputId acGroupName inputValue labelValue="">
<div class="acSelection" acGroupName="${acGroupName}">
    <p class="inline">
        <label>${labelValue}</label>
        <span class="acSelectionInfo"></span>
        <a href="${urlsBase}/individual?uri=" class="verifyMatch" title="${i18n().verify_this_match_title}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
        <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="${inputId}" name="${inputName}" value="${inputValue}" />
        <!-- Field value populated by JavaScript -->
</div>
</#macro>

<#--Given an edit configuration template object, get the current value for a uri field using the field name-->


<#function getEditConfigLiteralValue config varName>
	<#local literalValues = config.existingLiteralValues >
	<#if (literalValues?keys?seq_contains(varName)) && (literalValues[varName]?size > 0)>
		<#return literalValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Given an edit configuration template object, get the current value for a literal field using the field name-->

<#function getEditConfigUriValue config varName>
 	<#local uriValues = config.existingUriValues />
  <#if (uriValues?keys?seq_contains(varName)) && (uriValues[varName]?size > 0)>
		<#return uriValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Now check whether a given value returns either a uri or a literal value, if one empty then use other and
return - returns empty string if no value found-->
<#function getEditConfigValue config varName>
	<#local returnValue = getEditConfigUriValue(config, varName) />
	<#if (returnValue?length = 0)>
		<#local returnValue = getEditConfigLiteralValue(config, varName) />
	</#if>
	<#return returnValue>
</#function>


<#--Given edit submission object find values-->
<#function getEditSubmissionLiteralValue submission varName>
	<#local literalValues = submission.literalsFromForm >
	<#if (literalValues?keys?seq_contains(varName)) && (literalValues[varName]?size > 0)>
		<#return literalValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Given an edit configuration template object, get the current value for a literal field using the field name-->

<#function getEditSubmissionUriValue submission varName>
 	<#local uriValues = submission.urisFromForm />
  <#if (uriValues?keys?seq_contains(varName)) && (uriValues[varName]?size > 0)>
		<#return uriValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Get edit submission value for either literal or uri-->
<#function getEditSubmissionValue submission varName>
	<#local returnValue = getEditSubmissionUriValue(submission, varName) />
	<#if (returnValue?length = 0)>
		<#local returnValue = getEditSubmissionLiteralValue(submission, varName) />
	</#if> 
	<#return returnValue>
</#function>

<#--Get the value for the form field, checking edit submission first and then edit configuration-->
<#function getFormFieldValue submission config varName>
	<#local returnValue = "">
	<#if submission?has_content && submission.submissionExists = true>
		<#local returnValue = getEditSubmissionValue(submission varName)>
	<#else>
		<#local returnValue = getEditConfigValue(config varName)>
	</#if>
	<#return returnValue>
</#function>

<#--Check if submission error exists for a field name-->
<#function submissionErrorExists editSubmission fieldName>
	<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
		<#if editSubmission.validationErrors?keys?seq_contains(fieldName)>
			<#return true>
		</#if>
	</#if>
	<#return false>
</#function>
