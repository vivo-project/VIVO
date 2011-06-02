<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#authorInAuthorship. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
 
<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showAuthorship statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAuthorship statement>

    <#local linkedIndividual>
        <#if statement.infoResource??>
            <a href="${profileUrl(statement.infoResource)}">${statement.infoResourceName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.authorship)}">missing information resource</a>
        </#if>
    </#local>

    ${linkedIndividual} <@dt.yearSpan "${statement.dateTime!}" />

</#macro>