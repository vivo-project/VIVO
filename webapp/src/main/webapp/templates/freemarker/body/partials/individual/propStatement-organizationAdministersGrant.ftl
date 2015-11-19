<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<@showAdministeredGrant statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAdministeredGrant statement>
 
    <#local linkedIndividual>
        <#if statement.grant??>
            <a href="${profileUrl(statement.uri("grant"))}" title="${i18n().grant_name}">${statement.grantLabel!""}</a>
        <#else>
            <a href="${profileUrl(statement.uri("administratorRole"))}" title="${i18n().grant_administered_by}">${i18n().missing_grant}</a>
        </#if>
    </#local>

    ${linkedIndividual!}

 </#macro>
