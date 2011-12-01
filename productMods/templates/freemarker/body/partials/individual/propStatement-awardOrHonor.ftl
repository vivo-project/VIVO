<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#mailingAddress. 
    
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
            <a href="${profileUrl(statement.uri("award"))}" title="award name">${statement.awardLabel!statement.localName}</a>
        </#if>
    </#local>

    <#local dateTimeVal>
        <#if statement.dateTime??>
            <@dt.yearSpan statement.dateTime! /> 
        <#else>
            <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
        </#if>
    </#local>

    <#local conferredByOrg>
        <#if statement.conferredBy?has_content && statement.conferredByLabel?has_content>
             conferred by <a href="${profileUrl(statement.uri("conferredBy"))}" title="conferred by">${statement.conferredByLabel}</a>
        </#if>
    </#local>

    <@s.join [ linkedIndividual,  conferredByOrg!, dateTimeVal! ] />   
 </#macro>