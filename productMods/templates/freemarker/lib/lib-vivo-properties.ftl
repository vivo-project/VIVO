<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Macros related to the display of vivo ontology properties -->

<#import "lib-properties.ftl" as p>

<#-- Display preferredTitle if it exists; otherwise display mostSpecificTypes -->
<#macro displayTitle individual>
    <#if individual.preferredTitle?has_content>
        <span class="display-title">${individual.preferredTitle}</span>
    <#else>
        <@p.mostSpecificTypes individual />
    </#if>
</#macro>