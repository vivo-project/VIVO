<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />

<#--This flag is for clearing the label field on submission for an existing object being selected from autocomplete.
Set this flag on the input acUriReceiver where you would like this behavior to occur. -->
<#assign flagClearLabelForExisting = "flagClearLabelForExisting" />

<h2>${i18n().create_own_concept_all_caps}</h2>

<@lvf.unsupportedBrowser urls.base /> 

<form id="addUserDefinedConceptForm" class="customForm noIE67" action = "${submitUrl}" method="post">
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
   <#--Autocomplete for looking up existing skos concepts -->
						<p>
		            <label for="relatedIndLabel">${i18n().concept_capitalized} <span class='requiredHint'> *</span></label>
		            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" acGroupName="concept" name="conceptLabel" value="" />
		        </p>
		
		        <div class="acSelection" acGroupName="concept">
		            <p class="inline">
		                <label>${i18n().selected_concept}:</label>
		                <span class="acSelectionInfo"></span>
                        <a href="" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or} 
                        <a href="#" class="changeSelection" id="changeSelection" title="${i18n().change_selection}">${i18n().change_selection})</a>
		            </p>
		            <input class="acUriReceiver" type="hidden" id="conceptNode" name="conceptNode" value="" ${flagClearLabelForExisting}="true"/>
        </div>

    <br />
    
    
    
		<p class="submit">
				<input type="hidden" name = "editKey" value="${editKey}"/>
				<input type="submit" id="submit" value="${i18n().create_concept}"/><span class="or"> ${i18n().or} </span><a class="cancel" href="${cancelUrl}">${i18n().return_to_manage_concepts}</a>
		</p>
		
		<p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>
    
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
        baseHref: '${urls.base}/individual?uri=',
        limitToConceptClasses:["http://www.w3.org/2004/02/skos/core#Concept"],
        flagClearLabelForExisting: '${flagClearLabelForExisting}'
    };
    var i18nStrings = {
        selectAnExisting: '${i18n().select_an_existing}',
        orCreateNewOne: '${i18n().or_create_new_one}',
        selectedString: '${i18n().selected}'
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}