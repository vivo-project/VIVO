<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom form for adding authors to information resources -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain page specific information information-->
<#assign newRank = editConfiguration.pageData.newRank />
<#assign existingAuthorInfo = editConfiguration.pageData.existingAuthorInfo />
<#assign rankPredicate = editConfiguration.pageData.rankPredicate />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#--Submission values for these fields may be returned if user did not fill out fields for new person-->
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName") />
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName") />
<#assign middleNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "middleName") />



<#--UL class based on size of existing authors-->
<#assign ulClass = ""/>
<#if (existingAuthorInfo?size > 0)>
	<#assign ulClass = "class='dd'"/>
</#if>

<#assign title="<em>${editConfiguration.subjectName}</em>" />
<#assign requiredHint="<span class='requiredHint'> *</span>" />
<#assign initialHint="<span class='hint'>(initial okay)</span>" />

<@lvf.unsupportedBrowser urls.base/>

<h2>${title}</h2>

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert" class="validationError">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        		  ${submissionErrors[errorFieldName]} <br/>
        </#list>
        
        </p>
    </section>
</#if>

<h3>Manage Authors</h3>

<ul id="authorships" ${ulClass}>

<script type="text/javascript">
    var authorshipData = [];
</script>


<#assign authorHref="/individual?uri=" />
<#--This should be a list of java objects where URI and name can be retrieved-->
<#list existingAuthorInfo as authorship>
	<#assign authorUri = authorship.authorUri/>
	<#assign authorName = authorship.authorName/>

	<li class="authorship">
			<#-- span.author will be used in the next phase, when we display a message that the author has been
			removed. That text will replace the a.authorName, which will be removed. -->    
			<span class="author">
					<#-- This span is here to assign a width to. We can't assign directly to the a.authorName,
					for the case when it's followed by an em tag - we want the width to apply to the whole thing. -->
					<span class="authorNameWrapper">
							<#if (authorUri?length > 0)>
									<span class="authorName">${authorName}</span>
								<#else>      
									<span class="authorName">${authorship.authorshipName}</span><em> (no linked author)</em>
							</#if>
					</span>

					<a href="${urls.base}/edit/primitiveDelete" class="remove" title="remove author link">Remove</a>
			</span>
	</li>

	<script type="text/javascript">
			authorshipData.push({
					"authorshipUri": "${authorship.authorshipUri}",
					"authorUri": "${authorUri}",
					"authorName": "${authorName}"                
			});
	</script>
</#list>

   

</ul>

<section id="showAddForm" role="region">
    <input type="hidden" name = "editKey" value="${editKey}" />
    <input type="submit" id="showAddFormButton" value="Add Author" role="button" />

    <span class="or"> or </span>
    <a class="cancel" href="${cancelUrl}&url=/individual" title="Cancel">Return to Publication</a>
</section> 

<form id="addAuthorForm" action ="${submitUrl}" class="customForm noIE67">
    <h3>Add an Author</h3>
    		<#--These wrapper paragraph elements are important because javascript hides parent of these fields, since last name
    		should be visible even when first name/middle name are not, the parents should be separate for each field-->
    		<p class="inline">
        <label for="lastName">Last name <span class='requiredHint'> *</span></label>
        <input class="acSelector" size="35"  type="text" id="lastName" name="lastName" value="${lastNameValue}" role="input" />
        </p>
				
				<p class="inline">
        <label for="firstName">First name ${requiredHint} ${initialHint}</label>
        <input  size="20"  type="text" id="firstName" name="firstName" value="${firstNameValue}"  role="input" />
        </p>
        

				<p class="inline">
				<label for="middleName">Middle name <span class='hint'>(initial okay)</span></label>
        <input  size="20"  type="text" id="middleName" name="middleName" value="${middleNameValue}"  role="input" />
        </p>
      
        <input type="hidden" id="label" name="label" value=""  role="input" />  <!-- Field value populated by JavaScript -->

        <div id="selectedAuthor" class="acSelection">
            <p class="inline">
                <label>Selected author:&nbsp;</label>
                <span class="acSelectionInfo" id="selectedAuthorName"></span>
                <a href="${urls.base}/individual?uri=" class="verifyMatch"  title="verify match">(Verify this match)</a>
                <input type="hidden" id="personUri" name="personUri" value=""  role="input" /> <!-- Field value populated by JavaScript -->
            </p>
        </div>
        <input type="hidden" name="rank" id="rank" value="${newRank}" role="input" />
    
        <p class="submit">
            <input type="hidden" name = "editKey" value="${editKey}" role="input" />
            <input type="submit" id="submit" value="Add Author" role="button" role="input" />
            
            <span class="or"> or </span>
            
            <a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
        </p>

        <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<script type="text/javascript">
var customFormData = {
    rankPredicate: '${rankPredicate}',
    acUrl: '${urls.base}/autocomplete?type=http://xmlns.com/foaf/0.1/Person&tokenize=true',
    reorderUrl: '${urls.base}/edit/reorder'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
									'<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />',
									'<link rel="stylesheet" href="${urls.base}/edit/forms/css/autocomplete.css" />',
									'<link rel="stylesheet" href="${urls.base}/edit/forms/css/addAuthorsToInformationResource.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/edit/forms/js/addAuthorsToInformationResource.js"></script>')}