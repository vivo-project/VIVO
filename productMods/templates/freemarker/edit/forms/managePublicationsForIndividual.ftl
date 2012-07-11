<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-vivo-form.ftl" as lvf>

<#-- Custom form for managing web pages for individuals -->
<#if subjectName?contains(",") >
<#assign lastName = subjectName?substring(0,subjectName?index_of(",")) />
<#assign firstName = subjectName?substring(subjectName?index_of(",") + 1) />
<h2>Manage Publications for ${firstName} ${lastName}</h2>
<#else>
<h2>Manage Publications for ${subjectName}</h2>
</#if>
<p style="margin-left:25px;margin-bottom:12px">
Check those publications you want to exclude from the profile page.
<script type="text/javascript">
    var publicationData = [];
</script>
</p>

<@lvf.unsupportedBrowser urls.base /> 

       
    <#list allSubclasses as sub>
    <h4>${sub}s</h4>
        <section id="pubsContainer" role="container">
        <#assign pubs = publications[sub]>
        <ul >
            <#list pubs as pub>
            <li>
                <input type="checkbox" class="pubCheckbox" <#if pub.hideThis??>checked</#if> />${pub.title}
            </li>
            <script type="text/javascript">
                publicationData.push({
                    "authorshipUri": "${pub.authorship}"              
                });
            </script>      
            
            </#list>
        </ul>
        </section>
    </#list>

<br />    
<p>
    <a href="${urls.referringPage}#publications" title="return to profile page">Return to profile page</a>
</p>

<script type="text/javascript">
var customFormData = {
    processingUrl: '${urls.base}/edit/primitiveRdfEdit'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
                '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/managePublicationsForIndividual.js"></script>')}
              
