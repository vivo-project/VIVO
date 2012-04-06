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

<h2>${titleVerb}&nbsp;award or honor for ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if orgLabelDisplayValue?has_content >
        <#assign orgLabelValue = orgLabelDisplayValue />
    </#if>
    <#if awardLabelDisplayValue?has_content >
        <#assign awardLabelValue = awardLabelDisplayValue />
    </#if>
        
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--Checking if any required fields are empty-->
        <#if lvf.submissionErrorExists(editSubmission, "awardLabel")>
 	        Please select an existing value or enter a new value in the Award or Honor Name field.
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

<section id="personHasAwardOrHonor" role="region">        
    
    <form id="personHasAwardOrHonor" class="customForm noIE67" action="${submitUrl}"  role="add/edit AwardOrHonor">
    <p>
        <label for="relatedIndLabel">Award or Honor Name ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="award" acGroupName="award" name="awardLabel" value="${awardLabelValue}">
            <input class="display" type="hidden" id="awardDisplay" acGroupName="award" name="awardLabelDisplay" value="${awardLabelDisplayValue}">
    </p>

    <div class="acSelection" acGroupName="award" id="awardAcSelection">
        <p class="inline">
            <label>Selected Award:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="awardUri" name="existingAward" value="${awardValue}" ${flagClearLabelForExisting}="true" />
    </div>
  <p>
      <label for="org">Conferred by</label>
      <input  class="acSelector" size="50" acGroupName="org" type="text" id="org" name="orgLabel" value="${orgLabelValue}" />
      <input  class="display" type="hidden" id="orgDisplay" acGroupName="org" name="orgLabelDisplay" value="${orgLabelDisplayValue}" />
  </p>
  
  <div class="acSelection" acGroupName="org" id="orgAcSelection">
      <p class="inline">
          <label>Selected Conferrer:</label>
          <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
      </p>
      <input class="acUriReceiver" type="hidden" id="orgUri" name="existingOrg" value="${orgValue}" ${flagClearLabelForExisting}="true"/>
  </div>
<#if editMode == "edit">
  <div class="hidden" id="hiddenOrgLabel">
    <p class="inline">
        <label>Selected Conferrer: </label>
        <span class="readOnly">${orgLabelValue}</span>
    </p>
  </div>
</#if>
    <p>
        <label for="description">Description</label>
        <input  size="50"  type="text" id="description" name="description" value="${descriptionValue}" />
    </p>
    <#assign htmlForElements = editConfiguration.pageData.htmlForElements />
    <p>
        <label for="yearAwardedDisplay" id="yearAwarded">Year Awarded</label>
        <input  size="4"  type="text" id="yearAwardedDisplay" name="yearAwardedDisplay" value="${yearAwardedDisplayValue}" /> ${yearHint}
    </p>
    <p>
        <h4>Years Inclusive <span class="hint">&nbsp;(e.g., for multi-year awards)</span></h4>
    </p>
    <#--Need to draw edit elements for dates here-->
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

    <input type="hidden" id="awardReceiptLabel" name="awardReceiptLabel" value="${awardReceiptLabelValue}"/>
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

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
    acTypes: {award: 'http://vivoweb.org/ontology/core#Award', org: 'http://xmlns.com/foaf/0.1/Organization'},
    editMode: '${editMode}',
    defaultTypeName: 'award',
    multipleTypeNames: {award: 'award', org: 'organization'},
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'};
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



