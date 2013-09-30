<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a new individual from the Site Admin page: VIVO version -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign typeName = editConfiguration.pageData.typeName />
<#assign isPersonType = editConfiguration.pageData.isPersonType />

<#--Get existing value for specific data literals and uris-->
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName")/>
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName")/>
<#assign middleNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "middleName")/>

<#assign labelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "label")/>

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>


<h2>${i18n().create_new} ${typeName}</h2>


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

<#assign requiredHint = "<span class='requiredHint'> *</span>" />

<section id="newIndividual" role="region">        
    
    <form id="newIndividual" class="customForm noIE67" action="${submitUrl}"  role="add new individual">
 
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

    <p class="submit">
        <input type="hidden" name = "editKey" value="${editKey}"/>
        <input type="submit" id="submit" value="${i18n().create_capitalized} ${typeName}"/>
        <span class="or"> ${i18n().or} </span><a class="cancel" href="${urls.base}/siteAdmin" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>

    </form>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/newIndividualFormUtils.js"></script>')}