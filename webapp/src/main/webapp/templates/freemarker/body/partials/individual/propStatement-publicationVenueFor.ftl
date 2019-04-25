<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- Custom object property statement view for faux property "editor of". See the PropertyConfig.n3 file for details.

     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showEditorship statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showEditorship statement>
<#local citationDetails>
    <#if statement.subclass??>
        <#if statement.subclass?contains("Article")>
                <#if statement.volume?? && statement.startPage?? && statement.endPage??>
                    ${statement.volume!}:${statement.startPage!}-${statement.endPage!}.
                <#elseif statement.volume?? && statement.startPage??>
                    ${statement.volume!}:${statement.startPage!}.
                <#elseif statement.volume??>
                    ${statement.volume!}.
                <#elseif statement.startPage?? && statement.endPage??>
                    ${statement.startPage!}-${statement.endPage!}.
                <#elseif statement.startPage??>
                    ${statement.startPage!}.
                </#if>
			</#if>
    </#if>
</#local>

    <#local resourceTitle>
        <#if statement.infoResource??>
            <#if citationDetails?has_content>
                <a href="${profileUrl(statement.uri("infoResource"))}"  title="${i18n().resource_name}">${statement.infoResourceName}</a>.&nbsp;
            <#else>
                <a href="${profileUrl(statement.uri("infoResource"))}"  title="${i18n().resource_name}">${statement.infoResourceName}</a>
            </#if>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("editorship"))}" title="${i18n().missing_info_resource}">${i18n().missing_info_resource}</a>
        </#if>
    </#local>

    ${resourceTitle} ${citationDetails!} <@dt.yearSpan "${statement.dateTime!}" />
</#macro>
