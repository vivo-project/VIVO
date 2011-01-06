<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#relatedRole -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showRole statement />

<#-- Use a macro to keep variable assignments local; otherwise the values get passed to the next
     statement -->
<#macro showRole statement>
    
    <#local linkedIndividual>
        <#if statement.person??>
            <a href="${profileUrl(statement.person)}">${statement.personName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.role)}">${statement.roleName}</a> (no linked person)
        </#if>
    </#local>
    
    <#local dateTimeInterval>
        <#if statement.dateTimeStart??>
            <#local startYear = dt.xsdDateTimeToYear(statement.dateTimeStart)>
        </#if>
        <#if statement.dateTimeEnd??>
            <#local endYear = dt.xsdDateTimeToYear(statement.dateTimeEnd)>
        </#if>
        <#if startYear?? && endYear??>
            ${startYear} - ${endYear}
        <#elseif startYear??>
            ${startYear} -
        <#elseif endYear ??>
            - ${endYear}
        </#if>
    </#local>
    
    <#local dateInRole>
        <#if dateTimeInterval?has_content>
            <span class="listDateTime">${dateTimeInterval}</span>
        </#if>
    </#local>
    
    ${linkedIndividual} ${statement.specificRole} ${dateInRole!}

</#macro>