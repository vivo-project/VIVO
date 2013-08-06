<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a position history-->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign editMode = editConfiguration.pageData.editMode />

<#assign blankSentinel = "" />
<#if editConfigurationConstants?has_content && editConfigurationConstants?keys?seq_contains("BLANK_SENTINEL")>
	<#assign blankSentinel = editConfigurationConstants["BLANK_SENTINEL"] />
</#if>

<#--This flag is for clearing the label field on submission for an existing object being selected from autocomplete.
Set this flag on the input acUriReceiver where you would like this behavior to occur. -->
<#assign flagClearLabelForExisting = "flagClearLabelForExisting" />

<#--Get existing value for specific data literals and uris-->
<#assign orgTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgType")/>
<#assign existingOrgValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingOrg")/>
<#assign orgLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabel")/>
<#assign orgLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabelDisplay")/>
<#assign positionTitleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "positionTitle")/>
<#assign positionTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "positionType")/>

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#assign disabledVal = ""/>
<#if editMode == "edit">        
        <#assign formAction="${i18n().edit_capitalized}">        
        <#assign submitButtonText="${i18n().save_changes}">
        <#assign disabledVal="disabled">
<#else>
        <#assign formAction="${i18n().create_capitalized}">        
        <#assign submitButtonText="${i18n().create_entry}">
        <#assign disabledVal="">
</#if>

<#assign requiredHint="<span class='requiredHint'> *</span>"/> 
<#assign yearHint     = "<span class='hint'>(${i18n().year_hint_format})</span>" />

<h2>${formAction} ${i18n().posn_entry_for} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if orgLabelDisplayValue?has_content >
        <#assign orgLabelValue = orgLabelDisplayValue />
    </#if>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	<#if errorFieldName == "startField">
        	    <#if submissionErrors[errorFieldName]?contains("before")>
        	        ${i18n().start_year_must_precede_end}
        	    <#else>
        	        ${submissionErrors[errorFieldName]}
        	    </#if>
        	    <br />
        	<#elseif errorFieldName == "endField">
    	        <#if submissionErrors[errorFieldName]?contains("after")>
    	            ${i18n().end_year_must_be_later}
    	        <#else>
    	            ${submissionErrors[errorFieldName]}
    	        </#if>
	        </#if>
        </#list>
        <#--Checking if Org Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "orgType")>
 	        ${i18n().select_organization_type}<br />
        </#if>
        <#--Checking if Org Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "orgLabel")>
 	        ${i18n().select_an_organization_name}<br />
        </#if>
        <#--Checking if Position Title field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "positionTitle")>
 	        ${i18n().enter_posn_title_value}<br />
        </#if>
        <#--Checking if Position Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "positionType")>
 	        ${i18n().enter_posn_type_value}<br />
        </#if>
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<form class="customForm" action ="${submitUrl}" class="customForm noIE67" role="${formAction} position entry">
  <p class="inline">    
    <label for="orgType">${i18n().org_type_capitalized}<#if editMode != "edit"> ${requiredHint}<#else>:</#if></label>
    <#assign orgTypeOpts = editConfiguration.pageData.orgType />
<#--
    <#if editMode == "edit">
      <#list orgTypeOpts?keys as key>             
          <#if orgTypeValue = key >
            <span class="readOnly" id="typeSelectorSpan">${orgTypeOpts[key]}</span> 
            <input type="hidden" id="typeSelectorInput" name="orgType" acGroupName="organization" value="${orgTypeValue}" >
          </#if>           
      </#list>
    <#else>
    </#if>
-->
<select id="typeSelector" name="orgType" acGroupName="organization">
    <option value="" selected="selected">${i18n().select_one}</option>                
    <#list orgTypeOpts?keys as key>             
        <option value="${key}"  <#if orgTypeValue = key>selected</#if>>${orgTypeOpts[key]}</option>            
    </#list>
</select>
  </p>

  <p>
    <label for="relatedIndLabel">${i18n().organization_capitalized} ${i18n().name_capitalized} ${requiredHint}</label>
    <input type="text" name="orgLabel" id="orgLabel" acGroupName="organization" size="50" class="acSelector" value="${orgLabelValue}" >
    <input class="display" type="hidden" id="orgDisplay" acGroupName="organization" name="orgLabelDisplay" value="${orgLabelDisplayValue}">
  </p>
    <div class="acSelection" acGroupName="organization">
        <p class="inline">
            <label>${i18n().selected_organization}:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
            <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="orgUri" name="existingOrg" value="${existingOrgValue}" ${flagClearLabelForExisting}="true" />
    </div>
    
    <label for="positionTitle">${i18n().position_title} ${requiredHint}</label>
    <input  size="30"  type="text" id="positionTitle" name="positionTitle" value="${positionTitleValue}" role="input" />

      <label for="positionType">${i18n().position_type} ${requiredHint}</label>
      <#assign posnTypeOpts = editConfiguration.pageData.positionType />
      <select name="positionType" style="margin-top:-2px" >
          <option value="" <#if positionTypeValue == "">selected</#if>>${i18n().select_one}</option>                
          <#list posnTypeOpts?keys as key>             
              <option value="${key}"  <#if positionTypeValue == key>selected</#if>>${posnTypeOpts[key]}</option>         
          </#list>
      </select>
      <p></p>
      <#--Need to draw edit elements for dates here-->
       <#if htmlForElements?keys?seq_contains("startField")>
  			<label class="dateTime" for="startField">${i18n().start_capitalized}</label>
  			${htmlForElements["startField"]} ${yearHint}
       </#if>
       <p></p>
       <#if htmlForElements?keys?seq_contains("endField")>
  			<label class="dateTime" for="endField">${i18n().end_capitalized}</label>
  		 	${htmlForElements["endField"]} ${yearHint}
       </#if>

    	<#--End draw elements-->
    	
      <input type="hidden" name = "editKey" value="${editKey}" role="input"/>

      <p class="submit">
        <#if editMode == "edit">  
            <input type="submit" id="submit" name="submit-${formAction}" value="${submitButtonText}" class="submit" /> 
        <#else>
            <input type="submit" id="submit" name="submit-${formAction}" value="${submitButtonText}" class="submit" /> 
        </#if>

        <span class="or"> ${i18n().or} </span><a class="cancel" href="${editConfiguration.cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
      </p>
      <p class="requiredHint"  id="requiredLegend" >* ${i18n().required_fields}</p>
      
</form>

<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true',
    acTypes: {organization: 'http://xmlns.com/foaf/0.1/Organization'},
    editMode: '${editMode}',
    defaultTypeName: 'organization', // used in repair mode, to generate button text and org name field label
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
};
var i18nStrings = {
    selectAnExisting: '${i18n().select_an_existing}',
    orCreateNewOne: '${i18n().or_create_new_one}',
    selectedString: '${i18n().selected}'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}
