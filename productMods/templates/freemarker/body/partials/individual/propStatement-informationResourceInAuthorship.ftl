<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#informationResourceInAuthorship -->

<#import "lib-sequence.ftl" as s>

<@showAuthorship statement />

<#-- Use a macro to keep variable assignments local; otherwise the values are in effect for the
     next statement -->
<#macro showAuthorship statement>

    <#local linkedIndividual>
        <#if statement.person??>
            <a href="${url(statement.person)}">${statement.personName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${url(statement.authorship)}">${statement.authorshipName}</a> (no linked author)
        </#if>
    </#local>
    
    <@s.join [ linkedIndividual ] />

</#macro>