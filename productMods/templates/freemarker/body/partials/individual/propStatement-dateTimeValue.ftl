<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeValue -->

<#import "lib-datetime.ftl" as dt>

<#-- No core:dateTime data property assigned. Display a link to the core:DateTimeValue object -->
<#if ! statement.dateTime??>
    <a href="${profileUrl(statement.dateTimeValue)}">${statement.dateTimeValueName}</a> (incomplete date/time data)
<#else>
    ${dt.formatXsdDateTimeLong(statement.dateTime, statement.precision!)}
</#if>