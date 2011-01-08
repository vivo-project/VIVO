<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeValue -->

<#import "lib-datetime.ftl" as dt>

<#--
${statement.dateTime} ${statement.precision} <br />

<#assign dateTime = statement.dateTime?replace("T", " ")>
${dateTime?datetime("yyyy-MM-dd HH:mm:ss")?string("h:mm a")} -->


${dt.formatXsdDateTime(statement.dateTime, statement.precision, "long")} 