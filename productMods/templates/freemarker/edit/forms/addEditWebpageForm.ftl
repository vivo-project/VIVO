<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding/editing core:webpages -->

<#if editConfig.object?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign titleVerb="Edit webpage of">        
        <#assign submitButtonText="Save changes">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Add">        
        <#assign submitButtonText="Add webpage">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint="<span class='requiredHint'> *</span>" />

<h2>${title} ${subjectName}</h2>

<form class="customForm" action ="${submitUrl}" class="customForm">

    <label for="url">URL ${requiredHint}</label>
    <input  size="70"  type="text" id="url" name="url" value="<#if url??>${url}</#if>" role="input" />
   
    <label for="anchor">Webpage Name</label>
    <input  size="70"  type="text" id="anchor" name="anchor" value="<#if anchor??>${anchor}</#if>" role="input" />

    <#if editMode="add">
        <input type="hidden" name="rank" value="${newRank}" />
    </#if>
    
    <p class="submit">
        <input type="submit" id="submit" value="${submitButtonText}" role="button" />
        
        <span class="or"> or </span>
        
        <a class="cancel" href="${editConfiguration.cancelUrl}" title="Cancel">Cancel</a>
    </p>
</form>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/userMenu/userMenuUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}