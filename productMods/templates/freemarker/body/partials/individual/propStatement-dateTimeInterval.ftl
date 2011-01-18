<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeInterval -->

<#import "lib-datetime.ftl" as dt>

<#if ! statement.valueStart?? && ! statement.valueEnd??>
    <a href="${profileUrl(statement.dateTimeInterval)}">${statement.intervalName}</a> (incomplete date/time interval data)
<#else>
    ${dt.dateTimeIntervalLong("${statement.dateTimeStart!}", "${statement.precisionStart!}", "${statement.dateTimeEnd!}", "${statement.precisionEnd!}")}
</#if>