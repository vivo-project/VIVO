<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom form for managing web pages for individuals -->

<#if (editConfiguration.pageData.webpages?size > 1) >
  <#assign ulClass="class='dd'">
<#else>
  <#assign ulClass="">
</#if>

<#assign baseEditWebpageUrl=editConfiguration.pageData.baseEditWebpageUrl!"baseEditWebpageUrl is undefined">
<#assign deleteWebpageUrl=editConfiguration.pageData.deleteWebpageUrl!"deleteWebpageUrl is undefined">
<#assign showAddFormUrl=editConfiguration.pageData.showAddFormUrl!"showAddFormUrl is undefined">

<#if (editConfiguration.pageData.subjectName??) >
<h2><em>${editConfiguration.pageData.subjectName}</em></h2>
</#if>

<h3>Manage Web Pages</h3>
       
<script type="text/javascript">
    var webpageData = [];
</script>

<#if !editConfiguration.pageData.webpages?has_content>
    <p>This individual currently has no web pages specified. Add a new web page by clicking on the button below.</p>
</#if>

<ul id="webpageList" ${ulClass} role="list">
    <#list editConfiguration.pageData.webpages as webpage>
        <li class="webpage" role="listitem">
            <#if webpage.anchor??>
                <#assign anchor=webpage.anchor >
            <#else>
                <#assign anchor=webpage.url >
            </#if>
            
            <span class="webpageName">
                <a href="${webpage.url}" title="webpage url">${anchor}</a>
            </span>
            <span class="editingLinks">
                <a href="${baseEditWebpageUrl}&objectUri=${webpage.link?url}" class="edit" title="edit web page link">Edit</a> | 
                <a href="${urls.base}${deleteWebpageUrl}" class="remove" title="delete web page link">Delete</a> 
            </span>
        </li>    
        
        <script type="text/javascript">
            webpageData.push({
                "webpageUri": "${webpage.link}"              
            });
        </script>      
    </#list>  
</ul>

<section id="addAndCancelLinks" role="section">
    <#-- There is no editConfig at this stage, so we don't need to go through postEditCleanup.jsp on cancel.
         These can just be ordinary links, rather than a v:input element, as in 
         addAuthorsToInformationResource.jsp. -->   
    <a href="${showAddFormUrl}" id="showAddForm" class="button green" title="add new web page">Add New Web Page</a>
       
    <a href="${cancelUrl}" id="returnToIndividual" class="return" title="return to individual">Return to Individual</a>
</section>


<script type="text/javascript">
var customFormData = {
    rankPredicate: '${editConfiguration.pageData.rankPredicate}',
    reorderUrl: '${urls.base}/edit/reorder'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/edit/forms/css/manageWebpagesForIndividual.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/manageWebpagesForIndividual.js"></script>')}