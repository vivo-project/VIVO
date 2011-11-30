<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a new individual from the Site Admin page: VIVO version -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign typeName = editConfiguration.pageData.typeName />
<#assign isPersonType = editConfiguration.pageData.isPersonType />

<#--Get existing value for specific data literals and uris-->
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName")/>
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName")/>
<#assign labelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "label")/>

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>


<h2>Create a new ${typeName}</h2>


<#if submissionErrors?has_content >
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#list submissionErrors?keys as errorFieldName>
    	    <#if errorFieldName == "firstName">
    	        Please enter a First Name for this person.
    	    <#elseif  errorFieldName == "lastName">
    	        Please enter a Last Name for this person.
        	<#elseif  errorFieldName == "label">
        	    Please enter a value in the name field.
    	    </#if>
    	    <br />
    	</#list>
        </p>
    </section>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />

<section id="newIndividual" role="region">        
    
    <form id="newIndividual" class="customForm noIE67" action="${submitUrl}"  role="add new individual">
 
    <#if isPersonType = "true">       
        <p>
            <label for="firstName">First Name ${requiredHint}</label>
            <input size="30"  type="text" id="firstName" name="firstName" value="${firstNameValue}" />
        </p>

        <p>
            <label for="lastName">Last Name ${requiredHint}</label>
            <input size="30"  type="text" id="lastName" name="lastName" value="${lastNameValue}" />
        </p>
    <#else>       
        <p>
            <label for="name">Name ${requiredHint}</label>
            <input size="30"  type="text" id="label" name="label" value="${labelValue}" />
        </p>
    </#if>

    <p class="submit">
        <input type="hidden" name = "editKey" value="${editKey}"/>
        <input type="submit" id="submit" value="Create ${typeName}"/>
        <span class="or"> or </span><a class="cancel" href="${urls.base}/siteAdmin" title="Cancel">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

    </form>


</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
