<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>

<#assign existingConcepts = editConfiguration.pageData.existingConcepts/>
<#assign userDefinedConceptUrl = editConfiguration.pageData.userDefinedConceptUrl/>

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>


<#--This is set for testing purposes - will be retrieved dynamically from the generator later-->
<#assign sources = [{"uri":"UMLS", "label":"UMLS"}, {"uri":"Agrovoc", "label":"Agrovoc"}]/>
<#assign selectedSource = "UMLS" />

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

    
    
<ul id="existingTerms" >
      
    <script type="text/javascript">
        var existingTermsData = [];
    </script>
    
    <#list existingConcepts as existingConcept>
        <li class="existingTerm">
              
            <span class="term">

                <span class="termWrapper">
                   <span class="termLabel">
                   ${existingConcept.conceptLabel} 
                   </span> 
                </span>
                <a href="${urls.base}/edit/primitiveDelete" class="remove">Remove</a>
            </span>
        </li>    
        
        <script type="text/javascript">
            existingTermsData.push({
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
    <input type="submit" value="Add Term" id="showAddFormButton" name="showAddFormButton">  or 
    <a class="cancel" href="${cancelUrl}">Return</a>
</div> 
    <form id="addTerminologyForm" class="customForm" action="${urls.base}/edit/processTerminologyAnnotation">

    <#list sources as source>
        <input type="radio" name="source" value="${source.uri}" role="radio" <#if selectedSource = source.uri>checked</#if> />
        <label class="inline" for="${source.label}"> ${source.label}</label>
        <br />
    </#list>

    <p class="inline">
        <input type="text" id="searchTerm" label="Search UMLS Terms" class="acSelector" size="35" />
        <input type="button" id="searchButton" name="searchButton" value="Search"/>
    </p>
    <input type="hidden" id="externalConceptURI" name="externalConceptURI" value=""/> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="externalConceptLabel" name="externalConceptLabel" value="" />  <!-- Field value populated by JavaScript -->

    <div id="selectedTerm" name="selectedTerm" class="acSelection">
        <%-- RY maybe make this a label and input field. See what looks best. --%>
        <p class="inline">
        </p>
        <!-- Field value populated by JavaScript -->
    </div>
    
    <a href="${userDefinedConceptUrl}" > Can't find the concept you want? Create your own.</a>
    	
    <p class="submit">
        <input type="submit" id="submit" name="submit" value="Add Term" />
        <span class="or"> or <a class="cancel" href="${cancelUrl}">Cancel</a>
    </p>

    </form>
</div>



    <script type="text/javascript">
    var customFormData = {
        dataServiceUrl: '${urls.base}/UMLSTermsRetrieval',
        UMLSCUIURL: 'http://link.informatics.stonybrook.edu/umls/CUI/'
    };
    </script>


${scripts.add('<script type="text/javascript" src="${urls.base}/edit/forms/js/addTerminology.js"></script>')}




