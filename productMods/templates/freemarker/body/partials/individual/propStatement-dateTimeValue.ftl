<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeValue -->

<#import "lib-datetime.ftl" as dt>

${dt.formatXsdDateTimeLong(statement.dateTime, statement.precision)}
