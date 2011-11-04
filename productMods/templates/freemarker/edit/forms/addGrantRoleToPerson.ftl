<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a grant role, such as principal investigator, to a foaf:Persons -->
<#import "lib-vivo-form.ftl" as lvf>
<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign literalValues = editConfiguration.existingLiteralValues />
<#assign uriValues = editConfiguration.existingUriValues />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#assign disabledVal = ""/>
<#if editMode = "edit">
	<#assign disabledVal = "disabled=\"disabled\"" />
</#if>

<#--the heading and submit button label depend on the predicate uri-->

<#assign formHeading =  "investigator entry for "/>
<#assign submitButtonLabel = "Investigator" />
<#if editConfiguration.predicateUri?ends_with("hasPrincipalInvestigatorRole") >
	<#assign formHeading = "principal investigator entry for "/>
	<#assign submitButtonLabel = "Principal Investigator" />
<#elseif editConfiguration.predicateUri?ends_with("hasCo-PrincipalInvestigatorRole") >
 	<#assign formHeading = "co-principal investigator entry for "/>
 		<#assign submitButtonLabel = "Co-Principal Investigator" />
</#if>

<#if editMode = "add">
	<#assign formHeading> Create ${formHeading} </#assign>
<#else>
	<#assign formHeading> Edit ${formHeading} </#assign>
	<#assign submitButtonLabel> Edit ${submitButtonLabel} </#assign>

</#if>



<#--Get existing value for specific data literals and uris-->


<#--Get selected activity type value if it exists, this is alternative to below-->
Test grant label: ${lvf.getFormFieldValue(editSubmission, editConfiguration, "grantLabel")} End Test

<#assign grantLabel = ""/>
<#if literalValues?keys?seq_contains("grantLabel") && (literalValues.grantLabel?size > 0)>
	<#assign grantLabel = literalValues.grantLabel[0] />
</#if>

 <#--Get existing grant label value-->
<#assign existingGrantLabel = "" />
<#if literalValues?keys?seq_contains("existingGrantLabel") && (literalValues.existingGrantLabel?size > 0)>
	<#assign existingGrantLabel = literalValues.existingGrantLabel[0] />
</#if>


<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#if editMode = "error">
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>

<h2>${formHeading} ${editConfiguration.subjectName}</h2>

<#if errorNameFieldIsEmpty??>
    <#assign errorMessage = "Enter a name for the grant." />
</#if>

<#if errorMessage?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>${errorMessage}</p>
    </section>
</#if>

<section id="addGrantRoleToPerson" role="region">        
    
<@lvf.unsupportedBrowser/>


    <form id="addGrantRoleToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit grant role">
        
        <p>
            <label for="relatedIndLabel">Grant Name ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="grantLabel" ${disabledVal} value="${grantLabel}" />
        </p>

        <#if editMode = "edit">
						<input type="hidden" id="grantLabel"  name="grantLabel" value="${grantLabel}"/>
         </#if>
        <div class="acSelection">
            <p class="inline">
                <label>Selected Grant:</label>
                <span class="acSelectionInfo"></span>
                <a href="/vivo/individual?uri=" class="verifyMatch">(Verify this match)</a>
            </p>
             <input class="acUriReceiver" type="hidden" id="grant" name="grant" value="" />

            <#--Field value populated by  javascript above-->
            <input class="acLabelReceiver" type="hidden" id="existingGrantLabel" name="existingGrantLabel" value="${existingGrantLabel}" />
        </div>

        <h4>Years of Participation in Grant</h4>							 
			 						<#if htmlForElements?keys?seq_contains("startField")>
			 							 <label for="startField">Start Year ${yearHint}</label>
			 							${htmlForElements["startField"]}
			 					 </#if>
			 					 <#if htmlForElements?keys?seq_contains("endField")>
			 							<label for="endField">End Year ${yearHint}</label>
			 							${htmlForElements["endField"]}
					 	</#if>
					 
            <p class="submit">
                <input type="hidden" name = "editKey" value="${editKey}"/>
                <input type="submit" id="submit" value="${submitButtonLabel}"/><span class="or"> or </span><a class="cancel" href="${cancelUrl}">Cancel</a>
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
    acType: 'http://vivoweb.org/ontology/core#Grant',
    editMode: 'add',
    submitButtonTextType: 'compound',
    typeName: 'Grant'         
};
</script>
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}




${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}
</#if>