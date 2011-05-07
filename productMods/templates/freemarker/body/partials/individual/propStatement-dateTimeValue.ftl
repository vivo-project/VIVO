<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeValue -->

<#import "lib-datetime.ftl" as dt>

<#if statement.dateTime??>
    <span rel="core:dateTimeValue">
        <span about="${statement.dateTimeValue}">
            <#if statement.precision??>
                <span rel="core:dateTimePrecision" resource="${statement.precision}"></span>
            </#if>
            <span about="${statement.dateTimeValue}" property="core:dateTime" content="${statement.dateTime}">
                ${dt.formatXsdDateTimeLong(statement.dateTime, statement.precision!)}
            </span>
        </span>
    </span>
<#else>
    <#-- No core:dateTime data property assigned. Display a link to the core:DateTimeValue object -->
    <a href="${profileUrl(statement.dateTimeValue)}">
        <span class="link" about="${individual.uri}" rel="core:dateTimeValue" resource="${statement.dateTimeValue}">
            incomplete date/time<#t>
        </span><#t>
    </a><#t>    
</#if>