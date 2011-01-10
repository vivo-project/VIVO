<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#relatedRole -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showRole statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showRole statement>
    
    <#local linkedIndividual>
        <#if statement.indivInRole??>
            <a href="${profileUrl(statement.indivInRole)}">${statement.indivName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.role)}">${statement.roleName}</a> (no linked individual in this role)
        </#if>
    </#local>

    <#-- Generally roles are assigned a label when entered through a custom form. Investigator and its subclasses do not,
    so use the type label instead. -->
    ${linkedIndividual} ${statement.roleLabel!statement.roleTypeLabel!} <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />

</#macro>