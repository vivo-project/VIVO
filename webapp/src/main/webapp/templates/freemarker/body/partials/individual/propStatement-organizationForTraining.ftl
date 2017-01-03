<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "organization for training". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<@showTraining statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showTraining statement>
    
    <#local linkedIndividual>
        <#if statement.person??>
            <a href="${profileUrl(statement.uri("person"))}" title="${i18n().person_name}">${statement.personName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("edTraining"))}" title="${i18n().missing_person_in_posn}">${i18n().missing_person_in_posn}</a>
        </#if>
    </#local>
    <#local detailedInfo>
        <#if statement.degree??>
            ${statement.degreeAbbr!} <#if statement.majorField??> ${i18n().in} ${statement.majorField!} </#if>
        <#elseif statement.majorField??>
            ${statement.majorField!}
        <#elseif statement.info??>
            ${statement.info!}
        </#if>
    </#local>
    <@s.join [ linkedIndividual, detailedInfo ] /> <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" false/>

</#macro>


