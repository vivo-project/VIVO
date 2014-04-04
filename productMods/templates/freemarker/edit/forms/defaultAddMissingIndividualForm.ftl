<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-vivo-form.ftl" as lvf>

<#assign formTitle>
 "${editConfiguration.propertyPublicDomainTitle}" entry for ${editConfiguration.subjectName}
</#assign>
<#if editConfiguration.objectUri?has_content>
    <#assign formTitle>${i18n().edit_capitalized} ${formTitle} </#assign>
    <#assign submitLabel>${i18n().save_changes}</#assign>
<#else>
    <#assign formTitle>${i18n().create_capitalized} ${formTitle} </#assign>
    <#assign submitLabel>${i18n().create_capitalized} "${editConfiguration.propertyPublicDomainTitle}" ${i18n().entry}</#assign>
</#if>
<#assign isPersonType = editConfiguration.pageData.isPersonType />
<#--Get existing value for specific data literals and uris-->
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName")/>
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName")/>
<#assign middleNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "middleName")/>
<#assign labelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "label")/>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<h2>${formTitle}</h2>

<#if submissionErrors?has_content >
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#list submissionErrors?keys as errorFieldName>
    	    <#if errorFieldName == "firstName">
    	        ${i18n().enter_first_name}
    	    <#elseif  errorFieldName == "lastName">
    	        ${i18n().enter_last_name}
        	<#elseif  errorFieldName == "label">
        	    ${i18n().enter_a_name}
    	    </#if>
    	    <br />
    	</#list>
        </p>
    </section>
</#if>

<form id="editForm" class="editForm" action="${submitUrl}">
<#if isPersonType = "true">       
    <p>
        <label for="firstName">${i18n().first_name} ${requiredHint}</label>
        <input size="30"  type="text" id="firstName" name="firstName" value="${firstNameValue}" />
    </p>
    
    <p>
        <label for="lastName">${i18n().middle_name} </label>
        <input size="30"  type="text" id="middleName" name="middleName" value="${middleNameValue}" />
    </p>

    <p>
        <label for="lastName">${i18n().last_name} ${requiredHint}</label>
        <input size="30"  type="text" id="lastName" name="lastName" value="${lastNameValue}" />
    </p>
    
    <input type="hidden" id="label" name="label" value="${labelValue}" />
<#else>       
    <p>
        <label for="name">${i18n().name_capitalized} ${requiredHint}</label>
        <input size="30"  type="text" id="label" name="label" value="${labelValue}" />
    </p>
</#if>
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
    
    <p class="submit">
        <input type="submit" id="submit" value="${submitLabel}" role="submit" />
        <span class="or"> or </span>
        <a class="cancel" title="${i18n().cancel_title}" href="${editConfiguration.cancelUrl}">${i18n().cancel_link}</a>
    </p>     
</form>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/defaultAddMissingIndividualFormUtils.js"></script>')}