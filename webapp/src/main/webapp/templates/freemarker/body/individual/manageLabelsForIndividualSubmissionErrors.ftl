<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#--VIVO specific errors for person-->
<#import "lib-vivo-form.ftl" as lvf>

<#--Get existing value for specific data literals and uris, in case the form is returned because of an error-->
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName")/>
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName")/>
<#assign middleNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "middleName") />

<#assign labelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "label")/>
<#assign newLabelLanguageValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "newLabelLanguage")/>

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