<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<@showAdministrator statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAdministrator statement>
 
    <#local linkedIndividual>
        <#if statement.organization??>
            <a href="${profileUrl(statement.uri("organization"))}" title="${i18n().organization_name}">${statement.organizationLabel!""}</a>
        <#else>
            <a href="${profileUrl(statement.uri("administratorRole"))}" title="${i18n().administering_organization_for}">${i18n().missing_organization}</a>
        </#if>
    </#local>

    ${linkedIndividual!}

 </#macro>
