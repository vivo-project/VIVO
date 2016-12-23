<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeValue. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-datetime.ftl" as dt>
<#import "lib-meta-tags.ftl" as lmt>

<#-- No core:dateTime data property assigned. Display a link to the core:DateTimeValue object -->
<#if ! statement.dateTime??>
    <a href="${profileUrl(statement.uri("dateTimeValue"))}" title="${i18n().incomplete_date_time_value}">${i18n().incomplete_date_time_value}</a>
<#else>
    ${dt.formatXsdDateTimeLong(statement.dateTime, statement.precision!)}
    <@lmt.addCitationMetaTag uri="http://vivoweb.org/ontology/core#dateTimeValue" content=dt.formatXsdDateTimeLong(statement.dateTime, statement.precision!) />
</#if>