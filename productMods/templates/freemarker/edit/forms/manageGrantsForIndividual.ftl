<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-vivo-form.ftl" as lvf>

<#-- Custom form for managing web pages for individuals -->
<#if subjectName?contains(",") >
<#assign lastName = subjectName?substring(0,subjectName?index_of(",")) />
<#assign firstName = subjectName?substring(subjectName?index_of(",") + 1) />
<h2>Manage Grants & Projects for ${firstName} ${lastName}</h2>
<#else>
<h2>Manage Grants & Projects for ${subjectName}</h2>
</#if>
<p style="margin-left:25px;margin-bottom:12px">
Check those grants and projects you want to exclude from the profile page.
<script type="text/javascript">
    var grantData = [];
</script>
</p>
<@lvf.unsupportedBrowser urls.base /> 
       
    <#list allSubclasses as sub>
    <h4>${sub}</h4>
        <section id="pubsContainer" role="container">
        <#assign grantList = grants[sub]>
        <ul >
            <#list grantList as grant>
            <li>
                <input type="checkbox" class="grantCheckbox" <#if grant.hideThis??>checked</#if> />${grant.label!}
            </li>
            <script type="text/javascript">
                grantData.push({
                    "roleUri": "${grant.role!}"              
                });
            </script>      
            
            </#list>
        </ul>
        </section>
    </#list>

<br />    
<p>
    <a href="${urls.referringPage}#research" title="return to profile page">Return to profile page</a>
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
                '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/manageGrantsForIndividual.js"></script>')}
              
