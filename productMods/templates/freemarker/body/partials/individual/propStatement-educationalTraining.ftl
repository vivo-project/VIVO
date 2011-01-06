<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#educationalTraining -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showEducationalTraining statement />

<#-- Use a macro to keep variable assignments local; otherwise the values get passed to the next
     statement -->
<#macro showEducationalTraining statement>

    <#local degree>
        <#if statement.degreeName??>
            <@s.join [ statement.degreeAbbr!statement.degreeName, statement.majorField! ], " in " />
        </#if>
    </#local>
    
    <#local linkedIndividual>
        <#if statement.org??>
            <a href="${profileUrl(statement.org)}">${statement.orgName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.edTraining)}">${statement.edTrainingName}</a> (no linked organization)
        </#if>
    </#local>

    <@s.join [ degree, linkedIndividual, statement.deptOrSchool!, statement.info! ] /> <@dt.yearInterval "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" false/>

</#macro>