<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#educationalTraining -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<#assign linkedIndividual>
    <#if statement.infoResource??>
        <a href="${url(statement.infoResource)}">${statement.infoResourceName}</a>
    <#else>
        <#-- This shouldn't happen, but we must provide for it -->
        <a href="${url(statement.authorship)}">${statement.authorshipName}</a> (no linked information resource)
    </#if>
</#assign>

<#if statement.dateTime??>
    <#assign year = dt.xsdDateTimeToYear(statement.dateTime)>
</#if>

<@s.join [ linkedIndividual, year! ] />