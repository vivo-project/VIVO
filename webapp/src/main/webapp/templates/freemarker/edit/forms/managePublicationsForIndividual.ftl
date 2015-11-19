<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-vivo-form.ftl" as lvf>

<#-- Custom form for managing web pages for individuals -->
<#if subjectName?contains(",") >
<#assign lastName = subjectName?substring(0,subjectName?index_of(",")) />
<#assign firstName = subjectName?substring(subjectName?index_of(",") + 1) />
<h2>${i18n().manage_publications_for} ${firstName} ${lastName}</h2>
<#else>
<h2>${i18n().manage_publications_for} ${subjectName}</h2>
</#if>
<p style="margin-left:25px;margin-bottom:12px">
${i18n().check_pubs_to_exclude}
<script type="text/javascript">
    var itemData = [];
</script>
</p>

<@lvf.unsupportedBrowser urls.base /> 

       
    <#list allSubclasses as sub>
    <h4>
        <#if sub = "Software">
            ${sub}
        <#elseif sub = "Thesis">
            ${i18n().theses_capitalized}
        <#elseif sub = "Speech">
            ${i18n().speeches_capitalized}
        <#else>
            ${sub}s
        </#if>
    </h4>
        <section id="pubsContainer" role="container">
        <#assign pubs = publications[sub]>
        <ul >
            <#list pubs as pub>
            <li>
                <input type="checkbox" class="itemCheckbox" <#if pub.hideThis??>checked</#if> />
                <#if pub.title?has_content>${pub.title!}<#else>${i18n().title_not_found}</#if>
            </li>
            <script type="text/javascript">
                itemData.push({
                    "relatedUri": "${pub.authorship}"              
                });
            </script>      
            
            </#list>
        </ul>
        </section>
    </#list>

<br />    
<p>
    <a href="${urls.referringPage}#publications" title="${i18n().return_to_profile}">${i18n().return_to_profile}</a>
</p>

<script type="text/javascript">
var customFormData = {
    processingUrl: '${urls.base}/edit/primitiveRdfEdit'
};
var i18nStrings = {
    itemSuccessfullyExcluded: '${i18n().publication_successfully_excluded}',
    errorExcludingItem: '${i18n().error_excluding_publication}'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
                '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/manageHideShowStatus.js"></script>')}
              
