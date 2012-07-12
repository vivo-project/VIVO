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

<#assign blankSentinel = "" />
<#if editConfigurationConstants?has_content && editConfigurationConstants?keys?seq_contains("BLANK_SENTINEL")>
	<#assign blankSentinel = editConfigurationConstants["BLANK_SENTINEL"] />
</#if>

<#--This flag is for clearing the label field on submission for an existing object being selected from autocomplete.
Set this flag on the input acUriReceiver where you would like this behavior to occur. -->
<#assign flagClearLabelForExisting = "flagClearLabelForExisting" />


<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#--Retrieve variables needed-->
<#assign existingPresentationValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingPresentation") />
<#assign presentationLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "presentationLabel") />
<#assign presentationLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "presentationLabelDisplay") />
<#assign presentationTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "presentationType") />
<#assign roleLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleLabel") />
<#assign conferenceValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingConference") />
<#assign conferenceLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conferenceLabel") />
<#assign conferenceLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conferenceLabelDisplay") />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit">    
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Save Changes">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Entry">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<h2>${titleVerb}&nbsp;presentation entry for ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if conferenceLabelDisplayValue?has_content >
        <#assign conferenceLabelValue = conferenceLabelDisplayValue />
    </#if>
    <#if presentationLabelDisplayValue?has_content >
        <#assign presentationLabelValue = presentationLabelDisplayValue />
    </#if>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--Checking if any required fields are empty-->
        <#if lvf.submissionErrorExists(editSubmission, "presentationLabel")>
 	        Please select an existing value or enter a new value in the Name field.
        </#if> 
        <#if lvf.submissionErrorExists(editSubmission, "roleLabel")>
 	        Please enter a new value in the Role field.
        </#if> 
        <#list submissionErrors?keys as errorFieldName>
        	<#if errorFieldName == "startField">
        	    <#if submissionErrors[errorFieldName]?contains("before")>
        	        The Start Year must be earlier than the End Year.
        	    <#else>
        	        ${submissionErrors[errorFieldName]}
        	    </#if>
        	    
        	<#elseif errorFieldName == "endField">
    	        <#if submissionErrors[errorFieldName]?contains("after")>
    	            The End Year must be later than the Start Year.
    	        <#else>
    	            ${submissionErrors[errorFieldName]}
    	        </#if>
	        </#if><br />
        </#list>
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="addPresenterRoleToPerson" role="region">        
    
    <form id="addPresenterRoleToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit Presentation">
    <p class="inline">    
      <label for="presentationType">Presentation Type<#if editMode != "edit"> ${requiredHint}<#else>:</#if></label>
      <#assign presentationTypeOpts = editConfiguration.pageData.presentationType />
      <select id="typeSelector" name="presentationType" acGroupName="presentation">
        <option value="" selected="selected">Select one</option>                
        <#list presentationTypeOpts?keys as key>             
            <option value="${key}" <#if presentationTypeValue = key>selected</#if>> <#if presentationTypeOpts[key] == "Other">Presentation<#else>${presentationTypeOpts[key]}</#if></option>            
        </#list>
    </select>
    </p>

    <div class="fullViewOnly">        
    <p>
        <label for="presentation">### Name ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="presentation" acGroupName="presentation" name="presentationLabel" value="${presentationLabelValue}">
            <input class="display" type="hidden" id="presentationDisplay" acGroupName="presentation" name="presentationLabelDisplay" value="${presentationLabelDisplayValue}">
    </p>

    <div class="acSelection" acGroupName="presentation">
        <p class="inline">
            <label></label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="presentationUri" name="existingPresentation" value="${existingPresentationValue}" ${flagClearLabelForExisting}="true" />
    </div>
    <p><label for="roleLabel">Role in ### ${requiredHint} <span class="hint">(e.g., Moderator, Speaker, Panelist)</span></label>
        <input  size="50"  type="text" id="roleLabel" name="roleLabel" value="${roleLabelValue}" />
    </p>
  <p>
      <label for="org">Presented At</label>
      <input  class="acSelector" size="50" acGroupName="conference" type="text" id="conference" name="conferenceLabel" value="${conferenceLabelValue}" />
      <input  class="display" acGroupName="conference" type="hidden" id="conferenceDisplay" name="conferenceLabelDisplay" value="${conferenceLabelDisplayValue}" />
  </p>
  <div class="acSelection" acGroupName="conference">
      <p class="inline">
          <label>Selected conference:</label>
          <span class="acSelectionInfo"></span>
          <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
          <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
      </p>
      <input class="acUriReceiver" type="hidden" id="conferenceUri" name="existingConference" value="${conferenceValue}" ${flagClearLabelForExisting}="true" />
  </div>
    <p>
        <h4 class="label">Years of Participation in ###</h4>
    </p>
    <#--Need to draw edit elements for dates here-->
    <#assign htmlForElements = editConfiguration.pageData.htmlForElements />
    <#if htmlForElements?keys?seq_contains("startField")>
        <label class="dateTime" for="startField">Start</label>
		${htmlForElements["startField"]} ${yearHint}
    </#if>
    <br/>
    <#if htmlForElements?keys?seq_contains("endField")>
		<label class="dateTime" for="endField">End</label>
	 	${htmlForElements["endField"]} ${yearHint}
    </#if>
	<#--End draw elements-->

    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

    </div>

    <p class="submit">
        <input type="submit" class="submit" value="${submitButtonText}"/><span class="or"> or </span>
        <a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

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
    acTypes: {conference: 'http://purl.org/ontology/bibo/Conference'},
    editMode: '${editMode}',
    defaultTypeName: 'presentation',
    multipleTypeNames: {presentation: 'presentation', conference: 'conference'},
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
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



