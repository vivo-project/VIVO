<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a position history-->

<#import "lib-vivo-form.ftl" as lvf>

<#if editConfiguration.objectUri?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign formAction="Edit">        
        <#assign submitButtonText="Edit Position">
<#else>
        <#assign formAction="Create">        
        <#assign submitButtonText="Create Position">
</#if>

<#--Get existing value for specific data literals and uris-->

<#assign orgLabel = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabel")/>
<#assign positionTitle = lvf.getFormFieldValue(editSubmission, editConfiguration, "positionTitle")/>
<#assign startField = lvf.getFormFieldValue(editSubmission, editConfiguration, "startField") />
<#assign endField = lvf.getFormFieldValue(editSubmission, editConfiguration, "endField") />

<#assign requiredHint="<span class='requiredHint'> *</span>"/> 

<#-- <@lvf.unsupportedBrowser urls.base /> -->

<h2>${formAction} position entry for ${editConfiguration.subjectName}</h2>

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
  <p class="inline">    
    <label for="orgType">Organization Type ${requiredHint}</label>
    <#assign orgTypeOpts = editConfiguration.pageData.orgType />
    <select id="typeSelector" name="orgType"  >
        <option value="" selected="selected">Select one</option>                
        <#list orgTypeOpts?keys as key>             
            <#if editConfiguration.objectUri?has_content && editConfiguration.objectUri = key>
                <option value="${key}"  selected >${orgTypeOpts[key]}</option>     
            <#else>
                <option value="${key}">${orgTypeOpts[key]}</option>
            </#if>             
        </#list>
    </select>   
  </p>

  <div class="fullViewOnly">        
  <p>
    <label for="relatedIndLabel">### Name ${requiredHint}</label>
    <input type="text" name="orgLabel" id="relatedIndLabel" size="50" class="acSelector" value="${orgLabel}" >
  </p>

    <@lvf.acSelection urls.base />


    <label for="positionTitle">Position Title ${requiredHint}</label>
    <input  size="30"  type="text" id="positionTitle" name="positionTitle" value="${positionTitle}" role="input" />

      <label for="positionType">Position Type ${requiredHint}</label>
      <#assign posnTypeOpts = editConfiguration.pageData.positionType />
      <select id="typeSelector" name="positionType" style="margin-top:-2px" >
          <option value="" selected="selected">Select one</option>                
          <#list posnTypeOpts?keys as key>             
              <#if editConfiguration.objectUri?has_content && editConfiguration.objectUri = key>
                  <option value="${key}"  selected >${posnTypeOpts[key]}</option>     
              <#else>
                  <option value="${key}">${posnTypeOpts[key]}</option>
              </#if>             
          </#list>
      </select>   

      <label for="startField">Start Year</label>
      <input class="text-field" name="startField-year" id="startField-year" type="text" value="${startField}" size="4" maxlength="4" role="input" />
      <span class='hint'>(YYYY)</span>
      <label for="endField">End Year</label>
      <input class="text-field" name="endField-year" id="endField-year" type="text" value="${endField}" size="4" maxlength="4" role="input" />        
      <span class='hint'>(YYYY)</span>
      <input type="hidden" name = "editKey" value="${editKey}" role="input"/>

   </div>
      <p class="submit">
        <#if editMode == "edit">  
            <input type="submit" id="submit" name="submit-${formAction}" value="${submitButtonText}" class="submit" /> 
        <#else>
            <input type="submit" id="submit" name="submit-${formAction}" value="${submitButtonText}" class="submit" /> 
        </#if>

        <span class="or"> or </span><a class="cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
      </p>
      <p class="requiredHint"  id="requiredLegend" >* required fields</p>
      
</form>

<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true',
    editMode: '${editMode}',
    submitButtonTextType: 'compound',
    defaultTypeName: 'organization' // used in repair mode, to generate button text and org name field label
};
</script>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}




