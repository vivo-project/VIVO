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
<#assign predicateUri=editConfiguration.predicateUri!"undefined">

<#if (editConfiguration.pageData.subjectName??) >
<h2><em>${editConfiguration.pageData.subjectName}</em></h2>
</#if>

<h3>${i18n().manage_web_pages}</h3>
       
<script type="text/javascript">
    var webpageData = [];
</script>

<#if !editConfiguration.pageData.webpages?has_content>
    <p>${i18n().has_no_webpages}</p>
</#if>

<ul id="dragDropList" ${ulClass} role="list">
    <#list editConfiguration.pageData.webpages as webpage>
        <li class="webpage" role="listitem">
            <#if webpage.label??>
                <#assign anchor=webpage.label >
            <#else>
                <#assign anchor=webpage.url >
            </#if>
            
            <span class="itemName extra-wide">
                <a href="${webpage.url}" title="${i18n().webpage_url}">${anchor}</a> 
                <#if webpage.typeLabel??>(<#if webpage.typeLabel == "URL">Standard Web Link<#else>${webpage.typeLabel}</#if></#if>)
            </span>
            <span class="editingLinks">
                <a href="${baseEditWebpageUrl}&objectUri=${webpage.vcard}&predicateUri=${predicateUri}&linkUri=${webpage.link}&rangeUri=${editConfiguration.rangeUri?url}&domainUri=${editConfiguration.domainUri?url}" class="edit" title="${i18n().edit_webpage_link}">${i18n().edit_capitalized}</a> | 
                <a href="${urls.base}${deleteWebpageUrl}" class="remove" title="${i18n().delete_webpage_link}">${i18n().delete_button}</a> 
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
    <a href="${showAddFormUrl}" id="showAddFormButton" title="${i18n().add_new_web_page}">${i18n().add_new_web_page}</a>
     <span class="or"> ${i18n().or} </span>  
    <a href="${cancelUrl}" id="returnToIndividual" class="return" title="${i18n().return_to_profile}">${i18n().return_to_profile}</a>
    <img id="indicator" class="indicator hidden" src="${urls.base}/images/indicatorWhite.gif" alt="${i18n().processing_indicator}"/>
</section>


<script type="text/javascript">
var customFormData = {
    rankPredicate: '${editConfiguration.pageData.rankPredicate}',
    reorderUrl: '${urls.base}/edit/reorder'
};
var i18nStrings = {
    dragDropToReorderWebpages: '${i18n().drag_drop_to_reorder_webpages}',
    webpageReorderingFailed: '${i18n().webpage_reordering_failed}',
    confirmWebpageDeletion: '${i18n().confirm_webpage_deletion}',
    errorRemovingWebpage: '${i18n().error_removing_webpage}'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/manageDragDropList.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/manageWebpagesForIndividual.js"></script>')}
