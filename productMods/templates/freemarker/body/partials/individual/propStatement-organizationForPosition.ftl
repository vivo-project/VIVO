<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#organizationForPosition -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showPosition statement />

<#-- Use a macro to keep variable assignments local; otherwise the values get passed to the next
     statement -->
<#macro showPosition statement>
    
    <#local linkedIndividual>
        <#if statement.person??>
            <a href="${profileUrl(statement.person)}">${statement.personName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.position)}">${statement.positionName}</a> (no linked person)
        </#if>
    </#local>

    <@s.join [ linkedIndividual, statement.positionTitle! ] /> <@dt.yearInterval "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />

</#macro>