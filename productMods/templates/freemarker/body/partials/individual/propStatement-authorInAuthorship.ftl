<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#authorInAuthorship -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showAuthorship statement />

<#-- Use a macro to keep variable assignments local; otherwise the values are in effect for the
     next statement -->
<#macro showAuthorship statement>

    <#local linkedIndividual>
        <#if statement.infoResource??>
            <a href="${profileUrl(statement.infoResource)}">${statement.infoResourceName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.authorship)}">${statement.authorshipName}</a> (no linked information resource)
        </#if>
    </#local>
    
    <#if statement.dateTime??>
        <#local year = dt.xsdDateTimeToYear(statement.dateTime)>
    </#if>
    
    <@s.join [ linkedIndividual, year! ] />

</#macro>