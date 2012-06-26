<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#assign subjectName=""/>
<#assign roleActivityUri="mysteryRoleActivityURI"/>
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
<#assign orgTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgType")/>
<#assign orgLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabel") />
<#assign orgLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabelDisplay") />
<#assign deptValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "dept") />
<#assign infoValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "info") />
<#assign majorFieldValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "majorField") />
<#assign degreeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "degree") />
<#assign existingOrgValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingOrg") />
<#assign trainingTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "trainingType")/>

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


<h2>${titleVerb}&nbsp;educational training entry for ${subjectName}${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if orgLabelDisplayValue?has_content >
        <#assign orgLabelValue = orgLabelDisplayValue />
    </#if>
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
 	        Please select a value in the Organization Type field.
        </#if>
        <#--Checking if Org Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "orgLabel")>
 	        Please enter or select a value in the Name field.
        </#if>
        <#--Checking if Training Type field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "trainingType")>
 	        Please select a value in the Type of Educational Training field.<br />
        </#if>
        
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="personHasEducationalTraining" role="region">        
    
    <form id="personHasEducationalTraining" class="customForm noIE67" action="${submitUrl}"  role="add/edit educational training">

    
    <p class="inline">    
        <label for="orgType">Organization Type ${requiredHint}</label>
        <#assign orgTypeOpts = editConfiguration.pageData.orgType />
<#--
        <#if editMode == "edit">
          <#list orgTypeOpts?keys as key>             
              <#if orgTypeValue = key >
                <span class="readOnly" id="typeSelectorSpan">${orgTypeOpts[key]}</span> 
                <input type="hidden" id="typeSelectorInput" name="orgType" acGroupName="org" value="${orgTypeValue}" >
              </#if>           
          </#list>
        <#else>
            <select id="typeSelector" name="orgType" acGroupName="org" ${disabledVal}>
                <option value="" selected="selected">Select one</option>                
                <#list orgTypeOpts?keys as key>             
                    <#if orgTypeValue = key>
                        <option value="${key}"  selected >${orgTypeOpts[key]}</option>     
                    <#else>
                        <option value="${key}">${orgTypeOpts[key]}</option>
                    </#if>             
                </#list>
            </select>
        </#if>   
-->
<select id="typeSelector" name="orgType" acGroupName="org">
    <option value="" selected="selected">Select one</option>                
    <#list orgTypeOpts?keys as key>             
        <#if orgTypeValue = key>
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
        <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="orgLabel" acGroupName="org" value="${orgLabelValue}"  />
        <input class="display" type="hidden" id="orgDisplay" acGroupName="org" name="orgLabelDisplay" value="${orgLabelDisplayValue}">
    </p>
        
    <div class="acSelection" acGroupName="org">
        <p class="inline">
            <label>Selected Organization:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="orgUri" name="existingOrg" value="${existingOrgValue}" ${flagClearLabelForExisting}="true" />
    </div>
    
    <label for="positionType">Type of Educational Training ${requiredHint}</label>
    <#assign trainingTypeOpts = editConfiguration.pageData.trainingType />
    <select name="trainingType" style="margin-top:-2px" >
        <option value="" <#if trainingTypeValue == "">selected</#if>>Select one</option>                
        <#list trainingTypeOpts?keys as key>             
            <option value="${key}"  <#if trainingTypeValue == key>selected</#if>><#if trainingTypeOpts[key] == "Other">Academic Studies or Other Training<#else>${trainingTypeOpts[key]}</#if></option>         
        </#list>
    </select>
    <p>
        <label for="dept">Department or School Name within the ###</label>
        <input  size="50"  type="text" id="dept" name="dept" value="${deptValue}" />
    </p>
    
    <div class="entry">
      <label for="degreeUri">Degree</label>      
    
      <#assign degreeOpts = editConfiguration.pageData.degree />  
      <select name="degree" id="degreeUri" >
        <option value="" <#if degreeValue = "">selected</#if>>Select one</option>        
               <#list degreeOpts?keys as key>                 
        <option value="${key}" <#if degreeValue = key>selected</#if>>${degreeOpts[key]}</option>                    
        </#list>                                
      </select>    
    </div>
    
    <p>    
        <label for="majorField">Major Field of Degree</label>
        <input type="text" id="majorField" name="majorField" size="30" value="${majorFieldValue}"/>   
    </p>   
          
    <p>    
        <label for="info">Supplemental Information 
            <span class="hint">&nbsp;(e.g., Thesis title, Transfer info, etc.)</span>
        </label>
        <input  size="60"  type="text" id="info" name="info" value="${infoValue}" />
        
    </p>
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
  </div>    
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>
    <p class="submit">
        <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span>
        <a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

</form>


<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true&stem=true',
    editMode: '${editMode}',
    defaultTypeName: 'organization',
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
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


