<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
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

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#if editConfiguration.objectUri?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#--Retrieve variables needed-->
<#assign advisorValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingAdvisor") />
<#assign advisorLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "advisorLabel") />
<#assign advisorLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "advisorLabelDisplay") />
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName") />
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName") />
<#assign advisingRelTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "advisingRelType") />
<#assign advisingRelLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "advisingRelLabel") />
<#assign subjAreaValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingSubjArea") />
<#assign subjAreaLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "subjAreaLabel") />
<#assign subjAreaLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "subjAreaLabelDisplay") />
<#assign degreeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "degree") />
<#assign acFilterForIndividuals =  "['" + editConfiguration.subjectUri + "']" />
<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />

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

<h2>${titleVerb}&nbsp;${i18n().advisee_relationship_entry_for} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if advisorLabelDisplayValue?has_content >
        <#assign advisorLabelValue = advisorLabelDisplayValue />
    </#if>
    <#if subjAreaLabelDisplayValue?has_content >
        <#assign subjAreaLabelValue = subjAreaLabelDisplayValue />
    </#if>
    
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alt="${i18n().error_alert_icon}" />
        <p>
        <#--Checking if any required fields are empty-->
        <#if lvf.submissionErrorExists(editSubmission, "advisingRelType")>
 	        ${i18n().select_advising_relationship_type}<br />
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
    	    <#elseif errorFieldName == "advisingRelType">
    	    <#else>
    	        ${submissionErrors[errorFieldName]}
	        </#if><br />
        </#list>
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="personHasAdvisingRelationship" role="region">        
    
    <form id="personHasAdvisingRelationship" class="customForm noIE67" action="${submitUrl}"  role="add/edit AdvisingRelationship">
    <p class="inline">    
      <label for="orgType">${i18n().advising_relationship_type}<#if editMode != "edit"> ${requiredHint}<#else>:</#if></label>
      <#assign advisingRelTypeOpts = editConfiguration.pageData.advisingRelType />
      <#if editMode == "edit">
        <#list advisingRelTypeOpts?keys as key>             
            <#if advisingRelTypeValue = key >
                <span class="readOnly">${advisingRelTypeOpts[key]}</span>
                <input type="hidden" id="typeSelectorInput" name="advisingRelType"  value="${advisingRelTypeValue}" >
            </#if>
        </#list>
      <#else>
        <select id="selector" name="advisingRelType"  ${disabledVal} >
            <option value="" selected="selected">${i18n().select_one}</option>                
            <#list advisingRelTypeOpts?keys as key>             
                <option value="${key}"  <#if advisingRelTypeValue = key>selected</#if>>${advisingRelTypeOpts[key]}</option>            
            </#list>
        </select>
      </#if>
    </p>
    <p >
        <label for="advisor">${i18n().advisor_capitalized}: ${i18n().last_name}  ${requiredHint}<span style="padding-left:322px">${i18n().first_name}  ${requiredHint}</span></label>
            <input class="acSelector" size="50"  type="text" acGroupName="advisor" id="advisor" name="advisorLabel" value="${advisorLabelValue}" >
            <input type="text" size="50"  id="maskLabelBuilding" name="maskLabelBuilding" value="" style="display:none" >
            <input  size="30"  type="text" id="firstName" name="firstName" value="${firstNameValue}" ><br />
            <input type="hidden" id="lastName" name="lastName" value="">
            <input class="display" type="hidden" acGroupName="advisor" id="advisorDisplay" name="advisorLabelDisplay" value="${advisorLabelDisplayValue}" >
    </p>

    <div class="acSelection" acGroupName="advisor" id="advisorAcSelection">
        <p class="inline">
            <label>${i18n().selected_advisor}
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
            <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="advisorUri" name="existingAdvisor" value="${advisorValue}" ${flagClearLabelForExisting}="true"/>
    </div>

    <p>
        <label for="SubjectArea">${i18n().subject_area}</label>
              <input class="acSelector" size="50"  type="text" id="SubjectArea" acGroupName="SubjectArea" name="subjAreaLabel" value="${subjAreaLabelValue}" />
              <input class="display" type="hidden" id="SubjectAreaDisplay" acGroupName="SubjectArea" name="subjAreaLabelDisplay" value="${subjAreaLabelDisplayValue}" />
    </p>
      <div class="acSelection" acGroupName="SubjectArea">
          <p class="inline">
              <label>${i18n().selected_subject_area}:</label>
              <span class="acSelectionInfo"></span>
              <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
              <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>
          </p>
          <#--When no autocomplete value is selected, the value of this field will be set to the 'blank sentinel'.
          When an autocomplete value is selected, the 'flagClearLabelField' attribute will clear out the associated label input. -->
          <input class="acUriReceiver" type="hidden" id="subjAreaUri" name="existingSubjArea" value="${subjAreaValue}"  ${flagClearLabelForExisting}="true"/>
      </div>

    <p>
    <label for="degreeUri">${i18n().degree_candidacy}</label>      
  
    <#assign degreeOpts = editConfiguration.pageData.degree />  
    <select name="degree" id="degreeUri" >
      <option value="${blankSentinel}" <#if degreeValue = "">selected</#if>>${i18n().select_one}</option>        
             <#list degreeOpts?keys as key>                 
      <option value="${key}" <#if degreeValue = key>selected</#if>>${degreeOpts[key]}</option>                    
      </#list>                                
    </select>    
    </p>

    <p>
        <h4>${i18n().years_participating}</h4>
    </p>
    <#--Need to draw edit elements for dates here-->
    <#assign htmlForElements = editConfiguration.pageData.htmlForElements />
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
    <input type="hidden" id="advisingRelLabel" name="advisingRelLabel" value="${advisingRelLabelValue}"/>
    <input type="hidden" id="saveAdvisorLabel" name="saveAdvisorLabel" value="${advisorLabelValue}"/>
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

   <p class="submit">
        <input type="submit" class="submit" value="${submitButtonText}"/><span class="or"> ${i18n().or} </span>
        <a class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>

</form>

</section>
<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >
<#assign doNotRemoveOriginalObject = "true" />
<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true&stem=true',
    acTypes: {advisor: 'http://xmlns.com/foaf/0.1/Person', SubjectArea: 'http://www.w3.org/2004/02/skos/core#Concept'},
    editMode: '${editMode}',
    defaultTypeName: 'advisor',
    multipleTypeNames: {advisor: 'advisor', SubjectArea: 'Subject Area'},
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acFilterForIndividuals: ${acFilterForIndividuals},
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
};
<#--Removing this line for now from above : newUriSentinel : '${newUriSentinel}',-->
<#--Also removed this: ,doNotRemoveOriginalObject: '${doNotRemoveOriginalObject}'-->
var i18nStrings = {
    selectAnExisting: '${i18n().select_an_existing}',
    orCreateNewOne: '${i18n().or_create_new_one}',
    selectedString: '${i18n().selected}',
    advisingString: '${i18n().advising}',
    advisingRelationshipString: '${i18n().advising_relationship}'
};
</script>

<script type="text/javascript">
$(document).ready(function(){
    adviseeRelUtils.onLoad('${editConfiguration.subjectName}', '${blankSentinel}');
});
</script> 
 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/adviseeRelationshipUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}


