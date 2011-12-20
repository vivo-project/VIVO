<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this template is for adding a person's position to an organization -->

<#import "lib-vivo-form.ftl" as lvf>

<#if editConfiguration.objectUri?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Position">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Position">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#assign positionTitleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "positionTitle") />
<#assign positionTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "positionType") />
<#assign personValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "person") />
<#assign personLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "personLabel") />

<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<h2>${titleVerb}&nbsp;position history entry for ${editConfiguration.subjectName}</h2>

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#if lvf.submissionErrorExists(editSubmission, "positionTitle")>
            Please enter a value in the Position Title field.<br />
        </#if> 
        <#if lvf.submissionErrorExists(editSubmission, "positionType")>
            Please select a value in the Position Type field.<br />
        </#if>
        <#if lvf.submissionErrorExists(editSubmission, "personLabel")>
 	        Please select an existing value or enter a new value in the Person field.
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

<@lvf.unsupportedBrowser urls.base/>

<section id="organizationHasPositionHistory" role="region">        
    
	<form id="organizationHasPositionHistory" class="customForm noIE67" action="${submitUrl}"  role="add/edit position history">
	    <p>
	        <label for="positionTitle">Position Title ${requiredHint}</label>
	        <input size="30" type="text" id="positionTitle" name="positionTitle" value="${positionTitleValue}" />
	    </p>
	    
	    <label for="positionType">Position Type ${requiredHint}</label>
        <#assign posnTypeOpts = editConfiguration.pageData.positionType />
	    <select id="positionType" name="positionType">
	        <option value="" selected="selected">Select one</option>
	        <#if (posnTypeOpts?keys)??>
		        <#list posnTypeOpts?keys as key>
                    <#if positionTypeValue?has_content && positionTypeValue = key>
                        <option value="${key}" selected >${posnTypeOpts[key]}</option>     
                    <#else>
                        <option value="${key}">${posnTypeOpts[key]}</option>
                    </#if>        
                </#list>
	        </#if>
	    </select>
  	    <p>
	        <label for="relatedIndLabel">Person ${requiredHint}</label>
	        <#if editMode == "edit">
	            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="personLabel" value="${personLabelValue}" disabled="disabled" >
                <input class="acLabelReceiver" type="hidden" id="existingPersonLabel" name="personLabel" value="${personLabelValue}" />
	        <#else>
	            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="personLabel" value="${personLabelValue}" >
	        </#if>
	    </p>
	
	    <div class="acSelection">
	        <p class="inline">
	            <label>Selected Person:</label>
	            <span class="acSelectionInfo"></span>
	            <a href="${urls.base}/individual?uri=" class="verifyMatch"  title="verify match">(Verify this match)</a>
	        </p>
	        <input class="acUriReceiver" type="hidden" id="personUri" name="person" value="${personValue}" />
	    </div>
        
        <br />
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


        <p class="submit">
            <input type="hidden" id="editKey" name="editKey" value="${editKey}" />
            <input type="submit" id="submit" value="${submitButtonText}"/>
            <span class="or"> or </span><a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
        </p>
	
	    <p id="requiredLegend" class="requiredHint">* required fields</p>

	</form>
	
	
	<script type="text/javascript">
	var customFormData  = {
	    acUrl: '${urls.base}/autocomplete?type=http://xmlns.com/foaf/0.1/Person&tokenize=true&stem=true',
	    editMode: '${editMode}',
	    submitButtonTextType: 'compound',
	    defaultTypeName: 'person'
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
