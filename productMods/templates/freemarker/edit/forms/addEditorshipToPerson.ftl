<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#assign subjectName=""/>
<#assign orgLabel="mysteryOrgLabel"/>

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
<#assign documentTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "documentType")/>
<#assign documentLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "documentLabel") />
<#assign documentLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "documentLabelDisplay") />
<#assign existingDocumentValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingDocument") />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit" || editMode == "repair">    
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


<h2>${titleVerb}&nbsp;${i18n().editor_of_entry} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if orgLabelDisplayValue?has_content >
        <#assign orgLabelValue = orgLabelDisplayValue />
    </#if>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#--Checking if Org Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "documentType")>
 	        ${i18n().select_document_type}
        </#if>
        <#--Checking if Org Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "documentLabel")>
 	        ${i18n().select_a_document_name}
        </#if>
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="addEditorshipToPerson" role="region">        
    
    <form id="addEditorshipToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit editorship">

    
    <p class="inline">    
        <label for="orgType">${i18n().document_type_capitalized} ${requiredHint}</label>
        <#assign docTypeOpts = editConfiguration.pageData.documentType />
        <select id="typeSelector" name="documentType" acGroupName="document">
            <option value="" selected="selected">${i18n().select_one}</option>                
            <#list docTypeOpts?keys as key>             
                <#if documentTypeValue = key>
                    <option value="${key}"  selected >${docTypeOpts[key]}</option>     
                <#else>
                    <option value="${key}">${docTypeOpts[key]}</option>
                </#if>
            </#list>
        </select>
    </p>     
    
    <p>
        <label for="relatedIndLabel">${i18n().document_name_capitalized} ${requiredHint}</label>
        <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="documentLabel" acGroupName="document" value="${documentLabelValue}"  />
        <input class="display" type="hidden" id="documentDisplay" acGroupName="document" name="documentLabelDisplay" value="${documentLabelDisplayValue}">
    </p>
        
    <div class="acSelection" acGroupName="document">
        <p class="inline">
            <label>${i18n().selected_document}:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
            <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="documentUri" name="existingDocument" value="${existingDocumentValue}" ${flagClearLabelForExisting}="true" />
    </div>
    
                                    
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
    acTypes: {document: 'http://purl.org/ontology/bibo/Document'},
    defaultTypeName: 'document',
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
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}


