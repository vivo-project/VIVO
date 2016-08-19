<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "positions". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showPosition statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showPosition statement>
    
    <#local posTitle>
        <span itemprop="jobTitle">${statement.positionTitle!statement.hrJobTitle!}</span>
    </#local>
    <#local linkedIndividual>
        <#if statement.org??>
            <span itemprop="worksFor" itemscope itemtype="http://schema.org/Organization">
               <a href="${profileUrl(statement.uri("org"))}" title="${i18n().organization_name}"><span itemprop="name">${statement.orgName}</span></a>
            </span>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("position"))}" title="${i18n().missing_organization}">${i18n().missing_organization}</a>
        </#if>
    </#local>
    <#-- The sparql query returns both the org's parent (middleOrg) and grandparent (outerOrg).
         For now, we are only displaying the parent in the list view. -->
    <#local middleOrganization>
        <#if statement.middleOrg??>
            <span itemprop="worksFor" itemscope itemtype="http://schema.org/Organization">
                <a href="${profileUrl(statement.uri("middleOrg"))}" title="${i18n().middle_organization}"><span itemprop="name">${statement.middleOrgName!}</span></a>
            </span>
        </#if>
    </#local>
    
    <@s.join [ posTitle, linkedIndividual, middleOrganization! ]/>  <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />

</#macro>
