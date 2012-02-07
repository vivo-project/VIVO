<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#personInPosition. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>

<@showResearchers statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showResearchers statement>
    
    <#local linkedIndividual>
        <#if statement.person??>
            <a href="${profileUrl(statement.uri("person"))}" title="organization name">${statement.personName!}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("researchArea"))}" title="missing person">missing person</a>
        </#if>
    </#local>
    <#local title>
        <#if statement.hrJobTitle?has_content >
            ${statement.hrJobTitle!}
        <#else>
            ${statement.positionTitle!}
        </#if>
    </#local>
    <#local organization>
        <#if statement.org??>
            ${statement.orgName!}
        </#if>
    </#local>
    
    <@s.join [ linkedIndividual, title!, organization! ]/>  

</#macro>