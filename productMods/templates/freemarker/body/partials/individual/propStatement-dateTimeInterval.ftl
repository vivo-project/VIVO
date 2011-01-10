<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeInterval -->

<#import "lib-datetime.ftl" as dt>

${dt.dateTimeIntervalLong("${statement.dateTimeStart!}", "${statement.precisionStart!}", 
                          "${statement.dateTimeEnd!}", "${statement.precisionEnd!}")}
