<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for core:webpage.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#assign linkText>
    <#if statement.anchor??>${statement.anchor}
    <#else>${statement.linkName} (no anchor text provided for link)
    </#if>    
</#assign>

<#if statement.url??>
    <a href="${statement.url}">${linkText}</a> 
<#else>
    <a href="${profileUrl(statement.link)}">${linkText}</a> (no url provided for link)    
</#if>