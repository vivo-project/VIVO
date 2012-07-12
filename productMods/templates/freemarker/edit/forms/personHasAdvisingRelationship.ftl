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
<#assign adviseeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingAdvisee") />
<#assign adviseeLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "adviseeLabel") />
<#assign adviseeLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "adviseeLabelDisplay") />
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

<h2>${titleVerb}&nbsp;advising relationship entry for ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if adviseeLabelDisplayValue?has_content >
        <#assign adviseeLabelValue = adviseeLabelDisplayValue />
    </#if>
    <#if subjAreaLabelDisplayValue?has_content >
        <#assign subjAreaLabelValue = subjAreaLabelDisplayValue />
    </#if>
    
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--Checking if any required fields are empty-->
        <#if lvf.submissionErrorExists(editSubmission, "advisingRelType")>
 	        Please select an Advising Relationship Type.<br />
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
      <label for="orgType">Advising Relationship Type<#if editMode != "edit"> ${requiredHint}<#else>:</#if></label>
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
            <option value="" selected="selected">Select one</option>                
            <#list advisingRelTypeOpts?keys as key>             
                <option value="${key}"  <#if advisingRelTypeValue = key>selected</#if>>${advisingRelTypeOpts[key]}</option>            
            </#list>
        </select>
      </#if>
    </p>
    <p >
        <label for="advisee">Advisee: Last Name  ${requiredHint}<span style="padding-left:322px">First Name  ${requiredHint}</span></label>
            <input class="acSelector" size="50"  type="text" acGroupName="advisee" id="advisee" name="adviseeLabel" value="${adviseeLabelValue}" >
            <input  size="30"  type="text" id="firstName" name="firstName" value="${firstNameValue}" ><br />
            <input type="hidden" id="lastName" name="lastName" value="">
            <input class="display" type="hidden" acGroupName="advisee" id="adviseeDisplay" name="adviseeLabelDisplay" value="${adviseeLabelDisplayValue}" >
    </p>

    <div class="acSelection" acGroupName="advisee" id="adviseeAcSelection">
        <p class="inline">
            <label>Selected Advisee:</label>
            <span class="acSelectionInfo" id="arf"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="adviseeUri" name="existingAdvisee" value="${adviseeValue}" ${flagClearLabelForExisting}="true"/>
    </div>

    <p>
        <label for="SubjectArea">Subject Area</label>
              <input class="acSelector" size="50"  type="text" id="SubjectArea" acGroupName="SubjectArea" name="subjAreaLabel" value="${subjAreaLabelValue}" />
              <input class="display" type="hidden" id="SubjectAreaDisplay" acGroupName="SubjectArea" name="subjAreaLabelDisplay" value="${subjAreaLabelDisplayValue}" />
    </p>
      <div class="acSelection" acGroupName="SubjectArea">
          <p class="inline">
              <label>Subject Area</label>
              <span class="acSelectionInfo"></span>
              <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
              <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
          </p>
          <#--When no autocomplete value is selected, the value of this field will be set to the 'blank sentinel'.
          When an autocomplete value is selected, the 'flagClearLabelField' attribute will clear out the associated label input. -->
          <input class="acUriReceiver" type="hidden" id="subjAreaUri" name="existingSubjArea" value="${subjAreaValue}"  ${flagClearLabelForExisting}="true"/>
      </div>

    <p>
    <label for="degreeUri">Degree Candidacy</label>      
  
    <#assign degreeOpts = editConfiguration.pageData.degree />  
    <select name="degree" id="degreeUri" >
      <option value="" <#if degreeValue = "">selected</#if>>Select one</option>        
             <#list degreeOpts?keys as key>                 
      <option value="${key}" <#if degreeValue = key>selected</#if>>${degreeOpts[key]}</option>                    
      </#list>                                
    </select>    
    </p>

    <p>
        <h4>Years of Participation</h4>
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
    <input type="hidden" id="advisingRelLabel" name="advisingRelLabel" value="${advisingRelLabelValue}"/>
    <input type="hidden" id="saveAdviseeLabel" name="saveAdviseeLabel" value="${adviseeLabelValue}"/>
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

   <p class="submit">
        <input type="submit" class="submit" value="${submitButtonText}"/><span class="or"> or </span>
        <a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

</form>

</section>
<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >
<#assign doNotRemoveOriginalObject = "true" />
<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true&stem=true',
    acTypes: {advisee: 'http://xmlns.com/foaf/0.1/Person', SubjectArea: 'http://www.w3.org/2004/02/skos/core#Concept'},
    editMode: '${editMode}',
    defaultTypeName: 'advisee',
    multipleTypeNames: {advisee: 'advisee', SubjectArea: 'Subject Area'},
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acFilterForIndividuals: ${acFilterForIndividuals},
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
};
<#--Removing this line for now from above : newUriSentinel : '${newUriSentinel}',-->
<#--Also removed this: ,doNotRemoveOriginalObject: '${doNotRemoveOriginalObject}'-->
</script>

<script type="text/javascript">
$(document).ready(function(){
    advisingRelUtils.onLoad('${editConfiguration.subjectName}', '${blankSentinel}');
});
</script> 
 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/advisingRelationshipUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}


