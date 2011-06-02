<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeInterval. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-datetime.ftl" as dt>

<#if ! statement.valueStart?? && ! statement.valueEnd??>
    <a href="${profileUrl(statement.dateTimeInterval)}">incomplete date/time interval</a>
<#else>
    ${dt.dateTimeIntervalLong("${statement.dateTimeStart!}", "${statement.precisionStart!}", "${statement.dateTimeEnd!}", "${statement.precisionEnd!}")}
</#if>