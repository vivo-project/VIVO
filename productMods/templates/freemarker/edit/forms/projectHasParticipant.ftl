<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#assign subjectName=""/>
<#assign roleActivityUri="mysteryRoleActivityURI"/>
<#assign personLabel="mysteryPersonLabel"/>

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

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

<#--Retrieve variables needed-->
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName")/>
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName") />
<#assign personLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "personLabel") />
<#assign personLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "personLabelDisplay") />
<#assign existingPersonValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingPerson") />
<#assign roleLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleLabel") />

<#-- If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit" || editMode == "repair">    
        <#assign titleVerb="${i18n().edit_capitalized}">        
        <#assign submitButtonText="${i18n().save_changes}">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="${i18n().add_capitalized}">        
        <#assign submitButtonText="${i18n().create_entry}">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(${i18n().year_hint_format})</span>" />


<h2>${titleVerb}&nbsp;${i18n().researcher}&nbsp;${i18n().to} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if personLabelDisplayValue?has_content >
        <#assign personLabelValue = personLabelDisplayValue />
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
            <#else>
                ${submissionErrors[errorFieldName]}
	        </#if>
        </#list>
        <#--Checking if Person Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "personType")>
 	        ${i18n().select_person_type}
        </#if>
        <#--Checking if Person Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "personLabel")>
 	        ${i18n().select_an_person_name}
        </#if>
        <#--Checking if Training Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "trainingType")>
 	        ${i18n().select_educational_training_value}<br />
        </#if>
        
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="projectHasParticipant" role="region">        
    
    <form id="projectHasParticipant" class="customForm noIE67" action="${submitUrl}"  role="add/edit organizational training">
    
    <p >
        <label for="person">${i18n().person_capitalized}: ${i18n().last_name}  ${requiredHint}<span style="padding-left:322px">${i18n().first_name}  ${requiredHint}</span></label>
            <input class="acSelector" size="50"  type="text" acGroupName="person" id="person" name="personLabel" value="${personLabelValue}" >
            <input type="text" size="50"  id="maskLabelBuilding" name="maskLabelBuilding" value="" style="display:none" >
            <input  size="30"  type="text" id="firstName" name="firstName" value="${firstNameValue}" ><br />
            <input type="hidden" id="lastName" name="lastName" value="">
            <input class="display" type="hidden" acGroupName="person" id="personDisplay" name="personLabelDisplay" value="${personLabelDisplayValue}" >
    </p>
        
    <div class="acSelection" acGroupName="person">
        <p class="inline">
            <label>${i18n().selected_person}:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
            <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="personUri" name="existingPerson" value="${existingPersonValue}" ${flagClearLabelForExisting}="true" />
    </div>
    <p>
        <label for="dept">${i18n().researcher_role} ${requiredHint}</label>
        <input  size="50"  type="text" id="roleLabel" name="roleLabel" value="${roleLabelValue}" />
    </p>    
                                    
  	<#--End draw elements-->
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>
    <p class="submit">
         <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> ${i18n().or} </span>
         <a class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
     </p>

    <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>

</form>


<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true&stem=true',
    editMode: '${editMode}',
    acTypes: {person: 'http://xmlns.com/foaf/0.1/Person'},
    defaultTypeName: 'person',
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}',
    subjectName: '${editConfiguration.subjectName}'
};
var i18nStrings = {
    selectAnExisting: '${i18n().select_an_existing}',
    orCreateNewOne: '${i18n().or_create_new_one}',
    selectedString: '${i18n().selected}'
};

$(document).ready(function() {
    projectHasParticipantUtils.onLoad('${blankSentinel}');
});
</script>

</section>
 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/projectHasParticipantUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}


