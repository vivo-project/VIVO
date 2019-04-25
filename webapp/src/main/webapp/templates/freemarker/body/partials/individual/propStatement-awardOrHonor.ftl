<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- Custom object property statement view for faux property "awards and honors". See the PropertyConfig.n3 file for details.

     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.
 -->
<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<@showAward statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAward statement>

    <#local linkedIndividual>
        <#if statement.award??>
            <a href="${profileUrl(statement.uri("award"))}" title="${i18n().award_name}">${statement.awardLabel!}</a>
        <#else>
            <a href="${profileUrl(statement.uri("awardReceipt"))}" title="${i18n().award_receipt_name}">${statement.receiptLabel!}</a>
        </#if>
    </#local>

    <#local dateTimeVal>
        <#if statement.dateTime??>
            <@dt.yearSpan statement.dateTime! />
        <#else>
            <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
        </#if>
    </#local>

    <#local assignedByOrg>
        <#if statement.assignedBy?has_content && statement.assignedByLabel?has_content>
             ${i18n().conferred_by} <a href="${profileUrl(statement.uri("assignedBy"))}" title="${i18n().conferred_by}">${statement.assignedByLabel}</a>
        </#if>
    </#local>

    <@s.join [ linkedIndividual, assignedByOrg!,  dateTimeVal! ] />

 </#macro>
