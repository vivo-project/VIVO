<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />

<h2>Create Your Own Concept</h2>

<@lvf.unsupportedBrowser urls.base /> 

<form id="addUserDefinedConceptForm" class="customForm noIE67" action = "${submitUrl}" method="post">
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
   <#--Autocomplete for looking up existing skos concepts -->
						<p>
		            <label for="relatedIndLabel">Concept <span class='requiredHint'> *</span></label>
		            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" acGroupName="concept" name="conceptLabel" value="" />
		        </p>
		
		        <div class="acSelection" acGroupName="concept">
		            <p class="inline">
		                <label>Selected Concept:</label>
		                <span class="acSelectionInfo"></span>
                        <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
                        <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
		            </p>
		            <input class="acUriReceiver" type="hidden" id="conceptNode" name="conceptNode" value="" />
        </div>

    <br />
    
    
    
		<p class="submit">
				<input type="hidden" name = "editKey" value="${editKey}"/>
				<input type="submit" id="submit" value="Create Concept"/><span class="or"> or </span><a class="cancel" href="${cancelUrl}">Return to Manage Concepts</a>
		</p>
		
		<p id="requiredLegend" class="requiredHint">* required fields</p>
    
</form>

<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >

    <script type="text/javascript">
    var customFormData  = {
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        acUrl: '${urls.base}/autocomplete?tokenize=true',
        acTypes: {concept: 'http://www.w3.org/2004/02/skos/core#Concept'},
        editMode: 'add',
        typeName: 'Concept',
        defaultTypeName: 'concept', // used in repair mode to generate button text
        baseHref: '${urls.base}/individual?uri='
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}