<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
    <#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#assign editMode = editConfiguration.pageData.editMode />
<#assign propertyPublicName = editConfiguration.propertyPublicName/>
<h2>${editConfiguration.formTitle}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        
        <#list submissionErrors?keys as errorFieldName>
            ${submissionErrors[errorFieldName]}
        </#list>
                        
        </p>
    </section>
</#if>

<#assign literalValues = "${editConfiguration.dataLiteralValuesAsString}" />

<form class="customForm" action = "${submitUrl}" method="post">
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
    <#if editConfiguration.dataPredicatePublicDescription?has_content>
       <label for="${editConfiguration.dataLiteral}"><p class="propEntryHelpText">${editConfiguration.dataPredicatePublicDescription}</p></label>
    </#if>   
	
    <p>
		<input class="acSelector" size="50"  type="text" id="literal" name="literal" value="${literalValues}" />
	</p>
								
	<div class="acSelection"> 
	<p class="inline">
	<label>${i18n().selected}:</label> 
	<span class="acSelectionInfo"></span> 
	
	<a href="#" class="cancel">(${i18n().change_selection})</a> 
	</p>
	</div>
    <br />

    <input type="submit" id="submit" value="${editConfiguration.submitLabel}" role="button"/>
    <span class="or"> ${i18n().or} </span>
    <a title="${i18n().cancel_title}" href="${cancelUrl}">${i18n().cancel_link}</a>

</form>

<#if editConfiguration.includeDeletionForm = true>
<#include "defaultDeletePropertyForm.ftl">
</#if>
<#--Not including defaultFormScripts.ftl which would trigger tinyMce-->
<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >
<#--Passing in object types only if there are any types returned, otherwise
the parameter should not be passed at all to the search.
Also multiple types parameter set to true only if more than one type returned-->
    <script type="text/javascript">	
    var customFormData  = {
        acUrl: '${urls.base}/dataautocomplete?',
      	property: '${editConfiguration.predicateUri}',
        submitButtonTextType: 'simple',
        editMode: '${editMode}', //Change this to check whether adding or editing
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        defaultTypeName: '${propertyPublicName}'
    };
    var i18nStrings = {
        selectExisting: '${i18n().select_an_existing}',
        orCreateNewOne: '$i18n().or_create_new_one}',
        selectedString: '$i18n().selected}'
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithDataAutocomplete.js"></script>')}
