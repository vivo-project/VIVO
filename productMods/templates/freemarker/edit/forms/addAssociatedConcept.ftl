<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-vivo-form.ftl" as lvf>

<#assign existingConcepts = editConfiguration.pageData.existingConcepts/>
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>



<h2>Manage Associated Concepts</h2>

<#--Display error messages if any-->


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


<section id="addAssociatedConcept" role="region">        
    
    
<ul id="existingTerms" >
      
    <script type="text/javascript">
        var existingTermsData = [];
    </script>
    
    <#list existingConcepts?keys as key>
    	<#local existingConcept = existingConcepts[key] />
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
</section>