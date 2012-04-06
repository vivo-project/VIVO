<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>

<#-- Template for adding a grant role, such as principal investigator, to a foaf:Persons -->
<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign literalValues = editConfiguration.existingLiteralValues />
<#assign uriValues = editConfiguration.existingUriValues />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>
<#assign disabledVal = ""/>
<#if editMode = "edit">
	<#assign disabledVal = "disabled=\"disabled\"" />
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

<#--the heading and submit button label depend on the predicate uri-->

<#assign formHeading =  "investigator entry for "/>
<#assign submitButtonLabel = "Investigator" />
<#if editConfiguration.predicateUri?ends_with("hasPrincipalInvestigatorRole") >
	<#assign formHeading = "principal investigator entry for "/>
<#elseif editConfiguration.predicateUri?ends_with("hasCo-PrincipalInvestigatorRole") >
 	<#assign formHeading = "co-principal investigator entry for "/>
</#if>

<#if editMode = "add">
	<#assign formHeading> Create ${formHeading} </#assign>
	<#assign submitButtonLabel>Create Entry</#assign>
<#else>
	<#assign formHeading> Edit ${formHeading} </#assign>
	<#assign submitButtonLabel>Save Changes</#assign>

</#if>



<#--Get existing value for specific data literals and uris-->


<#--Get selected activity type value if it exists, this is alternative to below-->

<#assign grantLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "grantLabel")/>
<#assign grantLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "grantLabelDisplay")/>
<#--Get existing grant value-->
<#assign existingGrantValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingGrant")/>


<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#if editMode = "error">
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>

<h2>${formHeading} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <#if grantLabelDisplayValue?has_content >
        <#assign grantLabelValue = grantLabelDisplayValue />
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
        <#--Checking if Name field is empty-->
         <#if lvf.submissionErrorExists(editSubmission, "grantLabel")>
 	        Please enter or select a value in the Grant Name field.
        </#if>
        
        </p>
    </section>
</#if>

<section id="addGrantRoleToPerson" role="region">        
    
<@lvf.unsupportedBrowser  urls.base />


    <form id="addGrantRoleToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit grant role">
        
        <p>
            <label for="grant">Grant Name ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="grant" acGroupName="grant" name="grantLabel"  value="${grantLabelValue}" />
            <input class="display" type="hidden" id="grantDisplay" acGroupName="grant" name="grantLabelDisplay" value="${grantLabelDisplayValue}">
        </p>

        <div class="acSelection" acGroupName="grant" id="grantAcSelection">
            <p class="inline">
                <label>Selected Grant:</label>
                <span class="acSelectionInfo"></span>
                <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
                <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
            </p>
            <input class="acUriReceiver" type="hidden" id="grantUri" name="existingGrant" value="${existingGrantValue}" ${flagClearLabelForExisting}="true" />
        </div>

        <h4>Years of Participation in Grant</h4>							 
			 						<#if htmlForElements?keys?seq_contains("startField")>
			 							 <label class="dateTime" for="startField">Start</label>
			 							${htmlForElements["startField"]} ${yearHint}
			 					 </#if>
			 					 <p></p>
			 					 <#if htmlForElements?keys?seq_contains("endField")>
			 							<label class="dateTime" for="endField">End</label>
			 							${htmlForElements["endField"]} ${yearHint}
					 	</#if>
					 
            <p class="submit">
                <input type="hidden" name = "editKey" value="${editKey}"/>
                <input type="submit" id="submit" value="${submitButtonLabel}"/><span class="or"> or </span><a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
            </p>

            <p id="requiredLegend" class="requiredHint">* required fields</p>
    </form>

</section>
<#assign acUrl="/autocomplete?tokenize=true" />
<#assign sparqlQueryUrl ="/ajax/sparqlQuery" />

    
<script type="text/javascript">
var customFormData  = {
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${urls.base}${sparqlQueryUrl}',
    acUrl: '${urls.base}${acUrl}',
    acTypes: {grant: 'http://vivoweb.org/ontology/core#Grant'},
    editMode: '${editMode}',
    typeName: 'Grant',
    baseHref: '${urls.base}/individual?uri=',
    blankSentinel: '${blankSentinel}',
    flagClearLabelForExisting: '${flagClearLabelForExisting}'
    };
</script>
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}
</#if>