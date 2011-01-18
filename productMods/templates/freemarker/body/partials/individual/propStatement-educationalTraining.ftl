<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#educationalTraining -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showEducationalTraining statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showEducationalTraining statement>

    <#local degree>
        <#if statement.degreeName??>
            <#-- RY Giving up on join here. Freemarker insists on removing the space before "in"
                 and leaving no space between the degree and major field, even though compress
                 should only delete consecutive spaces. Even &nbsp; doesn't help.
            <@s.join [ statement.degreeAbbr!statement.degreeName, statement.majorField! ], " in " /> -->
            ${statement.degreeAbbr!statement.degreeName} 
            <#if statement.majorField??> in ${statement.majorField}</#if>
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

    <@s.join [ degree, linkedIndividual, statement.deptOrSchool!, statement.info! ] /> <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" false/>

</#macro>