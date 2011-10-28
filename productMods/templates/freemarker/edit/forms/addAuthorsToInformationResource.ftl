<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom form for adding authors to information resources -->

<#import "lib-vivo-form.ftl" as lf>

<#assign title="<em>${infoResourceName}</em>" />
<#assign requiredHint="<span class='requiredHint'> *</span>" />
<#assign initialHint="<span class='hint'>(initial okay)</span>" />

<@lf.unsupportedBrowser>

<h2>${title}</h2>

<ul id="authorships" ${ulClass}>

<script type="text/javascript">
    var authorshipData = [];
</script>

<#list authors as author>
<li class="authorship">
    <#-- span.author will be used in the next phase, when we display a message that the author has been
    removed. That text will replace the a.authorName, which will be removed. -->    
    <span class="author">
        <#-- This span is here to assign a width to. We can't assign directly to the a.authorName,
        for the case when it's followed by an em tag - we want the width to apply to the whole thing. -->
        <span class="authorNameWrapper">
            <#if author <#--<c:when test="${!empty author}">-->>
                <#assign authorUri=author.URI />
                <#assign authorName=author.name />
                <#assign authorHref="/individual" />
                <#-- I don't know how to "translate"" this in to freemarker <c:param name="uri" value="${authorUri}"/>-->
                <span class="authorName">${authorName}</span>
              <#else>      
                <#assign authorUri="" />
                <#assign authorName="" />
                <#assign authorHref="/individual" />
                <#-- I don't know how to "translate" this in to freemarker  <c:param name="uri" value="${authorshipUri}"/>-->
                <span class="authorName">${authorshipName}</span><em> (no linked author)</em>
            </#if>
        </span>
        
        <#assign deleteAuthorshipHref="/edit/primitiveDelete" />
        <a href="${deleteAuthorshipHref}" class="remove">Remove</a>
    </span>
</li>

<script type="text/javascript">
    authorshipData.push({
        "authorshipUri": "${authorshipUri}",
        "authorUri": "${authorUri}",
        "authorName": "${authorName}"                
    });
</script>
</#list>

    <#--// A new author will be ranked last when added.
    // This value is now inserted by JavaScript, but leave it here as a safety net in case page
    // load reordering returns an error. 
    request.setAttribute("newRank", maxRank + 1);
    request.setAttribute("rankPredicate", rankPredicateUri);-->

</ul>

<section id="showAddForm" role="region">
    <input type="hidden" name = "editKey" value="${editKey}" />
    <input type="submit" id="showAddFormButton" value="${editConfiguration.submitLabel}" role="button" />

    <span class="or"> or </span>
    <a class="cancel" href="${editConfiguration.cancelUrl}" title="Cancel">Return to Publication</a>
</section> 

<form id="addAuthorForm" action ="${submitUrl}" class="customForm">
    <h3>Add an Author</h3>
        <label for="lastName">Last name <span class='requiredHint'> *</span></label>
        <input class="acSelector" size="35"  type="text" id="lastName" name="lastName" value="" role="input" />

        <label for="firstName">First name ${requiredHint} ${initialHint}</label>
        <input  size="20"  type="text" id="firstName" name="firstName" value=""  role="input" />

        <label for="middleName">Middle name <span class='hint'>(initial okay)</span></label>
        <input  size="20"  type="text" id="middleName" name="middleName" value=""  role="input" />
      
        <input type="hidden" id="label" name="label" value=""  role="input" />  <!-- Field value populated by JavaScript -->

        <div id="selectedAuthor" class="acSelection">
            <label>Selected author: </label><span class="acSelectionInfo" id="selectedAuthorName"></span></p>
            <input type="hidden" id="personUri" name="personUri" value=""  role="input" /> <!-- Field value populated by JavaScript -->
        </div>

        <input type="hidden" name="rank" id="rank" value="${newRank}" role="input" />
    
        <p class="submit">
            <input type="hidden" name = "editKey" value="${keyValue}" role="input" />
            <input type="submit" id="submit" value="${submitButtonText}" role="button" role="input" />
            
            <span class="or"> or </span>
            
            <a class="cancel" href="${editConfiguration.cancelUrl}" title="Cancel">Cancel</a>
        </p>

        <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<script type="text/javascript">
var customFormData = {
    rankPredicate: ${rankPredicate},
    acUrl: ${acUrl},
    reorderUrl: '/edit/reorder'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/edit/forms/css/addAuthorsToInformationResource.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>',
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/addAuthorsToInformationResource.js"></script>')}