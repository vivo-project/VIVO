<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding/editing core:webpages -->
<#import "lib-vivo-form.ftl" as lvf>

<#assign subjectName=editConfiguration.pageData.subjectName!"an Individual" />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
    <#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#--Retrieve variables needed-->
<#assign url = lvf.getFormFieldValue(editSubmission, editConfiguration, "url")/>
<#assign anchor = lvf.getFormFieldValue(editSubmission, editConfiguration, "anchor") />
<#assign newRank = lvf.getFormFieldValue(editSubmission, editConfiguration, "newRank") />

<#if url?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign titleVerb="Edit webpage of">        
        <#assign submitButtonText="Save changes">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Add webpage for">        
        <#assign submitButtonText="Add Web Page">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint="<span class='requiredHint'> *</span>" />

<h2>${titleVerb} ${subjectName}</h2>

<#if submissionErrors??>
<section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>       
        <#list submissionErrors?keys as errorFieldName>
            ${errorFieldName}: ${submissionErrors[errorFieldName]}  <br/>           
        </#list>
        </p>
</section>
</#if>    
    
<form class="customForm" action ="${submitUrl}" class="customForm">

    <label for="url">URL ${requiredHint}</label>
    <input  size="70"  type="text" id="url" name="url" value="${url}" role="input" />
   
    <label for="anchor">Webpage Name</label>
    <input  size="70"  type="text" id="anchor" name="anchor" value="${anchor}" role="input" />

    <#if editMode="add">
        <input type="hidden" name="rank" value="${newRank}" />
    </#if>
    
    <input type="hidden" id="editKey" name="editKey" value="${editConfiguration.editKey}"/>
    <p class="submit">
        <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span>
        <a class="cancel" href="${editConfiguration.cancelUrl}" title="Cancel">Cancel</a>
    </p>    
</form>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/userMenu/userMenuUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}