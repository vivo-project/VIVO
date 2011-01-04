<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#personInPosition -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showPosition statement />

<#-- Use a macro to keep variable assignments local; otherwise the values get passed to the next
     statement -->
<#macro showPosition statement>
    
    <#local linkedIndividual>
        <#if statement.org??>
            <a href="${profileUrl(statement.org)}">${statement.orgName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.position)}">${statement.positionName}</a> (no linked organization)
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
    
    <#local dateInPosition>
        <#if dateTimeInterval?has_content>
            <span class="listDateTime">${dateTimeInterval}</span>
        </#if>
    </#local>
    
    <@s.join [ linkedIndividual, statement.positionTitle! ] /> ${dateInPosition!}

</#macro>