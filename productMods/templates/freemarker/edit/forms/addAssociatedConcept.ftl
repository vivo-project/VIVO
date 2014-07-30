<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>
<#include "addAssociatedConceptVocabSpecificDisplay.ftl" >
<#assign existingConcepts = editConfiguration.pageData.existingConcepts/>
<#assign userDefinedConceptUrl = editConfiguration.pageData.userDefinedConceptUrl/>
<#assign sources = editConfiguration.pageData.searchServices/>
<#assign inversePredicate = editConfiguration.pageData.inversePredicate />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>


<h2>${i18n().manage_concepts}</h2>
    

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	${errorFieldName} :  ${submissionErrors[errorFieldName]}
        </#list>
       
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base/>

<div class="noIE67">

    
    
<ul id="existingConcepts">
      
    <script type="text/javascript">
        var existingConceptsData = [];
    </script>
    <#if (existingConcepts?size > 0)>
    	 <li class="conceptHeadings conceptsListContainer">
    	 	<div class="row">
    	 		 <div class="column conceptLabelInfo"> 
    	 		 	<h4>Concept (Type)</h4>
    	 		 </div>
    	 		 <div class="column conceptVocabSource"> 
    	 		 	<h4>Vocabulary Source</h4>
    	 		 </div>
    	 		 <div class="column conceptRemoval">&nbsp;
    	 		 </div>
    	 	</div>	
    	 </li>
    </#if>
    
    <#list existingConcepts as existingConcept>
        <li class="existingConcept conceptsListContainer">
                <div class="row">
                   <div class="column conceptLabelInfo"> ${existingConcept.conceptLabel} 
                   	<#if existingConcept.conceptSemanticTypeLabel?has_content>
                   	 (${existingConcept.conceptSemanticTypeLabel})
                   	</#if>
                   	</div>
                   	<div class="column conceptVocabSource">
                   	<#if existingConcept.vocabURI?has_content && existingConcept.vocabLabel?has_content>
                   		${existingConcept.vocabLabel}
                   	<#else>
                   		&nbsp;
                   		<#--We still want the column to be there even if no vocabulary source is present-->	
                   	</#if>
                  	</div> 
                  	<div class="column conceptRemoval">
                  	     <a href="${urls.base}/edit/primitiveRdfEdit" class="remove" title="${i18n().remove_capitalized}">${i18n().remove_capitalized}</a>
                  	
                  	</div>
                </div>
        </li>    
        
        <script type="text/javascript">
            existingConceptsData.push({
                "conceptNodeUri": "${existingConcept.conceptURI}",
                "conceptLabel": "${existingConcept.conceptLabel}"      
            });
        </script>         

      </#list>    

</ul>
       

<#if (existingConcepts?size = 0) >   
        <p>${i18n().no_concepts_specified}</p>
<#else>
        &nbsp;
</#if>

<div id="showAddForm">
    
    <input type="submit" value="${i18n().add_concept}" id="showAddFormButton" name="showAddFormButton">  ${i18n().or} 
    <a class="cancel" href="${cancelUrl}&url=/individual" title="${i18n().return_to_profile}">${i18n().return_to_profile}</a>
</div> 
    <form id="addConceptForm" class="customForm" action="${submitUrl}">
		<#assign checkedSource = false />
	<h4 class="services">${i18n().external_vocabulary_services}</h4>
    <#list sources?values?sort_by("label") as thisSource>
        <input type="radio"  name="source" value="${thisSource.schema}" role="radio" <#if checkedSource = false><#assign checkedSource = true/>checked="checked"</#if>>
        <label class="inline" for="${thisSource.label}"> <a href="${thisSource.url}">${thisSource.label}</a> &nbsp;(${thisSource.description})</label>
        <br />
    </#list>
    <p class="inline-search">
        <input type="text" id="searchTerm" label="Search" class="acSelector" size="35" />
        <input type="button" class="submit concept-search" id="searchButton" name="searchButton" value="${i18n().search_service_btn}"/>&nbsp;
    </p><span id="createOwnOne"> ${i18n().or} &nbsp;<a href="${userDefinedConceptUrl}" title="${i18n().create_own_concept}">${i18n().create_own_concept}&nbsp;</a></span>
    <input type="hidden" id="conceptNode" name="conceptNode" value=""/> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="conceptLabel" name="conceptLabel" value="" />  <!-- Field value populated by JavaScript -->
	<input type="hidden" id="conceptSource" name="conceptSource" value="" /> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="conceptSemanticTypeURI" name="conceptSemanticTypeURI" value="" /> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="conceptSemanticTypeLabel" name="conceptSemanticTypeLabel" value="" /> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="conceptBroaderURI" name="conceptBroaderURI" value=""/><!-- Field value populated by JavaScript -->
    <input type="hidden" id="conceptNarrowerURI" name="conceptNarrowerURI" value=""/><!-- Field value populated by JavaScript -->
    <div id="indicator" class="hidden">
    	<img id="loadingIndicator" class="indicator" src="${urls.base}/images/indicatorWhite.gif" alt="${i18n().processing_indicator}"/>
    </div>
    <div id="selectedConcept" name="selectedConcept" class="acSelection">
        <p class="inline">
         
        </p>
        
        <!-- Search results populated by JavaScript -->
    </div>
    <div id="showHideResults" name="showHideResults">
    	<a class="more-less" href="#show more content" id="showHideLink" title="${i18n().show_more_content}">
    	&nbsp;</a>
    </div>
    <div id="errors" name="errors"></div>
    
    <input type="hidden" name="editKey" id="editKey" value="${editKey}"/>
    <p class="submit">
        <input type="submit" id="submit" name="submit" value="${i18n().add_selected_concept}" />
        
    </p>
    <div id="createOwnTwo"><br />
        <a href="${userDefinedConceptUrl}" > ${i18n().cannot_find_concept}</a>
        
    </div>	
        <p>
            <span class="or"> ${i18n().or} </span><a class="cancel" href="${cancelUrl}&url=/individual" title="${i18n().return_to_profile}">${i18n().return_to_profile}</a>
        </p>
    </form>
</div>



<script type="text/javascript">
var customFormData = {
        dataServiceUrl: '${urls.base}/conceptSearchService',
        subjectUri: '${editConfiguration.subjectUri}',
        predicateUri: '${editConfiguration.predicateUri}',
        inversePredicateUri: '${inversePredicate}'
};
var vocabSpecificDisplay = {};
<#list vocabSpecificDisplay?keys as vocab>
vocabSpecificDisplay["${vocab}"] = "${vocabSpecificDisplay[vocab]}";
</#list>
var i18nStrings = {
    vocServiceUnavailable: '${i18n().vocabulary_service_unavailable}',
    noResultsFound: '${i18n().no_serch_results_found}',
    defaultLabelTypeString: '${i18n().label_type}',
    definitionString: '${i18n().definition_capitalized}',
    bestMatchString: '${i18n().best_match}',
    selectTermFromResults: '${i18n().select_term_from_results}',
    selectVocSource: '${i18n().select_vocabulary_source_to_search}',
    confirmTermDelete: '${i18n().confirm_term_deletion}',
    errorTernNotRemoved: '${i18n().error_term_not_deleted}',
    vocabSpecificLabels: vocabSpecificDisplay,
    displayMoreEllipsis: '${i18n().display_more_ellipsis}',
    displayLess: '${i18n().display_less}',
    showMoreContent: '${i18n().show_more_content}'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/addConcept.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/json2.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/addConcept.js"></script>')}




