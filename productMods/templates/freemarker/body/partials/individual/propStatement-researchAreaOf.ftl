<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#organizationForPosition. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>

<@showResearchers statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showResearchers statement>
    <#local linkedIndividual>
        <a href="${profileUrl(statement.uri("person"))}" title="person name">${statement.personName}</a>
    </#local>
    <#if statement.title?has_content >
        <#local posnTitle = statement.title>
    <#else>
        <#local posnTitle = statement.posnLabel!statement.personType>
    </#if>

    <@s.join [ linkedIndividual, posnTitle, statement.orgLabel!"" ] /> ${statement.type!}
</#macro>
