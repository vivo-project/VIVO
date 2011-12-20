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

<#--Retrieve variables needed-->
<#assign orgTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgType")/>
<#assign orgLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabel") />
<#assign deptValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "dept") />
<#assign infoValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "info") />
<#assign majorFieldValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "majorField") />
<#assign degreeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "degree") />
<#assign existingOrgValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "org") />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit">    
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Educational Training">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Educational Training">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />


<h2>${titleVerb}&nbsp;educational training entry for ${subjectName}${editConfiguration.subjectName}</h2>

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
 	        Please select a value in the Organization Type field.
        </#if>
        <#--Checking if Org Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "orgLabel")>
 	        Please enter or select a value in the Name field.
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
        <select id="typeSelector" name="orgType"  ${disabledVal}>
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
        <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="orgLabel" value="${orgLabelValue}" <#if (disabledVal!?length > 0)>disabled="${disabledVal}"</#if> />
    </p>
    
    <#--Store values in hidden fields-->
    <#if editMode="edit">
    	<input type="hidden" name="orgType" id="orgType" value="${orgTypeValue}"/>
    	<input type="hidden" name="orgLabel" id="orgLabel" value="${orgLabelValue}"/>
    </#if>
    
    <@lvf.acSelection urls.base "org" "org" existingOrgValue/>
    
    <p>
        <label for="dept">Department or School Name within the ###</label>
        <input  size="60"  type="text" id="dept" name="dept" value="${deptValue}" />
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
            <span class="hint">&nbsp;(e.g., Postdoctoral training or Transferred)</span>
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
    submitButtonTextType: 'compound',
    defaultTypeName: 'organization'
};
</script>

</section>
 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}


