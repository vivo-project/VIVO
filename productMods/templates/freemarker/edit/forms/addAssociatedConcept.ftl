<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>

<#assign existingConcepts = editConfiguration.pageData.existingConcepts/>
<#assign userDefinedConceptUrl = editConfiguration.pageData.userDefinedConceptUrl/>
<#assign sources = editConfiguration.pageData.searchServices/>

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>


<h2>Manage Concepts</h2>
    

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
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

    
    
<ul id="existingConcepts" >
      
    <script type="text/javascript">
        var existingConceptsData = [];
    </script>
    
    <#list existingConcepts as existingConcept>
        <li class="existingConcept">
              
            <span class="concept">

                <span class="conceptWrapper">
                   <span class="conceptLabel"> ${existingConcept.conceptLabel} 
                   	<#if existingConcept.vocabURI?has_content && existingConcept.vocabLabel?has_content>
                   		(${existingConcept.vocabLabel})
                   	</#if>
                   </span> 
                </span>
                <a href="${urls.base}/edit/primitiveDelete" class="remove">Remove</a>
            </span>
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
        <p>There are currently no concepts specified.</p>
</#if>

<div id="showAddForm">
    <input type="submit" value="Add Concept" id="showAddFormButton" name="showAddFormButton">  or 
    <a class="cancel" href="${cancelUrl}&url=/individual">Return</a>
</div> 
    <form id="addConceptForm" class="customForm" action="${submitUrl}">
		<#assign checkedSource = false />
    <#list sources?keys as sourceUri>
        <input type="radio"  name="source" value="${sourceUri}" role="radio" <#if checkedSource = false><#assign checkedSource = true/>checked="checked"</#if>>
        <label class="inline" for="${sources[sourceUri]}"> ${sources[sourceUri]}</label>
        <br />
    </#list>

    <p class="inline">
        <input type="text" id="searchTerm" label="Search" class="acSelector" size="35" />
        <input type="button" id="searchButton" name="searchButton" value="Search"/>
    </p>
    <input type="hidden" id="conceptNode" name="conceptNode" value=""/> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="conceptLabel" name="conceptLabel" value="" />  <!-- Field value populated by JavaScript -->
		<input type="hidden" id="conceptSource" name="conceptSource" value="" /> <!-- Field value populated by JavaScript -->
    <div id="selectedConcept" name="selectedConcept" class="acSelection">
        <p class="inline">
        </p>
        <!-- Search results populated by JavaScript -->
    </div>
    <div id="errors" name="errors"></div>
    
    <div><a href="${userDefinedConceptUrl}" > Can't find the concept you want? Create your own.</a>
    </div>	
    <input type="hidden" name="editKey" id="editKey" value="${editKey}"/>
    <p class="submit">
        <input type="submit" id="submit" name="submit" value="Add Term" />
        <span class="or"> or <a class="cancel" href="${cancelUrl}">Cancel</a>
    </p>

    </form>
</div>



    <script type="text/javascript">
    var customFormData = {
        dataServiceUrl: '${urls.base}/conceptSearchService'
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/addConcept.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/edit/forms/js/addConcept.js"></script>')}




