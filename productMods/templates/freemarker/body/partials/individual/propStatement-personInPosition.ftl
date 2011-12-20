<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#personInPosition. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showPosition statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showPosition statement>
    
    <#local linkedIndividual>
        <#if statement.org??>
            <a href="${profileUrl(statement.uri("org"))}" title="organization name">${statement.orgName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("position"))}" title="missing organization">missing organization</a>
        </#if>
    </#local>
    <#-- The sparql query returns both the org's parent (middleOrg) and grandparent (outerOrg).
         For now, we are only displaying the parent in the list view. -->
    <#local middleOrganization>
        <#if statement.middleOrg??>
            <a href="${profileUrl(statement.uri("middleOrg"))}" title="middle organization">${statement.middleOrgName!}</a>
        </#if>
    </#local>
    
    <@s.join [ statement.positionTitle!statement.hrJobTitle!, linkedIndividual, middleOrganization! ]/>  <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />

</#macro>