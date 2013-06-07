<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#if editConfiguration.objectUri?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#--The blank sentinel indicates what value should be put in a URI when no autocomplete result has been selected.
If the blank value is non-null or non-empty, n3 editing for an existing object will remove the original relationship
if nothing is selected for that object-->
<#assign blankSentinel = "" />
<#if editConfigurationConstants?has_content && editConfigurationConstants?keys?seq_contains("BLANK_SENTINEL")>
	<#assign blankSentinel = editConfigurationConstants["BLANK_SENTINEL"] />
</#if>

<#--This flag is for clearing the label field on submission for an existing object being selected from autocomplete.
Set this flag on the input acUriReceiver where you would like this behavior to occur. -->
<#assign flagClearLabelForExisting = "flagClearLabelForExisting" />

<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#--Retrieve variables needed-->
<#assign awardValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingAward") />
<#assign awardLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "awardLabel") />
<#assign awardReceiptLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "awardReceiptLabel") />
<#assign orgValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingOrg") />
<#assign orgLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabel") />
<#assign descriptionValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "description") />
<#assign yearAwardedDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "yearAwardedDisplay") />
<#assign orgLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabelDisplay") />
<#assign awardLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "awardLabelDisplay") />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit">    
        <#assign titleVerb="${i18n().edit_capitalized}">        
        <#assign submitButtonText="${i18n().save_changes}">
        <#assign disabledVal="disabled">
<#else>
<#assign titleVerb="${i18n().create_capitalized}">        
<#assign submitButtonText="${i18n().create_entry}">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(${i18n().year_hint_format})</span>" />

<h2>${titleVerb}&nbsp;${i18n().award_or_honor_for} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if orgLabelDisplayValue?has_content >
        <#assign orgLabelValue = orgLabelDisplayValue />
    </#if>
    <#if awardLabelDisplayValue?has_content >
        <#assign awardLabelValue = awardLabelDisplayValue />
    </#if>
        
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alt="${i18n().error_alert_icon}" />
        <p>
        <#--Checking if any required fields are empty-->
        <#if lvf.submissionErrorExists(editSubmission, "awardLabel")>
 	        ${i18n().select_Award_or_enter_name}
        </#if> 
        <#list submissionErrors?keys as errorFieldName>
        	<#if errorFieldName == "startField">
        	    <#if submissionErrors[errorFieldName]?contains("before")>
        	        ${i18n().start_year_must_precede_end}
        	    <#else>
        	        ${submissionErrors[errorFieldName]}
        	    </#if>
        	    
        	<#elseif errorFieldName == "endField">
    	        <#if submissionErrors[errorFieldName]?contains("after")>
    	            ${i18n().end_year_must_be_later}
    	        <#else>
    	            ${submissionErrors[errorFieldName]}
    	        </#if>
	        </#if><br />
        </#list>
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="personHasAwardOrHonor" role="region">        
    
    <form id="personHasAwardOrHonor" class="customForm noIE67" action="${submitUrl}"  role="add/edit AwardOrHonor">
    <p>
        <label for="relatedIndLabel">${i18n().award_honor_name} ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="award" acGroupName="award" name="awardLabel" value="${awardLabelValue}">
            <input class="display" type="hidden" id="awardDisplay" acGroupName="award" name="awardLabelDisplay" value="${awardLabelDisplayValue}">
    </p>

    <div class="acSelection" acGroupName="award" id="awardAcSelection">
        <p class="inline">
            <label>${i18n().selected_award}:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
            <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="awardUri" name="existingAward" value="${awardValue}" ${flagClearLabelForExisting}="true" />
    </div>
  <p>
      <label for="org">${i18n().conferred_by_capitalized}</label>
      <input  class="acSelector" size="50" acGroupName="org" type="text" id="org" name="orgLabel" value="${orgLabelValue}" />
      <input  class="display" type="hidden" id="orgDisplay" acGroupName="org" name="orgLabelDisplay" value="${orgLabelDisplayValue}" />
  </p>
  
  <div class="acSelection" acGroupName="org" id="orgAcSelection">
      <p class="inline">
          <label>${i18n().selected_conferred}:</label>
          <span class="acSelectionInfo"></span>
          <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
          <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
      </p>
      <input class="acUriReceiver" type="hidden" id="orgUri" name="existingOrg" value="${orgValue}" ${flagClearLabelForExisting}="true"/>
  </div>
<#if editMode == "edit">
  <div class="hidden" id="hiddenOrgLabel">
    <p class="inline">
        <label>${i18n().selected_conferred}: </label>
        <span class="readOnly">${orgLabelValue}</span>
    </p>
  </div>
</#if>
    <p>
        <label for="description">${i18n().description}</label>
        <input  size="50"  type="text" id="description" name="description" value="${descriptionValue}" />
    </p>
    <#assign htmlForElements = editConfiguration.pageData.htmlForElements />
    <p>
        <label for="yearAwardedDisplay" id="yearAwarded">${i18n().year_awarded}</label>
        <input  size="4"  type="text" id="yearAwardedDisplay" name="yearAwardedDisplay" value="${yearAwardedDisplayValue}" /> ${yearHint}
    </p>
    <p>
        <h4>${i18n().years_inclusive} <span class="hint">&nbsp;${i18n().award_hint}</span></h4>
    </p>
    <#--Need to draw edit elements for dates here-->
    <#if htmlForElements?keys?seq_contains("startField")>
        <label class="dateTime" for="startField">${i18n().start_capitalized}</label>
		${htmlForElements["startField"]} ${yearHint}
    </#if>
    <br/>
    <#if htmlForElements?keys?seq_contains("endField")>
		<label class="dateTime" for="endField">${i18n().end_capitalized}</label>
	 	${htmlForElements["endField"]} ${yearHint}
    </#if>
	<#--End draw elements-->

    <input type="hidden" id="awardReceiptLabel" name="awardReceiptLabel" value="${awardReceiptLabelValue}"/>
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

    <p class="submit">
         <input type="submit" class="submit" value="${submitButtonText}"/><span class="or"> ${i18n().or} </span>
         <a class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
     </p>

    <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>

    <#-- hide the html that gets written, and use java script to pass the value between the two -->
    <div class="hidden">
        <#if htmlForElements?keys?seq_contains("yearAwarded")>
		    ${htmlForElements["yearAwarded"]} 
        </#if>
    </div>

</form>

</section>


<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true&stem=true',
    acTypes: {award: 'http://vivoweb.org/ontology/core#Award', org: 'http://xmlns.com/foaf/0.1/Organization'},
    editMode: '${editMode}',
    defaultTypeName: 'award',
    multipleTypeNames: {award: 'award', org: 'organization'},
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
};
var i18nStrings = {
    selectAnOrganization: '${i18n().select_or_create_organization}',
    selectAnExisting: '${i18n().select_an_existing}',
    orCreateNewOne: '${i18n().or_create_new_one}',
    selectedString: '${i18n().selected}',
};

</script>

 
<script type="text/javascript">
 $(document).ready(function(){
    awardReceiptUtils.onLoad('${editMode}', '${editConfiguration.subjectName}', '${urls.base}/individual?uri=');
}); 
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/awardReceiptUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}



