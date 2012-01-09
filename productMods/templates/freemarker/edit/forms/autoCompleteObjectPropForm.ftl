<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--Assign variables from editConfig-->
<#assign rangeOptions = editConfiguration.pageData.objectVar />
<#assign rangeOptionsExist = false />
<#if (rangeOptions?keys?size > 0)>
	<#assign rangeOptionsExist = true/>
</#if>
<#assign objectTypes = editConfiguration.pageData.objectTypes />
<#assign objectTypesSize = objectTypes?length />
<#assign objectTypesExist = false />
<#assign multipleTypes = false />
<#if (objectTypesSize > 1)>
	<#assign objectTypesExist = true />
</#if>
<#if objectTypes?contains(",")>
	<#assign multipleTypes = true/>
</#if>
<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#assign editMode = editConfiguration.pageData.editMode />
<#assign propertyNameForDisplay = "" />
<#if editConfiguration.objectPropertyNameForDisplay?has_content>
	<#assign propertyNameForDisplay = editConfiguration.objectPropertyNameForDisplay />
</#if>
<#if editMode = "edit" >
	<#assign titleVerb = "Edit" />
	<#assign objectLabel = editConfiguration.pageData.objectLabel />
	<#assign selectedObjectUri = editConfiguration.objectUri />
<#else>
	<#assign titleVerb = "Add" >
	<#assign objectLabel = "" />
		<#assign selectedObjectUri = ""/>
</#if>

<#if editConfiguration.formTitle?contains("collaborator") >
    <#assign formTitle = "Select an existing Collaborator for ${editConfiguration.subjectName}" />
<#else>
    <#assign formTitle = editConfiguration.formTitle />
</#if>
<#--In order to fill out the subject-->
<#assign acFilterForIndividuals =  "['" + editConfiguration.subjectUri + "']" />

<h2>${formTitle}</h2>

<#if editConfiguration.propertySelectFromExisting = true>
    <#if rangeOptionsExist  = true >
        <form class="customForm" action = "${submitUrl}">
            <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
            <#if editConfiguration.propertyPublicDescription?has_content>
                <p>${editConfiguration.propertyPublicDescription}</p>
             </#if>     
             
            <#---This section should become autocomplete instead--> 
            <p>
								<label for="relatedIndLabel"> ${propertyNameForDisplay?capitalize} Name<span class='requiredHint'> *</span></label>
								<input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="objectLabel" value="${objectLabel}" />
						</p>
								
							<div class="acSelection"> 
							<p class="inline">
							<label>Selected:</label> 
							<span class="acSelectionInfo"></span> 
							<a href="/vivo/individual?uri="
									class="verifyMatch">(Verify this match)</a> 
							<a href="#"
									class="cancel">(Change selection)</a> 
							</p> <input class="acUriReceiver" type="hidden" id="objectVar" name="objectVar" value="${selectedObjectUri}" />
							</div>

            
            <#--The above section should be autocomplete-->
            
            
            <p>
                <input type="submit" id="submit" value="${editConfiguration.submitLabel}" role="button" disabled="disabled"/>
           
                <span class="or"> or </span>
                <a title="Cancel" class="cancel" href="${cancelUrl}">Cancel</a>
            </p>
        </form>
    <#else>
        <p> There are no entries in the system from which to select.  </p>  
    </#if>
</#if>
<p>&nbsp;</p>
<#if editConfiguration.propertyOfferCreateNewOption = true>
<#include "defaultOfferCreateNewOptionForm.ftl">

</#if>

<#if editConfiguration.propertySelectFromExisting = false && editConfiguration.propertyOfferCreateNewOption = false>
<p>This property is currently configured to prohibit editing. </p>
</#if>


<#if editConfiguration.includeDeletionForm = true>
<#include "defaultDeletePropertyForm.ftl">
</#if>


<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >
<#--Passing in object types only if there are any types returned, otherwise
the parameter should not be passed at all to the solr search.
Also multiple types parameter set to true only if more than one type returned-->
    <script type="text/javascript">	
    var customFormData  = {
        acUrl: '${urls.base}/autocomplete?tokenize=true',
        <#if objectTypesExist = true>
        acType: '${objectTypes}',
        </#if>
        <#if multipleTypes = true>
        acMultipleTypes: 'true',
        </#if>
        submitButtonTextType: 'simple',
        editMode: '${editMode}',
        typeName:'${propertyNameForDisplay}',
        supportEdit: 'true',
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        acFilterForIndividuals: ${acFilterForIndividuals},
        defaultTypeName: '${propertyNameForDisplay}' // used in repair mode to generate button text
    };
    </script>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}
