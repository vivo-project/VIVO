<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#dateTimeInterval -->

<#if statement.dateTimeStart??>
    ${dateTimeStart} ${precisionStart}
</#if>
 -
<#if statement.dateTimeEnd??>
    ${dateTimeEnd} ${precisionEnd}
</#if>