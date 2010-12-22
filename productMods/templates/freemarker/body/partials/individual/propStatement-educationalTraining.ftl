<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#educationalTraining -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<#assign degree>
    <#if statement.degreeName??>
        <@s.join [ statement.degreeAbbr!statement.degreeName, statement.majorField! ], " in " />
    </#if>
</#assign>

<#assign linkedIndividual>
    <#if statement.org??>
        <a href="${url(statement.org)}">${statement.orgName}</a>
    <#else>
        <#-- This shouldn't happen, but we must provide for it -->
        <a href="${url(statement.edTraining)}">${statement.edTrainingName}</a> (no linked organization)
    </#if>
</#assign>

<#if statement.dateTime??>
    <#assign year = dt.xsdDateTimeToYear(statement.dateTime)>
</#if>

<@s.join [ degree, linkedIndividual, statement.deptOrSchool!, statement.info!, year! ] />
