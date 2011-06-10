<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for core:hasEditorRole and core:hasReviewerRole.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showRole statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showRole statement>
    <#local linkedIndividual>
        <#if statement.infoResource??>
            <a href="${profileUrl(statement.infoResource)}">${statement.infoResourceLabel!statement.infoResourceName}</a>
        <#elseif statement.activity??>
            <a href="${profileUrl(statement.activity)}">${statement.activityLabel!statement.activityName}</a>
        <#else>
            <a href="${profileUrl(statement.role)}">${statement.roleLabel!}</a>
        </#if>
    </#local>
    
    <#local core = "http://vivoweb.org/ontology/core#">
    <#local dateTime>
        <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
    </#local>

    ${linkedIndividual} ${statement.roleLabel!} ${dateTime!}

</#macro>