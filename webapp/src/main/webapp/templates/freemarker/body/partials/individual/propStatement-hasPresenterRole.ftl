<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "presentations". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showRole statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showRole statement>
    <#local linkedIndividual>
        <#if statement.presentation??>
            <a href="${profileUrl(statement.uri("presentation"))}" title="${i18n().presentation_name}">${statement.presentationLabel!statement.presentationName!""}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("role"))}" title="${i18n().missing_presentation}">${i18n().missing_presentation}</a>
        </#if>
    </#local>
    
    <#local dateTime>
        <@dt.yearSpan statement.dateTime! /> 
    </#local>
    
    <#local conference>
        <#if statement.conference?has_content && statement.conferenceLabel?has_content>
            <a href="${profileUrl(statement.uri("conference"))}" title="${i18n().conference}">${statement.conferenceLabel}</a>
        <#elseif statement.series?has_content && statement.seriesLabel?has_content>
            <a href="${profileUrl(statement.uri("series"))}" title="${i18n().series}">${statement.seriesLabel}</a>
        </#if>
    </#local>

    <@s.join [ linkedIndividual, statement.roleLabel!, conference! ] /> ${dateTime!}

</#macro>