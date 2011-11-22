<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#assign returnUrl = ""/>

<h2>Add Your Own Concept</h2>


<form id="addUserDefinedConceptForm" class="editForm" action = "${submitUrl}" method="post">
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
   <#--Autocomplete for looking up existing skos concepts -->
		<p>
		            <label for="relatedIndLabel">Concept <span class='requiredHint'> *</span></label>
		            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="conceptLabel" 
		            <#if (disabledVal?length > 0)>disabled="${disabledVal}"</#if> value="" />
		        </p>
		
		        <div class="acSelection">
		            <p class="inline">
		                <label>Selected Publication:</label>
		                <span class="acSelectionInfo"></span>
		                <a href="/vivo/individual?uri=" class="verifyMatch">(Verify this match)</a>
		            </p>
		            <input class="acUriReceiver" type="hidden" id="conceptNode" name="conceptNode" value="" />
        </div>

    <br />
    
    
    
		<p class="submit">
				<input type="hidden" name = "editKey" value="${editKey}"/>
				<input type="submit" id="submit" value="Add Concept"/><span class="or"> or </span><a class="cancel" href="${returnUrl}">Return to Manage Concepts</a>
		</p>
		
		<p id="requiredLegend" class="requiredHint">* required fields</p>
    
</form>

<#assign sparqlQueryUrl = "/ajax/sparqlQuery" >

    <script type="text/javascript">
    var customFormData  = {
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        acUrl: '${urls.base}/autocomplete?tokenize=true',
        submitButtonTextType: 'simple',
        editMode: 'add',
        defaultTypeName: 'concept' // used in repair mode to generate button text
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}