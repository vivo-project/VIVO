<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign literalValues = editConfiguration.existingLiteralValues />
<#assign uriValues = editConfiguration.existingUriValues />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign showRoleLabelField = editConfiguration.pageData.showRoleLabelField />
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>
<#--Freemarker variables with default values that can be overridden by specific forms-->


<#--buttonText, typeSelectorLabel, numDateFields,  roleExamples-->
<#if !buttonText?has_content>
	<#assign buttonText = roleDescriptor />
</#if>
<#if !typeSelectorLabel?has_content>
	<#assign typeSelectorLabel = roleDescriptor />
</#if>
<#if !numDateFields?has_content>
	<#assign numDateFields = 2 />
</#if>

<#if !roleExamples?has_content>
	<#assign roleExamples = "" />
</#if>

<#--Setting values for titleVerb, submitButonText, and disabled Value-->
<#if editConfiguration.objectUri?has_content>
	<#assign titleVerb = "Edit"/>
	<#assign submitButtonText>Edit ${buttonText?capitalize}</#assign>
	<#if editMode = "repair">
		<#assign disabledVal = ""/>
	<#else>
		<#assign disabledVal = "disabled"/>
	</#if>
<#else>
	<#assign titleVerb = "Create"/>
	<#assign submitButtonText>${buttonText?capitalize}</#assign>
	<#assign disabledVal = ""/>
	<#assign editMode = "add" />
</#if>

<#--Get existing value for specific data literals and uris-->


<#--Get selected activity type value if it exists, this is alternative to below-->
<#assign activityTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleActivityType")/>

 <#--Get activity label value-->
<#assign activityLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "activityLabel") />


<#--Get role label-->
<#assign roleLabel = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleLabel") />

<#--For role activity uri-->
<#assign existingRoleActivityValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleActivity") />


<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<h2>${titleVerb}&nbsp;${roleDescriptor} entry for ${editConfiguration.subjectName}</h2>

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
        <#--Checking if Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "activityLabel")>
 	        Please enter or select a value in the Name field.<br />
        </#if>
        <#--Checking if role field is empty-->
        <#if lvf.submissionErrorExists(editSubmission, "roleLabel")>
    	    Please specify a role for this activity.
        </#if>
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="add${roleDescriptor?capitalize}RoleToPersonTwoStage" role="region">        
    
    <form id="add${roleDescriptor?capitalize}RoleToPersonTwoStage" class="customForm noIE67" action="${submitUrl}"  role="add/edit grant role">

       <p class="inline"><label for="typeSelector">${typeSelectorLabel?capitalize} ${requiredHint}</label>
           <select id="typeSelector" name="roleActivityType" 
           <#if disabledVal?has_content>
           	disabled = "${disabledVal}"
           </#if>
            >
            <#--Code below allows for selection of first 'select one' option if no activity type selected-->
            <#if activityTypeValue?has_content>
            	<#assign selectedActivityType = activityTypeValue />
            <#else>
            	<#assign selectedActivityType = "" />
            </#if>
           		<#assign roleActivityTypeSelect = editConfiguration.pageData.roleActivityType />
           		<#assign roleActivityTypeKeys = roleActivityTypeSelect?keys />
                <#list roleActivityTypeKeys as key>
                    <option value="${key}"<#if selectedActivityType = key>selected</#if>>${roleActivityTypeSelect[key]}</option>
                </#list>
           </select>
       </p>
       
       
   <div class="fullViewOnly">        
            <p>
                <label for="relatedIndLabel">### Name ${requiredHint}</label>
                <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="activityLabel"  value="${activityLabelValue}" 
                <#if disabledVal?has_content>
                	disabled=${disabledVal}
                </#if>
                />
            </p>
            
            <input type="hidden" id="roleToActivityPredicate" name="roleToActivityPredicate" value="" />
            <!--Populated or modified by JavaScript based on type of activity, type returned from AJAX request-->
            
            <#if editMode = "edit">
            	<input type="hidden" id="roleActivityType" name="roleActivityType" value="${activityTypeValue}"/>
            	<input type="hidden" id="activityLabel" name="activityLabel" value="${activityLabelValue}"/>
            </#if>
            <@lvf.acSelection urls.base "roleActivity" "roleActivityUri" existingRoleActivityValue />

            <#if showRoleLabelField = true>
            <p><label for="roleLabel">Role in ### ${requiredHint} ${roleExamples}</label>
                <input  size="50"  type="text" id="roleLabel" name="roleLabel" value="${roleLabel}" />
            </p>
        	</#if>
        	
            <#if numDateFields == 1 >
               <#--Generated html is a map with key name mapping to html string-->
               <#if htmlForElements?keys?seq_contains("startField")>
                	<label class="dateTimeLabel" for="startField">Start Year</label>
               		${htmlForElements["startField"]} ${yearHint}
               </#if>
            <#else>
                <h4 class="label">Years of Participation in ### </h4>
                <#if htmlForElements?keys?seq_contains("startField")>
                	    <label class="dateTime" for="startField">Start</label>
               		    ${htmlForElements["startField"]} ${yearHint}
               </#if>
               <p></p>
               <#if htmlForElements?keys?seq_contains("endField")>
               		    <label class="dateTime" for="endField">End</label>
               		    ${htmlForElements["endField"]} ${yearHint}
               </#if>
            </#if>
        </div>
        <p class="submit">
            <input type="hidden" id="editKey" name="editKey" value="${editKey}" />
            <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span><a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
        </p>

        <p id="requiredLegend" class="requiredHint">* required fields</p>
    </form>

<#--Specifying form-specific script and adding stylesheets and scripts-->    
    
 <script type="text/javascript">
	var customFormData  = {
	    acUrl: '${urls.base}/autocomplete?tokenize=true',
	    editMode: '${editMode}',
	    submitButtonTextType: 'compound',
	    defaultTypeName: 'activity' // used in repair mode, to generate button text and org name field label
	};
	</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}

</section>   