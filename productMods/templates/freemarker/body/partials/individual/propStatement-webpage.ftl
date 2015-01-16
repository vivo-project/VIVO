<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for core:webpage.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#assign linkText>
    <#if statement.label?has_content>${statement.label}<#t>
    <#elseif statement.url?has_content>${statement.url}<#t>
    </#if>    
</#assign>


<#if statement.url?has_content>
    <a href="${statement.url}" title="${i18n().link_text}">${linkText}</a> 
<#else>
    <a href="${profileUrl(statement.uri("link"))}" title="${i18n().link_name}">${statement.linkName}</a> (${i18n().no_url_provided})
</#if>