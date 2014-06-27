<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<@showAwardsGrant statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAwardsGrant statement>
 
    <#local linkedIndividual>
        <#if statement.grantOrContract??>
            <a href="${profileUrl(statement.uri("grantOrContract"))}" title="${i18n().grant_name}">${statement.label!""}</a>
        </#if>
    </#local>

    ${linkedIndividual!}

 </#macro>
