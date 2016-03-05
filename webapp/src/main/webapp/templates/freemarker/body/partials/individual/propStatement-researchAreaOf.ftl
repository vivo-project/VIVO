<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--     
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<@showResearchers statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showResearchers statement>
    <#local linkedIndividual>
        <a href="${profileUrl(statement.uri("person"))}" title="${i18n().person_name}">${statement.personName!}</a>
    </#local>
    <#if !(statement.dateTimeEnd?has_content)>
        <#if statement.title?has_content >
            <#local posnTitle = statement.title>
        <#elseif statement.posnLabel?has_content>
            <#local posnTitle = statement.posnLabel>
        </#if>
        <#if statement.org??>
            <#local orgString>
                <a href="${profileUrl(statement.uri("org"))}" title="${i18n().organization_name}">${statement.orgLabel!""}</a>
            </#local>
        </#if>
    </#if>

    <@s.join [ linkedIndividual, posnTitle!, orgString! ] />
</#macro>
