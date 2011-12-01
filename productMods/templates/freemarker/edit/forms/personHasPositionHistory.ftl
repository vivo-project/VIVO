<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a position history-->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign editMode = editConfiguration.pageData.editMode />

<#--Get existing value for specific data literals and uris-->
<#assign orgTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgType")/>
<#assign orgLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabel")/>
<#assign positionTitleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "positionTitle")/>
<#assign positionTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "positionType")/>
<#assign existingOrgValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "org")/>

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#assign disabledVal = ""/>
<#if editMode == "edit">        
        <#assign formAction="Edit">        
        <#assign submitButtonText="Edit Position">
        <#assign disabledVal="disabled">
<#else>
        <#assign formAction="Create">        
        <#assign submitButtonText="Position">
        <#assign disabledVal="">
</#if>

<#assign requiredHint="<span class='requiredHint'> *</span>"/> 
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<h2>${formAction} position entry for ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	<#if errorFieldName == "startField">
        	    <#if submissionErrors[errorFieldName]?contains("before")>
        	        The Start Year must be earlier than the End Year.
        	    <#else>
        	        ${submissionErrors[errorFieldName]}
        	    </#if>
        	    <br />
        	<#elseif errorFieldName == "endField">
    	        <#if submissionErrors[errorFieldName]?contains("after")>
    	            The End Year must be later than the Start Year.
    	        <#else>
    	            ${submissionErrors[errorFieldName]}
    	        </#if>
	        </#if>
        </#list>
        <#--Checking if Org Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "orgType")>
 	        Please select a value in the Organization Type field.<br />
        </#if>
        <#--Checking if Org Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "orgLabel")>
 	        Please enter or select a value in the Name field.<br />
        </#if>
        <#--Checking if Position Title field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "positionTitle")>
 	        Please enter a value in the Position Title field.<br />
        </#if>
        <#--Checking if Position Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "positionType")>
 	        Please select a value in the Position Type field.<br />
        </#if>
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<form class="customForm" action ="${submitUrl}" class="customForm noIE67" role="${formAction} position entry">
  <p class="inline">    
    <label for="orgType">Organization Type ${requiredHint}</label>
    <#assign orgTypeOpts = editConfiguration.pageData.orgType />
    <select id="typeSelector" name="orgType"  ${disabledVal} >
        <option value="" selected="selected">Select one</option>                
        <#list orgTypeOpts?keys as key>             
            <option value="${key}"  <#if orgTypeValue = key>selected</#if>>${orgTypeOpts[key]}</option>            
        </#list>
    </select>
  </p>

  <div class="fullViewOnly">        
  <p>
    <label for="relatedIndLabel">### Name ${requiredHint}</label>
    <input type="text" name="orgLabel" id="relatedIndLabel" size="50" class="acSelector" value="${orgLabelValue}" <#if (disabledVal!?length > 0)>disabled="${disabledVal}"</#if>  >
  </p>
  <#if editMode = "edit">
			<input type="hidden" id="orgLabel"  name="orgLabel" value="${orgLabelValue}"/>
   </#if>

    <@lvf.acSelection urls.base "org" "org" existingOrgValue/>


    <label for="positionTitle">Position Title ${requiredHint}</label>
    <input  size="30"  type="text" id="positionTitle" name="positionTitle" value="${positionTitleValue}" role="input" />

      <label for="positionType">Position Type ${requiredHint}</label>
      <#assign posnTypeOpts = editConfiguration.pageData.positionType />
      <select name="positionType" style="margin-top:-2px" >
          <option value="" <#if positionTypeValue == "">selected</#if>>Select one</option>                
          <#list posnTypeOpts?keys as key>             
              <option value="${key}"  <#if positionTypeValue == key>selected</#if>>${posnTypeOpts[key]}</option>         
          </#list>
      </select>
      <p></p>
      <#--Need to draw edit elements for dates here-->
       <#if htmlForElements?keys?seq_contains("startField")>
  			<label class="dateTime" for="startField">Start</label>
  			${htmlForElements["startField"]} ${yearHint}
       </#if>
       <p></p>
       <#if htmlForElements?keys?seq_contains("endField")>
  			<label class="dateTime" for="endField">End</label>
  		 	${htmlForElements["endField"]} ${yearHint}
       </#if>

    	<#--End draw elements-->
    	
      <input type="hidden" name = "editKey" value="${editKey}" role="input"/>

   </div>
      <p class="submit">
        <#if editMode == "edit">  
            <input type="submit" id="submit" name="submit-${formAction}" value="${submitButtonText}" class="submit" /> 
        <#else>
            <input type="submit" id="submit" name="submit-${formAction}" value="${submitButtonText}" class="submit" /> 
        </#if>

        <span class="or"> or </span><a class="cancel" href="${editConfiguration.cancelUrl}" title="Cancel">Cancel</a>
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
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}
