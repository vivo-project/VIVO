<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a position history-->

<#import "lib-vivo-form.ftl" as lf>

<#if editConfig.object?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign formAction="Edit">        
        <#assign submitButtonText="Edit Position">
<#else>
        <#assign formAction="Create">        
        <#assign submitButtonText="Create">
</#if>

<#assign requiredHint="<span class='requiredHint'> *</span>"/> 

<@lf.unsupportedBrowser>

<h2>${formAction} position entry for ${subjectName}</h2>

<#if errorOrgType??>
    <#assign errorMessage = "You must supply an organization type." />
</#if>

<#if errorOrgLabel??>
    <#assign errorMessage = "You must supply an organization name." />
</#if>

<#if errorPositionTitle??>
    <#assign errorMessage = "You must supply a position title." />
</#if>

<#if errorPositionType??>
    <#assign errorMessage = "You must supply a position type." />
</#if>

<#if errorMessage?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>${errorMessage}</p>
    </section>
</#if>

<form class="customForm" action ="${submitUrl}" class="customForm" role="${formAction} position entry">
    <label for="typeSelector">Organization Type ${requiredHint}</label>

    <select id="typeSelector" name="orgType"> 
        <option value="" <#if typeSelector = "">selected</#if> >Select one</option>
        <#list orgType as key>
        <option value="${key.uri}" <#if key = key.uri>selected</#if> >${key.label}</option>
        </#list>
    </select>
    
    <a title="Cancel" href="${formUrl}" class="cancel <#if key??>hidden</#>">Cancel</a>

    <#if orgType??>
        <label for="relatedIndLabel">${organizationType.label} ${requiredHint}</label>
        <input type="text" value="Select an existing ${orgType.label} or create a new one." name="orgLabel" id="relatedIndLabel" size="50" class="acSelector ui-autocomplete-input" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true">

        <label for="positionTitle">Position Title ${requiredHint}</label>
        <input  size="30"  type="text" id="positionTitle" name="positionTitle" value="${positionTitle}" role="input" />

        <label for="positionType">Position Type ${requiredHint}</label>
        <select id="positionType" name="positionType" role="select">    
            <option value="" role="option" <#if positionType = "">selected</#if> >Select one</option>
            <#list positionTypes as positionType>
            <option value="${positionType.uri}" role="option" <#if positionType = positionType.uri>selected</#if> >${positionType.label}</option>
            </#list>
        </select> <#--Should we do something like this  <@widget name="SelectList" fieldName="orgType" />, have a macro where we specified 
                      the type of form element and the type of list, in this case an organization type-->

        <label for="startField">Start Year</label>
        <input class="text-field" name="startField-year" id="startField-year" type="textxt" value="<#if startField-year??>${startField-year}</#if>" size="4" maxlength="4" role="input" />
        <span class='hint'>(YYYY)</span>

        <label for="endField">End Year</label>
        <input class="text-field" name="endField-year" id="endField-year" type="text" value="<#if endField-year??>${endField-year}</#if>" size="4" maxlength="4" role="input" />        
        <span class='hint'>(YYYY)</span>
        
        <input type="hidden" name = "editKey" value="${editKey}" role="input"/>

        <#if editMode == "edit">  
            <input type="submit" name="submit-${titleAction}" value="${submitButtonText}" class="submit" /> 
        <#else>
            <input type="submit" name="submit-${titleAction}" value="${submitButtonText} ${positionType.uri} and Position" class="submit" /> 
        </#if>

        or <a class="cancel" href="${formUrl.cancel}">Cancel</a>

        <p class="requiredHint">* required fields</p>
    </#if>
</form>
    
    <#assign acUrl="//autocomplete?tokenize=true" >

    <script type="text/javascript">
    var customFormData  = {
        acUrl: '${acUrl?url}',
        editMode: '${editMode}',
        submitButtonTextType: 'compound',
        defaultTypeName: 'organization' // used in repair mode, to generate button text and org name field label
    };
    </script>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/edit/forms/css/personHasEducationalTraining.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}   




