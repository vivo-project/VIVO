<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#hasRole and its child properties.
    
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
        <#if statement.activity??>
            <a href="${profileUrl(statement.activity)}">${statement.activityLabel!statement.activityName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.role)}">missing activity</a>
        </#if>
    </#local>
    
    <#local core = "http://vivoweb.org/ontology/core#">
    <#local dateTime>
        <#if statement.property == "${core}hasPresenterRole">
            <@dt.yearSpan statement.dateTimeStart! />
        <#else>
            <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
        </#if>
    </#local>
    
     <#if statement.property == "${core}hasInvestigatorRole" ||
         statement.property == "${core}hasPrincipalInvestigatorRole" ||
	 statement.property == "${core}hasCo-PrincipalInvestigatorRole">
         <#local roleLabel = "">
    <#else>
         <#local roleLabel = statement.roleLabel!>
    </#if>

	${linkedIndividual} ${roleLabel} ${dateTime!}

</#macro>